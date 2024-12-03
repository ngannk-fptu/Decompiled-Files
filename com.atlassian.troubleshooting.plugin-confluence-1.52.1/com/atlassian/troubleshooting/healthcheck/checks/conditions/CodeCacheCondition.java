/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.JavaVersion
 *  org.apache.commons.lang3.SystemUtils
 */
package com.atlassian.troubleshooting.healthcheck.checks.conditions;

import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheckCondition;
import org.apache.commons.lang3.JavaVersion;
import org.apache.commons.lang3.SystemUtils;

public class CodeCacheCondition
implements SupportHealthCheckCondition {
    @Override
    public boolean shouldDisplay() {
        return SystemUtils.isJavaVersionAtLeast((JavaVersion)JavaVersion.JAVA_1_8);
    }
}

