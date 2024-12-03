/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.http.pipeline.stages;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkProtocolMetadata;
import software.amazon.awssdk.core.interceptor.SdkInternalExecutionAttribute;
import software.amazon.awssdk.core.internal.http.RequestExecutionContext;
import software.amazon.awssdk.core.internal.http.pipeline.MutableRequestToRequestPipeline;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.utils.StringUtils;

@SdkInternalApi
public class QueryParametersToBodyStage
implements MutableRequestToRequestPipeline {
    private static final String DEFAULT_CONTENT_TYPE = "application/x-www-form-urlencoded; charset=" + StringUtils.lowerCase(StandardCharsets.UTF_8.toString());

    @Override
    public SdkHttpFullRequest.Builder execute(SdkHttpFullRequest.Builder request, RequestExecutionContext context) throws Exception {
        if (this.shouldPutParamsInBody(request.build(), context)) {
            return this.changeQueryParametersToFormData(request.build()).toBuilder();
        }
        return request;
    }

    private boolean shouldPutParamsInBody(SdkHttpFullRequest request, RequestExecutionContext context) {
        SdkProtocolMetadata protocolMetadata = context.executionAttributes().getAttribute(SdkInternalExecutionAttribute.PROTOCOL_METADATA);
        if (protocolMetadata == null) {
            return false;
        }
        String protocol = protocolMetadata.serviceProtocol();
        boolean isQueryProtocol = "query".equalsIgnoreCase(protocol) || "ec2".equalsIgnoreCase(protocol);
        return isQueryProtocol && request.method() == SdkHttpMethod.POST && !request.contentStreamProvider().isPresent() && request.numRawQueryParameters() > 0;
    }

    private SdkHttpFullRequest changeQueryParametersToFormData(SdkHttpFullRequest request) {
        byte[] params = request.encodedQueryParametersAsFormData().orElse("").getBytes(StandardCharsets.UTF_8);
        return request.toBuilder().clearQueryParameters().contentStreamProvider(() -> new ByteArrayInputStream(params)).putHeader("Content-Length", (List)Collections.singletonList(String.valueOf(params.length))).putHeader("Content-Type", (List)Collections.singletonList(DEFAULT_CONTENT_TYPE)).build();
    }
}

