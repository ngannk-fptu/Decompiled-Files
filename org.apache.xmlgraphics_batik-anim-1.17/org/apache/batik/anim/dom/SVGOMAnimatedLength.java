/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.AbstractElement;
import org.apache.batik.anim.dom.AbstractSVGAnimatedLength;

public class SVGOMAnimatedLength
extends AbstractSVGAnimatedLength {
    protected String defaultValue;

    public SVGOMAnimatedLength(AbstractElement elt, String ns, String ln, String def, short dir, boolean nonneg) {
        super(elt, ns, ln, dir, nonneg);
        this.defaultValue = def;
    }

    @Override
    protected String getDefaultValue() {
        return this.defaultValue;
    }
}

