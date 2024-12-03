/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.sprm;

import org.apache.poi.hwpf.sprm.SprmOperation;
import org.apache.poi.util.Internal;

@Internal
public final class SprmIterator {
    private byte[] _grpprl;
    int _offset;

    public SprmIterator(byte[] grpprl, int offset) {
        this._grpprl = grpprl;
        this._offset = offset;
    }

    public boolean hasNext() {
        return this._offset < this._grpprl.length - 1;
    }

    public SprmOperation next() {
        SprmOperation op = new SprmOperation(this._grpprl, this._offset);
        this._offset += op.size();
        return op;
    }
}

