/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.app;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import org.apache.velocity.util.ClassUtils;

public class FieldMethodizer {
    private HashMap fieldHash = new HashMap();

    public FieldMethodizer() {
    }

    public FieldMethodizer(String s) {
        try {
            this.addObject(s);
        }
        catch (Exception e) {
            System.err.println("Could not add " + s + " for field methodizing: " + e.getMessage());
        }
    }

    public FieldMethodizer(Object o) {
        try {
            this.addObject(o);
        }
        catch (Exception e) {
            System.err.println("Could not add " + o + " for field methodizing: " + e.getMessage());
        }
    }

    public void addObject(String s) throws Exception {
        this.inspect(ClassUtils.getClass(s));
    }

    public void addObject(Object o) throws Exception {
        this.inspect(o.getClass());
    }

    public Object get(String fieldName) {
        Object value = null;
        try {
            Field f = (Field)this.fieldHash.get(fieldName);
            if (f != null) {
                value = f.get(null);
            }
        }
        catch (IllegalAccessException e) {
            System.err.println("IllegalAccessException while trying to access " + fieldName + ": " + e.getMessage());
        }
        return value;
    }

    private void inspect(Class clas) {
        Field[] fields = clas.getFields();
        for (int i = 0; i < fields.length; ++i) {
            int mod = fields[i].getModifiers();
            if (!Modifier.isStatic(mod) || !Modifier.isPublic(mod)) continue;
            this.fieldHash.put(fields[i].getName(), fields[i]);
        }
    }
}

