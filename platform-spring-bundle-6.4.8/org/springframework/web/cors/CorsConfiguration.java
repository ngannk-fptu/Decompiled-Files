/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.cors;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class CorsConfiguration {
    public static final String ALL = "*";
    private static final List<String> ALL_LIST = Collections.singletonList("*");
    private static final OriginPattern ALL_PATTERN = new OriginPattern("*");
    private static final List<OriginPattern> ALL_PATTERN_LIST = Collections.singletonList(ALL_PATTERN);
    private static final List<String> DEFAULT_PERMIT_ALL = Collections.singletonList("*");
    private static final List<HttpMethod> DEFAULT_METHODS = Collections.unmodifiableList(Arrays.asList(HttpMethod.GET, HttpMethod.HEAD));
    private static final List<String> DEFAULT_PERMIT_METHODS = Collections.unmodifiableList(Arrays.asList(HttpMethod.GET.name(), HttpMethod.HEAD.name(), HttpMethod.POST.name()));
    @Nullable
    private List<String> allowedOrigins;
    @Nullable
    private List<OriginPattern> allowedOriginPatterns;
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
        this.allowedOriginPatterns = other.allowedOriginPatterns;
        this.allowedMethods = other.allowedMethods;
        this.resolvedMethods = other.resolvedMethods;
        this.allowedHeaders = other.allowedHeaders;
        this.exposedHeaders = other.exposedHeaders;
        this.allowCredentials = other.allowCredentials;
        this.maxAge = other.maxAge;
    }

    public void setAllowedOrigins(@Nullable List<String> origins) {
        this.allowedOrigins = origins == null ? null : origins.stream().filter(Objects::nonNull).map(this::trimTrailingSlash).collect(Collectors.toList());
    }

    private String trimTrailingSlash(String origin) {
        return origin.endsWith("/") ? origin.substring(0, origin.length() - 1) : origin;
    }

    @Nullable
    public List<String> getAllowedOrigins() {
        return this.allowedOrigins;
    }

    public void addAllowedOrigin(@Nullable String origin) {
        if (origin == null) {
            return;
        }
        if (this.allowedOrigins == null) {
            this.allowedOrigins = new ArrayList<String>(4);
        } else if (this.allowedOrigins == DEFAULT_PERMIT_ALL && CollectionUtils.isEmpty(this.allowedOriginPatterns)) {
            this.setAllowedOrigins(DEFAULT_PERMIT_ALL);
        }
        origin = this.trimTrailingSlash(origin);
        this.allowedOrigins.add(origin);
    }

    public CorsConfiguration setAllowedOriginPatterns(@Nullable List<String> allowedOriginPatterns) {
        if (allowedOriginPatterns == null) {
            this.allowedOriginPatterns = null;
        } else {
            this.allowedOriginPatterns = new ArrayList<OriginPattern>(allowedOriginPatterns.size());
            for (String patternValue : allowedOriginPatterns) {
                this.addAllowedOriginPattern(patternValue);
            }
        }
        return this;
    }

    @Nullable
    public List<String> getAllowedOriginPatterns() {
        if (this.allowedOriginPatterns == null) {
            return null;
        }
        return this.allowedOriginPatterns.stream().map(OriginPattern::getDeclaredPattern).collect(Collectors.toList());
    }

    public void addAllowedOriginPattern(@Nullable String originPattern) {
        if (originPattern == null) {
            return;
        }
        if (this.allowedOriginPatterns == null) {
            this.allowedOriginPatterns = new ArrayList<OriginPattern>(4);
        }
        originPattern = this.trimTrailingSlash(originPattern);
        this.allowedOriginPatterns.add(new OriginPattern(originPattern));
        if (this.allowedOrigins == DEFAULT_PERMIT_ALL) {
            this.allowedOrigins = null;
        }
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
        this.exposedHeaders = exposedHeaders != null ? new ArrayList<String>(exposedHeaders) : null;
    }

    @Nullable
    public List<String> getExposedHeaders() {
        return this.exposedHeaders;
    }

    public void addExposedHeader(String exposedHeader) {
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

    public void setMaxAge(Duration maxAge) {
        this.maxAge = maxAge.getSeconds();
    }

    public void setMaxAge(@Nullable Long maxAge) {
        this.maxAge = maxAge;
    }

    @Nullable
    public Long getMaxAge() {
        return this.maxAge;
    }

    public CorsConfiguration applyPermitDefaultValues() {
        if (this.allowedOrigins == null && this.allowedOriginPatterns == null) {
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

    public void validateAllowCredentials() {
        if (this.allowCredentials == Boolean.TRUE && this.allowedOrigins != null && this.allowedOrigins.contains(ALL)) {
            throw new IllegalArgumentException("When allowCredentials is true, allowedOrigins cannot contain the special value \"*\" since that cannot be set on the \"Access-Control-Allow-Origin\" response header. To allow credentials to a set of origins, list them explicitly or consider using \"allowedOriginPatterns\" instead.");
        }
    }

    public CorsConfiguration combine(@Nullable CorsConfiguration other) {
        Long maxAge;
        if (other == null) {
            return this;
        }
        CorsConfiguration config = new CorsConfiguration(this);
        List<String> origins = this.combine(this.getAllowedOrigins(), other.getAllowedOrigins());
        List<OriginPattern> patterns = this.combinePatterns(this.allowedOriginPatterns, other.allowedOriginPatterns);
        config.allowedOrigins = origins == DEFAULT_PERMIT_ALL && !CollectionUtils.isEmpty(patterns) ? null : origins;
        config.allowedOriginPatterns = patterns;
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
            return ALL_LIST;
        }
        LinkedHashSet<String> combined = new LinkedHashSet<String>(source.size() + other.size());
        combined.addAll(source);
        combined.addAll(other);
        return new ArrayList<String>(combined);
    }

    private List<OriginPattern> combinePatterns(@Nullable List<OriginPattern> source, @Nullable List<OriginPattern> other) {
        if (other == null) {
            return source != null ? source : Collections.emptyList();
        }
        if (source == null) {
            return other;
        }
        if (source.contains(ALL_PATTERN) || other.contains(ALL_PATTERN)) {
            return ALL_PATTERN_LIST;
        }
        LinkedHashSet<OriginPattern> combined = new LinkedHashSet<OriginPattern>(source.size() + other.size());
        combined.addAll(source);
        combined.addAll(other);
        return new ArrayList<OriginPattern>(combined);
    }

    @Nullable
    public String checkOrigin(@Nullable String origin) {
        if (!StringUtils.hasText(origin)) {
            return null;
        }
        String originToCheck = this.trimTrailingSlash(origin);
        if (!ObjectUtils.isEmpty(this.allowedOrigins)) {
            if (this.allowedOrigins.contains(ALL)) {
                this.validateAllowCredentials();
                return ALL;
            }
            for (String allowedOrigin : this.allowedOrigins) {
                if (!originToCheck.equalsIgnoreCase(allowedOrigin)) continue;
                return origin;
            }
        }
        if (!ObjectUtils.isEmpty(this.allowedOriginPatterns)) {
            for (OriginPattern p : this.allowedOriginPatterns) {
                if (!p.getDeclaredPattern().equals(ALL) && !p.getPattern().matcher(originToCheck).matches()) continue;
                return origin;
            }
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

    private static class OriginPattern {
        private static final Pattern PORTS_PATTERN = Pattern.compile("(.*):\\[(\\*|\\d+(,\\d+)*)]");
        private final String declaredPattern;
        private final Pattern pattern;

        OriginPattern(String declaredPattern) {
            this.declaredPattern = declaredPattern;
            this.pattern = OriginPattern.initPattern(declaredPattern);
        }

        private static Pattern initPattern(String patternValue) {
            String portList = null;
            Matcher matcher = PORTS_PATTERN.matcher(patternValue);
            if (matcher.matches()) {
                patternValue = matcher.group(1);
                portList = matcher.group(2);
            }
            patternValue = "\\Q" + patternValue + "\\E";
            patternValue = patternValue.replace(CorsConfiguration.ALL, "\\E.*\\Q");
            if (portList != null) {
                patternValue = patternValue + (portList.equals(CorsConfiguration.ALL) ? "(:\\d+)?" : ":(" + portList.replace(',', '|') + ")");
            }
            return Pattern.compile(patternValue);
        }

        public String getDeclaredPattern() {
            return this.declaredPattern;
        }

        public Pattern getPattern() {
            return this.pattern;
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || !this.getClass().equals(other.getClass())) {
                return false;
            }
            return ObjectUtils.nullSafeEquals(this.declaredPattern, ((OriginPattern)other).declaredPattern);
        }

        public int hashCode() {
            return this.declaredPattern.hashCode();
        }

        public String toString() {
            return this.declaredPattern;
        }
    }
}

