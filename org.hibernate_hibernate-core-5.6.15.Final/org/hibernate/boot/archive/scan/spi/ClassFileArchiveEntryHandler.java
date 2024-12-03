/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Converter
 *  javax.persistence.Embeddable
 *  javax.persistence.Entity
 *  javax.persistence.MappedSuperclass
 *  org.jboss.jandex.ClassInfo
 *  org.jboss.jandex.DotName
 *  org.jboss.jandex.Index
 *  org.jboss.jandex.Indexer
 */
package org.hibernate.boot.archive.scan.spi;

import java.io.IOException;
import java.io.InputStream;
import javax.persistence.Converter;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import org.hibernate.boot.archive.scan.internal.ClassDescriptorImpl;
import org.hibernate.boot.archive.scan.internal.ScanResultCollector;
import org.hibernate.boot.archive.scan.spi.ClassDescriptor;
import org.hibernate.boot.archive.spi.ArchiveContext;
import org.hibernate.boot.archive.spi.ArchiveEntry;
import org.hibernate.boot.archive.spi.ArchiveEntryHandler;
import org.hibernate.boot.archive.spi.ArchiveException;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.Index;
import org.jboss.jandex.Indexer;

public class ClassFileArchiveEntryHandler
implements ArchiveEntryHandler {
    private static final DotName CONVERTER = DotName.createSimple((String)Converter.class.getName());
    private static final DotName[] MODELS = new DotName[]{DotName.createSimple((String)Entity.class.getName()), DotName.createSimple((String)MappedSuperclass.class.getName()), DotName.createSimple((String)Embeddable.class.getName())};
    private final ScanResultCollector resultCollector;

    public ClassFileArchiveEntryHandler(ScanResultCollector resultCollector) {
        this.resultCollector = resultCollector;
    }

    @Override
    public void handleEntry(ArchiveEntry entry, ArchiveContext context) {
        ClassDescriptor classDescriptor = this.toClassDescriptor(entry);
        if (classDescriptor.getCategorization() == ClassDescriptor.Categorization.OTHER) {
            return;
        }
        this.resultCollector.handleClass(classDescriptor, context.isRootUrl());
    }

    private ClassDescriptor toClassDescriptor(ArchiveEntry entry) {
        ClassDescriptor classDescriptor;
        block8: {
            InputStream inputStream = entry.getStreamAccess().accessInputStream();
            try {
                Indexer indexer = new Indexer();
                indexer.index(inputStream);
                Index index = indexer.complete();
                ClassInfo classInfo = (ClassInfo)index.getKnownClasses().iterator().next();
                classDescriptor = this.toClassDescriptor(classInfo, index, entry);
                if (inputStream == null) break block8;
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
                    throw new ArchiveException("Could not build ClassInfo", e);
                }
            }
            inputStream.close();
        }
        return classDescriptor;
    }

    private ClassDescriptor toClassDescriptor(ClassInfo classInfo, Index index, ArchiveEntry entry) {
        ClassDescriptor.Categorization categorization = ClassDescriptor.Categorization.OTHER;
        if (this.isModel(index)) {
            categorization = ClassDescriptor.Categorization.MODEL;
        } else if (this.isConverter(index)) {
            categorization = ClassDescriptor.Categorization.CONVERTER;
        }
        return new ClassDescriptorImpl(classInfo.name().toString(), categorization, entry.getStreamAccess());
    }

    private boolean isConverter(Index index) {
        return !index.getAnnotations(CONVERTER).isEmpty();
    }

    private boolean isModel(Index index) {
        for (DotName model : MODELS) {
            if (index.getAnnotations(model).isEmpty()) continue;
            return true;
        }
        return false;
    }
}

