/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.editor;

import java.awt.BorderLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import org.jfree.chart.editor.PaletteSample;
import org.jfree.chart.plot.ColorPalette;
import org.jfree.chart.plot.RainbowPalette;

class PaletteChooserPanel
extends JPanel {
    private JComboBox selector;

    public PaletteChooserPanel(PaletteSample current, PaletteSample[] available) {
        this.setLayout(new BorderLayout());
        this.selector = new JComboBox<PaletteSample>(available);
        this.selector.setSelectedItem(current);
        this.selector.setRenderer(new PaletteSample(new RainbowPalette()));
        this.add(this.selector);
    }

    public ColorPalette getSelectedPalette() {
        PaletteSample sample = (PaletteSample)this.selector.getSelectedItem();
        return sample.getPalette();
    }
}

