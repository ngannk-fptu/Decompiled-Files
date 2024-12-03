/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.util.Objects;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.hwpf.model.types.FRDAbstractType;
import org.apache.poi.util.Internal;

@Internal
public final class FootnoteReferenceDescriptor
extends FRDAbstractType
implements Duplicatable {
    public FootnoteReferenceDescriptor() {
    }

    public FootnoteReferenceDescriptor(FootnoteReferenceDescriptor other) {
        super(other);
    }

    public FootnoteReferenceDescriptor(byte[] data, int offset) {
        this.fillFields(data, offset);
    }

    @Override
    public FootnoteReferenceDescriptor copy() {
        return new FootnoteReferenceDescriptor(this);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        FootnoteReferenceDescriptor other = (FootnoteReferenceDescriptor)obj;
        return this.field_1_nAuto == other.field_1_nAuto;
    }

    public int hashCode() {
        return Objects.hash(this.field_1_nAuto);
    }

    public boolean isEmpty() {
        return this.field_1_nAuto == 0;
    }

    @Override
    public String toString() {
        if (this.isEmpty()) {
            return "[FRD] EMPTY";
        }
        return super.toString();
    }
}

