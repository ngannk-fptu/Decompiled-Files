/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.accept;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.MappingMediaTypeFileExtensionResolver;
import org.springframework.web.context.request.NativeWebRequest;

public abstract class AbstractMappingContentNegotiationStrategy
extends MappingMediaTypeFileExtensionResolver
implements ContentNegotiationStrategy {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private boolean useRegisteredExtensionsOnly = false;
    private boolean ignoreUnknownExtensions = false;

    public AbstractMappingContentNegotiationStrategy(@Nullable Map<String, MediaType> mediaTypes) {
        super(mediaTypes);
    }

    public void setUseRegisteredExtensionsOnly(boolean useRegisteredExtensionsOnly) {
        this.useRegisteredExtensionsOnly = useRegisteredExtensionsOnly;
    }

    public boolean isUseRegisteredExtensionsOnly() {
        return this.useRegisteredExtensionsOnly;
    }

    public void setIgnoreUnknownExtensions(boolean ignoreUnknownExtensions) {
        this.ignoreUnknownExtensions = ignoreUnknownExtensions;
    }

    public boolean isIgnoreUnknownExtensions() {
        return this.ignoreUnknownExtensions;
    }

    @Override
    public List<MediaType> resolveMediaTypes(NativeWebRequest webRequest) throws HttpMediaTypeNotAcceptableException {
        return this.resolveMediaTypeKey(webRequest, this.getMediaTypeKey(webRequest));
    }

    public List<MediaType> resolveMediaTypeKey(NativeWebRequest webRequest, @Nullable String key) throws HttpMediaTypeNotAcceptableException {
        if (StringUtils.hasText(key)) {
            MediaType mediaType = this.lookupMediaType(key);
            if (mediaType != null) {
                this.handleMatch(key, mediaType);
                return Collections.singletonList(mediaType);
            }
            mediaType = this.handleNoMatch(webRequest, key);
            if (mediaType != null) {
                this.addMapping(key, mediaType);
                return Collections.singletonList(mediaType);
            }
        }
        return MEDIA_TYPE_ALL_LIST;
    }

    @Nullable
    protected abstract String getMediaTypeKey(NativeWebRequest var1);

    protected void handleMatch(String key, MediaType mediaType) {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Requested MediaType='" + mediaType + "' based on key='" + key + "'.");
        }
    }

    @Nullable
    protected MediaType handleNoMatch(NativeWebRequest request, String key) throws HttpMediaTypeNotAcceptableException {
        Optional<MediaType> mediaType;
        if (!this.isUseRegisteredExtensionsOnly() && (mediaType = MediaTypeFactory.getMediaType("file." + key)).isPresent()) {
            return mediaType.get();
        }
        if (this.isIgnoreUnknownExtensions()) {
            return null;
        }
        throw new HttpMediaTypeNotAcceptableException(this.getAllMediaTypes());
    }
}

