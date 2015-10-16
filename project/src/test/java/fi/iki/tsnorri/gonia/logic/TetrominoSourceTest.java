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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.junit.*;
import static org.junit.Assert.*;


/**
 *
 * @author tsnorri
 */
public class TetrominoSourceTest
{
	public TetrominoSourceTest()
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
	public void testSequential()
	{
		Tetromino.Type[] seq = {Tetromino.Type.J, Tetromino.Type.L, Tetromino.Type.O};
		SequentialTetrominoSource source = new SequentialTetrominoSource(seq);
		
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < seq.length; j++)
			{
				source.createTetromino();
				Tetromino tetromino = source.getTetromino();
				assertEquals(seq[j].toString(), tetromino.getName());
			}
		}
	}
	
	
	@Test
	public void testRandom()
	{
		Tetromino.Type[] types = {Tetromino.Type.J, Tetromino.Type.L, Tetromino.Type.O};
		RandomTetrominoSource source = new RandomTetrominoSource(types);

		Collection<String> names = new ArrayList<String>(types.length);
		for (int i = 0; i < types.length; i++)
			names.add(types[i].toString());

		for (int i = 0; i < 10000; i++)
		{
			source.createTetromino();
			Tetromino tetromino = source.getTetromino();
			assertTrue(names.contains(tetromino.getName()));
		}
	}
}
