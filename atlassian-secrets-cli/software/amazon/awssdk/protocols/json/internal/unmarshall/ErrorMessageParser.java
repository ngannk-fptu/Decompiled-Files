/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.protocols.json.internal.unmarshall;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;

@SdkInternalApi
public interface ErrorMessageParser {
    public String parseErrorMessage(SdkHttpFullResponse var1, JsonNode var2);
}

