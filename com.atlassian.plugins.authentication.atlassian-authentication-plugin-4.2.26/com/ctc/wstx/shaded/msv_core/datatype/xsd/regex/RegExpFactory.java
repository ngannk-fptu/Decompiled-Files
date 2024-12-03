/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd.regex;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.regex.RegExp;
import java.text.ParseException;

public abstract class RegExpFactory {
    public abstract RegExp compile(String var1) throws ParseException;

    public static RegExpFactory createFactory() {
        String[] classList = new String[]{"com.ctc.wstx.shaded.msv_core.datatype.regexp.InternalImpl", "com.ctc.wstx.shaded.msv_core.datatype.xsd.regex.XercesImpl", "com.ctc.wstx.shaded.msv_core.datatype.xsd.regex.JDKImpl"};
        for (int i = 0; i < classList.length; ++i) {
            String name = classList[i];
            try {
                return (RegExpFactory)RegExpFactory.class.getClassLoader().loadClass(name).newInstance();
            }
            catch (Throwable throwable) {
                continue;
            }
        }
        throw new Error("no implementation of regexp was found.");
    }
}

