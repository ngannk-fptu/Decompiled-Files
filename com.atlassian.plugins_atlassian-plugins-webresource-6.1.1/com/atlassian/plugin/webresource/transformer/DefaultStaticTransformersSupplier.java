/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.elements.ResourceLocation
 *  com.atlassian.webresource.api.prebake.DimensionAwareWebResourceTransformerFactory
 *  com.atlassian.webresource.api.prebake.Dimensions
 *  com.google.common.base.Predicate
 */
package com.atlassian.plugin.webresource.transformer;

import com.atlassian.plugin.elements.ResourceLocation;
import com.atlassian.plugin.webresource.WebResourceIntegration;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.plugin.webresource.cdn.CdnResourceUrlTransformer;
import com.atlassian.plugin.webresource.transformer.StaticTransformersSupplier;
import com.atlassian.plugin.webresource.transformer.WebResourceTransformerMatcher;
import com.atlassian.plugin.webresource.transformer.instance.RelativeUrlTransformerFactory;
import com.atlassian.plugin.webresource.transformer.instance.RelativeUrlTransformerMatcher;
import com.atlassian.webresource.api.prebake.DimensionAwareWebResourceTransformerFactory;
import com.atlassian.webresource.api.prebake.Dimensions;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DefaultStaticTransformersSupplier
implements StaticTransformersSupplier {
    private final Collection<DescribedTransformer> describedTransformers;

    public DefaultStaticTransformersSupplier(WebResourceIntegration webResourceIntegration, WebResourceUrlProvider urlProvider, CdnResourceUrlTransformer cdnResourceUrlTransformer) {
        RelativeUrlTransformerFactory relativeUrlTransformerFactory = new RelativeUrlTransformerFactory(webResourceIntegration, urlProvider, cdnResourceUrlTransformer);
        RelativeUrlTransformerMatcher relativeUrlTransformerMatcher = new RelativeUrlTransformerMatcher();
        this.describedTransformers = Arrays.asList(new DescribedTransformer(relativeUrlTransformerMatcher, relativeUrlTransformerFactory));
    }

    private static Iterable<DimensionAwareWebResourceTransformerFactory> toTransformerFactories(Collection<DescribedTransformer> describedTransformers, com.google.common.base.Predicate<DescribedTransformer> predicate) {
        return describedTransformers.stream().filter((Predicate<DescribedTransformer>)predicate).map(input -> ((DescribedTransformer)input).transformerFactory).collect(Collectors.toList());
    }

    @Override
    public Dimensions computeDimensions() {
        Dimensions d = Dimensions.empty();
        for (DescribedTransformer dt : this.describedTransformers) {
            DimensionAwareWebResourceTransformerFactory t = dt.transformerFactory;
            d = d.product(t.computeDimensions());
        }
        return d;
    }

    @Override
    public Iterable<DimensionAwareWebResourceTransformerFactory> get(String locationType) {
        return DefaultStaticTransformersSupplier.toTransformerFactories(this.describedTransformers, (com.google.common.base.Predicate<DescribedTransformer>)((com.google.common.base.Predicate)input -> ((DescribedTransformer)input).matcher.matches(locationType)));
    }

    @Override
    public Iterable<DimensionAwareWebResourceTransformerFactory> get(ResourceLocation resourceLocation) {
        return DefaultStaticTransformersSupplier.toTransformerFactories(this.describedTransformers, (com.google.common.base.Predicate<DescribedTransformer>)((com.google.common.base.Predicate)input -> ((DescribedTransformer)input).matcher.matches(resourceLocation)));
    }

    private static final class DescribedTransformer {
        private final WebResourceTransformerMatcher matcher;
        private final DimensionAwareWebResourceTransformerFactory transformerFactory;

        private DescribedTransformer(WebResourceTransformerMatcher matcher, DimensionAwareWebResourceTransformerFactory transformerFactory) {
            this.matcher = matcher;
            this.transformerFactory = transformerFactory;
        }
    }
}

