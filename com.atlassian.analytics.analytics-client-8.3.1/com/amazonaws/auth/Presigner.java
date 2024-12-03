/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.auth;

import com.amazonaws.SignableRequest;
import com.amazonaws.auth.AWSCredentials;
import java.util.Date;

public interface Presigner {
    public void presignRequest(SignableRequest<?> var1, AWSCredentials var2, Date var3);
}

