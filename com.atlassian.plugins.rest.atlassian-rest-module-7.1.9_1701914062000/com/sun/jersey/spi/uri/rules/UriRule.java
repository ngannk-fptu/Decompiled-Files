/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.spi.uri.rules;

import com.sun.jersey.spi.uri.rules.UriRuleContext;

public interface UriRule {
    public boolean accept(CharSequence var1, Object var2, UriRuleContext var3);
}

