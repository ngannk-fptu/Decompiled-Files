/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3c.dom.svg.SVGNumber
 */
package org.apache.batik.dom.svg;

import org.w3c.dom.svg.SVGNumber;

public abstract class AbstractSVGNumber
implements SVGNumber {
    protected float value;

    public float getValue() {
        return this.value;
    }

    public void setValue(float f) {
        this.value = f;
    }
}

