/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.http;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.http.HttpResponse;
import com.amazonaws.http.HttpResponseHandler;
import com.amazonaws.internal.http.ErrorCodeParser;
import com.amazonaws.internal.http.JsonErrorMessageParser;
import com.amazonaws.protocol.json.JsonContent;
import com.amazonaws.transform.EnhancedJsonErrorUnmarshaller;
import com.amazonaws.transform.JsonErrorUnmarshaller;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.JsonUnmarshallerContextImpl;
import com.amazonaws.transform.Unmarshaller;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SdkInternalApi
public class JsonErrorResponseHandler
implements HttpResponseHandler<AmazonServiceException> {
    private static final Log LOG = LogFactory.getLog(JsonErrorResponseHandler.class);
    private final List<JsonErrorUnmarshaller> unmarshallers;
    private final ErrorCodeParser errorCodeParser;
    private final JsonErrorMessageParser errorMessageParser;
    private final JsonFactory jsonFactory;
    private final Map<Class<?>, Unmarshaller<?, JsonUnmarshallerContext>> simpleTypeUnmarshallers;
    private final Map<JsonUnmarshallerContext.UnmarshallerType, Unmarshaller<?, JsonUnmarshallerContext>> customTypeUnmarshallers;

    public JsonErrorResponseHandler(List<JsonErrorUnmarshaller> errorUnmarshallers, ErrorCodeParser errorCodeParser, JsonErrorMessageParser errorMessageParser, JsonFactory jsonFactory) {
        this.unmarshallers = errorUnmarshallers;
        this.simpleTypeUnmarshallers = null;
        this.customTypeUnmarshallers = null;
        this.errorCodeParser = errorCodeParser;
        this.errorMessageParser = errorMessageParser;
        this.jsonFactory = jsonFactory;
    }

    public JsonErrorResponseHandler(List<JsonErrorUnmarshaller> errorUnmarshallers, Map<Class<?>, Unmarshaller<?, JsonUnmarshallerContext>> simpleTypeUnmarshallers, Map<JsonUnmarshallerContext.UnmarshallerType, Unmarshaller<?, JsonUnmarshallerContext>> customTypeUnmarshallers, ErrorCodeParser errorCodeParser, JsonErrorMessageParser errorMessageParser, JsonFactory jsonFactory) {
        this.unmarshallers = errorUnmarshallers;
        this.simpleTypeUnmarshallers = simpleTypeUnmarshallers;
        this.customTypeUnmarshallers = customTypeUnmarshallers;
        this.errorCodeParser = errorCodeParser;
        this.errorMessageParser = errorMessageParser;
        this.jsonFactory = jsonFactory;
    }

    @Override
    public boolean needsConnectionLeftOpen() {
        return false;
    }

    @Override
    public AmazonServiceException handle(HttpResponse response) throws Exception {
        JsonContent jsonContent = JsonContent.createJsonContent(response, this.jsonFactory);
        byte[] rawContent = jsonContent.getRawContent();
        String errorCode = this.errorCodeParser.parseErrorCode(response, jsonContent);
        AmazonServiceException ase = this.createException(errorCode, response, jsonContent.getJsonNode(), rawContent);
        if (ase.getErrorMessage() == null) {
            ase.setErrorMessage(this.errorMessageParser.parseErrorMessage(response, jsonContent.getJsonNode()));
        }
        ase.setErrorCode(errorCode);
        ase.setServiceName(response.getRequest().getServiceName());
        ase.setStatusCode(response.getStatusCode());
        ase.setErrorType(this.getErrorTypeFromStatusCode(response.getStatusCode()));
        ase.setRawResponse(rawContent);
        String requestId = this.getRequestIdFromHeaders(response.getHeaders());
        if (requestId != null) {
            ase.setRequestId(requestId);
        }
        ase.setHttpHeaders(response.getHeaders());
        return ase;
    }

    private AmazonServiceException createException(String errorCode, HttpResponse response, JsonNode jsonNode, byte[] rawContent) {
        AmazonServiceException ase = this.unmarshallException(errorCode, response, jsonNode, rawContent);
        if (ase == null) {
            ase = new AmazonServiceException("Unable to unmarshall exception response with the unmarshallers provided");
        }
        return ase;
    }

    private AmazonServiceException unmarshallException(String errorCode, HttpResponse response, JsonNode jsonNode, byte[] rawContent) {
        for (JsonErrorUnmarshaller unmarshaller : this.unmarshallers) {
            if (!unmarshaller.matchErrorCode(errorCode)) continue;
            try {
                if (unmarshaller instanceof EnhancedJsonErrorUnmarshaller) {
                    EnhancedJsonErrorUnmarshaller enhancedUnmarshaller = (EnhancedJsonErrorUnmarshaller)unmarshaller;
                    return this.doEnhancedUnmarshall(enhancedUnmarshaller, errorCode, response, rawContent);
                }
                return this.doLegacyUnmarshall(unmarshaller, jsonNode);
            }
            catch (Exception e) {
                LOG.debug((Object)"Unable to unmarshall exception content", (Throwable)e);
                return null;
            }
        }
        return null;
    }

    private AmazonServiceException doEnhancedUnmarshall(EnhancedJsonErrorUnmarshaller unmarshaller, String errorCode, HttpResponse response, byte[] rawContent) throws Exception {
        if (rawContent == null) {
            rawContent = new byte[]{};
        }
        JsonParser jsonParser = this.jsonFactory.createParser(rawContent);
        JsonUnmarshallerContextImpl unmarshallerContext = new JsonUnmarshallerContextImpl(jsonParser, this.simpleTypeUnmarshallers, this.customTypeUnmarshallers, response);
        try {
            return unmarshaller.unmarshallFromContext(unmarshallerContext);
        }
        catch (JsonParseException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug((Object)String.format("Received response with error code '%s', but response body did not contain valid JSON. Treating it as an empty object.", errorCode), (Throwable)e);
            }
            JsonParser emptyParser = this.jsonFactory.createParser("{}");
            unmarshallerContext = new JsonUnmarshallerContextImpl(emptyParser, this.simpleTypeUnmarshallers, this.customTypeUnmarshallers, response);
            return unmarshaller.unmarshallFromContext(unmarshallerContext);
        }
    }

    private AmazonServiceException doLegacyUnmarshall(JsonErrorUnmarshaller unmarshaller, JsonNode jsonNode) throws Exception {
        return unmarshaller.unmarshall(jsonNode);
    }

    private AmazonServiceException.ErrorType getErrorTypeFromStatusCode(int statusCode) {
        return statusCode < 500 ? AmazonServiceException.ErrorType.Client : AmazonServiceException.ErrorType.Service;
    }

    private String getRequestIdFromHeaders(Map<String, String> headers) {
        for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
            if (headerEntry.getKey().equalsIgnoreCase("x-amzn-RequestId")) {
                return headerEntry.getValue();
            }
            if (!headerEntry.getKey().equalsIgnoreCase("x-amz-request-id")) continue;
            return headerEntry.getValue();
        }
        return null;
    }
}

