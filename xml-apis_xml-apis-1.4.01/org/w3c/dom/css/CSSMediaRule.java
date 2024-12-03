/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.css;

import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.stylesheets.MediaList;

public interface CSSMediaRule
extends CSSRule {
    public MediaList getMedia();

    public CSSRuleList getCssRules();

    public int insertRule(String var1, int var2) throws DOMException;

    public void deleteRule(int var1) throws DOMException;
}

