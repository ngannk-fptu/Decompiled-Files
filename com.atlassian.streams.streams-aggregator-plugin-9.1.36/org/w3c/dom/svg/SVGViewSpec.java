/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGFitToViewBox;
import org.w3c.dom.svg.SVGTransformList;
import org.w3c.dom.svg.SVGZoomAndPan;

public interface SVGViewSpec
extends SVGZoomAndPan,
SVGFitToViewBox {
    public SVGTransformList getTransform();

    public SVGElement getViewTarget();

    public String getViewBoxString();

    public String getPreserveAspectRatioString();

    public String getTransformString();

    public String getViewTargetString();
}

