/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.core.support;

import java.util.Hashtable;
import javax.naming.directory.DirContext;
import org.springframework.ldap.core.support.DirContextAuthenticationStrategy;

public class DigestMd5DirContextAuthenticationStrategy
implements DirContextAuthenticationStrategy {
    private static final String DIGEST_MD5_AUTHENTICATION = "DIGEST-MD5";

    @Override
    public DirContext processContextAfterCreation(DirContext ctx, String userDn, String password) {
        return ctx;
    }

    @Override
    public void setupEnvironment(Hashtable<String, Object> env, String userDn, String password) {
        env.put("java.naming.security.authentication", DIGEST_MD5_AUTHENTICATION);
        env.put("java.naming.security.principal", userDn);
        env.put("java.naming.security.credentials", password);
    }
}

