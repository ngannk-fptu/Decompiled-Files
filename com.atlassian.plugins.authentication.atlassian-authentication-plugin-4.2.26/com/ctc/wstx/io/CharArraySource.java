/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.io;

import com.ctc.wstx.io.BaseInputSource;
import com.ctc.wstx.io.SystemId;
import com.ctc.wstx.io.WstxInputData;
import com.ctc.wstx.io.WstxInputSource;
import javax.xml.stream.Location;

public final class CharArraySource
extends BaseInputSource {
    int mOffset;
    final Location mContentStart;

    protected CharArraySource(WstxInputSource parent, String fromEntity, char[] chars, int offset, int len, Location loc, SystemId sysId) {
        super(parent, fromEntity, loc.getPublicId(), sysId);
        this.mBuffer = chars;
        this.mOffset = offset;
        this.mInputLast = offset + len;
        this.mContentStart = loc;
    }

    @Override
    public boolean fromInternalEntity() {
        return true;
    }

    @Override
    protected void doInitInputLocation(WstxInputData reader) {
        reader.mCurrInputProcessed = this.mContentStart.getCharacterOffset();
        reader.mCurrInputRow = this.mContentStart.getLineNumber();
        reader.mCurrInputRowStart = -this.mContentStart.getColumnNumber() + 1;
    }

    @Override
    public int readInto(WstxInputData reader) {
        if (this.mBuffer == null) {
            return -1;
        }
        int len = this.mInputLast - this.mOffset;
        if (len < 1) {
            return -1;
        }
        reader.mInputBuffer = this.mBuffer;
        reader.mInputPtr = this.mOffset;
        reader.mInputEnd = this.mInputLast;
        this.mOffset = this.mInputLast;
        return len;
    }

    @Override
    public boolean readMore(WstxInputData reader, int minAmount) {
        int len;
        if (reader.mInputPtr >= reader.mInputEnd && (len = this.mInputLast - this.mOffset) >= minAmount) {
            return this.readInto(reader) > 0;
        }
        return false;
    }

    @Override
    public void close() {
        this.mBuffer = null;
    }

    @Override
    public void closeCompletely() {
        this.close();
    }
}

