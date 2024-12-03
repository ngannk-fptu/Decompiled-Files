/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.output.StringBuilderWriter
 */
package org.apache.poi.hslf.dev;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.LittleEndian;

public final class PPTXMLDump {
    private static final int HEADER_SIZE = 8;
    private static final int PICT_HEADER_SIZE = 25;
    private static final String PICTURES_ENTRY = "Pictures";
    private static final String CR = System.getProperty("line.separator");
    private Writer out;
    private final byte[] docstream;
    private final byte[] pictstream;
    private final boolean hexHeader = true;
    private static final byte[] hexval = new byte[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70};

    public PPTXMLDump(File ppt) throws IOException {
        try (POIFSFileSystem fs = new POIFSFileSystem(ppt, true);){
            this.docstream = PPTXMLDump.readEntry(fs, "PowerPoint Document");
            this.pictstream = PPTXMLDump.readEntry(fs, PICTURES_ENTRY);
        }
    }

    /*
     * Exception decompiling
     */
    private static byte[] readEntry(POIFSFileSystem fs, String entry) throws IOException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    public void dump(Writer outWriter) throws IOException {
        this.out = outWriter;
        int padding = 0;
        PPTXMLDump.write(this.out, "<Presentation>" + CR, padding);
        ++padding;
        if (this.pictstream != null) {
            PPTXMLDump.write(this.out, "<Pictures>" + CR, padding);
            this.dumpPictures(this.pictstream, padding);
            PPTXMLDump.write(this.out, "</Pictures>" + CR, padding);
        }
        PPTXMLDump.write(this.out, "<PowerPointDocument>" + CR, padding);
        ++padding;
        if (this.docstream != null) {
            this.dump(this.docstream, 0, this.docstream.length, padding);
        }
        PPTXMLDump.write(this.out, "</PowerPointDocument>" + CR, --padding);
        PPTXMLDump.write(this.out, "</Presentation>", --padding);
    }

    public void dump(byte[] data, int offset, int length, int padding) throws IOException {
        int size;
        for (int pos = offset; pos <= offset + length - 8 && pos >= 0; pos += size) {
            boolean isContainer;
            int info = LittleEndian.getUShort(data, pos);
            int type = LittleEndian.getUShort(data, pos += 2);
            size = (int)LittleEndian.getUInt(data, pos += 2);
            String recname = RecordTypes.forTypeID(type).name();
            PPTXMLDump.write(this.out, "<" + recname + " info=\"" + info + "\" type=\"" + type + "\" size=\"" + size + "\" offset=\"" + ((pos += 4) - 8) + "\"", padding);
            this.out.write(" header=\"");
            PPTXMLDump.dump(this.out, data, pos - 8, 8, 0, false);
            this.out.write("\"");
            this.out.write(">" + CR);
            ++padding;
            boolean bl = isContainer = (info & 0xF) == 15;
            if (isContainer) {
                this.dump(data, pos, size, padding);
            } else {
                PPTXMLDump.dump(this.out, data, pos, Math.min(size, data.length - pos), padding, true);
            }
            PPTXMLDump.write(this.out, "</" + recname + ">" + CR, --padding);
        }
    }

    public void dumpPictures(byte[] data, int padding) throws IOException {
        int pos = 0;
        while (pos < data.length) {
            if (data.length - pos < 25) {
                return;
            }
            byte[] header = Arrays.copyOfRange(data, pos, pos + 25);
            int size = LittleEndian.getInt(header, 4) - 17;
            if (size < 0) {
                return;
            }
            pos += 25 + size;
            PPTXMLDump.write(this.out, "<picture size=\"" + size + "\" type=\"" + this.getPictureType(header) + "\">" + CR, ++padding);
            PPTXMLDump.write(this.out, "<header>" + CR, ++padding);
            PPTXMLDump.dump(this.out, header, 0, header.length, padding, true);
            PPTXMLDump.write(this.out, "</header>" + CR, padding);
            PPTXMLDump.write(this.out, "<imgdata>" + CR, padding);
            PPTXMLDump.dump(this.out, data, 0, Math.min(size, 100), padding, true);
            PPTXMLDump.write(this.out, "</imgdata>" + CR, padding);
            PPTXMLDump.write(this.out, "</picture>" + CR, --padding);
            --padding;
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Usage: PPTXMLDump (options) pptfile\nWhere options include:\n    -f     write output to <pptfile>.xml file in the current directory");
            return;
        }
        boolean outFile = false;
        for (String arg : args) {
            if (arg.startsWith("-")) {
                if (!"-f".equals(arg)) continue;
                outFile = true;
                continue;
            }
            File ppt = new File(arg);
            PPTXMLDump dump = new PPTXMLDump(ppt);
            System.out.println("Dumping " + arg);
            if (outFile) {
                FileOutputStream fos = new FileOutputStream(ppt.getName() + ".xml");
                OutputStreamWriter out = new OutputStreamWriter((OutputStream)fos, StandardCharsets.UTF_8);
                dump.dump(out);
                out.close();
                continue;
            }
            StringBuilderWriter out = new StringBuilderWriter(1024);
            dump.dump((Writer)out);
            System.out.println(out);
        }
    }

    private static void write(Writer out, String str, int padding) throws IOException {
        for (int i = 0; i < padding; ++i) {
            out.write("  ");
        }
        out.write(str);
    }

    private String getPictureType(byte[] header) {
        String type;
        int meta = LittleEndian.getUShort(header, 0);
        switch (meta) {
            case 18080: {
                type = "jpeg";
                break;
            }
            case 8544: {
                type = "wmf";
                break;
            }
            case 28160: {
                type = "png";
                break;
            }
            default: {
                type = "unknown";
            }
        }
        return type;
    }

    private static void dump(Writer out, byte[] data, int offset, int length, int padding, boolean nl) throws IOException {
        int i;
        int linesize = 25;
        for (i = 0; i < padding; ++i) {
            out.write("  ");
        }
        for (i = offset; i < offset + length; ++i) {
            byte c = data[i];
            out.write((char)hexval[(c & 0xF0) >> 4]);
            out.write((char)hexval[(c & 0xF) >> 0]);
            out.write(32);
            if ((i + 1 - offset) % linesize != 0 || i == offset + length - 1) continue;
            out.write(CR);
            for (int j = 0; j < padding; ++j) {
                out.write("  ");
            }
        }
        if (nl && length > 0) {
            out.write(CR);
        }
    }
}

