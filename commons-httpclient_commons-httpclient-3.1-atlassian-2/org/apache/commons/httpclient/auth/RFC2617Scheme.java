/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient.auth;

import java.util.Locale;
import java.util.Map;
import org.apache.commons.httpclient.auth.AuthChallengeParser;
import org.apache.commons.httpclient.auth.AuthScheme;
import org.apache.commons.httpclient.auth.MalformedChallengeException;

public abstract class RFC2617Scheme
implements AuthScheme {
    private Map params = null;

    public RFC2617Scheme() {
    }

    public RFC2617Scheme(String challenge) throws MalformedChallengeException {
        this.processChallenge(challenge);
    }

    @Override
    public void processChallenge(String challenge) throws MalformedChallengeException {
        String s = AuthChallengeParser.extractScheme(challenge);
        if (!s.equalsIgnoreCase(this.getSchemeName())) {
            throw new MalformedChallengeException("Invalid " + this.getSchemeName() + " challenge: " + challenge);
        }
        this.params = AuthChallengeParser.extractParams(challenge);
    }

    protected Map getParameters() {
        return this.params;
    }

    @Override
    public String getParameter(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Parameter name may not be null");
        }
        if (this.params == null) {
            return null;
        }
        return (String)this.params.get(name.toLowerCase(Locale.ENGLISH));
    }

    @Override
    public String getRealm() {
        return this.getParameter("realm");
    }

    @Override
    public String getID() {
        return this.getRealm();
    }
}

