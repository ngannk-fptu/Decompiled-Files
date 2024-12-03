/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.beans.swing;

import com.mchange.v2.beans.BeansUtils;
import com.mchange.v2.beans.swing.HostBindingInterface;
import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyVetoException;
import java.lang.reflect.Method;
import javax.swing.SwingUtilities;

class PropertyComponentBindingUtility {
    static final Object[] EMPTY_ARGS = new Object[0];
    HostBindingInterface hbi;
    Object bean;
    PropertyDescriptor pd = null;
    EventSetDescriptor propChangeEsd = null;
    Method addMethod = null;
    Method removeMethod = null;
    Method propGetter = null;
    Method propSetter = null;
    PropertyEditor propEditor = null;
    Object nullReplacement = null;

    PropertyComponentBindingUtility(final HostBindingInterface hostBindingInterface, Object object, final String string, boolean bl) throws IntrospectionException {
        this.hbi = hostBindingInterface;
        this.bean = object;
        BeanInfo beanInfo = Introspector.getBeanInfo(object.getClass());
        PropertyDescriptor[] propertyDescriptorArray = beanInfo.getPropertyDescriptors();
        int n = propertyDescriptorArray.length;
        for (int i = 0; i < n; ++i) {
            PropertyDescriptor propertyDescriptor = propertyDescriptorArray[i];
            if (!string.equals(propertyDescriptor.getName())) continue;
            this.pd = propertyDescriptor;
            break;
        }
        if (this.pd == null) {
            throw new IntrospectionException("Cannot find property on bean Object with name '" + string + "'.");
        }
        for (EventSetDescriptor eventSetDescriptor : beanInfo.getEventSetDescriptors()) {
            if (!"propertyChange".equals(eventSetDescriptor.getName())) continue;
            this.propChangeEsd = eventSetDescriptor;
            break;
        }
        if (this.propChangeEsd == null) {
            throw new IntrospectionException("Cannot find PropertyChangeEvent on bean Object with name '" + string + "'.");
        }
        this.propEditor = BeansUtils.findPropertyEditor(this.pd);
        if (bl && this.propEditor == null) {
            throw new IntrospectionException("Could not find an appropriate PropertyEditor for property: " + string);
        }
        this.propGetter = this.pd.getReadMethod();
        this.propSetter = this.pd.getWriteMethod();
        if (this.propGetter == null || this.propSetter == null) {
            throw new IntrospectionException("The specified property '" + string + "' must be both readdable and writable, but it is not!");
        }
        Class<?> clazz = this.pd.getPropertyType();
        if (clazz.isPrimitive()) {
            if (clazz == Boolean.TYPE) {
                this.nullReplacement = Boolean.FALSE;
            }
            if (clazz == Byte.TYPE) {
                this.nullReplacement = new Byte(0);
            } else if (clazz == Character.TYPE) {
                this.nullReplacement = new Character('\u0000');
            } else if (clazz == Short.TYPE) {
                this.nullReplacement = new Short(0);
            } else if (clazz == Integer.TYPE) {
                this.nullReplacement = new Integer(0);
            } else if (clazz == Long.TYPE) {
                this.nullReplacement = new Long(0L);
            } else if (clazz == Float.TYPE) {
                this.nullReplacement = new Float(0.0f);
            } else if (clazz == Double.TYPE) {
                this.nullReplacement = new Double(0.0);
            } else {
                throw new InternalError("What kind of primitive is " + clazz.getName() + "???");
            }
        }
        this.addMethod = this.propChangeEsd.getAddListenerMethod();
        this.removeMethod = this.propChangeEsd.getAddListenerMethod();
        PropertyChangeListener propertyChangeListener = new PropertyChangeListener(){

            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                String string2 = propertyChangeEvent.getPropertyName();
                if (string2.equals(string)) {
                    hostBindingInterface.syncToValue(PropertyComponentBindingUtility.this.propEditor, propertyChangeEvent.getNewValue());
                }
            }
        };
        try {
            this.addMethod.invoke(object, propertyChangeListener);
        }
        catch (Exception exception) {
            exception.printStackTrace();
            throw new IntrospectionException("The introspected PropertyChangeEvent adding method failed with an Exception.");
        }
        hostBindingInterface.addUserModificationListeners();
    }

    public void userModification() {
        Object object = null;
        try {
            object = this.propGetter.invoke(this.bean, EMPTY_ARGS);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        try {
            Object object2 = this.hbi.fetchUserModification(this.propEditor, object);
            if (object2 == null) {
                object2 = this.nullReplacement;
            }
            this.propSetter.invoke(this.bean, object2);
        }
        catch (Exception exception) {
            if (!(exception instanceof PropertyVetoException)) {
                exception.printStackTrace();
            }
            this.syncComponentToValue(true);
        }
    }

    public void resync() {
        this.syncComponentToValue(false);
    }

    private void syncComponentToValue(final boolean bl) {
        try {
            final Object object = this.propGetter.invoke(this.bean, EMPTY_ARGS);
            Runnable runnable = new Runnable(){

                @Override
                public void run() {
                    if (bl) {
                        PropertyComponentBindingUtility.this.hbi.alertErroneousInput();
                    }
                    PropertyComponentBindingUtility.this.hbi.syncToValue(PropertyComponentBindingUtility.this.propEditor, object);
                }
            };
            SwingUtilities.invokeLater(runnable);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}

