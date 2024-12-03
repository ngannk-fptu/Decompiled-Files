/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContextPathHolder
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceDescription
 *  com.atlassian.confluence.spaces.SpaceLogoManager
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.ia.impl;

import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.plugins.ia.model.SpaceBean;
import com.atlassian.confluence.plugins.ia.service.SpaceBeanFactory;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceDescription;
import com.atlassian.confluence.spaces.SpaceLogoManager;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.user.User;

public class DefaultSpaceBeanFactory
implements SpaceBeanFactory {
    private final SpaceLogoManager spaceLogoManager;
    private final ContextPathHolder contextPathHolder;

    public DefaultSpaceBeanFactory(SpaceLogoManager spaceLogoManager, ContextPathHolder contextPathHolder) {
        this.spaceLogoManager = spaceLogoManager;
        this.contextPathHolder = contextPathHolder;
    }

    @Override
    public SpaceBean createSpaceBean(Space space, User currentUser) {
        String homePath = space.getDeepLinkUri().toString();
        String logoUriReference = this.spaceLogoManager.getLogoUriReference(space, currentUser);
        String contextPath = this.contextPathHolder.getContextPath();
        SpaceDescription spaceDescription = space.getDescription();
        return new SpaceBean(space.getKey(), space.getName(), spaceDescription != null ? spaceDescription.getBodyAsString() : "", contextPath + homePath, logoUriReference, contextPath + this.getBrowseUrlPath(space), space.isPersonal(), currentUser != null && space.isPersonal() && space.getKey().equals("~" + currentUser.getName()));
    }

    private String getBrowseUrlPath(Space space) {
        return "/collector/pages.action?key=" + GeneralUtil.urlEncode((String)space.getKey());
    }
}

