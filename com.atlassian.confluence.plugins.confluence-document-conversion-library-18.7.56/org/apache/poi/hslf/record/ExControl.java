/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import org.apache.poi.hslf.record.ExControlAtom;
import org.apache.poi.hslf.record.ExEmbed;
import org.apache.poi.hslf.record.RecordTypes;

public final class ExControl
extends ExEmbed {
    protected ExControl(byte[] source, int start, int len) {
        super(source, start, len);
    }

    public ExControl() {
        super(new ExControlAtom());
    }

    public ExControlAtom getExControlAtom() {
        return (ExControlAtom)this._children[0];
    }

    @Override
    public long getRecordType() {
        return RecordTypes.ExControl.typeID;
    }
}

