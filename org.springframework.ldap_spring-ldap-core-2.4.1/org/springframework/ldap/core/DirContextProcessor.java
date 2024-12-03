/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.core;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;

public interface DirContextProcessor {
    public void preProcess(DirContext var1) throws NamingException;

    public void postProcess(DirContext var1) throws NamingException;
}

