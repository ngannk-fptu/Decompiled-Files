/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGExternalResourcesRequired;
import org.w3c.dom.svg.SVGTests;
import org.w3c.dom.svg.SVGURIReference;

public interface SVGCursorElement
extends SVGElement,
SVGURIReference,
SVGTests,
SVGExternalResourcesRequired {
    public SVGAnimatedLength getX();

    public SVGAnimatedLength getY();
}

