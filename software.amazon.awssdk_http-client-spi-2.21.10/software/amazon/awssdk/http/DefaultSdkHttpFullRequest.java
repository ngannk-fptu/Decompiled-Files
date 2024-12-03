/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.Immutable
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.CollectionUtils
 *  software.amazon.awssdk.utils.StringUtils
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.Validate
 *  software.amazon.awssdk.utils.http.SdkHttpUtils
 */
package software.amazon.awssdk.http;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.internal.http.LowCopyListMap;
import software.amazon.awssdk.utils.CollectionUtils;
import software.amazon.awssdk.utils.StringUtils;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.http.SdkHttpUtils;

@SdkInternalApi
@Immutable
final class DefaultSdkHttpFullRequest
implements SdkHttpFullRequest {
    private final String protocol;
    private final String host;
    private final Integer port;
    private final String path;
    private final LowCopyListMap.ForBuildable queryParameters;
    private final LowCopyListMap.ForBuildable headers;
    private final SdkHttpMethod httpMethod;
    private final ContentStreamProvider contentStreamProvider;

    private DefaultSdkHttpFullRequest(Builder builder) {
        this.protocol = this.standardizeProtocol(builder.protocol);
        this.host = (String)Validate.paramNotNull((Object)builder.host, (String)"host");
        this.port = this.standardizePort(builder.port);
        this.path = this.standardizePath(builder.path);
        this.httpMethod = (SdkHttpMethod)((Object)Validate.paramNotNull((Object)((Object)builder.httpMethod), (String)"method"));
        this.contentStreamProvider = builder.contentStreamProvider;
        this.queryParameters = builder.queryParameters.forBuildable();
        this.headers = builder.headers.forBuildable();
    }

    private String standardizeProtocol(String protocol) {
        Validate.paramNotNull((Object)protocol, (String)"protocol");
        String standardizedProtocol = StringUtils.lowerCase((String)protocol);
        Validate.isTrue((standardizedProtocol.equals("http") || standardizedProtocol.equals("https") ? 1 : 0) != 0, (String)"Protocol must be 'http' or 'https', but was %s", (Object[])new Object[]{protocol});
        return standardizedProtocol;
    }

    private String standardizePath(String path) {
        if (StringUtils.isEmpty((CharSequence)path)) {
            return "";
        }
        StringBuilder standardizedPath = new StringBuilder();
        if (!path.startsWith("/")) {
            standardizedPath.append('/');
        }
        standardizedPath.append(path);
        return standardizedPath.toString();
    }

    private Integer standardizePort(Integer port) {
        Validate.isTrue((port == null || port >= -1 ? 1 : 0) != 0, (String)"Port must be positive (or null/-1 to indicate no port), but was '%s'", (Object[])new Object[]{port});
        if (port != null && port == -1) {
            return null;
        }
        return port;
    }

    @Override
    public String protocol() {
        return this.protocol;
    }

    @Override
    public String host() {
        return this.host;
    }

    @Override
    public int port() {
        return Optional.ofNullable(this.port).orElseGet(() -> SdkHttpUtils.standardPort((String)this.protocol()));
    }

    @Override
    public Map<String, List<String>> headers() {
        return this.headers.forExternalRead();
    }

    @Override
    public List<String> matchingHeaders(String header) {
        return Collections.unmodifiableList(this.headers.forInternalRead().getOrDefault(header, Collections.emptyList()));
    }

    @Override
    public Optional<String> firstMatchingHeader(String headerName) {
        List<String> headers = this.headers.forInternalRead().get(headerName);
        if (headers == null || headers.isEmpty()) {
            return Optional.empty();
        }
        String header = headers.get(0);
        if (StringUtils.isEmpty((CharSequence)header)) {
            return Optional.empty();
        }
        return Optional.of(header);
    }

    @Override
    public Optional<String> firstMatchingHeader(Collection<String> headersToFind) {
        for (String headerName : headersToFind) {
            Optional<String> header = this.firstMatchingHeader(headerName);
            if (!header.isPresent()) continue;
            return header;
        }
        return Optional.empty();
    }

    @Override
    public void forEachHeader(BiConsumer<? super String, ? super List<String>> consumer) {
        this.headers.forInternalRead().forEach((k, v) -> consumer.accept((String)k, (List<String>)Collections.unmodifiableList(v)));
    }

    @Override
    public void forEachRawQueryParameter(BiConsumer<? super String, ? super List<String>> consumer) {
        this.queryParameters.forInternalRead().forEach((k, v) -> consumer.accept((String)k, (List<String>)Collections.unmodifiableList(v)));
    }

    @Override
    public int numHeaders() {
        return this.headers.forInternalRead().size();
    }

    @Override
    public int numRawQueryParameters() {
        return this.queryParameters.forInternalRead().size();
    }

    @Override
    public Optional<String> encodedQueryParameters() {
        return SdkHttpUtils.encodeAndFlattenQueryParameters(this.queryParameters.forInternalRead());
    }

    @Override
    public Optional<String> encodedQueryParametersAsFormData() {
        return SdkHttpUtils.encodeAndFlattenFormData(this.queryParameters.forInternalRead());
    }

    @Override
    public String encodedPath() {
        return this.path;
    }

    @Override
    public Map<String, List<String>> rawQueryParameters() {
        return this.queryParameters.forExternalRead();
    }

    @Override
    public Optional<String> firstMatchingRawQueryParameter(String key) {
        List<String> values = this.queryParameters.forInternalRead().get(key);
        return values == null ? Optional.empty() : values.stream().findFirst();
    }

    @Override
    public Optional<String> firstMatchingRawQueryParameter(Collection<String> keys) {
        for (String key : keys) {
            Optional<String> result = this.firstMatchingRawQueryParameter(key);
            if (!result.isPresent()) continue;
            return result;
        }
        return Optional.empty();
    }

    @Override
    public List<String> firstMatchingRawQueryParameters(String key) {
        List<String> values = this.queryParameters.forInternalRead().get(key);
        return values == null ? Collections.emptyList() : Collections.unmodifiableList(values);
    }

    @Override
    public SdkHttpMethod method() {
        return this.httpMethod;
    }

    @Override
    public Optional<ContentStreamProvider> contentStreamProvider() {
        return Optional.ofNullable(this.contentStreamProvider);
    }

    @Override
    public SdkHttpFullRequest.Builder toBuilder() {
        return new Builder(this);
    }

    public String toString() {
        return ToString.builder((String)"DefaultSdkHttpFullRequest").add("httpMethod", (Object)this.httpMethod).add("protocol", (Object)this.protocol).add("host", (Object)this.host).add("port", (Object)this.port).add("encodedPath", (Object)this.path).add("headers", this.headers.forInternalRead().keySet()).add("queryParameters", this.queryParameters.forInternalRead().keySet()).build();
    }

    static final class Builder
    implements SdkHttpFullRequest.Builder {
        private String protocol;
        private String host;
        private Integer port;
        private String path;
        private LowCopyListMap.ForBuilder queryParameters;
        private LowCopyListMap.ForBuilder headers;
        private SdkHttpMethod httpMethod;
        private ContentStreamProvider contentStreamProvider;

        Builder() {
            this.queryParameters = LowCopyListMap.emptyQueryParameters();
            this.headers = LowCopyListMap.emptyHeaders();
        }

        Builder(DefaultSdkHttpFullRequest request) {
            this.queryParameters = request.queryParameters.forBuilder();
            this.headers = request.headers.forBuilder();
            this.protocol = request.protocol;
            this.host = request.host;
            this.port = request.port;
            this.path = request.path;
            this.httpMethod = request.httpMethod;
            this.contentStreamProvider = request.contentStreamProvider;
        }

        @Override
        public String protocol() {
            return this.protocol;
        }

        @Override
        public SdkHttpFullRequest.Builder protocol(String protocol) {
            this.protocol = protocol;
            return this;
        }

        @Override
        public String host() {
            return this.host;
        }

        @Override
        public SdkHttpFullRequest.Builder host(String host) {
            this.host = host;
            return this;
        }

        @Override
        public Integer port() {
            return this.port;
        }

        @Override
        public SdkHttpFullRequest.Builder port(Integer port) {
            this.port = port;
            return this;
        }

        @Override
        public Builder encodedPath(String path) {
            this.path = path;
            return this;
        }

        @Override
        public String encodedPath() {
            return this.path;
        }

        @Override
        public Builder putRawQueryParameter(String paramName, List<String> paramValues) {
            this.queryParameters.forInternalWrite().put(paramName, new ArrayList<String>(paramValues));
            return this;
        }

        @Override
        public SdkHttpFullRequest.Builder appendRawQueryParameter(String paramName, String paramValue) {
            this.queryParameters.forInternalWrite().computeIfAbsent(paramName, k -> new ArrayList()).add(paramValue);
            return this;
        }

        @Override
        public Builder rawQueryParameters(Map<String, List<String>> queryParameters) {
            this.queryParameters.setFromExternal(queryParameters);
            return this;
        }

        @Override
        public Builder removeQueryParameter(String paramName) {
            this.queryParameters.forInternalWrite().remove(paramName);
            return this;
        }

        @Override
        public Builder clearQueryParameters() {
            this.queryParameters.forInternalWrite().clear();
            return this;
        }

        @Override
        public Map<String, List<String>> rawQueryParameters() {
            return CollectionUtils.unmodifiableMapOfLists(this.queryParameters.forInternalRead());
        }

        @Override
        public Builder method(SdkHttpMethod httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        @Override
        public SdkHttpMethod method() {
            return this.httpMethod;
        }

        @Override
        public Builder putHeader(String headerName, List<String> headerValues) {
            this.headers.forInternalWrite().put(headerName, new ArrayList<String>(headerValues));
            return this;
        }

        @Override
        public SdkHttpFullRequest.Builder appendHeader(String headerName, String headerValue) {
            this.headers.forInternalWrite().computeIfAbsent(headerName, k -> new ArrayList()).add(headerValue);
            return this;
        }

        @Override
        public Builder headers(Map<String, List<String>> headers) {
            this.headers.setFromExternal(headers);
            return this;
        }

        @Override
        public SdkHttpFullRequest.Builder removeHeader(String headerName) {
            this.headers.forInternalWrite().remove(headerName);
            return this;
        }

        @Override
        public SdkHttpFullRequest.Builder clearHeaders() {
            this.headers.clear();
            return this;
        }

        @Override
        public Map<String, List<String>> headers() {
            return CollectionUtils.unmodifiableMapOfLists(this.headers.forInternalRead());
        }

        @Override
        public List<String> matchingHeaders(String header) {
            return Collections.unmodifiableList(this.headers.forInternalRead().getOrDefault(header, Collections.emptyList()));
        }

        @Override
        public Optional<String> firstMatchingHeader(String headerName) {
            List<String> headers = this.headers.forInternalRead().get(headerName);
            if (headers == null || headers.isEmpty()) {
                return Optional.empty();
            }
            String header = headers.get(0);
            if (StringUtils.isEmpty((CharSequence)header)) {
                return Optional.empty();
            }
            return Optional.of(header);
        }

        @Override
        public Optional<String> firstMatchingHeader(Collection<String> headersToFind) {
            for (String headerName : headersToFind) {
                Optional<String> header = this.firstMatchingHeader(headerName);
                if (!header.isPresent()) continue;
                return header;
            }
            return Optional.empty();
        }

        @Override
        public void forEachHeader(BiConsumer<? super String, ? super List<String>> consumer) {
            this.headers.forInternalRead().forEach((k, v) -> consumer.accept((String)k, (List<String>)Collections.unmodifiableList(v)));
        }

        @Override
        public void forEachRawQueryParameter(BiConsumer<? super String, ? super List<String>> consumer) {
            this.queryParameters.forInternalRead().forEach((k, v) -> consumer.accept((String)k, (List<String>)Collections.unmodifiableList(v)));
        }

        @Override
        public int numHeaders() {
            return this.headers.forInternalRead().size();
        }

        @Override
        public int numRawQueryParameters() {
            return this.queryParameters.forInternalRead().size();
        }

        @Override
        public Optional<String> encodedQueryParameters() {
            return SdkHttpUtils.encodeAndFlattenQueryParameters(this.queryParameters.forInternalRead());
        }

        @Override
        public Builder contentStreamProvider(ContentStreamProvider contentStreamProvider) {
            this.contentStreamProvider = contentStreamProvider;
            return this;
        }

        @Override
        public ContentStreamProvider contentStreamProvider() {
            return this.contentStreamProvider;
        }

        @Override
        public SdkHttpFullRequest.Builder copy() {
            return this.build().toBuilder();
        }

        @Override
        public SdkHttpFullRequest.Builder applyMutation(Consumer<SdkHttpRequest.Builder> mutator) {
            mutator.accept(this);
            return this;
        }

        @Override
        public DefaultSdkHttpFullRequest build() {
            return new DefaultSdkHttpFullRequest(this);
        }
    }
}

