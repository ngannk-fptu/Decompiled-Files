/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.http.SdkHttpFullResponse
 */
package software.amazon.awssdk.protocols.json;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.protocols.json.JsonContent;

@SdkProtectedApi
public interface ErrorCodeParser {
    public String parseErrorCode(SdkHttpFullResponse var1, JsonContent var2);
}

