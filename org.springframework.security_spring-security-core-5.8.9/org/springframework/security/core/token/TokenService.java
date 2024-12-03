/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.core.token;

import org.springframework.security.core.token.Token;

public interface TokenService {
    public Token allocateToken(String var1);

    public Token verifyToken(String var1);
}

