/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.soy.renderer.JsExpression
 *  com.atlassian.soy.renderer.SoyClientFunction
 *  com.atlassian.soy.renderer.SoyServerFunction
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.plugins.soy;

import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.soy.renderer.JsExpression;
import com.atlassian.soy.renderer.SoyClientFunction;
import com.atlassian.soy.renderer.SoyServerFunction;
import com.google.common.collect.ImmutableSet;
import java.util.Set;

public final class StaticResourceUrlPrefixFunction
implements SoyServerFunction<String>,
SoyClientFunction {
    private final WebResourceUrlProvider webResourceUrlProvider;

    public StaticResourceUrlPrefixFunction(WebResourceUrlProvider webResourceUrlProvider) {
        this.webResourceUrlProvider = webResourceUrlProvider;
    }

    public String getName() {
        return "staticResourceUrlPrefix";
    }

    public Set<Integer> validArgSizes() {
        return ImmutableSet.of((Object)0);
    }

    public String apply(Object ... args) {
        return this.get();
    }

    public JsExpression generate(JsExpression ... args) {
        return new JsExpression("\"" + this.get() + "\"");
    }

    private String get() {
        return this.webResourceUrlProvider.getStaticResourcePrefix(UrlMode.AUTO);
    }
}

