/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.WebResourceIntegration
 *  com.atlassian.plugin.webresource.url.UrlBuilder
 *  org.osgi.framework.BundleContext
 *  org.osgi.util.tracker.ServiceTracker
 */
package com.atlassian.upm.transformers.webresource;

import com.atlassian.plugin.webresource.WebResourceIntegration;
import com.atlassian.plugin.webresource.url.UrlBuilder;
import com.atlassian.upm.transformers.webresource.LocaleUtils;
import java.util.Objects;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class UrlReadingWebResourceUrlBuilder {
    private static final String QUERY_KEY = "locale";
    private static final String HASH_KEY = "locale-hash";
    private ServiceTracker serviceTracker;
    private final BundleContext bundleContext;

    public UrlReadingWebResourceUrlBuilder(BundleContext bundleContext) {
        this.bundleContext = Objects.requireNonNull(bundleContext, "bundleContext");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void build(UrlBuilder urlBuilder) {
        if (this.serviceTracker == null) {
            UrlReadingWebResourceUrlBuilder urlReadingWebResourceUrlBuilder = this;
            synchronized (urlReadingWebResourceUrlBuilder) {
                if (this.serviceTracker == null) {
                    this.serviceTracker = new ServiceTracker(this.bundleContext, WebResourceIntegration.class.getName(), null);
                    this.serviceTracker.open();
                }
            }
        }
        WebResourceIntegration webResourceIntegration = (WebResourceIntegration)Objects.requireNonNull(this.serviceTracker.getService(), "couldn't locate WebResourceIntegration service");
        urlBuilder.addToQueryString(QUERY_KEY, LocaleUtils.serialize(webResourceIntegration.getLocale()));
        String locale = webResourceIntegration.getStaticResourceLocale();
        if (locale != null) {
            urlBuilder.addToHash(HASH_KEY, (Object)locale);
        }
    }
}

