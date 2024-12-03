/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.jfree.ui.ArrowPanel;

public class Spinner
extends JPanel
implements MouseListener {
    private int value;
    private JTextField textField;
    private JPanel buttonPanel;
    private ArrowPanel upButton;
    private ArrowPanel downButton;

    public Spinner(int value) {
        super(new BorderLayout());
        this.value = value;
        this.textField = new JTextField(Integer.toString(this.value));
        this.textField.setHorizontalAlignment(4);
        this.add(this.textField);
        this.buttonPanel = new JPanel(new GridLayout(2, 1, 0, 1));
        this.upButton = new ArrowPanel(0);
        this.upButton.addMouseListener(this);
        this.downButton = new ArrowPanel(1);
        this.downButton.addMouseListener(this);
        this.buttonPanel.add(this.upButton);
        this.buttonPanel.add(this.downButton);
        this.add((Component)this.buttonPanel, "East");
    }

    public int getValue() {
        return this.value;
    }

    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == this.upButton) {
            ++this.value;
            this.textField.setText(Integer.toString(this.value));
            this.firePropertyChange("value", this.value - 1, this.value);
        } else if (e.getSource() == this.downButton) {
            --this.value;
            this.textField.setText(Integer.toString(this.value));
            this.firePropertyChange("value", this.value + 1, this.value);
        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }
}

