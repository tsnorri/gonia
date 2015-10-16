/*
 * Copyright (c) 2012, 2015 Tuukka Norri, tsnorri@iki.fi.
 *
 * This software is licensed as described in the file COPYING, which
 * you should have received as part of this distribution.
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY
 * KIND, either express or implied.
 */
package fi.iki.tsnorri.gonia.logic;

import java.util.*;


/**
 * Tetromino trajectory.
 *
 * @author tsnorri
 */
public class Trajectory
{
	/**
	 * Trajectory types.
	 */
	public enum Type
	{
		DiagonalLeft, DiagonalRight, Vertical
	}


	/**
	 * Trajectory type implementation.
	 *
	 * @author tsnorri
	 */
	abstract static class Specific
	{
		/**
		 * Next points on the trajectory.
		 *
		 * @param currentPoints The current location.
		 * @param gameBoard The game board.
		 * @return The next points or null if no space is available.
		 */
		protected abstract Collection<HexPoint> nextPoints(Collection<? extends HexPoint> currentPoints, GameBoard gameBoard);


		/**
		 * All remaining points on the trajectory.
		 *
		 * @param currentPoints The current location.
		 * @param gameBoard The game borad.
		 * @return The remaining points or null if no space is available.
		 */
		public Set<HexPoint> remainingPoints(Collection<? extends HexPoint> currentPoints, GameBoard gameBoard)
		{
			// We don't cache the calculated trajectory since it isn't very long.
			Set<HexPoint> remainingPoints = new TreeSet<HexPoint>(new HexPoint.Comparator());
			Collection<? extends HexPoint> nextPoints = currentPoints;
			while (null != (nextPoints = nextPoints(nextPoints, gameBoard)))
				remainingPoints.addAll(nextPoints);

			if (0 == remainingPoints.size())
				remainingPoints = null;

			return remainingPoints;
		}
	}


	/**
	 * A trajectory along the hexagonal X or Z axis.
	 *
	 * @author tsnorri
	 */
	static class Diagonal extends Specific
	{
		protected int[][] transformation;
		Vertical vertical;


		/**
		 * Constructor.
		 *
		 * @param moveRight If true, choose the Z axis, otherwise the X axis.
		 */
		public Diagonal(boolean moveRight)
		{
			this.transformation = HexPoint.translationTransformation(0, -1, moveRight);
		}


		/**
		 * Set the vertical trajectory to be used if there is no space in the
		 * axis direction.
		 *
		 * @param vertical The vertical trajectory.
		 */
		public void setVertical(Vertical vertical)
		{
			this.vertical = vertical;
		}


		/**
		 * The next points on the trajectory in the axis direction.
		 *
		 * @param currentPoints The current location.
		 * @param gameBoard The game borad.
		 * @return The next points or null if no space is available.
		 */
		protected Collection<HexPoint> nextDiagonalPoints(Collection<? extends HexPoint> currentPoints, GameBoard gameBoard)
		{
			HexPoint[] points = new HexPoint[currentPoints.size()];
			int i = 0;
			for (HexPoint point : currentPoints)
			{
				points[i] = point.transformedCopy(transformation);
				if (!gameBoard.isValidAndVacant(points[i]))
					return null;

				i++;
			}
			return Arrays.asList(points);
		}


		@Override
		protected Collection<HexPoint> nextPoints(Collection<? extends HexPoint> currentPoints, GameBoard gameBoard)
		{
			Collection<HexPoint> retval = nextDiagonalPoints(currentPoints, gameBoard);
			if (null == retval)
				retval = vertical.nextPoints(currentPoints, gameBoard);
			return retval;
		}
	}


	/**
	 * A vertical trajectory. Alternates left and right shifts.
	 *
	 * @author tsnorri
	 */
	static class Vertical extends Specific
	{
		protected Diagonal[] diagonals;


		/**
		 * Constructor.
		 *
		 * @param left Trajectory along the hexagonal Z axis.
		 * @param right Trajectory along the hexagonal X axis.
		 */
		public Vertical(Diagonal left, Diagonal right)
		{
			this.diagonals = new Diagonal[]
			{
				right, left
			};
		}


		@Override
		protected Collection<HexPoint> nextPoints(Collection<? extends HexPoint> currentPoints, GameBoard gameBoard)
		{
			int min = Integer.MAX_VALUE;
			for (HexPoint point : currentPoints)
				min = Math.min(min, point.getY());

			int idx = Math.abs(min % 2);
			Collection<HexPoint> nextPoints = diagonals[idx].nextDiagonalPoints(currentPoints, gameBoard);
			if (null == nextPoints)
				nextPoints = diagonals[(1 + idx) % 2].nextDiagonalPoints(currentPoints, gameBoard);

			return nextPoints;
		}
	}

	private Type preferredType;
	private Diagonal diagonalLeft;
	private Diagonal diagonalRight;
	private Vertical vertical;


	/**
	 * Constructor.
	 */
	public Trajectory()
	{
		diagonalLeft = new Diagonal(false);
		diagonalRight = new Diagonal(true);
		vertical = new Vertical(diagonalLeft, diagonalRight);

		diagonalLeft.setVertical(vertical);
		diagonalRight.setVertical(vertical);

		preferredType = Type.Vertical;
	}


	/**
	 * Set the preferred trajectory.
	 *
	 * @param preferredType The type.
	 */
	public void setPreferredType(Type preferredType)
	{
		this.preferredType = preferredType;
	}


	private Specific preferredSpecific()
	{
		switch (preferredType)
		{
			case DiagonalLeft:
				return diagonalLeft;

			case DiagonalRight:
				return diagonalRight;

			case Vertical:
				return vertical;
		}
		return null;
	}


	/**
	 * Next points on the trajectory.
	 *
	 * @param points The current location.
	 * @param gameBoard The game borad.
	 * @return The next points or null if no space is available.
	 */
	public Collection<HexPoint> nextPoints(Collection<? extends HexPoint> points, GameBoard gameBoard)
	{
		return preferredSpecific().nextPoints(points, gameBoard);
	}


	/**
	 * All the remaining points on the trajectory.
	 *
	 * @param points The current location.
	 * @param gameBoard The game board.
	 * @return The next points or null if no space is available.
	 */
	public Set<HexPoint> remainingPoints(Collection<? extends HexPoint> points, GameBoard gameBoard)
	{
		return preferredSpecific().remainingPoints(points, gameBoard);
	}
}
