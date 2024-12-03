/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.DOMException;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.smil.ElementTimeControl;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGExternalResourcesRequired;
import org.w3c.dom.svg.SVGTests;

public interface SVGAnimationElement
extends SVGElement,
SVGTests,
SVGExternalResourcesRequired,
ElementTimeControl,
EventTarget {
    public SVGElement getTargetElement();

    public float getStartTime();

    public float getCurrentTime();

    public float getSimpleDuration() throws DOMException;
}

