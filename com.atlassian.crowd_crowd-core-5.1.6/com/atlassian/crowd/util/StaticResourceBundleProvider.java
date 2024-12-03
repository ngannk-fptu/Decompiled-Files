/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.util.I18nHelperConfiguration
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 */
package com.atlassian.crowd.util;

import com.atlassian.crowd.util.I18nHelperConfiguration;
import com.atlassian.crowd.util.ResourceBundleProvider;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.Locale;
import java.util.ResourceBundle;

public class StaticResourceBundleProvider
implements ResourceBundleProvider {
    private final Iterable<String> locations;
    private final Locale locale;
    private final Function<String, ResourceBundle> toBundle;

    public StaticResourceBundleProvider(I18nHelperConfiguration configuration) {
        this.locations = ImmutableList.copyOf((Collection)configuration.getBundleLocations());
        this.locale = configuration.getLocale();
        this.toBundle = new Function<String, ResourceBundle>(){

            public ResourceBundle apply(String bundleLocation) {
                return ResourceBundle.getBundle(bundleLocation, StaticResourceBundleProvider.this.locale);
            }
        };
    }

    @Override
    public Iterable<ResourceBundle> getResourceBundles() {
        return Iterables.transform(this.locations, this.toBundle);
    }
}

