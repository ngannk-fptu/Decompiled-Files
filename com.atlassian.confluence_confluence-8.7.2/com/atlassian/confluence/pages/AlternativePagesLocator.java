/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.struts2.ServletActionContext
 */
package com.atlassian.confluence.pages;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.struts2.ServletActionContext;

public class AlternativePagesLocator {
    private static final int MAX_RESULTS = 20;
    private List<AbstractPage> possiblesInTrash;
    private List<AbstractPage> possibleRenamesInSpace;
    private List<AbstractPage> possibleMovesInOtherSpaces;
    private List<AbstractPage> pagesWithSimilarTitles;
    private final Space space;
    private final String title;
    private PageManager pageManager;
    private PermissionManager permissionManager;

    public AlternativePagesLocator(PageManager pageManager, PermissionManager permissionManager, Space space, String title) {
        this.pageManager = pageManager;
        this.permissionManager = permissionManager;
        this.space = space;
        this.title = title;
    }

    public boolean hasAlternatives() {
        if (this.foundInTrash()) {
            return true;
        }
        if (this.hasBlogPostContext()) {
            return !this.getRenamedBlogsInSpace().isEmpty() || !this.getBlogsInOtherSpaces().isEmpty();
        }
        return !this.getRenamedPagesInSpace().isEmpty() || !this.getPagesInOtherSpaces().isEmpty() || !this.getPagesWithSimilarTitleInSpace().isEmpty();
    }

    public boolean foundInTrash() {
        return !this.getPossiblesInTrash().isEmpty();
    }

    public List<AbstractPage> getPossiblesInTrash() {
        if (this.space == null || this.title == null) {
            return Collections.emptyList();
        }
        if (this.possiblesInTrash == null) {
            this.possiblesInTrash = this.getPermittedEntitiesOf(this.pageManager.getPageInTrash(this.space.getKey(), this.title), 20);
            this.possiblesInTrash.addAll(this.getPermittedEntitiesOf(this.pageManager.getBlogPostsInTrash(this.space.getKey(), this.title), 20));
        }
        return this.possiblesInTrash;
    }

    public List<AbstractPage> getPagesInOtherSpaces() {
        if (this.title == null) {
            return Collections.emptyList();
        }
        if (this.possibleMovesInOtherSpaces == null) {
            List<AbstractPage> redirects = this.pageManager.getPossibleRedirectsNotInSpace(this.space, this.title, 50);
            this.possibleMovesInOtherSpaces = this.getPermittedEntitiesOf(redirects, 10);
        }
        return this.possibleMovesInOtherSpaces;
    }

    public List<AbstractPage> getBlogsInOtherSpaces() {
        if (this.title == null) {
            return Collections.emptyList();
        }
        if (this.possibleMovesInOtherSpaces == null) {
            List<AbstractPage> redirects = this.pageManager.getPossibleBlogRedirectsNotInSpace(this.space, this.title, 50);
            this.possibleMovesInOtherSpaces = this.getPermittedEntitiesOf(redirects, 10);
        }
        return this.possibleMovesInOtherSpaces;
    }

    public List<AbstractPage> getRenamedPagesInSpace() {
        if (this.space == null || this.title == null) {
            return Collections.emptyList();
        }
        if (this.possibleRenamesInSpace == null) {
            List<AbstractPage> redirects = this.pageManager.getPossibleRedirectsInSpace(this.space, this.title, 25);
            this.possibleRenamesInSpace = this.getPermittedEntitiesOf(redirects, 5);
        }
        return this.possibleRenamesInSpace;
    }

    private List<AbstractPage> getRenamedBlogsInSpace() {
        if (this.space == null || this.title == null) {
            return Collections.emptyList();
        }
        if (this.possibleRenamesInSpace == null) {
            List<AbstractPage> redirects = this.pageManager.getPossibleBlogRedirectsInSpace(this.space, this.title, 25);
            this.possibleRenamesInSpace = this.getPermittedEntitiesOf(redirects, 5);
        }
        return this.possibleRenamesInSpace;
    }

    public List<AbstractPage> getPagesWithSimilarTitleInSpace() {
        if (this.space == null || this.title == null) {
            return Collections.emptyList();
        }
        if (this.pagesWithSimilarTitles == null) {
            List allPossibles = this.pageManager.getPagesStartingWith(this.space, this.title);
            Collections.sort(allPossibles, new PageByLength());
            this.pagesWithSimilarTitles = this.getPermittedEntitiesOf(allPossibles, 5);
        }
        return this.pagesWithSimilarTitles;
    }

    private List<AbstractPage> getPermittedEntitiesOf(List<? extends AbstractPage> pages, int maxCount) {
        return this.permissionManager.getPermittedEntities(AuthenticatedUserThreadLocal.get(), Permission.VIEW, pages.iterator(), maxCount);
    }

    private boolean hasBlogPostContext() {
        return ServletActionContext.getContext().getParameters().containsKey((Object)"postingDay");
    }

    private static class PageByLength
    implements Comparator<AbstractPage> {
        private PageByLength() {
        }

        @Override
        public int compare(AbstractPage a, AbstractPage b) {
            int lengthDiff = a.getTitle().length() - b.getTitle().length();
            return lengthDiff == 0 ? a.getTitle().compareToIgnoreCase(b.getTitle()) : lengthDiff;
        }
    }
}

