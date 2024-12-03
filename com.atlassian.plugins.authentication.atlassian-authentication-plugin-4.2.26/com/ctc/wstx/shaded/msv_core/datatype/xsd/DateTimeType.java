/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.DateTimeBaseType;

public class DateTimeType
extends DateTimeBaseType {
    public static final DateTimeType theInstance = new DateTimeType();
    private static final long serialVersionUID = 1L;

    private DateTimeType() {
        super("dateTime");
    }

    protected final String getFormat() {
        return "%Y-%M-%DT%h:%m:%s%z";
    }
}

