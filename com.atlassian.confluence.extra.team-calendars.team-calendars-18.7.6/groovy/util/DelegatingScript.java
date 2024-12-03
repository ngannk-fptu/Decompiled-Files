/*
 * Decompiled with CFR 0.152.
 */
package groovy.util;

import groovy.lang.Binding;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.MissingMethodException;
import groovy.lang.MissingPropertyException;
import groovy.lang.Script;
import org.codehaus.groovy.runtime.InvokerHelper;

public abstract class DelegatingScript
extends Script {
    private Object delegate;
    private MetaClass metaClass;

    protected DelegatingScript() {
    }

    protected DelegatingScript(Binding binding) {
        super(binding);
    }

    public void setDelegate(Object delegate) {
        this.delegate = delegate;
        this.metaClass = InvokerHelper.getMetaClass(delegate.getClass());
    }

    @Override
    public Object invokeMethod(String name, Object args) {
        try {
            if (this.delegate instanceof GroovyObject) {
                return ((GroovyObject)this.delegate).invokeMethod(name, args);
            }
            return this.metaClass.invokeMethod(this.delegate, name, args);
        }
        catch (MissingMethodException mme) {
            return super.invokeMethod(name, args);
        }
    }

    @Override
    public Object getProperty(String property) {
        try {
            return this.metaClass.getProperty(this.delegate, property);
        }
        catch (MissingPropertyException e) {
            return super.getProperty(property);
        }
    }

    @Override
    public void setProperty(String property, Object newValue) {
        try {
            this.metaClass.setProperty(this.delegate, property, newValue);
        }
        catch (MissingPropertyException e) {
            super.setProperty(property, newValue);
        }
    }

    public Object getDelegate() {
        return this.delegate;
    }
}

