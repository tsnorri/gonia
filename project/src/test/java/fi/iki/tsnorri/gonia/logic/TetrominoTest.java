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
import static org.junit.Assert.assertTrue;
import org.junit.*;


/**
 *
 * @author tsnorri
 */
public class TetrominoTest
{
	static class StubGameBoard implements GameBoard
	{
		@Override
		public int getWidth()
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}


		@Override
		public int getHeight()
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}


		@Override
		public boolean isValidAndVacant(HexPoint point)
		{
			return true;
		}


		@Override
		public boolean isOccupied(HexPoint point)
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}


		@Override
		public Color colorForOccupiedPoint(HexPoint point)
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}


		@Override
		public boolean areAllValidAndVacant(Collection<? extends HexPoint> points)
		{
			return true;
		}


		@Override
		public boolean hasSpaceLeft(Collection<? extends HexPoint> points)
		{
			return true;
		}


		@Override
		public boolean hasSpaceRight(Collection<? extends HexPoint> points)
		{
			return true;
		}


		@Override
		public boolean hasSpaceUnder(Collection<? extends HexPoint> points)
		{
			return true;
		}


		@Override
		public int occupySpace(HexPoint[] points, Color color)
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}
	}

	
	public TetrominoTest()
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


	private void checkPoints(Collection<? extends HexPoint> expected, Collection<? extends HexPoint> actual)
	{
		assertTrue(expected.containsAll(actual));
		assertTrue(actual.containsAll(expected));
	}

	
	public HexPoint[] expectedTetrominoT1InitialPosition()
	{
		HexPoint[] retval = {
			new HexPoint (-1, 0, 1),
			new HexPoint (0, 0, 0),
			new HexPoint (1, 0, -1),
			new HexPoint (-1, 1, 0)
		};
		return retval;
	}
	
	
	public HexPoint[][] expectedTetrominoT1Steps()
	{
		HexPoint[][] steps = {
			this.expectedTetrominoT1InitialPosition (),
			{
				new HexPoint (-2, 2, 0),
				new HexPoint (-1, 1, 0),
				new HexPoint (0, 0, 0),
				new HexPoint (-1, 2, -1),
			},
			{
				new HexPoint (-1, 2, -1),
				new HexPoint (-1, 1, 0),
				new HexPoint (-1, 0, 1),
				new HexPoint (0, 1, -1),
			},
			{
				new HexPoint (0, 1, -1),
				new HexPoint (-1, 1, 0),
				new HexPoint (-2, 1, 1),
				new HexPoint (0, 0, 0)
			},
			{
				new HexPoint (0, 0, 0),
				new HexPoint (-1, 1, 0),
				new HexPoint (-2, 2, 0),
				new HexPoint (-1, 0, 1),
			},
			{
				new HexPoint (-1, 0, 1),
				new HexPoint (-1, 1, 0),
				new HexPoint (-1, 2, -1),
				new HexPoint (-2, 1, 1),
			}
		};
		return steps;
	}
	
	
	@Test
	public void testRotateCW()
	{
		Tetromino t = Tetromino.tetrominoWithType(Tetromino.Type.T1);
		HexPoint[][] expectedSteps = this.expectedTetrominoT1Steps();
		GameBoard gb = new StubGameBoard();
		for (int i = 0; i < 7; i++)
		{
			int idx = i % expectedSteps.length;
			Collection<? extends HexPoint> currentPoints = t.getPoints();
			Collection<HexPoint> expectedPoints = Arrays.asList(expectedSteps[idx]);
			this.checkPoints(expectedPoints, currentPoints);
			t.rotateCW(gb);
		}
	}


	@Test
	public void testRotateCCW()
	{
		Tetromino t = Tetromino.tetrominoWithType(Tetromino.Type.T1);
		HexPoint[][] expectedSteps = this.expectedTetrominoT1Steps();
		GameBoard gb = new StubGameBoard();
		for (int i = 6; i >= 0; i--)
		{
			int idx = i % expectedSteps.length;
			Collection<? extends HexPoint> currentPoints = t.getPoints();
			Collection<HexPoint> expectedPoints = Arrays.asList(expectedSteps[idx]);
			this.checkPoints(expectedPoints, currentPoints);
			t.rotateCCW(gb);
		}
	}

	
	@Test
	public void testMove()
	{
		Tetromino t = Tetromino.tetrominoWithType(Tetromino.Type.T1);
		this.checkPoints(
			Arrays.asList(this.expectedTetrominoT1InitialPosition()),
			t.getPoints());

		{
			t.moveTo(0, 9);
			HexPoint[] expected = {
				new HexPoint (-5, 9, -4),
				new HexPoint (-4, 9, -5),
				new HexPoint (-3, 9, -6),
				new HexPoint (-5, 10, -5)
			};
			
			Collection<? extends HexPoint> currentPoints = t.getPoints();
			Collection<HexPoint> expectedPoints = Arrays.asList(expected);
			this.checkPoints(expectedPoints, currentPoints);
		}
	}


	@Test
	public void testDrop()
	{
		GameBoard gb = new StubGameBoard();
		Trajectory trajectory = new Trajectory();
		trajectory.setPreferredType(Trajectory.Type.Vertical);

		Tetromino t = Tetromino.tetrominoWithType(Tetromino.Type.T1);
		this.checkPoints(
			Arrays.asList(this.expectedTetrominoT1InitialPosition()),
			t.getPoints());
		t.moveTo(0, 9);

		{
			t.dropOne(trajectory, gb);
			HexPoint[] expected = {
				new HexPoint (-5, 8, -3),
				new HexPoint (-4, 8, -4),
				new HexPoint (-3, 8, -5),
				new HexPoint (-5, 9, -4)
			};
			Collection<? extends HexPoint> currentPoints = t.getPoints();
			Collection<HexPoint> expectedPoints = Arrays.asList(expected);
			this.checkPoints(expectedPoints, currentPoints);
		}

		{
			t.dropOne(trajectory, gb);
			HexPoint[] expected = {
				new HexPoint (-4, 7, -3),
				new HexPoint (-3, 7, -4),
				new HexPoint (-2, 7, -5),
				new HexPoint (-4, 8, -4)
			};
			Collection<? extends HexPoint> currentPoints = t.getPoints();
			Collection<HexPoint> expectedPoints = Arrays.asList(expected);
			this.checkPoints(expectedPoints, currentPoints);
		}
	}


	@Test
	public void testMoveLeft()
	{
		GameBoard gb = new StubGameBoard();
		Trajectory trajectory = new Trajectory();
		trajectory.setPreferredType(Trajectory.Type.Vertical);

		Tetromino t = Tetromino.tetrominoWithType(Tetromino.Type.T1);
		this.checkPoints(
			Arrays.asList(this.expectedTetrominoT1InitialPosition()),
			t.getPoints());
		t.moveTo(5, 5);
		t.moveLeft(gb);
		HexPoint[] expected = {
			new HexPoint (1, 5, -6),
			new HexPoint (2, 5, -7),
			new HexPoint (3, 5, -8),
			new HexPoint (1, 6, -7)
		};
		Collection<? extends HexPoint> currentPoints = t.getPoints();
		Collection<HexPoint> expectedPoints = Arrays.asList(expected);
		this.checkPoints(expectedPoints, currentPoints);
	}


	@Test
	public void testMoveRight()
	{
		GameBoard gb = new StubGameBoard();
		Trajectory trajectory = new Trajectory();
		trajectory.setPreferredType(Trajectory.Type.Vertical);

		Tetromino t = Tetromino.tetrominoWithType(Tetromino.Type.T1);
		this.checkPoints(
			Arrays.asList(this.expectedTetrominoT1InitialPosition()),
			t.getPoints());
		t.moveTo(5, 5);
		t.moveRight(gb);
		HexPoint[] expected =
		{
			new HexPoint (3, 5, -8),
			new HexPoint (4, 5, -9),
			new HexPoint (5, 5, -10),
			new HexPoint (3, 6, -9)
		};
		Collection<? extends HexPoint> currentPoints = t.getPoints();
		Collection<HexPoint> expectedPoints = Arrays.asList(expected);
		this.checkPoints(expectedPoints, currentPoints);
	}
}
