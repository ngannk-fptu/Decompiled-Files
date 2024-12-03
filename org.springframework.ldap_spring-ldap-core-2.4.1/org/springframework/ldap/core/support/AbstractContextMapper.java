/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.core.support;

import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;

public abstract class AbstractContextMapper<T>
implements ContextMapper<T> {
    @Override
    public final T mapFromContext(Object ctx) {
        return this.doMapFromContext((DirContextOperations)ctx);
    }

    protected abstract T doMapFromContext(DirContextOperations var1);
}

