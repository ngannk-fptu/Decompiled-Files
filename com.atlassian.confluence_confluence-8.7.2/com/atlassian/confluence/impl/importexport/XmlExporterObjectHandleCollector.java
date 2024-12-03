/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.user.propertyset.BucketPropertySet
 *  bucket.user.propertyset.BucketPropertySetItem
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  org.hibernate.HibernateException
 *  org.hibernate.Session
 */
package com.atlassian.confluence.impl.importexport;

import bucket.user.propertyset.BucketPropertySet;
import bucket.user.propertyset.BucketPropertySetItem;
import com.atlassian.confluence.content.CustomContentManager;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.ConfluencePropertySetManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.persistence.hibernate.TransientHibernateHandle;
import com.atlassian.confluence.importexport.DefaultExportContext;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.importexport.impl.ContentUserKeyExtractor;
import com.atlassian.confluence.importexport.impl.HibernateObjectHandleTranslator;
import com.atlassian.confluence.internal.relations.dao.Content2ContentRelationEntity;
import com.atlassian.confluence.internal.relations.dao.User2ContentRelationEntity;
import com.atlassian.confluence.internal.relations.dao.hibernate.Content2ContentHibernateRelationDao;
import com.atlassian.confluence.internal.relations.dao.hibernate.User2ContentHibernateRelationDao;
import com.atlassian.confluence.like.LikeEntityDao;
import com.atlassian.confluence.mail.notification.Notification;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.setup.bandana.persistence.dao.ConfluenceBandanaRecordDao;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUserImpl;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.hibernate.HibernateException;
import org.hibernate.Session;

final class XmlExporterObjectHandleCollector {
    private final HibernateObjectHandleTranslator translator;
    private final Session session;
    private final DefaultExportContext exportContext;
    private final LikeEntityDao likeDao;
    private final NotificationManager notificationManager;
    private final PageManager pageManager;
    private final ConfluenceBandanaRecordDao confluenceBandanaRecordDao;
    private final CustomContentManager customContentManager;
    private final ConfluencePropertySetManager propertySetManager;
    private final ContentUserKeyExtractor contentUserKeyExtractor;
    private final User2ContentHibernateRelationDao user2ContentHibernateRelationDao;
    private final Content2ContentHibernateRelationDao content2ContentHibernateRelationDao;

    public XmlExporterObjectHandleCollector(HibernateObjectHandleTranslator translator, Session session, DefaultExportContext exportContext, LikeEntityDao likeDao, NotificationManager notificationManager, PageManager pageManager, ConfluenceBandanaRecordDao confluenceBandanaRecordDao, CustomContentManager customContentManager, ConfluencePropertySetManager propertySetManager, ContentUserKeyExtractor contentUserKeyExtractor, User2ContentHibernateRelationDao user2ContentHibernateRelationDao, Content2ContentHibernateRelationDao content2ContentHibernateRelationDao) {
        this.translator = translator;
        this.session = session;
        this.exportContext = exportContext;
        this.likeDao = likeDao;
        this.notificationManager = notificationManager;
        this.pageManager = pageManager;
        this.confluenceBandanaRecordDao = confluenceBandanaRecordDao;
        this.customContentManager = customContentManager;
        this.propertySetManager = propertySetManager;
        this.contentUserKeyExtractor = contentUserKeyExtractor;
        this.user2ContentHibernateRelationDao = user2ContentHibernateRelationDao;
        this.content2ContentHibernateRelationDao = content2ContentHibernateRelationDao;
    }

    public List<TransientHibernateHandle> getHandlesOfObjectsForExport() throws ImportExportException {
        List<ConfluenceEntityObject> workingEntities = this.exportContext.getWorkingEntities();
        if (workingEntities == null || workingEntities.isEmpty()) {
            throw new ImportExportException("Nothing to export!");
        }
        return this.getObjectHandles(workingEntities);
    }

