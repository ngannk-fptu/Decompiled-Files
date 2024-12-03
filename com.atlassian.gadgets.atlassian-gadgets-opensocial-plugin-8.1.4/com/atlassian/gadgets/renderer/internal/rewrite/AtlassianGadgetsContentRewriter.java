/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Inject
 *  com.google.inject.Singleton
 *  com.google.inject.name.Named
 *  org.apache.shindig.gadgets.GadgetSpecFactory
 *  org.apache.shindig.gadgets.rewrite.lexer.DefaultContentRewriter
 */
package com.atlassian.gadgets.renderer.internal.rewrite;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.apache.shindig.gadgets.GadgetSpecFactory;
import org.apache.shindig.gadgets.rewrite.lexer.DefaultContentRewriter;

@Singleton
public class AtlassianGadgetsContentRewriter
extends DefaultContentRewriter {
    @Inject
    public AtlassianGadgetsContentRewriter(GadgetSpecFactory specFactory, @Named(value="shindig.content-rewrite.include-urls") String includeUrls, @Named(value="shindig.content-rewrite.exclude-urls") String excludeUrls, @Named(value="shindig.content-rewrite.expires") String expires, @Named(value="shindig.content-rewrite.include-tags") String includeTags, @Named(value="shindig.content-rewrite.proxy-url") String proxyUrl, @Named(value="shindig.content-rewrite.concat-url") String concatUrl) {
        super(specFactory, includeUrls, excludeUrls, expires, includeTags, proxyUrl, concatUrl);
    }

    protected String getProxyUrl() {
        return null;
    }

    protected String getConcatUrl() {
        return null;
    }
}

