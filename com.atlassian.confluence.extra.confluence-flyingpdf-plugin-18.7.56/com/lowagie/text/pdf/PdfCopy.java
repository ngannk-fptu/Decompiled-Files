/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BadPdfFormatException;
import com.lowagie.text.pdf.ByteBuffer;
import com.lowagie.text.pdf.PRIndirectReference;
import com.lowagie.text.pdf.PRStream;
import com.lowagie.text.pdf.PageResources;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfContents;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfDocument;
import com.lowagie.text.pdf.PdfException;
import com.lowagie.text.pdf.PdfFormField;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfLiteral;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNull;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfOutline;
import com.lowagie.text.pdf.PdfPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfReaderInstance;
import com.lowagie.text.pdf.PdfRectangle;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class PdfCopy
extends PdfWriter {
    protected HashMap<RefKey, IndirectReferences> indirects;
    protected HashMap<PdfReader, HashMap<RefKey, IndirectReferences>> indirectMap;
    protected PdfReader reader;
    protected PdfIndirectReference acroForm;
    protected int[] namePtr = new int[]{0};
    private boolean rotateContents = true;
    protected PdfArray fieldArray;
    protected HashMap<PdfTemplate, Object> fieldTemplates;

    public PdfCopy(Document document, OutputStream os) throws DocumentException {
        super(new PdfDocument(), os);
        document.addDocListener(this.pdf);
        this.pdf.addWriter(this);
        this.indirectMap = new HashMap();
    }

    public boolean isRotateContents() {
        return this.rotateContents;
    }

    public void setRotateContents(boolean rotateContents) {
        this.rotateContents = rotateContents;
    }

    @Override
    public PdfImportedPage getImportedPage(PdfReader reader, int pageNumber) {
        if (this.currentPdfReaderInstance != null) {
            if (this.currentPdfReaderInstance.getReader() != reader) {
                try {
                    this.currentPdfReaderInstance.getReader().close();
                    this.currentPdfReaderInstance.getReaderFile().close();
                }
                catch (IOException iOException) {
                    // empty catch block
                }
                this.currentPdfReaderInstance = reader.getPdfReaderInstance(this);
            }
        } else {
            this.currentPdfReaderInstance = reader.getPdfReaderInstance(this);
        }
        return this.currentPdfReaderInstance.getImportedPage(pageNumber);
    }

    protected PdfIndirectReference copyIndirect(PRIndirectReference in) throws IOException, BadPdfFormatException {
        PdfObject type;
        PdfObject obj;
        PdfIndirectReference theRef;
        RefKey key = new RefKey(in);
        IndirectReferences iRef = this.indirects.get(key);
        if (iRef != null) {
            theRef = iRef.getRef();
            if (iRef.getCopied()) {
                return theRef;
            }
        } else {
            theRef = this.body.getPdfIndirectReference();
            iRef = new IndirectReferences(theRef);
            this.indirects.put(key, iRef);
        }
        if ((obj = PdfReader.getPdfObjectRelease(in)) != null && obj.isDictionary() && PdfName.PAGE.equals(type = PdfReader.getPdfObjectRelease(((PdfDictionary)obj).get(PdfName.TYPE)))) {
            return theRef;
        }
        iRef.setCopied();
        obj = this.copyObject(obj);
        this.addToBody(obj, theRef);
        return theRef;
    }

    protected PdfDictionary copyDictionary(PdfDictionary in) throws IOException, BadPdfFormatException {
        PdfDictionary out = new PdfDictionary();
        PdfObject type = PdfReader.getPdfObjectRelease(in.get(PdfName.TYPE));
        for (PdfName key : in.getKeys()) {
            PdfObject value = in.get(key);
            if (PdfName.PAGE.equals(type)) {
                if (key.equals(PdfName.B) || key.equals(PdfName.PARENT)) continue;
                out.put(key, this.copyObject(value));
                continue;
            }
            out.put(key, this.copyObject(value));
        }
        return out;
    }

    protected PdfStream copyStream(PRStream in) throws IOException, BadPdfFormatException {
        PRStream out = new PRStream(in, null);
        for (PdfName key : in.getKeys()) {
            PdfObject value = in.get(key);
            out.put(key, this.copyObject(value));
        }
        return out;
    }

    protected PdfArray copyArray(PdfArray in) throws IOException, BadPdfFormatException {
        PdfArray out = new PdfArray();
        for (PdfObject value : in.getElements()) {
            out.add(this.copyObject(value));
        }
        return out;
    }

    protected PdfObject copyObject(PdfObject in) throws IOException, BadPdfFormatException {
        if (in == null) {
            return PdfNull.PDFNULL;
        }
        switch (in.type) {
            case 6: {
                return this.copyDictionary((PdfDictionary)in);
            }
            case 10: {
                return this.copyIndirect((PRIndirectReference)in);
            }
            case 5: {
                return this.copyArray((PdfArray)in);
            }
            case 0: 
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 8: {
                return in;
            }
            case 7: {
                return this.copyStream((PRStream)in);
            }
        }
        if (in.type < 0) {
            String lit = in.toString();
            if (lit.equals("true") || lit.equals("false")) {
                return new PdfBoolean(lit);
            }
            return new PdfLiteral(lit);
        }
        System.out.println("CANNOT COPY type " + in.type);
        return null;
    }

    protected int setFromIPage(PdfImportedPage iPage) {
        int pageNum = iPage.getPageNumber();
        PdfReaderInstance inst = this.currentPdfReaderInstance = iPage.getPdfReaderInstance();
        this.reader = inst.getReader();
        this.setFromReader(this.reader);
        return pageNum;
    }

    protected void setFromReader(PdfReader reader) {
        this.reader = reader;
        this.indirects = this.indirectMap.get(reader);
        if (this.indirects == null) {
            this.indirects = new HashMap();
            this.indirectMap.put(reader, this.indirects);
            PdfDictionary catalog = reader.getCatalog();
            PdfObject o = catalog.get(PdfName.ACROFORM);
            if (o == null || o.type() != 10) {
                return;
            }
            PRIndirectReference ref = (PRIndirectReference)o;
            if (this.acroForm == null) {
                this.acroForm = this.body.getPdfIndirectReference();
            }
            this.indirects.put(new RefKey(ref), new IndirectReferences(this.acroForm));
        }
    }

    public void addPage(PdfImportedPage iPage) throws IOException, BadPdfFormatException {
        int pageNum = this.setFromIPage(iPage);
        PdfDictionary thePage = this.reader.getPageN(pageNum);
        PRIndirectReference origRef = this.reader.getPageOrigRef(pageNum);
        this.reader.releasePage(pageNum);
        RefKey key = new RefKey(origRef);
        IndirectReferences iRef = this.indirects.get(key);
        if (iRef != null && !iRef.getCopied()) {
            this.pageReferences.add(iRef.getRef());
            iRef.setCopied();
        }
        PdfIndirectReference pageRef = this.getCurrentPage();
        if (iRef == null) {
            iRef = new IndirectReferences(pageRef);
            this.indirects.put(key, iRef);
        }
        iRef.setCopied();
        PdfDictionary newPage = this.copyDictionary(thePage);
        this.root.addPage(newPage);
        ++this.currentPageNumber;
    }

    public void addPage(Rectangle rect, int rotation) {
        PdfRectangle mediabox = new PdfRectangle(rect, rotation);
        PageResources resources = new PageResources();
        PdfPage page = new PdfPage(mediabox, new HashMap(), resources.getResources(), 0);
        page.put(PdfName.TABS, this.getTabs());
        this.root.addPage(page);
        ++this.currentPageNumber;
    }

    public void copyAcroForm(PdfReader reader) throws IOException, BadPdfFormatException {
        PdfIndirectReference myRef;
        this.setFromReader(reader);
        PdfDictionary catalog = reader.getCatalog();
        PRIndirectReference hisRef = null;
        PdfObject o = catalog.get(PdfName.ACROFORM);
        if (o != null && o.type() == 10) {
            hisRef = (PRIndirectReference)o;
        }
        if (hisRef == null) {
            return;
        }
        RefKey key = new RefKey(hisRef);
        IndirectReferences iRef = this.indirects.get(key);
        if (iRef != null) {
            this.acroForm = myRef = iRef.getRef();
        } else {
            this.acroForm = myRef = this.body.getPdfIndirectReference();
            iRef = new IndirectReferences(myRef);
            this.indirects.put(key, iRef);
        }
        if (!iRef.getCopied()) {
            iRef.setCopied();
            PdfDictionary theForm = this.copyDictionary((PdfDictionary)PdfReader.getPdfObject(hisRef));
            this.addToBody((PdfObject)theForm, myRef);
        }
    }

    @Override
    protected PdfDictionary getCatalog(PdfIndirectReference rootObj) {
        try {
            PdfDocument.PdfCatalog theCat = this.pdf.getCatalog(rootObj);
            if (this.fieldArray == null) {
                if (this.acroForm != null) {
                    theCat.put(PdfName.ACROFORM, this.acroForm);
                }
            } else {
                this.addFieldResources(theCat);
            }
            return theCat;
        }
        catch (IOException e) {
            throw new ExceptionConverter(e);
        }
    }

    private void addFieldResources(PdfDictionary catalog) throws IOException {
        PdfDictionary dic;
        if (this.fieldArray == null) {
            return;
        }
        PdfDictionary acroForm = new PdfDictionary();
        catalog.put(PdfName.ACROFORM, acroForm);
        acroForm.put(PdfName.FIELDS, this.fieldArray);
        acroForm.put(PdfName.DA, new PdfString("/Helv 0 Tf 0 g "));
        if (this.fieldTemplates.isEmpty()) {
            return;
        }
        PdfDictionary dr = new PdfDictionary();
        acroForm.put(PdfName.DR, dr);
        Iterator<PdfTemplate> iterator = this.fieldTemplates.keySet().iterator();
        while (iterator.hasNext()) {
            PdfTemplate o;
            PdfTemplate template = o = iterator.next();
            PdfFormField.mergeResources(dr, (PdfDictionary)template.getResources());
        }
        PdfDictionary fonts = dr.getAsDict(PdfName.FONT);
        if (fonts == null) {
            fonts = new PdfDictionary();
            dr.put(PdfName.FONT, fonts);
        }
        if (!fonts.contains(PdfName.HELV)) {
            dic = new PdfDictionary(PdfName.FONT);
            dic.put(PdfName.BASEFONT, PdfName.HELVETICA);
            dic.put(PdfName.ENCODING, PdfName.WIN_ANSI_ENCODING);
            dic.put(PdfName.NAME, PdfName.HELV);
            dic.put(PdfName.SUBTYPE, PdfName.TYPE1);
            fonts.put(PdfName.HELV, this.addToBody(dic).getIndirectReference());
        }
        if (!fonts.contains(PdfName.ZADB)) {
            dic = new PdfDictionary(PdfName.FONT);
            dic.put(PdfName.BASEFONT, PdfName.ZAPFDINGBATS);
            dic.put(PdfName.NAME, PdfName.ZADB);
            dic.put(PdfName.SUBTYPE, PdfName.TYPE1);
            fonts.put(PdfName.ZADB, this.addToBody(dic).getIndirectReference());
        }
    }

    @Override
    public void close() {
        if (this.open) {
            PdfReaderInstance ri = this.currentPdfReaderInstance;
            this.pdf.close();
            super.close();
            if (ri != null) {
                try {
                    ri.getReader().close();
                    ri.getReaderFile().close();
                }
                catch (IOException iOException) {
                    // empty catch block
                }
            }
        }
    }

    public PdfIndirectReference add(PdfOutline outline) {
        return null;
    }

    @Override
    public void addAnnotation(PdfAnnotation annot) {
    }

    @Override
    PdfIndirectReference add(PdfPage page, PdfContents contents) throws PdfException {
        return null;
    }

    @Override
    public void freeReader(PdfReader reader) throws IOException {
        this.indirectMap.remove(reader);
        if (this.currentPdfReaderInstance != null && this.currentPdfReaderInstance.getReader() == reader) {
            try {
                this.currentPdfReaderInstance.getReader().close();
                this.currentPdfReaderInstance.getReaderFile().close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
            this.currentPdfReaderInstance = null;
        }
    }

    public PageStamp createPageStamp(PdfImportedPage iPage) {
        int pageNum = iPage.getPageNumber();
        PdfReader reader = iPage.getPdfReaderInstance().getReader();
        PdfDictionary pageN = reader.getPageN(pageNum);
        return new PageStamp(reader, pageN, this);
    }

    public static class StampContent
    extends PdfContentByte {
        PageResources pageResources;

        StampContent(PdfWriter writer, PageResources pageResources) {
            super(writer);
            this.pageResources = pageResources;
        }

        @Override
        public PdfContentByte getDuplicate() {
            return new StampContent(this.writer, this.pageResources);
        }

        @Override
        PageResources getPageResources() {
            return this.pageResources;
        }
    }

    public static class PageStamp {
        PdfDictionary pageN;
        StampContent under;
        StampContent over;
        PageResources pageResources;
        PdfReader reader;
        PdfCopy cstp;

        PageStamp(PdfReader reader, PdfDictionary pageN, PdfCopy cstp) {
            this.pageN = pageN;
            this.reader = reader;
            this.cstp = cstp;
        }

        public PdfContentByte getUnderContent() {
            if (this.under == null) {
                if (this.pageResources == null) {
                    this.pageResources = new PageResources();
                    PdfDictionary resources = this.pageN.getAsDict(PdfName.RESOURCES);
                    this.pageResources.setOriginalResources(resources, this.cstp.namePtr);
                }
                this.under = new StampContent(this.cstp, this.pageResources);
            }
            return this.under;
        }

        public PdfContentByte getOverContent() {
            if (this.over == null) {
                if (this.pageResources == null) {
                    this.pageResources = new PageResources();
                    PdfDictionary resources = this.pageN.getAsDict(PdfName.RESOURCES);
                    this.pageResources.setOriginalResources(resources, this.cstp.namePtr);
                }
                this.over = new StampContent(this.cstp, this.pageResources);
            }
            return this.over;
        }

        public void alterContents() throws IOException {
            if (this.over == null && this.under == null) {
                return;
            }
            PdfArray ar = null;
            PdfObject content = PdfReader.getPdfObject(this.pageN.get(PdfName.CONTENTS), this.pageN);
            if (content == null) {
                ar = new PdfArray();
                this.pageN.put(PdfName.CONTENTS, ar);
            } else if (content.isArray()) {
                ar = (PdfArray)content;
            } else if (content.isStream()) {
                ar = new PdfArray();
                ar.add(this.pageN.get(PdfName.CONTENTS));
                this.pageN.put(PdfName.CONTENTS, ar);
            } else {
                ar = new PdfArray();
                this.pageN.put(PdfName.CONTENTS, ar);
            }
            ByteBuffer out = new ByteBuffer();
            if (this.under != null) {
                out.append(PdfContents.SAVESTATE);
                this.applyRotation(this.pageN, out);
                out.append(this.under.getInternalBuffer());
                out.append(PdfContents.RESTORESTATE);
            }
            if (this.over != null) {
                out.append(PdfContents.SAVESTATE);
            }
            PdfStream stream = new PdfStream(out.toByteArray());
            stream.flateCompress(this.cstp.getCompressionLevel());
            PdfIndirectReference ref1 = this.cstp.addToBody(stream).getIndirectReference();
            ar.addFirst(ref1);
            out.reset();
            if (this.over != null) {
                out.append(' ');
                out.append(PdfContents.RESTORESTATE);
                out.append(PdfContents.SAVESTATE);
                this.applyRotation(this.pageN, out);
                out.append(this.over.getInternalBuffer());
                out.append(PdfContents.RESTORESTATE);
                stream = new PdfStream(out.toByteArray());
                stream.flateCompress(this.cstp.getCompressionLevel());
                ar.add(this.cstp.addToBody(stream).getIndirectReference());
            }
            this.pageN.put(PdfName.RESOURCES, this.pageResources.getResources());
        }

        void applyRotation(PdfDictionary pageN, ByteBuffer out) {
            if (!this.cstp.rotateContents) {
                return;
            }
            Rectangle page = this.reader.getPageSizeWithRotation(pageN);
            int rotation = page.getRotation();
            switch (rotation) {
                case 90: {
                    out.append(PdfContents.ROTATE90);
                    out.append(page.getTop());
                    out.append(' ').append('0').append(PdfContents.ROTATEFINAL);
                    break;
                }
                case 180: {
                    out.append(PdfContents.ROTATE180);
                    out.append(page.getRight());
                    out.append(' ');
                    out.append(page.getTop());
                    out.append(PdfContents.ROTATEFINAL);
                    break;
                }
                case 270: {
                    out.append(PdfContents.ROTATE270);
                    out.append('0').append(' ');
                    out.append(page.getRight());
                    out.append(PdfContents.ROTATEFINAL);
                }
            }
        }

        private void addDocumentField(PdfIndirectReference ref) {
            if (this.cstp.fieldArray == null) {
                this.cstp.fieldArray = new PdfArray();
            }
            this.cstp.fieldArray.add(ref);
        }

        private void expandFields(PdfFormField field, List<PdfAnnotation> allAnnots) {
            allAnnots.add(field);
            List<PdfFormField> kids = field.getKidFields();
            if (kids != null) {
                for (PdfFormField kid : kids) {
                    this.expandFields(kid, allAnnots);
                }
            }
        }

        public void addAnnotation(PdfAnnotation annot) {
            try {
                ArrayList<PdfAnnotation> allAnnots = new ArrayList<PdfAnnotation>();
                if (annot.isForm()) {
                    PdfFormField field = (PdfFormField)annot;
                    if (field.getParent() != null) {
                        return;
                    }
                    this.expandFields(field, allAnnots);
                    if (this.cstp.fieldTemplates == null) {
                        this.cstp.fieldTemplates = new HashMap();
                    }
                } else {
                    allAnnots.add(annot);
                }
                for (PdfAnnotation allAnnot : allAnnots) {
                    annot = allAnnot;
                    if (annot.isForm()) {
                        PdfFormField field;
                        HashMap<PdfTemplate, Object> templates;
                        if (!annot.isUsed() && (templates = annot.getTemplates()) != null) {
                            this.cstp.fieldTemplates.putAll(templates);
                        }
                        if ((field = (PdfFormField)annot).getParent() == null) {
                            this.addDocumentField(field.getIndirectReference());
                        }
                    }
                    if (annot.isAnnotation()) {
                        PdfRectangle rect;
                        PdfArray annots;
                        PdfObject pdfobj = PdfReader.getPdfObject(this.pageN.get(PdfName.ANNOTS), this.pageN);
                        if (pdfobj == null || !pdfobj.isArray()) {
                            annots = new PdfArray();
                            this.pageN.put(PdfName.ANNOTS, annots);
                        } else {
                            annots = (PdfArray)pdfobj;
                        }
                        annots.add(annot.getIndirectReference());
                        if (!(annot.isUsed() || (rect = (PdfRectangle)annot.get(PdfName.RECT)) == null || rect.left() == 0.0f && rect.right() == 0.0f && rect.top() == 0.0f && rect.bottom() == 0.0f)) {
                            int rotation = this.reader.getPageRotation(this.pageN);
                            Rectangle pageSize = this.reader.getPageSizeWithRotation(this.pageN);
                            switch (rotation) {
                                case 90: {
                                    annot.put(PdfName.RECT, new PdfRectangle(pageSize.getTop() - rect.bottom(), rect.left(), pageSize.getTop() - rect.top(), rect.right()));
                                    break;
                                }
                                case 180: {
                                    annot.put(PdfName.RECT, new PdfRectangle(pageSize.getRight() - rect.left(), pageSize.getTop() - rect.bottom(), pageSize.getRight() - rect.right(), pageSize.getTop() - rect.top()));
                                    break;
                                }
                                case 270: {
                                    annot.put(PdfName.RECT, new PdfRectangle(rect.bottom(), pageSize.getRight() - rect.left(), rect.top(), pageSize.getRight() - rect.right()));
                                }
                            }
                        }
                    }
                    if (annot.isUsed()) continue;
                    annot.setUsed();
                    this.cstp.addToBody((PdfObject)annot, annot.getIndirectReference());
                }
            }
            catch (IOException e) {
                throw new ExceptionConverter(e);
            }
        }
    }

    protected static class RefKey {
        int num;
        int gen;

        RefKey(int num, int gen) {
            this.num = num;
            this.gen = gen;
        }

        RefKey(PdfIndirectReference ref) {
            this.num = ref.getNumber();
            this.gen = ref.getGeneration();
        }

        RefKey(PRIndirectReference ref) {
            this.num = ref.getNumber();
            this.gen = ref.getGeneration();
        }

        public int hashCode() {
            return (this.gen << 16) + this.num;
        }

        public boolean equals(Object o) {
            if (!(o instanceof RefKey)) {
                return false;
            }
            RefKey other = (RefKey)o;
            return this.gen == other.gen && this.num == other.num;
        }

        public String toString() {
            return Integer.toString(this.num) + ' ' + this.gen;
        }
    }

    static class IndirectReferences {
        PdfIndirectReference theRef;
        boolean hasCopied;

        IndirectReferences(PdfIndirectReference ref) {
            this.theRef = ref;
            this.hasCopied = false;
        }

        void setCopied() {
            this.hasCopied = true;
        }

        boolean getCopied() {
            return this.hasCopied;
        }

        PdfIndirectReference getRef() {
            return this.theRef;
        }
    }
}

