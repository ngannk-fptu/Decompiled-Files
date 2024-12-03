/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.protocols.json;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;
import software.amazon.awssdk.protocols.jsoncore.JsonNodeParser;
import software.amazon.awssdk.thirdparty.jackson.core.JsonFactory;
import software.amazon.awssdk.utils.IoUtils;

@SdkProtectedApi
public class JsonContent {
    private static final Logger LOG = LoggerFactory.getLogger(JsonContent.class);
    private final byte[] rawContent;
    private final JsonNode jsonNode;

    JsonContent(byte[] rawJsonContent, JsonNode jsonNode) {
        this.rawContent = rawJsonContent;
        this.jsonNode = jsonNode;
    }

    private JsonContent(byte[] rawJsonContent, JsonFactory jsonFactory) {
        this.rawContent = rawJsonContent;
        this.jsonNode = JsonContent.parseJsonContent(rawJsonContent, jsonFactory);
    }

    public static JsonContent createJsonContent(SdkHttpFullResponse httpResponse, JsonFactory jsonFactory) {
        byte[] rawJsonContent = httpResponse.content().map(c -> {
            try {
                return IoUtils.toByteArray(c);
            }
            catch (IOException e) {
                LOG.debug("Unable to read HTTP response content", e);
                return null;
            }
        }).orElse(null);
        return new JsonContent(rawJsonContent, jsonFactory);
    }

    private static JsonNode parseJsonContent(byte[] rawJsonContent, JsonFactory jsonFactory) {
        if (rawJsonContent == null || rawJsonContent.length == 0) {
            return JsonNode.emptyObjectNode();
        }
        try {
            JsonNodeParser parser = JsonNodeParser.builder().jsonFactory(jsonFactory).build();
            return parser.parse(rawJsonContent);
        }
        catch (Exception e) {
            LOG.debug("Unable to parse HTTP response content", e);
            return JsonNode.emptyObjectNode();
        }
    }

    public byte[] getRawContent() {
        return this.rawContent;
    }

    public JsonNode getJsonNode() {
        return this.jsonNode;
    }
}

