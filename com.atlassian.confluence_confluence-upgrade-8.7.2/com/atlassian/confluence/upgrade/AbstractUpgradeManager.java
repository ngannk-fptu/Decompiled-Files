/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.johnson.JohnsonEventContainer
 *  com.atlassian.johnson.event.Event
 *  com.atlassian.johnson.event.EventLevel
 *  com.atlassian.johnson.event.EventType
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  org.apache.commons.collections4.CollectionUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.confluence.upgrade;

import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.config.ConfigurationException;
import com.atlassian.confluence.upgrade.BackupSupport;
import com.atlassian.confluence.upgrade.BuildNumber;
import com.atlassian.confluence.upgrade.BuildNumberComparator;
import com.atlassian.confluence.upgrade.BuildNumberUpgradeConstraint;
import com.atlassian.confluence.upgrade.DeferredUpgradeTask;
import com.atlassian.confluence.upgrade.IsNewerThan;
import com.atlassian.confluence.upgrade.MutableUpgradedFlag;
import com.atlassian.confluence.upgrade.PluginExportCompatibility;
import com.atlassian.confluence.upgrade.UpgradeError;
import com.atlassian.confluence.upgrade.UpgradeException;
import com.atlassian.confluence.upgrade.UpgradeManager;
import com.atlassian.confluence.upgrade.UpgradeTask;
import com.atlassian.confluence.upgrade.UpgradeTaskInfo;
import com.atlassian.confluence.upgrade.UpgradeTaskInfoService;
import com.atlassian.johnson.JohnsonEventContainer;
import com.atlassian.johnson.event.Event;
import com.atlassian.johnson.event.EventLevel;
import com.atlassian.johnson.event.EventType;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;

