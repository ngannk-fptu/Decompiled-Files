/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedInteger;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGExternalResourcesRequired;
import org.w3c.dom.svg.SVGLangSpace;
import org.w3c.dom.svg.SVGStylable;
import org.w3c.dom.svg.SVGURIReference;
import org.w3c.dom.svg.SVGUnitTypes;

public interface SVGFilterElement
extends SVGElement,
SVGURIReference,
SVGLangSpace,
SVGExternalResourcesRequired,
SVGStylable,
SVGUnitTypes {
    public SVGAnimatedEnumeration getFilterUnits();

    public SVGAnimatedEnumeration getPrimitiveUnits();

    public SVGAnimatedLength getX();

    public SVGAnimatedLength getY();

    public SVGAnimatedLength getWidth();

    public SVGAnimatedLength getHeight();

    public SVGAnimatedInteger getFilterResX();

    public SVGAnimatedInteger getFilterResY();

    public void setFilterRes(int var1, int var2);
}

