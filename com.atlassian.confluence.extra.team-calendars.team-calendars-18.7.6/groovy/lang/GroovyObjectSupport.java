/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import org.codehaus.groovy.runtime.InvokerHelper;

public abstract class GroovyObjectSupport
implements GroovyObject {
    private transient MetaClass metaClass = this.getDefaultMetaClass();

    @Override
    public Object getProperty(String property) {
        return this.getMetaClass().getProperty(this, property);
    }

    @Override
    public void setProperty(String property, Object newValue) {
        this.getMetaClass().setProperty(this, property, newValue);
    }

    @Override
    public Object invokeMethod(String name, Object args) {
        return this.getMetaClass().invokeMethod((Object)this, name, args);
    }

    @Override
    public MetaClass getMetaClass() {
        return this.metaClass;
    }

    @Override
    public void setMetaClass(MetaClass metaClass) {
        this.metaClass = null == metaClass ? this.getDefaultMetaClass() : metaClass;
    }

    private MetaClass getDefaultMetaClass() {
        return InvokerHelper.getMetaClass(this.getClass());
    }
}

