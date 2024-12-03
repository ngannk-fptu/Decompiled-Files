/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.crowd.embedded.api.UserWithAttributes
 *  com.atlassian.crowd.embedded.propertyset.DebugLoggingPropertySet
 *  com.atlassian.user.Entity
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.User
 *  com.atlassian.user.properties.PropertySetFactory
 *  com.opensymphony.module.propertyset.PropertySet
 *  com.opensymphony.module.propertyset.PropertySetManager
 *  com.opensymphony.module.propertyset.hibernate.HibernateConfigurationProvider
 */
package com.atlassian.crowd.embedded.atlassianuser;

import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.crowd.embedded.api.UserWithAttributes;
import com.atlassian.crowd.embedded.propertyset.DebugLoggingPropertySet;
import com.atlassian.user.Entity;
import com.atlassian.user.EntityException;
import com.atlassian.user.User;
import com.atlassian.user.properties.PropertySetFactory;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetManager;
import com.opensymphony.module.propertyset.hibernate.HibernateConfigurationProvider;
import java.util.HashMap;

@Deprecated
public class EmbeddedCrowdPropertySetFactory
implements PropertySetFactory {
    private static final String HIBERNATE_PROPERTY_SET = "hibernate";
    public static final String PROPERTY_PREFIX = "CWD_";
    private final CrowdService crowdService;
    private final HibernateConfigurationProvider configProvider;

    public EmbeddedCrowdPropertySetFactory(CrowdService crowdService, HibernateConfigurationProvider configProvider) {
        this.crowdService = crowdService;
        this.configProvider = configProvider;
    }

    public PropertySet getPropertySet(Entity entity) throws EntityException {
        if (!(entity instanceof User)) {
            throw new UnsupportedOperationException("This implementation only supports user properties");
        }
        UserWithAttributes user = this.crowdService.getUserWithAttributes(entity.getName());
        if (user == null) {
            return null;
        }
        HashMap<String, Object> args = new HashMap<String, Object>();
        args.put("entityId", 0L);
        args.put("entityName", PROPERTY_PREFIX + user.getName());
        args.put("configurationProvider", this.configProvider);
        return new DebugLoggingPropertySet(PropertySetManager.getInstance((String)HIBERNATE_PROPERTY_SET, args));
    }
}

