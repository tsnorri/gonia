/*
 * Copyright (c) 2012, 2015 Tuukka Norri, tsnorri@iki.fi.
 *
 * This software is licensed as described in the file COPYING, which
 * you should have received as part of this distribution.
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY
 * KIND, either express or implied.
 */
package fi.iki.tsnorri.gonia.app;


import fi.iki.tsnorri.gonia.logic.RandomTetrominoSource;
import fi.iki.tsnorri.gonia.logic.Tetromino;
import fi.iki.tsnorri.gonia.logic.HexPoint;
import fi.iki.tsnorri.gonia.logic.Trajectory;
import fi.iki.tsnorri.gonia.logic.GameController;
import fi.iki.tsnorri.gonia.logic.TetrominoSource;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.Set;
import java.util.prefs.Preferences;
import javax.swing.*;



/**
 * View controller for GoniaView.
 * 
 * @author tsnorri
 */
public class GuiGameController
{
	private JFrame parentFrame;
	private GameController gameController;
	private GoniaView goniaView;
	private JTextField scoreField;
	private JTextField highScoreField;
	private JTextField linesField;
	private Timer timer;
	private boolean gameInProgress;
	
	private static final String HIGHSCORE_AMOUNT_KEY = "highscore.amount";
	private static final String HIGHSCORE_OWNER_KEY = "highscore.owner";
	private static final int ROW_COUNT = 18;
	private static final int COL_COUNT = 10;
	private static final int SPEED = 500;
	
