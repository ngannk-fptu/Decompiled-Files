/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;

public class InferenceContext {
    private TypeBinding[][][] collectedSubstitutes;
    MethodBinding genericMethod;
    int depth;
    int status;
    TypeBinding expectedType;
    boolean hasExplicitExpectedType;
    public boolean isUnchecked;
    TypeBinding[] substitutes;
    static final int FAILED = 1;

    public InferenceContext(MethodBinding genericMethod) {
        this.genericMethod = genericMethod;
        TypeVariableBinding[] typeVariables = genericMethod.typeVariables;
        int varLength = typeVariables.length;
        this.collectedSubstitutes = new TypeBinding[varLength][3][];
        this.substitutes = new TypeBinding[varLength];
    }

    public TypeBinding[] getSubstitutes(TypeVariableBinding typeVariable, int constraint) {
        return this.collectedSubstitutes[typeVariable.rank][constraint];
    }

    public boolean hasUnresolvedTypeArgument() {
        int i = 0;
        int varLength = this.substitutes.length;
        while (i < varLength) {
            if (this.substitutes[i] == null) {
                return true;
            }
            ++i;
        }
        return false;
    }

    public void recordSubstitute(TypeVariableBinding typeVariable, TypeBinding actualType, int constraint) {
        block5: {
            int length;
            TypeBinding[][] variableSubstitutes = this.collectedSubstitutes[typeVariable.rank];
            TypeBinding[] constraintSubstitutes = variableSubstitutes[constraint];
            if (constraintSubstitutes == null) {
                length = 0;
                constraintSubstitutes = new TypeBinding[1];
            } else {
                length = constraintSubstitutes.length;
                int i = 0;
                while (i < length) {
                    TypeBinding substitute = constraintSubstitutes[i];
                    if (substitute == actualType) {
                        return;
                    }
                    if (substitute == null) {
                        constraintSubstitutes[i] = actualType;
                        break block5;
                    }
                    ++i;
                }
                TypeBinding[] typeBindingArray = constraintSubstitutes;
                constraintSubstitutes = new TypeBinding[length + 1];
                System.arraycopy(typeBindingArray, 0, constraintSubstitutes, 0, length);
            }
            constraintSubstitutes[length] = actualType;
            variableSubstitutes[constraint] = constraintSubstitutes;
        }
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer(20);
        buffer.append("InferenceContex for ");
        int i = 0;
        int length = this.genericMethod.typeVariables.length;
        while (i < length) {
            buffer.append(this.genericMethod.typeVariables[i]);
            ++i;
        }
        buffer.append(this.genericMethod);
        buffer.append("\n\t[status=");
        switch (this.status) {
            case 0: {
                buffer.append("ok]");
                break;
            }
            case 1: {
                buffer.append("failed]");
            }
        }
        if (this.expectedType == null) {
            buffer.append(" [expectedType=null]");
        } else {
            buffer.append(" [expectedType=").append(this.expectedType.shortReadableName()).append(']');
        }
        buffer.append(" [depth=").append(this.depth).append(']');
        buffer.append("\n\t[collected={");
        i = 0;
        length = this.collectedSubstitutes == null ? 0 : this.collectedSubstitutes.length;
        while (i < length) {
            TypeBinding[][] collected = this.collectedSubstitutes[i];
            int j = 0;
            while (j <= 2) {
                TypeBinding[] constraintCollected = collected[j];
                if (constraintCollected != null) {
                    int k = 0;
                    int clength = constraintCollected.length;
                    while (k < clength) {
                        buffer.append("\n\t\t").append(this.genericMethod.typeVariables[i].sourceName);
                        switch (j) {
                            case 0: {
                                buffer.append("=");
                                break;
                            }
                            case 1: {
                                buffer.append("<:");
                                break;
                            }
                            case 2: {
                                buffer.append(">:");
                            }
                        }
                        if (constraintCollected[k] != null) {
                            buffer.append(constraintCollected[k].shortReadableName());
                        }
                        ++k;
                    }
                }
                ++j;
            }
            ++i;
        }
        buffer.append("}]");
        buffer.append("\n\t[inferred=");
        int count = 0;
        int i2 = 0;
        int length2 = this.substitutes == null ? 0 : this.substitutes.length;
        while (i2 < length2) {
            if (this.substitutes[i2] != null) {
                ++count;
                buffer.append('{').append(this.genericMethod.typeVariables[i2].sourceName);
                buffer.append("=").append(this.substitutes[i2].shortReadableName()).append('}');
            }
            ++i2;
        }
        if (count == 0) {
            buffer.append("{}");
        }
        buffer.append(']');
        return buffer.toString();
    }
}

