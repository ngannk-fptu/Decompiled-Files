/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.user.Entity
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.User
 *  com.atlassian.user.properties.PropertySetFactory
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableMap
 *  com.opensymphony.module.propertyset.PropertySet
 *  com.opensymphony.module.propertyset.PropertySetManager
 */
package com.atlassian.confluence.user;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.DebugLoggingPropertySet;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.user.Entity;
import com.atlassian.user.EntityException;
import com.atlassian.user.User;
import com.atlassian.user.properties.PropertySetFactory;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetManager;
import java.util.Map;
import java.util.function.Function;

public class ConfluenceUserPropertySetFactory
implements PropertySetFactory {
    private static final String HIBERNATE_PROPERTY_SET = "hibernate";
    public static final String PROPERTY_PREFIX = "USERPROPS-";

    public PropertySet getPropertySet(Entity entity) throws EntityException {
        return this.getPropertySet(entity, args -> PropertySetManager.getInstance((String)HIBERNATE_PROPERTY_SET, (Map)args));
    }

    @VisibleForTesting
    @Internal
    PropertySet getPropertySet(Entity entity, Function<Map<String, Object>, PropertySet> propertySetSupplier) {
        if (!(entity instanceof User)) {
            throw new UnsupportedOperationException("This implementation only supports ConfluenceUser properties");
        }
        ConfluenceUser user = FindUserHelper.getUser((User)entity);
        if (user == null) {
            throw new UnsupportedOperationException("This implementation only supports ConfluenceUser properties");
        }
        ImmutableMap args = ImmutableMap.builder().put((Object)"entityId", (Object)0L).put((Object)"entityName", (Object)(PROPERTY_PREFIX + user.getKey())).build();
        return new DebugLoggingPropertySet(propertySetSupplier.apply((Map<String, Object>)args));
    }
}

