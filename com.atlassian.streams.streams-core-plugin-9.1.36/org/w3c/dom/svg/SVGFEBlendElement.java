/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGFilterPrimitiveStandardAttributes;

public interface SVGFEBlendElement
extends SVGElement,
SVGFilterPrimitiveStandardAttributes {
    public static final short SVG_FEBLEND_MODE_UNKNOWN = 0;
    public static final short SVG_FEBLEND_MODE_NORMAL = 1;
    public static final short SVG_FEBLEND_MODE_MULTIPLY = 2;
    public static final short SVG_FEBLEND_MODE_SCREEN = 3;
    public static final short SVG_FEBLEND_MODE_DARKEN = 4;
    public static final short SVG_FEBLEND_MODE_LIGHTEN = 5;

    public SVGAnimatedString getIn1();

    public SVGAnimatedString getIn2();

    public SVGAnimatedEnumeration getMode();
}

