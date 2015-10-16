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
import static org.junit.Assert.*;
import org.junit.*;


/**
 *
 * @author tsnorri
 */
public class GameBoardTest
{
	protected static interface Check
	{
		public boolean check(GameBoard gb, Collection<? extends HexPoint> points);
	}
	
	
	static class CheckLeftSide implements Check
	{
		@Override
		public boolean check(GameBoard gb, Collection<? extends HexPoint> points)
		{
			return gb.hasSpaceLeft(points);
		}
	}


	static class CheckRightSide implements Check
	{
		@Override
		public boolean check(GameBoard gb, Collection<? extends HexPoint> points)
		{
			return gb.hasSpaceRight(points);
		}

	}


	static class CheckUnder implements Check
	{
		@Override
		public boolean check(GameBoard gb, Collection<? extends HexPoint> points)
		{
			return gb.hasSpaceUnder(points);
		}

	}

	
	public GameBoardTest()
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

	
	private boolean[][] convertToBooleanMatrix(int[][] input)
	{
		boolean[][] retval = new boolean[input.length][input[0].length];
		for (int i = 0; i < input.length; i++)
		{
			for (int j = 0; j < input[i].length; j++)
			{
				int val = input[i][j];
				assertTrue(
					String.format("Expected (%d, %d) to be either 0 or 1, was %d.", j, i, val),
					0 == val || 1 == val
				);
				if (1 == val)
					retval[i][j] = true;
				else
					retval[i][j] = false;
			}
		}
		return retval;
	}
	

	protected void performCheck(GameBoard gb, Check check, int[][] coords, boolean[] values)
	{
		assertEquals(coords.length, values.length);
		for (int i = 0; i < coords.length; i++)
		{
			int pair[] = coords[i];
			boolean value = values[i];
			assertEquals(2, pair.length);
			HexPoint[] points = {HexPoint.createWithOffsets (pair[0], pair[1])};
			assertEquals(value, check.check(gb, Arrays.asList(points)));
		}
	}

	
	@Test
	public void testCreation()
	{
		GameBoard gb = new ConcreteGameBoard(5, 4);
		gb.toString();
	}


	@Test
	public void testCreationBadWidth()
	{
		Exception exc = null;
		try
		{
			GameBoard gb = new ConcreteGameBoard(0, 4);
		}
		catch (IllegalArgumentException e)
		{
			exc = e;
		}

		assertNotNull(exc);
		assertEquals(IllegalArgumentException.class, exc.getClass());
		assertEquals("Width must be positive.", exc.getMessage());
	}


	@Test
	public void testCreationBadHeight()
	{
		Exception exc = null;
		try
		{
			GameBoard gb = new ConcreteGameBoard(5, 0);
		}
		catch (IllegalArgumentException e)
		{
			exc = e;
		}

		assertNotNull(exc);
		assertEquals(IllegalArgumentException.class, exc.getClass());
		assertEquals("Height must be positive.", exc.getMessage());
	}


	@Test
	public void testToString()
	{
		// Smoke test only.
		GameBoard gb = new ConcreteGameBoard(7, 9);
		assertNotNull(gb.toString());
	}


	private GameBoard createGameBoard(int[][] givenModel)
	{
		boolean[][] model = convertToBooleanMatrix(givenModel);
		final int height = model.length;
		final int width = model[0].length;
		GameBoard gb = new ConcreteGameBoard(width, height);

		HexPoint[] occupied = null;

		{
			int count = 0;
			for (int y = 0; y < height; y++)
			{
				for (int x = 0; x < width; x++)
				{
					if (true == model[y][x])
						count++;
				}
			}
			occupied = new HexPoint[count];
		}

		{
			int i = 0;
			for (int y = 0; y < height; y++)
			{
				for (int x = 0; x < width; x++)
				{
					if (true == model[y][x])
					{
						occupied[i] = HexPoint.createWithOffsets(x, y);
						i++;
					}
				}
			}
		}

		gb.occupySpace(occupied, Color.BLACK);
		return gb;
	}


