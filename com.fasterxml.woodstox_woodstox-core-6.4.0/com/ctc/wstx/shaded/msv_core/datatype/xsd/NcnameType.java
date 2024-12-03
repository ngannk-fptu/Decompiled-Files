/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.NameType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.TokenType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XmlNames;

public class NcnameType
extends TokenType {
    public static final NcnameType theInstance = new NcnameType("NCName");
    private static final long serialVersionUID = 1L;

    protected NcnameType(String typeName) {
        super(typeName, false);
    }

    public final XSDatatype getBaseType() {
        return NameType.theInstance;
    }

    public Object _createValue(String content, ValidationContext context) {
        if (XmlNames.isNCName(content)) {
            return content;
        }
        return null;
    }
}

