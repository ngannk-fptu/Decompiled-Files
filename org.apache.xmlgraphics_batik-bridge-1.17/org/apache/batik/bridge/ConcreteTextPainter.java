/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.bridge;

import java.awt.Graphics2D;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.text.AttributedCharacterIterator;
import org.apache.batik.bridge.BasicTextPainter;
import org.apache.batik.bridge.TextNode;

public abstract class ConcreteTextPainter
extends BasicTextPainter {
    public void paint(AttributedCharacterIterator aci, Point2D location, TextNode.Anchor anchor, Graphics2D g2d) {
        TextLayout layout = new TextLayout(aci, this.fontRenderContext);
        float advance = layout.getAdvance();
        float tx = 0.0f;
        switch (anchor.getType()) {
            case 1: {
                tx = -advance / 2.0f;
                break;
            }
            case 2: {
                tx = -advance;
            }
        }
        layout.draw(g2d, (float)(location.getX() + (double)tx), (float)location.getY());
    }
}

