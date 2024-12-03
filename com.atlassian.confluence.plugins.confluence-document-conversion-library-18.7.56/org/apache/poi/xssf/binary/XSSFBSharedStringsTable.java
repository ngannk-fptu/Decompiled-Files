/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xssf.binary;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.xssf.binary.XSSFBParseException;
import org.apache.poi.xssf.binary.XSSFBParser;
import org.apache.poi.xssf.binary.XSSFBRecordType;
import org.apache.poi.xssf.binary.XSSFBRelation;
import org.apache.poi.xssf.binary.XSSFBRichStr;
import org.apache.poi.xssf.binary.XSSFBUtils;
import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.SAXException;

@Internal
public class XSSFBSharedStringsTable
implements SharedStrings {
    private int count;
    private int uniqueCount;
    private List<String> strings = new ArrayList<String>();

    public XSSFBSharedStringsTable(OPCPackage pkg) throws IOException, SAXException {
        ArrayList<PackagePart> parts = pkg.getPartsByContentType(XSSFBRelation.SHARED_STRINGS_BINARY.getContentType());
        if (!parts.isEmpty()) {
            PackagePart sstPart = parts.get(0);
            try (InputStream stream = sstPart.getInputStream();){
                this.readFrom(stream);
            }
        }
    }

    XSSFBSharedStringsTable(PackagePart part) throws IOException, SAXException {
        try (InputStream stream = part.getInputStream();){
            this.readFrom(stream);
        }
    }

    private void readFrom(InputStream inputStream) throws IOException {
        SSTBinaryReader reader = new SSTBinaryReader(inputStream);
        reader.parse();
    }

    @Override
    public RichTextString getItemAt(int idx) {
        return new XSSFRichTextString(this.strings.get(idx));
    }

    @Override
    public int getCount() {
        return this.count;
    }

    @Override
    public int getUniqueCount() {
        return this.uniqueCount;
    }

    private class SSTBinaryReader
    extends XSSFBParser {
        SSTBinaryReader(InputStream is) {
            super(is);
        }

        @Override
        public void handleRecord(int recordType, byte[] data) throws XSSFBParseException {
            XSSFBRecordType type = XSSFBRecordType.lookup(recordType);
            switch (type) {
                case BrtSstItem: {
                    XSSFBRichStr rstr = XSSFBRichStr.build(data, 0);
                    XSSFBSharedStringsTable.this.strings.add(rstr.getString());
                    break;
                }
                case BrtBeginSst: {
                    XSSFBSharedStringsTable.this.count = XSSFBUtils.castToInt(LittleEndian.getUInt(data, 0));
                    XSSFBSharedStringsTable.this.uniqueCount = XSSFBUtils.castToInt(LittleEndian.getUInt(data, 4));
                }
            }
        }
    }
}

