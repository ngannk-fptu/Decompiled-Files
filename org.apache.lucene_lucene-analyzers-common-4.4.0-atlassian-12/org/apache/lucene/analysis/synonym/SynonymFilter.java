/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.OffsetAttribute
 *  org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute
 *  org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute
 *  org.apache.lucene.analysis.tokenattributes.TypeAttribute
 *  org.apache.lucene.store.ByteArrayDataInput
 *  org.apache.lucene.util.ArrayUtil
 *  org.apache.lucene.util.AttributeSource$State
 *  org.apache.lucene.util.BytesRef
 *  org.apache.lucene.util.CharsRef
 *  org.apache.lucene.util.RamUsageEstimator
 *  org.apache.lucene.util.UnicodeUtil
 *  org.apache.lucene.util.fst.FST
 *  org.apache.lucene.util.fst.FST$Arc
 *  org.apache.lucene.util.fst.FST$BytesReader
 */
package org.apache.lucene.analysis.synonym;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.store.ByteArrayDataInput;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.CharsRef;
import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.util.UnicodeUtil;
import org.apache.lucene.util.fst.FST;

public final class SynonymFilter
extends TokenFilter {
    public static final String TYPE_SYNONYM = "SYNONYM";
    private final SynonymMap synonyms;
    private final boolean ignoreCase;
    private final int rollBufferSize;
    private int captureCount;
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private final PositionIncrementAttribute posIncrAtt = (PositionIncrementAttribute)this.addAttribute(PositionIncrementAttribute.class);
    private final PositionLengthAttribute posLenAtt = (PositionLengthAttribute)this.addAttribute(PositionLengthAttribute.class);
    private final TypeAttribute typeAtt = (TypeAttribute)this.addAttribute(TypeAttribute.class);
    private final OffsetAttribute offsetAtt = (OffsetAttribute)this.addAttribute(OffsetAttribute.class);
    private int inputSkipCount;
    private final PendingInput[] futureInputs;
    private final ByteArrayDataInput bytesReader = new ByteArrayDataInput();
    private final PendingOutputs[] futureOutputs;
    private int nextWrite;
    private int nextRead;
    private boolean finished;
    private final FST.Arc<BytesRef> scratchArc;
    private final FST<BytesRef> fst;
    private final FST.BytesReader fstReader;
    private final BytesRef scratchBytes = new BytesRef();
    private final CharsRef scratchChars = new CharsRef();
    private int lastStartOffset;
    private int lastEndOffset;

    public SynonymFilter(TokenStream input, SynonymMap synonyms, boolean ignoreCase) {
        super(input);
        this.synonyms = synonyms;
        this.ignoreCase = ignoreCase;
        this.fst = synonyms.fst;
        this.fstReader = this.fst.getBytesReader();
        if (this.fst == null) {
            throw new IllegalArgumentException("fst must be non-null");
        }
        this.rollBufferSize = 1 + synonyms.maxHorizontalContext;
        this.futureInputs = new PendingInput[this.rollBufferSize];
        this.futureOutputs = new PendingOutputs[this.rollBufferSize];
        for (int pos = 0; pos < this.rollBufferSize; ++pos) {
            this.futureInputs[pos] = new PendingInput();
            this.futureOutputs[pos] = new PendingOutputs();
        }
        this.scratchArc = new FST.Arc();
    }

    private void capture() {
        ++this.captureCount;
        PendingInput input = this.futureInputs[this.nextWrite];
        input.state = this.captureState();
        input.consumed = false;
        input.term.copyChars(this.termAtt.buffer(), 0, this.termAtt.length());
        this.nextWrite = this.rollIncr(this.nextWrite);
        assert (this.nextWrite != this.nextRead);
    }

    /*
     * Enabled aggressive block sorting
     */
    private void parse() throws IOException {
        assert (this.inputSkipCount == 0);
        int curNextRead = this.nextRead;
        BytesRef matchOutput = null;
        int matchInputLength = 0;
        int matchEndOffset = -1;
        BytesRef pendingOutput = (BytesRef)this.fst.outputs.getNoOutput();
        this.fst.getFirstArc(this.scratchArc);
        assert (this.scratchArc.output == this.fst.outputs.getNoOutput());
        int tokenCount = 0;
        block0: while (true) {
            int codePoint;
            int bufferLen;
            char[] buffer;
            int inputEndOffset;
            block14: {
                block15: {
                    inputEndOffset = 0;
                    if (curNextRead != this.nextWrite) break block15;
                    if (this.finished) break;
                    assert (this.futureInputs[this.nextWrite].consumed);
                    if (this.input.incrementToken()) {
                        buffer = this.termAtt.buffer();
                        bufferLen = this.termAtt.length();
                        PendingInput input = this.futureInputs[this.nextWrite];
                        this.lastStartOffset = input.startOffset = this.offsetAtt.startOffset();
                        this.lastEndOffset = input.endOffset = this.offsetAtt.endOffset();
                        inputEndOffset = input.endOffset;
                        if (this.nextRead != this.nextWrite) {
                            this.capture();
                            break block14;
                        } else {
                            input.consumed = false;
                        }
                        break block14;
                    } else {
                        this.finished = true;
                        break;
                    }
                }
                buffer = this.futureInputs[curNextRead].term.chars;
                bufferLen = this.futureInputs[curNextRead].term.length;
                inputEndOffset = this.futureInputs[curNextRead].endOffset;
            }
            ++tokenCount;
            for (int bufUpto = 0; bufUpto < bufferLen; bufUpto += Character.charCount(codePoint)) {
                codePoint = Character.codePointAt(buffer, bufUpto, bufferLen);
                if (this.fst.findTargetArc(this.ignoreCase ? Character.toLowerCase(codePoint) : codePoint, this.scratchArc, this.scratchArc, this.fstReader) == null) break block0;
                pendingOutput = (BytesRef)this.fst.outputs.add((Object)pendingOutput, this.scratchArc.output);
            }
            if (this.scratchArc.isFinal()) {
                matchOutput = (BytesRef)this.fst.outputs.add((Object)pendingOutput, this.scratchArc.nextFinalOutput);
                matchInputLength = tokenCount;
                matchEndOffset = inputEndOffset;
            }
            if (this.fst.findTargetArc(0, this.scratchArc, this.scratchArc, this.fstReader) == null) break;
            pendingOutput = (BytesRef)this.fst.outputs.add((Object)pendingOutput, this.scratchArc.output);
            if (this.nextRead == this.nextWrite) {
                this.capture();
            }
            curNextRead = this.rollIncr(curNextRead);
        }
        if (this.nextRead == this.nextWrite && !this.finished) {
            this.nextWrite = this.rollIncr(this.nextWrite);
        }
        if (matchOutput != null) {
            this.inputSkipCount = matchInputLength;
            this.addOutput(matchOutput, matchInputLength, matchEndOffset);
            return;
        }
        if (this.nextRead != this.nextWrite) {
            this.inputSkipCount = 1;
            return;
        }
        if ($assertionsDisabled) return;
        if (this.finished) return;
        throw new AssertionError();
    }

    private void addOutput(BytesRef bytes, int matchInputLength, int matchEndOffset) {
        this.bytesReader.reset(bytes.bytes, bytes.offset, bytes.length);
        int code = this.bytesReader.readVInt();
        boolean keepOrig = (code & 1) == 0;
        int count = code >>> 1;
        for (int outputIDX = 0; outputIDX < count; ++outputIDX) {
            this.synonyms.words.get(this.bytesReader.readVInt(), this.scratchBytes);
            UnicodeUtil.UTF8toUTF16((BytesRef)this.scratchBytes, (CharsRef)this.scratchChars);
            int lastStart = this.scratchChars.offset;
            int chEnd = lastStart + this.scratchChars.length;
            int outputUpto = this.nextRead;
            for (int chIDX = lastStart; chIDX <= chEnd; ++chIDX) {
                int posLen;
                int endOffset;
                if (chIDX != chEnd && this.scratchChars.chars[chIDX] != '\u0000') continue;
                int outputLen = chIDX - lastStart;
                assert (outputLen > 0) : "output contains empty string: " + this.scratchChars;
                if (chIDX == chEnd && lastStart == this.scratchChars.offset) {
                    endOffset = matchEndOffset;
                    posLen = keepOrig ? matchInputLength : 1;
                } else {
                    endOffset = -1;
                    posLen = 1;
                }
                this.futureOutputs[outputUpto].add(this.scratchChars.chars, lastStart, outputLen, endOffset, posLen);
                lastStart = 1 + chIDX;
                outputUpto = this.rollIncr(outputUpto);
                assert (this.futureOutputs[outputUpto].posIncr == 1) : "outputUpto=" + outputUpto + " vs nextWrite=" + this.nextWrite;
            }
        }
        int upto = this.nextRead;
        for (int idx = 0; idx < matchInputLength; ++idx) {
            this.futureInputs[upto].keepOrig |= keepOrig;
            this.futureInputs[upto].matched = true;
            upto = this.rollIncr(upto);
        }
    }

    private int rollIncr(int count) {
        if (++count == this.rollBufferSize) {
            return 0;
        }
        return count;
    }

    int getCaptureCount() {
        return this.captureCount;
    }

    public boolean incrementToken() throws IOException {
        while (true) {
            if (this.inputSkipCount != 0) {
                PendingInput input = this.futureInputs[this.nextRead];
                PendingOutputs outputs = this.futureOutputs[this.nextRead];
                if (!(input.consumed || !input.keepOrig && input.matched)) {
                    if (input.state != null) {
                        this.restoreState(input.state);
                    } else assert (this.inputSkipCount == 1) : "inputSkipCount=" + this.inputSkipCount + " nextRead=" + this.nextRead;
                    input.reset();
                    if (outputs.count > 0) {
                        outputs.posIncr = 0;
                    } else {
                        this.nextRead = this.rollIncr(this.nextRead);
                        --this.inputSkipCount;
                    }
                    return true;
                }
                if (outputs.upto < outputs.count) {
                    input.reset();
                    int posIncr = outputs.posIncr;
                    CharsRef output = outputs.pullNext();
                    this.clearAttributes();
                    this.termAtt.copyBuffer(output.chars, output.offset, output.length);
                    this.typeAtt.setType(TYPE_SYNONYM);
                    int endOffset = outputs.getLastEndOffset();
                    if (endOffset == -1) {
                        endOffset = input.endOffset;
                    }
                    this.offsetAtt.setOffset(input.startOffset, endOffset);
                    this.posIncrAtt.setPositionIncrement(posIncr);
                    this.posLenAtt.setPositionLength(outputs.getLastPosLength());
                    if (outputs.count == 0) {
                        this.nextRead = this.rollIncr(this.nextRead);
                        --this.inputSkipCount;
                    }
                    return true;
                }
                input.reset();
                this.nextRead = this.rollIncr(this.nextRead);
                --this.inputSkipCount;
                continue;
            }
            if (this.finished && this.nextRead == this.nextWrite) {
                PendingOutputs outputs = this.futureOutputs[this.nextRead];
                if (outputs.upto < outputs.count) {
                    int posIncr = outputs.posIncr;
                    CharsRef output = outputs.pullNext();
                    this.futureInputs[this.nextRead].reset();
                    if (outputs.count == 0) {
                        this.nextWrite = this.nextRead = this.rollIncr(this.nextRead);
                    }
                    this.clearAttributes();
                    this.offsetAtt.setOffset(this.lastStartOffset, this.lastEndOffset);
                    this.termAtt.copyBuffer(output.chars, output.offset, output.length);
                    this.typeAtt.setType(TYPE_SYNONYM);
                    this.posIncrAtt.setPositionIncrement(posIncr);
                    return true;
                }
                return false;
            }
            this.parse();
        }
    }

    public void reset() throws IOException {
        super.reset();
        this.captureCount = 0;
        this.finished = false;
        this.inputSkipCount = 0;
        this.nextWrite = 0;
        this.nextRead = 0;
        for (PendingInput input : this.futureInputs) {
            input.reset();
        }
        for (PendingOutputs output : this.futureOutputs) {
            output.reset();
        }
    }

    private static class PendingOutputs {
        CharsRef[] outputs = new CharsRef[1];
        int[] endOffsets = new int[1];
        int[] posLengths = new int[1];
        int upto;
        int count;
        int posIncr = 1;
        int lastEndOffset;
        int lastPosLength;

        public void reset() {
            this.count = 0;
            this.upto = 0;
            this.posIncr = 1;
        }

        public CharsRef pullNext() {
            assert (this.upto < this.count);
            this.lastEndOffset = this.endOffsets[this.upto];
            this.lastPosLength = this.posLengths[this.upto];
            CharsRef result = this.outputs[this.upto++];
            this.posIncr = 0;
            if (this.upto == this.count) {
                this.reset();
            }
            return result;
        }

        public int getLastEndOffset() {
            return this.lastEndOffset;
        }

        public int getLastPosLength() {
            return this.lastPosLength;
        }

        public void add(char[] output, int offset, int len, int endOffset, int posLength) {
            Object[] next;
            if (this.count == this.outputs.length) {
                next = new CharsRef[ArrayUtil.oversize((int)(1 + this.count), (int)RamUsageEstimator.NUM_BYTES_OBJECT_REF)];
                System.arraycopy(this.outputs, 0, next, 0, this.count);
                this.outputs = next;
            }
            if (this.count == this.endOffsets.length) {
                next = new int[ArrayUtil.oversize((int)(1 + this.count), (int)4)];
                System.arraycopy(this.endOffsets, 0, next, 0, this.count);
                this.endOffsets = (int[])next;
            }
            if (this.count == this.posLengths.length) {
                next = new int[ArrayUtil.oversize((int)(1 + this.count), (int)4)];
                System.arraycopy(this.posLengths, 0, next, 0, this.count);
                this.posLengths = (int[])next;
            }
            if (this.outputs[this.count] == null) {
                this.outputs[this.count] = new CharsRef();
            }
            this.outputs[this.count].copyChars(output, offset, len);
            this.endOffsets[this.count] = endOffset;
            this.posLengths[this.count] = posLength;
            ++this.count;
        }
    }

    private static class PendingInput {
        final CharsRef term = new CharsRef();
        AttributeSource.State state;
        boolean keepOrig;
        boolean matched;
        boolean consumed = true;
        int startOffset;
        int endOffset;

        private PendingInput() {
        }

        public void reset() {
            this.state = null;
            this.consumed = true;
            this.keepOrig = false;
            this.matched = false;
        }
    }
}

