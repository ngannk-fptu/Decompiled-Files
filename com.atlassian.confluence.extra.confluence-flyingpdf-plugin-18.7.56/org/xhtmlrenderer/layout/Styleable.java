/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.layout;

import org.w3c.dom.Element;
import org.xhtmlrenderer.css.style.CalculatedStyle;

public interface Styleable {
    public CalculatedStyle getStyle();

    public void setStyle(CalculatedStyle var1);

    public Element getElement();

    public void setElement(Element var1);

    public String getPseudoElementOrClass();
}

