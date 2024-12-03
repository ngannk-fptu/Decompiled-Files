/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import org.jfree.ui.L1R2ButtonPanel;
import org.jfree.util.ResourceBundleWrapper;

public class StandardDialog
extends JDialog
implements ActionListener {
    private boolean cancelled = false;
    protected static final ResourceBundle localizationResources = ResourceBundleWrapper.getBundle("org.jfree.ui.LocalizationBundle");

    public StandardDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
    }

    public StandardDialog(Dialog owner, String title, boolean modal) {
        super(owner, title, modal);
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (!command.equals("helpButton")) {
            if (command.equals("okButton")) {
                this.cancelled = false;
                this.setVisible(false);
            } else if (command.equals("cancelButton")) {
                this.cancelled = true;
                this.setVisible(false);
            }
        }
    }

    protected JPanel createButtonPanel() {
        L1R2ButtonPanel buttons = new L1R2ButtonPanel(localizationResources.getString("Help"), localizationResources.getString("OK"), localizationResources.getString("Cancel"));
        JButton helpButton = buttons.getLeftButton();
        helpButton.setActionCommand("helpButton");
        helpButton.addActionListener(this);
        JButton okButton = buttons.getRightButton1();
        okButton.setActionCommand("okButton");
        okButton.addActionListener(this);
        JButton cancelButton = buttons.getRightButton2();
        cancelButton.setActionCommand("cancelButton");
        cancelButton.addActionListener(this);
        buttons.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        return buttons;
    }
}

