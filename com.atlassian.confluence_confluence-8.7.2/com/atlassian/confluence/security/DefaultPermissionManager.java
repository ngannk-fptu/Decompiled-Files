/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.user.Group
 *  com.atlassian.user.User
 *  org.hibernate.Hibernate
 */
package com.atlassian.confluence.security;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.content.Content;
import com.atlassian.confluence.content.ContentType;
import com.atlassian.confluence.content.ContentTypeManager;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.impl.content.ContentTypeModuleResolver;
import com.atlassian.confluence.impl.security.delegate.ScopesRequestCacheDelegate;
import com.atlassian.confluence.internal.accessmode.AccessModeManager;
import com.atlassian.confluence.internal.security.ThreadLocalPermissionsCacheInternal;
import com.atlassian.confluence.security.NoPermissionDelegate;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionCheckExemptions;
import com.atlassian.confluence.security.PermissionDelegate;
import com.atlassian.confluence.security.PermissionDelegateRegistry;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.access.AccessStatus;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.user.Group;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.hibernate.Hibernate;

public class DefaultPermissionManager
implements PermissionManager,
PermissionDelegateRegistry {
    private Map<String, PermissionDelegate> delegates = new HashMap<String, PermissionDelegate>();
    private PermissionCheckExemptions permissionCheckExemptions;
    private ContentTypeManager contentTypeManager;
    private ContentTypeModuleResolver contentTypeModuleResolver;
    private ConfluenceAccessManager confluenceAccessManager;
    private AccessModeManager accessModeManager;
    private ScopesRequestCacheDelegate scopesRequestCacheDelegate;

    @Override
    public boolean hasPermission(User user, Permission permission, Object target) {
        if (target == null) {
            return false;
        }
        if (this.scopesRequestCacheDelegate.hasPermission(permission, target) && this.isExempt(user) && this.permissionAllowedInReadOnlyAccessMode(permission)) {
            return true;
        }
        return this.hasPermissionNoExemptions(user, permission, target);
    }

    @VisibleForTesting
    boolean permissionAllowedInReadOnlyAccessMode(Permission permission) {
        return !this.accessModeManager.shouldEnforceReadOnlyAccess() || !permission.isMutative();
    }

    @Override
    public boolean hasPermission(User user, Permission permission, Class targetType) {
        if (!this.scopesRequestCacheDelegate.hasPermission(permission, (Object)targetType)) {
            return false;
        }
        if (targetType == null) {
            return false;
        }
        if (!Permission.VIEW.equals(permission)) {
            throw new UnsupportedOperationException("Only Permission.VIEW is supported.");
        }
        if (this.isExempt(user)) {
            return true;
        }
        if (!this.canUseConfluence(user)) {
            return false;
        }
        return this.findDelegateFor(targetType).canView(user);
    }

    @Override
    public boolean hasPermissionNoExemptions(User user, Permission permission, Object target) {
        if (!this.permissionAllowedInReadOnlyAccessMode(permission)) {
            return false;
        }
        if (!this.canUseConfluence(user)) {
            return false;
        }
        return this.scopesRequestCacheDelegate.hasPermission(permission, target) && permission.checkAgainst(this.findDelegateFor(target), user, target);
    }

    @Override
    public boolean hasCreatePermission(User user, Object container, Class<?> typeToCreate) {
        if (!this.scopesRequestCacheDelegate.hasPermission(Permission.EDIT, typeToCreate)) {
            return false;
        }
        if (this.accessModeManager.shouldEnforceReadOnlyAccess()) {
            return false;
        }
        if (this.isExempt(user)) {
            return true;
        }
        if (container == null || !this.canUseConfluence(user)) {
            return false;
        }
        return this.findDelegateFor(typeToCreate).canCreate(user, container);
    }

    @Override
    public boolean hasCreatePermission(User user, Object container, Object objectToCreate) {
        if (!this.scopesRequestCacheDelegate.hasPermission(Permission.EDIT, objectToCreate)) {
            return false;
        }
        if (this.accessModeManager.shouldEnforceReadOnlyAccess()) {
            return false;
        }
        if (this.isExempt(user)) {
            return true;
        }
        if (container == null || !this.canUseConfluence(user)) {
            return false;
        }
        return this.findDelegateFor(objectToCreate).canCreate(user, container);
    }

    private boolean isExempt(User user) {
        return this.permissionCheckExemptions.isExempt(user);
    }

    private boolean canUseConfluence(User user) {
        AccessStatus accessStatus = this.confluenceAccessManager.getUserAccessStatusNoExemptions(user);
        return accessStatus.canUseConfluence();
    }

    @Override
    public <X> List<X> getPermittedEntities(User user, Permission permission, List<? extends X> objects) {
        ArrayList<X> returnList = new ArrayList<X>(objects.size());
        for (X o : objects) {
            if (!this.hasPermission(user, permission, o)) continue;
            returnList.add(o);
        }
        return returnList;
    }

    @Override
    public <X> List<X> getPermittedEntities(User user, Permission permission, Iterator<? extends X> objects, int maxResults) {
        ArrayList<X> returnList = new ArrayList<X>(Math.min(100, maxResults));
        while (objects.hasNext() && returnList.size() < maxResults) {
            X o = objects.next();
            if (!this.hasPermission(user, permission, o)) continue;
            returnList.add(o);
        }
        return returnList;
    }

    @Override
    public <X> List<X> getPermittedEntities(User user, Permission permission, Iterator<X> entities, int maxResults, Collection<? extends PermissionManager.Criterion> otherCriteria) {
        ArrayList<X> permittedEntities = new ArrayList<X>();
        while (permittedEntities.size() <= maxResults && entities.hasNext()) {
            X originalEntity = entities.next();
            if (!this.hasPermission(user, permission, originalEntity) || !this.checkCriteria(otherCriteria, permittedEntities, originalEntity)) continue;
            permittedEntities.add(originalEntity);
        }
        return permittedEntities;
    }

    @Override
    public <X> List<X> getPermittedEntitiesNoExemptions(User user, Permission permission, List<? extends X> objects) {
        ArrayList<X> returnList = new ArrayList<X>(objects.size());
        for (X o : objects) {
            if (!this.hasPermissionNoExemptions(user, permission, o)) continue;
            returnList.add(o);
        }
        return returnList;
    }

    @Override
    public <X> List<X> getPermittedEntitiesNoExemptions(User user, Permission permission, Iterator<? extends X> objects, int maxResults) {
        ArrayList<X> returnList = new ArrayList<X>(Math.min(100, maxResults));
        while (objects.hasNext() && returnList.size() < maxResults) {
            X o = objects.next();
            if (!this.hasPermissionNoExemptions(user, permission, o)) continue;
            returnList.add(o);
        }
        return returnList;
    }

    @Override
    public <X> List<X> getPermittedEntitiesNoExemptions(User user, Permission permission, Iterator<X> entities, int maxResults, Collection<? extends PermissionManager.Criterion> otherCriteria) {
        ArrayList<X> permittedEntities = new ArrayList<X>();
        while (permittedEntities.size() <= maxResults && entities.hasNext()) {
            X originalEntity = entities.next();
            if (!this.hasPermissionNoExemptions(user, permission, originalEntity) || !this.checkCriteria(otherCriteria, permittedEntities, originalEntity)) continue;
            permittedEntities.add(originalEntity);
        }
        return permittedEntities;
    }

    @Override
    public boolean isConfluenceAdministrator(User user) {
        return this.scopesRequestCacheDelegate.hasPermission(Permission.ADMINISTER, TARGET_APPLICATION) && this.hasPermission(user, Permission.ADMINISTER, TARGET_APPLICATION);
    }

    @Override
    public boolean isSystemAdministrator(User user) {
        return this.scopesRequestCacheDelegate.hasPermission(Permission.ADMINISTER, TARGET_SYSTEM) && this.hasPermission(user, Permission.ADMINISTER, TARGET_SYSTEM);
    }

    @Override
    public void withExemption(Runnable runnable) {
        if (ThreadLocalPermissionsCacheInternal.hasTemporaryPermissionExemption()) {
            runnable.run();
        } else {
            ThreadLocalPermissionsCacheInternal.enableTemporaryPermissionExemption();
            try {
                runnable.run();
            }
            finally {
                ThreadLocalPermissionsCacheInternal.disableTemporaryPermissionExemption();
            }
        }
    }

    private boolean checkCriteria(Collection<? extends PermissionManager.Criterion> criteria, List<?> alreadyChosenEntities, Object entity) {
        if (criteria.size() == 0) {
            return true;
        }
        for (PermissionManager.Criterion criterion : criteria) {
            if (criterion.test(alreadyChosenEntities, entity)) continue;
            return false;
        }
        return true;
    }

    private PermissionDelegate findDelegateFor(Object target) {
        PermissionDelegate delegate = null;
        if (target instanceof Content) {
            target = ((Content)target).getEntity();
        }
        if (target instanceof CustomContentEntityObject) {
            String pluginModuleKey = ((CustomContentEntityObject)target).getPluginModuleKey();
            if (this.contentTypeModuleResolver != null) {
                return this.contentTypeModuleResolver.findContentType(pluginModuleKey).map(ContentType::getPermissionDelegate).orElseGet(NoPermissionDelegate::new);
            }
            if (this.contentTypeManager != null) {
                delegate = this.contentTypeManager.getContentType(pluginModuleKey).getPermissionDelegate();
            }
        } else {
            delegate = this.delegates.get(this.makeDelegatesKeyFor(target));
        }
        if (delegate == null) {
            throw new IllegalArgumentException("Could not check permissions for " + target + " no suitable delegate found.");
        }
        return delegate;
    }

    private String makeDelegatesKeyFor(Object target) {
        if (target instanceof String) {
            return (String)target;
        }
        if (target instanceof Class) {
            return this.getClassNameOnly((Class)target);
        }
        return this.getClassNameOnly(Hibernate.getClass((Object)target));
    }

    private String getClassNameOnly(Class<?> clazz) {
        if (User.class.isAssignableFrom(clazz)) {
            return "User";
        }
        if (Group.class.isAssignableFrom(clazz)) {
            return "Group";
        }
        return clazz.getSimpleName();
    }

    @Deprecated
    public void setDelegates(Map<String, PermissionDelegate> delegates) {
        this.delegates = delegates;
    }

    public void setPermissionCheckExemptions(PermissionCheckExemptions permissionCheckExemptions) {
        this.permissionCheckExemptions = permissionCheckExemptions;
    }

    @Deprecated
    public void setContentTypeManager(ContentTypeManager contentTypeManager) {
        this.contentTypeManager = contentTypeManager;
    }

    public void setContentTypeModuleResolver(ContentTypeModuleResolver contentTypeModuleResolver) {
        this.contentTypeModuleResolver = contentTypeModuleResolver;
    }

    public void setConfluenceAccessManager(ConfluenceAccessManager confluenceAccessManager) {
        this.confluenceAccessManager = confluenceAccessManager;
    }

    public void setAccessModeManager(AccessModeManager accessModeManager) {
        this.accessModeManager = accessModeManager;
    }

    public void setScopesRequestCacheDelegate(ScopesRequestCacheDelegate scopesRequestCache) {
        this.scopesRequestCacheDelegate = scopesRequestCache;
    }

    @Override
    public boolean hasMovePermission(User user, Object source, Object target, String movePoint) {
        if (!this.scopesRequestCacheDelegate.hasPermission(Permission.EDIT, source)) {
            return false;
        }
        if (this.accessModeManager.shouldEnforceReadOnlyAccess()) {
            return false;
        }
        if (this.isExempt(user)) {
            return true;
        }
        if (!this.canUseConfluence(user)) {
            return false;
        }
        return this.findDelegateFor(source).canMove(user, source, target, movePoint);
    }

    @Override
    public boolean hasRemoveHierarchyPermission(User user, Object target) {
        if (!this.scopesRequestCacheDelegate.hasPermission(Permission.REMOVE, target)) {
            return false;
        }
        if (target == null) {
            return false;
        }
        if (this.accessModeManager.shouldEnforceReadOnlyAccess()) {
            return false;
        }
        if (this.isExempt(user)) {
            return true;
        }
        if (!this.canUseConfluence(user)) {
            return false;
        }
        return this.findDelegateFor(target).canRemoveHierarchy(user, target);
    }

    @Override
    public void register(String key, PermissionDelegate<?> delegate) {
        this.delegates.put(key, delegate);
    }
}

