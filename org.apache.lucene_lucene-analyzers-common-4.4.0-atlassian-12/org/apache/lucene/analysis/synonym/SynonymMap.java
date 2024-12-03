/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute
 *  org.apache.lucene.store.ByteArrayDataOutput
 *  org.apache.lucene.util.BytesRef
 *  org.apache.lucene.util.BytesRefHash
 *  org.apache.lucene.util.CharsRef
 *  org.apache.lucene.util.IntsRef
 *  org.apache.lucene.util.UnicodeUtil
 *  org.apache.lucene.util.fst.Builder
 *  org.apache.lucene.util.fst.ByteSequenceOutputs
 *  org.apache.lucene.util.fst.FST
 *  org.apache.lucene.util.fst.FST$INPUT_TYPE
 *  org.apache.lucene.util.fst.Outputs
 *  org.apache.lucene.util.fst.Util
 */
package org.apache.lucene.analysis.synonym;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.store.ByteArrayDataOutput;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BytesRefHash;
import org.apache.lucene.util.CharsRef;
import org.apache.lucene.util.IntsRef;
import org.apache.lucene.util.UnicodeUtil;
import org.apache.lucene.util.fst.ByteSequenceOutputs;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.util.fst.Outputs;
import org.apache.lucene.util.fst.Util;

public class SynonymMap {
    public static final char WORD_SEPARATOR = '\u0000';
    public final FST<BytesRef> fst;
    public final BytesRefHash words;
    public final int maxHorizontalContext;

    public SynonymMap(FST<BytesRef> fst, BytesRefHash words, int maxHorizontalContext) {
        this.fst = fst;
        this.words = words;
        this.maxHorizontalContext = maxHorizontalContext;
    }

    public static class Builder {
        private final HashMap<CharsRef, MapEntry> workingSet = new HashMap();
        private final BytesRefHash words = new BytesRefHash();
        private final BytesRef utf8Scratch = new BytesRef(8);
        private int maxHorizontalContext;
        private final boolean dedup;

        public Builder(boolean dedup) {
            this.dedup = dedup;
        }

        public static CharsRef join(String[] words, CharsRef reuse) {
            int upto = 0;
            char[] buffer = reuse.chars;
            for (String word : words) {
                int needed;
                int wordLen = word.length();
                int n = needed = 0 == upto ? wordLen : 1 + upto + wordLen;
                if (needed > buffer.length) {
                    reuse.grow(needed);
                    buffer = reuse.chars;
                }
                if (upto > 0) {
                    buffer[upto++] = '\u0000';
                }
                word.getChars(0, wordLen, buffer, upto);
                upto += wordLen;
            }
            reuse.length = upto;
            return reuse;
        }

        public static CharsRef analyze(Analyzer analyzer, String text, CharsRef reuse) throws IOException {
            TokenStream ts = analyzer.tokenStream("", text);
            CharTermAttribute termAtt = (CharTermAttribute)ts.addAttribute(CharTermAttribute.class);
            PositionIncrementAttribute posIncAtt = (PositionIncrementAttribute)ts.addAttribute(PositionIncrementAttribute.class);
            ts.reset();
            reuse.length = 0;
            while (ts.incrementToken()) {
                int length = termAtt.length();
                if (length == 0) {
                    throw new IllegalArgumentException("term: " + text + " analyzed to a zero-length token");
                }
                if (posIncAtt.getPositionIncrement() != 1) {
                    throw new IllegalArgumentException("term: " + text + " analyzed to a token with posinc != 1");
                }
                reuse.grow(reuse.length + length + 1);
                int end = reuse.offset + reuse.length;
                if (reuse.length > 0) {
                    reuse.chars[end++] = '\u0000';
                    ++reuse.length;
                }
                System.arraycopy(termAtt.buffer(), 0, reuse.chars, end, length);
                reuse.length += length;
            }
            ts.end();
            ts.close();
            if (reuse.length == 0) {
                throw new IllegalArgumentException("term: " + text + " was completely eliminated by analyzer");
            }
            return reuse;
        }

        private boolean hasHoles(CharsRef chars) {
            int end = chars.offset + chars.length;
            for (int idx = chars.offset + 1; idx < end; ++idx) {
                if (chars.chars[idx] != '\u0000' || chars.chars[idx - 1] != '\u0000') continue;
                return true;
            }
            if (chars.chars[chars.offset] == '\u0000') {
                return true;
            }
            return chars.chars[chars.offset + chars.length - 1] == '\u0000';
        }

