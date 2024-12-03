/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.font.ttf;

import com.sun.pdfview.font.ttf.TrueTypeTable;
import java.nio.ByteBuffer;

public class MaxpTable
extends TrueTypeTable {
    private int version;
    private int numGlyphs;
    private int maxPoints;
    private int maxContours;
    private int maxComponentPoints;
    private int maxComponentContours;
    private int maxZones;
    private int maxTwilightPoints;
    private int maxStorage;
    private int maxFunctionDefs;
    private int maxInstructionDefs;
    private int maxStackElements;
    private int maxSizeOfInstructions;
    private int maxComponentElements;
    private int maxComponentDepth;

    protected MaxpTable() {
        super(1835104368);
        this.setVersion(65536);
        this.setNumGlyphs(0);
        this.setMaxPoints(0);
        this.setMaxContours(0);
        this.setMaxComponentPoints(0);
        this.setMaxComponentContours(0);
        this.setMaxZones(2);
        this.setMaxTwilightPoints(0);
        this.setMaxStorage(0);
        this.setMaxFunctionDefs(0);
        this.setMaxInstructionDefs(0);
        this.setMaxStackElements(0);
        this.setMaxSizeOfInstructions(0);
        this.setMaxComponentElements(0);
        this.setMaxComponentDepth(0);
    }

    @Override
    public void setData(ByteBuffer data) {
        if (data.remaining() != 32) {
            throw new IllegalArgumentException("Bad size for Maxp table");
        }
        this.setVersion(data.getInt());
        this.setNumGlyphs(data.getShort());
        this.setMaxPoints(data.getShort());
        this.setMaxContours(data.getShort());
        this.setMaxComponentPoints(data.getShort());
        this.setMaxComponentContours(data.getShort());
        this.setMaxZones(data.getShort());
        this.setMaxTwilightPoints(data.getShort());
        this.setMaxStorage(data.getShort());
        this.setMaxFunctionDefs(data.getShort());
        this.setMaxInstructionDefs(data.getShort());
        this.setMaxStackElements(data.getShort());
        this.setMaxSizeOfInstructions(data.getShort());
        this.setMaxComponentElements(data.getShort());
        this.setMaxComponentDepth(data.getShort());
    }

    @Override
    public ByteBuffer getData() {
        ByteBuffer buf = ByteBuffer.allocate(this.getLength());
        buf.putInt(this.getVersion());
        buf.putShort((short)this.getNumGlyphs());
        buf.putShort((short)this.getMaxPoints());
        buf.putShort((short)this.getMaxContours());
        buf.putShort((short)this.getMaxComponentPoints());
        buf.putShort((short)this.getMaxComponentContours());
        buf.putShort((short)this.getMaxZones());
        buf.putShort((short)this.getMaxTwilightPoints());
        buf.putShort((short)this.getMaxStorage());
        buf.putShort((short)this.getMaxFunctionDefs());
        buf.putShort((short)this.getMaxInstructionDefs());
        buf.putShort((short)this.getMaxStackElements());
        buf.putShort((short)this.getMaxSizeOfInstructions());
        buf.putShort((short)this.getMaxComponentElements());
        buf.putShort((short)this.getMaxComponentDepth());
        buf.flip();
        return buf;
    }

    @Override
    public int getLength() {
        return 32;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getNumGlyphs() {
        return this.numGlyphs & 0xFFFF;
    }

    public void setNumGlyphs(int numGlyphs) {
        this.numGlyphs = numGlyphs;
    }

    public int getMaxPoints() {
        return this.maxPoints & 0xFFFF;
    }

    public void setMaxPoints(int maxPoints) {
        this.maxPoints = maxPoints;
    }

    public int getMaxContours() {
        return this.maxContours & 0xFFFF;
    }

    public void setMaxContours(int maxContours) {
        this.maxContours = maxContours;
    }

    public int getMaxComponentPoints() {
        return this.maxComponentPoints & 0xFFFF;
    }

    public void setMaxComponentPoints(int maxComponentPoints) {
        this.maxComponentPoints = maxComponentPoints;
    }

    public int getMaxComponentContours() {
        return this.maxComponentContours & 0xFFFF;
    }

    public void setMaxComponentContours(int maxComponentContours) {
        this.maxComponentContours = maxComponentContours;
    }

    public int getMaxZones() {
        return this.maxZones & 0xFFFF;
    }

    public void setMaxZones(int maxZones) {
        this.maxZones = maxZones;
    }

    public int getMaxTwilightPoints() {
        return this.maxTwilightPoints & 0xFFFF;
    }

    public void setMaxTwilightPoints(int maxTwilightPoints) {
        this.maxTwilightPoints = maxTwilightPoints;
    }

    public int getMaxStorage() {
        return this.maxStorage & 0xFFFF;
    }

    public void setMaxStorage(int maxStorage) {
        this.maxStorage = maxStorage;
    }

    public int getMaxFunctionDefs() {
        return this.maxFunctionDefs & 0xFFFF;
    }

    public void setMaxFunctionDefs(int maxFunctionDefs) {
        this.maxFunctionDefs = maxFunctionDefs;
    }

    public int getMaxInstructionDefs() {
        return this.maxInstructionDefs & 0xFFFF;
    }

    public void setMaxInstructionDefs(int maxInstructionDefs) {
        this.maxInstructionDefs = maxInstructionDefs;
    }

    public int getMaxStackElements() {
        return this.maxStackElements & 0xFFFF;
    }

    public void setMaxStackElements(int maxStackElements) {
        this.maxStackElements = maxStackElements;
    }

    public int getMaxSizeOfInstructions() {
        return this.maxSizeOfInstructions & 0xFFFF;
    }

    public void setMaxSizeOfInstructions(int maxSizeOfInstructions) {
        this.maxSizeOfInstructions = maxSizeOfInstructions;
    }

    public int getMaxComponentElements() {
        return this.maxComponentElements & 0xFFFF;
    }

    public void setMaxComponentElements(int maxComponentElements) {
        this.maxComponentElements = maxComponentElements;
    }

    public int getMaxComponentDepth() {
        return this.maxComponentDepth & 0xFFFF;
    }

    public void setMaxComponentDepth(int maxComponentDepth) {
        this.maxComponentDepth = maxComponentDepth;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        String indent = "    ";
        buf.append(indent + "Version          : " + Integer.toHexString(this.getVersion()) + "\n");
        buf.append(indent + "NumGlyphs        : " + this.getNumGlyphs() + "\n");
        buf.append(indent + "MaxPoints        : " + this.getMaxPoints() + "\n");
        buf.append(indent + "MaxContours      : " + this.getMaxContours() + "\n");
        buf.append(indent + "MaxCompPoints    : " + this.getMaxComponentPoints() + "\n");
        buf.append(indent + "MaxCompContours  : " + this.getMaxComponentContours() + "\n");
        buf.append(indent + "MaxZones         : " + this.getMaxZones() + "\n");
        buf.append(indent + "MaxTwilightPoints: " + this.getMaxTwilightPoints() + "\n");
        buf.append(indent + "MaxStorage       : " + this.getMaxStorage() + "\n");
        buf.append(indent + "MaxFuncDefs      : " + this.getMaxFunctionDefs() + "\n");
        buf.append(indent + "MaxInstDefs      : " + this.getMaxInstructionDefs() + "\n");
        buf.append(indent + "MaxStackElements : " + this.getMaxStackElements() + "\n");
        buf.append(indent + "MaxSizeInst      : " + this.getMaxSizeOfInstructions() + "\n");
        buf.append(indent + "MaxCompElements  : " + this.getMaxComponentElements() + "\n");
        buf.append(indent + "MaxCompDepth     : " + this.getMaxComponentDepth() + "\n");
        return buf.toString();
    }
}

