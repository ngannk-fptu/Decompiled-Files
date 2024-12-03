/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.hslf.record.AnimationInfoAtom;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.hslf.record.RecordContainer;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.util.LittleEndian;

public final class AnimationInfo
extends RecordContainer {
    private byte[] _header;
    private AnimationInfoAtom animationAtom;

    protected AnimationInfo(byte[] source, int start, int len) {
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this._children = Record.findChildRecords(source, start + 8, len - 8);
        this.findInterestingChildren();
    }

    private void findInterestingChildren() {
        Record child = this._children[0];
        if (child instanceof AnimationInfoAtom) {
            this.animationAtom = (AnimationInfoAtom)child;
        } else {
            LOG.atError().log("First child record wasn't a AnimationInfoAtom, was of type {}", (Object)Unbox.box(child.getRecordType()));
        }
    }

    public AnimationInfo() {
        this._header = new byte[8];
        this._header[0] = 15;
        LittleEndian.putShort(this._header, 2, (short)this.getRecordType());
        this._children = new Record[1];
        this.animationAtom = new AnimationInfoAtom();
        this._children[0] = this.animationAtom;
    }

    @Override
    public long getRecordType() {
        return RecordTypes.AnimationInfo.typeID;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        this.writeOut(this._header[0], this._header[1], this.getRecordType(), this._children, out);
    }

    public AnimationInfoAtom getAnimationInfoAtom() {
        return this.animationAtom;
    }
}

