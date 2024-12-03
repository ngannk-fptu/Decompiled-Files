/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.protocol.json;

import com.amazonaws.annotation.SdkProtectedApi;
import com.amazonaws.http.JsonErrorResponseHandler;
import com.amazonaws.http.JsonResponseHandler;
import com.amazonaws.protocol.json.JsonOperationMetadata;
import com.amazonaws.protocol.json.StructuredJsonGenerator;
import com.amazonaws.transform.JsonErrorUnmarshaller;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;
import java.util.List;

@SdkProtectedApi
public interface SdkStructuredJsonFactory {
    public StructuredJsonGenerator createWriter(String var1);

    public <T> JsonResponseHandler<T> createResponseHandler(JsonOperationMetadata var1, Unmarshaller<T, JsonUnmarshallerContext> var2);

    public JsonErrorResponseHandler createErrorResponseHandler(List<JsonErrorUnmarshaller> var1, String var2);
}

