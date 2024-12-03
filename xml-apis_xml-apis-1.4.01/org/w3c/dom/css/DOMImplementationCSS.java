/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.css;

import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.css.CSSStyleSheet;

public interface DOMImplementationCSS
extends DOMImplementation {
    public CSSStyleSheet createCSSStyleSheet(String var1, String var2) throws DOMException;
}

