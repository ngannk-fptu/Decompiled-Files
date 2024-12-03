/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.protocols.json;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.protocols.json.ErrorCodeParser;
import software.amazon.awssdk.protocols.json.StructuredJsonGenerator;
import software.amazon.awssdk.thirdparty.jackson.core.JsonFactory;

@SdkProtectedApi
public interface StructuredJsonFactory {
    public StructuredJsonGenerator createWriter(String var1);

    public JsonFactory getJsonFactory();

    public ErrorCodeParser getErrorCodeParser(String var1);
}

