/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.soy.renderer;

import com.atlassian.annotations.PublicApi;

@PublicApi
public class JsExpression {
    private final String text;

    public JsExpression(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }
}

