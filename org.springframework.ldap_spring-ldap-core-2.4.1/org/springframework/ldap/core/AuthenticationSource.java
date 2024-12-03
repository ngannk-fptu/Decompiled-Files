/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.core;

public interface AuthenticationSource {
    public String getPrincipal();

    public String getCredentials();
}

