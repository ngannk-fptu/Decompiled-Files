/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.elements.ResourceLocation
 *  com.atlassian.plugin.webresource.QueryParams
 *  com.atlassian.plugin.webresource.transformer.TransformableResource
 *  com.atlassian.plugin.webresource.transformer.TransformerParameters
 *  com.atlassian.plugin.webresource.transformer.TransformerUrlBuilder
 *  com.atlassian.plugin.webresource.transformer.TwoPhaseResourceTransformer
 *  com.atlassian.plugin.webresource.transformer.UrlReadingWebResourceTransformer
 *  com.atlassian.plugin.webresource.url.UrlBuilder
 *  com.atlassian.webresource.api.prebake.DimensionAwareWebResourceTransformerFactory
 *  com.atlassian.webresource.api.prebake.Dimensions
 */
package com.atlassian.plugin.webresource.transformer;

import com.atlassian.plugin.elements.ResourceLocation;
import com.atlassian.plugin.webresource.QueryParams;
import com.atlassian.plugin.webresource.impl.RequestCache;
import com.atlassian.plugin.webresource.impl.UrlBuildingStrategy;
import com.atlassian.plugin.webresource.impl.helpers.ResourceServingHelpers;
import com.atlassian.plugin.webresource.impl.snapshot.Bundle;
import com.atlassian.plugin.webresource.impl.support.Content;
import com.atlassian.plugin.webresource.transformer.StaticTransformers;
import com.atlassian.plugin.webresource.transformer.StaticTransformersSupplier;
import com.atlassian.plugin.webresource.transformer.TransformableResource;
import com.atlassian.plugin.webresource.transformer.TransformerParameters;
import com.atlassian.plugin.webresource.transformer.TransformerUrlBuilder;
import com.atlassian.plugin.webresource.transformer.TwoPhaseResourceTransformer;
import com.atlassian.plugin.webresource.transformer.UrlReadingWebResourceTransformer;
import com.atlassian.plugin.webresource.url.UrlBuilder;
import com.atlassian.webresource.api.prebake.DimensionAwareWebResourceTransformerFactory;
import com.atlassian.webresource.api.prebake.Dimensions;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.StreamSupport;

public class DefaultStaticTransformers
implements StaticTransformers {
    private final StaticTransformersSupplier staticTransformersSupplier;
    private Set<String> paramKeys = new HashSet<String>();
    private Function<String, InputStream> loader;
    private ResourceLocation loaderResourceLocation;

    public DefaultStaticTransformers(StaticTransformersSupplier staticTransformersSupplier) {
        this.staticTransformersSupplier = staticTransformersSupplier;
        staticTransformersSupplier.computeDimensions().cartesianProduct().forEach(coordinate -> {
            for (String key : coordinate.getKeys()) {
                this.paramKeys.add(key);
            }
        });
    }

    @Override
    public Dimensions computeDimensions() {
        return this.staticTransformersSupplier.computeDimensions();
    }

    @Override
    public Dimensions computeBundleDimensions(Bundle bundle) {
        if (bundle == null) {
            return Dimensions.empty();
        }
        RequestCache requestCache = new RequestCache(null);
        return bundle.getResources(requestCache).values().stream().flatMap(r -> StreamSupport.stream(this.staticTransformersSupplier.get(r.getNameOrLocationType()).spliterator(), false)).map(DimensionAwareWebResourceTransformerFactory::computeDimensions).reduce(Dimensions::product).orElse(Dimensions.empty());
    }

    @Override
    public void addToUrl(String locationType, TransformerParameters transformerParameters, UrlBuilder urlBuilder, UrlBuildingStrategy urlBuildingStrategy) {
        for (DimensionAwareWebResourceTransformerFactory transformerFactory : this.transformersForType(locationType)) {
            urlBuildingStrategy.addToUrl((TransformerUrlBuilder)transformerFactory.makeUrlBuilder(transformerParameters), urlBuilder);
        }
    }

    public void loadTwoPhaseProperties(ResourceLocation resourceLocation, Function<String, InputStream> loadFromFile) {
        this.loader = loadFromFile;
        this.loaderResourceLocation = resourceLocation;
    }

    public boolean hasTwoPhaseProperties() {
        return false;
    }

    @Override
    public Content transform(Content content, TransformerParameters transformerParameters, ResourceLocation resourceLocation, QueryParams queryParams, String sourceUrl) {
        for (DimensionAwareWebResourceTransformerFactory transformerFactory : this.transformersForLocation(resourceLocation)) {
            TransformableResource tr = new TransformableResource(resourceLocation, ResourceServingHelpers.asDownloadableResource(content));
            UrlReadingWebResourceTransformer transformer = transformerFactory.makeResourceTransformer(transformerParameters);
            if (this.loader != null && resourceLocation.equals(this.loaderResourceLocation) && transformer instanceof TwoPhaseResourceTransformer) {
                ((TwoPhaseResourceTransformer)transformer).loadTwoPhaseProperties(resourceLocation, this.loader);
            }
            content = ResourceServingHelpers.asContent(transformer.transform(tr, queryParams), null, true);
        }
        return content;
    }

    @Override
    public Set<String> getParamKeys() {
        return this.paramKeys;
    }

    private Iterable<DimensionAwareWebResourceTransformerFactory> transformersForType(String locationType) {
        return this.staticTransformersSupplier.get(locationType);
    }

    private Iterable<DimensionAwareWebResourceTransformerFactory> transformersForLocation(ResourceLocation location) {
        return this.staticTransformersSupplier.get(location);
    }
}

