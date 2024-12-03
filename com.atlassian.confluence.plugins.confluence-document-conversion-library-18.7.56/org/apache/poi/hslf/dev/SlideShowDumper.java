/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.dev;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Locale;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.ddf.EscherTextboxRecord;
import org.apache.poi.hslf.record.HSLFEscherRecordFactory;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;

public final class SlideShowDumper {
    private static final int DEFAULT_MAX_RECORD_LENGTH = 100000;
    private static int MAX_RECORD_LENGTH = 100000;
    private byte[] docstream;
    private boolean ddfEscher;
    private boolean basicEscher;
    private PrintStream out;

    public static void setMaxRecordLength(int length) {
        MAX_RECORD_LENGTH = length;
    }

    public static int getMaxRecordLength() {
        return MAX_RECORD_LENGTH;
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("Usage: SlideShowDumper [-escher|-basicescher] <filename>");
            return;
        }
        String filename = args[0];
        if (args.length > 1) {
            filename = args[1];
        }
        try (POIFSFileSystem poifs = new POIFSFileSystem(new File(filename));){
            SlideShowDumper foo = new SlideShowDumper(poifs, System.out);
            if (args.length > 1) {
                if (args[0].equalsIgnoreCase("-escher")) {
                    foo.setDDFEscher(true);
                } else {
                    foo.setBasicEscher(true);
                }
            }
            foo.printDump();
        }
    }

    public SlideShowDumper(POIFSFileSystem filesystem, PrintStream out) throws IOException {
        DocumentInputStream is = filesystem.createDocumentInputStream("PowerPoint Document");
        this.docstream = IOUtils.toByteArray(is);
        ((InputStream)is).close();
        this.out = out;
    }

    public void setDDFEscher(boolean grok) {
        this.ddfEscher = grok;
        this.basicEscher = !grok;
    }

    public void setBasicEscher(boolean grok) {
        this.basicEscher = grok;
        this.ddfEscher = !grok;
    }

    public void printDump() throws IOException {
        this.walkTree(0, 0, this.docstream.length);
    }

    public void walkTree(int depth, int startPos, int maxLen) throws IOException {
        long len;
        String ind;
        int endPos = startPos + maxLen;
        String string = ind = depth == 0 ? "%1$s" : "%1$" + depth + "s";
        for (int pos = startPos; pos <= endPos - 8; pos += (int)len) {
            long type = LittleEndian.getUShort(this.docstream, pos + 2);
            len = LittleEndian.getUInt(this.docstream, pos + 4);
            byte opt = this.docstream[pos];
            String fmt = ind + "At position %2$d (%2$04x): type is %3$d (%3$04x), len is %4$d (%4$04x)";
            this.out.printf(Locale.ROOT, fmt + "%n", "", pos, type, len);
            String recordName = RecordTypes.forTypeID((short)type).name();
            pos += 8;
            this.out.printf(Locale.ROOT, ind + "That's a %2$s%n", "", recordName);
            int container = opt & 0xF;
            if (type == 5003L && (long)opt == 0L) {
                container = 15;
            }
            this.out.println();
            if (type == 0L || container != 15) continue;
            if (type == 1035L || type == 1036L) {
                if (this.ddfEscher) {
                    this.walkEscherDDF(depth + 3, pos + 8, (int)len - 8);
                    continue;
                }
                if (!this.basicEscher) continue;
                this.walkEscherBasic(depth + 3, pos + 8, (int)len - 8);
                continue;
            }
            this.walkTree(depth + 2, pos, (int)len);
        }
    }

    public void walkEscherDDF(int indent, int pos, int len) {
        if (len < 8) {
            return;
        }
        String ind = indent == 0 ? "%1$s" : "%1$" + indent + "s";
        byte[] contents = IOUtils.safelyClone(this.docstream, pos, len, MAX_RECORD_LENGTH);
        HSLFEscherRecordFactory erf = new HSLFEscherRecordFactory();
        EscherRecord record = erf.createRecord(contents, 0);
        record.fillFields(contents, 0, erf);
        long atomType = LittleEndian.getUShort(contents, 2);
        long atomLen = LittleEndian.getUShort(contents, 4);
        int recordLen = record.getRecordSize();
        String fmt = ind + "At position %2$d (%2$04x): type is %3$d (%3$04x), len is %4$d (%4$04x) (%5$d) - record claims %6$d";
        this.out.printf(Locale.ROOT, fmt + "%n", "", pos, atomType, atomLen, atomLen + 8L, recordLen);
        if (recordLen != 8 && (long)recordLen != atomLen + 8L) {
            this.out.printf(Locale.ROOT, ind + "** Atom length of $2d ($3d) doesn't match record length of %4d%n", "", atomLen, atomLen + 8L, recordLen);
        }
        String recordStr = record.toString().replace("\n", String.format(Locale.ROOT, "\n" + ind, ""));
        this.out.printf(Locale.ROOT, ind + "%2$s%n", "", recordStr);
        if (record instanceof EscherContainerRecord) {
            this.walkEscherDDF(indent + 3, pos + 8, (int)atomLen);
        }
        if (atomType == 61451L) {
            recordLen = (int)atomLen + 8;
        }
        if (atomType == 61453L) {
            recordLen = (int)atomLen + 8;
            record.fillFields(contents, 0, erf);
            if (!(record instanceof EscherTextboxRecord)) {
                this.out.printf(Locale.ROOT, ind + "%2$s%n", "", "** Really a msofbtClientTextbox !");
            }
        }
        if (recordLen == 8 && atomLen > 8L) {
            this.walkEscherDDF(indent + 3, pos + 8, (int)atomLen);
        }
        pos = Math.toIntExact((long)pos + atomLen) + 8;
        if ((len = Math.toIntExact((long)len - atomLen) - 8) >= 8) {
            this.walkEscherDDF(indent, pos, len);
        }
    }

    public void walkEscherBasic(int indent, int pos, int len) throws IOException {
        if (len < 8) {
            return;
        }
        String ind = indent == 0 ? "%1$s" : "%1$" + indent + "s";
        long type = LittleEndian.getUShort(this.docstream, pos + 2);
        long atomlen = LittleEndian.getUInt(this.docstream, pos + 4);
        String fmt = ind + "At position %2$d ($2$04x): type is %3$d (%3$04x), len is %4$d (%4$04x)";
        this.out.printf(Locale.ROOT, fmt + "%n", "", pos, type, atomlen);
        String typeName = RecordTypes.forTypeID((short)type).name();
        this.out.printf(Locale.ROOT, ind + "%2$s%n", "That's an Escher Record: ", typeName);
        if (type == 61453L) {
            HexDump.dump(this.docstream, 0L, this.out, pos + 8, 8);
            HexDump.dump(this.docstream, 0L, this.out, pos + 20, 8);
            this.out.println();
        }
        this.out.println();
        if (type == 61443L || type == 61444L) {
            this.walkEscherBasic(indent + 3, pos + 8, (int)atomlen);
        }
        if (atomlen < (long)len) {
            int atomleni = (int)atomlen;
            this.walkEscherBasic(indent, pos + atomleni + 8, len - atomleni - 8);
        }
    }
}

