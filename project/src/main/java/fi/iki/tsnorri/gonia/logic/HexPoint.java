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

import java.util.Arrays;
import java.util.Collection;


/**
 * A point in hexagonal coordinate system.
 *
 * @author tsnorri
 */
public class HexPoint implements Cloneable
{
	/**
	 * Sorts the points by distance w.r.t. to the hexagonal Y axis and the
	 * horizontal X axis.
	 *
	 * @author tsnorri
	 */
	public static class Comparator implements java.util.Comparator<HexPoint>
	{
		@Override
		public int compare(HexPoint lhs, HexPoint rhs)
		{
			int diff = lhs.getY() - rhs.getY();
			if (0 == diff)
				diff = lhs.getHorizontalOffset() - rhs.getHorizontalOffset();
			return diff;
		}
	}


	/**
	 * A factory for use with the static methods.
	 *
	 * @param <T> HexPoint or a subclass.
	 * @author tsnorri
	 */
	protected static abstract class Factory<T extends HexPoint>
	{
		/**
		 * Call T's constructor.
		 *
		 * @param x Hexagonal X co-ordinate.
		 * @param y Hexagonal Y co-ordinate.
		 * @param z Hexagonal Z co-ordinate.
		 * @return An instance of T.
		 */
		public abstract T instantiate(int x, int y, int z);


		/**
		 * Create a point from orthogonal co-ordinates.
		 *
		 * @param h Horizontal distance.
		 * @param v Vertical distance.
		 * @return The created point.
		 */
		public T createWithOffsets(int h, int v)
		{
			int x = h - v / 2;
			int y = v;
			int z = -(x + y);
			return this.instantiate(x, y, z);
		}
	}

	private static Factory<HexPoint> factory;
	protected int x;
	protected int y;
	protected int z;


	static
	{
		factory = new Factory<HexPoint>()
		{
			@Override
			public HexPoint instantiate(int x, int y, int z)
			{
				return new HexPoint(x, y, z);
			}
		};
	}


	/**
	 * Constructor.
	 *
	 * @param x Hexagonal X co-ordinate.
	 * @param y Hexagonal Y co-ordinate.
	 * @param z Hexagonal Z co-ordinate.
	 */
	public HexPoint(int x, int y, int z)
	{
		if (0 != x + y + z)
			throw new IllegalArgumentException("The sum of coordinates must be zero.");
		this.x = x;
		this.y = y;
		this.z = z;
	}


	/**
	 * Create a point from orthogonal co-ordinates.
	 *
	 * @param h Horizontal distance.
	 * @param v Vertical distance.
	 * @return The created point.
	 */
	public static HexPoint createWithOffsets(int h, int v)
	{
		return factory.createWithOffsets(h, v);
	}


	/**
	 * X co-ordinate.
	 *
	 * @return The co-ordinate.
	 */
	public int getX()
	{
		return this.x;
	}


	/**
	 * Y co-ordinate.
	 *
	 * @return The co-ordinate.
	 */
	public int getY()
	{
		return this.y;
	}


	/**
	 * Z co-ordinate.
	 *
	 * @return The co-ordinate.
	 */
	public int getZ()
	{
		return this.z;
	}


	/**
	 * Distance on the orthogonal X axis.
	 *
	 * @return The distance.
	 */
	public int getHorizontalOffset()
	{
		int retval = 0;
		if (this.y > 0)
			retval = +this.x + this.y / 2;
		else
			retval = -this.z + this.y / 2;
		return retval;
	}


	/**
	 * Copy the points and move them on the orthogonal axes.
	 *
	 * @param points The points.
	 * @param dw Distance on the orthogonal X axis.
	 * @param dh Distance on the orthogonal Y axis.
	 * @return An array that contains the copied points.
	 */
	public static HexPoint[] copyWithOrthogonalTranslation(HexPoint[] points, int dw, int dh)
	{
		return copyWithOrthogonalTranslation(Arrays.asList(points), dw, dh);
	}


	/**
	 * Copy the points and move them on the orthogonal axes.
	 *
	 * @param points The points.
	 * @param dw Distance on the orthogonal X axis.
	 * @param dh Distance on the orthogonal Y axis.
	 * @return An array that contains the copied points.
	 */
	public static HexPoint[] copyWithOrthogonalTranslation(Collection<? extends HexPoint> points, int dw, int dh)
	{
		int[][] transformation = translationTransformationForPoints(points, dw, dh);
		HexPoint[] retval = new HexPoint[points.size()];
		int i = 0;
		for (HexPoint point : points)
		{
			retval[i] = point.transformedCopy(transformation);
			i++;
		}
		return retval;
	}


	/**
	 * Move the given points on the orthogonal axes.
	 *
	 * @param points The points.
	 * @param dw Distance on the orthogonal X axis.
	 * @param dh Distance on the orthogonal Y axis.
	 */
	public static void applyOrthogonalTranslation(MutableHexPoint[] points, int dw, int dh)
	{
		applyOrthogonalTranslation(Arrays.asList(points), dw, dh);
	}


	/**
	 * Move the given points on the orthogonal axes.
	 *
	 * @param points The points.
	 * @param dw Distance on the orthogonal X axis.
	 * @param dh Distance on the orthogonal Y axis.
	 */
	public static void applyOrthogonalTranslation(Collection<MutableHexPoint> points, int dw, int dh)
	{
		int[][] transformation = translationTransformationForPoints(points, dw, dh);
		for (MutableHexPoint point : points)
			point.transform(transformation);
	}


