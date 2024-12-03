/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model.types;

import org.apache.poi.hwpf.usermodel.BorderCode;
import org.apache.poi.hwpf.usermodel.ShadingDescriptor;
import org.apache.poi.util.BitField;
import org.apache.poi.util.Internal;

@Internal
public abstract class TCAbstractType {
    private static final BitField fFirstMerged = new BitField(1);
    private static final BitField fMerged = new BitField(2);
    private static final BitField fVertical = new BitField(4);
    private static final BitField fBackward = new BitField(8);
    private static final BitField fRotateFont = new BitField(16);
    private static final BitField fVertMerge = new BitField(32);
    private static final BitField fVertRestart = new BitField(64);
    private static final BitField vertAlign = new BitField(384);
    private static final BitField ftsWidth = new BitField(3584);
    private static final BitField fFitText = new BitField(4096);
    private static final BitField fNoWrap = new BitField(8192);
    private static final BitField fUnused = new BitField(49152);
    protected short field_1_rgf;
    protected short field_2_wWidth;
    protected ShadingDescriptor field_3_shd;
    protected short field_4_wCellPaddingLeft;
    protected short field_5_wCellPaddingTop;
    protected short field_6_wCellPaddingBottom;
    protected short field_7_wCellPaddingRight;
    protected byte field_8_ftsCellPaddingLeft;
    protected byte field_9_ftsCellPaddingTop;
    protected byte field_10_ftsCellPaddingBottom;
    protected byte field_11_ftsCellPaddingRight;
    protected short field_12_wCellSpacingLeft;
    protected short field_13_wCellSpacingTop;
    protected short field_14_wCellSpacingBottom;
    protected short field_15_wCellSpacingRight;
    protected byte field_16_ftsCellSpacingLeft;
    protected byte field_17_ftsCellSpacingTop;
    protected byte field_18_ftsCellSpacingBottom;
    protected byte field_19_ftsCellSpacingRight;
    protected BorderCode field_20_brcTop;
    protected BorderCode field_21_brcLeft;
    protected BorderCode field_22_brcBottom;
    protected BorderCode field_23_brcRight;

    protected TCAbstractType() {
        this.field_3_shd = new ShadingDescriptor();
        this.field_20_brcTop = new BorderCode();
        this.field_21_brcLeft = new BorderCode();
        this.field_22_brcBottom = new BorderCode();
        this.field_23_brcRight = new BorderCode();
    }

