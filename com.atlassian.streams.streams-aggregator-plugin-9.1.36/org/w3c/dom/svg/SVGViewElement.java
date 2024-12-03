/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGExternalResourcesRequired;
import org.w3c.dom.svg.SVGFitToViewBox;
import org.w3c.dom.svg.SVGStringList;
import org.w3c.dom.svg.SVGZoomAndPan;

public interface SVGViewElement
extends SVGElement,
SVGExternalResourcesRequired,
SVGFitToViewBox,
SVGZoomAndPan {
    public SVGStringList getViewTarget();
}

