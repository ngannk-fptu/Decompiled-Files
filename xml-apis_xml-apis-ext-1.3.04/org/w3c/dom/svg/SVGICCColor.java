/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGNumberList;

public interface SVGICCColor {
    public String getColorProfile();

    public void setColorProfile(String var1) throws DOMException;

    public SVGNumberList getColors();
}

