/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  org.springframework.core.io.Resource
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.web.accept;

import java.util.Map;
import javax.servlet.ServletContext;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.PathExtensionContentNegotiationStrategy;
import org.springframework.web.context.request.NativeWebRequest;

@Deprecated
public class ServletPathExtensionContentNegotiationStrategy
extends PathExtensionContentNegotiationStrategy {
    private final ServletContext servletContext;

    public ServletPathExtensionContentNegotiationStrategy(ServletContext context) {
        this(context, null);
    }

    public ServletPathExtensionContentNegotiationStrategy(ServletContext servletContext, @Nullable Map<String, MediaType> mediaTypes) {
        super(mediaTypes);
        Assert.notNull((Object)servletContext, (String)"ServletContext is required");
        this.servletContext = servletContext;
    }

    @Override
    @Nullable
    protected MediaType handleNoMatch(NativeWebRequest webRequest, String extension) throws HttpMediaTypeNotAcceptableException {
        MediaType superMediaType;
        MediaType mediaType = null;
        String mimeType = this.servletContext.getMimeType("file." + extension);
        if (StringUtils.hasText((String)mimeType)) {
            mediaType = MediaType.parseMediaType(mimeType);
        }
        if ((mediaType == null || MediaType.APPLICATION_OCTET_STREAM.equals(mediaType)) && (superMediaType = super.handleNoMatch(webRequest, extension)) != null) {
            mediaType = superMediaType;
        }
        return mediaType;
    }

    @Override
    public MediaType getMediaTypeForResource(Resource resource) {
        MediaType superMediaType;
        MediaType mediaType = null;
        String mimeType = this.servletContext.getMimeType(resource.getFilename());
        if (StringUtils.hasText((String)mimeType)) {
            mediaType = MediaType.parseMediaType(mimeType);
        }
        if ((mediaType == null || MediaType.APPLICATION_OCTET_STREAM.equals(mediaType)) && (superMediaType = super.getMediaTypeForResource(resource)) != null) {
            mediaType = superMediaType;
        }
        return mediaType;
    }
}

