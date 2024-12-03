/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.xmlimport;

import com.atlassian.confluence.core.persistence.hibernate.TransientHibernateHandle;
import com.atlassian.confluence.importexport.xmlimport.ImportProcessorContext;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedObject;
import java.util.List;

@Deprecated
public interface ObjectPersister {
    public List<TransientHibernateHandle> persist(ImportProcessorContext var1, ImportedObject var2) throws Exception;
}

