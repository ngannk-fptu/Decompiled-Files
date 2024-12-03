/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.spring.container.ContainerManager
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.setup.actions;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.config.ConfigurationException;
import com.atlassian.confluence.core.ConfluenceSidManager;
import com.atlassian.confluence.security.trust.KeyPairInitialiser;
import com.atlassian.confluence.setup.SetupLocks;
import com.atlassian.confluence.setup.actions.AbstractSetupDatabaseAction;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.spring.container.ContainerManager;
import com.google.common.collect.ImmutableSet;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Date;
import java.util.Set;

public abstract class AbstractDatabaseCreationAction
extends AbstractSetupDatabaseAction {
    protected static final String DATA_EXISTS = "data-exists";
    protected static final String DATABASE_CREATION_UNFINISHED = "database-creation-unfinished";
    private ConfluenceSidManager sidManager;
    private BandanaManager bandanaManager;
    private KeyPairInitialiser keyPairInitialiser;
    private SetupLocks setupLocks;

    public String execute() throws ConfigurationException {
        String result;
        if (this.setupLocks.compareAndSet(SetupLocks.Lock.CURRENTLY_INSTALLING_DATABASE, false, true)) {
            result = this.setupDatabase();
            if (!this.getUnsuccessfulCodes().contains(result)) {
                this.performEarlyStartup();
                this.persistInstanceKey();
                this.persistInstallationDate();
                this.getSetupPersister().progessSetupStep();
            }
            this.setupLocks.set(SetupLocks.Lock.CURRENTLY_INSTALLING_DATABASE, false);
        } else {
            result = DATABASE_CREATION_UNFINISHED;
        }
        return result;
    }

    private void persistInstallationDate() {
        this.getBandanaManager().setValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, "confluence.server.installation.date", (Object)new Date());
    }

    protected Set<String> getUnsuccessfulCodes() {
        return ImmutableSet.of((Object)DATA_EXISTS, (Object)"error");
    }

    abstract String setupDatabase() throws ConfigurationException;

    protected void persistInstanceKey() {
        try {
            if (!this.getSidManager().isSidSet()) {
                this.getSidManager().initSid();
            }
        }
        catch (ConfigurationException e) {
            throw new IllegalStateException("Cannot persist sid key", e);
        }
        try {
            this.getKeyPairInitialiser().initConfluenceKey();
        }
        catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new IllegalStateException("Cannot persist confluence key", e);
        }
    }

    public ConfluenceSidManager getSidManager() {
        if (this.sidManager == null) {
            this.sidManager = (ConfluenceSidManager)ContainerManager.getComponent((String)"sidManager");
        }
        return this.sidManager;
    }

    public BandanaManager getBandanaManager() {
        if (this.bandanaManager == null) {
            this.bandanaManager = (BandanaManager)ContainerManager.getComponent((String)"bandanaManager");
        }
        return this.bandanaManager;
    }

    public void setSidManager(ConfluenceSidManager sidManager) {
        this.sidManager = sidManager;
    }

    public void setSetupLocks(SetupLocks setupLocks) {
        this.setupLocks = setupLocks;
    }

    public void setKeyPairInitialiser(KeyPairInitialiser keyPairInitialiser) {
        this.keyPairInitialiser = keyPairInitialiser;
    }

    public KeyPairInitialiser getKeyPairInitialiser() {
        if (this.keyPairInitialiser == null) {
            this.keyPairInitialiser = (KeyPairInitialiser)ContainerManager.getComponent((String)"keyPairInitialiser");
        }
        return this.keyPairInitialiser;
    }

    public void setBandanaManager(BandanaManager bandanaManager) {
        this.bandanaManager = bandanaManager;
    }
}

