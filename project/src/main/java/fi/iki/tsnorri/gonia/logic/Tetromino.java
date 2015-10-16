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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;


/**
 * Tetromino.
 *
 * @author tsnorri
 */
public class Tetromino
{
	/**
	 * Tetromino type.
	 */
	public enum Type
	{
		C, I, S, Z, L, J, O, T1, T2, Y
	};

	private static final float SATURATION = 1.0f;
	private static final float BRIGHTNESS = 0.4f;

	MutableHexPoint[] points;
	MutableHexPoint[] shapePoints;
	// Location along orthogonal axes.
	int w = 0;
	int h = 0;
	String name;
	Color color;


	/**
	 * Constructor.
	 *
	 * @param name The name of the tetromino, e.g. ”T1”.
	 * @param color The colour of the tetromino.
	 * @param points The points occupied by the tetromino.
	 */
	public Tetromino(String name, Color color, HexPoint... points)
	{
		if (0 == points.length)
			throw new IllegalArgumentException("No points given.");

		this.points = new MutableHexPoint[points.length];
		this.shapePoints = new MutableHexPoint[points.length];
		for (int i = 0; i < points.length; i++)
		{
			this.points[i] = points[i].mutableClone();
			this.shapePoints[i] = points[i].mutableClone();
		}

		this.name = name;
		this.color = color;
	}


	/**
	 * Create a tetromino of the given type.
	 *
	 * @param type The type.
	 * @return Tetromino.
	 */
	public static Tetromino tetrominoWithType(Type type)
	{
		Tetromino retval = null;
		switch (type)
		{
			case C:
				retval = new Tetromino(
					"C",
					tetrominoColor(184.0f),
					new HexPoint(1, -1, 0),
					new HexPoint(0, -1, 1),
					new HexPoint(-1, 0, 1),
					new HexPoint(-1, 1, 0));
				break;
			case I:
				retval = new Tetromino(
					"I",
					tetrominoColor(220.0f),
					new HexPoint(-2, 0, 2),
					new HexPoint(-1, 0, 1),
					new HexPoint(0, 0, 0),
					new HexPoint(1, 0, -1));
				break;

			case S:
				retval = new Tetromino(
					"S",
					tetrominoColor(256.0f),
					new HexPoint(-1, 0, 1),
					new HexPoint(0, 0, 0),
					new HexPoint(0, 1, -1),
					new HexPoint(1, 1, -2));
				break;

			case Z:
				retval = new Tetromino(
					"Z",
					tetrominoColor(292.0f),
					new HexPoint(1, 0, -1),
					new HexPoint(0, 0, 0),
					new HexPoint(-1, 1, 0),
					new HexPoint(-2, 1, 1));
				break;

			case L:
				retval = new Tetromino(
					"L",
					tetrominoColor(328.0f),
					new HexPoint(-2, 1, 1),
					new HexPoint(-1, 0, 1),
					new HexPoint(0, 0, 0),
					new HexPoint(1, 0, -1));
				break;

			case J:
				retval = new Tetromino(
					"J",
					tetrominoColor(4.0f),
					new HexPoint(1, 1, -2),
					new HexPoint(1, 0, -1),
					new HexPoint(0, 0, 0),
					new HexPoint(-1, 0, 1));
				break;

			case O:
				retval = new Tetromino(
					"O",
					tetrominoColor(40.0f),
					new HexPoint(-1, 0, 1),
					new HexPoint(-1, 1, 0),
					new HexPoint(0, 0, 0),
					new HexPoint(0, 1, -1));
				break;

			case T1:
				retval = new Tetromino(
					"T1",
					tetrominoColor(76.0f),
					new HexPoint(-1, 0, 1),
					new HexPoint(0, 0, 0),
					new HexPoint(1, 0, -1),
					new HexPoint(-1, 1, 0));
				break;

			case T2:
				retval = new Tetromino(
					"T2",
					tetrominoColor(112.0f),
					new HexPoint(-1, 0, 1),
					new HexPoint(0, 0, 0),
					new HexPoint(1, 0, -1),
					new HexPoint(0, 1, -1));
				break;

			case Y:
				retval = new Tetromino(
					"Y",
					tetrominoColor(148.0f),
					new HexPoint(0, 0, 0),
					new HexPoint(1, -1, 0),
					new HexPoint(-1, 0, 1),
					new HexPoint(0, 1, -1));
				break;
		}
		return retval;
	}


	private static Color tetrominoColor(float angle)
	{
		int rgb = Color.HSBtoRGB(angle / 360.0f, SATURATION, BRIGHTNESS);
		return new Color(rgb);
	}


	/**
	 * The points occupied by the tetromino.
	 *
	 * @return The points.
	 */
	public Collection<? extends HexPoint> getPoints()
	{
		return Collections.unmodifiableList(Arrays.asList(points));
	}


