/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom.svg;

import org.apache.batik.dom.svg.AbstractSVGList;
import org.apache.batik.dom.svg.SVGItem;

public abstract class AbstractSVGItem
implements SVGItem {
    protected AbstractSVGList parent;
    protected String itemStringValue;

    protected abstract String getStringValue();

    protected AbstractSVGItem() {
    }

    @Override
    public void setParent(AbstractSVGList list) {
        this.parent = list;
    }

    @Override
    public AbstractSVGList getParent() {
        return this.parent;
    }

    protected void resetAttribute() {
        if (this.parent != null) {
            this.itemStringValue = null;
            this.parent.itemChanged();
        }
    }

    @Override
    public String getValueAsString() {
        if (this.itemStringValue == null) {
            this.itemStringValue = this.getStringValue();
        }
        return this.itemStringValue;
    }
}

