/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.InferenceContext18;
import org.eclipse.jdt.internal.compiler.lookup.InferenceVariable;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.Substitution;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;

public class InferenceSubstitution
extends Scope.Substitutor
implements Substitution {
    private LookupEnvironment environment;
    private InferenceVariable[] variables;
    private InvocationSite[] sites;

    public InferenceSubstitution(LookupEnvironment environment, InferenceVariable[] variables, InvocationSite site) {
        this.environment = environment;
        this.variables = variables;
        this.sites = new InvocationSite[]{site};
    }

    public InferenceSubstitution(InferenceContext18 context) {
        this(context.environment, context.inferenceVariables, context.currentInvocation);
    }

    public InferenceSubstitution addContext(InferenceContext18 otherContext) {
        InferenceSubstitution subst = new InferenceSubstitution(this.environment, null, null){

            @Override
            protected boolean isSameParameter(TypeBinding p1, TypeBinding originalType) {
                if (TypeBinding.equalsEquals(p1, originalType)) {
                    return true;
                }
                if (p1 instanceof TypeVariableBinding && originalType instanceof TypeVariableBinding) {
                    TypeVariableBinding var1 = (TypeVariableBinding)p1;
                    TypeVariableBinding var2 = (TypeVariableBinding)originalType;
                    Binding declaring1 = var1.declaringElement;
                    Binding declaring2 = var2.declaringElement;
                    if (declaring1 instanceof MethodBinding && declaring2 instanceof MethodBinding) {
                        declaring1 = ((MethodBinding)declaring1).original();
                        declaring2 = ((MethodBinding)declaring2).original();
                    }
                    return declaring1 == declaring2 && var1.rank == var2.rank;
                }
                return false;
            }
        };
        int l1 = this.sites.length;
        subst.sites = new InvocationSite[l1 + 1];
        System.arraycopy(this.sites, 0, subst.sites, 0, l1);
        subst.sites[l1] = otherContext.currentInvocation;
        subst.variables = this.variables;
        return subst;
    }

    @Override
    public TypeBinding substitute(Substitution substitution, TypeBinding originalType) {
        int i = 0;
        while (i < this.variables.length) {
            InferenceVariable variable = this.variables[i];
            if (variable.isFromInitialSubstitution && this.isInSites(variable.site) && this.isSameParameter(this.getP(i), originalType)) {
                if (this.environment.globalOptions.isAnnotationBasedNullAnalysisEnabled && originalType.hasNullTypeAnnotations()) {
                    return this.environment.createAnnotatedType(variable.withoutToplevelNullAnnotation(), originalType.getTypeAnnotations());
                }
                return variable;
            }
            ++i;
        }
        return super.substitute(substitution, originalType);
    }

    private boolean isInSites(InvocationSite otherSite) {
        int i = 0;
        while (i < this.sites.length) {
            if (InferenceContext18.isSameSite(this.sites[i], otherSite)) {
                return true;
            }
            ++i;
        }
        return false;
    }

    protected boolean isSameParameter(TypeBinding p1, TypeBinding originalType) {
        return TypeBinding.equalsEquals(p1, originalType);
    }

    protected TypeBinding getP(int i) {
        return this.variables[i].typeParameter;
    }

    @Override
    public TypeBinding substitute(TypeVariableBinding typeVariable) {
        ReferenceBinding superclass = typeVariable.superclass;
        ReferenceBinding[] superInterfaces = typeVariable.superInterfaces;
        boolean hasSubstituted = false;
        int i = 0;
        while (i < this.variables.length) {
            InferenceVariable variable = this.variables[i];
            TypeBinding pi = this.getP(i);
            if (TypeBinding.equalsEquals(pi, typeVariable)) {
                return variable;
            }
            if (TypeBinding.equalsEquals(pi, superclass)) {
                superclass = variable;
                hasSubstituted = true;
            } else if (superInterfaces != null) {
                int ifcLen = superInterfaces.length;
                int j = 0;
                while (j < ifcLen) {
                    if (TypeBinding.equalsEquals(pi, superInterfaces[j])) {
                        if (superInterfaces == typeVariable.superInterfaces) {
                            ReferenceBinding[] referenceBindingArray = superInterfaces;
                            superInterfaces = new ReferenceBinding[ifcLen];
                            System.arraycopy(referenceBindingArray, 0, superInterfaces, 0, ifcLen);
                        }
                        superInterfaces[j] = variable;
                        hasSubstituted = true;
                        break;
                    }
                    ++j;
                }
            }
            ++i;
        }
        if (hasSubstituted) {
            typeVariable = new TypeVariableBinding(typeVariable.sourceName, typeVariable.declaringElement, typeVariable.rank, this.environment);
            typeVariable.superclass = superclass;
            typeVariable.superInterfaces = superInterfaces;
            TypeBinding typeBinding = typeVariable.firstBound = superclass != null ? superclass : superInterfaces[0];
            if (typeVariable.firstBound.hasNullTypeAnnotations()) {
                typeVariable.tagBits |= 0x100000L;
            }
        }
        return typeVariable;
    }

    @Override
    public LookupEnvironment environment() {
        return this.environment;
    }

    @Override
    public boolean isRawSubstitution() {
        return false;
    }
}

