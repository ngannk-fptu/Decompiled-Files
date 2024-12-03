/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGStylable;

public interface SVGFilterPrimitiveStandardAttributes
extends SVGStylable {
    public SVGAnimatedLength getX();

    public SVGAnimatedLength getY();

    public SVGAnimatedLength getWidth();

    public SVGAnimatedLength getHeight();

    public SVGAnimatedString getResult();
}

