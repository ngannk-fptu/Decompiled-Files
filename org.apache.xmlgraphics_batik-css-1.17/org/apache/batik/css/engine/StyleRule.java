/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3c.css.sac.SelectorList
 */
package org.apache.batik.css.engine;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.Rule;
import org.apache.batik.css.engine.StyleDeclaration;
import org.w3c.css.sac.SelectorList;

public class StyleRule
implements Rule {
    public static final short TYPE = 0;
    protected SelectorList selectorList;
    protected StyleDeclaration styleDeclaration;

    @Override
    public short getType() {
        return 0;
    }

    public void setSelectorList(SelectorList sl) {
        this.selectorList = sl;
    }

    public SelectorList getSelectorList() {
        return this.selectorList;
    }

    public void setStyleDeclaration(StyleDeclaration sd) {
        this.styleDeclaration = sd;
    }

    public StyleDeclaration getStyleDeclaration() {
        return this.styleDeclaration;
    }

    @Override
    public String toString(CSSEngine eng) {
        StringBuffer sb = new StringBuffer();
        if (this.selectorList != null) {
            sb.append(this.selectorList.item(0));
            for (int i = 1; i < this.selectorList.getLength(); ++i) {
                sb.append(", ");
                sb.append(this.selectorList.item(i));
            }
        }
        sb.append(" {\n");
        if (this.styleDeclaration != null) {
            sb.append(this.styleDeclaration.toString(eng));
        }
        sb.append("}\n");
        return sb.toString();
    }
}

