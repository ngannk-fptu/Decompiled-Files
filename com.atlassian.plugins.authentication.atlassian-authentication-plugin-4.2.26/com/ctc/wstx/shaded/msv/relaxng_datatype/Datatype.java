/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv.relaxng_datatype;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeStreamingValidator;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;

public interface Datatype {
    public static final int ID_TYPE_NULL = 0;
    public static final int ID_TYPE_ID = 1;
    public static final int ID_TYPE_IDREF = 2;
    public static final int ID_TYPE_IDREFS = 3;

    public boolean isValid(String var1, ValidationContext var2);

    public void checkValid(String var1, ValidationContext var2) throws DatatypeException;

    public DatatypeStreamingValidator createStreamingValidator(ValidationContext var1);

    public Object createValue(String var1, ValidationContext var2);

    public boolean sameValue(Object var1, Object var2);

    public int valueHashCode(Object var1);

    public int getIdType();

    public boolean isContextDependent();
}

