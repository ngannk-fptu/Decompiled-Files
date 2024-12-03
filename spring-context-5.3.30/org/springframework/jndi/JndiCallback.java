/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.jndi;

import javax.naming.Context;
import javax.naming.NamingException;
import org.springframework.lang.Nullable;

@FunctionalInterface
public interface JndiCallback<T> {
    @Nullable
    public T doInContext(Context var1) throws NamingException;
}

