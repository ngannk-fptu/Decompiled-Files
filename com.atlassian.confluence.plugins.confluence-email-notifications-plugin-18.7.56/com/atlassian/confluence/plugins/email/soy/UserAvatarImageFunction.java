/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.DataSourceFactory
 *  com.atlassian.confluence.mail.embed.MimeBodyPartRecorder
 *  com.atlassian.confluence.mail.embed.MimeBodyPartReference
 *  com.atlassian.confluence.notifications.SystemUser
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.soy.renderer.SoyServerFunction
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableSet
 *  javax.activation.DataSource
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.email.soy;

import com.atlassian.confluence.core.DataSourceFactory;
import com.atlassian.confluence.mail.embed.MimeBodyPartRecorder;
import com.atlassian.confluence.mail.embed.MimeBodyPartReference;
import com.atlassian.confluence.notifications.SystemUser;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.soy.renderer.SoyServerFunction;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import javax.activation.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserAvatarImageFunction
implements SoyServerFunction<String> {
    private static final Logger log = LoggerFactory.getLogger(UserAvatarImageFunction.class);
    private static final ImmutableSet<Integer> ARG_SIZES = ImmutableSet.of((Object)1);
    private final DataSourceFactory dataSourceFactory;
    private final MimeBodyPartRecorder bodyPartRecorder;
    private final UserAccessor userAccessor;

    public UserAvatarImageFunction(DataSourceFactory dataSourceFactory, MimeBodyPartRecorder bodyPartRecorder, UserAccessor userAccessor) {
        this.dataSourceFactory = dataSourceFactory;
        this.bodyPartRecorder = bodyPartRecorder;
        this.userAccessor = userAccessor;
    }

    public String apply(Object ... args) {
        DataSource avatarDataSource;
        Object arg = args[0];
        if (arg instanceof UserKey) {
            arg = this.userAccessor.getUserByKey((UserKey)arg);
        }
        if (arg instanceof ConfluenceUser) {
            ConfluenceUser user = (ConfluenceUser)arg;
            avatarDataSource = this.dataSourceFactory.getAvatar((User)user);
            log.debug("using avatar datasource {} for user {}[{}]", new Object[]{avatarDataSource.getName(), user.getName(), user.getKey().getStringValue()});
        } else if (arg instanceof SystemUser) {
            avatarDataSource = this.dataSourceFactory.getAvatar(null);
            log.debug("using avatar datasource {} for system user", (Object)avatarDataSource.getName());
        } else {
            this.checkType(arg);
            avatarDataSource = this.dataSourceFactory.getAvatar(null);
            log.debug("using avatar datasource {} for null user", (Object)avatarDataSource.getName());
        }
        return ((MimeBodyPartReference)this.bodyPartRecorder.track(avatarDataSource).get()).getLocator().toASCIIString();
    }

    private void checkType(Object arg) {
        if (arg != null) {
            throw new IllegalArgumentException("argument 0 is not of type 'ConfluenceUser' or 'UserKey' in 'avatarImage' soy function: " + arg.getClass().getName());
        }
    }

    public String getName() {
        return "avatarImage";
    }

    public Set<Integer> validArgSizes() {
        return ARG_SIZES;
    }
}

