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

import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.WindowConstants;


/**
 * A GUI for the game.
 *
 * @author tsnorri
 */
public class Gui implements Runnable
{
	private JFrame frame;


	/**
	 * Constructor.
	 */
	public Gui()
	{
	}


	/**
	 * Create a new window and start the game.
	 */
	@Override
	public void run()
	{
		frame = new JFrame("Gonia");
		frame.setPreferredSize(new Dimension(480, 800));
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		GuiGameController controller = new GuiGameController();
		controller.prepare(frame);
		controller.startGame();

		frame.pack();
		frame.setVisible(true);
	}
}
