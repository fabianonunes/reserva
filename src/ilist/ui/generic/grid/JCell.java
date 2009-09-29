package ilist.ui.generic.grid;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.AbstractLayerUI;

public class JCell<T extends Comparable<T>> extends JPanel implements
		Comparable<JCell<T>> {

	private static final long serialVersionUID = 1L;
	private int index;
	private JComponent component;
	private JXLayer<JComponent> layer;
	private T comparable;
	private JGrid<T> grid;

	/**
	 * @param index
	 *            position inside the grid
	 * @param component
	 *            the component inside the grid, given by the renderer
	 */
	public JCell(JGrid<T> grid, int index, JComponent component) {

		super(true);

		setGrid(grid);

		this.index = index;
		this.component = component;
		this.component.setPreferredSize(getGrid().getCellSize());
		this.layer = new JXLayer<JComponent>(component);

		CellLayerUI<JComponent> selectionLayerUI = new CellLayerUI<JComponent>(
				index);
		getGrid().getSelectionModel()
				.addListSelectionListener(selectionLayerUI);

		this.layer.setUI(selectionLayerUI);
		this.setLayout(new BorderLayout());
		this.add(layer, BorderLayout.CENTER);
		this.setPreferredSize(getGrid().getCellSize());
		this.setOpaque(false);

	}

	public void setIndex(int index) {
		this.index = index;
	}

	public JComponent getComponent() {
		return component;
	}

	public int getIndex() {
		return index;
	}

	public JXLayer<JComponent> getLayer() {
		return layer;
	}

	/**
	 * @param comparable
	 *            the comparable to set
	 */
	public void setComparable(T comparable) {
		this.comparable = comparable;
	}

	/**
	 * @return the comparable
	 */
	public T getComparable() {
		return comparable;
	}

	@Override
	public int compareTo(JCell<T> o) {
		return getComparable().compareTo(o.getComparable());
	}

	/**
	 * @param grid
	 *            the grid to set
	 */
	public void setGrid(JGrid<T> grid) {
		this.grid = grid;
	}

	/**
	 * @return the grid
	 */
	public JGrid<T> getGrid() {
		return grid;
	}

	protected class CellLayerUI<V extends JComponent> extends
			AbstractLayerUI<V> implements ListSelectionListener {

		private static final long serialVersionUID = 1L;

		private int index;

		public CellLayerUI(int index) {
			this.index = index;
		}

		protected void paintLayer(Graphics2D g2, JXLayer<? extends V> l) {

			if (getGrid().getSelectionModel().isSelectedIndex(index)) {

				g2.setPaint(new GradientPaint(0, 0, new Color(234, 247, 252,
						128), 0, l.getHeight(), new Color(167, 222, 249, 128)));
				g2.fillRoundRect(0, 0, l.getWidth() - 1, l.getHeight() - 1, 6,
						6);
				g2.setColor(new Color(153, 222, 253));
				g2.drawRoundRect(0, 0, l.getWidth() - 1, l.getHeight() - 1, 6,
						6);
			}

			super.paintLayer(g2, l);

		}

		@Override
		public long getLayerEventMask() {

			return (AWTEvent.MOUSE_EVENT_MASK
					| AWTEvent.MOUSE_MOTION_EVENT_MASK
					| AWTEvent.KEY_EVENT_MASK | AWTEvent.FOCUS_EVENT_MASK);
			// AWTEvent.MOUSE_WHEEL_EVENT_MASK

		}

		protected void processMouseEvent(MouseEvent e, JXLayer<? extends V> l) {

			super.processMouseEvent(e, l);

			if (e.getID() == MouseEvent.MOUSE_CLICKED) {

				getGrid().inCell = true;

				if (e.isControlDown()) {

					getGrid().getSelectionModel().addSelectionInterval(index,
							index);

				} else if (e.isShiftDown()) {

					getGrid().getSelectionModel().setSelectionInterval(
							index,
							getGrid().getSelectionModel()
									.getLeadSelectionIndex());

				} else {

					getGrid().getSelectionModel().setSelectionInterval(index,
							index);

				}
			}

		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (index >= e.getFirstIndex() && index <= e.getLastIndex())
				setDirty(true);
		}

	}

}
