/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.awscore.AwsResponse
 *  software.amazon.awssdk.awscore.AwsResponseMetadata
 *  software.amazon.awssdk.awscore.DefaultAwsResponseMetadata
 *  software.amazon.awssdk.core.http.HttpResponseHandler
 *  software.amazon.awssdk.core.interceptor.ExecutionAttributes
 *  software.amazon.awssdk.http.SdkHttpFullResponse
 *  software.amazon.awssdk.http.SdkHttpResponse
 */
package software.amazon.awssdk.protocols.xml.internal.unmarshall;

import java.util.Collection;
import java.util.HashMap;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.awscore.AwsResponse;
import software.amazon.awssdk.awscore.AwsResponseMetadata;
import software.amazon.awssdk.awscore.DefaultAwsResponseMetadata;
import software.amazon.awssdk.core.http.HttpResponseHandler;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.http.SdkHttpResponse;

@SdkInternalApi
public final class AwsXmlResponseHandler<T>
implements HttpResponseHandler<T> {
    private final HttpResponseHandler<T> delegate;

    public AwsXmlResponseHandler(HttpResponseHandler<T> responseHandler) {
        this.delegate = responseHandler;
    }

    public T handle(SdkHttpFullResponse response, ExecutionAttributes executionAttributes) throws Exception {
        Object result = this.delegate.handle(response, executionAttributes);
        if (result instanceof AwsResponse) {
            AwsResponseMetadata responseMetadata = this.generateResponseMetadata((SdkHttpResponse)response);
            return (T)((AwsResponse)result).toBuilder().responseMetadata(responseMetadata).build();
        }
        return (T)result;
    }

    private AwsResponseMetadata generateResponseMetadata(SdkHttpResponse response) {
        HashMap<String, Object> metadata = new HashMap<String, Object>();
        metadata.put("AWS_REQUEST_ID", response.firstMatchingHeader((Collection)X_AMZN_REQUEST_ID_HEADERS).orElse(null));
        response.forEachHeader((key, value) -> {
            String cfr_ignored_0 = (String)metadata.put((String)key, value.get(0));
        });
        return DefaultAwsResponseMetadata.create(metadata);
    }

    public boolean needsConnectionLeftOpen() {
        return this.delegate.needsConnectionLeftOpen();
    }
}

