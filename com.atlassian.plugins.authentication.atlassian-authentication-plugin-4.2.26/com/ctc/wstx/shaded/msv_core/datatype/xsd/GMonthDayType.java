/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.DateTimeBaseType;

public class GMonthDayType
extends DateTimeBaseType {
    public static final GMonthDayType theInstance = new GMonthDayType();
    private static final long serialVersionUID = 1L;

    private GMonthDayType() {
        super("gMonthDay");
    }

    protected final String getFormat() {
        return "--%M-%D%z";
    }
}

