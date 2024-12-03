/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.LocalGadgetSpecProvider
 *  com.google.common.base.Preconditions
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.inject.Inject
 *  com.google.inject.Singleton
 *  com.google.inject.name.Named
 *  org.apache.shindig.common.uri.Uri
 *  org.apache.shindig.gadgets.GadgetContext
 *  org.apache.shindig.gadgets.GadgetException
 *  org.apache.shindig.gadgets.GadgetException$Code
 *  org.apache.shindig.gadgets.GadgetSpecFactory
 *  org.apache.shindig.gadgets.spec.GadgetSpec
 *  org.apache.shindig.gadgets.spec.SpecParserException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.gadgets.renderer.internal.local;

import com.atlassian.gadgets.LocalGadgetSpecProvider;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import org.apache.shindig.common.uri.Uri;
import org.apache.shindig.gadgets.GadgetContext;
import org.apache.shindig.gadgets.GadgetException;
import org.apache.shindig.gadgets.GadgetSpecFactory;
import org.apache.shindig.gadgets.spec.GadgetSpec;
import org.apache.shindig.gadgets.spec.SpecParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class LocalGadgetSpecFactory
implements GadgetSpecFactory {
    private final Parser parser;
    private final LoadingCache<URI, Optional<CacheableGadgetSpec>> cache;
    private final GadgetSpecFactory fallback;

    @Inject
    public LocalGadgetSpecFactory(Iterable<LocalGadgetSpecProvider> providers, @Named(value="fallback") GadgetSpecFactory fallback) {
        this.parser = new Parser((Iterable)Preconditions.checkNotNull(providers, (Object)"providers"));
        this.cache = CacheBuilder.newBuilder().softValues().build((CacheLoader)new CacheLoader<URI, Optional<CacheableGadgetSpec>>(){

            public Optional<CacheableGadgetSpec> load(URI gadgetUri) throws Exception {
                return Optional.ofNullable(LocalGadgetSpecFactory.this.parser.get(gadgetUri));
            }
        });
        this.fallback = (GadgetSpecFactory)Preconditions.checkNotNull((Object)fallback, (Object)"fallback");
    }

    public GadgetSpec getGadgetSpec(GadgetContext context) throws GadgetException {
        return this.getGadgetSpec(context.getUrl(), context.getIgnoreCache());
    }

    public GadgetSpec getGadgetSpec(URI gadgetUri, boolean ignoreCache) throws GadgetException {
        Optional spec;
        if (ignoreCache) {
            spec = Optional.ofNullable(this.parser.get(gadgetUri));
        } else {
            try {
                spec = (Optional)this.cache.get((Object)gadgetUri);
                if (spec.isPresent() && ((CacheableGadgetSpec)((Object)spec.get())).isExpired()) {
                    this.cache.invalidate((Object)gadgetUri);
                    spec = (Optional)this.cache.get((Object)gadgetUri);
                }
            }
            catch (ExecutionException e) {
                throw new GadgetException(GadgetException.Code.FAILED_TO_RETRIEVE_CONTENT, (Throwable)e);
            }
        }
        if (spec.isPresent()) {
            return (GadgetSpec)spec.get();
        }
        return this.fallback.getGadgetSpec(gadgetUri, ignoreCache);
    }

    public void clearCache() {
        this.cache.invalidateAll();
    }

    private static final class Parser {
        private static final Logger log = LoggerFactory.getLogger(Parser.class);
        private final Iterable<LocalGadgetSpecProvider> providers;

        public Parser(Iterable<LocalGadgetSpecProvider> providers) {
            this.providers = providers;
        }

        public CacheableGadgetSpec get(URI gadgetUri) throws GadgetException {
            for (LocalGadgetSpecProvider provider : this.providers) {
                block10: {
                    try {
                        if (!provider.contains(gadgetUri)) {
                        }
                        break block10;
                    }
                    catch (RuntimeException e) {
                        if (!log.isDebugEnabled()) continue;
                        log.debug("Could not determine whether " + provider + " contains " + gadgetUri, (Throwable)e);
                    }
                    continue;
                }
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                try {
                    provider.writeGadgetSpecTo(gadgetUri, (OutputStream)output);
                }
                catch (RuntimeException e) {
                    if (log.isDebugEnabled()) {
                        log.warn("Could not retrieve gadget spec " + gadgetUri + " from " + provider, (Throwable)e);
                        continue;
                    }
                    if (!log.isWarnEnabled()) continue;
                    log.warn("Could not retrieve gadget spec " + gadgetUri + " from " + provider + ": " + e.getMessage());
                    continue;
                }
                catch (IOException e) {
                    throw new GadgetException(GadgetException.Code.FAILED_TO_RETRIEVE_CONTENT, (Throwable)e);
                }
                try {
                    return new CacheableGadgetSpec(provider, gadgetUri, new String(output.toByteArray(), "UTF-8"));
                }
                catch (UnsupportedEncodingException e) {
                    throw new AssertionError((Object)"UTF-8 encoding is required by the Java specification");
                }
            }
            return null;
        }
    }

    private static final class CacheableGadgetSpec
    extends GadgetSpec {
        private static final Logger log = LoggerFactory.getLogger(CacheableGadgetSpec.class);
        private final URI uri;
        private final LocalGadgetSpecProvider provider;
        private final Date cachedAt = new Date();

        public CacheableGadgetSpec(LocalGadgetSpecProvider provider, URI uri, String xml) throws SpecParserException {
            super(Uri.fromJavaUri((URI)uri), xml);
            this.provider = (LocalGadgetSpecProvider)Preconditions.checkNotNull((Object)provider, (Object)"provider");
            this.uri = (URI)Preconditions.checkNotNull((Object)uri, (Object)"uri");
        }

        public boolean isExpired() {
            try {
                return this.provider.getLastModified(this.uri).after(this.cachedAt);
            }
            catch (RuntimeException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Could not determine whether " + this.provider + " contains " + this.uri, (Throwable)e);
                }
                return true;
            }
        }
    }
}

