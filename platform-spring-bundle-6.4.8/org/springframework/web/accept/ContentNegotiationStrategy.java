/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.accept;

import java.util.Collections;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.context.request.NativeWebRequest;

@FunctionalInterface
public interface ContentNegotiationStrategy {
    public static final List<MediaType> MEDIA_TYPE_ALL_LIST = Collections.singletonList(MediaType.ALL);

    public List<MediaType> resolveMediaTypes(NativeWebRequest var1) throws HttpMediaTypeNotAcceptableException;
}

