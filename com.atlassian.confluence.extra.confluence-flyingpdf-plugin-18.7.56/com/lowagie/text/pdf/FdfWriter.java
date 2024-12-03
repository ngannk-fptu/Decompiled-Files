/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocWriter;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.FdfReader;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfDocument;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class FdfWriter {
    private static final byte[] HEADER_FDF = DocWriter.getISOBytes("%FDF-1.2\n%\u00e2\u00e3\u00cf\u00d3\n");
    HashMap<String, Object> fields = new HashMap();
    private String file;

    public void writeTo(OutputStream os) throws IOException {
        Wrt wrt = new Wrt(os, this);
        wrt.writeTo();
    }

    boolean setField(String field, PdfObject value) {
        Object obj;
        String s;
        Map<String, Object> map;
        block4: {
            map = this.fields;
            StringTokenizer tk = new StringTokenizer(field, ".");
            if (!tk.hasMoreTokens()) {
                return false;
            }
            while (true) {
                s = tk.nextToken();
                obj = map.get(s);
                if (!tk.hasMoreTokens()) break block4;
                if (obj == null) {
                    obj = new HashMap();
                    map.put(s, obj);
                    map = (Map)obj;
                    continue;
                }
                if (!(obj instanceof Map)) break;
                map = (Map)obj;
            }
            return false;
        }
        if (!(obj instanceof Map)) {
            map.put(s, value);
            return true;
        }
        return false;
    }

    void iterateFields(HashMap<String, Object> values, HashMap<String, Object> map, String name) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String s = entry.getKey();
            Object obj = entry.getValue();
            if (obj instanceof HashMap) {
                this.iterateFields(values, (HashMap)obj, name + "." + s);
                continue;
            }
            values.put((name + "." + s).substring(1), obj);
        }
    }

    public boolean removeField(String field) {
        Object obj;
        ArrayList<String> histStr;
        ArrayList<HashMap<String, Object>> histMap;
        Map<String, Object> map;
        block5: {
            map = this.fields;
            StringTokenizer tk = new StringTokenizer(field, ".");
            if (!tk.hasMoreTokens()) {
                return false;
            }
            histMap = new ArrayList<HashMap<String, Object>>();
            histStr = new ArrayList<String>();
            while (true) {
                String s;
                if ((obj = map.get(s = tk.nextToken())) == null) {
                    return false;
                }
                histMap.add((HashMap<String, Object>)map);
                histStr.add(s);
                if (!tk.hasMoreTokens()) break block5;
                if (!(obj instanceof Map)) break;
                map = (Map)obj;
            }
            return false;
        }
        if (obj instanceof Map) {
            return false;
        }
        for (int k = histMap.size() - 1; k >= 0; --k) {
            map = (Map)histMap.get(k);
            String s = (String)histStr.get(k);
            map.remove(s);
            if (!map.isEmpty()) break;
        }
        return true;
    }

    public HashMap<String, Object> getFields() {
        HashMap<String, Object> values = new HashMap<String, Object>();
        this.iterateFields(values, this.fields, "");
        return values;
    }

    public String getField(String field) {
        Object obj;
        block5: {
            Map<String, Object> map = this.fields;
            StringTokenizer tk = new StringTokenizer(field, ".");
            if (!tk.hasMoreTokens()) {
                return null;
            }
            while (true) {
                String s;
                if ((obj = map.get(s = tk.nextToken())) == null) {
                    return null;
                }
                if (!tk.hasMoreTokens()) break block5;
                if (!(obj instanceof Map)) break;
                map = (Map)obj;
            }
            return null;
        }
        if (obj instanceof Map) {
            return null;
        }
        if (((PdfObject)obj).isString()) {
            return ((PdfString)obj).toUnicodeString();
        }
        return PdfName.decodeName(obj.toString());
    }

    public boolean setFieldAsName(String field, String value) {
        return this.setField(field, new PdfName(value));
    }

    public boolean setFieldAsString(String field, String value) {
        return this.setField(field, new PdfString(value, "UnicodeBig"));
    }

    public boolean setFieldAsAction(String field, PdfAction action) {
        return this.setField(field, action);
    }

    public void setFields(FdfReader fdf) {
        Map<String, PdfDictionary> map = fdf.getAllFields();
        for (Map.Entry<String, PdfDictionary> entry : map.entrySet()) {
            String key = entry.getKey();
            PdfDictionary dic = entry.getValue();
            PdfObject v = dic.get(PdfName.V);
            if (v != null) {
                this.setField(key, v);
            }
            if ((v = dic.get(PdfName.A)) == null) continue;
            this.setField(key, v);
        }
    }

    public void setFields(PdfReader pdf) {
        this.setFields(pdf.getAcroFields());
    }

    public void setFields(AcroFields af) {
        for (Map.Entry<String, AcroFields.Item> entry : af.getAllFields().entrySet()) {
            PdfObject ft;
            String fn = entry.getKey();
            AcroFields.Item item = entry.getValue();
            PdfDictionary dic = item.getMerged(0);
            PdfObject v = PdfReader.getPdfObjectRelease(dic.get(PdfName.V));
            if (v == null || (ft = PdfReader.getPdfObjectRelease(dic.get(PdfName.FT))) == null || PdfName.SIG.equals(ft)) continue;
            this.setField(fn, v);
        }
    }

    public String getFile() {
        return this.file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    static class Wrt
    extends PdfWriter {
        private FdfWriter fdf;

        Wrt(OutputStream os, FdfWriter fdf) throws IOException {
            super(new PdfDocument(), os);
            this.fdf = fdf;
            this.os.write(HEADER_FDF);
            this.body = new PdfWriter.PdfBody(this);
        }

        void writeTo() throws IOException {
            PdfDictionary dic = new PdfDictionary();
            dic.put(PdfName.FIELDS, this.calculate(this.fdf.fields));
            if (this.fdf.file != null) {
                dic.put(PdfName.F, new PdfString(this.fdf.file, "UnicodeBig"));
            }
            PdfDictionary fd = new PdfDictionary();
            fd.put(PdfName.FDF, dic);
            PdfIndirectReference ref = this.addToBody(fd).getIndirectReference();
            this.os.write(Wrt.getISOBytes("trailer\n"));
            PdfDictionary trailer = new PdfDictionary();
            trailer.put(PdfName.ROOT, ref);
            trailer.toPdf(null, this.os);
            this.os.write(Wrt.getISOBytes("\n%%EOF\n"));
            this.os.close();
        }

        PdfArray calculate(HashMap<String, Object> map) {
            PdfArray ar = new PdfArray();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                Object v = entry.getValue();
                PdfDictionary dic = new PdfDictionary();
                dic.put(PdfName.T, new PdfString(key, "UnicodeBig"));
                if (v instanceof HashMap) {
                    dic.put(PdfName.KIDS, this.calculate((HashMap)v));
                } else if (v instanceof PdfAction) {
                    dic.put(PdfName.A, (PdfAction)v);
                } else {
                    dic.put(PdfName.V, (PdfObject)v);
                }
                ar.add(dic);
            }
            return ar;
        }
    }
}

