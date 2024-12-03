/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import java.util.ArrayList;

public class PdfPages {
    private ArrayList<PdfIndirectReference> pages = new ArrayList();
    private ArrayList<PdfIndirectReference> parents = new ArrayList();
    private int leafSize = 10;
    private PdfWriter writer;
    private PdfIndirectReference topParent;

    PdfPages(PdfWriter writer) {
        this.writer = writer;
    }

    void addPage(PdfDictionary page) {
        try {
            if (this.pages.size() % this.leafSize == 0) {
                this.parents.add(this.writer.getPdfIndirectReference());
            }
            PdfIndirectReference parent = this.parents.get(this.parents.size() - 1);
            page.put(PdfName.PARENT, parent);
            PdfIndirectReference current = this.writer.getCurrentPage();
            this.writer.addToBody((PdfObject)page, current);
            this.pages.add(current);
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    PdfIndirectReference addPageRef(PdfIndirectReference pageRef) {
        try {
            if (this.pages.size() % this.leafSize == 0) {
                this.parents.add(this.writer.getPdfIndirectReference());
            }
            this.pages.add(pageRef);
            return this.parents.get(this.parents.size() - 1);
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    PdfIndirectReference writePageTree() throws IOException {
        if (this.pages.isEmpty()) {
            throw new IOException(MessageLocalization.getComposedMessage("the.document.has.no.pages"));
        }
        int leaf = 1;
        ArrayList<PdfIndirectReference> tParents = this.parents;
        ArrayList<PdfIndirectReference> tPages = this.pages;
        ArrayList<PdfIndirectReference> nextParents = new ArrayList<PdfIndirectReference>();
        while (true) {
            leaf *= this.leafSize;
            int stdCount = this.leafSize;
            int rightCount = tPages.size() % this.leafSize;
            if (rightCount == 0) {
                rightCount = this.leafSize;
            }
            for (int p = 0; p < tParents.size(); ++p) {
                int count;
                int thisLeaf = leaf;
                if (p == tParents.size() - 1) {
                    count = rightCount;
                    thisLeaf = this.pages.size() % leaf;
                    if (thisLeaf == 0) {
                        thisLeaf = leaf;
                    }
                } else {
                    count = stdCount;
                }
                PdfDictionary top = new PdfDictionary(PdfName.PAGES);
                top.put(PdfName.COUNT, new PdfNumber(thisLeaf));
                PdfArray kids = new PdfArray(tPages.subList(p * stdCount, p * stdCount + count));
                top.put(PdfName.KIDS, kids);
                if (tParents.size() > 1) {
                    if (p % this.leafSize == 0) {
                        nextParents.add(this.writer.getPdfIndirectReference());
                    }
                    top.put(PdfName.PARENT, (PdfObject)nextParents.get(p / this.leafSize));
                } else {
                    top.put(PdfName.ITXT, new PdfString(Document.getRelease()));
                }
                this.writer.addToBody((PdfObject)top, tParents.get(p));
            }
            if (tParents.size() == 1) {
                this.topParent = tParents.get(0);
                return this.topParent;
            }
            tPages = tParents;
            tParents = nextParents;
            nextParents = new ArrayList();
        }
    }

    PdfIndirectReference getTopParent() {
        return this.topParent;
    }

    void setLinearMode(PdfIndirectReference topParent) {
        if (this.parents.size() > 1) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("linear.page.mode.can.only.be.called.with.a.single.parent"));
        }
        if (topParent != null) {
            this.topParent = topParent;
            this.parents.clear();
            this.parents.add(topParent);
        }
        this.leafSize = 10000000;
    }

    void addPage(PdfIndirectReference page) {
        this.pages.add(page);
    }

    int reorderPages(int[] order) throws DocumentException {
        if (order == null) {
            return this.pages.size();
        }
        if (this.parents.size() > 1) {
            throw new DocumentException(MessageLocalization.getComposedMessage("page.reordering.requires.a.single.parent.in.the.page.tree.call.pdfwriter.setlinearmode.after.open"));
        }
        if (order.length != this.pages.size()) {
            throw new DocumentException(MessageLocalization.getComposedMessage("page.reordering.requires.an.array.with.the.same.size.as.the.number.of.pages"));
        }
        int max = this.pages.size();
        boolean[] temp = new boolean[max];
        for (int k = 0; k < max; ++k) {
            int p = order[k];
            if (p < 1 || p > max) {
                throw new DocumentException(MessageLocalization.getComposedMessage("page.reordering.requires.pages.between.1.and.1.found.2", String.valueOf(max), String.valueOf(p)));
            }
            if (temp[p - 1]) {
                throw new DocumentException(MessageLocalization.getComposedMessage("page.reordering.requires.no.page.repetition.page.1.is.repeated", p));
            }
            temp[p - 1] = true;
        }
        PdfIndirectReference[] copy = this.pages.toArray(new PdfIndirectReference[0]);
        for (int k = 0; k < max; ++k) {
            this.pages.set(k, copy[order[k] - 1]);
        }
        return max;
    }
}

