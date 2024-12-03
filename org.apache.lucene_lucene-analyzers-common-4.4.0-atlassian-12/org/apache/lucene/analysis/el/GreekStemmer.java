/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.el;

import java.util.Arrays;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

public class GreekStemmer {
    private static final CharArraySet exc4 = new CharArraySet(Version.LUCENE_31, Arrays.asList("\u03b8", "\u03b4", "\u03b5\u03bb", "\u03b3\u03b1\u03bb", "\u03bd", "\u03c0", "\u03b9\u03b4", "\u03c0\u03b1\u03c1"), false);
    private static final CharArraySet exc6 = new CharArraySet(Version.LUCENE_31, Arrays.asList("\u03b1\u03bb", "\u03b1\u03b4", "\u03b5\u03bd\u03b4", "\u03b1\u03bc\u03b1\u03bd", "\u03b1\u03bc\u03bc\u03bf\u03c7\u03b1\u03bb", "\u03b7\u03b8", "\u03b1\u03bd\u03b7\u03b8", "\u03b1\u03bd\u03c4\u03b9\u03b4", "\u03c6\u03c5\u03c3", "\u03b2\u03c1\u03c9\u03bc", "\u03b3\u03b5\u03c1", "\u03b5\u03be\u03c9\u03b4", "\u03ba\u03b1\u03bb\u03c0", "\u03ba\u03b1\u03bb\u03bb\u03b9\u03bd", "\u03ba\u03b1\u03c4\u03b1\u03b4", "\u03bc\u03bf\u03c5\u03bb", "\u03bc\u03c0\u03b1\u03bd", "\u03bc\u03c0\u03b1\u03b3\u03b9\u03b1\u03c4", "\u03bc\u03c0\u03bf\u03bb", "\u03bc\u03c0\u03bf\u03c3", "\u03bd\u03b9\u03c4", "\u03be\u03b9\u03ba", "\u03c3\u03c5\u03bd\u03bf\u03bc\u03b7\u03bb", "\u03c0\u03b5\u03c4\u03c3", "\u03c0\u03b9\u03c4\u03c3", "\u03c0\u03b9\u03ba\u03b1\u03bd\u03c4", "\u03c0\u03bb\u03b9\u03b1\u03c4\u03c3", "\u03c0\u03bf\u03c3\u03c4\u03b5\u03bb\u03bd", "\u03c0\u03c1\u03c9\u03c4\u03bf\u03b4", "\u03c3\u03b5\u03c1\u03c4", "\u03c3\u03c5\u03bd\u03b1\u03b4", "\u03c4\u03c3\u03b1\u03bc", "\u03c5\u03c0\u03bf\u03b4", "\u03c6\u03b9\u03bb\u03bf\u03bd", "\u03c6\u03c5\u03bb\u03bf\u03b4", "\u03c7\u03b1\u03c3"), false);
    private static final CharArraySet exc7 = new CharArraySet(Version.LUCENE_31, Arrays.asList("\u03b1\u03bd\u03b1\u03c0", "\u03b1\u03c0\u03bf\u03b8", "\u03b1\u03c0\u03bf\u03ba", "\u03b1\u03c0\u03bf\u03c3\u03c4", "\u03b2\u03bf\u03c5\u03b2", "\u03be\u03b5\u03b8", "\u03bf\u03c5\u03bb", "\u03c0\u03b5\u03b8", "\u03c0\u03b9\u03ba\u03c1", "\u03c0\u03bf\u03c4", "\u03c3\u03b9\u03c7", "\u03c7"), false);
    private static final CharArraySet exc8a = new CharArraySet(Version.LUCENE_31, Arrays.asList("\u03c4\u03c1", "\u03c4\u03c3"), false);
    private static final CharArraySet exc8b = new CharArraySet(Version.LUCENE_31, Arrays.asList("\u03b2\u03b5\u03c4\u03b5\u03c1", "\u03b2\u03bf\u03c5\u03bb\u03ba", "\u03b2\u03c1\u03b1\u03c7\u03bc", "\u03b3", "\u03b4\u03c1\u03b1\u03b4\u03bf\u03c5\u03bc", "\u03b8", "\u03ba\u03b1\u03bb\u03c0\u03bf\u03c5\u03b6", "\u03ba\u03b1\u03c3\u03c4\u03b5\u03bb", "\u03ba\u03bf\u03c1\u03bc\u03bf\u03c1", "\u03bb\u03b1\u03bf\u03c0\u03bb", "\u03bc\u03c9\u03b1\u03bc\u03b5\u03b8", "\u03bc", "\u03bc\u03bf\u03c5\u03c3\u03bf\u03c5\u03bb\u03bc", "\u03bd", "\u03bf\u03c5\u03bb", "\u03c0", "\u03c0\u03b5\u03bb\u03b5\u03ba", "\u03c0\u03bb", "\u03c0\u03bf\u03bb\u03b9\u03c3", "\u03c0\u03bf\u03c1\u03c4\u03bf\u03bb", "\u03c3\u03b1\u03c1\u03b1\u03ba\u03b1\u03c4\u03c3", "\u03c3\u03bf\u03c5\u03bb\u03c4", "\u03c4\u03c3\u03b1\u03c1\u03bb\u03b1\u03c4", "\u03bf\u03c1\u03c6", "\u03c4\u03c3\u03b9\u03b3\u03b3", "\u03c4\u03c3\u03bf\u03c0", "\u03c6\u03c9\u03c4\u03bf\u03c3\u03c4\u03b5\u03c6", "\u03c7", "\u03c8\u03c5\u03c7\u03bf\u03c0\u03bb", "\u03b1\u03b3", "\u03bf\u03c1\u03c6", "\u03b3\u03b1\u03bb", "\u03b3\u03b5\u03c1", "\u03b4\u03b5\u03ba", "\u03b4\u03b9\u03c0\u03bb", "\u03b1\u03bc\u03b5\u03c1\u03b9\u03ba\u03b1\u03bd", "\u03bf\u03c5\u03c1", "\u03c0\u03b9\u03b8", "\u03c0\u03bf\u03c5\u03c1\u03b9\u03c4", "\u03c3", "\u03b6\u03c9\u03bd\u03c4", "\u03b9\u03ba", "\u03ba\u03b1\u03c3\u03c4", "\u03ba\u03bf\u03c0", "\u03bb\u03b9\u03c7", "\u03bb\u03bf\u03c5\u03b8\u03b7\u03c1", "\u03bc\u03b1\u03b9\u03bd\u03c4", "\u03bc\u03b5\u03bb", "\u03c3\u03b9\u03b3", "\u03c3\u03c0", "\u03c3\u03c4\u03b5\u03b3", "\u03c4\u03c1\u03b1\u03b3", "\u03c4\u03c3\u03b1\u03b3", "\u03c6", "\u03b5\u03c1", "\u03b1\u03b4\u03b1\u03c0", "\u03b1\u03b8\u03b9\u03b3\u03b3", "\u03b1\u03bc\u03b7\u03c7", "\u03b1\u03bd\u03b9\u03ba", "\u03b1\u03bd\u03bf\u03c1\u03b3", "\u03b1\u03c0\u03b7\u03b3", "\u03b1\u03c0\u03b9\u03b8", "\u03b1\u03c4\u03c3\u03b9\u03b3\u03b3", "\u03b2\u03b1\u03c3", "\u03b2\u03b1\u03c3\u03ba", "\u03b2\u03b1\u03b8\u03c5\u03b3\u03b1\u03bb", "\u03b2\u03b9\u03bf\u03bc\u03b7\u03c7", "\u03b2\u03c1\u03b1\u03c7\u03c5\u03ba", "\u03b4\u03b9\u03b1\u03c4", "\u03b4\u03b9\u03b1\u03c6", "\u03b5\u03bd\u03bf\u03c1\u03b3", "\u03b8\u03c5\u03c3", "\u03ba\u03b1\u03c0\u03bd\u03bf\u03b2\u03b9\u03bf\u03bc\u03b7\u03c7", "\u03ba\u03b1\u03c4\u03b1\u03b3\u03b1\u03bb", "\u03ba\u03bb\u03b9\u03b2", "\u03ba\u03bf\u03b9\u03bb\u03b1\u03c1\u03c6", "\u03bb\u03b9\u03b2", "\u03bc\u03b5\u03b3\u03bb\u03bf\u03b2\u03b9\u03bf\u03bc\u03b7\u03c7", "\u03bc\u03b9\u03ba\u03c1\u03bf\u03b2\u03b9\u03bf\u03bc\u03b7\u03c7", "\u03bd\u03c4\u03b1\u03b2", "\u03be\u03b7\u03c1\u03bf\u03ba\u03bb\u03b9\u03b2", "\u03bf\u03bb\u03b9\u03b3\u03bf\u03b4\u03b1\u03bc", "\u03bf\u03bb\u03bf\u03b3\u03b1\u03bb", "\u03c0\u03b5\u03bd\u03c4\u03b1\u03c1\u03c6", "\u03c0\u03b5\u03c1\u03b7\u03c6", "\u03c0\u03b5\u03c1\u03b9\u03c4\u03c1", "\u03c0\u03bb\u03b1\u03c4", "\u03c0\u03bf\u03bb\u03c5\u03b4\u03b1\u03c0", "\u03c0\u03bf\u03bb\u03c5\u03bc\u03b7\u03c7", "\u03c3\u03c4\u03b5\u03c6", "\u03c4\u03b1\u03b2", "\u03c4\u03b5\u03c4", "\u03c5\u03c0\u03b5\u03c1\u03b7\u03c6", "\u03c5\u03c0\u03bf\u03ba\u03bf\u03c0", "\u03c7\u03b1\u03bc\u03b7\u03bb\u03bf\u03b4\u03b1\u03c0", "\u03c8\u03b7\u03bb\u03bf\u03c4\u03b1\u03b2"), false);
    private static final CharArraySet exc9 = new CharArraySet(Version.LUCENE_31, Arrays.asList("\u03b1\u03b2\u03b1\u03c1", "\u03b2\u03b5\u03bd", "\u03b5\u03bd\u03b1\u03c1", "\u03b1\u03b2\u03c1", "\u03b1\u03b4", "\u03b1\u03b8", "\u03b1\u03bd", "\u03b1\u03c0\u03bb", "\u03b2\u03b1\u03c1\u03bf\u03bd", "\u03bd\u03c4\u03c1", "\u03c3\u03ba", "\u03ba\u03bf\u03c0", "\u03bc\u03c0\u03bf\u03c1", "\u03bd\u03b9\u03c6", "\u03c0\u03b1\u03b3", "\u03c0\u03b1\u03c1\u03b1\u03ba\u03b1\u03bb", "\u03c3\u03b5\u03c1\u03c0", "\u03c3\u03ba\u03b5\u03bb", "\u03c3\u03c5\u03c1\u03c6", "\u03c4\u03bf\u03ba", "\u03c5", "\u03b4", "\u03b5\u03bc", "\u03b8\u03b1\u03c1\u03c1", "\u03b8"), false);
    private static final CharArraySet exc12a = new CharArraySet(Version.LUCENE_31, Arrays.asList("\u03c0", "\u03b1\u03c0", "\u03c3\u03c5\u03bc\u03c0", "\u03b1\u03c3\u03c5\u03bc\u03c0", "\u03b1\u03ba\u03b1\u03c4\u03b1\u03c0", "\u03b1\u03bc\u03b5\u03c4\u03b1\u03bc\u03c6"), false);
    private static final CharArraySet exc12b = new CharArraySet(Version.LUCENE_31, Arrays.asList("\u03b1\u03bb", "\u03b1\u03c1", "\u03b5\u03ba\u03c4\u03b5\u03bb", "\u03b6", "\u03bc", "\u03be", "\u03c0\u03b1\u03c1\u03b1\u03ba\u03b1\u03bb", "\u03b1\u03c1", "\u03c0\u03c1\u03bf", "\u03bd\u03b9\u03c3"), false);
    private static final CharArraySet exc13 = new CharArraySet(Version.LUCENE_31, Arrays.asList("\u03b4\u03b9\u03b1\u03b8", "\u03b8", "\u03c0\u03b1\u03c1\u03b1\u03ba\u03b1\u03c4\u03b1\u03b8", "\u03c0\u03c1\u03bf\u03c3\u03b8", "\u03c3\u03c5\u03bd\u03b8"), false);
    private static final CharArraySet exc14 = new CharArraySet(Version.LUCENE_31, Arrays.asList("\u03c6\u03b1\u03c1\u03bc\u03b1\u03ba", "\u03c7\u03b1\u03b4", "\u03b1\u03b3\u03ba", "\u03b1\u03bd\u03b1\u03c1\u03c1", "\u03b2\u03c1\u03bf\u03bc", "\u03b5\u03ba\u03bb\u03b9\u03c0", "\u03bb\u03b1\u03bc\u03c0\u03b9\u03b4", "\u03bb\u03b5\u03c7", "\u03bc", "\u03c0\u03b1\u03c4", "\u03c1", "\u03bb", "\u03bc\u03b5\u03b4", "\u03bc\u03b5\u03c3\u03b1\u03b6", "\u03c5\u03c0\u03bf\u03c4\u03b5\u03b9\u03bd", "\u03b1\u03bc", "\u03b1\u03b9\u03b8", "\u03b1\u03bd\u03b7\u03ba", "\u03b4\u03b5\u03c3\u03c0\u03bf\u03b6", "\u03b5\u03bd\u03b4\u03b9\u03b1\u03c6\u03b5\u03c1", "\u03b4\u03b5", "\u03b4\u03b5\u03c5\u03c4\u03b5\u03c1\u03b5\u03c5", "\u03ba\u03b1\u03b8\u03b1\u03c1\u03b5\u03c5", "\u03c0\u03bb\u03b5", "\u03c4\u03c3\u03b1"), false);
    private static final CharArraySet exc15a = new CharArraySet(Version.LUCENE_31, Arrays.asList("\u03b1\u03b2\u03b1\u03c3\u03c4", "\u03c0\u03bf\u03bb\u03c5\u03c6", "\u03b1\u03b4\u03b7\u03c6", "\u03c0\u03b1\u03bc\u03c6", "\u03c1", "\u03b1\u03c3\u03c0", "\u03b1\u03c6", "\u03b1\u03bc\u03b1\u03bb", "\u03b1\u03bc\u03b1\u03bb\u03bb\u03b9", "\u03b1\u03bd\u03c5\u03c3\u03c4", "\u03b1\u03c0\u03b5\u03c1", "\u03b1\u03c3\u03c0\u03b1\u03c1", "\u03b1\u03c7\u03b1\u03c1", "\u03b4\u03b5\u03c1\u03b2\u03b5\u03bd", "\u03b4\u03c1\u03bf\u03c3\u03bf\u03c0", "\u03be\u03b5\u03c6", "\u03bd\u03b5\u03bf\u03c0", "\u03bd\u03bf\u03bc\u03bf\u03c4", "\u03bf\u03bb\u03bf\u03c0", "\u03bf\u03bc\u03bf\u03c4", "\u03c0\u03c1\u03bf\u03c3\u03c4", "\u03c0\u03c1\u03bf\u03c3\u03c9\u03c0\u03bf\u03c0", "\u03c3\u03c5\u03bc\u03c0", "\u03c3\u03c5\u03bd\u03c4", "\u03c4", "\u03c5\u03c0\u03bf\u03c4", "\u03c7\u03b1\u03c1", "\u03b1\u03b5\u03b9\u03c0", "\u03b1\u03b9\u03bc\u03bf\u03c3\u03c4", "\u03b1\u03bd\u03c5\u03c0", "\u03b1\u03c0\u03bf\u03c4", "\u03b1\u03c1\u03c4\u03b9\u03c0", "\u03b4\u03b9\u03b1\u03c4", "\u03b5\u03bd", "\u03b5\u03c0\u03b9\u03c4", "\u03ba\u03c1\u03bf\u03ba\u03b1\u03bb\u03bf\u03c0", "\u03c3\u03b9\u03b4\u03b7\u03c1\u03bf\u03c0", "\u03bb", "\u03bd\u03b1\u03c5", "\u03bf\u03c5\u03bb\u03b1\u03bc", "\u03bf\u03c5\u03c1", "\u03c0", "\u03c4\u03c1", "\u03bc"), false);
    private static final CharArraySet exc15b = new CharArraySet(Version.LUCENE_31, Arrays.asList("\u03c8\u03bf\u03c6", "\u03bd\u03b1\u03c5\u03bb\u03bf\u03c7"), false);
    private static final CharArraySet exc16 = new CharArraySet(Version.LUCENE_31, Arrays.asList("\u03bd", "\u03c7\u03b5\u03c1\u03c3\u03bf\u03bd", "\u03b4\u03c9\u03b4\u03b5\u03ba\u03b1\u03bd", "\u03b5\u03c1\u03b7\u03bc\u03bf\u03bd", "\u03bc\u03b5\u03b3\u03b1\u03bb\u03bf\u03bd", "\u03b5\u03c0\u03c4\u03b1\u03bd"), false);
    private static final CharArraySet exc17 = new CharArraySet(Version.LUCENE_31, Arrays.asList("\u03b1\u03c3\u03b2", "\u03c3\u03b2", "\u03b1\u03c7\u03c1", "\u03c7\u03c1", "\u03b1\u03c0\u03bb", "\u03b1\u03b5\u03b9\u03bc\u03bd", "\u03b4\u03c5\u03c3\u03c7\u03c1", "\u03b5\u03c5\u03c7\u03c1", "\u03ba\u03bf\u03b9\u03bd\u03bf\u03c7\u03c1", "\u03c0\u03b1\u03bb\u03b9\u03bc\u03c8"), false);
    private static final CharArraySet exc18 = new CharArraySet(Version.LUCENE_31, Arrays.asList("\u03bd", "\u03c1", "\u03c3\u03c0\u03b9", "\u03c3\u03c4\u03c1\u03b1\u03b2\u03bf\u03bc\u03bf\u03c5\u03c4\u03c3", "\u03ba\u03b1\u03ba\u03bf\u03bc\u03bf\u03c5\u03c4\u03c3", "\u03b5\u03be\u03c9\u03bd"), false);
    private static final CharArraySet exc19 = new CharArraySet(Version.LUCENE_31, Arrays.asList("\u03c0\u03b1\u03c1\u03b1\u03c3\u03bf\u03c5\u03c3", "\u03c6", "\u03c7", "\u03c9\u03c1\u03b9\u03bf\u03c0\u03bb", "\u03b1\u03b6", "\u03b1\u03bb\u03bb\u03bf\u03c3\u03bf\u03c5\u03c3", "\u03b1\u03c3\u03bf\u03c5\u03c3"), false);

