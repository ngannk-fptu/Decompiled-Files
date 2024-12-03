/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.confluence.api.service.accessmode.AccessModeService
 *  com.atlassian.confluence.api.service.exceptions.ReadOnlyException
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.support.TransactionSynchronization
 *  org.springframework.transaction.support.TransactionSynchronizationAdapter
 *  org.springframework.transaction.support.TransactionSynchronizationManager
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.like;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.api.service.accessmode.AccessModeService;
import com.atlassian.confluence.api.service.exceptions.ReadOnlyException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.like.AbstractLikeEvent;
import com.atlassian.confluence.event.events.like.AsyncLikeCreatedEvent;
import com.atlassian.confluence.event.events.like.AsyncLikeRemovedEvent;
import com.atlassian.confluence.event.events.like.LikeCreatedEvent;
import com.atlassian.confluence.event.events.like.LikeRemovedEvent;
import com.atlassian.confluence.like.Like;
import com.atlassian.confluence.like.LikeEntity;
import com.atlassian.confluence.like.LikeEntityDao;
import com.atlassian.confluence.like.LikeManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.access.AccessStatus;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

public class DefaultLikeManager
implements LikeManager {
    private final PlatformTransactionManager transactionManager;
    private final LikeEntityDao dao;
    private final EventPublisher publisher;
    private final PermissionManager permissionManager;
    private final ConfluenceAccessManager confluenceAccessManager;
    private final ContentEntityManager contentEntityManager;
    private final AccessModeService accessModeService;

    public DefaultLikeManager(PlatformTransactionManager transactionManager, LikeEntityDao dao, EventPublisher publisher, PermissionManager permissionManager, ConfluenceAccessManager confluenceAccessManager, ContentEntityManager contentEntityManager, AccessModeService accessModeService) {
        this.transactionManager = transactionManager;
        this.dao = dao;
        this.publisher = publisher;
        this.permissionManager = permissionManager;
        this.confluenceAccessManager = confluenceAccessManager;
        this.contentEntityManager = contentEntityManager;
        this.accessModeService = accessModeService;
    }

    @Override
    public Like addLike(ContentEntityObject contentEntity, final User user) {
        this.validateAccessMode();
        final long contentId = contentEntity.getId();
        if (contentId <= 0L) {
            return null;
        }
        if (user == null || StringUtils.isBlank((CharSequence)user.getName())) {
            return null;
        }
        if (!this.userCanLikeContent(user, contentEntity)) {
            return null;
        }
        if (this.hasLike(contentEntity, user)) {
            return null;
        }
        LikeEntity likeEntity = this.dao.addLike(contentEntity, user);
        TransactionSynchronizationManager.registerSynchronization((TransactionSynchronization)new TransactionSynchronizationAdapter(){

            public void afterCommit() {
                DefaultLikeManager.this.publish(new LikeCreatedEvent((Object)this, user, DefaultLikeManager.this.contentEntityManager.getById(contentId)));
            }
        });
        this.publisher.publish((Object)new AsyncLikeCreatedEvent(this, user, contentId));
        return new Like(likeEntity);
    }

    @Override
    public void removeLike(ContentEntityObject contentEntity, final User user) {
        this.validateAccessMode();
        final long contentId = contentEntity.getId();
        if (contentId <= 0L) {
            return;
        }
        if (user == null || StringUtils.isBlank((CharSequence)user.getName())) {
            return;
        }
        if (!this.userCanLikeContent(user, contentEntity)) {
            return;
        }
        if (!this.hasLike(contentEntity, user)) {
            return;
        }
        this.dao.removeLike(contentEntity, user);
        TransactionSynchronizationManager.registerSynchronization((TransactionSynchronization)new TransactionSynchronizationAdapter(){

            public void afterCommit() {
                DefaultLikeManager.this.publish(new LikeRemovedEvent((Object)this, user, DefaultLikeManager.this.contentEntityManager.getById(contentId)));
            }
        });
        this.publisher.publish((Object)new AsyncLikeRemovedEvent(this, user, contentId));
    }

    @Override
    public void removeAllLikesOn(ContentEntityObject contentEntity) {
        this.validateAccessMode();
        if (contentEntity == null || contentEntity.getId() <= 0L) {
            return;
        }
        this.dao.removeAllLikesOn(contentEntity);
    }

    @Override
    public void removeAllLikesFor(String username) {
        this.validateAccessMode();
        if (StringUtils.isBlank((CharSequence)username)) {
            return;
        }
        this.dao.removeAllLikesFor(username);
    }

    @Override
    public void removeAllLikesFor(@NonNull UserKey key) {
        this.validateAccessMode();
        this.dao.removeAllLikesFor(key);
    }

    private boolean userCanLikeContent(User user, ContentEntityObject contentEntity) {
        AccessStatus accessStatus = this.confluenceAccessManager.getUserAccessStatus(user);
        return this.permissionManager.hasPermission(user, Permission.VIEW, contentEntity) && accessStatus.hasLicensedAccess();
    }

    @Override
    public boolean hasLike(ContentEntityObject contentEntity, User user) {
        if (contentEntity == null || contentEntity.getId() <= 0L) {
            return false;
        }
        return user != null && !StringUtils.isBlank((CharSequence)user.getName()) && this.dao.hasLike(contentEntity, user);
    }

    @Override
    public List<Like> getLikes(ContentEntityObject contentEntity) {
        if (contentEntity == null || contentEntity.getId() <= 0L) {
            return Collections.emptyList();
        }
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, contentEntity)) {
            return Collections.emptyList();
        }
        return this.dao.getLikeEntities(Collections.singletonList(contentEntity)).stream().map(Like::new).collect(Collectors.toList());
    }

    @Override
    public Map<Long, List<Like>> getLikes(Collection<? extends ContentEntityObject> contentEntities) {
        if (contentEntities.isEmpty()) {
            return Collections.emptyMap();
        }
        Iterator<? extends ContentEntityObject> iterator = contentEntities.iterator();
        while (iterator.hasNext()) {
            ContentEntityObject entityObject = iterator.next();
            if (this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, entityObject)) continue;
            iterator.remove();
        }
        HashMap<Long, List<Like>> contentIdToLikesMap = new HashMap<Long, List<Like>>();
        this.dao.getLikeEntities(contentEntities).stream().map(Like::new).forEach(like -> {
            LinkedList<Like> likes = (LinkedList<Like>)contentIdToLikesMap.get(like.getContentId());
            if (likes == null) {
                likes = new LinkedList<Like>();
                contentIdToLikesMap.put(like.getContentId(), likes);
            }
            likes.add((Like)like);
        });
        return contentIdToLikesMap;
    }

    @Override
    public Map<Searchable, Integer> countLikes(Collection<? extends Searchable> searchables) {
        return this.dao.countLikes(searchables);
    }

    @Override
    public int countLikes(Searchable searchable) {
        return this.dao.countLikes(searchable);
    }

    private void publish(AbstractLikeEvent event) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(this.transactionManager);
        transactionTemplate.setPropagationBehavior(3);
        transactionTemplate.execute(status -> {
            this.publisher.publish((Object)event);
            return null;
        });
    }

    private void validateAccessMode() throws ServiceException {
        if (this.accessModeService.shouldEnforceReadOnlyAccess()) {
            throw new ReadOnlyException();
        }
    }
}

