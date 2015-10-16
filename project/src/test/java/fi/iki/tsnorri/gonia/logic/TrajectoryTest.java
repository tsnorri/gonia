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
import java.util.List;
import java.util.Set;
import static org.junit.Assert.assertTrue;
import org.junit.*;


/**
 *
 * @author tsnorri
 */
public class TrajectoryTest
{
	public TrajectoryTest()
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


	public void next(Trajectory.Type trajectoryType, HexPoint[] initial, HexPoint[] expected)
	{
		List<HexPoint> initialList = Arrays.asList(initial);
		List<HexPoint> expectedList = Arrays.asList(expected);

		Trajectory trajectory = new Trajectory();
		trajectory.setPreferredType(trajectoryType);
		GameBoard gb = new ConcreteGameBoard(6, 6);

		Collection<HexPoint> nextPoints = trajectory.nextPoints(initialList, gb);

		assertTrue(expectedList.containsAll(nextPoints));
		assertTrue(nextPoints.containsAll(expectedList));
	}


	public void remaining(Trajectory.Type trajectoryType, HexPoint[] initialPoints, HexPoint[] expected)
	{
		GameBoard gb = new ConcreteGameBoard(6, 6);
		remaining(trajectoryType, initialPoints, expected, gb);
	}


	public void remaining(Trajectory.Type trajectoryType, HexPoint[] initial, HexPoint[] expected, GameBoard gb)
	{
		List<HexPoint> initialList = Arrays.asList(initial);
		List<HexPoint> expectedList = Arrays.asList(expected);

		Trajectory trajectory = new Trajectory();
		trajectory.setPreferredType(trajectoryType);

		Set<HexPoint> remainingPoints = trajectory.remainingPoints(initialList, gb);

		assertTrue(expectedList.containsAll(remainingPoints));
		assertTrue(remainingPoints.containsAll(expectedList));
	}

	
	@Test
	public void testNextDiagonalLeft1()
	{
		HexPoint[] initialPoints =
		{
			new HexPoint(0, 3, -3),
			new HexPoint(0, 4, -4),
		};

		HexPoint[] expectedPoints =
		{
			new HexPoint(0, 2, -2),
			new HexPoint(0, 3, -3)
		};

		next(Trajectory.Type.DiagonalLeft, initialPoints, expectedPoints);
	}


	@Test
	public void testNextDiagonalLeft2()
	{
		HexPoint[] initialPoints =
		{
			new HexPoint(0, 4, -4),
			new HexPoint(1, 4, -5),
		};

		HexPoint[] expectedPoints =
		{
			new HexPoint(0, 3, -3),
			new HexPoint(1, 3, -4),
		};

		next(Trajectory.Type.DiagonalLeft, initialPoints, expectedPoints);
	}


	@Test
	public void testNextDiagonalRight1()
	{
		HexPoint[] initialPoints =
		{
			new HexPoint(-2, 4, -2),
			new HexPoint(-1, 3, -2),
		};

		HexPoint[] expectedPoints =
		{
			new HexPoint(-1, 3, -2),
			new HexPoint(0, 2, -2)
		};

		next(Trajectory.Type.DiagonalRight, initialPoints, expectedPoints);
	}

	
	@Test
	public void testNextDiagonalRight2()
	{
		HexPoint[] initialPoints =
		{
			new HexPoint(-2, 4, -2),
			new HexPoint(-1, 4, -3),
		};

		HexPoint[] expectedPoints =
		{
			new HexPoint(-1, 3, -2),
			new HexPoint(0, 3, -3),
		};

		next(Trajectory.Type.DiagonalLeft, initialPoints, expectedPoints);
	}
	
	
	@Test
	public void testRemainingDiagonalLeft1()
	{
		HexPoint[] initialPoints = {
			new HexPoint(0, 4, -4),
			new HexPoint(1, 4, -5),
		};
		
		HexPoint[] expectedPoints = {
			new HexPoint(0, 0, 0),
			new HexPoint(0, 1, -1),
			new HexPoint(0, 2, -2),
			new HexPoint(0, 3, -3),
			new HexPoint(1, 0, -1),
			new HexPoint(1, 1, -2),
			new HexPoint(1, 2, -3),
			new HexPoint(1, 3, -4)
		};
		
		remaining(Trajectory.Type.DiagonalLeft, initialPoints, expectedPoints);
	}
	

