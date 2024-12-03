/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.password;

public interface PasswordEncoder {
    public String encodePassword(String var1) throws IllegalArgumentException;

    public boolean isValidPassword(String var1, String var2) throws IllegalArgumentException;

    public boolean canDecodePassword(String var1);
}

