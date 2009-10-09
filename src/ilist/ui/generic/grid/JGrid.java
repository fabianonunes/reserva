package ilist.ui.generic.grid;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;

import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.AbstractLayerUI;

public class JGrid<C extends Comparable<C>> extends JPanel implements
		Scrollable {

	private static final long serialVersionUID = 1L;
	public JPanel contentPanel;
	private GridCellRenderer defaultRenderer;
	private Map<Class<?>, GridCellRenderer> renderers = new HashMap<Class<?>, GridCellRenderer>();
	private ListSelectionModel selectionModel = new DefaultListSelectionModel();
	private FlowLayout layout;
	private Dimension cellSize = new Dimension(300, 380);
	boolean inCell = false;

	private ArrayList<String> names = new ArrayList<String>();

	public SortedSet<JCell<C>> cells = Collections
			.synchronizedSortedSet(new TreeSet<JCell<C>>());

	public JGrid() {

		super(new BorderLayout(), true);
		this.contentPanel = new JPanel(true);
		this.layout = new FlowLayout(FlowLayout.LEADING);
		this.contentPanel.setLayout(layout);
		this.contentPanel.setFocusable(true);
		this.contentPanel.requestFocusInWindow();
		this.contentPanel.setOpaque(false);

		// JXLayer<JPanel> layer = new JXLayer<JPanel>(contentPanel);
		// layer.setUI(new GridLayerUI<JPanel>());
		this.add(contentPanel, BorderLayout.CENTER);

	}

	public void addCell(JCell<C> cell) {

		cells.add(cell);

		int position = cells.headSet(cell).size();

		contentPanel.add(cell, position);

		int col = (int) Math
				.floor((JGrid.this.getWidth() - getHorizontalGap() + 0.0)
						/ (getCellSize().width + getHorizontalGap()));

		if (col > 0) {
			int row = (int) Math.ceil((JGrid.this.cells.size() + 0.0) / col);

			setPreferredSize(new Dimension(getHorizontalGap() + col
					* (getCellSize().width + getHorizontalGap()),
					getVerticalGap() + row
							* (getCellSize().height + getVerticalGap())));
		}

		revalidate();
		repaint();

	}

	public JCell<C> getCell(Object value, int pos) {

		JComponent result;
		GridCellRenderer r = renderers.get(value.getClass());

		if (r == null) {

			if (getDefaultRenderer() == null) {

				result = new JPanel(true);

			} else {

				result = getDefaultRenderer().getGridCellRendererComponent(
						this, value, getCellSize(),
						getSelectionModel().isSelectedIndex(pos), false, pos);

			}

		} else {

			result = r.getGridCellRendererComponent(this, value, getCellSize(),
					getSelectionModel().isSelectedIndex(pos), false, pos);

		}

		return new JCell<C>(this, pos, result);
	}

	// public ListModel getModel() {
	// return model;
	// }

	public ListSelectionModel getSelectionModel() {
		return selectionModel;
	}

	/**
	 * @return the renderer used when no renderer is found using
	 *         <code>setRenderer(Class, GridCellRenderer)</code>
	 * @see JGrid#setDefaultRenderer(GridCellRenderer)
	 * @see JGrid#setRenderer(Class, GridCellRenderer)
	 */
	public GridCellRenderer getDefaultRenderer() {
		return defaultRenderer;
	}

	/**
	 * set the renderer used when no renderer is found using
	 * <code>setRenderer(Class, GridCellRenderer)</code>
	 * 
	 * @param defaultRenderer
	 * @see JGrid#setRenderer(Class, GridCellRenderer)
	 */
	public void setDefaultRenderer(GridCellRenderer defaultRenderer) {
		this.defaultRenderer = defaultRenderer;
	}

	/**
	 * @return all the registered renderer
	 * @see JGrid#setRenderer(Class, GridCellRenderer)
	 */
	public Map<Class<?>, GridCellRenderer> getRenderers() {
		return renderers;
	}

	/**
	 * set the renderer used for the values of class <code>c</code>
	 * 
	 * @param c
	 * @param renderer
	 * @see JGrid#setDefaultRenderer(GridCellRenderer)
	 */
	public void setRenderer(Class<?> c, GridCellRenderer renderer) {
		renderers.put(c, renderer);
	}

	/**
	 * @return the size of the cells
	 */
	public Dimension getCellSize() {
		return cellSize;
	}

	/**
	 * set the size of the cells
	 * 
	 * @param cellSize
	 */
	public void setCellSize(Dimension cellSize) {
		if (cellSize != null)
			this.cellSize = cellSize;
	}

	/**
	 * set the vertical distance between 2 cells
	 * 
	 * @param gap
	 */
	public void setVerticalGap(int gap) {
		layout.setVgap(gap);
	}

	/**
	 * @return the vertical distance between 2 cells
	 */
	public int getVerticalGap() {
		return layout.getVgap();
	}

	/**
	 * set the horizontal distance between 2 cells
	 * 
	 * @param gap
	 */
	public void setHorizontalGap(int gap) {
		layout.setHgap(gap);
	}

	/**
	 * @return the horizontal distance between 2 cells
	 */
	public int getHorizontalGap() {
		return layout.getHgap();
	}

	/**
	 * Sets the alignment for the cells. Possible values are
	 * <ul>
	 * <li><code>FlowLayout.LEFT</code>
	 * <li><code>FlowLayout.RIGHT</code>
	 * <li><code>FlowLayout.CENTER</code>
	 * <li><code>FlowLayout.LEADING</code>
	 * <li><code>FlowLayout.TRAILING</code>
	 * </ul>
	 * 
	 * @param align
	 */
	public void setAlignment(int align) {
		layout.setAlignment(align);
	}

	/**
	 * @return the alignment for the cells. Possible values are
	 *         <ul>
	 *         <li><code>FlowLayout.LEFT</code>
	 *         <li><code>FlowLayout.RIGHT</code>
	 *         <li><code>FlowLayout.CENTER</code>
	 *         <li><code>FlowLayout.LEADING</code>
	 *         <li><code>FlowLayout.TRAILING</code>
	 *         </ul>
	 */
	public int getAlignment() {
		return layout.getAlignment();
	}

	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return 30;
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return getCellSize().height;
	}

	public boolean getScrollableTracksViewportWidth() {
		return true;
	}

	public boolean getScrollableTracksViewportHeight() {
		if (getParent() instanceof JViewport)
			return (((JViewport) getParent()).getHeight() > getPreferredSize().height);

		return false;

	}

	public void setNames(ArrayList<String> names) {
		this.names = names;
	}

	public ArrayList<String> getNames() {
		return names;
	}

	/**
	 * the layer over all the grid
	 */
	protected class GridLayerUI<V extends JComponent> extends
			AbstractLayerUI<V> {

		private static final long serialVersionUID = 1L;
		private Point startDrag, endDrag;
		private int index = -1;

		@Override
		protected void processKeyEvent(KeyEvent e, JXLayer<? extends V> l) {
			super.processKeyEvent(e, l);

			int end, start = 0;
			if (e.getID() == KeyEvent.KEY_PRESSED) {
				int col = (int) Math
						.floor((getWidth() - getHorizontalGap() + 0.0)
								/ (getCellSize().width + getHorizontalGap()));

				switch (e.getKeyCode()) {
				case KeyEvent.VK_LEFT:
					end = getSelectionModel().getLeadSelectionIndex() - 1;
					break;

				case KeyEvent.VK_RIGHT:
					end = getSelectionModel().getLeadSelectionIndex() + 1;
					break;

				case KeyEvent.VK_UP:
					end = getSelectionModel().getLeadSelectionIndex() - col;
					if (end < 0)
						return;
					break;

				case KeyEvent.VK_DOWN:
					end = getSelectionModel().getLeadSelectionIndex() + col;
					if (end > cells.size() - 1)
						return;
					break;

				case KeyEvent.VK_END:
					end = cells.size() - 1;
					break;

				case KeyEvent.VK_HOME:
					end = 0;
					break;

				case KeyEvent.VK_A:
					if (e.isControlDown())
						getSelectionModel().setSelectionInterval(0,
								cells.size() - 1);
					return;

				default:
					return;
				}

				if (e.isShiftDown()) {
					if (index == -1)
						index = getSelectionModel().getLeadSelectionIndex();
					start = index;
				} else {
					index = -1;
					start = end;
				}

				if (e.isControlDown())
					getSelectionModel().addSelectionInterval(start, end);
				else
					getSelectionModel().setSelectionInterval(start, end);
			}
		}

		@Override
		protected void processMouseEvent(MouseEvent e, JXLayer<? extends V> l) {
			super.processMouseEvent(e, l);

			// if (!inCell && e.getID() == MouseEvent.MOUSE_CLICKED) {
			// getSelectionModel().clearSelection();
			// }
			//
			// inCell = false;

			if (e.getID() == MouseEvent.MOUSE_PRESSED) {

				startDrag = SwingUtilities.convertPoint(e.getComponent(), e
						.getPoint(), l);

			} else if (e.getID() == MouseEvent.MOUSE_RELEASED) {

				endDrag = null;
				startDrag = null;
				setDirty(true);

			}

			contentPanel.requestFocusInWindow();

		}

		@Override
		protected void processMouseMotionEvent(MouseEvent e,
				JXLayer<? extends V> l) {

			super.processMouseMotionEvent(e, l);

			if (e.getID() == MouseEvent.MOUSE_DRAGGED) {

				endDrag = SwingUtilities.convertPoint(e.getComponent(), e
						.getPoint(), l);

				if (startDrag != null) {

					int col = (int) Math
							.floor((getWidth() - getHorizontalGap() + 0.0)
									/ (getCellSize().width + getHorizontalGap()));
					;

					int startCol = startDrag.x
							/ (getCellSize().width + getHorizontalGap());

					int endCol = endDrag.x
							/ (getCellSize().width + getHorizontalGap());

					int startRow = startDrag.y
							/ (getCellSize().height + getVerticalGap());

					int endRow = endDrag.y
							/ (getCellSize().height + getVerticalGap());

					if (endCol >= col) {
						endCol = col - 1;
					}

					if (startRow > endRow) {
						int temp = endRow;
						endRow = startRow;
						startRow = temp;
					}

					if (!e.isControlDown()) {
						getSelectionModel().clearSelection();
					}

					for (int i = startRow; i <= endRow; i++) {
						getSelectionModel().addSelectionInterval(
								i * col + startCol, i * col + endCol);
					}

				}

				setDirty(true);

			}
		}

		@Override
		protected void paintLayer(Graphics2D g2, JXLayer<? extends V> l) {

			super.paintLayer(g2, l);

			if (endDrag != null && startDrag != null) {

				g2.setPaint(new Color(137, 177, 237, 128));
				g2
						.fillRoundRect(Math.min(startDrag.x, endDrag.x), Math
								.min(startDrag.y, endDrag.y), Math
								.abs(endDrag.x - startDrag.x), Math
								.abs(endDrag.y - startDrag.y), 2, 2);
				g2.setColor(new Color(19, 106, 197));
				g2
						.drawRoundRect(Math.min(startDrag.x, endDrag.x), Math
								.min(startDrag.y, endDrag.y), Math
								.abs(endDrag.x - startDrag.x), Math
								.abs(endDrag.y - startDrag.y), 2, 2);
			}

		}

		@Override
		public long getLayerEventMask() {
			return (AWTEvent.MOUSE_EVENT_MASK
					| AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.FOCUS_EVENT_MASK);
		}

	}

}
