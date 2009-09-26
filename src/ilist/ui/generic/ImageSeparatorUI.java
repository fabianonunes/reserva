package ilist.ui.generic;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicToolBarSeparatorUI;

/**
 * @author Christophe Le Besnerais
 */
public class ImageSeparatorUI extends BasicToolBarSeparatorUI {

	private final Image image;


	public ImageSeparatorUI(Image image) {
		this.image = image;
	}

	
	@Override
	public void paint(Graphics g, JComponent c) {
		g.drawImage(image, 0, 0, null);
	}

	@Override
	public Dimension getPreferredSize(JComponent c) {
		return new Dimension(image.getWidth(null), image.getHeight(null));
	}

}
