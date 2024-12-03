/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Entity
 *  net.java.ao.Preload
 */
package com.atlassian.confluence.plugins.ia;

import com.atlassian.confluence.plugins.ia.SidebarLinkCategory;
import java.io.Serializable;
import net.java.ao.Entity;
import net.java.ao.Preload;

@Preload
public interface SidebarLink
extends Entity,
Serializable {
    public static final long NO_ID = -2L;

    public String getSpaceKey();

    public void setSpaceKey(String var1);

    public SidebarLinkCategory getCategory();

    public void setCategory(SidebarLinkCategory var1);

    public Type getType();

    public void setType(Type var1);

    public String getWebItemKey();

    public void setWebItemKey(String var1);

    public boolean getHidden();

    public void setHidden(boolean var1);

    public int getPosition();

    public void setPosition(int var1);

    public String getCustomTitle();

    public void setCustomTitle(String var1);

    public String getHardcodedUrl();

    public void setHardcodedUrl(String var1);

    public String getCustomIconClass();

    public void setCustomIconClass(String var1);

    public long getDestPageId();

    public void setDestPageId(long var1);

    public static enum Type {
        WEB_ITEM("WEB_ITEM"),
        FORGE("FORGE"),
        PINNED_BLOG_POST("PINNED_BLOG_POST", "pinned_blog_post", "blogpost"),
        PINNED_PAGE("PINNED_PAGE", "pinned_page", "page"),
        EXTERNAL_LINK("EXTERNAL_LINK", "external_link"),
        PINNED_USER_INFO("PINNED_USER_INFO", "pinned_profile", "userinfo"),
        PINNED_SPACE("PINNED_SPACE", "pinned_space", "space"),
        PINNED_ATTACHMENT("PINNED_ATTACHMENT", "pinned_attachment", "attachment");

        String value;
        String resourceType;
        String styleClass;

        private Type(String value) {
            this.value = value;
        }

        private Type(String value, String styleClass) {
            this.value = value;
            this.styleClass = styleClass;
        }

        private Type(String value, String styleClass, String resourceType) {
            this.value = value;
            this.styleClass = styleClass;
            this.resourceType = resourceType;
        }

        public static Type getDefault() {
            return EXTERNAL_LINK;
        }

        public static Type fromResourceType(String resourceType) {
            if (resourceType == null) {
                return Type.getDefault();
            }
            for (Type type : Type.values()) {
                if (!resourceType.equals(type.getResourceType())) continue;
                return type;
            }
            return Type.getDefault();
        }

        public String getResourceType() {
            return this.resourceType;
        }

        public String getStyleClass() {
            return this.styleClass;
        }

        public String toString() {
            return this.value;
        }
    }
}