    public int stem(char[] s, int len) {
        if (len < 4) {
            return len;
        }
        int origLen = len;
        len = this.rule0(s, len);
        len = this.rule1(s, len);
        len = this.rule2(s, len);
        len = this.rule3(s, len);
        len = this.rule4(s, len);
        len = this.rule5(s, len);
        len = this.rule6(s, len);
        len = this.rule7(s, len);
        len = this.rule8(s, len);
        len = this.rule9(s, len);
        len = this.rule10(s, len);
        len = this.rule11(s, len);
        len = this.rule12(s, len);
        len = this.rule13(s, len);
        len = this.rule14(s, len);
        len = this.rule15(s, len);
        len = this.rule16(s, len);
        len = this.rule17(s, len);
        len = this.rule18(s, len);
        len = this.rule19(s, len);
        if ((len = this.rule20(s, len)) == origLen) {
            len = this.rule21(s, len);
        }
        return this.rule22(s, len);
    }

    private int rule0(char[] s, int len) {
        if (len > 9 && (this.endsWith(s, len, "\u03ba\u03b1\u03b8\u03b5\u03c3\u03c4\u03c9\u03c4\u03bf\u03c3") || this.endsWith(s, len, "\u03ba\u03b1\u03b8\u03b5\u03c3\u03c4\u03c9\u03c4\u03c9\u03bd"))) {
            return len - 4;
        }
        if (len > 8 && (this.endsWith(s, len, "\u03b3\u03b5\u03b3\u03bf\u03bd\u03bf\u03c4\u03bf\u03c3") || this.endsWith(s, len, "\u03b3\u03b5\u03b3\u03bf\u03bd\u03bf\u03c4\u03c9\u03bd"))) {
            return len - 4;
        }
        if (len > 8 && this.endsWith(s, len, "\u03ba\u03b1\u03b8\u03b5\u03c3\u03c4\u03c9\u03c4\u03b1")) {
            return len - 3;
        }
        if (len > 7 && (this.endsWith(s, len, "\u03c4\u03b1\u03c4\u03bf\u03b3\u03b9\u03bf\u03c5") || this.endsWith(s, len, "\u03c4\u03b1\u03c4\u03bf\u03b3\u03b9\u03c9\u03bd"))) {
            return len - 4;
        }
        if (len > 7 && this.endsWith(s, len, "\u03b3\u03b5\u03b3\u03bf\u03bd\u03bf\u03c4\u03b1")) {
            return len - 3;
        }
        if (len > 7 && this.endsWith(s, len, "\u03ba\u03b1\u03b8\u03b5\u03c3\u03c4\u03c9\u03c3")) {
            return len - 2;
        }
        if (len > 6 && this.endsWith(s, len, "\u03c3\u03ba\u03b1\u03b3\u03b9\u03bf\u03c5") || this.endsWith(s, len, "\u03c3\u03ba\u03b1\u03b3\u03b9\u03c9\u03bd") || this.endsWith(s, len, "\u03bf\u03bb\u03bf\u03b3\u03b9\u03bf\u03c5") || this.endsWith(s, len, "\u03bf\u03bb\u03bf\u03b3\u03b9\u03c9\u03bd") || this.endsWith(s, len, "\u03ba\u03c1\u03b5\u03b1\u03c4\u03bf\u03c3") || this.endsWith(s, len, "\u03ba\u03c1\u03b5\u03b1\u03c4\u03c9\u03bd") || this.endsWith(s, len, "\u03c0\u03b5\u03c1\u03b1\u03c4\u03bf\u03c3") || this.endsWith(s, len, "\u03c0\u03b5\u03c1\u03b1\u03c4\u03c9\u03bd") || this.endsWith(s, len, "\u03c4\u03b5\u03c1\u03b1\u03c4\u03bf\u03c3") || this.endsWith(s, len, "\u03c4\u03b5\u03c1\u03b1\u03c4\u03c9\u03bd")) {
            return len - 4;
        }
        if (len > 6 && this.endsWith(s, len, "\u03c4\u03b1\u03c4\u03bf\u03b3\u03b9\u03b1")) {
            return len - 3;
        }
        if (len > 6 && this.endsWith(s, len, "\u03b3\u03b5\u03b3\u03bf\u03bd\u03bf\u03c3")) {
            return len - 2;
        }
        if (len > 5 && (this.endsWith(s, len, "\u03c6\u03b1\u03b3\u03b9\u03bf\u03c5") || this.endsWith(s, len, "\u03c6\u03b1\u03b3\u03b9\u03c9\u03bd") || this.endsWith(s, len, "\u03c3\u03bf\u03b3\u03b9\u03bf\u03c5") || this.endsWith(s, len, "\u03c3\u03bf\u03b3\u03b9\u03c9\u03bd"))) {
            return len - 4;
        }
        if (len > 5 && (this.endsWith(s, len, "\u03c3\u03ba\u03b1\u03b3\u03b9\u03b1") || this.endsWith(s, len, "\u03bf\u03bb\u03bf\u03b3\u03b9\u03b1") || this.endsWith(s, len, "\u03ba\u03c1\u03b5\u03b1\u03c4\u03b1") || this.endsWith(s, len, "\u03c0\u03b5\u03c1\u03b1\u03c4\u03b1") || this.endsWith(s, len, "\u03c4\u03b5\u03c1\u03b1\u03c4\u03b1"))) {
            return len - 3;
        }
        if (len > 4 && (this.endsWith(s, len, "\u03c6\u03b1\u03b3\u03b9\u03b1") || this.endsWith(s, len, "\u03c3\u03bf\u03b3\u03b9\u03b1") || this.endsWith(s, len, "\u03c6\u03c9\u03c4\u03bf\u03c3") || this.endsWith(s, len, "\u03c6\u03c9\u03c4\u03c9\u03bd"))) {
            return len - 3;
        }
        if (len > 4 && (this.endsWith(s, len, "\u03ba\u03c1\u03b5\u03b1\u03c3") || this.endsWith(s, len, "\u03c0\u03b5\u03c1\u03b1\u03c3") || this.endsWith(s, len, "\u03c4\u03b5\u03c1\u03b1\u03c3"))) {
            return len - 2;
        }
        if (len > 3 && this.endsWith(s, len, "\u03c6\u03c9\u03c4\u03b1")) {
            return len - 2;
        }
        if (len > 2 && this.endsWith(s, len, "\u03c6\u03c9\u03c3")) {
            return len - 1;
        }
        return len;
    }

