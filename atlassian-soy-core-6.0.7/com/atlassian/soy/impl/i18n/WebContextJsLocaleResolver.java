/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.soy.impl.i18n;

import com.atlassian.soy.spi.i18n.JsLocaleResolver;
import com.atlassian.soy.spi.web.WebContextProvider;
import java.util.Locale;

public class WebContextJsLocaleResolver
implements JsLocaleResolver {
    private final WebContextProvider webContextProvider;

    public WebContextJsLocaleResolver(WebContextProvider webContextProvider) {
        this.webContextProvider = webContextProvider;
    }

    @Override
    public Locale getLocale() {
        return this.webContextProvider.getLocale();
    }
}

