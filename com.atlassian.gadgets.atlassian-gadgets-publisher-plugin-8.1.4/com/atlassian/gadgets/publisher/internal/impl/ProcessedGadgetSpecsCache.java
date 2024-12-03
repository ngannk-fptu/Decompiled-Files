/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.plugins.PluginGadgetSpec
 *  com.google.common.base.Function
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.collect.ComputationException
 *  org.apache.commons.io.IOUtils
 */
package com.atlassian.gadgets.publisher.internal.impl;

import com.atlassian.gadgets.plugins.PluginGadgetSpec;
import com.atlassian.gadgets.publisher.internal.GadgetProcessor;
import com.atlassian.gadgets.publisher.internal.PublishedGadgetSpecNotFoundException;
import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ComputationException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.IOUtils;

class ProcessedGadgetSpecsCache {
    private final LoadingCache<String, byte[]> processedGadgetSpecs;

    public ProcessedGadgetSpecsCache(GadgetProcessor gadgetProcessor, PluginGadgetSpec pluginGadgetSpec) {
        this.processedGadgetSpecs = CacheBuilder.newBuilder().expireAfterAccess(24L, TimeUnit.HOURS).build(CacheLoader.from((Function)new ProcessedGadgetSpecFunction(gadgetProcessor, pluginGadgetSpec)));
    }

    public byte[] get(String baseUrl) {
        return (byte[])this.processedGadgetSpecs.getUnchecked((Object)baseUrl);
    }

    private static final class ProcessedGadgetSpecFunction
    implements Function<String, byte[]> {
        private final GadgetProcessor gadgetProcessor;
        private final PluginGadgetSpec pluginGadgetSpec;

        public ProcessedGadgetSpecFunction(GadgetProcessor gadgetProcessor, PluginGadgetSpec pluginGadgetSpec) {
            this.gadgetProcessor = Objects.requireNonNull(gadgetProcessor, "gadgetProcessor");
            this.pluginGadgetSpec = Objects.requireNonNull(pluginGadgetSpec, "pluginGadgetSpec");
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public byte[] apply(String baseUrl) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                InputStream gadgetSpecStream = this.pluginGadgetSpec.getInputStream();
                if (gadgetSpecStream == null) {
                    throw new PublishedGadgetSpecNotFoundException(String.format("Could not write gadget spec: %s because the resource was not found", this.pluginGadgetSpec));
                }
                BufferedInputStream in = new BufferedInputStream(gadgetSpecStream);
                BufferedOutputStream out = new BufferedOutputStream(baos);
                try {
                    this.gadgetProcessor.process(in, out);
                    out.flush();
                }
                finally {
                    IOUtils.closeQuietly((InputStream)gadgetSpecStream);
                }
            }
            catch (IOException ioe) {
                throw new ComputationException((Throwable)ioe);
            }
            return baos.toByteArray();
        }
    }
}

