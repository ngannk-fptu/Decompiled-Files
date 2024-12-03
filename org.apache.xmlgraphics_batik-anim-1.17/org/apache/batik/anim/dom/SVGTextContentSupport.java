/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.svg.SVGOMPoint
 *  org.apache.batik.dom.svg.SVGTextContent
 *  org.w3c.dom.svg.SVGPoint
 *  org.w3c.dom.svg.SVGRect
 */
package org.apache.batik.anim.dom;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.dom.svg.SVGOMPoint;
import org.apache.batik.dom.svg.SVGTextContent;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGRect;

public class SVGTextContentSupport {
    public static int getNumberOfChars(Element elt) {
        SVGOMElement svgelt = (SVGOMElement)((Object)elt);
        return ((SVGTextContent)svgelt.getSVGContext()).getNumberOfChars();
    }

    public static SVGRect getExtentOfChar(Element elt, final int charnum) {
        final SVGOMElement svgelt = (SVGOMElement)((Object)elt);
        if (charnum < 0 || charnum >= SVGTextContentSupport.getNumberOfChars(elt)) {
            throw svgelt.createDOMException((short)1, "", null);
        }
        final SVGTextContent context = (SVGTextContent)svgelt.getSVGContext();
        return new SVGRect(){

            public float getX() {
                return (float)SVGTextContentSupport.getExtent(svgelt, context, charnum).getX();
            }

            public void setX(float x) throws DOMException {
                throw svgelt.createDOMException((short)7, "readonly.rect", null);
            }

            public float getY() {
                return (float)SVGTextContentSupport.getExtent(svgelt, context, charnum).getY();
            }

            public void setY(float y) throws DOMException {
                throw svgelt.createDOMException((short)7, "readonly.rect", null);
            }

            public float getWidth() {
                return (float)SVGTextContentSupport.getExtent(svgelt, context, charnum).getWidth();
            }

            public void setWidth(float width) throws DOMException {
                throw svgelt.createDOMException((short)7, "readonly.rect", null);
            }

            public float getHeight() {
                return (float)SVGTextContentSupport.getExtent(svgelt, context, charnum).getHeight();
            }

            public void setHeight(float height) throws DOMException {
                throw svgelt.createDOMException((short)7, "readonly.rect", null);
            }
        };
    }

    protected static Rectangle2D getExtent(SVGOMElement svgelt, SVGTextContent context, int charnum) {
        Rectangle2D r2d = context.getExtentOfChar(charnum);
        if (r2d == null) {
            throw svgelt.createDOMException((short)1, "", null);
        }
        return r2d;
    }

    public static SVGPoint getStartPositionOfChar(Element elt, final int charnum) throws DOMException {
        SVGOMElement svgelt = (SVGOMElement)((Object)elt);
        if (charnum < 0 || charnum >= SVGTextContentSupport.getNumberOfChars(elt)) {
            throw svgelt.createDOMException((short)1, "", null);
        }
        final SVGTextContent context = (SVGTextContent)svgelt.getSVGContext();
        return new SVGTextPoint(svgelt){

            public float getX() {
                return (float)SVGTextContentSupport.getStartPos(this.svgelt, context, charnum).getX();
            }

            public float getY() {
                return (float)SVGTextContentSupport.getStartPos(this.svgelt, context, charnum).getY();
            }
        };
    }

    protected static Point2D getStartPos(SVGOMElement svgelt, SVGTextContent context, int charnum) {
        Point2D p2d = context.getStartPositionOfChar(charnum);
        if (p2d == null) {
            throw svgelt.createDOMException((short)1, "", null);
        }
        return p2d;
    }

    public static SVGPoint getEndPositionOfChar(Element elt, final int charnum) throws DOMException {
        SVGOMElement svgelt = (SVGOMElement)((Object)elt);
        if (charnum < 0 || charnum >= SVGTextContentSupport.getNumberOfChars(elt)) {
            throw svgelt.createDOMException((short)1, "", null);
        }
        final SVGTextContent context = (SVGTextContent)svgelt.getSVGContext();
        return new SVGTextPoint(svgelt){

            public float getX() {
                return (float)SVGTextContentSupport.getEndPos(this.svgelt, context, charnum).getX();
            }

            public float getY() {
                return (float)SVGTextContentSupport.getEndPos(this.svgelt, context, charnum).getY();
            }
        };
    }

    protected static Point2D getEndPos(SVGOMElement svgelt, SVGTextContent context, int charnum) {
        Point2D p2d = context.getEndPositionOfChar(charnum);
        if (p2d == null) {
            throw svgelt.createDOMException((short)1, "", null);
        }
        return p2d;
    }

    public static void selectSubString(Element elt, int charnum, int nchars) {
        SVGOMElement svgelt = (SVGOMElement)((Object)elt);
        if (charnum < 0 || charnum >= SVGTextContentSupport.getNumberOfChars(elt)) {
            throw svgelt.createDOMException((short)1, "", null);
        }
        SVGTextContent context = (SVGTextContent)svgelt.getSVGContext();
        context.selectSubString(charnum, nchars);
    }

    public static float getRotationOfChar(Element elt, int charnum) {
        SVGOMElement svgelt = (SVGOMElement)((Object)elt);
        if (charnum < 0 || charnum >= SVGTextContentSupport.getNumberOfChars(elt)) {
            throw svgelt.createDOMException((short)1, "", null);
        }
        SVGTextContent context = (SVGTextContent)svgelt.getSVGContext();
        return context.getRotationOfChar(charnum);
    }

    public static float getComputedTextLength(Element elt) {
        SVGOMElement svgelt = (SVGOMElement)((Object)elt);
        SVGTextContent context = (SVGTextContent)svgelt.getSVGContext();
        return context.getComputedTextLength();
    }

    public static float getSubStringLength(Element elt, int charnum, int nchars) {
        SVGOMElement svgelt = (SVGOMElement)((Object)elt);
        if (charnum < 0 || charnum >= SVGTextContentSupport.getNumberOfChars(elt)) {
            throw svgelt.createDOMException((short)1, "", null);
        }
        SVGTextContent context = (SVGTextContent)svgelt.getSVGContext();
        return context.getSubStringLength(charnum, nchars);
    }

    public static int getCharNumAtPosition(Element elt, float x, float y) throws DOMException {
        SVGOMElement svgelt = (SVGOMElement)((Object)elt);
        SVGTextContent context = (SVGTextContent)svgelt.getSVGContext();
        return context.getCharNumAtPosition(x, y);
    }

    public static class SVGTextPoint
    extends SVGOMPoint {
        SVGOMElement svgelt;

        SVGTextPoint(SVGOMElement elem) {
            this.svgelt = elem;
        }

        public void setX(float x) throws DOMException {
            throw this.svgelt.createDOMException((short)7, "readonly.point", null);
        }

        public void setY(float y) throws DOMException {
            throw this.svgelt.createDOMException((short)7, "readonly.point", null);
        }
    }
}

