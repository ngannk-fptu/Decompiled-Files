/*
 * Decompiled with CFR 0.152.
 */
package javax.el;

import javax.el.PropertyNotWritableException;

public abstract class BeanNameResolver {
    public boolean isNameResolved(String beanName) {
        return false;
    }

    public Object getBean(String beanName) {
        return null;
    }

    public void setBeanValue(String beanName, Object value) throws PropertyNotWritableException {
        throw new PropertyNotWritableException();
    }

    public boolean isReadOnly(String beanName) {
        return true;
    }

    public boolean canCreateBean(String beanName) {
        return false;
    }
}

