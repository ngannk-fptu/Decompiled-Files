/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cglib.core.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.cglib.core.Customizer;
import org.springframework.cglib.core.KeyFactoryCustomizer;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class CustomizerRegistry {
    private final Class[] customizerTypes;
    private Map<Class, List<KeyFactoryCustomizer>> customizers = new HashMap<Class, List<KeyFactoryCustomizer>>();

    public CustomizerRegistry(Class[] customizerTypes) {
        this.customizerTypes = customizerTypes;
    }

    public void add(KeyFactoryCustomizer customizer2) {
        Class<?> klass = customizer2.getClass();
        for (Class type : this.customizerTypes) {
            if (!type.isAssignableFrom(klass)) continue;
            List<KeyFactoryCustomizer> list = this.customizers.get(type);
            if (list == null) {
                list = new ArrayList<KeyFactoryCustomizer>();
                this.customizers.put(type, list);
            }
            list.add(customizer2);
        }
    }

    public <T> List<T> get(Class<T> klass) {
        List<KeyFactoryCustomizer> list = this.customizers.get(klass);
        if (list == null) {
            return Collections.emptyList();
        }
        return list;
    }

    @Deprecated
    public static CustomizerRegistry singleton(Customizer customizer2) {
        CustomizerRegistry registry = new CustomizerRegistry(new Class[]{Customizer.class});
        registry.add(customizer2);
        return registry;
    }
}