    protected TCAbstractType(TCAbstractType other) {
        this.field_1_rgf = other.field_1_rgf;
        this.field_2_wWidth = other.field_2_wWidth;
        this.field_3_shd = other.field_3_shd == null ? null : other.field_3_shd.copy();
        this.field_4_wCellPaddingLeft = other.field_4_wCellPaddingLeft;
        this.field_5_wCellPaddingTop = other.field_5_wCellPaddingTop;
        this.field_6_wCellPaddingBottom = other.field_6_wCellPaddingBottom;
        this.field_7_wCellPaddingRight = other.field_7_wCellPaddingRight;
        this.field_8_ftsCellPaddingLeft = other.field_8_ftsCellPaddingLeft;
        this.field_9_ftsCellPaddingTop = other.field_9_ftsCellPaddingTop;
        this.field_10_ftsCellPaddingBottom = other.field_10_ftsCellPaddingBottom;
        this.field_11_ftsCellPaddingRight = other.field_11_ftsCellPaddingRight;
        this.field_12_wCellSpacingLeft = other.field_12_wCellSpacingLeft;
        this.field_13_wCellSpacingTop = other.field_13_wCellSpacingTop;
        this.field_14_wCellSpacingBottom = other.field_14_wCellSpacingBottom;
        this.field_15_wCellSpacingRight = other.field_15_wCellSpacingRight;
        this.field_16_ftsCellSpacingLeft = other.field_16_ftsCellSpacingLeft;
        this.field_17_ftsCellSpacingTop = other.field_17_ftsCellSpacingTop;
        this.field_18_ftsCellSpacingBottom = other.field_18_ftsCellSpacingBottom;
        this.field_19_ftsCellSpacingRight = other.field_19_ftsCellSpacingRight;
        this.field_20_brcTop = other.field_20_brcTop == null ? null : other.field_20_brcTop.copy();
        this.field_21_brcLeft = other.field_21_brcLeft == null ? null : other.field_21_brcLeft.copy();
        this.field_22_brcBottom = other.field_22_brcBottom == null ? null : other.field_22_brcBottom.copy();
        this.field_23_brcRight = other.field_23_brcRight == null ? null : other.field_23_brcRight.copy();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[TC]\n");
        builder.append("    .rgf                  = ");
        builder.append(" (").append(this.getRgf()).append(" )\n");
        builder.append("         .fFirstMerged             = ").append(this.isFFirstMerged()).append('\n');
        builder.append("         .fMerged                  = ").append(this.isFMerged()).append('\n');
        builder.append("         .fVertical                = ").append(this.isFVertical()).append('\n');
        builder.append("         .fBackward                = ").append(this.isFBackward()).append('\n');
        builder.append("         .fRotateFont              = ").append(this.isFRotateFont()).append('\n');
        builder.append("         .fVertMerge               = ").append(this.isFVertMerge()).append('\n');
        builder.append("         .fVertRestart             = ").append(this.isFVertRestart()).append('\n');
        builder.append("         .vertAlign                = ").append(this.getVertAlign()).append('\n');
        builder.append("         .ftsWidth                 = ").append(this.getFtsWidth()).append('\n');
        builder.append("         .fFitText                 = ").append(this.isFFitText()).append('\n');
        builder.append("         .fNoWrap                  = ").append(this.isFNoWrap()).append('\n');
        builder.append("         .fUnused                  = ").append(this.getFUnused()).append('\n');
        builder.append("    .wWidth               = ");
        builder.append(" (").append(this.getWWidth()).append(" )\n");
        builder.append("    .shd                  = ");
        builder.append(" (").append(this.getShd()).append(" )\n");
        builder.append("    .wCellPaddingLeft     = ");
        builder.append(" (").append(this.getWCellPaddingLeft()).append(" )\n");
        builder.append("    .wCellPaddingTop      = ");
        builder.append(" (").append(this.getWCellPaddingTop()).append(" )\n");
        builder.append("    .wCellPaddingBottom   = ");
        builder.append(" (").append(this.getWCellPaddingBottom()).append(" )\n");
        builder.append("    .wCellPaddingRight    = ");
        builder.append(" (").append(this.getWCellPaddingRight()).append(" )\n");
        builder.append("    .ftsCellPaddingLeft   = ");
        builder.append(" (").append(this.getFtsCellPaddingLeft()).append(" )\n");
        builder.append("    .ftsCellPaddingTop    = ");
        builder.append(" (").append(this.getFtsCellPaddingTop()).append(" )\n");
        builder.append("    .ftsCellPaddingBottom = ");
        builder.append(" (").append(this.getFtsCellPaddingBottom()).append(" )\n");
        builder.append("    .ftsCellPaddingRight  = ");
        builder.append(" (").append(this.getFtsCellPaddingRight()).append(" )\n");
        builder.append("    .wCellSpacingLeft     = ");
        builder.append(" (").append(this.getWCellSpacingLeft()).append(" )\n");
        builder.append("    .wCellSpacingTop      = ");
        builder.append(" (").append(this.getWCellSpacingTop()).append(" )\n");
        builder.append("    .wCellSpacingBottom   = ");
        builder.append(" (").append(this.getWCellSpacingBottom()).append(" )\n");
        builder.append("    .wCellSpacingRight    = ");
        builder.append(" (").append(this.getWCellSpacingRight()).append(" )\n");
        builder.append("    .ftsCellSpacingLeft   = ");
        builder.append(" (").append(this.getFtsCellSpacingLeft()).append(" )\n");
        builder.append("    .ftsCellSpacingTop    = ");
        builder.append(" (").append(this.getFtsCellSpacingTop()).append(" )\n");
        builder.append("    .ftsCellSpacingBottom = ");
        builder.append(" (").append(this.getFtsCellSpacingBottom()).append(" )\n");
        builder.append("    .ftsCellSpacingRight  = ");
        builder.append(" (").append(this.getFtsCellSpacingRight()).append(" )\n");
        builder.append("    .brcTop               = ");
        builder.append(" (").append(this.getBrcTop()).append(" )\n");
        builder.append("    .brcLeft              = ");
        builder.append(" (").append(this.getBrcLeft()).append(" )\n");
        builder.append("    .brcBottom            = ");
        builder.append(" (").append(this.getBrcBottom()).append(" )\n");
        builder.append("    .brcRight             = ");
        builder.append(" (").append(this.getBrcRight()).append(" )\n");
        builder.append("[/TC]\n");
        return builder.toString();
    }

    @Internal
    public short getRgf() {
        return this.field_1_rgf;
    }

    @Internal
    public void setRgf(short field_1_rgf) {
        this.field_1_rgf = field_1_rgf;
    }

    @Internal
    public short getWWidth() {
        return this.field_2_wWidth;
    }

    @Internal
    public void setWWidth(short field_2_wWidth) {
        this.field_2_wWidth = field_2_wWidth;
    }

    @Internal
    public ShadingDescriptor getShd() {
        return this.field_3_shd;
    }

    @Internal
    public void setShd(ShadingDescriptor field_3_shd) {
        this.field_3_shd = field_3_shd;
    }

