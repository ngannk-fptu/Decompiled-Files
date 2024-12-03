/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 */
package com.atlassian.crowd.manager.recovery;

import com.atlassian.crowd.embedded.api.Directory;

public interface RecoveryModeService {
    public boolean isRecoveryModeOn();

    public Directory getRecoveryDirectory();

    public String getRecoveryUsername();

    public boolean isRecoveryDirectory(Directory var1);
}

