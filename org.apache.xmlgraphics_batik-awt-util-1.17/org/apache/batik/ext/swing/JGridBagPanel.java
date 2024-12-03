/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.swing;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import javax.swing.JPanel;
import org.apache.batik.ext.swing.GridBagConstants;

public class JGridBagPanel
extends JPanel
implements GridBagConstants {
    public static final InsetsManager ZERO_INSETS = new ZeroInsetsManager();
    public static final InsetsManager DEFAULT_INSETS = new DefaultInsetsManager();
    public InsetsManager insetsManager;

    public JGridBagPanel() {
        this(new DefaultInsetsManager());
    }

    public JGridBagPanel(InsetsManager insetsManager) {
        super(new GridBagLayout());
        this.insetsManager = insetsManager != null ? insetsManager : new DefaultInsetsManager();
    }

    @Override
    public void setLayout(LayoutManager layout) {
        if (layout instanceof GridBagLayout) {
            super.setLayout(layout);
        }
    }

    public void add(Component cmp, int gridx, int gridy, int gridwidth, int gridheight, int anchor, int fill, double weightx, double weighty) {
        Insets insets = this.insetsManager.getInsets(gridx, gridy);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = gridx;
        constraints.gridy = gridy;
        constraints.gridwidth = gridwidth;
        constraints.gridheight = gridheight;
        constraints.anchor = anchor;
        constraints.fill = fill;
        constraints.weightx = weightx;
        constraints.weighty = weighty;
        constraints.insets = insets;
        this.add(cmp, constraints);
    }

    private static class DefaultInsetsManager
    implements InsetsManager {
        int leftInset = 5;
        int topInset = 5;
        public Insets positiveInsets = new Insets(this.topInset, this.leftInset, 0, 0);
        public Insets leftInsets = new Insets(this.topInset, 0, 0, 0);
        public Insets topInsets = new Insets(0, this.leftInset, 0, 0);
        public Insets topLeftInsets = new Insets(0, 0, 0, 0);

        private DefaultInsetsManager() {
        }

        @Override
        public Insets getInsets(int gridx, int gridy) {
            if (gridx > 0) {
                if (gridy > 0) {
                    return this.positiveInsets;
                }
                return this.topInsets;
            }
            if (gridy > 0) {
                return this.leftInsets;
            }
            return this.topLeftInsets;
        }
    }

    private static class ZeroInsetsManager
    implements InsetsManager {
        private Insets insets = new Insets(0, 0, 0, 0);

        private ZeroInsetsManager() {
        }

        @Override
        public Insets getInsets(int gridx, int gridy) {
            return this.insets;
        }
    }

    public static interface InsetsManager {
        public Insets getInsets(int var1, int var2);
    }
}

