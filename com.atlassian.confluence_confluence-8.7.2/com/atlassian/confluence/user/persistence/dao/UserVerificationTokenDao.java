/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.user.persistence.dao;

import com.atlassian.confluence.user.UserVerificationToken;
import com.atlassian.confluence.user.UserVerificationTokenType;
import org.springframework.transaction.annotation.Transactional;

public interface UserVerificationTokenDao {
    @Transactional
    public void storeToken(UserVerificationToken var1);

    @Transactional(readOnly=true)
    public UserVerificationToken getToken(String var1, UserVerificationTokenType var2);

    @Transactional
    public void clearToken(String var1, UserVerificationTokenType var2);

    @Transactional(readOnly=true)
    public Iterable<String> getUsernamesByTokenType(UserVerificationTokenType var1);
}

