/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.auth;

import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.Credentials;
import org.apache.hc.core5.http.protocol.HttpContext;

public interface CredentialsProvider {
    public Credentials getCredentials(AuthScope var1, HttpContext var2);
}

