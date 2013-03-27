package org.uqbar.eclipse.swt.jface;

/* 
 * Table example snippet: Images on the right hand side of a TableItem
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 * 
 * @since 3.2
 */

import org.eclipse.swt.*;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class Snippet230 {

    public static void main(String[] args) {
	Display display = new Display();
	final Image image = display.getSystemImage(SWT.ICON_INFORMATION);
	Shell shell = new Shell(display);
	shell.setText("Images on the right side of the TableItem");
	shell.setLayout(new FillLayout());
	final Table table = new Table(shell, SWT.MULTI | SWT.FULL_SELECTION);
	table.setHeaderVisible(true);
	table.setLinesVisible(true);
	int columnCount = 1;
	for (int i = 0; i < columnCount; i++) {
	    TableColumn column = new TableColumn(table, SWT.NONE);
	    column.setText("Column " + i);
	}
	int itemCount = 8;
	for (int i = 0; i < itemCount; i++) {
	    TableItem item = new TableItem(table, SWT.NONE);
	    item.setText(new String[] { "item " + i + " a", "item " + i + " b", "item " + i + " c" });
	}
	/*
	 * NOTE: MeasureItem, PaintItem and EraseItem are called repeatedly.
	 * Therefore, it is critical for performance that these methods be as
	 * efficient as possible.
	 */
	final ListenerLoco paintListener = new ListenerLoco(image, table);

	table.addListener(SWT.MeasureItem, paintListener);
	table.addListener(SWT.PaintItem, paintListener);
	table.addMouseMoveListener(paintListener);

	for (int i = 0; i < columnCount; i++) {
	    table.getColumn(i).pack();
	}
	shell.setSize(500, 200);
	shell.open();
	while (!shell.isDisposed()) {
	    if (!display.readAndDispatch())
		display.sleep();
	}
	if (image != null)
	    image.dispose();
	display.dispose();
    }
}
