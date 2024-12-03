/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.DateTimeBaseType;

public class TimeType
extends DateTimeBaseType {
    public static final TimeType theInstance = new TimeType();
    private static final long serialVersionUID = 1L;

    private TimeType() {
        super("time");
    }

    protected final String getFormat() {
        return "%h:%m:%s%z";
    }
}

