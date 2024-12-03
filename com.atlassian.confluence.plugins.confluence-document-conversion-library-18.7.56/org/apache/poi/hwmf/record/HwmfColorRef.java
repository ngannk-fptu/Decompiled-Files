/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwmf.record;

import java.awt.Color;
import java.io.IOException;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.util.GenericRecordJsonWriter;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianInputStream;

public class HwmfColorRef
implements Duplicatable,
GenericRecord {
    private Color colorRef = Color.BLACK;

    public HwmfColorRef() {
    }

    public HwmfColorRef(HwmfColorRef other) {
        this.colorRef = other.colorRef;
    }

    public HwmfColorRef(Color colorRef) {
        this.colorRef = colorRef;
    }

    public int init(LittleEndianInputStream leis) throws IOException {
        int red = leis.readUByte();
        int green = leis.readUByte();
        int blue = leis.readUByte();
        leis.readUByte();
        this.colorRef = new Color(red, green, blue);
        return 4;
    }

    public Color getColor() {
        return this.colorRef;
    }

    public void setColor(Color color) {
        this.colorRef = color;
    }

    @Override
    public HwmfColorRef copy() {
        return new HwmfColorRef(this);
    }

    public String toString() {
        return GenericRecordJsonWriter.marshal(this);
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("color", this::getColor);
    }
}

