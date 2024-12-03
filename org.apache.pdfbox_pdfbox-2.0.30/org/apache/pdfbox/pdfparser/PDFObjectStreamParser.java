/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdfparser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdfparser.BaseParser;
import org.apache.pdfbox.pdfparser.InputStreamSource;

public class PDFObjectStreamParser
extends BaseParser {
    private static final Log LOG = LogFactory.getLog(PDFObjectStreamParser.class);
    private List<COSObject> streamObjects = null;
    private final int numberOfObjects;
    private final int firstObject;

    public PDFObjectStreamParser(COSStream stream, COSDocument document) throws IOException {
        super(new InputStreamSource(stream.createInputStream()));
        this.document = document;
        this.numberOfObjects = stream.getInt(COSName.N);
        if (this.numberOfObjects == -1) {
            throw new IOException("/N entry missing in object stream");
        }
        if (this.numberOfObjects < 0) {
            throw new IOException("Illegal /N entry in object stream: " + this.numberOfObjects);
        }
        this.firstObject = stream.getInt(COSName.FIRST);
        if (this.firstObject == -1) {
            throw new IOException("/First entry missing in object stream");
        }
        if (this.firstObject < 0) {
            throw new IOException("Illegal /First entry in object stream: " + this.firstObject);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void parse() throws IOException {
        try {
            Map<Integer, Long> offsets = this.readOffsets();
            this.streamObjects = new ArrayList<COSObject>(offsets.size());
            for (Map.Entry<Integer, Long> offset : offsets.entrySet()) {
                COSBase cosObject = this.parseObject(offset.getKey());
                COSObject object = new COSObject(cosObject);
                object.setGenerationNumber(0);
                object.setObjectNumber(offset.getValue());
                this.streamObjects.add(object);
                if (!LOG.isDebugEnabled()) continue;
                LOG.debug((Object)("parsed=" + object));
            }
        }
        finally {
            this.seqSource.close();
        }
    }

    public List<COSObject> getObjects() {
        return this.streamObjects;
    }

    private Map<Integer, Long> readOffsets() throws IOException {
        TreeMap<Integer, Long> objectNumbers = new TreeMap<Integer, Long>();
        long firstObjectPosition = this.seqSource.getPosition() + (long)this.firstObject - 1L;
        for (int i = 0; i < this.numberOfObjects && this.seqSource.getPosition() < firstObjectPosition; ++i) {
            long objectNumber = this.readObjectNumber();
            int offset = (int)this.readLong();
            objectNumbers.put(offset, objectNumber);
        }
        return objectNumbers;
    }

    private COSBase parseObject(int offset) throws IOException {
        long currentPosition = this.seqSource.getPosition();
        int finalPosition = this.firstObject + offset;
        if (finalPosition > 0 && currentPosition < (long)finalPosition) {
            this.seqSource.readFully(finalPosition - (int)currentPosition);
        }
        return this.parseDirObject();
    }
}

