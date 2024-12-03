/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.archive.internal;

import java.io.IOException;
import java.net.URL;
import java.util.jar.JarInputStream;
import org.hibernate.boot.archive.spi.AbstractArchiveDescriptor;
import org.hibernate.boot.archive.spi.ArchiveContext;
import org.hibernate.boot.archive.spi.ArchiveDescriptorFactory;
import org.hibernate.boot.archive.spi.ArchiveEntry;
import org.hibernate.boot.archive.spi.ArchiveException;
import org.hibernate.boot.archive.spi.InputStreamAccess;
import org.hibernate.internal.log.UrlMessageBundle;

public class JarInputStreamBasedArchiveDescriptor
extends AbstractArchiveDescriptor {
    public JarInputStreamBasedArchiveDescriptor(ArchiveDescriptorFactory archiveDescriptorFactory, URL url, String entry) {
        super(archiveDescriptorFactory, url, entry);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public void visitArchive(ArchiveContext context) {
        try {
            jarInputStream = new JarInputStream(this.getArchiveUrl().openStream());
        }
        catch (Exception e) {
            UrlMessageBundle.URL_LOGGER.logUnableToFindFileByUrl(this.getArchiveUrl(), e);
            return;
        }
        try {
            while (true) lbl-1000:
            // 5 sources

            {
                if ((jarEntry = jarInputStream.getNextJarEntry()) == null) {
                    jarInputStream.close();
                    return;
                }
                jarEntryName = jarEntry.getName();
                if (this.getEntryBasePrefix() != null && !jarEntryName.startsWith(this.getEntryBasePrefix()) || jarEntry.isDirectory()) continue;
                if (jarEntryName.equals(this.getEntryBasePrefix())) {
                    try {
                        subJarInputStream = new JarInputStream(jarInputStream);
                        try {
                            subZipEntry = jarInputStream.getNextEntry();
                            while (true) {
                                if (subZipEntry == null) ** GOTO lbl-1000
                                if (!subZipEntry.isDirectory()) {
                                    subName = this.extractName(subZipEntry);
                                    inputStreamAccess = this.buildByteBasedInputStreamAccess(subName, subJarInputStream);
                                    entry = new ArchiveEntry(){

                                        @Override
                                        public String getName() {
                                            return subName;
                                        }

                                        @Override
                                        public String getNameWithinArchive() {
                                            return subName;
                                        }

                                        @Override
                                        public InputStreamAccess getStreamAccess() {
                                            return inputStreamAccess;
                                        }
                                    };
                                    context.obtainArchiveEntryHandler(entry).handleEntry(entry, context);
                                }
                                subZipEntry = jarInputStream.getNextJarEntry();
                            }
                        }
                        finally {
                            subJarInputStream.close();
                        }
                    }
                    catch (Exception e) {
                        throw new ArchiveException("Error accessing nested jar", e);
                    }
                }
                entryName = this.extractName(jarEntry);
                inputStreamAccess = this.buildByteBasedInputStreamAccess(entryName, jarInputStream);
                relativeName = this.extractRelativeName(jarEntry);
                entry = new ArchiveEntry(){

                    @Override
                    public String getName() {
                        return entryName;
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
        catch (IOException ioe) {
            throw new ArchiveException(String.format("Error accessing JarInputStream [%s]", new Object[]{this.getArchiveUrl()}), ioe);
        }
    }
}

