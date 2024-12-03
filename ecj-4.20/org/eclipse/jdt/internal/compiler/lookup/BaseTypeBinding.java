/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class BaseTypeBinding
extends TypeBinding {
    public static final int[] CONVERSIONS = BaseTypeBinding.initializeConversions();
    public static final int IDENTITY = 1;
    public static final int WIDENING = 2;
    public static final int NARROWING = 4;
    public static final int MAX_CONVERSIONS = 256;
    public char[] simpleName;
    private char[] constantPoolName;

    public static final int[] initializeConversions() {
        int[] table = new int[256];
        table[85] = 1;
        table[51] = 1;
        table[67] = 2;
        table[35] = 4;
        table[163] = 2;
        table[115] = 2;
        table[147] = 2;
        table[131] = 2;
        table[52] = 4;
        table[68] = 1;
        table[36] = 4;
        table[164] = 2;
        table[116] = 2;
        table[148] = 2;
        table[132] = 2;
        table[50] = 4;
        table[66] = 4;
        table[34] = 1;
        table[162] = 2;
        table[114] = 2;
        table[146] = 2;
        table[130] = 2;
        table[58] = 4;
        table[74] = 4;
        table[42] = 4;
        table[170] = 1;
        table[122] = 2;
        table[154] = 2;
        table[138] = 2;
        table[55] = 4;
        table[71] = 4;
        table[39] = 4;
        table[167] = 4;
        table[119] = 1;
        table[151] = 2;
        table[135] = 2;
        table[57] = 4;
        table[73] = 4;
        table[41] = 4;
        table[169] = 4;
        table[121] = 4;
        table[153] = 1;
        table[137] = 2;
        table[56] = 4;
        table[72] = 4;
        table[40] = 4;
        table[168] = 4;
        table[120] = 4;
        table[152] = 4;
        table[136] = 1;
        return table;
    }

    public static final boolean isNarrowing(int left, int right) {
        int right2left = right + (left << 4);
        return right2left >= 0 && right2left < 256 && (CONVERSIONS[right2left] & 5) != 0;
    }

    public static final boolean isWidening(int left, int right) {
        int right2left = right + (left << 4);
        return right2left >= 0 && right2left < 256 && (CONVERSIONS[right2left] & 3) != 0;
    }

    BaseTypeBinding(int id, char[] name, char[] constantPoolName) {
        this.tagBits |= 2L;
        this.id = id;
        this.simpleName = name;
        this.constantPoolName = constantPoolName;
    }

    @Override
    public char[] computeUniqueKey(boolean isLeaf) {
        return this.constantPoolName();
    }

    @Override
    public char[] constantPoolName() {
        return this.constantPoolName;
    }

    @Override
    public TypeBinding clone(TypeBinding enclosingType) {
        return new BaseTypeBinding(this.id, this.simpleName, this.constantPoolName);
    }

    @Override
    public PackageBinding getPackage() {
        return null;
    }

    @Override
    public final boolean isCompatibleWith(TypeBinding right, Scope captureScope) {
        if (BaseTypeBinding.equalsEquals(this, right)) {
            return true;
        }
        int right2left = this.id + (right.id << 4);
        if (right2left >= 0 && right2left < 256 && (CONVERSIONS[right2left] & 3) != 0) {
            return true;
        }
        return this == TypeBinding.NULL && !right.isBaseType();
    }

    @Override
    public void setTypeAnnotations(AnnotationBinding[] annotations, boolean evalNullAnnotations) {
        super.setTypeAnnotations(annotations, false);
    }

    @Override
    public TypeBinding unannotated() {
        if (!this.hasTypeAnnotations()) {
            return this;
        }
        switch (this.id) {
            case 5: {
                return TypeBinding.BOOLEAN;
            }
            case 3: {
                return TypeBinding.BYTE;
            }
            case 2: {
                return TypeBinding.CHAR;
            }
            case 8: {
                return TypeBinding.DOUBLE;
            }
            case 9: {
                return TypeBinding.FLOAT;
            }
            case 10: {
                return TypeBinding.INT;
            }
            case 7: {
                return TypeBinding.LONG;
            }
            case 4: {
                return TypeBinding.SHORT;
            }
        }
        throw new IllegalStateException();
    }

    @Override
    public boolean isUncheckedException(boolean includeSupertype) {
        return this == TypeBinding.NULL;
    }

    @Override
    public int kind() {
        return 132;
    }

    @Override
    public char[] qualifiedSourceName() {
        return this.simpleName;
    }

    @Override
    public char[] readableName() {
        return this.simpleName;
    }

    @Override
    public char[] shortReadableName() {
        return this.simpleName;
    }

    @Override
    public char[] sourceName() {
        return this.simpleName;
    }

    public String toString() {
        return this.hasTypeAnnotations() ? this.annotatedDebugName() : new String(this.readableName());
    }
}

