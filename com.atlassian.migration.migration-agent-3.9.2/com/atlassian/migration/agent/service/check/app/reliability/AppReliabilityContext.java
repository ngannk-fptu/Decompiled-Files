/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckContext
 */
package com.atlassian.migration.agent.service.check.app.reliability;

import com.atlassian.cmpt.check.base.CheckContext;
import java.util.Set;

public class AppReliabilityContext
implements CheckContext {
    public final Set<String> appKeys;

    public AppReliabilityContext(Set<String> appKeys) {
        this.appKeys = appKeys;
    }
}

