/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ReflectionUtils
 */
package org.springframework.data.querydsl;

import com.querydsl.core.types.EntityPath;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Optional;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

public class SimpleEntityPathResolver
implements EntityPathResolver {
    private static final String NO_CLASS_FOUND_TEMPLATE = "Did not find a query class %s for domain class %s!";
    private static final String NO_FIELD_FOUND_TEMPLATE = "Did not find a static field of the same type in %s!";
    public static final SimpleEntityPathResolver INSTANCE = new SimpleEntityPathResolver("");
    private final String querySuffix;

    public SimpleEntityPathResolver(String querySuffix) {
        Assert.notNull((Object)querySuffix, (String)"Query suffix must not be null!");
        this.querySuffix = querySuffix;
    }

    @Override
    public <T> EntityPath<T> createPath(Class<T> domainClass) {
        String pathClassName = this.getQueryClassName(domainClass);
        try {
            Class pathClass = ClassUtils.forName((String)pathClassName, (ClassLoader)domainClass.getClassLoader());
            return this.getStaticFieldOfType(pathClass).map(it -> (EntityPath)ReflectionUtils.getField((Field)it, null)).orElseThrow(() -> new IllegalStateException(String.format(NO_FIELD_FOUND_TEMPLATE, pathClass)));
        }
        catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(String.format(NO_CLASS_FOUND_TEMPLATE, pathClassName, domainClass.getName()), e);
        }
    }

    private Optional<Field> getStaticFieldOfType(Class<?> type) {
        for (Field field : type.getDeclaredFields()) {
            boolean isStatic = Modifier.isStatic(field.getModifiers());
            boolean hasSameType = type.equals(field.getType());
            if (!isStatic || !hasSameType) continue;
            return Optional.of(field);
        }
        Class<?> superclass = type.getSuperclass();
        return Object.class.equals(superclass) ? Optional.empty() : this.getStaticFieldOfType(superclass);
    }

    private String getQueryClassName(Class<?> domainClass) {
        String simpleClassName = ClassUtils.getShortName(domainClass);
        String packageName = domainClass.getPackage().getName();
        return String.format("%s%s.Q%s%s", packageName, this.querySuffix, this.getClassBase(simpleClassName), domainClass.getSimpleName());
    }

    private String getClassBase(String shortName) {
        String[] parts = shortName.split("\\.");
        return parts.length < 2 ? "" : parts[0] + "_";
    }
}

