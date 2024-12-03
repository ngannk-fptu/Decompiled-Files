/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.component.BambooComponent
 */
package com.atlassian.plugins.authentication.impl.analytics.product;

import com.atlassian.plugin.spring.scanner.annotation.component.BambooComponent;
import com.atlassian.plugins.authentication.impl.analytics.NodeIdProvider;

@BambooComponent
public class BambooNodeIdProvider
implements NodeIdProvider {
    @Override
    public String getNodeId() {
        return "NOT_CLUSTERED";
    }
}

