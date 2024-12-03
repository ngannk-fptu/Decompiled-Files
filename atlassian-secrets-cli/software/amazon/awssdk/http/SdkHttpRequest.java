/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.http.DefaultSdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpHeaders;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;
import software.amazon.awssdk.utils.http.SdkHttpUtils;

@SdkProtectedApi
@Immutable
public interface SdkHttpRequest
extends SdkHttpHeaders,
ToCopyableBuilder<Builder, SdkHttpRequest> {
    public static Builder builder() {
        return new DefaultSdkHttpFullRequest.Builder();
    }

    public String protocol();

    public String host();

    public int port();

    public String encodedPath();

    public Map<String, List<String>> rawQueryParameters();

    default public Optional<String> firstMatchingRawQueryParameter(String key) {
        List<String> values = this.rawQueryParameters().get(key);
        return values == null ? Optional.empty() : values.stream().findFirst();
    }

    default public Optional<String> firstMatchingRawQueryParameter(Collection<String> keys) {
        for (String key : keys) {
            Optional<String> result = this.firstMatchingRawQueryParameter(key);
            if (!result.isPresent()) continue;
            return result;
        }
        return Optional.empty();
    }

    default public List<String> firstMatchingRawQueryParameters(String key) {
        List<String> values = this.rawQueryParameters().get(key);
        return values == null ? Collections.emptyList() : values;
    }

    default public void forEachRawQueryParameter(BiConsumer<? super String, ? super List<String>> consumer) {
        this.rawQueryParameters().forEach(consumer);
    }

    default public int numRawQueryParameters() {
        return this.rawQueryParameters().size();
    }

    default public Optional<String> encodedQueryParameters() {
        return SdkHttpUtils.encodeAndFlattenQueryParameters(this.rawQueryParameters());
    }

    default public Optional<String> encodedQueryParametersAsFormData() {
        return SdkHttpUtils.encodeAndFlattenFormData(this.rawQueryParameters());
    }

    default public URI getUri() {
        String encodedQueryString = this.encodedQueryParameters().map(value -> "?" + value).orElse("");
        String portString = SdkHttpUtils.isUsingStandardPort(this.protocol(), this.port()) ? "" : ":" + this.port();
        return URI.create(this.protocol() + "://" + this.host() + portString + this.encodedPath() + encodedQueryString);
    }

    public SdkHttpMethod method();

    public static interface Builder
    extends CopyableBuilder<Builder, SdkHttpRequest>,
    SdkHttpHeaders {
        default public Builder uri(URI uri) {
            Builder builder = this.protocol(uri.getScheme()).host(uri.getHost()).port(uri.getPort()).encodedPath(SdkHttpUtils.appendUri(uri.getRawPath(), this.encodedPath()));
            if (uri.getRawQuery() != null) {
                builder.clearQueryParameters();
                SdkHttpUtils.uriParams(uri).forEach(this::putRawQueryParameter);
            }
            return builder;
        }

        public String protocol();

        public Builder protocol(String var1);

        public String host();

        public Builder host(String var1);

        public Integer port();

        public Builder port(Integer var1);

        public String encodedPath();

        public Builder encodedPath(String var1);

        public Map<String, List<String>> rawQueryParameters();

        default public Builder putRawQueryParameter(String paramName, String paramValue) {
            return this.putRawQueryParameter(paramName, Collections.singletonList(paramValue));
        }

        public Builder appendRawQueryParameter(String var1, String var2);

        public Builder putRawQueryParameter(String var1, List<String> var2);

        public Builder rawQueryParameters(Map<String, List<String>> var1);

        public Builder removeQueryParameter(String var1);

        public Builder clearQueryParameters();

        default public void forEachRawQueryParameter(BiConsumer<? super String, ? super List<String>> consumer) {
            this.rawQueryParameters().forEach(consumer);
        }

        default public int numRawQueryParameters() {
            return this.rawQueryParameters().size();
        }

        default public Optional<String> encodedQueryParameters() {
            return SdkHttpUtils.encodeAndFlattenQueryParameters(this.rawQueryParameters());
        }

        public SdkHttpMethod method();

        public Builder method(SdkHttpMethod var1);

        @Override
        public Map<String, List<String>> headers();

        default public Builder putHeader(String headerName, String headerValue) {
            return this.putHeader(headerName, Collections.singletonList(headerValue));
        }

        public Builder putHeader(String var1, List<String> var2);

        public Builder appendHeader(String var1, String var2);

        public Builder headers(Map<String, List<String>> var1);

        public Builder removeHeader(String var1);

        public Builder clearHeaders();
    }
}

