/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.HibernateException
 */
package com.atlassian.confluence.importexport;

import com.atlassian.confluence.core.persistence.hibernate.TransientHibernateHandle;
import com.atlassian.confluence.importexport.ImportExportException;
import java.util.Collection;
import java.util.Set;
import org.hibernate.HibernateException;

@Deprecated
public interface ImportProcessorSummary {
    public Collection<TransientHibernateHandle> getImportedObjectHandlesOfType(Class var1);

    public <T> Collection<T> getImportedObjectsOfType(Class<T> var1) throws HibernateException, ImportExportException;

    public Object getUnfixedIdFor(Class var1, Object var2);

    public Set<TransientHibernateHandle> getPersistedMappedHandles();

    public Set<TransientHibernateHandle> getPersistedUnmappedHandles();

    public Object getIdMappingFor(TransientHibernateHandle var1);
}

