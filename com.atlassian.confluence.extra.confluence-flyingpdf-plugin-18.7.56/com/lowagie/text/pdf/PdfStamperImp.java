/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.exceptions.BadPasswordException;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.ByteBuffer;
import com.lowagie.text.pdf.FdfReader;
import com.lowagie.text.pdf.IntHashtable;
import com.lowagie.text.pdf.PRIndirectReference;
import com.lowagie.text.pdf.PRStream;
import com.lowagie.text.pdf.PageResources;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfAppearance;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfContents;
import com.lowagie.text.pdf.PdfDate;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfDocument;
import com.lowagie.text.pdf.PdfEncryption;
import com.lowagie.text.pdf.PdfException;
import com.lowagie.text.pdf.PdfFormField;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfIndirectObject;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfLayer;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNameTree;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfRectangle;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfTransition;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.lowagie.text.pdf.StampContent;
import com.lowagie.text.pdf.collection.PdfCollection;
import com.lowagie.text.pdf.internal.PdfViewerPreferencesImp;
import com.lowagie.text.xml.xmp.XmpReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.xml.sax.SAXException;

class PdfStamperImp
extends PdfWriter {
    HashMap<PdfReader, IntHashtable> readers2intrefs = new HashMap();
    HashMap<PdfReader, RandomAccessFileOrArray> readers2file = new HashMap();
    RandomAccessFileOrArray file;
    PdfReader reader;
    IntHashtable myXref = new IntHashtable();
    HashMap<PdfDictionary, PageStamp> pagesToContent = new HashMap();
    boolean closed = false;
    private boolean rotateContents = true;
    protected AcroFields acroFields;
    protected boolean flat = false;
    protected boolean flatFreeText = false;
    protected int[] namePtr = new int[]{0};
    protected Set<String> partialFlattening = new HashSet<String>();
    protected boolean useVp = false;
    protected PdfViewerPreferencesImp viewerPreferences = new PdfViewerPreferencesImp();
    protected Map<PdfTemplate, Object> fieldTemplates = new HashMap<PdfTemplate, Object>();
    protected boolean fieldsAdded = false;
    protected int sigFlags = 0;
    protected boolean append;
    protected IntHashtable marked;
    protected int initialXrefSize;
    protected PdfAction openAction;
    private boolean includeFileID = true;
    private PdfObject overrideFileId = null;
    private Calendar modificationDate = null;

    PdfStamperImp(PdfReader reader, OutputStream os, char pdfVersion, boolean append) throws DocumentException, IOException {
        super(new PdfDocument(), os);
        if (!reader.isOpenedWithFullPermissions()) {
            throw new BadPasswordException(MessageLocalization.getComposedMessage("pdfreader.not.opened.with.owner.password"));
        }
        if (reader.isTampered()) {
            throw new DocumentException(MessageLocalization.getComposedMessage("the.original.document.was.reused.read.it.again.from.file"));
        }
        reader.setTampered(true);
        this.reader = reader;
        this.file = reader.getSafeFile();
        this.append = append;
        if (append) {
            int n;
            if (reader.isRebuilt()) {
                throw new DocumentException(MessageLocalization.getComposedMessage("append.mode.requires.a.document.without.errors.even.if.recovery.was.possible"));
            }
            if (reader.isEncrypted()) {
                this.crypto = new PdfEncryption(reader.getDecrypt());
            }
            this.pdf_version.setAppendmode(true);
            this.file.reOpen();
            byte[] buf = new byte[8192];
            while ((n = this.file.read(buf)) > 0) {
                this.os.write(buf, 0, n);
            }
            this.file.close();
            this.prevxref = reader.getLastXref();
            reader.setAppendable(true);
        } else if (pdfVersion == '\u0000') {
            super.setPdfVersion(reader.getPdfVersion());
        } else {
            super.setPdfVersion(pdfVersion);
        }
        super.open();
        this.pdf.addWriter(this);
        if (append) {
            this.body.setRefnum(reader.getXrefSize());
            this.marked = new IntHashtable();
            if (reader.isNewXrefType()) {
                this.fullCompression = true;
            }
            if (reader.isHybridXref()) {
                this.fullCompression = false;
            }
        }
        this.initialXrefSize = reader.getXrefSize();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void close(Map<String, String> moreInfo) throws IOException {
        if (this.closed) {
            return;
        }
        if (this.useVp) {
            this.reader.setViewerPreferences(this.viewerPreferences);
            this.markUsed(this.reader.getTrailer().get(PdfName.ROOT));
        }
        if (this.flat) {
            this.flatFields();
        }
        if (this.flatFreeText) {
            this.flatFreeTextFields();
        }
        this.addFieldResources();
        PdfDictionary catalog = this.reader.getCatalog();
        PdfDictionary pages = (PdfDictionary)PdfReader.getPdfObject(catalog.get(PdfName.PAGES));
        pages.put(PdfName.ITXT, new PdfString(Document.getRelease()));
        this.markUsed(pages);
        PdfObject acroFormObject = PdfReader.getPdfObject(catalog.get(PdfName.ACROFORM), this.reader.getCatalog());
        if (acroFormObject instanceof PdfDictionary) {
            PdfDictionary acroForm = (PdfDictionary)acroFormObject;
            if (this.acroFields != null && this.acroFields.getXfa().isChanged()) {
                this.markUsed(acroForm);
                if (!this.flat) {
                    this.acroFields.getXfa().setXfa(this);
                }
            }
            if (this.sigFlags != 0) {
                acroForm.put(PdfName.SIGFLAGS, new PdfNumber(this.sigFlags));
                this.markUsed(acroForm);
                this.markUsed(catalog);
            }
        }
        this.closed = true;
        this.addSharedObjectsToBody();
        this.setOutlines();
        this.setJavaScript();
        this.addFileAttachments();
        if (this.openAction != null) {
            catalog.put(PdfName.OPENACTION, this.openAction);
        }
        if (this.pdf.pageLabels != null) {
            catalog.put(PdfName.PAGELABELS, this.pdf.pageLabels.getDictionary(this));
        }
        if (!this.documentOCG.isEmpty()) {
            this.fillOCProperties(false);
            PdfDictionary ocdict = catalog.getAsDict(PdfName.OCPROPERTIES);
            if (ocdict == null) {
                this.reader.getCatalog().put(PdfName.OCPROPERTIES, this.OCProperties);
            } else {
                ocdict.put(PdfName.OCGS, this.OCProperties.get(PdfName.OCGS));
                PdfDictionary ddict = ocdict.getAsDict(PdfName.D);
                if (ddict == null) {
                    ddict = new PdfDictionary();
                    ocdict.put(PdfName.D, ddict);
                }
                ddict.put(PdfName.ORDER, this.OCProperties.getAsDict(PdfName.D).get(PdfName.ORDER));
                ddict.put(PdfName.RBGROUPS, this.OCProperties.getAsDict(PdfName.D).get(PdfName.RBGROUPS));
                ddict.put(PdfName.OFF, this.OCProperties.getAsDict(PdfName.D).get(PdfName.OFF));
                ddict.put(PdfName.AS, this.OCProperties.getAsDict(PdfName.D).get(PdfName.AS));
            }
        }
        int skipInfo = -1;
        PRIndirectReference iInfo = (PRIndirectReference)this.reader.getTrailer().get(PdfName.INFO);
        PdfDictionary oldInfo = (PdfDictionary)PdfReader.getPdfObject(iInfo);
        String producer = null;
        if (iInfo != null) {
            skipInfo = iInfo.getNumber();
        }
        if (oldInfo != null && oldInfo.get(PdfName.PRODUCER) != null) {
            producer = oldInfo.getAsString(PdfName.PRODUCER).toUnicodeString();
        }
        if (producer == null) {
            producer = Document.getVersion();
        } else if (!producer.contains(Document.getProduct())) {
            StringBuilder buf = new StringBuilder(producer);
            buf.append("; modified using ");
            buf.append(Document.getVersion());
            producer = buf.toString();
        }
        if (moreInfo != null && moreInfo.containsKey("Producer")) {
            producer = moreInfo.get("Producer");
        }
        byte[] altMetadata = null;
        PdfObject xmpo = PdfReader.getPdfObject(catalog.get(PdfName.METADATA));
        if (xmpo != null && xmpo.isStream()) {
            altMetadata = PdfReader.getStreamBytesRaw((PRStream)xmpo);
            PdfReader.killIndirect(catalog.get(PdfName.METADATA));
        }
        if (this.xmpMetadata != null) {
            altMetadata = this.xmpMetadata;
        }
        PdfDate date = null;
        date = this.modificationDate == null ? new PdfDate() : new PdfDate(this.modificationDate);
        if (altMetadata != null) {
            PdfStream xmp = null;
            try {
                XmpReader xmpr = new XmpReader(altMetadata);
                Object producerXMP = producer;
                if (producerXMP == null) {
                    producerXMP = "";
                }
                if (!xmpr.replace("http://ns.adobe.com/pdf/1.3/", "Producer", (String)producerXMP) && !"".equals(producerXMP)) {
                    xmpr.add("rdf:Description", "http://ns.adobe.com/pdf/1.3/", "pdf:Producer", (String)producerXMP);
                }
                if (!xmpr.replace("http://ns.adobe.com/xap/1.0/", "ModifyDate", date.getW3CDate())) {
                    xmpr.add("rdf:Description", "http://ns.adobe.com/xap/1.0/", "xmp:ModifyDate", date.getW3CDate());
                }
                xmpr.replace("http://ns.adobe.com/xap/1.0/", "MetadataDate", date.getW3CDate());
                xmp = new PdfStream(xmpr.serializeDoc());
            }
            catch (IOException | SAXException e) {
                xmp = new PdfStream(altMetadata);
            }
            xmp.put(PdfName.TYPE, PdfName.METADATA);
            xmp.put(PdfName.SUBTYPE, PdfName.XML);
            if (this.crypto != null && !this.crypto.isMetadataEncrypted()) {
                PdfArray ar = new PdfArray();
                ar.add(PdfName.CRYPT);
                xmp.put(PdfName.FILTER, ar);
            }
            if (this.append && xmpo != null) {
                this.body.add((PdfObject)xmp, xmpo.getIndRef());
            } else {
                catalog.put(PdfName.METADATA, this.body.add(xmp).getIndirectReference());
                this.markUsed(catalog);
            }
        }
        try {
            this.file.reOpen();
            this.alterContents();
            int rootN = ((PRIndirectReference)this.reader.trailer.get(PdfName.ROOT)).getNumber();
            if (this.append) {
                int[] keys = this.marked.getKeys();
                for (Object j : (Object)keys) {
                    PdfObject obj = this.reader.getPdfObjectRelease((int)j);
                    if (obj == null || skipInfo == j || j >= this.initialXrefSize) continue;
                    this.addToBody(obj, (int)j, j != rootN);
                }
                for (int k = this.initialXrefSize; k < this.reader.getXrefSize(); ++k) {
                    PdfObject obj = this.reader.getPdfObject(k);
                    if (obj == null) continue;
                    this.addToBody(obj, this.getNewObjectNumber(this.reader, k, 0));
                }
            } else {
                for (int k = 1; k < this.reader.getXrefSize(); ++k) {
                    PdfObject obj = this.reader.getPdfObjectRelease(k);
                    if (obj == null || skipInfo == k) continue;
                    this.addToBody(obj, this.getNewObjectNumber(this.reader, k, 0), k != rootN);
                }
            }
        }
        finally {
            try {
                this.file.close();
            }
            catch (Exception rootN) {}
        }
        PdfIndirectReference encryption = null;
        PdfObject fileID = null;
        if (this.crypto != null) {
            if (this.append) {
                encryption = this.reader.getCryptoRef();
            } else {
                PdfIndirectObject encryptionObject = this.addToBody((PdfObject)this.crypto.getEncryptionDictionary(), false);
                encryption = encryptionObject.getIndirectReference();
            }
            if (this.includeFileID) {
                fileID = this.crypto.getFileID();
            }
        } else if (this.includeFileID) {
            fileID = this.overrideFileId != null ? this.overrideFileId : PdfEncryption.createInfoId(PdfEncryption.createDocumentId());
        }
        PRIndirectReference iRoot = (PRIndirectReference)this.reader.trailer.get(PdfName.ROOT);
        PdfIndirectReference root = new PdfIndirectReference(0, this.getNewObjectNumber(this.reader, iRoot.getNumber(), 0));
        PdfIndirectReference info = null;
        PdfDictionary newInfo = new PdfDictionary();
        if (oldInfo != null) {
            for (PdfName pdfName : oldInfo.getKeys()) {
                PdfObject value = PdfReader.getPdfObject(oldInfo.get(pdfName));
                newInfo.put(pdfName, value);
            }
        }
        newInfo.put(PdfName.MODDATE, date);
        if (producer != null) {
            newInfo.put(PdfName.PRODUCER, new PdfString(producer));
        }
        if (moreInfo != null) {
            for (Map.Entry entry : moreInfo.entrySet()) {
                String key = (String)entry.getKey();
                PdfName keyName = new PdfName(key);
                String value = (String)entry.getValue();
                if (value == null) {
                    newInfo.remove(keyName);
                    continue;
                }
                newInfo.put(keyName, new PdfString(value, "UnicodeBig"));
            }
        }
        info = this.append ? (iInfo == null ? this.addToBody((PdfObject)newInfo, false).getIndirectReference() : this.addToBody((PdfObject)newInfo, iInfo.getNumber(), false).getIndirectReference()) : this.addToBody((PdfObject)newInfo, false).getIndirectReference();
        this.body.writeCrossReferenceTable(this.os, root, info, encryption, fileID, this.prevxref);
        this.os.write(PdfStamperImp.getISOBytes("startxref\n"));
        this.os.write(PdfStamperImp.getISOBytes(String.valueOf(this.body.offset())));
        this.os.write(PdfStamperImp.getISOBytes("\n%%EOF\n"));
        this.os.flush();
        if (this.isCloseStream()) {
            this.os.close();
        }
        this.reader.close();
    }

    void applyRotation(PdfDictionary pageN, ByteBuffer out) {
        if (!this.rotateContents) {
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

    void alterContents() throws IOException {
        Iterator<PageStamp> iterator = this.pagesToContent.values().iterator();
        while (iterator.hasNext()) {
            PageStamp o;
            PageStamp ps = o = iterator.next();
            PdfDictionary pageN = ps.pageN;
            this.markUsed(pageN);
            PdfArray ar = null;
            PdfObject content = PdfReader.getPdfObject(pageN.get(PdfName.CONTENTS), pageN);
            if (content == null) {
                ar = new PdfArray();
                pageN.put(PdfName.CONTENTS, ar);
            } else if (content.isArray()) {
                ar = (PdfArray)content;
                this.markUsed(ar);
            } else if (content.isStream()) {
                ar = new PdfArray();
                ar.add(pageN.get(PdfName.CONTENTS));
                pageN.put(PdfName.CONTENTS, ar);
            } else {
                ar = new PdfArray();
                pageN.put(PdfName.CONTENTS, ar);
            }
            ByteBuffer out = new ByteBuffer();
            if (ps.under != null) {
                out.append(PdfContents.SAVESTATE);
                this.applyRotation(pageN, out);
                out.append(ps.under.getInternalBuffer());
                out.append(PdfContents.RESTORESTATE);
            }
            if (ps.over != null) {
                out.append(PdfContents.SAVESTATE);
            }
            PdfStream stream = new PdfStream(out.toByteArray());
            stream.flateCompress(this.compressionLevel);
            ar.addFirst(this.addToBody(stream).getIndirectReference());
            out.reset();
            if (ps.over != null) {
                out.append(' ');
                out.append(PdfContents.RESTORESTATE);
                ByteBuffer buf = ps.over.getInternalBuffer();
                out.append(buf.getBuffer(), 0, ps.replacePoint);
                out.append(PdfContents.SAVESTATE);
                this.applyRotation(pageN, out);
                out.append(buf.getBuffer(), ps.replacePoint, buf.size() - ps.replacePoint);
                out.append(PdfContents.RESTORESTATE);
                stream = new PdfStream(out.toByteArray());
                stream.flateCompress(this.compressionLevel);
                ar.add(this.addToBody(stream).getIndirectReference());
            }
            this.alterResources(ps);
        }
    }

    void alterResources(PageStamp ps) {
        ps.pageN.put(PdfName.RESOURCES, ps.pageResources.getResources());
    }

    @Override
    protected int getNewObjectNumber(PdfReader reader, int number, int generation) {
        IntHashtable ref = this.readers2intrefs.get(reader);
        if (ref != null) {
            int n = ref.get(number);
            if (n == 0) {
                n = this.getIndirectReferenceNumber();
                ref.put(number, n);
            }
            return n;
        }
        if (this.currentPdfReaderInstance == null) {
            if (this.append && number < this.initialXrefSize) {
                return number;
            }
            int n = this.myXref.get(number);
            if (n == 0) {
                n = this.getIndirectReferenceNumber();
                this.myXref.put(number, n);
            }
            return n;
        }
        return this.currentPdfReaderInstance.getNewObjectNumber(number, generation);
    }

    @Override
    RandomAccessFileOrArray getReaderFile(PdfReader reader) {
        if (this.readers2intrefs.containsKey(reader)) {
            RandomAccessFileOrArray raf = this.readers2file.get(reader);
            if (raf != null) {
                return raf;
            }
            return reader.getSafeFile();
        }
        if (this.currentPdfReaderInstance == null) {
            return this.file;
        }
        return this.currentPdfReaderInstance.getReaderFile();
    }

    public void registerReader(PdfReader reader, boolean openFile) throws IOException {
        if (this.readers2intrefs.containsKey(reader)) {
            return;
        }
        this.readers2intrefs.put(reader, new IntHashtable());
        if (openFile) {
            RandomAccessFileOrArray raf = reader.getSafeFile();
            this.readers2file.put(reader, raf);
            raf.reOpen();
        }
    }

    public void unRegisterReader(PdfReader reader) {
        if (!this.readers2intrefs.containsKey(reader)) {
            return;
        }
        this.readers2intrefs.remove(reader);
        RandomAccessFileOrArray raf = this.readers2file.get(reader);
        if (raf == null) {
            return;
        }
        this.readers2file.remove(reader);
        try {
            raf.close();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    static void findAllObjects(PdfReader reader, PdfObject obj, IntHashtable hits) {
        if (obj == null) {
            return;
        }
        switch (obj.type()) {
            case 10: {
                PRIndirectReference iref = (PRIndirectReference)obj;
                if (reader != iref.getReader()) {
                    return;
                }
                if (hits.containsKey(iref.getNumber())) {
                    return;
                }
                hits.put(iref.getNumber(), 1);
                PdfStamperImp.findAllObjects(reader, PdfReader.getPdfObject(obj), hits);
                return;
            }
            case 5: {
                PdfArray a = (PdfArray)obj;
                for (int k = 0; k < a.size(); ++k) {
                    PdfStamperImp.findAllObjects(reader, a.getPdfObject(k), hits);
                }
                return;
            }
            case 6: 
            case 7: {
                PdfDictionary dic = (PdfDictionary)obj;
                for (PdfName name : dic.getKeys()) {
                    PdfStamperImp.findAllObjects(reader, dic.get(name), hits);
                }
                return;
            }
        }
    }

    public void addComments(FdfReader fdf) throws IOException {
        if (this.readers2intrefs.containsKey(fdf)) {
            return;
        }
        PdfDictionary catalog = fdf.getCatalog();
        if ((catalog = catalog.getAsDict(PdfName.FDF)) == null) {
            return;
        }
        PdfArray annots = catalog.getAsArray(PdfName.ANNOTS);
        if (annots == null || annots.size() == 0) {
            return;
        }
        this.registerReader(fdf, false);
        IntHashtable hits = new IntHashtable();
        HashMap<String, PdfObject> irt = new HashMap<String, PdfObject>();
        ArrayList<PdfObject> an = new ArrayList<PdfObject>();
        for (int k = 0; k < annots.size(); ++k) {
            PdfObject nm;
            PdfObject obj = annots.getPdfObject(k);
            PdfDictionary annot = (PdfDictionary)PdfReader.getPdfObject(obj);
            PdfNumber page = annot.getAsNumber(PdfName.PAGE);
            if (page == null || page.intValue() >= this.reader.getNumberOfPages()) continue;
            PdfStamperImp.findAllObjects(fdf, obj, hits);
            an.add(obj);
            if (obj.type() != 10 || (nm = PdfReader.getPdfObject(annot.get(PdfName.NM))) == null || nm.type() != 3) continue;
            irt.put(nm.toString(), obj);
        }
        int[] arhits = hits.getKeys();
        for (int n : arhits) {
            PdfObject i;
            PdfObject str;
            PdfObject obj = fdf.getPdfObject(n);
            if (obj.type() == 6 && (str = PdfReader.getPdfObject(((PdfDictionary)obj).get(PdfName.IRT))) != null && str.type() == 3 && (i = (PdfObject)irt.get(str.toString())) != null) {
                PdfDictionary dic2 = new PdfDictionary();
                dic2.merge((PdfDictionary)obj);
                dic2.put(PdfName.IRT, i);
                obj = dic2;
            }
            this.addToBody(obj, this.getNewObjectNumber(fdf, n, 0));
        }
        Object object = an.iterator();
        while (object.hasNext()) {
            Object o = object.next();
            PdfObject obj = (PdfObject)o;
            PdfDictionary annot = (PdfDictionary)PdfReader.getPdfObject(obj);
            PdfNumber page = annot.getAsNumber(PdfName.PAGE);
            PdfDictionary dic = this.reader.getPageN(page.intValue() + 1);
            PdfArray annotsp = (PdfArray)PdfReader.getPdfObject(dic.get(PdfName.ANNOTS), dic);
            if (annotsp == null) {
                annotsp = new PdfArray();
                dic.put(PdfName.ANNOTS, annotsp);
                this.markUsed(dic);
            }
            this.markUsed(annotsp);
            annotsp.add(obj);
        }
    }

    PageStamp getPageStamp(int pageNum) {
        PdfDictionary pageN = this.reader.getPageN(pageNum);
        PageStamp ps = this.pagesToContent.get(pageN);
        if (ps == null) {
            ps = new PageStamp(this, this.reader, pageN);
            this.pagesToContent.put(pageN, ps);
        }
        return ps;
    }

    PdfContentByte getUnderContent(int pageNum) {
        if (pageNum < 1 || pageNum > this.reader.getNumberOfPages()) {
            return null;
        }
        PageStamp ps = this.getPageStamp(pageNum);
        if (ps.under == null) {
            ps.under = new StampContent(this, ps);
        }
        return ps.under;
    }

    PdfContentByte getOverContent(int pageNum) {
        if (pageNum < 1 || pageNum > this.reader.getNumberOfPages()) {
            return null;
        }
        PageStamp ps = this.getPageStamp(pageNum);
        if (ps.over == null) {
            ps.over = new StampContent(this, ps);
        }
        return ps.over;
    }

    void correctAcroFieldPages(int page) {
        if (this.acroFields == null) {
            return;
        }
        if (page > this.reader.getNumberOfPages()) {
            return;
        }
        Map<String, AcroFields.Item> fields = this.acroFields.getAllFields();
        for (AcroFields.Item item : fields.values()) {
            for (int k = 0; k < item.size(); ++k) {
                int p = item.getPage(k);
                if (p < page) continue;
                item.forcePage(k, p + 1);
            }
        }
    }

    private static void moveRectangle(PdfDictionary dic2, PdfReader r, int pageImported, PdfName key, String name) {
        Rectangle m = r.getBoxSize(pageImported, name);
        if (m == null) {
            dic2.remove(key);
        } else {
            dic2.put(key, new PdfRectangle(m));
        }
    }

    void replacePage(PdfReader r, int pageImported, int pageReplaced) {
        PdfDictionary pageN = this.reader.getPageN(pageReplaced);
        if (this.pagesToContent.containsKey(pageN)) {
            throw new IllegalStateException(MessageLocalization.getComposedMessage("this.page.cannot.be.replaced.new.content.was.already.added"));
        }
        PdfImportedPage p = this.getImportedPage(r, pageImported);
        PdfDictionary dic2 = this.reader.getPageNRelease(pageReplaced);
        dic2.remove(PdfName.RESOURCES);
        dic2.remove(PdfName.CONTENTS);
        PdfStamperImp.moveRectangle(dic2, r, pageImported, PdfName.MEDIABOX, "media");
        PdfStamperImp.moveRectangle(dic2, r, pageImported, PdfName.CROPBOX, "crop");
        PdfStamperImp.moveRectangle(dic2, r, pageImported, PdfName.TRIMBOX, "trim");
        PdfStamperImp.moveRectangle(dic2, r, pageImported, PdfName.ARTBOX, "art");
        PdfStamperImp.moveRectangle(dic2, r, pageImported, PdfName.BLEEDBOX, "bleed");
        dic2.put(PdfName.ROTATE, new PdfNumber(r.getPageRotation(pageImported)));
        PdfContentByte cb = this.getOverContent(pageReplaced);
        cb.addTemplate(p, 0.0f, 0.0f);
        PageStamp ps = this.pagesToContent.get(pageN);
        ps.replacePoint = ps.over.getInternalBuffer().size();
    }

    void insertPage(int pageNumber, Rectangle mediabox) {
        PdfDictionary parent;
        PRIndirectReference parentRef;
        Rectangle media = new Rectangle(mediabox);
        int rotation = media.getRotation() % 360;
        PdfDictionary page = new PdfDictionary(PdfName.PAGE);
        PdfDictionary resources = new PdfDictionary();
        PdfArray procset = new PdfArray();
        procset.add(PdfName.PDF);
        procset.add(PdfName.TEXT);
        procset.add(PdfName.IMAGEB);
        procset.add(PdfName.IMAGEC);
        procset.add(PdfName.IMAGEI);
        resources.put(PdfName.PROCSET, procset);
        page.put(PdfName.RESOURCES, resources);
        page.put(PdfName.ROTATE, new PdfNumber(rotation));
        page.put(PdfName.MEDIABOX, new PdfRectangle(media, rotation));
        PRIndirectReference pref = this.reader.addPdfObject(page);
        if (pageNumber > this.reader.getNumberOfPages()) {
            PdfDictionary lastPage = this.reader.getPageNRelease(this.reader.getNumberOfPages());
            parentRef = (PRIndirectReference)lastPage.get(PdfName.PARENT);
            parentRef = new PRIndirectReference(this.reader, parentRef.getNumber());
            parent = (PdfDictionary)PdfReader.getPdfObject(parentRef);
            PdfArray kids = (PdfArray)PdfReader.getPdfObject(parent.get(PdfName.KIDS), parent);
            kids.add(pref);
            this.markUsed(kids);
            this.reader.pageRefs.insertPage(pageNumber, pref);
        } else {
            if (pageNumber < 1) {
                pageNumber = 1;
            }
            PdfDictionary firstPage = this.reader.getPageN(pageNumber);
            PRIndirectReference firstPageRef = this.reader.getPageOrigRef(pageNumber);
            this.reader.releasePage(pageNumber);
            parentRef = (PRIndirectReference)firstPage.get(PdfName.PARENT);
            parentRef = new PRIndirectReference(this.reader, parentRef.getNumber());
            parent = (PdfDictionary)PdfReader.getPdfObject(parentRef);
            PdfArray kids = (PdfArray)PdfReader.getPdfObject(parent.get(PdfName.KIDS), parent);
            int len = kids.size();
            int num = firstPageRef.getNumber();
            for (int k = 0; k < len; ++k) {
                PRIndirectReference cur = (PRIndirectReference)kids.getPdfObject(k);
                if (num != cur.getNumber()) continue;
                kids.add(k, pref);
                break;
            }
            if (len == kids.size()) {
                throw new RuntimeException(MessageLocalization.getComposedMessage("internal.inconsistence"));
            }
            this.markUsed(kids);
            this.reader.pageRefs.insertPage(pageNumber, pref);
            this.correctAcroFieldPages(pageNumber);
        }
        page.put(PdfName.PARENT, parentRef);
        while (parent != null) {
            this.markUsed(parent);
            PdfNumber count = (PdfNumber)PdfReader.getPdfObjectRelease(parent.get(PdfName.COUNT));
            parent.put(PdfName.COUNT, new PdfNumber(count.intValue() + 1));
            parent = parent.getAsDict(PdfName.PARENT);
        }
    }

    boolean isRotateContents() {
        return this.rotateContents;
    }

    void setRotateContents(boolean rotateContents) {
        this.rotateContents = rotateContents;
    }

    boolean isContentWritten() {
        return this.body.size() > 1;
    }

    AcroFields getAcroFields() {
        if (this.acroFields == null) {
            this.acroFields = new AcroFields(this.reader, this);
        }
        return this.acroFields;
    }

    void setFormFlattening(boolean flat) {
        this.flat = flat;
    }

    void setFreeTextFlattening(boolean flat) {
        this.flatFreeText = flat;
    }

    boolean partialFormFlattening(String name) {
        this.getAcroFields();
        if (this.acroFields.getXfa().isXfaPresent()) {
            throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("partial.form.flattening.is.not.supported.with.xfa.forms"));
        }
        if (!this.acroFields.getAllFields().containsKey(name)) {
            return false;
        }
        this.partialFlattening.add(name);
        return true;
    }

    void flatFields() {
        if (this.append) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("field.flattening.is.not.supported.in.append.mode"));
        }
        this.getAcroFields();
        Map<String, AcroFields.Item> fields = this.acroFields.getAllFields();
        if (this.fieldsAdded && this.partialFlattening.isEmpty()) {
            this.partialFlattening.addAll(fields.keySet());
        }
        PdfDictionary acroForm = this.reader.getCatalog().getAsDict(PdfName.ACROFORM);
        PdfArray acroFds = null;
        if (acroForm != null) {
            acroFds = (PdfArray)PdfReader.getPdfObject(acroForm.get(PdfName.FIELDS), acroForm);
        }
        for (Map.Entry<String, AcroFields.Item> entry : fields.entrySet()) {
            String name = entry.getKey();
            if (!this.partialFlattening.isEmpty() && !this.partialFlattening.contains(name)) continue;
            AcroFields.Item item = entry.getValue();
            for (int k = 0; k < item.size(); ++k) {
                PdfDictionary pageDic;
                PdfArray annots;
                PdfDictionary merged = item.getMerged(k);
                PdfNumber ff = merged.getAsNumber(PdfName.F);
                int flags = 0;
                if (ff != null) {
                    flags = ff.intValue();
                }
                int page = item.getPage(k);
                PdfDictionary appDic = merged.getAsDict(PdfName.AP);
                if (appDic != null && (flags & 4) != 0 && (flags & 2) == 0) {
                    PdfObject obj = appDic.get(PdfName.N);
                    PdfAppearance app = null;
                    if (obj != null) {
                        PdfIndirectReference iref;
                        PdfName as;
                        PdfObject objReal = PdfReader.getPdfObject(obj);
                        if (obj instanceof PdfIndirectReference && !obj.isIndirect()) {
                            app = new PdfAppearance((PdfIndirectReference)obj);
                        } else if (objReal instanceof PdfStream) {
                            ((PdfDictionary)objReal).put(PdfName.SUBTYPE, PdfName.FORM);
                            app = new PdfAppearance((PdfIndirectReference)obj);
                        } else if (objReal != null && objReal.isDictionary() && (as = merged.getAsName(PdfName.AS)) != null && (iref = (PdfIndirectReference)((PdfDictionary)objReal).get(as)) != null) {
                            app = new PdfAppearance(iref);
                            if (iref.isIndirect()) {
                                objReal = PdfReader.getPdfObject(iref);
                                ((PdfDictionary)objReal).put(PdfName.SUBTYPE, PdfName.FORM);
                            }
                        }
                    }
                    if (app != null) {
                        Rectangle box = PdfReader.getNormalizedRectangle(merged.getAsArray(PdfName.RECT));
                        PdfContentByte cb = this.getOverContent(page);
                        cb.setLiteral("Q ");
                        cb.addTemplate(app, box.getLeft(), box.getBottom());
                        cb.setLiteral("q ");
                    }
                }
                if (this.partialFlattening.isEmpty() || (annots = (pageDic = this.reader.getPageN(page)).getAsArray(PdfName.ANNOTS)) == null) continue;
                block2: for (int idx = 0; idx < annots.size(); ++idx) {
                    PdfIndirectReference ran2;
                    PdfObject ran = annots.getPdfObject(idx);
                    if (!ran.isIndirect() || !(ran2 = item.getWidgetRef(k)).isIndirect() || ((PRIndirectReference)ran).getNumber() != ((PRIndirectReference)ran2).getNumber()) continue;
                    annots.remove(idx--);
                    PRIndirectReference wdref = (PRIndirectReference)ran2;
                    while (true) {
                        PdfDictionary wd = (PdfDictionary)PdfReader.getPdfObject(wdref);
                        PRIndirectReference parentRef = (PRIndirectReference)wd.get(PdfName.PARENT);
                        PdfReader.killIndirect(wdref);
                        if (parentRef == null) {
                            for (int fr = 0; fr < acroFds.size(); ++fr) {
                                PdfObject h = acroFds.getPdfObject(fr);
                                if (!h.isIndirect() || ((PRIndirectReference)h).getNumber() != wdref.getNumber()) continue;
                                acroFds.remove(fr);
                                --fr;
                            }
                            continue block2;
                        }
                        PdfDictionary parent = (PdfDictionary)PdfReader.getPdfObject(parentRef);
                        PdfArray kids = parent.getAsArray(PdfName.KIDS);
                        for (int fr = 0; fr < kids.size(); ++fr) {
                            PdfObject h = kids.getPdfObject(fr);
                            if (!h.isIndirect() || ((PRIndirectReference)h).getNumber() != wdref.getNumber()) continue;
                            kids.remove(fr);
                            --fr;
                        }
                        if (!kids.isEmpty()) continue block2;
                        wdref = parentRef;
                    }
                }
                if (!annots.isEmpty()) continue;
                PdfReader.killIndirect(pageDic.get(PdfName.ANNOTS));
                pageDic.remove(PdfName.ANNOTS);
            }
        }
        if (!this.fieldsAdded && this.partialFlattening.isEmpty()) {
            for (int page = 1; page <= this.reader.getNumberOfPages(); ++page) {
                PdfDictionary pageDic = this.reader.getPageN(page);
                PdfArray annots = pageDic.getAsArray(PdfName.ANNOTS);
                if (annots == null) continue;
                for (int idx = 0; idx < annots.size(); ++idx) {
                    PdfObject annoto = annots.getDirectObject(idx);
                    if (annoto instanceof PdfIndirectReference && !annoto.isIndirect() || annoto.isDictionary() && !PdfName.WIDGET.equals(((PdfDictionary)annoto).get(PdfName.SUBTYPE))) continue;
                    annots.remove(idx);
                    --idx;
                }
                if (!annots.isEmpty()) continue;
                PdfReader.killIndirect(pageDic.get(PdfName.ANNOTS));
                pageDic.remove(PdfName.ANNOTS);
            }
            this.eliminateAcroformObjects();
        }
    }

    void eliminateAcroformObjects() {
        PdfObject acro = this.reader.getCatalog().get(PdfName.ACROFORM);
        if (acro == null) {
            return;
        }
        PdfDictionary acrodic = (PdfDictionary)PdfReader.getPdfObject(acro);
        this.reader.killXref(acrodic.get(PdfName.XFA));
        acrodic.remove(PdfName.XFA);
        PdfObject iFields = acrodic.get(PdfName.FIELDS);
        if (iFields != null) {
            PdfDictionary kids = new PdfDictionary();
            kids.put(PdfName.KIDS, iFields);
            this.sweepKids(kids);
            PdfReader.killIndirect(iFields);
            acrodic.put(PdfName.FIELDS, new PdfArray());
        }
        acrodic.remove(PdfName.SIGFLAGS);
    }

    void sweepKids(PdfObject obj) {
        PdfObject oo = PdfReader.killIndirect(obj);
        if (oo == null || !oo.isDictionary()) {
            return;
        }
        PdfDictionary dic = (PdfDictionary)oo;
        PdfArray kids = (PdfArray)PdfReader.killIndirect(dic.get(PdfName.KIDS));
        if (kids == null) {
            return;
        }
        for (int k = 0; k < kids.size(); ++k) {
            this.sweepKids(kids.getPdfObject(k));
        }
    }

    private void flatFreeTextFields() {
        if (this.append) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("freetext.flattening.is.not.supported.in.append.mode"));
        }
        for (int page = 1; page <= this.reader.getNumberOfPages(); ++page) {
            int idx;
            PdfDictionary pageDic = this.reader.getPageN(page);
            PdfArray annots = pageDic.getAsArray(PdfName.ANNOTS);
            if (annots == null) continue;
            for (idx = 0; idx < annots.size(); ++idx) {
                PdfIndirectReference iref;
                PdfName as_p;
                PdfObject obj1;
                int flags;
                PdfDictionary annDic;
                PdfObject annoto = annots.getDirectObject(idx);
                if (annoto instanceof PdfIndirectReference && !annoto.isIndirect() || !(annDic = (PdfDictionary)annoto).get(PdfName.SUBTYPE).equals(PdfName.FREETEXT)) continue;
                PdfNumber ff = annDic.getAsNumber(PdfName.F);
                int n = flags = ff != null ? ff.intValue() : 0;
                if ((flags & 4) == 0 || (flags & 2) != 0 || (obj1 = annDic.get(PdfName.AP)) == null) continue;
                PdfDictionary appDic = obj1 instanceof PdfIndirectReference ? (PdfDictionary)PdfReader.getPdfObject(obj1) : (PdfDictionary)obj1;
                PdfObject obj = appDic.get(PdfName.N);
                PdfAppearance app = null;
                PdfObject objReal = PdfReader.getPdfObject(obj);
                if (obj instanceof PdfIndirectReference && !obj.isIndirect()) {
                    app = new PdfAppearance((PdfIndirectReference)obj);
                } else if (objReal instanceof PdfStream) {
                    ((PdfDictionary)objReal).put(PdfName.SUBTYPE, PdfName.FORM);
                    app = new PdfAppearance((PdfIndirectReference)obj);
                } else if (objReal.isDictionary() && (as_p = appDic.getAsName(PdfName.AS)) != null && (iref = (PdfIndirectReference)((PdfDictionary)objReal).get(as_p)) != null) {
                    app = new PdfAppearance(iref);
                    if (iref.isIndirect()) {
                        objReal = PdfReader.getPdfObject(iref);
                        ((PdfDictionary)objReal).put(PdfName.SUBTYPE, PdfName.FORM);
                    }
                }
                if (app == null) continue;
                Rectangle box = PdfReader.getNormalizedRectangle(annDic.getAsArray(PdfName.RECT));
                PdfContentByte cb = this.getOverContent(page);
                cb.setLiteral("Q ");
                cb.addTemplate(app, box.getLeft(), box.getBottom());
                cb.setLiteral("q ");
            }
            for (idx = 0; idx < annots.size(); ++idx) {
                PdfDictionary annot = annots.getAsDict(idx);
                if (annot == null || !PdfName.FREETEXT.equals(annot.get(PdfName.SUBTYPE))) continue;
                annots.remove(idx);
                --idx;
            }
            if (!annots.isEmpty()) continue;
            PdfReader.killIndirect(pageDic.get(PdfName.ANNOTS));
            pageDic.remove(PdfName.ANNOTS);
        }
    }

    @Override
    public PdfIndirectReference getPageReference(int page) {
        PRIndirectReference ref = this.reader.getPageOrigRef(page);
        if (ref == null) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("invalid.page.number.1", page));
        }
        return ref;
    }

    @Override
    public void addAnnotation(PdfAnnotation annot) {
        throw new RuntimeException(MessageLocalization.getComposedMessage("unsupported.in.this.context.use.pdfstamper.addannotation"));
    }

    void addDocumentField(PdfIndirectReference ref) {
        PdfArray fields;
        PdfDictionary catalog = this.reader.getCatalog();
        PdfDictionary acroForm = (PdfDictionary)PdfReader.getPdfObject(catalog.get(PdfName.ACROFORM), catalog);
        if (acroForm == null) {
            acroForm = new PdfDictionary();
            catalog.put(PdfName.ACROFORM, acroForm);
            this.markUsed(catalog);
        }
        if ((fields = (PdfArray)PdfReader.getPdfObject(acroForm.get(PdfName.FIELDS), acroForm)) == null) {
            fields = new PdfArray();
            acroForm.put(PdfName.FIELDS, fields);
            this.markUsed(acroForm);
        }
        if (!acroForm.contains(PdfName.DA)) {
            acroForm.put(PdfName.DA, new PdfString("/Helv 0 Tf 0 g "));
            this.markUsed(acroForm);
        }
        fields.add(ref);
        this.markUsed(fields);
    }

    void addFieldResources() throws IOException {
        PdfDictionary dic;
        PdfDictionary dr;
        if (this.fieldTemplates.isEmpty()) {
            return;
        }
        PdfDictionary catalog = this.reader.getCatalog();
        PdfDictionary acroForm = (PdfDictionary)PdfReader.getPdfObject(catalog.get(PdfName.ACROFORM), catalog);
        if (acroForm == null) {
            acroForm = new PdfDictionary();
            catalog.put(PdfName.ACROFORM, acroForm);
            this.markUsed(catalog);
        }
        if ((dr = (PdfDictionary)PdfReader.getPdfObject(acroForm.get(PdfName.DR), acroForm)) == null) {
            dr = new PdfDictionary();
            acroForm.put(PdfName.DR, dr);
            this.markUsed(acroForm);
        }
        this.markUsed(dr);
        for (PdfTemplate template : this.fieldTemplates.keySet()) {
            PdfFormField.mergeResources(dr, (PdfDictionary)template.getResources(), this);
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
        if (acroForm.get(PdfName.DA) == null) {
            acroForm.put(PdfName.DA, new PdfString("/Helv 0 Tf 0 g "));
            this.markUsed(acroForm);
        }
    }

    void expandFields(PdfFormField field, List<PdfAnnotation> annotations) {
        annotations.add(field);
        List<PdfFormField> kids = field.getKidFields();
        if (kids != null) {
            for (PdfFormField kid : kids) {
                this.expandFields(kid, annotations);
            }
        }
    }

    void addAnnotation(PdfAnnotation annotation, PdfDictionary pageN) {
        try {
            ArrayList<PdfAnnotation> annotations = new ArrayList<PdfAnnotation>();
            if (annotation.isForm()) {
                this.fieldsAdded = true;
                this.getAcroFields();
                PdfFormField field = (PdfFormField)annotation;
                if (field.getParent() != null) {
                    return;
                }
                this.expandFields(field, annotations);
            } else {
                annotations.add(annotation);
            }
            for (PdfAnnotation pdfAnnotation : annotations) {
                annotation = pdfAnnotation;
                if (annotation.getPlaceInPage() > 0) {
                    pageN = this.reader.getPageN(annotation.getPlaceInPage());
                }
                if (annotation.isForm()) {
                    PdfFormField field;
                    HashMap<PdfTemplate, Object> templates;
                    if (!annotation.isUsed() && (templates = annotation.getTemplates()) != null) {
                        this.fieldTemplates.putAll(templates);
                    }
                    if ((field = (PdfFormField)annotation).getParent() == null) {
                        this.addDocumentField(field.getIndirectReference());
                    }
                }
                if (annotation.isAnnotation()) {
                    PdfRectangle rect;
                    PdfObject pdfobj = PdfReader.getPdfObject(pageN.get(PdfName.ANNOTS), pageN);
                    PdfArray annots = null;
                    if (pdfobj == null || !pdfobj.isArray()) {
                        annots = new PdfArray();
                        pageN.put(PdfName.ANNOTS, annots);
                        this.markUsed(pageN);
                    } else {
                        annots = (PdfArray)pdfobj;
                    }
                    annots.add(annotation.getIndirectReference());
                    this.markUsed(annots);
                    if (!(annotation.isUsed() || (rect = (PdfRectangle)annotation.get(PdfName.RECT)) == null || rect.left() == 0.0f && rect.right() == 0.0f && rect.top() == 0.0f && rect.bottom() == 0.0f)) {
                        int rotation = this.reader.getPageRotation(pageN);
                        Rectangle pageSize = this.reader.getPageSizeWithRotation(pageN);
                        switch (rotation) {
                            case 90: {
                                annotation.put(PdfName.RECT, new PdfRectangle(pageSize.getTop() - rect.top(), rect.right(), pageSize.getTop() - rect.bottom(), rect.left()));
                                break;
                            }
                            case 180: {
                                annotation.put(PdfName.RECT, new PdfRectangle(pageSize.getRight() - rect.left(), pageSize.getTop() - rect.bottom(), pageSize.getRight() - rect.right(), pageSize.getTop() - rect.top()));
                                break;
                            }
                            case 270: {
                                annotation.put(PdfName.RECT, new PdfRectangle(rect.bottom(), pageSize.getRight() - rect.left(), rect.top(), pageSize.getRight() - rect.right()));
                            }
                        }
                    }
                }
                if (annotation.isUsed()) continue;
                annotation.setUsed();
                this.addToBody((PdfObject)annotation, annotation.getIndirectReference());
            }
        }
        catch (IOException e) {
            throw new ExceptionConverter(e);
        }
    }

    @Override
    void addAnnotation(PdfAnnotation annot, int page) {
        annot.setPage(page);
        this.addAnnotation(annot, this.reader.getPageN(page));
    }

    private void outlineTravel(PRIndirectReference outline) {
        while (outline != null) {
            PdfDictionary outlineR = (PdfDictionary)PdfReader.getPdfObjectRelease(outline);
            PRIndirectReference first = (PRIndirectReference)outlineR.get(PdfName.FIRST);
            if (first != null) {
                this.outlineTravel(first);
            }
            PdfReader.killIndirect(outlineR.get(PdfName.DEST));
            PdfReader.killIndirect(outlineR.get(PdfName.A));
            PdfReader.killIndirect(outline);
            outline = (PRIndirectReference)outlineR.get(PdfName.NEXT);
        }
    }

    void deleteOutlines() {
        PdfDictionary catalog = this.reader.getCatalog();
        PRIndirectReference outlines = (PRIndirectReference)catalog.get(PdfName.OUTLINES);
        if (outlines == null) {
            return;
        }
        this.outlineTravel(outlines);
        PdfReader.killIndirect(outlines);
        catalog.remove(PdfName.OUTLINES);
        this.markUsed(catalog);
    }

    void setJavaScript() throws IOException {
        HashMap<String, PdfIndirectReference> djs = this.pdf.getDocumentLevelJS();
        if (djs.isEmpty()) {
            return;
        }
        PdfDictionary catalog = this.reader.getCatalog();
        PdfDictionary names = (PdfDictionary)PdfReader.getPdfObject(catalog.get(PdfName.NAMES), catalog);
        if (names == null) {
            names = new PdfDictionary();
            catalog.put(PdfName.NAMES, names);
            this.markUsed(catalog);
        }
        this.markUsed(names);
        PdfDictionary tree = PdfNameTree.writeTree(djs, (PdfWriter)this);
        names.put(PdfName.JAVASCRIPT, this.addToBody(tree).getIndirectReference());
    }

    void addFileAttachments() throws IOException {
        HashMap<String, PdfIndirectReference> fs = this.pdf.getDocumentFileAttachment();
        if (fs.isEmpty()) {
            return;
        }
        PdfDictionary catalog = this.reader.getCatalog();
        PdfDictionary names = (PdfDictionary)PdfReader.getPdfObject(catalog.get(PdfName.NAMES), catalog);
        if (names == null) {
            names = new PdfDictionary();
            catalog.put(PdfName.NAMES, names);
            this.markUsed(catalog);
        }
        this.markUsed(names);
        HashMap<String, PdfObject> old = PdfNameTree.readTree((PdfDictionary)PdfReader.getPdfObjectRelease(names.get(PdfName.EMBEDDEDFILES)));
        for (Map.Entry entry : fs.entrySet()) {
            String name = (String)entry.getKey();
            int k = 0;
            String nn = name;
            while (old.containsKey(nn)) {
                nn = nn + " " + ++k;
            }
            old.put(nn, (PdfObject)entry.getValue());
        }
        PdfDictionary tree = PdfNameTree.writeTree(old, (PdfWriter)this);
        PdfObject oldEmbeddedFiles = names.get(PdfName.EMBEDDEDFILES);
        if (oldEmbeddedFiles != null) {
            PdfReader.killIndirect(oldEmbeddedFiles);
        }
        names.put(PdfName.EMBEDDEDFILES, this.addToBody(tree).getIndirectReference());
    }

    void makePackage(PdfCollection collection) {
        PdfDictionary catalog = this.reader.getCatalog();
        catalog.put(PdfName.COLLECTION, collection);
    }

    void setOutlines() throws IOException {
        if (this.newBookmarks == null) {
            return;
        }
        this.deleteOutlines();
        if (this.newBookmarks.isEmpty()) {
            return;
        }
        PdfDictionary catalog = this.reader.getCatalog();
        boolean namedAsNames = catalog.get(PdfName.DESTS) != null;
        this.writeOutlines(catalog, namedAsNames);
        this.markUsed(catalog);
    }

    @Override
    public void setViewerPreferences(int preferences) {
        this.useVp = true;
        this.viewerPreferences.setViewerPreferences(preferences);
    }

    @Override
    public void addViewerPreference(PdfName key, PdfObject value) {
        this.useVp = true;
        this.viewerPreferences.addViewerPreference(key, value);
    }

    @Override
    public void setSigFlags(int f) {
        this.sigFlags |= f;
    }

    @Override
    public void setPageAction(PdfName actionType, PdfAction action) throws PdfException {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("use.setpageaction.pdfname.actiontype.pdfaction.action.int.page"));
    }

    void setPageAction(PdfName actionType, PdfAction action, int page) throws PdfException {
        if (!actionType.equals(PAGE_OPEN) && !actionType.equals(PAGE_CLOSE)) {
            throw new PdfException(MessageLocalization.getComposedMessage("invalid.page.additional.action.type.1", actionType.toString()));
        }
        PdfDictionary pg = this.reader.getPageN(page);
        PdfDictionary aa = (PdfDictionary)PdfReader.getPdfObject(pg.get(PdfName.AA), pg);
        if (aa == null) {
            aa = new PdfDictionary();
            pg.put(PdfName.AA, aa);
            this.markUsed(pg);
        }
        aa.put(actionType, action);
        this.markUsed(aa);
    }

    @Override
    public void setDuration(int seconds) {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("use.setpageaction.pdfname.actiontype.pdfaction.action.int.page"));
    }

    @Override
    public void setTransition(PdfTransition transition) {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("use.setpageaction.pdfname.actiontype.pdfaction.action.int.page"));
    }

    void setDuration(int seconds, int page) {
        PdfDictionary pg = this.reader.getPageN(page);
        if (seconds < 0) {
            pg.remove(PdfName.DUR);
        } else {
            pg.put(PdfName.DUR, new PdfNumber(seconds));
        }
        this.markUsed(pg);
    }

    void setTransition(PdfTransition transition, int page) {
        PdfDictionary pg = this.reader.getPageN(page);
        if (transition == null) {
            pg.remove(PdfName.TRANS);
        } else {
            pg.put(PdfName.TRANS, transition.getTransitionDictionary());
        }
        this.markUsed(pg);
    }

    protected void markUsed(PdfObject obj) {
        if (this.append && obj != null) {
            PRIndirectReference ref = null;
            ref = obj.type() == 10 ? (PRIndirectReference)obj : obj.getIndRef();
            if (ref != null) {
                this.marked.put(ref.getNumber(), 1);
            }
        }
    }

    protected void markUsed(int num) {
        if (this.append) {
            this.marked.put(num, 1);
        }
    }

    boolean isAppend() {
        return this.append;
    }

    @Override
    public void setAdditionalAction(PdfName actionType, PdfAction action) throws PdfException {
        if (!(actionType.equals(DOCUMENT_CLOSE) || actionType.equals(WILL_SAVE) || actionType.equals(DID_SAVE) || actionType.equals(WILL_PRINT) || actionType.equals(DID_PRINT))) {
            throw new PdfException(MessageLocalization.getComposedMessage("invalid.additional.action.type.1", actionType.toString()));
        }
        PdfDictionary aa = this.reader.getCatalog().getAsDict(PdfName.AA);
        if (aa == null) {
            if (action == null) {
                return;
            }
            aa = new PdfDictionary();
            this.reader.getCatalog().put(PdfName.AA, aa);
        }
        this.markUsed(aa);
        if (action == null) {
            aa.remove(actionType);
        } else {
            aa.put(actionType, action);
        }
    }

    @Override
    public void setOpenAction(PdfAction action) {
        this.openAction = action;
    }

    @Override
    public void setOpenAction(String name) {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("open.actions.by.name.are.not.supported"));
    }

    @Override
    public void setThumbnail(Image image) {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("use.pdfstamper.setthumbnail"));
    }

    void setThumbnail(Image image, int page) throws DocumentException {
        PdfIndirectReference thumb = this.getImageReference(this.addDirectImageSimple(image));
        this.reader.resetReleasePage();
        PdfDictionary dic = this.reader.getPageN(page);
        dic.put(PdfName.THUMB, thumb);
        this.reader.resetReleasePage();
    }

    @Override
    public PdfContentByte getDirectContentUnder() {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("use.pdfstamper.getundercontent.or.pdfstamper.getovercontent"));
    }

    @Override
    public PdfContentByte getDirectContent() {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("use.pdfstamper.getundercontent.or.pdfstamper.getovercontent"));
    }

    protected void readOCProperties() {
        PdfArray order;
        PdfLayer layer;
        PdfIndirectReference ref;
        if (!this.documentOCG.isEmpty()) {
            return;
        }
        PdfDictionary dict = this.reader.getCatalog().getAsDict(PdfName.OCPROPERTIES);
        if (dict == null) {
            return;
        }
        PdfArray ocgs = dict.getAsArray(PdfName.OCGS);
        HashMap<String, PdfLayer> ocgmap = new HashMap<String, PdfLayer>();
        for (PdfObject pdfObject : ocgs.getElements()) {
            ref = (PdfIndirectReference)pdfObject;
            layer = new PdfLayer(null);
            layer.setRef(ref);
            layer.setOnPanel(false);
            layer.merge((PdfDictionary)PdfReader.getPdfObject(ref));
            ocgmap.put(ref.toString(), layer);
        }
        PdfDictionary d = dict.getAsDict(PdfName.D);
        PdfArray off = d.getAsArray(PdfName.OFF);
        if (off != null) {
            for (PdfObject pdfObject : off.getElements()) {
                ref = (PdfIndirectReference)pdfObject;
                layer = (PdfLayer)ocgmap.get(ref.toString());
                layer.setOn(false);
            }
        }
        if ((order = d.getAsArray(PdfName.ORDER)) != null) {
            this.addOrder(null, order, ocgmap);
        }
        this.documentOCG.addAll(ocgmap.values());
        this.OCGRadioGroup = d.getAsArray(PdfName.RBGROUPS);
        this.OCGLocked = d.getAsArray(PdfName.LOCKED);
        if (this.OCGLocked == null) {
            this.OCGLocked = new PdfArray();
        }
    }

    private void addOrder(PdfLayer parent, PdfArray arr, Map ocgmap) {
        for (int i = 0; i < arr.size(); ++i) {
            PdfLayer layer;
            PdfObject obj = arr.getPdfObject(i);
            if (obj.isIndirect()) {
                layer = (PdfLayer)ocgmap.get(obj.toString());
                layer.setOnPanel(true);
                this.registerLayer(layer);
                if (parent != null) {
                    parent.addChild(layer);
                }
                if (arr.size() <= i + 1 || !arr.getPdfObject(i + 1).isArray()) continue;
                this.addOrder(layer, (PdfArray)arr.getPdfObject(++i), ocgmap);
                continue;
            }
            if (!obj.isArray()) continue;
            PdfArray sub = (PdfArray)obj;
            if (sub.isEmpty()) {
                return;
            }
            obj = sub.getPdfObject(0);
            if (obj.isString()) {
                layer = new PdfLayer(obj.toString());
                layer.setOnPanel(true);
                this.registerLayer(layer);
                if (parent != null) {
                    parent.addChild(layer);
                }
                PdfArray array = new PdfArray();
                sub.getElements().forEach(array::add);
                this.addOrder(layer, array, ocgmap);
                continue;
            }
            this.addOrder(parent, (PdfArray)obj, ocgmap);
        }
    }

    public Map getPdfLayers() {
        if (this.documentOCG.isEmpty()) {
            this.readOCProperties();
        }
        HashMap<String, PdfLayer> map = new HashMap<String, PdfLayer>();
        for (Object o : this.documentOCG) {
            PdfLayer layer = (PdfLayer)o;
            String key = layer.getTitle() == null ? layer.getAsString(PdfName.NAME).toString() : layer.getTitle();
            if (map.containsKey(key)) {
                int seq = 2;
                String tmp = key + "(" + seq + ")";
                while (map.containsKey(tmp)) {
                    tmp = key + "(" + ++seq + ")";
                }
                key = tmp;
            }
            map.put(key, layer);
        }
        return map;
    }

    public boolean isIncludeFileID() {
        return this.includeFileID;
    }

    public void setIncludeFileID(boolean includeFileID) {
        this.includeFileID = includeFileID;
    }

    public PdfObject getOverrideFileId() {
        return this.overrideFileId;
    }

    public void setOverrideFileId(PdfObject overrideFileId) {
        this.overrideFileId = overrideFileId;
    }

    public Calendar getModificationDate() {
        return this.modificationDate;
    }

    public void setModificationDate(Calendar modificationDate) {
        this.modificationDate = modificationDate;
    }

    static class PageStamp {
        PdfDictionary pageN;
        StampContent under;
        StampContent over;
        PageResources pageResources;
        int replacePoint = 0;

        PageStamp(PdfStamperImp stamper, PdfReader reader, PdfDictionary pageN) {
            this.pageN = pageN;
            this.pageResources = new PageResources();
            PdfDictionary resources = pageN.getAsDict(PdfName.RESOURCES);
            this.pageResources.setOriginalResources(resources, stamper.namePtr);
        }
    }
}

