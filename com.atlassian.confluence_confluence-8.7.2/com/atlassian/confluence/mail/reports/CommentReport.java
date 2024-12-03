/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.mail.reports;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.mail.reports.AbstractContentEntityReport;
import com.atlassian.confluence.mail.reports.ChangeDigestReport;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.HashSet;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;

public class CommentReport
extends AbstractContentEntityReport {
    private final ContentEntityObject commentOwner;
    private final Set<String> commentAuthors = new HashSet<String>();
    private int count;

    public CommentReport(@NonNull Comment comment, ChangeDigestReport report) {
        super(comment, report);
        this.commentOwner = comment.getContainer();
        this.count = 1;
        ConfluenceUser creator = comment.getCreator();
        this.addCommentAuthor(creator != null ? creator.getName() : null);
    }

    public ContentEntityObject getCommentOwner() {
        return this.commentOwner;
    }

    public void incrementCount() {
        ++this.count;
    }

    public int getCount() {
        return this.count;
    }

    public void addCommentAuthor(String author) {
        this.commentAuthors.add(author);
    }

    public Set getCommentAuthors() {
        return this.commentAuthors;
    }
}

