/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.Path
 *  javax.validation.metadata.ConstraintDescriptor
 */
package org.hibernate.validator.internal.engine;

import java.lang.invoke.MethodHandles;
import java.util.Map;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.messageinterpolation.HibernateMessageInterpolatorContext;

public class MessageInterpolatorContext
implements HibernateMessageInterpolatorContext {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final ConstraintDescriptor<?> constraintDescriptor;
    private final Object validatedValue;
    private final Class<?> rootBeanType;
    private final Path propertyPath;
    private final Map<String, Object> messageParameters;
    private final Map<String, Object> expressionVariables;

    public MessageInterpolatorContext(ConstraintDescriptor<?> constraintDescriptor, Object validatedValue, Class<?> rootBeanType, Path propertyPath, Map<String, Object> messageParameters, Map<String, Object> expressionVariables) {
        this.constraintDescriptor = constraintDescriptor;
        this.validatedValue = validatedValue;
        this.rootBeanType = rootBeanType;
        this.propertyPath = propertyPath;
        this.messageParameters = CollectionHelper.toImmutableMap(messageParameters);
        this.expressionVariables = CollectionHelper.toImmutableMap(expressionVariables);
    }

    public ConstraintDescriptor<?> getConstraintDescriptor() {
        return this.constraintDescriptor;
    }

    public Object getValidatedValue() {
        return this.validatedValue;
    }

    @Override
    public Class<?> getRootBeanType() {
        return this.rootBeanType;
    }

    @Override
    public Map<String, Object> getMessageParameters() {
        return this.messageParameters;
    }

    @Override
    public Map<String, Object> getExpressionVariables() {
        return this.expressionVariables;
    }

    @Override
    public Path getPropertyPath() {
        return this.propertyPath;
    }

    public <T> T unwrap(Class<T> type) {
        if (type.isAssignableFrom(HibernateMessageInterpolatorContext.class)) {
            return type.cast(this);
        }
        throw LOG.getTypeNotSupportedForUnwrappingException(type);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MessageInterpolatorContext that = (MessageInterpolatorContext)o;
        if (this.constraintDescriptor != null ? !this.constraintDescriptor.equals(that.constraintDescriptor) : that.constraintDescriptor != null) {
            return false;
        }
        if (this.rootBeanType != null ? !this.rootBeanType.equals(that.rootBeanType) : that.rootBeanType != null) {
            return false;
        }
        return !(this.validatedValue != null ? this.validatedValue != that.validatedValue : that.validatedValue != null);
    }

    public int hashCode() {
        int result = this.constraintDescriptor != null ? this.constraintDescriptor.hashCode() : 0;
        result = 31 * result + System.identityHashCode(this.validatedValue);
        result = 31 * result + (this.rootBeanType != null ? this.rootBeanType.hashCode() : 0);
        return result;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MessageInterpolatorContext");
        sb.append("{constraintDescriptor=").append(this.constraintDescriptor);
        sb.append(", validatedValue=").append(this.validatedValue);
        sb.append(", rootBeanType=").append(this.rootBeanType.getName());
        sb.append(", propertyPath=").append(this.propertyPath);
        sb.append(", messageParameters=").append(this.messageParameters);
        sb.append(", expressionVariables=").append(this.expressionVariables);
        sb.append('}');
        return sb.toString();
    }
}

