/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.core;

import javax.naming.directory.DirContext;
import org.springframework.ldap.NamingException;

public interface ContextSource {
    public DirContext getReadOnlyContext() throws NamingException;

    public DirContext getReadWriteContext() throws NamingException;

    public DirContext getContext(String var1, String var2) throws NamingException;
}

