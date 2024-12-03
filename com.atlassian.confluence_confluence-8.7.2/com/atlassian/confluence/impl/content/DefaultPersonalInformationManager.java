/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.confluence.api.service.retention.RetentionFeatureChecker
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  com.google.common.base.Preconditions
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.hibernate.SessionFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.content;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.api.service.retention.RetentionFeatureChecker;
import com.atlassian.confluence.audit.AuditingContext;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SaveContext;
import com.atlassian.confluence.core.persistence.ContentEntityObjectDao;
import com.atlassian.confluence.event.events.content.user.PersonalInformationCreateEvent;
import com.atlassian.confluence.event.events.content.user.PersonalInformationRemoveEvent;
import com.atlassian.confluence.event.events.content.user.PersonalInformationUpdateEvent;
import com.atlassian.confluence.impl.content.DefaultContentEntityManager;
import com.atlassian.confluence.internal.relations.RelationManager;
import com.atlassian.confluence.setup.settings.CollaborativeEditingHelper;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.confluence.user.PersonalInformationManager;
import com.atlassian.confluence.user.persistence.dao.PersonalInformationDao;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.confluence.xhtml.api.WikiToStorageConverter;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import com.google.common.base.Preconditions;
import java.util.List;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class DefaultPersonalInformationManager
extends DefaultContentEntityManager
implements PersonalInformationManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultPersonalInformationManager.class);
    private final PersonalInformationDao personalInformationDao;

    public DefaultPersonalInformationManager(ContentEntityObjectDao<ContentEntityObject> contentEntityObjectDao, SessionFactory sessionFactory, WikiToStorageConverter wikiToStorageConverter, EventPublisher eventPublisher, PersonalInformationDao personalInformationDao, RelationManager relationManager, CollaborativeEditingHelper collaborativeEditingHelper, AuditingContext auditingContext, RetentionFeatureChecker retentionFeatureChecker) {
        super(contentEntityObjectDao, sessionFactory, wikiToStorageConverter, eventPublisher, relationManager, collaborativeEditingHelper, auditingContext, retentionFeatureChecker, DefaultPersonalInformationManager.eventFactory());
        this.personalInformationDao = (PersonalInformationDao)Preconditions.checkNotNull((Object)personalInformationDao);
    }

    @Override
    public @NonNull PersonalInformation getOrCreatePersonalInformation(User user) {
        Preconditions.checkNotNull((Object)user);
        ConfluenceUser confluenceUser = FindUserHelper.getUser(user);
        if (confluenceUser == null) {
            throw new NullPointerException("Unable to find user mapping for " + user.getName());
        }
        PersonalInformation result = this.personalInformationDao.getByUser(confluenceUser);
        if (result == null) {
            log.debug("Creating PersonalInformation for {}", (Object)user.getName());
            result = new PersonalInformation();
            result.setUser(confluenceUser);
            this.saveContentEntity(result, null);
        }
        return result;
    }

    @Override
    public @NonNull PersonalInformation createPersonalInformation(User user) {
        return this.getOrCreatePersonalInformation(user);
    }

    @Override
    public void savePersonalInformation(PersonalInformation newInfo, @Nullable PersonalInformation oldInfo) {
        this.saveContentEntity(newInfo, oldInfo, null);
    }

    @Override
    public void savePersonalInformation(User user, String newInfoString, String fullName) {
        PersonalInformation current = this.getOrCreatePersonalInformation(user);
        this.saveNewVersion(current, personalInformation -> personalInformation.setBodyAsString(newInfoString.trim()));
    }

    @Override
    public void removePersonalInformation(@Nullable ConfluenceUser user) {
        if (user == null) {
            return;
        }
        List<PersonalInformation> infos = this.personalInformationDao.getAllByUser(user);
        if (infos.isEmpty()) {
            log.debug("No PersonalInformation found for user '" + user.getName() + "'; Nothing to remove.");
            return;
        }
        infos.stream().forEach(this::removeContentEntity);
    }

    private static DefaultContentEntityManager.EventFactory eventFactory() {
        return new DefaultContentEntityManager.EventFactory(){

            @Override
            public Optional<?> newCreateEvent(Object source, ContentEntityObject obj, @Nullable SaveContext saveContext) {
                return Optional.of(new PersonalInformationCreateEvent(source, (PersonalInformation)obj, saveContext));
            }

            @Override
            public Optional<?> newUpdateEvent(Object source, ContentEntityObject object, @Nullable ContentEntityObject origObject, @Nullable SaveContext saveContext) {
                PersonalInformation updated = (PersonalInformation)object;
                PersonalInformation original = (PersonalInformation)origObject;
                boolean suppressNotifications = saveContext != null && saveContext.isSuppressNotifications();
                return Optional.of(new PersonalInformationUpdateEvent(source, updated, original, suppressNotifications));
            }

            @Override
            public Optional<?> newRemoveEvent(Object source, ContentEntityObject obj) {
                return Optional.of(new PersonalInformationRemoveEvent(source, (PersonalInformation)obj));
            }
        };
    }

    @Override
    public boolean hasPersonalInformation(@Nullable String username) {
        ConfluenceUser user = FindUserHelper.getUserByUsername(username);
        return user != null && this.personalInformationDao.getByUser(user) != null;
    }

    @Override
    public boolean hasPersonalInformation(@Nullable UserKey userKey) {
        ConfluenceUser user = FindUserHelper.getUserByUserKey(userKey);
        return user != null && this.personalInformationDao.getByUser(user) != null;
    }
}

