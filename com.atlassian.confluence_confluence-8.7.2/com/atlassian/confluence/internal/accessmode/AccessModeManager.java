/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.confluence.api.model.accessmode.AccessMode
 */
package com.atlassian.confluence.internal.accessmode;

import com.atlassian.annotations.Internal;
import com.atlassian.config.ConfigurationException;
import com.atlassian.confluence.api.model.accessmode.AccessMode;

@Internal
public interface AccessModeManager {
    public static final String ACCESS_MODE = "access.mode";

    public AccessMode getAccessMode();

    public void updateAccessMode(AccessMode var1) throws ConfigurationException;

    public boolean isReadOnlyAccessModeEnabled();

    public boolean shouldEnforceReadOnlyAccess();
}

