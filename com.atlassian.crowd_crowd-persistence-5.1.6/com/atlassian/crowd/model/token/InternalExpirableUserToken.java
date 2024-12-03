/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.token.ExpirableUserToken
 *  com.atlassian.crowd.model.token.ExpirableUserTokenType
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.model.token;

import com.atlassian.crowd.model.token.ExpirableUserToken;
import com.atlassian.crowd.model.token.ExpirableUserTokenType;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import javax.annotation.Nullable;

public final class InternalExpirableUserToken
implements Serializable,
ExpirableUserToken {
    private long id;
    private String token;
    @Nullable
    private String username;
    @Nullable
    private String emailAddress;
    private long expiryDate;
    private long directoryId;
    private ExpirableUserTokenType type;

    InternalExpirableUserToken() {
    }

    public static InternalExpirableUserToken createResetPasswordToken(String token, String username, String email, long expiryDate, long directoryId) {
        return new InternalExpirableUserToken(token, username, email, expiryDate, directoryId, ExpirableUserTokenType.UNSPECIFIED);
    }

    public static InternalExpirableUserToken createInviteUserToken(String token, String email, long expiryDate, long directoryId) {
        return new InternalExpirableUserToken(token, null, email, expiryDate, directoryId, ExpirableUserTokenType.UNSPECIFIED);
    }

    public static InternalExpirableUserToken createChangeEmailToken(String token, String username, String newEmailAddress, long expiryDate, long directoryId) {
        return new InternalExpirableUserToken(token, username, newEmailAddress, expiryDate, directoryId, ExpirableUserTokenType.VALIDATE_EMAIL);
    }

    private InternalExpirableUserToken(String token, @Nullable String username, @Nullable String emailAddress, long expiryDate, long directoryId, ExpirableUserTokenType type) {
        this.token = (String)Preconditions.checkNotNull((Object)token, (Object)"token must not be null");
        this.username = username;
        this.emailAddress = emailAddress;
        this.expiryDate = expiryDate;
        this.directoryId = directoryId;
        this.type = type;
    }

    public long getId() {
        return this.id;
    }

    private void setId(long id) {
        this.id = id;
    }

    public String getToken() {
        return this.token;
    }

    private void setToken(String token) {
        this.token = (String)Preconditions.checkNotNull((Object)token, (Object)"token must not be null");
    }

    @Nullable
    public String getUsername() {
        return this.username;
    }

    private void setUsername(@Nullable String username) {
        this.username = username;
    }

    @Nullable
    public String getEmailAddress() {
        return this.emailAddress;
    }

    private void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public long getExpiryDate() {
        return this.expiryDate;
    }

    private void setExpiryDate(long expiryDate) {
        this.expiryDate = expiryDate;
    }

    public long getDirectoryId() {
        return this.directoryId;
    }

    private void setDirectoryId(long directoryId) {
        this.directoryId = directoryId;
    }

    public ExpirableUserTokenType getType() {
        return this.type;
    }

    public void setType(ExpirableUserTokenType type) {
        this.type = type;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        InternalExpirableUserToken that = (InternalExpirableUserToken)o;
        return !(this.token != null ? !this.token.equals(that.token) : that.token != null);
    }

    public int hashCode() {
        return this.token != null ? this.token.hashCode() : 0;
    }
}

