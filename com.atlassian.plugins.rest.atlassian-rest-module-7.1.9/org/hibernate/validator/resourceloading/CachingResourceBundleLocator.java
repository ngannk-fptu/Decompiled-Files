/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.resourceloading;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.hibernate.validator.resourceloading.DelegatingResourceBundleLocator;
import org.hibernate.validator.spi.resourceloading.ResourceBundleLocator;

public class CachingResourceBundleLocator
extends DelegatingResourceBundleLocator {
    private final ConcurrentMap<Locale, ResourceBundle> bundleCache = new ConcurrentHashMap<Locale, ResourceBundle>();

    public CachingResourceBundleLocator(ResourceBundleLocator delegate) {
        super(delegate);
    }

    @Override
    public ResourceBundle getResourceBundle(Locale locale) {
        ResourceBundle bundle2;
        ResourceBundle cachedResourceBundle = (ResourceBundle)this.bundleCache.get(locale);
        if (cachedResourceBundle == null && (bundle2 = super.getResourceBundle(locale)) != null && (cachedResourceBundle = this.bundleCache.putIfAbsent(locale, bundle2)) == null) {
            return bundle2;
        }
        return cachedResourceBundle;
    }
}

