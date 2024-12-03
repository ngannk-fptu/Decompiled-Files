/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.events.UIEvent;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGRect;

public interface SVGZoomEvent
extends UIEvent {
    public SVGRect getZoomRectScreen();

    public float getPreviousScale();

    public SVGPoint getPreviousTranslate();

    public float getNewScale();

    public SVGPoint getNewTranslate();
}

