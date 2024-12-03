/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom.svg;

import org.apache.batik.dom.svg.AbstractSVGList;

public interface SVGItem {
    public void setParent(AbstractSVGList var1);

    public AbstractSVGList getParent();

    public String getValueAsString();
}

