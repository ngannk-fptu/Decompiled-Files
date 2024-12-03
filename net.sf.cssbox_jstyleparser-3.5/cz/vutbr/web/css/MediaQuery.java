/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.css;

import cz.vutbr.web.css.MediaExpression;
import cz.vutbr.web.css.Rule;

public interface MediaQuery
extends Rule<MediaExpression> {
    public void setNegative(boolean var1);

    public boolean isNegative();

    public void setType(String var1);

    public String getType();
}