    private int rule1(char[] s, int len) {
        if (!(len <= 4 || !this.endsWith(s, len, "\u03b1\u03b4\u03b5\u03c3") && !this.endsWith(s, len, "\u03b1\u03b4\u03c9\u03bd") || this.endsWith(s, len -= 4, "\u03bf\u03ba") || this.endsWith(s, len, "\u03bc\u03b1\u03bc") || this.endsWith(s, len, "\u03bc\u03b1\u03bd") || this.endsWith(s, len, "\u03bc\u03c0\u03b1\u03bc\u03c0") || this.endsWith(s, len, "\u03c0\u03b1\u03c4\u03b5\u03c1") || this.endsWith(s, len, "\u03b3\u03b9\u03b1\u03b3\u03b9") || this.endsWith(s, len, "\u03bd\u03c4\u03b1\u03bd\u03c4") || this.endsWith(s, len, "\u03ba\u03c5\u03c1") || this.endsWith(s, len, "\u03b8\u03b5\u03b9") || this.endsWith(s, len, "\u03c0\u03b5\u03b8\u03b5\u03c1"))) {
            len += 2;
        }
        return len;
    }

    private int rule2(char[] s, int len) {
        if (len > 4 && (this.endsWith(s, len, "\u03b5\u03b4\u03b5\u03c3") || this.endsWith(s, len, "\u03b5\u03b4\u03c9\u03bd")) && (this.endsWith(s, len -= 4, "\u03bf\u03c0") || this.endsWith(s, len, "\u03b9\u03c0") || this.endsWith(s, len, "\u03b5\u03bc\u03c0") || this.endsWith(s, len, "\u03c5\u03c0") || this.endsWith(s, len, "\u03b3\u03b7\u03c0") || this.endsWith(s, len, "\u03b4\u03b1\u03c0") || this.endsWith(s, len, "\u03ba\u03c1\u03b1\u03c3\u03c0") || this.endsWith(s, len, "\u03bc\u03b9\u03bb"))) {
            len += 2;
        }
        return len;
    }

