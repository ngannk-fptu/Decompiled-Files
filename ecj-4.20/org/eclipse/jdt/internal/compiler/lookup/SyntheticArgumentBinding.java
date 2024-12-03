/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;

public class SyntheticArgumentBinding
extends LocalVariableBinding {
    public LocalVariableBinding actualOuterLocalVariable;
    public FieldBinding matchingField;

    public SyntheticArgumentBinding(LocalVariableBinding actualOuterLocalVariable) {
        super(CharOperation.concat(TypeConstants.SYNTHETIC_OUTER_LOCAL_PREFIX, actualOuterLocalVariable.name), actualOuterLocalVariable.type, 16, true);
        this.tagBits |= 0x400L;
        this.useFlag = 1;
        this.actualOuterLocalVariable = actualOuterLocalVariable;
    }

    public SyntheticArgumentBinding(ReferenceBinding enclosingType) {
        super(CharOperation.concat(TypeConstants.SYNTHETIC_ENCLOSING_INSTANCE_PREFIX, String.valueOf(enclosingType.depth()).toCharArray()), (TypeBinding)enclosingType, 16, true);
        this.tagBits |= 0x400L;
        this.useFlag = 1;
    }
}

