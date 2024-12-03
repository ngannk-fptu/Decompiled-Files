/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGAnimatedTransformList;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGExternalResourcesRequired;
import org.w3c.dom.svg.SVGFitToViewBox;
import org.w3c.dom.svg.SVGLangSpace;
import org.w3c.dom.svg.SVGStylable;
import org.w3c.dom.svg.SVGTests;
import org.w3c.dom.svg.SVGURIReference;
import org.w3c.dom.svg.SVGUnitTypes;

public interface SVGPatternElement
extends SVGElement,
SVGURIReference,
SVGTests,
SVGLangSpace,
SVGExternalResourcesRequired,
SVGStylable,
SVGFitToViewBox,
SVGUnitTypes {
    public SVGAnimatedEnumeration getPatternUnits();

    public SVGAnimatedEnumeration getPatternContentUnits();

    public SVGAnimatedTransformList getPatternTransform();

    public SVGAnimatedLength getX();

    public SVGAnimatedLength getY();

    public SVGAnimatedLength getWidth();

    public SVGAnimatedLength getHeight();
}

