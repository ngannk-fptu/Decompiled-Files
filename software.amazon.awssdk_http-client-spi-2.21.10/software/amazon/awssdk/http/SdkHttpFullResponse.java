/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.Immutable
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.http;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.http.DefaultSdkHttpFullResponse;
import software.amazon.awssdk.http.SdkHttpResponse;

@SdkProtectedApi
@Immutable
public interface SdkHttpFullResponse
extends SdkHttpResponse {
    public static Builder builder() {
        return new DefaultSdkHttpFullResponse.Builder();
    }

    public Builder toBuilder();

    public Optional<AbortableInputStream> content();

    public static interface Builder
    extends SdkHttpResponse.Builder {
        @Override
        public String statusText();

        @Override
        public Builder statusText(String var1);

        @Override
        public int statusCode();

        @Override
        public Builder statusCode(int var1);

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

        public AbortableInputStream content();

        public Builder content(AbortableInputStream var1);

        public SdkHttpFullResponse build();
    }
}

