/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.css;

import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSRule;

public interface CSSCharsetRule
extends CSSRule {
    public String getEncoding();

    public void setEncoding(String var1) throws DOMException;
}

