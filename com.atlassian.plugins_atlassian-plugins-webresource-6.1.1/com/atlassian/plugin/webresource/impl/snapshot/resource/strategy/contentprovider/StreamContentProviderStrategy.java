/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sourcemap.ReadableSourceMap
 */
package com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.contentprovider;

import com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.contentprovider.ContentProviderStrategy;
import com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.contenttype.ContentTypeStrategy;
import com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.path.PathStrategy;
import com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.stream.StreamStrategy;
import com.atlassian.plugin.webresource.impl.support.Content;
import com.atlassian.plugin.webresource.impl.support.ContentImpl;
import com.atlassian.plugin.webresource.impl.support.Support;
import com.atlassian.sourcemap.ReadableSourceMap;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamContentProviderStrategy
implements ContentProviderStrategy {
    private final StreamStrategy streamStrategy;
    private final ContentTypeStrategy contentTypeStrategy;
    private final PathStrategy pathStrategy;

    StreamContentProviderStrategy(StreamStrategy streamStrategy, ContentTypeStrategy contentTypeStrategy, PathStrategy pathStrategy) {
        this.streamStrategy = streamStrategy;
        this.contentTypeStrategy = contentTypeStrategy;
        this.pathStrategy = pathStrategy;
    }

    @Override
    public Content getContent() {
        return new ContentImpl(this.contentTypeStrategy.getContentType(), false){

            @Override
            public ReadableSourceMap writeTo(OutputStream out, boolean isSourceMapEnabled) {
                InputStream is = StreamContentProviderStrategy.this.streamStrategy.getInputStream(StreamContentProviderStrategy.this.pathStrategy.getPath());
                if (is == null) {
                    throw new RuntimeException("Cannot read resource " + StreamContentProviderStrategy.this.pathStrategy.getPath());
                }
                Support.copy(is, out);
                return null;
            }
        };
    }
}

