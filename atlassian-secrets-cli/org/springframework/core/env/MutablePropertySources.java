/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.env;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.PropertySources;
import org.springframework.lang.Nullable;

public class MutablePropertySources
implements PropertySources {
    private final Log logger;
    private final List<PropertySource<?>> propertySourceList = new CopyOnWriteArrayList();

    public MutablePropertySources() {
        this.logger = LogFactory.getLog(this.getClass());
    }

    public MutablePropertySources(PropertySources propertySources) {
        this();
        for (PropertySource propertySource : propertySources) {
            this.addLast(propertySource);
        }
    }

    MutablePropertySources(Log logger) {
        this.logger = logger;
    }

    @Override
    public Iterator<PropertySource<?>> iterator() {
        return this.propertySourceList.iterator();
    }

    @Override
    public boolean contains(String name) {
        return this.propertySourceList.contains(PropertySource.named(name));
    }

    @Override
    @Nullable
    public PropertySource<?> get(String name) {
        int index = this.propertySourceList.indexOf(PropertySource.named(name));
        return index != -1 ? this.propertySourceList.get(index) : null;
    }

    public void addFirst(PropertySource<?> propertySource) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Adding PropertySource '" + propertySource.getName() + "' with highest search precedence");
        }
        this.removeIfPresent(propertySource);
        this.propertySourceList.add(0, propertySource);
    }

    public void addLast(PropertySource<?> propertySource) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Adding PropertySource '" + propertySource.getName() + "' with lowest search precedence");
        }
        this.removeIfPresent(propertySource);
        this.propertySourceList.add(propertySource);
    }

    public void addBefore(String relativePropertySourceName, PropertySource<?> propertySource) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Adding PropertySource '" + propertySource.getName() + "' with search precedence immediately higher than '" + relativePropertySourceName + "'");
        }
        this.assertLegalRelativeAddition(relativePropertySourceName, propertySource);
        this.removeIfPresent(propertySource);
        int index = this.assertPresentAndGetIndex(relativePropertySourceName);
        this.addAtIndex(index, propertySource);
    }

    public void addAfter(String relativePropertySourceName, PropertySource<?> propertySource) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Adding PropertySource '" + propertySource.getName() + "' with search precedence immediately lower than '" + relativePropertySourceName + "'");
        }
        this.assertLegalRelativeAddition(relativePropertySourceName, propertySource);
        this.removeIfPresent(propertySource);
        int index = this.assertPresentAndGetIndex(relativePropertySourceName);
        this.addAtIndex(index + 1, propertySource);
    }

    public int precedenceOf(PropertySource<?> propertySource) {
        return this.propertySourceList.indexOf(propertySource);
    }

    @Nullable
    public PropertySource<?> remove(String name) {
        int index;
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Removing PropertySource '" + name + "'");
        }
        return (index = this.propertySourceList.indexOf(PropertySource.named(name))) != -1 ? this.propertySourceList.remove(index) : null;
    }

    public void replace(String name, PropertySource<?> propertySource) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Replacing PropertySource '" + name + "' with '" + propertySource.getName() + "'");
        }
        int index = this.assertPresentAndGetIndex(name);
        this.propertySourceList.set(index, propertySource);
    }

    public int size() {
        return this.propertySourceList.size();
    }

    public String toString() {
        return this.propertySourceList.toString();
    }

    protected void assertLegalRelativeAddition(String relativePropertySourceName, PropertySource<?> propertySource) {
        String newPropertySourceName = propertySource.getName();
        if (relativePropertySourceName.equals(newPropertySourceName)) {
            throw new IllegalArgumentException("PropertySource named '" + newPropertySourceName + "' cannot be added relative to itself");
        }
    }

    protected void removeIfPresent(PropertySource<?> propertySource) {
        this.propertySourceList.remove(propertySource);
    }

    private void addAtIndex(int index, PropertySource<?> propertySource) {
        this.removeIfPresent(propertySource);
        this.propertySourceList.add(index, propertySource);
    }

    private int assertPresentAndGetIndex(String name) {
        int index = this.propertySourceList.indexOf(PropertySource.named(name));
        if (index == -1) {
            throw new IllegalArgumentException("PropertySource named '" + name + "' does not exist");
        }
        return index;
    }
}

