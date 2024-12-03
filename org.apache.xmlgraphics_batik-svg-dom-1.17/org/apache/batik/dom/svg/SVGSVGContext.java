/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3c.dom.svg.SVGRect
 */
package org.apache.batik.dom.svg;

import java.util.List;
import org.apache.batik.dom.svg.SVGContext;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGRect;

public interface SVGSVGContext
extends SVGContext {
    public List getIntersectionList(SVGRect var1, Element var2);

    public List getEnclosureList(SVGRect var1, Element var2);

    public boolean checkIntersection(Element var1, SVGRect var2);

    public boolean checkEnclosure(Element var1, SVGRect var2);

    public void deselectAll();

    public int suspendRedraw(int var1);

    public boolean unsuspendRedraw(int var1);

    public void unsuspendRedrawAll();

    public void forceRedraw();

    public void pauseAnimations();

    public void unpauseAnimations();

    public boolean animationsPaused();

    public float getCurrentTime();

    public void setCurrentTime(float var1);
}

