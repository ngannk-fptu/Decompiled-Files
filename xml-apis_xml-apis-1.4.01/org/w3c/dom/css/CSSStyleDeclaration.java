/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.css;

import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSValue;

public interface CSSStyleDeclaration {
    public String getCssText();

    public void setCssText(String var1) throws DOMException;

    public String getPropertyValue(String var1);

    public CSSValue getPropertyCSSValue(String var1);

    public String removeProperty(String var1) throws DOMException;

    public String getPropertyPriority(String var1);

    public void setProperty(String var1, String var2, String var3) throws DOMException;

    public int getLength();

    public String item(int var1);

    public CSSRule getParentRule();
}

