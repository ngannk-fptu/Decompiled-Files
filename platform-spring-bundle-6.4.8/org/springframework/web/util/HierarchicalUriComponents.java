/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.util;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

final class HierarchicalUriComponents
extends UriComponents {
    private static final char PATH_DELIMITER = '/';
    private static final String PATH_DELIMITER_STRING = String.valueOf('/');
    private static final MultiValueMap<String, String> EMPTY_QUERY_PARAMS = CollectionUtils.unmodifiableMultiValueMap(new LinkedMultiValueMap());
    static final PathComponent NULL_PATH_COMPONENT = new PathComponent(){

        @Override
        public String getPath() {
            return "";
        }

        @Override
        public List<String> getPathSegments() {
            return Collections.emptyList();
        }

        @Override
        public PathComponent encode(BiFunction<String, Type, String> encoder) {
            return this;
        }

        @Override
        public void verify() {
        }

        @Override
        public PathComponent expand(UriComponents.UriTemplateVariables uriVariables, @Nullable UnaryOperator<String> encoder) {
            return this;
        }

        @Override
        public void copyToUriComponentsBuilder(UriComponentsBuilder builder) {
        }

        public boolean equals(@Nullable Object other) {
            return this == other;
        }

        public int hashCode() {
            return this.getClass().hashCode();
        }
    };
    @Nullable
    private final String userInfo;
    @Nullable
    private final String host;
    @Nullable
    private final String port;
    private final PathComponent path;
    private final MultiValueMap<String, String> queryParams;
    private final EncodeState encodeState;
    @Nullable
    private UnaryOperator<String> variableEncoder;

    HierarchicalUriComponents(@Nullable String scheme, @Nullable String fragment, @Nullable String userInfo, @Nullable String host, @Nullable String port, @Nullable PathComponent path, @Nullable MultiValueMap<String, String> query, boolean encoded) {
        super(scheme, fragment);
        this.userInfo = userInfo;
        this.host = host;
        this.port = port;
        this.path = path != null ? path : NULL_PATH_COMPONENT;
        this.queryParams = query != null ? CollectionUtils.unmodifiableMultiValueMap(query) : EMPTY_QUERY_PARAMS;
        EncodeState encodeState = this.encodeState = encoded ? EncodeState.FULLY_ENCODED : EncodeState.RAW;
        if (encoded) {
            this.verify();
        }
    }

    private HierarchicalUriComponents(@Nullable String scheme, @Nullable String fragment, @Nullable String userInfo, @Nullable String host, @Nullable String port, PathComponent path, MultiValueMap<String, String> queryParams, EncodeState encodeState, @Nullable UnaryOperator<String> variableEncoder) {
        super(scheme, fragment);
        this.userInfo = userInfo;
        this.host = host;
        this.port = port;
        this.path = path;
        this.queryParams = queryParams;
        this.encodeState = encodeState;
        this.variableEncoder = variableEncoder;
    }

    @Override
    @Nullable
    public String getSchemeSpecificPart() {
        return null;
    }

    @Override
    @Nullable
    public String getUserInfo() {
        return this.userInfo;
    }

    @Override
    @Nullable
    public String getHost() {
        return this.host;
    }

    @Override
    public int getPort() {
        if (this.port == null) {
            return -1;
        }
        if (this.port.contains("{")) {
            throw new IllegalStateException("The port contains a URI variable but has not been expanded yet: " + this.port);
        }
        try {
            return Integer.parseInt(this.port);
        }
        catch (NumberFormatException ex) {
            throw new IllegalStateException("The port must be an integer: " + this.port);
        }
    }

    @Override
    @NonNull
    public String getPath() {
        return this.path.getPath();
    }

    @Override
    public List<String> getPathSegments() {
        return this.path.getPathSegments();
    }

    @Override
    @Nullable
    public String getQuery() {
        if (!this.queryParams.isEmpty()) {
            StringBuilder queryBuilder = new StringBuilder();
            this.queryParams.forEach((name, values) -> {
                if (CollectionUtils.isEmpty(values)) {
                    if (queryBuilder.length() != 0) {
                        queryBuilder.append('&');
                    }
                    queryBuilder.append((String)name);
                } else {
                    for (Object value : values) {
                        if (queryBuilder.length() != 0) {
                            queryBuilder.append('&');
                        }
                        queryBuilder.append((String)name);
                        if (value == null) continue;
                        queryBuilder.append('=').append(value.toString());
                    }
                }
            });
            return queryBuilder.toString();
        }
        return null;
    }

    @Override
    public MultiValueMap<String, String> getQueryParams() {
        return this.queryParams;
    }

    HierarchicalUriComponents encodeTemplate(Charset charset) {
        if (this.encodeState.isEncoded()) {
            return this;
        }
        this.variableEncoder = value -> HierarchicalUriComponents.encodeUriComponent(value, charset, Type.URI);
        UriTemplateEncoder encoder = new UriTemplateEncoder(charset);
        String schemeTo = this.getScheme() != null ? encoder.apply(this.getScheme(), Type.SCHEME) : null;
        String fragmentTo = this.getFragment() != null ? encoder.apply(this.getFragment(), Type.FRAGMENT) : null;
        String userInfoTo = this.getUserInfo() != null ? encoder.apply(this.getUserInfo(), Type.USER_INFO) : null;
        String hostTo = this.getHost() != null ? encoder.apply(this.getHost(), this.getHostType()) : null;
        PathComponent pathTo = this.path.encode(encoder);
        MultiValueMap<String, String> queryParamsTo = this.encodeQueryParams(encoder);
        return new HierarchicalUriComponents(schemeTo, fragmentTo, userInfoTo, hostTo, this.port, pathTo, queryParamsTo, EncodeState.TEMPLATE_ENCODED, this.variableEncoder);
    }

    @Override
    public HierarchicalUriComponents encode(Charset charset) {
        if (this.encodeState.isEncoded()) {
            return this;
        }
        String scheme = this.getScheme();
        String fragment = this.getFragment();
        String schemeTo = scheme != null ? HierarchicalUriComponents.encodeUriComponent(scheme, charset, Type.SCHEME) : null;
        String fragmentTo = fragment != null ? HierarchicalUriComponents.encodeUriComponent(fragment, charset, Type.FRAGMENT) : null;
        String userInfoTo = this.userInfo != null ? HierarchicalUriComponents.encodeUriComponent(this.userInfo, charset, Type.USER_INFO) : null;
        String hostTo = this.host != null ? HierarchicalUriComponents.encodeUriComponent(this.host, charset, this.getHostType()) : null;
        BiFunction<String, Type, String> encoder = (s, type) -> HierarchicalUriComponents.encodeUriComponent(s, charset, type);
        PathComponent pathTo = this.path.encode(encoder);
        MultiValueMap<String, String> queryParamsTo = this.encodeQueryParams(encoder);
        return new HierarchicalUriComponents(schemeTo, fragmentTo, userInfoTo, hostTo, this.port, pathTo, queryParamsTo, EncodeState.FULLY_ENCODED, null);
    }

    private MultiValueMap<String, String> encodeQueryParams(BiFunction<String, Type, String> encoder) {
        int size = this.queryParams.size();
        LinkedMultiValueMap result = new LinkedMultiValueMap(size);
        this.queryParams.forEach((key, values) -> {
            String name = (String)encoder.apply((String)key, Type.QUERY_PARAM);
            ArrayList<String> encodedValues = new ArrayList<String>(values.size());
            for (String value : values) {
                encodedValues.add(value != null ? (String)encoder.apply(value, Type.QUERY_PARAM) : null);
            }
            result.put(name, encodedValues);
        });
        return CollectionUtils.unmodifiableMultiValueMap(result);
    }

    static String encodeUriComponent(String source, String encoding, Type type) {
        return HierarchicalUriComponents.encodeUriComponent(source, Charset.forName(encoding), type);
    }

    static String encodeUriComponent(String source, Charset charset, Type type) {
        if (!StringUtils.hasLength(source)) {
            return source;
        }
        Assert.notNull((Object)charset, "Charset must not be null");
        Assert.notNull((Object)type, "Type must not be null");
        byte[] bytes = source.getBytes(charset);
        boolean original = true;
        for (byte b : bytes) {
            if (type.isAllowed(b)) continue;
            original = false;
            break;
        }
        if (original) {
            return source;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length);
        for (byte b : bytes) {
            if (type.isAllowed(b)) {
                baos.write(b);
                continue;
            }
            baos.write(37);
            char hex1 = Character.toUpperCase(Character.forDigit(b >> 4 & 0xF, 16));
            char hex2 = Character.toUpperCase(Character.forDigit(b & 0xF, 16));
            baos.write(hex1);
            baos.write(hex2);
        }
        return StreamUtils.copyToString(baos, charset);
    }

    private Type getHostType() {
        return this.host != null && this.host.startsWith("[") ? Type.HOST_IPV6 : Type.HOST_IPV4;
    }

    private void verify() {
        HierarchicalUriComponents.verifyUriComponent(this.getScheme(), Type.SCHEME);
        HierarchicalUriComponents.verifyUriComponent(this.userInfo, Type.USER_INFO);
        HierarchicalUriComponents.verifyUriComponent(this.host, this.getHostType());
        this.path.verify();
        this.queryParams.forEach((key, values) -> {
            HierarchicalUriComponents.verifyUriComponent(key, Type.QUERY_PARAM);
            for (String value : values) {
                HierarchicalUriComponents.verifyUriComponent(value, Type.QUERY_PARAM);
            }
        });
        HierarchicalUriComponents.verifyUriComponent(this.getFragment(), Type.FRAGMENT);
    }

    private static void verifyUriComponent(@Nullable String source, Type type) {
        if (source == null) {
            return;
        }
        int length = source.length();
        for (int i2 = 0; i2 < length; ++i2) {
            char ch = source.charAt(i2);
            if (ch == '%') {
                if (i2 + 2 < length) {
                    char hex1 = source.charAt(i2 + 1);
                    char hex2 = source.charAt(i2 + 2);
                    int u = Character.digit(hex1, 16);
                    int l = Character.digit(hex2, 16);
                    if (u == -1 || l == -1) {
                        throw new IllegalArgumentException("Invalid encoded sequence \"" + source.substring(i2) + "\"");
                    }
                    i2 += 2;
                    continue;
                }
                throw new IllegalArgumentException("Invalid encoded sequence \"" + source.substring(i2) + "\"");
            }
            if (type.isAllowed(ch)) continue;
            throw new IllegalArgumentException("Invalid character '" + ch + "' for " + type.name() + " in \"" + source + "\"");
        }
    }

    @Override
    protected HierarchicalUriComponents expandInternal(UriComponents.UriTemplateVariables uriVariables) {
        Assert.state(!this.encodeState.equals((Object)EncodeState.FULLY_ENCODED), "URI components already encoded, and could not possibly contain '{' or '}'.");
        String schemeTo = HierarchicalUriComponents.expandUriComponent(this.getScheme(), uriVariables, this.variableEncoder);
        String userInfoTo = HierarchicalUriComponents.expandUriComponent(this.userInfo, uriVariables, this.variableEncoder);
        String hostTo = HierarchicalUriComponents.expandUriComponent(this.host, uriVariables, this.variableEncoder);
        String portTo = HierarchicalUriComponents.expandUriComponent(this.port, uriVariables, this.variableEncoder);
        PathComponent pathTo = this.path.expand(uriVariables, this.variableEncoder);
        MultiValueMap<String, String> queryParamsTo = this.expandQueryParams(uriVariables);
        String fragmentTo = HierarchicalUriComponents.expandUriComponent(this.getFragment(), uriVariables, this.variableEncoder);
        return new HierarchicalUriComponents(schemeTo, fragmentTo, userInfoTo, hostTo, portTo, pathTo, queryParamsTo, this.encodeState, this.variableEncoder);
    }

    private MultiValueMap<String, String> expandQueryParams(UriComponents.UriTemplateVariables variables) {
        int size = this.queryParams.size();
        LinkedMultiValueMap result = new LinkedMultiValueMap(size);
        QueryUriTemplateVariables queryVariables = new QueryUriTemplateVariables(variables);
        this.queryParams.forEach((key, values) -> {
            String name = HierarchicalUriComponents.expandUriComponent(key, queryVariables, this.variableEncoder);
            ArrayList<String> expandedValues = new ArrayList<String>(values.size());
            for (String value : values) {
                expandedValues.add(HierarchicalUriComponents.expandUriComponent(value, queryVariables, this.variableEncoder));
            }
            result.put(name, expandedValues);
        });
        return CollectionUtils.unmodifiableMultiValueMap(result);
    }

    @Override
    public UriComponents normalize() {
        String normalizedPath = StringUtils.cleanPath(this.getPath());
        FullPathComponent path = new FullPathComponent(normalizedPath);
        return new HierarchicalUriComponents(this.getScheme(), this.getFragment(), this.userInfo, this.host, this.port, path, this.queryParams, this.encodeState, this.variableEncoder);
    }

    @Override
    public String toUriString() {
        String query;
        String path;
        StringBuilder uriBuilder = new StringBuilder();
        if (this.getScheme() != null) {
            uriBuilder.append(this.getScheme()).append(':');
        }
        if (this.userInfo != null || this.host != null) {
            uriBuilder.append("//");
            if (this.userInfo != null) {
                uriBuilder.append(this.userInfo).append('@');
            }
            if (this.host != null) {
                uriBuilder.append(this.host);
            }
            if (this.getPort() != -1) {
                uriBuilder.append(':').append(this.port);
            }
        }
        if (StringUtils.hasLength(path = this.getPath())) {
            if (uriBuilder.length() != 0 && path.charAt(0) != '/') {
                uriBuilder.append('/');
            }
            uriBuilder.append(path);
        }
        if ((query = this.getQuery()) != null) {
            uriBuilder.append('?').append(query);
        }
        if (this.getFragment() != null) {
            uriBuilder.append('#').append(this.getFragment());
        }
        return uriBuilder.toString();
    }

    @Override
    public URI toUri() {
        try {
            if (this.encodeState.isEncoded()) {
                return new URI(this.toUriString());
            }
            String path = this.getPath();
            if (StringUtils.hasLength(path) && path.charAt(0) != '/' && (this.getScheme() != null || this.getUserInfo() != null || this.getHost() != null || this.getPort() != -1)) {
                path = '/' + path;
            }
            return new URI(this.getScheme(), this.getUserInfo(), this.getHost(), this.getPort(), path, this.getQuery(), this.getFragment());
        }
        catch (URISyntaxException ex) {
            throw new IllegalStateException("Could not create URI object: " + ex.getMessage(), ex);
        }
    }

    @Override
    protected void copyToUriComponentsBuilder(UriComponentsBuilder builder) {
        if (this.getScheme() != null) {
            builder.scheme(this.getScheme());
        }
        if (this.getUserInfo() != null) {
            builder.userInfo(this.getUserInfo());
        }
        if (this.getHost() != null) {
            builder.host(this.getHost());
        }
        if (this.port != null) {
            builder.port(this.port);
        }
        this.path.copyToUriComponentsBuilder(builder);
        if (!this.getQueryParams().isEmpty()) {
            builder.queryParams((MultiValueMap)this.getQueryParams());
        }
        if (this.getFragment() != null) {
            builder.fragment(this.getFragment());
        }
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof HierarchicalUriComponents)) {
            return false;
        }
        HierarchicalUriComponents otherComp = (HierarchicalUriComponents)other;
        return ObjectUtils.nullSafeEquals(this.getScheme(), otherComp.getScheme()) && ObjectUtils.nullSafeEquals(this.getUserInfo(), otherComp.getUserInfo()) && ObjectUtils.nullSafeEquals(this.getHost(), otherComp.getHost()) && this.getPort() == otherComp.getPort() && this.path.equals(otherComp.path) && this.queryParams.equals(otherComp.queryParams) && ObjectUtils.nullSafeEquals(this.getFragment(), otherComp.getFragment());
    }

    public int hashCode() {
        int result = ObjectUtils.nullSafeHashCode(this.getScheme());
        result = 31 * result + ObjectUtils.nullSafeHashCode(this.userInfo);
        result = 31 * result + ObjectUtils.nullSafeHashCode(this.host);
        result = 31 * result + ObjectUtils.nullSafeHashCode(this.port);
        result = 31 * result + this.path.hashCode();
        result = 31 * result + this.queryParams.hashCode();
        result = 31 * result + ObjectUtils.nullSafeHashCode(this.getFragment());
        return result;
    }

    private static class QueryUriTemplateVariables
    implements UriComponents.UriTemplateVariables {
        private final UriComponents.UriTemplateVariables delegate;

        public QueryUriTemplateVariables(UriComponents.UriTemplateVariables delegate) {
            this.delegate = delegate;
        }

        @Override
        public Object getValue(@Nullable String name) {
            Object value = this.delegate.getValue(name);
            if (ObjectUtils.isArray(value)) {
                value = StringUtils.arrayToCommaDelimitedString(ObjectUtils.toObjectArray(value));
            }
            return value;
        }
    }

    static final class PathComponentComposite
    implements PathComponent {
        private final List<PathComponent> pathComponents;

        public PathComponentComposite(List<PathComponent> pathComponents) {
            Assert.notNull(pathComponents, "PathComponent List must not be null");
            this.pathComponents = pathComponents;
        }

        @Override
        public String getPath() {
            StringBuilder pathBuilder = new StringBuilder();
            for (PathComponent pathComponent : this.pathComponents) {
                pathBuilder.append(pathComponent.getPath());
            }
            return pathBuilder.toString();
        }

        @Override
        public List<String> getPathSegments() {
            ArrayList<String> result = new ArrayList<String>();
            for (PathComponent pathComponent : this.pathComponents) {
                result.addAll(pathComponent.getPathSegments());
            }
            return result;
        }

        @Override
        public PathComponent encode(BiFunction<String, Type, String> encoder) {
            ArrayList<PathComponent> encodedComponents = new ArrayList<PathComponent>(this.pathComponents.size());
            for (PathComponent pathComponent : this.pathComponents) {
                encodedComponents.add(pathComponent.encode(encoder));
            }
            return new PathComponentComposite(encodedComponents);
        }

        @Override
        public void verify() {
            for (PathComponent pathComponent : this.pathComponents) {
                pathComponent.verify();
            }
        }

        @Override
        public PathComponent expand(UriComponents.UriTemplateVariables uriVariables, @Nullable UnaryOperator<String> encoder) {
            ArrayList<PathComponent> expandedComponents = new ArrayList<PathComponent>(this.pathComponents.size());
            for (PathComponent pathComponent : this.pathComponents) {
                expandedComponents.add(pathComponent.expand(uriVariables, encoder));
            }
            return new PathComponentComposite(expandedComponents);
        }

        @Override
        public void copyToUriComponentsBuilder(UriComponentsBuilder builder) {
            for (PathComponent pathComponent : this.pathComponents) {
                pathComponent.copyToUriComponentsBuilder(builder);
            }
        }
    }

    static final class PathSegmentComponent
    implements PathComponent {
        private final List<String> pathSegments;

        public PathSegmentComponent(List<String> pathSegments) {
            Assert.notNull(pathSegments, "List must not be null");
            this.pathSegments = Collections.unmodifiableList(new ArrayList<String>(pathSegments));
        }

        @Override
        public String getPath() {
            String delimiter = PATH_DELIMITER_STRING;
            StringJoiner pathBuilder = new StringJoiner(delimiter, delimiter, "");
            for (String pathSegment : this.pathSegments) {
                pathBuilder.add(pathSegment);
            }
            return pathBuilder.toString();
        }

        @Override
        public List<String> getPathSegments() {
            return this.pathSegments;
        }

        @Override
        public PathComponent encode(BiFunction<String, Type, String> encoder) {
            List<String> pathSegments = this.getPathSegments();
            ArrayList<String> encodedPathSegments = new ArrayList<String>(pathSegments.size());
            for (String pathSegment : pathSegments) {
                String encodedPathSegment = encoder.apply(pathSegment, Type.PATH_SEGMENT);
                encodedPathSegments.add(encodedPathSegment);
            }
            return new PathSegmentComponent(encodedPathSegments);
        }

        @Override
        public void verify() {
            for (String pathSegment : this.getPathSegments()) {
                HierarchicalUriComponents.verifyUriComponent(pathSegment, Type.PATH_SEGMENT);
            }
        }

        @Override
        public PathComponent expand(UriComponents.UriTemplateVariables uriVariables, @Nullable UnaryOperator<String> encoder) {
            List<String> pathSegments = this.getPathSegments();
            ArrayList<String> expandedPathSegments = new ArrayList<String>(pathSegments.size());
            for (String pathSegment : pathSegments) {
                String expandedPathSegment = UriComponents.expandUriComponent(pathSegment, uriVariables, encoder);
                expandedPathSegments.add(expandedPathSegment);
            }
            return new PathSegmentComponent(expandedPathSegments);
        }

        @Override
        public void copyToUriComponentsBuilder(UriComponentsBuilder builder) {
            builder.pathSegment(StringUtils.toStringArray(this.getPathSegments()));
        }

        public boolean equals(@Nullable Object other) {
            return this == other || other instanceof PathSegmentComponent && this.getPathSegments().equals(((PathSegmentComponent)other).getPathSegments());
        }

        public int hashCode() {
            return this.getPathSegments().hashCode();
        }
    }

    static final class FullPathComponent
    implements PathComponent {
        private final String path;

        public FullPathComponent(@Nullable String path) {
            this.path = path != null ? path : "";
        }

        @Override
        public String getPath() {
            return this.path;
        }

        @Override
        public List<String> getPathSegments() {
            String[] segments = StringUtils.tokenizeToStringArray(this.getPath(), PATH_DELIMITER_STRING);
            return Collections.unmodifiableList(Arrays.asList(segments));
        }

        @Override
        public PathComponent encode(BiFunction<String, Type, String> encoder) {
            String encodedPath = encoder.apply(this.getPath(), Type.PATH);
            return new FullPathComponent(encodedPath);
        }

        @Override
        public void verify() {
            HierarchicalUriComponents.verifyUriComponent(this.getPath(), Type.PATH);
        }

        @Override
        public PathComponent expand(UriComponents.UriTemplateVariables uriVariables, @Nullable UnaryOperator<String> encoder) {
            String expandedPath = UriComponents.expandUriComponent(this.getPath(), uriVariables, encoder);
            return new FullPathComponent(expandedPath);
        }

        @Override
        public void copyToUriComponentsBuilder(UriComponentsBuilder builder) {
            builder.path(this.getPath());
        }

        public boolean equals(@Nullable Object other) {
            return this == other || other instanceof FullPathComponent && this.getPath().equals(((FullPathComponent)other).getPath());
        }

        public int hashCode() {
            return this.getPath().hashCode();
        }
    }

    static interface PathComponent
    extends Serializable {
        public String getPath();

        public List<String> getPathSegments();

        public PathComponent encode(BiFunction<String, Type, String> var1);

        public void verify();

        public PathComponent expand(UriComponents.UriTemplateVariables var1, @Nullable UnaryOperator<String> var2);

        public void copyToUriComponentsBuilder(UriComponentsBuilder var1);
    }

    private static class UriTemplateEncoder
    implements BiFunction<String, Type, String> {
        private final Charset charset;
        private final StringBuilder currentLiteral = new StringBuilder();
        private final StringBuilder currentVariable = new StringBuilder();
        private final StringBuilder output = new StringBuilder();
        private boolean variableWithNameAndRegex;

        public UriTemplateEncoder(Charset charset) {
            this.charset = charset;
        }

        @Override
        public String apply(String source, Type type) {
            if (this.isUriVariable(source)) {
                return source;
            }
            if (source.indexOf(123) == -1) {
                return HierarchicalUriComponents.encodeUriComponent(source, this.charset, type);
            }
            int level = 0;
            this.clear(this.currentLiteral);
            this.clear(this.currentVariable);
            this.clear(this.output);
            for (int i2 = 0; i2 < source.length(); ++i2) {
                char c = source.charAt(i2);
                if (c == ':' && level == 1) {
                    this.variableWithNameAndRegex = true;
                }
                if (c == '{' && ++level == 1) {
                    this.append(this.currentLiteral, true, type);
                }
                if (c == '}' && level > 0) {
                    this.currentVariable.append('}');
                    if (--level == 0) {
                        boolean encode = !this.isUriVariable(this.currentVariable);
                        this.append(this.currentVariable, encode, type);
                        continue;
                    }
                    if (this.variableWithNameAndRegex) continue;
                    this.append(this.currentVariable, true, type);
                    level = 0;
                    continue;
                }
                if (level > 0) {
                    this.currentVariable.append(c);
                    continue;
                }
                this.currentLiteral.append(c);
            }
            if (level > 0) {
                this.currentLiteral.append((CharSequence)this.currentVariable);
            }
            this.append(this.currentLiteral, true, type);
            return this.output.toString();
        }

        private boolean isUriVariable(CharSequence source) {
            if (source.length() < 2 || source.charAt(0) != '{' || source.charAt(source.length() - 1) != '}') {
                return false;
            }
            boolean hasText = false;
            for (int i2 = 1; i2 < source.length() - 1; ++i2) {
                char c = source.charAt(i2);
                if (c == ':' && i2 > 1) {
                    return true;
                }
                if (c == '{' || c == '}') {
                    return false;
                }
                hasText = hasText || !Character.isWhitespace(c);
            }
            return hasText;
        }

        private void append(StringBuilder sb, boolean encode, Type type) {
            this.output.append(encode ? HierarchicalUriComponents.encodeUriComponent(sb.toString(), this.charset, type) : sb);
            this.clear(sb);
            this.variableWithNameAndRegex = false;
        }

        private void clear(StringBuilder sb) {
            sb.delete(0, sb.length());
        }
    }

    private static enum EncodeState {
        RAW,
        FULLY_ENCODED,
        TEMPLATE_ENCODED;


        public boolean isEncoded() {
            return this.equals((Object)FULLY_ENCODED) || this.equals((Object)TEMPLATE_ENCODED);
        }
    }

    static enum Type {
        SCHEME{

            @Override
            public boolean isAllowed(int c) {
                return this.isAlpha(c) || this.isDigit(c) || 43 == c || 45 == c || 46 == c;
            }
        }
        ,
        AUTHORITY{

            @Override
            public boolean isAllowed(int c) {
                return this.isUnreserved(c) || this.isSubDelimiter(c) || 58 == c || 64 == c;
            }
        }
        ,
        USER_INFO{

            @Override
            public boolean isAllowed(int c) {
                return this.isUnreserved(c) || this.isSubDelimiter(c) || 58 == c;
            }
        }
        ,
        HOST_IPV4{

            @Override
            public boolean isAllowed(int c) {
                return this.isUnreserved(c) || this.isSubDelimiter(c);
            }
        }
        ,
        HOST_IPV6{

            @Override
            public boolean isAllowed(int c) {
                return this.isUnreserved(c) || this.isSubDelimiter(c) || 91 == c || 93 == c || 58 == c;
            }
        }
        ,
        PORT{

            @Override
            public boolean isAllowed(int c) {
                return this.isDigit(c);
            }
        }
        ,
        PATH{

            @Override
            public boolean isAllowed(int c) {
                return this.isPchar(c) || 47 == c;
            }
        }
        ,
        PATH_SEGMENT{

            @Override
            public boolean isAllowed(int c) {
                return this.isPchar(c);
            }
        }
        ,
        QUERY{

            @Override
            public boolean isAllowed(int c) {
                return this.isPchar(c) || 47 == c || 63 == c;
            }
        }
        ,
        QUERY_PARAM{

            @Override
            public boolean isAllowed(int c) {
                if (61 == c || 38 == c) {
                    return false;
                }
                return this.isPchar(c) || 47 == c || 63 == c;
            }
        }
        ,
        FRAGMENT{

            @Override
            public boolean isAllowed(int c) {
                return this.isPchar(c) || 47 == c || 63 == c;
            }
        }
        ,
        URI{

            @Override
            public boolean isAllowed(int c) {
                return this.isUnreserved(c);
            }
        };


        public abstract boolean isAllowed(int var1);

        protected boolean isAlpha(int c) {
            return c >= 97 && c <= 122 || c >= 65 && c <= 90;
        }

        protected boolean isDigit(int c) {
            return c >= 48 && c <= 57;
        }

        protected boolean isGenericDelimiter(int c) {
            return 58 == c || 47 == c || 63 == c || 35 == c || 91 == c || 93 == c || 64 == c;
        }

        protected boolean isSubDelimiter(int c) {
            return 33 == c || 36 == c || 38 == c || 39 == c || 40 == c || 41 == c || 42 == c || 43 == c || 44 == c || 59 == c || 61 == c;
        }

        protected boolean isReserved(int c) {
            return this.isGenericDelimiter(c) || this.isSubDelimiter(c);
        }

        protected boolean isUnreserved(int c) {
            return this.isAlpha(c) || this.isDigit(c) || 45 == c || 46 == c || 95 == c || 126 == c;
        }

        protected boolean isPchar(int c) {
            return this.isUnreserved(c) || this.isSubDelimiter(c) || 58 == c || 64 == c;
        }
    }
}