    private int rule3(char[] s, int len) {
        if (len > 5 && (this.endsWith(s, len, "\u03bf\u03c5\u03b4\u03b5\u03c3") || this.endsWith(s, len, "\u03bf\u03c5\u03b4\u03c9\u03bd")) && (this.endsWith(s, len -= 5, "\u03b1\u03c1\u03ba") || this.endsWith(s, len, "\u03ba\u03b1\u03bb\u03b9\u03b1\u03ba") || this.endsWith(s, len, "\u03c0\u03b5\u03c4\u03b1\u03bb") || this.endsWith(s, len, "\u03bb\u03b9\u03c7") || this.endsWith(s, len, "\u03c0\u03bb\u03b5\u03be") || this.endsWith(s, len, "\u03c3\u03ba") || this.endsWith(s, len, "\u03c3") || this.endsWith(s, len, "\u03c6\u03bb") || this.endsWith(s, len, "\u03c6\u03c1") || this.endsWith(s, len, "\u03b2\u03b5\u03bb") || this.endsWith(s, len, "\u03bb\u03bf\u03c5\u03bb") || this.endsWith(s, len, "\u03c7\u03bd") || this.endsWith(s, len, "\u03c3\u03c0") || this.endsWith(s, len, "\u03c4\u03c1\u03b1\u03b3") || this.endsWith(s, len, "\u03c6\u03b5"))) {
            len += 3;
        }
        return len;
    }

