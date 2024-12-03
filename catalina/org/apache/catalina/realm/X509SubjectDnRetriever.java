/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.realm;

import java.security.cert.X509Certificate;
import org.apache.catalina.realm.X509UsernameRetriever;

public class X509SubjectDnRetriever
implements X509UsernameRetriever {
    @Override
    public String getUsername(X509Certificate clientCert) {
        return clientCert.getSubjectX500Principal().toString();
    }
}

