/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.verifier.structurals;

import java.util.Arrays;
import org.apache.bcel.generic.ReferenceType;
import org.apache.bcel.generic.Type;
import org.apache.bcel.verifier.exc.AssertionViolatedException;
import org.apache.bcel.verifier.exc.StructuralCodeConstraintException;
import org.apache.bcel.verifier.structurals.UninitializedObjectType;

public class LocalVariables
implements Cloneable {
    private final Type[] locals;

    public LocalVariables(int localVariableCount) {
        this.locals = new Type[localVariableCount];
        Arrays.fill(this.locals, Type.UNKNOWN);
    }

    public Object clone() {
        LocalVariables lvs = new LocalVariables(this.locals.length);
        System.arraycopy(this.locals, 0, lvs.locals, 0, this.locals.length);
        return lvs;
    }

    public boolean equals(Object o) {
        if (!(o instanceof LocalVariables)) {
            return false;
        }
        LocalVariables lv = (LocalVariables)o;
        if (this.locals.length != lv.locals.length) {
            return false;
        }
        for (int i = 0; i < this.locals.length; ++i) {
            if (this.locals[i].equals(lv.locals[i])) continue;
            return false;
        }
        return true;
    }

    public Type get(int slotIndex) {
        return this.locals[slotIndex];
    }

    public LocalVariables getClone() {
        return (LocalVariables)this.clone();
    }

    public int hashCode() {
        return this.locals.length;
    }

    public void initializeObject(UninitializedObjectType uninitializedObjectType) {
        for (int i = 0; i < this.locals.length; ++i) {
            if (this.locals[i] != uninitializedObjectType) continue;
            this.locals[i] = uninitializedObjectType.getInitialized();
        }
    }

    public int maxLocals() {
        return this.locals.length;
    }

    public void merge(LocalVariables localVariable) {
        if (this.locals.length != localVariable.locals.length) {
            throw new AssertionViolatedException("Merging LocalVariables of different size?!? From different methods or what?!?");
        }
        for (int i = 0; i < this.locals.length; ++i) {
            this.merge(localVariable, i);
        }
    }

    private void merge(LocalVariables lv, int i) {
        try {
            if (!(this.locals[i] instanceof UninitializedObjectType) && lv.locals[i] instanceof UninitializedObjectType) {
                throw new StructuralCodeConstraintException("Backwards branch with an uninitialized object in the local variables detected.");
            }
            if (!this.locals[i].equals(lv.locals[i]) && this.locals[i] instanceof UninitializedObjectType && lv.locals[i] instanceof UninitializedObjectType) {
                throw new StructuralCodeConstraintException("Backwards branch with an uninitialized object in the local variables detected.");
            }
            if (this.locals[i] instanceof UninitializedObjectType && !(lv.locals[i] instanceof UninitializedObjectType)) {
                this.locals[i] = ((UninitializedObjectType)this.locals[i]).getInitialized();
            }
            if (this.locals[i] instanceof ReferenceType && lv.locals[i] instanceof ReferenceType) {
                if (!this.locals[i].equals(lv.locals[i])) {
                    ReferenceType sup = ((ReferenceType)this.locals[i]).getFirstCommonSuperclass((ReferenceType)lv.locals[i]);
                    if (sup == null) {
                        throw new AssertionViolatedException("Could not load all the super classes of '" + this.locals[i] + "' and '" + lv.locals[i] + "'.");
                    }
                    this.locals[i] = sup;
                }
            } else if (!this.locals[i].equals(lv.locals[i])) {
                this.locals[i] = Type.UNKNOWN;
            }
        }
        catch (ClassNotFoundException e) {
            throw new AssertionViolatedException("Missing class: " + e, e);
        }
    }

    public void set(int slotIndex, Type type) {
        if (type == Type.BYTE || type == Type.SHORT || type == Type.BOOLEAN || type == Type.CHAR) {
            throw new AssertionViolatedException("LocalVariables do not know about '" + type + "'. Use Type.INT instead.");
        }
        this.locals[slotIndex] = type;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.locals.length; ++i) {
            sb.append(Integer.toString(i));
            sb.append(": ");
            sb.append(this.locals[i]);
            sb.append("\n");
        }
        return sb.toString();
    }
}

