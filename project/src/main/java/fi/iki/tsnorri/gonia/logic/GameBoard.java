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
import java.util.Collection;


/**
 * Game board.
 *
 * @author tsnorri
 */
public interface GameBoard
{
	/**
	 * Game board width.
	 *
	 * @return The width.
	 */
	public int getWidth();


	/**
	 * Game board height.
	 *
	 * @return The height.
	 */
	public int getHeight();


	/**
	 * Check whether the given point is valid and vacant.
	 *
	 * @param point The point.
	 * @return Boolean.
	 */
	public boolean isValidAndVacant(HexPoint point);


	/**
	 * Check whether the given point is occupied.
	 *
	 * @param point The point.
	 * @return Boolean.
	 */
	public boolean isOccupied(HexPoint point);


	/**
	 * Check whether the given point is occupied and return its colour.
	 *
	 * @param point The point.
	 * @return The colour if the point was occupied, otherwise null.
	 */
	public Color colorForOccupiedPoint(HexPoint point);


	/**
	 * Check whether all the given points are valid and vacant.
	 *
	 * @param points The points.
	 * @return Boolean.
	 */
	public boolean areAllValidAndVacant(Collection<? extends HexPoint> points);


	/**
	 * Check whether there is space on left side of the given points.
	 *
	 * @param points The points.
	 * @return Boolean.
	 */
	public boolean hasSpaceLeft(Collection<? extends HexPoint> points);


	/**
	 * Check whether there is space on right side of the given points.
	 *
	 * @param points The points.
	 * @return Boolean.
	 */
	public boolean hasSpaceRight(Collection<? extends HexPoint> points);


	/**
	 * Check whether there is space below the given points.
	 *
	 * @param points The points.
	 * @return Boolean.
	 */
	public boolean hasSpaceUnder(Collection<? extends HexPoint> points);


	/**
	 * Mark all the points occupied and remove lines.
	 *
	 * @param points The points
	 * @return The number of removed lines.
	 */
	public int occupySpace(HexPoint[] points, Color color);
}
