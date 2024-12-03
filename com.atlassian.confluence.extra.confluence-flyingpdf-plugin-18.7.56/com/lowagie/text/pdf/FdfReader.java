/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfString;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class FdfReader
extends PdfReader {
    private Map<String, PdfDictionary> fields;
    private String fileSpec;
    PdfName encoding;

    public FdfReader(String filename) throws IOException {
        super(filename);
    }

    public FdfReader(byte[] pdfIn) throws IOException {
        super(pdfIn);
    }

    public FdfReader(URL url) throws IOException {
        super(url);
    }

    public FdfReader(InputStream is) throws IOException {
        super(is);
    }

    @Override
    protected void readPdf() throws IOException {
        this.fields = new HashMap<String, PdfDictionary>();
        try {
            this.tokens.checkFdfHeader();
            this.rebuildXref();
            this.readDocObj();
        }
        finally {
            try {
                this.tokens.close();
            }
            catch (Exception exception) {}
        }
        this.readFields();
    }

    protected void kidNode(PdfDictionary merged, String name) {
        PdfArray kids = merged.getAsArray(PdfName.KIDS);
        if (kids == null || kids.isEmpty()) {
            if (name.length() > 0) {
                name = name.substring(1);
            }
            this.fields.put(name, merged);
        } else {
            merged.remove(PdfName.KIDS);
            for (int k = 0; k < kids.size(); ++k) {
                PdfDictionary dic = new PdfDictionary();
                dic.merge(merged);
                PdfDictionary newDic = kids.getAsDict(k);
                PdfString t = newDic.getAsString(PdfName.T);
                String newName = name;
                if (t != null) {
                    newName = newName + "." + t.toUnicodeString();
                }
                dic.merge(newDic);
                dic.remove(PdfName.T);
                this.kidNode(dic, newName);
            }
        }
    }

    protected void readFields() {
        PdfArray fld;
        this.catalog = this.trailer.getAsDict(PdfName.ROOT);
        PdfDictionary fdf = this.catalog.getAsDict(PdfName.FDF);
        if (fdf == null) {
            return;
        }
        PdfString fs = fdf.getAsString(PdfName.F);
        if (fs != null) {
            this.fileSpec = fs.toUnicodeString();
        }
        if ((fld = fdf.getAsArray(PdfName.FIELDS)) == null) {
            return;
        }
        this.encoding = fdf.getAsName(PdfName.ENCODING);
        PdfDictionary merged = new PdfDictionary();
        merged.put(PdfName.KIDS, fld);
        this.kidNode(merged, "");
    }

    @Deprecated
    public HashMap<String, PdfDictionary> getFields() {
        return (HashMap)this.fields;
    }

    public Map<String, PdfDictionary> getAllFields() {
        return this.fields;
    }

    public PdfDictionary getField(String name) {
        return this.fields.get(name);
    }

    public String getFieldValue(String name) {
        PdfDictionary field = this.fields.get(name);
        if (field == null) {
            return null;
        }
        PdfObject v = FdfReader.getPdfObject(field.get(PdfName.V));
        if (v == null) {
            return null;
        }
        if (v.isName()) {
            return PdfName.decodeName(v.toString());
        }
        if (v.isString()) {
            PdfString vs = (PdfString)v;
            if (this.encoding == null || vs.getEncoding() != null) {
                return vs.toUnicodeString();
            }
            byte[] b = vs.getBytes();
            if (b.length >= 2 && b[0] == -2 && b[1] == -1) {
                return vs.toUnicodeString();
            }
            try {
                if (this.encoding.equals(PdfName.SHIFT_JIS)) {
                    return new String(b, "SJIS");
                }
                if (this.encoding.equals(PdfName.UHC)) {
                    return new String(b, "MS949");
                }
                if (this.encoding.equals(PdfName.GBK)) {
                    return new String(b, "GBK");
                }
                if (this.encoding.equals(PdfName.BIGFIVE)) {
                    return new String(b, "Big5");
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
            return vs.toUnicodeString();
        }
        return null;
    }

    public String getFileSpec() {
        return this.fileSpec;
    }
}

