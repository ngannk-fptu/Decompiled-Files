/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.AbstractPropertyAccessor
 *  org.springframework.beans.BeanUtils
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.ConfigurablePropertyAccessor
 *  org.springframework.beans.NotWritablePropertyException
 *  org.springframework.context.expression.MapAccessor
 *  org.springframework.core.CollectionFactory
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.core.convert.TypeDescriptor
 *  org.springframework.expression.AccessException
 *  org.springframework.expression.EvaluationContext
 *  org.springframework.expression.Expression
 *  org.springframework.expression.PropertyAccessor
 *  org.springframework.expression.TypedValue
 *  org.springframework.expression.spel.SpelEvaluationException
 *  org.springframework.expression.spel.SpelParserConfiguration
 *  org.springframework.expression.spel.standard.SpelExpressionParser
 *  org.springframework.expression.spel.support.SimpleEvaluationContext
 *  org.springframework.lang.NonNull
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.web.bind.WebDataBinder
 */
package org.springframework.data.web;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.AbstractPropertyAccessor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.context.expression.MapAccessor;
import org.springframework.core.CollectionFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.data.util.TypeInformation;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.bind.WebDataBinder;

class MapDataBinder
extends WebDataBinder {
    private final Class<?> type;
    private final ConversionService conversionService;

    public MapDataBinder(Class<?> type, ConversionService conversionService) {
        super(new HashMap());
        this.type = type;
        this.conversionService = conversionService;
    }

    @NonNull
    public Map<String, Object> getTarget() {
        Object target = super.getTarget();
        if (target == null) {
            throw new IllegalStateException("Target bean should never be null!");
        }
        return (Map)target;
    }

    protected ConfigurablePropertyAccessor getPropertyAccessor() {
        return new MapPropertyAccessor(this.type, (Map<String, Object>)this.getTarget(), this.conversionService);
    }

    private static class MapPropertyAccessor
    extends AbstractPropertyAccessor {
        private static final SpelExpressionParser PARSER = new SpelExpressionParser(new SpelParserConfiguration(false, true));
        private final Class<?> type;
        private final Map<String, Object> map;
        private final ConversionService conversionService;

        public MapPropertyAccessor(Class<?> type, Map<String, Object> map, ConversionService conversionService) {
            this.type = type;
            this.map = map;
            this.conversionService = conversionService;
        }

        public boolean isReadableProperty(String propertyName) {
            throw new UnsupportedOperationException();
        }

        public boolean isWritableProperty(String propertyName) {
            try {
                return this.getPropertyPath(propertyName) != null;
            }
            catch (PropertyReferenceException o_O) {
                return false;
            }
        }

        @Nullable
        public TypeDescriptor getPropertyTypeDescriptor(String propertyName) throws BeansException {
            throw new UnsupportedOperationException();
        }

        @Nullable
        public Object getPropertyValue(String propertyName) throws BeansException {
            throw new UnsupportedOperationException();
        }

        public void setPropertyValue(String propertyName, @Nullable Object value) throws BeansException {
            if (!this.isWritableProperty(propertyName)) {
                throw new NotWritablePropertyException(this.type, propertyName);
            }
            PropertyPath leafProperty = this.getPropertyPath(propertyName).getLeafProperty();
            TypeInformation<?> owningType = leafProperty.getOwningType();
            TypeInformation<?> propertyType = leafProperty.getTypeInformation();
            TypeInformation<?> typeInformation = propertyType = propertyName.endsWith("]") ? propertyType.getActualType() : propertyType;
            if (propertyType != null && this.conversionRequired(value, propertyType.getType())) {
                PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(owningType.getType(), (String)leafProperty.getSegment());
                if (descriptor == null) {
                    throw new IllegalStateException(String.format("Couldn't find PropertyDescriptor for %s on %s!", leafProperty.getSegment(), owningType.getType()));
                }
                MethodParameter methodParameter = new MethodParameter(descriptor.getReadMethod(), -1);
                TypeDescriptor typeDescriptor = TypeDescriptor.nested((MethodParameter)methodParameter, (int)0);
                if (typeDescriptor == null) {
                    throw new IllegalStateException(String.format("Couldn't obtain type descriptor for method parameter %s!", methodParameter));
                }
                value = this.conversionService.convert(value, TypeDescriptor.forObject((Object)value), typeDescriptor);
            }
            SimpleEvaluationContext context = SimpleEvaluationContext.forPropertyAccessors((PropertyAccessor[])new PropertyAccessor[]{new PropertyTraversingMapAccessor(this.type, this.conversionService)}).withConversionService(this.conversionService).withRootObject(this.map).build();
            Expression expression = PARSER.parseExpression(propertyName);
            try {
                expression.setValue((EvaluationContext)context, value);
            }
            catch (SpelEvaluationException o_O) {
                throw new NotWritablePropertyException(this.type, propertyName, "Could not write property!", (Throwable)o_O);
            }
        }

        private boolean conversionRequired(@Nullable Object source, Class<?> targetType) {
            if (source == null || targetType.isInstance(source)) {
                return false;
            }
            return this.conversionService.canConvert(source.getClass(), targetType);
        }

        private PropertyPath getPropertyPath(String propertyName) {
            String plainPropertyPath = propertyName.replaceAll("\\[.*?\\]", "");
            return PropertyPath.from(plainPropertyPath, this.type);
        }

        private static final class PropertyTraversingMapAccessor
        extends MapAccessor {
            private final ConversionService conversionService;
            private Class<?> type;

            public PropertyTraversingMapAccessor(Class<?> type, ConversionService conversionService) {
                Assert.notNull(type, (String)"Type must not be null!");
                Assert.notNull((Object)conversionService, (String)"ConversionService must not be null!");
                this.type = type;
                this.conversionService = conversionService;
            }

            public boolean canRead(EvaluationContext context, @Nullable Object target, String name) throws AccessException {
                return true;
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public TypedValue read(EvaluationContext context, @Nullable Object target, String name) throws AccessException {
                if (target == null) {
                    return TypedValue.NULL;
                }
                PropertyPath path = PropertyPath.from(name, this.type);
                try {
                    TypedValue typedValue = super.read(context, target, name);
                    return typedValue;
                }
                catch (AccessException o_O) {
                    Object emptyResult = path.isCollection() ? CollectionFactory.createCollection(List.class, (int)0) : CollectionFactory.createMap(Map.class, (int)0);
                    ((Map)target).put(name, emptyResult);
                    TypedValue typedValue = new TypedValue(emptyResult, this.getDescriptor(path, emptyResult));
                    return typedValue;
                }
                finally {
                    this.type = path.getType();
                }
            }

            private TypeDescriptor getDescriptor(PropertyPath path, Object emptyValue) {
                Class<?> actualPropertyType = path.getType();
                TypeDescriptor valueDescriptor = this.conversionService.canConvert(String.class, actualPropertyType) ? TypeDescriptor.valueOf(String.class) : TypeDescriptor.valueOf(HashMap.class);
                return path.isCollection() ? TypeDescriptor.collection(emptyValue.getClass(), (TypeDescriptor)valueDescriptor) : TypeDescriptor.map(emptyValue.getClass(), (TypeDescriptor)TypeDescriptor.valueOf(String.class), (TypeDescriptor)valueDescriptor);
            }
        }
    }
}

