/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui;

import java.awt.Insets;
import java.util.ResourceBundle;
import javax.swing.JTextField;
import org.jfree.util.ResourceBundleWrapper;

public class InsetsTextField
extends JTextField {
    protected static ResourceBundle localizationResources = ResourceBundleWrapper.getBundle("org.jfree.ui.LocalizationBundle");

    public InsetsTextField(Insets insets) {
        this.setInsets(insets);
        this.setEnabled(false);
    }

    public String formatInsetsString(Insets insets) {
        insets = insets == null ? new Insets(0, 0, 0, 0) : insets;
        return localizationResources.getString("T") + insets.top + ", " + localizationResources.getString("L") + insets.left + ", " + localizationResources.getString("B") + insets.bottom + ", " + localizationResources.getString("R") + insets.right;
    }

    public void setInsets(Insets insets) {
        this.setText(this.formatInsetsString(insets));
    }
}

