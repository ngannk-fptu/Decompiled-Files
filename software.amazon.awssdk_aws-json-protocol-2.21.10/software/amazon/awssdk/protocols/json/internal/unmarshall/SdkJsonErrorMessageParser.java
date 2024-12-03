/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.SdkHttpFullResponse
 *  software.amazon.awssdk.protocols.jsoncore.JsonNode
 */
package software.amazon.awssdk.protocols.json.internal.unmarshall;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.protocols.json.internal.unmarshall.ErrorMessageParser;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;

@SdkInternalApi
public class SdkJsonErrorMessageParser
implements ErrorMessageParser {
    private static final List<String> DEFAULT_ERROR_MESSAGE_LOCATIONS = Arrays.asList("message", "Message", "errorMessage");
    public static final SdkJsonErrorMessageParser DEFAULT_ERROR_MESSAGE_PARSER = new SdkJsonErrorMessageParser(DEFAULT_ERROR_MESSAGE_LOCATIONS);
    private final List<String> errorMessageJsonLocations;

    private SdkJsonErrorMessageParser(List<String> errorMessageJsonLocations) {
        this.errorMessageJsonLocations = new LinkedList<String>(errorMessageJsonLocations);
    }

    @Override
    public String parseErrorMessage(SdkHttpFullResponse httpResponse, JsonNode jsonNode) {
        for (String field : this.errorMessageJsonLocations) {
            String value = jsonNode.field(field).map(JsonNode::text).orElse(null);
            if (value == null) continue;
            return value;
        }
        return null;
    }
}

