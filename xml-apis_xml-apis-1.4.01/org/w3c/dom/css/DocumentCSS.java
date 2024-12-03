/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.css;

import org.w3c.dom.Element;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.stylesheets.DocumentStyle;

public interface DocumentCSS
extends DocumentStyle {
    public CSSStyleDeclaration getOverrideStyle(Element var1, String var2);
}

