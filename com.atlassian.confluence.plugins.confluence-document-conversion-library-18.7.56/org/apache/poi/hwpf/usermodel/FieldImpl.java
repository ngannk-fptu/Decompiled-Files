/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.usermodel;

import org.apache.poi.hwpf.model.PlexOfField;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Field;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.util.Internal;

@Internal
class FieldImpl
implements Field {
    private PlexOfField endPlex;
    private PlexOfField separatorPlex;
    private PlexOfField startPlex;

    public FieldImpl(PlexOfField startPlex, PlexOfField separatorPlex, PlexOfField endPlex) {
        if (startPlex == null) {
            throw new IllegalArgumentException("startPlex == null");
        }
        if (endPlex == null) {
            throw new IllegalArgumentException("endPlex == null");
        }
        if (startPlex.getFld().getBoundaryType() != 19) {
            throw new IllegalArgumentException("startPlex (" + startPlex + ") is not type of FIELD_BEGIN");
        }
        if (separatorPlex != null && separatorPlex.getFld().getBoundaryType() != 20) {
            throw new IllegalArgumentException("separatorPlex" + separatorPlex + ") is not type of FIELD_SEPARATOR");
        }
        if (endPlex.getFld().getBoundaryType() != 21) {
            throw new IllegalArgumentException("endPlex (" + endPlex + ") is not type of FIELD_END");
        }
        this.startPlex = startPlex;
        this.separatorPlex = separatorPlex;
        this.endPlex = endPlex;
    }

    @Override
    public Range firstSubrange(Range parent) {
        if (this.hasSeparator()) {
            if (this.getMarkStartOffset() + 1 == this.getMarkSeparatorOffset()) {
                return null;
            }
            return new Range(this.getMarkStartOffset() + 1, this.getMarkSeparatorOffset(), parent){

                @Override
                public String toString() {
                    return "FieldSubrange1 (" + super.toString() + ")";
                }
            };
        }
        if (this.getMarkStartOffset() + 1 == this.getMarkEndOffset()) {
            return null;
        }
        return new Range(this.getMarkStartOffset() + 1, this.getMarkEndOffset(), parent){

            @Override
            public String toString() {
                return "FieldSubrange1 (" + super.toString() + ")";
            }
        };
    }

    @Override
    public int getFieldEndOffset() {
        return this.endPlex.getFcStart() + 1;
    }

    @Override
    public int getFieldStartOffset() {
        return this.startPlex.getFcStart();
    }

    @Override
    public CharacterRun getMarkEndCharacterRun(Range parent) {
        return new Range(this.getMarkEndOffset(), this.getMarkEndOffset() + 1, parent).getCharacterRun(0);
    }

    @Override
    public int getMarkEndOffset() {
        return this.endPlex.getFcStart();
    }

    @Override
    public CharacterRun getMarkSeparatorCharacterRun(Range parent) {
        if (!this.hasSeparator()) {
            return null;
        }
        return new Range(this.getMarkSeparatorOffset(), this.getMarkSeparatorOffset() + 1, parent).getCharacterRun(0);
    }

    @Override
    public int getMarkSeparatorOffset() {
        return this.separatorPlex.getFcStart();
    }

    @Override
    public CharacterRun getMarkStartCharacterRun(Range parent) {
        return new Range(this.getMarkStartOffset(), this.getMarkStartOffset() + 1, parent).getCharacterRun(0);
    }

    @Override
    public int getMarkStartOffset() {
        return this.startPlex.getFcStart();
    }

    @Override
    public int getType() {
        return this.startPlex.getFld().getFieldType();
    }

    @Override
    public boolean hasSeparator() {
        return this.separatorPlex != null;
    }

    @Override
    public boolean isHasSep() {
        return this.endPlex.getFld().isFHasSep();
    }

    @Override
    public boolean isLocked() {
        return this.endPlex.getFld().isFLocked();
    }

    @Override
    public boolean isNested() {
        return this.endPlex.getFld().isFNested();
    }

    @Override
    public boolean isPrivateResult() {
        return this.endPlex.getFld().isFPrivateResult();
    }

    @Override
    public boolean isResultDirty() {
        return this.endPlex.getFld().isFResultDirty();
    }

    @Override
    public boolean isResultEdited() {
        return this.endPlex.getFld().isFResultEdited();
    }

    @Override
    public boolean isZombieEmbed() {
        return this.endPlex.getFld().isFZombieEmbed();
    }

    @Override
    public Range secondSubrange(Range parent) {
        if (!this.hasSeparator() || this.getMarkSeparatorOffset() + 1 == this.getMarkEndOffset()) {
            return null;
        }
        return new Range(this.getMarkSeparatorOffset() + 1, this.getMarkEndOffset(), parent){

            @Override
            public String toString() {
                return "FieldSubrange2 (" + super.toString() + ")";
            }
        };
    }

    public String toString() {
        return "Field [" + this.getFieldStartOffset() + "; " + this.getFieldEndOffset() + "] (type: 0x" + Integer.toHexString(this.getType()) + " = " + this.getType() + " )";
    }
}

