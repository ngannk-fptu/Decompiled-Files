/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.CharsRef
 *  org.apache.lucene.util.fst.CharSequenceOutputs
 *  org.apache.lucene.util.fst.FST
 *  org.apache.lucene.util.fst.FST$Arc
 *  org.apache.lucene.util.fst.FST$BytesReader
 *  org.apache.lucene.util.fst.Outputs
 */
package org.apache.lucene.analysis.charfilter;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import org.apache.lucene.analysis.charfilter.BaseCharFilter;
import org.apache.lucene.analysis.charfilter.NormalizeCharMap;
import org.apache.lucene.analysis.util.RollingCharBuffer;
import org.apache.lucene.util.CharsRef;
import org.apache.lucene.util.fst.CharSequenceOutputs;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.util.fst.Outputs;

public class MappingCharFilter
extends BaseCharFilter {
    private final Outputs<CharsRef> outputs = CharSequenceOutputs.getSingleton();
    private final FST<CharsRef> map;
    private final FST.BytesReader fstReader;
    private final RollingCharBuffer buffer = new RollingCharBuffer();
    private final FST.Arc<CharsRef> scratchArc = new FST.Arc();
    private final Map<Character, FST.Arc<CharsRef>> cachedRootArcs;
    private CharsRef replacement;
    private int replacementPointer;
    private int inputOff;

    public MappingCharFilter(NormalizeCharMap normMap, Reader in) {
        super(in);
        this.buffer.reset(in);
        this.map = normMap.map;
        this.cachedRootArcs = normMap.cachedRootArcs;
        this.fstReader = this.map != null ? this.map.getBytesReader() : null;
    }

    public void reset() throws IOException {
        this.input.reset();
        this.buffer.reset(this.input);
        this.replacement = null;
        this.inputOff = 0;
    }

    public int read() throws IOException {
        while (true) {
            FST.Arc arc;
            if (this.replacement != null && this.replacementPointer < this.replacement.length) {
                return this.replacement.chars[this.replacement.offset + this.replacementPointer++];
            }
            int lastMatchLen = -1;
            CharsRef lastMatch = null;
            int firstCH = this.buffer.get(this.inputOff);
            if (firstCH != -1 && (arc = this.cachedRootArcs.get(Character.valueOf((char)firstCH))) != null) {
                if (!FST.targetHasArcs(arc)) {
                    assert (arc.isFinal());
                    lastMatchLen = 1;
                    lastMatch = (CharsRef)arc.output;
                } else {
                    int lookahead = 0;
                    CharsRef output = (CharsRef)arc.output;
                    while (true) {
                        int ch;
                        ++lookahead;
                        if (arc.isFinal()) {
                            lastMatchLen = lookahead;
                            lastMatch = (CharsRef)this.outputs.add((Object)output, arc.nextFinalOutput);
                        }
                        if (!FST.targetHasArcs(arc) || (ch = this.buffer.get(this.inputOff + lookahead)) == -1 || (arc = this.map.findTargetArc(ch, arc, this.scratchArc, this.fstReader)) == null) break;
                        output = (CharsRef)this.outputs.add((Object)output, arc.output);
                    }
                }
            }
            if (lastMatch == null) break;
            this.inputOff += lastMatchLen;
            int diff = lastMatchLen - lastMatch.length;
            if (diff != 0) {
                int prevCumulativeDiff = this.getLastCumulativeDiff();
                if (diff > 0) {
                    this.addOffCorrectMap(this.inputOff - diff - prevCumulativeDiff, prevCumulativeDiff + diff);
                } else {
                    int outputStart = this.inputOff - prevCumulativeDiff;
                    for (int extraIDX = 0; extraIDX < -diff; ++extraIDX) {
                        this.addOffCorrectMap(outputStart + extraIDX, prevCumulativeDiff - extraIDX - 1);
                    }
                }
            }
            this.replacement = lastMatch;
            this.replacementPointer = 0;
        }
        int ret = this.buffer.get(this.inputOff);
        if (ret != -1) {
            ++this.inputOff;
            this.buffer.freeBefore(this.inputOff);
        }
        return ret;
    }

    public int read(char[] cbuf, int off, int len) throws IOException {
        int c;
        int numRead = 0;
        for (int i = off; i < off + len && (c = this.read()) != -1; ++i) {
            cbuf[i] = (char)c;
            ++numRead;
        }
        return numRead == 0 ? -1 : numRead;
    }
}

