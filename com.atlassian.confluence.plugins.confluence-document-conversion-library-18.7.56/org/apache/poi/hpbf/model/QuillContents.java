/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hpbf.model;

import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hpbf.model.EscherPart;
import org.apache.poi.hpbf.model.HPBFPart;
import org.apache.poi.hpbf.model.qcbits.QCBit;
import org.apache.poi.hpbf.model.qcbits.QCPLCBit;
import org.apache.poi.hpbf.model.qcbits.QCTextBit;
import org.apache.poi.hpbf.model.qcbits.UnknownQCBit;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LocaleUtil;

public final class QuillContents
extends HPBFPart {
    private static final Logger LOG = LogManager.getLogger(QuillContents.class);
    private static final String[] PATH = new String[]{"Quill", "QuillSub", "CONTENTS"};
    private final QCBit[] bits;

    public QuillContents(DirectoryNode baseDir) throws IOException {
        super(baseDir, PATH);
        byte[] data = this.getData();
        String f8 = new String(data, 0, 8, LocaleUtil.CHARSET_1252);
        if (!f8.equals("CHNKINK ")) {
            throw new IllegalArgumentException("Expecting 'CHNKINK ' but was '" + f8 + "'");
        }
        this.bits = new QCBit[20];
        for (int i = 0; i < 20; ++i) {
            int offset = 32 + i * 24;
            if (data[offset] != 24 || data[offset + 1] != 0) continue;
            String thingType = new String(data, offset + 2, 4, LocaleUtil.CHARSET_1252);
            int optA = LittleEndian.getUShort(data, offset + 6);
            int optB = LittleEndian.getUShort(data, offset + 8);
            int optC = LittleEndian.getUShort(data, offset + 10);
            String bitType = new String(data, offset + 12, 4, LocaleUtil.CHARSET_1252);
            int from = (int)LittleEndian.getUInt(data, offset + 16);
            int len = (int)LittleEndian.getUInt(data, offset + 20);
            byte[] bitData = IOUtils.safelyClone(data, from, len, EscherPart.getMaxRecordLength());
            if (bitType.equals("TEXT")) {
                this.bits[i] = new QCTextBit(thingType, bitType, bitData);
            } else if (bitType.equals("PLC ")) {
                try {
                    this.bits[i] = QCPLCBit.createQCPLCBit(thingType, bitType, bitData);
                }
                catch (ArrayIndexOutOfBoundsException e) {
                    LOG.atWarn().log("Unable to read Quill Contents PLC Bit record. Ignoring this record.");
                    this.bits[i] = new UnknownQCBit(thingType, bitType, bitData);
                }
            } else {
                this.bits[i] = new UnknownQCBit(thingType, bitType, bitData);
            }
            this.bits[i].setOptA(optA);
            this.bits[i].setOptB(optB);
            this.bits[i].setOptC(optC);
            this.bits[i].setDataOffset(from);
        }
    }

    public QCBit[] getBits() {
        return this.bits;
    }

    @Override
    protected void generateData() {
        throw new IllegalStateException("Not done yet!");
    }
}

