/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;

public interface DatabindableDatatype
extends Datatype {
    public Object createJavaObject(String var1, ValidationContext var2);

    public String serializeJavaObject(Object var1, SerializationContext var2) throws IllegalArgumentException;

    public Class getJavaObjectType();
}

