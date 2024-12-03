/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGFilterPrimitiveStandardAttributes;

public interface SVGFECompositeElement
extends SVGElement,
SVGFilterPrimitiveStandardAttributes {
    public static final short SVG_FECOMPOSITE_OPERATOR_UNKNOWN = 0;
    public static final short SVG_FECOMPOSITE_OPERATOR_OVER = 1;
    public static final short SVG_FECOMPOSITE_OPERATOR_IN = 2;
    public static final short SVG_FECOMPOSITE_OPERATOR_OUT = 3;
    public static final short SVG_FECOMPOSITE_OPERATOR_ATOP = 4;
    public static final short SVG_FECOMPOSITE_OPERATOR_XOR = 5;
    public static final short SVG_FECOMPOSITE_OPERATOR_ARITHMETIC = 6;

    public SVGAnimatedString getIn1();

    public SVGAnimatedString getIn2();

    public SVGAnimatedEnumeration getOperator();

    public SVGAnimatedNumber getK1();

    public SVGAnimatedNumber getK2();

    public SVGAnimatedNumber getK3();

    public SVGAnimatedNumber getK4();
}

