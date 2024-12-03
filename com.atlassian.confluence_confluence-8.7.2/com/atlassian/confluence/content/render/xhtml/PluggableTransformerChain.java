/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Iterables
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.TransformerChain;
import com.atlassian.confluence.content.render.xhtml.TransformerWeight;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.transformers.Transformer;
import com.atlassian.confluence.plugin.descriptor.TransformerModuleDescriptor;
import com.atlassian.plugin.PluginAccessor;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluggableTransformerChain
implements Transformer {
    private static final Logger log = LoggerFactory.getLogger(PluggableTransformerChain.class);
    private final PluginAccessor pluginAccessor;
    private final List<TransformerWeight> defaultTransformers;
    private final String transformerChainName;

    public PluggableTransformerChain(PluginAccessor pluginAccessor, List<TransformerWeight> defaultTransformers, @NonNull String transformerChainName) {
        this.pluginAccessor = pluginAccessor;
        this.defaultTransformers = defaultTransformers;
        this.transformerChainName = (String)Preconditions.checkNotNull((Object)transformerChainName);
    }

    @Override
    public String transform(Reader input, ConversionContext conversionContext) throws XhtmlException {
        return new TransformerChain(this.getTransformers()).transform(input, conversionContext);
    }

    private Iterable<Transformer> getTransformers() {
        ArrayList<TransformerWeight> transformerWeights = new ArrayList<TransformerWeight>();
        transformerWeights.addAll(this.defaultTransformers);
        transformerWeights.addAll(this.pluginAccessor.getEnabledModuleDescriptorsByClass(TransformerModuleDescriptor.class).stream().filter(descriptor -> this.transformerChainName.equals(descriptor.getTransformerChain())).map(TransformerWeight::create).collect(Collectors.toList()));
        Collections.sort(transformerWeights, TransformerWeight.SORT_BY_WEIGHT);
        log.debug("Returning transformers sorted by weight: {} ", transformerWeights);
        return Iterables.transform(transformerWeights, TransformerWeight::getTransformer);
    }
}

