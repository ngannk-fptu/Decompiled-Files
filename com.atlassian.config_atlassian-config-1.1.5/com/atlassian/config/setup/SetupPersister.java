/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.config.setup;

import com.atlassian.config.setup.SetupException;
import java.util.List;

public interface SetupPersister {
    public static final String SETUP_TYPE_INITIAL = "initial";
    public static final String SETUP_TYPE_INSTALL = "install";
    public static final String SETUP_TYPE_CUSTOM = "custom";
    public static final String SETUP_INSTALL_DEMO_DATA = "demo";
    public static final String SETUP_STATE_COMPLETE = "complete";

    public List getUncompletedSteps();

    public List getCompletedSteps();

    public String getSetupType();

    public void setSetupType(String var1);

    public void finishSetup() throws SetupException;

    public void progessSetupStep();

    public String getCurrentStep();

    public boolean isDemonstrationContentInstalled();

    public void setDemonstrationContentInstalled();
}

