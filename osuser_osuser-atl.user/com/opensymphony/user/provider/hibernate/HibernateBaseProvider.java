/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.util.ClassLoaderUtil
 *  net.sf.hibernate.HibernateException
 *  net.sf.hibernate.MappingException
 *  net.sf.hibernate.tool.hbm2ddl.SchemaExport
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.user.provider.hibernate;

import com.opensymphony.user.Entity;
import com.opensymphony.user.provider.UserProvider;
import com.opensymphony.user.provider.hibernate.OSUserHibernateConfigurationProvider;
import com.opensymphony.user.provider.hibernate.dao.HibernateGroupDAO;
import com.opensymphony.user.provider.hibernate.dao.HibernateUserDAO;
import com.opensymphony.user.provider.hibernate.impl.OSUserHibernateConfigurationProviderImpl;
import com.opensymphony.util.ClassLoaderUtil;
import java.util.List;
import java.util.Properties;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.MappingException;
import net.sf.hibernate.tool.hbm2ddl.SchemaExport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class HibernateBaseProvider
implements UserProvider {
    protected static Log log = LogFactory.getLog((String)(class$com$opensymphony$user$provider$hibernate$HibernateBaseProvider == null ? (class$com$opensymphony$user$provider$hibernate$HibernateBaseProvider = HibernateBaseProvider.class$("com.opensymphony.user.provider.hibernate.HibernateBaseProvider")) : class$com$opensymphony$user$provider$hibernate$HibernateBaseProvider).getName());
    OSUserHibernateConfigurationProvider configProvider;
    static /* synthetic */ Class class$com$opensymphony$user$provider$hibernate$HibernateBaseProvider;

    public HibernateGroupDAO getGroupDAO() {
        return this.configProvider.getGroupDAO();
    }

    public HibernateUserDAO getUserDAO() {
        return this.configProvider.getUserDAO();
    }

    public boolean create(String name) {
        return false;
    }

    public boolean init(Properties properties) {
        boolean result = false;
        try {
            String configProviderClass = properties.getProperty("configuration.provider.class");
            this.configProvider = null;
            if (configProviderClass != null) {
                try {
                    this.configProvider = (OSUserHibernateConfigurationProvider)ClassLoaderUtil.loadClass((String)configProviderClass, this.getClass()).newInstance();
                }
                catch (Exception e) {
                    log.error((Object)("Unable to load configuration provider class: " + configProviderClass), (Throwable)e);
                    return false;
                }
            } else {
                this.configProvider = new OSUserHibernateConfigurationProviderImpl();
            }
            this.configProvider.setupConfiguration(properties);
            if ("true".equals(properties.getProperty("create.tables", "false"))) {
                SchemaExport ex = new SchemaExport(this.configProvider.getConfiguration(), properties);
                ex.create(true, false);
            }
            result = true;
        }
        catch (MappingException me) {
            log.error((Object)"Unable to configure Hibernate.", (Throwable)me);
        }
        catch (HibernateException he) {
            log.error((Object)"Unable to obtain Hibernate SessionFactory.", (Throwable)he);
        }
        return result;
    }

    public List list() {
        return null;
    }

    public boolean load(String name, Entity.Accessor accessor) {
        accessor.setMutable(true);
        return true;
    }

    public boolean remove(String name) {
        return false;
    }

    public boolean store(String name, Entity.Accessor accessor) {
        return false;
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

