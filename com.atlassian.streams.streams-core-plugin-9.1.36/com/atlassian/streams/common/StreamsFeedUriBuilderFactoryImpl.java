/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.builder.StreamsFeedUriBuilder
 *  com.atlassian.streams.api.builder.StreamsFeedUriBuilderFactory
 *  com.atlassian.streams.spi.UriAuthenticationParameterProvider
 *  com.google.common.base.Preconditions
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.streams.common;

import com.atlassian.streams.api.builder.StreamsFeedUriBuilder;
import com.atlassian.streams.api.builder.StreamsFeedUriBuilderFactory;
import com.atlassian.streams.common.StreamsFeedUriBuilderImpl;
import com.atlassian.streams.spi.UriAuthenticationParameterProvider;
import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Qualifier;

public class StreamsFeedUriBuilderFactoryImpl
implements StreamsFeedUriBuilderFactory {
    private final UriAuthenticationParameterProvider authProvider;

    public StreamsFeedUriBuilderFactoryImpl(@Qualifier(value="uriAuthParamProvider") UriAuthenticationParameterProvider authProvider) {
        this.authProvider = (UriAuthenticationParameterProvider)Preconditions.checkNotNull((Object)authProvider, (Object)"authProvider");
    }

    public StreamsFeedUriBuilder getStreamsFeedUriBuilder(String baseUrl) {
        return new StreamsFeedUriBuilderImpl(baseUrl, this.authProvider);
    }
}

