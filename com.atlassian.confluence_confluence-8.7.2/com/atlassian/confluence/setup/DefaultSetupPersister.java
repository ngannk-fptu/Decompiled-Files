/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.config.setup.SetupException
 *  com.atlassian.core.util.PairType
 *  com.google.common.collect.ImmutableSet
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.setup;

import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.config.ConfigurationException;
import com.atlassian.config.setup.SetupException;
import com.atlassian.confluence.cluster.DefaultClusterConfigurationHelper;
import com.atlassian.confluence.setup.actions.ConfluenceSetupPersister;
import com.atlassian.confluence.setup.settings.init.ConfluenceAdminUiProperties;
import com.atlassian.core.util.PairType;
import com.google.common.collect.ImmutableSet;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSetupPersister
implements ConfluenceSetupPersister {
    private static final Logger log = LoggerFactory.getLogger(DefaultSetupPersister.class);
    private static final String PROPERTY_DEMO_CONTENT_INSTALLED = "confluence.democontent.installed";
    private static final String INVISIBLE = "invisible";
    private Stack<PairType> setupStack;
    private Stack<PairType> finishedStack;
    private ApplicationConfiguration applicationConfig;
    public static final String SETUP_TYPE_CLUSTER = "cluster";
    public static final String SETUP_TYPE_STANDALONE_TO_CLUSTER = "standalone.to.cluster";
    public static final String SETUP_TYPE_CLUSTER_TO_STANDALONE = "cluster.to.standalone";
    public static final Set<String> MIGRATION_SETUP_TYPES = ImmutableSet.of((Object)"standalone.to.cluster", (Object)"cluster.to.standalone");
    public static final Set<String> CLUSTER_SETUP_TYPES = ImmutableSet.of((Object)"standalone.to.cluster", (Object)"cluster");

    public void setApplicationConfig(ApplicationConfiguration applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    private Stack<PairType> getSetupStack() {
        if (this.setupStack == null) {
            this.configureSetupSequence(this.getSetupType());
        }
        return this.setupStack;
    }

    protected void configureSetupSequence(String setupType) {
        this.setupStack = new Stack();
        this.finishedStack = new Stack();
        if ("initial".equals(setupType) || "install".equals(setupType)) {
            this.setupStack.add(new PairType((Serializable)((Object)"complete"), (Serializable)((Object)"setup.is.complete")));
            this.setupStack.add(new PairType((Serializable)((Object)"finishsetup"), (Serializable)((Object)INVISIBLE)));
            this.setupStack.add(new PairType((Serializable)((Object)"connecttojirasyncdirectory-start"), (Serializable)((Object)INVISIBLE)));
            this.setupStack.add(new PairType((Serializable)((Object)"setupusermanagementchoice-start"), (Serializable)((Object)"user.management")));
            this.setupStack.add(new PairType((Serializable)((Object)"setupdemocontent"), (Serializable)((Object)INVISIBLE)));
            this.setupStack.add(new PairType((Serializable)((Object)"setuppaths"), (Serializable)((Object)INVISIBLE)));
            this.setupStack.add(new PairType((Serializable)((Object)"setupembeddeddb-default"), (Serializable)((Object)INVISIBLE)));
            this.setupStack.add(new PairType((Serializable)((Object)"evallicense"), (Serializable)((Object)"install.license")));
            this.setupStack.add(new PairType((Serializable)((Object)"setupstart"), (Serializable)((Object)INVISIBLE)));
        } else if ("custom".equals(setupType)) {
            this.setupStack.add(new PairType((Serializable)((Object)"complete"), (Serializable)((Object)"setup.is.complete")));
            this.setupStack.add(new PairType((Serializable)((Object)"finishsetup"), (Serializable)((Object)INVISIBLE)));
            this.setupStack.add(new PairType((Serializable)((Object)"connecttojirasyncdirectory-start"), (Serializable)((Object)INVISIBLE)));
            this.setupStack.add(new PairType((Serializable)((Object)"setupusermanagementchoice-start"), (Serializable)((Object)"user.management")));
            this.setupStack.add(new PairType((Serializable)((Object)"setupdata-start"), (Serializable)((Object)"load.content")));
            this.setupStack.add(new PairType((Serializable)((Object)"setuppaths"), (Serializable)((Object)INVISIBLE)));
            this.setupStack.add(new PairType((Serializable)((Object)"setupdbchoice-start"), (Serializable)((Object)"configure.database")));
            this.setupStack.add(new PairType((Serializable)((Object)"setuplicense"), (Serializable)((Object)"install.license")));
            this.setupStack.add(new PairType((Serializable)((Object)"setupstart"), (Serializable)((Object)INVISIBLE)));
        } else if (SETUP_TYPE_CLUSTER.equals(setupType)) {
            this.setupStack.add(new PairType((Serializable)((Object)"complete"), (Serializable)((Object)"setup.is.complete")));
            this.setupStack.add(new PairType((Serializable)((Object)"finishsetup"), (Serializable)((Object)INVISIBLE)));
            this.setupStack.add(new PairType((Serializable)((Object)"connecttojirasyncdirectory-start"), (Serializable)((Object)INVISIBLE)));
            this.setupStack.add(new PairType((Serializable)((Object)"setupusermanagementchoice-start"), (Serializable)((Object)"user.management")));
            this.setupStack.add(new PairType((Serializable)((Object)"setupdata-start"), (Serializable)((Object)"load.content")));
            this.setupStack.add(new PairType((Serializable)((Object)"setuppaths"), (Serializable)((Object)INVISIBLE)));
            this.setupStack.add(new PairType((Serializable)((Object)"setupdbchoice-start"), (Serializable)((Object)"configure.database")));
            this.setupStack.add(new PairType((Serializable)((Object)"setupcluster-start"), (Serializable)((Object)"configure.cluster")));
            this.setupStack.add(new PairType((Serializable)((Object)"setuplicense"), (Serializable)((Object)"install.license")));
            this.setupStack.add(new PairType((Serializable)((Object)"setupstart"), (Serializable)((Object)INVISIBLE)));
        } else if (SETUP_TYPE_STANDALONE_TO_CLUSTER.equals(setupType)) {
            this.setupStack.add(new PairType((Serializable)((Object)"complete"), (Serializable)((Object)"setup.is.complete")));
            this.setupStack.add(new PairType((Serializable)((Object)"finishsetup"), (Serializable)((Object)INVISIBLE)));
            this.setupStack.add(new PairType((Serializable)((Object)"setupcluster-start"), (Serializable)((Object)"configure.cluster")));
            this.setupStack.add(new PairType((Serializable)((Object)"setupstart"), (Serializable)((Object)INVISIBLE)));
        } else if (SETUP_TYPE_CLUSTER_TO_STANDALONE.equals(setupType)) {
            this.setupStack.add(new PairType((Serializable)((Object)"complete"), (Serializable)((Object)"setup.is.complete")));
            this.setupStack.add(new PairType((Serializable)((Object)"finishsetup"), (Serializable)((Object)INVISIBLE)));
            this.setupStack.add(new PairType((Serializable)((Object)"setupstart"), (Serializable)((Object)INVISIBLE)));
        }
        String currentStep = this.getCurrentStep();
        this.synchSetupStackWithConfigRecord(currentStep);
    }

    @Override
    public void synchSetupStackWithConfigRecord(String currentStep) {
        if (currentStep != null) {
            for (PairType pairType : this.getSetupStack()) {
                if (!pairType.getKey().equals(currentStep)) continue;
                while (!this.getSetupStack().peek().getKey().equals(currentStep)) {
                    this.finishedStack.push(this.getSetupStack().pop());
                }
            }
        }
    }

    public List<PairType> getUncompletedSteps() {
        Iterator iter = this.getSetupStack().iterator();
        ArrayList<PairType> reversedSteps = new ArrayList<PairType>();
        while (iter.hasNext()) {
            reversedSteps.add((PairType)iter.next());
        }
        Collections.reverse(reversedSteps);
        return reversedSteps;
    }

    public List<PairType> getCompletedSteps() {
        return new ArrayList<PairType>(this.finishedStack.subList(0, this.finishedStack.size()));
    }

    public List<PairType> getAllVisibleSteps() {
        ArrayList<PairType> visibleSteps = new ArrayList<PairType>();
        for (PairType step : this.getCompletedSteps()) {
            if (INVISIBLE.equals(step.getValue())) continue;
            visibleSteps.add(step);
        }
        for (PairType step : this.getUncompletedSteps()) {
            if (INVISIBLE.equals(step.getValue())) continue;
            visibleSteps.add(step);
        }
        return visibleSteps;
    }

    public String getSetupType() {
        return this.applicationConfig.getSetupType();
    }

    public void setSetupType(String setupType) {
        if (StringUtils.isEmpty((CharSequence)setupType)) {
            setupType = "initial";
        }
        this.applicationConfig.setSetupType(setupType);
        this.saveApplicationConfig();
        this.configureSetupSequence(setupType);
    }

    public void finishSetup() throws SetupException {
        String currentStep = this.getCurrentStep();
        if (!currentStep.equals("complete")) {
            throw new SetupException("Tried to finish setup but had not run through the whole wizard? Current step: " + currentStep);
        }
        this.applicationConfig.setSetupComplete(true);
        this.applicationConfig.removeProperty((Object)PROPERTY_DEMO_CONTENT_INSTALLED);
        ConfluenceAdminUiProperties.initAdminUiProperties(this.applicationConfig);
        this.tryFinishMigration();
        this.saveApplicationConfig();
    }

    private void tryFinishMigration() {
        if (SETUP_TYPE_STANDALONE_TO_CLUSTER.equals(this.getSetupType())) {
            this.applicationConfig.setSetupType(SETUP_TYPE_CLUSTER);
        } else if (SETUP_TYPE_CLUSTER_TO_STANDALONE.equals(this.getSetupType())) {
            this.applicationConfig.setSetupType("install");
        } else if (MIGRATION_SETUP_TYPES.contains(this.getSetupType())) {
            throw new IllegalStateException("Unsupported migration setup type: " + this.getSetupType());
        }
    }

    public void progessSetupStep() {
        try {
            PairType completedPair = this.getSetupStack().pop();
            this.finishedStack.push(completedPair);
            if (!this.getSetupStack().empty()) {
                String newCurrentStep = (String)((Object)this.getSetupStack().peek().getKey());
                log.debug("New Current Step : {}", (Object)newCurrentStep);
                this.setCurrentStep(newCurrentStep);
            } else {
                log.error("setupStack is empty of actions.");
            }
        }
        catch (EmptyStackException e) {
            log.error("The setupStack is empty; the last action should always be '{}', which will prohibit further setupStack activity! Odds are it wasn't in this case.", (Object)"complete");
        }
    }

    public String getCurrentDisplayStep() {
        if (this.getSetupStack().empty()) {
            return null;
        }
        String currentStep = this.getCurrentStep();
        if (!this.getCurrentStepPair().getValue().equals(INVISIBLE)) {
            return currentStep;
        }
        List<PairType> completedSteps = this.getCompletedSteps();
        if (!completedSteps.isEmpty()) {
            for (int i = completedSteps.size() - 1; i >= 0; --i) {
                PairType step = completedSteps.get(i);
                if (step.getValue().equals(INVISIBLE)) continue;
                return (String)((Object)step.getKey());
            }
        }
        return currentStep;
    }

    public String getCurrentStep() {
        String currentStep = this.applicationConfig.getCurrentSetupStep();
        if (StringUtils.isEmpty((CharSequence)currentStep)) {
            currentStep = (String)((Object)this.getCurrentStepPair().getKey());
            this.setCurrentStep(currentStep);
        }
        return currentStep;
    }

    private PairType getCurrentStepPair() {
        return this.getSetupStack().peek();
    }

    private void setCurrentStep(String newCurrentStep) {
        this.applicationConfig.setCurrentSetupStep(newCurrentStep);
        this.saveApplicationConfig();
    }

    public void setDemonstrationContentInstalled() {
        this.applicationConfig.setProperty((Object)PROPERTY_DEMO_CONTENT_INSTALLED, (Object)"true");
    }

    public boolean isDemonstrationContentInstalled() {
        return "true".equals(this.applicationConfig.getProperty((Object)PROPERTY_DEMO_CONTENT_INSTALLED));
    }

    @Override
    public boolean isSetupTypeClustered() {
        return CLUSTER_SETUP_TYPES.contains(this.getSetupType());
    }

    @Override
    public boolean isSetupTypeMigration() {
        return MIGRATION_SETUP_TYPES.contains(this.getSetupType());
    }

    @Override
    public void convertToClusterMigration() {
        this.applicationConfig.setSetupComplete(false);
        this.applicationConfig.setCurrentSetupStep("setupstart");
        this.setSetupType(SETUP_TYPE_STANDALONE_TO_CLUSTER);
    }

    @Override
    public void convertToStandaloneMigration() {
        this.applicationConfig.setSetupComplete(false);
        this.applicationConfig.setCurrentSetupStep("setupstart");
        this.setSetupType(SETUP_TYPE_CLUSTER_TO_STANDALONE);
    }

    @Override
    public void resetCancelledMigration() {
        if (this.applicationConfig.getBooleanProperty((Object)"migration.cancelled")) {
            String setupType = this.getSetupType();
            if (setupType.equals(SETUP_TYPE_STANDALONE_TO_CLUSTER)) {
                this.setSetupType("custom");
            } else {
                this.setSetupType(SETUP_TYPE_CLUSTER);
            }
            this.setCurrentStep("complete");
            this.applicationConfig.removeProperty((Object)"migration.cancelled");
            this.applicationConfig.setSetupComplete(true);
            this.saveApplicationConfig();
        }
    }

    @Override
    public void setMigrationCancelled() {
        this.applicationConfig.setProperty((Object)"migration.cancelled", true);
        this.saveApplicationConfig();
    }

    @Override
    public void removeClusterSetupEntries() {
        for (String clusterSetupEntry : DefaultClusterConfigurationHelper.CLUSTER_SETUP_ENTRIES) {
            this.applicationConfig.removeProperty((Object)clusterSetupEntry);
        }
        this.saveApplicationConfig();
    }

    private void saveApplicationConfig() {
        try {
            this.applicationConfig.save();
        }
        catch (ConfigurationException e) {
            log.error("Error writing state to confluence.cfg.xml", (Throwable)e);
        }
    }
}

