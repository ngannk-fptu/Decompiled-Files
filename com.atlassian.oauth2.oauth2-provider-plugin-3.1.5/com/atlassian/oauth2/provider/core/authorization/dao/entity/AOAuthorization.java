/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Accessor
 *  net.java.ao.Mutator
 *  net.java.ao.RawEntity
 *  net.java.ao.schema.NotNull
 *  net.java.ao.schema.PrimaryKey
 *  net.java.ao.schema.Table
 */
package com.atlassian.oauth2.provider.core.authorization.dao.entity;

import net.java.ao.Accessor;
import net.java.ao.Mutator;
import net.java.ao.RawEntity;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.Table;

@Table(value="AUTHORIZATION")
public interface AOAuthorization
extends RawEntity<String> {
    public static final String AUTHORIZATION_CODE = "AUTHORIZATION_CODE";
    public static final String CLIENT_ID = "CLIENT_ID";
    public static final String REDIRECT_URI = "REDIRECT_URI";
    public static final String USER_KEY = "USER_KEY";
    public static final String CREATED_AT = "CREATED_AT";
    public static final String SCOPE = "SCOPE";
    public static final String CODE_CHALLENGE_METHOD = "CODE_CHALLENGE_METHOD";
    public static final String CODE_CHALLENGE = "CODE_CHALLENGE";

    @NotNull
    @PrimaryKey
    public String getAuthorizationCode();

    public void setAuthorizationCode(String var1);

    @NotNull
    public String getClientId();

    public void setClientId(String var1);

    @NotNull
    @Accessor(value="REDIRECT_URI")
    public String getRedirectUri();

    @Mutator(value="REDIRECT_URI")
    public void setRedirectUri(String var1);

    @NotNull
    public String getUserKey();

    public void setUserKey(String var1);

    @NotNull
    public Long getCreatedAt();

    public void setCreatedAt(Long var1);

    @NotNull
    public String getScope();

    public void setScope(String var1);

    public String getCodeChallengeMethod();

    public void setCodeChallengeMethod(String var1);

    public String getCodeChallenge();

    public void setCodeChallenge(String var1);
}

