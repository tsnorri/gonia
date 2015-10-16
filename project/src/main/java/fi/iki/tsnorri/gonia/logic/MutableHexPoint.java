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


/**
 * A mutable point in hexagonal coordinate system.
 *
 * @author tsnorri
 */
public class MutableHexPoint extends HexPoint
{
	private static Factory<MutableHexPoint> factory;


	static
	{
		factory = new Factory<MutableHexPoint>()
		{
			@Override
			public MutableHexPoint instantiate(int x, int y, int z)
			{
				return new MutableHexPoint(x, y, z);
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
	public MutableHexPoint(int x, int y, int z)
	{
		super(x, y, z);
	}


	public static MutableHexPoint createWithOffsets(int h, int v)
	{
		return factory.createWithOffsets(h, v);
	}


	/**
	 * Apply a transformation.
	 *
	 * @param transformation The transformation matrix.
	 */
	public void transform(int[][] transformation)
	{
		super._transform(transformation);
	}


	/**
	 * Clone the current point, return an immutable type.
	 *
	 * @return The clone.
	 */
	@Override
	public HexPoint clone()
	{
		try
		{
			return super._clone();
		}
		catch (CloneNotSupportedException exc)
		{
			throw new RuntimeException(exc);
		}
	}


	/**
	 * Copy the co-ordinates from another point.
	 *
	 * @param point The source point.
	 */
	public void copyFrom(HexPoint point)
	{
		this.x = point.x;
		this.y = point.y;
		this.z = point.z;
	}


	/**
	 * Set the co-ordinates according to the orthogonal X and Y co-ordinates.
	 *
	 * @param h The orthogonal X co-ordinate.
	 * @param v The orthogonal Y co-ordinate.
	 */
	public void assignOffsets(int h, int v)
	{
		this.x = h - v / 2;
		this.y = v;
		this.z = -(x + y);
	}
}