    private int rule4(char[] s, int len) {
        if (len > 3 && (this.endsWith(s, len, "\u03b5\u03c9\u03c3") || this.endsWith(s, len, "\u03b5\u03c9\u03bd")) && exc4.contains(s, 0, len -= 3)) {
            ++len;
        }
        return len;
    }

    private int rule5(char[] s, int len) {
        if (len > 2 && this.endsWith(s, len, "\u03b9\u03b1")) {
            if (this.endsWithVowel(s, len -= 2)) {
                ++len;
            }
        } else if (len > 3 && (this.endsWith(s, len, "\u03b9\u03bf\u03c5") || this.endsWith(s, len, "\u03b9\u03c9\u03bd")) && this.endsWithVowel(s, len -= 3)) {
            ++len;
        }
        return len;
    }

    private int rule6(char[] s, int len) {
        boolean removed = false;
        if (len > 3 && (this.endsWith(s, len, "\u03b9\u03ba\u03b1") || this.endsWith(s, len, "\u03b9\u03ba\u03bf"))) {
            len -= 3;
            removed = true;
        } else if (len > 4 && (this.endsWith(s, len, "\u03b9\u03ba\u03bf\u03c5") || this.endsWith(s, len, "\u03b9\u03ba\u03c9\u03bd"))) {
            len -= 4;
            removed = true;
        }
        if (removed && (this.endsWithVowel(s, len) || exc6.contains(s, 0, len))) {
            len += 2;
        }
        return len;
    }

    private int rule7(char[] s, int len) {
        if (len == 5 && this.endsWith(s, len, "\u03b1\u03b3\u03b1\u03bc\u03b5")) {
            return len - 1;
        }
        if (len > 7 && this.endsWith(s, len, "\u03b7\u03b8\u03b7\u03ba\u03b1\u03bc\u03b5")) {
            len -= 7;
        } else if (len > 6 && this.endsWith(s, len, "\u03bf\u03c5\u03c3\u03b1\u03bc\u03b5")) {
            len -= 6;
        } else if (len > 5 && (this.endsWith(s, len, "\u03b1\u03b3\u03b1\u03bc\u03b5") || this.endsWith(s, len, "\u03b7\u03c3\u03b1\u03bc\u03b5") || this.endsWith(s, len, "\u03b7\u03ba\u03b1\u03bc\u03b5"))) {
            len -= 5;
        }
        if (len > 3 && this.endsWith(s, len, "\u03b1\u03bc\u03b5") && exc7.contains(s, 0, len -= 3)) {
            len += 2;
        }
        return len;
    }

    private int rule8(char[] s, int len) {
        boolean removed = false;
        if (len > 8 && this.endsWith(s, len, "\u03b9\u03bf\u03c5\u03bd\u03c4\u03b1\u03bd\u03b5")) {
            len -= 8;
            removed = true;
        } else if (len > 7 && this.endsWith(s, len, "\u03b9\u03bf\u03bd\u03c4\u03b1\u03bd\u03b5") || this.endsWith(s, len, "\u03bf\u03c5\u03bd\u03c4\u03b1\u03bd\u03b5") || this.endsWith(s, len, "\u03b7\u03b8\u03b7\u03ba\u03b1\u03bd\u03b5")) {
            len -= 7;
            removed = true;
        } else if (len > 6 && this.endsWith(s, len, "\u03b9\u03bf\u03c4\u03b1\u03bd\u03b5") || this.endsWith(s, len, "\u03bf\u03bd\u03c4\u03b1\u03bd\u03b5") || this.endsWith(s, len, "\u03bf\u03c5\u03c3\u03b1\u03bd\u03b5")) {
            len -= 6;
            removed = true;
        } else if (len > 5 && this.endsWith(s, len, "\u03b1\u03b3\u03b1\u03bd\u03b5") || this.endsWith(s, len, "\u03b7\u03c3\u03b1\u03bd\u03b5") || this.endsWith(s, len, "\u03bf\u03c4\u03b1\u03bd\u03b5") || this.endsWith(s, len, "\u03b7\u03ba\u03b1\u03bd\u03b5")) {
            len -= 5;
            removed = true;
        }
        if (removed && exc8a.contains(s, 0, len)) {
            s[(len += 4) - 4] = 945;
            s[len - 3] = 947;
            s[len - 2] = 945;
            s[len - 1] = 957;
        }
        if (len > 3 && this.endsWith(s, len, "\u03b1\u03bd\u03b5") && (this.endsWithVowelNoY(s, len -= 3) || exc8b.contains(s, 0, len))) {
            len += 2;
        }
        return len;
    }

