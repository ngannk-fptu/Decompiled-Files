/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugins.navlink.producer.contentlinks.customcontentlink.CustomContentLink
 *  com.atlassian.plugins.navlink.producer.contentlinks.customcontentlink.CustomContentLinkService
 *  com.atlassian.plugins.navlink.producer.contentlinks.customcontentlink.NoAdminPermissionException
 *  com.atlassian.plugins.navlink.spi.Project
 *  com.atlassian.plugins.navlink.spi.ProjectManager
 *  com.atlassian.plugins.navlink.spi.ProjectNotFoundException
 *  com.atlassian.plugins.navlink.spi.ProjectPermissionManager
 *  com.atlassian.sal.api.transaction.TransactionCallback
 *  com.atlassian.sal.api.user.UserManager
 *  com.google.common.base.Function
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  net.java.ao.Query
 *  net.java.ao.RawEntity
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.navlink.producer.contentlinks.customcontentlink;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugins.navlink.producer.contentlinks.customcontentlink.CustomContentLink;
import com.atlassian.plugins.navlink.producer.contentlinks.customcontentlink.CustomContentLinkAO;
import com.atlassian.plugins.navlink.producer.contentlinks.customcontentlink.CustomContentLinkService;
import com.atlassian.plugins.navlink.producer.contentlinks.customcontentlink.NoAdminPermissionException;
import com.atlassian.plugins.navlink.producer.contentlinks.plugin.CustomContentLinkProviderModuleDescriptor;
import com.atlassian.plugins.navlink.spi.Project;
import com.atlassian.plugins.navlink.spi.ProjectManager;
import com.atlassian.plugins.navlink.spi.ProjectNotFoundException;
import com.atlassian.plugins.navlink.spi.ProjectPermissionManager;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.user.UserManager;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.java.ao.Query;
import net.java.ao.RawEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultCustomContentLinkService
implements CustomContentLinkService {
    private static final Logger log = LoggerFactory.getLogger(DefaultCustomContentLinkService.class);
    final Function<CustomContentLinkAO, CustomContentLink> aoToEntityTransformer = new Function<CustomContentLinkAO, CustomContentLink>(){

        public CustomContentLink apply(@Nullable CustomContentLinkAO input) {
            return CustomContentLink.builder().id(input.getID()).url(input.getLinkUrl()).label(input.getLinkLabel()).key(input.getContentKey()).sequence(input.getSequence()).build();
        }
    };
    private final ProjectManager projectManager;
    private final ProjectPermissionManager projectPermissionManager;
    private final UserManager userManager;
    private ActiveObjects ao;
    private PluginAccessor pluginAccessor;

    public DefaultCustomContentLinkService(ActiveObjects ao, PluginAccessor pluginAccessor, ProjectManager projectManager, ProjectPermissionManager projectPermissionManager, UserManager userManager) {
        this.ao = ao;
        this.pluginAccessor = pluginAccessor;
        this.projectManager = projectManager;
        this.projectPermissionManager = projectPermissionManager;
        this.userManager = userManager;
    }

    public CustomContentLink addCustomContentLink(final CustomContentLink customContentLink) throws NoAdminPermissionException {
        this.checkPermission(customContentLink.getContentKey());
        return (CustomContentLink)this.aoToEntityTransformer.apply(this.ao.executeInTransaction((TransactionCallback)new TransactionCallback<CustomContentLinkAO>(){

            public CustomContentLinkAO doInTransaction() {
                CustomContentLinkAO link = (CustomContentLinkAO)DefaultCustomContentLinkService.this.ao.create(CustomContentLinkAO.class, Collections.emptyMap());
                link.setContentKey(customContentLink.getContentKey());
                link.setLinkLabel(customContentLink.getLinkLabel());
                link.setLinkUrl(customContentLink.getLinkUrl());
                link.setSequence(DefaultCustomContentLinkService.this.getNextSequence(customContentLink.getContentKey()));
                link.save();
                return link;
            }
        }));
    }

    private void copyToAO(CustomContentLink customContentLink, CustomContentLinkAO link) {
        link.setContentKey(customContentLink.getContentKey());
        link.setLinkLabel(customContentLink.getLinkLabel());
        link.setLinkUrl(customContentLink.getLinkUrl());
    }

    private void checkPermission(String contentKey) throws NoAdminPermissionException {
        String userName = this.userManager.getRemoteUsername();
        try {
            Project p = this.projectManager.getProjectByKey(contentKey);
            if (!this.projectPermissionManager.canAdminister(p, userName)) {
                throw new NoAdminPermissionException(userName, contentKey, null);
            }
        }
        catch (ProjectNotFoundException e) {
            throw new NoAdminPermissionException(userName, contentKey, (Throwable)e);
        }
    }

    public List<CustomContentLink> getCustomContentLinks(final String key) {
        List links = (List)this.ao.executeInTransaction((TransactionCallback)new TransactionCallback<List<CustomContentLink>>(){

            public List<CustomContentLink> doInTransaction() {
                return Lists.newArrayList((Iterable)Lists.transform((List)DefaultCustomContentLinkService.this.getAOsByKey(key), DefaultCustomContentLinkService.this.aoToEntityTransformer));
            }
        });
        return links;
    }

    private List<CustomContentLinkAO> getAOsByKey(String key) {
        return Arrays.asList(this.ao.find(CustomContentLinkAO.class, Query.select().where("CONTENT_KEY = ?", new Object[]{key}).order("SEQUENCE asc")));
    }

    public List<CustomContentLink> getPluginCustomContentLinks(String key) {
        ArrayList<CustomContentLink> links = new ArrayList<CustomContentLink>();
        List moduleDescriptors = this.pluginAccessor.getEnabledModuleDescriptorsByClass(CustomContentLinkProviderModuleDescriptor.class);
        for (CustomContentLinkProviderModuleDescriptor descriptor : moduleDescriptors) {
            try {
                links.addAll(descriptor.getModule().getCustomContentLinks(key));
            }
            catch (Exception ex) {
                log.warn("Error getting custom content links using CustomContentLinkProviderModule: {} with content link key: {}", (Object)descriptor.getModule(), (Object)key);
                log.debug("Stack trace:", (Throwable)ex);
            }
        }
        return links;
    }

    private CustomContentLinkAO[] getMatchingCustomContentLink(CustomContentLink entity) {
        return (CustomContentLinkAO[])this.ao.find(CustomContentLinkAO.class, Query.select().where("CONTENT_KEY = ? AND LINK_URL = ? and LINK_LABEL = ?", new Object[]{entity.getContentKey(), entity.getLinkUrl(), entity.getLinkLabel()}));
    }

    public void removeCustomContentLink(final CustomContentLink customContentLink) throws NoAdminPermissionException {
        this.checkPermission(customContentLink.getContentKey());
        this.ao.executeInTransaction((TransactionCallback)new TransactionCallback<Boolean>(){

            public Boolean doInTransaction() {
                try {
                    CustomContentLinkAO[] results = DefaultCustomContentLinkService.this.getMatchingCustomContentLink(customContentLink);
                    if (results.length > 0) {
                        DefaultCustomContentLinkService.this.ao.delete(new RawEntity[]{results[0]});
                        Boolean bl = true;
                        return bl;
                    }
                    Boolean bl = false;
                    return bl;
                }
                finally {
                    DefaultCustomContentLinkService.this.reSequence(customContentLink.getContentKey());
                }
            }
        });
    }

    public CustomContentLink getById(int id) {
        CustomContentLinkAO link = this.getAOById(id);
        return link == null ? null : (CustomContentLink)this.aoToEntityTransformer.apply((Object)link);
    }

    public void removeById(int id) throws NoAdminPermissionException {
        final CustomContentLinkAO link = this.getAOById(id);
        if (link != null) {
            this.checkPermission(link.getContentKey());
            this.ao.executeInTransaction((TransactionCallback)new TransactionCallback<Void>(){

                public Void doInTransaction() {
                    DefaultCustomContentLinkService.this.ao.delete(new RawEntity[]{link});
                    DefaultCustomContentLinkService.this.reSequence(link.getContentKey());
                    return null;
                }
            });
        }
    }

    public void update(final CustomContentLink newValue) throws NoAdminPermissionException {
        final CustomContentLinkAO link = this.getAOById(newValue.getId());
        if (link != null) {
            this.checkPermission(link.getContentKey());
            this.ao.executeInTransaction((TransactionCallback)new TransactionCallback<Void>(){

                public Void doInTransaction() {
                    DefaultCustomContentLinkService.this.copyToAO(newValue, link);
                    link.save();
                    return null;
                }
            });
        }
    }

    public void moveAfter(int idToMove, int idToComeAfter) throws NoAdminPermissionException {
        CustomContentLinkAO linkToMove = this.getAOById(idToMove);
        CustomContentLinkAO linkToComeAfter = this.getAOById(idToComeAfter);
        if (!linkToComeAfter.getContentKey().equals(linkToMove.getContentKey())) {
            throw new IllegalArgumentException("Tried to move link " + linkToMove + " after " + linkToComeAfter + ", content keys differ.");
        }
        this.moveToIndex(linkToMove, linkToComeAfter.getSequence() + 1);
    }

    private void moveToIndex(CustomContentLinkAO linkToMove, int indexToMoveTo) throws NoAdminPermissionException {
        this.checkPermission(linkToMove.getContentKey());
        ArrayList<CustomContentLinkAO> links = new ArrayList<CustomContentLinkAO>(this.getAOsByKey(linkToMove.getContentKey()));
        int indexOfMovingLink = links.indexOf(linkToMove);
        if (indexToMoveTo > indexOfMovingLink) {
            --indexToMoveTo;
        }
        links.remove(linkToMove);
        if (indexToMoveTo >= links.size()) {
            links.add(linkToMove);
        } else {
            links.add(indexToMoveTo, linkToMove);
        }
        this.reSequenceList(links);
    }

    private void reSequenceList(List<CustomContentLinkAO> links) {
        int sequence = 0;
        for (CustomContentLinkAO link : links) {
            link.setSequence(sequence++);
            link.save();
        }
    }

    public void moveToStart(int id) throws NoAdminPermissionException {
        this.moveToIndex(this.getAOById(id), 0);
    }

    private void reSequence(String contentKey) {
        this.reSequenceList(this.getAOsByKey(contentKey));
    }

    private int getNextSequence(String key) {
        List<CustomContentLinkAO> links = this.getAOsByKey(key);
        Integer maxSequence = null;
        for (CustomContentLinkAO link : links) {
            if (maxSequence != null && link.getSequence() <= maxSequence) continue;
            maxSequence = link.getSequence();
        }
        return maxSequence == null ? 0 : maxSequence + 1;
    }

    private CustomContentLinkAO getAOById(int id) {
        return (CustomContentLinkAO)this.ao.get(CustomContentLinkAO.class, (Object)id);
    }
}

