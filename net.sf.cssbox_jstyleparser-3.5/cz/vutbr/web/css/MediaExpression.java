/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.css;

import cz.vutbr.web.css.Rule;
import cz.vutbr.web.css.Term;

public interface MediaExpression
extends Rule<Term<?>> {
    public String getFeature();

    public void setFeature(String var1);
}

