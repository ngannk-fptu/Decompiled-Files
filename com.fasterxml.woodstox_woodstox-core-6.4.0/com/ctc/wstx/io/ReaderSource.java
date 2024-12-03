/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.io;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.exc.WstxException;
import com.ctc.wstx.io.BaseInputSource;
import com.ctc.wstx.io.BaseReader;
import com.ctc.wstx.io.SystemId;
import com.ctc.wstx.io.WstxInputData;
import com.ctc.wstx.io.WstxInputSource;
import java.io.IOException;
import java.io.Reader;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

public class ReaderSource
extends BaseInputSource {
    final ReaderConfig mConfig;
    protected Reader mReader;
    final boolean mDoRealClose;
    int mInputProcessed = 0;
    int mInputRow = 1;
    int mInputRowStart = 0;

    public ReaderSource(ReaderConfig cfg, WstxInputSource parent, String fromEntity, String pubId, SystemId sysId, Reader r, boolean realClose) {
        super(parent, fromEntity, pubId, sysId);
        this.mConfig = cfg;
        this.mReader = r;
        this.mDoRealClose = realClose;
        int bufSize = cfg.getInputBufferLength();
        this.mBuffer = cfg.allocFullCBuffer(bufSize);
    }

    public void setInputOffsets(int proc, int row, int rowStart) {
        this.mInputProcessed = proc;
        this.mInputRow = row;
        this.mInputRowStart = rowStart;
    }

    @Override
    protected void doInitInputLocation(WstxInputData reader) {
        reader.mCurrInputProcessed = this.mInputProcessed;
        reader.mCurrInputRow = this.mInputRow;
        reader.mCurrInputRowStart = this.mInputRowStart;
    }

    @Override
    public boolean fromInternalEntity() {
        return false;
    }

    @Override
    public int readInto(WstxInputData reader) throws IOException, XMLStreamException {
        if (this.mBuffer == null) {
            return -1;
        }
        int count = this.mReader.read(this.mBuffer, 0, this.mBuffer.length);
        if (count < 1) {
            this.mInputLast = 0;
            reader.mInputPtr = 0;
            reader.mInputEnd = 0;
            if (count == 0) {
                throw new WstxException("Reader (of type " + this.mReader.getClass().getName() + ") returned 0 characters, even when asked to read up to " + this.mBuffer.length, (Location)((Object)this.getLocation()));
            }
            return -1;
        }
        reader.mInputBuffer = this.mBuffer;
        reader.mInputPtr = 0;
        this.mInputLast = count;
        reader.mInputEnd = count;
        return count;
    }

    @Override
    public boolean readMore(WstxInputData reader, int minAmount) throws IOException, XMLStreamException {
        if (this.mBuffer == null) {
            return false;
        }
        int ptr = reader.mInputPtr;
        int currAmount = this.mInputLast - ptr;
        reader.mCurrInputProcessed += (long)ptr;
        reader.mCurrInputRowStart -= ptr;
        if (currAmount > 0) {
            System.arraycopy(this.mBuffer, ptr, this.mBuffer, 0, currAmount);
            minAmount -= currAmount;
        }
        reader.mInputBuffer = this.mBuffer;
        reader.mInputPtr = 0;
        this.mInputLast = currAmount;
        while (minAmount > 0) {
            int amount = this.mBuffer.length - currAmount;
            int actual = this.mReader.read(this.mBuffer, currAmount, amount);
            if (actual < 1) {
                if (actual == 0) {
                    throw new WstxException("Reader (of type " + this.mReader.getClass().getName() + ") returned 0 characters, even when asked to read up to " + amount, (Location)((Object)this.getLocation()));
                }
                reader.mInputEnd = this.mInputLast = currAmount;
                return false;
            }
            currAmount += actual;
            minAmount -= actual;
        }
        reader.mInputEnd = this.mInputLast = currAmount;
        return true;
    }

    @Override
    public void close() throws IOException {
        if (this.mBuffer != null) {
            this.closeAndRecycle(this.mDoRealClose);
        }
    }

    @Override
    public void closeCompletely() throws IOException {
        if (this.mReader != null) {
            this.closeAndRecycle(true);
        }
    }

    private void closeAndRecycle(boolean fullClose) throws IOException {
        char[] buf = this.mBuffer;
        if (buf != null) {
            this.mBuffer = null;
            this.mConfig.freeFullCBuffer(buf);
        }
        if (this.mReader != null) {
            if (this.mReader instanceof BaseReader) {
                ((BaseReader)this.mReader).freeBuffers();
            }
            if (fullClose) {
                Reader r = this.mReader;
                this.mReader = null;
                r.close();
            }
        }
    }
}

