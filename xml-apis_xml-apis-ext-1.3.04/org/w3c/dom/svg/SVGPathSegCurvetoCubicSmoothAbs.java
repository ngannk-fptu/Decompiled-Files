/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGPathSeg;

public interface SVGPathSegCurvetoCubicSmoothAbs
extends SVGPathSeg {
    public float getX();

    public void setX(float var1) throws DOMException;

    public float getY();

    public void setY(float var1) throws DOMException;

    public float getX2();

    public void setX2(float var1) throws DOMException;

    public float getY2();

    public void setY2(float var1) throws DOMException;
}

