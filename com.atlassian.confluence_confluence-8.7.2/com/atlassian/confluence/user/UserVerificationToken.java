/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.security.utils.ConstantTimeComparison
 *  com.google.common.base.Preconditions
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.user;

import com.atlassian.confluence.user.UserVerificationTokenType;
import com.atlassian.security.utils.ConstantTimeComparison;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.Date;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonAutoDetect
public class UserVerificationToken
implements Serializable {
    @JsonProperty
    private String userName;
    @JsonProperty
    private UserVerificationTokenType tokenType;
    @JsonProperty
    private String tokenString;
    @JsonProperty
    private Date issueDate;

    public UserVerificationToken() {
    }

    public UserVerificationToken(UserVerificationTokenType tokenType, String userName, String tokenString, Date issueDate) {
        this.userName = (String)Preconditions.checkNotNull((Object)userName);
        this.tokenType = (UserVerificationTokenType)((Object)Preconditions.checkNotNull((Object)((Object)tokenType)));
        this.tokenString = (String)Preconditions.checkNotNull((Object)tokenString);
        this.issueDate = new Date(((Date)Preconditions.checkNotNull((Object)issueDate)).getTime());
    }

    public String getUserName() {
        return this.userName;
    }

    public UserVerificationTokenType getTokenType() {
        return this.tokenType;
    }

    public String getTokenString() {
        return this.tokenString;
    }

    public Date getIssueDate() {
        return new Date(this.issueDate.getTime());
    }

    public boolean wasIssuedAfter(Date date) {
        return this.issueDate.after(date);
    }

    public boolean matchesToken(String tokenStr) {
        return ConstantTimeComparison.isEqual((String)this.tokenString, (String)tokenStr);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        UserVerificationToken token = (UserVerificationToken)o;
        return this.userName.equals(token.userName) && this.tokenType == token.tokenType && this.tokenString.equals(token.tokenString) && this.issueDate.equals(token.issueDate);
    }

    public int hashCode() {
        return this.tokenString.hashCode();
    }

    public String toString() {
        return "UserVerificationToken{issueDate=" + this.issueDate + ", userName='" + this.userName + "', tokenType=" + this.tokenType + ", tokenString='" + this.tokenString + "'}";
    }
}

