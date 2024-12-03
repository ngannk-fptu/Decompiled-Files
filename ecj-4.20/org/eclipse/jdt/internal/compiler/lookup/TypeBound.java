/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.lookup.InferenceSubstitution;
import org.eclipse.jdt.internal.compiler.lookup.InferenceVariable;
import org.eclipse.jdt.internal.compiler.lookup.ReductionResult;
import org.eclipse.jdt.internal.compiler.lookup.Substitution;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class TypeBound
extends ReductionResult {
    InferenceVariable left;
    boolean isSoft;
    long nullHints;

    static TypeBound createBoundOrDependency(InferenceSubstitution theta, TypeBinding type, InferenceVariable variable) {
        return new TypeBound(variable, theta.substitute((Substitution)theta, type), 2, true);
    }

    TypeBound(InferenceVariable inferenceVariable, TypeBinding typeBinding, int relation) {
        this(inferenceVariable, typeBinding, relation, false);
    }

    TypeBound(InferenceVariable inferenceVariable, TypeBinding typeBinding, int relation, boolean isSoft) {
        this.left = inferenceVariable;
        this.right = typeBinding;
        if (((inferenceVariable.tagBits | this.right.tagBits) & 0x180000000000000L) != 0L) {
            if ((inferenceVariable.tagBits & 0x180000000000000L) == (this.right.tagBits & 0x180000000000000L)) {
                this.left = (InferenceVariable)inferenceVariable.withoutToplevelNullAnnotation();
                this.right = this.right.withoutToplevelNullAnnotation();
            } else {
                long mask = 0L;
                switch (relation) {
                    case 4: {
                        mask = 0x180000000000000L;
                        break;
                    }
                    case 2: {
                        mask = 0x100000000000000L;
                        break;
                    }
                    case 3: {
                        mask = 0x80000000000000L;
                    }
                }
                inferenceVariable.prototype().nullHints |= this.right.tagBits & mask;
            }
        }
        this.relation = relation;
        this.isSoft = isSoft;
    }

    boolean isBound() {
        return this.right.isProperType(true);
    }

    public int hashCode() {
        return this.left.hashCode() + this.right.hashCode() + this.relation;
    }

    public boolean equals(Object obj) {
        if (obj instanceof TypeBound) {
            TypeBound other = (TypeBound)obj;
            return this.relation == other.relation && TypeBinding.equalsEquals(this.left, other.left) && TypeBinding.equalsEquals(this.right, other.right);
        }
        return false;
    }

    public String toString() {
        boolean isBound = this.right.isProperType(true);
        StringBuffer buf = new StringBuffer();
        buf.append(isBound ? "TypeBound  " : "Dependency ");
        buf.append(this.left.sourceName);
        buf.append(TypeBound.relationToString(this.relation));
        buf.append(this.right.readableName());
        return buf.toString();
    }
}