	/**
	 * Create a transformation matrix for moving the points on the orthogonal
	 * axes.
	 *
	 * @param dw Distance on the orthogonal X axis.
	 * @param dh Distance on the orthogonal Y axis.
	 * @param moveRight Whether the transformation should shift if dh is odd.
	 * @return The transformation matrix.
	 */
	static int[][] translationTransformation(int dw, int dh, boolean moveRight)
	{
		int dx = 0, dy = 0, dz = 0;

		// Horizontal
		dx += dw;
		dz -= dw;

		// Vertical
		dy += dh;
		dx -= dh / 2;
		dz -= dh / 2;
		int remainder = dh % 2;
		if (0 < remainder)
		{
			if (moveRight)
				dz -= 1;
			else
				dx -= 1;
		}
		else if (0 > remainder)
		{
			if (moveRight)
				dx += 1;
			else
				dz += 1;
		}

		int[][] transform =
		{
			{1, 0, 0, 0},
			{0, 1, 0, 0},
			{0, 0, 1, 0},
			{dx, dy, dz, 1}
		};

		return transform;
	}


	/**
	 * Create a transformation matrix for moving the points on the orthogonal
	 * axes according to the least Y co-ordinate.
	 *
	 * @param dw Distance on the orthogonal X axis.
	 * @param dh Distance on the orthogonal Y axis.
	 * @param points The transformed points.
	 * @return The transformation matrix.
	 */
	static int[][] translationTransformationForPoints(Collection<? extends HexPoint> points, int dw, int dh)
	{
		int min = Integer.MAX_VALUE;
		for (HexPoint point : points)
			min = Math.min(min, point.getY());

		int transformation[][] = translationTransformation(dw, dh, (0 == min % 2 ? true : false));
		return transformation;
	}


	@Override
	public boolean equals(Object o)
	{
		boolean retval = false;
		if (o instanceof HexPoint)
		{
			HexPoint other = (HexPoint) o;
			retval = (this.x == other.x
				&& this.y == other.y
				&& this.z == other.z);
		}
		return retval;
	}


	@Override
	public int hashCode()
	{
		int hash = 7;
		hash = 1 * hash + this.x;
		hash = 83 * hash + this.y;
		hash = 167 * hash + this.z;
		return hash;
	}


	/**
	 * Apply a transformation.
	 *
	 * @param transformation The transformation, a 4x4-matrix.
	 */
	protected void _transform(int transformation[][])
	{
		if (!(4 == transformation.length && 4 == transformation[0].length))
			throw new IllegalArgumentException("Transformation matrix must be 4×4.");

		if (!(0 == transformation[3][0] + transformation[3][1] + transformation[3][2]))
			throw new IllegalArgumentException("The sum of translations must be zero.");

		int src[] =
		{
			this.x, this.y, this.z, 1
		};
		int dst[] =
		{
			0, 0, 0, 0
		};

		for (int i = 0; i < transformation.length; i++)
		{
			for (int j = 0; j < transformation[i].length; j++)
				dst[j] += src[i] * transformation[i][j];
		}

		assert 1 == dst[3];
		this.x = dst[0];
		this.y = dst[1];
		this.z = dst[2];
	}


	/**
	 * Copy a point and transform.
	 *
	 * @param transformation The transformation matrix.
	 * @return The created point.
	 */
	public HexPoint transformedCopy(int transformation[][])
	{
		HexPoint retval = new HexPoint(this.x, this.y, this.z);
		retval._transform(transformation);
		return retval;
	}


	/**
	 * Call super's clone.
	 *
	 * @throws CloneNotSupportedException
	 */
	protected HexPoint _clone() throws CloneNotSupportedException
	{
		return (HexPoint) super.clone();
	}


	/**
	 * Return this.
	 *
	 * @return this.
	 */
	@Override
	public HexPoint clone()
	{
		return this;
	}


	/**
	 * Create a mutable copy.
	 *
	 * @return The clone.
	 */
	public MutableHexPoint mutableClone()
	{
		return new MutableHexPoint(this.x, this.y, this.z);
	}


	@Override
	public String toString()
	{
		return String.format("(%d, %d, %d)", this.x, this.y, this.z);
	}


	/**
	 * Draw an ASCII representation of the given points.
	 *
	 * @param points The points.
	 * @param dh Horizontal distance from the origin.
	 * @param dv Vertical distance from the origin.
	 * @return The representation.
	 */
	public static String stringRepresentationForPoints(Collection<? extends HexPoint> points, int dh, int dv)
	{
		return stringRepresentationForPoints(points, dh, dv, 0, 0);
	}


	/**
	 * Draw an ASCII representation of the given points.
	 *
	 * @param points The points.
	 * @param dh Horizontal distance from the origin.
	 * @param dv Vertical distance from the origin.
	 * @param initialWidth Initial width of the drawn co-ordinate system.
	 * @param initialHeight Initial height of the drawn co-ordinate system.
	 * @return The representation.
	 */
	public static String stringRepresentationForPoints(Collection<? extends HexPoint> points, int dh, int dv, int initialWidth, int initialHeight)
	{
		StringBuilder builder = new StringBuilder();

		int h = initialWidth;
		int v = initialHeight;
		for (HexPoint point : points)
		{
			h = Math.max(h, 1 + point.getHorizontalOffset() - dh);
			v = Math.max(v, 1 + point.getY() - dv);
		}

		boolean occupied[][] = new boolean[v][h];
		for (HexPoint point : points)
			occupied[point.getY() - dv][point.getHorizontalOffset() - dh] = true;

		for (int y = occupied.length - 1; y >= 0; y--)
		{
			if (1 == Math.abs((y - dv) % 2))
				builder.append(' ');

			for (int x = 0; x < occupied[y].length; x++)
			{
				builder.append(' ');
				builder.append(occupied[y][x] ? 1 : 0);
				builder.append(' ');
			}
			builder.append('\n');
		}
		return builder.toString();

	}
}
