/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.web.accept;

import java.util.Collections;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.context.request.NativeWebRequest;

public class FixedContentNegotiationStrategy
implements ContentNegotiationStrategy {
    private final List<MediaType> contentTypes;

    public FixedContentNegotiationStrategy(MediaType contentType) {
        this(Collections.singletonList(contentType));
    }

    public FixedContentNegotiationStrategy(List<MediaType> contentTypes) {
        Assert.notNull(contentTypes, (String)"'contentTypes' must not be null");
        this.contentTypes = Collections.unmodifiableList(contentTypes);
    }

    public List<MediaType> getContentTypes() {
        return this.contentTypes;
    }

    @Override
    public List<MediaType> resolveMediaTypes(NativeWebRequest request) {
        return this.contentTypes;
    }
}

