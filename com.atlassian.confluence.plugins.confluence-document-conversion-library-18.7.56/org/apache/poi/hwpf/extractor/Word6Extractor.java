/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.extractor;

import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.extractor.POIOLE2TextExtractor;
import org.apache.poi.hwpf.HWPFOldDocument;
import org.apache.poi.hwpf.converter.WordToTextConverter;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public final class Word6Extractor
implements POIOLE2TextExtractor {
    private final HWPFOldDocument doc;
    private boolean doCloseFilesystem = true;

    public Word6Extractor(InputStream is) throws IOException {
        this(new POIFSFileSystem(is));
    }

    public Word6Extractor(POIFSFileSystem fs) throws IOException {
        this(fs.getRoot());
    }

    @Deprecated
    public Word6Extractor(DirectoryNode dir, POIFSFileSystem fs) throws IOException {
        this(dir);
    }

    public Word6Extractor(DirectoryNode dir) throws IOException {
        this(new HWPFOldDocument(dir));
    }

    public Word6Extractor(HWPFOldDocument doc) {
        this.doc = doc;
    }

    @Deprecated
    public String[] getParagraphText() {
        String[] ret;
        try {
            Range r = this.doc.getRange();
            ret = WordExtractor.getParagraphText(r);
        }
        catch (Exception e) {
            ret = new String[this.doc.getTextTable().getTextPieces().size()];
            for (int i = 0; i < ret.length; ++i) {
                ret[i] = this.doc.getTextTable().getTextPieces().get(i).getStringBuilder().toString();
                ret[i] = ret[i].replace("\r", "\ufffe");
                ret[i] = ret[i].replace("\ufffe", "\r\n");
            }
        }
        return ret;
    }

    @Override
    public String getText() {
        try {
            WordToTextConverter wordToTextConverter = new WordToTextConverter();
            wordToTextConverter.processDocument(this.doc);
            return wordToTextConverter.getText();
        }
        catch (Exception exc) {
            StringBuilder text = new StringBuilder();
            for (String t : this.getParagraphText()) {
                text.append(t);
            }
            return text.toString();
        }
    }

    @Override
    public HWPFOldDocument getDocument() {
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
    public HWPFOldDocument getFilesystem() {
        return this.doc;
    }
}

