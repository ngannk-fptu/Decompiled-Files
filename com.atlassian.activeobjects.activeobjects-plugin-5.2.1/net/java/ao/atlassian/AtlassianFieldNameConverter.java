/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.java.ao.atlassian;

import com.google.common.collect.Lists;
import java.lang.reflect.Method;
import java.util.Objects;
import net.java.ao.atlassian.ConverterUtils;
import net.java.ao.schema.AbstractFieldNameConverter;
import net.java.ao.schema.AccessorFieldNameResolver;
import net.java.ao.schema.Case;
import net.java.ao.schema.FieldNameConverter;
import net.java.ao.schema.FieldNameProcessor;
import net.java.ao.schema.FieldNameResolver;
import net.java.ao.schema.GetterFieldNameResolver;
import net.java.ao.schema.IgnoredFieldNameResolver;
import net.java.ao.schema.IsAFieldNameResolver;
import net.java.ao.schema.MutatorFieldNameResolver;
import net.java.ao.schema.NullFieldNameResolver;
import net.java.ao.schema.PrimaryKeyFieldNameResolver;
import net.java.ao.schema.RelationalFieldNameResolver;
import net.java.ao.schema.SetterFieldNameResolver;
import net.java.ao.schema.UnderscoreFieldNameConverter;

public final class AtlassianFieldNameConverter
implements FieldNameConverter,
FieldNameProcessor {
    private AbstractFieldNameConverter fieldNameConverter = new UnderscoreFieldNameConverter(Case.UPPER, Lists.newArrayList((Object[])new FieldNameResolver[]{new IgnoredFieldNameResolver(), new RelationalFieldNameResolver(), new TransformingFieldNameResolver(new MutatorFieldNameResolver()), new TransformingFieldNameResolver(new AccessorFieldNameResolver()), new TransformingFieldNameResolver(new PrimaryKeyFieldNameResolver()), new GetterFieldNameResolver(), new SetterFieldNameResolver(), new IsAFieldNameResolver(), new NullFieldNameResolver()}));

    @Override
    public String getName(Method method) {
        String name = this.fieldNameConverter.getName(method);
        return ConverterUtils.checkLength(name, "Invalid entity, generated field name (" + name + ") for method '" + method + "' is too long! It should be no longer than " + 30 + " chars.");
    }

    @Override
    public String getPolyTypeName(Method method) {
        String name = this.fieldNameConverter.getPolyTypeName(method);
        return ConverterUtils.checkLength(name, "Invalid entity, generated field polymorphic type name (" + name + ") for method '" + method + "' is too long! It should be no longer than " + 30 + " chars.");
    }

    @Override
    public String convertName(String name) {
        return this.fieldNameConverter.convertName(name);
    }

    private static final class TransformingFieldNameResolver
    implements FieldNameResolver {
        private final FieldNameResolver delegate;

        public TransformingFieldNameResolver(FieldNameResolver delegate) {
            this.delegate = Objects.requireNonNull(delegate, "delegate can't be null");
        }

        @Override
        public boolean accept(Method method) {
            return this.delegate.accept(method);
        }

        @Override
        public String resolve(Method method) {
            return this.delegate.resolve(method);
        }

        @Override
        public boolean transform() {
            return true;
        }
    }
}

