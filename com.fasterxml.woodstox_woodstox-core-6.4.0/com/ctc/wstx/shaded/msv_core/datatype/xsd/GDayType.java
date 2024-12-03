/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.DateTimeBaseType;

public class GDayType
extends DateTimeBaseType {
    public static final GDayType theInstance = new GDayType();
    private static final long serialVersionUID = 1L;

    private GDayType() {
        super("gDay");
    }

    protected final String getFormat() {
        return "---%D%z";
    }
}

