/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.xmlimport.persister;

import com.atlassian.confluence.importexport.impl.StorageFormatUserRewriter;
import com.atlassian.confluence.importexport.xmlimport.ObjectPersisterFactory;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedObject;
import com.atlassian.confluence.importexport.xmlimport.persister.ReflectiveObjectPersister;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.user.persistence.dao.ConfluenceUserDao;

@Deprecated
public class ReflectiveObjectPersisterFactory
implements ObjectPersisterFactory {
    private final LabelManager labelManager;
    private final ConfluenceUserDao confluenceUserDao;
    private final StorageFormatUserRewriter storageFormatUserRewriter;

    public ReflectiveObjectPersisterFactory(LabelManager labelManager, ConfluenceUserDao confluenceUserDao, StorageFormatUserRewriter storageFormatUserRewriter) {
        this.labelManager = labelManager;
        this.confluenceUserDao = confluenceUserDao;
        this.storageFormatUserRewriter = storageFormatUserRewriter;
    }

    @Override
    public ReflectiveObjectPersister createPersisterFor(ImportedObject importedObject) {
        return new ReflectiveObjectPersister(this.labelManager, this.confluenceUserDao, this.storageFormatUserRewriter);
    }
}

