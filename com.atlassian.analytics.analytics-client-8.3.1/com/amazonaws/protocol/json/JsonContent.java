/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.protocol.json;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.http.HttpResponse;
import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SdkInternalApi
public class JsonContent {
    private static final Log LOG = LogFactory.getLog(JsonContent.class);
    private final byte[] rawContent;
    private final JsonNode jsonNode;

    public static JsonContent createJsonContent(HttpResponse httpResponse, JsonFactory jsonFactory) {
        byte[] rawJsonContent = null;
        try {
            if (httpResponse.getContent() != null) {
                rawJsonContent = IOUtils.toByteArray(httpResponse.getContent());
            }
        }
        catch (Exception e) {
            LOG.debug((Object)"Unable to read HTTP response content", (Throwable)e);
        }
        return new JsonContent(rawJsonContent, new ObjectMapper(jsonFactory).configure(JsonParser.Feature.ALLOW_COMMENTS, true));
    }

    public JsonContent(byte[] rawJsonContent, JsonNode jsonNode) {
        this.rawContent = rawJsonContent;
        this.jsonNode = jsonNode;
    }

    private JsonContent(byte[] rawJsonContent, ObjectMapper mapper) {
        this.rawContent = rawJsonContent;
        this.jsonNode = JsonContent.parseJsonContent(rawJsonContent, mapper);
    }

    private static JsonNode parseJsonContent(byte[] rawJsonContent, ObjectMapper mapper) {
        if (rawJsonContent == null) {
            return mapper.createObjectNode();
        }
        try {
            JsonNode jsonNode = mapper.readTree(rawJsonContent);
            if (jsonNode.isMissingNode()) {
                return mapper.createObjectNode();
            }
            return jsonNode;
        }
        catch (Exception e) {
            LOG.debug((Object)"Unable to parse HTTP response content", (Throwable)e);
            return mapper.createObjectNode();
        }
    }

    public byte[] getRawContent() {
        return this.rawContent;
    }

    public JsonNode getJsonNode() {
        return this.jsonNode;
    }
}

