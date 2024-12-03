/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.awscore.AwsResponse
 *  software.amazon.awssdk.awscore.AwsResponseMetadata
 *  software.amazon.awssdk.awscore.DefaultAwsResponseMetadata
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.SdkStandardLogger
 *  software.amazon.awssdk.core.http.HttpResponseHandler
 *  software.amazon.awssdk.core.interceptor.ExecutionAttributes
 *  software.amazon.awssdk.http.SdkHttpFullResponse
 *  software.amazon.awssdk.http.SdkHttpResponse
 *  software.amazon.awssdk.utils.Logger
 *  software.amazon.awssdk.utils.Pair
 */
package software.amazon.awssdk.protocols.query.internal.unmarshall;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.awscore.AwsResponse;
import software.amazon.awssdk.awscore.AwsResponseMetadata;
import software.amazon.awssdk.awscore.DefaultAwsResponseMetadata;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.SdkStandardLogger;
import software.amazon.awssdk.core.http.HttpResponseHandler;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.protocols.query.internal.unmarshall.QueryProtocolUnmarshaller;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.Pair;

@SdkInternalApi
public final class AwsQueryResponseHandler<T extends AwsResponse>
implements HttpResponseHandler<T> {
    private static final Logger log = Logger.loggerFor(AwsQueryResponseHandler.class);
    private final QueryProtocolUnmarshaller unmarshaller;
    private final Function<SdkHttpFullResponse, SdkPojo> pojoSupplier;

    public AwsQueryResponseHandler(QueryProtocolUnmarshaller unmarshaller, Function<SdkHttpFullResponse, SdkPojo> pojoSupplier) {
        this.unmarshaller = unmarshaller;
        this.pojoSupplier = pojoSupplier;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public T handle(SdkHttpFullResponse response, ExecutionAttributes executionAttributes) throws Exception {
        try {
            T t = this.unmarshallResponse(response);
            return t;
        }
        finally {
            response.content().ifPresent(i -> {
                try {
                    i.close();
                }
                catch (IOException e) {
                    log.warn(() -> "Error closing HTTP content.", (Throwable)e);
                }
            });
        }
    }

    private T unmarshallResponse(SdkHttpFullResponse response) throws Exception {
        SdkStandardLogger.REQUEST_LOGGER.trace(() -> "Parsing service response XML.");
        Pair result = this.unmarshaller.unmarshall(this.pojoSupplier.apply(response), response);
        SdkStandardLogger.REQUEST_LOGGER.trace(() -> "Done parsing service response.");
        AwsResponseMetadata responseMetadata = this.generateResponseMetadata((SdkHttpResponse)response, (Map)result.right());
        return (T)((AwsResponse)result.left()).toBuilder().responseMetadata(responseMetadata).build();
    }

    private AwsResponseMetadata generateResponseMetadata(SdkHttpResponse response, Map<String, String> metadata) {
        if (!metadata.containsKey("AWS_REQUEST_ID")) {
            metadata.put("AWS_REQUEST_ID", response.firstMatchingHeader((Collection)X_AMZN_REQUEST_ID_HEADERS).orElse(null));
        }
        response.forEachHeader((key, value) -> {
            String cfr_ignored_0 = (String)metadata.put((String)key, (String)value.get(0));
        });
        return DefaultAwsResponseMetadata.create(metadata);
    }

    public boolean needsConnectionLeftOpen() {
        return false;
    }
}

