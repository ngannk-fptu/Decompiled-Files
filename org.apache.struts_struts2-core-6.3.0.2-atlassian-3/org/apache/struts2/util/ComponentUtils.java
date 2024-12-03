/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.util;

public class ComponentUtils {
    public static String stripExpression(String expr) {
        if (ComponentUtils.isExpression(expr)) {
            return expr.substring(2, expr.length() - 1);
        }
        return expr;
    }

    public static boolean isExpression(String expr) {
        return expr != null && expr.startsWith("%{") && expr.endsWith("}");
    }

    public static boolean containsExpression(String expr) {
        return expr != null && expr.contains("%{") && expr.contains("}");
    }
}

