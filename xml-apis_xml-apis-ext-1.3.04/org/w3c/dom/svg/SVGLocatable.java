/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGRect;

public interface SVGLocatable {
    public SVGElement getNearestViewportElement();

    public SVGElement getFarthestViewportElement();

    public SVGRect getBBox();

    public SVGMatrix getCTM();

    public SVGMatrix getScreenCTM();

    public SVGMatrix getTransformToElement(SVGElement var1) throws SVGException;
}

