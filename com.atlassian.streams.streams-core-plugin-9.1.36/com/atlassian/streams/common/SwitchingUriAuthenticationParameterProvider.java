/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.common.Option
 *  com.atlassian.streams.api.common.Pair
 *  com.atlassian.streams.spi.OptionalService
 *  com.atlassian.streams.spi.UriAuthenticationParameterProvider
 *  com.google.common.base.Preconditions
 *  org.osgi.framework.BundleContext
 */
package com.atlassian.streams.common;

import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.api.common.Pair;
import com.atlassian.streams.spi.OptionalService;
import com.atlassian.streams.spi.UriAuthenticationParameterProvider;
import com.google.common.base.Preconditions;
import org.osgi.framework.BundleContext;

public class SwitchingUriAuthenticationParameterProvider
extends OptionalService<UriAuthenticationParameterProvider>
implements UriAuthenticationParameterProvider {
    private final UriAuthenticationParameterProvider defaultProvider;

    public SwitchingUriAuthenticationParameterProvider(UriAuthenticationParameterProvider defaultProvider, BundleContext bundleContext) {
        super(UriAuthenticationParameterProvider.class, bundleContext);
        this.defaultProvider = (UriAuthenticationParameterProvider)Preconditions.checkNotNull((Object)defaultProvider, (Object)"defaultProvider");
    }

    public Option<Pair<String, String>> get() {
        return ((UriAuthenticationParameterProvider)this.getService().getOrElse((Object)this.defaultProvider)).get();
    }
}

