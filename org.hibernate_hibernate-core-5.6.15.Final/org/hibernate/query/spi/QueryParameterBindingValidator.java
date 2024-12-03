/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.TemporalType
 */
package org.hibernate.query.spi;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import javax.persistence.TemporalType;
import org.hibernate.type.Type;

public class QueryParameterBindingValidator {
    public static final QueryParameterBindingValidator INSTANCE = new QueryParameterBindingValidator();

    private QueryParameterBindingValidator() {
    }

    public <P> void validate(Type paramType, Object bind) {
        this.validate(paramType, bind, null);
    }

    public <P> void validate(Type paramType, Object bind, TemporalType temporalType) {
        if (bind == null || paramType == null) {
            return;
        }
        Class parameterType = paramType.getReturnedClass();
        if (parameterType == null) {
            return;
        }
        if (Collection.class.isInstance(bind) && !Collection.class.isAssignableFrom(parameterType)) {
            this.validateCollectionValuedParameterBinding(parameterType, (Collection)bind, temporalType);
        } else if (bind.getClass().isArray()) {
            this.validateArrayValuedParameterBinding(parameterType, bind, temporalType);
        } else if (!QueryParameterBindingValidator.isValidBindValue(parameterType, bind, temporalType)) {
            throw new IllegalArgumentException(String.format("Parameter value [%s] did not match expected type [%s (%s)]", bind, parameterType.getName(), this.extractName(temporalType)));
        }
    }

    private String extractName(TemporalType temporalType) {
        return temporalType == null ? "n/a" : temporalType.name();
    }

    private void validateCollectionValuedParameterBinding(Class parameterType, Collection value, TemporalType temporalType) {
        for (Object element : value) {
            if (QueryParameterBindingValidator.isValidBindValue(parameterType, element, temporalType)) continue;
            throw new IllegalArgumentException(String.format("Parameter value element [%s] did not match expected type [%s (%s)]", element, parameterType.getName(), this.extractName(temporalType)));
        }
    }

    private static boolean isValidBindValue(Class expectedType, Object value, TemporalType temporalType) {
        if (expectedType.isPrimitive()) {
            if (expectedType == Boolean.TYPE) {
                return Boolean.class.isInstance(value);
            }
            if (expectedType == Character.TYPE) {
                return Character.class.isInstance(value);
            }
            if (expectedType == Byte.TYPE) {
                return Byte.class.isInstance(value);
            }
            if (expectedType == Short.TYPE) {
                return Short.class.isInstance(value);
            }
            if (expectedType == Integer.TYPE) {
                return Integer.class.isInstance(value);
            }
            if (expectedType == Long.TYPE) {
                return Long.class.isInstance(value);
            }
            if (expectedType == Float.TYPE) {
                return Float.class.isInstance(value);
            }
            if (expectedType == Double.TYPE) {
                return Double.class.isInstance(value);
            }
            return false;
        }
        if (value == null) {
            return true;
        }
        if (expectedType.isInstance(value)) {
            return true;
        }
        if (temporalType != null) {
            boolean bindIsTemporal;
            boolean parameterDeclarationIsTemporal = Date.class.isAssignableFrom(expectedType) || Calendar.class.isAssignableFrom(expectedType);
            boolean bl = bindIsTemporal = Date.class.isInstance(value) || Calendar.class.isInstance(value);
            if (parameterDeclarationIsTemporal && bindIsTemporal) {
                return true;
            }
        }
        return false;
    }

    private void validateArrayValuedParameterBinding(Class parameterType, Object value, TemporalType temporalType) {
        if (!parameterType.isArray()) {
            throw new IllegalArgumentException(String.format("Encountered array-valued parameter binding, but was expecting [%s (%s)]", parameterType.getName(), this.extractName(temporalType)));
        }
        if (value.getClass().getComponentType().isPrimitive()) {
            if (!parameterType.getComponentType().isAssignableFrom(value.getClass().getComponentType())) {
                throw new IllegalArgumentException(String.format("Primitive array-valued parameter bind value type [%s] did not match expected type [%s (%s)]", value.getClass().getComponentType().getName(), parameterType.getName(), this.extractName(temporalType)));
            }
        } else {
            Object[] array;
            for (Object element : array = (Object[])value) {
                if (QueryParameterBindingValidator.isValidBindValue(parameterType.getComponentType(), element, temporalType)) continue;
                throw new IllegalArgumentException(String.format("Array-valued parameter value element [%s] did not match expected type [%s (%s)]", element, parameterType.getName(), this.extractName(temporalType)));
            }
        }
    }
}

