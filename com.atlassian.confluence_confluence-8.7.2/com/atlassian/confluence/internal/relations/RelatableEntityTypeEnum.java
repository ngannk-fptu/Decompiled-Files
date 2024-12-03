/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.bean.EntityObject
 */
package com.atlassian.confluence.internal.relations;

import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceDescription;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.core.bean.EntityObject;

public enum RelatableEntityTypeEnum {
    PAGE(Page.class),
    COMMENT(Comment.class),
    BLOG(BlogPost.class),
    ATTACHMENT(Attachment.class),
    USER(ConfluenceUser.class),
    SPACE(Space.class),
    DRAFT(Draft.class),
    CUSTOM(CustomContentEntityObject.class);

    private final Class<? extends EntityObject> typeClass;

    private RelatableEntityTypeEnum(Class typeClass) {
        this.typeClass = typeClass;
    }

    public Class<? extends EntityObject> getType() {
        return this.typeClass;
    }

    public String getTypeName() {
        return this.typeClass.getName();
    }

    public static RelatableEntityTypeEnum getByContentEntityObject(ContentEntityObject object) {
        if (object instanceof Page) {
            return PAGE;
        }
        if (object instanceof SpaceDescription) {
            return SPACE;
        }
        if (object instanceof BlogPost) {
            return BLOG;
        }
        if (object instanceof Attachment) {
            return ATTACHMENT;
        }
        if (object instanceof Comment) {
            return COMMENT;
        }
        if (object instanceof Draft) {
            return DRAFT;
        }
        if (object instanceof CustomContentEntityObject) {
            return CUSTOM;
        }
        throw new IllegalArgumentException(object + " is not supported");
    }
}

