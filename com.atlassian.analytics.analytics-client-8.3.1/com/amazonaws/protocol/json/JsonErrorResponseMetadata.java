/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.protocol.json;

import com.amazonaws.annotation.NotThreadSafe;
import com.amazonaws.annotation.SdkProtectedApi;
import com.amazonaws.protocol.json.JsonErrorShapeMetadata;
import java.util.List;

@NotThreadSafe
@SdkProtectedApi
public class JsonErrorResponseMetadata {
    private String customErrorCodeFieldName;
    private List<JsonErrorShapeMetadata> errorShapes;

    public String getCustomErrorCodeFieldName() {
        return this.customErrorCodeFieldName;
    }

    public JsonErrorResponseMetadata withCustomErrorCodeFieldName(String errorCodeFieldName) {
        this.customErrorCodeFieldName = errorCodeFieldName;
        return this;
    }

    public List<JsonErrorShapeMetadata> getErrorShapes() {
        return this.errorShapes;
    }

    public JsonErrorResponseMetadata withErrorShapes(List<JsonErrorShapeMetadata> errorShapes) {
        this.errorShapes = errorShapes;
        return this;
    }
}

