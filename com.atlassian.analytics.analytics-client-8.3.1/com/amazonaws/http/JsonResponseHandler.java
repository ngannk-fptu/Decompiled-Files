/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.http;

import com.amazonaws.AmazonWebServiceResponse;
import com.amazonaws.ResponseMetadata;
import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.http.HttpResponse;
import com.amazonaws.http.HttpResponseHandler;
import com.amazonaws.internal.CRC32MismatchException;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.JsonUnmarshallerContextImpl;
import com.amazonaws.transform.Unmarshaller;
import com.amazonaws.transform.VoidJsonUnmarshaller;
import com.amazonaws.util.IOUtils;
import com.amazonaws.util.ValidationUtils;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import java.io.IOException;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SdkInternalApi
public class JsonResponseHandler<T>
implements HttpResponseHandler<AmazonWebServiceResponse<T>> {
    private Unmarshaller<T, JsonUnmarshallerContext> responseUnmarshaller;
    private static final Log log = LogFactory.getLog((String)"com.amazonaws.request");
    private final JsonFactory jsonFactory;
    private final boolean needsConnectionLeftOpen;
    private final boolean isPayloadJson;
    private final Map<Class<?>, Unmarshaller<?, JsonUnmarshallerContext>> simpleTypeUnmarshallers;
    private final Map<JsonUnmarshallerContext.UnmarshallerType, Unmarshaller<?, JsonUnmarshallerContext>> customTypeMarshallers;

    public JsonResponseHandler(Unmarshaller<T, JsonUnmarshallerContext> responseUnmarshaller, Map<Class<?>, Unmarshaller<?, JsonUnmarshallerContext>> simpleTypeUnmarshallers, Map<JsonUnmarshallerContext.UnmarshallerType, Unmarshaller<?, JsonUnmarshallerContext>> customTypeMarshallers, JsonFactory jsonFactory, boolean needsConnectionLeftOpen, boolean isPayloadJson) {
        this.responseUnmarshaller = responseUnmarshaller != null ? responseUnmarshaller : new VoidJsonUnmarshaller();
        this.needsConnectionLeftOpen = needsConnectionLeftOpen;
        this.isPayloadJson = isPayloadJson;
        this.simpleTypeUnmarshallers = ValidationUtils.assertNotNull(simpleTypeUnmarshallers, "simple type unmarshallers");
        this.customTypeMarshallers = ValidationUtils.assertNotNull(customTypeMarshallers, "custom type marshallers");
        this.jsonFactory = ValidationUtils.assertNotNull(jsonFactory, "JSONFactory");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public AmazonWebServiceResponse<T> handle(HttpResponse response) throws Exception {
        log.trace((Object)"Parsing service response JSON");
        String CRC32Checksum = response.getHeaders().get("x-amz-crc32");
        JsonParser jsonParser = null;
        if (this.shouldParsePayloadAsJson()) {
            jsonParser = this.jsonFactory.createParser(response.getContent());
        }
        try {
            AmazonWebServiceResponse<T> awsResponse = new AmazonWebServiceResponse<T>();
            JsonUnmarshallerContextImpl unmarshallerContext = new JsonUnmarshallerContextImpl(jsonParser, this.simpleTypeUnmarshallers, this.customTypeMarshallers, response);
            this.registerAdditionalMetadataExpressions(unmarshallerContext);
            T result = this.responseUnmarshaller.unmarshall(unmarshallerContext);
            if (this.shouldParsePayloadAsJson() && response.getContent() != null) {
                IOUtils.drainInputStream(response.getContent());
            }
            if (CRC32Checksum != null) {
                long serverSideCRC = Long.parseLong(CRC32Checksum);
                long clientSideCRC = response.getCRC32Checksum();
                if (clientSideCRC != serverSideCRC) {
                    throw new CRC32MismatchException("Client calculated crc32 checksum didn't match that calculated by server side");
                }
            }
            awsResponse.setResult(result);
            Map<String, String> metadata = ((JsonUnmarshallerContext)unmarshallerContext).getMetadata();
            metadata.put("AWS_REQUEST_ID", response.getHeaders().get("x-amzn-RequestId"));
            awsResponse.setResponseMetadata(new ResponseMetadata(metadata));
            log.trace((Object)"Done parsing service response");
            AmazonWebServiceResponse<T> amazonWebServiceResponse = awsResponse;
            return amazonWebServiceResponse;
        }
        finally {
            if (this.shouldParsePayloadAsJson()) {
                try {
                    jsonParser.close();
                }
                catch (IOException e) {
                    log.warn((Object)"Error closing json parser", (Throwable)e);
                }
            }
        }
    }

    protected void registerAdditionalMetadataExpressions(JsonUnmarshallerContext unmarshallerContext) {
    }

    @Override
    public boolean needsConnectionLeftOpen() {
        return this.needsConnectionLeftOpen;
    }

    private boolean shouldParsePayloadAsJson() {
        return !this.needsConnectionLeftOpen && this.isPayloadJson;
    }
}

