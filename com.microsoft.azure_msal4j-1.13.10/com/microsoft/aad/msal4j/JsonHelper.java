/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonInclude$Include
 *  com.fasterxml.jackson.core.JsonParser$Feature
 *  com.fasterxml.jackson.databind.DeserializationFeature
 *  com.fasterxml.jackson.databind.JsonNode
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.fasterxml.jackson.databind.node.ObjectNode
 */
package com.microsoft.aad.msal4j;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.microsoft.aad.msal4j.ClaimsRequest;
import com.microsoft.aad.msal4j.MsalClientException;
import com.microsoft.aad.msal4j.RequestedClaimAdditionalInfo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

class JsonHelper {
    static ObjectMapper mapper = new ObjectMapper();

    private JsonHelper() {
    }

    static <T> T convertJsonToObject(String json, Class<T> tClass) {
        try {
            return (T)mapper.readValue(json, tClass);
        }
        catch (IOException e) {
            throw new MsalClientException(e);
        }
    }

    static void validateJsonFormat(String jsonString) {
        try {
            mapper.readTree(jsonString);
        }
        catch (IOException e) {
            throw new MsalClientException(e.getMessage(), "invalid_json");
        }
    }

    public static String formCapabilitiesJson(Set<String> clientCapabilities) {
        if (clientCapabilities != null && !clientCapabilities.isEmpty()) {
            ClaimsRequest cr = new ClaimsRequest();
            RequestedClaimAdditionalInfo capabilitiesValues = new RequestedClaimAdditionalInfo(false, null, new ArrayList<String>(clientCapabilities));
            cr.requestClaimInAccessToken("xms_cc", capabilitiesValues);
            return cr.formatAsJSONString();
        }
        return null;
    }

    static String mergeJSONString(String mainJsonString, String addJsonString) {
        JsonNode addJson;
        JsonNode mainJson;
        try {
            mainJson = mapper.readTree(mainJsonString);
            addJson = mapper.readTree(addJsonString);
        }
        catch (IOException e) {
            throw new MsalClientException(e.getMessage(), "invalid_json");
        }
        JsonHelper.mergeJSONNode(mainJson, addJson);
        return mainJson.toString();
    }

    static void mergeJSONNode(JsonNode mainNode, JsonNode addNode) {
        if (addNode == null) {
            return;
        }
        Iterator fieldNames = addNode.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = (String)fieldNames.next();
            JsonNode jsonNode = mainNode.get(fieldName);
            if (jsonNode != null && jsonNode.isObject()) {
                JsonHelper.mergeJSONNode(jsonNode, addNode.get(fieldName));
                continue;
            }
            if (!(mainNode instanceof ObjectNode)) continue;
            JsonNode value = addNode.get(fieldName);
            ((ObjectNode)mainNode).put(fieldName, value);
        }
    }

    static {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
}

