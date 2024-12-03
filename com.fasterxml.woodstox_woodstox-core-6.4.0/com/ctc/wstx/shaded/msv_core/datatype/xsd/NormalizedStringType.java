/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.StringType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.WhiteSpaceProcessor;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;

public class NormalizedStringType
extends StringType {
    public static final NormalizedStringType theInstance = new NormalizedStringType("normalizedString", true);
    private static final long serialVersionUID = 1L;

    protected NormalizedStringType(String typeName, boolean isAlwaysValid) {
        super(typeName, WhiteSpaceProcessor.theReplace, isAlwaysValid);
    }

    public XSDatatype getBaseType() {
        return StringType.theInstance;
    }
}

