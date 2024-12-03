/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.confluence.healthcheck.conditions;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheckCondition;
import org.springframework.beans.factory.annotation.Autowired;

public class HasSynchronyCondition
implements SupportHealthCheckCondition {
    private final boolean shouldDisplay;

    @Autowired
    public HasSynchronyCondition(ApplicationProperties props) {
        this.shouldDisplay = Integer.parseInt(props.getBuildNumber()) >= 7100;
    }

    @Override
    public boolean shouldDisplay() {
        return this.shouldDisplay;
    }
}