    private List<TransientHibernateHandle> getObjectHandles(Iterable<ConfluenceEntityObject> workingEntities) {
        ArrayList<TransientHibernateHandle> objectHandles = new ArrayList<TransientHibernateHandle>();
        for (ConfluenceEntityObject confluenceEntityObject : workingEntities) {
            if (confluenceEntityObject instanceof Space) {
                objectHandles.addAll(this.addEntityHandlesFromSpace((Space)confluenceEntityObject));
                continue;
            }
            if (!(confluenceEntityObject instanceof ContentEntityObject)) continue;
            TransientHibernateHandle contentHandle = this.translator.objectToHandle(confluenceEntityObject);
            objectHandles.add(contentHandle);
            this.addPropertiesForContent(contentHandle);
        }
        return objectHandles;
    }

    private Collection<TransientHibernateHandle> addEntityHandlesFromSpace(Space space) {
        ArrayList<TransientHibernateHandle> objectHandles = new ArrayList<TransientHibernateHandle>();
        objectHandles.add(this.translator.objectToHandle(space));
        objectHandles.addAll(this.getContentAndContentPropertiesFromSpace(space));
        objectHandles.addAll(this.getBandanaRecords(space));
        Iterables.addAll(objectHandles, this.getNotificationsFromSpace(space));
        Iterables.addAll(objectHandles, this.getUserToContentRelationsFromSpace(space));
        Iterables.addAll(objectHandles, this.getContentToContentRelationsFromSpace(space));
        return objectHandles;
    }

    private List<TransientHibernateHandle> getContentAndContentPropertiesFromSpace(Space space) {
        List<ContentEntityObject> contentList = this.getContentFromSpace(space);
        Iterable<TransientHibernateHandle> likeHandles = this.getLikesFromContent(contentList);
        Iterable contentHandles = contentList.stream().map(item -> this.translator.objectToHandle(item)).collect(Collectors.toList());
        Iterable<TransientHibernateHandle> users = this.getUsersFromBodyContent(contentList);
        XmlExporterObjectHandleCollector.flushAndClearSession(this.session);
        return Lists.newArrayList((Iterable)Iterables.concat((Iterable)contentHandles, this.getContentPropertiesFromContent(contentHandles), likeHandles, users));
    }

    private Iterable<TransientHibernateHandle> getUsersFromBodyContent(List<ContentEntityObject> contentEntities) {
        Set<UserKey> userKeys = this.contentUserKeyExtractor.extractUserKeysFromContentEntities(contentEntities, true);
        return userKeys.stream().map(item -> this.translator.idToHandleTransformer(ConfluenceUserImpl.class).apply((UserKey)item)).collect(Collectors.toSet());
    }

    private Iterable<TransientHibernateHandle> getLikesFromContent(List<ContentEntityObject> contentList) {
        ArrayList<TransientHibernateHandle> likeHandles = new ArrayList<TransientHibernateHandle>();
        int BATCH_SIZE = 500;
        for (List contentSublist : Lists.partition(contentList, (int)500)) {
            likeHandles.addAll(this.likeDao.getLikeEntities(contentSublist).stream().map(item -> this.translator.objectToHandle(item)).collect(Collectors.toList()));
            for (ContentEntityObject obj : contentSublist) {
                likeHandles.addAll(this.likeDao.getLikeEntities(obj.getComments()).stream().map(item -> this.translator.objectToHandle(item)).collect(Collectors.toList()));
            }
        }
        return likeHandles;
    }

    private Collection<TransientHibernateHandle> getBandanaRecords(Space space) {
        return Collections2.transform((Collection)this.confluenceBandanaRecordDao.findForContext(space.getKey()), this.translator::objectToHandle);
    }

    private Iterable<TransientHibernateHandle> getNotificationsFromSpace(Space space) {
        return StreamSupport.stream(this.notificationManager.findPageAndSpaceNotificationIdsFromSpace(space).spliterator(), false).map(item -> this.translator.idToHandleTransformer(Notification.class).apply((Long)item)).collect(Collectors.toList());
    }

