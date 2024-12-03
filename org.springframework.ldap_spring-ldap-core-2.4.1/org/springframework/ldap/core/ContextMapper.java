/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.core;

import javax.naming.NamingException;

public interface ContextMapper<T> {
    public T mapFromContext(Object var1) throws NamingException;
}

