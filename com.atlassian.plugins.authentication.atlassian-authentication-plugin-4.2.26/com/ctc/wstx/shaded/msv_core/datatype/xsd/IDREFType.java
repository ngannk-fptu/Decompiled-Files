/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.NcnameType;

public class IDREFType
extends NcnameType {
    public static final IDREFType theInstance = new IDREFType();
    private static final long serialVersionUID = 1L;

    protected IDREFType() {
        super("IDREF");
    }

    public int getIdType() {
        return 2;
    }

    protected Object readResolve() {
        return theInstance;
    }
}

