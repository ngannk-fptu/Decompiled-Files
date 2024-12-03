/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.swing;

import java.awt.Font;
import org.xhtmlrenderer.render.FSFont;

public class AWTFSFont
implements FSFont {
    private Font _font;

    public AWTFSFont(Font font) {
        this._font = font;
    }

    @Override
    public float getSize2D() {
        return this._font.getSize2D();
    }

    public Font getAWTFont() {
        return this._font;
    }
}

