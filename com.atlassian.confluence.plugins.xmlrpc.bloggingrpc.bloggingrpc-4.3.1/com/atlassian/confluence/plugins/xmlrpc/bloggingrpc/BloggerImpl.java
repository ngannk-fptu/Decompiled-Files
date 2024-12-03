/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.rpc.AuthenticationFailedException
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
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.xmlrpc.bloggingrpc;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.xmlrpc.bloggingrpc.Blogger;
import com.atlassian.confluence.plugins.xmlrpc.bloggingrpc.BloggingUtils;
import com.atlassian.confluence.plugins.xmlrpc.bloggingrpc.NotImplementedException;
import com.atlassian.confluence.rpc.AuthenticationFailedException;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BloggerImpl
implements Blogger {
    private static Logger log = LoggerFactory.getLogger(BloggerImpl.class);
    private final TransactionTemplate transactionTemplate;
    private final BloggingUtils bloggingUtils;
    private final SpaceManager spaceManager;
    private final SpacePermissionManager spacePermissionManager;
    private final PageManager pageManager;
    private final PermissionManager permissionManager;
    private final ApplicationProperties applicationProperties;
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String POSTID = "postid";
    public static final String BLOGID = "blogid";
    public static final String BLOG_NAME = "blogName";
    public static final String URL = "url";
    public static final String DATE_CREATED = "dateCreated";
    public static final String AUTHOR_NAME = "authorName";
    public static final String AUTHOR_EMAIL = "authorEmail";
    public static final String FIRST_NAME = "firstname";
    public static final String EMAIL = "email";
    public static final String USER_ID = "userid";

    public BloggerImpl(BloggingUtils bloggingUtils, TransactionTemplate transactionTemplate, SpaceManager spaceManager, SpacePermissionManager spacePermissionManager, PageManager pageManager, PermissionManager permissionManager, ApplicationProperties applicationProperties) {
        this.bloggingUtils = bloggingUtils;
        this.transactionTemplate = transactionTemplate;
        this.spaceManager = spaceManager;
        this.spacePermissionManager = spacePermissionManager;
        this.pageManager = pageManager;
        this.permissionManager = permissionManager;
        this.applicationProperties = applicationProperties;
    }

    @Override
    public Vector<Hashtable<String, String>> getUsersBlogs(String appKey, String username, String password) throws RemoteException {
        Object result = this.transactionTemplate.execute(() -> {
            try {
                Vector blogs = new Vector();
                for (Space space : this.bloggingUtils.getBlogs((User)this.bloggingUtils.authenticateUser(username, password))) {
                    Hashtable<String, String> blog = new Hashtable<String, String>();
                    blog.put(BLOG_NAME, space.getName());
                    blog.put(URL, this.applicationProperties.getBaseUrl(UrlMode.CANONICAL) + "/pages/viewrecentblogposts.action?key=" + space.getKey());
                    blog.put(BLOGID, space.getKey());
                    blogs.add(blog);
                }
                Vector vector = blogs;
                return vector;
            }
            catch (AuthenticationFailedException auth) {
                AuthenticationFailedException authenticationFailedException = auth;
                return authenticationFailedException;
            }
            finally {
                AuthenticatedUserThreadLocal.set(null);
            }
        });
        return (Vector)this.checkRemoteException(result);
    }

    @Override
    public String newPost(String appKey, String blogid, String username, String password, String content, boolean publish) throws RemoteException {
        if (!publish) {
            throw new NotImplementedException(this.bloggingUtils.getText("error.draft.notimplemented"));
        }
        Object result = this.transactionTemplate.execute(() -> {
            try {
                ConfluenceUser user = this.bloggingUtils.authenticateUser(username, password);
                Space space = this.spaceManager.getSpace(blogid);
                if (space == null) {
                    throw new RemoteException(this.bloggingUtils.getText("error.validation.space.unknown", new String[]{blogid}));
                }
                if (!this.spacePermissionManager.hasPermission("EDITBLOG", space, (User)user)) {
                    throw new NotPermittedException(this.bloggingUtils.getText("error.permission.edit.space.blog", new String[]{space.getKey()}));
                }
                Map<String, String> blogMap = this.splitBlogContent(content);
                String title = blogMap.get(TITLE);
                if (StringUtils.isBlank((CharSequence)title)) {
                    throw new RemoteException(this.bloggingUtils.getText("error.validation.blog.title.blank"));
                }
                String cleanContent = blogMap.get(CONTENT);
                Calendar publishDate = Calendar.getInstance();
                BlogPost blogPost = this.pageManager.getBlogPost(space.getKey(), title, publishDate);
                if (blogPost != null) {
                    throw new RemoteException(this.bloggingUtils.getText("error.validation.blog.duplicate", new String[]{title, publishDate.toString()}));
                }
                blogPost = new BlogPost();
                blogPost.setCreationDate(new Date(publishDate.getTimeInMillis()));
                blogPost.setSpace(space);
                blogPost.setBodyAsString(cleanContent);
                blogPost.setTitle(title);
                blogPost.setCreator(user);
                this.pageManager.saveContentEntity((ContentEntityObject)blogPost, null);
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
    public boolean editPost(String appKey, String postid, String username, String password, String content, boolean publish) throws RemoteException {
        if (!publish) {
            throw new NotImplementedException(this.bloggingUtils.getText("error.draft.notimplemented"));
        }
        Object result = this.transactionTemplate.execute(() -> {
            try {
                BlogPost originalPost;
                ConfluenceUser user = this.bloggingUtils.authenticateUser(username, password);
                BlogPost blogPost = this.pageManager.getBlogPost(new Long(postid).longValue());
                if (blogPost == null) {
                    throw new RemoteException(this.bloggingUtils.getText("error.validation.blog.doesnotexists", new String[]{postid}));
                }
                try {
                    originalPost = (BlogPost)blogPost.clone();
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
                Space space = blogPost.getSpace();
                if (!this.spacePermissionManager.hasPermission("EDITBLOG", space, (User)user)) {
                    throw new NotPermittedException(this.bloggingUtils.getText("error.permission.edit.space.blog", new String[]{space.getKey()}));
                }
                Map<String, String> blogMap = this.splitBlogContent(content);
                String title = blogMap.get(TITLE);
                if (StringUtils.isBlank((CharSequence)title)) {
                    throw new RemoteException(this.bloggingUtils.getText("error.validation.blog.title.blank"));
                }
                if (!StringUtils.equals((CharSequence)title, (CharSequence)blogPost.getTitle())) {
                    Calendar publishedDate = Calendar.getInstance();
                    publishedDate.setTime(blogPost.getCreationDate());
                    if (this.pageManager.getBlogPost(space.getKey(), title, publishedDate) != null) {
                        throw new RemoteException(this.bloggingUtils.getText("error.validation.blog.duplicate", new Object[]{title, publishedDate}));
                    }
                }
                blogPost.setTitle(blogMap.get(TITLE));
                blogPost.setBodyAsString(blogMap.get(CONTENT));
                this.pageManager.saveContentEntity((ContentEntityObject)blogPost, (ContentEntityObject)originalPost, null);
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
    public Vector<Hashtable<String, Object>> getRecentPosts(String appkey, String blogid, String username, String password, int numposts) throws RemoteException {
        Object result = this.transactionTemplate.execute(() -> {
            RemoteException returnObject;
            try {
                ConfluenceUser user = this.bloggingUtils.authenticateUser(username, password);
                Space space = this.spaceManager.getSpace(blogid);
                if (space == null) {
                    throw new RemoteException(this.bloggingUtils.getText("error.validation.space.unknown", new String[]{blogid}));
                }
                if (!this.spacePermissionManager.hasPermission("VIEWSPACE", space, (User)user)) {
                    throw new NotPermittedException(this.bloggingUtils.getText("error.permission.view.space.blog", new String[]{space.getKey()}));
                }
                List recentPosts = this.pageManager.getRecentlyAddedBlogPosts(numposts, space.getKey());
                Vector recentPostsVector = new Vector();
                log.debug(String.format("Found %d blogs in space: %s", recentPosts.size(), space.getKey()));
                recentPostsVector.addAll(recentPosts.stream().map(arg_0 -> this.lambda$getRecentPosts$3(space, (User)user, arg_0)).collect(Collectors.toList()));
                Vector vector = recentPostsVector;
                return vector;
            }
            catch (RemoteException re) {
                returnObject = re;
            }
            finally {
                AuthenticatedUserThreadLocal.set(null);
            }
            return returnObject;
        });
        return (Vector)this.checkRemoteException(result);
    }

    private Hashtable<String, Object> getBlogPostAsHashtable(Space space, BlogPost blogPost, User forUser) {
        String title = StringUtils.defaultString((String)blogPost.getTitle());
        Hashtable<String, Object> blogStruct = new Hashtable<String, Object>();
        blogStruct.put(POSTID, blogPost.getIdAsString());
        blogStruct.put(BLOGID, space.getKey());
        blogStruct.put(TITLE, title);
        blogStruct.put(URL, this.applicationProperties.getBaseUrl(UrlMode.CANONICAL) + blogPost.getUrlPath());
        blogStruct.put(CONTENT, "<title>" + title + "</title>" + this.bloggingUtils.convertStorageFormatToView(blogPost));
        blogStruct.put(DATE_CREATED, blogPost.getCreationDate());
        blogStruct.put(AUTHOR_NAME, blogPost.getCreator().getName());
        blogStruct.put(AUTHOR_EMAIL, StringUtils.defaultString((String)forUser.getEmail()));
        return blogStruct;
    }

    @Override
    public boolean deletePost(String appkey, String postid, String username, String password, boolean publish) throws RemoteException {
        if (!publish) {
            throw new NotImplementedException(this.bloggingUtils.getText("error.draft.notimplemented"));
        }
        Object result = this.transactionTemplate.execute(() -> {
            try {
                ConfluenceUser user = this.bloggingUtils.authenticateUser(username, password);
                BlogPost blogPost = this.pageManager.getBlogPost(new Long(postid).longValue());
                if (blogPost == null) {
                    throw new RemoteException(this.bloggingUtils.getText("error.validation.blog.doesnotexists", new String[]{postid}));
                }
                Space space = blogPost.getSpace();
                if (this.spacePermissionManager.hasPermission("REMOVEBLOG", space, (User)user)) {
                    blogPost.trash();
                    Boolean bl = true;
                    return bl;
                }
                try {
                    throw new NotPermittedException(this.bloggingUtils.getText("error.permission.remove.space.blog", new String[]{space.getKey()}));
                }
                catch (RemoteException re) {
                    RemoteException remoteException = re;
                    return remoteException;
                }
            }
            finally {
                AuthenticatedUserThreadLocal.set(null);
            }
        });
        return (Boolean)this.checkRemoteException(result);
    }

    @Override
    public Hashtable<String, Object> getPost(String appkey, String postid, String username, String password) throws RemoteException {
        Object result = this.transactionTemplate.execute(() -> {
            try {
                BlogPost blogPost;
                ConfluenceUser user = this.bloggingUtils.authenticateUser(username, password);
                try {
                    long postIdLong = Long.parseLong(postid);
                    blogPost = this.pageManager.getBlogPost(postIdLong);
                    if (null == blogPost) {
                        throw new RemoteException(this.bloggingUtils.getText("error.validation.blog.doesnotexists", new String[]{postid}));
                    }
                }
                catch (NumberFormatException nan) {
                    throw new RemoteException(this.bloggingUtils.getText("error.validation.blog.invalidpostid"));
                }
                if (!this.permissionManager.hasPermission((User)user, Permission.VIEW, (Object)blogPost)) {
                    throw new RemoteException(this.bloggingUtils.getText("error.permission.view.blog", new String[]{postid}));
                }
                Hashtable<String, Object> hashtable = this.getBlogPostAsHashtable(blogPost.getSpace(), blogPost, (User)user);
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
    public Hashtable<String, String> getUserInfo(String appkey, String username, String password) throws RemoteException {
        Object result = this.transactionTemplate.execute(() -> {
            try {
                ConfluenceUser user = this.bloggingUtils.authenticateUser(username, password);
                Hashtable<String, String> userInformation = new Hashtable<String, String>();
                userInformation.put(FIRST_NAME, user.getFullName());
                userInformation.put(EMAIL, user.getEmail());
                userInformation.put(USER_ID, user.getName());
                Hashtable<String, String> hashtable = userInformation;
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

    @Override
    public String getTemplate(String appkey, String blogid, String username, String password, String templateType) throws RemoteException {
        throw new NotImplementedException(this.bloggingUtils.getText("error.template.notimplemented"));
    }

    @Override
    public boolean setTemplate(String appkey, String blogid, String username, String password, String template, String templateType) throws RemoteException {
        throw new NotImplementedException(this.bloggingUtils.getText("error.template.notimplemented"));
    }

    Map<String, String> splitBlogContent(String originalContent) {
        HashMap<String, String> splitContent = new HashMap<String, String>();
        Pattern pattern = Pattern.compile("<[tT][iI][tT][lL][eE]>.*?</[tT][iI][tT][lL][eE]>");
        Matcher matcher = pattern.matcher(originalContent);
        if (matcher.find()) {
            String fullTitleString = matcher.group();
            String title = fullTitleString.substring(7, fullTitleString.length() - 8);
            String contents = originalContent.substring(matcher.end());
            splitContent.put(TITLE, title);
            splitContent.put(CONTENT, contents);
        } else {
            splitContent.put(CONTENT, originalContent);
        }
        return splitContent;
    }

    private /* synthetic */ Hashtable lambda$getRecentPosts$3(Space space, User user, BlogPost blogPost) {
        return this.getBlogPostAsHashtable(space, blogPost, user);
    }
}

