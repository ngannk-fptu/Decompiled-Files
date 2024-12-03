/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.bean.EntityObject
 */
package com.atlassian.confluence.search.service;

import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceDescription;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.core.bean.EntityObject;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ContentTypeEnum {
    PAGE("page", Page.class),
    COMMENT("comment", Comment.class),
    BLOG("blogpost", BlogPost.class),
    ATTACHMENT("attachment", Attachment.class),
    PERSONAL_INFORMATION("userinfo", PersonalInformation.class),
    SPACE_DESCRIPTION("spacedesc", SpaceDescription.class),
    PERSONAL_SPACE_DESCRIPTION("personalspacedesc", SpaceDescription.class),
    SPACE("space", Space.class),
    DRAFT("draft", Draft.class),
    CUSTOM("custom", CustomContentEntityObject.class);

    private static final Map<String, ContentTypeEnum> representationLookup;
    private static final Map<String, ContentTypeEnum> classNameLookup;
    private final String key;
    private final Class<? extends EntityObject> typeClass;

    private ContentTypeEnum(String key, Class<? extends EntityObject> typeClass) {
        this.key = key;
        this.typeClass = typeClass;
    }

    public String getRepresentation() {
        return this.key;
    }

    public boolean hasRepresentation(String key) {
        return this.getRepresentation().equals(key);
    }

    public Class<? extends EntityObject> getType() {
        return this.typeClass;
    }

    public String getTypeName() {
        return this.typeClass.getName();
    }

    public String toString() {
        return this.getRepresentation();
    }

    public static ContentTypeEnum getByRepresentation(String representation) {
        return representationLookup.get(representation);
    }

    public static ContentTypeEnum getByClassName(String className) {
        return classNameLookup.get(className);
    }

    static {
        representationLookup = new HashMap<String, ContentTypeEnum>(8);
        classNameLookup = new HashMap<String, ContentTypeEnum>(8);
        for (ContentTypeEnum contentTypeEnum : EnumSet.allOf(ContentTypeEnum.class)) {
            representationLookup.put(contentTypeEnum.getRepresentation(), contentTypeEnum);
            classNameLookup.put(contentTypeEnum.getTypeName(), contentTypeEnum);
        }
    }
}

