/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.bandana.BandanaPersister
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.confluence.upgrade.BuildAndVersionNumber
 *  com.atlassian.confluence.upgrade.BuildNumberComparator
 *  com.atlassian.confluence.upgrade.DeferredUpgradeTask
 *  com.atlassian.confluence.upgrade.UpgradeError
 *  com.atlassian.confluence.upgrade.UpgradeFinalizationManager
 *  com.atlassian.confluence.upgrade.UpgradeManager
 *  com.atlassian.confluence.upgrade.UpgradeTask
 *  com.atlassian.core.util.ProgressMeter
 *  com.atlassian.plugin.manager.PluginPersistentStateStore
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.core.SchedulerServiceController
 *  com.google.common.collect.Lists
 *  com.opensymphony.util.TextUtils
 *  org.apache.commons.collections.CollectionUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.hibernate.HibernateException
 *  org.hibernate.MappingException
 *  org.hibernate.Session
 *  org.hibernate.Transaction
 *  org.hibernate.engine.spi.SessionImplementor
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Required
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.TransactionStatus
 *  org.springframework.transaction.support.DefaultTransactionDefinition
 *  org.springframework.transaction.support.TransactionCallback
 *  org.springframework.transaction.support.TransactionCallbackWithoutResult
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.importexport.xmlimport;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.bandana.BandanaPersister;
import com.atlassian.cache.CacheManager;
import com.atlassian.config.ConfigurationException;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.persistence.hibernate.TransientHibernateHandle;
import com.atlassian.confluence.impl.cache.CacheFlusher;
import com.atlassian.confluence.impl.core.persistence.hibernate.schema.ConfluenceSchemaCreator;
import com.atlassian.confluence.impl.core.persistence.hibernate.schema.ConfluenceSchemaHelper;
import com.atlassian.confluence.importexport.ChainedImportedObjectPreProcessor;
import com.atlassian.confluence.importexport.DefaultImportContext;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.importexport.ImportProcessorSummary;
import com.atlassian.confluence.importexport.ImportedObjectPreProcessor;
import com.atlassian.confluence.importexport.Importer;
import com.atlassian.confluence.importexport.impl.ExportScope;
import com.atlassian.confluence.importexport.xmlimport.HibernateHiLoIdFixer;
import com.atlassian.confluence.importexport.xmlimport.InputStreamFactory;
import com.atlassian.confluence.importexport.xmlimport.RestoreBandanaValuesTransactionCallbackDecorator;
import com.atlassian.confluence.importexport.xmlimport.RestorePluginStateStoreTransactionCallbackDecorator;
import com.atlassian.confluence.importexport.xmlimport.XmlImporter;
import com.atlassian.confluence.labels.Labelling;
import com.atlassian.confluence.links.OutgoingLink;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.schedule.ScheduleUtil;
import com.atlassian.confluence.search.IndexManager;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.setup.DefaultHibernateConfigurator;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.upgrade.BuildAndVersionNumber;
import com.atlassian.confluence.upgrade.BuildNumberComparator;
import com.atlassian.confluence.upgrade.DeferredUpgradeTask;
import com.atlassian.confluence.upgrade.UpgradeError;
import com.atlassian.confluence.upgrade.UpgradeFinalizationManager;
import com.atlassian.confluence.upgrade.UpgradeManager;
import com.atlassian.confluence.upgrade.UpgradeTask;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.core.util.ProgressMeter;
import com.atlassian.plugin.manager.PluginPersistentStateStore;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.core.SchedulerServiceController;
import com.google.common.collect.Lists;
import com.opensymphony.util.TextUtils;
import java.io.File;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.engine.spi.SessionImplementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@Deprecated
public abstract class BackupImporter
extends Importer {
    public static final String CONFLUENCE_IMPORT_USE_LEGACY_IMPORTER = "confluence.import.use-legacy-importer";
    public static final int MINIMUM_BUILD_NUMBER = 7103;
    public static final String MINIMUM_VERSION = "6.0.5";
    public static final BuildAndVersionNumber MINIMUM_FULL_IMPORT_BUILD_NUMBER = new BuildAndVersionNumber(Integer.valueOf(7103), "6.0.5");
    public static final BuildAndVersionNumber MINIMUM_SPACE_IMPORT_BUILD_NUMBER = new BuildAndVersionNumber(Integer.valueOf(7103), "6.0.5");
    public static final BuildAndVersionNumber FULL_EXPORT_BACKWARDS_COMPATIBILITY;
    public static final BuildAndVersionNumber SPACE_EXPORT_BACKWARDS_COMPATIBILITY;
    private static final Logger log;
    private SpaceManager spaceManager;
    private CacheFlusher cacheFlusher;
    private UserAccessor userAccessor;
    private ConfluenceSchemaCreator confluenceSchemaCreator;
    private IndexManager indexManager;
    private SchedulerServiceController schedulerServiceController;
    private I18NBeanFactory i18NBeanFactory;
    private UpgradeManager upgradeManager;
    private UpgradeFinalizationManager upgradeFinalizationManager;
    private PlatformTransactionManager transactionManager;
    private BandanaManager bandanaManager;
    private BandanaPersister bandanaPersister;
    protected SettingsManager settingsManager;
    private boolean backupAttachments = true;
    private List<UpgradeTask> postRestoreUpgradeTasks;
    private Boolean incrementalImport = null;
    private XmlImporter xmlImporter;
    private List<ImportedObjectPreProcessor> preProcessors;
    private PluginPersistentStateStore pluginStateStore;

    @Override
    protected void preImport() throws ImportExportException {
        if (!BackupImporter.isBackupSupportedVersion(this.getBuildNumberOfImport()) && !BackupImporter.isBackupSupportedVersion(this.getCreatedByBuildNumberOfImport())) {
            throw new ImportExportException("Unable to import backups from versions of Confluence prior to 6.0.5 (build number: 7103). Build number of backup: " + this.getBuildNumberOfImport() + " CreatedBy Build Number of backup: " + this.getCreatedByBuildNumberOfImport());
        }
        if (!this.isIncrementalImport()) {
            this.unIndexAll();
            this.pauseSchedulerAndFlushJobs();
        }
    }

    private void pauseSchedulerAndFlushJobs() throws ImportExportException {
        log.info("Switching scheduler to standby mode");
        try {
            this.schedulerServiceController.standby();
            this.schedulerServiceController.waitUntilIdle(ScheduleUtil.getSchedulerFlushTimeout(), TimeUnit.SECONDS);
        }
        catch (SchedulerServiceException e) {
            String m = e.getMessage();
            Throwable t = e.getCause();
            log.error(m, t);
            throw new ImportExportException(this.getText("backup.importer.scheduler.standby.error"), t);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            String m = e.getMessage();
            Throwable t = e.getCause();
            log.error(m, t);
            throw new ImportExportException(this.getText("backup.importer.scheduler.standby.error"), t);
        }
    }

    public static boolean isBackupSupportedVersion(String buildNumberOfImport) {
        if ("0".equals(buildNumberOfImport)) {
            return true;
        }
        if (StringUtils.isBlank((CharSequence)buildNumberOfImport)) {
            return false;
        }
        try {
            Integer.parseInt(buildNumberOfImport);
        }
        catch (NumberFormatException x) {
            return false;
        }
        return new BuildNumberComparator().compare(buildNumberOfImport, String.valueOf(7103)) >= 0;
    }

    private void logUpgradeErrors(Collection<UpgradeError> errors) {
        log.error(errors.size() + " errors were encountered during upgrade:");
        int i = 1;
        for (UpgradeError error : errors) {
            log.error("{}: {}", (Object)i++, (Object)(error.getError() != null ? error.getError().getMessage() : error.getMessage()));
        }
    }

    @Override
    protected ImportProcessorSummary doImportInternal() throws ImportExportException {
        boolean shouldUpgrade;
        log.info("Starting import of data");
        Session s = this.sessionFactory.getCurrentSession();
        this.flushAndCommitSession(s);
        s.clear();
        this.addPreProcessors();
        AtomicReference<ImportProcessorSummary> summaryReference = new AtomicReference<ImportProcessorSummary>();
        try {
            TransactionTemplate tt = new TransactionTemplate(this.transactionManager, (TransactionDefinition)new DefaultTransactionDefinition(3));
            tt.execute(this.getImportTxCallback(summaryReference));
        }
        catch (RuntimeException e) {
            if (e.getCause() instanceof ImportExportException) {
                throw (ImportExportException)e.getCause();
            }
            throw e;
        }
        boolean bl = shouldUpgrade = this.shouldUpgrade() && StringUtils.isNotBlank((CharSequence)this.getBuildNumberOfImport());
        if (shouldUpgrade) {
            log.info("Executing upgrades");
            this.doUpgrades();
        }
        s = this.sessionFactory.getCurrentSession();
        this.flushAndCommitSession(s);
        try {
            if (!this.isIncrementalImport()) {
                this.confluenceSchemaCreator.createAdditionalDatabaseConstraints();
            }
        }
        catch (MappingException e) {
            throw new ImportExportException(e);
        }
        this.cacheFlusher.flushCaches();
        return summaryReference.get();
    }

    private TransactionCallback<Object> getImportTxCallback(final AtomicReference<ImportProcessorSummary> summaryReference) {
        TransactionCallbackWithoutResult importCallback = new TransactionCallbackWithoutResult(){

            protected void doInTransactionWithoutResult(TransactionStatus txStatus) {
                try {
                    log.info("Beginning inital import process");
                    ImportProcessorSummary importSummary = BackupImporter.this.importEverything();
                    log.info("Completed initial import process");
                    summaryReference.set(importSummary);
                }
                catch (ImportExportException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        return new RestoreBandanaValuesTransactionCallbackDecorator<Object>(this.bandanaManager, this.bandanaPersister, new RestorePluginStateStoreTransactionCallbackDecorator(this.pluginStateStore, importCallback));
    }

    private void addPreProcessors() {
        DefaultImportContext defaultContext = (DefaultImportContext)this.context;
        ArrayList preProcessors = Lists.newArrayList(this.preProcessors);
        ImportedObjectPreProcessor currentPreProcessor = defaultContext.getPreProcessor();
        if (currentPreProcessor != null) {
            preProcessors.add(currentPreProcessor);
        }
        defaultContext.setPreProcessor(new ChainedImportedObjectPreProcessor(preProcessors));
    }

    @Override
    protected void postImportAndCleanUp() throws ImportExportException {
        try {
            log.info("Restarting the scheduler");
            this.schedulerServiceController.start();
            log.info("The scheduler was successfully restarted");
        }
        catch (SchedulerServiceException e) {
            log.error("Could not restart atlassian-scheduler after the import completed", (Throwable)e);
            throw new ImportExportException(this.getText("backup.importer.scheduler.restart.error"), e);
        }
    }

    void doUpgrades() throws ImportExportException {
        String importBuildNumber = this.getCreatedByBuildNumberOfImport();
        if (StringUtils.isBlank((CharSequence)importBuildNumber)) {
            importBuildNumber = this.getBuildNumberOfImport();
        }
        ArrayList<DeferredUpgradeTask> deferredUpgradeTasks = new ArrayList<DeferredUpgradeTask>();
        int i = 0;
        for (UpgradeTask upgradeTask : this.postRestoreUpgradeTasks) {
            log.debug("Running upgrade task {} of {}", (Object)(++i), (Object)this.postRestoreUpgradeTasks.size());
            if (!upgradeTask.getConstraint().test(Integer.parseInt(importBuildNumber))) {
                log.debug("Skipping upgrade task: " + upgradeTask.getClass());
                continue;
            }
            try {
                upgradeTask.doUpgrade();
            }
            catch (Exception e) {
                log.error("Error while upgrading imported data " + e.getMessage(), (Throwable)e);
                throw new ImportExportException("Error while upgrading imported data " + e.getMessage(), e);
            }
            if (CollectionUtils.isNotEmpty((Collection)upgradeTask.getErrors())) {
                this.logUpgradeErrors(upgradeTask.getErrors());
                throw new ImportExportException(upgradeTask.getErrors().size() + " errors occurred while upgrading imported data. See logs for details.");
            }
            if (!(upgradeTask instanceof DeferredUpgradeTask)) continue;
            deferredUpgradeTasks.add((DeferredUpgradeTask)upgradeTask);
        }
        i = 0;
        for (DeferredUpgradeTask deferredUpgradeTask : deferredUpgradeTasks) {
            log.debug("Running deferred upgrade task {} of {}", (Object)(++i), (Object)deferredUpgradeTasks.size());
            try {
                deferredUpgradeTask.doDeferredUpgrade();
            }
            catch (Exception e) {
                log.error("Error while upgrading imported data " + e.getMessage(), (Throwable)e);
                throw new ImportExportException("Error while upgrading imported data " + e.getMessage(), e);
            }
        }
        try {
            this.sessionFactory.getCurrentSession().flush();
        }
        catch (HibernateException e) {
            throw new ImportExportException("Import failed flushing session to database", e);
        }
        if (this.upgradeManager.configuredBuildNumberNewerThan(importBuildNumber)) {
            this.upgradeManager.setDatabaseBuildNumber();
            this.upgradeManager.entireUpgradeFinished();
        }
        try {
            this.upgradeFinalizationManager.markAsFullyFinalized(true);
        }
        catch (ConfigurationException e) {
            throw new ImportExportException("Failed to configure finalized build-number", e);
        }
    }

    protected ImportProcessorSummary importEverything() throws ImportExportException {
        return this.importEverything(false);
    }

    protected ImportProcessorSummary importEverything(boolean hasExtraToImport) throws ImportExportException {
        ImportProcessorSummary summary = this.importEntities(hasExtraToImport);
        if (!this.isIncrementalImport()) {
            this.resetIdentifierGenerators();
        }
        return summary;
    }

    private void resetIdentifierGenerators() throws ImportExportException {
        new HibernateHiLoIdFixer(this.getEventPublisher(), this.sessionFactory).fixHiLoTable();
    }

    protected ImportProcessorSummary importEntities(boolean hasExtraToImport) throws ImportExportException {
        ProgressMeter meter = this.context.getProgressMeter();
        Session session = null;
        try {
            meter.setPercentage(0);
            this.prepareDatabaseForRestore(meter);
            session = this.sessionFactory.getCurrentSession();
            session.clear();
            log.info("Importing XML");
            ImportProcessorSummary result = this.xmlImporter.doImport(session, this.getXmlImportStreamFactory(), this.isIncrementalImport(), this.context);
            log.info("XML import complete");
            Set<TransientHibernateHandle> writtenIds = result.getPersistedMappedHandles();
            this.postProcess(session, writtenIds, meter);
            return result;
        }
        catch (Exception e) {
            log.error("Cannot import the entities: ", (Throwable)e);
            throw new ImportExportException(e);
        }
    }

    private InputStreamFactory getXmlImportStreamFactory() {
        return this::getXmlEntitiesStream;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void postProcess(Session session, Set<TransientHibernateHandle> writtenIds, ProgressMeter meter) throws HibernateException, ImportExportException {
        boolean dirty = false;
        int processedCount = 0;
        meter.setStatus("Applying special processing");
        meter.setTotalObjects(meter.getCurrentCount() + writtenIds.size());
        Transaction tx = session.getTransaction();
        try {
            for (TransientHibernateHandle id : writtenIds) {
                ++processedCount;
                log.debug("Applying special processing on " + id);
                if (this.doSpecialProcessing(session, id)) {
                    dirty = true;
                }
                if (this.context.getPostProcessor() != null) {
                    Object obj = id.get(session);
                    try {
                        if (this.context.getPostProcessor().process(obj)) {
                            session.update(obj);
                            dirty = true;
                        }
                    }
                    catch (Exception e) {
                        log.error("Postprocessing failed on " + obj + ": " + e.getMessage(), (Throwable)e);
                    }
                }
                meter.setCurrentCount(meter.getCurrentCount() + 1);
                if (processedCount % 100 != 0) continue;
                if (dirty) {
                    tx.commit();
                    tx = session.beginTransaction();
                    dirty = false;
                } else {
                    session.flush();
                }
                session.clear();
            }
            tx.commit();
            session.clear();
            session.beginTransaction();
        }
        finally {
            if (!tx.getStatus().canRollback()) {
                tx.rollback();
            }
            this.cacheFlusher.flushCaches();
        }
        meter.setPercentage(90);
        meter.setStatus("Entities loaded...");
    }

    private void prepareDatabaseForRestore(ProgressMeter meter) throws ImportExportException, HibernateException, SQLException, ConfigurationException {
        Session s = this.sessionFactory.getCurrentSession();
        this.flushAndCommitSession(s);
        if (!this.isIncrementalImport()) {
            meter.setStatus("Deleting existing content.");
            this.deleteAllDatabaseContent();
            AuthenticatedUserThreadLocal.reset();
        }
        this.flushAndCommitSession(s);
    }

    private void flushAndCommitSession(Session s) {
        if (s != null) {
            try {
                log.info("Flushing session and committing pending transactions");
                if (s.getTransaction().isActive()) {
                    s.flush();
                }
                ((SessionImplementor)s).connection().commit();
                log.info("Session flush and commit complete");
            }
            catch (HibernateException he) {
                log.error("error flushing session", (Throwable)he);
            }
            catch (RuntimeException sqle) {
                log.error("error committing connection", (Throwable)sqle);
            }
            catch (SQLException exception) {
                log.error("error committing connection", (Throwable)exception);
            }
        }
    }

    protected boolean doSpecialProcessing(Session session, TransientHibernateHandle key) throws ImportExportException, HibernateException {
        Labelling labelling;
        boolean changed = false;
        if (ContentEntityObject.class.isAssignableFrom(key.getClazz())) {
            ContentEntityObject entityObject = (ContentEntityObject)key.get(session);
            Iterator<ConfluenceEntityObject> it = entityObject.getAttachments().iterator();
            while (it.hasNext()) {
                Attachment att = it.next();
                if (TextUtils.stringSet((String)att.getFileName())) continue;
                it.remove();
                session.delete((Object)att);
                changed = true;
            }
            it = entityObject.getOutgoingLinks().iterator();
            while (it.hasNext()) {
                OutgoingLink link = (OutgoingLink)it.next();
                if (TextUtils.stringSet((String)link.getDestinationSpaceKey())) continue;
                it.remove();
                changed = true;
            }
            if (entityObject instanceof Page) {
                Space s;
                Page page = (Page)entityObject;
                String defaultSpaceKey = ((DefaultImportContext)this.context).getDefaultSpaceKey();
                if (page.getSpace() == null && page.isLatestVersion() && defaultSpaceKey != null && (s = this.spaceManager.getSpace(defaultSpaceKey)) != null) {
                    page.setSpace(s);
                    changed = true;
                }
                if (page.getParent() != null && page.getParent().getId() == page.getId()) {
                    log.error("Detected page with self as its parent. Removing relationship.");
                    page.setParentPage(null);
                    changed = true;
                }
            }
            if (changed) {
                session.update((Object)entityObject);
            }
        } else if (this.isIncrementalImport() && Space.class.isAssignableFrom(key.getClazz())) {
            Space space = (Space)key.get(session);
            String defaultUsersGroup = this.settingsManager.getGlobalSettings().getDefaultUsersGroup();
            Iterator<SpacePermission> iter = space.getPermissions().iterator();
            while (iter.hasNext()) {
                SpacePermission permission = iter.next();
                if (this.isExportedDefaultUserGroupPermission(permission)) {
                    permission.setSpace(space);
                    permission.setGroup(defaultUsersGroup);
                    changed = true;
                    continue;
                }
                if (!this.isSpacePermissionUnknown(permission)) continue;
                permission.setSpace(space);
                iter.remove();
                session.delete((Object)permission);
                changed = true;
            }
            if (changed) {
                session.update((Object)space);
            }
        } else if (OutgoingLink.class.isAssignableFrom(key.getClazz())) {
            OutgoingLink link = (OutgoingLink)key.get(session);
            if (!TextUtils.stringSet((String)link.getDestinationSpaceKey())) {
                session.delete((Object)link);
                changed = true;
            } else {
                String destinationPageTitle = link.getDestinationPageTitle();
                if (destinationPageTitle == null || destinationPageTitle.trim().length() == 0) {
                    link.setDestinationPageTitle("unknown");
                    session.update((Object)link);
                    changed = true;
                }
            }
        } else if (Labelling.class.isAssignableFrom(key.getClazz()) && ((labelling = (Labelling)key.get(session)).getLableable() == null || labelling.getLabel() == null)) {
            log.warn("Deleting invalid labelling [" + labelling + "]");
            session.delete((Object)labelling);
            changed = true;
        }
        return changed;
    }

    protected boolean isExportedDefaultUserGroupPermission(SpacePermission permission) {
        String groupName = permission.getGroup();
        String userName = permission.getUserName();
        String defaultUsersGroupFromImportContext = (String)StringUtils.defaultIfEmpty((CharSequence)this.context.getDefaultUsersGroup(), (CharSequence)"confluence-users");
        return defaultUsersGroupFromImportContext.equals(groupName) && userName == null;
    }

    private boolean isSpacePermissionUnknown(SpacePermission permission) {
        String groupName = permission.getGroup();
        String userName = permission.getUserName();
        return groupName != null && this.userAccessor.getGroup(groupName) == null || userName != null && this.userAccessor.getUserByName(userName) == null;
    }

    public void deleteAllDatabaseContent() throws HibernateException, SQLException, ConfigurationException {
        log.info("Dropping and recreating Confluence Schema");
        this.confluenceSchemaCreator.createSchema(true);
        log.info("Flushing all caches");
        this.cacheFlusher.flushCaches();
    }

    protected void unIndexAll() {
        log.info("Deleting search index");
        this.indexManager.unIndexAll();
    }

    protected abstract Properties getDescriptorProperties() throws ImportExportException;

    @Deprecated
    public void setCacheManager(CacheManager cacheManager) {
        this.cacheFlusher = CacheFlusher.cacheFlusher(cacheManager);
    }

    @Required
    public void setCacheFlusher(CacheFlusher cacheFlusher) {
        this.cacheFlusher = cacheFlusher;
    }

    @Required
    public void setUserAccessor(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    @Deprecated
    public void setSchemaHelper(ConfluenceSchemaHelper schemaHelper) {
        ConfluenceSchemaCreator confluenceSchemaCreator = DefaultHibernateConfigurator.createConfluenceSchemaCreator(schemaHelper.getConfiguration());
        this.setConfluenceSchemaCreator(confluenceSchemaCreator);
    }

    public String getExportType() throws ImportExportException {
        return this.getDescriptorProperties().getProperty("exportType");
    }

    public boolean isBackupAttachments() throws ImportExportException {
        String backupAttachmentsProperty = this.getDescriptorProperties().getProperty("backupAttachments");
        if (TextUtils.stringSet((String)backupAttachmentsProperty)) {
            this.backupAttachments = Boolean.valueOf(backupAttachmentsProperty);
        }
        return this.backupAttachments;
    }

    public String getBuildNumberOfImport() throws ImportExportException {
        return this.getDescriptorProperties().getProperty("buildNumber");
    }

    public String getCreatedByBuildNumberOfImport() throws ImportExportException {
        return this.getDescriptorProperties().getProperty("createdByBuildNumber");
    }

    public boolean isIncrementalImport() throws ImportExportException {
        if (this.incrementalImport == null) {
            boolean isIncrementalImportSetToTrue = this.context instanceof DefaultImportContext && ((DefaultImportContext)this.context).isIncrementalImport();
            boolean isImportTypeIncremental = !this.isExportScopeAll();
            this.incrementalImport = isIncrementalImportSetToTrue || isImportTypeIncremental;
        }
        return this.incrementalImport;
    }

    private boolean shouldUpgrade() throws ImportExportException {
        if (!this.isIncrementalImport()) {
            return true;
        }
        if (this.context instanceof DefaultImportContext) {
            return ((DefaultImportContext)this.context).isRequireUpgrades();
        }
        return false;
    }

    public boolean isExportScopeAll() throws ImportExportException {
        if (this.getExportType() == null) {
            File backupedFilePath = new File(this.context.getWorkingFile());
            return backupedFilePath.getName().startsWith("export-");
        }
        return this.getExportType().equalsIgnoreCase(ExportScope.ALL.getString());
    }

    private String getText(String key) {
        try {
            I18NBean i18nBean = this.i18NBeanFactory.getI18NBean();
            return i18nBean.getText(key);
        }
        catch (Exception t) {
            log.error("Erroring translating i18n key: " + key, (Throwable)t);
            return key;
        }
    }

    public abstract InputStream getXmlEntitiesStream() throws ImportExportException;

    @Required
    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    @Required
    public void setIndexManager(IndexManager indexManager) {
        this.indexManager = indexManager;
    }

    @Required
    public void setI18NBeanFactory(I18NBeanFactory i18NBeanFactory) {
        this.i18NBeanFactory = i18NBeanFactory;
    }

    @Required
    public void setPostRestoreUpgradeTasks(List<UpgradeTask> postRestoreUpgradeTasks) {
        this.postRestoreUpgradeTasks = postRestoreUpgradeTasks;
    }

    @Required
    public void setUpgradeManager(UpgradeManager upgradeManager) {
        this.upgradeManager = upgradeManager;
    }

    protected UpgradeManager getUpgradeManager() {
        return this.upgradeManager;
    }

    @Required
    public void setUpgradeFinalizationManager(UpgradeFinalizationManager upgradeFinalizationManager) {
        this.upgradeFinalizationManager = upgradeFinalizationManager;
    }

    @Required
    public void setXmlImporter(XmlImporter xmlImporter) {
        this.xmlImporter = xmlImporter;
    }

    @Required
    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Required
    public void setPreProcessors(List<ImportedObjectPreProcessor> preProcessors) {
        this.preProcessors = preProcessors;
    }

    @Required
    public void setPluginStateStore(PluginPersistentStateStore pluginStateStore) {
        this.pluginStateStore = pluginStateStore;
    }

    @Required
    public void setBandanaManager(BandanaManager bandanaManager) {
        this.bandanaManager = bandanaManager;
    }

    @Required
    public void setBandanaPersister(BandanaPersister bandanaPersister) {
        this.bandanaPersister = bandanaPersister;
    }

    @Required
    public void setSettingsManager(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    @Required
    public void setConfluenceSchemaCreator(ConfluenceSchemaCreator confluenceSchemaCreator) {
        this.confluenceSchemaCreator = confluenceSchemaCreator;
    }

    public void setSchedulerServiceController(SchedulerServiceController schedulerServiceController) {
        this.schedulerServiceController = schedulerServiceController;
    }

    static {
        SPACE_EXPORT_BACKWARDS_COMPATIBILITY = FULL_EXPORT_BACKWARDS_COMPATIBILITY = new BuildAndVersionNumber(Integer.valueOf(9001), "8.0.0");
        log = LoggerFactory.getLogger(BackupImporter.class);
    }
}

