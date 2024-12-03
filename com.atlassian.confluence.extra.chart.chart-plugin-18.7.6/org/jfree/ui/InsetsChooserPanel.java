/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ResourceBundle;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import org.jfree.ui.IntegerDocument;
import org.jfree.util.ResourceBundleWrapper;

public class InsetsChooserPanel
extends JPanel {
    private JTextField topValueEditor;
    private JTextField leftValueEditor;
    private JTextField bottomValueEditor;
    private JTextField rightValueEditor;
    protected static ResourceBundle localizationResources = ResourceBundleWrapper.getBundle("org.jfree.ui.LocalizationBundle");

    public InsetsChooserPanel() {
        this(new Insets(0, 0, 0, 0));
    }

    public InsetsChooserPanel(Insets current) {
        current = current == null ? new Insets(0, 0, 0, 0) : current;
        this.topValueEditor = new JTextField(new IntegerDocument(), "" + current.top, 0);
        this.leftValueEditor = new JTextField(new IntegerDocument(), "" + current.left, 0);
        this.bottomValueEditor = new JTextField(new IntegerDocument(), "" + current.bottom, 0);
        this.rightValueEditor = new JTextField(new IntegerDocument(), "" + current.right, 0);
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder(localizationResources.getString("Insets")));
        panel.add((Component)new JLabel(localizationResources.getString("Top")), new GridBagConstraints(1, 0, 3, 1, 0.0, 0.0, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
        panel.add((Component)new JLabel(" "), new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, 10, 1, new Insets(0, 12, 0, 12), 8, 0));
        panel.add((Component)this.topValueEditor, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, 10, 2, new Insets(0, 0, 0, 0), 0, 0));
        panel.add((Component)new JLabel(" "), new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, 10, 1, new Insets(0, 12, 0, 11), 8, 0));
        panel.add((Component)new JLabel(localizationResources.getString("Left")), new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, 10, 1, new Insets(0, 4, 0, 4), 0, 0));
        panel.add((Component)this.leftValueEditor, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, 10, 1, new Insets(0, 0, 0, 0), 0, 0));
        panel.add((Component)new JLabel(" "), new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, 10, 0, new Insets(0, 12, 0, 12), 8, 0));
        panel.add((Component)this.rightValueEditor, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0, 10, 2, new Insets(0, 0, 0, 0), 0, 0));
        panel.add((Component)new JLabel(localizationResources.getString("Right")), new GridBagConstraints(4, 2, 1, 1, 0.0, 0.0, 10, 0, new Insets(0, 4, 0, 4), 0, 0));
        panel.add((Component)this.bottomValueEditor, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, 10, 2, new Insets(0, 0, 0, 0), 0, 0));
        panel.add((Component)new JLabel(localizationResources.getString("Bottom")), new GridBagConstraints(1, 4, 3, 1, 0.0, 0.0, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
        this.setLayout(new BorderLayout());
        this.add((Component)panel, "Center");
    }

    public Insets getInsetsValue() {
        return new Insets(Math.abs(this.stringToInt(this.topValueEditor.getText())), Math.abs(this.stringToInt(this.leftValueEditor.getText())), Math.abs(this.stringToInt(this.bottomValueEditor.getText())), Math.abs(this.stringToInt(this.rightValueEditor.getText())));
    }

    protected int stringToInt(String value) {
        if ((value = value.trim()).length() == 0) {
            return 0;
        }
        try {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }

    public void removeNotify() {
        super.removeNotify();
        this.removeAll();
    }
}

