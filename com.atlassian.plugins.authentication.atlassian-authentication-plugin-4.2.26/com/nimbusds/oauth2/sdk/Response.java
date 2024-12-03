/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.oauth2.sdk.Message;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;

public interface Response
extends Message {
    public boolean indicatesSuccess();

    public HTTPResponse toHTTPResponse();
}

