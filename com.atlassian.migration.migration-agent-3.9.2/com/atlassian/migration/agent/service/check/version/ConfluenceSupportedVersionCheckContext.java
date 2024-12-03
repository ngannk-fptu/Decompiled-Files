/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckContext
 */
package com.atlassian.migration.agent.service.check.version;

import com.atlassian.cmpt.check.base.CheckContext;

public class ConfluenceSupportedVersionCheckContext
implements CheckContext {
    public final String confluenceVersion;

    public ConfluenceSupportedVersionCheckContext(String confluenceVersion) {
        this.confluenceVersion = confluenceVersion;
    }
}

