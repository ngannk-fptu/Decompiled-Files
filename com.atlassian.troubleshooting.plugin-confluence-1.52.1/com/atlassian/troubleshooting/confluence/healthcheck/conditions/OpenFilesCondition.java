/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.SystemUtils
 */
package com.atlassian.troubleshooting.confluence.healthcheck.conditions;

import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheckCondition;
import org.apache.commons.lang3.SystemUtils;

public class OpenFilesCondition
implements SupportHealthCheckCondition {
    @Override
    public boolean shouldDisplay() {
        return SystemUtils.IS_OS_UNIX;
    }
}

