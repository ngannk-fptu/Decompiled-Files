/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.NormalizedStringType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.StringType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.WhiteSpaceProcessor;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;

public class TokenType
extends StringType {
    public static final TokenType theInstance = new TokenType("token", true);
    private static final long serialVersionUID = 1L;

    protected TokenType(String typeName, boolean isAlwaysValid) {
        super(typeName, WhiteSpaceProcessor.theCollapse, isAlwaysValid);
    }

    public XSDatatype getBaseType() {
        return NormalizedStringType.theInstance;
    }
}

