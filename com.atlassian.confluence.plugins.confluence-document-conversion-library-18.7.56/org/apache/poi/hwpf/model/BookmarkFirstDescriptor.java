/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.util.Objects;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.hwpf.model.types.BKFAbstractType;
import org.apache.poi.util.Internal;

@Internal
public final class BookmarkFirstDescriptor
extends BKFAbstractType
implements Duplicatable {
    public BookmarkFirstDescriptor() {
    }

    public BookmarkFirstDescriptor(BookmarkFirstDescriptor other) {
        super(other);
    }

    public BookmarkFirstDescriptor(byte[] data, int offset) {
        this.fillFields(data, offset);
    }

    @Override
    public BookmarkFirstDescriptor copy() {
        return new BookmarkFirstDescriptor(this);
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
        BookmarkFirstDescriptor other = (BookmarkFirstDescriptor)obj;
        if (this.field_1_ibkl != other.field_1_ibkl) {
            return false;
        }
        return this.field_2_bkf_flags == other.field_2_bkf_flags;
    }

    public int hashCode() {
        return Objects.hash(this.field_1_ibkl, this.field_2_bkf_flags);
    }

    public boolean isEmpty() {
        return this.field_1_ibkl == 0 && this.field_2_bkf_flags == 0;
    }

    @Override
    public String toString() {
        if (this.isEmpty()) {
            return "[BKF] EMPTY";
        }
        return super.toString();
    }
}