	/**
	 * Constructor.
	 */
	public GuiGameController ()
	{
		timer = new Timer (SPEED, new ActionListener () {
			@Override
			public void actionPerformed (ActionEvent ae)
			{
				boolean status = gameController.step ();
				goniaView.repaint ();

				updateStats ();

				if (!status)
					finishGame ();
			}
		});
		timer.setInitialDelay (0);
		timer.setRepeats (true);
	}
	
	
	/**
	 * Add GoniaView to a superview.
	 * @param frame The view to which GoniaView will be added.
	 */
	void prepare (JFrame frame)
	{
		parentFrame = frame;
		
		goniaView = new GoniaView (COL_COUNT, ROW_COUNT, new GoniaView.Delegate ()
		{
			@Override
			public Tetromino getCurrentTetromino ()
			{
				return gameController.getCurrentTetromino ();
			}


			@Override
			public Color colorForOccupiedPoint (HexPoint point)
			{
				return gameController.getGameBoard ().colorForOccupiedPoint (point);
			}


			@Override
			public Set<HexPoint> trajectoryPoints ()
			{
				return gameController.trajectoryPoints ();
			}
		});
		
		goniaView.addKeyListener (new KeyListener ()
		{
			@Override
			public void keyTyped (KeyEvent evt)
			{
				if (gameInProgress)
				{
					final char input = Character.toLowerCase (evt.getKeyChar ());
					if (timer.isRunning ())
					{
						switch (input)
						{
							case 'p':
								goniaView.setMessage ("Paused");
								timer.stop ();
								break;

							case 'j':
								gameController.moveLeft ();
								break;

							case 'k':
								gameController.rotateCW ();
								break;

							case 'i':
								gameController.rotateCCW ();
								break;

							case 'l':
								gameController.moveRight ();
								break;

							case 'm':
								gameController.setPreferredTrajectory (Trajectory.Type.DiagonalLeft);
								break;

							case ',':
								gameController.setPreferredTrajectory (Trajectory.Type.Vertical);
								break;

							case '.':
								gameController.setPreferredTrajectory (Trajectory.Type.DiagonalRight);
								break;

							case ' ':
							{
								boolean status = gameController.drop ();
								updateStats ();
								if (!status)
									finishGame ();
								break;
							}
						}
					}
					else
					{
						if ('p' == input)
						{
							goniaView.setMessage (null);
							timer.start ();
						}
					}
					goniaView.repaint ();
				}
			}


			@Override
			public void keyPressed (KeyEvent ke)
			{
			}


			@Override
			public void keyReleased (KeyEvent ke)
			{
			}
		});
		goniaView.setVisible (true);
		frame.add (goniaView);
		
		JPanel northPanel = new JPanel (new BorderLayout ());
		frame.add (northPanel, BorderLayout.NORTH);
		
		{
			JButton button = new JButton ("New Game");
			button.setFocusable (false);
			button.addActionListener (new ActionListener () {
				@Override
				public void actionPerformed (ActionEvent ae)
				{
					startGame ();
				}
			});
			northPanel.add (button, BorderLayout.NORTH);
		}
		{
			JPanel subpanel = new JPanel (new GridLayout (3, 2));
			
			JLabel linesText = new JLabel ("Lines");
			JLabel scoreText = new JLabel ("Score");
			JLabel highScoreText = new JLabel ("High Score");
			
			linesField = new JTextField ();
			scoreField = new JTextField ();
			highScoreField = new JTextField ();
			
			JTextField[] fields = {linesField, scoreField, highScoreField};
			for (JTextField field : Arrays.asList (fields))
			{
				field.setVisible (true);
				field.setEnabled (true);
				field.setEditable (false);
				field.setFocusable (false);
			}
			
			{
				Preferences prefs = Preferences.userNodeForPackage (this.getClass ());
				String owner = prefs.get (HIGHSCORE_OWNER_KEY, null);
				if (null == owner)
					highScoreField.setText ("None");
				else
				{
					int amount = prefs.getInt (HIGHSCORE_AMOUNT_KEY, 0);
					showHighScore (owner, amount);
				}
			}
			
			subpanel.add (scoreText);
			subpanel.add (scoreField);
			subpanel.add (highScoreText);
			subpanel.add (highScoreField);
			subpanel.add (linesText);
			subpanel.add (linesField);

			northPanel.add (subpanel, BorderLayout.CENTER);
		}
		
		goniaView.grabFocus ();
	}
	
	
	/**
	 * Start the game.
	 */
	void startGame ()
	{
		gameInProgress = true;
		
		Tetromino.Type[] allowedTypes = Tetromino.Type.values ();
		//Tetromino.Type[] allowedTypes = {Tetromino.Type.C};
		TetrominoSource source = new RandomTetrominoSource (allowedTypes);
		gameController = new GameController (source, COL_COUNT, ROW_COUNT);
		
		goniaView.setMessage (null);
		
		timer.restart ();
	};


	/**
	 * Pysäytä ajastin ja tarkista, ylittikö pistemäärä edellisen ennätyksen.
	 */
	private void finishGame ()
	{
		gameInProgress = false;
		timer.stop ();
		goniaView.setMessage ("Game Over");
		goniaView.repaint ();
		Preferences prefs = Preferences.userNodeForPackage (this.getClass ());
		int highScore = prefs.getInt (HIGHSCORE_AMOUNT_KEY, 0);
		int score = gameController.getScore ();
		if (highScore < score)
		{
			String answer = JOptionPane.showInputDialog (parentFrame, "New high score!", "Your name");
			prefs.put (HIGHSCORE_OWNER_KEY, answer);
			prefs.putInt (HIGHSCORE_AMOUNT_KEY, score);
			showHighScore (answer, score);
		}
	}


	/**
	 * Näytä ennätyspistemäärä.
	 * @param owner Ennätyksen tekijä.
	 * @param amount Pistemäärä.
	 */
	private void showHighScore (String owner, int amount)
	{
		highScoreField.setText (String.format ("%d (%s)", amount, owner));
	}


	/**
	 * Päivitä tekstikentät.
	 */
	private void updateStats ()
	{
		scoreField.setText (String.format ("%d", gameController.getScore ()));
		linesField.setText (String.format ("%d", gameController.getLines ()));
	}
}