	/**
	 * The colour.
	 *
	 * @return A colour.
	 */
	public Color getColor()
	{
		return this.color;
	}


	/**
	 * The name.
	 *
	 * @return A string.
	 */
	public String getName()
	{
		return this.name;
	}


	/**
	 * Move the tetromino along the orthogonal axes.
	 *
	 * @param w Horizontal co-ordinate.
	 * @param h Vertical co-ordinate.
	 */
	public void moveTo(int w, int h)
	{
		int dw = w - this.w;
		int dh = h - this.h;

		this.applyTranslation(dw, dh);
	}


	/**
	 * Move the tetromino left.
	 *
	 * @param gb The game board to check.
	 */
	public void moveLeft(GameBoard gb)
	{
		if (gb.hasSpaceLeft(Arrays.asList(this.points)))
			this.applyTranslation(-1, 0);
	}


	/**
	 * Move the tetromino right.
	 *
	 * @param gb The game board to check.
	 */
	public void moveRight(GameBoard gb)
	{
		if (gb.hasSpaceRight(Arrays.asList(this.points)))
			this.applyTranslation(1, 0);
	}


	/**
	 * Rotate the tetromino clockwise.
	 *
	 * @param gb The game board to check.
	 */
	public void rotateCW(GameBoard gb)
	{
		int[][] transform =
		{
			{0, -1, 0, 0},
			{0, 0, -1, 0},
			{-1, 0, 0, 0},
			{0, 0, 0, 1},
		};
		this.checkAvailabilityAndTransform(gb, transform);
	}


	/**
	 * Rotate the tetromino counterclockwise.
	 *
	 * @param gb The game board to check.
	 */
	public void rotateCCW(GameBoard gb)
	{
		int[][] transform =
		{
			{0, 0, -1, 0},
			{-1, 0, 0, 0},
			{0, -1, 0, 0},
			{0, 0, 0, 1},
		};
		this.checkAvailabilityAndTransform(gb, transform);
	}


	/**
	 * Move the tetromino down on a trajectory.
	 *
	 * @param trajectory The trajectory.
	 * @param gb The game board to check.
	 * @return -1 if the tetromino can still move, the number of lines removed
	 * if the tetromino occupied space on the game board.
	 */
	public int dropOne(Trajectory trajectory, GameBoard gb)
	{
		int retval = -1;
		Collection<HexPoint> nextPoints = trajectory.nextPoints(Arrays.asList(points), gb);

		if (null == nextPoints)
			retval = gb.occupySpace(points, color);
		else
		{
			int i = 0;
			for (HexPoint point : nextPoints)
			{
				points[i].copyFrom(point);
				i++;
			}
			this.h--;
		}
		return retval;
	}


	/**
	 * Drop the tetromino.
	 *
	 * @param trajectory The trajectory.
	 * @param gb The game board to check.
	 * @return A pair: the dropped distance and how many lines were removed.
	 */
	public Pair<Integer, Integer> drop(Trajectory trajectory, GameBoard gb)
	{
		int distance = 0;
		int lines = 0;
		while (-1 == (lines = this.dropOne(trajectory, gb)))
			distance++;
		return new Pair<Integer, Integer>(distance, lines);
	}


	/**
	 * Transform if possible.
	 *
	 * @param gb The game board to check.
	 * @param transform The transformation.
	 */
	protected void checkAvailabilityAndTransform(GameBoard gb, int[][] transform)
	{
		MutableHexPoint newShapePoints[] = new MutableHexPoint[this.shapePoints.length];
		MutableHexPoint newPoints[] = new MutableHexPoint[this.shapePoints.length];
		int minY = 0;
		for (int i = 0; i < this.shapePoints.length; i++)
		{
			newShapePoints[i] = this.shapePoints[i].mutableClone();
			newShapePoints[i].transform(transform);
			minY = Math.min(minY, newShapePoints[i].getY());
		}

		for (int i = 0; i < this.shapePoints.length; i++)
			newPoints[i] = newShapePoints[i].mutableClone();
		HexPoint.applyOrthogonalTranslation(Arrays.asList(newPoints), this.w, this.h - minY);

		if (gb.areAllValidAndVacant(Arrays.asList(newPoints)))
		{
			for (int i = 0; i < this.shapePoints.length; i++)
			{
				this.shapePoints[i].copyFrom(newShapePoints[i]);
				this.points[i].copyFrom(newPoints[i]);
			}
		}
	}


	/**
	 * Apply a transformation along orthogonal axes.
	 *
	 * @param dw Horizontal distance.
	 * @param dh vertical distance.
	 */
	protected void applyTranslation(int dw, int dh)
	{
		HexPoint.applyOrthogonalTranslation(points, dw, dh);
		this.w += dw;
		this.h += dh;
	}


	@Override
	public String toString()
	{
		return String.format("Tetromino %s w: %d h: %d points: (%s)",
			this.name, this.w, this.h, StringUtils.join(points, ", "));
	}
}
