/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ArrayUtils
 */
package org.apache.bcel.util;

import java.util.HashMap;
import java.util.Map;
import org.apache.bcel.classfile.JavaClass;
import org.apache.commons.lang3.ArrayUtils;

public class ClassSet {
    private final Map<String, JavaClass> map = new HashMap<String, JavaClass>();

    public boolean add(JavaClass clazz) {
        return this.map.putIfAbsent(clazz.getClassName(), clazz) != null;
    }

    public boolean empty() {
        return this.map.isEmpty();
    }

    public String[] getClassNames() {
        return this.map.keySet().toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    public void remove(JavaClass clazz) {
        this.map.remove(clazz.getClassName());
    }

    public JavaClass[] toArray() {
        return this.map.values().toArray(JavaClass.EMPTY_ARRAY);
    }
}

