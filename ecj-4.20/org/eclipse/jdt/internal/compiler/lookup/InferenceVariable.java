/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import java.util.Map;
import java.util.Set;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.InferenceContext18;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;

public class InferenceVariable
extends TypeVariableBinding {
    InvocationSite site;
    TypeBinding typeParameter;
    long nullHints;
    private InferenceVariable prototype;
    int varId;
    public boolean isFromInitialSubstitution;

    public static InferenceVariable get(TypeBinding typeParameter, int rank, InvocationSite site, Scope scope, ReferenceBinding object, boolean initial) {
        Map<InferenceVarKey, InferenceVariable> uniqueInferenceVariables = scope.compilationUnitScope().uniqueInferenceVariables;
        InferenceVariable var = null;
        InferenceVarKey key = null;
        if (site != null && typeParameter != null) {
            key = new InferenceVarKey(typeParameter, site, rank);
            var = uniqueInferenceVariables.get(key);
        }
        if (var == null) {
            int newVarId = uniqueInferenceVariables.size();
            var = new InferenceVariable(typeParameter, rank, newVarId, site, scope.environment(), object, initial);
            if (key != null) {
                uniqueInferenceVariables.put(key, var);
            }
        }
        return var;
    }

    private InferenceVariable(TypeBinding typeParameter, int parameterRank, int iVarId, InvocationSite site, LookupEnvironment environment, ReferenceBinding object, boolean initial) {
        this(typeParameter, parameterRank, site, InferenceVariable.makeName(typeParameter, iVarId), environment, object);
        this.varId = iVarId;
        this.isFromInitialSubstitution = initial;
    }

    private static char[] makeName(TypeBinding typeParameter, int iVarId) {
        if (typeParameter.getClass() == TypeVariableBinding.class) {
            return CharOperation.concat(typeParameter.shortReadableName(), Integer.toString(iVarId).toCharArray(), '#');
        }
        return CharOperation.concat(CharOperation.concat('(', typeParameter.shortReadableName(), ')'), Integer.toString(iVarId).toCharArray(), '#');
    }

    private InferenceVariable(TypeBinding typeParameter, int parameterRank, InvocationSite site, char[] sourceName, LookupEnvironment environment, ReferenceBinding object) {
        super(sourceName, null, parameterRank, environment);
        this.site = site;
        this.typeParameter = typeParameter;
        this.tagBits |= typeParameter.tagBits & 0x180000000000000L;
        if (typeParameter.isTypeVariable()) {
            TypeVariableBinding typeVariable = (TypeVariableBinding)typeParameter;
            if (typeVariable.firstBound != null) {
                long boundBits = typeVariable.firstBound.tagBits & 0x180000000000000L;
                if (boundBits == 0x100000000000000L) {
                    this.tagBits |= boundBits;
                } else {
                    this.nullHints |= boundBits;
                }
            }
        }
        this.superclass = object;
        this.prototype = this;
    }

    @Override
    public TypeBinding clone(TypeBinding enclosingType) {
        InferenceVariable clone = new InferenceVariable(this.typeParameter, this.rank, this.site, this.sourceName, this.environment, this.superclass);
        clone.tagBits = this.tagBits;
        clone.nullHints = this.nullHints;
        clone.varId = this.varId;
        clone.isFromInitialSubstitution = this.isFromInitialSubstitution;
        clone.prototype = this;
        return clone;
    }

    @Override
    public InferenceVariable prototype() {
        return this.prototype;
    }

    @Override
    public char[] constantPoolName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public PackageBinding getPackage() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCompatibleWith(TypeBinding right, Scope scope) {
        return true;
    }

    @Override
    public boolean isProperType(boolean admitCapture18) {
        return false;
    }

    @Override
    TypeBinding substituteInferenceVariable(InferenceVariable var, TypeBinding substituteType) {
        if (TypeBinding.equalsEquals(this, var)) {
            return substituteType;
        }
        return this;
    }

    @Override
    void collectInferenceVariables(Set<InferenceVariable> variables) {
        variables.add(this);
    }

    @Override
    public ReferenceBinding[] superInterfaces() {
        return Binding.NO_SUPERINTERFACES;
    }

    @Override
    public char[] qualifiedSourceName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public char[] sourceName() {
        return this.sourceName;
    }

    @Override
    public char[] readableName() {
        return this.sourceName;
    }

    @Override
    public boolean hasTypeBit(int bit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String debugName() {
        return String.valueOf(this.sourceName);
    }

    @Override
    public String toString() {
        return this.debugName();
    }

    @Override
    public int hashCode() {
        int code = this.typeParameter.hashCode() + 17 * this.rank;
        if (this.site != null) {
            code = 31 * code + this.site.sourceStart();
            code = 31 * code + this.site.sourceEnd();
        }
        return code;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof InferenceVariable)) {
            return false;
        }
        InferenceVariable other = (InferenceVariable)obj;
        return this.rank == other.rank && InferenceContext18.isSameSite(this.site, other.site) && TypeBinding.equalsEquals(this.typeParameter, other.typeParameter);
    }

    @Override
    public TypeBinding erasure() {
        if (this.superclass == null) {
            this.superclass = this.environment.getType(TypeConstants.JAVA_LANG_OBJECT);
        }
        return super.erasure();
    }

    static class InferenceVarKey {
        TypeBinding typeParameter;
        long position;
        int rank;

        InferenceVarKey(TypeBinding typeParameter, InvocationSite site, int rank) {
            this.typeParameter = typeParameter;
            this.position = ((long)site.sourceStart() << 32) + (long)site.sourceEnd();
            this.rank = rank;
        }

        public int hashCode() {
            int result = 1;
            result = 31 * result + (int)(this.position ^ this.position >>> 32);
            result = 31 * result + this.rank;
            result = 31 * result + this.typeParameter.id;
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof InferenceVarKey)) {
                return false;
            }
            InferenceVarKey other = (InferenceVarKey)obj;
            if (this.position != other.position) {
                return false;
            }
            if (this.rank != other.rank) {
                return false;
            }
            return !TypeBinding.notEquals(this.typeParameter, other.typeParameter);
        }
    }
}

