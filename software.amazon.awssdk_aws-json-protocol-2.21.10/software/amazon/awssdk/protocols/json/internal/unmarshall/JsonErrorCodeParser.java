/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.SdkHttpFullResponse
 *  software.amazon.awssdk.protocols.jsoncore.JsonNode
 */
package software.amazon.awssdk.protocols.json.internal.unmarshall;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.protocols.json.ErrorCodeParser;
import software.amazon.awssdk.protocols.json.JsonContent;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;

@SdkInternalApi
public class JsonErrorCodeParser
implements ErrorCodeParser {
    public static final String X_AMZN_ERROR_TYPE = "x-amzn-ErrorType";
    static final String ERROR_CODE_HEADER = ":error-code";
    static final String EXCEPTION_TYPE_HEADER = ":exception-type";
    private static final Logger log = LoggerFactory.getLogger(JsonErrorCodeParser.class);
    private final List<String> errorCodeHeaders;
    private final String errorCodeFieldName;

    public JsonErrorCodeParser(String errorCodeFieldName) {
        this.errorCodeFieldName = errorCodeFieldName == null ? "__type" : errorCodeFieldName;
        this.errorCodeHeaders = Arrays.asList(X_AMZN_ERROR_TYPE, ERROR_CODE_HEADER, EXCEPTION_TYPE_HEADER);
    }

    @Override
    public String parseErrorCode(SdkHttpFullResponse response, JsonContent jsonContent) {
        String errorCodeFromHeader = this.parseErrorCodeFromHeader(response);
        if (errorCodeFromHeader != null) {
            return errorCodeFromHeader;
        }
        if (jsonContent != null) {
            return this.parseErrorCodeFromContents(jsonContent.getJsonNode());
        }
        return null;
    }

    private String parseErrorCodeFromHeader(SdkHttpFullResponse response) {
        for (String errorCodeHeader : this.errorCodeHeaders) {
            Optional errorCode = response.firstMatchingHeader(errorCodeHeader);
            if (!errorCode.isPresent()) continue;
            if (X_AMZN_ERROR_TYPE.equals(errorCodeHeader)) {
                return this.parseErrorCodeFromXAmzErrorType((String)errorCode.get());
            }
            return (String)errorCode.get();
        }
        return null;
    }

    private String parseErrorCodeFromXAmzErrorType(String headerValue) {
        int separator;
        if (headerValue != null && (separator = headerValue.indexOf(58)) != -1) {
            headerValue = headerValue.substring(0, separator);
        }
        return headerValue;
    }

    private String parseErrorCodeFromContents(JsonNode jsonContents) {
        if (jsonContents == null) {
            return null;
        }
        JsonNode errorCodeField = jsonContents.field(this.errorCodeFieldName).orElse(null);
        if (errorCodeField == null) {
            return null;
        }
        String code = errorCodeField.text();
        int separator = code.lastIndexOf(35);
        return code.substring(separator + 1);
    }
}

