/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.parsepasses.contextautoesc;

import com.google.template.soy.parsepasses.contextautoesc.Context;

public final class DerivedTemplateUtils {
    private static final String CONTEXT_SEPARATOR = "__C";

    public static String getSuffix(Context startContext) {
        if (Context.HTML_PCDATA.equals(startContext)) {
            return "";
        }
        return CONTEXT_SEPARATOR + Integer.toString(startContext.packedBits(), 16);
    }

    public static String getBaseName(String templateName) {
        int separatorIndex = templateName.lastIndexOf(CONTEXT_SEPARATOR);
        return separatorIndex < 0 ? templateName : templateName.substring(0, separatorIndex);
    }

    public static String getQualifiedName(String baseName, Context startContext) {
        return DerivedTemplateUtils.getBaseName(baseName) + DerivedTemplateUtils.getSuffix(startContext);
    }

    private DerivedTemplateUtils() {
    }
}

