/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.extractor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hslf.record.CString;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.hslf.record.TextBytesAtom;
import org.apache.poi.hslf.record.TextCharsAtom;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;

public final class QuickButCruddyTextExtractor {
    private POIFSFileSystem fs;
    private InputStream is;
    private final byte[] pptContents;

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Usage:");
            System.err.println("\tQuickButCruddyTextExtractor <file>");
            System.exit(1);
        }
        String file = args[0];
        QuickButCruddyTextExtractor ppe = new QuickButCruddyTextExtractor(file);
        System.out.println(ppe.getTextAsString());
        ppe.close();
    }

    public QuickButCruddyTextExtractor(String fileName) throws IOException {
        this(new POIFSFileSystem(new File(fileName)));
    }

    public QuickButCruddyTextExtractor(InputStream iStream) throws IOException {
        this(new POIFSFileSystem(iStream));
        this.is = iStream;
    }

    public QuickButCruddyTextExtractor(POIFSFileSystem poifs) throws IOException {
        this.fs = poifs;
        DocumentInputStream pptIs = this.fs.createDocumentInputStream("PowerPoint Document");
        this.pptContents = IOUtils.toByteArray(pptIs);
        ((InputStream)pptIs).close();
    }

    public void close() throws IOException {
        if (this.is != null) {
            this.is.close();
        }
        this.fs = null;
    }

    public String getTextAsString() {
        StringBuilder ret = new StringBuilder();
        List<String> textV = this.getTextAsVector();
        for (String text : textV) {
            ret.append(text);
            if (text.endsWith("\n")) continue;
            ret.append('\n');
        }
        return ret.toString();
    }

    public List<String> getTextAsVector() {
        ArrayList<String> textV = new ArrayList<String>();
        int walkPos = 0;
        while (walkPos != -1) {
            walkPos = this.findTextRecords(walkPos, textV);
        }
        return textV;
    }

    public int findTextRecords(int startPos, List<String> textV) {
        int newPos;
        CString cs;
        String text;
        int len = (int)LittleEndian.getUInt(this.pptContents, startPos + 4);
        byte opt = this.pptContents[startPos];
        int container = opt & 0xF;
        if (container == 15) {
            return startPos + 8;
        }
        int type = LittleEndian.getUShort(this.pptContents, startPos + 2);
        if (type == RecordTypes.TextBytesAtom.typeID) {
            TextBytesAtom tba = (TextBytesAtom)Record.createRecordForType(type, this.pptContents, startPos, len + 8);
            text = HSLFTextParagraph.toExternalString(tba.getText(), -1);
            textV.add(text);
        }
        if (type == RecordTypes.TextCharsAtom.typeID) {
            TextCharsAtom tca = (TextCharsAtom)Record.createRecordForType(type, this.pptContents, startPos, len + 8);
            text = HSLFTextParagraph.toExternalString(tca.getText(), -1);
            textV.add(text);
        }
        if (type == RecordTypes.CString.typeID && !"___PPT10".equals(text = (cs = (CString)Record.createRecordForType(type, this.pptContents, startPos, len + 8)).getText()) && !"Default Design".equals(text)) {
            textV.add(text);
        }
        if ((newPos = startPos + 8 + len) > this.pptContents.length - 8) {
            newPos = -1;
        }
        return newPos;
    }
}

