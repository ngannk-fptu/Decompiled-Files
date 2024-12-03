/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.javasrc.restricted;

import com.google.template.soy.data.SoyData;
import com.google.template.soy.data.restricted.BooleanData;
import com.google.template.soy.data.restricted.FloatData;
import com.google.template.soy.data.restricted.IntegerData;
import com.google.template.soy.data.restricted.NumberData;
import com.google.template.soy.data.restricted.StringData;
import com.google.template.soy.javasrc.restricted.JavaExpr;

public class SoyJavaSrcFunctionUtils {
    private SoyJavaSrcFunctionUtils() {
    }

    public static JavaExpr toBooleanJavaExpr(String exprText) {
        return new JavaExpr(exprText, BooleanData.class, Integer.MAX_VALUE);
    }

    public static JavaExpr toIntegerJavaExpr(String exprText) {
        return new JavaExpr(exprText, IntegerData.class, Integer.MAX_VALUE);
    }

    public static JavaExpr toFloatJavaExpr(String exprText) {
        return new JavaExpr(exprText, FloatData.class, Integer.MAX_VALUE);
    }

    public static JavaExpr toNumberJavaExpr(String exprText) {
        return new JavaExpr(exprText, NumberData.class, Integer.MAX_VALUE);
    }

    public static JavaExpr toStringJavaExpr(String exprText) {
        return new JavaExpr(exprText, StringData.class, Integer.MAX_VALUE);
    }

    public static JavaExpr toUnknownJavaExpr(String exprText) {
        return new JavaExpr(exprText, SoyData.class, Integer.MAX_VALUE);
    }
}

