/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.swing;

import java.awt.Graphics2D;
import org.xhtmlrenderer.extend.FontContext;

public class Java2DFontContext
implements FontContext {
    private Graphics2D _graphics;

    public Java2DFontContext(Graphics2D graphics) {
        this._graphics = graphics;
    }

    public Graphics2D getGraphics() {
        return this._graphics;
    }
}

