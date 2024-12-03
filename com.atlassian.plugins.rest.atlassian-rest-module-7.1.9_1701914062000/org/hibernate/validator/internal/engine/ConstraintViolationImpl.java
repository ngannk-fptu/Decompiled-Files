/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ConstraintViolation
 *  javax.validation.Path
 *  javax.validation.metadata.ConstraintDescriptor
 */
package org.hibernate.validator.internal.engine;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;
import org.hibernate.validator.engine.HibernateConstraintViolation;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

public class ConstraintViolationImpl<T>
implements HibernateConstraintViolation<T>,
Serializable {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private static final long serialVersionUID = -4970067626703103139L;
    private final String interpolatedMessage;
    private final T rootBean;
    private final Object value;
    private final Path propertyPath;
    private final Object leafBeanInstance;
    private final ConstraintDescriptor<?> constraintDescriptor;
    private final String messageTemplate;
    private final Map<String, Object> messageParameters;
    private final Map<String, Object> expressionVariables;
    private final Class<T> rootBeanClass;
    private final ElementType elementType;
    private final Object[] executableParameters;
    private final Object executableReturnValue;
    private final Object dynamicPayload;
    private final int hashCode;

    public static <T> ConstraintViolation<T> forBeanValidation(String messageTemplate, Map<String, Object> messageParameters, Map<String, Object> expressionVariables, String interpolatedMessage, Class<T> rootBeanClass, T rootBean, Object leafBeanInstance, Object value, Path propertyPath, ConstraintDescriptor<?> constraintDescriptor, ElementType elementType, Object dynamicPayload) {
        return new ConstraintViolationImpl<T>(messageTemplate, messageParameters, expressionVariables, interpolatedMessage, rootBeanClass, rootBean, leafBeanInstance, value, propertyPath, constraintDescriptor, elementType, null, null, dynamicPayload);
    }

    public static <T> ConstraintViolation<T> forParameterValidation(String messageTemplate, Map<String, Object> messageParameters, Map<String, Object> expressionVariables, String interpolatedMessage, Class<T> rootBeanClass, T rootBean, Object leafBeanInstance, Object value, Path propertyPath, ConstraintDescriptor<?> constraintDescriptor, ElementType elementType, Object[] executableParameters, Object dynamicPayload) {
        return new ConstraintViolationImpl<T>(messageTemplate, messageParameters, expressionVariables, interpolatedMessage, rootBeanClass, rootBean, leafBeanInstance, value, propertyPath, constraintDescriptor, elementType, executableParameters, null, dynamicPayload);
    }

    public static <T> ConstraintViolation<T> forReturnValueValidation(String messageTemplate, Map<String, Object> messageParameters, Map<String, Object> expressionVariables, String interpolatedMessage, Class<T> rootBeanClass, T rootBean, Object leafBeanInstance, Object value, Path propertyPath, ConstraintDescriptor<?> constraintDescriptor, ElementType elementType, Object executableReturnValue, Object dynamicPayload) {
        return new ConstraintViolationImpl<T>(messageTemplate, messageParameters, expressionVariables, interpolatedMessage, rootBeanClass, rootBean, leafBeanInstance, value, propertyPath, constraintDescriptor, elementType, null, executableReturnValue, dynamicPayload);
    }

    private ConstraintViolationImpl(String messageTemplate, Map<String, Object> messageParameters, Map<String, Object> expressionVariables, String interpolatedMessage, Class<T> rootBeanClass, T rootBean, Object leafBeanInstance, Object value, Path propertyPath, ConstraintDescriptor<?> constraintDescriptor, ElementType elementType, Object[] executableParameters, Object executableReturnValue, Object dynamicPayload) {
        this.messageTemplate = messageTemplate;
        this.messageParameters = messageParameters;
        this.expressionVariables = expressionVariables;
        this.interpolatedMessage = interpolatedMessage;
        this.rootBean = rootBean;
        this.value = value;
        this.propertyPath = propertyPath;
        this.leafBeanInstance = leafBeanInstance;
        this.constraintDescriptor = constraintDescriptor;
        this.rootBeanClass = rootBeanClass;
        this.elementType = elementType;
        this.executableParameters = executableParameters;
        this.executableReturnValue = executableReturnValue;
        this.dynamicPayload = dynamicPayload;
        this.hashCode = this.createHashCode();
    }

    public final String getMessage() {
        return this.interpolatedMessage;
    }

    public final String getMessageTemplate() {
        return this.messageTemplate;
    }

    public Map<String, Object> getMessageParameters() {
        return this.messageParameters;
    }

    public Map<String, Object> getExpressionVariables() {
        return this.expressionVariables;
    }

    public final T getRootBean() {
        return this.rootBean;
    }

    public final Class<T> getRootBeanClass() {
        return this.rootBeanClass;
    }

    public final Object getLeafBean() {
        return this.leafBeanInstance;
    }

    public final Object getInvalidValue() {
        return this.value;
    }

    public final Path getPropertyPath() {
        return this.propertyPath;
    }

    public final ConstraintDescriptor<?> getConstraintDescriptor() {
        return this.constraintDescriptor;
    }

    public <C> C unwrap(Class<C> type) {
        if (type.isAssignableFrom(ConstraintViolation.class)) {
            return type.cast(this);
        }
        if (type.isAssignableFrom(HibernateConstraintViolation.class)) {
            return type.cast(this);
        }
        throw LOG.getTypeNotSupportedForUnwrappingException(type);
    }

    public Object[] getExecutableParameters() {
        return this.executableParameters;
    }

    public Object getExecutableReturnValue() {
        return this.executableReturnValue;
    }

    @Override
    public <C> C getDynamicPayload(Class<C> type) {
        if (this.dynamicPayload != null && type.isAssignableFrom(this.dynamicPayload.getClass())) {
            return type.cast(this.dynamicPayload);
        }
        return null;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ConstraintViolationImpl that = (ConstraintViolationImpl)o;
        if (this.interpolatedMessage != null ? !this.interpolatedMessage.equals(that.interpolatedMessage) : that.interpolatedMessage != null) {
            return false;
        }
        if (this.messageTemplate != null ? !this.messageTemplate.equals(that.messageTemplate) : that.messageTemplate != null) {
            return false;
        }
        if (this.propertyPath != null ? !this.propertyPath.equals(that.propertyPath) : that.propertyPath != null) {
            return false;
        }
        if (this.rootBean != null ? this.rootBean != that.rootBean : that.rootBean != null) {
            return false;
        }
        if (this.leafBeanInstance != null ? this.leafBeanInstance != that.leafBeanInstance : that.leafBeanInstance != null) {
            return false;
        }
        if (this.value != null ? this.value != that.value : that.value != null) {
            return false;
        }
        if (this.constraintDescriptor != null ? !this.constraintDescriptor.equals(that.constraintDescriptor) : that.constraintDescriptor != null) {
            return false;
        }
        return !(this.elementType != null ? !this.elementType.equals((Object)that.elementType) : that.elementType != null);
    }

    public int hashCode() {
        return this.hashCode;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ConstraintViolationImpl");
        sb.append("{interpolatedMessage='").append(this.interpolatedMessage).append('\'');
        sb.append(", propertyPath=").append(this.propertyPath);
        sb.append(", rootBeanClass=").append(this.rootBeanClass);
        sb.append(", messageTemplate='").append(this.messageTemplate).append('\'');
        sb.append('}');
        return sb.toString();
    }

    private int createHashCode() {
        int result = this.interpolatedMessage != null ? this.interpolatedMessage.hashCode() : 0;
        result = 31 * result + (this.propertyPath != null ? this.propertyPath.hashCode() : 0);
        result = 31 * result + System.identityHashCode(this.rootBean);
        result = 31 * result + System.identityHashCode(this.leafBeanInstance);
        result = 31 * result + System.identityHashCode(this.value);
        result = 31 * result + (this.constraintDescriptor != null ? this.constraintDescriptor.hashCode() : 0);
        result = 31 * result + (this.messageTemplate != null ? this.messageTemplate.hashCode() : 0);
        result = 31 * result + (this.elementType != null ? this.elementType.hashCode() : 0);
        return result;
    }
}

