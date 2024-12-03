/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hpbf.dev;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.ddf.DefaultEscherRecordFactory;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LocaleUtil;
import org.apache.poi.util.StringUtil;

public final class HPBFDumper {
    private POIFSFileSystem fs;

    public HPBFDumper(POIFSFileSystem fs) {
        this.fs = fs;
    }

    public HPBFDumper(InputStream inp) throws IOException {
        this(new POIFSFileSystem(inp));
    }

    private static byte[] getData(DirectoryNode dir, String name) throws IOException {
        DocumentInputStream is = dir.createDocumentInputStream(name);
        byte[] d = IOUtils.toByteArray(is);
        ((InputStream)is).close();
        return d;
    }

    private String dumpBytes(byte[] data, int offset, int len) {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < len; ++i) {
            String bs;
            int j = i + offset;
            int b = data[j];
            if (b < 0) {
                b += 256;
            }
            if ((bs = Integer.toHexString(b)).length() == 1) {
                ret.append('0');
            }
            ret.append(bs);
            ret.append(' ');
        }
        return ret.toString();
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Use:");
            System.err.println("  HPBFDumper <filename>");
            System.exit(1);
        }
        HPBFDumper dump = new HPBFDumper(new POIFSFileSystem(new File(args[0])));
        System.out.println("Dumping " + args[0]);
        dump.dumpContents();
        dump.dumpEnvelope();
        dump.dumpEscher();
        dump.dump001CompObj(dump.fs.getRoot());
        dump.dumpQuill();
    }

    public void dumpEscher() throws IOException {
        DirectoryNode escherDir = (DirectoryNode)this.fs.getRoot().getEntry("Escher");
        this.dumpEscherStm(escherDir);
        this.dumpEscherDelayStm(escherDir);
    }

    private void dumpEscherStream(byte[] data) {
        EscherRecord er;
        DefaultEscherRecordFactory erf = new DefaultEscherRecordFactory();
        for (int left = data.length; left > 0; left -= er.getRecordSize()) {
            er = erf.createRecord(data, 0);
            er.fillFields(data, 0, erf);
            System.out.println(er);
        }
    }

    protected void dumpEscherStm(DirectoryNode escherDir) throws IOException {
        byte[] data = HPBFDumper.getData(escherDir, "EscherStm");
        System.out.println();
        System.out.println("EscherStm - " + data.length + " bytes long:");
        if (data.length > 0) {
            this.dumpEscherStream(data);
        }
    }

    protected void dumpEscherDelayStm(DirectoryNode escherDir) throws IOException {
        byte[] data = HPBFDumper.getData(escherDir, "EscherDelayStm");
        System.out.println();
        System.out.println("EscherDelayStm - " + data.length + " bytes long:");
        if (data.length > 0) {
            this.dumpEscherStream(data);
        }
    }

    public void dumpEnvelope() throws IOException {
        byte[] data = HPBFDumper.getData(this.fs.getRoot(), "Envelope");
        System.out.println();
        System.out.println("Envelope - " + data.length + " bytes long:");
    }

    public void dumpContents() throws IOException {
        byte[] data = HPBFDumper.getData(this.fs.getRoot(), "Contents");
        System.out.println();
        System.out.println("Contents - " + data.length + " bytes long:");
    }

    public void dumpCONTENTSraw(DirectoryNode dir) throws IOException {
        int blen;
        byte[] data = HPBFDumper.getData(dir, "CONTENTS");
        System.out.println();
        System.out.println("CONTENTS - " + data.length + " bytes long:");
        System.out.println(new String(data, 0, 8, LocaleUtil.CHARSET_1252) + this.dumpBytes(data, 8, 24));
        boolean sixNotEight = true;
        for (int pos = 32; pos < 512; pos += 4 + blen) {
            if (sixNotEight) {
                System.out.println(this.dumpBytes(data, pos, 2));
                pos += 2;
            }
            String text = new String(data, pos, 4, LocaleUtil.CHARSET_1252);
            blen = 8;
            if (sixNotEight) {
                blen = 6;
            }
            System.out.println(text + " " + this.dumpBytes(data, pos + 4, blen));
            sixNotEight = !sixNotEight;
        }
        int textStop = -1;
        for (int i = 512; i < data.length - 2 && textStop == -1; ++i) {
            if (data[i] != 0 || data[i + 1] != 0 || data[i + 2] != 0) continue;
            textStop = i;
        }
        if (textStop > 0) {
            int len = (textStop - 512) / 2;
            System.out.println();
            System.out.println(StringUtil.getFromUnicodeLE(data, 512, len));
        }
    }

    public void dumpCONTENTSguessed(DirectoryNode dir) throws IOException {
        int i;
        byte[] data = HPBFDumper.getData(dir, "CONTENTS");
        System.out.println();
        System.out.println("CONTENTS - " + data.length + " bytes long:");
        String[] startType = new String[20];
        String[] endType = new String[20];
        int[] optA = new int[20];
        int[] optB = new int[20];
        int[] optC = new int[20];
        int[] from = new int[20];
        int[] len = new int[20];
        for (int i2 = 0; i2 < 20; ++i2) {
            int offset = 32 + i2 * 24;
            if (data[offset] != 24 || data[offset + 1] != 0) continue;
            startType[i2] = new String(data, offset + 2, 4, LocaleUtil.CHARSET_1252);
            optA[i2] = LittleEndian.getUShort(data, offset + 6);
            optB[i2] = LittleEndian.getUShort(data, offset + 8);
            optC[i2] = LittleEndian.getUShort(data, offset + 10);
            endType[i2] = new String(data, offset + 12, 4, LocaleUtil.CHARSET_1252);
            from[i2] = (int)LittleEndian.getUInt(data, offset + 16);
            len[i2] = (int)LittleEndian.getUInt(data, offset + 20);
        }
        String text = StringUtil.getFromUnicodeLE(data, from[0], len[0] / 2);
        for (i = 0; i < 20; ++i) {
            String num = Integer.toString(i);
            if (i < 10) {
                num = "0" + i;
            }
            System.out.print(num + " ");
            if (startType[i] == null) {
                System.out.println("(not present)");
                continue;
            }
            System.out.println("\t" + startType[i] + " " + optA[i] + " " + optB[i] + " " + optC[i]);
            System.out.println("\t" + endType[i] + " from: " + Integer.toHexString(from[i]) + " (" + from[i] + "), len: " + Integer.toHexString(len[i]) + " (" + len[i] + ")");
        }
        System.out.println();
        System.out.println("TEXT:");
        System.out.println(text);
        System.out.println();
        for (i = 0; i < 20; ++i) {
            if (startType[i] == null) continue;
            int start = from[i];
            System.out.println(startType[i] + " -> " + endType[i] + " @ " + Integer.toHexString(start) + " (" + start + ")");
            System.out.println("\t" + this.dumpBytes(data, start, 4));
            System.out.println("\t" + this.dumpBytes(data, start + 4, 4));
            System.out.println("\t" + this.dumpBytes(data, start + 8, 4));
            System.out.println("\t(etc)");
        }
    }

    protected void dump001CompObj(DirectoryNode dir) {
    }

    public void dumpQuill() throws IOException {
        DirectoryNode quillDir = (DirectoryNode)this.fs.getRoot().getEntry("Quill");
        DirectoryNode quillSubDir = (DirectoryNode)quillDir.getEntry("QuillSub");
        this.dump001CompObj(quillSubDir);
        this.dumpCONTENTSraw(quillSubDir);
        this.dumpCONTENTSguessed(quillSubDir);
    }
}

