/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.metadata.filter;

import java.util.List;
import org.apache.tika.config.ServiceLoader;
import org.apache.tika.metadata.filter.CompositeMetadataFilter;
import org.apache.tika.metadata.filter.MetadataFilter;
import org.apache.tika.utils.ServiceLoaderUtils;

public class DefaultMetadataFilter
extends CompositeMetadataFilter {
    public DefaultMetadataFilter(ServiceLoader serviceLoader) {
        super(DefaultMetadataFilter.getDefaultFilters(serviceLoader));
    }

    public DefaultMetadataFilter(List<MetadataFilter> metadataFilters) {
        super(metadataFilters);
    }

    public DefaultMetadataFilter() {
        this(new ServiceLoader());
    }

    private static List<MetadataFilter> getDefaultFilters(ServiceLoader loader) {
        List<MetadataFilter> metadataFilters = loader.loadStaticServiceProviders(MetadataFilter.class);
        ServiceLoaderUtils.sortLoadedClasses(metadataFilters);
        return metadataFilters;
    }
}

