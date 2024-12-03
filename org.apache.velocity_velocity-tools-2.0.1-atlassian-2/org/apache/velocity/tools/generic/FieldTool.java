/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.runtime.log.Log
 */
package org.apache.velocity.tools.generic;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.tools.ClassUtils;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.generic.SafeConfig;
import org.apache.velocity.tools.generic.ValueParser;

@DefaultKey(value="field")
public class FieldTool
extends SafeConfig {
    public static final String INCLUDE_KEY = "include";
    public static final String STORE_DYNAMIC_KEY = "storeDynamicLookups";
    protected Log log;
    protected HashMap storage = new HashMap();
    protected boolean storeDynamicLookups = true;

    @Override
    protected void configure(ValueParser values) {
        this.log = (Log)values.getValue("log");
        String[] classnames = values.getStrings(INCLUDE_KEY);
        if (classnames != null) {
            for (String classname : classnames) {
                if (this.in(classname) != null) continue;
                throw new RuntimeException("Could not find " + classname + " in the classpath");
            }
        }
        this.storeDynamicLookups = values.getBoolean(STORE_DYNAMIC_KEY, this.storeDynamicLookups);
    }

    public Object get(String name) {
        Object o;
        block4: {
            o = this.storage.get(name);
            if (o instanceof MutableField) {
                return ((MutableField)o).getValue();
            }
            if (o == null && name.indexOf(46) > 0) {
                try {
                    return ClassUtils.getFieldValue(name);
                }
                catch (Exception e) {
                    if (this.log == null) break block4;
                    this.log.debug((Object)("Unable to retrieve value of field at " + name), (Throwable)e);
                }
            }
        }
        return o;
    }

    public FieldToolSub in(String classname) {
        try {
            return this.in(ClassUtils.getClass(classname));
        }
        catch (ClassNotFoundException cnfe) {
            return null;
        }
    }

    public FieldToolSub in(Object instance) {
        if (instance == null) {
            return null;
        }
        return this.in(instance.getClass());
    }

    public FieldToolSub in(Class clazz) {
        if (clazz == null) {
            return null;
        }
        Map<String, Object> results = this.inspect(clazz);
        if (this.storeDynamicLookups && !results.isEmpty()) {
            this.storage.putAll(results);
        }
        return new FieldToolSub(results);
    }

    protected Map<String, Object> inspect(Class clazz) {
        HashMap<String, Object> results = new HashMap<String, Object>();
        for (Field field : clazz.getFields()) {
            int mod = field.getModifiers();
            if (!Modifier.isStatic(mod) || !Modifier.isPublic(mod)) continue;
            if (this.log != null && this.log.isDebugEnabled() && results.containsKey(field.getName())) {
                this.log.debug((Object)("FieldTool: " + field.getName() + " is being overridden by " + clazz.getName()));
            }
            if (Modifier.isFinal(mod)) {
                results.put(field.getName(), FieldTool.retrieve(field, clazz, this.log));
                continue;
            }
            results.put(field.getName(), new MutableField(field, clazz, this.log));
        }
        return results;
    }

    protected static Object retrieve(Field field, Class clazz, Log log) {
        try {
            return field.get(clazz);
        }
        catch (IllegalAccessException iae) {
            if (log != null) {
                log.warn((Object)("IllegalAccessException while trying to access " + field.getName()), (Throwable)iae);
            }
            return null;
        }
    }

    public static class MutableField {
        private final Class clazz;
        private final Field field;
        private final Log log;

        public MutableField(Field f, Class c, Log l) {
            if (f == null || c == null) {
                throw new NullPointerException("Both Class and Field must NOT be null");
            }
            this.field = f;
            this.clazz = c;
            this.log = l;
        }

        public Object getValue() {
            return FieldTool.retrieve(this.field, this.clazz, this.log);
        }
    }

    public static class FieldToolSub {
        private final Map<String, Object> results;

        public FieldToolSub(Map<String, Object> results) {
            if (results == null) {
                throw new NullPointerException("Cannot create sub with null field results map");
            }
            this.results = results;
        }

        public Object get(String name) {
            Object o = this.results.get(name);
            if (o instanceof MutableField) {
                return ((MutableField)o).getValue();
            }
            return o;
        }

        public String toString() {
            return this.results.toString();
        }
    }
}

