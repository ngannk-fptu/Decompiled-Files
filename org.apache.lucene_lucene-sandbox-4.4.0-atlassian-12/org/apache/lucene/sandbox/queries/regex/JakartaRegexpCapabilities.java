/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.BytesRef
 *  org.apache.lucene.util.CharsRef
 *  org.apache.lucene.util.UnicodeUtil
 *  org.apache.regexp.CharacterIterator
 *  org.apache.regexp.RE
 *  org.apache.regexp.REProgram
 */
package org.apache.lucene.sandbox.queries.regex;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.apache.lucene.sandbox.queries.regex.RegexCapabilities;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.CharsRef;
import org.apache.lucene.util.UnicodeUtil;
import org.apache.regexp.CharacterIterator;
import org.apache.regexp.RE;
import org.apache.regexp.REProgram;

public class JakartaRegexpCapabilities
implements RegexCapabilities {
    private static Field prefixField;
    private static Method getPrefixMethod;
    private int flags = 0;
    public static final int FLAG_MATCH_NORMAL = 0;
    public static final int FLAG_MATCH_CASEINDEPENDENT = 1;

    public JakartaRegexpCapabilities() {
    }

    public JakartaRegexpCapabilities(int flags) {
        this.flags = flags;
    }

    @Override
    public RegexCapabilities.RegexMatcher compile(String regex) {
        return new JakartaRegexMatcher(regex, this.flags);
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + this.flags;
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        JakartaRegexpCapabilities other = (JakartaRegexpCapabilities)obj;
        return this.flags == other.flags;
    }

    static {
        try {
            getPrefixMethod = REProgram.class.getMethod("getPrefix", new Class[0]);
        }
        catch (Exception e) {
            getPrefixMethod = null;
        }
        try {
            prefixField = REProgram.class.getDeclaredField("prefix");
            prefixField.setAccessible(true);
        }
        catch (Exception e) {
            prefixField = null;
        }
    }

    class JakartaRegexMatcher
    implements RegexCapabilities.RegexMatcher {
        private RE regexp;
        private final CharsRef utf16 = new CharsRef(10);
        private final CharacterIterator utf16wrapper = new CharacterIterator(){

            public char charAt(int pos) {
                return ((JakartaRegexMatcher)JakartaRegexMatcher.this).utf16.chars[pos];
            }

            public boolean isEnd(int pos) {
                return pos >= ((JakartaRegexMatcher)JakartaRegexMatcher.this).utf16.length;
            }

            public String substring(int beginIndex) {
                return this.substring(beginIndex, ((JakartaRegexMatcher)JakartaRegexMatcher.this).utf16.length);
            }

            public String substring(int beginIndex, int endIndex) {
                return new String(((JakartaRegexMatcher)JakartaRegexMatcher.this).utf16.chars, beginIndex, endIndex - beginIndex);
            }
        };

        public JakartaRegexMatcher(String regex, int flags) {
            this.regexp = new RE(regex, flags);
        }

        @Override
        public boolean match(BytesRef term) {
            UnicodeUtil.UTF8toUTF16((byte[])term.bytes, (int)term.offset, (int)term.length, (CharsRef)this.utf16);
            return this.regexp.match(this.utf16wrapper, 0);
        }

        @Override
        public String prefix() {
            try {
                char[] prefix;
                if (getPrefixMethod != null) {
                    prefix = (char[])getPrefixMethod.invoke((Object)this.regexp.getProgram(), new Object[0]);
                } else if (prefixField != null) {
                    prefix = (char[])prefixField.get(this.regexp.getProgram());
                } else {
                    return null;
                }
                return prefix == null ? null : new String(prefix);
            }
            catch (Exception e) {
                return null;
            }
        }
    }
}

