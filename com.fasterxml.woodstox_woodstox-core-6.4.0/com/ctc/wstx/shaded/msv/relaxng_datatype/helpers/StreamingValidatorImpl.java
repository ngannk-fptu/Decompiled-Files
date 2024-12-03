/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv.relaxng_datatype.helpers;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeStreamingValidator;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;

public final class StreamingValidatorImpl
implements DatatypeStreamingValidator {
    private final StringBuffer buffer = new StringBuffer();
    private final Datatype baseType;
    private final ValidationContext context;

    public void addCharacters(char[] cArray, int n, int n2) {
        this.buffer.append(cArray, n, n2);
    }

    public boolean isValid() {
        return this.baseType.isValid(this.buffer.toString(), this.context);
    }

    public void checkValid() throws DatatypeException {
        this.baseType.checkValid(this.buffer.toString(), this.context);
    }

    public StreamingValidatorImpl(Datatype datatype, ValidationContext validationContext) {
        this.baseType = datatype;
        this.context = validationContext;
    }
}

