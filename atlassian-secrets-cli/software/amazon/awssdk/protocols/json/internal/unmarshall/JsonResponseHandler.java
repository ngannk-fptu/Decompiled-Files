/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.protocols.json.internal.unmarshall;

import java.util.function.Function;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.SdkStandardLogger;
import software.amazon.awssdk.core.http.HttpResponseHandler;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.protocols.json.internal.unmarshall.JsonProtocolUnmarshaller;
import software.amazon.awssdk.utils.FunctionalUtils;
import software.amazon.awssdk.utils.IoUtils;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class JsonResponseHandler<T extends SdkPojo>
implements HttpResponseHandler<T> {
    private final Function<SdkHttpFullResponse, SdkPojo> pojoSupplier;
    private final boolean needsConnectionLeftOpen;
    private final boolean isPayloadJson;
    private JsonProtocolUnmarshaller unmarshaller;

    public JsonResponseHandler(JsonProtocolUnmarshaller unmarshaller, Function<SdkHttpFullResponse, SdkPojo> pojoSupplier, boolean needsConnectionLeftOpen, boolean isPayloadJson) {
        this.unmarshaller = Validate.paramNotNull(unmarshaller, "unmarshaller");
        this.pojoSupplier = pojoSupplier;
        this.needsConnectionLeftOpen = needsConnectionLeftOpen;
        this.isPayloadJson = isPayloadJson;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public T handle(SdkHttpFullResponse response, ExecutionAttributes executionAttributes) throws Exception {
        SdkStandardLogger.REQUEST_LOGGER.trace(() -> "Parsing service response JSON.");
        try {
            Object result = this.unmarshaller.unmarshall(this.pojoSupplier.apply(response), response);
            if (this.shouldParsePayloadAsJson() && response.content().isPresent()) {
                IoUtils.drainInputStream(response.content().get());
            }
            SdkStandardLogger.REQUEST_LOGGER.trace(() -> "Done parsing service response.");
            Object TypeT = result;
            return (T)TypeT;
        }
        finally {
            if (!this.needsConnectionLeftOpen) {
                response.content().ifPresent(i -> FunctionalUtils.invokeSafely(i::close));
            }
        }
    }

    @Override
    public boolean needsConnectionLeftOpen() {
        return this.needsConnectionLeftOpen;
    }

    private boolean shouldParsePayloadAsJson() {
        return !this.needsConnectionLeftOpen && this.isPayloadJson;
    }
}

