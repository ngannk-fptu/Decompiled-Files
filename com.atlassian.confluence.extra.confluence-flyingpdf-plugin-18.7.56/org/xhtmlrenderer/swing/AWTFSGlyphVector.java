/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.swing;

import java.awt.font.GlyphVector;
import org.xhtmlrenderer.extend.FSGlyphVector;

public class AWTFSGlyphVector
implements FSGlyphVector {
    private final GlyphVector _glyphVector;

    public AWTFSGlyphVector(GlyphVector vector) {
        this._glyphVector = vector;
    }

    public GlyphVector getGlyphVector() {
        return this._glyphVector;
    }
}

