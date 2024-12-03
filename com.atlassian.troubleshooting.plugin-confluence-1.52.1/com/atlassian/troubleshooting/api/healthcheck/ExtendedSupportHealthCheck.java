/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.api.healthcheck;

import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheck;

public interface ExtendedSupportHealthCheck
extends SupportHealthCheck {
    public int getTimeOut();

    public String getKey();

    public String getClassName();

    public String getName();

    public String getDescription();

    public String getHelpPathKey();

    public String getTag();

    public boolean isSoftLaunch();

    public boolean isEnabled();

    public void setEnabled(boolean var1);
}

