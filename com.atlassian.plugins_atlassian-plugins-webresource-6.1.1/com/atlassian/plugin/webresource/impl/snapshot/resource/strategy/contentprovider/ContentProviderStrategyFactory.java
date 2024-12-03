/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.contentprovider;

import com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.contentprovider.ContentProviderStrategy;
import com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.contentprovider.StreamContentProviderStrategy;
import com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.contenttype.ContentTypeStrategy;
import com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.path.PathStrategy;
import com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.stream.StreamStrategy;

public class ContentProviderStrategyFactory {
    public ContentProviderStrategy createStreamContentProviderStrategy(StreamStrategy streamStrategy, ContentTypeStrategy contentTypeStrategy, PathStrategy pathStrategy) {
        return new StreamContentProviderStrategy(streamStrategy, contentTypeStrategy, pathStrategy);
    }
}

