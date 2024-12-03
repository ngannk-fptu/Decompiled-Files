/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Maybe
 *  javax.activation.DataSource
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.util.FugueConversionUtil;
import com.atlassian.fugue.Maybe;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import javax.activation.DataSource;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface PluginDataSourceFactory {
    public Optional<Iterable<DataSource>> getResourcesFromModules(String var1, @Nullable Predicate<ResourceView> var2);

    @Deprecated
    default public Maybe<Iterable<DataSource>> resourcesFromModules(String moduleKey) {
        return FugueConversionUtil.toComMaybe(this.getResourcesFromModules(moduleKey));
    }

    public Optional<Iterable<DataSource>> getResourcesFromModules(String var1);

    @Deprecated
    default public Maybe<DataSource> resourceFromModuleByName(String moduleKey, String resourceName) {
        return FugueConversionUtil.toComMaybe(this.getResourceFromModuleByName(moduleKey, resourceName));
    }

    public Optional<DataSource> getResourceFromModuleByName(String var1, String var2);

    public static interface ResourceView {
        public String name();

        public String type();

        public String location();

        public Map<String, String> params();

        public String contentType();
    }

    public static enum FilterByType implements Predicate<ResourceView>
    {
        EMBEDDED(input -> "embedded".equalsIgnoreCase(input.type())),
        DOWNLOAD(resource -> "download".equalsIgnoreCase(resource.type())),
        RELATED(resource -> "related".equalsIgnoreCase(resource.type())),
        IMAGE(resource -> resource.contentType().startsWith("image/")),
        CSS(resource -> resource.contentType().equals("text/css"));

        private Predicate<ResourceView> filter;

        private FilterByType(Predicate<ResourceView> filter) {
            this.filter = filter;
        }

        @Override
        public boolean test(ResourceView resource) {
            return this.filter.test(resource);
        }

        @Deprecated
        public boolean evaluate(ResourceView resource) {
            return this.test(resource);
        }

        public String toString() {
            return new ToStringBuilder((Object)this, ToStringStyle.SHORT_PREFIX_STYLE).append((Object)this.name()).toString();
        }
    }
}

