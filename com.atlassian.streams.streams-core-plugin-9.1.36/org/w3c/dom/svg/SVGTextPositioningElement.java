/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.svg.SVGAnimatedLengthList;
import org.w3c.dom.svg.SVGAnimatedNumberList;
import org.w3c.dom.svg.SVGTextContentElement;

public interface SVGTextPositioningElement
extends SVGTextContentElement {
    public SVGAnimatedLengthList getX();

    public SVGAnimatedLengthList getY();

    public SVGAnimatedLengthList getDx();

    public SVGAnimatedLengthList getDy();

    public SVGAnimatedNumberList getRotate();
}