	private void checkOccupancy (GameBoard gb, int[][] givenModel)
	{
		boolean[][] model = convertToBooleanMatrix(givenModel);
		for (int y = 0; y < model.length; y++)
		{
			for (int x = 0; x < model[y].length; x++)
			{
				boolean status = gb.isOccupied(HexPoint.createWithOffsets(x, y));
				assertEquals(String.format("Coordinates (%d,%d) didn't match.", x, y), model[y][x], status);
			}
		}
	}
	
	
	@Test
	public void testOccupancy ()
	{
		int[][] model = 
		{
			{0, 0, 0},
			{0, 1, 0},
			{0, 1, 0},
			{0, 1, 0},
			{0, 0, 0}
		};
		
			GameBoard gb = createGameBoard(model);
		checkOccupancy(gb, model);
	}

	
	@Test
	public void testOccupancyAndColor ()
	{
		final int width = 4;
		final int height = 5;
		GameBoard gb = new ConcreteGameBoard(width, height);
		gb.occupySpace (new HexPoint[] {HexPoint.createWithOffsets (1, 2)}, Color.yellow);
		gb.occupySpace (new HexPoint[] {HexPoint.createWithOffsets (3, 4)}, Color.blue);
		
		for (int i = 0; i < width; i++)
		{
			for (int j = 0; j < height; j++)
			{
				HexPoint point = HexPoint.createWithOffsets(i, j);
				if (i == 1 && j == 2)
				{
					assertTrue(gb.isOccupied(point));
					assertEquals(Color.yellow, gb.colorForOccupiedPoint(point));
				}
				else if (i == 3 && j == 4)
				{
					assertTrue(gb.isOccupied(point));
					assertEquals(Color.blue, gb.colorForOccupiedPoint(point));
				}
				else
				{
					assertFalse(gb.isOccupied(point));
					assertNull(gb.colorForOccupiedPoint(point));
				}
			}
		}
	}
	
	
	@Test
	public void testValidity()
	{
		int[][] model = {
			{0, 0},
			{0, 0},
			{0, 1}
		};
		GameBoard gb = this.createGameBoard(model);

		// Invalid points.
		{
			int[][] coords = {
				{-1, -1},
				{ 0, -1},
				{ 1, -1},
				{ 2, -1},
				{-1,  0},
				{ 2,  0},
				{-1,  1},
				{ 2,  1},
				{-1,  2},
				{ 2,  2},
				{-1,  3},
				{ 0,  3},
				{ 1,  3},
				{ 2,  3}
			};
			for (int i = 0; i < coords.length; i++)
				assertFalse(gb.isValidAndVacant(HexPoint.createWithOffsets(coords[i][0], coords[i][1])));
		}

		// Free points.
		{
			int[][] coords = {
				{0, 0},
				{1, 0},
				{0, 1},
				{1, 1},
				{0, 2}
			};
			for (int i = 0; i < coords.length; i++)
				assertTrue(gb.isValidAndVacant(HexPoint.createWithOffsets(coords[i][0], coords[i][1])));
		}

		// Taken points.
		assertFalse(gb.isValidAndVacant(HexPoint.createWithOffsets(1, 2)));
	}
	
	
	@Test
	public void testValidityMultiple ()
	{
		GameBoard gb = new ConcreteGameBoard(2, 2);
		HexPoint point = HexPoint.createWithOffsets(1, 1);
		gb.occupySpace (new HexPoint[] {point}, Color.yellow);
	
		HexPoint a1 = HexPoint.createWithOffsets(0, 1);
		HexPoint a2 = HexPoint.createWithOffsets(0, 0);
		HexPoint i1 = HexPoint.createWithOffsets(-1, -1);
	
		HexPoint[] availablePoints = {a1, a2};
		HexPoint[] oneTaken = {a1, a2, point};
		HexPoint[] oneInvalid = {a1, a2, i1};
	
		assertTrue(gb.areAllValidAndVacant(Arrays.asList(availablePoints)));
		assertFalse(gb.areAllValidAndVacant(Arrays.asList(oneTaken)));
		assertFalse(gb.areAllValidAndVacant(Arrays.asList(oneInvalid)));
	}
		
	
	@Test
	public void testOccupancyRightSide()
	{
		int[][] model = 
		{
			{0, 0, 0},
			{0, 1, 0},
			{0, 1, 0},
			{0, 1, 0},
			{0, 0, 0}
		};

		GameBoard gb = createGameBoard(model);
		checkOccupancy(gb, model);
		
		int[][] coords = {
			{0, 0},
			{0, 1},
			{0, 2},
			{0, 3},
			{0, 4},

			{1, 0},
			{1, 1},
			{1, 2},
			{1, 3},
			{1, 4},
		};
		
		boolean[] values = {
			true,
			false,
			false,
			false,
			true,
			
			true,
			true,
			true,
			true,
			true
		};
		
		this.performCheck(gb, new CheckRightSide(), coords, values);
	}


	@Test
	public void testOccupancyLeftSide()
	{
		int[][] model = 
		{
			{0, 0, 0},
			{0, 1, 0},
			{0, 1, 0},
			{0, 1, 0},
			{0, 0, 0}
		};
		
		GameBoard gb = createGameBoard(model);
		checkOccupancy(gb, model);

		int[][] coords = {
			{2, 0},
			{2, 1},
			{2, 2},
			{2, 3},
			{2, 4},

			{1, 0},
			{1, 1},
			{1, 2},
			{1, 3},
			{1, 4},
		};
		
		boolean[] values = {
			true,
			false,
			false,
			false,
			true,
			
			true,
			true,
			true,
			true,
			true
		};

		this.performCheck(gb, new CheckLeftSide(), coords, values);
	}


