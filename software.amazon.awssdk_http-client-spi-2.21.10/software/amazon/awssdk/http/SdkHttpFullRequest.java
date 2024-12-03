/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.Immutable
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.utils.http.SdkHttpUtils
 */
package software.amazon.awssdk.http;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.http.DefaultSdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.utils.http.SdkHttpUtils;

@SdkPublicApi
@Immutable
public interface SdkHttpFullRequest
extends SdkHttpRequest {
    public static Builder builder() {
        return new DefaultSdkHttpFullRequest.Builder();
    }

    public Builder toBuilder();

    public Optional<ContentStreamProvider> contentStreamProvider();

    public static interface Builder
    extends SdkHttpRequest.Builder {
        @Override
        default public Builder uri(URI uri) {
            Builder builder = this.protocol(uri.getScheme()).host(uri.getHost()).port(uri.getPort()).encodedPath(SdkHttpUtils.appendUri((String)uri.getRawPath(), (String)this.encodedPath()));
            if (uri.getRawQuery() != null) {
                builder.clearQueryParameters();
                SdkHttpUtils.uriParams((URI)uri).forEach((string, list) -> this.putRawQueryParameter((String)string, (List)list));
            }
            return builder;
        }

        @Override
        public String protocol();

        @Override
        public Builder protocol(String var1);

        @Override
        public String host();

        @Override
        public Builder host(String var1);

        @Override
        public Integer port();

        @Override
        public Builder port(Integer var1);

        @Override
        public String encodedPath();

        @Override
        public Builder encodedPath(String var1);

        @Override
        public Map<String, List<String>> rawQueryParameters();

        @Override
        default public Builder putRawQueryParameter(String paramName, String paramValue) {
            return this.putRawQueryParameter(paramName, (List)Collections.singletonList(paramValue));
        }

        @Override
        public Builder appendRawQueryParameter(String var1, String var2);

        @Override
        public Builder putRawQueryParameter(String var1, List<String> var2);

        @Override
        public Builder rawQueryParameters(Map<String, List<String>> var1);

        @Override
        public Builder removeQueryParameter(String var1);

        @Override
        public Builder clearQueryParameters();

        @Override
        public SdkHttpMethod method();

        @Override
        public Builder method(SdkHttpMethod var1);

        @Override
        public Map<String, List<String>> headers();

        @Override
        default public Builder putHeader(String headerName, String headerValue) {
            return this.putHeader(headerName, (List)Collections.singletonList(headerValue));
        }

        @Override
        public Builder putHeader(String var1, List<String> var2);

        @Override
        public Builder appendHeader(String var1, String var2);

        @Override
        public Builder headers(Map<String, List<String>> var1);

        @Override
        public Builder removeHeader(String var1);

        @Override
        public Builder clearHeaders();

        public Builder contentStreamProvider(ContentStreamProvider var1);

        public ContentStreamProvider contentStreamProvider();

        public Builder copy();

        public Builder applyMutation(Consumer<SdkHttpRequest.Builder> var1);

        public SdkHttpFullRequest build();
    }
}

