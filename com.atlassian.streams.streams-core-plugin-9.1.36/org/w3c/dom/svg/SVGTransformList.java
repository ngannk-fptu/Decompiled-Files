/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGTransform;

public interface SVGTransformList {
    public int getNumberOfItems();

    public void clear() throws DOMException;

    public SVGTransform initialize(SVGTransform var1) throws DOMException, SVGException;

    public SVGTransform getItem(int var1) throws DOMException;

    public SVGTransform insertItemBefore(SVGTransform var1, int var2) throws DOMException, SVGException;

    public SVGTransform replaceItem(SVGTransform var1, int var2) throws DOMException, SVGException;

    public SVGTransform removeItem(int var1) throws DOMException;

    public SVGTransform appendItem(SVGTransform var1) throws DOMException, SVGException;

    public SVGTransform createSVGTransformFromMatrix(SVGMatrix var1);

    public SVGTransform consolidate();
}

