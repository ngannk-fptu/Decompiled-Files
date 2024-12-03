/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.VersionHistorySummary
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceGroup
 */
package com.atlassian.confluence.rpc.soap;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.VersionHistorySummary;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.rpc.soap.beans.RemoteAttachment;
import com.atlassian.confluence.rpc.soap.beans.RemoteBlogEntrySummary;
import com.atlassian.confluence.rpc.soap.beans.RemoteComment;
import com.atlassian.confluence.rpc.soap.beans.RemotePageHistory;
import com.atlassian.confluence.rpc.soap.beans.RemotePageSummary;
import com.atlassian.confluence.rpc.soap.beans.RemoteSpaceGroup;
import com.atlassian.confluence.rpc.soap.beans.RemoteSpaceSummary;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceGroup;
import java.util.List;

public class SoapUtils {
    public static final String __PARANAMER_DATA = "getAttachments java.util.List attachments \ngetBlogEntrySummaries java.util.List blogEntries \ngetComments java.util.List comments \ngetPageHistory com.atlassian.confluence.pages.AbstractPage,com.atlassian.confluence.pages.PageManager page,pageManager \ngetPageSummaries java.util.List pages \ngetSpaceGroups java.util.List spaceGroups \ngetSpaceSummaries java.util.List spaces \n";

    public static RemoteSpaceSummary[] getSpaceSummaries(List spaces) {
        RemoteSpaceSummary[] result = new RemoteSpaceSummary[spaces.size()];
        for (int i = 0; i < spaces.size(); ++i) {
            result[i] = new RemoteSpaceSummary((Space)spaces.get(i));
        }
        return result;
    }

    @Deprecated
    public static RemoteSpaceGroup[] getSpaceGroups(List spaceGroups) {
        RemoteSpaceGroup[] result = new RemoteSpaceGroup[spaceGroups.size()];
        for (int i = 0; i < spaceGroups.size(); ++i) {
            result[i] = new RemoteSpaceGroup((SpaceGroup)spaceGroups.get(i));
        }
        return result;
    }

    public static RemotePageSummary[] getPageSummaries(List pages) {
        RemotePageSummary[] result = new RemotePageSummary[pages.size()];
        for (int i = 0; i < pages.size(); ++i) {
            result[i] = new RemotePageSummary((Page)pages.get(i));
        }
        return result;
    }

    public static RemoteBlogEntrySummary[] getBlogEntrySummaries(List blogEntries) {
        RemoteBlogEntrySummary[] result = new RemoteBlogEntrySummary[blogEntries.size()];
        for (int i = 0; i < blogEntries.size(); ++i) {
            result[i] = new RemoteBlogEntrySummary((AbstractPage)((BlogPost)blogEntries.get(i)));
        }
        return result;
    }

    public static RemotePageHistory[] getPageHistory(AbstractPage page, PageManager pageManager) {
        List history = pageManager.getVersionHistorySummaries((ContentEntityObject)page);
        history.remove(0);
        RemotePageHistory[] result = new RemotePageHistory[history.size()];
        for (int i = 0; i < result.length; ++i) {
            result[i] = new RemotePageHistory((VersionHistorySummary)history.get(i));
        }
        return result;
    }

    public static RemoteComment[] getComments(List comments) {
        RemoteComment[] result = new RemoteComment[comments.size()];
        for (int i = 0; i < result.length; ++i) {
            result[i] = new RemoteComment((Comment)comments.get(i));
        }
        return result;
    }

    public static RemoteAttachment[] getAttachments(List attachments) {
        RemoteAttachment[] result = new RemoteAttachment[attachments.size()];
        for (int i = 0; i < result.length; ++i) {
            result[i] = new RemoteAttachment((Attachment)attachments.get(i));
        }
        return result;
    }
}

