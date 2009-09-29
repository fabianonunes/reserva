package ilist.ui;

import ilist.ui.generic.grid.GridCellRenderer;
import ilist.ui.generic.grid.JGrid;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import org.jdesktop.swingx.JXImagePanel;

import com.fabianonunes.solar.thumbs.model.PageImage;

public class ItemRendererWrapper implements GridCellRenderer {

	public JComponent getGridCellRendererComponent(JGrid<?> table,
			Object value, Dimension size, boolean isSelected, boolean hasFocus,
			int pos) {

		PageImage pageImage = (PageImage) value;

		JXImagePanel imagePanel = new JXImagePanel();
		imagePanel.setPreferredSize(new Dimension(pageImage.getImage()
				.getWidth(), pageImage.getImage().getHeight()));
		imagePanel.setImage((Image) pageImage.getImage());
		imagePanel.setBorder(new LineBorder(Color.GRAY));

		JPanel panel = new JPanel(true);
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setOpaque(false);
		panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

		JPanel inside = new JPanel(true);
		inside.setOpaque(false);
		inside.add(imagePanel);

		panel.add(inside);

		JLabel label = new JLabel(pageImage.getPageNumber().toString());
		label.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(label);

		return panel;
	}

}
