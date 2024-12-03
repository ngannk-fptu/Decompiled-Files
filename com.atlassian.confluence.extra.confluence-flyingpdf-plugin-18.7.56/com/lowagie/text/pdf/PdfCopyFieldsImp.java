/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.exceptions.BadPasswordException;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.IntHashtable;
import com.lowagie.text.pdf.PRIndirectReference;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfDocument;
import com.lowagie.text.pdf.PdfFormField;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

class PdfCopyFieldsImp
extends PdfWriter {
    private static final PdfName iTextTag = new PdfName("_iTextTag_");
    private static final Integer zero = 0;
    private List<PdfReader> readers = new ArrayList<PdfReader>();
    Map<PdfReader, IntHashtable> readers2intrefs = new HashMap<PdfReader, IntHashtable>();
    private Map<PdfReader, IntHashtable> pages2intrefs = new HashMap<PdfReader, IntHashtable>();
    private Map<PdfReader, IntHashtable> visited = new HashMap<PdfReader, IntHashtable>();
    List<AcroFields> fields = new ArrayList<AcroFields>();
    RandomAccessFileOrArray file;
    Map<String, Object> fieldTree = new HashMap<String, Object>();
    List<PdfIndirectReference> pageRefs = new ArrayList<PdfIndirectReference>();
    List<PdfDictionary> pageDics = new ArrayList<PdfDictionary>();
    PdfDictionary resources = new PdfDictionary();
    PdfDictionary form;
    boolean closing = false;
    Document nd;
    private Map<PdfArray, List<Integer>> tabOrder;
    private List<Object> calculationOrder = new ArrayList<Object>();
    private List<Object> calculationOrderRefs;
    private boolean hasSignature;
    protected static final Map<PdfName, Integer> widgetKeys = new HashMap<PdfName, Integer>();
    protected static final Map<PdfName, Integer> fieldKeys = new HashMap<PdfName, Integer>();

    PdfCopyFieldsImp(OutputStream os) throws DocumentException {
        this(os, '\u0000');
    }

    PdfCopyFieldsImp(OutputStream os, char pdfVersion) throws DocumentException {
        super(new PdfDocument(), os);
        this.pdf.addWriter(this);
        if (pdfVersion != '\u0000') {
            super.setPdfVersion(pdfVersion);
        }
        this.nd = new Document();
        this.nd.addDocListener(this.pdf);
    }

    void addDocument(PdfReader reader, List<Integer> pagesToKeep) throws DocumentException, IOException {
        if (!this.readers2intrefs.containsKey(reader) && reader.isTampered()) {
            throw new DocumentException(MessageLocalization.getComposedMessage("the.document.was.reused"));
        }
        reader = new PdfReader(reader);
        reader.selectPages(pagesToKeep);
        if (reader.getNumberOfPages() == 0) {
            return;
        }
        reader.setTampered(false);
        this.addDocument(reader);
    }

    void addDocument(PdfReader reader) throws DocumentException, IOException {
        if (!reader.isOpenedWithFullPermissions()) {
            throw new BadPasswordException(MessageLocalization.getComposedMessage("pdfreader.not.opened.with.owner.password"));
        }
        this.openDoc();
        if (this.readers2intrefs.containsKey(reader)) {
            reader = new PdfReader(reader);
        } else {
            if (reader.isTampered()) {
                throw new DocumentException(MessageLocalization.getComposedMessage("the.document.was.reused"));
            }
            reader.consolidateNamedDestinations();
            reader.setTampered(true);
        }
        reader.shuffleSubsetNames();
        this.readers2intrefs.put(reader, new IntHashtable());
        this.readers.add(reader);
        int len = reader.getNumberOfPages();
        IntHashtable refs = new IntHashtable();
        for (int p = 1; p <= len; ++p) {
            refs.put(reader.getPageOrigRef(p).getNumber(), 1);
            reader.releasePage(p);
        }
        this.pages2intrefs.put(reader, refs);
        this.visited.put(reader, new IntHashtable());
        this.fields.add(reader.getAcroFields());
        this.updateCalculationOrder(reader);
    }

    private static String getCOName(PdfReader reader, PRIndirectReference ref) {
        PdfObject obj;
        String name = "";
        while (ref != null && (obj = PdfReader.getPdfObject(ref)) != null && obj.type() == 6) {
            PdfDictionary dic = (PdfDictionary)obj;
            PdfString t = dic.getAsString(PdfName.T);
            if (t != null) {
                name = t.toUnicodeString() + "." + name;
            }
            ref = (PRIndirectReference)dic.get(PdfName.PARENT);
        }
        if (name.endsWith(".")) {
            name = name.substring(0, name.length() - 1);
        }
        return name;
    }

    protected void updateCalculationOrder(PdfReader reader) {
        PdfDictionary catalog = reader.getCatalog();
        PdfDictionary acro = catalog.getAsDict(PdfName.ACROFORM);
        if (acro == null) {
            return;
        }
        PdfArray co = acro.getAsArray(PdfName.CO);
        if (co == null || co.size() == 0) {
            return;
        }
        AcroFields af = reader.getAcroFields();
        for (int k = 0; k < co.size(); ++k) {
            String name;
            PdfObject obj = co.getPdfObject(k);
            if (obj == null || !obj.isIndirect() || af.getFieldItem(name = PdfCopyFieldsImp.getCOName(reader, (PRIndirectReference)obj)) == null || this.calculationOrder.contains(name = "." + name)) continue;
            this.calculationOrder.add(name);
        }
    }

    private void propagate(PdfObject obj, PdfIndirectReference refo, boolean restricted) {
        if (obj == null) {
            return;
        }
        if (obj instanceof PdfIndirectReference) {
            return;
        }
        switch (obj.type()) {
            case 6: 
            case 7: {
                PdfDictionary dic = (PdfDictionary)obj;
                for (PdfName key : dic.getKeys()) {
                    if (restricted && (key.equals(PdfName.PARENT) || key.equals(PdfName.KIDS))) continue;
                    PdfObject ob = dic.get(key);
                    if (ob != null && ob.isIndirect()) {
                        PRIndirectReference ind = (PRIndirectReference)ob;
                        if (this.setVisited(ind) || this.isPage(ind)) continue;
                        PdfIndirectReference ref = this.getNewReference(ind);
                        this.propagate(PdfReader.getPdfObjectRelease(ind), ref, restricted);
                        continue;
                    }
                    this.propagate(ob, null, restricted);
                }
                break;
            }
            case 5: {
                for (PdfObject ob : ((PdfArray)obj).getElements()) {
                    if (ob != null && ob.isIndirect()) {
                        PRIndirectReference ind = (PRIndirectReference)ob;
                        if (this.isVisited(ind) || this.isPage(ind)) continue;
                        PdfIndirectReference ref = this.getNewReference(ind);
                        this.propagate(PdfReader.getPdfObjectRelease(ind), ref, restricted);
                        continue;
                    }
                    this.propagate(ob, null, restricted);
                }
                break;
            }
            case 10: {
                throw new RuntimeException(MessageLocalization.getComposedMessage("reference.pointing.to.reference"));
            }
        }
    }

    private void adjustTabOrder(PdfArray annots, PdfIndirectReference ind, PdfNumber nn) {
        int v = nn.intValue();
        List<Integer> t = this.tabOrder.get(annots);
        if (t == null) {
            t = new ArrayList<Integer>();
            int size = annots.size() - 1;
            for (int k = 0; k < size; ++k) {
                t.add(zero);
            }
            t.add(v);
            this.tabOrder.put(annots, t);
            annots.add(ind);
        } else {
            int size;
            for (int k = size = t.size() - 1; k >= 0; --k) {
                if (t.get(k) > v) continue;
                t.add(k + 1, v);
                annots.add(k + 1, ind);
                size = -2;
                break;
            }
            if (size != -2) {
                t.add(0, v);
                annots.add(0, ind);
            }
        }
    }

    @Deprecated
    protected PdfArray branchForm(HashMap level, PdfIndirectReference parent, String fname) throws IOException {
        return this.branchForm((Map<String, Object>)level, parent, fname);
    }

    protected PdfArray branchForm(Map<String, Object> level, PdfIndirectReference parent, String fname) throws IOException {
        PdfArray arr = new PdfArray();
        for (Map.Entry<String, Object> entry : level.entrySet()) {
            Object obj;
            String name = entry.getKey();
            PdfIndirectReference ind = this.getPdfIndirectReference();
            PdfDictionary dic = new PdfDictionary();
            if (parent != null) {
                dic.put(PdfName.PARENT, parent);
            }
            dic.put(PdfName.T, new PdfString(name, "UnicodeBig"));
            String fname2 = fname + "." + name;
            int coidx = this.calculationOrder.indexOf(fname2);
            if (coidx >= 0) {
                this.calculationOrderRefs.set(coidx, ind);
            }
            if ((obj = entry.getValue()) instanceof Map) {
                Map map = (Map)obj;
                dic.put(PdfName.KIDS, this.branchForm(map, ind, fname2));
                arr.add(ind);
                this.addToBody((PdfObject)dic, ind);
                continue;
            }
            ArrayList list = (ArrayList)obj;
            dic.mergeDifferent((PdfDictionary)list.get(0));
            if (list.size() == 3) {
                dic.mergeDifferent((PdfDictionary)list.get(2));
                int page = (Integer)list.get(1);
                PdfDictionary pageDic = this.pageDics.get(page - 1);
                PdfArray annots = pageDic.getAsArray(PdfName.ANNOTS);
                if (annots == null) {
                    annots = new PdfArray();
                    pageDic.put(PdfName.ANNOTS, annots);
                }
                PdfNumber nn = (PdfNumber)dic.get(iTextTag);
                dic.remove(iTextTag);
                this.adjustTabOrder(annots, ind, nn);
            } else {
                PdfArray kids = new PdfArray();
                for (int k = 1; k < list.size(); k += 2) {
                    int page = (Integer)list.get(k);
                    PdfDictionary pageDic = this.pageDics.get(page - 1);
                    PdfArray annots = pageDic.getAsArray(PdfName.ANNOTS);
                    if (annots == null) {
                        annots = new PdfArray();
                        pageDic.put(PdfName.ANNOTS, annots);
                    }
                    PdfDictionary widget = new PdfDictionary();
                    widget.merge((PdfDictionary)list.get(k + 1));
                    widget.put(PdfName.PARENT, ind);
                    PdfNumber nn = (PdfNumber)widget.get(iTextTag);
                    widget.remove(iTextTag);
                    PdfIndirectReference wref = this.addToBody(widget).getIndirectReference();
                    this.adjustTabOrder(annots, wref, nn);
                    kids.add(wref);
                    this.propagate(widget, null, false);
                }
                dic.put(PdfName.KIDS, kids);
            }
            arr.add(ind);
            this.addToBody((PdfObject)dic, ind);
            this.propagate(dic, null, false);
        }
        return arr;
    }

    protected void createAcroForms() throws IOException {
        if (this.fieldTree.isEmpty()) {
            return;
        }
        this.form = new PdfDictionary();
        this.form.put(PdfName.DR, this.resources);
        this.propagate(this.resources, null, false);
        this.form.put(PdfName.DA, new PdfString("/Helv 0 Tf 0 g "));
        this.tabOrder = new HashMap<PdfArray, List<Integer>>();
        this.calculationOrderRefs = new ArrayList<Object>(this.calculationOrder);
        this.form.put(PdfName.FIELDS, this.branchForm(this.fieldTree, null, ""));
        if (this.hasSignature) {
            this.form.put(PdfName.SIGFLAGS, new PdfNumber(3));
        }
        PdfArray co = new PdfArray();
        for (Object obj : this.calculationOrderRefs) {
            if (!(obj instanceof PdfIndirectReference)) continue;
            co.add((PdfIndirectReference)obj);
        }
        if (co.size() > 0) {
            this.form.put(PdfName.CO, co);
        }
    }

    @Override
    public void close() {
        if (this.closing) {
            super.close();
            return;
        }
        this.closing = true;
        try {
            this.closeIt();
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void closeIt() throws IOException {
        int page;
        for (PdfReader pdfReader : this.readers) {
            pdfReader.removeFields();
        }
        for (PdfReader pdfReader : this.readers) {
            for (page = 1; page <= pdfReader.getNumberOfPages(); ++page) {
                this.pageRefs.add(this.getNewReference(pdfReader.getPageOrigRef(page)));
                this.pageDics.add(pdfReader.getPageN(page));
            }
        }
        this.mergeFields();
        this.createAcroForms();
        for (PdfReader pdfReader : this.readers) {
            for (page = 1; page <= pdfReader.getNumberOfPages(); ++page) {
                PdfDictionary dic = pdfReader.getPageN(page);
                PdfIndirectReference pageRef = this.getNewReference(pdfReader.getPageOrigRef(page));
                PdfIndirectReference parent = this.root.addPageRef(pageRef);
                dic.put(PdfName.PARENT, parent);
                this.propagate(dic, pageRef, false);
            }
        }
        for (Map.Entry entry : this.readers2intrefs.entrySet()) {
            PdfReader reader = (PdfReader)entry.getKey();
            try {
                int[] keys;
                this.file = reader.getSafeFile();
                this.file.reOpen();
                IntHashtable t = (IntHashtable)entry.getValue();
                for (int key : keys = t.toOrderedKeys()) {
                    PRIndirectReference ref = new PRIndirectReference(reader, key);
                    this.addToBody(PdfReader.getPdfObjectRelease(ref), t.get(key));
                }
            }
            finally {
                try {
                    this.file.close();
                    reader.close();
                }
                catch (Exception exception) {}
            }
        }
        this.pdf.close();
    }

    void addPageOffsetToField(Map<String, AcroFields.Item> fd, int pageOffset) {
        if (pageOffset == 0) {
            return;
        }
        for (AcroFields.Item item : fd.values()) {
            for (int k = 0; k < item.size(); ++k) {
                int p = item.getPage(k);
                item.forcePage(k, p + pageOffset);
            }
        }
    }

    void createWidgets(List<Object> list, AcroFields.Item item) {
        for (int k = 0; k < item.size(); ++k) {
            list.add(item.getPage(k));
            PdfDictionary merged = item.getMerged(k);
            PdfObject dr = merged.get(PdfName.DR);
            if (dr != null) {
                PdfFormField.mergeResources(this.resources, (PdfDictionary)PdfReader.getPdfObject(dr));
            }
            PdfDictionary widget = new PdfDictionary();
            for (PdfName key : merged.getKeys()) {
                if (!widgetKeys.containsKey(key)) continue;
                widget.put(key, merged.get(key));
            }
            widget.put(iTextTag, new PdfNumber(item.getTabOrder(k) + 1));
            list.add(widget);
        }
    }

    void mergeField(String name, AcroFields.Item item) {
        Object obj;
        String s;
        HashMap<String, HashMap<String, ArrayList<Object>>> map;
        block16: {
            map = this.fieldTree;
            StringTokenizer tk = new StringTokenizer(name, ".");
            if (!tk.hasMoreTokens()) {
                return;
            }
            while (true) {
                HashMap<String, HashMap<String, ArrayList<Object>>> castMap;
                s = tk.nextToken();
                obj = map.get(s);
                if (!tk.hasMoreTokens()) break block16;
                if (obj == null) {
                    HashMap<String, HashMap<String, ArrayList<Object>>> tempMap = new HashMap<String, HashMap<String, ArrayList<Object>>>();
                    map.put(s, tempMap);
                    map = tempMap;
                    continue;
                }
                if (!(obj instanceof Map)) break;
                map = castMap = (HashMap<String, HashMap<String, ArrayList<Object>>>)obj;
            }
            return;
        }
        if (obj instanceof HashMap) {
            return;
        }
        PdfDictionary merged = item.getMerged(0);
        if (obj == null) {
            PdfDictionary field = new PdfDictionary();
            if (PdfName.SIG.equals(merged.get(PdfName.FT))) {
                this.hasSignature = true;
            }
            for (PdfName key : merged.getKeys()) {
                if (!fieldKeys.containsKey(key)) continue;
                field.put(key, merged.get(key));
            }
            ArrayList<Object> list = new ArrayList<Object>();
            list.add(field);
            this.createWidgets(list, item);
            map.put(s, (HashMap<String, ArrayList<Object>>)((Object)list));
        } else {
            List list = (List)obj;
            PdfDictionary field = (PdfDictionary)list.get(0);
            PdfName type1 = (PdfName)field.get(PdfName.FT);
            PdfName type2 = (PdfName)merged.get(PdfName.FT);
            if (type1 == null || !type1.equals(type2)) {
                return;
            }
            int flag1 = 0;
            PdfObject f1 = field.get(PdfName.FF);
            if (f1 != null && f1.isNumber()) {
                flag1 = ((PdfNumber)f1).intValue();
            }
            int flag2 = 0;
            PdfObject f2 = merged.get(PdfName.FF);
            if (f2 != null && f2.isNumber()) {
                flag2 = ((PdfNumber)f2).intValue();
            }
            if (type1.equals(PdfName.BTN)) {
                if (((flag1 ^ flag2) & 0x10000) != 0) {
                    return;
                }
                if ((flag1 & 0x10000) == 0 && ((flag1 ^ flag2) & 0x8000) != 0) {
                    return;
                }
            } else if (type1.equals(PdfName.CH) && ((flag1 ^ flag2) & 0x20000) != 0) {
                return;
            }
            this.createWidgets(list, item);
        }
    }

    void mergeWithMaster(Map<String, AcroFields.Item> fd) {
        for (Map.Entry<String, AcroFields.Item> entry : fd.entrySet()) {
            String name = entry.getKey();
            this.mergeField(name, entry.getValue());
        }
    }

    void mergeFields() {
        int pageOffset = 0;
        for (int k = 0; k < this.fields.size(); ++k) {
            Map<String, AcroFields.Item> fd = this.fields.get(k).getAllFields();
            this.addPageOffsetToField(fd, pageOffset);
            this.mergeWithMaster(fd);
            pageOffset += this.readers.get(k).getNumberOfPages();
        }
    }

    @Override
    public PdfIndirectReference getPageReference(int page) {
        return this.pageRefs.get(page - 1);
    }

    @Override
    protected PdfDictionary getCatalog(PdfIndirectReference rootObj) {
        try {
            PdfDocument.PdfCatalog cat = this.pdf.getCatalog(rootObj);
            if (this.form != null) {
                PdfIndirectReference ref = this.addToBody(this.form).getIndirectReference();
                cat.put(PdfName.ACROFORM, ref);
            }
            return cat;
        }
        catch (IOException e) {
            throw new ExceptionConverter(e);
        }
    }

    protected PdfIndirectReference getNewReference(PRIndirectReference ref) {
        return new PdfIndirectReference(0, this.getNewObjectNumber(ref.getReader(), ref.getNumber(), 0));
    }

    @Override
    protected int getNewObjectNumber(PdfReader reader, int number, int generation) {
        IntHashtable refs = this.readers2intrefs.get(reader);
        int n = refs.get(number);
        if (n == 0) {
            n = this.getIndirectReferenceNumber();
            refs.put(number, n);
        }
        return n;
    }

    protected boolean setVisited(PRIndirectReference ref) {
        IntHashtable refs = this.visited.get(ref.getReader());
        if (refs != null) {
            return refs.put(ref.getNumber(), 1) != 0;
        }
        return false;
    }

    protected boolean isVisited(PRIndirectReference ref) {
        IntHashtable refs = this.visited.get(ref.getReader());
        if (refs != null) {
            return refs.containsKey(ref.getNumber());
        }
        return false;
    }

    protected boolean isVisited(PdfReader reader, int number, int generation) {
        IntHashtable refs = this.readers2intrefs.get(reader);
        return refs.containsKey(number);
    }

    protected boolean isPage(PRIndirectReference ref) {
        IntHashtable refs = this.pages2intrefs.get(ref.getReader());
        if (refs != null) {
            return refs.containsKey(ref.getNumber());
        }
        return false;
    }

    @Override
    RandomAccessFileOrArray getReaderFile(PdfReader reader) {
        return this.file;
    }

    public void openDoc() {
        if (!this.nd.isOpen()) {
            this.nd.open();
        }
    }

    static {
        Integer one = 1;
        widgetKeys.put(PdfName.SUBTYPE, one);
        widgetKeys.put(PdfName.CONTENTS, one);
        widgetKeys.put(PdfName.RECT, one);
        widgetKeys.put(PdfName.NM, one);
        widgetKeys.put(PdfName.M, one);
        widgetKeys.put(PdfName.F, one);
        widgetKeys.put(PdfName.BS, one);
        widgetKeys.put(PdfName.BORDER, one);
        widgetKeys.put(PdfName.AP, one);
        widgetKeys.put(PdfName.AS, one);
        widgetKeys.put(PdfName.C, one);
        widgetKeys.put(PdfName.A, one);
        widgetKeys.put(PdfName.STRUCTPARENT, one);
        widgetKeys.put(PdfName.OC, one);
        widgetKeys.put(PdfName.H, one);
        widgetKeys.put(PdfName.MK, one);
        widgetKeys.put(PdfName.DA, one);
        widgetKeys.put(PdfName.Q, one);
        fieldKeys.put(PdfName.AA, one);
        fieldKeys.put(PdfName.FT, one);
        fieldKeys.put(PdfName.TU, one);
        fieldKeys.put(PdfName.TM, one);
        fieldKeys.put(PdfName.FF, one);
        fieldKeys.put(PdfName.V, one);
        fieldKeys.put(PdfName.DV, one);
        fieldKeys.put(PdfName.DS, one);
        fieldKeys.put(PdfName.RV, one);
        fieldKeys.put(PdfName.OPT, one);
        fieldKeys.put(PdfName.MAXLEN, one);
        fieldKeys.put(PdfName.TI, one);
        fieldKeys.put(PdfName.I, one);
        fieldKeys.put(PdfName.LOCK, one);
        fieldKeys.put(PdfName.SV, one);
    }
}

