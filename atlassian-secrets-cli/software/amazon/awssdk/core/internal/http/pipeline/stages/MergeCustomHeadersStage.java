/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.http.pipeline.stages;

import java.util.List;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.client.config.SdkClientOption;
import software.amazon.awssdk.core.internal.http.HttpClientDependencies;
import software.amazon.awssdk.core.internal.http.RequestExecutionContext;
import software.amazon.awssdk.core.internal.http.pipeline.MutableRequestToRequestPipeline;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.utils.http.SdkHttpUtils;

@SdkInternalApi
public class MergeCustomHeadersStage
implements MutableRequestToRequestPipeline {
    private final SdkClientConfiguration config;

    public MergeCustomHeadersStage(HttpClientDependencies dependencies) {
        this.config = dependencies.clientConfiguration();
    }

    @Override
    public SdkHttpFullRequest.Builder execute(SdkHttpFullRequest.Builder request, RequestExecutionContext context) throws Exception {
        this.addOverrideHeaders(request, this.config.option(SdkClientOption.ADDITIONAL_HTTP_HEADERS), context.requestConfig().headers());
        return request;
    }

    @SafeVarargs
    private final void addOverrideHeaders(SdkHttpFullRequest.Builder request, Map<String, List<String>> ... overrideHeaders) {
        for (Map<String, List<String>> overrideHeader : overrideHeaders) {
            overrideHeader.forEach((headerName, headerValues) -> {
                if (SdkHttpUtils.isSingleHeader(headerName)) {
                    request.removeHeader((String)headerName);
                }
                headerValues.forEach(v -> request.appendHeader((String)headerName, (String)v));
            });
        }
    }
}

