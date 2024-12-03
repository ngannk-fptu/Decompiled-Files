/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGMatrix;

public interface SVGPoint {
    public float getX();

    public void setX(float var1) throws DOMException;

    public float getY();

    public void setY(float var1) throws DOMException;

    public SVGPoint matrixTransform(SVGMatrix var1);
}

