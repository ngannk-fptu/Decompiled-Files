/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.template.soy.shared.internal;

import com.google.inject.Key;
import com.google.template.soy.internal.i18n.BidiGlobalDir;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.shared.internal.GuiceSimpleScope;
import com.google.template.soy.shared.restricted.ApiCallScopeBindingAnnotations;
import javax.annotation.Nullable;

public class ApiCallScopeUtils {
    private ApiCallScopeUtils() {
    }

    public static void seedSharedParams(GuiceSimpleScope apiCallScope, @Nullable SoyMsgBundle msgBundle, int bidiGlobalDir) {
        ApiCallScopeUtils.seedSharedParams(apiCallScope, msgBundle, bidiGlobalDir == 0 ? null : BidiGlobalDir.forStaticIsRtl(bidiGlobalDir < 0));
    }

    public static void seedSharedParams(GuiceSimpleScope apiCallScope, @Nullable SoyMsgBundle msgBundle, @Nullable BidiGlobalDir bidiGlobalDir) {
        String localeString;
        String string = localeString = msgBundle != null ? msgBundle.getLocaleString() : null;
        if (bidiGlobalDir == null) {
            bidiGlobalDir = BidiGlobalDir.forStaticLocale(localeString);
        }
        apiCallScope.seed(SoyMsgBundle.class, msgBundle);
        apiCallScope.seed(Key.get(String.class, ApiCallScopeBindingAnnotations.LocaleString.class), localeString);
        apiCallScope.seed(BidiGlobalDir.class, bidiGlobalDir);
    }
}

