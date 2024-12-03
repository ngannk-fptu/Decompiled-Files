/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Publisher
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.annotations.ThreadSafe
 *  software.amazon.awssdk.http.SdkHttpRequest
 *  software.amazon.awssdk.http.SdkHttpResponse
 */
package software.amazon.awssdk.core.interceptor;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Optional;
import org.reactivestreams.Publisher;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.SdkResponse;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.SdkHttpResponse;

@SdkProtectedApi
public final class Context {
    private Context() {
    }

    @ThreadSafe
    @SdkPublicApi
    public static interface FailedExecution {
        public Throwable exception();

        public SdkRequest request();

        public Optional<SdkHttpRequest> httpRequest();

        public Optional<SdkHttpResponse> httpResponse();

        public Optional<SdkResponse> response();
    }

    @ThreadSafe
    @SdkPublicApi
    public static interface AfterExecution
    extends ModifyResponse {
    }

    @ThreadSafe
    @SdkPublicApi
    public static interface ModifyResponse
    extends AfterUnmarshalling {
    }

    @ThreadSafe
    @SdkPublicApi
    public static interface AfterUnmarshalling
    extends BeforeUnmarshalling {
        public SdkResponse response();
    }

    @ThreadSafe
    @SdkPublicApi
    public static interface BeforeUnmarshalling
    extends ModifyHttpResponse {
    }

    @ThreadSafe
    @SdkPublicApi
    public static interface ModifyHttpResponse
    extends AfterTransmission {
    }

    @ThreadSafe
    @SdkPublicApi
    public static interface AfterTransmission
    extends BeforeTransmission {
        public SdkHttpResponse httpResponse();

        public Optional<Publisher<ByteBuffer>> responsePublisher();

        public Optional<InputStream> responseBody();
    }

    @ThreadSafe
    @SdkPublicApi
    public static interface BeforeTransmission
    extends ModifyHttpRequest {
    }

    @ThreadSafe
    @SdkPublicApi
    public static interface ModifyHttpRequest
    extends AfterMarshalling {
    }

    @ThreadSafe
    @SdkPublicApi
    public static interface AfterMarshalling
    extends BeforeMarshalling {
        public SdkHttpRequest httpRequest();

        public Optional<RequestBody> requestBody();

        public Optional<AsyncRequestBody> asyncRequestBody();
    }

    @ThreadSafe
    @SdkPublicApi
    public static interface BeforeMarshalling
    extends ModifyRequest {
    }

    @ThreadSafe
    @SdkPublicApi
    public static interface ModifyRequest
    extends BeforeExecution {
    }

    @ThreadSafe
    @SdkPublicApi
    public static interface BeforeExecution {
        public SdkRequest request();
    }
}

