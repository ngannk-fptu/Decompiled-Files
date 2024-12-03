/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.httpclient.auth;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.auth.AuthChallengeParser;
import org.apache.commons.httpclient.auth.AuthScheme;
import org.apache.commons.httpclient.auth.AuthenticationException;
import org.apache.commons.httpclient.auth.InvalidCredentialsException;
import org.apache.commons.httpclient.auth.MalformedChallengeException;
import org.apache.commons.httpclient.auth.NTLM;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NTLMScheme
implements AuthScheme {
    private static final Log LOG = LogFactory.getLog(NTLMScheme.class);
    private String ntlmchallenge = null;
    private static final int UNINITIATED = 0;
    private static final int INITIATED = 1;
    private static final int TYPE1_MSG_GENERATED = 2;
    private static final int TYPE2_MSG_RECEIVED = 3;
    private static final int TYPE3_MSG_GENERATED = 4;
    private static final int FAILED = Integer.MAX_VALUE;
    private int state;

    public NTLMScheme() {
        this.state = 0;
    }

    public NTLMScheme(String challenge) throws MalformedChallengeException {
        this.processChallenge(challenge);
    }

    @Override
    public void processChallenge(String challenge) throws MalformedChallengeException {
        String s = AuthChallengeParser.extractScheme(challenge);
        if (!s.equalsIgnoreCase(this.getSchemeName())) {
            throw new MalformedChallengeException("Invalid NTLM challenge: " + challenge);
        }
        int i = challenge.indexOf(32);
        if (i != -1) {
            s = challenge.substring(i, challenge.length());
            this.ntlmchallenge = s.trim();
            this.state = 3;
        } else {
            this.ntlmchallenge = "";
            this.state = this.state == 0 ? 1 : Integer.MAX_VALUE;
        }
    }

    @Override
    public boolean isComplete() {
        return this.state == 4 || this.state == Integer.MAX_VALUE;
    }

    @Override
    public String getSchemeName() {
        return "ntlm";
    }

    @Override
    public String getRealm() {
        return null;
    }

    @Override
    public String getID() {
        return this.ntlmchallenge;
    }

    @Override
    public String getParameter(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Parameter name may not be null");
        }
        return null;
    }

    @Override
    public boolean isConnectionBased() {
        return true;
    }

    public static String authenticate(NTCredentials credentials, String challenge) throws AuthenticationException {
        LOG.trace((Object)"enter NTLMScheme.authenticate(NTCredentials, String)");
        if (credentials == null) {
            throw new IllegalArgumentException("Credentials may not be null");
        }
        NTLM ntlm = new NTLM();
        String s = ntlm.getResponseFor(challenge, credentials.getUserName(), credentials.getPassword(), credentials.getHost(), credentials.getDomain());
        return "NTLM " + s;
    }

    public static String authenticate(NTCredentials credentials, String challenge, String charset) throws AuthenticationException {
        LOG.trace((Object)"enter NTLMScheme.authenticate(NTCredentials, String)");
        if (credentials == null) {
            throw new IllegalArgumentException("Credentials may not be null");
        }
        NTLM ntlm = new NTLM();
        ntlm.setCredentialCharset(charset);
        String s = ntlm.getResponseFor(challenge, credentials.getUserName(), credentials.getPassword(), credentials.getHost(), credentials.getDomain());
        return "NTLM " + s;
    }

    @Override
    public String authenticate(Credentials credentials, String method, String uri) throws AuthenticationException {
        LOG.trace((Object)"enter NTLMScheme.authenticate(Credentials, String, String)");
        NTCredentials ntcredentials = null;
        try {
            ntcredentials = (NTCredentials)credentials;
        }
        catch (ClassCastException e) {
            throw new InvalidCredentialsException("Credentials cannot be used for NTLM authentication: " + credentials.getClass().getName());
        }
        return NTLMScheme.authenticate(ntcredentials, this.ntlmchallenge);
    }

    @Override
    public String authenticate(Credentials credentials, HttpMethod method) throws AuthenticationException {
        LOG.trace((Object)"enter NTLMScheme.authenticate(Credentials, HttpMethod)");
        if (this.state == 0) {
            throw new IllegalStateException("NTLM authentication process has not been initiated");
        }
        NTCredentials ntcredentials = null;
        try {
            ntcredentials = (NTCredentials)credentials;
        }
        catch (ClassCastException e) {
            throw new InvalidCredentialsException("Credentials cannot be used for NTLM authentication: " + credentials.getClass().getName());
        }
        NTLM ntlm = new NTLM();
        ntlm.setCredentialCharset(method.getParams().getCredentialCharset());
        String response = null;
        if (this.state == 1 || this.state == Integer.MAX_VALUE) {
            response = ntlm.getType1Message(ntcredentials.getHost(), ntcredentials.getDomain());
            this.state = 2;
        } else {
            response = ntlm.getType3Message(ntcredentials.getUserName(), ntcredentials.getPassword(), ntcredentials.getHost(), ntcredentials.getDomain(), ntlm.parseType2Message(this.ntlmchallenge));
            this.state = 4;
        }
        return "NTLM " + response;
    }
}

