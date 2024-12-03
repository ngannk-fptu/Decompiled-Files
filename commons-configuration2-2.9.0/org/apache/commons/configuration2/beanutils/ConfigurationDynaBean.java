/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.beanutils.DynaBean
 *  org.apache.commons.beanutils.DynaClass
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.configuration2.beanutils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ConfigurationMap;
import org.apache.commons.configuration2.SubsetConfiguration;
import org.apache.commons.configuration2.beanutils.ConfigurationDynaClass;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConfigurationDynaBean
extends ConfigurationMap
implements DynaBean {
    private static final String PROPERTY_DELIMITER = ".";
    private static final Log LOG = LogFactory.getLog(ConfigurationDynaBean.class);

    public ConfigurationDynaBean(Configuration configuration) {
        super(configuration);
        if (LOG.isTraceEnabled()) {
            LOG.trace((Object)("ConfigurationDynaBean(" + configuration + ")"));
        }
    }

    public void set(String name, Object value) {
        if (LOG.isTraceEnabled()) {
            LOG.trace((Object)("set(" + name + "," + value + ")"));
        }
        Objects.requireNonNull(value, "Error trying to set property to null.");
        if (value instanceof Collection) {
            Collection collection = (Collection)value;
            collection.forEach((? super T v) -> this.getConfiguration().addProperty(name, v));
        } else if (value.getClass().isArray()) {
            int length = Array.getLength(value);
            for (int i = 0; i < length; ++i) {
                this.getConfiguration().addProperty(name, Array.get(value, i));
            }
        } else {
            this.getConfiguration().setProperty(name, value);
        }
    }

    public Object get(String name) {
        SubsetConfiguration subset;
        Object result;
        if (LOG.isTraceEnabled()) {
            LOG.trace((Object)("get(" + name + ")"));
        }
        if ((result = this.getConfiguration().getProperty(name)) == null && !(subset = new SubsetConfiguration(this.getConfiguration(), name, PROPERTY_DELIMITER)).isEmpty()) {
            result = new ConfigurationDynaBean(subset);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug((Object)(name + "=[" + result + "]"));
        }
        if (result == null) {
            throw new IllegalArgumentException("Property '" + name + "' does not exist.");
        }
        return result;
    }

    public boolean contains(String name, String key) {
        Configuration subset = this.getConfiguration().subset(name);
        if (subset == null) {
            throw new IllegalArgumentException("Mapped property '" + name + "' does not exist.");
        }
        return subset.containsKey(key);
    }

    public Object get(String name, int index) {
        if (!this.checkIndexedProperty(name)) {
            throw new IllegalArgumentException("Property '" + name + "' is not indexed.");
        }
        List<Object> list = this.getConfiguration().getList(name);
        return list.get(index);
    }

    public Object get(String name, String key) {
        Configuration subset = this.getConfiguration().subset(name);
        if (subset == null) {
            throw new IllegalArgumentException("Mapped property '" + name + "' does not exist.");
        }
        return subset.getProperty(key);
    }

    public DynaClass getDynaClass() {
        return new ConfigurationDynaClass(this.getConfiguration());
    }

    public void remove(String name, String key) {
        SubsetConfiguration subset = new SubsetConfiguration(this.getConfiguration(), name, PROPERTY_DELIMITER);
        subset.setProperty(key, null);
    }

    public void set(String name, int index, Object value) {
        if (!this.checkIndexedProperty(name) && index > 0) {
            throw new IllegalArgumentException("Property '" + name + "' is not indexed.");
        }
        Object property = this.getConfiguration().getProperty(name);
        if (property instanceof List) {
            List list = (List)property;
            list.set(index, value);
            this.getConfiguration().setProperty(name, list);
        } else if (property.getClass().isArray()) {
            Array.set(property, index, value);
        } else if (index == 0) {
            this.getConfiguration().setProperty(name, value);
        }
    }

    public void set(String name, String key, Object value) {
        this.getConfiguration().setProperty(name + PROPERTY_DELIMITER + key, value);
    }

    private boolean checkIndexedProperty(String name) {
        Object property = this.getConfiguration().getProperty(name);
        if (property == null) {
            throw new IllegalArgumentException("Property '" + name + "' does not exist.");
        }
        return property instanceof List || property.getClass().isArray();
    }
}

