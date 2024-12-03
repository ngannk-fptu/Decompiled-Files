/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.google.template.soy.internal.i18n;

import com.google.common.base.Preconditions;
import com.google.template.soy.data.Dir;
import com.google.template.soy.internal.base.CharEscapers;
import com.google.template.soy.internal.i18n.BidiUtils;
import com.ibm.icu.util.ULocale;
import javax.annotation.Nullable;

public class BidiFormatter {
    private static final int FLAG_ALWAYS_SPAN = 1;
    private static final int FLAG_STEREO_RESET = 2;
    private static final int DEFAULT_FLAGS = 0;
    private static final BidiFormatter DEFAULT_LTR_INSTANCE = new BidiFormatter(Dir.LTR, 0);
    private static final BidiFormatter DEFAULT_RTL_INSTANCE = new BidiFormatter(Dir.RTL, 0);
    private final Dir contextDir;
    private final int flags;

    public static BidiFormatter getInstance(@Nullable Dir contextDir, boolean alwaysSpan) {
        return new Builder(contextDir).alwaysSpan(alwaysSpan).build();
    }

    public static BidiFormatter getInstance(@Nullable Dir contextDir) {
        return new Builder(contextDir).build();
    }

    public static BidiFormatter getInstance(boolean rtlContext, boolean alwaysSpan) {
        return new Builder(rtlContext).alwaysSpan(alwaysSpan).build();
    }

    public static BidiFormatter getInstance(boolean rtlContext) {
        return new Builder(rtlContext).build();
    }

    public static BidiFormatter getInstance(ULocale locale, boolean alwaysSpan) {
        return new Builder(locale).alwaysSpan(alwaysSpan).build();
    }

    public static BidiFormatter getInstance(ULocale locale) {
        return new Builder(locale).build();
    }

    public static BidiFormatter getInstanceWithNoContext() {
        return new Builder((Dir)null).build();
    }

    private BidiFormatter(@Nullable Dir contextDir, int flags) {
        this.contextDir = contextDir;
        this.flags = flags;
    }

    @Nullable
    public Dir getContextDir() {
        return this.contextDir;
    }

    public boolean isRtlContext() {
        return this.contextDir == Dir.RTL;
    }

    public boolean getAlwaysSpan() {
        return (this.flags & 1) != 0;
    }

    public boolean getStereoReset() {
        return (this.flags & 2) != 0;
    }

    public String dirAttrValue(String str, boolean isHtml) {
        return this.knownDirAttrValue(BidiFormatter.estimateDirection(str, isHtml));
    }

    public String dirAttrValue(String str) {
        return this.dirAttrValue(str, false);
    }

    public String knownDirAttrValue(Dir dir) {
        Preconditions.checkNotNull((Object)((Object)dir));
        if (dir == Dir.NEUTRAL) {
            dir = this.contextDir;
        }
        return dir == Dir.RTL ? "rtl" : "ltr";
    }

    public String dirAttr(String str, boolean isHtml) {
        return this.knownDirAttr(BidiFormatter.estimateDirection(str, isHtml));
    }

    public String dirAttr(String str) {
        return this.dirAttr(str, false);
    }

    public String knownDirAttr(Dir dir) {
        Preconditions.checkNotNull((Object)((Object)dir));
        if (dir != this.contextDir) {
            return dir == Dir.LTR ? "dir=\"ltr\"" : (dir == Dir.RTL ? "dir=\"rtl\"" : "");
        }
        return "";
    }

    public String spanWrap(String str, boolean isHtml, boolean isolate) {
        return this.spanWrapWithKnownDir(null, str, isHtml, isolate);
    }

    public String spanWrap(String str, boolean isHtml) {
        return this.spanWrap(str, isHtml, true);
    }

    public String spanWrap(String str) {
        return this.spanWrap(str, false, true);
    }

    public String spanWrapWithKnownDir(@Nullable Dir dir, String str, boolean isHtml, boolean isolate) {
        boolean dirCondition;
        if (dir == null) {
            dir = BidiFormatter.estimateDirection(str, isHtml);
        }
        String origStr = str;
        if (!isHtml) {
            str = CharEscapers.asciiHtmlEscaper().escape(str);
        }
        StringBuilder result = new StringBuilder();
        if (this.getStereoReset() && isolate) {
            result.append(this.markBeforeKnownDir(dir, origStr, isHtml));
        }
        boolean bl = dirCondition = dir != Dir.NEUTRAL && dir != this.contextDir;
        if (this.getAlwaysSpan() || dirCondition) {
            result.append("<span");
            if (dirCondition) {
                result.append(' ').append(dir == Dir.RTL ? "dir=\"rtl\"" : "dir=\"ltr\"");
            }
            result.append('>').append(str).append("</span>");
        } else {
            result.append(str);
        }
        if (isolate) {
            result.append(this.markAfterKnownDir(dir, origStr, isHtml));
        }
        return result.toString();
    }

    public String spanWrapWithKnownDir(@Nullable Dir dir, String str, boolean isHtml) {
        return this.spanWrapWithKnownDir(dir, str, isHtml, true);
    }

    public String spanWrapWithKnownDir(@Nullable Dir dir, String str) {
        return this.spanWrapWithKnownDir(dir, str, false, true);
    }

    public String unicodeWrap(String str, boolean isHtml, boolean isolate) {
        return this.unicodeWrapWithKnownDir(null, str, isHtml, isolate);
    }

