/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGFilterPrimitiveStandardAttributes;

public interface SVGFEMorphologyElement
extends SVGElement,
SVGFilterPrimitiveStandardAttributes {
    public static final short SVG_MORPHOLOGY_OPERATOR_UNKNOWN = 0;
    public static final short SVG_MORPHOLOGY_OPERATOR_ERODE = 1;
    public static final short SVG_MORPHOLOGY_OPERATOR_DILATE = 2;

    public SVGAnimatedString getIn1();

    public SVGAnimatedEnumeration getOperator();

    public SVGAnimatedNumber getRadiusX();

    public SVGAnimatedNumber getRadiusY();
}

