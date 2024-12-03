/*
 * Decompiled with CFR 0.152.
 */
package groovy.util;

import groovy.lang.DelegatingMetaClass;
import groovy.lang.MetaClass;
import groovy.lang.MissingMethodException;
import groovy.util.FactoryBuilderSupport;
import org.codehaus.groovy.runtime.InvokerHelper;

class FactoryInterceptorMetaClass
extends DelegatingMetaClass {
    FactoryBuilderSupport builder;

    public FactoryInterceptorMetaClass(MetaClass delegate, FactoryBuilderSupport builder) {
        super(delegate);
        this.builder = builder;
    }

    @Override
    public Object invokeMethod(Object object, String methodName, Object arguments) {
        try {
            return this.delegate.invokeMethod(object, methodName, arguments);
        }
        catch (MissingMethodException mme) {
            try {
                if (this.builder.getMetaClass().respondsTo(this.builder, methodName).isEmpty()) {
                    return this.builder.invokeMethod(methodName, arguments);
                }
                return InvokerHelper.invokeMethod(this.builder, methodName, arguments);
            }
            catch (MissingMethodException mme2) {
                Throwable root = mme;
                while (root.getCause() != null) {
                    root = root.getCause();
                }
                root.initCause(mme2);
                throw mme;
            }
        }
    }

    @Override
    public Object invokeMethod(Object object, String methodName, Object[] arguments) {
        try {
            return this.delegate.invokeMethod(object, methodName, arguments);
        }
        catch (MissingMethodException mme) {
            try {
                if (this.builder.getMetaClass().respondsTo(this.builder, methodName).isEmpty()) {
                    return this.builder.invokeMethod(methodName, arguments);
                }
                return InvokerHelper.invokeMethod(this.builder, methodName, arguments);
            }
            catch (MissingMethodException mme2) {
                Throwable root = mme;
                while (root.getCause() != null) {
                    root = root.getCause();
                }
                root.initCause(mme2);
                throw mme;
            }
        }
    }
}

