/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  javax.annotation.Nullable
 */
package com.atlassian.plugins.whitelist;

import com.atlassian.annotations.PublicApi;
import com.atlassian.plugins.whitelist.WhitelistType;
import java.io.Serializable;
import javax.annotation.Nullable;

@PublicApi
public interface WhitelistRule
extends Serializable {
    @Nullable
    public Integer getId();

    public String getExpression();

    public WhitelistType getType();

    public boolean isAllowInbound();

    public boolean isAuthenticationRequired();
}

