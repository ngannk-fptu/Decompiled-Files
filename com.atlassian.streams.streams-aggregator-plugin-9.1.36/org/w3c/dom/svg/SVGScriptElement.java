/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGExternalResourcesRequired;
import org.w3c.dom.svg.SVGURIReference;

public interface SVGScriptElement
extends SVGElement,
SVGURIReference,
SVGExternalResourcesRequired {
    public String getType();

    public void setType(String var1) throws DOMException;
}

