package ilist.ui.generic;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

/**
 * @author Christophe Le Besnerais
 */
@SuppressWarnings("serial")
public class ImageSplitPaneUI extends BasicSplitPaneUI {	
	
	private final Image image;
	

	public ImageSplitPaneUI(Image image) {
		this.image = image;
	}
	
	
	@Override
	public BasicSplitPaneDivider createDefaultDivider() {
		BasicSplitPaneDivider divider = new BasicSplitPaneDivider(this) {
			@Override
			public void paint(Graphics g) {
				g.drawImage(image, (this.getWidth() - image.getWidth(null)) / 2, 0, null);
			}
		};
		
		return divider;
	}
	
	@Override
	protected void installDefaults() {
		super.installDefaults();
		this.splitPane.setBorder(null);
	}
	
	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		this.splitPane.setDividerSize(image.getHeight(null)+2);
	}
	
}
