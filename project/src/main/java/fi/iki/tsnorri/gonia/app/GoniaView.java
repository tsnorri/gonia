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

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Collection;
import java.util.Set;
import fi.iki.tsnorri.gonia.logic.HexPoint;
import fi.iki.tsnorri.gonia.logic.MutableHexPoint;
import fi.iki.tsnorri.gonia.logic.Tetromino;
import javax.swing.JPanel;


/**
 * The Gonia view.
 *
 * @author tsnorri
 */
public class GoniaView extends JPanel
{
	/**
	 * The view delegate.
	 */
	public static interface Delegate
	{
		/**
		 * The current tetromino.
		 *
		 * @return Tetromino.
		 */
		public Tetromino getCurrentTetromino();


		/**
		 * Check whether the given location is occupied and return its colour.
		 *
		 * @param point The location.
		 * @return The colour if the location was occupied, otherwise null.
		 */
		public Color colorForOccupiedPoint(HexPoint point);


		/**
		 * Tetromino trajectory.
		 *
		 * @return The points on the trajectory.
		 */
		public Set<HexPoint> trajectoryPoints();
	}

	final int rowCount;
	final int colCount;
	final Color trajectoryColor;
	String message;
	Font font = new Font("Sans Serif", Font.BOLD, 100);
	Delegate delegate;


	/**
	 * Constructor.
	 *
	 * @param colCount Game board width.
	 * @param rowCount Game board height.
	 * @param delegate The delegate.
	 */
	public GoniaView(int colCount, int rowCount, Delegate delegate)
	{
		if (null == delegate)
			throw new NullPointerException("Delegate may not be null.");

		this.rowCount = rowCount;
		this.colCount = colCount;
		this.trajectoryColor = new Color(0.8f, 0.8f, 0.8f);
		this.delegate = delegate;
		super.setBackground(Color.WHITE);
	}


	/**
	 * The view is focusable.
	 *
	 * @return true.
	 */
	@Override
	public boolean isFocusable()
	{
		return true;
	}


	public void setMessage(String message)
	{
		this.message = message;
	}


	@Override
	protected void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);
		Graphics2D graphics2D = (Graphics2D) graphics;
		final AffineTransform originalTransform = graphics2D.getTransform();

		final Dimension size = this.getSize();
		final double height = size.getHeight();
		final double width = size.getWidth();
		final double hexDim = 100.0;
		final double multiplier = 1.14; // Make the spacing tighter.
		final double diff = multiplier * hexDim - hexDim;
		final double translatedWidth = (100.0 * (0.5 + colCount));
		final double translatedHeight = (diff + 100.0 * (1 + (rowCount - 1) * (0.5 * Math.sqrt(3.0))));

		// Scale, move the hexes so that the top and bottom ones aren't cropped.
		graphics2D.scale(width / translatedWidth, height / translatedHeight);
		graphics2D.translate(0.0, diff / 2.0);
		final AffineTransform initialTransform = graphics2D.getTransform();

		Polygon hex = null;
		{
			// Create a hex polygon.
			final int nPoints = 6;
			int[] xCoords = new int[nPoints];
			int[] yCoords = new int[nPoints];
			for (int i = 0; i < nPoints; i++)
			{
				double angle = i * Math.PI / 3.0 + Math.PI / 6.0;
				xCoords[i] = (int) (0.5 * multiplier * hexDim * Math.cos(angle));
				yCoords[i] = (int) (0.5 * multiplier * hexDim * Math.sin(angle));
			}
			hex = new Polygon(xCoords, yCoords, nPoints);
		}

		// Enable antialiasing.
		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Draw the tetrominoes.
		graphics2D.translate(0.0, hexDim / 2.0);
		MutableHexPoint point = new MutableHexPoint(0, 0, 0);
		Tetromino currentTetromino = delegate.getCurrentTetromino();
		Collection<? extends HexPoint> tetrominoPoints = null;
		if (null != currentTetromino)
			tetrominoPoints = currentTetromino.getPoints();
		Set<HexPoint> trajectoryPoints = delegate.trajectoryPoints();
		for (int i = 0; i < rowCount; i++)
		{
			AffineTransform transform = graphics2D.getTransform();
			graphics2D.translate((0 == i % 2 ? 1.0 : 0.5) * hexDim, 0.0);

			for (int j = 0; j < colCount; j++)
			{
				Color pointColor = null;
				point.assignOffsets(j, rowCount - (1 + i));
				if (null != tetrominoPoints && tetrominoPoints.contains(point))
					graphics2D.setColor(currentTetromino.getColor());
				else if (null != trajectoryPoints && trajectoryPoints.contains(point))
					graphics2D.setColor(trajectoryColor);
				else if (null != (pointColor = delegate.colorForOccupiedPoint(point)))
					graphics2D.setColor(pointColor);
				else
					graphics2D.setColor(Color.GRAY);

				graphics.fillPolygon(hex);
				graphics2D.translate(hexDim, 0);
			}

			graphics2D.setTransform(transform);
			graphics2D.translate(0.0, hexDim * Math.sin(Math.PI / 3.0));
		}

		if (null != message)
		{
			graphics2D.setTransform(initialTransform);
			graphics2D.setFont(font);
			graphics2D.setColor(Color.BLACK);
			FontMetrics fm = graphics2D.getFontMetrics();
			float h = fm.stringWidth(message) / 2.0f;
			float v = fm.getDescent();
			graphics2D.drawString(message, (float) translatedWidth / 2.0f - h, (float) translatedHeight / 2.0f + v);
		}

		// Restore the original transformation matrix.
		graphics2D.setTransform(originalTransform);
	}
}
