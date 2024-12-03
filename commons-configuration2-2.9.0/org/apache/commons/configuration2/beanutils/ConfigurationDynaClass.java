/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.beanutils.DynaBean
 *  org.apache.commons.beanutils.DynaClass
 *  org.apache.commons.beanutils.DynaProperty
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.configuration2.beanutils;

import java.util.ArrayList;
import java.util.Iterator;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.beanutils.ConfigurationDynaBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConfigurationDynaClass
implements DynaClass {
    private static final Log LOG = LogFactory.getLog(ConfigurationDynaClass.class);
    private final Configuration configuration;

    public ConfigurationDynaClass(Configuration configuration) {
        if (LOG.isTraceEnabled()) {
            LOG.trace((Object)("ConfigurationDynaClass(" + configuration + ")"));
        }
        this.configuration = configuration;
    }

    public DynaProperty getDynaProperty(String name) {
        if (LOG.isTraceEnabled()) {
            LOG.trace((Object)("getDynaProperty(" + name + ")"));
        }
        if (name == null) {
            throw new IllegalArgumentException("Property name must not be null!");
        }
        Object value = this.configuration.getProperty(name);
        if (value == null) {
            return null;
        }
        Class<Object> type = value.getClass();
        if (type == Byte.class) {
            type = Byte.TYPE;
        }
        if (type == Character.class) {
            type = Character.TYPE;
        } else if (type == Boolean.class) {
            type = Boolean.TYPE;
        } else if (type == Double.class) {
            type = Double.TYPE;
        } else if (type == Float.class) {
            type = Float.TYPE;
        } else if (type == Integer.class) {
            type = Integer.TYPE;
        } else if (type == Long.class) {
            type = Long.TYPE;
        } else if (type == Short.class) {
            type = Short.TYPE;
        }
        return new DynaProperty(name, type);
    }

    public DynaProperty[] getDynaProperties() {
        if (LOG.isTraceEnabled()) {
            LOG.trace((Object)"getDynaProperties()");
        }
        Iterator<String> keys = this.configuration.getKeys();
        ArrayList<DynaProperty> properties = new ArrayList<DynaProperty>();
        while (keys.hasNext()) {
            String key = keys.next();
            DynaProperty property = this.getDynaProperty(key);
            properties.add(property);
        }
        DynaProperty[] propertyArray = new DynaProperty[properties.size()];
        properties.toArray(propertyArray);
        if (LOG.isDebugEnabled()) {
            LOG.debug((Object)("Found " + properties.size() + " properties."));
        }
        return propertyArray;
    }

    public String getName() {
        return ConfigurationDynaBean.class.getName();
    }

    public DynaBean newInstance() throws IllegalAccessException, InstantiationException {
        return new ConfigurationDynaBean(this.configuration);
    }
}

