/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hpbf.extractor;

import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.extractor.POIOLE2TextExtractor;
import org.apache.poi.hpbf.HPBFDocument;
import org.apache.poi.hpbf.model.qcbits.QCBit;
import org.apache.poi.hpbf.model.qcbits.QCPLCBit;
import org.apache.poi.hpbf.model.qcbits.QCTextBit;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public final class PublisherTextExtractor
implements POIOLE2TextExtractor {
    private final HPBFDocument doc;
    private boolean hyperlinksByDefault;
    private boolean doCloseFilesystem = true;

    public PublisherTextExtractor(HPBFDocument doc) {
        this.doc = doc;
    }

    public PublisherTextExtractor(DirectoryNode dir) throws IOException {
        this(new HPBFDocument(dir));
    }

    public PublisherTextExtractor(POIFSFileSystem fs) throws IOException {
        this(new HPBFDocument(fs));
    }

    public PublisherTextExtractor(InputStream is) throws IOException {
        this(new POIFSFileSystem(is));
    }

    public void setHyperlinksByDefault(boolean hyperlinksByDefault) {
        this.hyperlinksByDefault = hyperlinksByDefault;
    }

    @Override
    public String getText() {
        QCBit[] bits;
        StringBuilder text = new StringBuilder();
        for (QCBit bit1 : bits = this.doc.getQuillContents().getBits()) {
            if (!(bit1 instanceof QCTextBit)) continue;
            QCTextBit t = (QCTextBit)bit1;
            text.append(t.getText().replace('\r', '\n'));
        }
        if (this.hyperlinksByDefault) {
            for (QCBit bit : bits) {
                if (!(bit instanceof QCPLCBit.Type12)) continue;
                QCPLCBit.Type12 hyperlinks = (QCPLCBit.Type12)bit;
                for (int j = 0; j < hyperlinks.getNumberOfHyperlinks(); ++j) {
                    text.append("<");
                    text.append(hyperlinks.getHyperlink(j));
                    text.append(">\n");
                }
            }
        }
        return text.toString();
    }

    @Override
    public HPBFDocument getDocument() {
        return this.doc;
    }

    @Override
    public void setCloseFilesystem(boolean doCloseFilesystem) {
        this.doCloseFilesystem = doCloseFilesystem;
    }

    @Override
    public boolean isCloseFilesystem() {
        return this.doCloseFilesystem;
    }

    @Override
    public HPBFDocument getFilesystem() {
        return this.doc;
    }
}

