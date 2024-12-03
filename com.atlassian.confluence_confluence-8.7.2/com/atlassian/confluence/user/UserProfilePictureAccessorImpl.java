/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugins.avatar.Avatar
 *  com.atlassian.plugins.avatar.AvatarOwner
 *  com.atlassian.user.User
 *  com.atlassian.user.properties.PropertySetFactory
 *  com.opensymphony.module.propertyset.PropertySet
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.support.DefaultTransactionDefinition
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.user;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.user.UserProfilePictureUpdateEvent;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.user.ConfluenceLocalAvatarFactory;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.confluence.user.ThreadLocalProfilePictureCache;
import com.atlassian.confluence.user.UserProfilePictureAccessor;
import com.atlassian.confluence.user.UserPropertySetAccessor;
import com.atlassian.confluence.user.actions.ProfilePictureInfo;
import com.atlassian.confluence.user.avatar.AvatarProviderAccessor;
import com.atlassian.confluence.user.avatar.ConfluenceAvatarOwner;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugins.avatar.Avatar;
import com.atlassian.plugins.avatar.AvatarOwner;
import com.atlassian.user.User;
import com.atlassian.user.properties.PropertySetFactory;
import com.opensymphony.module.propertyset.PropertySet;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

final class UserProfilePictureAccessorImpl
implements UserProfilePictureAccessor {
    private final AvatarProviderAccessor avatarProviderAccessor;
    private final PlatformTransactionManager transactionManager;
    private final UserPropertySetAccessor propertySetAccessor;
    private final EventPublisher eventPublisher;
    private final ConfluenceLocalAvatarFactory localAvatarFactory;

    public UserProfilePictureAccessorImpl(AvatarProviderAccessor avatarProviderAccessor, PlatformTransactionManager transactionManager, PropertySetFactory propertySetFactory, EventPublisher eventPublisher, ConfluenceLocalAvatarFactory localAvatarFactory) {
        this.avatarProviderAccessor = Objects.requireNonNull(avatarProviderAccessor);
        this.transactionManager = Objects.requireNonNull(transactionManager);
        this.propertySetAccessor = new UserPropertySetAccessor(propertySetFactory);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.localAvatarFactory = Objects.requireNonNull(localAvatarFactory);
    }

    @Override
    public ProfilePictureInfo getUserProfilePicture(@Nullable User user) {
        ProfilePictureInfo cachedProfilePictureInfo = ThreadLocalProfilePictureCache.hasProfilePicture(user);
        if (cachedProfilePictureInfo != null) {
            return cachedProfilePictureInfo;
        }
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        definition.setPropagationBehavior(0);
        return (ProfilePictureInfo)new TransactionTemplate(this.transactionManager, (TransactionDefinition)definition).execute(transactionStatus -> {
            ProfilePictureInfo profilePictureInfo = this.getLogoForUser(user);
            ThreadLocalProfilePictureCache.cacheHasProfilePicture(user, profilePictureInfo);
            return profilePictureInfo;
        });
    }

    ProfilePictureInfo getLogoForUser(@Nullable User user) {
        Avatar avatar = this.avatarProviderAccessor.getAvatarProvider().getAvatar((AvatarOwner)new ConfluenceAvatarOwner(user), this.localAvatarFactory::getConfluenceLocalAvatar, 48);
        return new ProfilePictureInfo(avatar);
    }

    void setUserProfilePicture(User user, String imagePath) {
        PropertySet propertySet = this.propertySetAccessor.getPropertySet(user);
        propertySet.setString("confluence.user.profile.picture", imagePath);
        this.eventPublisher.publish((Object)new UserProfilePictureUpdateEvent((Object)this, user, imagePath));
    }

    void setUserProfilePicture(User user, Attachment attachment) {
        ContentEntityObject container = attachment.getContainer();
        if (!(container instanceof PersonalInformation)) {
            throw new IllegalArgumentException("Attachment does not belong to a personal information object. Attachment owner is: " + container);
        }
        PersonalInformation pi = (PersonalInformation)container;
        if (!pi.belongsTo(user)) {
            throw new IllegalArgumentException("Profile picture belongs to a different user. Needed user: " + user + " attachment belongs to " + pi.getUsername());
        }
        PropertySet propertySet = this.propertySetAccessor.getPropertySet(user);
        propertySet.setString("confluence.user.profile.picture", attachment.getFileName());
        this.eventPublisher.publish((Object)new UserProfilePictureUpdateEvent((Object)this, user, attachment));
    }
}

