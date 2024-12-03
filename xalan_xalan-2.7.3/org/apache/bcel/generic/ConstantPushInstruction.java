/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.PushInstruction;
import org.apache.bcel.generic.TypedInstruction;

public interface ConstantPushInstruction
extends PushInstruction,
TypedInstruction {
    public Number getValue();
}

