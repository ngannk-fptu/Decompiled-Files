/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.lang.GString;
import java.awt.Component;
import java.awt.Container;
import java.util.Enumeration;
import java.util.Iterator;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.ListModel;
import javax.swing.MenuElement;
import javax.swing.MutableComboBoxModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class SwingGroovyMethods {
    public static int size(Container self) {
        return self.getComponentCount();
    }

    public static Component getAt(Container self, int index) {
        return self.getComponent(index);
    }

    public static Container leftShift(Container self, Component c) {
        self.add(c);
        return self;
    }

    public static Iterator<Component> iterator(Container self) {
        return DefaultGroovyMethods.iterator(self.getComponents());
    }

    public static void clear(Container self) {
        self.removeAll();
    }

    public static int size(ButtonGroup self) {
        return self.getButtonCount();
    }

    public static AbstractButton getAt(ButtonGroup self, int index) {
        int size = self.getButtonCount();
        if (index < 0 || index >= size) {
            return null;
        }
        Enumeration<AbstractButton> buttons = self.getElements();
        for (int i = 0; i <= index; ++i) {
            AbstractButton b = buttons.nextElement();
            if (i != index) continue;
            return b;
        }
        return null;
    }

    public static ButtonGroup leftShift(ButtonGroup self, AbstractButton b) {
        self.add(b);
        return self;
    }

    public static Iterator<AbstractButton> iterator(ButtonGroup self) {
        return DefaultGroovyMethods.iterator(self.getElements());
    }

    public static int size(ListModel self) {
        return self.getSize();
    }

    public static Object getAt(ListModel self, int index) {
        return self.getElementAt(index);
    }

    public static Iterator iterator(final ListModel self) {
        return new Iterator(){
            private int index = 0;

            @Override
            public boolean hasNext() {
                return this.index < self.getSize();
            }

            public Object next() {
                return self.getElementAt(this.index++);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("LisModel is immutable.");
            }
        };
    }

    public static DefaultListModel leftShift(DefaultListModel self, Object e) {
        self.addElement(e);
        return self;
    }

    public static void putAt(DefaultListModel self, int index, Object e) {
        self.set(index, e);
    }

    public static void clear(DefaultListModel self) {
        self.removeAllElements();
    }

    public static Iterator iterator(final DefaultListModel self) {
        return new Iterator(){
            private int index = 0;

            @Override
            public boolean hasNext() {
                return this.index > -1 && this.index < self.getSize();
            }

            public Object next() {
                return self.getElementAt(this.index++);
            }

            @Override
            public void remove() {
                if (this.hasNext()) {
                    self.removeElementAt(this.index--);
                }
            }
        };
    }

    public static int size(JComboBox self) {
        return self.getItemCount();
    }

    public static Object getAt(JComboBox self, int index) {
        return self.getItemAt(index);
    }

    public static JComboBox leftShift(JComboBox self, Object i) {
        self.addItem(i);
        return self;
    }

    public static void clear(JComboBox self) {
        self.removeAllItems();
    }

    public static Iterator iterator(JComboBox self) {
        return SwingGroovyMethods.iterator(self.getModel());
    }

    public static MutableComboBoxModel leftShift(MutableComboBoxModel self, Object i) {
        self.addElement(i);
        return self;
    }

    public static void putAt(MutableComboBoxModel self, int index, Object i) {
        self.insertElementAt(i, index);
    }

    public static Iterator iterator(final MutableComboBoxModel self) {
        return new Iterator(){
            private int index = 0;

            @Override
            public boolean hasNext() {
                return this.index > -1 && this.index < self.getSize();
            }

            public Object next() {
                return self.getElementAt(this.index++);
            }

            @Override
            public void remove() {
                if (this.hasNext()) {
                    self.removeElementAt(this.index--);
                }
            }
        };
    }

    public static void clear(DefaultComboBoxModel self) {
        self.removeAllElements();
    }

    public static int size(TableModel self) {
        return self.getRowCount();
    }

    public static Object[] getAt(TableModel self, int index) {
        int cols = self.getColumnCount();
        Object[] rowData = new Object[cols];
        for (int col = 0; col < cols; ++col) {
            rowData[col] = self.getValueAt(index, col);
        }
        return rowData;
    }

    public static Iterator iterator(final TableModel self) {
        return new Iterator(){
            private int row = 0;

            @Override
            public boolean hasNext() {
                return this.row < self.getRowCount();
            }

            public Object next() {
                int cols = self.getColumnCount();
                Object[] rowData = new Object[cols];
                for (int col = 0; col < cols; ++col) {
                    rowData[col] = self.getValueAt(this.row, col);
                }
                ++this.row;
                return rowData;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("TableModel is immutable.");
            }
        };
    }

    public static DefaultTableModel leftShift(DefaultTableModel self, Object row) {
        if (row == null) {
            self.addRow((Object[])null);
            return self;
        }
        self.addRow(SwingGroovyMethods.buildRowData(self, row));
        return self;
    }

    public static void putAt(DefaultTableModel self, int index, Object row) {
        if (row == null) {
            self.insertRow(index, (Object[])null);
            return;
        }
        self.insertRow(index, SwingGroovyMethods.buildRowData(self, row));
    }

    private static Object[] buildRowData(DefaultTableModel delegate, Object row) {
        int cols = delegate.getColumnCount();
        Object[] rowData = new Object[cols];
        int i = 0;
        Iterator it = DefaultGroovyMethods.iterator(row);
        while (it.hasNext() && i < cols) {
            rowData[i++] = it.next();
        }
        return rowData;
    }

    public static Iterator iterator(final DefaultTableModel self) {
        return new Iterator(){
            private int row = 0;

            @Override
            public boolean hasNext() {
                return this.row > -1 && this.row < self.getRowCount();
            }

            public Object next() {
                int cols = self.getColumnCount();
                Object[] rowData = new Object[cols];
                for (int col = 0; col < cols; ++col) {
                    rowData[col] = self.getValueAt(this.row, col);
                }
                ++this.row;
                return rowData;
            }

            @Override
            public void remove() {
                if (this.hasNext()) {
                    self.removeRow(this.row--);
                }
            }
        };
    }

    public static int size(TableColumnModel self) {
        return self.getColumnCount();
    }

    public static TableColumn getAt(TableColumnModel self, int index) {
        return self.getColumn(index);
    }

    public static Iterator<TableColumn> iterator(final TableColumnModel self) {
        return new Iterator<TableColumn>(){
            private int index = 0;

            @Override
            public boolean hasNext() {
                return this.index > -1 && this.index < self.getColumnCount();
            }

            @Override
            public TableColumn next() {
                return self.getColumn(this.index++);
            }

            @Override
            public void remove() {
                if (this.hasNext()) {
                    self.removeColumn(self.getColumn(this.index--));
                }
            }
        };
    }

    public static TableColumnModel leftShift(TableColumnModel self, TableColumn column) {
        self.addColumn(column);
        return self;
    }

    public static int size(TreePath self) {
        return self.getPathCount();
    }

    public static Object getAt(TreePath self, int index) {
        return self.getPath()[index];
    }

    public static TreePath leftShift(TreePath self, Object p) {
        return self.pathByAddingChild(p);
    }

    public static Iterator iterator(TreePath self) {
        return DefaultGroovyMethods.iterator(self.getPath());
    }

    public static int size(TreeNode self) {
        return self.getChildCount();
    }

    public static TreeNode getAt(TreeNode self, int index) {
        return self.getChildAt(index);
    }

    public static Iterator<TreeNode> iterator(TreeNode self) {
        return DefaultGroovyMethods.iterator(self.children());
    }

    public static MutableTreeNode leftShift(MutableTreeNode self, MutableTreeNode node) {
        self.insert(node, self.getChildCount());
        return self;
    }

    public static void putAt(MutableTreeNode self, int index, MutableTreeNode node) {
        self.insert(node, index);
    }

    public static DefaultMutableTreeNode leftShift(DefaultMutableTreeNode self, DefaultMutableTreeNode node) {
        self.add(node);
        return self;
    }

    public static void clear(DefaultMutableTreeNode self) {
        self.removeAllChildren();
    }

    public static int size(JMenu self) {
        return self.getMenuComponentCount();
    }

    public static Component getAt(JMenu self, int index) {
        return self.getMenuComponent(index);
    }

    public static JMenu leftShift(JMenu self, Action action) {
        self.add(action);
        return self;
    }

    public static JMenu leftShift(JMenu self, Component component) {
        self.add(component);
        return self;
    }

    public static JMenu leftShift(JMenu self, JMenuItem item) {
        self.add(item);
        return self;
    }

    public static JMenu leftShift(JMenu self, String str) {
        self.add(str);
        return self;
    }

    public static JMenu leftShift(JMenu self, GString gstr) {
        self.add(gstr.toString());
        return self;
    }

    public static Iterator iterator(JMenu self) {
        return DefaultGroovyMethods.iterator(self.getMenuComponents());
    }

    public static int size(JMenuBar self) {
        return self.getMenuCount();
    }

    public static JMenu getAt(JMenuBar self, int index) {
        return self.getMenu(index);
    }

    public static JMenuBar leftShift(JMenuBar self, JMenu menu) {
        self.add(menu);
        return self;
    }

    public static Iterator iterator(JMenuBar self) {
        return DefaultGroovyMethods.iterator(self.getSubElements());
    }

    public static JPopupMenu leftShift(JPopupMenu self, Action action) {
        self.add(action);
        return self;
    }

    public static JPopupMenu leftShift(JPopupMenu self, Component component) {
        self.add(component);
        return self;
    }

    public static JPopupMenu leftShift(JPopupMenu self, JMenuItem item) {
        self.add(item);
        return self;
    }

    public static JPopupMenu leftShift(JPopupMenu self, String str) {
        self.add(str);
        return self;
    }

    public static JPopupMenu leftShift(JPopupMenu self, GString gstr) {
        self.add(gstr.toString());
        return self;
    }

    public static Iterator<MenuElement> iterator(JPopupMenu self) {
        return DefaultGroovyMethods.iterator(self.getSubElements());
    }

    public static int size(JTabbedPane self) {
        return self.getTabCount();
    }

    public static void clear(JTabbedPane self) {
        self.removeAll();
    }

    public static Component getAt(JTabbedPane self, int index) {
        return self.getComponentAt(index);
    }

    public static Iterator<Component> iterator(final JTabbedPane self) {
        return new Iterator<Component>(){
            private int index = 0;

            @Override
            public boolean hasNext() {
                return this.index > -1 && this.index < self.getTabCount();
            }

            @Override
            public Component next() {
                return self.getComponentAt(this.index++);
            }

            @Override
            public void remove() {
                if (this.hasNext()) {
                    self.removeTabAt(this.index--);
                }
            }
        };
    }

    public static JToolBar leftShift(JToolBar self, Action action) {
        self.add(action);
        return self;
    }

    public static Component getAt(JToolBar self, int index) {
        return self.getComponentAtIndex(index);
    }

    public static void setMnemonic(AbstractButton button, String mnemonic) {
        char c = ShortTypeHandling.castToChar(mnemonic).charValue();
        button.setMnemonic(c);
    }
}

