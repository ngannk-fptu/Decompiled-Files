/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.render.xhtml.transformers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.TransformerWeight;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.content.render.xhtml.transformers.ReferencesFromBodyContentExtractorMarkerV2;
import com.atlassian.confluence.plugin.descriptor.TransformerModuleDescriptor;
import com.atlassian.confluence.security.InvalidOperationException;
import com.atlassian.plugin.PluginAccessor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginFragmentTransformer<T>
implements FragmentTransformer {
    private static final Logger log = LoggerFactory.getLogger(PluginFragmentTransformer.class);
    private final Class<T> markerClass;
    private final PluginAccessor pluginAccessor;
    private List<FragmentTransformer> fragmentTransformerList;
    private final String transformerChainName;
    private final List<FragmentTransformer> defaultFragmentTransformerList;
    private final boolean isLazyInitialised;

    public PluginFragmentTransformer(PluginAccessor pluginAccessor, String transformerChainName, Class<T> markerClass) {
        this(pluginAccessor, transformerChainName, markerClass, Collections.emptyList(), true);
    }

    public PluginFragmentTransformer(PluginAccessor pluginAccessor, String transformerChainName, Class<T> markerClass, List<FragmentTransformer> defaultFragmentTransformerList) {
        this(pluginAccessor, transformerChainName, markerClass, defaultFragmentTransformerList, false);
    }

    public PluginFragmentTransformer(PluginAccessor pluginAccessor, String transformerChainName, Class<T> markerClass, List<FragmentTransformer> defaultFragmentTransformerList, boolean isLazyInitialPluginTransformer) {
        this.pluginAccessor = pluginAccessor;
        this.transformerChainName = transformerChainName;
        this.markerClass = markerClass;
        this.defaultFragmentTransformerList = new ArrayList<FragmentTransformer>();
        this.isLazyInitialised = isLazyInitialPluginTransformer;
        if (defaultFragmentTransformerList != null && !defaultFragmentTransformerList.isEmpty()) {
            this.defaultFragmentTransformerList.addAll(defaultFragmentTransformerList.stream().filter(Objects::nonNull).collect(Collectors.toList()));
        }
        if (!isLazyInitialPluginTransformer) {
            this.fragmentTransformerList = this.initialiseFragmentTransformers();
        }
    }

    @Override
    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        for (FragmentTransformer transformer : this.getFragmentTransformers()) {
            if (!transformer.handles(startElementEvent, conversionContext)) continue;
            return true;
        }
        return false;
    }

    private List<FragmentTransformer> initialiseFragmentTransformers() {
        ArrayList<FragmentTransformer> fragmentTransformerList = new ArrayList<FragmentTransformer>(this.defaultFragmentTransformerList);
        ArrayList<TransformerWeight> transformerWeights = new ArrayList<TransformerWeight>();
        transformerWeights.addAll(this.pluginAccessor.getEnabledModuleDescriptorsByClass(TransformerModuleDescriptor.class).stream().filter(descriptor -> this.transformerChainName.equals(descriptor.getTransformerChain())).map(descriptor -> new TransformerWeight(descriptor.getModule(), descriptor.getTransformerWeight())).collect(Collectors.toList()));
        if (transformerWeights.isEmpty()) {
            log.warn("Could not collect any FragmentTransformer from plugin for chainname {}", (Object)this.transformerChainName);
        }
        transformerWeights.sort(Comparator.comparingInt(TransformerWeight::getWeight));
        fragmentTransformerList.addAll(transformerWeights.stream().map(TransformerWeight::getTransformer).filter(FragmentTransformer.class::isInstance).map(FragmentTransformer.class::cast).filter(transformer -> this.markerClass.isInstance(transformer)).map(this::handleReferencesFromBodyContentExtractorMarkerV2).collect(Collectors.toList()));
        log.debug("Returning fragment transformers: {}", fragmentTransformerList);
        return fragmentTransformerList;
    }

    public List<FragmentTransformer> getFragmentTransformers() {
        if (this.isLazyInitialised) {
            return this.initialiseFragmentTransformers();
        }
        return this.fragmentTransformerList;
    }

    private FragmentTransformer handleReferencesFromBodyContentExtractorMarkerV2(FragmentTransformer pluginTransformer) {
        if (!this.markerClass.isAssignableFrom(ReferencesFromBodyContentExtractorMarkerV2.class)) {
            return pluginTransformer;
        }
        ReferencesFromBodyContentExtractorMarkerV2 extractorMarker = (ReferencesFromBodyContentExtractorMarkerV2)((Object)pluginTransformer);
        FragmentTransformer newPluginTransformer = extractorMarker.createNewInstance();
        if (pluginTransformer.equals(newPluginTransformer)) {
            throw new InvalidOperationException("Invalid implementation of ReferencesFromBodyContentExtractorMarker detected for class" + pluginTransformer.getClass().getCanonicalName());
        }
        return newPluginTransformer;
    }

    @Override
    public Streamable transform(XMLEventReader reader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        List<FragmentTransformer> fragmentTransformers = this.getFragmentTransformers();
        for (FragmentTransformer transformer : fragmentTransformers) {
            try {
                if (!transformer.handles(reader.peek().asStartElement(), conversionContext)) continue;
                log.debug("Performing transform on: {}", (Object)transformer);
                return transformer.transform(reader, mainFragmentTransformer, conversionContext);
            }
            catch (XMLStreamException e) {
                log.error("Could not check XML event reader, caused by: " + e);
                return Streamables.empty();
            }
        }
        log.debug("Could not transform as no plugin transformer found out of {} transformers that can handle reader start element", (Object)fragmentTransformers.size());
        return Streamables.empty();
    }
}

