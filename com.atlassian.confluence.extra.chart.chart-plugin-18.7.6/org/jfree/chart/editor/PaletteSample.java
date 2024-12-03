/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.editor;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import org.jfree.chart.plot.ColorPalette;

public class PaletteSample
extends JComponent
implements ListCellRenderer {
    private ColorPalette palette;
    private Dimension preferredSize;

    public PaletteSample(ColorPalette palette) {
        this.palette = palette;
        this.preferredSize = new Dimension(80, 18);
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value instanceof PaletteSample) {
            PaletteSample in = (PaletteSample)value;
            this.setPalette(in.getPalette());
        }
        return this;
    }

    public ColorPalette getPalette() {
        return this.palette;
    }

    public Dimension getPreferredSize() {
        return this.preferredSize;
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        Dimension size = this.getSize();
        Insets insets = this.getInsets();
        double ww = size.getWidth() - (double)insets.left - (double)insets.right;
        double hh = size.getHeight() - (double)insets.top - (double)insets.bottom;
        g2.setStroke(new BasicStroke(1.0f));
        double y1 = insets.top;
        double y2 = y1 + hh;
        Line2D.Double line = new Line2D.Double();
        int count = 0;
        for (double xx = (double)insets.left; xx <= (double)insets.left + ww; xx += 1.0) {
            ((Line2D)line).setLine(xx, y1, xx, y2);
            g2.setPaint(this.palette.getColor(++count));
            g2.draw(line);
        }
    }

    public void setPalette(ColorPalette palette) {
        this.palette = palette;
        this.repaint();
    }
}

