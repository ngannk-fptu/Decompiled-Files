/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model.types;

import org.apache.poi.util.BitField;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public abstract class StdfPost2000AbstractType {
    protected short field_1_info1;
    private static final BitField istdLink = new BitField(4095);
    private static final BitField fHasOriginalStyle = new BitField(4096);
    private static final BitField fSpare = new BitField(57344);
    protected long field_2_rsid;
    protected short field_3_info3;
    private static final BitField iftcHtml = new BitField(7);
    private static final BitField unused = new BitField(8);
    private static final BitField iPriority = new BitField(65520);

    protected StdfPost2000AbstractType() {
    }

    protected void fillFields(byte[] data, int offset) {
        this.field_1_info1 = LittleEndian.getShort(data, 0 + offset);
        this.field_2_rsid = LittleEndian.getUInt(data, 2 + offset);
        this.field_3_info3 = LittleEndian.getShort(data, 6 + offset);
    }

    public void serialize(byte[] data, int offset) {
        LittleEndian.putShort(data, 0 + offset, this.field_1_info1);
        LittleEndian.putUInt(data, 2 + offset, this.field_2_rsid);
        LittleEndian.putShort(data, 6 + offset, this.field_3_info3);
    }

    public static int getSize() {
        return 8;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[StdfPost2000]\n");
        builder.append("    .info1                = ");
        builder.append(" (").append(this.getInfo1()).append(" )\n");
        builder.append("         .istdLink                 = ").append(this.getIstdLink()).append('\n');
        builder.append("         .fHasOriginalStyle        = ").append(this.isFHasOriginalStyle()).append('\n');
        builder.append("         .fSpare                   = ").append(this.getFSpare()).append('\n');
        builder.append("    .rsid                 = ");
        builder.append(" (").append(this.getRsid()).append(" )\n");
        builder.append("    .info3                = ");
        builder.append(" (").append(this.getInfo3()).append(" )\n");
        builder.append("         .iftcHtml                 = ").append(this.getIftcHtml()).append('\n');
        builder.append("         .unused                   = ").append(this.isUnused()).append('\n');
        builder.append("         .iPriority                = ").append(this.getIPriority()).append('\n');
        builder.append("[/StdfPost2000]\n");
        return builder.toString();
    }

    @Internal
    public short getInfo1() {
        return this.field_1_info1;
    }

    @Internal
    public void setInfo1(short field_1_info1) {
        this.field_1_info1 = field_1_info1;
    }

    @Internal
    public long getRsid() {
        return this.field_2_rsid;
    }

    @Internal
    public void setRsid(long field_2_rsid) {
        this.field_2_rsid = field_2_rsid;
    }

    @Internal
    public short getInfo3() {
        return this.field_3_info3;
    }

    @Internal
    public void setInfo3(short field_3_info3) {
        this.field_3_info3 = field_3_info3;
    }

    @Internal
    public void setIstdLink(short value) {
        this.field_1_info1 = (short)istdLink.setValue(this.field_1_info1, value);
    }

    @Internal
    public short getIstdLink() {
        return (short)istdLink.getValue(this.field_1_info1);
    }

    @Internal
    public void setFHasOriginalStyle(boolean value) {
        this.field_1_info1 = (short)fHasOriginalStyle.setBoolean(this.field_1_info1, value);
    }

    @Internal
    public boolean isFHasOriginalStyle() {
        return fHasOriginalStyle.isSet(this.field_1_info1);
    }

    @Internal
    public void setFSpare(byte value) {
        this.field_1_info1 = (short)fSpare.setValue(this.field_1_info1, value);
    }

    @Internal
    public byte getFSpare() {
        return (byte)fSpare.getValue(this.field_1_info1);
    }

    @Internal
    public void setIftcHtml(byte value) {
        this.field_3_info3 = (short)iftcHtml.setValue(this.field_3_info3, value);
    }

    @Internal
    public byte getIftcHtml() {
        return (byte)iftcHtml.getValue(this.field_3_info3);
    }

    @Internal
    public void setUnused(boolean value) {
        this.field_3_info3 = (short)unused.setBoolean(this.field_3_info3, value);
    }

    @Internal
    public boolean isUnused() {
        return unused.isSet(this.field_3_info3);
    }

    @Internal
    public void setIPriority(short value) {
        this.field_3_info3 = (short)iPriority.setValue(this.field_3_info3, value);
    }

    @Internal
    public short getIPriority() {
        return (short)iPriority.getValue(this.field_3_info3);
    }
}

