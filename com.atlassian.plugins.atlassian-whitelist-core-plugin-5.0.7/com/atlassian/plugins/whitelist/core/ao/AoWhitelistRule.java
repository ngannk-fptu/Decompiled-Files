/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.whitelist.WhitelistRule
 *  com.atlassian.plugins.whitelist.WhitelistType
 *  net.java.ao.Accessor
 *  net.java.ao.Mutator
 *  net.java.ao.Preload
 *  net.java.ao.RawEntity
 *  net.java.ao.schema.AutoIncrement
 *  net.java.ao.schema.Default
 *  net.java.ao.schema.NotNull
 *  net.java.ao.schema.PrimaryKey
 *  net.java.ao.schema.StringLength
 *  net.java.ao.schema.Table
 */
package com.atlassian.plugins.whitelist.core.ao;

import com.atlassian.plugins.whitelist.WhitelistRule;
import com.atlassian.plugins.whitelist.WhitelistType;
import net.java.ao.Accessor;
import net.java.ao.Mutator;
import net.java.ao.Preload;
import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.Default;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.StringLength;
import net.java.ao.schema.Table;

@Table(value="WHITELIST_RULES")
@Preload
public interface AoWhitelistRule
extends WhitelistRule,
RawEntity<Integer> {
    public static final String ID_COLUMN = "ID";
    public static final String EXPRESSION_COLUMN = "EXPRESSION";
    public static final String TYPE_COLUMN = "TYPE";
    public static final String ALLOW_INBOUND_COLUMN = "ALLOWINBOUND";
    public static final String AUTHENTICATION_REQUIRED_COLUMN = "AUTHENTICATIONREQUIRED";

    @AutoIncrement
    @NotNull
    @PrimaryKey(value="ID")
    public Integer getId();

    @Accessor(value="EXPRESSION")
    @NotNull
    @StringLength(value=-1)
    public String getExpression();

    @Mutator(value="EXPRESSION")
    public void setExpression(String var1);

    @Accessor(value="TYPE")
    @NotNull
    public WhitelistType getType();

    @Mutator(value="TYPE")
    public void setType(WhitelistType var1);

    @Accessor(value="ALLOWINBOUND")
    public boolean isAllowInbound();

    @Mutator(value="ALLOWINBOUND")
    public void setAllowInbound(boolean var1);

    @Accessor(value="AUTHENTICATIONREQUIRED")
    @Default(value="false")
    @NotNull
    public boolean isAuthenticationRequired();

    @Mutator(value="AUTHENTICATIONREQUIRED")
    public void setAuthenticationRequired(boolean var1);
}

