/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  net.java.ao.DBParam
 *  net.java.ao.RawEntity
 */
package com.atlassian.confluence.plugins.ia.impl;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.plugins.ia.SidebarLink;
import com.atlassian.confluence.plugins.ia.SidebarLinkCategory;
import com.atlassian.confluence.plugins.ia.SidebarLinkManager;
import com.atlassian.confluence.plugins.ia.SidebarLinks;
import java.util.Arrays;
import java.util.Objects;
import net.java.ao.DBParam;
import net.java.ao.RawEntity;

public class DefaultSidebarLinkManager
implements SidebarLinkManager {
    private final ActiveObjects activeObjects;

    public DefaultSidebarLinkManager(ActiveObjects activeObjects) {
        this.activeObjects = activeObjects;
    }

    @Override
    public SidebarLink createLink(String spaceKey, SidebarLinkCategory category, SidebarLink.Type type, String webItemKey, int position, String customTitle, String hardcodedUrl, String customIconClass, long destResourceId) {
        return (SidebarLink)this.activeObjects.executeInTransaction(() -> {
            for (SidebarLink result : (SidebarLink[])this.activeObjects.find(SidebarLink.class, "SPACE_KEY = ? AND POSITION >= ?", new Object[]{spaceKey, position})) {
                result.setPosition(result.getPosition() + 1);
                result.save();
            }
            SidebarLink link = (SidebarLink)this.activeObjects.create(SidebarLink.class, new DBParam[0]);
            link.setSpaceKey(spaceKey);
            link.setCategory(category);
            link.setType(type);
            link.setWebItemKey(webItemKey);
            link.setPosition(position);
            link.setCustomTitle(customTitle);
            link.setHardcodedUrl(hardcodedUrl);
            link.setCustomIconClass(customIconClass);
            link.setDestPageId(destResourceId);
            link.save();
            return link;
        });
    }

    @Override
    public void moveLink(SidebarLink link, int from, int to) {
        Objects.requireNonNull(link);
        this.activeObjects.executeInTransaction(() -> {
            for (SidebarLink result : (SidebarLink[])this.activeObjects.find(SidebarLink.class, "SPACE_KEY = ? AND POSITION > ? AND POSITION <= ?", new Object[]{link.getSpaceKey(), from, to})) {
                result.setPosition(result.getPosition() - 1);
                result.save();
            }
            for (SidebarLink result : (SidebarLink[])this.activeObjects.find(SidebarLink.class, "SPACE_KEY = ? AND POSITION >= ?", new Object[]{link.getSpaceKey(), to})) {
                result.setPosition(result.getPosition() + 1);
                result.save();
            }
            link.setPosition(to);
            link.save();
            return null;
        });
    }

    @Override
    public void deleteLink(SidebarLink link) {
        Objects.requireNonNull(link);
        this.activeObjects.executeInTransaction(() -> {
            for (SidebarLink result : (SidebarLink[])this.activeObjects.find(SidebarLink.class, "SPACE_KEY = ? AND POSITION > ?", new Object[]{link.getSpaceKey(), link.getPosition()})) {
                result.setPosition(result.getPosition() - 1);
                result.save();
            }
            this.activeObjects.delete(new RawEntity[]{link});
            return null;
        });
    }

    @Override
    public void deleteLinks(long pageId, SidebarLink.Type type) {
        this.activeObjects.executeInTransaction(() -> {
            SidebarLink[] linksToDelete;
            for (SidebarLink linkToDelete : linksToDelete = (SidebarLink[])this.activeObjects.find(SidebarLink.class, "DEST_PAGE_ID = ? AND TYPE = ?", new Object[]{pageId, type})) {
                for (SidebarLink result : (SidebarLink[])this.activeObjects.find(SidebarLink.class, "SPACE_KEY = ? AND POSITION > ?", new Object[]{linkToDelete.getSpaceKey(), linkToDelete.getPosition()})) {
                    result.setPosition(result.getPosition() - 1);
                    result.save();
                }
                this.activeObjects.delete(new RawEntity[]{linkToDelete});
            }
            return null;
        });
    }

    @Override
    public void deleteLinksForSpace(String spaceKey) {
        this.activeObjects.executeInTransaction(() -> {
            SidebarLink[] linksToDelete;
            for (SidebarLink link : linksToDelete = (SidebarLink[])this.activeObjects.find(SidebarLink.class, "SPACE_KEY = ?", new Object[]{spaceKey})) {
                this.activeObjects.delete(new RawEntity[]{link});
            }
            return null;
        });
    }

    @Override
    public void hideLink(SidebarLink link) {
        Objects.requireNonNull(link);
        this.activeObjects.executeInTransaction(() -> {
            link.setHidden(true);
            link.save();
            return null;
        });
    }

    @Override
    public void showLink(SidebarLink link) {
        Objects.requireNonNull(link);
        this.activeObjects.executeInTransaction(() -> {
            link.setHidden(false);
            link.save();
            return null;
        });
    }

    @Override
    public SidebarLink findById(int id) {
        return (SidebarLink)this.activeObjects.executeInTransaction(() -> {
            SidebarLink[] results = (SidebarLink[])this.activeObjects.find(SidebarLink.class, "ID = ?", new Object[]{id});
            return results.length > 0 ? results[0] : null;
        });
    }

    @Override
    public SidebarLinks findBySpace(String spaceKey) {
        return new SidebarLinks((Iterable)this.activeObjects.executeInTransaction(() -> Arrays.asList((SidebarLink[])this.activeObjects.find(SidebarLink.class, "SPACE_KEY = ? AND TYPE != ?", new Object[]{spaceKey, SidebarLink.Type.FORGE}))));
    }
}

