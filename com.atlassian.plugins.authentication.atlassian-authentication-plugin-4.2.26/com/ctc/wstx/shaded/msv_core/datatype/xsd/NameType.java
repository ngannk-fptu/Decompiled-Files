/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.TokenType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XmlNames;

public class NameType
extends TokenType {
    public static final NameType theInstance = new NameType();
    private static final long serialVersionUID = 1L;

    private NameType() {
        super("Name", false);
    }

    public final XSDatatype getBaseType() {
        return TokenType.theInstance;
    }

    public Object _createValue(String content, ValidationContext context) {
        if (XmlNames.isName(content)) {
            return content;
        }
        return null;
    }
}

