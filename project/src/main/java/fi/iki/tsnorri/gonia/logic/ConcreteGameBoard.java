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

import java.awt.Color;
import java.util.*;


/**
 * Game board implementation.
 *
 * @author tsnorri
 */
public class ConcreteGameBoard implements GameBoard
{
	private int width;
	private int height;
	private SortedMap<HexPoint, Color> occupiedTiles;


	/**
	 * Konstruktori.
	 *
	 * @param width Game board width, positive.
	 * @param height Game board height, positive.
	 * @throws IllegalArgumentException
	 */
	public ConcreteGameBoard(int width, int height)
	{
		if (!(0 < width))
			throw new IllegalArgumentException("Width must be positive.");
		if (!(0 < height))
			throw new IllegalArgumentException("Height must be positive.");

		this.width = width;
		this.height = height;
		this.occupiedTiles = new TreeMap<HexPoint, Color>(new HexPoint.Comparator());
	}


	@Override
	public int getWidth()
	{
		return this.width;
	}


	@Override
	public int getHeight()
	{
		return this.height;
	}


	@Override
	public boolean isOccupied(HexPoint point)
	{
		return this.occupiedTiles.containsKey(point);
	}


	@Override
	public Color colorForOccupiedPoint(HexPoint point)
	{
		return this.occupiedTiles.get(point);
	}


	@Override
	public boolean areAllValidAndVacant(Collection<? extends HexPoint> points)
	{
		for (HexPoint point : points)
		{
			if (this.occupiedTiles.containsKey(point))
				return false;

			int h = point.getHorizontalOffset();
			int v = point.getY();
			if (!(0 <= h && h < this.width && 0 <= v && v < this.height))
				return false;
		}
		return true;
	}


	@Override
	public boolean isValidAndVacant(HexPoint point)
	{
		boolean retval = false;
		if (!this.occupiedTiles.containsKey(point))
		{
			int h = point.getHorizontalOffset();
			int v = point.getY();
			if (0 <= h && h < this.width && 0 <= v && v < this.height)
				retval = true;
		}
		return retval;
	}


	/**
	 * Check whether there is free space around the given positions.
	 *
	 * @param points Locations.
	 * @param dw Horizontal distance from the locations.
	 * @param dh Vertical distance from the locations.
	 * @return Boolean.
	 */
	private boolean hasSpace(Collection<? extends HexPoint> points, int dw, int dh)
	{
		HexPoint[] translated = HexPoint.copyWithOrthogonalTranslation(points, dw, dh);
		for (HexPoint point : Arrays.asList(translated))
		{
			if (!this.isValidAndVacant(point))
				return false;
		}
		return true;
	}


	@Override
	public boolean hasSpaceLeft(Collection<? extends HexPoint> points)
	{
		return hasSpace(points, -1, 0);
	}


	@Override
	public boolean hasSpaceRight(Collection<? extends HexPoint> points)
	{
		return hasSpace(points, +1, 0);
	}


	@Override
	public boolean hasSpaceUnder(Collection<? extends HexPoint> points)
	{
		return hasSpace(points, 0, -1);
	}


	@Override
	public int occupySpace(HexPoint[] points, Color color)
	{
		int k = 0;
		int l = 0;
		int previous = -1;
		int[] yCoords = new int[points.length];
		int[] lines = new int[1 + points.length];

		// Add the given points and remember the possible lines.
		Arrays.sort(points, new HexPoint.Comparator());
		for (HexPoint point : Arrays.asList(points))
		{
			int y = point.getY();
			this.occupiedTiles.put(point.clone(), color);
			if (previous != y)
			{
				previous = y;
				yCoords[k] = y;
				k++;
			}
		}

		// Check the lines.
		{
			List<HexPoint> buffer = new ArrayList<HexPoint>();
			for (int i = 0; i < k; i++)
			{
				HexPoint first = HexPoint.createWithOffsets(0, yCoords[i]);
				HexPoint last = HexPoint.createWithOffsets(this.width, yCoords[i]);
				SortedMap<HexPoint, Color> line = this.occupiedTiles.subMap(first, last);
				if (this.width == line.size())
				{
					buffer.addAll(line.keySet());
					for (HexPoint point : buffer)
						this.occupiedTiles.remove(point);
					buffer.clear();

					lines[l] = yCoords[i];
					l++;
				}
			}
			lines[l] = this.height;
		}

		{
			// Some points may end up outside the game board, fix them by wrapping.
			int[][] t1 = HexPoint.translationTransformation(+this.width, 0, false);
			int[][] t2 = HexPoint.translationTransformation(-this.width, 0, false);

			SortedMap<HexPoint, Color> buffer = new TreeMap<HexPoint, Color>(new HexPoint.Comparator());
			for (int i = 0; i < l; i++)
			{
				HexPoint first = HexPoint.createWithOffsets(0, lines[i]);
				HexPoint last = HexPoint.createWithOffsets(0, lines[i + 1]);
				SortedMap<HexPoint, Color> between = this.occupiedTiles.subMap(first, last);
				buffer.putAll(between);
				for (HexPoint point : buffer.keySet())
					this.occupiedTiles.remove(point);

				// We make the assumption that keySet remains sorted even though it hasn't been declared as such.
				HexPoint[] transformed = HexPoint.copyWithOrthogonalTranslation(buffer.keySet(), 0, -(1 + i));

				int j = 0;
				for (Color currentColor : buffer.values())
				{
					int h = transformed[j].getHorizontalOffset();
					if (h < 0)
						transformed[j] = transformed[j].transformedCopy(t1);
					else if (this.width <= h)
						transformed[j] = transformed[j].transformedCopy(t2);

					this.occupiedTiles.put(transformed[j], currentColor);
					j++;
				}
				buffer.clear();
			}
		}

		return l;
	}


	@Override
	public String toString()
	{
		return HexPoint.stringRepresentationForPoints(occupiedTiles.keySet(), 0, 0, width, height);
	}
}
