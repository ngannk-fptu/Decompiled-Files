/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.svg.SVGAnimatedPreserveAspectRatio;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGExternalResourcesRequired;
import org.w3c.dom.svg.SVGFilterPrimitiveStandardAttributes;
import org.w3c.dom.svg.SVGLangSpace;
import org.w3c.dom.svg.SVGURIReference;

public interface SVGFEImageElement
extends SVGElement,
SVGURIReference,
SVGLangSpace,
SVGExternalResourcesRequired,
SVGFilterPrimitiveStandardAttributes {
    public SVGAnimatedPreserveAspectRatio getPreserveAspectRatio();
}

