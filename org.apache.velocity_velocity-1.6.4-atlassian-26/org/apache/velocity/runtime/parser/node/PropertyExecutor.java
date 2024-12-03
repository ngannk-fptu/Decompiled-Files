/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.velocity.runtime.parser.node;

import java.lang.reflect.InvocationTargetException;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeLogger;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.runtime.log.RuntimeLoggerLog;
import org.apache.velocity.runtime.parser.node.AbstractExecutor;
import org.apache.velocity.util.introspection.Introspector;

public class PropertyExecutor
extends AbstractExecutor {
    private final Introspector introspector;

    public PropertyExecutor(Log log, Introspector introspector, Class clazz, String property) {
        this.log = log;
        this.introspector = introspector;
        if (StringUtils.isNotEmpty((CharSequence)property)) {
            this.discover(clazz, property);
        }
    }

    public PropertyExecutor(RuntimeLogger r, Introspector introspector, Class clazz, String property) {
        this(new RuntimeLoggerLog(r), introspector, clazz, property);
    }

    protected Introspector getIntrospector() {
        return this.introspector;
    }

    protected void discover(Class clazz, String property) {
        try {
            Object[] params = new Object[]{};
            StringBuffer sb = new StringBuffer("get");
            sb.append(property);
            this.setMethod(this.introspector.getMethod(clazz, sb.toString(), params));
            if (!this.isAlive()) {
                char c = sb.charAt(3);
                if (Character.isLowerCase(c)) {
                    sb.setCharAt(3, Character.toUpperCase(c));
                } else {
                    sb.setCharAt(3, Character.toLowerCase(c));
                }
                this.setMethod(this.introspector.getMethod(clazz, sb.toString(), params));
            }
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            String msg = "Exception while looking for property getter for '" + property;
            this.log.error(msg, e);
            throw new VelocityException(msg, e);
        }
    }

    @Override
    public Object execute(Object o) throws IllegalAccessException, InvocationTargetException {
        return this.isAlive() ? this.getMethod().invoke(o, (Object[])null) : null;
    }
}

