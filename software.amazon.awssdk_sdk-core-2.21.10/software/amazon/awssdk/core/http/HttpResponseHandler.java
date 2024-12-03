/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.http.SdkHttpFullResponse
 */
package software.amazon.awssdk.core.http;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.http.SdkHttpFullResponse;

@FunctionalInterface
@SdkProtectedApi
public interface HttpResponseHandler<T> {
    public static final String X_AMZN_REQUEST_ID_HEADER = "x-amzn-RequestId";
    public static final String X_AMZN_REQUEST_ID_HEADER_ALTERNATE = "x-amz-request-id";
    public static final Set<String> X_AMZN_REQUEST_ID_HEADERS = Collections.unmodifiableSet(Stream.of("x-amzn-RequestId", "x-amz-request-id").collect(Collectors.toSet()));
    public static final String X_AMZ_ID_2_HEADER = "x-amz-id-2";

    public T handle(SdkHttpFullResponse var1, ExecutionAttributes var2) throws Exception;

    default public boolean needsConnectionLeftOpen() {
        return false;
    }
}

