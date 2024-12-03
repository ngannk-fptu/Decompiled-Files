/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hpsf;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import org.apache.poi.hpsf.ClassID;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.NoPropertySetStreamException;
import org.apache.poi.hpsf.PropertySet;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.util.LittleEndianInputStream;

public class PropertySetFactory {
    public static PropertySet create(DirectoryEntry dir, String name) throws FileNotFoundException, NoPropertySetStreamException, IOException, UnsupportedEncodingException {
        try (DocumentInputStream inp = ((DirectoryNode)dir).createDocumentInputStream(name);){
            PropertySet propertySet = PropertySetFactory.create(inp);
            return propertySet;
        }
    }

    public static PropertySet create(InputStream stream) throws NoPropertySetStreamException, IOException {
        stream.mark(45);
        LittleEndianInputStream leis = new LittleEndianInputStream(stream);
        int byteOrder = leis.readUShort();
        int format = leis.readUShort();
        leis.readUInt();
        byte[] clsIdBuf = new byte[16];
        leis.readFully(clsIdBuf);
        int sectionCount = (int)leis.readUInt();
        if (byteOrder != 65534 || format != 0 || sectionCount < 0) {
            throw new NoPropertySetStreamException("ByteOrder: " + byteOrder + ", format: " + format + ", sectionCount: " + sectionCount);
        }
        if (sectionCount > 0) {
            leis.readFully(clsIdBuf);
        }
        stream.reset();
        ClassID clsId = new ClassID(clsIdBuf, 0);
        if (sectionCount > 0 && PropertySet.matchesSummary(clsId, SummaryInformation.FORMAT_ID)) {
            return new SummaryInformation(stream);
        }
        if (sectionCount > 0 && PropertySet.matchesSummary(clsId, DocumentSummaryInformation.FORMAT_ID)) {
            return new DocumentSummaryInformation(stream);
        }
        return new PropertySet(stream);
    }

    public static SummaryInformation newSummaryInformation() {
        return new SummaryInformation();
    }

    public static DocumentSummaryInformation newDocumentSummaryInformation() {
        return new DocumentSummaryInformation();
    }
}

