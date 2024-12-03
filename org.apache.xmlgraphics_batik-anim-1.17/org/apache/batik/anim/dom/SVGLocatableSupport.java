/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.css.engine.SVGCSSEngine
 *  org.apache.batik.dom.svg.AbstractSVGMatrix
 *  org.apache.batik.dom.svg.SVGContext
 *  org.w3c.dom.svg.SVGElement
 *  org.w3c.dom.svg.SVGException
 *  org.w3c.dom.svg.SVGFitToViewBox
 *  org.w3c.dom.svg.SVGMatrix
 *  org.w3c.dom.svg.SVGRect
 */
package org.apache.batik.anim.dom;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.css.engine.SVGCSSEngine;
import org.apache.batik.dom.svg.AbstractSVGMatrix;
import org.apache.batik.dom.svg.SVGContext;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGFitToViewBox;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGRect;

public class SVGLocatableSupport {
    public static SVGElement getNearestViewportElement(Element e) {
        Element elt = e;
        while (elt != null && !((elt = SVGCSSEngine.getParentCSSStylableElement((Element)elt)) instanceof SVGFitToViewBox)) {
        }
        return (SVGElement)elt;
    }

    public static SVGElement getFarthestViewportElement(Element elt) {
        Element rootSVG = elt.getOwnerDocument().getDocumentElement();
        if (elt == rootSVG) {
            return null;
        }
        return (SVGElement)rootSVG;
    }

    public static SVGRect getBBox(Element elt) {
        final SVGOMElement svgelt = (SVGOMElement)((Object)elt);
        SVGContext svgctx = svgelt.getSVGContext();
        if (svgctx == null) {
            return null;
        }
        if (svgctx.getBBox() == null) {
            return null;
        }
        return new SVGRect(){

            public float getX() {
                return (float)svgelt.getSVGContext().getBBox().getX();
            }

            public void setX(float x) throws DOMException {
                throw svgelt.createDOMException((short)7, "readonly.rect", null);
            }

            public float getY() {
                return (float)svgelt.getSVGContext().getBBox().getY();
            }

            public void setY(float y) throws DOMException {
                throw svgelt.createDOMException((short)7, "readonly.rect", null);
            }

            public float getWidth() {
                return (float)svgelt.getSVGContext().getBBox().getWidth();
            }

            public void setWidth(float width) throws DOMException {
                throw svgelt.createDOMException((short)7, "readonly.rect", null);
            }

            public float getHeight() {
                return (float)svgelt.getSVGContext().getBBox().getHeight();
            }

            public void setHeight(float height) throws DOMException {
                throw svgelt.createDOMException((short)7, "readonly.rect", null);
            }
        };
    }

    public static SVGMatrix getCTM(Element elt) {
        final SVGOMElement svgelt = (SVGOMElement)((Object)elt);
        return new AbstractSVGMatrix(){

            protected AffineTransform getAffineTransform() {
                return svgelt.getSVGContext().getCTM();
            }
        };
    }

    public static SVGMatrix getScreenCTM(Element elt) {
        final SVGOMElement svgelt = (SVGOMElement)((Object)elt);
        return new AbstractSVGMatrix(){

            protected AffineTransform getAffineTransform() {
                SVGContext context = svgelt.getSVGContext();
                AffineTransform ret = context.getGlobalTransform();
                AffineTransform scrnTrans = context.getScreenTransform();
                if (scrnTrans != null) {
                    ret.preConcatenate(scrnTrans);
                }
                return ret;
            }
        };
    }

    public static SVGMatrix getTransformToElement(Element elt, SVGElement element) throws SVGException {
        final SVGOMElement currentElt = (SVGOMElement)((Object)elt);
        final SVGOMElement targetElt = (SVGOMElement)element;
        return new AbstractSVGMatrix(){

            protected AffineTransform getAffineTransform() {
                AffineTransform tat;
                AffineTransform cat = currentElt.getSVGContext().getGlobalTransform();
                if (cat == null) {
                    cat = new AffineTransform();
                }
                if ((tat = targetElt.getSVGContext().getGlobalTransform()) == null) {
                    tat = new AffineTransform();
                }
                AffineTransform at = new AffineTransform(cat);
                try {
                    at.preConcatenate(tat.createInverse());
                    return at;
                }
                catch (NoninvertibleTransformException ex) {
                    throw currentElt.createSVGException((short)2, "noninvertiblematrix", null);
                }
            }
        };
    }
}

