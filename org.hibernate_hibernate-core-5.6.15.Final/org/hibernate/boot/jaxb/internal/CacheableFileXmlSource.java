/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.jaxb.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Serializable;
import org.hibernate.boot.MappingException;
import org.hibernate.boot.jaxb.Origin;
import org.hibernate.boot.jaxb.SourceType;
import org.hibernate.boot.jaxb.internal.FileXmlSource;
import org.hibernate.boot.jaxb.spi.Binder;
import org.hibernate.boot.jaxb.spi.Binding;
import org.hibernate.boot.jaxb.spi.XmlSource;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.SerializationHelper;
import org.hibernate.type.SerializationException;

public class CacheableFileXmlSource
extends XmlSource {
    private static final CoreMessageLogger log = CoreLogging.messageLogger(CacheableFileXmlSource.class);
    private final File xmlFile;
    private final File serFile;
    private final boolean strict;

    public CacheableFileXmlSource(Origin origin, File xmlFile, boolean strict) {
        super(origin);
        this.xmlFile = xmlFile;
        this.strict = strict;
        this.serFile = CacheableFileXmlSource.determineCachedFile(xmlFile);
        if (strict) {
            if (!this.serFile.exists()) {
                throw new MappingException(String.format("Cached file [%s] could not be found", origin.getName()), origin);
            }
            if (this.isSerfileObsolete()) {
                throw new MappingException(String.format("Cached file [%s] could not be used as the mapping file is newer", origin.getName()), origin);
            }
        }
    }

    public static File determineCachedFile(File xmlFile) {
        return new File(xmlFile.getAbsolutePath() + ".bin");
    }

    @Override
    public Binding doBind(Binder binder) {
        block8: {
            if (this.strict) {
                try {
                    return new Binding(this.readSerFile(), this.getOrigin());
                }
                catch (SerializationException e) {
                    throw new MappingException(String.format("Unable to deserialize from cached file [%s]", this.getOrigin().getName()), (Throwable)((Object)e), this.getOrigin());
                }
                catch (FileNotFoundException e) {
                    throw new MappingException(String.format("Unable to locate cached file [%s]", this.getOrigin().getName()), e, this.getOrigin());
                }
            }
            if (!this.isSerfileObsolete()) {
                try {
                    return (Binding)this.readSerFile();
                }
                catch (SerializationException e) {
                    log.unableToDeserializeCache(this.serFile.getName(), e);
                    break block8;
                }
                catch (FileNotFoundException e) {
                    log.cachedFileNotFound(this.serFile.getName(), e);
                    break block8;
                }
            }
            log.cachedFileObsolete(this.serFile);
        }
        log.readingMappingsFromFile(this.xmlFile.getPath());
        Binding binding = FileXmlSource.doBind(binder, this.xmlFile, this.getOrigin());
        this.writeSerFile(binding);
        return binding;
    }

    private <T> T readSerFile() throws SerializationException, FileNotFoundException {
        log.readingCachedMappings(this.serFile);
        return SerializationHelper.deserialize(new FileInputStream(this.serFile));
    }

    private void writeSerFile(Object binding) {
        CacheableFileXmlSource.writeSerFile((Serializable)binding, this.xmlFile, this.serFile);
    }

    private static void writeSerFile(Serializable binding, File xmlFile, File serFile) {
        try (FileOutputStream fos = new FileOutputStream(serFile);){
            if (log.isDebugEnabled()) {
                log.debugf("Writing cache file for: %s to: %s", xmlFile.getAbsolutePath(), serFile.getAbsolutePath());
            }
            SerializationHelper.serialize(binding, fos);
            boolean success = serFile.setLastModified(System.currentTimeMillis());
            if (!success) {
                log.warn("Could not update cacheable hbm.xml bin file timestamp");
            }
        }
        catch (Exception e) {
            log.unableToWriteCachedFile(serFile.getAbsolutePath(), e.getMessage());
        }
    }

    public static void createSerFile(File xmlFile, Binder binder) {
        Origin origin = new Origin(SourceType.FILE, xmlFile.getAbsolutePath());
        CacheableFileXmlSource.writeSerFile(FileXmlSource.doBind(binder, xmlFile, origin), xmlFile, CacheableFileXmlSource.determineCachedFile(xmlFile));
    }

    private boolean isSerfileObsolete() {
        return this.xmlFile.exists() && this.serFile.exists() && this.xmlFile.lastModified() > this.serFile.lastModified();
    }
}

