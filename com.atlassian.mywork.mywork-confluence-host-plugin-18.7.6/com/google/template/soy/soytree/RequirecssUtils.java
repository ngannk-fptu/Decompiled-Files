/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 */
package com.google.template.soy.soytree;

import com.google.common.collect.ImmutableList;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.base.internal.BaseUtils;
import javax.annotation.Nullable;

public class RequirecssUtils {
    private RequirecssUtils() {
    }

    public static ImmutableList<String> parseRequirecssAttr(@Nullable String requirecssAttr) {
        Object[] namespaces;
        if (requirecssAttr == null) {
            return ImmutableList.of();
        }
        for (Object namespace : namespaces = requirecssAttr.trim().split("\\s*,\\s*")) {
            if (BaseUtils.isDottedIdentifier((String)namespace)) continue;
            throw SoySyntaxException.createWithoutMetaInfo("Invalid required CSS namespace name \"" + (String)namespace + "\".");
        }
        return ImmutableList.copyOf((Object[])namespaces);
    }
}

