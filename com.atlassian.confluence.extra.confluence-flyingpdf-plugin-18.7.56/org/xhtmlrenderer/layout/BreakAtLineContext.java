/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.layout;

import org.xhtmlrenderer.render.BlockBox;

public class BreakAtLineContext {
    private final BlockBox _block;
    private final int _line;

    public BreakAtLineContext(BlockBox block, int line) {
        this._block = block;
        this._line = line;
    }

    public BlockBox getBlock() {
        return this._block;
    }

    public int getLine() {
        return this._line;
    }
}

