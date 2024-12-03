/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.google.common.base.Objects
 *  com.google.common.base.Preconditions
 *  javax.annotation.concurrent.Immutable
 */
package com.atlassian.plugins.whitelist;

import com.atlassian.annotations.PublicApi;
import com.atlassian.plugins.whitelist.ImmutableWhitelistRuleBuilder;
import com.atlassian.plugins.whitelist.WhitelistRule;
import com.atlassian.plugins.whitelist.WhitelistType;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import javax.annotation.concurrent.Immutable;

@Immutable
@PublicApi
public final class ImmutableWhitelistRule
implements WhitelistRule,
Serializable {
    private final Integer id;
    private final String expression;
    private final WhitelistType type;
    private final boolean allowInbound;
    private final boolean authenticationRequired;

    public ImmutableWhitelistRule(ImmutableWhitelistRuleBuilder builder) {
        Preconditions.checkNotNull((Object)builder, (Object)"builder");
        this.id = builder.getId();
        this.expression = (String)Preconditions.checkNotNull((Object)builder.getExpression(), (Object)"expression");
        this.type = (WhitelistType)((Object)Preconditions.checkNotNull((Object)((Object)builder.getType()), (Object)"type"));
        this.allowInbound = builder.isAllowInbound();
        this.authenticationRequired = builder.isAuthenticationRequired();
    }

    public ImmutableWhitelistRule(WhitelistRule copy) {
        Preconditions.checkNotNull((Object)copy, (Object)"copy");
        this.id = copy.getId();
        this.expression = (String)Preconditions.checkNotNull((Object)copy.getExpression(), (Object)"expression");
        this.type = (WhitelistType)((Object)Preconditions.checkNotNull((Object)((Object)copy.getType()), (Object)"type"));
        this.allowInbound = copy.isAllowInbound();
        this.authenticationRequired = copy.isAuthenticationRequired();
    }

    public static ImmutableWhitelistRuleBuilder builder() {
        return new ImmutableWhitelistRuleBuilder();
    }

    @Override
    public Integer getId() {
        return this.id;
    }

    @Override
    public String getExpression() {
        return this.expression;
    }

    @Override
    public WhitelistType getType() {
        return this.type;
    }

    @Override
    public boolean isAllowInbound() {
        return this.allowInbound;
    }

    @Override
    public boolean isAuthenticationRequired() {
        return this.authenticationRequired;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ImmutableWhitelistRule that = (ImmutableWhitelistRule)o;
        return Objects.equal((Object)this.id, (Object)that.id) && Objects.equal((Object)this.expression, (Object)that.expression) && Objects.equal((Object)((Object)this.type), (Object)((Object)that.type)) && Objects.equal((Object)this.allowInbound, (Object)that.allowInbound) && Objects.equal((Object)this.authenticationRequired, (Object)that.authenticationRequired);
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.id, this.expression, this.type, this.allowInbound, this.authenticationRequired});
    }

    public String toString() {
        return "ImmutableWhitelistRule{id=" + this.id + ", expression='" + this.expression + '\'' + ", type=" + (Object)((Object)this.type) + ", allowInbound=" + this.allowInbound + ", authenticationRequired=" + this.authenticationRequired + '}';
    }
}

