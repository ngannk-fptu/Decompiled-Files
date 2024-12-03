/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.exprtree;

import com.google.template.soy.types.SoyType;

public interface VarDefn {
    public Kind kind();

    public String name();

    public SoyType type();

    public static enum Kind {
        PARAM,
        IJ_PARAM,
        LOCAL_VAR,
        UNDECLARED;

    }
}

