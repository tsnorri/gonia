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

import javax.swing.SwingUtilities;


/**
 * The main class.
 *
 * @author tsnorri
 */
public class Main
{
	public static void main(String[] args)
	{
		Gui gui = new Gui();
		SwingUtilities.invokeLater(gui);
	}
}
