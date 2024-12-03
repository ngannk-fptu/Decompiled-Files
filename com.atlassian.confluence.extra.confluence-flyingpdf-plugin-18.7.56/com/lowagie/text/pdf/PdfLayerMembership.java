/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfLayer;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfOCG;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class PdfLayerMembership
extends PdfDictionary
implements PdfOCG {
    public static final PdfName ALLON = new PdfName("AllOn");
    public static final PdfName ANYON = new PdfName("AnyOn");
    public static final PdfName ANYOFF = new PdfName("AnyOff");
    public static final PdfName ALLOFF = new PdfName("AllOff");
    PdfIndirectReference ref;
    PdfArray members = new PdfArray();
    Set<PdfLayer> layers = new HashSet<PdfLayer>();

    public PdfLayerMembership(PdfWriter writer) {
        super(PdfName.OCMD);
        this.put(PdfName.OCGS, this.members);
        this.ref = writer.getPdfIndirectReference();
    }

    @Override
    public PdfIndirectReference getRef() {
        return this.ref;
    }

    public void addMember(PdfLayer layer) {
        if (!this.layers.contains(layer)) {
            this.members.add(layer.getRef());
            this.layers.add(layer);
        }
    }

    public Collection getLayers() {
        return this.layers;
    }

    public void setVisibilityPolicy(PdfName type) {
        this.put(PdfName.P, type);
    }

    @Override
    public PdfObject getPdfObject() {
        return this;
    }
}

