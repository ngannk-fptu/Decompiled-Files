/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.java.ao.PolymorphicTypeMapper;
import net.java.ao.RawEntity;
import net.java.ao.schema.PluralizedTableNameConverter;
import net.java.ao.schema.TableNameConverter;

public final class DefaultPolymorphicTypeMapper
implements PolymorphicTypeMapper {
    private final Map<Class<? extends RawEntity<?>>, String> mappings;
    private final Map<String, Set<Class<? extends RawEntity<?>>>> reverse;
    private Class<? extends RawEntity<?>>[] types;

    public DefaultPolymorphicTypeMapper(Class<? extends RawEntity<?>> ... types) {
        this(new HashMap());
        this.types = types;
    }

    public DefaultPolymorphicTypeMapper(Map<Class<? extends RawEntity<?>>, String> mappings) {
        this.mappings = mappings;
        this.reverse = new HashMap();
        this.createReverseMappings();
    }

    private void createReverseMappings() {
        this.reverse.clear();
        for (Class<? extends RawEntity<?>> clazz : this.mappings.keySet()) {
            String value = this.mappings.get(clazz);
            Set<Class<RawEntity<?>>> set = this.reverse.get(value);
            if (set == null) {
                set = new HashSet();
                this.reverse.put(value, set);
            }
            set.add(clazz);
        }
    }

    void resolveMappings(TableNameConverter converter) {
        if (this.types == null) {
            return;
        }
        while (converter instanceof PluralizedTableNameConverter) {
            converter = ((PluralizedTableNameConverter)converter).getDelegate();
        }
        for (Class<? extends RawEntity<?>> type : this.types) {
            this.mappings.put(type, converter.getName(type));
        }
        this.types = null;
        this.createReverseMappings();
    }

    @Override
    public String convert(Class<? extends RawEntity<?>> type) {
        String back = this.mappings.get(type);
        if (back == null) {
            return type.getName();
        }
        return back;
    }

    @Override
    public Class<? extends RawEntity<?>> invert(Class<? extends RawEntity<?>> parent, String type) {
        Set<Class<RawEntity<?>>> set = this.reverse.get(type);
        if (set != null && set.size() != 0) {
            for (Class<? extends RawEntity<?>> clazz : set) {
                if (!parent.isAssignableFrom(clazz)) continue;
                return clazz;
            }
        }
        try {
            return Class.forName(type);
        }
        catch (Throwable throwable) {
            throw new IllegalArgumentException("No valid inverse mapping for type value \"" + type + '\"');
        }
    }
}

