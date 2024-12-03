/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.springframework.core.io.Resource
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.web.accept;

import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.accept.AbstractMappingContentNegotiationStrategy;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.UrlPathHelper;

@Deprecated
public class PathExtensionContentNegotiationStrategy
extends AbstractMappingContentNegotiationStrategy {
    private UrlPathHelper urlPathHelper = new UrlPathHelper();

    public PathExtensionContentNegotiationStrategy() {
        this(null);
    }

    public PathExtensionContentNegotiationStrategy(@Nullable Map<String, MediaType> mediaTypes) {
        super(mediaTypes);
        this.setUseRegisteredExtensionsOnly(false);
        this.setIgnoreUnknownExtensions(true);
        this.urlPathHelper.setUrlDecode(false);
    }

    public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
        this.urlPathHelper = urlPathHelper;
    }

    @Deprecated
    public void setUseJaf(boolean useJaf) {
        this.setUseRegisteredExtensionsOnly(!useJaf);
    }

    @Override
    @Nullable
    protected String getMediaTypeKey(NativeWebRequest webRequest) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            return null;
        }
        String path = this.urlPathHelper.getLookupPathForRequest(request);
        String extension = UriUtils.extractFileExtension(path);
        return StringUtils.hasText((String)extension) ? extension.toLowerCase(Locale.ENGLISH) : null;
    }

    @Nullable
    public MediaType getMediaTypeForResource(Resource resource) {
        Assert.notNull((Object)resource, (String)"Resource must not be null");
        MediaType mediaType = null;
        String filename = resource.getFilename();
        String extension = StringUtils.getFilenameExtension((String)filename);
        if (extension != null) {
            mediaType = this.lookupMediaType(extension);
        }
        if (mediaType == null) {
            mediaType = MediaTypeFactory.getMediaType(filename).orElse(null);
        }
        return mediaType;
    }
}

