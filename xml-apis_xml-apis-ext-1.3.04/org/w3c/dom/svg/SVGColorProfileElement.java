/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGRenderingIntent;
import org.w3c.dom.svg.SVGURIReference;

public interface SVGColorProfileElement
extends SVGElement,
SVGURIReference,
SVGRenderingIntent {
    public String getLocal();

    public void setLocal(String var1) throws DOMException;

    public String getName();

    public void setName(String var1) throws DOMException;

    public short getRenderingIntent();

    public void setRenderingIntent(short var1) throws DOMException;
}

