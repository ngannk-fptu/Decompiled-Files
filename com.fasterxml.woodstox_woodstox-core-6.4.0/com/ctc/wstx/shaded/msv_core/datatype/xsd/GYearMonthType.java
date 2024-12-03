/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.DateTimeBaseType;

public class GYearMonthType
extends DateTimeBaseType {
    public static final GYearMonthType theInstance = new GYearMonthType();
    private static final long serialVersionUID = 1L;

    private GYearMonthType() {
        super("gYearMonth");
    }

    protected final String getFormat() {
        return "%Y-%M%z";
    }
}