    public String unicodeWrap(String str, boolean isHtml) {
        return this.unicodeWrap(str, isHtml, true);
    }

    public String unicodeWrap(String str) {
        return this.unicodeWrap(str, false, true);
    }

    public String unicodeWrapWithKnownDir(@Nullable Dir dir, String str, boolean isHtml, boolean isolate) {
        if (dir == null) {
            dir = BidiFormatter.estimateDirection(str, isHtml);
        }
        StringBuilder result = new StringBuilder();
        if (this.getStereoReset() && isolate) {
            result.append(this.markBeforeKnownDir(dir, str, isHtml));
        }
        if (dir != Dir.NEUTRAL && dir != this.contextDir) {
            result.append(dir == Dir.RTL ? (char)'\u202b' : '\u202a');
            result.append(str);
            result.append('\u202c');
        } else {
            result.append(str);
        }
        if (isolate) {
            result.append(this.markAfterKnownDir(dir, str, isHtml));
        }
        return result.toString();
    }

    public String unicodeWrapWithKnownDir(@Nullable Dir dir, String str, boolean isHtml) {
        return this.unicodeWrapWithKnownDir(dir, str, isHtml, true);
    }

    public String unicodeWrapWithKnownDir(@Nullable Dir dir, String str) {
        return this.unicodeWrapWithKnownDir(dir, str, false, true);
    }

    public String markAfter(String str, boolean isHtml) {
        return this.markAfterKnownDir(null, str, isHtml);
    }

    public String markAfter(String str) {
        return this.markAfter(str, false);
    }

    public String markAfterKnownDir(@Nullable Dir dir, String str, boolean isHtml) {
        if (dir == null) {
            dir = BidiFormatter.estimateDirection(str, isHtml);
        }
        if (this.contextDir == Dir.LTR && (dir == Dir.RTL || BidiUtils.getExitDir(str, isHtml) == Dir.RTL)) {
            return BidiUtils.Format.LRM_STRING;
        }
        if (this.contextDir == Dir.RTL && (dir == Dir.LTR || BidiUtils.getExitDir(str, isHtml) == Dir.LTR)) {
            return BidiUtils.Format.RLM_STRING;
        }
        return "";
    }

    public String markAfterKnownDir(@Nullable Dir dir, String str) {
        return this.markAfterKnownDir(dir, str, false);
    }

    public String markBefore(String str, boolean isHtml) {
        return this.markBeforeKnownDir(null, str, isHtml);
    }

    public String markBefore(String str) {
        return this.markBefore(str, false);
    }

    public String markBeforeKnownDir(@Nullable Dir dir, String str, boolean isHtml) {
        if (dir == null) {
            dir = BidiFormatter.estimateDirection(str, isHtml);
        }
        if (this.contextDir == Dir.LTR && (dir == Dir.RTL || BidiUtils.getEntryDir(str, isHtml) == Dir.RTL)) {
            return BidiUtils.Format.LRM_STRING;
        }
        if (this.contextDir == Dir.RTL && (dir == Dir.LTR || BidiUtils.getEntryDir(str, isHtml) == Dir.LTR)) {
            return BidiUtils.Format.RLM_STRING;
        }
        return "";
    }

    public String markBeforeKnownDir(@Nullable Dir dir, String str) {
        return this.markBeforeKnownDir(dir, str, false);
    }

    public String mark() {
        return this.contextDir == Dir.LTR ? BidiUtils.Format.LRM_STRING : (this.contextDir == Dir.RTL ? BidiUtils.Format.RLM_STRING : "");
    }

    public String startEdge() {
        return this.contextDir == Dir.RTL ? "right" : "left";
    }

    public String endEdge() {
        return this.contextDir == Dir.RTL ? "left" : "right";
    }

    public static Dir estimateDirection(String str, boolean isHtml) {
        return BidiUtils.estimateDirection(str, isHtml);
    }

    public static Dir estimateDirection(String str) {
        return BidiUtils.estimateDirection(str);
    }

    public static final class Builder {
        private Dir contextDir;
        private int flags;

        public Builder(@Nullable Dir contextDir) {
            Preconditions.checkArgument((contextDir != Dir.NEUTRAL ? 1 : 0) != 0);
            this.initialize(contextDir);
        }

        public Builder(boolean rtlContext) {
            this.initialize(rtlContext ? Dir.RTL : Dir.LTR);
        }

        public Builder(ULocale locale) {
            this.initialize(BidiUtils.languageDir(locale));
        }

        private void initialize(@Nullable Dir contextDir) {
            this.contextDir = contextDir;
            this.flags = 0;
        }

        public Builder alwaysSpan(boolean alwaysSpan) {
            this.flags = alwaysSpan ? (this.flags |= 1) : (this.flags &= 0xFFFFFFFE);
            return this;
        }

        public Builder stereoReset(boolean stereoReset) {
            this.flags = stereoReset ? (this.flags |= 2) : (this.flags &= 0xFFFFFFFD);
            return this;
        }

        public BidiFormatter build() {
            if (this.flags == 0) {
                if (this.contextDir == Dir.LTR) {
                    return DEFAULT_LTR_INSTANCE;
                }
                if (this.contextDir == Dir.RTL) {
                    return DEFAULT_RTL_INSTANCE;
                }
            }
            return new BidiFormatter(this.contextDir, this.flags);
        }
    }
}

