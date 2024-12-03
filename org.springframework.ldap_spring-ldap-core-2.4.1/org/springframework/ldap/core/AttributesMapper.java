/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.core;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

public interface AttributesMapper<T> {
    public T mapFromAttributes(Attributes var1) throws NamingException;
}

