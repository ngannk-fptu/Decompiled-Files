/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedInteger;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGFilterPrimitiveStandardAttributes;

public interface SVGFETurbulenceElement
extends SVGElement,
SVGFilterPrimitiveStandardAttributes {
    public static final short SVG_TURBULENCE_TYPE_UNKNOWN = 0;
    public static final short SVG_TURBULENCE_TYPE_FRACTALNOISE = 1;
    public static final short SVG_TURBULENCE_TYPE_TURBULENCE = 2;
    public static final short SVG_STITCHTYPE_UNKNOWN = 0;
    public static final short SVG_STITCHTYPE_STITCH = 1;
    public static final short SVG_STITCHTYPE_NOSTITCH = 2;

    public SVGAnimatedNumber getBaseFrequencyX();

    public SVGAnimatedNumber getBaseFrequencyY();

    public SVGAnimatedInteger getNumOctaves();

    public SVGAnimatedNumber getSeed();

    public SVGAnimatedEnumeration getStitchTiles();

    public SVGAnimatedEnumeration getType();
}

