/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import org.jfree.ui.FontChooserPanel;
import org.jfree.ui.StandardDialog;

public class FontChooserDialog
extends StandardDialog {
    private FontChooserPanel fontChooserPanel;

    public FontChooserDialog(Dialog owner, String title, boolean modal, Font font) {
        super(owner, title, modal);
        this.setContentPane(this.createContent(font));
    }

    public FontChooserDialog(Frame owner, String title, boolean modal, Font font) {
        super(owner, title, modal);
        this.setContentPane(this.createContent(font));
    }

    public Font getSelectedFont() {
        return this.fontChooserPanel.getSelectedFont();
    }

    private JPanel createContent(Font font) {
        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        if (font == null) {
            font = new Font("Dialog", 10, 0);
        }
        this.fontChooserPanel = new FontChooserPanel(font);
        content.add(this.fontChooserPanel);
        JPanel buttons = this.createButtonPanel();
        buttons.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        content.add((Component)buttons, "South");
        return content;
    }
}

