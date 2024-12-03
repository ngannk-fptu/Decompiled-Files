/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.parser.PathHandler
 *  org.w3c.dom.svg.SVGPathSeg
 *  org.w3c.dom.svg.SVGPathSegArcAbs
 *  org.w3c.dom.svg.SVGPathSegArcRel
 *  org.w3c.dom.svg.SVGPathSegCurvetoCubicAbs
 *  org.w3c.dom.svg.SVGPathSegCurvetoCubicRel
 *  org.w3c.dom.svg.SVGPathSegCurvetoCubicSmoothAbs
 *  org.w3c.dom.svg.SVGPathSegCurvetoCubicSmoothRel
 *  org.w3c.dom.svg.SVGPathSegCurvetoQuadraticAbs
 *  org.w3c.dom.svg.SVGPathSegCurvetoQuadraticRel
 *  org.w3c.dom.svg.SVGPathSegCurvetoQuadraticSmoothAbs
 *  org.w3c.dom.svg.SVGPathSegCurvetoQuadraticSmoothRel
 *  org.w3c.dom.svg.SVGPathSegLinetoAbs
 *  org.w3c.dom.svg.SVGPathSegLinetoHorizontalAbs
 *  org.w3c.dom.svg.SVGPathSegLinetoHorizontalRel
 *  org.w3c.dom.svg.SVGPathSegLinetoRel
 *  org.w3c.dom.svg.SVGPathSegLinetoVerticalAbs
 *  org.w3c.dom.svg.SVGPathSegLinetoVerticalRel
 *  org.w3c.dom.svg.SVGPathSegList
 *  org.w3c.dom.svg.SVGPathSegMovetoAbs
 *  org.w3c.dom.svg.SVGPathSegMovetoRel
 */
package org.apache.batik.dom.svg;

import org.apache.batik.parser.PathHandler;
import org.w3c.dom.svg.SVGPathSeg;
import org.w3c.dom.svg.SVGPathSegArcAbs;
import org.w3c.dom.svg.SVGPathSegArcRel;
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
import org.w3c.dom.svg.SVGPathSegList;
import org.w3c.dom.svg.SVGPathSegMovetoAbs;
import org.w3c.dom.svg.SVGPathSegMovetoRel;

public abstract class SVGAnimatedPathDataSupport {
    public static void handlePathSegList(SVGPathSegList p, PathHandler h) {
        int n = p.getNumberOfItems();
        h.startPath();
        block21: for (int i = 0; i < n; ++i) {
            SVGPathSeg seg = p.getItem(i);
            switch (seg.getPathSegType()) {
                case 1: {
                    h.closePath();
                    continue block21;
                }
                case 2: {
                    SVGPathSegMovetoAbs s = (SVGPathSegMovetoAbs)seg;
                    h.movetoAbs(s.getX(), s.getY());
                    continue block21;
                }
                case 3: {
                    SVGPathSegMovetoAbs s = (SVGPathSegMovetoRel)seg;
                    h.movetoRel(s.getX(), s.getY());
                    continue block21;
                }
                case 4: {
                    SVGPathSegMovetoAbs s = (SVGPathSegLinetoAbs)seg;
                    h.linetoAbs(s.getX(), s.getY());
                    continue block21;
                }
                case 5: {
                    SVGPathSegMovetoAbs s = (SVGPathSegLinetoRel)seg;
                    h.linetoRel(s.getX(), s.getY());
                    continue block21;
                }
                case 6: {
                    SVGPathSegMovetoAbs s = (SVGPathSegCurvetoCubicAbs)seg;
                    h.curvetoCubicAbs(s.getX1(), s.getY1(), s.getX2(), s.getY2(), s.getX(), s.getY());
                    continue block21;
                }
                case 7: {
                    SVGPathSegMovetoAbs s = (SVGPathSegCurvetoCubicRel)seg;
                    h.curvetoCubicRel(s.getX1(), s.getY1(), s.getX2(), s.getY2(), s.getX(), s.getY());
                    continue block21;
                }
                case 8: {
                    SVGPathSegMovetoAbs s = (SVGPathSegCurvetoQuadraticAbs)seg;
                    h.curvetoQuadraticAbs(s.getX1(), s.getY1(), s.getX(), s.getY());
                    continue block21;
                }
                case 9: {
                    SVGPathSegMovetoAbs s = (SVGPathSegCurvetoQuadraticRel)seg;
                    h.curvetoQuadraticRel(s.getX1(), s.getY1(), s.getX(), s.getY());
                    continue block21;
                }
                case 10: {
                    SVGPathSegMovetoAbs s = (SVGPathSegArcAbs)seg;
                    h.arcAbs(s.getR1(), s.getR2(), s.getAngle(), s.getLargeArcFlag(), s.getSweepFlag(), s.getX(), s.getY());
                    continue block21;
                }
                case 11: {
                    SVGPathSegMovetoAbs s = (SVGPathSegArcRel)seg;
                    h.arcRel(s.getR1(), s.getR2(), s.getAngle(), s.getLargeArcFlag(), s.getSweepFlag(), s.getX(), s.getY());
                    continue block21;
                }
                case 12: {
                    SVGPathSegMovetoAbs s = (SVGPathSegLinetoHorizontalAbs)seg;
                    h.linetoHorizontalAbs(s.getX());
                    continue block21;
                }
                case 13: {
                    SVGPathSegMovetoAbs s = (SVGPathSegLinetoHorizontalRel)seg;
                    h.linetoHorizontalRel(s.getX());
                    continue block21;
                }
                case 14: {
                    SVGPathSegMovetoAbs s = (SVGPathSegLinetoVerticalAbs)seg;
                    h.linetoVerticalAbs(s.getY());
                    continue block21;
                }
                case 15: {
                    SVGPathSegMovetoAbs s = (SVGPathSegLinetoVerticalRel)seg;
                    h.linetoVerticalRel(s.getY());
                    continue block21;
                }
                case 16: {
                    SVGPathSegMovetoAbs s = (SVGPathSegCurvetoCubicSmoothAbs)seg;
                    h.curvetoCubicSmoothAbs(s.getX2(), s.getY2(), s.getX(), s.getY());
                    continue block21;
                }
                case 17: {
                    SVGPathSegMovetoAbs s = (SVGPathSegCurvetoCubicSmoothRel)seg;
                    h.curvetoCubicSmoothRel(s.getX2(), s.getY2(), s.getX(), s.getY());
                    continue block21;
                }
                case 18: {
                    SVGPathSegMovetoAbs s = (SVGPathSegCurvetoQuadraticSmoothAbs)seg;
                    h.curvetoQuadraticSmoothAbs(s.getX(), s.getY());
                    continue block21;
                }
                case 19: {
                    SVGPathSegMovetoAbs s = (SVGPathSegCurvetoQuadraticSmoothRel)seg;
                    h.curvetoQuadraticSmoothRel(s.getX(), s.getY());
                    continue block21;
                }
            }
        }
        h.endPath();
    }
}

