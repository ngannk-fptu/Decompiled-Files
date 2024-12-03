/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGLength;

public interface SVGLengthList {
    public int getNumberOfItems();

    public void clear() throws DOMException;

    public SVGLength initialize(SVGLength var1) throws DOMException, SVGException;

    public SVGLength getItem(int var1) throws DOMException;

    public SVGLength insertItemBefore(SVGLength var1, int var2) throws DOMException, SVGException;

    public SVGLength replaceItem(SVGLength var1, int var2) throws DOMException, SVGException;

    public SVGLength removeItem(int var1) throws DOMException;

    public SVGLength appendItem(SVGLength var1) throws DOMException, SVGException;
}

