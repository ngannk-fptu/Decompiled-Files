/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.bridge;

import java.awt.font.FontRenderContext;
import java.awt.geom.Point2D;
import java.text.AttributedCharacterIterator;
import org.apache.batik.bridge.TextSpanLayout;

public interface TextLayoutFactory {
    public TextSpanLayout createTextLayout(AttributedCharacterIterator var1, int[] var2, Point2D var3, FontRenderContext var4);
}

