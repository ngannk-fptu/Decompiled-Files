/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.text.StrBuilder
 */
package org.apache.velocity.runtime.parser.node;

import java.lang.reflect.InvocationTargetException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.runtime.parser.node.SetExecutor;
import org.apache.velocity.util.introspection.Introspector;

public class SetPropertyExecutor
extends SetExecutor {
    private final Introspector introspector;

    public SetPropertyExecutor(Log log, Introspector introspector, Class clazz, String property, Object arg) {
        this.log = log;
        this.introspector = introspector;
        if (StringUtils.isNotEmpty((CharSequence)property)) {
            this.discover(clazz, property, arg);
        }
    }

    protected Introspector getIntrospector() {
        return this.introspector;
    }

    protected void discover(Class clazz, String property, Object arg) {
        Object[] params = new Object[]{arg};
        try {
            StrBuilder sb = new StrBuilder("set");
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
            String msg = "Exception while looking for property setter for '" + property;
            this.log.error(msg, e);
            throw new VelocityException(msg, e);
        }
    }

    @Override
    public Object execute(Object o, Object value) throws IllegalAccessException, InvocationTargetException {
        Object[] params = new Object[]{value};
        return this.isAlive() ? this.getMethod().invoke(o, params) : null;
    }
}

