/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.layout;

import org.xhtmlrenderer.render.BlockBox;

public class FloatLayoutResult {
    private boolean _pending;
    private BlockBox _block;

    public boolean isPending() {
        return this._pending;
    }

    public void setPending(boolean pending) {
        this._pending = pending;
    }

    public BlockBox getBlock() {
        return this._block;
    }

    public void setBlock(BlockBox block) {
        this._block = block;
    }
}

