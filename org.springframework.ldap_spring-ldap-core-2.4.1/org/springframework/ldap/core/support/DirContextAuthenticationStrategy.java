/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.core.support;

import java.util.Hashtable;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;

public interface DirContextAuthenticationStrategy {
    public void setupEnvironment(Hashtable<String, Object> var1, String var2, String var3) throws NamingException;

    public DirContext processContextAfterCreation(DirContext var1, String var2, String var3) throws NamingException;
}