    @Internal
    public short getWCellPaddingLeft() {
        return this.field_4_wCellPaddingLeft;
    }

    @Internal
    public void setWCellPaddingLeft(short field_4_wCellPaddingLeft) {
        this.field_4_wCellPaddingLeft = field_4_wCellPaddingLeft;
    }

    @Internal
    public short getWCellPaddingTop() {
        return this.field_5_wCellPaddingTop;
    }

    @Internal
    public void setWCellPaddingTop(short field_5_wCellPaddingTop) {
        this.field_5_wCellPaddingTop = field_5_wCellPaddingTop;
    }

    @Internal
    public short getWCellPaddingBottom() {
        return this.field_6_wCellPaddingBottom;
    }

    @Internal
    public void setWCellPaddingBottom(short field_6_wCellPaddingBottom) {
        this.field_6_wCellPaddingBottom = field_6_wCellPaddingBottom;
    }

    @Internal
    public short getWCellPaddingRight() {
        return this.field_7_wCellPaddingRight;
    }

    @Internal
    public void setWCellPaddingRight(short field_7_wCellPaddingRight) {
        this.field_7_wCellPaddingRight = field_7_wCellPaddingRight;
    }

    @Internal
    public byte getFtsCellPaddingLeft() {
        return this.field_8_ftsCellPaddingLeft;
    }

    @Internal
    public void setFtsCellPaddingLeft(byte field_8_ftsCellPaddingLeft) {
        this.field_8_ftsCellPaddingLeft = field_8_ftsCellPaddingLeft;
    }

    @Internal
    public byte getFtsCellPaddingTop() {
        return this.field_9_ftsCellPaddingTop;
    }

    @Internal
    public void setFtsCellPaddingTop(byte field_9_ftsCellPaddingTop) {
        this.field_9_ftsCellPaddingTop = field_9_ftsCellPaddingTop;
    }

    @Internal
    public byte getFtsCellPaddingBottom() {
        return this.field_10_ftsCellPaddingBottom;
    }

    @Internal
    public void setFtsCellPaddingBottom(byte field_10_ftsCellPaddingBottom) {
        this.field_10_ftsCellPaddingBottom = field_10_ftsCellPaddingBottom;
    }

    @Internal
    public byte getFtsCellPaddingRight() {
        return this.field_11_ftsCellPaddingRight;
    }

    @Internal
    public void setFtsCellPaddingRight(byte field_11_ftsCellPaddingRight) {
        this.field_11_ftsCellPaddingRight = field_11_ftsCellPaddingRight;
    }

    @Internal
    public short getWCellSpacingLeft() {
        return this.field_12_wCellSpacingLeft;
    }

    @Internal
    public void setWCellSpacingLeft(short field_12_wCellSpacingLeft) {
        this.field_12_wCellSpacingLeft = field_12_wCellSpacingLeft;
    }

    @Internal
    public short getWCellSpacingTop() {
        return this.field_13_wCellSpacingTop;
    }

    @Internal
    public void setWCellSpacingTop(short field_13_wCellSpacingTop) {
        this.field_13_wCellSpacingTop = field_13_wCellSpacingTop;
    }

    @Internal
    public short getWCellSpacingBottom() {
        return this.field_14_wCellSpacingBottom;
    }

    @Internal
    public void setWCellSpacingBottom(short field_14_wCellSpacingBottom) {
        this.field_14_wCellSpacingBottom = field_14_wCellSpacingBottom;
    }

    @Internal
    public short getWCellSpacingRight() {
        return this.field_15_wCellSpacingRight;
    }

    @Internal
    public void setWCellSpacingRight(short field_15_wCellSpacingRight) {
        this.field_15_wCellSpacingRight = field_15_wCellSpacingRight;
    }

    @Internal
    public byte getFtsCellSpacingLeft() {
        return this.field_16_ftsCellSpacingLeft;
    }

    @Internal
    public void setFtsCellSpacingLeft(byte field_16_ftsCellSpacingLeft) {
        this.field_16_ftsCellSpacingLeft = field_16_ftsCellSpacingLeft;
    }

    @Internal
    public byte getFtsCellSpacingTop() {
        return this.field_17_ftsCellSpacingTop;
    }

    @Internal
    public void setFtsCellSpacingTop(byte field_17_ftsCellSpacingTop) {
        this.field_17_ftsCellSpacingTop = field_17_ftsCellSpacingTop;
    }

    @Internal
    public byte getFtsCellSpacingBottom() {
        return this.field_18_ftsCellSpacingBottom;
    }

    @Internal
    public void setFtsCellSpacingBottom(byte field_18_ftsCellSpacingBottom) {
        this.field_18_ftsCellSpacingBottom = field_18_ftsCellSpacingBottom;
    }

