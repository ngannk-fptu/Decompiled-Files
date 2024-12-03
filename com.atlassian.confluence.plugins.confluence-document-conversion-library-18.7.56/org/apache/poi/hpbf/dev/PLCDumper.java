/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hpbf.dev;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.hpbf.HPBFDocument;
import org.apache.poi.hpbf.model.QuillContents;
import org.apache.poi.hpbf.model.qcbits.QCBit;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.HexDump;

public final class PLCDumper {
    private HPBFDocument doc;
    private QuillContents qc;

    public PLCDumper(HPBFDocument hpbfDoc) {
        this.doc = hpbfDoc;
        this.qc = this.doc.getQuillContents();
    }

    public PLCDumper(POIFSFileSystem fs) throws IOException {
        this(new HPBFDocument(fs));
    }

    public PLCDumper(InputStream inp) throws IOException {
        this(new POIFSFileSystem(inp));
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Use:");
            System.err.println("  PLCDumper <filename>");
            System.exit(1);
        }
        try (FileInputStream fis = new FileInputStream(args[0]);){
            PLCDumper dump = new PLCDumper(fis);
            System.out.println("Dumping " + args[0]);
            dump.dumpPLC();
        }
    }

    private void dumpPLC() {
        QCBit[] bits = this.qc.getBits();
        for (int i = 0; i < bits.length; ++i) {
            if (bits[i] == null || !bits[i].getBitType().equals("PLC ")) continue;
            this.dumpBit(bits[i], i);
        }
    }

    private void dumpBit(QCBit bit, int index) {
        System.out.println();
        System.out.println("Dumping " + bit.getBitType() + " bit at " + index);
        System.out.println("  Is a " + bit.getThingType() + ", number is " + bit.getOptA());
        System.out.println("  Starts at " + bit.getDataOffset() + " (0x" + Integer.toHexString(bit.getDataOffset()) + ")");
        System.out.println("  Runs for  " + bit.getLength() + " (0x" + Integer.toHexString(bit.getLength()) + ")");
        System.out.println(HexDump.dump(bit.getData(), 0L, 0));
    }
}

