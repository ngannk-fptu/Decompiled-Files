/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGException;

public interface SVGStringList {
    public int getNumberOfItems();

    public void clear() throws DOMException;

    public String initialize(String var1) throws DOMException, SVGException;

    public String getItem(int var1) throws DOMException;

    public String insertItemBefore(String var1, int var2) throws DOMException, SVGException;

    public String replaceItem(String var1, int var2) throws DOMException, SVGException;

    public String removeItem(int var1) throws DOMException;

    public String appendItem(String var1) throws DOMException, SVGException;
}

