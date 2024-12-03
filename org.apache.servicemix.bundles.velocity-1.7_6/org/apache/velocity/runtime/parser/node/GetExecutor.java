/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.parser.node;

import java.lang.reflect.InvocationTargetException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeLogger;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.runtime.log.RuntimeLoggerLog;
import org.apache.velocity.runtime.parser.node.AbstractExecutor;
import org.apache.velocity.util.introspection.Introspector;

public class GetExecutor
extends AbstractExecutor {
    private final Introspector introspector;
    private Object[] params = new Object[0];

    public GetExecutor(Log log, Introspector introspector, Class clazz, String property) {
        this.log = log;
        this.introspector = introspector;
        if (property != null) {
            this.params = new Object[]{property};
        }
        this.discover(clazz);
    }

    public GetExecutor(RuntimeLogger rlog, Introspector introspector, Class clazz, String property) {
        this(new RuntimeLoggerLog(rlog), introspector, clazz, property);
    }

    protected void discover(Class clazz) {
        try {
            this.setMethod(this.introspector.getMethod(clazz, "get", this.params));
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            String msg = "Exception while looking for get('" + this.params[0] + "') method";
            this.log.error(msg, e);
            throw new VelocityException(msg, e);
        }
    }

    public Object execute(Object o) throws IllegalAccessException, InvocationTargetException {
        return this.isAlive() ? this.getMethod().invoke(o, this.params) : null;
    }
}

