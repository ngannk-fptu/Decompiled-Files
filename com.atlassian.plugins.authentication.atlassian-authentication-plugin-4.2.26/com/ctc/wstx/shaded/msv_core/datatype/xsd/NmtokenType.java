/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.TokenType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XmlNames;

public class NmtokenType
extends TokenType {
    public static final NmtokenType theInstance = new NmtokenType("NMTOKEN");
    private static final long serialVersionUID = 1L;

    protected NmtokenType(String typeName) {
        super(typeName, false);
    }

    public final XSDatatype getBaseType() {
        return TokenType.theInstance;
    }

    public Object _createValue(String content, ValidationContext context) {
        if (XmlNames.isNmtoken(content)) {
            return content;
        }
        return null;
    }
}

