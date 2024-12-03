/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.plugins.osgi.javaconfig.conditions.product;

import com.atlassian.annotations.PublicApi;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.AbstractProductCondition;

@PublicApi
public final class JiraOnly
extends AbstractProductCondition {
    public JiraOnly() {
        super("com.atlassian.jira.bc.issue.IssueService");
    }
}

