/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGException;

public interface SVGMatrix {
    public float getA();

    public void setA(float var1) throws DOMException;

    public float getB();

    public void setB(float var1) throws DOMException;

    public float getC();

    public void setC(float var1) throws DOMException;

    public float getD();

    public void setD(float var1) throws DOMException;

    public float getE();

    public void setE(float var1) throws DOMException;

    public float getF();

    public void setF(float var1) throws DOMException;

    public SVGMatrix multiply(SVGMatrix var1);

    public SVGMatrix inverse() throws SVGException;

    public SVGMatrix translate(float var1, float var2);

    public SVGMatrix scale(float var1);

    public SVGMatrix scaleNonUniform(float var1, float var2);

    public SVGMatrix rotate(float var1);

    public SVGMatrix rotateFromVector(float var1, float var2) throws SVGException;

    public SVGMatrix flipX();

    public SVGMatrix flipY();

    public SVGMatrix skewX(float var1);

    public SVGMatrix skewY(float var1);
}

