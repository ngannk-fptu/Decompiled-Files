/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.svg.SVGAngle;
import org.w3c.dom.svg.SVGAnimatedAngle;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGExternalResourcesRequired;
import org.w3c.dom.svg.SVGFitToViewBox;
import org.w3c.dom.svg.SVGLangSpace;
import org.w3c.dom.svg.SVGStylable;

public interface SVGMarkerElement
extends SVGElement,
SVGLangSpace,
SVGExternalResourcesRequired,
SVGStylable,
SVGFitToViewBox {
    public static final short SVG_MARKERUNITS_UNKNOWN = 0;
    public static final short SVG_MARKERUNITS_USERSPACEONUSE = 1;
    public static final short SVG_MARKERUNITS_STROKEWIDTH = 2;
    public static final short SVG_MARKER_ORIENT_UNKNOWN = 0;
    public static final short SVG_MARKER_ORIENT_AUTO = 1;
    public static final short SVG_MARKER_ORIENT_ANGLE = 2;

    public SVGAnimatedLength getRefX();

    public SVGAnimatedLength getRefY();

    public SVGAnimatedEnumeration getMarkerUnits();

    public SVGAnimatedLength getMarkerWidth();

    public SVGAnimatedLength getMarkerHeight();

    public SVGAnimatedEnumeration getOrientType();

    public SVGAnimatedAngle getOrientAngle();

    public void setOrientToAuto();

    public void setOrientToAngle(SVGAngle var1);
}

