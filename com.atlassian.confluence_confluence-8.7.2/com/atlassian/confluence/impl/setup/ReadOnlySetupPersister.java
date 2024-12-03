/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.setup.SetupException
 *  com.atlassian.config.setup.SetupPersister
 */
package com.atlassian.confluence.impl.setup;

import com.atlassian.config.setup.SetupException;
import com.atlassian.config.setup.SetupPersister;
import java.util.List;

public class ReadOnlySetupPersister
implements SetupPersister {
    private final SetupPersister delegate;

    public ReadOnlySetupPersister(SetupPersister delegate) {
        this.delegate = delegate;
    }

    public List getUncompletedSteps() {
        return this.delegate.getUncompletedSteps();
    }

    public List getCompletedSteps() {
        return this.delegate.getCompletedSteps();
    }

    public String getSetupType() {
        return this.delegate.getSetupType();
    }

    public void setSetupType(String setupType) {
        throw new UnsupportedOperationException("Mutation not allowed");
    }

    public void finishSetup() throws SetupException {
        throw new UnsupportedOperationException("Mutation not allowed");
    }

    public void progessSetupStep() {
        throw new UnsupportedOperationException("Mutation not allowed");
    }

    public String getCurrentStep() {
        return this.delegate.getCurrentStep();
    }

    public boolean isDemonstrationContentInstalled() {
        return this.delegate.isDemonstrationContentInstalled();
    }

    public void setDemonstrationContentInstalled() {
        throw new UnsupportedOperationException("Mutation not allowed");
    }
}

