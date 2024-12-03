/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedTransformList;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGExternalResourcesRequired;
import org.w3c.dom.svg.SVGStylable;
import org.w3c.dom.svg.SVGURIReference;
import org.w3c.dom.svg.SVGUnitTypes;

public interface SVGGradientElement
extends SVGElement,
SVGURIReference,
SVGExternalResourcesRequired,
SVGStylable,
SVGUnitTypes {
    public static final short SVG_SPREADMETHOD_UNKNOWN = 0;
    public static final short SVG_SPREADMETHOD_PAD = 1;
    public static final short SVG_SPREADMETHOD_REFLECT = 2;
    public static final short SVG_SPREADMETHOD_REPEAT = 3;

    public SVGAnimatedEnumeration getGradientUnits();

    public SVGAnimatedTransformList getGradientTransform();

    public SVGAnimatedEnumeration getSpreadMethod();
}

