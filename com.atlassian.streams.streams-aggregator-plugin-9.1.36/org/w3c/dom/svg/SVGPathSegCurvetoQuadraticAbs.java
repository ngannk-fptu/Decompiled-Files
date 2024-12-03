/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGPathSeg;

public interface SVGPathSegCurvetoQuadraticAbs
extends SVGPathSeg {
    public float getX();

    public void setX(float var1) throws DOMException;

    public float getY();

    public void setY(float var1) throws DOMException;

    public float getX1();

    public void setX1(float var1) throws DOMException;

    public float getY1();

    public void setY1(float var1) throws DOMException;
}

