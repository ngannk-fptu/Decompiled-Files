/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.RawEntity
 *  net.java.ao.schema.NotNull
 *  net.java.ao.schema.PrimaryKey
 *  net.java.ao.schema.Table
 */
package com.atlassian.oauth2.provider.core.token.access.dao.entity;

import net.java.ao.RawEntity;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.Table;

@Table(value="ACCESS_TOKEN")
public interface AOAccessToken
extends RawEntity<String> {
    @PrimaryKey(value="ID")
    @NotNull
    public String getId();

    public void setId(String var1);

    @NotNull
    public String getClientId();

    public void setClientId(String var1);

    @NotNull
    public String getUserKey();

    public void setUserKey(String var1);

    @NotNull
    public String getAuthorizationCode();

    public void setAuthorizationCode(String var1);

    @NotNull
    public String getScope();

    public void setScope(String var1);

    @NotNull
    public Long getAuthorizationDate();

    public void setAuthorizationDate(Long var1);

    @NotNull
    public Long getCreatedAt();

    public void setCreatedAt(Long var1);

    public Long getLastAccessed();

    public void setLastAccessed(Long var1);

    public static interface MetaData {
        public static final String AUTHORIZATION_DATE = "AUTHORIZATION_DATE";
        public static final String CREATED_AT = "CREATED_AT";
        public static final String LAST_ACCESSED = "LAST_ACCESSED";
    }

    public static interface CoreField {
        public static final String ID = "ID";
        public static final String CLIENT_ID = "CLIENT_ID";
        public static final String USER_KEY = "USER_KEY";
        public static final String AUTHORIZATION_CODE = "AUTHORIZATION_CODE";
        public static final String SCOPE = "SCOPE";
    }
}

