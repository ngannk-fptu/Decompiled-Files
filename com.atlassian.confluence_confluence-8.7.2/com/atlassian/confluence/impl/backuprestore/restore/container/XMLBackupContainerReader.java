/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.PropertyUtils
 *  com.atlassian.security.xml.SecureXmlParserFactory
 *  org.apache.commons.io.input.ReaderInputStream
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.restore.container;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.BackupRestoreConsumer;
import com.atlassian.confluence.impl.backuprestore.restore.container.BackupContainerReader;
import com.atlassian.confluence.impl.backuprestore.restore.container.BackupProperties;
import com.atlassian.confluence.impl.backuprestore.restore.container.PluginDataReader;
import com.atlassian.confluence.impl.backuprestore.restore.container.SanitizedXmlFilterReader;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import com.atlassian.confluence.impl.backuprestore.restore.preprocessing.ImportedObjectV1ToV2Converter;
import com.atlassian.confluence.importexport.xmlimport.parser.BackupParser;
import com.atlassian.core.util.PropertyUtils;
import com.atlassian.security.xml.SecureXmlParserFactory;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.io.input.ReaderInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class XMLBackupContainerReader
implements BackupContainerReader {
    private static final Logger log = LoggerFactory.getLogger(XMLBackupContainerReader.class);
    private final ZipFile zipFile;
    private InputStream entitiesXmlZipInputStream;
    private final ImportedObjectV1ToV2Converter importedObjectV1ToV2Converter;
    private final PluginDataReader pluginDataReader;

    public XMLBackupContainerReader(ZipFile zipFile, ImportedObjectV1ToV2Converter importedObjectV1ToV2Converter, PluginDataReader pluginDataReader) {
        this.importedObjectV1ToV2Converter = importedObjectV1ToV2Converter;
        this.zipFile = zipFile;
        this.pluginDataReader = pluginDataReader;
    }

    @Override
    public BackupProperties getBackupProperties() throws BackupRestoreException {
        BackupProperties backupProperties;
        block9: {
            ZipEntry zipEntry = this.zipFile.getEntry("exportDescriptor.properties");
            if (zipEntry == null) {
                throw new FileNotFoundException("exportDescriptor.properties file was not found in the backup file");
            }
            InputStream inputStream = this.zipFile.getInputStream(zipEntry);
            try {
                backupProperties = new BackupProperties(PropertyUtils.getPropertiesFromStream((InputStream)inputStream));
                if (inputStream == null) break block9;
            }
            catch (Throwable throwable) {
                try {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException e) {
                    throw new BackupRestoreException(e);
                }
            }
            inputStream.close();
        }
        return backupProperties;
    }

    @Override
    @Deprecated
    public Properties getLegacyBackupProperties() throws BackupRestoreException {
        Properties properties;
        block9: {
            ZipEntry zipEntry = this.zipFile.getEntry("exportDescriptor.properties");
            if (zipEntry == null) {
                throw new FileNotFoundException("exportDescriptor.properties file was not found in the backup file");
            }
            InputStream inputStream = this.zipFile.getInputStream(zipEntry);
            try {
                properties = PropertyUtils.getPropertiesFromStream((InputStream)inputStream);
                if (inputStream == null) break block9;
            }
            catch (Throwable throwable) {
                try {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException e) {
                    throw new BackupRestoreException(e);
                }
            }
            inputStream.close();
        }
        return properties;
    }

    @Override
    public void readObjects(BackupRestoreConsumer<ImportedObjectV2> objectConsumer) throws BackupRestoreException {
        try {
            XMLReader xmlReader = SecureXmlParserFactory.newXmlReader();
            xmlReader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            xmlReader.setContentHandler(new BackupParser(obj -> {
                Optional<ImportedObjectV2> importedObjectV2 = this.importedObjectV1ToV2Converter.apply(obj);
                if (importedObjectV2.isPresent()) {
                    objectConsumer.accept(importedObjectV2.get());
                } else {
                    log.debug("This input object has been skipped because it had been rejected by the converter. Object: {}", (Object)obj);
                }
            }));
            SanitizedXmlFilterReader sanitizedReader = new SanitizedXmlFilterReader(new InputStreamReader(this.getEntitiesXmlInputStream(), StandardCharsets.UTF_8));
            ReaderInputStream sanitizedInputStream = new ReaderInputStream((Reader)sanitizedReader, StandardCharsets.UTF_8);
            InputSource inputSource = new InputSource((InputStream)sanitizedInputStream);
            inputSource.setEncoding(StandardCharsets.UTF_8.name());
            inputSource.setCharacterStream(sanitizedReader);
            xmlReader.parse(inputSource);
        }
        catch (IOException | SAXException e) {
            throw new BackupRestoreException(e);
        }
    }

    @Override
    public void readPluginModuleData() throws BackupRestoreException {
        this.pluginDataReader.readPluginData(this.getBackupProperties());
    }

    private InputStream getEntitiesXmlInputStream() throws IOException {
        ZipEntry zipEntry = this.zipFile.getEntry("entities.xml");
        if (zipEntry == null) {
            throw new FileNotFoundException("Entities XML file was not found in backup archive");
        }
        this.entitiesXmlZipInputStream = this.zipFile.getInputStream(zipEntry);
        return this.entitiesXmlZipInputStream;
    }

    @Override
    public void close() throws BackupRestoreException {
        try {
            this.closeIfNotNull(this.zipFile);
            this.closeIfNotNull(this.entitiesXmlZipInputStream);
        }
        catch (IOException e) {
            throw new BackupRestoreException(e);
        }
    }

    private void closeIfNotNull(Closeable resource) throws IOException {
        if (resource != null) {
            resource.close();
        }
    }
}

