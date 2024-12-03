/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.authentication;

abstract class AuthenticationUtil {
    static String getLoginPath(String authMount) {
        return String.format("auth/%s/login", authMount);
    }

    private AuthenticationUtil() {
    }
}

