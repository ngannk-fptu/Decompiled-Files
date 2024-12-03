/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.Analyzer$TokenStreamComponents
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.Tokenizer
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.OffsetAttribute
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.miscellaneous;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

@Deprecated
public final class PatternAnalyzer
extends Analyzer {
    public static final Pattern NON_WORD_PATTERN = Pattern.compile("\\W+");
    public static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");
    private static final CharArraySet EXTENDED_ENGLISH_STOP_WORDS = CharArraySet.unmodifiableSet(new CharArraySet(Version.LUCENE_CURRENT, Arrays.asList("a", "about", "above", "across", "adj", "after", "afterwards", "again", "against", "albeit", "all", "almost", "alone", "along", "already", "also", "although", "always", "among", "amongst", "an", "and", "another", "any", "anyhow", "anyone", "anything", "anywhere", "are", "around", "as", "at", "be", "became", "because", "become", "becomes", "becoming", "been", "before", "beforehand", "behind", "being", "below", "beside", "besides", "between", "beyond", "both", "but", "by", "can", "cannot", "co", "could", "down", "during", "each", "eg", "either", "else", "elsewhere", "enough", "etc", "even", "ever", "every", "everyone", "everything", "everywhere", "except", "few", "first", "for", "former", "formerly", "from", "further", "had", "has", "have", "he", "hence", "her", "here", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "him", "himself", "his", "how", "however", "i", "ie", "if", "in", "inc", "indeed", "into", "is", "it", "its", "itself", "last", "latter", "latterly", "least", "less", "ltd", "many", "may", "me", "meanwhile", "might", "more", "moreover", "most", "mostly", "much", "must", "my", "myself", "namely", "neither", "never", "nevertheless", "next", "no", "nobody", "none", "noone", "nor", "not", "nothing", "now", "nowhere", "of", "off", "often", "on", "once one", "only", "onto", "or", "other", "others", "otherwise", "our", "ours", "ourselves", "out", "over", "own", "per", "perhaps", "rather", "s", "same", "seem", "seemed", "seeming", "seems", "several", "she", "should", "since", "so", "some", "somehow", "someone", "something", "sometime", "sometimes", "somewhere", "still", "such", "t", "than", "that", "the", "their", "them", "themselves", "then", "thence", "there", "thereafter", "thereby", "therefor", "therein", "thereupon", "these", "they", "this", "those", "though", "through", "throughout", "thru", "thus", "to", "together", "too", "toward", "towards", "under", "until", "up", "upon", "us", "very", "via", "was", "we", "well", "were", "what", "whatever", "whatsoever", "when", "whence", "whenever", "whensoever", "where", "whereafter", "whereas", "whereat", "whereby", "wherefrom", "wherein", "whereinto", "whereof", "whereon", "whereto", "whereunto", "whereupon", "wherever", "wherewith", "whether", "which", "whichever", "whichsoever", "while", "whilst", "whither", "who", "whoever", "whole", "whom", "whomever", "whomsoever", "whose", "whosoever", "why", "will", "with", "within", "without", "would", "xsubj", "xcal", "xauthor", "xother ", "xnote", "yet", "you", "your", "yours", "yourself", "yourselves"), true));
    public static final PatternAnalyzer DEFAULT_ANALYZER = new PatternAnalyzer(Version.LUCENE_CURRENT, NON_WORD_PATTERN, true, StopAnalyzer.ENGLISH_STOP_WORDS_SET);
    public static final PatternAnalyzer EXTENDED_ANALYZER = new PatternAnalyzer(Version.LUCENE_CURRENT, NON_WORD_PATTERN, true, EXTENDED_ENGLISH_STOP_WORDS);
    private final Pattern pattern;
    private final boolean toLowerCase;
    private final CharArraySet stopWords;
    private final Version matchVersion;

    public PatternAnalyzer(Version matchVersion, Pattern pattern, boolean toLowerCase, CharArraySet stopWords) {
        if (pattern == null) {
            throw new IllegalArgumentException("pattern must not be null");
        }
        if (PatternAnalyzer.eqPattern(NON_WORD_PATTERN, pattern)) {
            pattern = NON_WORD_PATTERN;
        } else if (PatternAnalyzer.eqPattern(WHITESPACE_PATTERN, pattern)) {
            pattern = WHITESPACE_PATTERN;
        }
        if (stopWords != null && stopWords.size() == 0) {
            stopWords = null;
        }
        this.pattern = pattern;
        this.toLowerCase = toLowerCase;
        this.stopWords = stopWords;
        this.matchVersion = matchVersion;
    }

    public Analyzer.TokenStreamComponents createComponents(String fieldName, Reader reader, String text) {
        if (reader == null) {
            reader = new FastStringReader(text);
        }
        if (this.pattern == NON_WORD_PATTERN) {
            return new Analyzer.TokenStreamComponents((Tokenizer)new FastStringTokenizer(reader, true, this.toLowerCase, this.stopWords));
        }
        if (this.pattern == WHITESPACE_PATTERN) {
            return new Analyzer.TokenStreamComponents((Tokenizer)new FastStringTokenizer(reader, false, this.toLowerCase, this.stopWords));
        }
        PatternTokenizer tokenizer = new PatternTokenizer(reader, this.pattern, this.toLowerCase);
        Object result = this.stopWords != null ? new StopFilter(this.matchVersion, (TokenStream)tokenizer, this.stopWords) : tokenizer;
        return new Analyzer.TokenStreamComponents((Tokenizer)tokenizer, (TokenStream)result);
    }

    public Analyzer.TokenStreamComponents createComponents(String fieldName, Reader reader) {
        return this.createComponents(fieldName, reader, null);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (this == DEFAULT_ANALYZER && other == EXTENDED_ANALYZER) {
            return false;
        }
        if (other == DEFAULT_ANALYZER && this == EXTENDED_ANALYZER) {
            return false;
        }
        if (other instanceof PatternAnalyzer) {
            PatternAnalyzer p2 = (PatternAnalyzer)((Object)other);
            return this.toLowerCase == p2.toLowerCase && PatternAnalyzer.eqPattern(this.pattern, p2.pattern) && PatternAnalyzer.eq(this.stopWords, p2.stopWords);
        }
        return false;
    }

    public int hashCode() {
        if (this == DEFAULT_ANALYZER) {
            return -1218418418;
        }
        if (this == EXTENDED_ANALYZER) {
            return 1303507063;
        }
        int h = 1;
        h = 31 * h + this.pattern.pattern().hashCode();
        h = 31 * h + this.pattern.flags();
        h = 31 * h + (this.toLowerCase ? 1231 : 1237);
        h = 31 * h + (this.stopWords != null ? this.stopWords.hashCode() : 0);
        return h;
    }

    private static boolean eq(Object o1, Object o2) {
        return o1 == o2 || o1 != null && o1.equals(o2);
    }

    private static boolean eqPattern(Pattern p1, Pattern p2) {
        return p1 == p2 || p1.flags() == p2.flags() && p1.pattern().equals(p2.pattern());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static String toString(Reader input) throws IOException {
        if (input instanceof FastStringReader) {
            return ((FastStringReader)input).getString();
        }
        try {
            int n;
            int len = 256;
            char[] buffer = new char[len];
            char[] output = new char[len];
            len = 0;
            while ((n = input.read(buffer)) >= 0) {
                if (len + n > output.length) {
                    char[] tmp = new char[Math.max(output.length << 1, len + n)];
                    System.arraycopy(output, 0, tmp, 0, len);
                    System.arraycopy(buffer, 0, tmp, len, n);
                    buffer = output;
                    output = tmp;
                } else {
                    System.arraycopy(buffer, 0, output, len, n);
                }
                len += n;
            }
            String string = new String(output, 0, len);
            return string;
        }
        finally {
            input.close();
        }
    }

    static final class FastStringReader
    extends StringReader {
        private final String s;

        FastStringReader(String s) {
            super(s);
            this.s = s;
        }

        String getString() {
            return this.s;
        }
    }

    private static final class FastStringTokenizer
    extends Tokenizer {
        private String str;
        private int pos;
        private final boolean isLetter;
        private final boolean toLowerCase;
        private final CharArraySet stopWords;
        private static final Locale locale = Locale.getDefault();
        private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
        private final OffsetAttribute offsetAtt = (OffsetAttribute)this.addAttribute(OffsetAttribute.class);

        public FastStringTokenizer(Reader input, boolean isLetter, boolean toLowerCase, CharArraySet stopWords) {
            super(input);
            this.isLetter = isLetter;
            this.toLowerCase = toLowerCase;
            this.stopWords = stopWords;
        }

        public boolean incrementToken() {
            String text;
            this.clearAttributes();
            String s = this.str;
            int len = s.length();
            int i = this.pos;
            boolean letter = this.isLetter;
            int start = 0;
            do {
                text = null;
                while (i < len && !this.isTokenChar(s.charAt(i), letter)) {
                    ++i;
                }
                if (i >= len) continue;
                start = i;
                while (i < len && this.isTokenChar(s.charAt(i), letter)) {
                    ++i;
                }
                text = s.substring(start, i);
                if (!this.toLowerCase) continue;
                text = text.toLowerCase(locale);
            } while (text != null && this.isStopWord(text));
            this.pos = i;
            if (text == null) {
                return false;
            }
            this.termAtt.setEmpty().append(text);
            this.offsetAtt.setOffset(this.correctOffset(start), this.correctOffset(i));
            return true;
        }

        public final void end() {
            int finalOffset = this.str.length();
            this.offsetAtt.setOffset(this.correctOffset(finalOffset), this.correctOffset(finalOffset));
        }

        private boolean isTokenChar(char c, boolean isLetter) {
            return isLetter ? Character.isLetter(c) : !Character.isWhitespace(c);
        }

        private boolean isStopWord(String text) {
            return this.stopWords != null && this.stopWords.contains(text);
        }

        public void reset() throws IOException {
            super.reset();
            this.str = PatternAnalyzer.toString(this.input);
            this.pos = 0;
        }
    }

    private static final class PatternTokenizer
    extends Tokenizer {
        private final Pattern pattern;
        private String str;
        private final boolean toLowerCase;
        private Matcher matcher;
        private int pos = 0;
        private static final Locale locale = Locale.getDefault();
        private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
        private final OffsetAttribute offsetAtt = (OffsetAttribute)this.addAttribute(OffsetAttribute.class);

        public PatternTokenizer(Reader input, Pattern pattern, boolean toLowerCase) {
            super(input);
            this.pattern = pattern;
            this.matcher = pattern.matcher("");
            this.toLowerCase = toLowerCase;
        }

        public final boolean incrementToken() {
            boolean isMatch;
            if (this.matcher == null) {
                return false;
            }
            this.clearAttributes();
            do {
                int end;
                int start = this.pos;
                isMatch = this.matcher.find();
                if (isMatch) {
                    end = this.matcher.start();
                    this.pos = this.matcher.end();
                } else {
                    end = this.str.length();
                    this.matcher = null;
                }
                if (start == end) continue;
                String text = this.str.substring(start, end);
                if (this.toLowerCase) {
                    text = text.toLowerCase(locale);
                }
                this.termAtt.setEmpty().append(text);
                this.offsetAtt.setOffset(this.correctOffset(start), this.correctOffset(end));
                return true;
            } while (isMatch);
            return false;
        }

        public final void end() {
            int finalOffset = this.correctOffset(this.str.length());
            this.offsetAtt.setOffset(finalOffset, finalOffset);
        }

        public void reset() throws IOException {
            super.reset();
            this.str = PatternAnalyzer.toString(this.input);
            this.matcher = this.pattern.matcher(this.str);
            this.pos = 0;
        }
    }
}