        private void add(CharsRef input, int numInputWords, CharsRef output, int numOutputWords, boolean includeOrig) {
            MapEntry e;
            if (numInputWords <= 0) {
                throw new IllegalArgumentException("numInputWords must be > 0 (got " + numInputWords + ")");
            }
            if (input.length <= 0) {
                throw new IllegalArgumentException("input.length must be > 0 (got " + input.length + ")");
            }
            if (numOutputWords <= 0) {
                throw new IllegalArgumentException("numOutputWords must be > 0 (got " + numOutputWords + ")");
            }
            if (output.length <= 0) {
                throw new IllegalArgumentException("output.length must be > 0 (got " + output.length + ")");
            }
            assert (!this.hasHoles(input)) : "input has holes: " + input;
            assert (!this.hasHoles(output)) : "output has holes: " + output;
            int hashCode = UnicodeUtil.UTF16toUTF8WithHash((char[])output.chars, (int)output.offset, (int)output.length, (BytesRef)this.utf8Scratch);
            int ord = this.words.add(this.utf8Scratch, hashCode);
            if (ord < 0) {
                ord = -ord - 1;
            }
            if ((e = this.workingSet.get(input)) == null) {
                e = new MapEntry();
                this.workingSet.put(CharsRef.deepCopyOf((CharsRef)input), e);
            }
            e.ords.add(ord);
            e.includeOrig |= includeOrig;
            this.maxHorizontalContext = Math.max(this.maxHorizontalContext, numInputWords);
            this.maxHorizontalContext = Math.max(this.maxHorizontalContext, numOutputWords);
        }

        private int countWords(CharsRef chars) {
            int wordCount = 1;
            int upto = chars.offset;
            int limit = chars.offset + chars.length;
            while (upto < limit) {
                if (chars.chars[upto++] != '\u0000') continue;
                ++wordCount;
            }
            return wordCount;
        }

        public void add(CharsRef input, CharsRef output, boolean includeOrig) {
            this.add(input, this.countWords(input), output, this.countWords(output), includeOrig);
        }

        public SynonymMap build() throws IOException {
            ByteSequenceOutputs outputs = ByteSequenceOutputs.getSingleton();
            org.apache.lucene.util.fst.Builder builder = new org.apache.lucene.util.fst.Builder(FST.INPUT_TYPE.BYTE4, (Outputs)outputs);
            BytesRef scratch = new BytesRef(64);
            ByteArrayDataOutput scratchOutput = new ByteArrayDataOutput();
            HashSet<Integer> dedupSet = this.dedup ? new HashSet<Integer>() : null;
            byte[] spare = new byte[5];
            Set<CharsRef> keys = this.workingSet.keySet();
            CharsRef[] sortedKeys = keys.toArray(new CharsRef[keys.size()]);
            Arrays.sort(sortedKeys, CharsRef.getUTF16SortedAsUTF8Comparator());
            IntsRef scratchIntsRef = new IntsRef();
            for (int keyIdx = 0; keyIdx < sortedKeys.length; ++keyIdx) {
                CharsRef input = sortedKeys[keyIdx];
                MapEntry output = this.workingSet.get(input);
                int numEntries = output.ords.size();
                int estimatedSize = 5 + numEntries * 5;
                scratch.grow(estimatedSize);
                scratchOutput.reset(scratch.bytes, scratch.offset, scratch.bytes.length);
                assert (scratch.offset == 0);
                int count = 0;
                for (int i = 0; i < numEntries; ++i) {
                    if (dedupSet != null) {
                        Integer ent = output.ords.get(i);
                        if (dedupSet.contains(ent)) continue;
                        dedupSet.add(ent);
                    }
                    scratchOutput.writeVInt(output.ords.get(i).intValue());
                    ++count;
                }
                int pos = scratchOutput.getPosition();
                scratchOutput.writeVInt(count << 1 | (output.includeOrig ? 0 : 1));
                int pos2 = scratchOutput.getPosition();
                int vIntLen = pos2 - pos;
                System.arraycopy(scratch.bytes, pos, spare, 0, vIntLen);
                System.arraycopy(scratch.bytes, 0, scratch.bytes, vIntLen, pos);
                System.arraycopy(spare, 0, scratch.bytes, 0, vIntLen);
                if (dedupSet != null) {
                    dedupSet.clear();
                }
                scratch.length = scratchOutput.getPosition() - scratch.offset;
                builder.add(Util.toUTF32((CharSequence)input, (IntsRef)scratchIntsRef), (Object)BytesRef.deepCopyOf((BytesRef)scratch));
            }
            FST fst = builder.finish();
            return new SynonymMap((FST<BytesRef>)fst, this.words, this.maxHorizontalContext);
        }

        private static class MapEntry {
            boolean includeOrig;
            ArrayList<Integer> ords = new ArrayList();

            private MapEntry() {
            }
        }
    }
}

