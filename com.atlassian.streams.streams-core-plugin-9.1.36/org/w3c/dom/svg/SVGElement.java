/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGSVGElement;

public interface SVGElement
extends Element {
    public String getId();

    public void setId(String var1) throws DOMException;

    public String getXMLbase();

    public void setXMLbase(String var1) throws DOMException;

    public SVGSVGElement getOwnerSVGElement();

    public SVGElement getViewportElement();
}

