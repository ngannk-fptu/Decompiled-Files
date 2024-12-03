/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PRIndirectReference;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfString;
import java.util.ArrayList;
import java.util.HashMap;

public class PRAcroForm
extends PdfDictionary {
    ArrayList<FieldInformation> fields;
    ArrayList<PdfDictionary> stack;
    HashMap<String, FieldInformation> fieldByName;
    PdfReader reader;

    public PRAcroForm(PdfReader reader) {
        this.reader = reader;
        this.fields = new ArrayList();
        this.fieldByName = new HashMap();
        this.stack = new ArrayList();
    }

    @Override
    public int size() {
        return this.fields.size();
    }

    public ArrayList<FieldInformation> getFields() {
        return this.fields;
    }

    public FieldInformation getField(String name) {
        return this.fieldByName.get(name);
    }

    public PRIndirectReference getRefByName(String name) {
        FieldInformation fi = this.fieldByName.get(name);
        if (fi == null) {
            return null;
        }
        return fi.getRef();
    }

    public void readAcroForm(PdfDictionary root) {
        if (root == null) {
            return;
        }
        this.hashMap = root.hashMap;
        this.pushAttrib(root);
        PdfArray fieldlist = (PdfArray)PdfReader.getPdfObjectRelease(root.get(PdfName.FIELDS));
        this.iterateFields(fieldlist, null, null);
    }

    protected void iterateFields(PdfArray fieldlist, PRIndirectReference fieldDict, String title) {
        for (PdfObject pdfObject : fieldlist.getElements()) {
            PdfArray kids;
            boolean isFieldDict;
            PRIndirectReference ref = (PRIndirectReference)pdfObject;
            PdfDictionary dict = (PdfDictionary)PdfReader.getPdfObjectRelease(ref);
            PRIndirectReference myFieldDict = fieldDict;
            String myTitle = title;
            PdfString tField = (PdfString)dict.get(PdfName.T);
            boolean bl = isFieldDict = tField != null;
            if (isFieldDict) {
                myFieldDict = ref;
                myTitle = title == null ? tField.toString() : title + '.' + tField.toString();
            }
            if ((kids = (PdfArray)dict.get(PdfName.KIDS)) != null) {
                this.pushAttrib(dict);
                this.iterateFields(kids, myFieldDict, myTitle);
                this.stack.remove(this.stack.size() - 1);
                continue;
            }
            if (myFieldDict == null) continue;
            PdfDictionary mergedDict = this.stack.get(this.stack.size() - 1);
            if (isFieldDict) {
                mergedDict = this.mergeAttrib(mergedDict, dict);
            }
            mergedDict.put(PdfName.T, new PdfString(myTitle));
            FieldInformation fi = new FieldInformation(myTitle, mergedDict, myFieldDict);
            this.fields.add(fi);
            this.fieldByName.put(myTitle, fi);
        }
    }

    protected PdfDictionary mergeAttrib(PdfDictionary parent, PdfDictionary child) {
        PdfDictionary targ = new PdfDictionary();
        if (parent != null) {
            targ.putAll(parent);
        }
        for (PdfName key : child.getKeys()) {
            if (!key.equals(PdfName.DR) && !key.equals(PdfName.DA) && !key.equals(PdfName.Q) && !key.equals(PdfName.FF) && !key.equals(PdfName.DV) && !key.equals(PdfName.V) && !key.equals(PdfName.FT) && !key.equals(PdfName.F)) continue;
            targ.put(key, child.get(key));
        }
        return targ;
    }

    protected void pushAttrib(PdfDictionary dict) {
        PdfDictionary dic = null;
        if (!this.stack.isEmpty()) {
            dic = this.stack.get(this.stack.size() - 1);
        }
        dic = this.mergeAttrib(dic, dict);
        this.stack.add(dic);
    }

    public static class FieldInformation {
        String name;
        PdfDictionary info;
        PRIndirectReference ref;

        FieldInformation(String name, PdfDictionary info, PRIndirectReference ref) {
            this.name = name;
            this.info = info;
            this.ref = ref;
        }

        public String getName() {
            return this.name;
        }

        public PdfDictionary getInfo() {
            return this.info;
        }

        public PRIndirectReference getRef() {
            return this.ref;
        }
    }
}

