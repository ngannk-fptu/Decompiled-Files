/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3c.dom.svg.SVGMatrix
 */
package org.apache.batik.dom.svg;

import java.awt.geom.AffineTransform;
import org.apache.batik.dom.svg.AbstractSVGMatrix;
import org.apache.batik.dom.svg.AbstractSVGTransform;
import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGMatrix;

public class SVGOMTransform
extends AbstractSVGTransform {
    public SVGOMTransform() {
        this.affineTransform = new AffineTransform();
    }

    @Override
    protected SVGMatrix createMatrix() {
        return new AbstractSVGMatrix(){

            @Override
            protected AffineTransform getAffineTransform() {
                return SVGOMTransform.this.affineTransform;
            }

            @Override
            public void setA(float a) throws DOMException {
                SVGOMTransform.this.setType((short)1);
                super.setA(a);
            }

            @Override
            public void setB(float b) throws DOMException {
                SVGOMTransform.this.setType((short)1);
                super.setB(b);
            }

            @Override
            public void setC(float c) throws DOMException {
                SVGOMTransform.this.setType((short)1);
                super.setC(c);
            }

            @Override
            public void setD(float d) throws DOMException {
                SVGOMTransform.this.setType((short)1);
                super.setD(d);
            }

            @Override
            public void setE(float e) throws DOMException {
                SVGOMTransform.this.setType((short)1);
                super.setE(e);
            }

            @Override
            public void setF(float f) throws DOMException {
                SVGOMTransform.this.setType((short)1);
                super.setF(f);
            }
        };
    }
}

