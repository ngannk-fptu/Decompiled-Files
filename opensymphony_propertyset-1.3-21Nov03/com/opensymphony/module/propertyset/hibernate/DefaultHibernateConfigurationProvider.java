/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.sf.hibernate.HibernateException
 *  net.sf.hibernate.SessionFactory
 *  net.sf.hibernate.cfg.Configuration
 */
package com.opensymphony.module.propertyset.hibernate;

import com.opensymphony.module.propertyset.hibernate.HibernateConfigurationProvider;
import com.opensymphony.module.propertyset.hibernate.HibernatePropertySetDAO;
import com.opensymphony.module.propertyset.hibernate.HibernatePropertySetDAOImpl;
import java.util.Iterator;
import java.util.Map;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.cfg.Configuration;

public class DefaultHibernateConfigurationProvider
implements HibernateConfigurationProvider {
    private Configuration configuration;
    private HibernatePropertySetDAO propertySetDAO;
    private SessionFactory sessionFactory;
    static /* synthetic */ Class class$com$opensymphony$module$propertyset$hibernate$PropertySetItem;

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public Configuration getConfiguration() {
        return this.configuration;
    }

    public HibernatePropertySetDAO getPropertySetDAO() {
        if (this.propertySetDAO == null) {
            this.propertySetDAO = new HibernatePropertySetDAOImpl(this.sessionFactory);
        }
        return this.propertySetDAO;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void setupConfiguration(Map configurationProperties) {
        try {
            this.configuration = new Configuration().addClass(class$com$opensymphony$module$propertyset$hibernate$PropertySetItem == null ? (class$com$opensymphony$module$propertyset$hibernate$PropertySetItem = DefaultHibernateConfigurationProvider.class$("com.opensymphony.module.propertyset.hibernate.PropertySetItem")) : class$com$opensymphony$module$propertyset$hibernate$PropertySetItem);
            Iterator itr = configurationProperties.keySet().iterator();
            while (itr.hasNext()) {
                String key = (String)itr.next();
                if (!key.startsWith("hibernate")) continue;
                this.configuration.setProperty(key, (String)configurationProperties.get(key));
            }
            this.sessionFactory = this.configuration.buildSessionFactory();
        }
        catch (HibernateException hibernateException) {
            // empty catch block
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

