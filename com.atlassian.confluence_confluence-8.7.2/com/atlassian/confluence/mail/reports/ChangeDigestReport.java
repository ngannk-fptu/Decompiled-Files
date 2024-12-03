/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.mail.reports;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.mail.reports.ChangeDigestStats;
import com.atlassian.confluence.mail.reports.CommentReport;
import com.atlassian.confluence.mail.reports.PageReport;
import com.atlassian.confluence.mail.reports.PersonalInfoReport;
import com.atlassian.confluence.mail.reports.SpaceReport;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.user.User;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ChangeDigestReport {
    private final User user;
    private final SortedMap<String, SpaceReport> spaceReportsBySpaceKey = new TreeMap<Object, SpaceReport>(Collator.getInstance());
    private final TreeMap<String, PersonalInfoReport> changedPersonalInfos = new TreeMap();
    private final UserAccessor userAccessor;
    private ChangeDigestStats stats;
    private long coverPeriod = 86400000L;

    public ChangeDigestReport(User user, UserAccessor userAccessor) {
        this.user = user;
        this.userAccessor = userAccessor;
    }

    UserAccessor getUserAccessor() {
        return this.userAccessor;
    }

    public void addPage(Page page) {
        this.getSpaceReport(page.getSpace()).addPage(new PageReport(page, this));
    }

    public void addBlogPost(BlogPost blogPost) {
        this.getSpaceReport(blogPost.getSpace()).addBlogPost(new PageReport(blogPost, this));
    }

    public void addComment(Comment comment) {
        ContentEntityObject commentOwner = comment.getContainer();
        if (commentOwner instanceof SpaceContentEntityObject && ((SpaceContentEntityObject)commentOwner).getSpace() != null) {
            this.getSpaceReport(((SpaceContentEntityObject)commentOwner).getSpace()).addComment(new CommentReport(comment, this));
        }
    }

    public List getSpaceReports() {
        return new ArrayList<SpaceReport>(this.spaceReportsBySpaceKey.values());
    }

    public User getUser() {
        return this.user;
    }

    public long getCoverPeriod() {
        return this.coverPeriod;
    }

    public void setCoverPeriod(long coverPeriod) {
        this.coverPeriod = coverPeriod;
    }

    public ChangeDigestStats getStats() {
        if (this.stats == null) {
            this.stats = new ChangeDigestStats(this);
        }
        return this.stats;
    }

    public void setStats(ChangeDigestStats stats) {
        this.stats = stats;
    }

    private SpaceReport getSpaceReport(@NonNull Space space) {
        SpaceReport spaceReport = (SpaceReport)this.spaceReportsBySpaceKey.get(space.getKey());
        if (spaceReport != null) {
            return spaceReport;
        }
        SpaceReport newSpaceReport = new SpaceReport(space, this);
        this.spaceReportsBySpaceKey.put(space.getKey(), newSpaceReport);
        return newSpaceReport;
    }

    public void addPersonalInformation(PersonalInformation personalInformation) {
        String lastModifierName;
        PersonalInfoReport personalInfoReport = new PersonalInfoReport(personalInformation, this);
        if (personalInfoReport.getLastModifierName() == null) {
            return;
        }
        ConfluenceUser user = personalInformation.getUser();
        String userName = user != null ? user.getName() : null;
        if (Objects.equals(userName, lastModifierName = personalInfoReport.getLastModifierName())) {
            this.changedPersonalInfos.put(user != null ? user.getName() : null, personalInfoReport);
        }
    }

    public Collection getChangedPersonalInformation() {
        return this.changedPersonalInfos.values();
    }

    public boolean hasChanges() {
        for (Object o : this.getSpaceReports()) {
            SpaceReport spaceReport = (SpaceReport)o;
            if (spaceReport.getChangeSize() <= 0) continue;
            return true;
        }
        return false;
    }
}

