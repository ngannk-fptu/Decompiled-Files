/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.archive.internal;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import org.hibernate.boot.archive.internal.FileInputStreamAccess;
import org.hibernate.boot.archive.spi.AbstractArchiveDescriptor;
import org.hibernate.boot.archive.spi.ArchiveContext;
import org.hibernate.boot.archive.spi.ArchiveDescriptorFactory;
import org.hibernate.boot.archive.spi.ArchiveEntry;
import org.hibernate.boot.archive.spi.ArchiveException;
import org.hibernate.boot.archive.spi.InputStreamAccess;
import org.hibernate.internal.log.UrlMessageBundle;

public class ExplodedArchiveDescriptor
extends AbstractArchiveDescriptor {
    public ExplodedArchiveDescriptor(ArchiveDescriptorFactory archiveDescriptorFactory, URL archiveUrl, String entryBasePrefix) {
        super(archiveDescriptorFactory, archiveUrl, entryBasePrefix);
    }

    @Override
    public void visitArchive(ArchiveContext context) {
        File rootDirectory = this.resolveRootDirectory();
        if (rootDirectory == null) {
            return;
        }
        if (rootDirectory.isDirectory()) {
            this.processDirectory(rootDirectory, null, context);
        } else {
            this.processZippedRoot(rootDirectory, context);
        }
    }

    private File resolveRootDirectory() {
        File archiveUrlDirectory;
        try {
            String filePart = this.getArchiveUrl().getFile();
            archiveUrlDirectory = filePart != null && filePart.indexOf(32) != -1 ? new File(filePart) : new File(this.getArchiveUrl().toURI().getSchemeSpecificPart());
        }
        catch (URISyntaxException e) {
            UrlMessageBundle.URL_LOGGER.logMalformedUrl(this.getArchiveUrl(), e);
            return null;
        }
        if (!archiveUrlDirectory.exists()) {
            UrlMessageBundle.URL_LOGGER.logFileDoesNotExist(this.getArchiveUrl());
            return null;
        }
        if (!archiveUrlDirectory.isDirectory()) {
            UrlMessageBundle.URL_LOGGER.logFileIsNotDirectory(this.getArchiveUrl());
            return null;
        }
        String entryBase = this.getEntryBasePrefix();
        if (entryBase != null && entryBase.length() > 0 && !"/".equals(entryBase)) {
            return new File(archiveUrlDirectory, entryBase);
        }
        return archiveUrlDirectory;
    }

    private void processDirectory(File directory, String path, ArchiveContext context) {
        if (directory == null) {
            return;
        }
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        path = path == null ? "" : path + "/";
        for (File localFile : files) {
            if (!localFile.exists()) continue;
            if (localFile.isDirectory()) {
                this.processDirectory(localFile, path + localFile.getName(), context);
                continue;
            }
            final String name = localFile.getAbsolutePath();
            final String relativeName = path + localFile.getName();
            final FileInputStreamAccess inputStreamAccess = new FileInputStreamAccess(name, localFile);
            ArchiveEntry entry = new ArchiveEntry(){

                @Override
                public String getName() {
                    return name;
                }

                @Override
                public String getNameWithinArchive() {
                    return relativeName;
                }

                @Override
                public InputStreamAccess getStreamAccess() {
                    return inputStreamAccess;
                }
            };
            context.obtainArchiveEntryHandler(entry).handleEntry(entry, context);
        }
    }

    private void processZippedRoot(File rootFile, ArchiveContext context) {
        try (JarFile jarFile = new JarFile(rootFile);){
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                InputStreamAccess inputStreamAccess;
                ZipEntry zipEntry = entries.nextElement();
                if (zipEntry.isDirectory()) continue;
                final String name = this.extractName(zipEntry);
                final String relativeName = this.extractRelativeName(zipEntry);
                try {
                    inputStreamAccess = this.buildByteBasedInputStreamAccess(name, jarFile.getInputStream(zipEntry));
                }
                catch (IOException e) {
                    throw new ArchiveException(String.format("Unable to access stream from jar file [%s] for entry [%s]", jarFile.getName(), zipEntry.getName()));
                }
                ArchiveEntry entry = new ArchiveEntry(){

                    @Override
                    public String getName() {
                        return name;
                    }

                    @Override
                    public String getNameWithinArchive() {
                        return relativeName;
                    }

                    @Override
                    public InputStreamAccess getStreamAccess() {
                        return inputStreamAccess;
                    }
                };
                context.obtainArchiveEntryHandler(entry).handleEntry(entry, context);
            }
        }
        catch (IOException e) {
            throw new ArchiveException("Error accessing jar file [" + rootFile.getAbsolutePath() + "]", e);
        }
    }
}

