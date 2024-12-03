/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package com.atlassian.confluence.plugins.macros.advanced.recentupdate;

import com.atlassian.confluence.plugins.macros.advanced.recentupdate.Updater;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.UserLink;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.UserProfileLink;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.i18n.I18NBean;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class DefaultUpdater
implements Updater {
    private final String username;
    private final I18NBean i18n;
    private UserProfileLink userProfileLink;
    private UserLink userLink;

    public DefaultUpdater(String username, I18NBean i18n) {
        this.username = username;
        this.i18n = i18n;
    }

    public DefaultUpdater(ConfluenceUser user, I18NBean i18n) {
        this.username = user != null ? user.getName() : null;
        this.i18n = i18n;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getLinkedProfilePicture() {
        if (this.userProfileLink == null) {
            this.userProfileLink = new UserProfileLink(this.username, this.i18n);
        }
        return this.userProfileLink.toString();
    }

    @Override
    public String getLinkedFullName() {
        if (this.userLink == null) {
            this.userLink = new UserLink(this.username, this.i18n);
        }
        return this.userLink.toString();
    }

    public int hashCode() {
        return new HashCodeBuilder().append((Object)this.username).toHashCode();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DefaultUpdater)) {
            return false;
        }
        DefaultUpdater that = (DefaultUpdater)obj;
        return new EqualsBuilder().append((Object)this.username, (Object)that.username).isEquals();
    }
}

