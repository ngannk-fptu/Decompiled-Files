/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.descriptor.web.SecurityConstraint
 */
package org.apache.catalina;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.security.Principal;
import java.security.cert.X509Certificate;
import org.apache.catalina.Contained;
import org.apache.catalina.Context;
import org.apache.catalina.CredentialHandler;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSName;

public interface Realm
extends Contained {
    public CredentialHandler getCredentialHandler();

    public void setCredentialHandler(CredentialHandler var1);

    public void addPropertyChangeListener(PropertyChangeListener var1);

    public Principal authenticate(String var1);

    public Principal authenticate(String var1, String var2);

    @Deprecated
    public Principal authenticate(String var1, String var2, String var3, String var4, String var5, String var6, String var7, String var8);

    default public Principal authenticate(String username, String digest, String nonce, String nc, String cnonce, String qop, String realm, String digestA2, String algorithm) {
        return this.authenticate(username, digest, nonce, nc, cnonce, qop, realm, digestA2);
    }

    public Principal authenticate(GSSContext var1, boolean var2);

    default public Principal authenticate(GSSName gssName, GSSCredential gssCredential) {
        return null;
    }

    public Principal authenticate(X509Certificate[] var1);

    public void backgroundProcess();

    public SecurityConstraint[] findSecurityConstraints(Request var1, Context var2);

    public boolean hasResourcePermission(Request var1, Response var2, SecurityConstraint[] var3, Context var4) throws IOException;

    public boolean hasRole(Wrapper var1, Principal var2, String var3);

    public boolean hasUserDataPermission(Request var1, Response var2, SecurityConstraint[] var3) throws IOException;

    public void removePropertyChangeListener(PropertyChangeListener var1);

    @Deprecated
    public String[] getRoles(Principal var1);

    default public boolean isAvailable() {
        return true;
    }
}

