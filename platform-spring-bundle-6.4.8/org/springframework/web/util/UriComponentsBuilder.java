/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.util;

import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HierarchicalUriComponents;
import org.springframework.web.util.OpaqueUriComponents;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponents;

public class UriComponentsBuilder
implements UriBuilder,
Cloneable {
    private static final Pattern QUERY_PARAM_PATTERN = Pattern.compile("([^&=]+)(=?)([^&]+)?");
    private static final String SCHEME_PATTERN = "([^:/?#]+):";
    private static final String HTTP_PATTERN = "(?i)(http|https):";
    private static final String USERINFO_PATTERN = "([^@\\[/?#]*)";
    private static final String HOST_IPV4_PATTERN = "[^\\[/?#:]*";
    private static final String HOST_IPV6_PATTERN = "\\[[\\p{XDigit}:.]*[%\\p{Alnum}]*]";
    private static final String HOST_PATTERN = "(\\[[\\p{XDigit}:.]*[%\\p{Alnum}]*]|[^\\[/?#:]*)";
    private static final String PORT_PATTERN = "(\\{[^}]+\\}?|[^/?#]*)";
    private static final String PATH_PATTERN = "([^?#]*)";
    private static final String QUERY_PATTERN = "([^#]*)";
    private static final String LAST_PATTERN = "(.*)";
    private static final Pattern URI_PATTERN = Pattern.compile("^(([^:/?#]+):)?(//(([^@\\[/?#]*)@)?(\\[[\\p{XDigit}:.]*[%\\p{Alnum}]*]|[^\\[/?#:]*)(:(\\{[^}]+\\}?|[^/?#]*))?)?([^?#]*)(\\?([^#]*))?(#(.*))?");
    private static final Pattern HTTP_URL_PATTERN = Pattern.compile("^(?i)(http|https):(//(([^@\\[/?#]*)@)?(\\[[\\p{XDigit}:.]*[%\\p{Alnum}]*]|[^\\[/?#:]*)(:(\\{[^}]+\\}?|[^/?#]*))?)?([^?#]*)(\\?([^#]*))?(#(.*))?");
    private static final String FORWARDED_VALUE = "\"?([^;,\"]+)\"?";
    private static final Pattern FORWARDED_HOST_PATTERN = Pattern.compile("(?i:host)=\"?([^;,\"]+)\"?");
    private static final Pattern FORWARDED_PROTO_PATTERN = Pattern.compile("(?i:proto)=\"?([^;,\"]+)\"?");
    private static final Pattern FORWARDED_FOR_PATTERN = Pattern.compile("(?i:for)=\"?([^;,\"]+)\"?");
    private static final Object[] EMPTY_VALUES = new Object[0];
    @Nullable
    private String scheme;
    @Nullable
    private String ssp;
    @Nullable
    private String userInfo;
    @Nullable
    private String host;
    @Nullable
    private String port;
    private CompositePathComponentBuilder pathBuilder;
    private final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
    @Nullable
    private String fragment;
    private final Map<String, Object> uriVariables = new HashMap<String, Object>(4);
    private boolean encodeTemplate;
    private Charset charset = StandardCharsets.UTF_8;

    protected UriComponentsBuilder() {
        this.pathBuilder = new CompositePathComponentBuilder();
    }

    protected UriComponentsBuilder(UriComponentsBuilder other) {
        this.scheme = other.scheme;
        this.ssp = other.ssp;
        this.userInfo = other.userInfo;
        this.host = other.host;
        this.port = other.port;
        this.pathBuilder = other.pathBuilder.cloneBuilder();
        this.uriVariables.putAll(other.uriVariables);
        this.queryParams.addAll(other.queryParams);
        this.fragment = other.fragment;
        this.encodeTemplate = other.encodeTemplate;
        this.charset = other.charset;
    }

    public static UriComponentsBuilder newInstance() {
        return new UriComponentsBuilder();
    }

    public static UriComponentsBuilder fromPath(String path) {
        UriComponentsBuilder builder = new UriComponentsBuilder();
        builder.path(path);
        return builder;
    }

    public static UriComponentsBuilder fromUri(URI uri) {
        UriComponentsBuilder builder = new UriComponentsBuilder();
        builder.uri(uri);
        return builder;
    }

    public static UriComponentsBuilder fromUriString(String uri) {
        Assert.notNull((Object)uri, "URI must not be null");
        Matcher matcher = URI_PATTERN.matcher(uri);
        if (matcher.matches()) {
            String rest;
            UriComponentsBuilder builder = new UriComponentsBuilder();
            String scheme = matcher.group(2);
            String userInfo = matcher.group(5);
            String host = matcher.group(6);
            String port = matcher.group(8);
            String path = matcher.group(9);
            String query = matcher.group(11);
            String fragment = matcher.group(13);
            boolean opaque = false;
            if (StringUtils.hasLength(scheme) && !(rest = uri.substring(scheme.length())).startsWith(":/")) {
                opaque = true;
            }
            builder.scheme(scheme);
            if (opaque) {
                String ssp = uri.substring(scheme.length() + 1);
                if (StringUtils.hasLength(fragment)) {
                    ssp = ssp.substring(0, ssp.length() - (fragment.length() + 1));
                }
                builder.schemeSpecificPart(ssp);
            } else {
                if (StringUtils.hasLength(scheme) && scheme.startsWith("http") && !StringUtils.hasLength(host)) {
                    throw new IllegalArgumentException("[" + uri + "] is not a valid HTTP URL");
                }
                builder.userInfo(userInfo);
                builder.host(host);
                if (StringUtils.hasLength(port)) {
                    builder.port(port);
                }
                builder.path(path);
                builder.query(query);
            }
            if (StringUtils.hasText(fragment)) {
                builder.fragment(fragment);
            }
            return builder;
        }
        throw new IllegalArgumentException("[" + uri + "] is not a valid URI");
    }

    public static UriComponentsBuilder fromHttpUrl(String httpUrl) {
        Assert.notNull((Object)httpUrl, "HTTP URL must not be null");
        Matcher matcher = HTTP_URL_PATTERN.matcher(httpUrl);
        if (matcher.matches()) {
            UriComponentsBuilder builder = new UriComponentsBuilder();
            String scheme = matcher.group(1);
            builder.scheme(scheme != null ? scheme.toLowerCase() : null);
            builder.userInfo(matcher.group(4));
            String host = matcher.group(5);
            if (StringUtils.hasLength(scheme) && !StringUtils.hasLength(host)) {
                throw new IllegalArgumentException("[" + httpUrl + "] is not a valid HTTP URL");
            }
            builder.host(host);
            String port = matcher.group(7);
            if (StringUtils.hasLength(port)) {
                builder.port(port);
            }
            builder.path(matcher.group(8));
            builder.query(matcher.group(10));
            String fragment = matcher.group(12);
            if (StringUtils.hasText(fragment)) {
                builder.fragment(fragment);
            }
            return builder;
        }
        throw new IllegalArgumentException("[" + httpUrl + "] is not a valid HTTP URL");
    }

    public static UriComponentsBuilder fromHttpRequest(HttpRequest request) {
        return UriComponentsBuilder.fromUri(request.getURI()).adaptFromForwardedHeaders(request.getHeaders());
    }

    @Nullable
    public static InetSocketAddress parseForwardedFor(HttpRequest request, @Nullable InetSocketAddress remoteAddress) {
        String forwardedToUse;
        Matcher matcher;
        int port = remoteAddress != null ? remoteAddress.getPort() : ("https".equals(request.getURI().getScheme()) ? 443 : 80);
        String forwardedHeader = request.getHeaders().getFirst("Forwarded");
        if (StringUtils.hasText(forwardedHeader) && (matcher = FORWARDED_FOR_PATTERN.matcher(forwardedToUse = StringUtils.tokenizeToStringArray(forwardedHeader, ",")[0])).find()) {
            int squareBracketIdx;
            String value;
            String host = value = matcher.group(1).trim();
            int portSeparatorIdx = value.lastIndexOf(58);
            if (portSeparatorIdx > (squareBracketIdx = value.lastIndexOf(93))) {
                if (squareBracketIdx == -1 && value.indexOf(58) != portSeparatorIdx) {
                    throw new IllegalArgumentException("Invalid IPv4 address: " + value);
                }
                host = value.substring(0, portSeparatorIdx);
                try {
                    port = Integer.parseInt(value.substring(portSeparatorIdx + 1));
                }
                catch (NumberFormatException ex) {
                    throw new IllegalArgumentException("Failed to parse a port from \"forwarded\"-type header value: " + value);
                }
            }
            return InetSocketAddress.createUnresolved(host, port);
        }
        String forHeader = request.getHeaders().getFirst("X-Forwarded-For");
        if (StringUtils.hasText(forHeader)) {
            String host = StringUtils.tokenizeToStringArray(forHeader, ",")[0];
            return InetSocketAddress.createUnresolved(host, port);
        }
        return null;
    }

    public static UriComponentsBuilder fromOriginHeader(String origin) {
        Matcher matcher = URI_PATTERN.matcher(origin);
        if (matcher.matches()) {
            UriComponentsBuilder builder = new UriComponentsBuilder();
            String scheme = matcher.group(2);
            String host = matcher.group(6);
            String port = matcher.group(8);
            if (StringUtils.hasLength(scheme)) {
                builder.scheme(scheme);
            }
            builder.host(host);
            if (StringUtils.hasLength(port)) {
                builder.port(port);
            }
            return builder;
        }
        throw new IllegalArgumentException("[" + origin + "] is not a valid \"Origin\" header value");
    }

    public final UriComponentsBuilder encode() {
        return this.encode(StandardCharsets.UTF_8);
    }

    public UriComponentsBuilder encode(Charset charset) {
        this.encodeTemplate = true;
        this.charset = charset;
        return this;
    }

    public UriComponents build() {
        return this.build(false);
    }

    public UriComponents build(boolean encoded) {
        return this.buildInternal(encoded ? EncodingHint.FULLY_ENCODED : (this.encodeTemplate ? EncodingHint.ENCODE_TEMPLATE : EncodingHint.NONE));
    }

    private UriComponents buildInternal(EncodingHint hint) {
        UriComponents result;
        if (this.ssp != null) {
            result = new OpaqueUriComponents(this.scheme, this.ssp, this.fragment);
        } else {
            HierarchicalUriComponents uric = new HierarchicalUriComponents(this.scheme, this.fragment, this.userInfo, this.host, this.port, this.pathBuilder.build(), this.queryParams, hint == EncodingHint.FULLY_ENCODED);
            UriComponents uriComponents = result = hint == EncodingHint.ENCODE_TEMPLATE ? uric.encodeTemplate(this.charset) : uric;
        }
        if (!this.uriVariables.isEmpty()) {
            result = result.expand(name -> this.uriVariables.getOrDefault(name, UriComponents.UriTemplateVariables.SKIP_VALUE));
        }
        return result;
    }

    public UriComponents buildAndExpand(Map<String, ?> uriVariables) {
        return this.build().expand(uriVariables);
    }

    public UriComponents buildAndExpand(Object ... uriVariableValues) {
        return this.build().expand(uriVariableValues);
    }

    @Override
    public URI build(Object ... uriVariables) {
        return this.buildInternal(EncodingHint.ENCODE_TEMPLATE).expand(uriVariables).toUri();
    }

    @Override
    public URI build(Map<String, ?> uriVariables) {
        return this.buildInternal(EncodingHint.ENCODE_TEMPLATE).expand(uriVariables).toUri();
    }

    public String toUriString() {
        return this.uriVariables.isEmpty() ? this.build().encode().toUriString() : this.buildInternal(EncodingHint.ENCODE_TEMPLATE).toUriString();
    }

    public UriComponentsBuilder uri(URI uri) {
        Assert.notNull((Object)uri, "URI must not be null");
        this.scheme = uri.getScheme();
        if (uri.isOpaque()) {
            this.ssp = uri.getRawSchemeSpecificPart();
            this.resetHierarchicalComponents();
        } else {
            if (uri.getRawUserInfo() != null) {
                this.userInfo = uri.getRawUserInfo();
            }
            if (uri.getHost() != null) {
                this.host = uri.getHost();
            }
            if (uri.getPort() != -1) {
                this.port = String.valueOf(uri.getPort());
            }
            if (StringUtils.hasLength(uri.getRawPath())) {
                this.pathBuilder = new CompositePathComponentBuilder();
                this.pathBuilder.addPath(uri.getRawPath());
            }
            if (StringUtils.hasLength(uri.getRawQuery())) {
                this.queryParams.clear();
                this.query(uri.getRawQuery());
            }
            this.resetSchemeSpecificPart();
        }
        if (uri.getRawFragment() != null) {
            this.fragment = uri.getRawFragment();
        }
        return this;
    }

    public UriComponentsBuilder uriComponents(UriComponents uriComponents) {
        Assert.notNull((Object)uriComponents, "UriComponents must not be null");
        uriComponents.copyToUriComponentsBuilder(this);
        return this;
    }

    @Override
    public UriComponentsBuilder scheme(@Nullable String scheme) {
        this.scheme = scheme;
        return this;
    }

    public UriComponentsBuilder schemeSpecificPart(String ssp) {
        this.ssp = ssp;
        this.resetHierarchicalComponents();
        return this;
    }

    @Override
    public UriComponentsBuilder userInfo(@Nullable String userInfo) {
        this.userInfo = userInfo;
        this.resetSchemeSpecificPart();
        return this;
    }

    @Override
    public UriComponentsBuilder host(@Nullable String host) {
        this.host = host;
        if (host != null) {
            this.resetSchemeSpecificPart();
        }
        return this;
    }

    @Override
    public UriComponentsBuilder port(int port) {
        Assert.isTrue(port >= -1, "Port must be >= -1");
        this.port = String.valueOf(port);
        if (port > -1) {
            this.resetSchemeSpecificPart();
        }
        return this;
    }

    @Override
    public UriComponentsBuilder port(@Nullable String port) {
        this.port = port;
        if (port != null) {
            this.resetSchemeSpecificPart();
        }
        return this;
    }

    @Override
    public UriComponentsBuilder path(String path) {
        this.pathBuilder.addPath(path);
        this.resetSchemeSpecificPart();
        return this;
    }

    @Override
    public UriComponentsBuilder pathSegment(String ... pathSegments) throws IllegalArgumentException {
        this.pathBuilder.addPathSegments(pathSegments);
        this.resetSchemeSpecificPart();
        return this;
    }

    @Override
    public UriComponentsBuilder replacePath(@Nullable String path) {
        this.pathBuilder = new CompositePathComponentBuilder();
        if (path != null) {
            this.pathBuilder.addPath(path);
        }
        this.resetSchemeSpecificPart();
        return this;
    }

    @Override
    public UriComponentsBuilder query(@Nullable String query) {
        if (query != null) {
            Matcher matcher = QUERY_PARAM_PATTERN.matcher(query);
            while (matcher.find()) {
                String name = matcher.group(1);
                String eq = matcher.group(2);
                String value = matcher.group(3);
                this.queryParam(name, value != null ? value : (StringUtils.hasLength(eq) ? "" : null));
            }
            this.resetSchemeSpecificPart();
        } else {
            this.queryParams.clear();
        }
        return this;
    }

    @Override
    public UriComponentsBuilder replaceQuery(@Nullable String query) {
        this.queryParams.clear();
        if (query != null) {
            this.query(query);
            this.resetSchemeSpecificPart();
        }
        return this;
    }

    @Override
    public UriComponentsBuilder queryParam(String name, Object ... values) {
        Assert.notNull((Object)name, "Name must not be null");
        if (!ObjectUtils.isEmpty(values)) {
            for (Object value : values) {
                String valueAsString = this.getQueryParamValue(value);
                this.queryParams.add(name, valueAsString);
            }
        } else {
            this.queryParams.add(name, null);
        }
        this.resetSchemeSpecificPart();
        return this;
    }

    @Nullable
    private String getQueryParamValue(@Nullable Object value) {
        if (value != null) {
            return value instanceof Optional ? (String)((Optional)value).map(Object::toString).orElse(null) : value.toString();
        }
        return null;
    }

    @Override
    public UriComponentsBuilder queryParam(String name, @Nullable Collection<?> values) {
        return this.queryParam(name, CollectionUtils.isEmpty(values) ? EMPTY_VALUES : values.toArray());
    }

    @Override
    public UriComponentsBuilder queryParamIfPresent(String name, Optional<?> value) {
        value.ifPresent(o -> {
            if (o instanceof Collection) {
                this.queryParam(name, (Collection)o);
            } else {
                this.queryParam(name, o);
            }
        });
        return this;
    }

    @Override
    public UriComponentsBuilder queryParams(@Nullable MultiValueMap<String, String> params) {
        if (params != null) {
            this.queryParams.addAll(params);
            this.resetSchemeSpecificPart();
        }
        return this;
    }

    @Override
    public UriComponentsBuilder replaceQueryParam(String name, Object ... values) {
        Assert.notNull((Object)name, "Name must not be null");
        this.queryParams.remove(name);
        if (!ObjectUtils.isEmpty(values)) {
            this.queryParam(name, values);
        }
        this.resetSchemeSpecificPart();
        return this;
    }

    @Override
    public UriComponentsBuilder replaceQueryParam(String name, @Nullable Collection<?> values) {
        return this.replaceQueryParam(name, CollectionUtils.isEmpty(values) ? EMPTY_VALUES : values.toArray());
    }

    @Override
    public UriComponentsBuilder replaceQueryParams(@Nullable MultiValueMap<String, String> params) {
        this.queryParams.clear();
        if (params != null) {
            this.queryParams.putAll(params);
        }
        return this;
    }

    @Override
    public UriComponentsBuilder fragment(@Nullable String fragment) {
        if (fragment != null) {
            Assert.hasLength(fragment, "Fragment must not be empty");
            this.fragment = fragment;
        } else {
            this.fragment = null;
        }
        return this;
    }

    public UriComponentsBuilder uriVariables(Map<String, Object> uriVariables) {
        this.uriVariables.putAll(uriVariables);
        return this;
    }

    UriComponentsBuilder adaptFromForwardedHeaders(HttpHeaders headers) {
        try {
            String forwardedHeader = headers.getFirst("Forwarded");
            if (StringUtils.hasText(forwardedHeader)) {
                Matcher matcher = FORWARDED_PROTO_PATTERN.matcher(forwardedHeader);
                if (matcher.find()) {
                    this.scheme(matcher.group(1).trim());
                    this.port(null);
                } else if (this.isForwardedSslOn(headers)) {
                    this.scheme("https");
                    this.port(null);
                }
                matcher = FORWARDED_HOST_PATTERN.matcher(forwardedHeader);
                if (matcher.find()) {
                    this.adaptForwardedHost(matcher.group(1).trim());
                }
            } else {
                String portHeader;
                String protocolHeader = headers.getFirst("X-Forwarded-Proto");
                if (StringUtils.hasText(protocolHeader)) {
                    this.scheme(StringUtils.tokenizeToStringArray(protocolHeader, ",")[0]);
                    this.port(null);
                } else if (this.isForwardedSslOn(headers)) {
                    this.scheme("https");
                    this.port(null);
                }
                String hostHeader = headers.getFirst("X-Forwarded-Host");
                if (StringUtils.hasText(hostHeader)) {
                    this.adaptForwardedHost(StringUtils.tokenizeToStringArray(hostHeader, ",")[0]);
                }
                if (StringUtils.hasText(portHeader = headers.getFirst("X-Forwarded-Port"))) {
                    this.port(Integer.parseInt(StringUtils.tokenizeToStringArray(portHeader, ",")[0]));
                }
            }
        }
        catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Failed to parse a port from \"forwarded\"-type headers. If not behind a trusted proxy, consider using ForwardedHeaderFilter with the removeOnly=true. Request headers: " + headers);
        }
        if (this.scheme != null && ((this.scheme.equals("http") || this.scheme.equals("ws")) && "80".equals(this.port) || (this.scheme.equals("https") || this.scheme.equals("wss")) && "443".equals(this.port))) {
            this.port(null);
        }
        return this;
    }

    private boolean isForwardedSslOn(HttpHeaders headers) {
        String forwardedSsl = headers.getFirst("X-Forwarded-Ssl");
        return StringUtils.hasText(forwardedSsl) && forwardedSsl.equalsIgnoreCase("on");
    }

    private void adaptForwardedHost(String rawValue) {
        int squareBracketIdx;
        int portSeparatorIdx = rawValue.lastIndexOf(58);
        if (portSeparatorIdx > (squareBracketIdx = rawValue.lastIndexOf(93))) {
            if (squareBracketIdx == -1 && rawValue.indexOf(58) != portSeparatorIdx) {
                throw new IllegalArgumentException("Invalid IPv4 address: " + rawValue);
            }
            this.host(rawValue.substring(0, portSeparatorIdx));
            this.port(Integer.parseInt(rawValue.substring(portSeparatorIdx + 1)));
        } else {
            this.host(rawValue);
            this.port(null);
        }
    }

    private void resetHierarchicalComponents() {
        this.userInfo = null;
        this.host = null;
        this.port = null;
        this.pathBuilder = new CompositePathComponentBuilder();
        this.queryParams.clear();
    }

    private void resetSchemeSpecificPart() {
        this.ssp = null;
    }

    public Object clone() {
        return this.cloneBuilder();
    }

    public UriComponentsBuilder cloneBuilder() {
        return new UriComponentsBuilder(this);
    }

    private static enum EncodingHint {
        ENCODE_TEMPLATE,
        FULLY_ENCODED,
        NONE;

    }

    private static class PathSegmentComponentBuilder
    implements PathComponentBuilder {
        private final List<String> pathSegments = new ArrayList<String>();

        private PathSegmentComponentBuilder() {
        }

        public void append(String ... pathSegments) {
            for (String pathSegment : pathSegments) {
                if (!StringUtils.hasText(pathSegment)) continue;
                this.pathSegments.add(pathSegment);
            }
        }

        @Override
        public HierarchicalUriComponents.PathComponent build() {
            return this.pathSegments.isEmpty() ? null : new HierarchicalUriComponents.PathSegmentComponent(this.pathSegments);
        }

        @Override
        public PathSegmentComponentBuilder cloneBuilder() {
            PathSegmentComponentBuilder builder = new PathSegmentComponentBuilder();
            builder.pathSegments.addAll(this.pathSegments);
            return builder;
        }
    }

    private static class FullPathComponentBuilder
    implements PathComponentBuilder {
        private final StringBuilder path = new StringBuilder();

        private FullPathComponentBuilder() {
        }

        public void append(String path) {
            this.path.append(path);
        }

        @Override
        public HierarchicalUriComponents.PathComponent build() {
            if (this.path.length() == 0) {
                return null;
            }
            String sanitized = FullPathComponentBuilder.getSanitizedPath(this.path);
            return new HierarchicalUriComponents.FullPathComponent(sanitized);
        }

        private static String getSanitizedPath(StringBuilder path) {
            int index = path.indexOf("//");
            if (index >= 0) {
                StringBuilder sanitized = new StringBuilder(path);
                while (index != -1) {
                    sanitized.deleteCharAt(index);
                    index = sanitized.indexOf("//", index);
                }
                return sanitized.toString();
            }
            return path.toString();
        }

        public void removeTrailingSlash() {
            int index = this.path.length() - 1;
            if (this.path.charAt(index) == '/') {
                this.path.deleteCharAt(index);
            }
        }

        @Override
        public FullPathComponentBuilder cloneBuilder() {
            FullPathComponentBuilder builder = new FullPathComponentBuilder();
            builder.append(this.path.toString());
            return builder;
        }
    }

    private static class CompositePathComponentBuilder
    implements PathComponentBuilder {
        private final Deque<PathComponentBuilder> builders = new ArrayDeque<PathComponentBuilder>();

        private CompositePathComponentBuilder() {
        }

        public void addPathSegments(String ... pathSegments) {
            if (!ObjectUtils.isEmpty(pathSegments)) {
                PathSegmentComponentBuilder psBuilder = this.getLastBuilder(PathSegmentComponentBuilder.class);
                FullPathComponentBuilder fpBuilder = this.getLastBuilder(FullPathComponentBuilder.class);
                if (psBuilder == null) {
                    psBuilder = new PathSegmentComponentBuilder();
                    this.builders.add(psBuilder);
                    if (fpBuilder != null) {
                        fpBuilder.removeTrailingSlash();
                    }
                }
                psBuilder.append(pathSegments);
            }
        }

        public void addPath(String path) {
            if (StringUtils.hasText(path)) {
                PathSegmentComponentBuilder psBuilder = this.getLastBuilder(PathSegmentComponentBuilder.class);
                FullPathComponentBuilder fpBuilder = this.getLastBuilder(FullPathComponentBuilder.class);
                if (psBuilder != null) {
                    String string = path = path.startsWith("/") ? path : "/" + path;
                }
                if (fpBuilder == null) {
                    fpBuilder = new FullPathComponentBuilder();
                    this.builders.add(fpBuilder);
                }
                fpBuilder.append(path);
            }
        }

        @Nullable
        private <T> T getLastBuilder(Class<T> builderClass) {
            PathComponentBuilder last;
            if (!this.builders.isEmpty() && builderClass.isInstance(last = this.builders.getLast())) {
                return (T)last;
            }
            return null;
        }

        @Override
        public HierarchicalUriComponents.PathComponent build() {
            int size = this.builders.size();
            ArrayList<HierarchicalUriComponents.PathComponent> components = new ArrayList<HierarchicalUriComponents.PathComponent>(size);
            for (PathComponentBuilder componentBuilder : this.builders) {
                HierarchicalUriComponents.PathComponent pathComponent = componentBuilder.build();
                if (pathComponent == null) continue;
                components.add(pathComponent);
            }
            if (components.isEmpty()) {
                return HierarchicalUriComponents.NULL_PATH_COMPONENT;
            }
            if (components.size() == 1) {
                return (HierarchicalUriComponents.PathComponent)components.get(0);
            }
            return new HierarchicalUriComponents.PathComponentComposite(components);
        }

        @Override
        public CompositePathComponentBuilder cloneBuilder() {
            CompositePathComponentBuilder compositeBuilder = new CompositePathComponentBuilder();
            for (PathComponentBuilder builder : this.builders) {
                compositeBuilder.builders.add(builder.cloneBuilder());
            }
            return compositeBuilder;
        }
    }

    private static interface PathComponentBuilder {
        @Nullable
        public HierarchicalUriComponents.PathComponent build();

        public PathComponentBuilder cloneBuilder();
    }
}

