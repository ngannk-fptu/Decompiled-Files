/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.bridge;

import java.awt.font.FontRenderContext;
import java.awt.geom.Point2D;
import java.text.AttributedCharacterIterator;
import org.apache.batik.bridge.GlyphLayout;
import org.apache.batik.bridge.TextLayoutFactory;
import org.apache.batik.bridge.TextSpanLayout;

public class ConcreteTextLayoutFactory
implements TextLayoutFactory {
    @Override
    public TextSpanLayout createTextLayout(AttributedCharacterIterator aci, int[] charMap, Point2D offset, FontRenderContext frc) {
        return new GlyphLayout(aci, charMap, offset, frc);
    }
}

