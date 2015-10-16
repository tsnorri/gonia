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

import java.util.Set;
import org.javatuples.Pair;


/**
 * Game controller; takes care of the game process.
 *
 * @author tsnorri
 */
public class GameController
{
	private GameBoard gameBoard;
	private Trajectory trajectory;
	private TetrominoSource tetrominoSource;
	private Tetromino currentTetromino;
	private int blocks;
	private int lines;
	private int steps;
	private int score;


	/**
	 * Constructor.
	 *
	 * @param tetrominoSource Tetromino factory.
	 * @param width Game board width.
	 * @param height Game board height.
	 */
	public GameController(TetrominoSource tetrominoSource, int width, int height)
	{
		this.gameBoard = new ConcreteGameBoard(width, 4 + height);
		this.trajectory = new Trajectory();
		this.tetrominoSource = tetrominoSource;
		this.tetrominoSource.createTetromino();
	}


	/**
	 * Advance time.
	 *
	 * @return Boolean indicating whether the game continues.
	 */
	public boolean step()
	{
		boolean retval = true;
		if (null == currentTetromino)
		{
			blocks++;
			steps = 0;
			currentTetromino = tetrominoSource.getTetromino();
			tetrominoSource.createTetromino();

			int width = gameBoard.getWidth();
			currentTetromino.moveTo(width / 2, gameBoard.getHeight() - 4);
		}
		else
		{
			int res = currentTetromino.dropOne(trajectory, gameBoard);
			if (-1 == res)
				steps++;
			else
			{
				lines += res;
				if (steps < 1)
					retval = false;
				else
					addToScore(res, 0);
				currentTetromino = null;
			}
		}
		return retval;
	}


	/**
	 * Drop the current tetromino.
	 *
	 * @return Boolean indicating whether the game continues.
	 */
	public boolean drop()
	{
		boolean retval = true;
		if (null != currentTetromino)
		{
			Pair<Integer, Integer> res = currentTetromino.drop(trajectory, gameBoard);
			int distance = res.getValue0();
			int currentLines = res.getValue1();
			steps += distance;
			if (steps + currentLines < 1)
				retval = false;
			else
			{
				this.lines += currentLines;
				addToScore(currentLines, distance);
			}
			currentTetromino = null;
		}
		return retval;
	}


	/**
	 * Rotate the current tetromino clockwise.
	 */
	public void rotateCW()
	{
		if (null != currentTetromino)
			currentTetromino.rotateCW(gameBoard);
	}


	/**
	 * Rotate the current tetromino counterclockwise.
	 */
	public void rotateCCW()
	{
		if (null != currentTetromino)
			currentTetromino.rotateCCW(gameBoard);
	}


	/**
	 * Move the current tetromino left.
	 */
	public void moveLeft()
	{
		if (null != currentTetromino)
			currentTetromino.moveLeft(gameBoard);
	}


	/**
	 * Move the current tetromino right.
	 */
	public void moveRight()
	{
		if (null != currentTetromino)
			currentTetromino.moveRight(gameBoard);
	}


	/**
	 * The game board associated with the controller.
	 *
	 * @return The game board.
	 */
	public GameBoard getGameBoard()
	{
		return gameBoard;
	}


	/**
	 * The current tetromino.
	 *
	 * @return A tetromino.
	 */
	public Tetromino getCurrentTetromino()
	{
		return currentTetromino;
	}


	/**
	 * The current score.
	 *
	 * @return The score.
	 */
	public int getScore()
	{
		return score;
	}


	/**
	 * The number of lines cleared.
	 *
	 * @return The number of lines.
	 */
	public int getLines()
	{
		return lines;
	}


	/**
	 * Set the preferred tetromino trajectory.
	 *
	 * @param prerredType The preferred trajectory.
	 */
	public void setPreferredTrajectory(Trajectory.Type prerredType)
	{
		trajectory.setPreferredType(prerredType);
	}


	/**
	 * The points on the trajectory of the current tetromino.
	 *
	 * @return A set of points.
	 */
	public Set<HexPoint> trajectoryPoints()
	{
		Set<HexPoint> retval = null;
		if (null != currentTetromino)
			retval = trajectory.remainingPoints(currentTetromino.getPoints(), gameBoard);
		return retval;
	}


	/**
	 * Increase score.
	 *
	 * @param lines The number of lines cleared.
	 * @param droppedDistance The distance from which the player dropped the
	 * tetromino.
	 */
	public void addToScore(int lines, int droppedDistance)
	{
		score += blocks / 3 + steps + 2 * droppedDistance;
		if (0 < lines)
			score += Math.pow(20, lines);
	}
}
