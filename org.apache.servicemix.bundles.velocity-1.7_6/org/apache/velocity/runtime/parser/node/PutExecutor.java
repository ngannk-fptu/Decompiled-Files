/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.parser.node;

import java.lang.reflect.InvocationTargetException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.runtime.parser.node.SetExecutor;
import org.apache.velocity.util.introspection.Introspector;

public class PutExecutor
extends SetExecutor {
    private final Introspector introspector;
    private final String property;

    public PutExecutor(Log log, Introspector introspector, Class clazz, Object arg, String property) {
        this.log = log;
        this.introspector = introspector;
        this.property = property;
        this.discover(clazz, arg);
    }

    protected void discover(Class clazz, Object arg) {
        Object[] params = this.property == null ? new Object[]{arg} : new Object[]{this.property, arg};
        try {
            this.setMethod(this.introspector.getMethod(clazz, "put", params));
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            String msg = "Exception while looking for put('" + params[0] + "') method";
            this.log.error(msg, e);
            throw new VelocityException(msg, e);
        }
    }

    public Object execute(Object o, Object value) throws IllegalAccessException, InvocationTargetException {
        if (this.isAlive()) {
            Object[] params = this.property == null ? new Object[]{value} : new Object[]{this.property, value};
            return this.getMethod().invoke(o, params);
        }
        return null;
    }
}

