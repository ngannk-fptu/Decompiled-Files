/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Toolkit;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.xhtmlrenderer.context.StyleReference;
import org.xhtmlrenderer.css.constants.ValueConstants;
import org.xhtmlrenderer.css.util.ConversionUtil;

class ElementPropertiesPanel
extends JPanel {
    private static final long serialVersionUID = 1L;
    private StyleReference _sr;
    private JTable _properties;
    private TableModel _defaultTableModel;

    ElementPropertiesPanel(StyleReference sr) {
        this._sr = sr;
        this._properties = new PropertiesJTable();
        this._defaultTableModel = new DefaultTableModel();
        this.setLayout(new BorderLayout());
        this.add((Component)new JScrollPane(this._properties), "Center");
    }

    public void setForElement(Node node) {
        try {
            this._properties.setModel(this.tableModel(node));
            TableColumnModel model = this._properties.getColumnModel();
            if (model.getColumnCount() > 0) {
                model.getColumn(0).sizeWidthToFit();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private TableModel tableModel(Node node) {
        if (node.getNodeType() != 1) {
            Toolkit.getDefaultToolkit().beep();
            return this._defaultTableModel;
        }
        Map props = this._sr.getCascadedPropertiesMap((Element)node);
        return new PropertiesTableModel(props);
    }

    static class PropertiesTableModel
    extends AbstractTableModel {
        private static final long serialVersionUID = 1L;
        String[] _colNames = new String[]{"Property Name", "Text", "Value"};
        Map _properties;

        PropertiesTableModel(Map cssProperties) {
            this._properties = cssProperties;
        }

        @Override
        public String getColumnName(int col) {
            return this._colNames[col];
        }

        @Override
        public int getColumnCount() {
            return this._colNames.length;
        }

        @Override
        public int getRowCount() {
            return this._properties.size();
        }

        @Override
        public Object getValueAt(int row, int col) {
            Map.Entry me = (Map.Entry)this._properties.entrySet().toArray()[row];
            CSSPrimitiveValue cpv = (CSSPrimitiveValue)me.getValue();
            Object val = null;
            switch (col) {
                case 0: {
                    val = me.getKey();
                    break;
                }
                case 1: {
                    val = cpv.getCssText();
                    break;
                }
                case 2: {
                    val = ValueConstants.isNumber(cpv.getPrimitiveType()) ? new Float(cpv.getFloatValue(cpv.getPrimitiveType())) : "";
                }
            }
            return val;
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    }

    static class PropertiesJTable
    extends JTable {
        private static final long serialVersionUID = 1L;
        Font propLabelFont;
        Font defaultFont;

        PropertiesJTable() {
            this.setColumnSelectionAllowed(false);
            this.setSelectionMode(0);
            this.propLabelFont = new Font("Courier New", 1, 12);
            this.defaultFont = new Font("Default", 0, 12);
        }

        @Override
        public TableCellRenderer getCellRenderer(int row, int col) {
            JLabel label = (JLabel)((Object)super.getCellRenderer(row, col));
            label.setBackground(Color.white);
            label.setFont(this.defaultFont);
            if (col == 0) {
                label.setFont(this.propLabelFont);
            } else if (col == 2) {
                PropertiesTableModel pmodel = (PropertiesTableModel)this.getModel();
                Map.Entry me = (Map.Entry)pmodel._properties.entrySet().toArray()[row];
                CSSPrimitiveValue cpv = (CSSPrimitiveValue)me.getValue();
                if (cpv.getCssText().startsWith("rgb")) {
                    label.setBackground(ConversionUtil.rgbToColor(cpv.getRGBColorValue()));
                }
            }
            return (TableCellRenderer)((Object)label);
        }
    }
}

