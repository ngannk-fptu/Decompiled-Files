/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedNumberList;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGFilterPrimitiveStandardAttributes;

public interface SVGFEColorMatrixElement
extends SVGElement,
SVGFilterPrimitiveStandardAttributes {
    public static final short SVG_FECOLORMATRIX_TYPE_UNKNOWN = 0;
    public static final short SVG_FECOLORMATRIX_TYPE_MATRIX = 1;
    public static final short SVG_FECOLORMATRIX_TYPE_SATURATE = 2;
    public static final short SVG_FECOLORMATRIX_TYPE_HUEROTATE = 3;
    public static final short SVG_FECOLORMATRIX_TYPE_LUMINANCETOALPHA = 4;

    public SVGAnimatedString getIn1();

    public SVGAnimatedEnumeration getType();

    public SVGAnimatedNumberList getValues();
}

