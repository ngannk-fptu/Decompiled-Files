/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.KeywordAttribute
 *  org.apache.lucene.util.BytesRef
 *  org.apache.lucene.util.BytesRefHash
 *  org.apache.lucene.util.CharsRef
 *  org.apache.lucene.util.IntsRef
 *  org.apache.lucene.util.UnicodeUtil
 *  org.apache.lucene.util.fst.Builder
 *  org.apache.lucene.util.fst.ByteSequenceOutputs
 *  org.apache.lucene.util.fst.FST
 *  org.apache.lucene.util.fst.FST$Arc
 *  org.apache.lucene.util.fst.FST$BytesReader
 *  org.apache.lucene.util.fst.FST$INPUT_TYPE
 *  org.apache.lucene.util.fst.Outputs
 */
package org.apache.lucene.analysis.miscellaneous;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BytesRefHash;
import org.apache.lucene.util.CharsRef;
import org.apache.lucene.util.IntsRef;
import org.apache.lucene.util.UnicodeUtil;
import org.apache.lucene.util.fst.ByteSequenceOutputs;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.util.fst.Outputs;

public final class StemmerOverrideFilter
extends TokenFilter {
    private final StemmerOverrideMap stemmerOverrideMap;
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private final KeywordAttribute keywordAtt = (KeywordAttribute)this.addAttribute(KeywordAttribute.class);
    private final FST.BytesReader fstReader;
    private final FST.Arc<BytesRef> scratchArc = new FST.Arc();
    private final CharsRef spare = new CharsRef();

    public StemmerOverrideFilter(TokenStream input, StemmerOverrideMap stemmerOverrideMap) {
        super(input);
        this.stemmerOverrideMap = stemmerOverrideMap;
        this.fstReader = stemmerOverrideMap.getBytesReader();
    }

    public boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            BytesRef stem;
            if (this.fstReader == null) {
                return true;
            }
            if (!this.keywordAtt.isKeyword() && (stem = this.stemmerOverrideMap.get(this.termAtt.buffer(), this.termAtt.length(), this.scratchArc, this.fstReader)) != null) {
                this.spare.chars = this.termAtt.buffer();
                char[] buffer = this.spare.chars;
                UnicodeUtil.UTF8toUTF16((byte[])stem.bytes, (int)stem.offset, (int)stem.length, (CharsRef)this.spare);
                if (this.spare.chars != buffer) {
                    this.termAtt.copyBuffer(this.spare.chars, this.spare.offset, this.spare.length);
                }
                this.termAtt.setLength(this.spare.length);
                this.keywordAtt.setKeyword(true);
            }
            return true;
        }
        return false;
    }

    public static class Builder {
        private final BytesRefHash hash = new BytesRefHash();
        private final BytesRef spare = new BytesRef();
        private final ArrayList<CharSequence> outputValues = new ArrayList();
        private final boolean ignoreCase;
        private final CharsRef charsSpare = new CharsRef();

        public Builder() {
            this(false);
        }

        public Builder(boolean ignoreCase) {
            this.ignoreCase = ignoreCase;
        }

        public boolean add(CharSequence input, CharSequence output) {
            int length = input.length();
            if (this.ignoreCase) {
                this.charsSpare.grow(length);
                char[] buffer = this.charsSpare.chars;
                for (int i = 0; i < length; i += Character.toChars(Character.toLowerCase(Character.codePointAt(input, i)), buffer, i)) {
                }
                UnicodeUtil.UTF16toUTF8((char[])buffer, (int)0, (int)length, (BytesRef)this.spare);
            } else {
                UnicodeUtil.UTF16toUTF8((CharSequence)input, (int)0, (int)length, (BytesRef)this.spare);
            }
            if (this.hash.add(this.spare) >= 0) {
                this.outputValues.add(output);
                return true;
            }
            return false;
        }

        public StemmerOverrideMap build() throws IOException {
            ByteSequenceOutputs outputs = ByteSequenceOutputs.getSingleton();
            org.apache.lucene.util.fst.Builder builder = new org.apache.lucene.util.fst.Builder(FST.INPUT_TYPE.BYTE4, (Outputs)outputs);
            int[] sort = this.hash.sort(BytesRef.getUTF8SortedAsUnicodeComparator());
            IntsRef intsSpare = new IntsRef();
            int size = this.hash.size();
            for (int i = 0; i < size; ++i) {
                int id = sort[i];
                BytesRef bytesRef = this.hash.get(id, this.spare);
                UnicodeUtil.UTF8toUTF32((BytesRef)bytesRef, (IntsRef)intsSpare);
                builder.add(intsSpare, (Object)new BytesRef(this.outputValues.get(id)));
            }
            return new StemmerOverrideMap((FST<BytesRef>)builder.finish(), this.ignoreCase);
        }
    }

    public static final class StemmerOverrideMap {
        private final FST<BytesRef> fst;
        private final boolean ignoreCase;

        StemmerOverrideMap(FST<BytesRef> fst, boolean ignoreCase) {
            this.fst = fst;
            this.ignoreCase = ignoreCase;
        }

        FST.BytesReader getBytesReader() {
            if (this.fst == null) {
                return null;
            }
            return this.fst.getBytesReader();
        }

        BytesRef get(char[] buffer, int bufferLen, FST.Arc<BytesRef> scratchArc, FST.BytesReader fstReader) throws IOException {
            int codePoint;
            BytesRef pendingOutput = (BytesRef)this.fst.outputs.getNoOutput();
            BytesRef matchOutput = null;
            this.fst.getFirstArc(scratchArc);
            for (int bufUpto = 0; bufUpto < bufferLen; bufUpto += Character.charCount(codePoint)) {
                codePoint = Character.codePointAt(buffer, bufUpto, bufferLen);
                if (this.fst.findTargetArc(this.ignoreCase ? Character.toLowerCase(codePoint) : codePoint, scratchArc, scratchArc, fstReader) == null) {
                    return null;
                }
                pendingOutput = (BytesRef)this.fst.outputs.add((Object)pendingOutput, scratchArc.output);
            }
            if (scratchArc.isFinal()) {
                matchOutput = (BytesRef)this.fst.outputs.add((Object)pendingOutput, scratchArc.nextFinalOutput);
            }
            return matchOutput;
        }
    }
}

