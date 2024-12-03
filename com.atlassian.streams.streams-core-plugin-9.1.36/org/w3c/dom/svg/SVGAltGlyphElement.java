/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGTextPositioningElement;
import org.w3c.dom.svg.SVGURIReference;

public interface SVGAltGlyphElement
extends SVGTextPositioningElement,
SVGURIReference {
    public String getGlyphRef();

    public void setGlyphRef(String var1) throws DOMException;

    public String getFormat();

    public void setFormat(String var1) throws DOMException;
}