    @Internal
    public byte getFtsCellSpacingRight() {
        return this.field_19_ftsCellSpacingRight;
    }

    @Internal
    public void setFtsCellSpacingRight(byte field_19_ftsCellSpacingRight) {
        this.field_19_ftsCellSpacingRight = field_19_ftsCellSpacingRight;
    }

    @Internal
    public BorderCode getBrcTop() {
        return this.field_20_brcTop;
    }

    @Internal
    public void setBrcTop(BorderCode field_20_brcTop) {
        this.field_20_brcTop = field_20_brcTop;
    }

    @Internal
    public BorderCode getBrcLeft() {
        return this.field_21_brcLeft;
    }

    @Internal
    public void setBrcLeft(BorderCode field_21_brcLeft) {
        this.field_21_brcLeft = field_21_brcLeft;
    }

    @Internal
    public BorderCode getBrcBottom() {
        return this.field_22_brcBottom;
    }

    @Internal
    public void setBrcBottom(BorderCode field_22_brcBottom) {
        this.field_22_brcBottom = field_22_brcBottom;
    }

    @Internal
    public BorderCode getBrcRight() {
        return this.field_23_brcRight;
    }

    @Internal
    public void setBrcRight(BorderCode field_23_brcRight) {
        this.field_23_brcRight = field_23_brcRight;
    }

    @Internal
    public void setFFirstMerged(boolean value) {
        this.field_1_rgf = (short)fFirstMerged.setBoolean(this.field_1_rgf, value);
    }

    @Internal
    public boolean isFFirstMerged() {
        return fFirstMerged.isSet(this.field_1_rgf);
    }

    @Internal
    public void setFMerged(boolean value) {
        this.field_1_rgf = (short)fMerged.setBoolean(this.field_1_rgf, value);
    }

    @Internal
    public boolean isFMerged() {
        return fMerged.isSet(this.field_1_rgf);
    }

    @Internal
    public void setFVertical(boolean value) {
        this.field_1_rgf = (short)fVertical.setBoolean(this.field_1_rgf, value);
    }

    @Internal
    public boolean isFVertical() {
        return fVertical.isSet(this.field_1_rgf);
    }

    @Internal
    public void setFBackward(boolean value) {
        this.field_1_rgf = (short)fBackward.setBoolean(this.field_1_rgf, value);
    }

    @Internal
    public boolean isFBackward() {
        return fBackward.isSet(this.field_1_rgf);
    }

    @Internal
    public void setFRotateFont(boolean value) {
        this.field_1_rgf = (short)fRotateFont.setBoolean(this.field_1_rgf, value);
    }

    @Internal
    public boolean isFRotateFont() {
        return fRotateFont.isSet(this.field_1_rgf);
    }

    @Internal
    public void setFVertMerge(boolean value) {
        this.field_1_rgf = (short)fVertMerge.setBoolean(this.field_1_rgf, value);
    }

    @Internal
    public boolean isFVertMerge() {
        return fVertMerge.isSet(this.field_1_rgf);
    }

    @Internal
    public void setFVertRestart(boolean value) {
        this.field_1_rgf = (short)fVertRestart.setBoolean(this.field_1_rgf, value);
    }

    @Internal
    public boolean isFVertRestart() {
        return fVertRestart.isSet(this.field_1_rgf);
    }

    @Internal
    public void setVertAlign(byte value) {
        this.field_1_rgf = (short)vertAlign.setValue(this.field_1_rgf, value);
    }

    @Internal
    public byte getVertAlign() {
        return (byte)vertAlign.getValue(this.field_1_rgf);
    }

    @Internal
    public void setFtsWidth(byte value) {
        this.field_1_rgf = (short)ftsWidth.setValue(this.field_1_rgf, value);
    }

    @Internal
    public byte getFtsWidth() {
        return (byte)ftsWidth.getValue(this.field_1_rgf);
    }

    @Internal
    public void setFFitText(boolean value) {
        this.field_1_rgf = (short)fFitText.setBoolean(this.field_1_rgf, value);
    }

    @Internal
    public boolean isFFitText() {
        return fFitText.isSet(this.field_1_rgf);
    }

    @Internal
    public void setFNoWrap(boolean value) {
        this.field_1_rgf = (short)fNoWrap.setBoolean(this.field_1_rgf, value);
    }

    @Internal
    public boolean isFNoWrap() {
        return fNoWrap.isSet(this.field_1_rgf);
    }

    @Internal
    public void setFUnused(byte value) {
        this.field_1_rgf = (short)fUnused.setValue(this.field_1_rgf, value);
    }

    @Internal
    public byte getFUnused() {
        return (byte)fUnused.getValue(this.field_1_rgf);
    }
}

