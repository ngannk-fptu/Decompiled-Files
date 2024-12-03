/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.oauth2.scopes.config;

import com.atlassian.oauth2.common.config.MultipleProductCondition;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.AbstractProductCondition;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.RefappOnly;
import com.google.common.collect.ImmutableList;
import java.util.List;

public class AlwaysAllowedScopesCondition
extends MultipleProductCondition {
    public AlwaysAllowedScopesCondition() {
        super((List<AbstractProductCondition>)ImmutableList.of((Object)new RefappOnly()));
    }
}

