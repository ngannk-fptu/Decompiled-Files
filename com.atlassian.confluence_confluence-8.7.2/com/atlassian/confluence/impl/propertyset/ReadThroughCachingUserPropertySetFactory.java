/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.Entity
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.User
 *  com.atlassian.user.properties.PropertySetFactory
 *  com.opensymphony.module.propertyset.PropertySet
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.propertyset;

import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.cache.CoreCache;
import com.atlassian.confluence.impl.cache.ReadThroughAtlassianCache;
import com.atlassian.confluence.impl.cache.ReadThroughCache;
import com.atlassian.confluence.impl.propertyset.ReadThroughCachingPropertySet;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.Entity;
import com.atlassian.user.EntityException;
import com.atlassian.user.User;
import com.atlassian.user.properties.PropertySetFactory;
import com.opensymphony.module.propertyset.PropertySet;
import java.util.Optional;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ReadThroughCachingUserPropertySetFactory
implements PropertySetFactory {
    private static final Logger log = LoggerFactory.getLogger(ReadThroughCachingUserPropertySetFactory.class);
    private final PropertySetFactory delegate;
    private final ReadThroughCache<UserKey, PropertySet> propertySetCache;
    private final BiFunction<UserKey, PropertySet, PropertySet> decorator;

    ReadThroughCachingUserPropertySetFactory(PropertySetFactory delegate, ReadThroughCache<UserKey, PropertySet> propertySetCache, BiFunction<UserKey, PropertySet, PropertySet> decorator) {
        this.delegate = delegate;
        this.propertySetCache = propertySetCache;
        this.decorator = decorator;
    }

    public static ReadThroughCachingUserPropertySetFactory create(PropertySetFactory delegate, CacheFactory cacheFactory) {
        return new ReadThroughCachingUserPropertySetFactory(delegate, ReadThroughAtlassianCache.create(cacheFactory, CoreCache.USER_PROPERTY_SET_REFERENCES), (user, propertySet) -> ReadThroughCachingPropertySet.create(propertySet, user, cacheFactory));
    }

    @Nullable
    public PropertySet getPropertySet(Entity entity) {
        return Optional.ofNullable(entity).map(User.class::cast).map(FindUserHelper::getUser).map(this::getPropertySet).orElse(null);
    }

    private PropertySet getPropertySet(ConfluenceUser user) {
        return this.propertySetCache.get(user.getKey(), () -> this.createUserPropertySet(user));
    }

    private PropertySet createUserPropertySet(ConfluenceUser user) {
        return this.decorator.apply(user.getKey(), this.getDelegatePropertySet((Entity)user));
    }

    private PropertySet getDelegatePropertySet(Entity user) {
        try {
            log.debug("Creating PropertySet for {}", (Object)user);
            return this.delegate.getPropertySet(user);
        }
        catch (EntityException e) {
            throw new RuntimeException(e);
        }
    }
}

