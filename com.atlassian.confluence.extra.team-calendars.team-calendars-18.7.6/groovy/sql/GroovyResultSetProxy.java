/*
 * Decompiled with CFR 0.152.
 */
package groovy.sql;

import groovy.lang.GroovyObject;
import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;
import groovy.sql.GroovyResultSet;
import groovy.sql.GroovyResultSetExtension;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.ResultSet;
import org.codehaus.groovy.runtime.InvokerHelper;

public final class GroovyResultSetProxy
implements InvocationHandler {
    private GroovyResultSetExtension extension;
    private MetaClass metaClass;

    public GroovyResultSetProxy(ResultSet set) {
        this.extension = new GroovyResultSetExtension(set);
    }

    public GroovyResultSetProxy(GroovyResultSetExtension ext) {
        this.extension = ext;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String name = method.getName();
        if (method.getDeclaringClass() == GroovyObject.class) {
            if (name.equals("getMetaClass")) {
                return this.getMetaClass();
            }
            if (name.equals("setMetaClass")) {
                return this.setMetaClass((MetaClass)args[0]);
            }
        }
        return InvokerHelper.invokeMethod(this.extension, method.getName(), args);
    }

    private MetaClass setMetaClass(MetaClass mc) {
        this.metaClass = mc;
        return mc;
    }

    private MetaClass getMetaClass() {
        if (this.metaClass == null) {
            this.metaClass = GroovySystem.getMetaClassRegistry().getMetaClass(DummyResultSet.class);
        }
        return this.metaClass;
    }

    public GroovyResultSet getImpl() {
        return (GroovyResultSet)Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{GroovyResultSet.class}, (InvocationHandler)this);
    }

    private static abstract class DummyResultSet
    implements GroovyResultSet {
        private DummyResultSet() {
        }
    }
}

