/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwmf.draw;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import org.apache.poi.util.Internal;

@Internal
public class HwmfGraphicsState {
    private Color background;
    private Shape clip;
    private Color color;
    private Composite composite;
    private Font font;
    private Paint paint;
    private Stroke stroke;
    private AffineTransform trans;

    public void backup(Graphics2D graphics2D) {
        this.background = graphics2D.getBackground();
        this.clip = graphics2D.getClip();
        this.color = graphics2D.getColor();
        this.composite = graphics2D.getComposite();
        this.font = graphics2D.getFont();
        this.paint = graphics2D.getPaint();
        this.stroke = graphics2D.getStroke();
        this.trans = graphics2D.getTransform();
    }

    public void restore(Graphics2D graphics2D) {
        graphics2D.setBackground(this.background);
        graphics2D.setClip(this.clip);
        graphics2D.setColor(this.color);
        graphics2D.setComposite(this.composite);
        graphics2D.setFont(this.font);
        graphics2D.setPaint(this.paint);
        graphics2D.setStroke(this.stroke);
        graphics2D.setTransform(this.trans);
    }
}

