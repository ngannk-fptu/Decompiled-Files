/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Indexer
 *  com.atlassian.confluence.upgrade.PluginExportCompatibility
 *  com.atlassian.confluence.upgrade.VersionNumberComparator
 *  com.atlassian.dc.filestore.api.compat.FilesystemPath
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.spring.container.ContainerManager
 *  com.google.common.base.Preconditions
 *  javax.persistence.PersistenceException
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.io.input.ReaderInputStream
 *  org.apache.commons.lang3.StringUtils
 *  org.hibernate.HibernateException
 *  org.hibernate.Session
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Required
 */
package com.atlassian.confluence.importexport.xmlimport;

import com.atlassian.bonnie.Indexer;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.persistence.hibernate.TransientHibernateHandle;
import com.atlassian.confluence.impl.journal.JournalStateStore;
import com.atlassian.confluence.importexport.ConfigurationMigrationEvent;
import com.atlassian.confluence.importexport.DefaultImportContext;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.importexport.ImportProcessorSummary;
import com.atlassian.confluence.importexport.ImportedPluginDataPreProcessor;
import com.atlassian.confluence.importexport.impl.ExportDescriptor;
import com.atlassian.confluence.importexport.impl.UnexpectedImportZipFileContents;
import com.atlassian.confluence.importexport.plugin.BackupRestoreProvider;
import com.atlassian.confluence.importexport.plugin.BackupRestoreProviderManager;
import com.atlassian.confluence.importexport.xmlimport.BackupImporter;
import com.atlassian.confluence.importexport.xmlimport.SanitizedFilterReader;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.DefaultAttachmentManager;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDao;
import com.atlassian.confluence.pages.persistence.dao.FileSystemAttachmentDataDao;
import com.atlassian.confluence.pages.persistence.dao.SimpleFileLocationResolver;
import com.atlassian.confluence.pages.persistence.dao.hibernate.NonTransactionalHibernateAttachmentDao;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.setup.settings.DarkFeatures;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.upgrade.PluginExportCompatibility;
import com.atlassian.confluence.upgrade.VersionNumberComparator;
import com.atlassian.confluence.util.BandanaConfigMigrator;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.LayoutHelper;
import com.atlassian.confluence.util.velocity.ConfluenceVelocityResourceCache;
import com.atlassian.confluence.util.zip.Unzipper;
import com.atlassian.dc.filestore.api.compat.FilesystemPath;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.spring.container.ContainerManager;
import com.google.common.base.Preconditions;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.persistence.PersistenceException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.ReaderInputStream;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

