/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGPathSeg;

public interface SVGPathSegArcAbs
extends SVGPathSeg {
    public float getX();

    public void setX(float var1) throws DOMException;

    public float getY();

    public void setY(float var1) throws DOMException;

    public float getR1();

    public void setR1(float var1) throws DOMException;

    public float getR2();

    public void setR2(float var1) throws DOMException;

    public float getAngle();

    public void setAngle(float var1) throws DOMException;

    public boolean getLargeArcFlag();

    public void setLargeArcFlag(boolean var1) throws DOMException;

    public boolean getSweepFlag();

    public void setSweepFlag(boolean var1) throws DOMException;
}

