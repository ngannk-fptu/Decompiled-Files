/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui;

import java.awt.Component;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import org.jfree.ui.BevelArrowIcon;
import org.jfree.ui.SortableTableModel;

public class SortButtonRenderer
implements TableCellRenderer {
    public static final int NONE = 0;
    public static final int DOWN = 1;
    public static final int UP = 2;
    private int pressedColumn = -1;
    private JButton normalButton;
    private JButton ascendingButton;
    private JButton descendingButton;
    private boolean useLabels = UIManager.getLookAndFeel().getID().equals("Aqua");
    private JLabel normalLabel;
    private JLabel ascendingLabel;
    private JLabel descendingLabel;

    public SortButtonRenderer() {
        Border border = UIManager.getBorder("TableHeader.cellBorder");
        if (this.useLabels) {
            this.normalLabel = new JLabel();
            this.normalLabel.setHorizontalAlignment(10);
            this.ascendingLabel = new JLabel();
            this.ascendingLabel.setHorizontalAlignment(10);
            this.ascendingLabel.setHorizontalTextPosition(2);
            this.ascendingLabel.setIcon(new BevelArrowIcon(1, false, false));
            this.descendingLabel = new JLabel();
            this.descendingLabel.setHorizontalAlignment(10);
            this.descendingLabel.setHorizontalTextPosition(2);
            this.descendingLabel.setIcon(new BevelArrowIcon(0, false, false));
            this.normalLabel.setBorder(border);
            this.ascendingLabel.setBorder(border);
            this.descendingLabel.setBorder(border);
        } else {
            this.normalButton = new JButton();
            this.normalButton.setMargin(new Insets(0, 0, 0, 0));
            this.normalButton.setHorizontalAlignment(10);
            this.ascendingButton = new JButton();
            this.ascendingButton.setMargin(new Insets(0, 0, 0, 0));
            this.ascendingButton.setHorizontalAlignment(10);
            this.ascendingButton.setHorizontalTextPosition(2);
            this.ascendingButton.setIcon(new BevelArrowIcon(1, false, false));
            this.ascendingButton.setPressedIcon(new BevelArrowIcon(1, false, true));
            this.descendingButton = new JButton();
            this.descendingButton.setMargin(new Insets(0, 0, 0, 0));
            this.descendingButton.setHorizontalAlignment(10);
            this.descendingButton.setHorizontalTextPosition(2);
            this.descendingButton.setIcon(new BevelArrowIcon(0, false, false));
            this.descendingButton.setPressedIcon(new BevelArrowIcon(0, false, true));
            this.normalButton.setBorder(border);
            this.ascendingButton.setBorder(border);
            this.descendingButton.setBorder(border);
        }
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JComponent component;
        boolean isPressed;
        if (table == null) {
            throw new NullPointerException("Table must not be null.");
        }
        SortableTableModel model = (SortableTableModel)table.getModel();
        int cc = table.convertColumnIndexToModel(column);
        boolean isSorting = model.getSortingColumn() == cc;
        boolean isAscending = model.isAscending();
        JTableHeader header = table.getTableHeader();
        boolean bl = isPressed = cc == this.pressedColumn;
        if (this.useLabels) {
            JLabel label = this.getRendererLabel(isSorting, isAscending);
            label.setText(value == null ? "" : value.toString());
            component = label;
        } else {
            JButton button = this.getRendererButton(isSorting, isAscending);
            button.setText(value == null ? "" : value.toString());
            button.getModel().setPressed(isPressed);
            button.getModel().setArmed(isPressed);
            component = button;
        }
        if (header != null) {
            component.setForeground(header.getForeground());
            component.setBackground(header.getBackground());
            component.setFont(header.getFont());
        }
        return component;
    }

    private JButton getRendererButton(boolean isSorting, boolean isAscending) {
        if (isSorting) {
            if (isAscending) {
                return this.ascendingButton;
            }
            return this.descendingButton;
        }
        return this.normalButton;
    }

    private JLabel getRendererLabel(boolean isSorting, boolean isAscending) {
        if (isSorting) {
            if (isAscending) {
                return this.ascendingLabel;
            }
            return this.descendingLabel;
        }
        return this.normalLabel;
    }

    public void setPressedColumn(int column) {
        this.pressedColumn = column;
    }
}

