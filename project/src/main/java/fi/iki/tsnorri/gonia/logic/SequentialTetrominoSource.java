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
 * Returns tetrominos in given order.
 *
 * @author tsnorri
 */
public class SequentialTetrominoSource implements TetrominoSource
{
	Tetromino currentTetromino;
	Tetromino.Type[] sequence;
	int idx;


	/**
	 * Constructor.
	 *
	 * @param sequence The types of the created tetrominos in order.
	 */
	public SequentialTetrominoSource(Tetromino.Type[] sequence)
	{
		this.sequence = sequence.clone();
	}


	@Override
	public Tetromino getTetromino()
	{
		return currentTetromino;
	}


	@Override
	public void createTetromino()
	{
		Tetromino.Type tetrominoType = this.sequence[this.idx];
		this.idx = (1 + this.idx) % this.sequence.length;

		currentTetromino = Tetromino.tetrominoWithType(tetrominoType);
	}
}