    private int rule9(char[] s, int len) {
        if (len > 5 && this.endsWith(s, len, "\u03b7\u03c3\u03b5\u03c4\u03b5")) {
            len -= 5;
        }
        if (len > 3 && this.endsWith(s, len, "\u03b5\u03c4\u03b5") && (exc9.contains(s, 0, len -= 3) || this.endsWithVowelNoY(s, len) || this.endsWith(s, len, "\u03bf\u03b4") || this.endsWith(s, len, "\u03b1\u03b9\u03c1") || this.endsWith(s, len, "\u03c6\u03bf\u03c1") || this.endsWith(s, len, "\u03c4\u03b1\u03b8") || this.endsWith(s, len, "\u03b4\u03b9\u03b1\u03b8") || this.endsWith(s, len, "\u03c3\u03c7") || this.endsWith(s, len, "\u03b5\u03bd\u03b4") || this.endsWith(s, len, "\u03b5\u03c5\u03c1") || this.endsWith(s, len, "\u03c4\u03b9\u03b8") || this.endsWith(s, len, "\u03c5\u03c0\u03b5\u03c1\u03b8") || this.endsWith(s, len, "\u03c1\u03b1\u03b8") || this.endsWith(s, len, "\u03b5\u03bd\u03b8") || this.endsWith(s, len, "\u03c1\u03bf\u03b8") || this.endsWith(s, len, "\u03c3\u03b8") || this.endsWith(s, len, "\u03c0\u03c5\u03c1") || this.endsWith(s, len, "\u03b1\u03b9\u03bd") || this.endsWith(s, len, "\u03c3\u03c5\u03bd\u03b4") || this.endsWith(s, len, "\u03c3\u03c5\u03bd") || this.endsWith(s, len, "\u03c3\u03c5\u03bd\u03b8") || this.endsWith(s, len, "\u03c7\u03c9\u03c1") || this.endsWith(s, len, "\u03c0\u03bf\u03bd") || this.endsWith(s, len, "\u03b2\u03c1") || this.endsWith(s, len, "\u03ba\u03b1\u03b8") || this.endsWith(s, len, "\u03b5\u03c5\u03b8") || this.endsWith(s, len, "\u03b5\u03ba\u03b8") || this.endsWith(s, len, "\u03bd\u03b5\u03c4") || this.endsWith(s, len, "\u03c1\u03bf\u03bd") || this.endsWith(s, len, "\u03b1\u03c1\u03ba") || this.endsWith(s, len, "\u03b2\u03b1\u03c1") || this.endsWith(s, len, "\u03b2\u03bf\u03bb") || this.endsWith(s, len, "\u03c9\u03c6\u03b5\u03bb"))) {
            len += 2;
        }
        return len;
    }

    private int rule10(char[] s, int len) {
        if (len > 5 && (this.endsWith(s, len, "\u03bf\u03bd\u03c4\u03b1\u03c3") || this.endsWith(s, len, "\u03c9\u03bd\u03c4\u03b1\u03c3"))) {
            if ((len -= 5) == 3 && this.endsWith(s, len, "\u03b1\u03c1\u03c7")) {
                s[(len += 3) - 3] = 959;
            }
            if (this.endsWith(s, len, "\u03ba\u03c1\u03b5")) {
                s[(len += 3) - 3] = 969;
            }
        }
        return len;
    }

    private int rule11(char[] s, int len) {
        if (len > 6 && this.endsWith(s, len, "\u03bf\u03bc\u03b1\u03c3\u03c4\u03b5")) {
            if ((len -= 6) == 2 && this.endsWith(s, len, "\u03bf\u03bd")) {
                len += 5;
            }
        } else if (len > 7 && this.endsWith(s, len, "\u03b9\u03bf\u03bc\u03b1\u03c3\u03c4\u03b5") && (len -= 7) == 2 && this.endsWith(s, len, "\u03bf\u03bd")) {
            s[(len += 5) - 5] = 959;
            s[len - 4] = 956;
            s[len - 3] = 945;
            s[len - 2] = 963;
            s[len - 1] = 964;
        }
        return len;
    }

    private int rule12(char[] s, int len) {
        if (len > 5 && this.endsWith(s, len, "\u03b9\u03b5\u03c3\u03c4\u03b5") && exc12a.contains(s, 0, len -= 5)) {
            len += 4;
        }
        if (len > 4 && this.endsWith(s, len, "\u03b5\u03c3\u03c4\u03b5") && exc12b.contains(s, 0, len -= 4)) {
            len += 3;
        }
        return len;
    }

    private int rule13(char[] s, int len) {
        if (len > 6 && this.endsWith(s, len, "\u03b7\u03b8\u03b7\u03ba\u03b5\u03c3")) {
            len -= 6;
        } else if (len > 5 && (this.endsWith(s, len, "\u03b7\u03b8\u03b7\u03ba\u03b1") || this.endsWith(s, len, "\u03b7\u03b8\u03b7\u03ba\u03b5"))) {
            len -= 5;
        }
        boolean removed = false;
        if (len > 4 && this.endsWith(s, len, "\u03b7\u03ba\u03b5\u03c3")) {
            len -= 4;
            removed = true;
        } else if (len > 3 && (this.endsWith(s, len, "\u03b7\u03ba\u03b1") || this.endsWith(s, len, "\u03b7\u03ba\u03b5"))) {
            len -= 3;
            removed = true;
        }
        if (removed && (exc13.contains(s, 0, len) || this.endsWith(s, len, "\u03c3\u03ba\u03c9\u03bb") || this.endsWith(s, len, "\u03c3\u03ba\u03bf\u03c5\u03bb") || this.endsWith(s, len, "\u03bd\u03b1\u03c1\u03b8") || this.endsWith(s, len, "\u03c3\u03c6") || this.endsWith(s, len, "\u03bf\u03b8") || this.endsWith(s, len, "\u03c0\u03b9\u03b8"))) {
            len += 2;
        }
        return len;
    }

    private int rule14(char[] s, int len) {
        boolean removed = false;
        if (len > 5 && this.endsWith(s, len, "\u03bf\u03c5\u03c3\u03b5\u03c3")) {
            len -= 5;
            removed = true;
        } else if (len > 4 && (this.endsWith(s, len, "\u03bf\u03c5\u03c3\u03b1") || this.endsWith(s, len, "\u03bf\u03c5\u03c3\u03b5"))) {
            len -= 4;
            removed = true;
        }
        if (removed && (exc14.contains(s, 0, len) || this.endsWithVowel(s, len) || this.endsWith(s, len, "\u03c0\u03bf\u03b4\u03b1\u03c1") || this.endsWith(s, len, "\u03b2\u03bb\u03b5\u03c0") || this.endsWith(s, len, "\u03c0\u03b1\u03bd\u03c4\u03b1\u03c7") || this.endsWith(s, len, "\u03c6\u03c1\u03c5\u03b4") || this.endsWith(s, len, "\u03bc\u03b1\u03bd\u03c4\u03b9\u03bb") || this.endsWith(s, len, "\u03bc\u03b1\u03bb\u03bb") || this.endsWith(s, len, "\u03ba\u03c5\u03bc\u03b1\u03c4") || this.endsWith(s, len, "\u03bb\u03b1\u03c7") || this.endsWith(s, len, "\u03bb\u03b7\u03b3") || this.endsWith(s, len, "\u03c6\u03b1\u03b3") || this.endsWith(s, len, "\u03bf\u03bc") || this.endsWith(s, len, "\u03c0\u03c1\u03c9\u03c4"))) {
            len += 3;
        }
        return len;
    }

