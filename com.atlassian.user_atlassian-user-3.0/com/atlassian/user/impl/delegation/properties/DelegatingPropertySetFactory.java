/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.profiling.UtilTimerStack
 *  com.opensymphony.module.propertyset.PropertySet
 */
package com.atlassian.user.impl.delegation.properties;

import com.atlassian.user.Entity;
import com.atlassian.user.EntityException;
import com.atlassian.user.properties.PropertySetFactory;
import com.atlassian.util.profiling.UtilTimerStack;
import com.opensymphony.module.propertyset.PropertySet;
import java.util.Collections;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DelegatingPropertySetFactory
implements PropertySetFactory {
    private final List<PropertySetFactory> propertySetFactories;

    public DelegatingPropertySetFactory(List<PropertySetFactory> propertySetFactories) {
        this.propertySetFactories = propertySetFactories;
    }

    @Override
    public PropertySet getPropertySet(Entity entity) throws EntityException {
        if (UtilTimerStack.isActive()) {
            UtilTimerStack.push((String)(this.getClass().getName() + "_delegating_getPropertySet(" + entity.getName() + ")"));
        }
        for (PropertySetFactory propertySetFactory : this.propertySetFactories) {
            PropertySet propertySet = propertySetFactory.getPropertySet(entity);
            if (propertySet == null) continue;
            if (UtilTimerStack.isActive()) {
                UtilTimerStack.pop((String)(this.getClass().getName() + "_delegating_getPropertySet(" + entity.getName() + ")"));
            }
            return propertySet;
        }
        if (UtilTimerStack.isActive()) {
            UtilTimerStack.pop((String)(this.getClass().getName() + "_delegating_getPropertySet(" + entity.getName() + ")"));
        }
        return null;
    }

    public List<PropertySetFactory> getPropertySetFactories() {
        return Collections.unmodifiableList(this.propertySetFactories);
    }
}

