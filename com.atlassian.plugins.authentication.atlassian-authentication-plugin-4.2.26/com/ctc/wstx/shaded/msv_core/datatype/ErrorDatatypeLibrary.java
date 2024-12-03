/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeBuilder;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeLibrary;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.StringType;

public class ErrorDatatypeLibrary
implements DatatypeLibrary,
DatatypeBuilder {
    public static final ErrorDatatypeLibrary theInstance = new ErrorDatatypeLibrary();

    private ErrorDatatypeLibrary() {
    }

    public Datatype createDatatype(String name) {
        return StringType.theInstance;
    }

    public DatatypeBuilder createDatatypeBuilder(String name) {
        return this;
    }

    public Datatype createDatatype() {
        return StringType.theInstance;
    }

    public void addParameter(String name, String value, ValidationContext context) {
    }
}

