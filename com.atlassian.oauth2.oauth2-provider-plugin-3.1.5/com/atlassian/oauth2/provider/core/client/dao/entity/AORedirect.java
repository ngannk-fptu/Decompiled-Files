/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Accessor
 *  net.java.ao.Mutator
 *  net.java.ao.RawEntity
 *  net.java.ao.schema.AutoIncrement
 *  net.java.ao.schema.NotNull
 *  net.java.ao.schema.PrimaryKey
 *  net.java.ao.schema.StringLength
 *  net.java.ao.schema.Table
 */
package com.atlassian.oauth2.provider.core.client.dao.entity;

import net.java.ao.Accessor;
import net.java.ao.Mutator;
import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.StringLength;
import net.java.ao.schema.Table;

@Table(value="REDIRECT_URI")
public interface AORedirect
extends RawEntity<Integer> {
    public static final String URI = "URI";
    public static final String CLIENT_ID = "CLIENT_ID";

    @AutoIncrement
    @NotNull
    @PrimaryKey
    public Integer getId();

    @Accessor(value="URI")
    @NotNull
    public String getUri();

    @Mutator(value="URI")
    @StringLength(value=450)
    public void setUri(String var1);

    @NotNull
    public String getClientId();

    public void setClientId();
}

