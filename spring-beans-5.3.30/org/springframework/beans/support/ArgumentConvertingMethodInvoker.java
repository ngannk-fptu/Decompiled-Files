/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.MethodInvoker
 *  org.springframework.util.ReflectionUtils
 */
package org.springframework.beans.support;

import java.beans.PropertyEditor;
import java.lang.reflect.Method;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.TypeMismatchException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MethodInvoker;
import org.springframework.util.ReflectionUtils;

public class ArgumentConvertingMethodInvoker
extends MethodInvoker {
    @Nullable
    private TypeConverter typeConverter;
    private boolean useDefaultConverter = true;

    public void setTypeConverter(@Nullable TypeConverter typeConverter) {
        this.typeConverter = typeConverter;
        this.useDefaultConverter = typeConverter == null;
    }

    @Nullable
    public TypeConverter getTypeConverter() {
        if (this.typeConverter == null && this.useDefaultConverter) {
            this.typeConverter = this.getDefaultTypeConverter();
        }
        return this.typeConverter;
    }

    protected TypeConverter getDefaultTypeConverter() {
        return new SimpleTypeConverter();
    }

    public void registerCustomEditor(Class<?> requiredType, PropertyEditor propertyEditor) {
        TypeConverter converter = this.getTypeConverter();
        if (!(converter instanceof PropertyEditorRegistry)) {
            throw new IllegalStateException("TypeConverter does not implement PropertyEditorRegistry interface: " + converter);
        }
        ((PropertyEditorRegistry)((Object)converter)).registerCustomEditor(requiredType, propertyEditor);
    }

    protected Method findMatchingMethod() {
        Method matchingMethod = super.findMatchingMethod();
        if (matchingMethod == null) {
            matchingMethod = this.doFindMatchingMethod(this.getArguments());
        }
        if (matchingMethod == null) {
            matchingMethod = this.doFindMatchingMethod(new Object[]{this.getArguments()});
        }
        return matchingMethod;
    }

    @Nullable
    protected Method doFindMatchingMethod(Object[] arguments) {
        TypeConverter converter = this.getTypeConverter();
        if (converter != null) {
            String targetMethod = this.getTargetMethod();
            Method matchingMethod = null;
            int argCount = arguments.length;
            Class targetClass = this.getTargetClass();
            Assert.state((targetClass != null ? 1 : 0) != 0, (String)"No target class set");
            Method[] candidates = ReflectionUtils.getAllDeclaredMethods((Class)targetClass);
            int minTypeDiffWeight = Integer.MAX_VALUE;
            Object[] argumentsToUse = null;
            for (Method candidate : candidates) {
                int typeDiffWeight;
                int parameterCount;
                if (!candidate.getName().equals(targetMethod) || (parameterCount = candidate.getParameterCount()) != argCount) continue;
                Class[] paramTypes = candidate.getParameterTypes();
                Object[] convertedArguments = new Object[argCount];
                boolean match = true;
                for (int j = 0; j < argCount && match; ++j) {
                    try {
                        convertedArguments[j] = converter.convertIfNecessary(arguments[j], paramTypes[j]);
                        continue;
                    }
                    catch (TypeMismatchException ex) {
                        match = false;
                    }
                }
                if (!match || (typeDiffWeight = ArgumentConvertingMethodInvoker.getTypeDifferenceWeight((Class[])paramTypes, (Object[])convertedArguments)) >= minTypeDiffWeight) continue;
                minTypeDiffWeight = typeDiffWeight;
                matchingMethod = candidate;
                argumentsToUse = convertedArguments;
            }
            if (matchingMethod != null) {
                this.setArguments(argumentsToUse);
                return matchingMethod;
            }
        }
        return null;
    }
}

