/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.rest.representations;

import com.atlassian.marketplace.client.model.AddonCategorySummary;
import com.atlassian.upm.rest.UpmUriBuilder;
import com.atlassian.upm.rest.representations.UpmLinkBuilder;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class CategoryRepresentation {
    @JsonProperty
    private final Map<String, URI> links;
    @JsonProperty
    private final String name;
    private static final Function<AddonCategorySummary, String> categoryName = AddonCategorySummary::getName;

    @JsonCreator
    public CategoryRepresentation(@JsonProperty(value="links") Map<String, URI> links, @JsonProperty(value="name") String name) {
        this.links = Collections.unmodifiableMap(new HashMap<String, URI>(links));
        this.name = Objects.requireNonNull(name, "name");
    }

    CategoryRepresentation(UpmLinkBuilder linkBuilder, UpmUriBuilder upmUriBuilder, String name) {
        this.name = Objects.requireNonNull(name, "name");
        this.links = linkBuilder.buildLinkForSelf(upmUriBuilder.buildUpmMarketplacePluginCategoryUri(name)).build();
    }

    public Map<String, URI> getLinks() {
        return this.links;
    }

    public String getName() {
        return this.name;
    }

    public static List<CategoryRepresentation> representUniqueCategories(Iterable<AddonCategorySummary> categories, UpmLinkBuilder linkBuilder, UpmUriBuilder uriBuilder) {
        return Collections.unmodifiableList(StreamSupport.stream(categories.spliterator(), false).map(categoryName).sorted(String.CASE_INSENSITIVE_ORDER).map(name -> new CategoryRepresentation(linkBuilder, uriBuilder, (String)name)).collect(Collectors.toList()));
    }
}

