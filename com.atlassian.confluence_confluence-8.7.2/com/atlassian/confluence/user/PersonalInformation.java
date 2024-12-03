/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.user;

import com.atlassian.confluence.core.BodyContent;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.HasLinkWikiMarkup;
import com.atlassian.confluence.links.linktypes.UserProfileLink;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.User;
import java.util.List;

public class PersonalInformation
extends ContentEntityObject
implements HasLinkWikiMarkup {
    public static final String CONTENT_TYPE = "userinfo";
    private ConfluenceUser user;
    private String hasPersonalSpace;

    public PersonalInformation() {
    }

    public PersonalInformation(ConfluenceUser user, String personalInformation) {
        this.user = user;
        this.setBodyAsString(personalInformation);
    }

    @Override
    public String toString() {
        return this.getType() + ": " + this.getUsername() + " v." + this.getVersion() + " (" + this.getId() + ")";
    }

    @Override
    public String getDisplayTitle() {
        return this.getFullName();
    }

    @Override
    public String getUrlPath() {
        return UserProfileLink.getLinkPath(this.user.getName());
    }

    @Override
    public String getAttachmentUrlPath(Attachment attachment) {
        this.ensureAttachmentBelongsToContent(attachment);
        if (attachment.isUserProfilePicture()) {
            return this.getUrlPath();
        }
        return super.getAttachmentUrlPath(attachment);
    }

    @Deprecated
    public String getUsername() {
        return this.user == null ? null : this.user.getName();
    }

    public ConfluenceUser getUser() {
        return this.user;
    }

    public void setUser(ConfluenceUser user) {
        this.user = user;
    }

    @Override
    public BodyType getDefaultBodyType() {
        return BodyType.WIKI;
    }

    @Override
    public List<BodyContent> getBodyContents() {
        return super.getBodyContents();
    }

    public boolean belongsTo(User user) {
        return user != null && this.user != null && this.user.getName().equalsIgnoreCase(user.getName());
    }

    @Deprecated
    public String getFullName() {
        return this.user == null ? null : this.user.getFullName();
    }

    @Deprecated
    public String getEmail() {
        return this.user == null ? null : this.user.getEmail();
    }

    @Override
    public String getType() {
        return CONTENT_TYPE;
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 29 + (this.user == null ? 0 : this.user.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        PersonalInformation that = (PersonalInformation)o;
        return !(this.user != null ? !this.user.equals(that.user) : that.user != null);
    }

    @Override
    public String getNameForComparison() {
        return this.getUsername();
    }

    public String getHasPersonalSpace() {
        if (this.hasPersonalSpace == null) {
            SpaceManager spaceManager = (SpaceManager)ContainerManager.getComponent((String)"spaceManager");
            this.hasPersonalSpace = String.valueOf(spaceManager.getPersonalSpace(this.user) != null);
        }
        return this.hasPersonalSpace;
    }

    @Override
    public String getLinkWikiMarkup() {
        return String.format("[~%s]", this.getUsername());
    }
}

