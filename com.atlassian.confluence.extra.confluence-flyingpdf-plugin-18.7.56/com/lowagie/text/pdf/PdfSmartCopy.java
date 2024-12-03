/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.pdf.BadPdfFormatException;
import com.lowagie.text.pdf.ByteBuffer;
import com.lowagie.text.pdf.PRIndirectReference;
import com.lowagie.text.pdf.PRStream;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PdfSmartCopy
extends PdfCopy {
    private Map<ByteStore, PdfIndirectReference> streamMap = new HashMap<ByteStore, PdfIndirectReference>();

    public PdfSmartCopy(Document document, OutputStream os) throws DocumentException {
        super(document, os);
    }

    @Override
    protected PdfIndirectReference copyIndirect(PRIndirectReference in) throws IOException, BadPdfFormatException {
        PdfObject type;
        PdfIndirectReference theRef;
        PdfCopy.RefKey key;
        PdfCopy.IndirectReferences iRef;
        PdfObject srcObj = PdfReader.getPdfObjectRelease(in);
        ByteStore streamKey = null;
        boolean validStream = false;
        if (srcObj.isStream()) {
            streamKey = new ByteStore((PRStream)srcObj);
            validStream = true;
            PdfIndirectReference streamRef = this.streamMap.get(streamKey);
            if (streamRef != null) {
                return streamRef;
            }
        }
        if ((iRef = (PdfCopy.IndirectReferences)this.indirects.get(key = new PdfCopy.RefKey(in))) != null) {
            theRef = iRef.getRef();
            if (iRef.getCopied()) {
                return theRef;
            }
        } else {
            theRef = this.body.getPdfIndirectReference();
            iRef = new PdfCopy.IndirectReferences(theRef);
            this.indirects.put(key, iRef);
        }
        if (srcObj.isDictionary() && PdfName.PAGE.equals(type = PdfReader.getPdfObjectRelease(((PdfDictionary)srcObj).get(PdfName.TYPE)))) {
            return theRef;
        }
        iRef.setCopied();
        if (validStream) {
            this.streamMap.put(streamKey, theRef);
        }
        PdfObject obj = this.copyObject(srcObj);
        this.addToBody(obj, theRef);
        return theRef;
    }

    static class ByteStore {
        private byte[] b;
        private int hash;
        private MessageDigest md5;

        private void serObject(PdfObject obj, int level, ByteBuffer bb) throws IOException {
            if (level <= 0) {
                return;
            }
            if (obj == null) {
                bb.append("$Lnull");
                return;
            }
            if ((obj = PdfReader.getPdfObject(obj)).isStream()) {
                bb.append("$B");
                this.serDic((PdfDictionary)obj, level - 1, bb);
                this.md5.reset();
                bb.append(this.md5.digest(PdfReader.getStreamBytesRaw((PRStream)obj)));
            } else if (obj.isDictionary()) {
                this.serDic((PdfDictionary)obj, level - 1, bb);
            } else if (obj.isArray()) {
                this.serArray((PdfArray)obj, level - 1, bb);
            } else if (obj.isString()) {
                bb.append("$S").append(obj.toString());
            } else if (obj.isName()) {
                bb.append("$N").append(obj.toString());
            } else {
                bb.append("$L").append(obj.toString());
            }
        }

        private void serDic(PdfDictionary dic, int level, ByteBuffer bb) throws IOException {
            bb.append("$D");
            if (level <= 0) {
                return;
            }
            Object[] keys = dic.getKeys().toArray();
            Arrays.sort(keys);
            for (Object key : keys) {
                this.serObject((PdfObject)key, level, bb);
                this.serObject(dic.get((PdfName)key), level, bb);
            }
        }

        private void serArray(PdfArray array, int level, ByteBuffer bb) throws IOException {
            bb.append("$A");
            if (level <= 0) {
                return;
            }
            for (int k = 0; k < array.size(); ++k) {
                this.serObject(array.getPdfObject(k), level, bb);
            }
        }

        ByteStore(PRStream str) throws IOException {
            try {
                this.md5 = MessageDigest.getInstance("MD5");
            }
            catch (Exception e) {
                throw new ExceptionConverter(e);
            }
            ByteBuffer bb = new ByteBuffer();
            int level = 100;
            this.serObject(str, level, bb);
            this.b = bb.toByteArray();
            this.md5 = null;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof ByteStore)) {
                return false;
            }
            if (this.hashCode() != obj.hashCode()) {
                return false;
            }
            return Arrays.equals(this.b, ((ByteStore)obj).b);
        }

        public int hashCode() {
            if (this.hash == 0) {
                int len = this.b.length;
                for (byte b1 : this.b) {
                    this.hash = this.hash * 31 + (b1 & 0xFF);
                }
            }
            return this.hash;
        }
    }
}

