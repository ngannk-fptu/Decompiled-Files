/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import org.apache.poi.hwpf.model.PropertyNode;
import org.apache.poi.hwpf.model.SectionDescriptor;
import org.apache.poi.hwpf.sprm.SectionSprmCompressor;
import org.apache.poi.hwpf.sprm.SectionSprmUncompressor;
import org.apache.poi.hwpf.sprm.SprmBuffer;
import org.apache.poi.hwpf.usermodel.SectionProperties;
import org.apache.poi.util.Internal;

@Internal
public final class SEPX
extends PropertyNode<SEPX> {
    SectionProperties sectionProperties;
    SectionDescriptor _sed;

    public SEPX(SEPX other) {
        super(other);
        this.sectionProperties = other.sectionProperties == null ? null : other.sectionProperties.copy();
        this._sed = other._sed == null ? null : other._sed.copy();
    }

    public SEPX(SectionDescriptor sed, int start, int end, byte[] grpprl) {
        super(start, end, new SprmBuffer(grpprl, 0));
        this._sed = sed;
    }

    public byte[] getGrpprl() {
        if (this.sectionProperties != null) {
            byte[] grpprl = SectionSprmCompressor.compressSectionProperty(this.sectionProperties);
            this._buf = new SprmBuffer(grpprl, 0);
        }
        return ((SprmBuffer)this._buf).toByteArray();
    }

    public SectionDescriptor getSectionDescriptor() {
        return this._sed;
    }

    public SectionProperties getSectionProperties() {
        if (this.sectionProperties == null) {
            this.sectionProperties = SectionSprmUncompressor.uncompressSEP(((SprmBuffer)this._buf).toByteArray(), 0);
        }
        return this.sectionProperties;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SEPX)) {
            return false;
        }
        SEPX sepx = (SEPX)o;
        if (super.equals(o)) {
            return sepx._sed.equals(this._sed);
        }
        return false;
    }

    @Override
    public int hashCode() {
        assert (false) : "hashCode not designed";
        return 42;
    }

    public String toString() {
        return "SEPX from " + this.getStart() + " to " + this.getEnd();
    }

    @Override
    public SEPX copy() {
        return new SEPX(this);
    }
}

