/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.elements.ResourceLocation
 *  com.atlassian.plugin.webresource.QueryParams
 *  com.atlassian.plugin.webresource.transformer.TransformableResource
 *  com.atlassian.plugin.webresource.transformer.TransformerParameters
 *  com.atlassian.plugin.webresource.transformer.TwoPhaseResourceTransformer
 *  com.atlassian.plugin.webresource.transformer.UrlReadingWebResourceTransformer
 *  com.atlassian.plugin.webresource.transformer.WebResourceTransformerFactory
 *  com.atlassian.plugin.webresource.url.UrlBuilder
 *  com.atlassian.webresource.api.prebake.DimensionAwareWebResourceTransformerFactory
 *  com.atlassian.webresource.api.prebake.Dimensions
 *  com.atlassian.webresource.spi.TransformationDto
 *  com.atlassian.webresource.spi.TransformerDto
 *  com.google.common.base.Preconditions
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.webresource;

import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.elements.ResourceLocation;
import com.atlassian.plugin.webresource.QueryParams;
import com.atlassian.plugin.webresource.cdn.CdnResourceUrlTransformer;
import com.atlassian.plugin.webresource.impl.UrlBuildingStrategy;
import com.atlassian.plugin.webresource.impl.helpers.Helpers;
import com.atlassian.plugin.webresource.impl.snapshot.resource.Resource;
import com.atlassian.plugin.webresource.impl.support.Content;
import com.atlassian.plugin.webresource.transformer.ContentTransformerModuleDescriptor;
import com.atlassian.plugin.webresource.transformer.TransformableResource;
import com.atlassian.plugin.webresource.transformer.TransformerCache;
import com.atlassian.plugin.webresource.transformer.TransformerParameters;
import com.atlassian.plugin.webresource.transformer.TwoPhaseResourceTransformer;
import com.atlassian.plugin.webresource.transformer.UrlReadingContentTransformer;
import com.atlassian.plugin.webresource.transformer.UrlReadingWebResourceTransformer;
import com.atlassian.plugin.webresource.transformer.UrlReadingWebResourceTransformerModuleDescriptor;
import com.atlassian.plugin.webresource.transformer.WebResourceTransformerFactory;
import com.atlassian.plugin.webresource.transformer.WebResourceTransformerModuleDescriptor;
import com.atlassian.plugin.webresource.url.UrlBuilder;
import com.atlassian.webresource.api.prebake.DimensionAwareWebResourceTransformerFactory;
import com.atlassian.webresource.api.prebake.Dimensions;
import com.atlassian.webresource.spi.TransformationDto;
import com.atlassian.webresource.spi.TransformerDto;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebResourceTransformation {
    private final String extension;
    private final String type;
    private final Map<String, Element> transformerElements;
    private final Iterable<String> transformerKeys;
    private final Logger log = LoggerFactory.getLogger(WebResourceTransformation.class);

    public WebResourceTransformation(TransformationDto transformationDto) {
        Objects.requireNonNull(transformationDto.extension);
        this.type = transformationDto.extension;
        this.extension = "." + this.type;
        ArrayList<String> keys = new ArrayList<String>();
        for (TransformerDto transformerDto : transformationDto.transformers) {
            keys.add(transformerDto.key);
        }
        this.transformerKeys = keys;
        this.transformerElements = Collections.emptyMap();
    }

    public WebResourceTransformation(Element element) {
        Preconditions.checkArgument((element.attribute("extension") != null ? 1 : 0) != 0, (Object)"extension");
        this.type = element.attributeValue("extension");
        this.extension = "." + this.type;
        LinkedHashMap<String, Element> transformers = new LinkedHashMap<String, Element>();
        for (Element transformElement : element.elements("transformer")) {
            transformers.put(transformElement.attributeValue("key"), transformElement);
        }
        this.transformerElements = Collections.unmodifiableMap(transformers);
        this.transformerKeys = this.transformerElements.keySet();
    }

    public boolean matches(ResourceLocation location) {
        String loc = location.getLocation();
        if (loc == null || "".equals(loc.trim())) {
            loc = location.getName();
        }
        return loc.endsWith(this.extension);
    }

    public boolean matches(String locationType) {
        return locationType.equals(this.type);
    }

    public void addTransformParameters(TransformerCache transformerCache, TransformerParameters transformerParameters, UrlBuilder urlBuilder, UrlBuildingStrategy urlBuildingStrategy) {
        for (String key : this.transformerKeys) {
            AbstractModuleDescriptor descriptor;
            Object descriptorAsObject = transformerCache.getDescriptor(key);
            if (descriptorAsObject == null) continue;
            if (descriptorAsObject instanceof UrlReadingWebResourceTransformerModuleDescriptor) {
                descriptor = (UrlReadingWebResourceTransformerModuleDescriptor)((Object)descriptorAsObject);
                urlBuildingStrategy.addToUrl(descriptor.getModule().makeUrlBuilder(transformerParameters), urlBuilder);
                continue;
            }
            if (descriptorAsObject instanceof ContentTransformerModuleDescriptor) {
                descriptor = (ContentTransformerModuleDescriptor)((Object)descriptorAsObject);
                urlBuildingStrategy.addToUrl(descriptor.getModule().makeUrlBuilder(transformerParameters), urlBuilder);
                continue;
            }
            if (descriptorAsObject instanceof WebResourceTransformerModuleDescriptor) continue;
            throw new RuntimeException("invalid usage, transformer descriptor expected but got " + descriptorAsObject);
        }
    }

    public boolean containsOnlyPureUrlReadingTransformers(TransformerCache transformerCache) {
        for (String key : this.transformerKeys) {
            Object descriptorAsObject = transformerCache.getDescriptor(key);
            if (descriptorAsObject == null || !(descriptorAsObject instanceof WebResourceTransformerModuleDescriptor)) continue;
            return false;
        }
        return true;
    }

    public Iterable<WebResourceTransformerModuleDescriptor> getDeprecatedTransformers(TransformerCache transformerCache) {
        LinkedList<WebResourceTransformerModuleDescriptor> deprecatedTransformers = new LinkedList<WebResourceTransformerModuleDescriptor>();
        for (String key : this.transformerKeys) {
            Object descriptorAsObject = transformerCache.getDescriptor(key);
            if (descriptorAsObject == null || !(descriptorAsObject instanceof WebResourceTransformerModuleDescriptor)) continue;
            deprecatedTransformers.add((WebResourceTransformerModuleDescriptor)((Object)descriptorAsObject));
        }
        return deprecatedTransformers;
    }

    public Content transform(CdnResourceUrlTransformer cdnResourceUrlTransformer, TransformerCache transformerCache, Resource resource, Content content, ResourceLocation resourceLocation, QueryParams params, String sourceUrl) {
        Content lastContent = content;
        for (String transformerKey : this.transformerKeys) {
            Object descriptorAsObject = transformerCache.getDescriptor(transformerKey);
            if (descriptorAsObject != null) {
                AbstractModuleDescriptor descriptor;
                if (descriptorAsObject instanceof ContentTransformerModuleDescriptor) {
                    ContentTransformerModuleDescriptor descriptor2 = (ContentTransformerModuleDescriptor)((Object)descriptorAsObject);
                    UrlReadingContentTransformer transformer = descriptor2.getModule().makeResourceTransformer(resource.getParent().getTransformerParameters());
                    if (transformer instanceof TwoPhaseResourceTransformer) {
                        ((TwoPhaseResourceTransformer)transformer).loadTwoPhaseProperties(resourceLocation, resource::getStreamFor);
                    }
                    lastContent = transformer.transform(cdnResourceUrlTransformer, lastContent, resourceLocation, params, sourceUrl);
                    continue;
                }
                Element configElement = this.transformerElements.get(transformerKey);
                if (descriptorAsObject instanceof UrlReadingWebResourceTransformerModuleDescriptor) {
                    descriptor = (UrlReadingWebResourceTransformerModuleDescriptor)((Object)descriptorAsObject);
                    TransformableResource transformableResource = new TransformableResource(resourceLocation, Helpers.asDownloadableResource(lastContent));
                    UrlReadingWebResourceTransformer transformer = descriptor.getModule().makeResourceTransformer(configElement, resource.getParent().getTransformerParameters());
                    if (transformer instanceof TwoPhaseResourceTransformer) {
                        ((TwoPhaseResourceTransformer)transformer).loadTwoPhaseProperties(resourceLocation, resource::getStreamFor);
                    }
                    lastContent = Helpers.asContent(transformer.transform(transformableResource, params), null, true);
                    continue;
                }
                if (descriptorAsObject instanceof WebResourceTransformerModuleDescriptor) {
                    descriptor = (WebResourceTransformerModuleDescriptor)((Object)descriptorAsObject);
                    lastContent = Helpers.asContent(descriptor.getModule().transform(configElement, resourceLocation, Helpers.asDownloadableResource(lastContent)), null, true);
                    continue;
                }
                throw new RuntimeException("invalid usage, transformer descriptor expected but got " + descriptorAsObject);
            }
            this.log.warn("Web resource transformer {} not found for resource {}, skipping", (Object)transformerKey, (Object)resourceLocation.getName());
        }
        return lastContent;
    }

    public String toString() {
        return this.getClass().getSimpleName() + " with " + this.transformerKeys.toString() + " transformers";
    }

    public Dimensions computeDimensions(TransformerCache transformerCache) {
        Dimensions d = Dimensions.empty();
        for (String key : this.transformerKeys) {
            UrlReadingWebResourceTransformerModuleDescriptor descriptor;
            WebResourceTransformerFactory module;
            Object descriptorAsObject = transformerCache.getDescriptor(key);
            if (!(descriptorAsObject instanceof UrlReadingWebResourceTransformerModuleDescriptor) || !((module = (descriptor = (UrlReadingWebResourceTransformerModuleDescriptor)((Object)descriptorAsObject)).getModule()) instanceof DimensionAwareWebResourceTransformerFactory)) continue;
            d = d.product(((DimensionAwareWebResourceTransformerFactory)module).computeDimensions());
        }
        return d;
    }
}

