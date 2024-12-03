/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.binding;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.codehaus.groovy.binding.TargetBinding;
import org.codehaus.groovy.runtime.InvokerHelper;

public class PropertyChangeProxyTargetBinding
implements TargetBinding {
    Object proxyObject;
    String propertyName;
    PropertyChangeListener listener;

    public PropertyChangeProxyTargetBinding(Object proxyObject, String propertyName, PropertyChangeListener listener) {
        this.proxyObject = proxyObject;
        this.propertyName = propertyName;
        this.listener = listener;
    }

    @Override
    public void updateTargetValue(Object value) {
        this.listener.propertyChange(new PropertyChangeEvent(this.proxyObject, this.propertyName, InvokerHelper.getProperty(this.proxyObject, this.propertyName), value));
    }
}

