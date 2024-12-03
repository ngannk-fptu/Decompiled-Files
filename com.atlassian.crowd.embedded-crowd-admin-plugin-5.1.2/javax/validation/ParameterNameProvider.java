/*
 * Decompiled with CFR 0.152.
 */
package javax.validation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

public interface ParameterNameProvider {
    public List<String> getParameterNames(Constructor<?> var1);

    public List<String> getParameterNames(Method var1);
}

