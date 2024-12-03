/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.container.filter;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.PathSegment;

public class UriConnegFilter
implements ContainerRequestFilter {
    private final Map<String, MediaType> mediaExtentions;
    private final Map<String, String> languageExtentions;

    public UriConnegFilter(Map<String, MediaType> mediaExtentions) {
        if (mediaExtentions == null) {
            throw new IllegalArgumentException();
        }
        this.mediaExtentions = mediaExtentions;
        this.languageExtentions = Collections.emptyMap();
    }

    public UriConnegFilter(Map<String, MediaType> mediaExtentions, Map<String, String> languageExtentions) {
        if (mediaExtentions == null) {
            throw new IllegalArgumentException();
        }
        if (languageExtentions == null) {
            throw new IllegalArgumentException();
        }
        this.mediaExtentions = mediaExtentions;
        this.languageExtentions = languageExtentions;
    }

    @Override
    public ContainerRequest filter(ContainerRequest request) {
        int index;
        String suffix;
        int i;
        String path = request.getRequestUri().getRawPath();
        if (path.indexOf(46) == -1) {
            return request;
        }
        List<PathSegment> l = request.getPathSegments(false);
        if (l.isEmpty()) {
            return request;
        }
        PathSegment segment = null;
        for (int i2 = l.size() - 1; i2 >= 0 && (segment = l.get(i2)).getPath().length() <= 0; --i2) {
        }
        if (segment == null) {
            return request;
        }
        int length = path.length();
        String[] suffixes = segment.getPath().split("\\.");
        for (i = suffixes.length - 1; i >= 1; --i) {
            MediaType accept;
            suffix = suffixes[i];
            if (suffix.length() == 0 || (accept = this.mediaExtentions.get(suffix)) == null) continue;
            request.getRequestHeaders().putSingle("Accept", accept.toString());
            index = path.lastIndexOf('.' + suffix);
            path = new StringBuilder(path).delete(index, index + suffix.length() + 1).toString();
            suffixes[i] = "";
            break;
        }
        for (i = suffixes.length - 1; i >= 1; --i) {
            String acceptLanguage;
            suffix = suffixes[i];
            if (suffix.length() == 0 || (acceptLanguage = this.languageExtentions.get(suffix)) == null) continue;
            request.getRequestHeaders().putSingle("Accept-Language", acceptLanguage);
            index = path.lastIndexOf('.' + suffix);
            path = new StringBuilder(path).delete(index, index + suffix.length() + 1).toString();
            suffixes[i] = "";
            break;
        }
        if (length != path.length()) {
            request.setUris(request.getBaseUri(), request.getRequestUriBuilder().replacePath(path).build(new Object[0]));
        }
        return request;
    }
}

