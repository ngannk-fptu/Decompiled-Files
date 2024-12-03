/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.http;

import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;

public interface HTTPEndpoint {
    public HTTPResponse process(HTTPRequest var1);
}