    private int rule15(char[] s, int len) {
        boolean removed = false;
        if (len > 4 && this.endsWith(s, len, "\u03b1\u03b3\u03b5\u03c3")) {
            len -= 4;
            removed = true;
        } else if (len > 3 && (this.endsWith(s, len, "\u03b1\u03b3\u03b1") || this.endsWith(s, len, "\u03b1\u03b3\u03b5"))) {
            len -= 3;
            removed = true;
        }
        if (removed) {
            boolean cond2;
            boolean cond1 = exc15a.contains(s, 0, len) || this.endsWith(s, len, "\u03bf\u03c6") || this.endsWith(s, len, "\u03c0\u03b5\u03bb") || this.endsWith(s, len, "\u03c7\u03bf\u03c1\u03c4") || this.endsWith(s, len, "\u03bb\u03bb") || this.endsWith(s, len, "\u03c3\u03c6") || this.endsWith(s, len, "\u03c1\u03c0") || this.endsWith(s, len, "\u03c6\u03c1") || this.endsWith(s, len, "\u03c0\u03c1") || this.endsWith(s, len, "\u03bb\u03bf\u03c7") || this.endsWith(s, len, "\u03c3\u03bc\u03b7\u03bd");
            boolean bl = cond2 = exc15b.contains(s, 0, len) || this.endsWith(s, len, "\u03ba\u03bf\u03bb\u03bb");
            if (cond1 && !cond2) {
                len += 2;
            }
        }
        return len;
    }

    private int rule16(char[] s, int len) {
        boolean removed = false;
        if (len > 4 && this.endsWith(s, len, "\u03b7\u03c3\u03bf\u03c5")) {
            len -= 4;
            removed = true;
        } else if (len > 3 && (this.endsWith(s, len, "\u03b7\u03c3\u03b5") || this.endsWith(s, len, "\u03b7\u03c3\u03b1"))) {
            len -= 3;
            removed = true;
        }
        if (removed && exc16.contains(s, 0, len)) {
            len += 2;
        }
        return len;
    }

    private int rule17(char[] s, int len) {
        if (len > 4 && this.endsWith(s, len, "\u03b7\u03c3\u03c4\u03b5") && exc17.contains(s, 0, len -= 4)) {
            len += 3;
        }
        return len;
    }

    private int rule18(char[] s, int len) {
        boolean removed = false;
        if (len > 6 && (this.endsWith(s, len, "\u03b7\u03c3\u03bf\u03c5\u03bd\u03b5") || this.endsWith(s, len, "\u03b7\u03b8\u03bf\u03c5\u03bd\u03b5"))) {
            len -= 6;
            removed = true;
        } else if (len > 4 && this.endsWith(s, len, "\u03bf\u03c5\u03bd\u03b5")) {
            len -= 4;
            removed = true;
        }
        if (removed && exc18.contains(s, 0, len)) {
            s[(len += 3) - 3] = 959;
            s[len - 2] = 965;
            s[len - 1] = 957;
        }
        return len;
    }

    private int rule19(char[] s, int len) {
        boolean removed = false;
        if (len > 6 && (this.endsWith(s, len, "\u03b7\u03c3\u03bf\u03c5\u03bc\u03b5") || this.endsWith(s, len, "\u03b7\u03b8\u03bf\u03c5\u03bc\u03b5"))) {
            len -= 6;
            removed = true;
        } else if (len > 4 && this.endsWith(s, len, "\u03bf\u03c5\u03bc\u03b5")) {
            len -= 4;
            removed = true;
        }
        if (removed && exc19.contains(s, 0, len)) {
            s[(len += 3) - 3] = 959;
            s[len - 2] = 965;
            s[len - 1] = 956;
        }
        return len;
    }

    private int rule20(char[] s, int len) {
        if (len > 5 && (this.endsWith(s, len, "\u03bc\u03b1\u03c4\u03c9\u03bd") || this.endsWith(s, len, "\u03bc\u03b1\u03c4\u03bf\u03c3"))) {
            len -= 3;
        } else if (len > 4 && this.endsWith(s, len, "\u03bc\u03b1\u03c4\u03b1")) {
            len -= 2;
        }
        return len;
    }

