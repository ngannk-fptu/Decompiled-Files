/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.core.support;

import org.springframework.ldap.core.LdapOperations;

public interface LdapOperationsCallback<T> {
    public T doWithLdapOperations(LdapOperations var1);
}

