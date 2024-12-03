/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.io;

import com.ctc.wstx.io.WstxInputData;
import com.ctc.wstx.io.WstxInputLocation;
import com.ctc.wstx.io.WstxInputSource;
import java.io.IOException;
import java.net.URL;
import javax.xml.stream.XMLStreamException;

public abstract class BaseInputSource
extends WstxInputSource {
    final String mPublicId;
    final String mSystemId;
    protected URL mSource;
    protected char[] mBuffer;
    protected int mInputLast;
    long mSavedInputProcessed = 0L;
    int mSavedInputRow = 1;
    int mSavedInputRowStart = 0;
    int mSavedInputPtr = 0;
    transient WstxInputLocation mParentLocation = null;

    protected BaseInputSource(WstxInputSource parent, String fromEntity, String publicId, String systemId, URL src) {
        super(parent, fromEntity);
        this.mSystemId = systemId;
        this.mPublicId = publicId;
        this.mSource = src;
    }

    public void overrideSource(URL src) {
        this.mSource = src;
    }

    public abstract boolean fromInternalEntity();

    public URL getSource() {
        return this.mSource;
    }

    public String getPublicId() {
        return this.mPublicId;
    }

    public String getSystemId() {
        return this.mSystemId;
    }

    protected abstract void doInitInputLocation(WstxInputData var1);

    public abstract int readInto(WstxInputData var1) throws IOException, XMLStreamException;

    public abstract boolean readMore(WstxInputData var1, int var2) throws IOException, XMLStreamException;

    public void saveContext(WstxInputData reader) {
        this.mSavedInputPtr = reader.mInputPtr;
        this.mSavedInputProcessed = reader.mCurrInputProcessed;
        this.mSavedInputRow = reader.mCurrInputRow;
        this.mSavedInputRowStart = reader.mCurrInputRowStart;
    }

    public void restoreContext(WstxInputData reader) {
        reader.mInputBuffer = this.mBuffer;
        reader.mInputEnd = this.mInputLast;
        reader.mInputPtr = this.mSavedInputPtr;
        reader.mCurrInputProcessed = this.mSavedInputProcessed;
        reader.mCurrInputRow = this.mSavedInputRow;
        reader.mCurrInputRowStart = this.mSavedInputRowStart;
    }

    public abstract void close() throws IOException;

    protected final WstxInputLocation getLocation() {
        return this.getLocation(this.mSavedInputProcessed + (long)this.mSavedInputPtr - 1L, this.mSavedInputRow, this.mSavedInputPtr - this.mSavedInputRowStart + 1);
    }

    public final WstxInputLocation getLocation(long total, int row, int col) {
        WstxInputLocation pl;
        if (this.mParent == null) {
            pl = null;
        } else {
            pl = this.mParentLocation;
            if (pl == null) {
                this.mParentLocation = pl = this.mParent.getLocation();
            }
            pl = this.mParent.getLocation();
        }
        return new WstxInputLocation(pl, this.getPublicId(), this.getSystemId(), (int)total, row, col);
    }
}

