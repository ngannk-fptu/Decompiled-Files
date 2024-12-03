/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.css.DocumentCSS;
import org.w3c.dom.css.ViewCSS;
import org.w3c.dom.events.DocumentEvent;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.svg.SVGAngle;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGExternalResourcesRequired;
import org.w3c.dom.svg.SVGFitToViewBox;
import org.w3c.dom.svg.SVGLangSpace;
import org.w3c.dom.svg.SVGLength;
import org.w3c.dom.svg.SVGLocatable;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGNumber;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGStylable;
import org.w3c.dom.svg.SVGTests;
import org.w3c.dom.svg.SVGTransform;
import org.w3c.dom.svg.SVGViewSpec;
import org.w3c.dom.svg.SVGZoomAndPan;

public interface SVGSVGElement
extends SVGElement,
SVGTests,
SVGLangSpace,
SVGExternalResourcesRequired,
SVGStylable,
SVGLocatable,
SVGFitToViewBox,
SVGZoomAndPan,
EventTarget,
DocumentEvent,
ViewCSS,
DocumentCSS {
    public SVGAnimatedLength getX();

    public SVGAnimatedLength getY();

    public SVGAnimatedLength getWidth();

    public SVGAnimatedLength getHeight();

    public String getContentScriptType();

    public void setContentScriptType(String var1) throws DOMException;

    public String getContentStyleType();

    public void setContentStyleType(String var1) throws DOMException;

    public SVGRect getViewport();

    public float getPixelUnitToMillimeterX();

    public float getPixelUnitToMillimeterY();

    public float getScreenPixelToMillimeterX();

    public float getScreenPixelToMillimeterY();

    public boolean getUseCurrentView();

    public void setUseCurrentView(boolean var1) throws DOMException;

    public SVGViewSpec getCurrentView();

    public float getCurrentScale();

    public void setCurrentScale(float var1) throws DOMException;

    public SVGPoint getCurrentTranslate();

    public int suspendRedraw(int var1);

    public void unsuspendRedraw(int var1) throws DOMException;

    public void unsuspendRedrawAll();

    public void forceRedraw();

    public void pauseAnimations();

    public void unpauseAnimations();

    public boolean animationsPaused();

    public float getCurrentTime();

    public void setCurrentTime(float var1);

    public NodeList getIntersectionList(SVGRect var1, SVGElement var2);

    public NodeList getEnclosureList(SVGRect var1, SVGElement var2);

    public boolean checkIntersection(SVGElement var1, SVGRect var2);

    public boolean checkEnclosure(SVGElement var1, SVGRect var2);

    public void deselectAll();

    public SVGNumber createSVGNumber();

    public SVGLength createSVGLength();

    public SVGAngle createSVGAngle();

    public SVGPoint createSVGPoint();

    public SVGMatrix createSVGMatrix();

    public SVGRect createSVGRect();

    public SVGTransform createSVGTransform();

    public SVGTransform createSVGTransformFromMatrix(SVGMatrix var1);

    public Element getElementById(String var1);
}

