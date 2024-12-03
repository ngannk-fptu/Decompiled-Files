/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.confluence.labels.LabelManager
 *  com.atlassian.confluence.labels.LabelPermissionEnforcer
 *  com.atlassian.confluence.labels.Labelable
 *  com.atlassian.confluence.labels.persistence.dao.LabelSearchResult
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.rpc.NotPermittedException
 *  com.atlassian.confluence.rpc.RemoteException
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.user.User
 *  com.google.common.base.Throwables
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.xmlrpc.bloggingrpc;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.labels.LabelPermissionEnforcer;
import com.atlassian.confluence.labels.Labelable;
import com.atlassian.confluence.labels.persistence.dao.LabelSearchResult;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.xmlrpc.bloggingrpc.BloggingUtils;
import com.atlassian.confluence.plugins.xmlrpc.bloggingrpc.MetaWeblog;
import com.atlassian.confluence.plugins.xmlrpc.bloggingrpc.NotImplementedException;
import com.atlassian.confluence.rpc.NotPermittedException;
import com.atlassian.confluence.rpc.RemoteException;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.user.User;
import com.google.common.base.Throwables;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetaWeblogImpl
implements MetaWeblog {
    private static final Logger log = LoggerFactory.getLogger(MetaWeblogImpl.class);
    private final BloggingUtils bloggingUtils;
    private final TransactionTemplate transactionTemplate;
    private final SpaceManager spaceManager;
    private final SpacePermissionManager spacePermissionManager;
    private final PageManager pageManager;
    private final PermissionManager permissionManager;
    private final LabelManager labelManager;
    private final LabelPermissionEnforcer labelPermissionEnforcer;
    private final ApplicationProperties applicationProperties;
    public static final String TITLE = "title";
    public static final String LINK = "link";
    public static final String PERMALINK = "permaLink";
    public static final String DESCRIPTION = "description";
    public static final String CATEGORIES = "categories";
    public static final String AUTHOR = "author";
    public static final String PUBDATE = "pubDate";
    public static final String DATECREATED = "dateCreated";
    public static final String HTMLURL = "htmlUrl";
    public static final String RSSURL = "rssUrl";
    public static final String POSTID = "postid";
    public static final String BLOGID = "blogid";

    public MetaWeblogImpl(BloggingUtils bloggingUtils, TransactionTemplate transactionTemplate, SpaceManager spaceManager, SpacePermissionManager spacePermissionManager, PageManager pageManager, PermissionManager permissionManager, LabelManager labelManager, LabelPermissionEnforcer labelPermissionEnforcer, ApplicationProperties applicationProperties) {
        this.bloggingUtils = bloggingUtils;
        this.transactionTemplate = transactionTemplate;
        this.spaceManager = spaceManager;
        this.spacePermissionManager = spacePermissionManager;
        this.pageManager = pageManager;
        this.permissionManager = permissionManager;
        this.labelManager = labelManager;
        this.labelPermissionEnforcer = labelPermissionEnforcer;
        this.applicationProperties = applicationProperties;
    }

    @Override
    public String newPost(String blogid, String username, String password, Hashtable<String, Object> struct, boolean publish) throws RemoteException {
        if (!publish) {
            throw new NotImplementedException(this.bloggingUtils.getText("error.draft.notimplemented"));
        }
        Object result = this.transactionTemplate.execute(() -> {
            try {
                BlogPost blogPost;
                ConfluenceUser user = this.bloggingUtils.authenticateUser(username, password);
                Space space = this.spaceManager.getSpace(blogid);
                if (space == null) {
                    throw new RemoteException(this.bloggingUtils.getText("error.validation.space.unknown", new String[]{blogid}));
                }
                if (!this.spacePermissionManager.hasPermission("EDITBLOG", space, (User)user)) {
                    throw new NotPermittedException(this.bloggingUtils.getText("error.permission.edit.space.blog", new String[]{space.getKey()}));
                }
                String title = (String)struct.get(TITLE);
                if (StringUtils.isBlank((CharSequence)title)) {
                    throw new RemoteException(this.bloggingUtils.getText("error.validation.blog.title.blank"));
                }
                String contents = StringUtils.defaultString((String)((String)struct.get(DESCRIPTION)));
                Calendar publishDate = Calendar.getInstance();
                Date dateCreated = (Date)struct.get(DATECREATED);
                if (dateCreated != null) {
                    publishDate.setTime(dateCreated);
                }
                if ((blogPost = this.pageManager.getBlogPost(space.getKey(), title, publishDate)) != null) {
                    throw new RemoteException(this.bloggingUtils.getText("error.validation.blog.duplicate", new String[]{blogPost.getTitle(), publishDate.toString()}));
                }
                blogPost = new BlogPost();
                blogPost.setCreationDate(new Date(publishDate.getTimeInMillis()));
                blogPost.setSpace(space);
                blogPost.setTitle(title);
                blogPost.setBodyAsString(contents);
                blogPost.setCreator(user);
                this.pageManager.saveContentEntity((ContentEntityObject)blogPost, null);
                Vector categories = (Vector)struct.get(CATEGORIES);
                if (categories != null) {
                    for (String category : categories) {
                        Label label = new Label(category);
                        this.labelManager.addLabel((Labelable)blogPost, label);
                    }
                }
                String string = blogPost.getIdAsString();
                return string;
            }
            catch (RemoteException re) {
                RemoteException remoteException = re;
                return remoteException;
            }
            finally {
                AuthenticatedUserThreadLocal.reset();
            }
        });
        return (String)this.checkRemoteException(result);
    }

    @Override
    public boolean editPost(String postid, String username, String password, Hashtable<String, Object> struct, boolean publish) throws RemoteException {
        if (!publish) {
            throw new NotImplementedException(this.bloggingUtils.getText("error.draft.notimplemented"));
        }
        Object result = this.transactionTemplate.execute(() -> {
            try {
                BlogPost originalPost;
                ConfluenceUser user = this.bloggingUtils.authenticateUser(username, password);
                BlogPost blogPost = this.pageManager.getBlogPost(Long.parseLong(postid));
                if (blogPost == null) {
                    throw new RemoteException(this.bloggingUtils.getText("error.validation.blog.doesnotexists", new String[]{postid}));
                }
                try {
                    originalPost = (BlogPost)blogPost.clone();
                }
                catch (Exception e) {
                    throw Throwables.propagate((Throwable)e);
                }
                Space space = blogPost.getSpace();
                if (!this.spacePermissionManager.hasPermission("EDITBLOG", space, (User)user)) {
                    throw new NotPermittedException(this.bloggingUtils.getText("error.permission.edit.space.blog", new String[]{space.getKey()}));
                }
                Date dateCreated = null == struct.get(DATECREATED) ? blogPost.getCreationDate() : (Date)struct.get(DATECREATED);
                String title = (String)StringUtils.defaultIfBlank((CharSequence)((String)struct.get(TITLE)), (CharSequence)blogPost.getTitle());
                String content = StringUtils.defaultString((String)((String)struct.get(DESCRIPTION)), (String)blogPost.getBodyAsString());
                if (!(blogPost.getCreationDate().equals(struct.get(DATECREATED)) && StringUtils.equals((CharSequence)title, (CharSequence)blogPost.getTitle()) && StringUtils.equals((CharSequence)content, (CharSequence)blogPost.getBodyAsString()))) {
                    if (!StringUtils.equals((CharSequence)title, (CharSequence)blogPost.getTitle())) {
                        Calendar publishDate = Calendar.getInstance();
                        publishDate.setTime(dateCreated);
                        BlogPost testBlogPost = this.pageManager.getBlogPost(space.getKey(), title, publishDate);
                        if (testBlogPost != null) {
                            throw new RemoteException(this.bloggingUtils.getText("error.validation.blog.duplicate", new String[]{title, publishDate.toString()}));
                        }
                    }
                    blogPost.setCreationDate(dateCreated);
                    blogPost.setTitle(title);
                    blogPost.setBodyAsString(content);
                    this.pageManager.saveContentEntity((ContentEntityObject)blogPost, (ContentEntityObject)originalPost, null);
                }
                this.labelManager.removeAllLabels((Labelable)blogPost);
                Vector categories = (Vector)struct.get(CATEGORIES);
                if (categories != null) {
                    categories.stream().filter(category -> StringUtils.isNotBlank((CharSequence)category)).forEach(category -> {
                        Label label = new Label(StringUtils.trim((String)category));
                        this.labelManager.addLabel((Labelable)blogPost, label);
                    });
                }
                Boolean bl = true;
                return bl;
            }
            catch (RemoteException re) {
                RemoteException remoteException = re;
                return remoteException;
            }
            finally {
                AuthenticatedUserThreadLocal.set(null);
            }
        });
        return (Boolean)this.checkRemoteException(result);
    }

    @Override
    public Vector<Hashtable<String, Object>> getRecentPosts(String blogid, String username, String password, int numberOfPosts) throws RemoteException {
        Object result = this.transactionTemplate.execute(() -> {
            try {
                ConfluenceUser user = this.bloggingUtils.authenticateUser(username, password);
                Space space = this.spaceManager.getSpace(blogid);
                if (space == null) {
                    throw new RemoteException(this.bloggingUtils.getText("error.validation.space.unknown", new String[]{blogid}));
                }
                if (!this.spacePermissionManager.hasPermission("VIEWSPACE", space, (User)user)) {
                    throw new NotPermittedException(this.bloggingUtils.getText("error.permission.view.space.blog", new String[]{blogid}));
                }
                List recentPosts = this.pageManager.getRecentlyAddedBlogPosts(numberOfPosts, space.getKey());
                Vector vector = recentPosts.stream().map(this::getBlogPostAsHashtable).collect(Collectors.toCollection(Vector::new));
                return vector;
            }
            catch (RemoteException re) {
                RemoteException remoteException = re;
                return remoteException;
            }
            finally {
                AuthenticatedUserThreadLocal.set(null);
            }
        });
        return (Vector)this.checkRemoteException(result);
    }

    @Override
    public Hashtable<String, Object> getPost(String postid, String username, String password) throws RemoteException {
        Object result = this.transactionTemplate.execute(() -> {
            try {
                BlogPost blogPost;
                ConfluenceUser user = this.bloggingUtils.authenticateUser(username, password);
                try {
                    blogPost = this.pageManager.getBlogPost(Long.parseLong(postid));
                    if (blogPost == null) {
                        throw new RemoteException(this.bloggingUtils.getText("error.validation.blog.doesnotexists", new String[]{postid}));
                    }
                }
                catch (NumberFormatException nfe) {
                    throw new RemoteException(this.bloggingUtils.getText("error.validation.blog.invalidpostid"));
                }
                if (!this.permissionManager.hasPermission((User)user, Permission.VIEW, (Object)blogPost)) {
                    throw new NotPermittedException(this.bloggingUtils.getText("error.permission.view.blog", new String[]{postid}));
                }
                Hashtable<String, Object> hashtable = this.getBlogPostAsHashtable(blogPost);
                return hashtable;
            }
            catch (RemoteException re) {
                RemoteException remoteException = re;
                return remoteException;
            }
            finally {
                AuthenticatedUserThreadLocal.set(null);
            }
        });
        return (Hashtable)this.checkRemoteException(result);
    }

    protected Hashtable<String, Object> getBlogPostAsHashtable(BlogPost blogPost) {
        Hashtable<String, Object> blogStruct = new Hashtable<String, Object>();
        blogStruct.put(POSTID, blogPost.getIdAsString());
        blogStruct.put(BLOGID, blogPost.getSpace().getKey());
        blogStruct.put(TITLE, StringUtils.defaultString((String)blogPost.getTitle(), (String)""));
        blogStruct.put(DESCRIPTION, this.bloggingUtils.convertStorageFormatToView(blogPost));
        blogStruct.put(PUBDATE, blogPost.getCreationDate());
        blogStruct.put(DATECREATED, blogPost.getCreationDate());
        String author = blogPost.getCreator() == null ? null : blogPost.getCreator().getName();
        blogStruct.put(AUTHOR, StringUtils.defaultString((String)author));
        String link = this.applicationProperties.getBaseUrl(UrlMode.CANONICAL) + "/" + blogPost.getUrlPath();
        blogStruct.put(LINK, link);
        blogStruct.put(PERMALINK, link);
        List visibleLabels = this.labelPermissionEnforcer.filterVisibleLabels(blogPost.getLabels(), (User)AuthenticatedUserThreadLocal.get(), true);
        Vector categories = visibleLabels.stream().map(Label::toString).collect(Collectors.toCollection(Vector::new));
        blogStruct.put(CATEGORIES, categories);
        return blogStruct;
    }

    @Override
    public Hashtable<String, Hashtable<String, String>> getCategories(String blogid, String username, String password) throws RemoteException {
        Object result = this.transactionTemplate.execute(() -> {
            try {
                ConfluenceUser user = this.bloggingUtils.authenticateUser(username, password);
                Space space = this.spaceManager.getSpace(blogid);
                if (space == null) {
                    throw new RemoteException(this.bloggingUtils.getText("error.validation.space.unknown", new String[]{blogid}));
                }
                if (!this.spacePermissionManager.hasPermission("VIEWSPACE", space, (User)user)) {
                    throw new NotPermittedException(this.bloggingUtils.getText("error.permission.view.space.blog", new String[]{blogid}));
                }
                List labelsInSpace = this.labelManager.getLabelsInSpace(space.getKey());
                List popularLabelsSearchResult = this.labelManager.getMostPopularLabels(20);
                HashSet labels = new HashSet();
                labels.addAll(labelsInSpace);
                labels.addAll(popularLabelsSearchResult.stream().map(LabelSearchResult::getLabel).collect(Collectors.toList()));
                List visibleLabels = this.labelPermissionEnforcer.filterVisibleLabels(new ArrayList(labels), (User)AuthenticatedUserThreadLocal.get(), true);
                Hashtable returnStruct = new Hashtable();
                String baseUrl = this.applicationProperties.getBaseUrl(UrlMode.CANONICAL);
                for (Label visibleLabel : visibleLabels) {
                    Hashtable<String, String> struct = new Hashtable<String, String>();
                    struct.put(DESCRIPTION, visibleLabel.toString());
                    struct.put(HTMLURL, baseUrl + visibleLabel.getUrlPath());
                    struct.put(RSSURL, baseUrl + "/createrssfeed.action?types=page&types=blogpost&types=mail&types=comment&" + "types=attachment&statuses=created&statuses=modified&showContent=true&showDiff=true" + ("&spaces=" + space.getKey()) + ("&labelString=" + visibleLabel.toString()) + "&rssType=atom&maxResults=20&timeSpan=30&publicFeed=false&" + "title=Confluence+Label+RSS+Feed&os_authType=basic");
                    returnStruct.put(visibleLabel.toString(), struct);
                }
                Hashtable hashtable = returnStruct;
                return hashtable;
            }
            catch (RemoteException re) {
                RemoteException remoteException = re;
                return remoteException;
            }
            finally {
                AuthenticatedUserThreadLocal.set(null);
            }
        });
        return (Hashtable)this.checkRemoteException(result);
    }

    private Object checkRemoteException(Object result) throws RemoteException {
        if (result instanceof RemoteException) {
            throw (RemoteException)((Object)result);
        }
        if (result instanceof Exception) {
            throw new RemoteException((Throwable)((Exception)result));
        }
        return result;
    }

    @Override
    public Hashtable<String, Object> newMediaObject(String blogid, String username, String password, Hashtable struct) throws RemoteException {
        throw new NotImplementedException(this.bloggingUtils.getText("error.newmediaobject.notimplemented"));
    }
}

