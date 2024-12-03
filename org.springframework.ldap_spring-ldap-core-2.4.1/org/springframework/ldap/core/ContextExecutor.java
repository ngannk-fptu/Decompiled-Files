/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.core;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;

public interface ContextExecutor<T> {
    public T executeWithContext(DirContext var1) throws NamingException;
}

