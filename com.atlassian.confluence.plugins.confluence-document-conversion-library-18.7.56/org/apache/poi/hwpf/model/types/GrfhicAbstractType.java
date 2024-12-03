/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model.types;

import java.util.Objects;
import org.apache.poi.util.BitField;
import org.apache.poi.util.Internal;

@Internal
public abstract class GrfhicAbstractType {
    protected byte field_1_grfhic;
    private static final BitField fHtmlChecked = new BitField(1);
    private static final BitField fHtmlUnsupported = new BitField(2);
    private static final BitField fHtmlListTextNotSharpDot = new BitField(4);
    private static final BitField fHtmlNotPeriod = new BitField(8);
    private static final BitField fHtmlFirstLineMismatch = new BitField(16);
    private static final BitField fHtmlTabLeftIndentMismatch = new BitField(32);
    private static final BitField fHtmlHangingIndentBeneathNumber = new BitField(64);
    private static final BitField fHtmlBuiltInBullet = new BitField(128);

    protected GrfhicAbstractType() {
    }

    protected void fillFields(byte[] data, int offset) {
        this.field_1_grfhic = data[0 + offset];
    }

    public void serialize(byte[] data, int offset) {
        data[0 + offset] = this.field_1_grfhic;
    }

    public byte[] serialize() {
        byte[] result = new byte[GrfhicAbstractType.getSize()];
        this.serialize(result, 0);
        return result;
    }

    public static int getSize() {
        return 1;
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
        GrfhicAbstractType other = (GrfhicAbstractType)obj;
        return this.field_1_grfhic == other.field_1_grfhic;
    }

    public int hashCode() {
        return Objects.hash(this.field_1_grfhic);
    }

    public String toString() {
        return "[Grfhic]\n    .grfhic               =  ( " + this.field_1_grfhic + " )\n         .fHtmlChecked             = " + this.isFHtmlChecked() + '\n' + "         .fHtmlUnsupported         = " + this.isFHtmlUnsupported() + '\n' + "         .fHtmlListTextNotSharpDot     = " + this.isFHtmlListTextNotSharpDot() + '\n' + "         .fHtmlNotPeriod           = " + this.isFHtmlNotPeriod() + '\n' + "         .fHtmlFirstLineMismatch     = " + this.isFHtmlFirstLineMismatch() + '\n' + "         .fHtmlTabLeftIndentMismatch     = " + this.isFHtmlTabLeftIndentMismatch() + '\n' + "         .fHtmlHangingIndentBeneathNumber     = " + this.isFHtmlHangingIndentBeneathNumber() + '\n' + "         .fHtmlBuiltInBullet       = " + this.isFHtmlBuiltInBullet() + '\n' + "[/Grfhic]";
    }

    @Internal
    public byte getGrfhic() {
        return this.field_1_grfhic;
    }

    @Internal
    public void setGrfhic(byte field_1_grfhic) {
        this.field_1_grfhic = field_1_grfhic;
    }

    @Internal
    public void setFHtmlChecked(boolean value) {
        this.field_1_grfhic = (byte)fHtmlChecked.setBoolean(this.field_1_grfhic, value);
    }

    @Internal
    public boolean isFHtmlChecked() {
        return fHtmlChecked.isSet(this.field_1_grfhic);
    }

    @Internal
    public void setFHtmlUnsupported(boolean value) {
        this.field_1_grfhic = (byte)fHtmlUnsupported.setBoolean(this.field_1_grfhic, value);
    }

    @Internal
    public boolean isFHtmlUnsupported() {
        return fHtmlUnsupported.isSet(this.field_1_grfhic);
    }

    @Internal
    public void setFHtmlListTextNotSharpDot(boolean value) {
        this.field_1_grfhic = (byte)fHtmlListTextNotSharpDot.setBoolean(this.field_1_grfhic, value);
    }

    @Internal
    public boolean isFHtmlListTextNotSharpDot() {
        return fHtmlListTextNotSharpDot.isSet(this.field_1_grfhic);
    }

    @Internal
    public void setFHtmlNotPeriod(boolean value) {
        this.field_1_grfhic = (byte)fHtmlNotPeriod.setBoolean(this.field_1_grfhic, value);
    }

    @Internal
    public boolean isFHtmlNotPeriod() {
        return fHtmlNotPeriod.isSet(this.field_1_grfhic);
    }

    @Internal
    public void setFHtmlFirstLineMismatch(boolean value) {
        this.field_1_grfhic = (byte)fHtmlFirstLineMismatch.setBoolean(this.field_1_grfhic, value);
    }

    @Internal
    public boolean isFHtmlFirstLineMismatch() {
        return fHtmlFirstLineMismatch.isSet(this.field_1_grfhic);
    }

    @Internal
    public void setFHtmlTabLeftIndentMismatch(boolean value) {
        this.field_1_grfhic = (byte)fHtmlTabLeftIndentMismatch.setBoolean(this.field_1_grfhic, value);
    }

    @Internal
    public boolean isFHtmlTabLeftIndentMismatch() {
        return fHtmlTabLeftIndentMismatch.isSet(this.field_1_grfhic);
    }

    @Internal
    public void setFHtmlHangingIndentBeneathNumber(boolean value) {
        this.field_1_grfhic = (byte)fHtmlHangingIndentBeneathNumber.setBoolean(this.field_1_grfhic, value);
    }

    @Internal
    public boolean isFHtmlHangingIndentBeneathNumber() {
        return fHtmlHangingIndentBeneathNumber.isSet(this.field_1_grfhic);
    }

    @Internal
    public void setFHtmlBuiltInBullet(boolean value) {
        this.field_1_grfhic = (byte)fHtmlBuiltInBullet.setBoolean(this.field_1_grfhic, value);
    }

    @Internal
    public boolean isFHtmlBuiltInBullet() {
        return fHtmlBuiltInBullet.isSet(this.field_1_grfhic);
    }
}

