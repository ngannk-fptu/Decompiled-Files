/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 */
package org.apache.lucene.analysis.ja;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ja.tokenattributes.ReadingAttribute;
import org.apache.lucene.analysis.ja.util.ToStringUtil;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public final class JapaneseReadingFormFilter
extends TokenFilter {
    private final CharTermAttribute termAttr = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private final ReadingAttribute readingAttr = (ReadingAttribute)this.addAttribute(ReadingAttribute.class);
    private StringBuilder buffer = new StringBuilder();
    private boolean useRomaji;

    public JapaneseReadingFormFilter(TokenStream input, boolean useRomaji) {
        super(input);
        this.useRomaji = useRomaji;
    }

    public JapaneseReadingFormFilter(TokenStream input) {
        this(input, false);
    }

    public boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            String reading = this.readingAttr.getReading();
            if (this.useRomaji) {
                if (reading == null) {
                    this.buffer.setLength(0);
                    ToStringUtil.getRomanization(this.buffer, (CharSequence)this.termAttr);
                    this.termAttr.setEmpty().append(this.buffer);
                } else {
                    ToStringUtil.getRomanization((Appendable)this.termAttr.setEmpty(), reading);
                }
            } else if (reading != null) {
                this.termAttr.setEmpty().append(reading);
            }
            return true;
        }
        return false;
    }
}

