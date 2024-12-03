/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.events.EventTarget;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGAnimatedPathData;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGExternalResourcesRequired;
import org.w3c.dom.svg.SVGLangSpace;
import org.w3c.dom.svg.SVGPathSegArcAbs;
import org.w3c.dom.svg.SVGPathSegArcRel;
import org.w3c.dom.svg.SVGPathSegClosePath;
import org.w3c.dom.svg.SVGPathSegCurvetoCubicAbs;
import org.w3c.dom.svg.SVGPathSegCurvetoCubicRel;
import org.w3c.dom.svg.SVGPathSegCurvetoCubicSmoothAbs;
import org.w3c.dom.svg.SVGPathSegCurvetoCubicSmoothRel;
import org.w3c.dom.svg.SVGPathSegCurvetoQuadraticAbs;
import org.w3c.dom.svg.SVGPathSegCurvetoQuadraticRel;
import org.w3c.dom.svg.SVGPathSegCurvetoQuadraticSmoothAbs;
import org.w3c.dom.svg.SVGPathSegCurvetoQuadraticSmoothRel;
import org.w3c.dom.svg.SVGPathSegLinetoAbs;
import org.w3c.dom.svg.SVGPathSegLinetoHorizontalAbs;
import org.w3c.dom.svg.SVGPathSegLinetoHorizontalRel;
import org.w3c.dom.svg.SVGPathSegLinetoRel;
import org.w3c.dom.svg.SVGPathSegLinetoVerticalAbs;
import org.w3c.dom.svg.SVGPathSegLinetoVerticalRel;
import org.w3c.dom.svg.SVGPathSegMovetoAbs;
import org.w3c.dom.svg.SVGPathSegMovetoRel;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGStylable;
import org.w3c.dom.svg.SVGTests;
import org.w3c.dom.svg.SVGTransformable;

public interface SVGPathElement
extends SVGElement,
SVGTests,
SVGLangSpace,
SVGExternalResourcesRequired,
SVGStylable,
SVGTransformable,
EventTarget,
SVGAnimatedPathData {
    public SVGAnimatedNumber getPathLength();

    public float getTotalLength();

    public SVGPoint getPointAtLength(float var1);

    public int getPathSegAtLength(float var1);

    public SVGPathSegClosePath createSVGPathSegClosePath();

    public SVGPathSegMovetoAbs createSVGPathSegMovetoAbs(float var1, float var2);

    public SVGPathSegMovetoRel createSVGPathSegMovetoRel(float var1, float var2);

    public SVGPathSegLinetoAbs createSVGPathSegLinetoAbs(float var1, float var2);

    public SVGPathSegLinetoRel createSVGPathSegLinetoRel(float var1, float var2);

    public SVGPathSegCurvetoCubicAbs createSVGPathSegCurvetoCubicAbs(float var1, float var2, float var3, float var4, float var5, float var6);

    public SVGPathSegCurvetoCubicRel createSVGPathSegCurvetoCubicRel(float var1, float var2, float var3, float var4, float var5, float var6);

    public SVGPathSegCurvetoQuadraticAbs createSVGPathSegCurvetoQuadraticAbs(float var1, float var2, float var3, float var4);

    public SVGPathSegCurvetoQuadraticRel createSVGPathSegCurvetoQuadraticRel(float var1, float var2, float var3, float var4);

    public SVGPathSegArcAbs createSVGPathSegArcAbs(float var1, float var2, float var3, float var4, float var5, boolean var6, boolean var7);

    public SVGPathSegArcRel createSVGPathSegArcRel(float var1, float var2, float var3, float var4, float var5, boolean var6, boolean var7);

    public SVGPathSegLinetoHorizontalAbs createSVGPathSegLinetoHorizontalAbs(float var1);

    public SVGPathSegLinetoHorizontalRel createSVGPathSegLinetoHorizontalRel(float var1);

    public SVGPathSegLinetoVerticalAbs createSVGPathSegLinetoVerticalAbs(float var1);

    public SVGPathSegLinetoVerticalRel createSVGPathSegLinetoVerticalRel(float var1);

    public SVGPathSegCurvetoCubicSmoothAbs createSVGPathSegCurvetoCubicSmoothAbs(float var1, float var2, float var3, float var4);

    public SVGPathSegCurvetoCubicSmoothRel createSVGPathSegCurvetoCubicSmoothRel(float var1, float var2, float var3, float var4);

    public SVGPathSegCurvetoQuadraticSmoothAbs createSVGPathSegCurvetoQuadraticSmoothAbs(float var1, float var2);

    public SVGPathSegCurvetoQuadraticSmoothRel createSVGPathSegCurvetoQuadraticSmoothRel(float var1, float var2);
}

