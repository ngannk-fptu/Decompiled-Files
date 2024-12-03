/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.rmi;

import com.sun.media.jai.rmi.JaiI18N;
import java.awt.RenderingHints;
import java.io.Serializable;
import java.lang.reflect.Method;
import javax.media.jai.remote.SerializableState;
import javax.media.jai.remote.SerializerFactory;

public abstract class SerializableStateImpl
implements SerializableState {
    protected Class theClass;
    protected transient Object theObject;

    public static Class[] getSupportedClasses() {
        throw new RuntimeException(JaiI18N.getString("SerializableStateImpl0"));
    }

    public static boolean permitsSubclasses() {
        return false;
    }

    protected SerializableStateImpl(Class c, Object o, RenderingHints h) {
        if (c == null || o == null) {
            throw new IllegalArgumentException(JaiI18N.getString("SerializableStateImpl1"));
        }
        boolean isInterface = c.isInterface();
        if (isInterface && !c.isInstance(o)) {
            throw new IllegalArgumentException(JaiI18N.getString("SerializableStateImpl2"));
        }
        if (!isInterface) {
            boolean permitsSubclasses = false;
            try {
                Method m = this.getClass().getMethod("permitsSubclasses", null);
                permitsSubclasses = (Boolean)m.invoke(null, null);
            }
            catch (Exception e) {
                throw new IllegalArgumentException(JaiI18N.getString("SerializableStateImpl5"));
            }
            if (!permitsSubclasses && !c.equals(o.getClass())) {
                throw new IllegalArgumentException(JaiI18N.getString("SerializableStateImpl3"));
            }
            if (permitsSubclasses && !c.isAssignableFrom(o.getClass())) {
                throw new IllegalArgumentException(JaiI18N.getString("SerializableStateImpl4"));
            }
        }
        this.theClass = c;
        this.theObject = o;
    }

    public Class getObjectClass() {
        return this.theClass;
    }

    public Object getObject() {
        return this.theObject;
    }

    protected Object getSerializableForm(Object object) {
        if (object instanceof Serializable) {
            return object;
        }
        if (object != null) {
            try {
                object = SerializerFactory.getState(object, null);
            }
            catch (Exception e) {
                object = null;
            }
        }
        return object;
    }

    protected Object getDeserializedFrom(Object object) {
        if (object instanceof SerializableState) {
            object = ((SerializableState)object).getObject();
        }
        return object;
    }
}

