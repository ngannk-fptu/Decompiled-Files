/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.io;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.io.ReaderSource;
import com.ctc.wstx.io.WstxInputData;
import com.ctc.wstx.util.TextBuffer;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import javax.xml.stream.XMLStreamException;

public final class BranchingReaderSource
extends ReaderSource {
    TextBuffer mBranchBuffer = null;
    int mBranchStartOffset = 0;
    boolean mConvertLFs = false;
    boolean mGotCR = false;

    public BranchingReaderSource(ReaderConfig cfg, String pubId, String sysId, URL src, Reader r, boolean realClose) {
        super(cfg, null, null, pubId, sysId, src, r, realClose);
    }

    public int readInto(WstxInputData reader) throws IOException, XMLStreamException {
        if (this.mBranchBuffer != null) {
            if (this.mInputLast > this.mBranchStartOffset) {
                this.appendBranched(this.mBranchStartOffset, this.mInputLast);
            }
            this.mBranchStartOffset = 0;
        }
        return super.readInto(reader);
    }

    public boolean readMore(WstxInputData reader, int minAmount) throws IOException, XMLStreamException {
        int ptr;
        int currAmount;
        if (this.mBranchBuffer != null && (currAmount = this.mInputLast - (ptr = reader.mInputPtr)) > 0) {
            if (ptr > this.mBranchStartOffset) {
                this.appendBranched(this.mBranchStartOffset, ptr);
            }
            this.mBranchStartOffset = 0;
        }
        return super.readMore(reader, minAmount);
    }

    public void startBranch(TextBuffer tb, int startOffset, boolean convertLFs) {
        this.mBranchBuffer = tb;
        this.mBranchStartOffset = startOffset;
        this.mConvertLFs = convertLFs;
        this.mGotCR = false;
    }

    public void endBranch(int endOffset) {
        if (this.mBranchBuffer != null) {
            if (endOffset > this.mBranchStartOffset) {
                this.appendBranched(this.mBranchStartOffset, endOffset);
            }
            this.mBranchBuffer = null;
        }
    }

    private void appendBranched(int startOffset, int pastEnd) {
        if (this.mConvertLFs) {
            char[] inBuf = this.mBuffer;
            char[] outBuf = this.mBranchBuffer.getCurrentSegment();
            int outPtr = this.mBranchBuffer.getCurrentSegmentSize();
            if (this.mGotCR && inBuf[startOffset] == '\n') {
                ++startOffset;
            }
            while (startOffset < pastEnd) {
                int c;
                if ((c = inBuf[startOffset++]) == 13) {
                    if (startOffset < pastEnd) {
                        if (inBuf[startOffset] == '\n') {
                            ++startOffset;
                        }
                    } else {
                        this.mGotCR = true;
                    }
                    c = 10;
                }
                outBuf[outPtr++] = c;
                if (outPtr < outBuf.length) continue;
                outBuf = this.mBranchBuffer.finishCurrentSegment();
                outPtr = 0;
            }
            this.mBranchBuffer.setCurrentLength(outPtr);
        } else {
            this.mBranchBuffer.append(this.mBuffer, startOffset, pastEnd - startOffset);
        }
    }
}

