/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class L1R2ButtonPanel
extends JPanel {
    private JButton left;
    private JButton right1;
    private JButton right2;

    public L1R2ButtonPanel(String label1, String label2, String label3) {
        this.setLayout(new BorderLayout());
        this.left = new JButton(label1);
        JPanel rightButtonPanel = new JPanel(new GridLayout(1, 2));
        this.right1 = new JButton(label2);
        this.right2 = new JButton(label3);
        rightButtonPanel.add(this.right1);
        rightButtonPanel.add(this.right2);
        this.add((Component)this.left, "West");
        this.add((Component)rightButtonPanel, "East");
    }

    public JButton getLeftButton() {
        return this.left;
    }

    public JButton getRightButton1() {
        return this.right1;
    }

    public JButton getRightButton2() {
        return this.right2;
    }
}

