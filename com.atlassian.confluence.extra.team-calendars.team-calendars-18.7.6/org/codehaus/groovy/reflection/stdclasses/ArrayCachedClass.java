/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.reflection.stdclasses;

import groovy.lang.GString;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class ArrayCachedClass
extends CachedClass {
    public ArrayCachedClass(Class klazz, ClassInfo classInfo) {
        super(klazz, classInfo);
    }

    @Override
    public Object coerceArgument(Object argument) {
        Class<?> argumentClass = argument.getClass();
        if (argumentClass.getName().charAt(0) != '[') {
            return argument;
        }
        Class<?> argumentComponent = argumentClass.getComponentType();
        Class<?> paramComponent = this.getTheClass().getComponentType();
        if (paramComponent.isPrimitive()) {
            argument = DefaultTypeTransformation.convertToPrimitiveArray(argument, paramComponent);
        } else if (paramComponent == String.class && argument instanceof GString[]) {
            GString[] strings = (GString[])argument;
            String[] ret = new String[strings.length];
            for (int i = 0; i < strings.length; ++i) {
                ret[i] = strings[i].toString();
            }
            argument = ret;
        } else if (paramComponent == Object.class && argumentComponent.isPrimitive()) {
            argument = DefaultTypeTransformation.primitiveArrayBox(argument);
        }
        return argument;
    }
}

