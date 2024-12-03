/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.google.template.soy.internal.i18n;

import com.google.common.base.Preconditions;
import com.google.template.soy.internal.i18n.SoyBidiUtils;
import javax.annotation.Nullable;

public class BidiGlobalDir {
    private String codeSnippet;
    private int staticValue;

    private BidiGlobalDir(int staticValue) {
        this.staticValue = staticValue;
        this.codeSnippet = Integer.toString(staticValue);
    }

    private BidiGlobalDir(String codeSnippet) {
        this.codeSnippet = codeSnippet;
    }

    public static BidiGlobalDir forStaticIsRtl(boolean isRtl) {
        return new BidiGlobalDir(isRtl ? -1 : 1);
    }

    public static BidiGlobalDir forStaticLocale(@Nullable String localeString) {
        return new BidiGlobalDir(SoyBidiUtils.getBidiGlobalDir(localeString));
    }

    public static BidiGlobalDir forIsRtlCodeSnippet(String isRtlCodeSnippet) {
        Preconditions.checkArgument((isRtlCodeSnippet != null && isRtlCodeSnippet.length() > 0 ? 1 : 0) != 0, (Object)"Bidi global direction source code snippet must be non-empty.");
        return new BidiGlobalDir(isRtlCodeSnippet + "?-1:1");
    }

    public boolean isStaticValue() {
        return this.staticValue != 0;
    }

    public int getStaticValue() {
        if (this.staticValue == 0) {
            throw new RuntimeException("Cannot get static value for nonstatic BidiGlobalDir object.");
        }
        return this.staticValue;
    }

    public String getCodeSnippet() {
        return this.codeSnippet;
    }
}

