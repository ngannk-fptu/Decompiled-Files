/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import org.aspectj.weaver.AnnotationAJ;
import org.aspectj.weaver.UnresolvedType;

public class Utils {
    public static boolean isSuppressing(AnnotationAJ[] anns, String lintkey) {
        if (anns == null) {
            return false;
        }
        for (int i = 0; i < anns.length; ++i) {
            String value;
            if (!UnresolvedType.SUPPRESS_AJ_WARNINGS.getSignature().equals(anns[i].getTypeSignature()) || (value = anns[i].getStringFormOfValue("value")) != null && value.indexOf(lintkey) == -1) continue;
            return true;
        }
        return false;
    }
}

