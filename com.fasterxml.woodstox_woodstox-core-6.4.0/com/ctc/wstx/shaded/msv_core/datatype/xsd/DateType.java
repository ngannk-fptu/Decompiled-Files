/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.DateTimeBaseType;

public class DateType
extends DateTimeBaseType {
    public static final DateType theInstance = new DateType();
    private static final long serialVersionUID = 1L;

    private DateType() {
        super("date");
    }

    protected final String getFormat() {
        return "%Y-%M-%D%z";
    }
}

