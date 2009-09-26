package ilist.ui;

import ilist.ui.generic.grid.GridCellRenderer;
import ilist.ui.generic.grid.JGrid;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class ItemRendererWrapper implements GridCellRenderer {

	public JComponent getGridCellRendererComponent(JGrid table, Object value,
			Dimension size, boolean isSelected, boolean hasFocus, int pos) {

		JPanel panel = new JPanel(true);
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setOpaque(false);
		panel.setBorder(new EmptyBorder(4, 4, 4, 4));

		// JPanel inside = new JPanel(true);
		// inside.setOpaque(false);
		JComponent component = (JPanel) value;
		component.setBorder(new LineBorder(Color.GRAY));
		// // component.setPreferredSize(new Dimension(size.width - 30,
		// // size.height-30));
		// inside.add(component);

		panel.add(component);

		// JPanel inside = (JPanel) value;
		// inside.setOpaque(false);
		// panel.add(inside);

		return panel;
	}

}
