/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugin.webresource.cdn.mapper;

import com.atlassian.plugin.webresource.cdn.mapper.Mapping;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;

public class DefaultMapping
implements Mapping {
    private final String originalResource;
    private final List<String> mappedResources;

    public DefaultMapping(@Nonnull String originalResource, @Nonnull Stream<String> mappedResources) {
        Preconditions.checkNotNull((Object)originalResource, (Object)"originalResource is null!");
        Preconditions.checkNotNull(mappedResources, (String)"mappedResources is null for originalResource '%s'!", (Object)originalResource);
        this.originalResource = originalResource;
        this.mappedResources = mappedResources.distinct().filter(StringUtils::isNotEmpty).collect(Collectors.toList());
        Preconditions.checkArgument((!this.mappedResources.isEmpty() ? 1 : 0) != 0, (String)"mappedResources is empty for originalResource '%s'!", (Object)originalResource);
    }

    @Override
    @Nonnull
    public String originalResource() {
        return this.originalResource;
    }

    @Override
    @Nonnull
    public List<String> mappedResources() {
        return this.mappedResources;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DefaultMapping mapping = (DefaultMapping)o;
        return Objects.equals(this.originalResource, mapping.originalResource) && Objects.equals(this.mappedResources, mapping.mappedResources);
    }

    public int hashCode() {
        return Objects.hash(this.originalResource, this.mappedResources);
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("originalResource", (Object)this.originalResource).add("mappedResources", this.mappedResources).toString();
    }
}

