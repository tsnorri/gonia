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
 * Returns randomly chosen tetrominos.
 *
 * @author tsnorri
 */
public class RandomTetrominoSource implements TetrominoSource
{
	Tetromino currentTetromino;
	Tetromino.Type[] allowedTypes;


	/**
	 * Constructor.
	 *
	 * @param allowedTypes Types of the created tetrominos.
	 */
	public RandomTetrominoSource(Tetromino.Type[] allowedTypes)
	{
		this.allowedTypes = allowedTypes.clone();
	}


	@Override
	public Tetromino getTetromino()
	{
		return currentTetromino;
	}


	@Override
	public void createTetromino()
	{
		int idx = (int) Math.floor(allowedTypes.length * Math.random());
		Tetromino.Type tetrominoType = this.allowedTypes[idx];
		currentTetromino = Tetromino.tetrominoWithType(tetrominoType);
	}
}
