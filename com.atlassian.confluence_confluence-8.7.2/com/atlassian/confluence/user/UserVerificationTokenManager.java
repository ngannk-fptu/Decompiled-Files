/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.user;

import com.atlassian.confluence.user.UserVerificationToken;
import com.atlassian.confluence.user.UserVerificationTokenType;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface UserVerificationTokenManager {
    public String generateAndSaveToken(String var1, UserVerificationTokenType var2);

    public boolean hasToken(String var1, UserVerificationTokenType var2);

    public boolean hasValidUserToken(String var1, UserVerificationTokenType var2, String var3);

    public boolean isFresh(UserVerificationToken var1);

    public void clearToken(String var1, UserVerificationTokenType var2);

    public boolean hasOutdatedUserToken(String var1, UserVerificationTokenType var2);

    public void clearToken(String var1);

    public int clearAllExpiredTokens();
}

