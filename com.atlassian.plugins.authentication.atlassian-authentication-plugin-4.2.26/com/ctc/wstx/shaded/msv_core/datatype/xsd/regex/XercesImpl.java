/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd.regex;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.regex.RegExp;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.regex.RegExpFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;

final class XercesImpl
extends RegExpFactory {
    private final Class regexp = this.getClass().getClassLoader().loadClass("org.apache.xerces.impl.xpath.regex.RegularExpression");
    private final Constructor ctor = this.regexp.getConstructor(String.class, String.class);
    private final Method matches = this.regexp.getMethod("matches", String.class);

    XercesImpl() throws Exception {
    }

    public RegExp compile(String exp) throws ParseException {
        Object re;
        try {
            re = this.ctor.newInstance(exp, "X");
        }
        catch (IllegalAccessException e) {
            throw new IllegalAccessError(e.getMessage());
        }
        catch (InvocationTargetException e) {
            throw new ParseException(e.getTargetException().getMessage(), -1);
        }
        catch (InstantiationException e) {
            throw new InstantiationError(e.getMessage());
        }
        return new RegExp(){

            public boolean matches(String text) {
                try {
                    return (Boolean)XercesImpl.this.matches.invoke(re, text);
                }
                catch (IllegalAccessException e) {
                    throw new IllegalAccessError(e.getMessage());
                }
                catch (InvocationTargetException e) {
                    throw new Error(e);
                }
            }
        };
    }
}

