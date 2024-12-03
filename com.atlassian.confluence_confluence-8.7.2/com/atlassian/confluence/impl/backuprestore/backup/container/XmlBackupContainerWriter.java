/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.google.common.annotations.VisibleForTesting
 *  org.apache.commons.compress.parallel.InputStreamSupplier
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.backup.container;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.backup.container.ArchiveWriter;
import com.atlassian.confluence.impl.backuprestore.backup.container.BackupContainerWriter;
import com.atlassian.confluence.impl.backuprestore.backup.container.EntityObjectsToXmlWriter;
import com.atlassian.confluence.impl.backuprestore.backup.container.PluginDataWriter;
import com.atlassian.confluence.impl.backuprestore.backup.models.AttachmentInfo;
import com.atlassian.confluence.impl.backuprestore.backup.models.EntityObjectReadyForExport;
import com.atlassian.confluence.impl.pages.attachments.filesystem.AttachmentDataFileSystem;
import com.atlassian.confluence.impl.pages.attachments.filesystem.model.Refs;
import com.atlassian.confluence.importexport.plugin.BackupRestoreProvider;
import com.atlassian.confluence.internal.pages.persistence.AttachmentDaoInternal;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDataStream;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDataStreamType;
import com.atlassian.confluence.pages.persistence.dao.filesystem.AttachmentDataFileSystemException;
import com.atlassian.plugin.ModuleDescriptor;
import com.google.common.annotations.VisibleForTesting;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.compress.parallel.InputStreamSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XmlBackupContainerWriter
implements BackupContainerWriter {
    private static final Logger log = LoggerFactory.getLogger(XmlBackupContainerWriter.class);
    public static final String EXPORT_DESCRIPTOR_FILE_NAME = "exportDescriptor.properties";
    public static final String ENTITIES_FILE_NAME = "entities.xml";
    private final File outputFile;
    private final ArchiveWriter archiveWriter;
    private final AttachmentDataFileSystem attachmentDataFileSystem;
    private final PluginDataWriter pluginWriter;
    private AtomicBoolean isPropertiesInitialised = new AtomicBoolean(false);
    private final AttachmentDaoInternal attachmentDaoInternal;
    PipedOutputStream entitiesOutputStream = new PipedOutputStream();
    PipedOutputStream propertiesOutputStream = new PipedOutputStream();
    InputStream entitiesInputStream;
    InputStream propertiesInputStream;
    OutputStreamWriter entitiesOutputStreamWriter = new OutputStreamWriter((OutputStream)this.entitiesOutputStream, StandardCharsets.UTF_8);
    OutputStreamWriter propertiesOutputStreamWriter = new OutputStreamWriter((OutputStream)this.propertiesOutputStream, StandardCharsets.UTF_8);
    EntityObjectsToXmlWriter xmlConverter;

    public XmlBackupContainerWriter(File outputFile, ArchiveWriter archiveWriter, AttachmentDataFileSystem attachmentDataFileSystem, PluginDataWriter pluginWriter, AttachmentDaoInternal attachmentDaoInternal) throws BackupRestoreException {
        this(outputFile, archiveWriter, attachmentDataFileSystem, pluginWriter, attachmentDaoInternal, Instant.now());
    }

    @VisibleForTesting
    public XmlBackupContainerWriter(File outputFile, ArchiveWriter archiveWriter, AttachmentDataFileSystem attachmentDataFileSystem, PluginDataWriter pluginWriter, AttachmentDaoInternal attachmentDaoInternal, Instant currentTime) throws BackupRestoreException {
        this.outputFile = outputFile;
        this.archiveWriter = archiveWriter;
        this.attachmentDataFileSystem = attachmentDataFileSystem;
        this.pluginWriter = pluginWriter;
        this.attachmentDaoInternal = attachmentDaoInternal;
        try {
            this.xmlConverter = new EntityObjectsToXmlWriter(this.entitiesOutputStreamWriter, currentTime);
            this.entitiesInputStream = new PipedInputStream(this.entitiesOutputStream);
            archiveWriter.compressFromStream(this.entitiesInputStream, ENTITIES_FILE_NAME);
            this.propertiesInputStream = new PipedInputStream(this.propertiesOutputStream);
            this.propertiesOutputStreamWriter.write(String.format("#%s%n", currentTime.atZone(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("EEE MMM d HH:mm:ss z yyyy", Locale.US))));
        }
        catch (IOException e) {
            throw new BackupRestoreException(e);
        }
    }

    @Override
    public void writeObjects(Collection<EntityObjectReadyForExport> objects) throws BackupRestoreException {
        try {
            this.xmlConverter.serialise(objects);
        }
        catch (IOException e) {
            throw new BackupRestoreException(e);
        }
    }

    @Override
    public void addAttachments(Collection<AttachmentInfo> attachments) {
        for (AttachmentInfo attachmentInfo : attachments) {
            try {
                AttachmentDataStream attachmentDataStream;
                Long originalAttachmentId = attachmentInfo.getOriginalVersion() == null ? attachmentInfo.getId() : attachmentInfo.getOriginalVersion();
                Attachment attachment = this.attachmentDaoInternal.getById(attachmentInfo.getId());
                if (attachment == null) {
                    log.warn("Attachment with id {} not found in database.", (Object)attachmentInfo.getId());
                    continue;
                }
                try {
                    attachmentDataStream = this.attachmentDataFileSystem.getAttachmentData(Refs.ref(attachment), AttachmentDataStreamType.RAW_BINARY);
                }
                catch (AttachmentDataFileSystemException e) {
                    log.warn("Attachment file not found. {}", (Object)e.getMessage());
                    continue;
                }
                InputStreamSupplier streamSupplier = () -> {
                    try {
                        return attachmentDataStream.getInputStream();
                    }
                    catch (IOException e) {
                        throw new IllegalStateException(String.format("Error while accessing attachment. Msg: %s", e.getMessage()), e);
                    }
                };
                String destDir = XmlBackupContainerWriter.calculatePathInZip(attachmentInfo.getContainerId(), originalAttachmentId, attachmentInfo.getVersion());
                this.archiveWriter.compressFromStreamSupplier(streamSupplier, destDir, attachmentInfo.getId().toString());
            }
            catch (Exception e) {
                log.error("Unexpected exception while backup up an attachment {}. The file might be skipped from the backup.", (Object)attachmentInfo, (Object)e);
            }
        }
    }

    public static String calculatePathInZip(Long containerId, Long originalAttachmentId, Integer attachmentVersion) {
        return String.format("attachments/%d/%d/%d", containerId, originalAttachmentId, attachmentVersion);
    }

    @Override
    public void addDescriptionProperty(String name, String value) throws BackupRestoreException {
        try {
            this.lazyInitDescriptionPropertiesArchiveEntry();
            this.propertiesOutputStreamWriter.write(String.format("%s=%s%n", name, value));
        }
        catch (IOException e) {
            throw new BackupRestoreException(e);
        }
    }

    @Override
    public void addPluginModuleData(List<? extends ModuleDescriptor<BackupRestoreProvider>> moduleDescriptors) throws BackupRestoreException {
        this.pluginWriter.writePluginData(moduleDescriptors);
    }

    @Override
    public void close() throws BackupRestoreException {
        try {
            log.debug("Closing zip streams and creating final zip {}", (Object)this.outputFile.getName());
            this.xmlConverter.close();
            this.entitiesOutputStreamWriter.close();
            this.entitiesOutputStream.close();
            this.propertiesOutputStreamWriter.close();
            this.propertiesOutputStream.close();
            this.archiveWriter.close();
        }
        catch (IOException e) {
            throw new BackupRestoreException(e);
        }
    }

    @Override
    public File getOutputFile() {
        return this.outputFile;
    }

    private void lazyInitDescriptionPropertiesArchiveEntry() {
        if (this.isPropertiesInitialised.compareAndSet(false, true)) {
            this.archiveWriter.compressFromStream(this.propertiesInputStream, EXPORT_DESCRIPTOR_FILE_NAME);
        }
    }
}

