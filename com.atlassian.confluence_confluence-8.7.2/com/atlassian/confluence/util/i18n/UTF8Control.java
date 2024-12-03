/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.util.i18n;

import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

class UTF8Control
extends ResourceBundle.Control {
    UTF8Control() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException {
        String bundleName = this.toBundleName(baseName, locale);
        String resourceName = this.toResourceName(bundleName, "properties");
        PropertyResourceBundle bundle = null;
        InputStream stream = null;
        if (reload) {
            URLConnection connection;
            URL url = loader.getResource(resourceName);
            if (url != null && (connection = url.openConnection()) != null) {
                connection.setUseCaches(false);
                stream = connection.getInputStream();
            }
        } else {
            stream = loader.getResourceAsStream(resourceName);
        }
        if (stream != null) {
            try {
                bundle = new PropertyResourceBundle(new InputStreamReader(stream, "UTF-8"));
            }
            finally {
                stream.close();
            }
        }
        return bundle;
    }

    @Override
    public List<String> getFormats(String baseName) {
        return ResourceBundle.Control.FORMAT_PROPERTIES;
    }

    @Override
    public Locale getFallbackLocale(String baseName, Locale locale) {
        return null;
    }

    @Override
    public List<Locale> getCandidateLocales(String baseName, Locale locale) {
        return ImmutableList.of((Object)locale);
    }
}

