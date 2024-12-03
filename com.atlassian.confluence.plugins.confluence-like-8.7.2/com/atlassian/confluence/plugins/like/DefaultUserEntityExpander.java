/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContextPathHolder
 *  com.atlassian.confluence.links.linktypes.UserProfileLink
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.like;

import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.links.linktypes.UserProfileLink;
import com.atlassian.confluence.plugins.like.UserEntityExpander;
import com.atlassian.confluence.plugins.like.rest.entities.UserEntity;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.user.User;
import org.apache.commons.lang3.StringUtils;

public class DefaultUserEntityExpander
implements UserEntityExpander {
    private final UserAccessor userAccessor;
    private final ContextPathHolder contextPathHolder;

    public DefaultUserEntityExpander(UserAccessor userAccessor, ContextPathHolder contextPathHolder) {
        this.userAccessor = userAccessor;
        this.contextPathHolder = contextPathHolder;
    }

    @Override
    public UserEntity expand(UserEntity userEntity) {
        if (StringUtils.isBlank((CharSequence)userEntity.getName())) {
            throw new IllegalArgumentException("username must be specified at least to expand");
        }
        ConfluenceUser user = this.userAccessor.getUserByName(userEntity.getName());
        if (user != null) {
            if (StringUtils.isBlank((CharSequence)userEntity.getFullName())) {
                userEntity.setFullName(StringUtils.isNotBlank((CharSequence)user.getFullName()) ? user.getFullName() : user.getName());
            }
            if (StringUtils.isBlank((CharSequence)userEntity.getUrl())) {
                userEntity.setUrl(this.contextPathHolder.getContextPath() + UserProfileLink.getLinkPath((String)user.getName()));
            }
            if (StringUtils.isBlank((CharSequence)userEntity.getAvatarUrl())) {
                userEntity.setAvatarUrl(this.userAccessor.getUserProfilePicture((User)user).getUriReference());
            }
        }
        return userEntity;
    }
}

