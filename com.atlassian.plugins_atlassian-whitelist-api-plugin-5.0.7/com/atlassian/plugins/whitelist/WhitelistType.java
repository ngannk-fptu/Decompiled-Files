/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.plugins.whitelist;

import com.atlassian.annotations.PublicApi;

@PublicApi
public enum WhitelistType {
    APPLICATION_LINK,
    DOMAIN_NAME,
    EXACT_URL,
    WILDCARD_EXPRESSION,
    REGULAR_EXPRESSION;

}

