/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Attributes
 *  com.atlassian.crowd.exception.GroupNotFoundException
 *  com.atlassian.crowd.manager.application.ApplicationService
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupWithAttributes
 *  com.atlassian.plugins.rest.common.Link
 *  org.apache.commons.lang3.Validate
 */
package com.atlassian.crowd.plugin.rest.util;

import com.atlassian.crowd.embedded.api.Attributes;
import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.manager.application.ApplicationService;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupWithAttributes;
import com.atlassian.crowd.plugin.rest.entity.GroupEntity;
import com.atlassian.crowd.plugin.rest.util.EntityTranslator;
import com.atlassian.crowd.plugin.rest.util.LinkUriHelper;
import com.atlassian.plugins.rest.common.Link;
import org.apache.commons.lang3.Validate;

public class GroupEntityUtil {
    private GroupEntityUtil() {
    }

    public static GroupEntity translate(Group group, Link oldLink) {
        Link updatedLink = LinkUriHelper.updateUserLink(oldLink, group.getName());
        return EntityTranslator.toGroupEntity(group, updatedLink);
    }

    public static GroupEntity expandGroup(ApplicationService applicationService, Application application, GroupEntity minimalGroupEntity, boolean expandAttributes) throws GroupNotFoundException {
        GroupEntity expandedGroup;
        Validate.notNull((Object)applicationService);
        Validate.notNull((Object)application);
        Validate.notNull((Object)minimalGroupEntity);
        Validate.notNull((Object)minimalGroupEntity.getName(), (String)"Minimal group entity must include a group name", (Object[])new Object[0]);
        Validate.notNull((Object)minimalGroupEntity.getLink(), (String)"Minimal group entity must include a link", (Object[])new Object[0]);
        String groupName = minimalGroupEntity.getName();
        Link groupLink = minimalGroupEntity.getLink();
        if (expandAttributes) {
            GroupWithAttributes group = applicationService.findGroupWithAttributesByName(application, groupName);
            Link updatedLink = LinkUriHelper.updateGroupLink(groupLink, group.getName());
            expandedGroup = EntityTranslator.toGroupEntity((Group)group, (Attributes)group, updatedLink);
        } else {
            Group group = applicationService.findGroupByName(application, groupName);
            Link updatedLink = LinkUriHelper.updateGroupLink(groupLink, group.getName());
            expandedGroup = EntityTranslator.toGroupEntity(group, updatedLink);
        }
        return expandedGroup;
    }
}

