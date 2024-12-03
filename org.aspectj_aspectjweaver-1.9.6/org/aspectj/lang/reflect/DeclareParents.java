/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.lang.reflect;

import java.lang.reflect.Type;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.TypePattern;

public interface DeclareParents {
    public AjType getDeclaringType();

    public TypePattern getTargetTypesPattern();

    public boolean isExtends();

    public boolean isImplements();

    public Type[] getParentTypes() throws ClassNotFoundException;
}

