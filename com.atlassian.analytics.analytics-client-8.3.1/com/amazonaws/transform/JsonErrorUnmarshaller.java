/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.transform;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.annotation.ThreadSafe;
import com.amazonaws.transform.AbstractErrorUnmarshaller;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.node.NullNode;

@SdkInternalApi
@ThreadSafe
public class JsonErrorUnmarshaller
extends AbstractErrorUnmarshaller<JsonNode> {
    public static final JsonErrorUnmarshaller DEFAULT_UNMARSHALLER = new JsonErrorUnmarshaller(AmazonServiceException.class, null);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final String handledErrorCode;

    public JsonErrorUnmarshaller(Class<? extends AmazonServiceException> exceptionClass, String handledErrorCode) {
        super(exceptionClass);
        this.handledErrorCode = handledErrorCode;
    }

    @Override
    public AmazonServiceException unmarshall(JsonNode jsonContent) throws Exception {
        if (jsonContent == null || NullNode.instance.equals(jsonContent)) {
            return null;
        }
        return (AmazonServiceException)MAPPER.treeToValue((TreeNode)jsonContent, this.exceptionClass);
    }

    public boolean matchErrorCode(String actualErrorCode) {
        if (this.handledErrorCode == null) {
            return true;
        }
        return this.handledErrorCode.equals(actualErrorCode);
    }

    static {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            MAPPER.setPropertyNamingStrategy(PropertyNamingStrategies.UPPER_CAMEL_CASE);
        }
        catch (LinkageError e) {
            MAPPER.setPropertyNamingStrategy(PropertyNamingStrategy.PASCAL_CASE_TO_CAMEL_CASE);
        }
    }
}

