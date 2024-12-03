/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.font;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import org.apache.pdfbox.util.Charsets;
import org.apache.pdfbox.util.Hex;

final class ToUnicodeWriter {
    private final Map<Integer, String> cidToUnicode = new TreeMap<Integer, String>();
    private int wMode = 0;
    static final int MAX_ENTRIES_PER_OPERATOR = 100;

    ToUnicodeWriter() {
    }

    public void setWMode(int wMode) {
        this.wMode = wMode;
    }

    public void add(int cid, String text) {
        if (cid < 0 || cid > 65535) {
            throw new IllegalArgumentException("CID is not valid");
        }
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Text is null or empty");
        }
        this.cidToUnicode.put(cid, text);
    }

    public void writeTo(OutputStream out) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, Charsets.US_ASCII));
        this.writeLine(writer, "/CIDInit /ProcSet findresource begin");
        this.writeLine(writer, "12 dict begin\n");
        this.writeLine(writer, "begincmap");
        this.writeLine(writer, "/CIDSystemInfo");
        this.writeLine(writer, "<< /Registry (Adobe)");
        this.writeLine(writer, "/Ordering (UCS)");
        this.writeLine(writer, "/Supplement 0");
        this.writeLine(writer, ">> def\n");
        this.writeLine(writer, "/CMapName /Adobe-Identity-UCS def");
        this.writeLine(writer, "/CMapType 2 def\n");
        if (this.wMode != 0) {
            this.writeLine(writer, "/WMode /" + this.wMode + " def");
        }
        this.writeLine(writer, "1 begincodespacerange");
        this.writeLine(writer, "<0000> <FFFF>");
        this.writeLine(writer, "endcodespacerange\n");
        ArrayList<Integer> srcFrom = new ArrayList<Integer>();
        ArrayList<Integer> srcTo = new ArrayList<Integer>();
        ArrayList<String> dstString = new ArrayList<String>();
        Map.Entry<Integer, String> prev = null;
        for (Map.Entry<Integer, String> next : this.cidToUnicode.entrySet()) {
            if (ToUnicodeWriter.allowCIDToUnicodeRange(prev, next)) {
                srcTo.set(srcTo.size() - 1, next.getKey());
            } else {
                srcFrom.add(next.getKey());
                srcTo.add(next.getKey());
                dstString.add(next.getValue());
            }
            prev = next;
        }
        int batchCount = (int)Math.ceil((double)srcFrom.size() / 100.0);
        for (int batch = 0; batch < batchCount; ++batch) {
            int count = batch == batchCount - 1 ? srcFrom.size() - 100 * batch : 100;
            writer.write(count + " beginbfrange\n");
            for (int j = 0; j < count; ++j) {
                int index = batch * 100 + j;
                writer.write(60);
                writer.write(Hex.getChars(((Integer)srcFrom.get(index)).shortValue()));
                writer.write("> ");
                writer.write(60);
                writer.write(Hex.getChars(((Integer)srcTo.get(index)).shortValue()));
                writer.write("> ");
                writer.write(60);
                writer.write(Hex.getCharsUTF16BE((String)dstString.get(index)));
                writer.write(">\n");
            }
            this.writeLine(writer, "endbfrange\n");
        }
        this.writeLine(writer, "endcmap");
        this.writeLine(writer, "CMapName currentdict /CMap defineresource pop");
        this.writeLine(writer, "end");
        this.writeLine(writer, "end");
        writer.flush();
    }

    private void writeLine(BufferedWriter writer, String text) throws IOException {
        writer.write(text);
        writer.write(10);
    }

    static boolean allowCIDToUnicodeRange(Map.Entry<Integer, String> prev, Map.Entry<Integer, String> next) {
        if (prev == null || next == null) {
            return false;
        }
        return ToUnicodeWriter.allowCodeRange(prev.getKey(), next.getKey()) && ToUnicodeWriter.allowDestinationRange(prev.getValue(), next.getValue());
    }

    static boolean allowCodeRange(int prev, int next) {
        if (prev + 1 != next) {
            return false;
        }
        int prevH = prev >> 8 & 0xFF;
        int prevL = prev & 0xFF;
        int nextH = next >> 8 & 0xFF;
        int nextL = next & 0xFF;
        return prevH == nextH && prevL < nextL;
    }

    static boolean allowDestinationRange(String prev, String next) {
        int nextCode;
        if (prev.isEmpty() || next.isEmpty()) {
            return false;
        }
        int prevCode = prev.codePointAt(0);
        return ToUnicodeWriter.allowCodeRange(prevCode, nextCode = next.codePointAt(0)) && prev.codePointCount(0, prev.length()) == 1;
    }
}

