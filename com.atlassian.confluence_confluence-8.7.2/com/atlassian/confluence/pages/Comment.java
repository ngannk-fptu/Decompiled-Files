/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.google.common.collect.Sets
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.pages;

import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.HasLinkWikiMarkup;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.impl.util.collections.SetAsList;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.CommentStatus;
import com.atlassian.confluence.pages.Contained;
import com.atlassian.confluence.pages.ContentConvertible;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.Spaced;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.UrlUtils;
import com.google.common.collect.Sets;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

public class Comment
extends ContentEntityObject
implements HasLinkWikiMarkup,
Spaced,
ContentConvertible,
Contained<ContentEntityObject> {
    public static final String CONTENT_TYPE = "comment";
    public static final String INLINE_PROP = "inline-comment";
    public static final String MARKER_REF_PROP = "inline-marker-ref";
    public static final String ORIGINAL_SELECTION_PROP = "inline-original-selection";
    public static final String INLINE = "inline";
    public static final String FOOTER = "footer";
    private Comment parent;
    private Set<Comment> children = new LinkedHashSet<Comment>();

    public ContentEntityObject getContentEntityObject() {
        return this;
    }

    @Override
    public @Nullable ContentEntityObject getContainer() {
        return this.getContainerContent();
    }

    public void setContainer(ContentEntityObject container) {
        this.setContainerContent(container);
    }

    @Override
    public Space getSpace() {
        ContentEntityObject owner = this.getContainer();
        if (owner == null) {
            return null;
        }
        if (owner instanceof Spaced) {
            return ((Spaced)((Object)owner)).getSpace();
        }
        return null;
    }

    @Override
    public String getDisplayTitle() {
        if (!this.isLatestVersion()) {
            return ((ContentEntityObject)this.getLatestVersion()).getDisplayTitle();
        }
        ContentEntityObject container = this.getContainer();
        if (container == null) {
            return "Erroneous orphaned comment";
        }
        return "Re: " + container.getTitle();
    }

    @Override
    public String getUrlPath() {
        return this.getUrlPathForVersion((Comment)this.getLatestVersion());
    }

    private String getUrlPathForVersion(Comment comment) {
        ContentEntityObject container = Objects.requireNonNull(comment.getContainer(), "Comment has no container, cannot generate URL path");
        if (container instanceof AbstractPage) {
            return UrlUtils.appendAmpersandOrQuestionMark(container.getUrlPath()) + "focusedCommentId=" + comment.getId() + "#comment-" + comment.getId();
        }
        if (container instanceof Attachment) {
            return GeneralUtil.getCommentUrl((Attachment)container, comment);
        }
        return container.getUrlPath() + "#comment-" + comment.getId();
    }

    @Override
    public String getType() {
        return CONTENT_TYPE;
    }

    @Override
    public String getNameForComparison() {
        return Objects.requireNonNull(this.getContainer()).getTitle();
    }

    @Deprecated
    public String getSpaceKey() {
        ContentEntityObject owner = this.getContainer();
        if (owner instanceof SpaceContentEntityObject) {
            return ((SpaceContentEntityObject)owner).getSpaceKey();
        }
        return null;
    }

    public Comment getParent() {
        return this.parent;
    }

    public void setParent(Comment parent) {
        this.parent = parent;
    }

    public List<Comment> getChildren() {
        return this.children != null ? new SetAsList<Comment>(this.children) : null;
    }

    public void setChildren(List<Comment> children) {
        this.children = children != null ? Sets.newLinkedHashSet(children) : null;
    }

    public void addChild(Comment child) {
        this.children.add(child);
        child.setParent(this);
        ContentEntityObject container = this.getContainer();
        if (container != null) {
            container.addComment(child);
        }
    }

    public int getDescendantsCount() {
        int descendants = 0;
        for (Comment child : this.children) {
            ++descendants;
            descendants += child.getDescendantsCount();
        }
        return descendants;
    }

    public Set<String> getDescendantAuthors() {
        HashSet<String> authors = new HashSet<String>();
        for (Comment child : this.children) {
            authors.add(child.getCreatorName());
            authors.addAll(child.getDescendantAuthors());
        }
        return authors;
    }

    public int getDepth() {
        if (this.parent == null) {
            return 0;
        }
        return 1 + this.parent.getDepth();
    }

    public Date getThreadChangedDate() {
        Date date = this.getCreationDate();
        for (Comment child : this.children) {
            Date childChangedDate = child.getThreadChangedDate();
            if (childChangedDate == null || date != null && !childChangedDate.after(date)) continue;
            date = childChangedDate;
        }
        return date;
    }

    public void reparentChildren(Comment newParent) {
        Iterator<Comment> it = this.children.iterator();
        while (it.hasNext()) {
            Comment child = it.next();
            if (newParent != null) {
                newParent.addChild(child);
            } else {
                child.setParent(null);
            }
            it.remove();
        }
    }

    @Override
    public void convertToHistoricalVersion() {
        super.convertToHistoricalVersion();
        this.children = new LinkedHashSet<Comment>();
        this.parent = null;
        ContentEntityObject owner = this.getContainer();
        if (owner != null) {
            owner.removeComment(this);
            this.setContainer(null);
        }
    }

    public void removeChild(Comment child) {
        if (child.getParent() != null && child.getParent().equals(this)) {
            child.setParent(null);
            this.children.remove(child);
        }
    }

    @Override
    public Object clone() {
        Comment comment = (Comment)super.clone();
        comment.setContainer(this.getContainer());
        return comment;
    }

    @Override
    public String getLinkWikiMarkup() {
        return String.format("[$%s]", this.getIdAsString());
    }

    @Override
    public ContentType getContentTypeObject() {
        return ContentType.COMMENT;
    }

    @Override
    public ContentId getContentId() {
        return ContentId.of((ContentType)ContentType.COMMENT, (long)this.getId());
    }

    @Override
    public boolean shouldConvertToContent() {
        return true;
    }

    public boolean isInlineComment() {
        return Boolean.valueOf(this.getProperties().getStringProperty(INLINE_PROP));
    }

    public void setInlineComment(boolean isInlineComment) {
        this.getProperties().setStringProperty(INLINE_PROP, Boolean.toString(isInlineComment));
    }

    public CommentStatus getStatus() {
        CommentStatus.Builder builder = new CommentStatus.Builder();
        builder.setValue(this.getProperties().getStringProperty("status"));
        builder.setLastModifiedDate(this.getProperties().getLongProperty("status-lastmoddate", 0L));
        builder.setLastModifider(this.getProperties().getStringProperty("status-lastmodifier"));
        return builder.build();
    }

    public void setStatus(CommentStatus status) {
        if (status.getValue() != null) {
            this.getProperties().setStringProperty("status", status.getValue().getStringValue());
        }
        if (StringUtils.isNotBlank((CharSequence)status.getLastModifier())) {
            this.getProperties().setStringProperty("status-lastmodifier", status.getLastModifier());
        }
        if (status.getLastModifiedDate() != null) {
            this.getProperties().setLongProperty("status-lastmoddate", status.getLastModifiedDate());
        }
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = super.equals(obj);
        if (result && this.getId() == 0L) {
            return this == obj;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}

