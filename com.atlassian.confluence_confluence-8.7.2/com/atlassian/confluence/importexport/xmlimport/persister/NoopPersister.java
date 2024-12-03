/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.importexport.xmlimport.persister;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.core.persistence.hibernate.TransientHibernateHandle;
import com.atlassian.confluence.importexport.xmlimport.ImportProcessorContext;
import com.atlassian.confluence.importexport.xmlimport.ObjectPersister;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedObject;
import java.util.Collections;
import java.util.List;

@Deprecated
@Internal
public class NoopPersister
implements ObjectPersister {
    @Override
    public List<TransientHibernateHandle> persist(ImportProcessorContext context, ImportedObject object) throws Exception {
        return Collections.emptyList();
    }
}

