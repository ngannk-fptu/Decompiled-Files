/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.authentication;

public interface AuthenticationDetailsSource<C, T> {
    public T buildDetails(C var1);
}

