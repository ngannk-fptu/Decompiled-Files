/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.UpgradeManager
 *  com.atlassian.core.util.ProgressMeter
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.util.profiling.Timers
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.hibernate.Transaction
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.importexport;

import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.FileExportable;
import com.atlassian.confluence.core.NotExportable;
import com.atlassian.confluence.core.persistence.hibernate.TransientHibernateHandle;
import com.atlassian.confluence.impl.importexport.AbstractExporterImpl;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.importexport.impl.AtlassianXMLDatabinder;
import com.atlassian.confluence.importexport.impl.DecoratedProgressMeter;
import com.atlassian.confluence.importexport.impl.ExportDescriptor;
import com.atlassian.confluence.importexport.impl.ExportScope;
import com.atlassian.confluence.importexport.impl.HibernateObjectHandleTranslator;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.upgrade.UpgradeManager;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.core.util.ProgressMeter;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timers;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractXmlExporter
extends AbstractExporterImpl {
    private static final Logger log = LoggerFactory.getLogger(AbstractXmlExporter.class);
    public static final String EXPORT_DESCRIPTOR_FILE_NAME = "exportDescriptor.properties";
    public static final String ENTITIES_FILE_NAME = "entities.xml";
    public static final String ATTACHMENTS_DIRECTORY = "attachments";
    public static final String TEMPLATES_DIRECTORY = "velocity";
    protected SessionFactory sessionFactory5;
    private SettingsManager settingsManager;
    protected BootstrapManager bootstrapManager;
    protected SpaceManager spaceManager;
    private UpgradeManager upgradeManager;

    protected abstract List<TransientHibernateHandle> getHandlesOfObjectsForExport(HibernateObjectHandleTranslator var1, Session var2) throws ImportExportException;

    public void setSessionFactory5(SessionFactory sessionFactory) {
        this.sessionFactory5 = sessionFactory;
    }

    public void setBootstrapManager(BootstrapManager bootstrapManager) {
        this.bootstrapManager = bootstrapManager;
    }

    protected List<ConfluenceEntityObject> getObjectsExcludedFromExport() throws ImportExportException {
        if (this.getWorkingExportContext() != null && this.getWorkingExportContext().getExceptionEntities() != null) {
            return this.getWorkingExportContext().getExceptionEntities();
        }
        return new ArrayList<ConfluenceEntityObject>();
    }

    public String doExport(HibernateObjectHandleTranslator translator, ProgressMeter progress) throws ImportExportException {
        String baseExportPath = this.createAndSetExportDirectory();
        this.writeBackupDescriptor();
        this.backupEverything(translator, progress);
        return baseExportPath;
    }

    protected void backupEverything(HibernateObjectHandleTranslator translator, ProgressMeter progress) throws ImportExportException {
        this.backupEntities(translator, progress);
    }

    protected void writeBackupDescriptor() throws ImportExportException {
        String createdByBuildNumber;
        ExportDescriptor exportDescriptor = new ExportDescriptor();
        ExportScope exportScope = this.getExportScope();
        exportDescriptor.setScope(exportScope);
        exportDescriptor.setDefaultUserGroup(this.settingsManager.getGlobalSettings().getDefaultUsersGroup());
        String earliestCompatibleBuildNumber = createdByBuildNumber = this.bootstrapManager.getBuildNumber();
        if (this.upgradeManager != null) {
            earliestCompatibleBuildNumber = this.upgradeManager.getExportBuildNumber(exportScope == ExportScope.SPACE);
            Map pluginExportCompatibility = this.upgradeManager.getPluginExportCompatibility(exportScope == ExportScope.SPACE);
            exportDescriptor.setPluginExportCompatibility(pluginExportCompatibility);
        }
        LicenseService licenseService = (LicenseService)ContainerManager.getComponent((String)"licenseService");
        String supportEntitlementNumber = licenseService.retrieve().getSupportEntitlementNumber();
        exportDescriptor.setCreatedByBuildNumber(createdByBuildNumber);
        exportDescriptor.setBuildNumber(earliestCompatibleBuildNumber);
        exportDescriptor.setVersionNumber(GeneralUtil.getVersionNumber());
        exportDescriptor.setSource(ExportDescriptor.Source.SERVER);
        this.setCustomProperties(exportDescriptor);
        if (supportEntitlementNumber != null) {
            exportDescriptor.setSupportEntitlementNumber(supportEntitlementNumber);
        }
        if (this.getContext() != null) {
            exportDescriptor.setBackupAttachments(this.getContext().isExportAttachments());
        }
        try (OutputStream os = this.getDescriptorOutputStream();){
            exportDescriptor.saveToOutputStream(os);
        }
        catch (IOException e) {
            throw new ImportExportException("Couldn't write the exportDescriptor file", e);
        }
    }

    protected void setCustomProperties(ExportDescriptor exportDescriptor) {
    }

    protected OutputStream getDescriptorOutputStream() throws FileNotFoundException {
        return new FileOutputStream(new File(this.getWorkingExportContext().getExportDirectory(), EXPORT_DESCRIPTOR_FILE_NAME));
    }

    protected OutputStream getXmlOutputStream() throws IOException {
        File destFile = new File(this.getWorkingExportContext().getExportDirectory(), ENTITIES_FILE_NAME);
        destFile.getParentFile().mkdirs();
        destFile.createNewFile();
        return new FileOutputStream(destFile);
    }

    protected ExportScope getExportScope() {
        if (this.getWorkingExportContext() == null) {
            return ExportScope.ALL;
        }
        return this.getWorkingExportContext().getExportScope();
    }

    protected abstract List getSourceTemplateDirForCopying();

    @Deprecated
    protected void backupEntities(HibernateObjectHandleTranslator translator, ProgressMeter progress) throws ImportExportException {
        try {
            String encoding = this.settingsManager.getGlobalSettings().getDefaultEncoding();
            SessionFactoryImplementor implementor = (SessionFactoryImplementor)this.sessionFactory5;
            AtlassianXMLDatabinder databinder = new AtlassianXMLDatabinder(implementor, encoding, translator);
            databinder.excludeClassFromEntityExport(FileExportable.class);
            databinder.excludeClassFromEntityExport(NotExportable.class);
            databinder.excludeClassFromReferenceExport(NotExportable.class);
            Session session = this.sessionFactory5.getCurrentSession();
            Transaction tx = session.getTransaction().isActive() ? session.getTransaction() : session.beginTransaction();
            progress.setStatus("Getting objects to export");
            List<TransientHibernateHandle> objectsForExport = this.getHandlesOfObjectsForExport(translator, session);
            List<ConfluenceEntityObject> excludedObjects = this.getObjectsExcludedFromExport();
            if (this.context != null && !this.context.isExportComments()) {
                databinder.excludeClass(Comment.class);
            }
            session.flush();
            session.clear();
            tx.commit();
            this.sessionFactory5.getCurrentSession().beginTransaction();
            databinder.bindAll(objectsForExport);
            databinder.unbindAll(excludedObjects);
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(this.getXmlOutputStream(), encoding), 32768);
                 Ticker ignored = Timers.start((String)"toGenericXml()");){
                DecoratedProgressMeter decoratedProgress = new DecoratedProgressMeter(progress);
                databinder.toGenericXML(writer, decoratedProgress);
            }
        }
        catch (IOException | RuntimeException e) {
            log.error("Couldn't backup database data.", (Throwable)e);
            throw new ImportExportException("Couldn't backup database data.", e);
        }
    }

    public void setSettingsManager(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    public void setUpgradeManager(UpgradeManager upgradeManager) {
        this.upgradeManager = upgradeManager;
    }

    protected SettingsManager getSettingsManager() {
        return this.settingsManager;
    }
}

