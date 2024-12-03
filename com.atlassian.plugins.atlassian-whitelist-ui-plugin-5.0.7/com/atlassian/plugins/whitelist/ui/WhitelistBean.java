/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.whitelist.ImmutableWhitelistRule
 *  com.atlassian.plugins.whitelist.WhitelistRule
 *  com.atlassian.plugins.whitelist.WhitelistType
 *  com.google.common.base.Objects
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.Immutable
 *  org.apache.commons.lang3.BooleanUtils
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.plugins.whitelist.ui;

import com.atlassian.plugins.whitelist.ImmutableWhitelistRule;
import com.atlassian.plugins.whitelist.WhitelistRule;
import com.atlassian.plugins.whitelist.WhitelistType;
import com.atlassian.plugins.whitelist.ui.WhitelistBeanBuilder;
import com.atlassian.plugins.whitelist.ui.WhitelistTypeMapper;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import org.apache.commons.lang3.BooleanUtils;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown=true)
@Immutable
public final class WhitelistBean {
    private final Integer id;
    private final String expression;
    private final WhitelistType type;
    private final Boolean allowInbound;
    private final Boolean allowAnonymousUser;
    private final String iconUrl;

    @JsonCreator
    public WhitelistBean(@JsonProperty(value="expression") String expression, @JsonProperty(value="type") String type, @JsonProperty(value="allowInbound") Boolean allowInbound, @JsonProperty(value="allowAnonymousUser") Boolean allowAnonymousUser) {
        this.id = null;
        this.expression = expression;
        this.type = type != null ? WhitelistTypeMapper.asType(type) : null;
        this.iconUrl = null;
        this.allowInbound = allowInbound;
        this.allowAnonymousUser = allowAnonymousUser;
    }

    public WhitelistBean(WhitelistBeanBuilder builder) {
        Objects.requireNonNull(builder, "builder");
        this.id = builder.getId();
        this.expression = builder.getExpression();
        this.type = builder.getType();
        this.iconUrl = builder.getIconUrl();
        this.allowInbound = builder.isAllowInbound();
        this.allowAnonymousUser = builder.isAllowAnonymousUser();
    }

    public static WhitelistBeanBuilder builder() {
        return new WhitelistBeanBuilder();
    }

    public WhitelistRule asRule() {
        return ImmutableWhitelistRule.builder().id(this.id).expression(this.expression).type(this.type).allowInbound(BooleanUtils.isTrue((Boolean)this.allowInbound)).authenticationRequired(BooleanUtils.isNotTrue((Boolean)this.allowAnonymousUser)).build();
    }

    public WhitelistRule populateWith(WhitelistRule whitelistRule) {
        Objects.requireNonNull(whitelistRule, "whitelistRule");
        String newExpression = Optional.ofNullable(this.expression).orElse(whitelistRule.getExpression());
        WhitelistType newType = Optional.ofNullable(this.type).orElse(whitelistRule.getType());
        boolean newAllowInbound = Optional.ofNullable(this.allowInbound).orElse(whitelistRule.isAllowInbound());
        boolean newAllowAnonymousUser = Optional.ofNullable(this.allowAnonymousUser).orElse(!whitelistRule.isAuthenticationRequired());
        return ImmutableWhitelistRule.builder().id(whitelistRule.getId()).expression(newExpression).type(newType).allowInbound(newAllowInbound).authenticationRequired(!newAllowAnonymousUser).build();
    }

    @Nullable
    public Integer getId() {
        return this.id;
    }

    public String getExpression() {
        return this.expression;
    }

    @JsonIgnore
    public WhitelistType getType() {
        return this.type;
    }

    @JsonProperty(value="type")
    public String getTypeAsString() {
        return WhitelistTypeMapper.asString(this.type);
    }

    public String getIconUrl() {
        return this.iconUrl;
    }

    public Boolean isAllowInbound() {
        return this.allowInbound;
    }

    public Boolean isAllowAnonymousUser() {
        return this.allowAnonymousUser;
    }

    public boolean isAllowEdit() {
        return this.type != WhitelistType.APPLICATION_LINK;
    }

    public boolean isAllowDelete() {
        return this.type != WhitelistType.APPLICATION_LINK;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        WhitelistBean that = (WhitelistBean)o;
        return com.google.common.base.Objects.equal((Object)this.id, (Object)that.id) && com.google.common.base.Objects.equal((Object)this.expression, (Object)that.expression) && com.google.common.base.Objects.equal((Object)this.type, (Object)that.type);
    }

    public int hashCode() {
        return com.google.common.base.Objects.hashCode((Object[])new Object[]{this.id, this.expression, this.type});
    }

    public String toString() {
        return "WhitelistBean{id=" + this.id + ", expression='" + this.expression + '\'' + ", type=" + this.type + '}';
    }
}

