/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils;

import java.lang.reflect.InvocationTargetException;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.WrapDynaBean;

public class ConvertingWrapDynaBean
extends WrapDynaBean {
    public ConvertingWrapDynaBean(Object instance) {
        super(instance);
    }

    @Override
    public void set(String name, Object value) {
        try {
            BeanUtils.copyProperty(this.instance, name, value);
        }
        catch (InvocationTargetException ite) {
            Throwable cause = ite.getTargetException();
            throw new IllegalArgumentException("Error setting property '" + name + "' nested exception - " + cause);
        }
        catch (Throwable t) {
            IllegalArgumentException iae = new IllegalArgumentException("Error setting property '" + name + "', exception - " + t);
            BeanUtils.initCause(iae, t);
            throw iae;
        }
    }
}

