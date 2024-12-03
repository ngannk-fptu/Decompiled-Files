/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ProgressMeter
 *  org.hibernate.Session
 *  org.springframework.orm.hibernate5.SessionFactoryUtils
 *  org.springframework.orm.hibernate5.SessionHolder
 *  org.springframework.transaction.support.TransactionSynchronizationManager
 */
package com.atlassian.confluence.impl.importexport;

import com.atlassian.confluence.core.NotExportable;
import com.atlassian.confluence.core.persistence.hibernate.ExporterAnyTypeDao;
import com.atlassian.confluence.core.persistence.hibernate.TransientHibernateHandle;
import com.atlassian.confluence.impl.importexport.AbstractFileXmlExporter;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.importexport.impl.HibernateObjectHandleTranslator;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.util.LayoutHelper;
import com.atlassian.core.util.ProgressMeter;
import java.io.File;
import java.util.Collections;
import java.util.List;
import org.hibernate.Session;
import org.springframework.orm.hibernate5.SessionFactoryUtils;
import org.springframework.orm.hibernate5.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class SiteBackupExporter
extends AbstractFileXmlExporter {
    private ExporterAnyTypeDao anyTypeDao;

    @Override
    protected List<TransientHibernateHandle> getHandlesOfObjectsForExport(HibernateObjectHandleTranslator translator, Session session) throws ImportExportException {
        return this.anyTypeDao.findAllPersistentObjectsHibernateHandles(Collections.singletonList(NotExportable.class));
    }

    @Override
    protected List getSourceTemplateDirForCopying() {
        return Collections.singletonList(new File(LayoutHelper.getFullTemplatePath()));
    }

    public void setAnyTypeDao(ExporterAnyTypeDao anyTypeDao) {
        this.anyTypeDao = anyTypeDao;
    }

    @Override
    protected List<Space> getIncludedSpaces() {
        return Collections.emptyList();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected String doExportInternal(ProgressMeter progress) throws ImportExportException {
        Session newSession = null;
        try {
            newSession = this.sessionFactory5.openSession();
            TransactionSynchronizationManager.bindResource((Object)this.sessionFactory5, (Object)new SessionHolder(newSession));
            String string = super.doExportInternal(progress);
            return string;
        }
        finally {
            if (TransactionSynchronizationManager.hasResource((Object)this.sessionFactory5)) {
                TransactionSynchronizationManager.unbindResource((Object)this.sessionFactory5);
            }
            SessionFactoryUtils.closeSession((Session)newSession);
        }
    }
}

