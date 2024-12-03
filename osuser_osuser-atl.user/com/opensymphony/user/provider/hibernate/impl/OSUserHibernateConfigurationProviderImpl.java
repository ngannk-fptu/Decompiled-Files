/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.module.propertyset.hibernate.DefaultHibernateConfigurationProvider
 *  net.sf.hibernate.HibernateException
 *  net.sf.hibernate.MappingException
 *  net.sf.hibernate.cfg.Configuration
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.user.provider.hibernate.impl;

import com.opensymphony.module.propertyset.hibernate.DefaultHibernateConfigurationProvider;
import com.opensymphony.user.provider.hibernate.OSUserHibernateConfigurationProvider;
import com.opensymphony.user.provider.hibernate.dao.HibernateGroupDAO;
import com.opensymphony.user.provider.hibernate.dao.HibernateUserDAO;
import com.opensymphony.user.provider.hibernate.dao.SessionManager;
import com.opensymphony.user.provider.hibernate.impl.HibernateGroupDAOImpl;
import com.opensymphony.user.provider.hibernate.impl.HibernateUserDAOImpl;
import java.util.Iterator;
import java.util.Map;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.MappingException;
import net.sf.hibernate.cfg.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OSUserHibernateConfigurationProviderImpl
extends DefaultHibernateConfigurationProvider
implements OSUserHibernateConfigurationProvider {
    protected static Log log = LogFactory.getLog((String)(class$com$opensymphony$user$provider$hibernate$impl$OSUserHibernateConfigurationProviderImpl == null ? (class$com$opensymphony$user$provider$hibernate$impl$OSUserHibernateConfigurationProviderImpl = OSUserHibernateConfigurationProviderImpl.class$("com.opensymphony.user.provider.hibernate.impl.OSUserHibernateConfigurationProviderImpl")) : class$com$opensymphony$user$provider$hibernate$impl$OSUserHibernateConfigurationProviderImpl).getName());
    private Configuration configuration;
    private HibernateGroupDAO groupDAO;
    private HibernateUserDAO userDAO;
    private SessionManager sessionManager;
    static /* synthetic */ Class class$com$opensymphony$user$provider$hibernate$impl$OSUserHibernateConfigurationProviderImpl;
    static /* synthetic */ Class class$com$opensymphony$user$provider$hibernate$entity$HibernateGroup;
    static /* synthetic */ Class class$com$opensymphony$user$provider$hibernate$entity$HibernateUser;
    static /* synthetic */ Class class$com$opensymphony$module$propertyset$hibernate$PropertySetItem;

    public Configuration getConfiguration() {
        return this.configuration;
    }

    public HibernateGroupDAO getGroupDAO() {
        if (this.groupDAO == null) {
            this.groupDAO = new HibernateGroupDAOImpl(this.sessionManager);
        }
        return this.groupDAO;
    }

    public HibernateUserDAO getUserDAO() {
        if (this.userDAO == null) {
            this.userDAO = new HibernateUserDAOImpl(this.sessionManager);
        }
        return this.userDAO;
    }

    public void setupConfiguration(Map configurationProperties) {
        if (this.configuration == null) {
            this.configuration = new Configuration();
            try {
                this.configuration.addClass(class$com$opensymphony$user$provider$hibernate$entity$HibernateGroup == null ? (class$com$opensymphony$user$provider$hibernate$entity$HibernateGroup = OSUserHibernateConfigurationProviderImpl.class$("com.opensymphony.user.provider.hibernate.entity.HibernateGroup")) : class$com$opensymphony$user$provider$hibernate$entity$HibernateGroup);
                this.configuration.addClass(class$com$opensymphony$user$provider$hibernate$entity$HibernateUser == null ? (class$com$opensymphony$user$provider$hibernate$entity$HibernateUser = OSUserHibernateConfigurationProviderImpl.class$("com.opensymphony.user.provider.hibernate.entity.HibernateUser")) : class$com$opensymphony$user$provider$hibernate$entity$HibernateUser);
                this.configuration.addClass(class$com$opensymphony$module$propertyset$hibernate$PropertySetItem == null ? (class$com$opensymphony$module$propertyset$hibernate$PropertySetItem = OSUserHibernateConfigurationProviderImpl.class$("com.opensymphony.module.propertyset.hibernate.PropertySetItem")) : class$com$opensymphony$module$propertyset$hibernate$PropertySetItem);
                if (configurationProperties != null) {
                    Iterator itr = configurationProperties.keySet().iterator();
                    while (itr.hasNext()) {
                        String key = (String)itr.next();
                        if (!key.startsWith("hibernate")) continue;
                        this.configuration.setProperty(key, (String)configurationProperties.get(key));
                    }
                }
                this.sessionManager = new SessionManager(this.configuration);
                super.setSessionFactory(this.sessionManager.getSessionFactory());
            }
            catch (MappingException e) {
                log.error((Object)("Could not create new Hibernate configuration: " + (Object)((Object)e)), (Throwable)e);
            }
            catch (HibernateException e) {
                log.error((Object)("Could not create new Hibernate configuration: " + (Object)((Object)e)), (Throwable)e);
            }
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

