/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.layout;

import java.util.ArrayList;
import java.util.List;

public class BoxRangeLists {
    private List _block = new ArrayList();
    private List _inline = new ArrayList();

    public List getBlock() {
        return this._block;
    }

    public List getInline() {
        return this._inline;
    }
}

