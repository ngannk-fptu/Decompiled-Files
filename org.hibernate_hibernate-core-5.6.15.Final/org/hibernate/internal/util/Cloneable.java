/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.hibernate.HibernateException;

public class Cloneable {
    private static final Object[] READER_METHOD_ARGS = new Object[0];

    public Object shallowCopy() {
        return AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                return Cloneable.this.copyListeners();
            }
        });
    }

    public void validate() throws HibernateException {
        AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                Cloneable.this.checkListeners();
                return null;
            }
        });
    }

    private Object copyListeners() {
        Object copy = null;
        BeanInfo beanInfo = null;
        try {
            PropertyDescriptor[] pds;
            beanInfo = Introspector.getBeanInfo(this.getClass(), Object.class);
            this.internalCheckListeners(beanInfo);
            copy = this.getClass().newInstance();
            for (PropertyDescriptor pd : pds = beanInfo.getPropertyDescriptors()) {
                try {
                    pd.getWriteMethod().invoke(copy, pd.getReadMethod().invoke((Object)this, READER_METHOD_ARGS));
                }
                catch (Throwable t) {
                    throw new HibernateException("Unable copy copy listener [" + pd.getName() + "]");
                }
            }
        }
        catch (Exception t) {
            throw new HibernateException("Unable to copy listeners", t);
        }
        finally {
            if (beanInfo != null) {
                Introspector.flushFromCaches(this.getClass());
            }
        }
        return copy;
    }

    private void checkListeners() {
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(this.getClass(), Object.class);
            this.internalCheckListeners(beanInfo);
        }
        catch (IntrospectionException t) {
            throw new HibernateException("Unable to validate listener config", t);
        }
        finally {
            if (beanInfo != null) {
                Introspector.flushFromCaches(this.getClass());
            }
        }
    }

    private void internalCheckListeners(BeanInfo beanInfo) {
        PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
        try {
            for (PropertyDescriptor pd : pds) {
                Object[] listenerArray;
                Object listener = pd.getReadMethod().invoke((Object)this, READER_METHOD_ARGS);
                if (listener == null) {
                    throw new HibernateException("Listener [" + pd.getName() + "] was null");
                }
                if (!listener.getClass().isArray()) continue;
                for (Object aListenerArray : listenerArray = (Object[])listener) {
                    if (aListenerArray != null) continue;
                    throw new HibernateException("Listener in [" + pd.getName() + "] was null");
                }
            }
        }
        catch (HibernateException e) {
            throw e;
        }
        catch (Throwable t) {
            throw new HibernateException("Unable to validate listener config");
        }
    }
}

