/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Inject
 *  com.google.inject.Singleton
 *  org.apache.shindig.gadgets.http.ContentFetcherFactory
 *  org.apache.shindig.gadgets.rewrite.ContentRewriterRegistry
 *  org.apache.shindig.gadgets.servlet.MakeRequestHandler
 */
package com.atlassian.gadgets.renderer.internal.servlet;

import com.atlassian.gadgets.renderer.internal.http.TrustedAppContentFetcherFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.shindig.gadgets.http.ContentFetcherFactory;
import org.apache.shindig.gadgets.rewrite.ContentRewriterRegistry;
import org.apache.shindig.gadgets.servlet.MakeRequestHandler;

@Singleton
public class TrustedAppMakeRequestHandler
extends MakeRequestHandler {
    @Inject
    public TrustedAppMakeRequestHandler(TrustedAppContentFetcherFactory contentFetcherFactory, ContentRewriterRegistry contentRewriterRegistry) {
        super((ContentFetcherFactory)contentFetcherFactory, contentRewriterRegistry);
    }
}

