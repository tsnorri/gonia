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
import org.junit.*;
import static org.junit.Assert.*;


/**
 *
 * @author tsnorri
 */
public class HexPointTest
{
	public HexPointTest()
	{
	}


	@BeforeClass
	public static void setUpClass() throws Exception
	{
	}


	@AfterClass
	public static void tearDownClass() throws Exception
	{
	}


	@Before
	public void setUp()
	{
	}


	@After
	public void tearDown()
	{
	}
	
	
	@Test
	public void testCreation1()
	{
		HexPoint c = new HexPoint(-1, 0, 1);
	}
	
	
	@Test
	public void testCreation2()
	{
		HexPoint c = new HexPoint(1, 1, -2);
	}
	
	
	@Test
	public void testCreation3()
	{
		HexPoint c = new HexPoint(0, 0, 0);
	}
	
	
	@Test
	public void testCreationByOffsets1()
	{
		HexPoint c = HexPoint.createWithOffsets(0, 0);
		assertEquals (0, c.getHorizontalOffset());
		assertEquals (0, c.getY());
	}


	@Test
	public void testCreationByOffsets2()
	{
		HexPoint c = HexPoint.createWithOffsets(5, 4);
		assertEquals(5, c.getHorizontalOffset());
		assertEquals(4, c.getY());
	}

	
	@Test
	public void testInvalidCoordinates()
	{
		Exception exc = null;
		try
		{
			HexPoint c = new HexPoint(1, 1, -1);
		}
		catch (IllegalArgumentException e)
		{
			exc = e;
		}

		assertNotNull(exc);
		assertEquals(IllegalArgumentException.class, exc.getClass());
		assertEquals("The sum of coordinates must be zero.", exc.getMessage());
	}


	@Test
	public void testEquals1()
	{
		HexPoint c1 = new HexPoint(1, -1, 0);
		HexPoint c2 = new HexPoint(1, -1, 0);
		assertEquals(c1, c2);
	}
	
	
	@Test
	public void testEquals2()
	{
		HexPoint c1 = new HexPoint(1, -1, 0);
		HexPoint c2 = new HexPoint(1, 0, -1);
		assertFalse(c1.equals (c2));
	}
	
	
	@Test
	public void testEquals3()
	{
		HexPoint c = new HexPoint(1, -1, 0);
		String s = "s";
		assertFalse(c.equals (s));
	}
	
	
	@Test
	public void testSort()
	{
		HexPoint p1 = HexPoint.createWithOffsets(0, 0);
		HexPoint p2 = HexPoint.createWithOffsets(0, 1);
		HexPoint p3 = HexPoint.createWithOffsets(3, 1);
		HexPoint p4 = HexPoint.createWithOffsets(2, 3);

		HexPoint[] initial = {p2, p4, p3, p1};
		HexPoint[] expected = {p1, p2, p3, p4};
		Arrays.sort (initial, new HexPoint.Comparator());

		assertArrayEquals(expected, initial);
	}
	
	
	@Test
	public void testHash()
	{
		// Smoke test only.
		HexPoint p = new HexPoint(1, 2, -3);
		assertNotSame (0, p.hashCode ());
	}
	
	
	@Test
	public void testInvalidTransformation1()
	{
		int[][] matrix = {
			{1, 0},
			{0, 1}
		};
		HexPoint p = new HexPoint(0, 1, -1);
		try
		{
			p.transformedCopy(matrix);
		}
		catch (IllegalArgumentException exc)
		{
			assertEquals("Transformation matrix must be 4×4.", exc.getMessage());
		}
	}

	
	@Test
	public void testInvalidTransformationMutable1()
	{
		int[][] matrix = {
			{1, 0},
			{0, 1}
		};
		MutableHexPoint p = new MutableHexPoint(0, 1, -1);
		try
		{
			p.transform(matrix);
		}
		catch (IllegalArgumentException exc)
		{
			assertEquals("Transformation matrix must be 4×4.", exc.getMessage());
		}
	}
	
	
	@Test
	public void testInvalidTransformation2()
	{
		int[][] matrix = {
			{1, 0, 0, 0},
			{0, 1, 0, 0},
			{0, 0, 1, 0},
			{1, 2, 3, 1}
		};
		HexPoint p = new HexPoint(0, 1, -1);
		try
		{
			p.transformedCopy(matrix);
		}
		catch (IllegalArgumentException exc)
		{
			assertEquals("The sum of translations must be zero.", exc.getMessage());
		}
	}

	
	@Test
	public void testInvalidTransformationMutable2()
	{
		int[][] matrix = {
			{1, 0, 0, 0},
			{0, 1, 0, 0},
			{0, 0, 1, 0},
			{1, 2, 3, 1}
		};
		MutableHexPoint p = new MutableHexPoint(0, 1, -1);
		try
		{
			p.transformedCopy(matrix);
		}
		catch (IllegalArgumentException exc)
		{
			assertEquals("The sum of translations must be zero.", exc.getMessage());
		}
	}
	
		
	@Test
	public void testTransformedCopy1()
	{
		int[][] matrix = {
			{ 0,  0, -1,  0},
			{-1,  0,  0,  0},
			{ 0, -1,  0,  0},
			{ 0,  0,  0,  1}
		};
		HexPoint c1 = new HexPoint(0, 1, -1);
		HexPoint c2 = c1.transformedCopy(matrix);
		HexPoint expected = new HexPoint(-1, 1, 0);
		assertEquals(expected, c2);
	}


