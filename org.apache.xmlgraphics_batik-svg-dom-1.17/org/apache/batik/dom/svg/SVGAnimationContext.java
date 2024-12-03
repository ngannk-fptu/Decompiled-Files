/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3c.dom.smil.ElementTimeControl
 *  org.w3c.dom.svg.SVGElement
 */
package org.apache.batik.dom.svg;

import org.apache.batik.dom.svg.SVGContext;
import org.w3c.dom.smil.ElementTimeControl;
import org.w3c.dom.svg.SVGElement;

public interface SVGAnimationContext
extends SVGContext,
ElementTimeControl {
    public SVGElement getTargetElement();

    public float getStartTime();

    public float getCurrentTime();

    public float getSimpleDuration();

    public float getHyperlinkBeginTime();
}

