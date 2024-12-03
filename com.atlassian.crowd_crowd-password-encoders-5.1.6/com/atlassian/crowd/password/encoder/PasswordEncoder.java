/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.password.encoder;

import com.atlassian.crowd.exception.PasswordEncoderException;

public interface PasswordEncoder {
    public String encodePassword(String var1, Object var2) throws PasswordEncoderException;

    public boolean isPasswordValid(String var1, String var2, Object var3);

    public String getKey();
}

