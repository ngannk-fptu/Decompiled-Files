/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.layout;

import org.xhtmlrenderer.layout.FloatManager;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;

public class PersistentBFC {
    private FloatManager _floatManager = new FloatManager();

    public PersistentBFC(BlockBox master, LayoutContext c) {
        master.setPersistentBFC(this);
        this._floatManager.setMaster(master);
    }

    public FloatManager getFloatManager() {
        return this._floatManager;
    }
}

