/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.font.ttf;

import com.sun.pdfview.font.ttf.TrueTypeTable;
import java.nio.ByteBuffer;

public class HheaTable
extends TrueTypeTable {
    private int version;
    private short ascent;
    private short descent;
    private short lineGap;
    private short advanceWidthMax;
    private short minLeftSideBearing;
    private short minRightSideBearing;
    private short xMaxExtent;
    private short caretSlopeRise;
    private short caretSlopeRun;
    private short caretOffset;
    private short metricDataFormat;
    private short numOfLongHorMetrics;

    protected HheaTable() {
        super(1751474532);
        this.setVersion(65536);
    }

    @Override
    public void setData(ByteBuffer data) {
        if (data.remaining() != 36) {
            throw new IllegalArgumentException("Bad Head table size");
        }
        this.setVersion(data.getInt());
        this.setAscent(data.getShort());
        this.setDescent(data.getShort());
        this.setLineGap(data.getShort());
        this.setAdvanceWidthMax(data.getShort());
        this.setMinLeftSideBearing(data.getShort());
        this.setMinRightSideBearing(data.getShort());
        this.setXMaxExtent(data.getShort());
        this.setCaretSlopeRise(data.getShort());
        this.setCaretSlopeRun(data.getShort());
        this.setCaretOffset(data.getShort());
        data.getShort();
        data.getShort();
        data.getShort();
        data.getShort();
        this.setMetricDataFormat(data.getShort());
        this.setNumOfLongHorMetrics(data.getShort());
    }

    @Override
    public ByteBuffer getData() {
        ByteBuffer buf = ByteBuffer.allocate(this.getLength());
        buf.putInt(this.getVersion());
        buf.putShort(this.getAscent());
        buf.putShort(this.getDescent());
        buf.putShort(this.getLineGap());
        buf.putShort(this.getAdvanceWidthMax());
        buf.putShort(this.getMinLeftSideBearing());
        buf.putShort(this.getMinRightSideBearing());
        buf.putShort(this.getXMaxExtent());
        buf.putShort(this.getCaretSlopeRise());
        buf.putShort(this.getCaretSlopeRun());
        buf.putShort(this.getCaretOffset());
        buf.putShort((short)0);
        buf.putShort((short)0);
        buf.putShort((short)0);
        buf.putShort((short)0);
        buf.putShort(this.getMetricDataFormat());
        buf.putShort((short)this.getNumOfLongHorMetrics());
        buf.flip();
        return buf;
    }

    @Override
    public int getLength() {
        return 36;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        String indent = "    ";
        buf.append(indent + "Version             : " + Integer.toHexString(this.getVersion()) + "\n");
        buf.append(indent + "Ascent              : " + this.getAscent() + "\n");
        buf.append(indent + "Descent             : " + this.getDescent() + "\n");
        buf.append(indent + "LineGap             : " + this.getLineGap() + "\n");
        buf.append(indent + "AdvanceWidthMax     : " + this.getAdvanceWidthMax() + "\n");
        buf.append(indent + "MinLSB              : " + this.getMinLeftSideBearing() + "\n");
        buf.append(indent + "MinRSB              : " + this.getMinRightSideBearing() + "\n");
        buf.append(indent + "MaxExtent           : " + this.getXMaxExtent() + "\n");
        buf.append(indent + "CaretSlopeRise      : " + this.getCaretSlopeRise() + "\n");
        buf.append(indent + "CaretSlopeRun       : " + this.getCaretSlopeRun() + "\n");
        buf.append(indent + "CaretOffset         : " + this.getCaretOffset() + "\n");
        buf.append(indent + "MetricDataFormat    : " + this.getMetricDataFormat() + "\n");
        buf.append(indent + "NumOfLongHorMetrics : " + this.getNumOfLongHorMetrics() + "\n");
        return buf.toString();
    }

    public short getAscent() {
        return this.ascent;
    }

    public void setAscent(short ascent) {
        this.ascent = ascent;
    }

    public short getDescent() {
        return this.descent;
    }

    public void setDescent(short descent) {
        this.descent = descent;
    }

    public short getLineGap() {
        return this.lineGap;
    }

    public void setLineGap(short lineGap) {
        this.lineGap = lineGap;
    }

    public short getAdvanceWidthMax() {
        return this.advanceWidthMax;
    }

    public void setAdvanceWidthMax(short advanceWidthMax) {
        this.advanceWidthMax = advanceWidthMax;
    }

    public short getMinLeftSideBearing() {
        return this.minLeftSideBearing;
    }

    public void setMinLeftSideBearing(short minLeftSideBearing) {
        this.minLeftSideBearing = minLeftSideBearing;
    }

    public short getMinRightSideBearing() {
        return this.minRightSideBearing;
    }

    public void setMinRightSideBearing(short minRightSideBearing) {
        this.minRightSideBearing = minRightSideBearing;
    }

    public short getXMaxExtent() {
        return this.xMaxExtent;
    }

    public void setXMaxExtent(short xMaxExtent) {
        this.xMaxExtent = xMaxExtent;
    }

    public short getCaretSlopeRise() {
        return this.caretSlopeRise;
    }

    public void setCaretSlopeRise(short caretSlopeRise) {
        this.caretSlopeRise = caretSlopeRise;
    }

    public short getCaretSlopeRun() {
        return this.caretSlopeRun;
    }

    public void setCaretSlopeRun(short caretSlopeRun) {
        this.caretSlopeRun = caretSlopeRun;
    }

    public short getCaretOffset() {
        return this.caretOffset;
    }

    public void setCaretOffset(short caretOffset) {
        this.caretOffset = caretOffset;
    }

    public short getMetricDataFormat() {
        return this.metricDataFormat;
    }

    public void setMetricDataFormat(short metricDataFormat) {
        this.metricDataFormat = metricDataFormat;
    }

    public int getNumOfLongHorMetrics() {
        return this.numOfLongHorMetrics & 0xFFFF;
    }

    public void setNumOfLongHorMetrics(short numOfLongHorMetrics) {
        this.numOfLongHorMetrics = numOfLongHorMetrics;
    }
}

