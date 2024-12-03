/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina;

import java.security.Principal;
import java.util.Collections;
import java.util.Enumeration;
import org.ietf.jgss.GSSCredential;

public interface TomcatPrincipal
extends Principal {
    public Principal getUserPrincipal();

    public GSSCredential getGssCredential();

    public void logout() throws Exception;

    default public Object getAttribute(String name) {
        return null;
    }

    default public Enumeration<String> getAttributeNames() {
        return Collections.emptyEnumeration();
    }
}

