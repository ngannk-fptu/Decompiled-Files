/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui;

import java.awt.BorderLayout;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import org.jfree.ui.StrokeSample;

public class StrokeChooserPanel
extends JPanel {
    private JComboBox selector;

    public StrokeChooserPanel(StrokeSample current, StrokeSample[] available) {
        this.setLayout(new BorderLayout());
        DefaultComboBoxModel<Stroke> model = new DefaultComboBoxModel<Stroke>();
        for (int i = 0; i < available.length; ++i) {
            model.addElement(available[i].getStroke());
        }
        this.selector = new JComboBox(model);
        this.selector.setSelectedItem(current.getStroke());
        this.selector.setRenderer(new StrokeSample(null));
        this.add(this.selector);
        this.selector.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent evt) {
                StrokeChooserPanel.this.getSelector().transferFocus();
            }
        });
    }

    protected final JComboBox getSelector() {
        return this.selector;
    }

    public Stroke getSelectedStroke() {
        return (Stroke)this.selector.getSelectedItem();
    }
}

