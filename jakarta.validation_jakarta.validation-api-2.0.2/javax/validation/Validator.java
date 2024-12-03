/*
 * Decompiled with CFR 0.152.
 */
package javax.validation;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.executable.ExecutableValidator;
import javax.validation.metadata.BeanDescriptor;

public interface Validator {
    public <T> Set<ConstraintViolation<T>> validate(T var1, Class<?> ... var2);

    public <T> Set<ConstraintViolation<T>> validateProperty(T var1, String var2, Class<?> ... var3);

    public <T> Set<ConstraintViolation<T>> validateValue(Class<T> var1, String var2, Object var3, Class<?> ... var4);

    public BeanDescriptor getConstraintsForClass(Class<?> var1);

    public <T> T unwrap(Class<T> var1);

    public ExecutableValidator forExecutables();
}

