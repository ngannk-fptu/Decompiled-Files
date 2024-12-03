/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core;

import com.atlassian.upm.core.DefaultHostApplicationInformation;

public interface VersionAwareHostApplicationInformation
extends DefaultHostApplicationInformation {
    public int getBuildNumber();

    public boolean isDevelopmentProductVersion();

    public boolean isJiraPostCarebear();
}

