/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckContext
 */
package com.atlassian.migration.agent.service.check.app.notinstalled;

import com.atlassian.cmpt.check.base.CheckContext;
import java.util.List;

public class AppsNotInstalledOnCloudContext
implements CheckContext {
    public final String cloudId;
    public final List<String> appKeys;

    public AppsNotInstalledOnCloudContext(String cloudId, List<String> appKeys) {
        this.cloudId = cloudId;
        this.appKeys = appKeys;
    }
}

