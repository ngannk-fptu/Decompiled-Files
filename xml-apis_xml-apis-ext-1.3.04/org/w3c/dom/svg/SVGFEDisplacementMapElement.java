/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGFilterPrimitiveStandardAttributes;

public interface SVGFEDisplacementMapElement
extends SVGElement,
SVGFilterPrimitiveStandardAttributes {
    public static final short SVG_CHANNEL_UNKNOWN = 0;
    public static final short SVG_CHANNEL_R = 1;
    public static final short SVG_CHANNEL_G = 2;
    public static final short SVG_CHANNEL_B = 3;
    public static final short SVG_CHANNEL_A = 4;

    public SVGAnimatedString getIn1();

    public SVGAnimatedString getIn2();

    public SVGAnimatedNumber getScale();

    public SVGAnimatedEnumeration getXChannelSelector();

    public SVGAnimatedEnumeration getYChannelSelector();
}

