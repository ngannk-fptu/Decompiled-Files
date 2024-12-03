/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.TokenType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;

public class LanguageType
extends TokenType {
    public static final LanguageType theInstance = new LanguageType();
    private static final long serialVersionUID = 1L;

    private LanguageType() {
        super("language", false);
    }

    public final XSDatatype getBaseType() {
        return TokenType.theInstance;
    }

    public Object _createValue(String content, ValidationContext context) {
        int len = content.length();
        int i = 0;
        int tokenSize = 0;
        while (i < len) {
            char ch;
            if ('a' <= (ch = content.charAt(i++)) && ch <= 'z' || 'A' <= ch && ch <= 'Z') {
                if (++tokenSize != 9) continue;
                return null;
            }
            if (ch == '-') {
                if (tokenSize == 0) {
                    return null;
                }
                tokenSize = 0;
                continue;
            }
            return null;
        }
        if (tokenSize == 0) {
            return null;
        }
        return content.toLowerCase();
    }
}

