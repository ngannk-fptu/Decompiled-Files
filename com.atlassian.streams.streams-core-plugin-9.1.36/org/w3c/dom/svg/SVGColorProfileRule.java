/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGCSSRule;
import org.w3c.dom.svg.SVGRenderingIntent;

public interface SVGColorProfileRule
extends SVGCSSRule,
SVGRenderingIntent {
    public String getSrc();

    public void setSrc(String var1) throws DOMException;

    public String getName();

    public void setName(String var1) throws DOMException;

    public short getRenderingIntent();

    public void setRenderingIntent(short var1) throws DOMException;
}

