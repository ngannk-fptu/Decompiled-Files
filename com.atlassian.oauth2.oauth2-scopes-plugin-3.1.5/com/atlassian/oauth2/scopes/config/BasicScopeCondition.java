/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.oauth2.scopes.config;

import com.atlassian.oauth2.common.config.MultipleProductCondition;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.AbstractProductCondition;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.ConfluenceOnly;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.JiraOnly;
import com.google.common.collect.ImmutableList;
import java.util.List;

public class BasicScopeCondition
extends MultipleProductCondition {
    public BasicScopeCondition() {
        super((List<AbstractProductCondition>)ImmutableList.of((Object)new JiraOnly(), (Object)new ConfluenceOnly()));
    }
}

