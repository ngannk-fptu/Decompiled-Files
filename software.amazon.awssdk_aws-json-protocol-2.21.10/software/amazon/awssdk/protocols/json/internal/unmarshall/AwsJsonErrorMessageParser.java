/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.SdkHttpFullResponse
 *  software.amazon.awssdk.protocols.jsoncore.JsonNode
 */
package software.amazon.awssdk.protocols.json.internal.unmarshall;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.protocols.json.internal.unmarshall.ErrorMessageParser;
import software.amazon.awssdk.protocols.json.internal.unmarshall.SdkJsonErrorMessageParser;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;

@SdkInternalApi
public final class AwsJsonErrorMessageParser
implements ErrorMessageParser {
    public static final ErrorMessageParser DEFAULT_ERROR_MESSAGE_PARSER = new AwsJsonErrorMessageParser(SdkJsonErrorMessageParser.DEFAULT_ERROR_MESSAGE_PARSER);
    private static final String X_AMZN_ERROR_MESSAGE = "x-amzn-error-message";
    private static final String EVENT_ERROR_MESSAGE = ":error-message";
    private SdkJsonErrorMessageParser errorMessageParser;

    public AwsJsonErrorMessageParser(SdkJsonErrorMessageParser errorMessageJsonLocations) {
        this.errorMessageParser = errorMessageJsonLocations;
    }

    @Override
    public String parseErrorMessage(SdkHttpFullResponse httpResponse, JsonNode jsonNode) {
        String headerMessage = httpResponse.firstMatchingHeader(X_AMZN_ERROR_MESSAGE).orElse(null);
        if (headerMessage != null) {
            return headerMessage;
        }
        String eventHeaderMessage = httpResponse.firstMatchingHeader(EVENT_ERROR_MESSAGE).orElse(null);
        if (eventHeaderMessage != null) {
            return eventHeaderMessage;
        }
        return this.errorMessageParser.parseErrorMessage(httpResponse, jsonNode);
    }
}

