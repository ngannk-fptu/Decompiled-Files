/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.soy.impl.i18n;

import com.atlassian.soy.spi.i18n.I18nResolver;
import com.atlassian.soy.spi.web.WebContextProvider;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ResourceBundleI18nResolver
implements I18nResolver {
    private final Map<Locale, ResourceBundle> bundles;
    private final WebContextProvider webContextProvider;

    public ResourceBundleI18nResolver(WebContextProvider webContextProvider) {
        this(webContextProvider, Collections.emptyMap());
    }

    public ResourceBundleI18nResolver(WebContextProvider webContextProvider, Map<Locale, ResourceBundle> bundles) {
        this.bundles = bundles;
        this.webContextProvider = webContextProvider;
    }

    @Override
    public String getText(String key) {
        return this.getText(this.webContextProvider.getLocale(), key, new Serializable[0]);
    }

    @Override
    public String getText(String key, Serializable ... arguments) {
        return this.getText(this.webContextProvider.getLocale(), key, arguments);
    }

    @Override
    public String getText(Locale locale, String key) {
        return this.getText(locale, key, new Serializable[0]);
    }

    @Override
    public String getRawText(Locale locale, String key) {
        try {
            ResourceBundle resourceBundle = this.bundles.get(locale);
            if (resourceBundle != null) {
                return resourceBundle.getString(key);
            }
        }
        catch (MissingResourceException missingResourceException) {
            // empty catch block
        }
        return key;
    }

    private String getText(Locale locale, String key, Serializable[] arguments) {
        String rawText = this.getRawText(locale, key);
        return key.equals(rawText) ? rawText : MessageFormat.format(rawText, arguments);
    }
}

