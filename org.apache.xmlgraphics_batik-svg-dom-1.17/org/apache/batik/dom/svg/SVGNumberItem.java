/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom.svg;

import org.apache.batik.dom.svg.AbstractSVGList;
import org.apache.batik.dom.svg.AbstractSVGNumber;
import org.apache.batik.dom.svg.SVGItem;

public class SVGNumberItem
extends AbstractSVGNumber
implements SVGItem {
    protected AbstractSVGList parentList;

    public SVGNumberItem(float value) {
        this.value = value;
    }

    @Override
    public String getValueAsString() {
        return Float.toString(this.value);
    }

    @Override
    public void setParent(AbstractSVGList list) {
        this.parentList = list;
    }

    @Override
    public AbstractSVGList getParent() {
        return this.parentList;
    }

    protected void reset() {
        if (this.parentList != null) {
            this.parentList.itemChanged();
        }
    }
}

