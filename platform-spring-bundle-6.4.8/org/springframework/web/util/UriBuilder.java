/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.util;

import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

public interface UriBuilder {
    public UriBuilder scheme(@Nullable String var1);

    public UriBuilder userInfo(@Nullable String var1);

    public UriBuilder host(@Nullable String var1);

    public UriBuilder port(int var1);

    public UriBuilder port(@Nullable String var1);

    public UriBuilder path(String var1);

    public UriBuilder replacePath(@Nullable String var1);

    public UriBuilder pathSegment(String ... var1) throws IllegalArgumentException;

    public UriBuilder query(String var1);

    public UriBuilder replaceQuery(@Nullable String var1);

    public UriBuilder queryParam(String var1, Object ... var2);

    public UriBuilder queryParam(String var1, @Nullable Collection<?> var2);

    public UriBuilder queryParamIfPresent(String var1, Optional<?> var2);

    public UriBuilder queryParams(MultiValueMap<String, String> var1);

    public UriBuilder replaceQueryParam(String var1, Object ... var2);

    public UriBuilder replaceQueryParam(String var1, @Nullable Collection<?> var2);

    public UriBuilder replaceQueryParams(MultiValueMap<String, String> var1);

    public UriBuilder fragment(@Nullable String var1);

    public URI build(Object ... var1);

    public URI build(Map<String, ?> var1);
}

