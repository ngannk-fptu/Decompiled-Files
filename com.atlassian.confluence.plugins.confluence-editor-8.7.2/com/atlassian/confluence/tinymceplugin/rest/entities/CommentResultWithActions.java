/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.Immutable
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.tinymceplugin.rest.entities;

import com.atlassian.confluence.tinymceplugin.rest.entities.CommentResult;
import com.atlassian.confluence.tinymceplugin.rest.entities.UserAction;
import com.google.errorprone.annotations.Immutable;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Immutable
public class CommentResultWithActions
extends CommentResult {
    @XmlElement
    private final List<UserAction> primaryActions;
    @XmlElement
    private final List<UserAction> secondaryActions;

    public CommentResultWithActions() {
        this.primaryActions = null;
        this.secondaryActions = null;
    }

    private CommentResultWithActions(CommentResultWithActionsBuilder builder) {
        super(builder.id, builder.html, builder.ownerId, builder.parentId, builder.asyncRenderSafe, builder.isInlineComment, builder.serializedHighlights, builder.dataRef);
        this.primaryActions = builder.primaryActions;
        this.secondaryActions = builder.secondaryActions;
    }

    public List<UserAction> getPrimaryActions() {
        return this.primaryActions;
    }

    public List<UserAction> getSecondaryActions() {
        return this.secondaryActions;
    }

    public static class CommentResultWithActionsBuilder {
        private long id;
        private String html;
        private boolean asyncRenderSafe;
        private long ownerId;
        private long parentId;
        private boolean isInlineComment;
        private String serializedHighlights;
        private String dataRef;
        private List<UserAction> primaryActions;
        private List<UserAction> secondaryActions;

        public CommentResultWithActionsBuilder(long id, String html, long ownerId, long parentId, boolean asyncRenderSafe) {
            this.id = id;
            this.html = html;
            this.ownerId = ownerId;
            this.parentId = parentId;
            this.asyncRenderSafe = asyncRenderSafe;
        }

        public CommentResultWithActionsBuilder setInlineComment(boolean isInlineComment) {
            this.isInlineComment = isInlineComment;
            return this;
        }

        public CommentResultWithActionsBuilder setSerializedHighlights(String serializedHighlights) {
            this.serializedHighlights = serializedHighlights;
            return this;
        }

        public CommentResultWithActionsBuilder setDataRef(String dataRef) {
            this.dataRef = dataRef;
            return this;
        }

        public CommentResultWithActionsBuilder setPrimaryActions(List<UserAction> primaryActions) {
            this.primaryActions = primaryActions;
            return this;
        }

        public CommentResultWithActionsBuilder setSecondaryActions(List<UserAction> secondaryActions) {
            this.secondaryActions = secondaryActions;
            return this;
        }

        public CommentResultWithActions build() {
            return new CommentResultWithActions(this);
        }
    }
}

