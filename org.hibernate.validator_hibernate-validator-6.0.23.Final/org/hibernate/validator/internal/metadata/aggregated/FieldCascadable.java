/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.metadata.aggregated;

import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.hibernate.validator.HibernateValidatorPermission;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.aggregated.CascadingMetaData;
import org.hibernate.validator.internal.metadata.aggregated.CascadingMetaDataBuilder;
import org.hibernate.validator.internal.metadata.facets.Cascadable;
import org.hibernate.validator.internal.util.ReflectionHelper;
import org.hibernate.validator.internal.util.privilegedactions.GetDeclaredField;

public class FieldCascadable
implements Cascadable {
    private final Field field;
    private final Type cascadableType;
    private final CascadingMetaData cascadingMetaData;

    FieldCascadable(Field field, CascadingMetaData cascadingMetaData) {
        this.field = field;
        this.cascadableType = ReflectionHelper.typeOf(field);
        this.cascadingMetaData = cascadingMetaData;
    }

    @Override
    public ElementType getElementType() {
        return ElementType.FIELD;
    }

    @Override
    public Type getCascadableType() {
        return this.cascadableType;
    }

    @Override
    public Object getValue(Object parent) {
        return ReflectionHelper.getValue(this.field, parent);
    }

    @Override
    public void appendTo(PathImpl path) {
        path.addPropertyNode(this.field.getName());
    }

    @Override
    public CascadingMetaData getCascadingMetaData() {
        return this.cascadingMetaData;
    }

    public static class Builder
    implements Cascadable.Builder {
        private final ValueExtractorManager valueExtractorManager;
        private final Field field;
        private CascadingMetaDataBuilder cascadingMetaDataBuilder;

        public Builder(ValueExtractorManager valueExtractorManager, Field field, CascadingMetaDataBuilder cascadingMetaDataBuilder) {
            this.valueExtractorManager = valueExtractorManager;
            this.field = field;
            this.cascadingMetaDataBuilder = cascadingMetaDataBuilder;
        }

        @Override
        public void mergeCascadingMetaData(CascadingMetaDataBuilder cascadingMetaData) {
            this.cascadingMetaDataBuilder = this.cascadingMetaDataBuilder.merge(cascadingMetaData);
        }

        @Override
        public FieldCascadable build() {
            return new FieldCascadable(this.getAccessible(this.field), this.cascadingMetaDataBuilder.build(this.valueExtractorManager, this.field));
        }

        private Field getAccessible(Field original) {
            SecurityManager sm = System.getSecurityManager();
            if (sm != null) {
                sm.checkPermission(HibernateValidatorPermission.ACCESS_PRIVATE_MEMBERS);
            }
            Class<?> clazz = original.getDeclaringClass();
            return this.run(GetDeclaredField.andMakeAccessible(clazz, original.getName()));
        }

        private <T> T run(PrivilegedAction<T> action) {
            return System.getSecurityManager() != null ? AccessController.doPrivileged(action) : action.run();
        }
    }
}

