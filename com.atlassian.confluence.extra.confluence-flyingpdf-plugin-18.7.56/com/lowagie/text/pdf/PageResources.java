/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfLiteral;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfResources;
import java.util.HashMap;

class PageResources {
    protected PdfDictionary fontDictionary = new PdfDictionary();
    protected PdfDictionary xObjectDictionary = new PdfDictionary();
    protected PdfDictionary colorDictionary = new PdfDictionary();
    protected PdfDictionary patternDictionary = new PdfDictionary();
    protected PdfDictionary shadingDictionary = new PdfDictionary();
    protected PdfDictionary extGStateDictionary = new PdfDictionary();
    protected PdfDictionary propertyDictionary = new PdfDictionary();
    protected HashMap<PdfName, ?> forbiddenNames;
    protected PdfDictionary originalResources;
    protected int[] namePtr = new int[]{0};
    protected HashMap<PdfName, PdfName> usedNames;

    PageResources() {
    }

    void setOriginalResources(PdfDictionary resources, int[] newNamePtr) {
        if (newNamePtr != null) {
            this.namePtr = newNamePtr;
        }
        this.forbiddenNames = new HashMap();
        this.usedNames = new HashMap();
        if (resources == null) {
            return;
        }
        this.originalResources = new PdfDictionary();
        this.originalResources.merge(resources);
        for (PdfName key : resources.getKeys()) {
            PdfObject sub = PdfReader.getPdfObject(resources.get(key));
            if (sub == null || !sub.isDictionary()) continue;
            PdfDictionary dic = (PdfDictionary)sub;
            for (PdfName pdfName : dic.getKeys()) {
                this.forbiddenNames.put(pdfName, null);
            }
            PdfDictionary dic2 = new PdfDictionary();
            dic2.merge(dic);
            this.originalResources.put(key, dic2);
        }
    }

    PdfName translateName(PdfName name) {
        PdfName translated = name;
        if (this.forbiddenNames != null && (translated = this.usedNames.get(name)) == null) {
            int n;
            do {
                n = this.namePtr[0];
                this.namePtr[0] = n + 1;
            } while (this.forbiddenNames.containsKey(translated = new PdfName("Xi" + n)));
            this.usedNames.put(name, translated);
        }
        return translated;
    }

    PdfName addFont(PdfName name, PdfIndirectReference reference) {
        name = this.translateName(name);
        this.fontDictionary.put(name, reference);
        return name;
    }

    PdfName addXObject(PdfName name, PdfIndirectReference reference) {
        name = this.translateName(name);
        this.xObjectDictionary.put(name, reference);
        return name;
    }

    PdfName addColor(PdfName name, PdfIndirectReference reference) {
        name = this.translateName(name);
        this.colorDictionary.put(name, reference);
        return name;
    }

    void addDefaultColor(PdfName name, PdfObject obj) {
        if (obj == null || obj.isNull()) {
            this.colorDictionary.remove(name);
        } else {
            this.colorDictionary.put(name, obj);
        }
    }

    void addDefaultColor(PdfDictionary dic) {
        this.colorDictionary.merge(dic);
    }

    void addDefaultColorDiff(PdfDictionary dic) {
        this.colorDictionary.mergeDifferent(dic);
    }

    PdfName addShading(PdfName name, PdfIndirectReference reference) {
        name = this.translateName(name);
        this.shadingDictionary.put(name, reference);
        return name;
    }

    PdfName addPattern(PdfName name, PdfIndirectReference reference) {
        name = this.translateName(name);
        this.patternDictionary.put(name, reference);
        return name;
    }

    PdfName addExtGState(PdfName name, PdfIndirectReference reference) {
        name = this.translateName(name);
        this.extGStateDictionary.put(name, reference);
        return name;
    }

    PdfName addProperty(PdfName name, PdfIndirectReference reference) {
        name = this.translateName(name);
        this.propertyDictionary.put(name, reference);
        return name;
    }

    PdfDictionary getResources() {
        PdfResources resources = new PdfResources();
        if (this.originalResources != null) {
            resources.putAll(this.originalResources);
        }
        resources.put(PdfName.PROCSET, new PdfLiteral("[/PDF /Text /ImageB /ImageC /ImageI]"));
        resources.add(PdfName.FONT, this.fontDictionary);
        resources.add(PdfName.XOBJECT, this.xObjectDictionary);
        resources.add(PdfName.COLORSPACE, this.colorDictionary);
        resources.add(PdfName.PATTERN, this.patternDictionary);
        resources.add(PdfName.SHADING, this.shadingDictionary);
        resources.add(PdfName.EXTGSTATE, this.extGStateDictionary);
        resources.add(PdfName.PROPERTIES, this.propertyDictionary);
        return resources;
    }

    boolean hasResources() {
        return this.fontDictionary.size() > 0 || this.xObjectDictionary.size() > 0 || this.colorDictionary.size() > 0 || this.patternDictionary.size() > 0 || this.shadingDictionary.size() > 0 || this.extGStateDictionary.size() > 0 || this.propertyDictionary.size() > 0;
    }
}

