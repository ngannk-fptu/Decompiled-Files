/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.config;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.BeanSelectionProvider;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Context;
import com.opensymphony.xwork2.inject.Factory;
import com.opensymphony.xwork2.inject.Scope;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractBeanSelectionProvider
implements BeanSelectionProvider {
    private static final Logger LOG = LogManager.getLogger(AbstractBeanSelectionProvider.class);
    public static final String DEFAULT_BEAN_NAME = "struts";

    @Override
    public void destroy() {
    }

    @Override
    public void loadPackages() throws ConfigurationException {
    }

    @Override
    public void init(Configuration configuration) throws ConfigurationException {
    }

    @Override
    public boolean needsReload() {
        return false;
    }

    protected void alias(Class type, String key, ContainerBuilder builder, Properties props) {
        this.alias(type, key, builder, props, Scope.SINGLETON);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected void alias(Class type, String key, ContainerBuilder builder, Properties props, Scope scope) {
        if (!builder.contains(type, "default")) {
            String foundName = props.getProperty(key, DEFAULT_BEAN_NAME);
            if (builder.contains(type, foundName)) {
                LOG.trace("Choosing bean ({}) for ({})", (Object)foundName, (Object)type.getName());
                builder.alias(type, foundName, "default");
                return;
            } else {
                try {
                    Class cls = ClassLoaderUtil.loadClass(foundName, this.getClass());
                    LOG.trace("Choosing bean ({}) for ({})", (Object)cls.getName(), (Object)type.getName());
                    builder.factory(type, cls, scope);
                    return;
                }
                catch (ClassNotFoundException ex) {
                    LOG.trace("Choosing bean ({}) for ({}) to be loaded from the ObjectFactory", (Object)foundName, (Object)type.getName());
                    if (DEFAULT_BEAN_NAME.equals(foundName)) return;
                    if (ObjectFactory.class == type) throw new ConfigurationException("Cannot locate the chosen ObjectFactory implementation: " + foundName);
                    builder.factory(type, new ObjectFactoryDelegateFactory(foundName, type), scope);
                    return;
                }
            }
        } else {
            LOG.warn("Unable to alias bean type ({}), default mapping already assigned.", (Object)type.getName());
        }
    }

    static class ObjectFactoryDelegateFactory
    implements Factory {
        String name;
        Class type;

        ObjectFactoryDelegateFactory(String name, Class type) {
            this.name = name;
            this.type = type;
        }

        public Object create(Context context) throws Exception {
            ObjectFactory objFactory = context.getContainer().getInstance(ObjectFactory.class);
            try {
                return objFactory.buildBean(this.name, null, true);
            }
            catch (ClassNotFoundException ex) {
                throw new ConfigurationException("Unable to load bean " + this.type.getName() + " (" + this.name + ")");
            }
        }

        public Class type() {
            return this.type;
        }
    }
}

