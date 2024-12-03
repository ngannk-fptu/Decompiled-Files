/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.binding;

import groovy.lang.GroovyObjectSupport;
import java.util.LinkedHashMap;
import java.util.Map;
import org.codehaus.groovy.binding.DeadEndObject;

class BindPathSnooper
extends GroovyObjectSupport {
    static final DeadEndObject DEAD_END = new DeadEndObject();
    Map<String, BindPathSnooper> fields = new LinkedHashMap<String, BindPathSnooper>();

    BindPathSnooper() {
    }

    @Override
    public Object getProperty(String property) {
        if (this.fields.containsKey(property)) {
            return this.fields.get(property);
        }
        BindPathSnooper snooper = new BindPathSnooper();
        this.fields.put(property, snooper);
        return snooper;
    }

    @Override
    public Object invokeMethod(String name, Object args) {
        return DEAD_END;
    }
}