    private Iterable<TransientHibernateHandle> getUserToContentRelationsFromSpace(Space space) {
        List<ConfluenceEntityObject> excludedObjects = this.exportContext.getExceptionEntities();
        List<Long> relationIds = this.user2ContentHibernateRelationDao.getAllRelationIdsForContentInSpace(space.getKey(), excludedObjects, 0, Integer.MAX_VALUE);
        return relationIds.stream().map(item -> this.translator.idToHandleTransformer(User2ContentRelationEntity.class).apply((Long)item)).collect(Collectors.toList());
    }

    private Iterable<TransientHibernateHandle> getContentToContentRelationsFromSpace(Space space) {
        List<ConfluenceEntityObject> excludedObjects = this.exportContext.getExceptionEntities();
        List<Long> relationIds = this.content2ContentHibernateRelationDao.getAllRelationIdsForContentInSpace(space.getKey(), excludedObjects, 0, Integer.MAX_VALUE);
        return relationIds.stream().map(item -> this.translator.idToHandleTransformer(Content2ContentRelationEntity.class).apply((Long)item)).collect(Collectors.toList());
    }

    private List<ContentEntityObject> getContentFromSpace(Space space) {
        List<Page> pages = this.pageManager.getPages(space, false);
        List<BlogPost> blogPosts = this.pageManager.getBlogPosts(space, false);
        LinkedList<ContentEntityObject> contentList = new LinkedList<ContentEntityObject>();
        contentList.addAll(pages);
        contentList.addAll(blogPosts);
        if (!this.exportContext.isExportAll()) {
            this.excludeInvisiblePages(pages);
            this.excludeInvisibleBlogPosts(blogPosts);
        }
        contentList.addAll(this.customContentManager.findAllInSpace(space));
        return contentList;
    }

    private void excludeInvisibleBlogPosts(List<BlogPost> blogs) {
        if (this.exportContext.getContentTree() != null) {
            List<BlogPost> permittedBlogPosts = this.exportContext.getContentTree().getBlogPosts();
            for (BlogPost blog : blogs) {
                if (blog.isLatestVersion() && !permittedBlogPosts.contains(blog)) {
                    this.exportContext.addExceptionEntity(blog);
                }
                if (blog.isLatestVersion() || permittedBlogPosts.contains(blog.getLatestVersion())) continue;
                this.exportContext.addExceptionEntity(blog);
            }
        }
    }

    private void excludeInvisiblePages(List<Page> pages) {
        if (this.exportContext.getContentTree() != null) {
            List<Page> includedPages = this.exportContext.getContentTree().getPages();
            for (Page page : pages) {
                if (page.isLatestVersion() && !includedPages.contains(page)) {
                    this.exportContext.addExceptionEntity(page);
                }
                if (page.isLatestVersion() || includedPages.contains(page.getLatestVersion())) continue;
                this.exportContext.addExceptionEntity(page);
            }
        }
    }

    private List<TransientHibernateHandle> getContentPropertiesFromContent(Iterable<TransientHibernateHandle> contentHandles) {
        ArrayList<TransientHibernateHandle> result = new ArrayList<TransientHibernateHandle>();
        for (TransientHibernateHandle contentHandle : contentHandles) {
            result.addAll(this.addPropertiesForContent(contentHandle));
        }
        return result;
    }

    private List<TransientHibernateHandle> addPropertiesForContent(TransientHibernateHandle contentHandle) {
        ArrayList<TransientHibernateHandle> result = new ArrayList<TransientHibernateHandle>();
        BucketPropertySet propertySet = (BucketPropertySet)this.propertySetManager.getPropertySet(contentHandle);
        for (Object propertyKey : propertySet.getKeys()) {
            String stringKey = (String)propertyKey;
            BucketPropertySetItem propertySetItem = propertySet.getByKey(stringKey);
            result.add(this.translator.objectToHandle(propertySetItem));
        }
        return result;
    }

    private static void flushAndClearSession(Session session) {
        try {
            session.flush();
        }
        catch (HibernateException e) {
            throw new RuntimeException("Export failed due to session flush failure", e);
        }
        session.clear();
    }
}

