/*
 * Decompiled with CFR 0.152.
 */
package javax.validation.executable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Set;
import javax.validation.ConstraintViolation;

public interface ExecutableValidator {
    public <T> Set<ConstraintViolation<T>> validateParameters(T var1, Method var2, Object[] var3, Class<?> ... var4);

    public <T> Set<ConstraintViolation<T>> validateReturnValue(T var1, Method var2, Object var3, Class<?> ... var4);

    public <T> Set<ConstraintViolation<T>> validateConstructorParameters(Constructor<? extends T> var1, Object[] var2, Class<?> ... var3);

    public <T> Set<ConstraintViolation<T>> validateConstructorReturnValue(Constructor<? extends T> var1, T var2, Class<?> ... var3);
}

