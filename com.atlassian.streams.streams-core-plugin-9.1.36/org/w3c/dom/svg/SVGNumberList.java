/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGNumber;

public interface SVGNumberList {
    public int getNumberOfItems();

    public void clear() throws DOMException;

    public SVGNumber initialize(SVGNumber var1) throws DOMException, SVGException;

    public SVGNumber getItem(int var1) throws DOMException;

    public SVGNumber insertItemBefore(SVGNumber var1, int var2) throws DOMException, SVGException;

    public SVGNumber replaceItem(SVGNumber var1, int var2) throws DOMException, SVGException;

    public SVGNumber removeItem(int var1) throws DOMException;

    public SVGNumber appendItem(SVGNumber var1) throws DOMException, SVGException;
}

