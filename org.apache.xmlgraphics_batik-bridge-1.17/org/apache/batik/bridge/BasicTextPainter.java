/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.bridge;

import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import org.apache.batik.bridge.ConcreteTextLayoutFactory;
import org.apache.batik.bridge.Mark;
import org.apache.batik.bridge.TextHit;
import org.apache.batik.bridge.TextLayoutFactory;
import org.apache.batik.bridge.TextNode;
import org.apache.batik.bridge.TextPainter;

public abstract class BasicTextPainter
implements TextPainter {
    private static TextLayoutFactory textLayoutFactory = new ConcreteTextLayoutFactory();
    protected FontRenderContext fontRenderContext = new FontRenderContext(new AffineTransform(), true, true);
    protected FontRenderContext aaOffFontRenderContext = new FontRenderContext(new AffineTransform(), false, true);

    protected TextLayoutFactory getTextLayoutFactory() {
        return textLayoutFactory;
    }

    @Override
    public Mark selectAt(double x, double y, TextNode node) {
        return this.hitTest(x, y, node);
    }

    @Override
    public Mark selectTo(double x, double y, Mark beginMark) {
        if (beginMark == null) {
            return null;
        }
        return this.hitTest(x, y, beginMark.getTextNode());
    }

    @Override
    public Rectangle2D getGeometryBounds(TextNode node) {
        return this.getOutline(node).getBounds2D();
    }

    protected abstract Mark hitTest(double var1, double var3, TextNode var5);

    protected static class BasicMark
    implements Mark {
        private TextNode node;
        private TextHit hit;

        protected BasicMark(TextNode node, TextHit hit) {
            this.hit = hit;
            this.node = node;
        }

        public TextHit getHit() {
            return this.hit;
        }

        @Override
        public TextNode getTextNode() {
            return this.node;
        }

        @Override
        public int getCharIndex() {
            return this.hit.getCharIndex();
        }
    }
}

