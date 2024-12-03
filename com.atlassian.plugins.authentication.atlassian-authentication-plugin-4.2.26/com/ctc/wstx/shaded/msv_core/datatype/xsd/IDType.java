/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.NcnameType;

public class IDType
extends NcnameType {
    public static final IDType theInstance = new IDType();
    private static final long serialVersionUID = 1L;

    protected IDType() {
        super("ID");
    }

    protected Object readResolve() {
        return theInstance;
    }

    public int getIdType() {
        return 1;
    }
}

