/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGFilterPrimitiveStandardAttributes;

public interface SVGFEGaussianBlurElement
extends SVGElement,
SVGFilterPrimitiveStandardAttributes {
    public SVGAnimatedString getIn1();

    public SVGAnimatedNumber getStdDeviationX();

    public SVGAnimatedNumber getStdDeviationY();

    public void setStdDeviation(float var1, float var2);
}

