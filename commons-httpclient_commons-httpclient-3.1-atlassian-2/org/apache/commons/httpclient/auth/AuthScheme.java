/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient.auth;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.auth.AuthenticationException;
import org.apache.commons.httpclient.auth.MalformedChallengeException;

public interface AuthScheme {
    public void processChallenge(String var1) throws MalformedChallengeException;

    public String getSchemeName();

    public String getParameter(String var1);

    public String getRealm();

    public String getID();

    public boolean isConnectionBased();

    public boolean isComplete();

    public String authenticate(Credentials var1, String var2, String var3) throws AuthenticationException;

    public String authenticate(Credentials var1, HttpMethod var2) throws AuthenticationException;
}

