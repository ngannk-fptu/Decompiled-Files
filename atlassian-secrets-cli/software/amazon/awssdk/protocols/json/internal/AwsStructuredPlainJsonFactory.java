/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.protocols.json.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.protocols.json.BaseAwsStructuredJsonFactory;
import software.amazon.awssdk.protocols.json.SdkJsonGenerator;
import software.amazon.awssdk.protocols.json.StructuredJsonGenerator;
import software.amazon.awssdk.protocols.jsoncore.JsonNodeParser;
import software.amazon.awssdk.thirdparty.jackson.core.JsonFactory;

@SdkInternalApi
public final class AwsStructuredPlainJsonFactory {
    private static final JsonFactory JSON_FACTORY = new JsonFactory();
    public static final BaseAwsStructuredJsonFactory SDK_JSON_FACTORY = new BaseAwsStructuredJsonFactory(JSON_FACTORY){

        @Override
        protected StructuredJsonGenerator createWriter(JsonFactory jsonFactory, String contentType) {
            return new SdkJsonGenerator(jsonFactory, contentType);
        }

        @Override
        public JsonFactory getJsonFactory() {
            return JsonNodeParser.DEFAULT_JSON_FACTORY;
        }
    };

    protected AwsStructuredPlainJsonFactory() {
    }
}

