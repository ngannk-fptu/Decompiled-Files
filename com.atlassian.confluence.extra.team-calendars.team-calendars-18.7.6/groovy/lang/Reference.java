/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.lang.GroovyObjectSupport;
import java.io.Serializable;
import org.codehaus.groovy.runtime.InvokerHelper;

public class Reference<T>
extends GroovyObjectSupport
implements Serializable {
    private static final long serialVersionUID = 4963704631487573488L;
    private T value;

    public Reference() {
    }

    public Reference(T value) {
        this.value = value;
    }

    @Override
    public Object getProperty(String property) {
        T value = this.get();
        if (value != null) {
            return InvokerHelper.getProperty(value, property);
        }
        return super.getProperty(property);
    }

    @Override
    public void setProperty(String property, Object newValue) {
        T value = this.get();
        if (value != null) {
            InvokerHelper.setProperty(value, property, newValue);
        } else {
            super.setProperty(property, newValue);
        }
    }

    @Override
    public Object invokeMethod(String name, Object args) {
        T value = this.get();
        if (value != null) {
            try {
                return InvokerHelper.invokeMethod(value, name, args);
            }
            catch (Exception e) {
                return super.invokeMethod(name, args);
            }
        }
        return super.invokeMethod(name, args);
    }

    public T get() {
        return this.value;
    }

    public void set(T value) {
        this.value = value;
    }
}

