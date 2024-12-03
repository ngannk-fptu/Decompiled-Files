/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.BigTimeDurationValueType;
import java.io.Serializable;

public interface ITimeDurationValueType
extends Serializable {
    public BigTimeDurationValueType getBigValue();

    public int compare(ITimeDurationValueType var1);
}

