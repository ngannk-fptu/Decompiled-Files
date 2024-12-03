/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ParameterNameProvider
 */
package org.hibernate.validator.parameternameprovider;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import javax.validation.ParameterNameProvider;
import org.hibernate.validator.internal.util.CollectionHelper;

@Deprecated
public class ReflectionParameterNameProvider
implements ParameterNameProvider {
    public List<String> getParameterNames(Constructor<?> constructor) {
        return this.getParameterNames(constructor.getParameters());
    }

    public List<String> getParameterNames(Method method) {
        return this.getParameterNames(method.getParameters());
    }

    private List<String> getParameterNames(Parameter[] parameters) {
        ArrayList<String> parameterNames = CollectionHelper.newArrayList();
        for (Parameter parameter : parameters) {
            parameterNames.add(parameter.getName());
        }
        return parameterNames;
    }
}

