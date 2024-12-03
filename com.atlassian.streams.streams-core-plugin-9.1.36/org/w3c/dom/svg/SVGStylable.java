/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.svg.SVGAnimatedString;

public interface SVGStylable {
    public SVGAnimatedString getClassName();

    public CSSStyleDeclaration getStyle();

    public CSSValue getPresentationAttribute(String var1);
}

