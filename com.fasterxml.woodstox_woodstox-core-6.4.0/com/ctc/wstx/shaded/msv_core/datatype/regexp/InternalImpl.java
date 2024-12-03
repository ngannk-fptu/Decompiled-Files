/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.regexp;

import com.ctc.wstx.shaded.msv_core.datatype.regexp.ParseException;
import com.ctc.wstx.shaded.msv_core.datatype.regexp.RegularExpression;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.regex.RegExp;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.regex.RegExpFactory;

public final class InternalImpl
extends RegExpFactory {
    public RegExp compile(String exp) throws java.text.ParseException {
        RegularExpression re;
        try {
            re = new RegularExpression(exp, "X");
        }
        catch (ParseException e) {
            throw new java.text.ParseException(e.getMessage(), e.getLocation());
        }
        return new RegExp(){

            public boolean matches(String text) {
                return re.matches(text);
            }
        };
    }
}

