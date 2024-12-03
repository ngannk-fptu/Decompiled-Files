/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGPoint;

public interface SVGPointList {
    public int getNumberOfItems();

    public void clear() throws DOMException;

    public SVGPoint initialize(SVGPoint var1) throws DOMException, SVGException;

    public SVGPoint getItem(int var1) throws DOMException;

    public SVGPoint insertItemBefore(SVGPoint var1, int var2) throws DOMException, SVGException;

    public SVGPoint replaceItem(SVGPoint var1, int var2) throws DOMException, SVGException;

    public SVGPoint removeItem(int var1) throws DOMException;

    public SVGPoint appendItem(SVGPoint var1) throws DOMException, SVGException;
}

