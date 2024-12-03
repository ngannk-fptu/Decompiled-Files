/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.extractor.ProductProvidedPropertyExtractor
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.analytics.client.extractor;

import com.atlassian.analytics.api.extractor.ProductProvidedPropertyExtractor;
import com.atlassian.analytics.client.extractor.PluginPropertyContributor;
import com.atlassian.analytics.client.extractor.PropertyExtractor;
import com.atlassian.analytics.client.extractor.PropertyExtractorHelper;
import com.atlassian.analytics.client.pipeline.serialize.RequestInfo;
import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class BitbucketPropertyExtractor
implements PropertyExtractor {
    private static final ImmutableSet<String> EXCLUDE_PROPERTIES = ImmutableSet.of();
    private final ProductProvidedPropertyExtractor productProvidedPropertyExtractor;
    private final PropertyExtractorHelper helper;

    public BitbucketPropertyExtractor(ProductProvidedPropertyExtractor productProvidedPropertyExtractor) {
        this.productProvidedPropertyExtractor = productProvidedPropertyExtractor;
        this.helper = new PropertyExtractorHelper((Set<String>)EXCLUDE_PROPERTIES, new PluginPropertyContributor());
    }

    @Override
    public Map<String, Object> extractProperty(String name, Object value) {
        Map<String, Object> extractedProperties = this.productProvidedPropertyExtractor.extractProperty(name, value);
        return extractedProperties == null ? this.helper.extractProperty(name, value) : extractedProperties;
    }

    @Override
    public boolean isExcluded(String name) {
        return this.helper.isExcluded(name);
    }

    @Override
    public String extractName(Object event) {
        String name = this.productProvidedPropertyExtractor.extractName(event);
        return name == null ? this.helper.extractName(event) : name;
    }

    @Override
    public String extractUser(Object event, Map<String, Object> properties) {
        return this.productProvidedPropertyExtractor.extractUser(event, properties);
    }

    @Override
    public Map<String, Object> enrichProperties(Object event) {
        Map properties = this.productProvidedPropertyExtractor.enrichProperties(event);
        return properties == null ? Collections.emptyMap() : properties;
    }

    @Override
    public String extractSubProduct(Object event, String product) {
        return this.helper.extractSubProduct(event, product);
    }

    @Override
    public String getApplicationAccess() {
        String applicationAccess = this.productProvidedPropertyExtractor.getApplicationAccess();
        return applicationAccess == null ? "" : applicationAccess;
    }

    @Override
    public String extractRequestCorrelationId(RequestInfo request) {
        return this.helper.extractRequestCorrelationId(request);
    }
}

