/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.EntityManager
 */
package com.atlassian.confluence.plugins.ia.impl;

import com.atlassian.confluence.plugins.ia.SidebarLink;
import com.atlassian.confluence.plugins.ia.SidebarLinkCategory;
import java.beans.PropertyChangeListener;
import net.java.ao.EntityManager;

class DefaultSidebarLink
implements SidebarLink {
    private final int id;
    private String spaceKey;
    private SidebarLinkCategory category;
    private SidebarLink.Type type;
    private String webItemKey;
    private boolean hidden;
    private int position;
    private String customTitle;
    private String hardcodedUrl;
    private String customIconClass;
    private long destResourceId;

    public DefaultSidebarLink(int id, String spaceKey, SidebarLinkCategory category, SidebarLink.Type type, String webItemKey, boolean hidden, int position, String customTitle, String hardcodedUrl, String customIconClass, long destResourceId) {
        this.id = id;
        this.spaceKey = spaceKey;
        this.category = category;
        this.type = type;
        this.webItemKey = webItemKey;
        this.hidden = hidden;
        this.position = position;
        this.customTitle = customTitle;
        this.hardcodedUrl = hardcodedUrl;
        this.customIconClass = customIconClass;
        this.destResourceId = destResourceId;
    }

    public DefaultSidebarLink(SidebarLink sidebarLink) {
        this(sidebarLink.getID(), sidebarLink.getSpaceKey(), sidebarLink.getCategory(), sidebarLink.getType(), sidebarLink.getWebItemKey(), sidebarLink.getHidden(), sidebarLink.getPosition(), sidebarLink.getCustomTitle(), sidebarLink.getHardcodedUrl(), sidebarLink.getCustomIconClass(), sidebarLink.getDestPageId());
    }

    public int getID() {
        return this.id;
    }

    @Override
    public String getSpaceKey() {
        return this.spaceKey;
    }

    @Override
    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    @Override
    public SidebarLinkCategory getCategory() {
        return this.category;
    }

    @Override
    public void setCategory(SidebarLinkCategory category) {
        this.category = category;
    }

    @Override
    public SidebarLink.Type getType() {
        return this.type;
    }

    @Override
    public void setType(SidebarLink.Type type) {
        this.type = type;
    }

    @Override
    public String getWebItemKey() {
        return this.webItemKey;
    }

    @Override
    public void setWebItemKey(String webItemKey) {
        this.webItemKey = webItemKey;
    }

    @Override
    public boolean getHidden() {
        return this.hidden;
    }

    @Override
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    @Override
    public int getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String getCustomTitle() {
        return this.customTitle;
    }

    @Override
    public void setCustomTitle(String customTitle) {
        this.customTitle = customTitle;
    }

    @Override
    public String getHardcodedUrl() {
        return this.hardcodedUrl;
    }

    @Override
    public void setHardcodedUrl(String hardcodedUrl) {
        this.hardcodedUrl = hardcodedUrl;
    }

    @Override
    public String getCustomIconClass() {
        return this.customIconClass;
    }

    @Override
    public void setCustomIconClass(String customIconClass) {
        this.customIconClass = customIconClass;
    }

    @Override
    public long getDestPageId() {
        return this.destResourceId;
    }

    @Override
    public void setDestPageId(long destResourceId) {
        this.destResourceId = destResourceId;
    }

    public void init() {
    }

    public void save() {
    }

    public EntityManager getEntityManager() {
        return null;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
    }

    public Class<DefaultSidebarLink> getEntityType() {
        return DefaultSidebarLink.class;
    }
}

