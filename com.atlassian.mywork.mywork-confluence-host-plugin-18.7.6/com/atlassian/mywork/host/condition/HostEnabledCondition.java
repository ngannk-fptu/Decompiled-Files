/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mywork.service.ServiceSelector$Target
 *  com.atlassian.plugin.web.Condition
 */
package com.atlassian.mywork.host.condition;

import com.atlassian.mywork.host.delegator.ServiceSelectorWrapper;
import com.atlassian.mywork.service.ServiceSelector;
import com.atlassian.plugin.web.Condition;
import java.util.Map;

public class HostEnabledCondition
implements Condition {
    public void init(Map<String, String> params) {
    }

    public boolean shouldDisplay(Map<String, Object> context) {
        return ServiceSelectorWrapper.getServiceSelector().getEffectiveTarget() == ServiceSelector.Target.LOCAL;
    }
}

