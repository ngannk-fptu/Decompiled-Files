/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.common;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDStream;

public class PDObjectStream
extends PDStream {
    public PDObjectStream(COSStream str) {
        super(str);
    }

    public static PDObjectStream createStream(PDDocument document) {
        COSStream cosStream = document.getDocument().createCOSStream();
        PDObjectStream strm = new PDObjectStream(cosStream);
        strm.getCOSObject().setItem(COSName.TYPE, (COSBase)COSName.OBJ_STM);
        return strm;
    }

    public String getType() {
        return this.getCOSObject().getNameAsString(COSName.TYPE);
    }

    public int getNumberOfObjects() {
        return this.getCOSObject().getInt(COSName.N, 0);
    }

    public void setNumberOfObjects(int n) {
        this.getCOSObject().setInt(COSName.N, n);
    }

    public int getFirstByteOffset() {
        return this.getCOSObject().getInt(COSName.FIRST, 0);
    }

    public void setFirstByteOffset(int n) {
        this.getCOSObject().setInt(COSName.FIRST, n);
    }

    public PDObjectStream getExtends() {
        PDObjectStream retval = null;
        COSStream stream = (COSStream)this.getCOSObject().getDictionaryObject(COSName.EXTENDS);
        if (stream != null) {
            retval = new PDObjectStream(stream);
        }
        return retval;
    }

    public void setExtends(PDObjectStream stream) {
        this.getCOSObject().setItem(COSName.EXTENDS, (COSObjectable)stream);
    }
}

