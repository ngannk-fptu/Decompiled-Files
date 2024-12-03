/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.rest.representations;

import com.atlassian.marketplace.client.model.AddonReference;
import com.atlassian.marketplace.client.model.AddonReviewsSummary;
import com.atlassian.upm.UpmFugueConverters;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.representations.LinksMapBuilder;
import com.atlassian.upm.rest.UpmUriBuilder;
import com.atlassian.upm.rest.representations.IconRepresentation;
import com.atlassian.upm.rest.representations.UpmLinkBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class RecommendedPluginCollectionRepresentation {
    @JsonProperty
    private final Map<String, URI> links;
    @JsonProperty
    private final List<RecommendedPluginEntry> recommendations;

    @JsonCreator
    public RecommendedPluginCollectionRepresentation(@JsonProperty(value="links") Map<String, URI> links, @JsonProperty(value="recommendations") Collection<RecommendedPluginEntry> recommendations) {
        this.links = Collections.unmodifiableMap(new HashMap<String, URI>(links));
        this.recommendations = Collections.unmodifiableList(new ArrayList<RecommendedPluginEntry>(recommendations));
    }

    public RecommendedPluginCollectionRepresentation(Iterable<AddonReference> recommendations, UpmUriBuilder uriBuilder, UpmLinkBuilder linkBuilder, String pluginKey) {
        LinksMapBuilder links = linkBuilder.buildLinkForSelf(uriBuilder.buildRecommendedPluginCollectionUri(pluginKey));
        this.links = links.build();
        this.recommendations = Collections.unmodifiableList(StreamSupport.stream(recommendations.spliterator(), false).map(r -> new RecommendedPluginEntry((AddonReference)r, linkBuilder, uriBuilder)).collect(Collectors.toList()));
    }

    public Map<String, URI> getLinks() {
        return this.links;
    }

    public Iterable<RecommendedPluginEntry> getRecommendations() {
        return this.recommendations;
    }

    public static class RecommendedPluginEntry {
        @JsonProperty
        private final Map<String, URI> links;
        @JsonProperty
        private final String key;
        @JsonProperty
        private final String name;
        @JsonProperty
        private final IconRepresentation logo;
        @JsonProperty
        private final Float rating;
        @JsonProperty
        private final Integer ratingCount;
        @JsonProperty
        private final Integer reviewCount;

        public RecommendedPluginEntry(AddonReference addon, UpmLinkBuilder linkBuilder, UpmUriBuilder uriBuilder) {
            LinksMapBuilder links = linkBuilder.buildLinkForSelf(uriBuilder.buildAvailablePluginUri(addon.getKey()));
            links.putIfPermitted(Permission.GET_AVAILABLE_PLUGINS, "singlePluginViewLink", uriBuilder.buildUpmSinglePluginViewUri(addon.getKey()));
            this.links = links.build();
            this.key = addon.getKey();
            this.name = addon.getName();
            this.logo = IconRepresentation.newIcon(UpmFugueConverters.toUpmOption(addon.getImage()));
            Float rating = null;
            Integer ratingCount = null;
            for (AddonReviewsSummary reviews : addon.getReviews()) {
                rating = Float.valueOf(reviews.getAverageStars());
                ratingCount = reviews.getCount();
            }
            this.rating = rating;
            this.ratingCount = ratingCount;
            this.reviewCount = ratingCount;
        }
    }
}

