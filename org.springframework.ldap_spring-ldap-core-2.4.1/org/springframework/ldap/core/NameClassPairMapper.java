/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.core;

import javax.naming.NameClassPair;
import javax.naming.NamingException;

public interface NameClassPairMapper<T> {
    public T mapFromNameClassPair(NameClassPair var1) throws NamingException;
}

