/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import org.apache.poi.hwpf.model.types.FLDAbstractType;
import org.apache.poi.util.Internal;

@Internal
public final class FieldDescriptor
extends FLDAbstractType {
    public static final int FIELD_BEGIN_MARK = 19;
    public static final int FIELD_SEPARATOR_MARK = 20;
    public static final int FIELD_END_MARK = 21;

    public FieldDescriptor(byte[] data) {
        this.fillFields(data, 0);
    }

    public int getBoundaryType() {
        return this.getCh();
    }

    public int getFieldType() {
        if (this.getCh() != 19) {
            throw new UnsupportedOperationException("This field is only defined for begin marks.");
        }
        return this.getFlt();
    }

    public boolean isZombieEmbed() {
        if (this.getCh() != 21) {
            throw new UnsupportedOperationException("This field is only defined for end marks.");
        }
        return this.isFZombieEmbed();
    }

    public boolean isResultDirty() {
        if (this.getCh() != 21) {
            throw new UnsupportedOperationException("This field is only defined for end marks.");
        }
        return this.isFResultDirty();
    }

    public boolean isResultEdited() {
        if (this.getCh() != 21) {
            throw new UnsupportedOperationException("This field is only defined for end marks.");
        }
        return this.isFResultEdited();
    }

    public boolean isLocked() {
        if (this.getCh() != 21) {
            throw new UnsupportedOperationException("This field is only defined for end marks.");
        }
        return this.isFLocked();
    }

    public boolean isPrivateResult() {
        if (this.getCh() != 21) {
            throw new UnsupportedOperationException("This field is only defined for end marks.");
        }
        return this.isFPrivateResult();
    }

    public boolean isNested() {
        if (this.getCh() != 21) {
            throw new UnsupportedOperationException("This field is only defined for end marks.");
        }
        return this.isFNested();
    }

    public boolean isHasSep() {
        if (this.getCh() != 21) {
            throw new UnsupportedOperationException("This field is only defined for end marks.");
        }
        return this.isFHasSep();
    }
}

