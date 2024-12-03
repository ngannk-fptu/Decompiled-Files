/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PdfNumberTree {
    private static final int leafSize = 64;

    public static PdfDictionary writeTree(Map<Integer, ? extends PdfObject> items, PdfWriter writer) throws IOException {
        if (items.isEmpty()) {
            return null;
        }
        Object[] numbers = new Integer[items.size()];
        numbers = items.keySet().toArray(numbers);
        Arrays.sort(numbers);
        if (numbers.length <= 64) {
            PdfDictionary dic = new PdfDictionary();
            PdfArray ar = new PdfArray();
            for (Object number : numbers) {
                ar.add(new PdfNumber((Integer)number));
                ar.add(items.get(number));
            }
            dic.put(PdfName.NUMS, ar);
            return dic;
        }
        int skip = 64;
        PdfIndirectReference[] kids = new PdfIndirectReference[(numbers.length + 64 - 1) / 64];
        for (int k = 0; k < kids.length; ++k) {
            int offset;
            int end = Math.min(offset + 64, numbers.length);
            PdfDictionary dic = new PdfDictionary();
            PdfArray arr = new PdfArray();
            arr.add(new PdfNumber((Integer)numbers[offset]));
            arr.add(new PdfNumber((Integer)numbers[end - 1]));
            dic.put(PdfName.LIMITS, arr);
            arr = new PdfArray();
            for (offset = k * 64; offset < end; ++offset) {
                arr.add(new PdfNumber((Integer)numbers[offset]));
                arr.add(items.get(numbers[offset]));
            }
            dic.put(PdfName.NUMS, arr);
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
            int tt = (numbers.length + (skip *= 64) - 1) / skip;
            for (k = 0; k < tt; ++k) {
                int offset;
                int end = Math.min(offset + 64, top);
                PdfDictionary dic = new PdfDictionary();
                PdfArray arr = new PdfArray();
                arr.add(new PdfNumber((Integer)numbers[k * skip]));
                arr.add(new PdfNumber((Integer)numbers[Math.min((k + 1) * skip, numbers.length) - 1]));
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

    private static void iterateItems(PdfDictionary dic, Map<PdfObject, PdfObject> items) {
        block3: {
            PdfArray nn;
            block2: {
                nn = (PdfArray)PdfReader.getPdfObjectRelease(dic.get(PdfName.NUMS));
                if (nn == null) break block2;
                for (int k = 0; k < nn.size(); ++k) {
                    PdfNumber s = (PdfNumber)PdfReader.getPdfObjectRelease(nn.getPdfObject(k++));
                    items.put(s, nn.getPdfObject(k));
                }
                break block3;
            }
            nn = (PdfArray)PdfReader.getPdfObjectRelease(dic.get(PdfName.KIDS));
            if (nn == null) break block3;
            for (int k = 0; k < nn.size(); ++k) {
                PdfDictionary kid = (PdfDictionary)PdfReader.getPdfObjectRelease(nn.getPdfObject(k));
                PdfNumberTree.iterateItems(kid, items);
            }
        }
    }

    public static HashMap<PdfObject, PdfObject> readTree(PdfDictionary dic) {
        HashMap<PdfObject, PdfObject> items = new HashMap<PdfObject, PdfObject>();
        if (dic != null) {
            PdfNumberTree.iterateItems(dic, items);
        }
        return items;
    }
}

