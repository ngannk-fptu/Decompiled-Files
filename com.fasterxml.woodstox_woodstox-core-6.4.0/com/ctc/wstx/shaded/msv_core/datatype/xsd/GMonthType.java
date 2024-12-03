/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.DateTimeBaseType;

public class GMonthType
extends DateTimeBaseType {
    public static final GMonthType theInstance = new GMonthType();
    private static final long serialVersionUID = 1L;

    private GMonthType() {
        super("gMonth");
    }

    protected final String getFormat() {
        return "--%M--%z";
    }
}

