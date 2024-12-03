/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.cache.MruCacheStorage;
import freemarker.core._DelayedGetMessage;
import freemarker.core._ErrorDescriptionBuilder;
import freemarker.core._TemplateModelException;
import freemarker.log.Logger;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.StringUtil;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

final class RegexpHelper {
    private static final Logger LOG = Logger.getLogger("freemarker.runtime");
    private static volatile boolean flagWarningsEnabled = LOG.isWarnEnabled();
    private static final int MAX_FLAG_WARNINGS_LOGGED = 25;
    private static final Object flagWarningsCntSync = new Object();
    private static int flagWarningsCnt;
    private static final MruCacheStorage patternCache;
    static final long RE_FLAG_CASE_INSENSITIVE;
    static final long RE_FLAG_MULTILINE;
    static final long RE_FLAG_COMMENTS;
    static final long RE_FLAG_DOTALL;
    static final long RE_FLAG_REGEXP = 0x100000000L;
    static final long RE_FLAG_FIRST_ONLY = 0x200000000L;

    private static long intFlagToLong(int flag) {
        return (long)flag & 0xFFFFL;
    }

    private RegexpHelper() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static Pattern getPattern(String patternString, int flags) throws TemplateModelException {
        Pattern result;
        PatternCacheKey patternKey = new PatternCacheKey(patternString, flags);
        MruCacheStorage mruCacheStorage = patternCache;
        synchronized (mruCacheStorage) {
            result = (Pattern)patternCache.get(patternKey);
        }
        if (result != null) {
            return result;
        }
        try {
            result = Pattern.compile(patternString, flags);
        }
        catch (PatternSyntaxException e) {
            throw new _TemplateModelException((Throwable)e, "Malformed regular expression: ", new _DelayedGetMessage(e));
        }
        mruCacheStorage = patternCache;
        synchronized (mruCacheStorage) {
            patternCache.put(patternKey, result);
        }
        return result;
    }

    static long parseFlagString(String flagString) {
        long flags = 0L;
        block8: for (int i = 0; i < flagString.length(); ++i) {
            char c = flagString.charAt(i);
            switch (c) {
                case 'i': {
                    flags |= RE_FLAG_CASE_INSENSITIVE;
                    continue block8;
                }
                case 'm': {
                    flags |= RE_FLAG_MULTILINE;
                    continue block8;
                }
                case 'c': {
                    flags |= RE_FLAG_COMMENTS;
                    continue block8;
                }
                case 's': {
                    flags |= RE_FLAG_DOTALL;
                    continue block8;
                }
                case 'r': {
                    flags |= 0x100000000L;
                    continue block8;
                }
                case 'f': {
                    flags |= 0x200000000L;
                    continue block8;
                }
                default: {
                    if (!flagWarningsEnabled) continue block8;
                    RegexpHelper.logFlagWarning("Unrecognized regular expression flag: " + StringUtil.jQuote(String.valueOf(c)) + ".");
                }
            }
        }
        return flags;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void logFlagWarning(String message) {
        int cnt;
        if (!flagWarningsEnabled) {
            return;
        }
        Object object = flagWarningsCntSync;
        synchronized (object) {
            cnt = flagWarningsCnt++;
            if (cnt >= 25) {
                flagWarningsEnabled = false;
                return;
            }
        }
        message = message + " This will be an error in some later FreeMarker version!";
        if (cnt + 1 == 25) {
            message = message + " [Will not log more regular expression flag problems until restart!]";
        }
        LOG.warn(message);
    }

    static void checkNonRegexpFlags(String biName, long flags) throws _TemplateModelException {
        RegexpHelper.checkOnlyHasNonRegexpFlags(biName, flags, false);
    }

    static void checkOnlyHasNonRegexpFlags(String biName, long flags, boolean strict) throws _TemplateModelException {
        String flag;
        if (!strict && !flagWarningsEnabled) {
            return;
        }
        if ((flags & RE_FLAG_MULTILINE) != 0L) {
            flag = "m";
        } else if ((flags & RE_FLAG_DOTALL) != 0L) {
            flag = "s";
        } else if ((flags & RE_FLAG_COMMENTS) != 0L) {
            flag = "c";
        } else {
            return;
        }
        Object[] msg = new Object[]{"?", biName, " doesn't support the \"", flag, "\" flag without the \"r\" flag."};
        if (strict) {
            throw new _TemplateModelException(msg);
        }
        RegexpHelper.logFlagWarning(new _ErrorDescriptionBuilder(msg).toString());
    }

    static {
        patternCache = new MruCacheStorage(50, 150);
        RE_FLAG_CASE_INSENSITIVE = RegexpHelper.intFlagToLong(2);
        RE_FLAG_MULTILINE = RegexpHelper.intFlagToLong(8);
        RE_FLAG_COMMENTS = RegexpHelper.intFlagToLong(4);
        RE_FLAG_DOTALL = RegexpHelper.intFlagToLong(32);
    }

    private static class PatternCacheKey {
        private final String patternString;
        private final int flags;
        private final int hashCode;

        public PatternCacheKey(String patternString, int flags) {
            this.patternString = patternString;
            this.flags = flags;
            this.hashCode = patternString.hashCode() + 31 * flags;
        }

        public boolean equals(Object that) {
            if (that instanceof PatternCacheKey) {
                PatternCacheKey thatPCK = (PatternCacheKey)that;
                return thatPCK.flags == this.flags && thatPCK.patternString.equals(this.patternString);
            }
            return false;
        }

        public int hashCode() {
            return this.hashCode;
        }
    }
}