@Deprecated
public final class FileBackupImporter
extends BackupImporter {
    private static final Logger log = LoggerFactory.getLogger(FileBackupImporter.class);
    private BandanaConfigMigrator bandanaConfigMigrator;
    private BootstrapManager bootstrapManager;
    private FilesystemPath confluenceHome;
    private File extractedDir;
    private Properties descriptorProperties;
    private BackupRestoreProviderManager backupRestoreProviderManager;
    private PluginAccessor pluginAccessor;
    private Indexer indexer;
    private final JournalStateStore journalStateStore;
    private final JournalStateStore bandanaJournalStateStore;
    private static final String DARK_FEATURE_DONT_FILTER_NONPRINTABLE_CHARS_ON_IMPORT = "confluence.import.dont.filter.unprintable.characters";
    private static final Set<Integer> CHARACTERS_TO_FILTER_OUT_ON_IMPORT = Collections.singleton(65535);

    public FileBackupImporter(JournalStateStore journalStateStore, JournalStateStore bandanaJournalStateStore) {
        this.journalStateStore = (JournalStateStore)Preconditions.checkNotNull((Object)journalStateStore);
        this.bandanaJournalStateStore = (JournalStateStore)Preconditions.checkNotNull((Object)bandanaJournalStateStore);
    }

    @Override
    protected void preImport() throws ImportExportException {
        this.extractedDir = FileBackupImporter.extractRestoreFile((DefaultImportContext)this.context);
        super.preImport();
    }

    @Override
    protected void postImportAndCleanUp() throws ImportExportException {
        super.postImportAndCleanUp();
        try {
            if (this.extractedDir != null) {
                log.info("Deleting " + this.extractedDir.getCanonicalPath());
                FileUtils.deleteDirectory((File)this.extractedDir);
            }
        }
        catch (Exception e) {
            log.error("Could not delete temp dir: " + this.extractedDir);
        }
    }

    private static File extractRestoreFile(DefaultImportContext importContext) throws ImportExportException {
        try {
            File extractedDir = GeneralUtil.createTempDirectoryInConfluenceTemp("import");
            Unzipper unzipper = null;
            if (importContext.getWorkingFile() != null) {
                String pathToRestoreFile = importContext.getWorkingFile();
                File backupFile = new File(pathToRestoreFile);
                log.info("Extracting backup file [ {} ] to [ {} ]", (Object)pathToRestoreFile, (Object)extractedDir);
                unzipper = GeneralUtil.getUnzipper(backupFile, extractedDir);
            } else if (importContext.getWorkingURL() != null) {
                URL restoreFileUrl = importContext.getWorkingURL();
                log.info("Extracting URL [ {} ] to [ {} ]", (Object)restoreFileUrl, (Object)extractedDir);
                unzipper = GeneralUtil.getUnzipper(restoreFileUrl, extractedDir);
            }
            if (unzipper != null) {
                FileBackupImporter.extractDescriptorAndUpdateContext(unzipper, importContext);
                unzipper.unzip();
            }
            return extractedDir;
        }
        catch (UnexpectedImportZipFileContents | Exception e) {
            log.error("Error extracting backup zip from file or url.", e);
            if (e instanceof IOException) {
                throw new ImportExportException("Error unzipping file: " + e.getMessage());
            }
            throw new ImportExportException("Error extracting backup zip from file or url.");
        }
    }

    private static void extractDescriptorAndUpdateContext(Unzipper unzipper, DefaultImportContext importContext) throws ImportExportException, UnexpectedImportZipFileContents, IOException {
        String spaceKey = importContext.getSpaceKeyOfSpaceImport();
        if (StringUtils.isNotEmpty((CharSequence)spaceKey)) {
            return;
        }
        ExportDescriptor descriptor = importContext.getExportDescriptor();
        if (descriptor == null) {
            descriptor = ExportDescriptor.getExportDescriptor(unzipper);
        }
        importContext.setSpaceKeyOfSpaceImport(descriptor.getSpaceKey());
    }

    @Override
    protected ImportProcessorSummary importEverything() throws ImportExportException {
        boolean importAttachments = this.isBackupAttachments();
        ImportProcessorSummary summary = super.importEverything(importAttachments);
        if (importAttachments) {
            try {
                log.info("Importing attachments");
                this.importAttachments(summary, this.extractedDir);
            }
            catch (PersistenceException e) {
                throw new ImportExportException("Error importing attachments: " + e.getMessage(), e);
            }
        }
        if (!this.isIncrementalImport()) {
            log.info("Importing templates");
            this.importTemplates();
            log.info("Importing config directory");
            this.importConfigDirectory();
            log.info("Imported resources directory");
            this.importResourcesDirectory();
            log.info("Resetting state for all journals");
            this.journalStateStore.resetAllJournalStates();
            this.bandanaJournalStateStore.resetAllJournalStates();
        }
        if (this.isExportScopeAll()) {
            log.info("Importing plugin data");
            this.importPluginData(summary);
        }
        return summary;
    }

    private Map<File, File> getIdBasedAttachmentPathMapping(ImportProcessorSummary xmlReader, File srcAttachmentsDir, File destAttachmentsDir) throws ImportExportException {
        HashMap<File, File> pathMapping = new HashMap<File, File>();
        Session dbSession = this.sessionFactory.getCurrentSession();
        Collection<TransientHibernateHandle> attachmentKeys = xmlReader.getImportedObjectHandlesOfType(Attachment.class);
        for (TransientHibernateHandle key : attachmentKeys) {
            Object obj;
            try {
                obj = key.get(dbSession);
            }
            catch (PersistenceException he) {
                throw new ImportExportException("error retrieving attachment object " + key.getId(), he);
            }
            if (!(obj instanceof Attachment)) continue;
            Attachment attachment = (Attachment)obj;
            ContentEntityObject content = attachment.getContainer();
            if (content == null) {
                log.info("Skipping attachment because it has no container: {}", obj);
                continue;
            }
            Long newContentId = content.getId();
            Long oldContentId = (Long)xmlReader.getUnfixedIdFor(content.getClass(), newContentId);
            Long newAttachmentId = attachment.getId();
            Long oldAttachmentId = (Long)xmlReader.getUnfixedIdFor(attachment.getClass(), newAttachmentId);
            File dest = new File(new File(destAttachmentsDir, newContentId.toString()), newAttachmentId.toString());
            File src = new File(new File(srcAttachmentsDir, oldContentId.toString()), oldAttachmentId.toString());
            pathMapping.put(src, dest);
        }
        return pathMapping;
    }

    private void copyPaths(Map<File, File> pathMapping) {
        for (Map.Entry<File, File> entry : pathMapping.entrySet()) {
            File src = entry.getKey();
            File dest = entry.getValue();
            if (log.isDebugEnabled()) {
                log.debug("Migrating attachments from: " + src + " to " + dest);
            }
            try {
                this.restoreDirectory(src, dest);
            }
            catch (Exception e) {
                log.error("There was a problem moving the attachments from the import to a temporary location.", (Throwable)e);
            }
        }
    }

    protected void importAttachments(ImportProcessorSummary context, File extractedDir) throws ImportExportException, HibernateException {
        File srcAttachmentsDir = new File(extractedDir, "attachments");
        File fixedAttachmentsDir = new File(extractedDir, "attachments-new");
        fixedAttachmentsDir.mkdirs();
        this.copyPaths(this.getIdBasedAttachmentPathMapping(context, srcAttachmentsDir, fixedAttachmentsDir));
        SimpleFileLocationResolver locationResolver = new SimpleFileLocationResolver(fixedAttachmentsDir);
        DefaultAttachmentManager sourceAttachmentManager = new DefaultAttachmentManager();
        ContainerManager.autowireComponent((Object)sourceAttachmentManager);
        NonTransactionalHibernateAttachmentDao filesystemAttachmentDao = new NonTransactionalHibernateAttachmentDao();
        filesystemAttachmentDao.setSessionFactory(this.sessionFactory);
        filesystemAttachmentDao.setEventPublisher(this.getEventPublisher());
        if (this.indexer != null) {
            filesystemAttachmentDao.setIndexer(this.indexer);
        }
        FileSystemAttachmentDataDao filesystemAttachmentDataDao = new FileSystemAttachmentDataDao();
        ContainerManager.autowireComponent((Object)filesystemAttachmentDataDao);
        filesystemAttachmentDataDao.setAttachmentsDirResolver(locationResolver);
        filesystemAttachmentDao.setDataDao(filesystemAttachmentDataDao);
        sourceAttachmentManager.setAttachmentDao(filesystemAttachmentDao);
        AttachmentManager destinationAttachmentManager = (AttachmentManager)ContainerManager.getComponent((String)"attachmentManager");
        AttachmentDao.AttachmentCopier copier = sourceAttachmentManager.getCopier(destinationAttachmentManager);
        if (!this.isExportScopeAll()) {
            copier.setSpacesToInclude(new ArrayList<Space>(context.getImportedObjectsOfType(Space.class)));
        }
        copier.copy();
    }

    private void importTemplates() throws ImportExportException {
        ConfluenceVelocityResourceCache cache;
        if (this.restoreDirectory(this.getTemplateBackupDirectory(this.extractedDir), new File(LayoutHelper.getFullTemplatePath())) && (cache = ConfluenceVelocityResourceCache.getInstance()) != null) {
            cache.clear();
        }
    }

    private File getTemplateBackupDirectory(File extractedDir) {
        File file = new File(extractedDir, "velocity");
        while (this.onlyContentsAreAnotherVelocityDirectory(file)) {
            file = new File(file, "velocity");
        }
        return file;
    }

    private boolean onlyContentsAreAnotherVelocityDirectory(File file) {
        String[] files = file.list();
        return files != null && files.length == 1 && files[0].equals("velocity");
    }

    private void importConfigDirectory() {
        File srcDir = new File(this.extractedDir, "config");
        this.bandanaConfigMigrator.run(srcDir);
        this.publishEvent(new ConfigurationMigrationEvent(this));
    }

    private void importResourcesDirectory() {
        File srcDir = new File(this.extractedDir, "resources");
        File destDir = new File(this.getConfluenceHome(), "resources");
        try {
            this.restoreDirectory(srcDir, destDir);
        }
        catch (ImportExportException e) {
            log.error("Error restoring resources directory [" + destDir + "] from backup [" + srcDir + "].");
        }
    }

    private File getConfluenceHome() {
        if (this.confluenceHome != null) {
            return this.confluenceHome.asJavaFile();
        }
        return new File(this.bootstrapManager.getConfluenceHome());
    }

    private void importPluginData(ImportProcessorSummary summary) throws ImportExportException {
        if (this.skipPluginData()) {
            return;
        }
        ImportedPluginDataPreProcessor pluginDataPreProcessor = this.context.getPluginDataPreProcessor();
        for (ModuleDescriptor moduleDescriptor : this.backupRestoreProviderManager.getModuleDescriptors()) {
            try {
                File extractedModuleFile = this.backupRestoreProviderManager.getModuleBackupFile(this.extractedDir, (ModuleDescriptor<BackupRestoreProvider>)moduleDescriptor);
                if (pluginDataPreProcessor != null && extractedModuleFile != null && extractedModuleFile.exists()) {
                    extractedModuleFile = pluginDataPreProcessor.process((ModuleDescriptor<BackupRestoreProvider>)moduleDescriptor, extractedModuleFile, summary);
                }
                if (extractedModuleFile != null && extractedModuleFile.exists()) {
                    BufferedInputStream is = new BufferedInputStream(new FileInputStream(extractedModuleFile));
                    try {
                        ((BackupRestoreProvider)moduleDescriptor.getModule()).restore(is);
                        continue;
                    }
                    finally {
                        ((InputStream)is).close();
                        continue;
                    }
                }
                log.info("No plugin data for : " + moduleDescriptor.getCompleteKey());
            }
            catch (IOException ex) {
                throw new ImportExportException("IOException while importing plugin data for : " + moduleDescriptor.getCompleteKey(), ex);
            }
        }
    }

    private boolean skipPluginData() throws ImportExportException {
        Map<String, PluginExportCompatibility> compatibilityMap = ExportDescriptor.getPluginExportCompatibility(this.getDescriptorProperties());
        if (compatibilityMap == null || compatibilityMap.isEmpty()) {
            return false;
        }
        boolean allowPluginDataImport = true;
        VersionNumberComparator comparator = new VersionNumberComparator();
        for (Map.Entry<String, PluginExportCompatibility> compatibility : compatibilityMap.entrySet()) {
            Plugin plugin = this.pluginAccessor.getPlugin(compatibility.getKey());
            if (plugin == null || plugin.getPluginInformation() == null) {
                log.info("Couldn't check ActiveObjects data is compatible with " + compatibility.getKey() + " because the plugin isn't installed");
                continue;
            }
            String version = plugin.getPluginInformation().getVersion();
            if (version == null) {
                log.info("Couldn't check ActiveObjects data is compatible with " + compatibility.getKey() + " because the version number is unavailable");
                continue;
            }
            String earliestVersion = compatibility.getValue().getEarliestVersion();
            String createdByVersion = compatibility.getValue().getCurrentVersion();
            boolean allow = comparator.compare(earliestVersion, version) <= 0;
            if (allow) continue;
            log.info("Plugin data import will be skipped because the plugin {} version {} is required, and you are using {}. The backup was created with version {}.", new Object[]{compatibility.getKey(), earliestVersion, version, createdByVersion});
            allowPluginDataImport = false;
        }
        return !allowPluginDataImport;
    }

    private boolean restoreDirectory(File srcDir, File destDir) throws ImportExportException {
        if (!srcDir.isDirectory()) {
            return false;
        }
        if (!destDir.isDirectory() && !destDir.mkdirs()) {
            throw new ImportExportException("Directory doesn't exist and can't be created within Confluence: " + destDir.getAbsolutePath());
        }
        try {
            FileUtils.copyDirectory((File)srcDir, (File)destDir, (boolean)true);
        }
        catch (IOException e) {
            String message = "Couldn't restore directory from backup! src: " + srcDir.getAbsolutePath() + " dest: " + destDir.getAbsolutePath();
            log.error(message);
            throw new ImportExportException(message, e);
        }
        return true;
    }

    @Override
    protected Properties getDescriptorProperties() throws ImportExportException {
        if (this.descriptorProperties == null) {
            this.descriptorProperties = this.loadDescriptorProperties();
        }
        return this.descriptorProperties;
    }

    private Properties loadDescriptorProperties() throws ImportExportException {
        Properties properties;
        File descriptorFile = new File(this.extractedDir, "exportDescriptor.properties");
        FileInputStream input = new FileInputStream(descriptorFile);
        try {
            Properties properties2 = new Properties();
            properties2.load(input);
            properties = properties2;
        }
        catch (Throwable throwable) {
            try {
                try {
                    ((InputStream)input).close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (IOException e) {
                throw new ImportExportException("Could not load export descriptor properties", e);
            }
        }
        ((InputStream)input).close();
        return properties;
    }

    @Override
    public InputStream getXmlEntitiesStream() throws ImportExportException {
        File xmlFile = new File(this.extractedDir, "entities.xml");
        if (!xmlFile.isFile()) {
            throw new ImportExportException("The zip doesn't contain an 'entities.xml' file in it.");
        }
        try {
            if (this.shouldSanitizeXmlImportForUtfSpecialCharacters()) {
                SanitizedFilterReader sanitizedReader = new SanitizedFilterReader(new InputStreamReader((InputStream)new FileInputStream(xmlFile), StandardCharsets.UTF_8), CHARACTERS_TO_FILTER_OUT_ON_IMPORT);
                return new ReaderInputStream((Reader)sanitizedReader, StandardCharsets.UTF_8);
            }
            return new FileInputStream(xmlFile);
        }
        catch (FileNotFoundException e) {
            throw new ImportExportException("Error finding and loading entities.xml into a stream.");
        }
    }

    private boolean shouldSanitizeXmlImportForUtfSpecialCharacters() {
        return "UTF-8".equals(this.settingsManager.getGlobalSettings().getDefaultEncoding()) && !DarkFeatures.isDarkFeatureEnabled(DARK_FEATURE_DONT_FILTER_NONPRINTABLE_CHARS_ON_IMPORT);
    }

    @Required
    public void setBandanaConfigMigrator(BandanaConfigMigrator bandanaConfigMigrator) {
        this.bandanaConfigMigrator = bandanaConfigMigrator;
    }

    @Required
    public void setBootstrapManager(BootstrapManager bootstrapManager) {
        this.bootstrapManager = bootstrapManager;
    }

    public void setConfluenceHome(FilesystemPath confluenceHome) {
        this.confluenceHome = confluenceHome;
    }

    @Required
    public void setBackupRestoreProviderManager(BackupRestoreProviderManager backupRestoreProviderManager) {
        this.backupRestoreProviderManager = backupRestoreProviderManager;
    }

    @Required
    public void setPluginAccessor(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    @Deprecated
    public void setIndexer(Indexer indexer) {
        this.indexer = indexer;
    }
}

