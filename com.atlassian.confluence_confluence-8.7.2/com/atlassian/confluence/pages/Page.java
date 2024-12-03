/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Depth
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.rest.api.model.pagination.PaginationLimits
 *  com.google.common.base.Preconditions
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.pages;

import com.atlassian.confluence.api.model.Depth;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.core.Hierarchical;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.ChildPositionComparator;
import com.atlassian.confluence.pages.ContentConvertible;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.rest.api.model.pagination.PaginationLimits;
import com.atlassian.confluence.spaces.Space;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class Page
extends AbstractPage
implements Hierarchical<Page>,
ContentConvertible {
    public static final String CONTENT_TYPE = "page";
    private Page parent;
    private List<Page> children = new ArrayList<Page>();
    private List<Page> ancestors = new ArrayList<Page>();
    private Integer position;
    static final Comparator<Page> CHILD_PAGE_COMPARATOR = ChildPositionComparator.INSTANCE;

    public void setPosition(Integer position) {
        this.position = position;
    }

    public void incPosition() {
        Integer n = this.position;
        Integer n2 = this.position = Integer.valueOf(this.position + 1);
    }

    public Integer getPosition() {
        return this.position;
    }

    @Override
    public boolean isRootLevel() {
        return this.parent == null;
    }

    @Override
    public Page getParent() {
        return this.parent;
    }

    @Override
    @Deprecated
    public void setParent(Page parent) {
        this.parent = parent;
    }

    public void setParentPage(Page parent) {
        this.checkParentValid(parent);
        this.parent = parent;
    }

    private void checkParentValid(Page parent) {
        boolean parentInSameSpace;
        if (parent == null) {
            return;
        }
        if (parent == this || this.getId() != 0L && parent.getId() == this.getId()) {
            throw new IllegalArgumentException("Can not set page as its own parent.");
        }
        Space parentSpace = parent.getSpace();
        Space childSpace = this.getSpace();
        boolean bl = parentInSameSpace = parentSpace == null || childSpace == null || parentSpace.getKey() == null || childSpace.getKey() == null || parentSpace.getKey().equals(childSpace.getKey());
        if (!parentInSameSpace) {
            throw new IllegalArgumentException("Can't add a parent from another space.");
        }
    }

    @Override
    public List<Page> getChildren() {
        return this.children;
    }

    @Override
    public boolean hasChildren() {
        return this.getChildren() != null && this.getChildren().size() > 0;
    }

    public List<Page> getSortedChildren() {
        ArrayList<Page> sortedKids = new ArrayList<Page>(this.getChildren());
        sortedKids.sort(CHILD_PAGE_COMPARATOR);
        return sortedKids;
    }

    @Override
    public void setChildren(List<Page> children) {
        this.children = children;
    }

    @Override
    public void addChild(Page child) {
        if (this.getAncestors().contains(child)) {
            throw new IllegalArgumentException("Cannot add an existing ancestor as a child!");
        }
        child.getAncestors().clear();
        this.children.add(child);
        child.setParentPage(this);
        child.getAncestors().addAll(this.getAncestors());
        child.getAncestors().add(this);
    }

    @Override
    public void removeChild(Page child) {
        child.setParentPage(null);
        this.children.remove(child);
        child.getAncestors().clear();
        child.setPosition(null);
    }

    public boolean isHomePage() {
        return this.getSpace() != null && this.getSpace().getHomePage() != null && this.getSpace().getHomePage().equals(this);
    }

    @Override
    public String getType() {
        return CONTENT_TYPE;
    }

    @Override
    public void convertToHistoricalVersion() {
        super.convertToHistoricalVersion();
        this.setChildren((List<Page>)new ArrayList<Page>());
        this.setParentPage(null);
        this.setAncestors(new ArrayList<Page>());
    }

    @Override
    public String getNameForComparison() {
        if (this.isDraft()) {
            return "Draft: " + (StringUtils.isNotBlank((CharSequence)this.getTitle()) ? this.getTitle() : "(No Title Specified)") + " by " + (StringUtils.isNotBlank((CharSequence)this.getCreatorName()) ? this.getCreatorName() : "Anonymous");
        }
        return this.getTitle();
    }

    public void severParentChildRelationships() {
        if (this.getParent() != null) {
            this.getParent().removeChild(this);
        }
        while (!this.getChildren().isEmpty()) {
            Page childPage = this.getChildren().get(0);
            this.removeChild(childPage);
        }
    }

    @Override
    public void remove(PageManager pageManager) {
        PageResponse<Page> pageResponse;
        this.resetCurrentSpaceHomePage(null);
        pageManager.removePageFromAncestorCollections(this);
        ArrayList draftChildren = new ArrayList();
        int numDraftsPerBatch = PaginationLimits.draftChildren((Expansions)Expansions.EMPTY);
        do {
            pageResponse = pageManager.getDraftChildren(this, LimitedRequestImpl.create((int)draftChildren.size(), (int)numDraftsPerBatch, (int)numDraftsPerBatch), Depth.ROOT);
            draftChildren.addAll(pageResponse.getResults());
        } while (pageResponse.hasMore());
        draftChildren.forEach(child -> {
            this.removeChild((Page)child);
            pageManager.saveContentEntity((ContentEntityObject)child, DefaultSaveContext.DRAFT);
        });
        this.severParentChildRelationships();
        super.remove(pageManager);
    }

    @Override
    public String getLinkWikiMarkup() {
        return String.format("[%s:%s]", this.getSpaceKey(), this.getTitle());
    }

    @Override
    public void trash() {
        this.severParentChildRelationships();
        super.trash();
    }

    public List<Page> getDescendants() {
        ArrayList<Page> descendants = new ArrayList<Page>();
        this.addDescendants(descendants);
        return descendants;
    }

    @Deprecated
    public List<Page> getDescendents() {
        return this.getDescendants();
    }

    private void addDescendants(List<Page> descendants) {
        Preconditions.checkArgument((boolean)Collections.disjoint(descendants, this.getChildren()), (Object)("Cannot add an existing ancestor as a descendant for " + this));
        descendants.addAll(this.getChildren());
        this.getChildren().stream().forEach(c -> c.addDescendants(descendants));
    }

    @Override
    public List<Page> getAncestors() {
        return this.ancestors;
    }

    public void setAncestors(List<Page> ancestors) {
        this.ancestors = ancestors;
    }

    @Override
    public void setSpace(Space newSpace) {
        this.resetCurrentSpaceHomePage(newSpace);
        super.setSpace(newSpace);
    }

    private void resetCurrentSpaceHomePage(Space newSpace) {
        Page homePage;
        if (this.getSpace() != null && (!this.getSpace().equals(newSpace) || newSpace == null) && (homePage = this.getSpace().getHomePage()) != null && homePage.equals(this)) {
            this.getSpace().setHomePage(null);
        }
    }

    @Override
    public boolean isIndexable() {
        return super.isIndexable() && !".bookmarks".equals(this.getTitle());
    }

    @Override
    public ContentType getContentTypeObject() {
        return ContentType.PAGE;
    }

    @Override
    public ContentId getContentId() {
        return ContentId.of((ContentType)ContentType.PAGE, (long)this.getId());
    }

    @Override
    public boolean shouldConvertToContent() {
        return true;
    }

    public Page copyLatestVersion() {
        Page copy = new Page();
        copy.setTitle(this.getTitle());
        copy.setPosition(this.getPosition());
        copy.setCreationDate(this.getCreationDate());
        copy.setLastModificationDate(this.getLastModificationDate());
        copy.setBodyAsString(this.getBodyAsString());
        return copy;
    }
}