    private int rule21(char[] s, int len) {
        if (len > 9 && this.endsWith(s, len, "\u03b9\u03bf\u03bd\u03c4\u03bf\u03c5\u03c3\u03b1\u03bd")) {
            return len - 9;
        }
        if (len > 8 && (this.endsWith(s, len, "\u03b9\u03bf\u03bc\u03b1\u03c3\u03c4\u03b1\u03bd") || this.endsWith(s, len, "\u03b9\u03bf\u03c3\u03b1\u03c3\u03c4\u03b1\u03bd") || this.endsWith(s, len, "\u03b9\u03bf\u03c5\u03bc\u03b1\u03c3\u03c4\u03b5") || this.endsWith(s, len, "\u03bf\u03bd\u03c4\u03bf\u03c5\u03c3\u03b1\u03bd"))) {
            return len - 8;
        }
        if (len > 7 && (this.endsWith(s, len, "\u03b9\u03b5\u03bc\u03b1\u03c3\u03c4\u03b5") || this.endsWith(s, len, "\u03b9\u03b5\u03c3\u03b1\u03c3\u03c4\u03b5") || this.endsWith(s, len, "\u03b9\u03bf\u03bc\u03bf\u03c5\u03bd\u03b1") || this.endsWith(s, len, "\u03b9\u03bf\u03c3\u03b1\u03c3\u03c4\u03b5") || this.endsWith(s, len, "\u03b9\u03bf\u03c3\u03bf\u03c5\u03bd\u03b1") || this.endsWith(s, len, "\u03b9\u03bf\u03c5\u03bd\u03c4\u03b1\u03b9") || this.endsWith(s, len, "\u03b9\u03bf\u03c5\u03bd\u03c4\u03b1\u03bd") || this.endsWith(s, len, "\u03b7\u03b8\u03b7\u03ba\u03b1\u03c4\u03b5") || this.endsWith(s, len, "\u03bf\u03bc\u03b1\u03c3\u03c4\u03b1\u03bd") || this.endsWith(s, len, "\u03bf\u03c3\u03b1\u03c3\u03c4\u03b1\u03bd") || this.endsWith(s, len, "\u03bf\u03c5\u03bc\u03b1\u03c3\u03c4\u03b5"))) {
            return len - 7;
        }
        if (len > 6 && (this.endsWith(s, len, "\u03b9\u03bf\u03bc\u03bf\u03c5\u03bd") || this.endsWith(s, len, "\u03b9\u03bf\u03bd\u03c4\u03b1\u03bd") || this.endsWith(s, len, "\u03b9\u03bf\u03c3\u03bf\u03c5\u03bd") || this.endsWith(s, len, "\u03b7\u03b8\u03b5\u03b9\u03c4\u03b5") || this.endsWith(s, len, "\u03b7\u03b8\u03b7\u03ba\u03b1\u03bd") || this.endsWith(s, len, "\u03bf\u03bc\u03bf\u03c5\u03bd\u03b1") || this.endsWith(s, len, "\u03bf\u03c3\u03b1\u03c3\u03c4\u03b5") || this.endsWith(s, len, "\u03bf\u03c3\u03bf\u03c5\u03bd\u03b1") || this.endsWith(s, len, "\u03bf\u03c5\u03bd\u03c4\u03b1\u03b9") || this.endsWith(s, len, "\u03bf\u03c5\u03bd\u03c4\u03b1\u03bd") || this.endsWith(s, len, "\u03bf\u03c5\u03c3\u03b1\u03c4\u03b5"))) {
            return len - 6;
        }
        if (len > 5 && (this.endsWith(s, len, "\u03b1\u03b3\u03b1\u03c4\u03b5") || this.endsWith(s, len, "\u03b9\u03b5\u03bc\u03b1\u03b9") || this.endsWith(s, len, "\u03b9\u03b5\u03c4\u03b1\u03b9") || this.endsWith(s, len, "\u03b9\u03b5\u03c3\u03b1\u03b9") || this.endsWith(s, len, "\u03b9\u03bf\u03c4\u03b1\u03bd") || this.endsWith(s, len, "\u03b9\u03bf\u03c5\u03bc\u03b1") || this.endsWith(s, len, "\u03b7\u03b8\u03b5\u03b9\u03c3") || this.endsWith(s, len, "\u03b7\u03b8\u03bf\u03c5\u03bd") || this.endsWith(s, len, "\u03b7\u03ba\u03b1\u03c4\u03b5") || this.endsWith(s, len, "\u03b7\u03c3\u03b1\u03c4\u03b5") || this.endsWith(s, len, "\u03b7\u03c3\u03bf\u03c5\u03bd") || this.endsWith(s, len, "\u03bf\u03bc\u03bf\u03c5\u03bd") || this.endsWith(s, len, "\u03bf\u03bd\u03c4\u03b1\u03b9") || this.endsWith(s, len, "\u03bf\u03bd\u03c4\u03b1\u03bd") || this.endsWith(s, len, "\u03bf\u03c3\u03bf\u03c5\u03bd") || this.endsWith(s, len, "\u03bf\u03c5\u03bc\u03b1\u03b9") || this.endsWith(s, len, "\u03bf\u03c5\u03c3\u03b1\u03bd"))) {
            return len - 5;
        }
        if (len > 4 && (this.endsWith(s, len, "\u03b1\u03b3\u03b1\u03bd") || this.endsWith(s, len, "\u03b1\u03bc\u03b1\u03b9") || this.endsWith(s, len, "\u03b1\u03c3\u03b1\u03b9") || this.endsWith(s, len, "\u03b1\u03c4\u03b1\u03b9") || this.endsWith(s, len, "\u03b5\u03b9\u03c4\u03b5") || this.endsWith(s, len, "\u03b5\u03c3\u03b1\u03b9") || this.endsWith(s, len, "\u03b5\u03c4\u03b1\u03b9") || this.endsWith(s, len, "\u03b7\u03b4\u03b5\u03c3") || this.endsWith(s, len, "\u03b7\u03b4\u03c9\u03bd") || this.endsWith(s, len, "\u03b7\u03b8\u03b5\u03b9") || this.endsWith(s, len, "\u03b7\u03ba\u03b1\u03bd") || this.endsWith(s, len, "\u03b7\u03c3\u03b1\u03bd") || this.endsWith(s, len, "\u03b7\u03c3\u03b5\u03b9") || this.endsWith(s, len, "\u03b7\u03c3\u03b5\u03c3") || this.endsWith(s, len, "\u03bf\u03bc\u03b1\u03b9") || this.endsWith(s, len, "\u03bf\u03c4\u03b1\u03bd"))) {
            return len - 4;
        }
        if (len > 3 && (this.endsWith(s, len, "\u03b1\u03b5\u03b9") || this.endsWith(s, len, "\u03b5\u03b9\u03c3") || this.endsWith(s, len, "\u03b7\u03b8\u03c9") || this.endsWith(s, len, "\u03b7\u03c3\u03c9") || this.endsWith(s, len, "\u03bf\u03c5\u03bd") || this.endsWith(s, len, "\u03bf\u03c5\u03c3"))) {
            return len - 3;
        }
        if (len > 2 && (this.endsWith(s, len, "\u03b1\u03bd") || this.endsWith(s, len, "\u03b1\u03c3") || this.endsWith(s, len, "\u03b1\u03c9") || this.endsWith(s, len, "\u03b5\u03b9") || this.endsWith(s, len, "\u03b5\u03c3") || this.endsWith(s, len, "\u03b7\u03c3") || this.endsWith(s, len, "\u03bf\u03b9") || this.endsWith(s, len, "\u03bf\u03c3") || this.endsWith(s, len, "\u03bf\u03c5") || this.endsWith(s, len, "\u03c5\u03c3") || this.endsWith(s, len, "\u03c9\u03bd"))) {
            return len - 2;
        }
        if (len > 1 && this.endsWithVowel(s, len)) {
            return len - 1;
        }
        return len;
    }

    private int rule22(char[] s, int len) {
        if (this.endsWith(s, len, "\u03b5\u03c3\u03c4\u03b5\u03c1") || this.endsWith(s, len, "\u03b5\u03c3\u03c4\u03b1\u03c4")) {
            return len - 5;
        }
        if (this.endsWith(s, len, "\u03bf\u03c4\u03b5\u03c1") || this.endsWith(s, len, "\u03bf\u03c4\u03b1\u03c4") || this.endsWith(s, len, "\u03c5\u03c4\u03b5\u03c1") || this.endsWith(s, len, "\u03c5\u03c4\u03b1\u03c4") || this.endsWith(s, len, "\u03c9\u03c4\u03b5\u03c1") || this.endsWith(s, len, "\u03c9\u03c4\u03b1\u03c4")) {
            return len - 4;
        }
        return len;
    }

    private boolean endsWith(char[] s, int len, String suffix) {
        int suffixLen = suffix.length();
        if (suffixLen > len) {
            return false;
        }
        for (int i = suffixLen - 1; i >= 0; --i) {
            if (s[len - (suffixLen - i)] == suffix.charAt(i)) continue;
            return false;
        }
        return true;
    }

    private boolean endsWithVowel(char[] s, int len) {
        if (len == 0) {
            return false;
        }
        switch (s[len - 1]) {
            case '\u03b1': 
            case '\u03b5': 
            case '\u03b7': 
            case '\u03b9': 
            case '\u03bf': 
            case '\u03c5': 
            case '\u03c9': {
                return true;
            }
        }
        return false;
    }

    private boolean endsWithVowelNoY(char[] s, int len) {
        if (len == 0) {
            return false;
        }
        switch (s[len - 1]) {
            case '\u03b1': 
            case '\u03b5': 
            case '\u03b7': 
            case '\u03b9': 
            case '\u03bf': 
            case '\u03c9': {
                return true;
            }
        }
        return false;
    }
}

