/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.parser.node;

import java.util.Map;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.runtime.parser.node.AbstractExecutor;

public class MapGetExecutor
extends AbstractExecutor {
    private final String property;

    public MapGetExecutor(Log log, Class clazz, String property) {
        this.log = log;
        this.property = property;
        this.discover(clazz);
    }

    protected void discover(Class clazz) {
        if (this.property != null && Map.class.isAssignableFrom(clazz)) {
            try {
                this.setMethod(Map.class.getMethod("get", Object.class));
            }
            catch (RuntimeException e) {
                throw e;
            }
            catch (Exception e) {
                String msg = "Exception while looking for get('" + this.property + "') method";
                this.log.error(msg, e);
                throw new VelocityException(msg, e);
            }
        }
    }

    public Object execute(Object o) {
        return ((Map)o).get(this.property);
    }
}

