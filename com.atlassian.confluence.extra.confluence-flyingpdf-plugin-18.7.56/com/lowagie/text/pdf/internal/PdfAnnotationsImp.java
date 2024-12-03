/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf.internal;

import com.lowagie.text.Annotation;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfAcroForm;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfFileSpecification;
import com.lowagie.text.pdf.PdfFormField;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfRectangle;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PdfAnnotationsImp {
    protected PdfAcroForm acroForm;
    protected List<PdfAnnotation> annotations;
    protected List<PdfAnnotation> delayedAnnotations = new ArrayList<PdfAnnotation>();

    public PdfAnnotationsImp(PdfWriter writer) {
        this.acroForm = new PdfAcroForm(writer);
    }

    public boolean hasValidAcroForm() {
        return this.acroForm.isValid();
    }

    public PdfAcroForm getAcroForm() {
        return this.acroForm;
    }

    public void setSigFlags(int f) {
        this.acroForm.setSigFlags(f);
    }

    public void addCalculationOrder(PdfFormField formField) {
        this.acroForm.addCalculationOrder(formField);
    }

    public void addAnnotation(PdfAnnotation annot) {
        if (annot.isForm()) {
            PdfFormField field = (PdfFormField)annot;
            if (field.getParent() == null) {
                this.addFormFieldRaw(field);
            }
        } else {
            this.annotations.add(annot);
        }
    }

    public void addPlainAnnotation(PdfAnnotation annot) {
        this.annotations.add(annot);
    }

    void addFormFieldRaw(PdfFormField field) {
        this.annotations.add(field);
        List<PdfFormField> kids = field.getKidFields();
        if (kids != null) {
            for (PdfFormField kid : kids) {
                this.addFormFieldRaw(kid);
            }
        }
    }

    public boolean hasUnusedAnnotations() {
        return !this.annotations.isEmpty();
    }

    public void resetAnnotations() {
        this.annotations = this.delayedAnnotations;
        this.delayedAnnotations = new ArrayList<PdfAnnotation>();
    }

    public PdfArray rotateAnnotations(PdfWriter writer, Rectangle pageSize) {
        PdfArray array = new PdfArray();
        int rotation = pageSize.getRotation() % 360;
        int currentPage = writer.getCurrentPageNumber();
        for (PdfAnnotation annotation : this.annotations) {
            PdfAnnotation dic = annotation;
            int page = dic.getPlaceInPage();
            if (page > currentPage) {
                this.delayedAnnotations.add(dic);
                continue;
            }
            if (dic.isForm()) {
                PdfFormField field;
                HashMap<PdfTemplate, Object> templates;
                if (!dic.isUsed() && (templates = dic.getTemplates()) != null) {
                    this.acroForm.addFieldTemplates((Map<PdfTemplate, Object>)templates);
                }
                if ((field = (PdfFormField)dic).getParent() == null) {
                    this.acroForm.addDocumentField(field.getIndirectReference());
                }
            }
            if (dic.isAnnotation()) {
                PdfRectangle rect;
                array.add(dic.getIndirectReference());
                if (!dic.isUsed() && (rect = (PdfRectangle)dic.get(PdfName.RECT)) != null) {
                    switch (rotation) {
                        case 90: {
                            dic.put(PdfName.RECT, new PdfRectangle(pageSize.getTop() - rect.bottom(), rect.left(), pageSize.getTop() - rect.top(), rect.right()));
                            break;
                        }
                        case 180: {
                            dic.put(PdfName.RECT, new PdfRectangle(pageSize.getRight() - rect.left(), pageSize.getTop() - rect.bottom(), pageSize.getRight() - rect.right(), pageSize.getTop() - rect.top()));
                            break;
                        }
                        case 270: {
                            dic.put(PdfName.RECT, new PdfRectangle(rect.bottom(), pageSize.getRight() - rect.left(), rect.top(), pageSize.getRight() - rect.right()));
                        }
                    }
                }
            }
            if (dic.isUsed()) continue;
            dic.setUsed();
            try {
                writer.addToBody((PdfObject)dic, dic.getIndirectReference());
            }
            catch (IOException e) {
                throw new ExceptionConverter(e);
            }
        }
        return array;
    }

    public static PdfAnnotation convertAnnotation(PdfWriter writer, Annotation annot, Rectangle defaultRect) throws IOException {
        switch (annot.annotationType()) {
            case 1: {
                return new PdfAnnotation(writer, annot.llx(), annot.lly(), annot.urx(), annot.ury(), new PdfAction((URL)annot.getAttributes().get("url")));
            }
            case 2: {
                return new PdfAnnotation(writer, annot.llx(), annot.lly(), annot.urx(), annot.ury(), new PdfAction((String)annot.getAttributes().get("file")));
            }
            case 3: {
                return new PdfAnnotation(writer, annot.llx(), annot.lly(), annot.urx(), annot.ury(), new PdfAction((String)annot.getAttributes().get("file"), (String)annot.getAttributes().get("destination")));
            }
            case 7: {
                boolean[] sparams = (boolean[])annot.getAttributes().get("parameters");
                String fname = (String)annot.getAttributes().get("file");
                String mimetype = (String)annot.getAttributes().get("mime");
                PdfFileSpecification fs = sparams[0] ? PdfFileSpecification.fileEmbedded(writer, fname, fname, null) : PdfFileSpecification.fileExtern(writer, fname);
                return PdfAnnotation.createScreen(writer, new Rectangle(annot.llx(), annot.lly(), annot.urx(), annot.ury()), fname, fs, mimetype, sparams[1]);
            }
            case 4: {
                return new PdfAnnotation(writer, annot.llx(), annot.lly(), annot.urx(), annot.ury(), new PdfAction((String)annot.getAttributes().get("file"), (Integer)annot.getAttributes().get("page")));
            }
            case 5: {
                return new PdfAnnotation(writer, annot.llx(), annot.lly(), annot.urx(), annot.ury(), new PdfAction((Integer)annot.getAttributes().get("named")));
            }
            case 6: {
                return new PdfAnnotation(writer, annot.llx(), annot.lly(), annot.urx(), annot.ury(), new PdfAction((String)annot.getAttributes().get("application"), (String)annot.getAttributes().get("parameters"), (String)annot.getAttributes().get("operation"), (String)annot.getAttributes().get("defaultdir")));
            }
        }
        return new PdfAnnotation(writer, defaultRect.getLeft(), defaultRect.getBottom(), defaultRect.getRight(), defaultRect.getTop(), new PdfString(annot.title(), "UnicodeBig"), new PdfString(annot.content(), "UnicodeBig"));
    }
}

