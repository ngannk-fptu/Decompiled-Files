/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.module.propertyset.PropertySet
 *  com.opensymphony.module.propertyset.PropertySetManager
 */
package com.atlassian.user.impl.hibernate.properties;

import com.atlassian.user.Entity;
import com.atlassian.user.EntityException;
import com.atlassian.user.ExternalEntity;
import com.atlassian.user.User;
import com.atlassian.user.UserManager;
import com.atlassian.user.impl.hibernate.DefaultHibernateUser;
import com.atlassian.user.impl.hibernate.ExternalEntityDAO;
import com.atlassian.user.impl.hibernate.HibernateUserManager;
import com.atlassian.user.impl.hibernate.repository.HibernateRepository;
import com.atlassian.user.properties.PropertySetFactory;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetManager;
import java.util.HashMap;
import java.util.Map;

public class HibernatePropertySetFactory
implements PropertySetFactory {
    public static final String HIBERNATE_PROPERTY_SET = "hibernate";
    public static final String EXTERNAL_ENTITY = "EXT";
    public static final String LOCAL_USER = "LOC";
    protected final UserManager userManager;
    protected final ExternalEntityDAO externalEntityDAO;
    protected final HibernateRepository repository;

    public HibernatePropertySetFactory(UserManager userManager, ExternalEntityDAO externalEntityDAO, HibernateRepository repository) {
        this.userManager = userManager;
        this.externalEntityDAO = externalEntityDAO;
        this.repository = repository;
    }

    public PropertySet getPropertySet(Entity entity) throws EntityException {
        return this.getPropertySet(entity.getName());
    }

    protected PropertySet getPropertySet(String entityName) throws EntityException {
        HashMap<String, Object> args = new HashMap<String, Object>();
        User user = null;
        if (this.userManager instanceof HibernateUserManager && (user = this.userManager.getUser(entityName)) != null) {
            args.put("entityId", ((DefaultHibernateUser)user).getId());
            args.put("entityName", "LOC_" + user.getName());
            args.put("configurationProvider", this.repository.getHibernateConfigurationProvider());
        }
        if (user == null) {
            ExternalEntity externalEntity = this.externalEntityDAO.getExternalEntity(entityName);
            if (externalEntity == null) {
                externalEntity = this.externalEntityDAO.createExternalEntity(entityName);
            }
            args.put("entityId", externalEntity.getId());
            args.put("entityName", externalEntity.getType() + "_" + externalEntity.getName());
            args.put("configurationProvider", this.repository.getHibernateConfigurationProvider());
        }
        PropertySet propertySet = this.getPropertySet(args);
        return propertySet;
    }

    protected PropertySet getPropertySet(HashMap args) {
        return PropertySetManager.getInstance((String)HIBERNATE_PROPERTY_SET, (Map)args);
    }
}

