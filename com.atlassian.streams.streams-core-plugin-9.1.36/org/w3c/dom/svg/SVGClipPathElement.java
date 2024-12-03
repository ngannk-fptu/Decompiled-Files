/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGExternalResourcesRequired;
import org.w3c.dom.svg.SVGLangSpace;
import org.w3c.dom.svg.SVGStylable;
import org.w3c.dom.svg.SVGTests;
import org.w3c.dom.svg.SVGTransformable;
import org.w3c.dom.svg.SVGUnitTypes;

public interface SVGClipPathElement
extends SVGElement,
SVGTests,
SVGLangSpace,
SVGExternalResourcesRequired,
SVGStylable,
SVGTransformable,
SVGUnitTypes {
    public SVGAnimatedEnumeration getClipPathUnits();
}

