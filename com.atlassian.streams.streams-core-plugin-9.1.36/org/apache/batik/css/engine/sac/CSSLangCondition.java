/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.sac;

import java.util.Set;
import org.apache.batik.css.engine.sac.ExtendedCondition;
import org.w3c.css.sac.LangCondition;
import org.w3c.dom.Element;

public class CSSLangCondition
implements LangCondition,
ExtendedCondition {
    protected String lang;
    protected String langHyphen;

    public CSSLangCondition(String lang) {
        this.lang = lang.toLowerCase();
        this.langHyphen = lang + '-';
    }

    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        CSSLangCondition c = (CSSLangCondition)obj;
        return c.lang.equals(this.lang);
    }

    @Override
    public short getConditionType() {
        return 6;
    }

    @Override
    public String getLang() {
        return this.lang;
    }

    @Override
    public int getSpecificity() {
        return 256;
    }

    @Override
    public boolean match(Element e, String pseudoE) {
        String s = e.getAttribute("lang").toLowerCase();
        if (s.equals(this.lang) || s.startsWith(this.langHyphen)) {
            return true;
        }
        s = e.getAttributeNS("http://www.w3.org/XML/1998/namespace", "lang").toLowerCase();
        return s.equals(this.lang) || s.startsWith(this.langHyphen);
    }

    @Override
    public void fillAttributeSet(Set attrSet) {
        attrSet.add("lang");
    }

    public String toString() {
        return ":lang(" + this.lang + ')';
    }
}

