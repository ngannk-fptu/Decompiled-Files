/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.expression.spel.ast;

import java.util.ArrayList;
import java.util.List;
import org.springframework.expression.PropertyAccessor;
import org.springframework.lang.Nullable;

public abstract class AstUtils {
    public static List<PropertyAccessor> getPropertyAccessorsToTry(@Nullable Class<?> targetType, List<PropertyAccessor> propertyAccessors) {
        ArrayList<PropertyAccessor> specificAccessors = new ArrayList<PropertyAccessor>();
        ArrayList<PropertyAccessor> generalAccessors = new ArrayList<PropertyAccessor>();
        for (PropertyAccessor resolver : propertyAccessors) {
            Class<?>[] targets = resolver.getSpecificTargetClasses();
            if (targets == null) {
                generalAccessors.add(resolver);
                continue;
            }
            if (targetType == null) continue;
            int pos = 0;
            for (Class<?> clazz : targets) {
                if (clazz == targetType) {
                    specificAccessors.add(pos++, resolver);
                    continue;
                }
                if (!clazz.isAssignableFrom(targetType)) continue;
                generalAccessors.add(resolver);
            }
        }
        ArrayList<PropertyAccessor> resolvers = new ArrayList<PropertyAccessor>(specificAccessors.size() + generalAccessors.size());
        resolvers.addAll(specificAccessors);
        resolvers.addAll(generalAccessors);
        return resolvers;
    }
}

