package org.uqbar.eclipse.swt.jface;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * @author jfernandes
 */
final class ListenerLoco implements Listener, MouseMoveListener {
    private final Image image;
    private final Table table;
    private TableItem hoverItem;

    public ListenerLoco(Image image, Table table) {
	this.image = image;
	this.table = table;
    }

    public void handleEvent(Event event) {
	if (event.item != this.hoverItem) {
	    return;
	}
	switch (event.type) {
	case SWT.MeasureItem: {
	    Rectangle rect = image.getBounds();
	    event.width += rect.width;
	    event.height = Math.max(event.height, rect.height + 2);
	    break;
	}
	case SWT.PaintItem: {
	    int x = event.x + event.width;
	    Rectangle rect = image.getBounds();
	    int offset = Math.max(0, (event.height - rect.height) / 2);

	    event.gc.drawImage(image, x, event.y + offset);
	    break;
	}
	}
    }

    public void mouseMove(MouseEvent e) {
	TableItem item = table.getItem(new Point(e.x, e.y));
	if (item != this.hoverItem) {
	    if (hoverItem != null) {
		this.exited(hoverItem);
	    }
	    this.hoverItem = item;
	    if (this.hoverItem != null) {
		this.entered(this.hoverItem);
	    }
	}
    }

    private void entered(TableItem item) {
	item.getParent().redraw();
    }

    private void exited(TableItem item) {
	item.getParent().redraw();
    }
}