package ilist.ui.generic.grid;

import java.awt.Dimension;

import javax.swing.JComponent;

/**
 * build a component to display a value in a <code>JGrid</code>
 * @author Christophe Le Besnerais
 */
public interface GridCellRenderer {

	/** 
	 * @param grid the current grid 
	 * @param value the value to display
	 * @param size	the size of the cell
	 * @param isSelected <code>true</code> if the cell is selected
	 * @param hasFocus <code>false</code>
	 * @param pos the position of the cell in the grid
	 * @return the component which will be used to display the given value
	 */
	JComponent getGridCellRendererComponent(JGrid grid, Object value, Dimension size, boolean isSelected, boolean hasFocus, int pos);
	
}
