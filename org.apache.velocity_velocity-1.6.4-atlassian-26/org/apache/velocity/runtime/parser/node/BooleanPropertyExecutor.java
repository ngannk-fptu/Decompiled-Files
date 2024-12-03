/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.parser.node;

import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeLogger;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.runtime.log.RuntimeLoggerLog;
import org.apache.velocity.runtime.parser.node.PropertyExecutor;
import org.apache.velocity.util.introspection.Introspector;

public class BooleanPropertyExecutor
extends PropertyExecutor {
    public BooleanPropertyExecutor(Log log, Introspector introspector, Class clazz, String property) {
        super(log, introspector, clazz, property);
    }

    public BooleanPropertyExecutor(RuntimeLogger rlog, Introspector introspector, Class clazz, String property) {
        super(new RuntimeLoggerLog(rlog), introspector, clazz, property);
    }

    @Override
    protected void discover(Class clazz, String property) {
        try {
            Object[] params = new Object[]{};
            StringBuffer sb = new StringBuffer("is");
            sb.append(property);
            this.setMethod(this.getIntrospector().getMethod(clazz, sb.toString(), params));
            if (!this.isAlive()) {
                char c = sb.charAt(2);
                if (Character.isLowerCase(c)) {
                    sb.setCharAt(2, Character.toUpperCase(c));
                } else {
                    sb.setCharAt(2, Character.toLowerCase(c));
                }
                this.setMethod(this.getIntrospector().getMethod(clazz, sb.toString(), params));
            }
            if (this.isAlive() && this.getMethod().getReturnType() != Boolean.TYPE && this.getMethod().getReturnType() != Boolean.class) {
                this.setMethod(null);
            }
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            String msg = "Exception while looking for boolean property getter for '" + property;
            this.log.error(msg, e);
            throw new VelocityException(msg, e);
        }
    }
}

