/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.CaseStatement;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.InnerEmulationDependency;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.NestedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;

public final class LocalTypeBinding
extends NestedTypeBinding {
    static final char[] LocalTypePrefix = new char[]{'$', 'L', 'o', 'c', 'a', 'l', '$'};
    private InnerEmulationDependency[] dependents;
    public CaseStatement enclosingCase;
    public int sourceStart;
    public MethodBinding enclosingMethod;

    public LocalTypeBinding(ClassScope scope, SourceTypeBinding enclosingType, CaseStatement switchCase) {
        super((char[][])new char[][]{CharOperation.concat(LocalTypePrefix, scope.referenceContext.name)}, scope, enclosingType);
        MethodScope lambdaScope;
        TypeDeclaration typeDeclaration = scope.referenceContext;
        this.tagBits = (typeDeclaration.bits & 0x200) != 0 ? (this.tagBits |= 0x834L) : (this.tagBits |= 0x814L);
        this.enclosingCase = switchCase;
        this.sourceStart = typeDeclaration.sourceStart;
        MethodScope methodScope = scope.enclosingMethodScope();
        MethodBinding methodBinding = methodScope.referenceMethodBinding();
        if (methodBinding != null) {
            this.enclosingMethod = methodBinding;
        }
        if ((lambdaScope = scope.enclosingLambdaScope()) != null) {
            ((LambdaExpression)lambdaScope.referenceContext).addLocalType(this);
        }
    }

    public LocalTypeBinding(LocalTypeBinding prototype) {
        super(prototype);
        this.dependents = prototype.dependents;
        this.enclosingCase = prototype.enclosingCase;
        this.sourceStart = prototype.sourceStart;
        this.enclosingMethod = prototype.enclosingMethod;
    }

    public void addInnerEmulationDependent(BlockScope dependentScope, boolean wasEnclosingInstanceSupplied) {
        int index;
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.dependents == null) {
            index = 0;
            this.dependents = new InnerEmulationDependency[1];
        } else {
            index = this.dependents.length;
            int i = 0;
            while (i < index) {
                if (this.dependents[i].scope == dependentScope) {
                    return;
                }
                ++i;
            }
            this.dependents = new InnerEmulationDependency[index + 1];
            System.arraycopy(this.dependents, 0, this.dependents, 0, index);
        }
        this.dependents[index] = new InnerEmulationDependency(dependentScope, wasEnclosingInstanceSupplied);
    }

    @Override
    public MethodBinding enclosingMethod() {
        return this.enclosingMethod;
    }

    public ReferenceBinding anonymousOriginalSuperType() {
        TypeReference typeReference;
        if (!this.isPrototype()) {
            return ((LocalTypeBinding)this.prototype).anonymousOriginalSuperType();
        }
        if (this.superclass == null && this.scope != null) {
            return this.scope.getJavaLangObject();
        }
        if (this.superInterfaces != Binding.NO_SUPERINTERFACES) {
            return this.superInterfaces[0];
        }
        if ((this.tagBits & 0x20000L) == 0L) {
            return this.superclass;
        }
        if (this.scope != null && (typeReference = this.scope.referenceContext.allocation.type) != null) {
            return (ReferenceBinding)typeReference.resolvedType;
        }
        return this.superclass;
    }

    @Override
    public char[] computeUniqueKey(boolean isLeaf) {
        if (!this.isPrototype()) {
            return this.prototype.computeUniqueKey(isLeaf);
        }
        char[] outerKey = this.outermostEnclosingType().computeUniqueKey(isLeaf);
        int semicolon = CharOperation.lastIndexOf(';', outerKey);
        StringBuffer sig = new StringBuffer();
        sig.append(outerKey, 0, semicolon);
        sig.append('$');
        sig.append(String.valueOf(this.sourceStart));
        if (!this.isAnonymousType()) {
            sig.append('$');
            sig.append(this.sourceName);
        }
        sig.append(outerKey, semicolon, outerKey.length - semicolon);
        int sigLength = sig.length();
        char[] uniqueKey = new char[sigLength];
        sig.getChars(0, sigLength, uniqueKey, 0);
        return uniqueKey;
    }

    @Override
    public char[] constantPoolName() {
        if (this.constantPoolName != null) {
            return this.constantPoolName;
        }
        if (!this.isPrototype()) {
            this.constantPoolName = this.prototype.constantPoolName();
            return this.constantPoolName;
        }
        if (this.constantPoolName == null && this.scope != null) {
            this.constantPoolName = this.scope.compilationUnitScope().computeConstantPoolName(this);
        }
        return this.constantPoolName;
    }

    @Override
    public TypeBinding clone(TypeBinding outerType) {
        LocalTypeBinding copy = new LocalTypeBinding(this);
        copy.enclosingType = (SourceTypeBinding)outerType;
        return copy;
    }

    @Override
    public int hashCode() {
        return this.enclosingType.hashCode();
    }

    @Override
    public char[] genericTypeSignature() {
        if (!this.isPrototype()) {
            return this.prototype.genericTypeSignature();
        }
        if (this.genericReferenceTypeSignature == null && this.constantPoolName == null) {
            if (this.isAnonymousType()) {
                this.setConstantPoolName(this.superclass().sourceName());
            } else {
                this.setConstantPoolName(this.sourceName());
            }
        }
        return super.genericTypeSignature();
    }

    @Override
    public char[] readableName() {
        char[] readableName = this.isAnonymousType() ? CharOperation.concat(TypeConstants.ANONYM_PREFIX, this.anonymousOriginalSuperType().readableName(), TypeConstants.ANONYM_SUFFIX) : (this.isMemberType() ? CharOperation.concat(this.enclosingType().readableName(), this.sourceName, '.') : this.sourceName);
        TypeVariableBinding[] typeVars = this.typeVariables();
        if (typeVars != Binding.NO_TYPE_VARIABLES) {
            StringBuffer nameBuffer = new StringBuffer(10);
            nameBuffer.append(readableName).append('<');
            int i = 0;
            int length = typeVars.length;
            while (i < length) {
                if (i > 0) {
                    nameBuffer.append(',');
                }
                nameBuffer.append(typeVars[i].readableName());
                ++i;
            }
            nameBuffer.append('>');
            int nameLength = nameBuffer.length();
            readableName = new char[nameLength];
            nameBuffer.getChars(0, nameLength, readableName, 0);
        }
        return readableName;
    }

    @Override
    public char[] shortReadableName() {
        char[] shortReadableName = this.isAnonymousType() ? CharOperation.concat(TypeConstants.ANONYM_PREFIX, this.anonymousOriginalSuperType().shortReadableName(), TypeConstants.ANONYM_SUFFIX) : (this.isMemberType() ? CharOperation.concat(this.enclosingType().shortReadableName(), this.sourceName, '.') : this.sourceName);
        TypeVariableBinding[] typeVars = this.typeVariables();
        if (typeVars != Binding.NO_TYPE_VARIABLES) {
            StringBuffer nameBuffer = new StringBuffer(10);
            nameBuffer.append(shortReadableName).append('<');
            int i = 0;
            int length = typeVars.length;
            while (i < length) {
                if (i > 0) {
                    nameBuffer.append(',');
                }
                nameBuffer.append(typeVars[i].shortReadableName());
                ++i;
            }
            nameBuffer.append('>');
            int nameLength = nameBuffer.length();
            shortReadableName = new char[nameLength];
            nameBuffer.getChars(0, nameLength, shortReadableName, 0);
        }
        return shortReadableName;
    }

    public void setAsMemberType() {
        if (!this.isPrototype()) {
            this.tagBits |= 0x80CL;
            ((LocalTypeBinding)this.prototype).setAsMemberType();
            return;
        }
        this.tagBits |= 0x80CL;
    }

    public void setConstantPoolName(char[] computedConstantPoolName) {
        if (!this.isPrototype()) {
            this.constantPoolName = computedConstantPoolName;
            ((LocalTypeBinding)this.prototype).setConstantPoolName(computedConstantPoolName);
            return;
        }
        this.constantPoolName = computedConstantPoolName;
    }

    public void transferConstantPoolNameTo(TypeBinding substType) {
        if (this.constantPoolName != null && substType instanceof LocalTypeBinding) {
            LocalTypeBinding substLocalType = (LocalTypeBinding)substType;
            if (substLocalType.constantPoolName == null) {
                substLocalType.setConstantPoolName(this.constantPoolName);
                this.scope.compilationUnitScope().constantPoolNameUsage.put(substLocalType.constantPoolName, substLocalType);
            }
        }
    }

    @Override
    public char[] signature() {
        if (!this.isPrototype()) {
            return this.prototype.signature();
        }
        if (this.signature == null && this.constantPoolName == null) {
            if (this.isAnonymousType()) {
                this.setConstantPoolName(this.superclass().sourceName());
            } else {
                this.setConstantPoolName(this.sourceName());
            }
        }
        return super.signature();
    }

    @Override
    public char[] sourceName() {
        if (this.isAnonymousType()) {
            return CharOperation.concat(TypeConstants.ANONYM_PREFIX, this.anonymousOriginalSuperType().sourceName(), TypeConstants.ANONYM_SUFFIX);
        }
        return this.sourceName;
    }

    @Override
    public String toString() {
        if (this.hasTypeAnnotations()) {
            return String.valueOf(this.annotatedDebugName()) + " (local)";
        }
        if (this.isAnonymousType()) {
            return "Anonymous type : " + super.toString();
        }
        if (this.isMemberType()) {
            return "Local member type : " + new String(this.sourceName()) + " " + super.toString();
        }
        return "Local type : " + new String(this.sourceName()) + " " + super.toString();
    }

    @Override
    public void updateInnerEmulationDependents() {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.dependents != null) {
            int i = 0;
            while (i < this.dependents.length) {
                InnerEmulationDependency dependency = this.dependents[i];
                dependency.scope.propagateInnerEmulation(this, dependency.wasEnclosingInstanceSupplied);
                ++i;
            }
        }
    }
}