	@Test
	public void testRemainingDiagonalLeft2()
	{
		HexPoint[] initialPoints = {
			new HexPoint(0, 4, -4),
			new HexPoint(1, 4, -5),
		};
		
		HexPoint[] expectedPoints = {
			new HexPoint(0, 0, 0),
			new HexPoint(0, 1, -1),
			new HexPoint(0, 2, -2),
			new HexPoint(0, 3, -3),
			new HexPoint(1, 0, -1),
			new HexPoint(1, 1, -2),
			new HexPoint(1, 2, -3),
			new HexPoint(1, 3, -4),
		};
		
		remaining(Trajectory.Type.DiagonalLeft, initialPoints, expectedPoints);
	}


	@Test
	public void testRemainingDiagonalRight1()
	{
		HexPoint[] initialPoints = {
			new HexPoint(-2, 4, -2),
			new HexPoint(-1, 3, -2),
		};
		
		HexPoint[] expectedPoints = {
			new HexPoint(-1, 3, -2),
			new HexPoint(0, 2, -2),
			new HexPoint(1, 1, -2),
			new HexPoint(2, 0, -2),
		};
		
		remaining(Trajectory.Type.DiagonalRight, initialPoints, expectedPoints);
	}
	
	
	@Test
	public void testRemainingDiagonalRight2()
	{
		HexPoint[] initialPoints = {
			new HexPoint(-2, 4, -2),
			new HexPoint(-1, 4, -3),
		};
		
		HexPoint[] expectedPoints = {
			new HexPoint(-1, 3, -2),
			new HexPoint(0, 2, -2),
			new HexPoint(1, 1, -2),
			new HexPoint(2, 0, -2),
			new HexPoint(0, 3, -3),
			new HexPoint(1, 2, -3),
			new HexPoint(2, 1, -3),
			new HexPoint(3, 0, -3)
		};
		
		remaining(Trajectory.Type.DiagonalRight, initialPoints, expectedPoints);
	}
	
	
	@Test
	public void testNextVertical1()
	{
		HexPoint[] initialPoints = {
			new HexPoint(-1, 3, -2),
			new HexPoint(-1, 4, -3),
		};
		
		HexPoint[] expectedPoints = {
			new HexPoint(-1, 2, -1),
			new HexPoint(-1, 3, -2),
		};

		next(Trajectory.Type.Vertical, initialPoints, expectedPoints);
	}


	@Test
	public void testNextVertical2()
	{
		HexPoint[] initialPoints = {
			new HexPoint(-1, 2, -1),
			new HexPoint(-1, 3, -2),
		};
		
		HexPoint[] expectedPoints = {
			new HexPoint(0, 1, -1),
			new HexPoint(0, 2, -2)
		};

		next(Trajectory.Type.Vertical, initialPoints, expectedPoints);
	}
	
	
	@Test
	public void testDodgeDiagonal()
	{
		GameBoard gb = new ConcreteGameBoard(6, 6);
		gb.occupySpace(new HexPoint[]
		{
			new HexPoint(0, 2, -2)
		}, Color.yellow);

		HexPoint[] initialPoints =
		{
			new HexPoint(0, 5, -5),
			new HexPoint(0, 4, -4)
		};
		
		HexPoint[] expectedPoints = {
			new HexPoint(0, 4, -4),
			new HexPoint(0, 3, -3),
			new HexPoint(1, 3, -4),
			new HexPoint(1, 2, -3),
			new HexPoint(1, 1, -2),
			new HexPoint(1, 0, -1)
		};

		remaining(Trajectory.Type.DiagonalLeft, initialPoints, expectedPoints, gb);
	}
	
	
	@Test
	public void testDodgeVertical ()
	{
		GameBoard gb = new ConcreteGameBoard(6, 6);
		gb.occupySpace (new HexPoint[] {new HexPoint(1, 1, -2)}, Color.yellow);

		HexPoint[] initialPoints = {
			new HexPoint(-1, 4, -3),
			new HexPoint(0, 3, -3)
		};
		
		HexPoint[] expectedPoints = {
			new HexPoint(-1, 3, -2),
			new HexPoint(0, 2, -2),
			new HexPoint(0, 1, -1),
			new HexPoint(-1, 2, -1),
			new HexPoint(1, 0, -1),
		};
		
		remaining(Trajectory.Type.Vertical, initialPoints, expectedPoints, gb);
	}
}
