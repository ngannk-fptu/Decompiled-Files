/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.pattern;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.lucene.analysis.charfilter.BaseCharFilter;

public class PatternReplaceCharFilter
extends BaseCharFilter {
    @Deprecated
    public static final int DEFAULT_MAX_BLOCK_CHARS = 10000;
    private final Pattern pattern;
    private final String replacement;
    private Reader transformedInput;

    public PatternReplaceCharFilter(Pattern pattern, String replacement, Reader in) {
        super(in);
        this.pattern = pattern;
        this.replacement = replacement;
    }

    @Deprecated
    public PatternReplaceCharFilter(Pattern pattern, String replacement, int maxBlockChars, String blockDelimiter, Reader in) {
        this(pattern, replacement, in);
    }

    public int read(char[] cbuf, int off, int len) throws IOException {
        if (this.transformedInput == null) {
            this.fill();
        }
        return this.transformedInput.read(cbuf, off, len);
    }

    private void fill() throws IOException {
        StringBuilder buffered = new StringBuilder();
        char[] temp = new char[1024];
        int cnt = this.input.read(temp);
        while (cnt > 0) {
            buffered.append(temp, 0, cnt);
            cnt = this.input.read(temp);
        }
        this.transformedInput = new StringReader(this.processPattern(buffered).toString());
    }

    public int read() throws IOException {
        if (this.transformedInput == null) {
            this.fill();
        }
        return this.transformedInput.read();
    }

    @Override
    protected int correct(int currentOff) {
        return Math.max(0, super.correct(currentOff));
    }

    CharSequence processPattern(CharSequence input) {
        Matcher m = this.pattern.matcher(input);
        StringBuffer cumulativeOutput = new StringBuffer();
        int cumulative = 0;
        int lastMatchEnd = 0;
        while (m.find()) {
            int groupSize = m.end() - m.start();
            int skippedSize = m.start() - lastMatchEnd;
            lastMatchEnd = m.end();
            int lengthBeforeReplacement = cumulativeOutput.length() + skippedSize;
            m.appendReplacement(cumulativeOutput, this.replacement);
            int replacementSize = cumulativeOutput.length() - lengthBeforeReplacement;
            if (groupSize == replacementSize) continue;
            if (replacementSize < groupSize) {
                int atIndex = lengthBeforeReplacement + replacementSize;
                this.addOffCorrectMap(atIndex, cumulative += groupSize - replacementSize);
                continue;
            }
            for (int i = groupSize; i < replacementSize; ++i) {
                this.addOffCorrectMap(lengthBeforeReplacement + i, --cumulative);
            }
        }
        m.appendTail(cumulativeOutput);
        return cumulativeOutput;
    }
}

