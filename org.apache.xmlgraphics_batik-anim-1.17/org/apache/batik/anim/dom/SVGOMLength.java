/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.AbstractElement;
import org.apache.batik.anim.dom.AbstractSVGLength;
import org.apache.batik.anim.dom.SVGOMElement;

public class SVGOMLength
extends AbstractSVGLength {
    protected AbstractElement element;

    public SVGOMLength(AbstractElement elt) {
        super((short)0);
        this.element = elt;
    }

    @Override
    protected SVGOMElement getAssociatedElement() {
        return (SVGOMElement)this.element;
    }
}

