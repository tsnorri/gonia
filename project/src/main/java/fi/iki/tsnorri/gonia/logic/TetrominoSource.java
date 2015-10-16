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
 * A tetromino factory.
 * 
 * @author tsnorri
 */
public interface TetrominoSource
{
	/**
	 * The current tetromino.
	 * 
	 * @return Tetromino.
	 */
	public Tetromino getTetromino ();
	
	/**
	 * Create a new tetromino.
	 */
	public void createTetromino ();
}
