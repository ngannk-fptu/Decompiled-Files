/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedInteger;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGAnimatedNumberList;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGFilterPrimitiveStandardAttributes;

public interface SVGFEConvolveMatrixElement
extends SVGElement,
SVGFilterPrimitiveStandardAttributes {
    public static final short SVG_EDGEMODE_UNKNOWN = 0;
    public static final short SVG_EDGEMODE_DUPLICATE = 1;
    public static final short SVG_EDGEMODE_WRAP = 2;
    public static final short SVG_EDGEMODE_NONE = 3;

    public SVGAnimatedInteger getOrderX();

    public SVGAnimatedInteger getOrderY();

    public SVGAnimatedNumberList getKernelMatrix();

    public SVGAnimatedNumber getDivisor();

    public SVGAnimatedNumber getBias();

    public SVGAnimatedInteger getTargetX();

    public SVGAnimatedInteger getTargetY();

    public SVGAnimatedEnumeration getEdgeMode();

    public SVGAnimatedNumber getKernelUnitLengthX();

    public SVGAnimatedNumber getKernelUnitLengthY();

    public SVGAnimatedBoolean getPreserveAlpha();
}

