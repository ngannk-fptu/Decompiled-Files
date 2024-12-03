/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import java.util.Set;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.InferenceVariable;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MissingTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;

public class RawTypeBinding
extends ParameterizedTypeBinding {
    public RawTypeBinding(ReferenceBinding type, ReferenceBinding enclosingType, LookupEnvironment environment) {
        super(type, null, enclosingType, environment);
        ParameterizedTypeBinding parameterizedTypeBinding;
        this.tagBits &= 0xFFFFFFFFFFFFFF7FL;
        if ((type.tagBits & 0x80L) != 0L) {
            if (type instanceof MissingTypeBinding) {
                this.tagBits |= 0x80L;
            } else if (type instanceof ParameterizedTypeBinding && (parameterizedTypeBinding = (ParameterizedTypeBinding)type).genericType() instanceof MissingTypeBinding) {
                this.tagBits |= 0x80L;
            }
        }
        if (enclosingType != null && (enclosingType.tagBits & 0x80L) != 0L) {
            if (enclosingType instanceof MissingTypeBinding) {
                this.tagBits |= 0x80L;
            } else if (enclosingType instanceof ParameterizedTypeBinding && (parameterizedTypeBinding = (ParameterizedTypeBinding)enclosingType).genericType() instanceof MissingTypeBinding) {
                this.tagBits |= 0x80L;
            }
        }
        if (enclosingType == null || !this.hasEnclosingInstanceContext() || (enclosingType.modifiers & 0x40000000) == 0) {
            this.modifiers &= 0xBFFFFFFF;
        }
    }

    @Override
    public char[] computeUniqueKey(boolean isLeaf) {
        StringBuffer sig = new StringBuffer(10);
        if (this.isMemberType() && (this.enclosingType().isParameterizedType() || this.enclosingType().isRawType())) {
            char[] typeSig;
            if (!this.hasEnclosingInstanceContext()) {
                typeSig = this.enclosingType().signature();
                sig.append(typeSig, 0, typeSig.length - 1);
                sig.append('$');
            } else {
                typeSig = this.enclosingType().computeUniqueKey(false);
                sig.append(typeSig, 0, typeSig.length - 1);
                sig.append('.');
            }
            sig.append(this.sourceName());
            if (this.genericType().typeVariables() != Binding.NO_TYPE_VARIABLES) {
                sig.append('<').append('>');
            }
            sig.append(';');
        } else {
            sig.append(this.genericType().computeUniqueKey(false));
            sig.insert(sig.length() - 1, "<>");
        }
        int sigLength = sig.length();
        char[] uniqueKey = new char[sigLength];
        sig.getChars(0, sigLength, uniqueKey, 0);
        return uniqueKey;
    }

    @Override
    public TypeBinding clone(TypeBinding outerType) {
        return new RawTypeBinding(this.actualType(), (ReferenceBinding)outerType, this.environment);
    }

    @Override
    public TypeBinding withoutToplevelNullAnnotation() {
        if (!this.hasNullTypeAnnotations()) {
            return this;
        }
        ReferenceBinding unannotatedGenericType = (ReferenceBinding)this.environment.getUnannotatedType(this.genericType());
        AnnotationBinding[] newAnnotations = this.environment.filterNullTypeAnnotations(this.typeAnnotations);
        return this.environment.createRawType(unannotatedGenericType, this.enclosingType(), newAnnotations);
    }

    @Override
    public ParameterizedMethodBinding createParameterizedMethod(MethodBinding originalMethod) {
        if (originalMethod.typeVariables == Binding.NO_TYPE_VARIABLES || originalMethod.isStatic()) {
            return super.createParameterizedMethod(originalMethod);
        }
        return this.environment.createParameterizedGenericMethod(originalMethod, this);
    }

    @Override
    public boolean isParameterizedType() {
        return false;
    }

    @Override
    public int kind() {
        return 1028;
    }

    @Override
    public String debugName() {
        if (this.hasTypeAnnotations()) {
            return this.annotatedDebugName();
        }
        StringBuffer nameBuffer = new StringBuffer(10);
        nameBuffer.append(this.actualType().sourceName()).append("#RAW");
        return nameBuffer.toString();
    }

    @Override
    public String annotatedDebugName() {
        StringBuffer buffer = new StringBuffer(super.annotatedDebugName());
        buffer.append("#RAW");
        return buffer.toString();
    }

    @Override
    public char[] genericTypeSignature() {
        if (this.genericTypeSignature == null) {
            if ((this.modifiers & 0x40000000) == 0) {
                this.genericTypeSignature = this.genericType().signature();
            } else {
                StringBuffer sig = new StringBuffer(10);
                if (this.isMemberType() && this.hasEnclosingInstanceContext()) {
                    ReferenceBinding enclosing = this.enclosingType();
                    char[] typeSig = enclosing.genericTypeSignature();
                    sig.append(typeSig, 0, typeSig.length - 1);
                    if ((enclosing.modifiers & 0x40000000) != 0) {
                        sig.append('.');
                    } else {
                        sig.append('$');
                    }
                    sig.append(this.sourceName());
                } else {
                    char[] typeSig = this.genericType().signature();
                    sig.append(typeSig, 0, typeSig.length - 1);
                }
                sig.append(';');
                int sigLength = sig.length();
                this.genericTypeSignature = new char[sigLength];
                sig.getChars(0, sigLength, this.genericTypeSignature, 0);
            }
        }
        return this.genericTypeSignature;
    }

    @Override
    public boolean isEquivalentTo(TypeBinding otherType) {
        if (RawTypeBinding.equalsEquals(this, otherType) || RawTypeBinding.equalsEquals(this.erasure(), otherType)) {
            return true;
        }
        if (otherType == null) {
            return false;
        }
        switch (otherType.kind()) {
            case 516: 
            case 8196: {
                return ((WildcardBinding)otherType).boundCheck(this);
            }
            case 260: 
            case 1028: 
            case 2052: {
                return TypeBinding.equalsEquals(this.erasure(), otherType.erasure());
            }
        }
        return false;
    }

    @Override
    public boolean isProvablyDistinct(TypeBinding otherType) {
        if (TypeBinding.equalsEquals(this, otherType) || TypeBinding.equalsEquals(this.erasure(), otherType)) {
            return false;
        }
        if (otherType == null) {
            return true;
        }
        switch (otherType.kind()) {
            case 260: 
            case 1028: 
            case 2052: {
                return TypeBinding.notEquals(this.erasure(), otherType.erasure());
            }
        }
        return true;
    }

    @Override
    public boolean isSubtypeOf(TypeBinding right, boolean simulatingBugJDK8026527) {
        if (simulatingBugJDK8026527) {
            right = this.environment.convertToRawType(right.erasure(), false);
        }
        return super.isSubtypeOf(right, simulatingBugJDK8026527);
    }

    @Override
    public boolean isProperType(boolean admitCapture18) {
        ReferenceBinding actualType = this.actualType();
        return actualType != null && ((TypeBinding)actualType).isProperType(admitCapture18);
    }

    @Override
    protected void initializeArguments() {
        TypeVariableBinding[] typeVariables = this.genericType().typeVariables();
        int length = typeVariables.length;
        TypeBinding[] typeArguments = new TypeBinding[length];
        int i = 0;
        while (i < length) {
            typeArguments[i] = this.environment.convertToRawType(typeVariables[i].erasure(), false);
            ++i;
        }
        this.arguments = typeArguments;
    }

    @Override
    public ParameterizedTypeBinding capture(Scope scope, int start, int end) {
        return this;
    }

    @Override
    public TypeBinding uncapture(Scope scope) {
        return this;
    }

    @Override
    TypeBinding substituteInferenceVariable(InferenceVariable var, TypeBinding substituteType) {
        return this;
    }

    @Override
    public MethodBinding getSingleAbstractMethod(Scope scope, boolean replaceWildcards) {
        ReferenceBinding genericType;
        MethodBinding theAbstractMethod;
        int index;
        int n = index = replaceWildcards ? 0 : 1;
        if (this.singleAbstractMethod != null) {
            if (this.singleAbstractMethod[index] != null) {
                return this.singleAbstractMethod[index];
            }
        } else {
            this.singleAbstractMethod = new MethodBinding[2];
        }
        if ((theAbstractMethod = (genericType = this.genericType()).getSingleAbstractMethod(scope, replaceWildcards)) == null || !theAbstractMethod.isValidBinding()) {
            this.singleAbstractMethod[index] = theAbstractMethod;
            return this.singleAbstractMethod[index];
        }
        ReferenceBinding declaringType = (ReferenceBinding)scope.environment().convertToRawType(genericType, true);
        declaringType = (ReferenceBinding)declaringType.findSuperTypeOriginatingFrom(theAbstractMethod.declaringClass);
        MethodBinding[] choices = declaringType.getMethods(theAbstractMethod.selector);
        int i = 0;
        int length = choices.length;
        while (i < length) {
            MethodBinding method = choices[i];
            if (method.isAbstract() && !method.redeclaresPublicObjectMethod(scope)) {
                this.singleAbstractMethod[index] = method;
                break;
            }
            ++i;
        }
        return this.singleAbstractMethod[index];
    }

    @Override
    public boolean mentionsAny(TypeBinding[] parameters, int idx) {
        return false;
    }

    @Override
    public char[] readableName(boolean showGenerics) {
        char[] readableName = this.isMemberType() ? CharOperation.concat(this.enclosingType().readableName(showGenerics && this.hasEnclosingInstanceContext()), this.sourceName, '.') : CharOperation.concatWith(this.actualType().compoundName, '.');
        return readableName;
    }

    @Override
    public char[] shortReadableName(boolean showGenerics) {
        char[] shortReadableName = this.isMemberType() ? CharOperation.concat(this.enclosingType().shortReadableName(showGenerics && this.hasEnclosingInstanceContext()), this.sourceName, '.') : this.actualType().sourceName;
        return shortReadableName;
    }

    @Override
    void collectInferenceVariables(Set<InferenceVariable> variables) {
    }

    @Override
    public ReferenceBinding upwardsProjection(Scope scope, TypeBinding[] mentionedTypeVariables) {
        return this;
    }

    @Override
    public ReferenceBinding downwardsProjection(Scope scope, TypeBinding[] mentionedTypeVariables) {
        return this;
    }

    @Override
    public ReferenceBinding enclosingType() {
        return this.enclosingType;
    }
}

