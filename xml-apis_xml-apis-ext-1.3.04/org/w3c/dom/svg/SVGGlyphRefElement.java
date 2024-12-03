/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGStylable;
import org.w3c.dom.svg.SVGURIReference;

public interface SVGGlyphRefElement
extends SVGElement,
SVGURIReference,
SVGStylable {
    public String getGlyphRef();

    public void setGlyphRef(String var1) throws DOMException;

    public String getFormat();

    public void setFormat(String var1) throws DOMException;

    public float getX();

    public void setX(float var1) throws DOMException;

    public float getY();

    public void setY(float var1) throws DOMException;

    public float getDx();

    public void setDx(float var1) throws DOMException;

    public float getDy();

    public void setDy(float var1) throws DOMException;
}

