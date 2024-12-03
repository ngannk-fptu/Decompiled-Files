/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.content.render.prefetch;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.core.BodyContent;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.impl.content.render.prefetch.ResourceIdentifierExtractor;
import com.atlassian.confluence.impl.content.render.prefetch.ResourceIdentifiers;
import com.atlassian.confluence.impl.content.render.prefetch.ResourcePrefetcher;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import java.util.Objects;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentResourcePrefetcher {
    private static final Logger log = LoggerFactory.getLogger(ContentResourcePrefetcher.class);
    private final ResourceIdentifierExtractor resourceIdentifierExtractor;
    private final DarkFeaturesManager darkFeaturesManager;
    private final Iterable<ResourcePrefetcher> prefetchers;

    public ContentResourcePrefetcher(ResourceIdentifierExtractor resourceIdentifierExtractor, DarkFeaturesManager darkFeaturesManager, Iterable<ResourcePrefetcher> prefetchers) {
        this.darkFeaturesManager = Objects.requireNonNull(darkFeaturesManager);
        this.resourceIdentifierExtractor = Objects.requireNonNull(resourceIdentifierExtractor);
        this.prefetchers = prefetchers;
    }

    public void prefetchContentResources(BodyContent bodyContent, ConversionContext conversionContext) {
        ContentEntityObject contentEntity = bodyContent.getContent();
        try {
            log.debug("Extracting ResourceIdentifiers from body content of {}", (Object)contentEntity);
            if (this.resourceIdentifierExtractor.handles(bodyContent.getBodyType())) {
                ResourceIdentifiers extractedResourceIds = this.resourceIdentifierExtractor.extractResourceIdentifiers(bodyContent, conversionContext);
                this.prefetchers.forEach(prefetcher -> ContentResourcePrefetcher.prefetch(conversionContext, extractedResourceIds, prefetcher));
            } else {
                log.debug("RI extractor cannot handle body type {}", (Object)bodyContent.getBodyType());
            }
        }
        catch (Exception ex) {
            log.warn("Failed to pre-fetch resources for {}: {}", (Object)contentEntity, (Object)ex.getMessage());
            log.debug("Failed to pre-fetch resources for {}", (Object)contentEntity, (Object)ex);
        }
    }

    private static <T extends ResourceIdentifier> void prefetch(ConversionContext conversionContext, ResourceIdentifiers extractedResourceIds, ResourcePrefetcher<T> prefetcher) {
        Set<T> identifiers = extractedResourceIds.getResourceIdentifiers(prefetcher.getResourceItentifierType());
        if (!identifiers.isEmpty()) {
            prefetcher.prefetch(identifiers, conversionContext);
        }
    }

    public boolean isEnabled() {
        return this.darkFeaturesManager.getDarkFeatures().isFeatureEnabled("confluence.render.prefetch");
    }
}

