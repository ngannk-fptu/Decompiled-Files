/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.xmlimport.persister;

import com.atlassian.confluence.core.persistence.hibernate.TransientHibernateHandle;
import com.atlassian.confluence.importexport.xmlimport.ImportProcessorContext;
import com.atlassian.confluence.importexport.xmlimport.ObjectPersister;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedObject;
import com.atlassian.confluence.security.persistence.dao.hibernate.AliasedKey;
import com.atlassian.confluence.security.persistence.dao.hibernate.KeyTransferBean;
import java.util.Collections;
import java.util.List;

@Deprecated
public class HibernateKeyPersister
implements ObjectPersister {
    @Override
    public List<TransientHibernateHandle> persist(ImportProcessorContext context, ImportedObject object) {
        KeyTransferBean transfer = new KeyTransferBean(object.getStringProperty("keyType"), object.getStringProperty("algorithm"), object.getStringProperty("encodedKey"));
        AliasedKey aliased = new AliasedKey();
        aliased.setAlias(object.getStringProperty("alias"));
        aliased.setKey(transfer.asKey());
        context.saveObject(aliased);
        return Collections.singletonList(TransientHibernateHandle.create(AliasedKey.class, Long.valueOf(aliased.getId())));
    }
}

