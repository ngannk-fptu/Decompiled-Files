/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.BigDateTimeValueType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.ITimeDurationValueType;
import java.io.Serializable;
import java.util.Calendar;

public interface IDateTimeValueType
extends Serializable {
    public BigDateTimeValueType getBigValue();

    public IDateTimeValueType add(ITimeDurationValueType var1);

    public IDateTimeValueType normalize();

    public int compare(IDateTimeValueType var1);

    public Calendar toCalendar();
}

