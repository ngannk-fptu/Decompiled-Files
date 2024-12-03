/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.google.template.soy.data;

import com.google.template.soy.data.Dir;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.data.SanitizedContents;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class UnsafeSanitizedContentOrdainer {
    private UnsafeSanitizedContentOrdainer() {
    }

    public static SanitizedContent ordainAsSafe(String value, SanitizedContent.ContentKind kind) {
        return UnsafeSanitizedContentOrdainer.ordainAsSafe(value, kind, SanitizedContents.getDefaultDir(kind));
    }

    public static SanitizedContent ordainAsSafe(String value, SanitizedContent.ContentKind kind, @Nullable Dir dir) {
        return new SanitizedContent(value, kind, dir);
    }
}

