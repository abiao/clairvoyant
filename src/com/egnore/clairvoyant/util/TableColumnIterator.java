package com.egnore.clairvoyant.util;

import org.htmlparser.Node;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;
import org.htmlparser.util.NodeList;

public class TableColumnIterator {
	protected TableRow row;
	protected NodeList nodes;
	protected int index;

	public void setTableRow(TableRow r) {
		row = r;
		nodes = row.getChildren();
		index = 0;
	}

	public TableColumn getNextTableColumn() {
		index++;
		for (; index < nodes.size(); index++) {
			Node node = nodes.elementAt(index);
			System.out.println(node.getClass().getName());
			if (node instanceof TableColumn) {
				return (TableColumn)node;
			}
		}
		return null;
	}
}
