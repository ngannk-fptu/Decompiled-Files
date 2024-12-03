/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.google.template.soy.internal.i18n;

import com.google.common.base.Preconditions;
import com.google.template.soy.data.Dir;
import com.google.template.soy.internal.i18n.BidiFormatter;
import com.google.template.soy.internal.i18n.BidiGlobalDir;
import com.google.template.soy.internal.i18n.BidiUtils;
import java.util.regex.Pattern;

public class SoyBidiUtils {
    private static final String GOOG_IS_RTL_CODE_SNIPPET = "soy.$$IS_LOCALE_RTL";
    private static final Pattern FAKE_RTL_LOCALES_PATTERN = Pattern.compile("qbi|.*[-_]psrtl", 2);

    private SoyBidiUtils() {
    }

    public static int getBidiGlobalDir(String localeString) {
        boolean isRtl;
        try {
            isRtl = localeString != null && (BidiUtils.isRtlLanguage(localeString) || FAKE_RTL_LOCALES_PATTERN.matcher(localeString).matches());
        }
        catch (IllegalArgumentException localeException) {
            isRtl = false;
        }
        return isRtl ? -1 : 1;
    }

    public static BidiGlobalDir decodeBidiGlobalDir(int bidiGlobalDir) {
        return SoyBidiUtils.decodeBidiGlobalDirFromOptions(bidiGlobalDir, false);
    }

    public static BidiGlobalDir decodeBidiGlobalDirFromOptions(int bidiGlobalDir, boolean useGoogIsRtlForBidiGlobalDir) {
        if (bidiGlobalDir == 0) {
            if (!useGoogIsRtlForBidiGlobalDir) {
                return null;
            }
            return BidiGlobalDir.forIsRtlCodeSnippet(GOOG_IS_RTL_CODE_SNIPPET);
        }
        Preconditions.checkState((!useGoogIsRtlForBidiGlobalDir ? 1 : 0) != 0, (Object)"Must not specify both bidiGlobalDir and bidiGlobalDirIsRtlCodeSnippet.");
        Preconditions.checkArgument((bidiGlobalDir == 1 || bidiGlobalDir == -1 ? 1 : 0) != 0, (Object)"If specified, bidiGlobalDir must be 1 for LTR or -1 for RTL.");
        return BidiGlobalDir.forStaticIsRtl(bidiGlobalDir < 0);
    }

    public static BidiFormatter getBidiFormatter(int dir) {
        Preconditions.checkArgument((dir != 0 ? 1 : 0) != 0);
        return BidiFormatter.getInstance(dir < 0 ? Dir.RTL : Dir.LTR);
    }
}

