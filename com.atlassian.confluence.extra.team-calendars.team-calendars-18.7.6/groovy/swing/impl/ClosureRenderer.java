/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.impl;

import groovy.lang.Closure;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

public class ClosureRenderer
implements ListCellRenderer,
TableCellRenderer,
TreeCellRenderer {
    Closure update;
    List children = new ArrayList();
    JList list;
    JTable table;
    JTree tree;
    Object value;
    boolean selected;
    boolean focused;
    boolean leaf;
    boolean expanded;
    int row;
    int column;
    boolean tableHeader;
    private boolean defaultRenderer;

    public ClosureRenderer() {
        this(null);
    }

    public ClosureRenderer(Closure c) {
        this.setUpdate(c);
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        this.list = list;
        this.table = null;
        this.tree = null;
        this.value = value;
        this.row = index;
        this.column = -1;
        this.selected = isSelected;
        this.focused = cellHasFocus;
        this.leaf = false;
        this.expanded = false;
        return this.render();
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        this.list = null;
        this.table = table;
        this.tree = null;
        this.value = value;
        this.row = row;
        this.column = column;
        this.selected = isSelected;
        this.focused = hasFocus;
        this.leaf = false;
        this.expanded = false;
        return this.render();
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        this.list = null;
        this.table = null;
        this.tree = tree;
        this.value = value;
        this.row = row;
        this.column = -1;
        this.selected = selected;
        this.focused = hasFocus;
        this.leaf = leaf;
        this.expanded = expanded;
        return this.render();
    }

    private Component render() {
        Object o;
        if (this.children.isEmpty() || this.defaultRenderer) {
            Object tcr;
            this.defaultRenderer = true;
            this.children.clear();
            if (this.table != null) {
                tcr = this.tableHeader ? this.table.getTableHeader().getDefaultRenderer() : this.table.getDefaultRenderer(this.table.getColumnClass(this.column));
                this.children.add(tcr.getTableCellRendererComponent(this.table, this.value, this.selected, this.focused, this.row, this.column));
            } else if (this.tree != null) {
                tcr = new DefaultTreeCellRenderer();
                this.children.add(tcr.getTreeCellRendererComponent(this.tree, this.value, this.selected, this.expanded, this.leaf, this.row, this.focused));
            } else if (this.list != null) {
                ListCellRenderer lcr = (ListCellRenderer)UIManager.get("List.cellRenderer");
                if (lcr == null) {
                    lcr = new DefaultListCellRenderer();
                }
                this.children.add(lcr.getListCellRendererComponent(this.list, this.value, this.row, this.selected, this.focused));
            }
        }
        if ((o = this.update.call()) instanceof Component) {
            return (Component)o;
        }
        return (Component)this.children.get(0);
    }

    public Closure getUpdate() {
        return this.update;
    }

    public void setUpdate(Closure update) {
        if (update != null) {
            update.setDelegate(this);
            update.setResolveStrategy(1);
        }
        this.update = update;
    }

    public void setTableHeader(boolean tableHeader) {
        this.tableHeader = tableHeader;
    }

    public boolean isTableHeader() {
        return this.tableHeader;
    }

    public List getChildren() {
        return this.children;
    }

    public JList getList() {
        return this.list;
    }

    public JTable getTable() {
        return this.table;
    }

    public Object getValue() {
        return this.value;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public boolean isFocused() {
        return this.focused;
    }

    public int getRow() {
        return this.row;
    }

    public int getColumn() {
        return this.column;
    }

    public JTree getTree() {
        return this.tree;
    }

    public boolean isLeaf() {
        return this.leaf;
    }

    public boolean isExpanded() {
        return this.expanded;
    }

    public boolean isDefaultRenderer() {
        return this.defaultRenderer;
    }
}