public abstract class AbstractUpgradeManager
implements UpgradeManager,
InitializingBean,
UpgradeTaskInfoService,
BeanFactoryAware {
    protected static final int MAXIMUM_CLOUD_BUILD_VERSION_NUMBER = 6452;
    private static final Logger log = LoggerFactory.getLogger(AbstractUpgradeManager.class);
    public static final Comparator<UpgradeTask> UPGRADE_TASK_COMPARATOR = (o1, o2) -> {
        Integer buildNumber1 = Integer.parseInt(o1.getBuildNumber());
        Integer buildNumber2 = Integer.parseInt(o2.getBuildNumber());
        return buildNumber1.compareTo(buildNumber2);
    };
    private ApplicationConfiguration applicationConfig;
    private PluginAccessor pluginAccessor;
    private BeanFactory beanFactory;
    private MutableUpgradedFlag upgradedFlag = new MutableUpgradedFlag();
    private Supplier<List<UpgradeTask>> upgradeTasks = Collections::emptyList;
    private Supplier<List<UpgradeTask>> preSchemaUpgradeTasks = Collections::emptyList;
    private Supplier<List<UpgradeTask>> schemaUpgradeTasks = Collections::emptyList;
    private Supplier<List<DeferredUpgradeTask>> pluginDependentUpgradeTasks = Collections::emptyList;
    private final List<UpgradeError> errors = new ArrayList<UpgradeError>();
    private static final String WORKDAY = "com.atlassian.mywork.mywork-confluence-host-plugin";
    private static final String SPACE_IA = "com.atlassian.confluence.plugins.confluence-space-ia";

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void setApplicationConfig(ApplicationConfiguration applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    public void setUpgradedFlag(MutableUpgradedFlag upgradedFlag) {
        this.upgradedFlag = upgradedFlag;
    }

    public void afterPropertiesSet() {
    }

    private void verifyAllUpgradeTasksAreForSupportedVersion() {
        ArrayList<String> oldUpgradeTasks = new ArrayList<String>();
        oldUpgradeTasks.addAll(AbstractUpgradeManager.findOldUpgradeTasks(this.getPreSchemaUpgradeTasks()));
        oldUpgradeTasks.addAll(AbstractUpgradeManager.findOldUpgradeTasks(this.getSchemaUpgradeTasks()));
        oldUpgradeTasks.addAll(AbstractUpgradeManager.findOldUpgradeTasks(this.getUpgradeTasks()));
        oldUpgradeTasks.addAll(AbstractUpgradeManager.findOldUpgradeTasks(this.getPluginDependentUpgradeTasks()));
        if (!oldUpgradeTasks.isEmpty()) {
            throw new IllegalStateException("Upgrade manager consistency check failed: the following upgrade tasks refer to an unsupported version: " + oldUpgradeTasks);
        }
    }

    private static List<String> findOldUpgradeTasks(Iterable<? extends UpgradeTask> upgradeTasks) {
        if (upgradeTasks == null) {
            return Collections.emptyList();
        }
        ArrayList<String> oldTasks = new ArrayList<String>();
        for (UpgradeTask upgradeTask : upgradeTasks) {
            if (!AbstractUpgradeManager.isUpgradeFromUnsupportedVersion(upgradeTask.getBuildNumber())) continue;
            oldTasks.add(upgradeTask.getClass().getSimpleName() + "( " + upgradeTask.getShortDescription() + ")");
        }
        return oldTasks;
    }

    protected abstract void validateSchemaUpdateIfNeeded() throws ConfigurationException;

    protected abstract void updateSchemaIfNeeded() throws ConfigurationException;

    protected abstract void releaseSchemaReferences();

    @Override
    public void upgrade(JohnsonEventContainer agentJohnson) throws UpgradeException {
        this.verifyAllUpgradeTasksAreForSupportedVersion();
        List<UpgradeError> upgradePreReqErrors = this.runUpgradePrerequisites();
        if (upgradePreReqErrors.size() > 0) {
            this.upgradedFlag.setUpgraded(false);
            this.errors.addAll(upgradePreReqErrors);
            Iterator<UpgradeError> upgradePreReqErrorsIterator = upgradePreReqErrors.iterator();
            StringBuilder upgradePreReqErrorMessage = new StringBuilder();
            while (upgradePreReqErrorsIterator.hasNext()) {
                upgradePreReqErrorMessage.append(upgradePreReqErrorsIterator.next().getMessage());
            }
            this.addJohnsonEvent(agentJohnson, upgradePreReqErrorMessage.toString());
            return;
        }
        if (AbstractUpgradeManager.isUpgradeFromUnsupportedVersion(this.getConfiguredBuildNumber())) {
            this.errors.add(new UpgradeError("Unable to upgrade from build number " + this.getConfiguredBuildNumber()));
            this.addJohnsonEvent(agentJohnson, "Upgrading directly from versions of Confluence prior to 6.0.5 is not supported. Please follow the instructions in the Confluence Upgrade Guide to upgrade via a supported version. http://confluence.atlassian.com/display/DOC/Upgrading+Confluence");
            return;
        }
        boolean needUpgrade = this.needUpgrade();
        boolean needSchemaUpgrade = this.neededSchemaUpgrade();
        if (needSchemaUpgrade || needUpgrade) {
            this.beforeUpgrade();
        }
        try {
            if (needSchemaUpgrade || this.isSchemaUpgradeRequired()) {
                this.validateSchemaUpgradeTasks(this.getPreSchemaUpgradeTasks());
                this.validateSchemaUpdateIfNeeded();
                this.validateSchemaUpgradeTasks(this.getSchemaUpgradeTasks());
            }
            if (needUpgrade) {
                this.validateUpgradeTasks(this.getUpgradeTasksToRun());
            }
        }
        catch (UpgradeException e) {
            this.upgradedFlag.setUpgraded(false);
            if (e.getKbURL() != null) {
                this.addModernJohnsonEvent(agentJohnson, e);
            } else {
                this.addJohnsonValidationEvent(agentJohnson, e.getUpgradeErrors().isEmpty() ? e.getMessage() : e.getUpgradeErrorUiMessage());
            }
            throw e;
        }
        catch (ConfigurationException e) {
            this.upgradedFlag.setUpgraded(false);
            this.errors.add(new UpgradeError(e));
            this.addJohnsonValidationEvent(agentJohnson, e.getMessage());
            throw new UpgradeException(e);
        }
        try {
            if (needSchemaUpgrade || this.isSchemaUpgradeRequired()) {
                this.runSchemaUpgradeTasks(this.getPreSchemaUpgradeTasks());
                this.updateSchemaIfNeeded();
                this.runSchemaUpgradeTasks(this.getSchemaUpgradeTasks());
            }
            if (needUpgrade) {
                this.runUpgradeTasks(this.getUpgradeTasksToRun());
                this.validateBuildNumberUpdate();
            }
            this.finalizeIfNeeded();
            this.upgradedFlag.setUpgraded(true);
        }
        catch (UpgradeException e) {
            this.upgradedFlag.setUpgraded(false);
            if (e.getKbURL() != null) {
                this.addModernJohnsonEvent(agentJohnson, e);
            } else {
                this.addJohnsonUpgradeEvent(agentJohnson, e.getMessage());
            }
            throw e;
        }
        catch (ConfigurationException e) {
            this.upgradedFlag.setUpgraded(false);
            this.errors.add(new UpgradeError(e));
            this.addJohnsonUpgradeEvent(agentJohnson, e.getMessage());
            throw new UpgradeException(e);
        }
        this.releaseSchemaReferences();
    }

    private void validateBuildNumberUpdate() throws UpgradeException {
        BuildNumber homeDirectoryBuildNumber = new BuildNumber(this.getConfiguredBuildNumber());
        BuildNumber databaseBuildNumber = new BuildNumber(this.getDatabaseBuildNumber());
        if (!databaseBuildNumber.equals(homeDirectoryBuildNumber)) {
            throw new UpgradeException("Confluence will not start up because the build number in the home directory [" + homeDirectoryBuildNumber + "] doesn't match the build number in the database [" + databaseBuildNumber + "].", AbstractUpgradeManager.createUrl("https://confluence.atlassian.com/x/MAh2Fg"), true);
        }
    }

    private static URL createUrl(String url) {
        try {
            return new URL(url);
        }
        catch (MalformedURLException e) {
            throw new IllegalArgumentException("The provided string is not a valid URL: '" + url + "'", e);
        }
    }

    protected boolean neededSchemaUpgrade() {
        return Boolean.getBoolean("atlassian.forceSchemaUpdate") || this.isSchemaUpgradeRequired();
    }

    protected abstract void finalizeIfNeeded() throws UpgradeException;

    private boolean isSchemaUpgradeRequired() {
        IsNewerThan currentBuildNumberConstraint = new IsNewerThan(this.getRealBuildNumber());
        return currentBuildNumberConstraint.test(Integer.parseInt(this.getDatabaseBuildNumber()));
    }

    private static boolean isUpgradeFromUnsupportedVersion(String buildNumber) {
        return new BuildNumberComparator().compare(buildNumber, String.valueOf(7103)) < 0;
    }

    private void validateSchemaUpgradeTasks(Iterable<UpgradeTask> upgradeTasks) throws UpgradeException {
        this.executeUpgradeStep(upgradeTasks, UpgradeStep.SCHEMA_VALIDATION);
    }

    private void runSchemaUpgradeTasks(Iterable<UpgradeTask> upgradeTasks) throws UpgradeException {
        this.executeUpgradeStep(upgradeTasks, UpgradeStep.SCHEMA_UPGRADE);
    }

    private void validateUpgradeTasks(Iterable<UpgradeTask> upgradeTasks) throws UpgradeException {
        this.executeUpgradeStep(upgradeTasks, UpgradeStep.VALIDATION);
    }

    protected void runUpgradeTasks(List<UpgradeTask> upgradeTasks) throws UpgradeException {
        this.upgradeStarted();
        this.executeUpgradeStep(upgradeTasks, UpgradeStep.UPGRADE);
        try {
            this.initialUpgradeFinished();
        }
        catch (Exception e) {
            this.errors.add(new UpgradeError(e));
            throw new UpgradeException(e);
        }
    }

    private void executeUpgradeStep(Iterable<UpgradeTask> upgradeTasks, UpgradeStep step) throws UpgradeException {
        int localConfigurationBuildNumber = Integer.parseInt(this.getConfiguredBuildNumber());
        int databaseBuildNumber = Integer.parseInt(this.getDatabaseBuildNumber());
        for (UpgradeTask upgradeTask : upgradeTasks) {
            BuildNumberUpgradeConstraint constraint = upgradeTask.getConstraint();
            if (!constraint.test(localConfigurationBuildNumber)) continue;
            try {
                this.executeUpgradeTask(step, databaseBuildNumber, upgradeTask, constraint);
            }
            catch (UpgradeException e) {
                throw e;
            }
            catch (Throwable e) {
                this.errors.add(new UpgradeError(e));
                throw new UpgradeException("Upgrade task " + upgradeTask + " failed during the " + step + " phase due to: " + e.getMessage(), e);
            }
            finally {
                if (!CollectionUtils.isEmpty(upgradeTask.getErrors())) {
                    this.errors.addAll(upgradeTask.getErrors());
                }
                step.postUpgrade(this, upgradeTask);
            }
        }
    }

    private void executeUpgradeTask(UpgradeStep step, int databaseBuildNumber, UpgradeTask upgradeTask, BuildNumberUpgradeConstraint constraint) throws Exception {
        if (upgradeTask.isDatabaseUpgrade()) {
            if (constraint.test(databaseBuildNumber) && this.permitDatabaseUpgrades()) {
                log.debug("Executing {} phase for database upgrade task for build number {}, '{}'", new Object[]{step, upgradeTask.getBuildNumber(), upgradeTask.getName()});
                step.execute(this, upgradeTask);
                if (step.shouldUpdateBuildNumber() && CollectionUtils.isEmpty(upgradeTask.getErrors())) {
                    this.setDatabaseBuildNumber(upgradeTask.getBuildNumber());
                }
            } else {
                log.debug("Skipping {} phase for database upgrade task for build number {}, '{}'", new Object[]{step, upgradeTask.getBuildNumber(), upgradeTask.getName()});
            }
        } else {
            log.debug("Executing {} phase for non-database upgrade task for build number {}, '{}'", new Object[]{step, upgradeTask.getBuildNumber(), upgradeTask.getName()});
            step.execute(this, upgradeTask);
        }
        if (!CollectionUtils.isEmpty(upgradeTask.getErrors())) {
            throw new UpgradeException("Upgrade task " + upgradeTask.getName() + " failed during the " + step + " phase", Lists.newArrayList(upgradeTask.getErrors()));
        }
        if (step.shouldUpdateBuildNumber()) {
            this.upgradeTaskSucceeded(upgradeTask);
        }
    }

    protected List<UpgradeTask> getAllUpgradeTasks() {
        ArrayList upgradeTasks = Lists.newArrayList(this.getUpgradeTasksToRun());
        upgradeTasks.addAll(this.getPreSchemaUpgradeTasks());
        Collections.sort(upgradeTasks, UPGRADE_TASK_COMPARATOR);
        return upgradeTasks;
    }

    @Override
    public Collection<UpgradeTaskInfo> getAllUpgradeTasksInfo() {
        return Collections2.transform(this.getAllUpgradeTasks(), upgradeTask -> new UpgradeTaskInfo((UpgradeTask)upgradeTask){
            final /* synthetic */ UpgradeTask val$upgradeTask;
            {
                this.val$upgradeTask = upgradeTask;
            }

            @Override
            public String getBuildNumber() {
                return this.val$upgradeTask.getBuildNumber();
            }

            @Override
            public String getName() {
                return this.val$upgradeTask.getName();
            }

            @Override
            public String getShortDescription() {
                return this.val$upgradeTask.getShortDescription();
            }

            @Override
            public boolean isDatabaseUpgrade() {
                return this.val$upgradeTask.isDatabaseUpgrade();
            }
        });
    }

    protected List<UpgradeTask> getUpgradeTasksToRun() {
        ArrayList<UpgradeTask> upgradeTasks = new ArrayList<UpgradeTask>();
        upgradeTasks.addAll(this.getUpgradeTasks());
        upgradeTasks.addAll(this.getPluginDependentUpgradeTasks());
        Collections.sort(upgradeTasks, UPGRADE_TASK_COMPARATOR);
        return upgradeTasks;
    }

    protected void upgradeTaskSucceeded(UpgradeTask upgradeTask) throws Exception {
        this.setConfiguredBuildNumber(upgradeTask.getBuildNumber());
    }

    private void addJohnsonValidationEvent(JohnsonEventContainer johnson, String message) {
        this.addJohnsonEvent(johnson, "Pre-upgrade validation failed. Check the Confluence application logs for more details. You'll need to fix these problems and restart Confluence before you can upgrade. Upgrade error message: " + message);
    }

    private void addJohnsonUpgradeEvent(JohnsonEventContainer johnson, String message) {
        this.addJohnsonEvent(johnson, "Upgrade failed. Please consult the system logs for details. You will need to fix these problems, restore your database and confluence home directory to the pre upgrade state. Then retry the upgrade. Upgrade error message: " + message);
    }

    private void addJohnsonEvent(JohnsonEventContainer johnson, String message) {
        if (johnson != null) {
            johnson.addEvent(new Event(EventType.get((String)"upgrade"), message, EventLevel.get((String)"error")));
        }
    }

    private void addModernJohnsonEvent(JohnsonEventContainer johnson, UpgradeException e) {
        if (johnson != null) {
            EventLevel level = EventLevel.get((String)(e.isFatal() ? "fatal" : "error"));
            String cause = e.getCause() == null ? null : e.getCause().getMessage();
            Event event = new Event(EventType.get((String)"upgrade"), e.getMessage(), cause, level);
            event.addAttribute((Object)"helpUrl", (Object)e.getKbURL());
            event.addAttribute((Object)"uiVersion", (Object)"CONFSRVDEV-2798");
            johnson.addEvent(event);
        }
    }

    protected void upgradeStarted() {
        log.info("Starting automatic upgrade of Confluence");
    }

    public List<UpgradeTask> getPreSchemaUpgradeTasks() {
        return this.preSchemaUpgradeTasks.get();
    }

    public void setPreSchemaUpgradeTasks(List<UpgradeTask> preSchemaUpgradeTasks) {
        this.preSchemaUpgradeTasks = () -> preSchemaUpgradeTasks;
    }

    public void setPreSchemaUpgradeTaskNames(List<String> preSchemaUpgradeTaskNames) {
        this.preSchemaUpgradeTasks = () -> this.resolveBeanNames(preSchemaUpgradeTaskNames, UpgradeTask.class);
    }

    private <T> List<T> resolveBeanNames(List<String> beanNames, Class<T> requiredType) {
        return beanNames.stream().map(name -> this.beanFactory.getBean(name, requiredType)).collect(Collectors.toList());
    }

    public void setUpgradeTasks(List<UpgradeTask> upgradeTasks) {
        AbstractUpgradeManager.assertNoDuplicateBuildNumbers(upgradeTasks);
        this.upgradeTasks = () -> upgradeTasks;
    }

    public void setUpgradeTaskNames(List<String> upgradeTaskNames) {
        this.upgradeTasks = () -> {
            List<UpgradeTask> tasks = this.resolveBeanNames(upgradeTaskNames, UpgradeTask.class);
            AbstractUpgradeManager.assertNoDuplicateBuildNumbers(tasks);
            return tasks;
        };
    }

    public void setPluginAccessor(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    public static void assertNoDuplicateBuildNumbers(Iterable<UpgradeTask> upgradeTasks) throws IllegalStateException {
        HashSet buildNumbers = Sets.newHashSet();
        for (UpgradeTask upgradeTask : upgradeTasks) {
            if (buildNumbers.contains(upgradeTask.getBuildNumber())) {
                throw new IllegalStateException("Duplicate build number for upgrade task: " + upgradeTask.getClass().getName() + ", build number: " + upgradeTask.getBuildNumber());
            }
            buildNumbers.add(upgradeTask.getBuildNumber());
        }
    }

    public List<UpgradeTask> getUpgradeTasks() {
        return this.upgradeTasks.get();
    }

    public void setSchemaUpgradeTasks(List<UpgradeTask> upgradeTasks) {
        this.schemaUpgradeTasks = () -> upgradeTasks;
    }

    public void setSchemaUpgradeTaskNames(List<String> upgradeTaskNames) {
        this.schemaUpgradeTasks = () -> this.resolveBeanNames(upgradeTaskNames, UpgradeTask.class);
    }

    public List<UpgradeTask> getSchemaUpgradeTasks() {
        return this.schemaUpgradeTasks.get();
    }

    @Override
    public List<UpgradeError> getErrors() {
        return this.errors;
    }

    public void setPluginDependentUpgradeTasks(List<DeferredUpgradeTask> upgradeTasks) {
        this.pluginDependentUpgradeTasks = () -> upgradeTasks;
    }

    public void setPluginDependentUpgradeTaskNames(List<String> upgradeTaskNames) {
        this.pluginDependentUpgradeTasks = () -> this.resolveBeanNames(upgradeTaskNames, DeferredUpgradeTask.class);
    }

    public List<DeferredUpgradeTask> getPluginDependentUpgradeTasks() {
        return this.pluginDependentUpgradeTasks.get();
    }

    protected abstract List<UpgradeError> runUpgradePrerequisites();

    protected String getConfiguredBuildNumber() {
        return this.applicationConfig.getBuildNumber();
    }

    protected void setConfiguredBuildNumber(String buildNumber) throws ConfigurationException {
        if (!this.configuredBuildNumberNewerThan(buildNumber)) {
            this.applicationConfig.setBuildNumber(buildNumber);
            this.applicationConfig.save();
        }
    }

    @Override
    public boolean needUpgrade() {
        if (!this.applicationConfig.isSetupComplete()) {
            return false;
        }
        try {
            int realBuildNumber = Integer.parseInt(this.getRealBuildNumber());
            int configuredBuildNumber = Integer.parseInt(this.getConfiguredBuildNumber());
            return realBuildNumber != configuredBuildNumber;
        }
        catch (NumberFormatException e) {
            log.warn("Skipping upgrade because build numbers cannot be compared (application: \"{}\", configuration: \"{}\")", (Object)this.getRealBuildNumber(), (Object)this.getConfiguredBuildNumber());
            return false;
        }
    }

    @Override
    public boolean configuredBuildNumberNewerThan(String buildNumber) {
        return new BuildNumberComparator().compare(this.getConfiguredBuildNumber(), buildNumber) > 0;
    }

    @Override
    public boolean taskNewerThan(String buildNumber, UpgradeTask upgradeTask) {
        return new BuildNumberComparator().compare(upgradeTask.getBuildNumber(), buildNumber) > 0;
    }

    protected void initialUpgradeFinished() throws Exception {
        if (this.errors.isEmpty()) {
            if (this.permitDatabaseUpgrades()) {
                this.setDatabaseBuildNumber(this.getRealBuildNumber());
            }
            this.setConfiguredBuildNumber(this.getRealBuildNumber());
        }
        log.info("Upgrade initial stage completed successfully");
    }

    @Override
    public void entireUpgradeFinished() {
        log.info("Upgrade completed successfully");
    }

    protected abstract String getRealBuildNumber();

    protected abstract String getDatabaseBuildNumber();

    protected abstract void setDatabaseBuildNumber(String var1) throws Exception;

    protected boolean permitDatabaseUpgrades() throws UpgradeException {
        return true;
    }

    protected void beforeUpgrade() throws UpgradeException {
    }

    protected void postUpgrade() {
    }

    @Override
    public boolean isUpgraded() {
        return this.upgradedFlag.isUpgraded();
    }

    @Override
    public String getOldestSpaceImportAllowed() {
        List<UpgradeTask> upgradeTasksForSpaceImport = this.getAllUpgradeTasks();
        log.info("Compatibility of Space Import: Checking...");
        for (UpgradeTask task : Lists.reverse(upgradeTasksForSpaceImport)) {
            boolean isBackupSupport = task instanceof BackupSupport;
            if (!isBackupSupport || ((BackupSupport)((Object)task)).runOnSpaceImport()) {
                log.info("Compatibility of Space Import: {} Upgrade task \"{}\" prevents importing", (Object)task.getBuildNumber(), (Object)task.getShortDescription());
                return task.getBuildNumber();
            }
            log.info("Compatibility of Space Import: {} Upgrade task \"{}\" - Ok", (Object)task.getBuildNumber(), (Object)task.getShortDescription());
        }
        return Integer.toString(7103);
    }

    @Override
    public String getExportBuildNumber(boolean isSpaceExport) {
        List latestTasks = Lists.reverse(this.getAllUpgradeTasks());
        if (log.isDebugEnabled()) {
            this.logAllUpgradeTasks(latestTasks);
        }
        log.info("Backward compatibility for export: Checking...");
        for (UpgradeTask task : latestTasks) {
            boolean backwardsCompatible = AbstractUpgradeManager.isBackwardsCompatible(isSpaceExport, task);
            log.info("Build number {}: \"{}\" - {}", (Object[])new String[]{task.getBuildNumber(), task.getShortDescription(), backwardsCompatible ? "Ok" : "Not Ok"});
            if (backwardsCompatible) continue;
            log.info("Backward compatibility for export: \"{}\" prevents compatibility before {}", (Object)task.getShortDescription(), (Object)task.getBuildNumber());
            return task.getBuildNumber();
        }
        return Integer.toString(6452);
    }

    private static boolean isBackwardsCompatible(boolean spaceExport, UpgradeTask task) {
        if (task instanceof BackupSupport) {
            return spaceExport ? !((BackupSupport)((Object)task)).runOnSpaceImport() : !((BackupSupport)((Object)task)).breaksBackwardCompatibility();
        }
        return false;
    }

    @Override
    public Map<String, PluginExportCompatibility> getPluginExportCompatibility(boolean isSpaceExport) {
        String spaceIAVersion;
        HashMap compatibility = Maps.newHashMap();
        String workDayVersion = this.getPluginVersion(WORKDAY);
        if (workDayVersion != null) {
            compatibility.put(WORKDAY, new PluginExportCompatibility("1.1.30", workDayVersion));
        }
        if ((spaceIAVersion = this.getPluginVersion(SPACE_IA)) != null) {
            compatibility.put(SPACE_IA, new PluginExportCompatibility("5.0", spaceIAVersion));
        }
        return compatibility;
    }

    private String getPluginVersion(String pluginKey) {
        Plugin plugin;
        if (this.pluginAccessor != null && (plugin = this.pluginAccessor.getPlugin(pluginKey)) != null) {
            return plugin.getPluginInformation().getVersion();
        }
        return null;
    }

    private void logAllUpgradeTasks(List<UpgradeTask> latestTasks) {
        for (UpgradeTask task : latestTasks) {
            String taskName = AbstractUpgradeManager.getTaskName(task);
            boolean breaksBackwardCompatibility = task instanceof BackupSupport ? ((BackupSupport)((Object)task)).breaksBackwardCompatibility() : false;
            boolean runOnSpaceImport = task instanceof BackupSupport ? ((BackupSupport)((Object)task)).runOnSpaceImport() : false;
            log.debug("Build number {}: \"{}\"{}{}", (Object[])new String[]{task.getBuildNumber(), taskName, breaksBackwardCompatibility ? " - breaks backward compatibility" : "", runOnSpaceImport ? " - prevents space export" : ""});
        }
    }

    private static String getTaskName(UpgradeTask task) {
        String className = task.getClass().getSimpleName();
        if (className.contains("Proxy")) {
            return task.getShortDescription();
        }
        return className;
    }

    private static enum UpgradeStep {
        SCHEMA_VALIDATION{

            @Override
            public void execute(AbstractUpgradeManager upgradeManager, UpgradeTask upgradeTask) throws Exception {
                upgradeTask.validate();
            }
        }
        ,
        VALIDATION{

            @Override
            public void execute(AbstractUpgradeManager upgradeManager, UpgradeTask upgradeTask) throws Exception {
                upgradeTask.validate();
            }
        }
        ,
        SCHEMA_UPGRADE{

            @Override
            public void execute(AbstractUpgradeManager upgradeManager, UpgradeTask upgradeTask) throws Exception {
                upgradeTask.doUpgrade();
            }

            @Override
            public void postUpgrade(AbstractUpgradeManager upgradeManager, UpgradeTask upgradeTask) {
                upgradeManager.postUpgrade();
            }
        }
        ,
        UPGRADE{

            @Override
            public void execute(AbstractUpgradeManager upgradeManager, UpgradeTask upgradeTask) throws Exception {
                upgradeTask.doUpgrade();
            }

            @Override
            public boolean shouldUpdateBuildNumber() {
                return true;
            }

            @Override
            public void postUpgrade(AbstractUpgradeManager upgradeManager, UpgradeTask upgradeTask) {
                upgradeManager.postUpgrade();
            }
        };


        public abstract void execute(AbstractUpgradeManager var1, UpgradeTask var2) throws Exception;

        public boolean shouldUpdateBuildNumber() {
            return false;
        }

        public void postUpgrade(AbstractUpgradeManager upgradeManager, UpgradeTask upgradeTask) {
        }
    }
}

