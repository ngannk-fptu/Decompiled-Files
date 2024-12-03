/*
 * Decompiled with CFR 0.152.
 */
package javax.validation;

import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;

public interface ConstraintViolation<T> {
    public String getMessage();

    public String getMessageTemplate();

    public T getRootBean();

    public Class<T> getRootBeanClass();

    public Object getLeafBean();

    public Object[] getExecutableParameters();

    public Object getExecutableReturnValue();

    public Path getPropertyPath();

    public Object getInvalidValue();

    public ConstraintDescriptor<?> getConstraintDescriptor();

    public <U> U unwrap(Class<U> var1);
}

