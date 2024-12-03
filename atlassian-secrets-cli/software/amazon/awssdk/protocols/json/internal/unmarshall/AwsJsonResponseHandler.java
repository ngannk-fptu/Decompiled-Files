/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.protocols.json.internal.unmarshall;

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
public final class AwsJsonResponseHandler<T>
implements HttpResponseHandler<T> {
    private final HttpResponseHandler<T> responseHandler;

    public AwsJsonResponseHandler(HttpResponseHandler<T> responseHandler) {
        this.responseHandler = responseHandler;
    }

    @Override
    public T handle(SdkHttpFullResponse response, ExecutionAttributes executionAttributes) throws Exception {
        T result = this.responseHandler.handle(response, executionAttributes);
        if (result instanceof AwsResponse) {
            AwsResponseMetadata responseMetadata = this.generateResponseMetadata(response);
            return (T)((AwsResponse)result).toBuilder().responseMetadata(responseMetadata).build();
        }
        return result;
    }

    private AwsResponseMetadata generateResponseMetadata(SdkHttpResponse response) {
        HashMap<String, String> metadata = new HashMap<String, String>();
        metadata.put("AWS_REQUEST_ID", response.firstMatchingHeader(X_AMZN_REQUEST_ID_HEADERS).orElse(null));
        response.forEachHeader((key, value) -> {
            String cfr_ignored_0 = (String)metadata.put((String)key, (String)value.get(0));
        });
        return DefaultAwsResponseMetadata.create(metadata);
    }
}

