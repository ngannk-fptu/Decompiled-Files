/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.Condition
 */
package com.atlassian.mywork.client.condition;

import com.atlassian.mywork.service.ServiceSelector;
import com.atlassian.plugin.web.Condition;
import java.util.Map;

public class ClientEnabledCondition
implements Condition {
    private final ServiceSelector serviceSelector;

    public ClientEnabledCondition(ServiceSelector serviceSelector) {
        this.serviceSelector = serviceSelector;
    }

    public void init(Map<String, String> params) {
    }

    public boolean shouldDisplay(Map<String, Object> context) {
        return this.serviceSelector.getEffectiveTarget() == ServiceSelector.Target.REMOTE;
    }
}

