/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.Condition
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.conditions;

import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import com.atlassian.upm.UpmHostApplicationInformation;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class BuildNumberCondition
implements Condition {
    private static final Logger log = LoggerFactory.getLogger(BuildNumberCondition.class);
    protected long actualBuildNumber;
    protected Long specifiedBuildNumber = null;

    public BuildNumberCondition(UpmHostApplicationInformation appInfo) {
        this.actualBuildNumber = appInfo.getBuildNumber();
    }

    public void init(Map<String, String> paramMap) throws PluginParseException {
        try {
            this.specifiedBuildNumber = Long.parseLong(paramMap.get("buildNumber"));
        }
        catch (Exception e) {
            log.error("Could not parse specified build number", (Throwable)e);
        }
    }
}

