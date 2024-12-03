/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.spring.container.LazyComponentReference
 *  com.atlassian.util.concurrent.Supplier
 */
package com.atlassian.confluence.impl.backuprestore.restore.stash;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import com.atlassian.confluence.impl.backuprestore.hibernate.HibernateMetadataHelper;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import com.atlassian.spring.container.LazyComponentReference;
import com.atlassian.util.concurrent.Supplier;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class StashObjectsSerialiser {
    private final Supplier<HibernateMetadataHelper> hibernateMetadataHelper;

    public StashObjectsSerialiser() {
        this.hibernateMetadataHelper = new LazyComponentReference("hibernateMetadataHelper");
    }

    @VisibleForTesting
    public StashObjectsSerialiser(HibernateMetadataHelper hibernateMetadataHelper) {
        this.hibernateMetadataHelper = () -> hibernateMetadataHelper;
    }

    public byte[] serialise(ImportedObjectV2 objectV2) throws IOException {
        try (ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();){
            byte[] byArray;
            try (ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayStream);){
                outputStream.writeObject(objectV2);
                outputStream.flush();
                byArray = byteArrayStream.toByteArray();
            }
            return byArray;
        }
    }

    public ImportedObjectV2 deserialise(byte[] byteArray) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);){
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            ImportedObjectV2 importedObjectV2 = (ImportedObjectV2)objectInputStream.readObject();
            ExportableEntityInfo exportableEntityInfo = ((HibernateMetadataHelper)this.hibernateMetadataHelper.get()).getEntityInfoByClass(importedObjectV2.getEntityClass());
            ImportedObjectV2 importedObjectV22 = new ImportedObjectV2(exportableEntityInfo, importedObjectV2.getId(), importedObjectV2.getPropertyValueMap());
            return importedObjectV22;
        }
    }
}

