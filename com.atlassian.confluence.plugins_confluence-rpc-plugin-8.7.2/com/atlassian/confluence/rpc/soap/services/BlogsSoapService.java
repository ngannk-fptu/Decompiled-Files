/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.Modification
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.DuplicateDataRuntimeException
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.rpc.RemoteException
 *  com.atlassian.confluence.spaces.Space
 */
package com.atlassian.confluence.rpc.soap.services;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.Modification;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.DuplicateDataRuntimeException;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.rpc.RemoteException;
import com.atlassian.confluence.rpc.soap.SoapUtils;
import com.atlassian.confluence.rpc.soap.beans.RemoteBlogEntry;
import com.atlassian.confluence.rpc.soap.beans.RemoteBlogEntrySummary;
import com.atlassian.confluence.rpc.soap.services.SoapServiceHelper;
import com.atlassian.confluence.spaces.Space;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class BlogsSoapService {
    private PageManager pageManager;
    private SoapServiceHelper soapServiceHelper;
    public static final String __PARANAMER_DATA = "getBlogEntries java.lang.String spaceKey \ngetBlogEntry long entryId \ngetBlogEntryByDateAndTitle java.lang.String,int,int,int,java.lang.String spaceKey,year,month,dayOfMonth,postTitle \ngetBlogEntryByDayAndTitle java.lang.String,int,java.lang.String spaceKey,dayOfMonth,postTitle \nsetPageManager com.atlassian.confluence.pages.PageManager pageManager \nsetSoapServiceHelper com.atlassian.confluence.rpc.soap.services.SoapServiceHelper soapServiceHelper \nstoreBlogEntry com.atlassian.confluence.rpc.soap.beans.RemoteBlogEntry remoteBlogEntry \n";

    public RemoteBlogEntry getBlogEntryByDayAndTitle(String spaceKey, int dayOfMonth, String postTitle) throws RemoteException {
        GregorianCalendar calDate = new GregorianCalendar();
        calDate.set(5, dayOfMonth);
        BlogPost blogPost = this.pageManager.getBlogPost(spaceKey, postTitle, (Calendar)calDate);
        this.soapServiceHelper.assertCanView((AbstractPage)blogPost);
        return new RemoteBlogEntry(blogPost);
    }

    public RemoteBlogEntry getBlogEntryByDateAndTitle(String spaceKey, int year, int month, int dayOfMonth, String postTitle) throws RemoteException {
        GregorianCalendar calDate = new GregorianCalendar();
        calDate.set(year, month - 1, dayOfMonth);
        BlogPost blogPost = this.pageManager.getBlogPost(spaceKey, postTitle, (Calendar)calDate);
        this.soapServiceHelper.assertCanView((AbstractPage)blogPost);
        return new RemoteBlogEntry(blogPost);
    }

    public RemoteBlogEntry getBlogEntry(long entryId) throws RemoteException {
        BlogPost entry = this.pageManager.getBlogPost(entryId);
        this.soapServiceHelper.assertCanView((AbstractPage)entry);
        return new RemoteBlogEntry(entry);
    }

    public RemoteBlogEntrySummary[] getBlogEntries(String spaceKey) throws RemoteException {
        Space space = this.soapServiceHelper.retrieveSpace(spaceKey);
        return SoapUtils.getBlogEntrySummaries(this.pageManager.getBlogPosts(space, true));
    }

    public RemoteBlogEntry storeBlogEntry(RemoteBlogEntry remoteBlogEntry) throws RemoteException {
        if (remoteBlogEntry.getId() <= 0L) {
            return this.createBlog(remoteBlogEntry);
        }
        return this.updateBlog(remoteBlogEntry);
    }

    private RemoteBlogEntry createBlog(RemoteBlogEntry remoteBlogEntry) throws RemoteException {
        Space space = this.soapServiceHelper.retrieveSpace(remoteBlogEntry.getSpace());
        this.soapServiceHelper.assertCanView(space);
        this.soapServiceHelper.assertCanCreateBlogPost(space);
        if (remoteBlogEntry.getPublishDate() != null && remoteBlogEntry.getPublishDate().after(new Date())) {
            throw new RemoteException("A publish date for a blog post cannot be in a future.");
        }
        BlogPost blogPost = new BlogPost();
        blogPost.setSpace(space);
        blogPost.setTitle(remoteBlogEntry.getTitle());
        blogPost.setBodyAsString(remoteBlogEntry.getContent());
        blogPost.setCreationDate(remoteBlogEntry.getPublishDate());
        try {
            this.pageManager.saveContentEntity((ContentEntityObject)blogPost, null);
        }
        catch (DuplicateDataRuntimeException ex) {
            throw new RemoteException(ex.getMessage(), ex.getCause());
        }
        return new RemoteBlogEntry(blogPost);
    }

    private RemoteBlogEntry updateBlog(final RemoteBlogEntry remoteBlogEntry) throws RemoteException {
        BlogPost blogPost = this.pageManager.getBlogPost(remoteBlogEntry.getId());
        this.soapServiceHelper.assertCanModify((AbstractPage)blogPost);
        if (blogPost == null) {
            throw new RemoteException("The blog post with id " + remoteBlogEntry.getId() + " you are trying to update does not exist.)");
        }
        if (!blogPost.getSpace().getKey().equals(remoteBlogEntry.getSpace())) {
            throw new RemoteException("You can't change an existing page's space.");
        }
        this.pageManager.saveNewVersion((ContentEntityObject)blogPost, (Modification)new Modification<BlogPost>(){

            public void modify(BlogPost blogPost) {
                blogPost.setBodyAsString(remoteBlogEntry.getContent());
                blogPost.setTitle(remoteBlogEntry.getTitle());
            }
        });
        return new RemoteBlogEntry(blogPost);
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    public void setSoapServiceHelper(SoapServiceHelper soapServiceHelper) {
        this.soapServiceHelper = soapServiceHelper;
    }
}

