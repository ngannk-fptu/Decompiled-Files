/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.protocols.json;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.protocols.json.ErrorCodeParser;
import software.amazon.awssdk.protocols.json.StructuredJsonFactory;
import software.amazon.awssdk.protocols.json.StructuredJsonGenerator;
import software.amazon.awssdk.protocols.json.internal.unmarshall.JsonErrorCodeParser;
import software.amazon.awssdk.thirdparty.jackson.core.JsonFactory;

@SdkProtectedApi
public abstract class BaseAwsStructuredJsonFactory
implements StructuredJsonFactory {
    private final JsonFactory jsonFactory;

    protected BaseAwsStructuredJsonFactory(JsonFactory jsonFactory) {
        this.jsonFactory = jsonFactory;
    }

    @Override
    public StructuredJsonGenerator createWriter(String contentType) {
        return this.createWriter(this.jsonFactory, contentType);
    }

    protected abstract StructuredJsonGenerator createWriter(JsonFactory var1, String var2);

    @Override
    public ErrorCodeParser getErrorCodeParser(String customErrorCodeFieldName) {
        return new JsonErrorCodeParser(customErrorCodeFieldName);
    }
}

