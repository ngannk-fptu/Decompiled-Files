/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 */
package org.apache.hc.client5.http.auth;

import java.io.Serializable;
import java.security.Principal;
import org.apache.hc.client5.http.auth.Credentials;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.ietf.jgss.GSSCredential;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
public class KerberosCredentials
implements Credentials,
Serializable {
    private static final long serialVersionUID = 487421613855550713L;
    private final GSSCredential gssCredential;

    public KerberosCredentials(GSSCredential gssCredential) {
        this.gssCredential = gssCredential;
    }

    public GSSCredential getGSSCredential() {
        return this.gssCredential;
    }

    @Override
    public Principal getUserPrincipal() {
        return null;
    }

    @Override
    public char[] getPassword() {
        return null;
    }
}