	@Test
	public void testTransformedCopy2()
	{
		int[][] matrix = {
			{ 0, -1,  0,  0},
			{ 0,  0, -1,  0},
			{-1,  0,  0,  0},
			{ 0,  0,  0,  1}
		};
		HexPoint c1 = new HexPoint(0, 1, -1);
		HexPoint c2 = c1.transformedCopy(matrix);
		HexPoint expected = new HexPoint(1, 0, -1);
		assertEquals(expected, c2);
	}


	@Test
	public void testTransformedCopy3()
	{
		HexPoint c1 = HexPoint.createWithOffsets(0, 0);
		int[][] matrix = HexPoint.translationTransformation(2, 0, true);
		HexPoint c2 = c1.transformedCopy(matrix);
		assertEquals(0, c1.getHorizontalOffset());
		assertEquals(0, c1.getY());
		assertEquals(2, c2.getHorizontalOffset());
		assertEquals(0, c2.getY());
	}

	
	@Test
	public void testTransformedCopy4()
	{
		HexPoint c1 = HexPoint.createWithOffsets (3, 1);
		int[][] matrix = HexPoint.translationTransformation(1, 0, false);
		HexPoint c2 = c1.transformedCopy(matrix);
		assertEquals(3, c1.getHorizontalOffset());
		assertEquals(1, c1.getY());
		assertEquals(4, c2.getHorizontalOffset());
		assertEquals(1, c2.getY());
	}


	@Test
	public void testTransformMultiple1()
	{
		HexPoint c1 = HexPoint.createWithOffsets(1, 0);
		HexPoint c2 = HexPoint.createWithOffsets(1, 1);

		HexPoint[] transformed = HexPoint.copyWithOrthogonalTranslation(new HexPoint[] {c1, c2}, 0, 1);
		assertEquals (2, transformed.length);

		HexPoint tc1 = transformed[0];
		HexPoint tc2 = transformed[1];
		assertEquals(1, tc1.getHorizontalOffset());
		assertEquals(2, tc2.getHorizontalOffset());
		assertEquals(1, tc1.getY());
		assertEquals(2, tc2.getY());
	}


	@Test
	public void testTransformMultiple2()
	{
		HexPoint c1 = HexPoint.createWithOffsets(1, 0);
		HexPoint c2 = HexPoint.createWithOffsets(1, 1);

		HexPoint[] transformed = HexPoint.copyWithOrthogonalTranslation(new HexPoint[] {c1, c2}, 0, -1);
		assertEquals(2, transformed.length);

		HexPoint tc1 = transformed[0];
		HexPoint tc2 = transformed[1];
		assertEquals(1, tc1.getHorizontalOffset());
		assertEquals(2, tc2.getHorizontalOffset());
		assertEquals(-1, tc1.getY());
		assertEquals(0, tc2.getY());
	}


	@Test
	public void testTransformMultipleMutable1()
	{
		MutableHexPoint c1 = MutableHexPoint.createWithOffsets(1, 0);
		MutableHexPoint c2 = MutableHexPoint.createWithOffsets(1, 1);

		HexPoint.applyOrthogonalTranslation(new MutableHexPoint[] {c1, c2}, 0, 1);
		
		assertEquals(1, c1.getHorizontalOffset());
		assertEquals(2, c2.getHorizontalOffset());
		assertEquals(1, c1.getY());
		assertEquals(2, c2.getY());
	}


	@Test
	public void testTransformMultipleMutable2()
	{
		MutableHexPoint c1 = MutableHexPoint.createWithOffsets(1, 0);
		MutableHexPoint c2 = MutableHexPoint.createWithOffsets(1, 1);

		HexPoint.applyOrthogonalTranslation(new MutableHexPoint[] {c1, c2}, 0, -1);
		
		assertEquals(1, c1.getHorizontalOffset());
		assertEquals(2, c2.getHorizontalOffset());
		assertEquals(-1, c1.getY());
		assertEquals(0, c2.getY());
	}


	@Test
	public void testCopyFrom()
	{
		HexPoint p1 = new HexPoint(1, 2, -3);
		MutableHexPoint p2 = new MutableHexPoint(0, 0, 0);

		assertNotSame(p2, p1);
		p2.copyFrom(p1);
		assertEquals(p2, p1);
	}


	@Test
	public void testAssignOffsets()
	{
		final int h = 2;
		final int v = 3;
		HexPoint p1 = HexPoint.createWithOffsets(h, v);
		MutableHexPoint p2 = MutableHexPoint.createWithOffsets(0, 0);

		assertNotSame(p2, p1);
		p2.assignOffsets(h, v);
		assertEquals(p2, p1);
	}
}
