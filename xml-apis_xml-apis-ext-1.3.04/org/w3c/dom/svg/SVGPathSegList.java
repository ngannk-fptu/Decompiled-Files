/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGPathSeg;

public interface SVGPathSegList {
    public int getNumberOfItems();

    public void clear() throws DOMException;

    public SVGPathSeg initialize(SVGPathSeg var1) throws DOMException, SVGException;

    public SVGPathSeg getItem(int var1) throws DOMException;

    public SVGPathSeg insertItemBefore(SVGPathSeg var1, int var2) throws DOMException, SVGException;

    public SVGPathSeg replaceItem(SVGPathSeg var1, int var2) throws DOMException, SVGException;

    public SVGPathSeg removeItem(int var1) throws DOMException;

    public SVGPathSeg appendItem(SVGPathSeg var1) throws DOMException, SVGException;
}

