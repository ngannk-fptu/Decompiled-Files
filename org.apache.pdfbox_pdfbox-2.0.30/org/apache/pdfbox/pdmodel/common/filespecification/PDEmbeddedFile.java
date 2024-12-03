/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.common.filespecification;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDStream;

public class PDEmbeddedFile
extends PDStream {
    public PDEmbeddedFile(PDDocument document) {
        super(document);
        this.getCOSObject().setName(COSName.TYPE, "EmbeddedFile");
    }

    public PDEmbeddedFile(COSStream str) {
        super(str);
    }

    public PDEmbeddedFile(PDDocument doc, InputStream str) throws IOException {
        super(doc, str);
        this.getCOSObject().setName(COSName.TYPE, "EmbeddedFile");
    }

    public PDEmbeddedFile(PDDocument doc, InputStream input, COSName filter) throws IOException {
        super(doc, input, filter);
        this.getCOSObject().setName(COSName.TYPE, "EmbeddedFile");
    }

    public void setSubtype(String mimeType) {
        this.getCOSObject().setName(COSName.SUBTYPE, mimeType);
    }

    public String getSubtype() {
        return this.getCOSObject().getNameAsString(COSName.SUBTYPE);
    }

    public int getSize() {
        return this.getCOSObject().getEmbeddedInt("Params", "Size");
    }

    public void setSize(int size) {
        this.getCOSObject().setEmbeddedInt("Params", "Size", size);
    }

    public Calendar getCreationDate() throws IOException {
        return this.getCOSObject().getEmbeddedDate("Params", "CreationDate");
    }

    public void setCreationDate(Calendar creation) {
        this.getCOSObject().setEmbeddedDate("Params", "CreationDate", creation);
    }

    public Calendar getModDate() throws IOException {
        return this.getCOSObject().getEmbeddedDate("Params", "ModDate");
    }

    public void setModDate(Calendar mod) {
        this.getCOSObject().setEmbeddedDate("Params", "ModDate", mod);
    }

    public String getCheckSum() {
        return this.getCOSObject().getEmbeddedString("Params", "CheckSum");
    }

    public void setCheckSum(String checksum) {
        this.getCOSObject().setEmbeddedString("Params", "CheckSum", checksum);
    }

    public String getMacSubtype() {
        String retval = null;
        COSDictionary params = (COSDictionary)this.getCOSObject().getDictionaryObject(COSName.PARAMS);
        if (params != null) {
            retval = params.getEmbeddedString("Mac", "Subtype");
        }
        return retval;
    }

    public void setMacSubtype(String macSubtype) {
        COSDictionary params = (COSDictionary)this.getCOSObject().getDictionaryObject(COSName.PARAMS);
        if (params == null && macSubtype != null) {
            params = new COSDictionary();
            this.getCOSObject().setItem(COSName.PARAMS, (COSBase)params);
        }
        if (params != null) {
            params.setEmbeddedString("Mac", "Subtype", macSubtype);
        }
    }

    public String getMacCreator() {
        String retval = null;
        COSDictionary params = (COSDictionary)this.getCOSObject().getDictionaryObject(COSName.PARAMS);
        if (params != null) {
            retval = params.getEmbeddedString("Mac", "Creator");
        }
        return retval;
    }

    public void setMacCreator(String macCreator) {
        COSDictionary params = (COSDictionary)this.getCOSObject().getDictionaryObject(COSName.PARAMS);
        if (params == null && macCreator != null) {
            params = new COSDictionary();
            this.getCOSObject().setItem(COSName.PARAMS, (COSBase)params);
        }
        if (params != null) {
            params.setEmbeddedString("Mac", "Creator", macCreator);
        }
    }

    public String getMacResFork() {
        String retval = null;
        COSDictionary params = (COSDictionary)this.getCOSObject().getDictionaryObject(COSName.PARAMS);
        if (params != null) {
            retval = params.getEmbeddedString("Mac", "ResFork");
        }
        return retval;
    }

    public void setMacResFork(String macResFork) {
        COSDictionary params = (COSDictionary)this.getCOSObject().getDictionaryObject(COSName.PARAMS);
        if (params == null && macResFork != null) {
            params = new COSDictionary();
            this.getCOSObject().setItem(COSName.PARAMS, (COSBase)params);
        }
        if (params != null) {
            params.setEmbeddedString("Mac", "ResFork", macResFork);
        }
    }
}

