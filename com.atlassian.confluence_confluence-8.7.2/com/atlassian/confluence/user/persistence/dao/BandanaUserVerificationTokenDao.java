/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 */
package com.atlassian.confluence.user.persistence.dao;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.user.UserVerificationToken;
import com.atlassian.confluence.user.UserVerificationTokenType;
import com.atlassian.confluence.user.persistence.dao.UserVerificationTokenDao;

public class BandanaUserVerificationTokenDao
implements UserVerificationTokenDao {
    private final BandanaManager bandanaManager;

    public BandanaUserVerificationTokenDao(BandanaManager bandanaManager) {
        this.bandanaManager = bandanaManager;
    }

    @Override
    public void storeToken(UserVerificationToken token) {
        BandanaContext context = BandanaUserVerificationTokenDao.getBandanaContext(token.getTokenType());
        this.bandanaManager.setValue(context, token.getUserName(), (Object)token);
    }

    @Override
    public UserVerificationToken getToken(String userName, UserVerificationTokenType tokenType) {
        BandanaContext context = BandanaUserVerificationTokenDao.getBandanaContext(tokenType);
        return (UserVerificationToken)this.bandanaManager.getValue(context, userName);
    }

    @Override
    public void clearToken(String userName, UserVerificationTokenType tokenType) {
        BandanaContext context = BandanaUserVerificationTokenDao.getBandanaContext(tokenType);
        this.bandanaManager.removeValue(context, userName);
    }

    @Override
    public Iterable<String> getUsernamesByTokenType(UserVerificationTokenType tokenType) {
        BandanaContext context = BandanaUserVerificationTokenDao.getBandanaContext(tokenType);
        return this.bandanaManager.getKeys(context);
    }

    private static BandanaContext getBandanaContext(UserVerificationTokenType tokenType) {
        return new ConfluenceBandanaContext(tokenType.getContextName());
    }
}

