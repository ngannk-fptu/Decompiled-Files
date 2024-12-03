/*
 * Decompiled with CFR 0.152.
 */
package net.minidev.asm;

import java.util.HashMap;
import java.util.LinkedHashSet;
import net.minidev.asm.ConvertDate;
import net.minidev.asm.DefaultConverter;

public class BeansAccessConfig {
    protected static HashMap<Class<?>, LinkedHashSet<Class<?>>> classMapper = new HashMap();
    protected static HashMap<Class<?>, HashMap<String, String>> classFiledNameMapper = new HashMap();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void addTypeMapper(Class<?> clz, Class<?> mapper) {
        HashMap<Class<?>, LinkedHashSet<Class<?>>> hashMap = classMapper;
        synchronized (hashMap) {
            LinkedHashSet<Class<Object>> h = classMapper.get(clz);
            if (h == null) {
                h = new LinkedHashSet();
                classMapper.put(clz, h);
            }
            h.add(mapper);
        }
    }

    static {
        BeansAccessConfig.addTypeMapper(Object.class, DefaultConverter.class);
        BeansAccessConfig.addTypeMapper(Object.class, ConvertDate.class);
    }
}

