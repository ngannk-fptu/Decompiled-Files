/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.verifier.statics;

import org.apache.bcel.generic.Type;

public final class DOUBLE_Upper
extends Type {
    private static final DOUBLE_Upper INSTANCE = new DOUBLE_Upper();

    public static DOUBLE_Upper theInstance() {
        return INSTANCE;
    }

    private DOUBLE_Upper() {
        super((byte)15, "Double_Upper");
    }
}

