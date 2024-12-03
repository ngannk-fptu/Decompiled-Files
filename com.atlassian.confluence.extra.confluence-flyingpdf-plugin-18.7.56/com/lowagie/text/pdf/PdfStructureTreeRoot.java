/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumberTree;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfStructureElement;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PdfStructureTreeRoot
extends PdfDictionary {
    private Map<Integer, PdfArray> parentTree = new HashMap<Integer, PdfArray>();
    private PdfIndirectReference reference;
    private PdfWriter writer;

    PdfStructureTreeRoot(PdfWriter writer) {
        super(PdfName.STRUCTTREEROOT);
        this.writer = writer;
        this.reference = writer.getPdfIndirectReference();
    }

    public void mapRole(PdfName used, PdfName standard) {
        PdfDictionary rm = (PdfDictionary)this.get(PdfName.ROLEMAP);
        if (rm == null) {
            rm = new PdfDictionary();
            this.put(PdfName.ROLEMAP, rm);
        }
        rm.put(used, standard);
    }

    public PdfWriter getWriter() {
        return this.writer;
    }

    public PdfIndirectReference getReference() {
        return this.reference;
    }

    void setPageMark(int page, PdfIndirectReference struc) {
        Integer i = page;
        PdfArray ar = this.parentTree.get(i);
        if (ar == null) {
            ar = new PdfArray();
            this.parentTree.put(i, ar);
        }
        ar.add(struc);
    }

    private void nodeProcess(PdfDictionary struc, PdfIndirectReference reference) throws IOException {
        PdfObject obj = struc.get(PdfName.K);
        if (obj != null && obj.isArray() && !((PdfArray)obj).getElements().get(0).isNumber()) {
            PdfArray ar = (PdfArray)obj;
            List<PdfObject> a = ar.getElements();
            for (int k = 0; k < a.size(); ++k) {
                PdfStructureElement e = (PdfStructureElement)a.get(k);
                a.set(k, e.getReference());
                this.nodeProcess(e, e.getReference());
            }
        }
        if (reference != null) {
            this.writer.addToBody((PdfObject)struc, reference);
        }
    }

    void buildTree() throws IOException {
        HashMap<Integer, PdfIndirectReference> numTree = new HashMap<Integer, PdfIndirectReference>();
        for (Integer i : this.parentTree.keySet()) {
            PdfArray ar = this.parentTree.get(i);
            numTree.put(i, this.writer.addToBody(ar).getIndirectReference());
        }
        PdfDictionary dicTree = PdfNumberTree.writeTree(numTree, this.writer);
        if (dicTree != null) {
            this.put(PdfName.PARENTTREE, this.writer.addToBody(dicTree).getIndirectReference());
        }
        this.nodeProcess(this, this.reference);
    }
}

