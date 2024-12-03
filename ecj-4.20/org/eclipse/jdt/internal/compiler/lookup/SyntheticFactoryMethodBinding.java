/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;

public class SyntheticFactoryMethodBinding
extends MethodBinding {
    private MethodBinding staticFactoryFor;
    private LookupEnvironment environment;
    private ReferenceBinding enclosingType;

    public SyntheticFactoryMethodBinding(MethodBinding method, LookupEnvironment environment, ReferenceBinding enclosingType) {
        super(method.modifiers | 8, TypeConstants.SYNTHETIC_STATIC_FACTORY, null, null, null, method.declaringClass);
        this.environment = environment;
        this.staticFactoryFor = method;
        this.enclosingType = enclosingType;
    }

    public MethodBinding getConstructor() {
        return this.staticFactoryFor;
    }

    public ParameterizedMethodBinding applyTypeArgumentsOnConstructor(TypeBinding[] typeArguments, TypeBinding[] constructorTypeArguments, boolean inferredWithUncheckedConversion, TypeBinding targetType) {
        ParameterizedTypeBinding parameterizedType = typeArguments == null ? this.environment.createRawType(this.declaringClass, this.enclosingType) : this.environment.createParameterizedType(this.declaringClass, typeArguments, this.enclosingType);
        MethodBinding[] methodBindingArray = ((ReferenceBinding)parameterizedType).methods();
        int n = methodBindingArray.length;
        int n2 = 0;
        while (n2 < n) {
            MethodBinding closestMatch;
            MethodBinding parameterizedMethod = methodBindingArray[n2];
            if (parameterizedMethod.original() == this.staticFactoryFor) {
                return constructorTypeArguments.length > 0 || inferredWithUncheckedConversion ? this.environment.createParameterizedGenericMethod(parameterizedMethod, constructorTypeArguments, inferredWithUncheckedConversion, false, targetType) : (ParameterizedMethodBinding)parameterizedMethod;
            }
            if (parameterizedMethod instanceof ProblemMethodBinding && (closestMatch = ((ProblemMethodBinding)parameterizedMethod).closestMatch) instanceof ParameterizedMethodBinding && closestMatch.original() == this.staticFactoryFor) {
                return (ParameterizedMethodBinding)closestMatch;
            }
            ++n2;
        }
        throw new IllegalArgumentException("Type doesn't have its own method?");
    }
}

