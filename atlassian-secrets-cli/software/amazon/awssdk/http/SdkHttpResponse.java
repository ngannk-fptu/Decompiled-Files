/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.http.DefaultSdkHttpFullResponse;
import software.amazon.awssdk.http.HttpStatusFamily;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.http.SdkHttpHeaders;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
@Immutable
public interface SdkHttpResponse
extends ToCopyableBuilder<Builder, SdkHttpResponse>,
SdkHttpHeaders,
Serializable {
    public static SdkHttpFullResponse.Builder builder() {
        return new DefaultSdkHttpFullResponse.Builder();
    }

    public Optional<String> statusText();

    public int statusCode();

    default public boolean isSuccessful() {
        return HttpStatusFamily.of(this.statusCode()) == HttpStatusFamily.SUCCESSFUL;
    }

    public static interface Builder
    extends CopyableBuilder<Builder, SdkHttpResponse>,
    SdkHttpHeaders {
        public String statusText();

        public Builder statusText(String var1);

        public int statusCode();

        public Builder statusCode(int var1);

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

