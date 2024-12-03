/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.troubleshooting.stp;

import com.atlassian.troubleshooting.stp.salext.ApplicationType;
import javax.annotation.Nonnull;

public interface ApplicationVersionInfo {
    @Nonnull
    public ApplicationType getApplicationType();

    public String getApplicationBuildNumber();
}

