/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui;

import java.awt.Color;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.BorderUIResource;

public class UIUtilities {
    private UIUtilities() {
    }

    public static void setupUI() {
        try {
            String classname = UIManager.getSystemLookAndFeelClassName();
            UIManager.setLookAndFeel(classname);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        UIDefaults defaults = UIManager.getDefaults();
        defaults.put("PopupMenu.border", new BorderUIResource.EtchedBorderUIResource(0, defaults.getColor("controlShadow"), defaults.getColor("controlLtHighlight")));
        MatteBorder matteborder = new MatteBorder(1, 1, 1, 1, Color.black);
        MatteBorder emptyborder = new MatteBorder(2, 2, 2, 2, defaults.getColor("control"));
        BorderUIResource.CompoundBorderUIResource compBorder = new BorderUIResource.CompoundBorderUIResource(emptyborder, matteborder);
        BorderUIResource.EmptyBorderUIResource emptyBorderUI = new BorderUIResource.EmptyBorderUIResource(0, 0, 0, 0);
        defaults.put("SplitPane.border", emptyBorderUI);
        defaults.put("Table.scrollPaneBorder", emptyBorderUI);
        defaults.put("ComboBox.border", compBorder);
        defaults.put("TextField.border", compBorder);
        defaults.put("TextArea.border", compBorder);
        defaults.put("CheckBox.border", compBorder);
        defaults.put("ScrollPane.border", emptyBorderUI);
    }
}

