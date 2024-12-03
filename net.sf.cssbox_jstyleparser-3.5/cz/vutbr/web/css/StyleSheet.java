/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.css;

import cz.vutbr.web.css.Rule;
import cz.vutbr.web.css.RuleBlock;

public interface StyleSheet
extends Rule<RuleBlock<?>> {
    public void setOrigin(Origin var1);

    public Origin getOrigin();

    public static enum Origin {
        AUTHOR,
        AGENT,
        USER;

    }
}

