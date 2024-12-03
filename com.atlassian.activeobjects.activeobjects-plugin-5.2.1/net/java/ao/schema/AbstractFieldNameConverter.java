/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 */
package net.java.ao.schema;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import net.java.ao.Common;
import net.java.ao.RawEntity;
import net.java.ao.schema.AccessorFieldNameResolver;
import net.java.ao.schema.FieldNameConverter;
import net.java.ao.schema.FieldNameProcessor;
import net.java.ao.schema.FieldNameResolver;
import net.java.ao.schema.GetterFieldNameResolver;
import net.java.ao.schema.IsAFieldNameResolver;
import net.java.ao.schema.MutatorFieldNameResolver;
import net.java.ao.schema.NullFieldNameResolver;
import net.java.ao.schema.PrimaryKeyFieldNameResolver;
import net.java.ao.schema.RelationalFieldNameResolver;
import net.java.ao.schema.SetterFieldNameResolver;

public abstract class AbstractFieldNameConverter
implements FieldNameConverter,
FieldNameProcessor {
    private final List<FieldNameResolver> fieldNameResolvers;

    protected AbstractFieldNameConverter() {
        this(Lists.newArrayList((Object[])new FieldNameResolver[]{new RelationalFieldNameResolver(), new MutatorFieldNameResolver(), new AccessorFieldNameResolver(), new PrimaryKeyFieldNameResolver(), new GetterFieldNameResolver(), new SetterFieldNameResolver(), new IsAFieldNameResolver(), new NullFieldNameResolver()}));
    }

    protected AbstractFieldNameConverter(List<FieldNameResolver> fieldNameResolvers) {
        this.fieldNameResolvers = Objects.requireNonNull(fieldNameResolvers);
    }

    @Override
    public final String getName(Method method) {
        return this.getNameInternal(method, PolyTypeHandler.STRAIGHT);
    }

    @Override
    public final String getPolyTypeName(Method method) {
        return this.getNameInternal(method, PolyTypeHandler.POLY);
    }

    private String getNameInternal(Method method, PolyTypeHandler polyTypeHandler) {
        FieldNameResolver fieldNameResolver = this.findFieldNameResolver(Objects.requireNonNull(method));
        String resolved = fieldNameResolver.resolve(method);
        if (resolved == null) {
            return null;
        }
        if (!fieldNameResolver.transform()) {
            return resolved;
        }
        if (polyTypeHandler.equals((Object)PolyTypeHandler.POLY)) {
            return this.convertName(polyTypeHandler.handle(resolved));
        }
        EntityFieldNameHandler from = EntityFieldNameHandler.from(method);
        return this.convertName(from.handle(polyTypeHandler.handle(resolved)));
    }

    private FieldNameResolver findFieldNameResolver(final Method method) {
        return (FieldNameResolver)Iterables.find(this.fieldNameResolvers, (Predicate)new Predicate<FieldNameResolver>(){

            public boolean apply(FieldNameResolver input) {
                return input.accept(method);
            }
        });
    }

    private static boolean isAttributeOfTypeEntity(Method method) {
        Class<?> attributeTypeFromMethod = Common.getAttributeTypeFromMethod(method);
        return attributeTypeFromMethod != null && RawEntity.class.isAssignableFrom(attributeTypeFromMethod);
    }

    @Override
    public abstract String convertName(String var1);

    private static enum EntityFieldNameHandler {
        PRIMITIVE,
        ENTITY{

            @Override
            String handle(String s) {
                return s + "ID";
            }
        };


        String handle(String s) {
            return s;
        }

        static EntityFieldNameHandler from(Method m) {
            return EntityFieldNameHandler.from(AbstractFieldNameConverter.isAttributeOfTypeEntity(m));
        }

        static EntityFieldNameHandler from(boolean isEntity) {
            return isEntity ? ENTITY : PRIMITIVE;
        }
    }

    private static enum PolyTypeHandler {
        STRAIGHT,
        POLY{

            @Override
            String handle(String s) {
                return s + "Type";
            }
        };


        String handle(String s) {
            return s;
        }
    }
}

