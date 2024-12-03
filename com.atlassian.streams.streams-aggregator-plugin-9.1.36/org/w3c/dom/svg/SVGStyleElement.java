/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGElement;

public interface SVGStyleElement
extends SVGElement {
    public String getXMLspace();

    public void setXMLspace(String var1) throws DOMException;

    public String getType();

    public void setType(String var1) throws DOMException;

    public String getMedia();

    public void setMedia(String var1) throws DOMException;

    public String getTitle();

    public void setTitle(String var1) throws DOMException;
}

