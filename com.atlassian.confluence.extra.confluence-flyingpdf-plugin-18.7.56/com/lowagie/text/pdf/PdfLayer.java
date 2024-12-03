/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfOCG;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import java.util.ArrayList;

public class PdfLayer
extends PdfDictionary
implements PdfOCG {
    protected PdfIndirectReference ref;
    protected ArrayList<PdfLayer> children;
    protected PdfLayer parent;
    protected String title;
    private boolean on = true;
    private boolean onPanel = true;

    PdfLayer(String title) {
        this.title = title;
    }

    public static PdfLayer createTitle(String title, PdfWriter writer) {
        if (title == null) {
            throw new NullPointerException(MessageLocalization.getComposedMessage("title.cannot.be.null"));
        }
        PdfLayer layer = new PdfLayer(title);
        writer.registerLayer(layer);
        return layer;
    }

    public PdfLayer(String name, PdfWriter writer) {
        super(PdfName.OCG);
        this.setName(name);
        this.ref = writer.getPdfIndirectReference();
        writer.registerLayer(this);
    }

    String getTitle() {
        return this.title;
    }

    public void addChild(PdfLayer child) {
        if (child.parent != null) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("the.layer.1.already.has.a.parent", ((PdfString)child.get(PdfName.NAME)).toUnicodeString()));
        }
        child.parent = this;
        if (this.children == null) {
            this.children = new ArrayList();
        }
        this.children.add(child);
    }

    public PdfLayer getParent() {
        return this.parent;
    }

    public ArrayList<PdfLayer> getChildren() {
        return this.children;
    }

    @Override
    public PdfIndirectReference getRef() {
        return this.ref;
    }

    void setRef(PdfIndirectReference ref) {
        this.ref = ref;
    }

    public void setName(String name) {
        this.put(PdfName.NAME, new PdfString(name, "UnicodeBig"));
    }

    @Override
    public PdfObject getPdfObject() {
        return this;
    }

    public boolean isOn() {
        return this.on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    private PdfDictionary getUsage() {
        PdfDictionary usage = (PdfDictionary)this.get(PdfName.USAGE);
        if (usage == null) {
            usage = new PdfDictionary();
            this.put(PdfName.USAGE, usage);
        }
        return usage;
    }

    public void setCreatorInfo(String creator, String subtype) {
        PdfDictionary usage = this.getUsage();
        PdfDictionary dic = new PdfDictionary();
        dic.put(PdfName.CREATOR, new PdfString(creator, "UnicodeBig"));
        dic.put(PdfName.SUBTYPE, new PdfName(subtype));
        usage.put(PdfName.CREATORINFO, dic);
    }

    public void setLanguage(String lang, boolean preferred) {
        PdfDictionary usage = this.getUsage();
        PdfDictionary dic = new PdfDictionary();
        dic.put(PdfName.LANG, new PdfString(lang, "UnicodeBig"));
        if (preferred) {
            dic.put(PdfName.PREFERRED, PdfName.ON);
        }
        usage.put(PdfName.LANGUAGE, dic);
    }

    public void setExport(boolean export) {
        PdfDictionary usage = this.getUsage();
        PdfDictionary dic = new PdfDictionary();
        dic.put(PdfName.EXPORTSTATE, export ? PdfName.ON : PdfName.OFF);
        usage.put(PdfName.EXPORT, dic);
    }

    public void setZoom(float min, float max) {
        if (min <= 0.0f && max < 0.0f) {
            return;
        }
        PdfDictionary usage = this.getUsage();
        PdfDictionary dic = new PdfDictionary();
        if (min > 0.0f) {
            dic.put(PdfName.MIN_LOWER_CASE, new PdfNumber(min));
        }
        if (max >= 0.0f) {
            dic.put(PdfName.MAX_LOWER_CASE, new PdfNumber(max));
        }
        usage.put(PdfName.ZOOM, dic);
    }

    public void setPrint(String subtype, boolean printstate) {
        PdfDictionary usage = this.getUsage();
        PdfDictionary dic = new PdfDictionary();
        dic.put(PdfName.SUBTYPE, new PdfName(subtype));
        dic.put(PdfName.PRINTSTATE, printstate ? PdfName.ON : PdfName.OFF);
        usage.put(PdfName.PRINT, dic);
    }

    public void setView(boolean view) {
        PdfDictionary usage = this.getUsage();
        PdfDictionary dic = new PdfDictionary();
        dic.put(PdfName.VIEWSTATE, view ? PdfName.ON : PdfName.OFF);
        usage.put(PdfName.VIEW, dic);
    }

    public boolean isOnPanel() {
        return this.onPanel;
    }

    public void setOnPanel(boolean onPanel) {
        this.onPanel = onPanel;
    }
}

