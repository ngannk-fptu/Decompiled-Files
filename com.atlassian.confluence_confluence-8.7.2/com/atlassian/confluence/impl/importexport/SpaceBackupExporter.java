/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ProgressMeter
 *  com.google.common.collect.Lists
 *  org.hibernate.Session
 */
package com.atlassian.confluence.impl.importexport;

import com.atlassian.confluence.content.CustomContentManager;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.ConfluencePropertySetManager;
import com.atlassian.confluence.core.persistence.hibernate.TransientHibernateHandle;
import com.atlassian.confluence.impl.importexport.AbstractFileXmlExporter;
import com.atlassian.confluence.impl.importexport.XmlExporterObjectHandleCollector;
import com.atlassian.confluence.importexport.ExportContext;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.importexport.impl.ContentUserKeyExtractor;
import com.atlassian.confluence.importexport.impl.ExportDescriptor;
import com.atlassian.confluence.importexport.impl.HibernateObjectHandleTranslator;
import com.atlassian.confluence.importexport.impl.StorageFormatUserRewriter;
import com.atlassian.confluence.internal.relations.dao.hibernate.Content2ContentHibernateRelationDao;
import com.atlassian.confluence.internal.relations.dao.hibernate.User2ContentHibernateRelationDao;
import com.atlassian.confluence.like.LikeEntityDao;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.setup.bandana.persistence.dao.ConfluenceBandanaRecordDao;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.core.util.ProgressMeter;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.hibernate.Session;

public class SpaceBackupExporter
extends AbstractFileXmlExporter {
    private ConfluenceBandanaRecordDao confluenceBandanaRecordDao;
    private NotificationManager notificationManager;
    private ConfluencePropertySetManager propertySetManager;
    private PageManager pageManager;
    private CustomContentManager customContentManager;
    private Space includedSpace;
    private LikeEntityDao likeDao;
    private StorageFormatUserRewriter storageFormatUserRewriter;
    private User2ContentHibernateRelationDao user2ContentHibernateRelationDao;
    private Content2ContentHibernateRelationDao content2ContentHibernateRelationDao;

    @Override
    public void setContext(ExportContext context) {
        super.setContext(context);
        this.includedSpace = SpaceBackupExporter.getIncludedSpaceFromWorkingEntity(this.getMainWorkingEntity());
    }

    @Override
    public String doExport(ProgressMeter progress) throws ImportExportException {
        this.checkHaveSomethingToExport();
        return super.doExport(progress);
    }

    @Override
    protected final List<Space> getIncludedSpaces() {
        return Collections.singletonList(this.includedSpace);
    }

    private static Space getIncludedSpaceFromWorkingEntity(ConfluenceEntityObject mainWorkingEntity) {
        if (mainWorkingEntity instanceof Space) {
            return (Space)mainWorkingEntity;
        }
        if (mainWorkingEntity instanceof AbstractPage) {
            return ((AbstractPage)mainWorkingEntity).getSpace();
        }
        throw new IllegalStateException("This exporter can only export from exactly one space");
    }

    @Override
    protected final void setCustomProperties(ExportDescriptor exportDescriptor) {
        exportDescriptor.setSpaceKey(this.includedSpace.getKey());
    }

    @Override
    protected List getSourceTemplateDirForCopying() {
        return new ArrayList();
    }

    private ConfluenceEntityObject getMainWorkingEntity() {
        return this.getWorkingExportContext().getWorkingEntities().get(0);
    }

    @Override
    protected List<TransientHibernateHandle> getHandlesOfObjectsForExport(HibernateObjectHandleTranslator translator, Session session) throws ImportExportException {
        XmlExporterObjectHandleCollector collector = new XmlExporterObjectHandleCollector(translator, session, this.getWorkingExportContext(), this.likeDao, this.notificationManager, this.pageManager, this.confluenceBandanaRecordDao, this.customContentManager, this.propertySetManager, new ContentUserKeyExtractor(this.storageFormatUserRewriter), this.user2ContentHibernateRelationDao, this.content2ContentHibernateRelationDao);
        return collector.getHandlesOfObjectsForExport();
    }

    @Override
    protected List<ConfluenceEntityObject> getObjectsExcludedFromExport() throws ImportExportException {
        Space space;
        List<ConfluenceEntityObject> exceptionEntities = super.getObjectsExcludedFromExport();
        ArrayList excludedObjects = Lists.newArrayList(exceptionEntities);
        ConfluenceEntityObject mainWorkingEntity = this.getMainWorkingEntity();
        if (mainWorkingEntity instanceof Page) {
            Page page = (Page)mainWorkingEntity;
            excludedObjects.add(page.getSpace());
        } else if (mainWorkingEntity instanceof Space && (space = (Space)mainWorkingEntity).getSpaceGroup() != null) {
            excludedObjects.add(space.getSpaceGroup());
        }
        return excludedObjects;
    }

    public void setConfluenceBandanaRecordDao(ConfluenceBandanaRecordDao dao) {
        this.confluenceBandanaRecordDao = dao;
    }

    public void setNotificationManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    public void setCustomContentManager(CustomContentManager customContentManager) {
        this.customContentManager = customContentManager;
    }

    public void setPropertySetManager(ConfluencePropertySetManager propertySetManager) {
        this.propertySetManager = propertySetManager;
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    public void setLikeEntityDao(LikeEntityDao likeEntityDao) {
        this.likeDao = likeEntityDao;
    }

    public void setStorageFormatUserRewriter(StorageFormatUserRewriter storageFormatUserRewriter) {
        this.storageFormatUserRewriter = storageFormatUserRewriter;
    }

    public void setUser2ContentHibernateRelationDao(User2ContentHibernateRelationDao user2ContentHibernateRelationDao) {
        this.user2ContentHibernateRelationDao = user2ContentHibernateRelationDao;
    }

    public void setContent2ContentHibernateRelationDao(Content2ContentHibernateRelationDao content2ContentHibernateRelationDao) {
        this.content2ContentHibernateRelationDao = content2ContentHibernateRelationDao;
    }
}

