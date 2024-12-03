/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfEncodings;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PdfNameTree {
    private static final int leafSize = 64;

    public static PdfDictionary writeTree(HashMap<String, ? extends PdfObject> items, PdfWriter writer) throws IOException {
        return PdfNameTree.writeTree(items, writer);
    }

    public static PdfDictionary writeTree(Map<String, ? extends PdfObject> items, PdfWriter writer) throws IOException {
        if (items.isEmpty()) {
            return null;
        }
        Object[] names = items.keySet().toArray(new String[0]);
        Arrays.sort(names);
        if (names.length <= 64) {
            PdfDictionary dic = new PdfDictionary();
            PdfArray ar = new PdfArray();
            for (Object name : names) {
                ar.add(new PdfString((String)name, null));
                ar.add(items.get(name));
            }
            dic.put(PdfName.NAMES, ar);
            return dic;
        }
        int skip = 64;
        PdfIndirectReference[] kids = new PdfIndirectReference[(names.length + 64 - 1) / 64];
        for (int k = 0; k < kids.length; ++k) {
            int offset;
            int end = Math.min(offset + 64, names.length);
            PdfDictionary dic = new PdfDictionary();
            PdfArray arr = new PdfArray();
            arr.add(new PdfString((String)names[offset], null));
            arr.add(new PdfString((String)names[end - 1], null));
            dic.put(PdfName.LIMITS, arr);
            arr = new PdfArray();
            for (offset = k * 64; offset < end; ++offset) {
                arr.add(new PdfString((String)names[offset], null));
                arr.add(items.get(names[offset]));
            }
            dic.put(PdfName.NAMES, arr);
            kids[k] = writer.addToBody(dic).getIndirectReference();
        }
        int top = kids.length;
        while (true) {
            int k;
            if (top <= 64) {
                PdfArray arr = new PdfArray();
                for (k = 0; k < top; ++k) {
                    arr.add(kids[k]);
                }
                PdfDictionary dic = new PdfDictionary();
                dic.put(PdfName.KIDS, arr);
                return dic;
            }
            int tt = (names.length + (skip *= 64) - 1) / skip;
            for (k = 0; k < tt; ++k) {
                int offset;
                int end = Math.min(offset + 64, top);
                PdfDictionary dic = new PdfDictionary();
                PdfArray arr = new PdfArray();
                arr.add(new PdfString((String)names[k * skip], null));
                arr.add(new PdfString((String)names[Math.min((k + 1) * skip, names.length) - 1], null));
                dic.put(PdfName.LIMITS, arr);
                arr = new PdfArray();
                for (offset = k * 64; offset < end; ++offset) {
                    arr.add(kids[offset]);
                }
                dic.put(PdfName.KIDS, arr);
                kids[k] = writer.addToBody(dic).getIndirectReference();
            }
            top = tt;
        }
    }

    private static void iterateItems(PdfDictionary dic, HashMap<String, PdfObject> items) {
        block3: {
            PdfArray nn;
            block2: {
                nn = (PdfArray)PdfReader.getPdfObjectRelease(dic.get(PdfName.NAMES));
                if (nn == null) break block2;
                for (int k = 0; k < nn.size(); ++k) {
                    PdfString s = (PdfString)PdfReader.getPdfObjectRelease(nn.getPdfObject(k++));
                    items.put(PdfEncodings.convertToString(s.getBytes(), null), nn.getPdfObject(k));
                }
                break block3;
            }
            nn = (PdfArray)PdfReader.getPdfObjectRelease(dic.get(PdfName.KIDS));
            if (nn == null) break block3;
            for (int k = 0; k < nn.size(); ++k) {
                PdfDictionary kid = (PdfDictionary)PdfReader.getPdfObjectRelease(nn.getPdfObject(k));
                PdfNameTree.iterateItems(kid, items);
            }
        }
    }

    public static HashMap<String, PdfObject> readTree(PdfDictionary dic) {
        HashMap<String, PdfObject> items = new HashMap<String, PdfObject>();
        if (dic != null) {
            PdfNameTree.iterateItems(dic, items);
        }
        return items;
    }
}

