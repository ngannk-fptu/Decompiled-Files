/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.mail.reports;

import com.atlassian.confluence.mail.reports.ChangeDigestReport;
import com.atlassian.confluence.mail.reports.CommentReport;
import com.atlassian.confluence.mail.reports.PageReport;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;

public class SpaceReport {
    private final Space space;
    private final List<PageReport> pages;
    private final List<CommentReport> comments;
    private final Date refDate;
    private final ChangeDigestReport parentReport;

    public SpaceReport(@NonNull Space space, ChangeDigestReport report) {
        this.space = space;
        this.parentReport = report;
        this.refDate = new Date(System.currentTimeMillis() - this.parentReport.getCoverPeriod());
        this.pages = new ArrayList<PageReport>();
        this.comments = new ArrayList<CommentReport>();
    }

    public Space getSpace() {
        return this.space;
    }

    public boolean isNewForPeriod() {
        return this.refDate.before(this.getCreationDate());
    }

    public ChangeDigestReport getParentReport() {
        return this.parentReport;
    }

    public String getCreatorName() {
        ConfluenceUser creator = this.space.getCreator();
        return creator != null ? creator.getName() : null;
    }

    public User getCreator() {
        return this.parentReport.getUserAccessor().getUserByName(this.getCreatorName());
    }

    public Date getCreationDate() {
        return this.space.getCreationDate();
    }

    public Date getLastModificationDate() {
        return this.space.getLastModificationDate();
    }

    public long getId() {
        return this.space.getId();
    }

    public String getLastModifierName() {
        ConfluenceUser lastModifier = this.space.getLastModifier();
        return lastModifier != null ? lastModifier.getName() : null;
    }

    public User getLastModifier() {
        return this.space.getLastModifier();
    }

    public String getKey() {
        return this.space.getKey();
    }

    public String getName() {
        return this.space.getName();
    }

    public void addPage(PageReport page) {
        this.pages.add(page);
    }

    public void removePage(PageReport page) {
        this.pages.remove(page);
    }

    public void addBlogPost(PageReport page) {
        this.pages.add(page);
    }

    public List getPages() {
        return this.pages;
    }

    public void addComment(CommentReport commentReport) {
        for (CommentReport existingReport : this.comments) {
            long commentReportPageId;
            long existingReportPageId = existingReport.getCommentOwner().getId();
            if (existingReportPageId != (commentReportPageId = commentReport.getCommentOwner().getId())) continue;
            existingReport.incrementCount();
            existingReport.addCommentAuthor(commentReport.getCreatorName());
            return;
        }
        this.comments.add(commentReport);
    }

    public List getComments() {
        return this.comments;
    }

    public int getChangeSize() {
        return this.pages.size() + this.comments.size();
    }
}

