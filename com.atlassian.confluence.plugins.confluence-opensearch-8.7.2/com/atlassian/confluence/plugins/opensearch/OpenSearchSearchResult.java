/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.persistence.hibernate.HibernateHandle
 *  com.atlassian.confluence.search.v2.AbstractSearchResult
 *  com.atlassian.confluence.search.v2.AbstractSearchResult$AlternateFieldNames
 *  com.atlassian.confluence.search.v2.SearchFieldMappings
 *  com.google.common.collect.ImmutableSet
 *  org.opensearch.client.json.JsonData
 *  org.opensearch.client.opensearch.core.search.Hit
 */
package com.atlassian.confluence.plugins.opensearch;

import com.atlassian.confluence.core.persistence.hibernate.HibernateHandle;
import com.atlassian.confluence.plugins.opensearch.encoder.Encoder;
import com.atlassian.confluence.search.v2.AbstractSearchResult;
import com.atlassian.confluence.search.v2.SearchFieldMappings;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch.core.search.Hit;

public class OpenSearchSearchResult
extends AbstractSearchResult {
    private static final String FRAGMENT_SEPARATOR = " &hellip; ";
    private final Hit<?> hit;
    private final Encoder encoder;
    private final AbstractSearchResult.AlternateFieldNames alternateNames = new AbstractSearchResult.AlternateFieldNames(){

        protected boolean fieldExists(String name) {
            return OpenSearchSearchResult.this.hit.fields().containsKey(name);
        }
    };

    public OpenSearchSearchResult(Hit<?> hit, Encoder encoder) {
        this.hit = Objects.requireNonNull(hit);
        this.encoder = encoder;
    }

    public long getHandleId() {
        return ((HibernateHandle)this.getHandle()).getId();
    }

    public Set<String> getFieldNames() {
        return this.alternateNames.expand(this.hit.fields().keySet());
    }

    public String getFieldValue(String fieldName) {
        return this.getFieldValues(fieldName).stream().findFirst().orElse(null);
    }

    public Set<String> getFieldValues(String fieldName) {
        JsonData data = (JsonData)this.hit.fields().get(this.alternateNames.resolve(fieldName));
        if (data == null) {
            return ImmutableSet.of();
        }
        return ImmutableSet.copyOf((Object[])((String[])data.to(String[].class)));
    }

    public String getDisplayTitleWithHighlights() {
        return this.highlightTextFor(SearchFieldMappings.TITLE.getName()).or(() -> this.highlightTextFor(SearchFieldMappings.DISPLAY_TITLE.getName())).orElseGet(() -> super.getDisplayTitleWithHighlights());
    }

    public String getResultExcerptWithHighlights() {
        return this.highlightTextFor(SearchFieldMappings.CONTENT.getName()).orElseGet(() -> super.getResultExcerptWithHighlights());
    }

    public List<String> getSort() {
        return this.hit.sort();
    }

    private Optional<String> highlightTextFor(String field) {
        return Optional.ofNullable((List)this.hit.highlight().get(field)).map(fragments -> fragments.stream().map(this.encoder::encode).collect(Collectors.joining(FRAGMENT_SEPARATOR)));
    }

    public Double getScore() {
        return this.hit.score();
    }
}

