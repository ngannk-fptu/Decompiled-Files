/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.whitelist.WhitelistRule
 *  com.atlassian.plugins.whitelist.WhitelistType
 */
package com.atlassian.plugins.whitelist.ui;

import com.atlassian.plugins.whitelist.WhitelistRule;
import com.atlassian.plugins.whitelist.WhitelistType;
import com.atlassian.plugins.whitelist.ui.WhitelistBean;

public class WhitelistBeanBuilder {
    private Integer id;
    private String expression;
    private WhitelistType type;
    private String iconUrl = "";
    private boolean allowInbound;
    private boolean allowAnonymousUser;

    public WhitelistBeanBuilder from(WhitelistRule input) {
        return this.id(input.getId()).expression(input.getExpression()).type(input.getType()).allowInbound(input.isAllowInbound()).allowAnonymousUser(!input.isAuthenticationRequired());
    }

    public WhitelistBeanBuilder copyOf(WhitelistBean whitelistBean) {
        return this.id(whitelistBean.getId()).expression(whitelistBean.getExpression()).type(whitelistBean.getType()).iconUrl(whitelistBean.getIconUrl()).allowInbound(whitelistBean.isAllowInbound()).allowAnonymousUser(whitelistBean.isAllowAnonymousUser());
    }

    public WhitelistBeanBuilder expression(String expression) {
        this.expression = expression;
        return this;
    }

    public WhitelistBeanBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public WhitelistBeanBuilder type(WhitelistType type) {
        this.type = type;
        return this;
    }

    public WhitelistBeanBuilder iconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
        return this;
    }

    public WhitelistBeanBuilder allowInbound(boolean allowInbound) {
        this.allowInbound = allowInbound;
        return this;
    }

    public WhitelistBeanBuilder allowAnonymousUser(boolean allowAnonymousUser) {
        this.allowAnonymousUser = allowAnonymousUser;
        return this;
    }

    public WhitelistBean build() {
        return new WhitelistBean(this);
    }

    public Integer getId() {
        return this.id;
    }

    public String getExpression() {
        return this.expression;
    }

    public WhitelistType getType() {
        return this.type;
    }

    public String getIconUrl() {
        return this.iconUrl;
    }

    public boolean isAllowInbound() {
        return this.allowInbound;
    }

    public boolean isAllowAnonymousUser() {
        return this.allowAnonymousUser;
    }
}

