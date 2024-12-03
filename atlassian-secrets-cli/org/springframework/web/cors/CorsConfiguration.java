/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.cors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class CorsConfiguration {
    public static final String ALL = "*";
    private static final List<HttpMethod> DEFAULT_METHODS = Collections.unmodifiableList(Arrays.asList(HttpMethod.GET, HttpMethod.HEAD));
    private static final List<String> DEFAULT_PERMIT_METHODS = Collections.unmodifiableList(Arrays.asList(HttpMethod.GET.name(), HttpMethod.HEAD.name(), HttpMethod.POST.name()));
    private static final List<String> DEFAULT_PERMIT_ALL = Collections.unmodifiableList(Collections.singletonList("*"));
    @Nullable
    private List<String> allowedOrigins;
    @Nullable
    private List<String> allowedMethods;
    @Nullable
    private List<HttpMethod> resolvedMethods = DEFAULT_METHODS;
    @Nullable
    private List<String> allowedHeaders;
    @Nullable
    private List<String> exposedHeaders;
    @Nullable
    private Boolean allowCredentials;
    @Nullable
    private Long maxAge;

    public CorsConfiguration() {
    }

    public CorsConfiguration(CorsConfiguration other) {
        this.allowedOrigins = other.allowedOrigins;
        this.allowedMethods = other.allowedMethods;
        this.resolvedMethods = other.resolvedMethods;
        this.allowedHeaders = other.allowedHeaders;
        this.exposedHeaders = other.exposedHeaders;
        this.allowCredentials = other.allowCredentials;
        this.maxAge = other.maxAge;
    }

    public void setAllowedOrigins(@Nullable List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins != null ? new ArrayList<String>(allowedOrigins) : null;
    }

    @Nullable
    public List<String> getAllowedOrigins() {
        return this.allowedOrigins;
    }

    public void addAllowedOrigin(String origin) {
        if (this.allowedOrigins == null) {
            this.allowedOrigins = new ArrayList<String>(4);
        } else if (this.allowedOrigins == DEFAULT_PERMIT_ALL) {
            this.setAllowedOrigins(DEFAULT_PERMIT_ALL);
        }
        this.allowedOrigins.add(origin);
    }

    public void setAllowedMethods(@Nullable List<String> allowedMethods) {
        ArrayList<String> arrayList = this.allowedMethods = allowedMethods != null ? new ArrayList<String>(allowedMethods) : null;
        if (!CollectionUtils.isEmpty(allowedMethods)) {
            this.resolvedMethods = new ArrayList<HttpMethod>(allowedMethods.size());
            for (String method : allowedMethods) {
                if (ALL.equals(method)) {
                    this.resolvedMethods = null;
                    break;
                }
                this.resolvedMethods.add(HttpMethod.resolve(method));
            }
        } else {
            this.resolvedMethods = DEFAULT_METHODS;
        }
    }

    @Nullable
    public List<String> getAllowedMethods() {
        return this.allowedMethods;
    }

    public void addAllowedMethod(HttpMethod method) {
        this.addAllowedMethod(method.name());
    }

    public void addAllowedMethod(String method) {
        if (StringUtils.hasText(method)) {
            if (this.allowedMethods == null) {
                this.allowedMethods = new ArrayList<String>(4);
                this.resolvedMethods = new ArrayList<HttpMethod>(4);
            } else if (this.allowedMethods == DEFAULT_PERMIT_METHODS) {
                this.setAllowedMethods(DEFAULT_PERMIT_METHODS);
            }
            this.allowedMethods.add(method);
            if (ALL.equals(method)) {
                this.resolvedMethods = null;
            } else if (this.resolvedMethods != null) {
                this.resolvedMethods.add(HttpMethod.resolve(method));
            }
        }
    }

    public void setAllowedHeaders(@Nullable List<String> allowedHeaders) {
        this.allowedHeaders = allowedHeaders != null ? new ArrayList<String>(allowedHeaders) : null;
    }

    @Nullable
    public List<String> getAllowedHeaders() {
        return this.allowedHeaders;
    }

    public void addAllowedHeader(String allowedHeader) {
        if (this.allowedHeaders == null) {
            this.allowedHeaders = new ArrayList<String>(4);
        } else if (this.allowedHeaders == DEFAULT_PERMIT_ALL) {
            this.setAllowedHeaders(DEFAULT_PERMIT_ALL);
        }
        this.allowedHeaders.add(allowedHeader);
    }

    public void setExposedHeaders(@Nullable List<String> exposedHeaders) {
        if (exposedHeaders != null && exposedHeaders.contains(ALL)) {
            throw new IllegalArgumentException("'*' is not a valid exposed header value");
        }
        this.exposedHeaders = exposedHeaders != null ? new ArrayList<String>(exposedHeaders) : null;
    }

    @Nullable
    public List<String> getExposedHeaders() {
        return this.exposedHeaders;
    }

    public void addExposedHeader(String exposedHeader) {
        if (ALL.equals(exposedHeader)) {
            throw new IllegalArgumentException("'*' is not a valid exposed header value");
        }
        if (this.exposedHeaders == null) {
            this.exposedHeaders = new ArrayList<String>(4);
        }
        this.exposedHeaders.add(exposedHeader);
    }

    public void setAllowCredentials(@Nullable Boolean allowCredentials) {
        this.allowCredentials = allowCredentials;
    }

    @Nullable
    public Boolean getAllowCredentials() {
        return this.allowCredentials;
    }

    public void setMaxAge(@Nullable Long maxAge) {
        this.maxAge = maxAge;
    }

    @Nullable
    public Long getMaxAge() {
        return this.maxAge;
    }

    public CorsConfiguration applyPermitDefaultValues() {
        if (this.allowedOrigins == null) {
            this.allowedOrigins = DEFAULT_PERMIT_ALL;
        }
        if (this.allowedMethods == null) {
            this.allowedMethods = DEFAULT_PERMIT_METHODS;
            this.resolvedMethods = DEFAULT_PERMIT_METHODS.stream().map(HttpMethod::resolve).collect(Collectors.toList());
        }
        if (this.allowedHeaders == null) {
            this.allowedHeaders = DEFAULT_PERMIT_ALL;
        }
        if (this.maxAge == null) {
            this.maxAge = 1800L;
        }
        return this;
    }

    @Nullable
    public CorsConfiguration combine(@Nullable CorsConfiguration other) {
        Long maxAge;
        if (other == null) {
            return this;
        }
        CorsConfiguration config = new CorsConfiguration(this);
        config.setAllowedOrigins(this.combine(this.getAllowedOrigins(), other.getAllowedOrigins()));
        config.setAllowedMethods(this.combine(this.getAllowedMethods(), other.getAllowedMethods()));
        config.setAllowedHeaders(this.combine(this.getAllowedHeaders(), other.getAllowedHeaders()));
        config.setExposedHeaders(this.combine(this.getExposedHeaders(), other.getExposedHeaders()));
        Boolean allowCredentials = other.getAllowCredentials();
        if (allowCredentials != null) {
            config.setAllowCredentials(allowCredentials);
        }
        if ((maxAge = other.getMaxAge()) != null) {
            config.setMaxAge(maxAge);
        }
        return config;
    }

    private List<String> combine(@Nullable List<String> source, @Nullable List<String> other) {
        if (other == null) {
            return source != null ? source : Collections.emptyList();
        }
        if (source == null) {
            return other;
        }
        if (source == DEFAULT_PERMIT_ALL || source == DEFAULT_PERMIT_METHODS) {
            return other;
        }
        if (other == DEFAULT_PERMIT_ALL || other == DEFAULT_PERMIT_METHODS) {
            return source;
        }
        if (source.contains(ALL) || other.contains(ALL)) {
            return new ArrayList<String>(Collections.singletonList(ALL));
        }
        LinkedHashSet<String> combined = new LinkedHashSet<String>(source);
        combined.addAll(other);
        return new ArrayList<String>(combined);
    }

    @Nullable
    public String checkOrigin(@Nullable String requestOrigin) {
        if (!StringUtils.hasText(requestOrigin)) {
            return null;
        }
        if (ObjectUtils.isEmpty(this.allowedOrigins)) {
            return null;
        }
        if (this.allowedOrigins.contains(ALL)) {
            if (this.allowCredentials != Boolean.TRUE) {
                return ALL;
            }
            return requestOrigin;
        }
        for (String allowedOrigin : this.allowedOrigins) {
            if (!requestOrigin.equalsIgnoreCase(allowedOrigin)) continue;
            return requestOrigin;
        }
        return null;
    }

    @Nullable
    public List<HttpMethod> checkHttpMethod(@Nullable HttpMethod requestMethod) {
        if (requestMethod == null) {
            return null;
        }
        if (this.resolvedMethods == null) {
            return Collections.singletonList(requestMethod);
        }
        return this.resolvedMethods.contains((Object)requestMethod) ? this.resolvedMethods : null;
    }

    @Nullable
    public List<String> checkHeaders(@Nullable List<String> requestHeaders) {
        if (requestHeaders == null) {
            return null;
        }
        if (requestHeaders.isEmpty()) {
            return Collections.emptyList();
        }
        if (ObjectUtils.isEmpty(this.allowedHeaders)) {
            return null;
        }
        boolean allowAnyHeader = this.allowedHeaders.contains(ALL);
        ArrayList<String> result = new ArrayList<String>(requestHeaders.size());
        block0: for (String requestHeader : requestHeaders) {
            if (!StringUtils.hasText(requestHeader)) continue;
            requestHeader = requestHeader.trim();
            if (allowAnyHeader) {
                result.add(requestHeader);
                continue;
            }
            for (String allowedHeader : this.allowedHeaders) {
                if (!requestHeader.equalsIgnoreCase(allowedHeader)) continue;
                result.add(requestHeader);
                continue block0;
            }
        }
        return result.isEmpty() ? null : result;
    }
}

