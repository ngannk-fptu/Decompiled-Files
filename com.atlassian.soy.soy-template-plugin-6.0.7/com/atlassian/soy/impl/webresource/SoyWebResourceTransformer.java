/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.servlet.DownloadableResource
 *  com.atlassian.plugin.webresource.QueryParams
 *  com.atlassian.plugin.webresource.transformer.CharSequenceDownloadableResource
 *  com.atlassian.plugin.webresource.transformer.TransformableResource
 *  com.atlassian.plugin.webresource.transformer.UrlReadingWebResourceTransformer
 *  com.atlassian.soy.impl.SoyManager
 *  com.google.common.base.Supplier
 */
package com.atlassian.soy.impl.webresource;

import com.atlassian.plugin.servlet.DownloadableResource;
import com.atlassian.plugin.webresource.QueryParams;
import com.atlassian.plugin.webresource.transformer.CharSequenceDownloadableResource;
import com.atlassian.plugin.webresource.transformer.TransformableResource;
import com.atlassian.plugin.webresource.transformer.UrlReadingWebResourceTransformer;
import com.atlassian.soy.impl.SoyManager;
import com.atlassian.soy.impl.webresource.ThreadLocalQueryParamsResolver;
import com.google.common.base.Supplier;

public class SoyWebResourceTransformer
implements UrlReadingWebResourceTransformer {
    private final SoyManager soyManager;
    private final ThreadLocalQueryParamsResolver queryParamsResolver;

    public SoyWebResourceTransformer(SoyManager soyManager, ThreadLocalQueryParamsResolver queryParamsResolver) {
        this.soyManager = soyManager;
        this.queryParamsResolver = queryParamsResolver;
    }

    public DownloadableResource transform(TransformableResource transformableResource, QueryParams queryParams) {
        return new SoyDownloadableResource(transformableResource.nextResource(), transformableResource.location().getLocation(), queryParams);
    }

    private class SoyDownloadableResource
    extends CharSequenceDownloadableResource {
        private final String location;
        private final QueryParams queryParams;

        private SoyDownloadableResource(DownloadableResource nextResource, String location, QueryParams queryParams) {
            super(nextResource);
            this.location = location;
            this.queryParams = queryParams;
        }

        protected CharSequence transform(final CharSequence originalContent) {
            return SoyWebResourceTransformer.this.queryParamsResolver.withQueryParams(this.queryParams, new Supplier<CharSequence>(){

                public CharSequence get() {
                    return SoyWebResourceTransformer.this.soyManager.compile(originalContent, SoyDownloadableResource.this.location);
                }
            });
        }
    }
}

