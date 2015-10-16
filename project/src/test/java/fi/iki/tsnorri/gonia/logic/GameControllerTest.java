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

import org.junit.*;
import static org.junit.Assert.*;


/**
 *
 * @author tsnorri
 */
public class GameControllerTest
{
	public GameControllerTest()
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
	public void testCreation()
	{
		Tetromino.Type[] seq =
		{
			Tetromino.Type.T1
		};
		TetrominoSource source = new SequentialTetrominoSource(seq);
		GameController gc = new GameController(source, 5, 5);
	}


	@Test
	public void testRunning()
	{
		Tetromino.Type[] seq =
		{
			Tetromino.Type.T1
		};
		TetrominoSource source = new SequentialTetrominoSource(seq);
		GameController gc = new GameController(source, 5, 4);

		for (int i = 0; i < 11; i++)
			assertTrue(gc.step());

		assertFalse(gc.step());
	}


	@Test
	public void testDrop()
	{
		Tetromino.Type[] seq =
		{
			Tetromino.Type.T1
		};
		TetrominoSource source = new SequentialTetrominoSource(seq);
		GameController gc = new GameController(source, 5, 4);

		for (int i = 0; i < 11; i++)
		{
			assertTrue(gc.step());
			if (2 == i)
				break;
			assertTrue(gc.drop());
		}

		assertFalse(gc.drop());
	}
}
