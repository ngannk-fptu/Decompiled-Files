/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.security.denormalisedpermissions;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.annotations.Internal;
import com.atlassian.confluence.security.denormalisedpermissions.DenormalisedPermissionServiceState;
import com.atlassian.confluence.security.denormalisedpermissions.StateChangeInformation;
import java.util.List;

@ExperimentalApi
@Internal
public interface DenormalisedPermissionStateManager {
    public boolean isApiReady();

    public boolean isSpaceApiReady();

    public boolean isContentApiReady();

    public DenormalisedPermissionServiceState getSpaceServiceState(boolean var1);

    public DenormalisedPermissionServiceState getContentServiceState(boolean var1);

    public void enableService();

    public void disableService(boolean var1);

    public Long getSpacePermissionUpdateLag();

    public Long getContentPermissionUpdateLag();

    public List<StateChangeInformation> getStateChangeLog(int var1);

    public void scheduled();
}

