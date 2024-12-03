/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.verifier.statics;

import org.apache.bcel.generic.Type;

public final class LONG_Upper
extends Type {
    private static final LONG_Upper INSTANCE = new LONG_Upper();

    public static LONG_Upper theInstance() {
        return INSTANCE;
    }

    private LONG_Upper() {
        super((byte)15, "Long_Upper");
    }
}

