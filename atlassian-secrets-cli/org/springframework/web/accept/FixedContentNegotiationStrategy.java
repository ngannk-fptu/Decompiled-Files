/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.accept;

import java.util.Collections;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.context.request.NativeWebRequest;

public class FixedContentNegotiationStrategy
implements ContentNegotiationStrategy {
    private static final Log logger = LogFactory.getLog(FixedContentNegotiationStrategy.class);
    private final List<MediaType> contentTypes;

    public FixedContentNegotiationStrategy(MediaType contentType) {
        this(Collections.singletonList(contentType));
    }

    public FixedContentNegotiationStrategy(List<MediaType> contentTypes) {
        Assert.notNull(contentTypes, "'contentTypes' must not be null");
        this.contentTypes = Collections.unmodifiableList(contentTypes);
    }

    public List<MediaType> getContentTypes() {
        return this.contentTypes;
    }

    @Override
    public List<MediaType> resolveMediaTypes(NativeWebRequest request) {
        if (logger.isDebugEnabled()) {
            logger.debug("Requested media types: " + this.contentTypes);
        }
        return this.contentTypes;
    }
}

