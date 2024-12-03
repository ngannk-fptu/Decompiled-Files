/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.data.mapping.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import org.springframework.util.Assert;

public class SimpleTypeHolder {
    private static final Set<Class<?>> DEFAULTS;
    public static final SimpleTypeHolder DEFAULT;
    private volatile Map<Class<?>, Boolean> simpleTypes;

    protected SimpleTypeHolder() {
        this(Collections.emptySet(), true);
    }

    public SimpleTypeHolder(Set<? extends Class<?>> customSimpleTypes, boolean registerDefaults) {
        Assert.notNull(customSimpleTypes, (String)"CustomSimpleTypes must not be null!");
        this.simpleTypes = new WeakHashMap(customSimpleTypes.size() + DEFAULTS.size());
        this.register(customSimpleTypes);
        if (registerDefaults) {
            this.register(DEFAULTS);
        }
    }

    public SimpleTypeHolder(Set<? extends Class<?>> customSimpleTypes, SimpleTypeHolder source) {
        Assert.notNull(customSimpleTypes, (String)"CustomSimpleTypes must not be null!");
        Assert.notNull((Object)source, (String)"SourceTypeHolder must not be null!");
        this.simpleTypes = new WeakHashMap(customSimpleTypes.size() + source.simpleTypes.size());
        this.register(customSimpleTypes);
        this.registerCachePositives(source.simpleTypes);
    }

    private void registerCachePositives(Map<Class<?>, Boolean> source) {
        for (Map.Entry<Class<?>, Boolean> entry : source.entrySet()) {
            if (!entry.getValue().booleanValue()) continue;
            this.simpleTypes.put(entry.getKey(), true);
        }
    }

    public boolean isSimpleType(Class<?> type) {
        Assert.notNull(type, (String)"Type must not be null!");
        Map<Class<?>, Boolean> localSimpleTypes = this.simpleTypes;
        Boolean isSimpleType = localSimpleTypes.get(type);
        if (Object.class.equals(type) || Enum.class.isAssignableFrom(type)) {
            return true;
        }
        if (isSimpleType != null) {
            return isSimpleType;
        }
        String typeName = type.getName();
        if (typeName.startsWith("java.lang") || type.getName().startsWith("java.time") || typeName.equals("kotlin.Unit")) {
            return true;
        }
        for (Class<?> simpleType : localSimpleTypes.keySet()) {
            if (!simpleType.isAssignableFrom(type)) continue;
            isSimpleType = localSimpleTypes.get(simpleType);
            this.simpleTypes = SimpleTypeHolder.put(localSimpleTypes, type, isSimpleType);
            return isSimpleType;
        }
        this.simpleTypes = SimpleTypeHolder.put(localSimpleTypes, type, false);
        return false;
    }

    private void register(Collection<? extends Class<?>> types) {
        types.forEach(customSimpleType -> this.simpleTypes.put((Class<?>)customSimpleType, true));
    }

    private static Map<Class<?>, Boolean> put(Map<Class<?>, Boolean> simpleTypes, Class<?> type, boolean isSimpleType) {
        WeakHashMap copy = new WeakHashMap(simpleTypes);
        copy.put(type, isSimpleType);
        return copy;
    }

    static {
        HashSet<Class<Object>> defaults = new HashSet<Class<Object>>();
        defaults.add(Boolean.TYPE);
        defaults.add(boolean[].class);
        defaults.add(Long.TYPE);
        defaults.add(long[].class);
        defaults.add(Short.TYPE);
        defaults.add(short[].class);
        defaults.add(Integer.TYPE);
        defaults.add(int[].class);
        defaults.add(Byte.TYPE);
        defaults.add(byte[].class);
        defaults.add(Float.TYPE);
        defaults.add(float[].class);
        defaults.add(Double.TYPE);
        defaults.add(double[].class);
        defaults.add(Character.TYPE);
        defaults.add(char[].class);
        defaults.add(Boolean.class);
        defaults.add(Long.class);
        defaults.add(Short.class);
        defaults.add(Integer.class);
        defaults.add(Byte.class);
        defaults.add(Float.class);
        defaults.add(Double.class);
        defaults.add(Character.class);
        defaults.add(String.class);
        defaults.add(Date.class);
        defaults.add(Locale.class);
        defaults.add(Class.class);
        defaults.add(Enum.class);
        DEFAULTS = Collections.unmodifiableSet(defaults);
        DEFAULT = new SimpleTypeHolder();
    }
}