	@Test
	public void testOccupancyUnder()
	{
		int[][] model = 
		{
			{0, 0, 0},
			{0, 1, 0},
			{0, 1, 0},
			{0, 1, 0},
			{0, 0, 0}
		};

		GameBoard gb = createGameBoard(model);
		checkOccupancy(gb, model);

		int[][] coords = {
			{0, 1},
			{1, 1},
			{2, 1},

			{0, 2},
			{1, 2},
			{2, 2},

			{0, 3},
			{1, 3},
			{2, 3},

			{0, 4},
			{1, 4},
			{2, 4},
		};
		
		boolean[] values = {
			true,
			true,
			true,
			
			true,
			false,
			true,
			
			true,
			false,
			true,
			
			true,
			false,
			true
		};

		this.performCheck(gb, new CheckUnder(), coords, values);
	}


	@Test
	public void testSpaceOutsideGameBoard()
	{
		int[][] model = 
		{
			{0, 0, 0},
			{0, 1, 0},
			{0, 1, 0},
			{0, 1, 0},
			{0, 0, 0}
		};

		GameBoard gb = createGameBoard(model);
		checkOccupancy(gb, model);
		
		{
			int[][] coords = {
				{0, 0},
				{0, 1},
				{0, 2},
				{0, 3},
				{0, 4},
			};
					
			boolean[] values = {
				false,
				false,
				false,
				false,
				false
			};
			
			this.performCheck(gb, new CheckLeftSide(), coords, values);
		}
		
		{
			int[][] coords = {
				{2, 0},
				{2, 1},
				{2, 2},
				{2, 3},
				{2, 4},
			};
			
			boolean[] values = {
				false,
				false,
				false,
				false,
				false
			};

			this.performCheck(gb, new CheckRightSide(), coords, values);
		}
		
		{
			int[][] coords = {
				{0, 0},
				{1, 0},
				{2, 0},
			};
	
			boolean[] values = {
				false,
				false,
				false
			};

			this.performCheck(gb, new CheckUnder(), coords, values);
		}
	}

	
	@Test
	public void testFindLines()
	{
		int[][] model = 
		{
			{0, 0, 0},
			{0, 1, 0},
			{0, 1, 0},
			{0, 1, 0},
			{0, 0, 0}
		};
		
		GameBoard gb = createGameBoard(model);
		checkOccupancy(gb, model);
		
		gb.occupySpace(new HexPoint[] {HexPoint.createWithOffsets (0, 1)}, Color.BLACK);
		model[1][0] = 1;
		checkOccupancy(gb, model);

		gb.occupySpace (new HexPoint[] {HexPoint.createWithOffsets (2, 1)}, Color.BLACK);
		int[][] model2 = 
		{
			{0, 0, 0},
			{0, 1, 0},
			{0, 0, 1},
			{0, 0, 0},
			{0, 0, 0}
		};
		checkOccupancy(gb, model2);
	}


	@Test
	public void testFindConsecutiveAndNonConsecutiveLines()
	{
		int[][] model =
		{
			{0, 1, 0, 0},
			{1, 0, 0, 0},
			{0, 1, 0, 0},
			{0, 0, 1, 0},
			{1, 0, 0, 0},
			{0, 0, 0, 0},
			{0, 0, 1, 1},
			{0, 0, 1, 0},
			{0, 1, 0, 0},
		};

		GameBoard gb = createGameBoard(model);
		checkOccupancy(gb, model);

		int[][] newCoords =
		{
			{1, 1},
			{2, 1},
			{3, 1},
			{1, 3},
			{1, 4},
			{2, 4},
			{3, 4},
			{0, 5},
			{1, 5},
			{2, 5},
			{3, 5}
		};
		HexPoint[] newPoints = new HexPoint[newCoords.length];
		for (int i = 0; i < newCoords.length; i++)
			newPoints[i] = HexPoint.createWithOffsets(newCoords[i][0], newCoords[i][1]);

		gb.occupySpace(newPoints, Color.BLACK);

		int[][] model2 =
		{
			{0, 1, 0, 0},
			{0, 1, 0, 0},
			{0, 0, 1, 1},
			{0, 0, 1, 1},
			{0, 0, 0, 1},
			{0, 1, 0, 0},
			{0, 0, 0, 0},
			{0, 0, 0, 0},
			{0, 0, 0, 0},
		};
		checkOccupancy(gb, model2);
	}
}
