/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.SdkStandardLogger
 *  software.amazon.awssdk.core.http.HttpResponseHandler
 *  software.amazon.awssdk.core.interceptor.ExecutionAttributes
 *  software.amazon.awssdk.http.SdkHttpFullResponse
 *  software.amazon.awssdk.utils.Logger
 */
package software.amazon.awssdk.protocols.xml.internal.unmarshall;

import java.io.IOException;
import java.util.function.Function;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.SdkStandardLogger;
import software.amazon.awssdk.core.http.HttpResponseHandler;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.protocols.xml.internal.unmarshall.XmlProtocolUnmarshaller;
import software.amazon.awssdk.utils.Logger;

@SdkInternalApi
public final class XmlResponseHandler<T extends SdkPojo>
implements HttpResponseHandler<T> {
    private static final Logger log = Logger.loggerFor(XmlResponseHandler.class);
    private final XmlProtocolUnmarshaller unmarshaller;
    private final Function<SdkHttpFullResponse, SdkPojo> pojoSupplier;
    private final boolean needsConnectionLeftOpen;

    public XmlResponseHandler(XmlProtocolUnmarshaller unmarshaller, Function<SdkHttpFullResponse, SdkPojo> pojoSupplier, boolean needsConnectionLeftOpen) {
        this.unmarshaller = unmarshaller;
        this.pojoSupplier = pojoSupplier;
        this.needsConnectionLeftOpen = needsConnectionLeftOpen;
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
            if (!this.needsConnectionLeftOpen) {
                this.closeStream(response);
            }
        }
    }

    private void closeStream(SdkHttpFullResponse response) {
        response.content().ifPresent(i -> {
            try {
                i.close();
            }
            catch (IOException e) {
                log.warn(() -> "Error closing HTTP content.", (Throwable)e);
            }
        });
    }

    private T unmarshallResponse(SdkHttpFullResponse response) throws Exception {
        SdkStandardLogger.REQUEST_LOGGER.trace(() -> "Parsing service response XML.");
        Object result = this.unmarshaller.unmarshall(this.pojoSupplier.apply(response), response);
        SdkStandardLogger.REQUEST_LOGGER.trace(() -> "Done parsing service response.");
        return (T)result;
    }

    public boolean needsConnectionLeftOpen() {
        return this.needsConnectionLeftOpen;
    }
}

