/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.events.EventTarget;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGExternalResourcesRequired;
import org.w3c.dom.svg.SVGLangSpace;
import org.w3c.dom.svg.SVGStylable;
import org.w3c.dom.svg.SVGTests;
import org.w3c.dom.svg.SVGTransformable;

public interface SVGEllipseElement
extends SVGElement,
SVGTests,
SVGLangSpace,
SVGExternalResourcesRequired,
SVGStylable,
SVGTransformable,
EventTarget {
    public SVGAnimatedLength getCx();

    public SVGAnimatedLength getCy();

    public SVGAnimatedLength getRx();

    public SVGAnimatedLength getRy();
}

