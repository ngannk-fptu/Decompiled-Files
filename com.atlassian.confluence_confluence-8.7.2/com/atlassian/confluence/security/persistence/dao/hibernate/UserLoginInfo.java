/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.confluence.security.persistence.dao.hibernate;

import com.atlassian.confluence.security.login.HistoricalLoginInfo;
import com.atlassian.confluence.user.ConfluenceUser;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.Date;

public class UserLoginInfo
implements Serializable,
HistoricalLoginInfo {
    private long id;
    private int currentFailedLoginCount;
    private int totalFailedLoginCount;
    private Date lastSuccessfulLoginDate;
    private Date previousSuccessfulLoginDate;
    private Date lastFailedLoginDate;
    private ConfluenceUser user;

    public UserLoginInfo() {
    }

    public UserLoginInfo(ConfluenceUser user) {
        this.user = (ConfluenceUser)Preconditions.checkNotNull((Object)user);
    }

    public void successfulLogin(Date date) {
        this.previousSuccessfulLoginDate = this.lastSuccessfulLoginDate;
        this.lastSuccessfulLoginDate = date;
        this.resetFailedLoginCount();
    }

    public void failedLogin(Date date) {
        ++this.totalFailedLoginCount;
        ++this.currentFailedLoginCount;
        this.lastFailedLoginDate = date;
    }

    public void resetFailedLoginCount() {
        this.currentFailedLoginCount = 0;
    }

    @Override
    public Date getPreviousSuccessfulLoginDate() {
        return this.previousSuccessfulLoginDate;
    }

    public long getId() {
        return this.id;
    }

    public ConfluenceUser getUser() {
        return this.user;
    }

    @Override
    public int getCurrentFailedLoginCount() {
        return this.currentFailedLoginCount;
    }

    @Override
    public int getTotalFailedLoginCount() {
        return this.totalFailedLoginCount;
    }

    @Override
    public Date getLastSuccessfulLoginDate() {
        return this.lastSuccessfulLoginDate;
    }

    @Override
    public Date getLastFailedLoginDate() {
        return this.lastFailedLoginDate;
    }

    public void setCurrentFailedLoginCount(int currentFailedLoginCount) {
        this.currentFailedLoginCount = currentFailedLoginCount;
    }

    public void setTotalFailedLoginCount(int totalFailedLoginCount) {
        this.totalFailedLoginCount = totalFailedLoginCount;
    }

    public void setLastSuccessfulLoginDate(Date lastSuccessfulLoginDate) {
        this.lastSuccessfulLoginDate = lastSuccessfulLoginDate;
    }

    public void setPreviousSuccessfulLoginDate(Date previousSuccessfulLoginDate) {
        this.previousSuccessfulLoginDate = previousSuccessfulLoginDate;
    }

    public void setLastFailedLoginDate(Date lastFailedLoginDate) {
        this.lastFailedLoginDate = lastFailedLoginDate;
    }
}

