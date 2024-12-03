/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.util.introspection;

import java.lang.reflect.Method;
import org.apache.velocity.runtime.RuntimeLogger;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.runtime.log.RuntimeLoggerLog;
import org.apache.velocity.util.introspection.IntrospectorBase;
import org.apache.velocity.util.introspection.MethodMap;

public class Introspector
extends IntrospectorBase {
    public Introspector(Log log) {
        super(log);
    }

    public Introspector(RuntimeLogger logger) {
        this(new RuntimeLoggerLog(logger));
    }

    public Method getMethod(Class c, String name, Object[] params) throws IllegalArgumentException {
        try {
            return super.getMethod(c, name, params);
        }
        catch (MethodMap.AmbiguousException ae) {
            StringBuffer msg = new StringBuffer("Introspection Error : Ambiguous method invocation ").append(name).append("(");
            for (int i = 0; i < params.length; ++i) {
                if (i > 0) {
                    msg.append(", ");
                }
                if (params[i] == null) {
                    msg.append("null");
                    continue;
                }
                msg.append(params[i].getClass().getName());
            }
            msg.append(") for class ").append(c);
            this.log.debug(msg.toString());
            return null;
        }
    }
}

