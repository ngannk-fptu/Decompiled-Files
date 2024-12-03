/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.usermodel;

import java.util.Objects;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.hwpf.model.types.TLPAbstractType;

public class TableAutoformatLookSpecifier
extends TLPAbstractType
implements Duplicatable {
    public static final int SIZE = 4;

    public TableAutoformatLookSpecifier() {
    }

    public TableAutoformatLookSpecifier(TableAutoformatLookSpecifier other) {
        super(other);
    }

    public TableAutoformatLookSpecifier(byte[] data, int offset) {
        this.fillFields(data, offset);
    }

    @Override
    public TableAutoformatLookSpecifier copy() {
        return new TableAutoformatLookSpecifier(this);
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
        TableAutoformatLookSpecifier other = (TableAutoformatLookSpecifier)obj;
        if (this.field_1_itl != other.field_1_itl) {
            return false;
        }
        return this.field_2_tlp_flags == other.field_2_tlp_flags;
    }

    public int hashCode() {
        return Objects.hash(this.field_1_itl, this.field_2_tlp_flags);
    }

    public boolean isEmpty() {
        return this.field_1_itl == 0 && this.field_2_tlp_flags == 0;
    }
}

