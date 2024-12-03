/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.parser.node;

import java.lang.reflect.Method;
import java.util.Map;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.runtime.parser.node.AbstractExecutor;

public class MapGetExecutor
extends AbstractExecutor {
    private final String property;
    private final boolean isAlive;

    public MapGetExecutor(Log log, Object object, String property) {
        this.log = log;
        this.property = property;
        this.isAlive = this.discover(object);
    }

    @Override
    public Method getMethod() {
        if (this.isAlive()) {
            return MapGetMethod.instance();
        }
        return null;
    }

    @Override
    public boolean isAlive() {
        return this.isAlive;
    }

    protected boolean discover(Object object) {
        return object instanceof Map && this.property != null;
    }

    @Override
    public Object execute(Object o) {
        return ((Map)o).get(this.property);
    }

    private static final class MapGetMethod {
        private static final Method instance;

        private MapGetMethod() {
        }

        static Method instance() {
            return instance;
        }

        static {
            try {
                instance = Map.class.getMethod("get", Object.class);
            }
            catch (NoSuchMethodException mapGetMethodMissingError) {
                throw new Error(mapGetMethodMissingError);
            }
        }
    }
}

