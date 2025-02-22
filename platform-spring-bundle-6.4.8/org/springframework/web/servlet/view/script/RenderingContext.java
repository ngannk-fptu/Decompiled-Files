/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.servlet.view.script;

import java.util.Locale;
import java.util.function.Function;
import org.springframework.context.ApplicationContext;

public class RenderingContext {
    private final ApplicationContext applicationContext;
    private final Locale locale;
    private final Function<String, String> templateLoader;
    private final String url;

    public RenderingContext(ApplicationContext applicationContext, Locale locale, Function<String, String> templateLoader, String url) {
        this.applicationContext = applicationContext;
        this.locale = locale;
        this.templateLoader = templateLoader;
        this.url = url;
    }

    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public Function<String, String> getTemplateLoader() {
        return this.templateLoader;
    }

    public String getUrl() {
        return this.url;
    }
}

