/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.DateTimeBaseType;

public class GYearType
extends DateTimeBaseType {
    public static final GYearType theInstance = new GYearType();
    private static final long serialVersionUID = 1L;

    private GYearType() {
        super("gYear");
    }

    protected final String getFormat() {
        return "%Y%z";
    }
}

