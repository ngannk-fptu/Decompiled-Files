/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.BytesRef
 *  org.apache.lucene.util.CharsRef
 *  org.apache.lucene.util.UnicodeUtil
 */
package org.apache.lucene.sandbox.queries.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.lucene.sandbox.queries.regex.RegexCapabilities;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.CharsRef;
import org.apache.lucene.util.UnicodeUtil;

public class JavaUtilRegexCapabilities
implements RegexCapabilities {
    private int flags = 0;
    public static final int FLAG_CANON_EQ = 128;
    public static final int FLAG_CASE_INSENSITIVE = 2;
    public static final int FLAG_COMMENTS = 4;
    public static final int FLAG_DOTALL = 32;
    public static final int FLAG_LITERAL = 16;
    public static final int FLAG_MULTILINE = 8;
    public static final int FLAG_UNICODE_CASE = 64;
    public static final int FLAG_UNIX_LINES = 1;

    public JavaUtilRegexCapabilities() {
        this.flags = 0;
    }

    public JavaUtilRegexCapabilities(int flags) {
        this.flags = flags;
    }

    @Override
    public RegexCapabilities.RegexMatcher compile(String regex) {
        return new JavaUtilRegexMatcher(regex, this.flags);
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
        JavaUtilRegexCapabilities other = (JavaUtilRegexCapabilities)obj;
        return this.flags == other.flags;
    }

    class JavaUtilRegexMatcher
    implements RegexCapabilities.RegexMatcher {
        private final Pattern pattern;
        private final Matcher matcher;
        private final CharsRef utf16 = new CharsRef(10);

        public JavaUtilRegexMatcher(String regex, int flags) {
            this.pattern = Pattern.compile(regex, flags);
            this.matcher = this.pattern.matcher((CharSequence)this.utf16);
        }

        @Override
        public boolean match(BytesRef term) {
            UnicodeUtil.UTF8toUTF16((byte[])term.bytes, (int)term.offset, (int)term.length, (CharsRef)this.utf16);
            return this.matcher.reset().matches();
        }

        @Override
        public String prefix() {
            return null;
        }
    }
}

