/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.internal.http;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.http.HttpResponse;
import com.amazonaws.protocol.json.JsonContent;

@SdkInternalApi
public interface ErrorCodeParser {
    public String parseErrorCode(HttpResponse var1, JsonContent var2);
}

