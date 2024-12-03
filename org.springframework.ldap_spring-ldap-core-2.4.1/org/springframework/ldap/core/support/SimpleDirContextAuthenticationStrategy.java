/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.core.support;

import java.util.Hashtable;
import javax.naming.directory.DirContext;
import org.springframework.ldap.core.support.DirContextAuthenticationStrategy;

public class SimpleDirContextAuthenticationStrategy
implements DirContextAuthenticationStrategy {
    private static final String SIMPLE_AUTHENTICATION = "simple";

    @Override
    public void setupEnvironment(Hashtable<String, Object> env, String userDn, String password) {
        env.put("java.naming.security.authentication", SIMPLE_AUTHENTICATION);
        env.put("java.naming.security.principal", userDn);
        env.put("java.naming.security.credentials", password);
    }

    @Override
    public DirContext processContextAfterCreation(DirContext ctx, String userDn, String password) {
        return ctx;
    }
}

