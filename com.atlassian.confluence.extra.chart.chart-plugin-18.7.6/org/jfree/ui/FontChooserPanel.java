/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import org.jfree.util.ResourceBundleWrapper;

public class FontChooserPanel
extends JPanel {
    public static final String[] SIZES = new String[]{"9", "10", "11", "12", "14", "16", "18", "20", "22", "24", "28", "36", "48", "72"};
    private JList fontlist;
    private JList sizelist;
    private JCheckBox bold;
    private JCheckBox italic;
    protected static ResourceBundle localizationResources = ResourceBundleWrapper.getBundle("org.jfree.ui.LocalizationBundle");

    public FontChooserPanel(Font font) {
        GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fonts = g.getAvailableFontFamilyNames();
        this.setLayout(new BorderLayout());
        JPanel right = new JPanel(new BorderLayout());
        JPanel fontPanel = new JPanel(new BorderLayout());
        fontPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), localizationResources.getString("Font")));
        this.fontlist = new JList<String>(fonts);
        JScrollPane fontpane = new JScrollPane(this.fontlist);
        fontpane.setBorder(BorderFactory.createEtchedBorder());
        fontPanel.add(fontpane);
        this.add(fontPanel);
        JPanel sizePanel = new JPanel(new BorderLayout());
        sizePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), localizationResources.getString("Size")));
        this.sizelist = new JList<String>(SIZES);
        JScrollPane sizepane = new JScrollPane(this.sizelist);
        sizepane.setBorder(BorderFactory.createEtchedBorder());
        sizePanel.add(sizepane);
        JPanel attributes = new JPanel(new GridLayout(1, 2));
        this.bold = new JCheckBox(localizationResources.getString("Bold"));
        this.italic = new JCheckBox(localizationResources.getString("Italic"));
        attributes.add(this.bold);
        attributes.add(this.italic);
        attributes.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), localizationResources.getString("Attributes")));
        right.add((Component)sizePanel, "Center");
        right.add((Component)attributes, "South");
        this.add((Component)right, "East");
        this.setSelectedFont(font);
    }

    public Font getSelectedFont() {
        return new Font(this.getSelectedName(), this.getSelectedStyle(), this.getSelectedSize());
    }

    public String getSelectedName() {
        return (String)this.fontlist.getSelectedValue();
    }

    public int getSelectedStyle() {
        if (this.bold.isSelected() && this.italic.isSelected()) {
            return 3;
        }
        if (this.bold.isSelected()) {
            return 1;
        }
        if (this.italic.isSelected()) {
            return 2;
        }
        return 0;
    }

    public int getSelectedSize() {
        String selected = (String)this.sizelist.getSelectedValue();
        if (selected != null) {
            return Integer.parseInt(selected);
        }
        return 10;
    }

    public void setSelectedFont(Font font) {
        if (font == null) {
            throw new NullPointerException();
        }
        this.bold.setSelected(font.isBold());
        this.italic.setSelected(font.isItalic());
        String fontName = font.getName();
        ListModel model = this.fontlist.getModel();
        this.fontlist.clearSelection();
        for (int i = 0; i < model.getSize(); ++i) {
            if (!fontName.equals(model.getElementAt(i))) continue;
            this.fontlist.setSelectedIndex(i);
            break;
        }
        String fontSize = String.valueOf(font.getSize());
        model = this.sizelist.getModel();
        this.sizelist.clearSelection();
        for (int i = 0; i < model.getSize(); ++i) {
            if (!fontSize.equals(model.getElementAt(i))) continue;
            this.sizelist.setSelectedIndex(i);
            break;
        }
    }
}

