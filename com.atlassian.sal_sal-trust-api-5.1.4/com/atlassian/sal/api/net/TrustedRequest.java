/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.net.Request
 */
package com.atlassian.sal.api.net;

import com.atlassian.sal.api.net.Request;

public interface TrustedRequest
extends Request {
    public TrustedRequest addTrustedTokenAuthentication(String var1);

    public TrustedRequest addTrustedTokenAuthentication(String var1, String var2);
}

