/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Either
 *  com.atlassian.plugins.avatar.AbstractAvatar
 *  com.atlassian.plugins.avatar.Avatar
 *  com.atlassian.plugins.avatar.AvatarOwner
 *  com.atlassian.user.User
 *  com.atlassian.user.properties.PropertySetFactory
 *  com.opensymphony.module.propertyset.PropertySet
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user;

import com.atlassian.confluence.importexport.resource.ResourceAccessor;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.confluence.user.PersonalInformationManager;
import com.atlassian.confluence.user.UnknownUser;
import com.atlassian.confluence.user.UserPropertySetAccessor;
import com.atlassian.fugue.Either;
import com.atlassian.plugins.avatar.AbstractAvatar;
import com.atlassian.plugins.avatar.Avatar;
import com.atlassian.plugins.avatar.AvatarOwner;
import com.atlassian.user.User;
import com.atlassian.user.properties.PropertySetFactory;
import com.opensymphony.module.propertyset.PropertySet;
import java.io.InputStream;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ConfluenceLocalAvatarFactory {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceLocalAvatarFactory.class);
    private final UserPropertySetAccessor propertySetAccessor;
    private final PersonalInformationManager personalInformationManager;
    private final AttachmentManager attachmentManager;
    private final ResourceAccessor resourceAccessor;

    public ConfluenceLocalAvatarFactory(PropertySetFactory propertySetFactory, PersonalInformationManager personalInformationManager, AttachmentManager attachmentManager, ResourceAccessor resourceAccessor) {
        this.propertySetAccessor = new UserPropertySetAccessor(propertySetFactory);
        this.personalInformationManager = Objects.requireNonNull(personalInformationManager);
        this.attachmentManager = Objects.requireNonNull(attachmentManager);
        this.resourceAccessor = Objects.requireNonNull(resourceAccessor);
    }

    Avatar getConfluenceLocalAvatar(AvatarOwner<User> avatarOwner) {
        return new ConfluenceLocalAvatar(avatarOwner.getIdentifier(), this.getAvatarPathOrAttachment((User)avatarOwner.get()));
    }

    private Either<String, Attachment> getAvatarPathOrAttachment(@Nullable User user) {
        if (user == null || user instanceof UnknownUser) {
            return Either.left((Object)"/images/icons/profilepics/anonymous.svg");
        }
        PropertySet propertySet = this.propertySetAccessor.getPropertySet(user);
        if (propertySet == null) {
            log.debug("PropertySet not found for user " + user.getName());
            return Either.left((Object)"/images/icons/profilepics/default.svg");
        }
        String profilePicture = propertySet.getString("confluence.user.profile.picture");
        if (profilePicture == null) {
            log.debug("Property [{}] not found for user [{}]", (Object)"confluence.user.profile.picture", (Object)user.getName());
            return Either.left((Object)"/images/icons/profilepics/default.svg");
        }
        if (profilePicture.startsWith("/images/icons/profilepics/")) {
            return Either.left((Object)profilePicture);
        }
        PersonalInformation personalInformation = this.personalInformationManager.getOrCreatePersonalInformation(user);
        Attachment a = this.attachmentManager.getAttachment(personalInformation, profilePicture);
        if (a == null) {
            log.debug("Attachment [{}] for profile picture not found for user [{}]", (Object)profilePicture, (Object)user.getName());
            return Either.left((Object)"/images/icons/profilepics/default.svg");
        }
        return Either.right((Object)a);
    }

    private class ConfluenceLocalAvatar
    extends AbstractAvatar {
        private final Either<String, Attachment> pathOrAttachment;

        public ConfluenceLocalAvatar(String userEmail, Either<String, Attachment> pathOrAttachment) {
            super(userEmail, (String)pathOrAttachment.fold(path -> "image/png", Attachment::getMediaType), 48);
            this.pathOrAttachment = pathOrAttachment;
        }

        public String getUrl() {
            return (String)this.pathOrAttachment.fold(path -> path, Attachment::getDownloadPathWithoutVersionOrApiRevision);
        }

        public boolean isExternal() {
            return false;
        }

        public InputStream getBytes() {
            return (InputStream)this.pathOrAttachment.fold(ConfluenceLocalAvatarFactory.this.resourceAccessor::getResource, ConfluenceLocalAvatarFactory.this.attachmentManager::getAttachmentData);
        }

        public Avatar atSize(int size) {
            return this;
        }
    }
}

