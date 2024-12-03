/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocListener;
import com.lowagie.text.DocWriter;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Image;
import com.lowagie.text.ImgJBIG2;
import com.lowagie.text.ImgWMF;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Table;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ByteBuffer;
import com.lowagie.text.pdf.ColorDetails;
import com.lowagie.text.pdf.DocumentFont;
import com.lowagie.text.pdf.ExtendedColor;
import com.lowagie.text.pdf.FontDetails;
import com.lowagie.text.pdf.OutputStreamCounter;
import com.lowagie.text.pdf.PRIndirectReference;
import com.lowagie.text.pdf.PRStream;
import com.lowagie.text.pdf.PdfAcroForm;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfContents;
import com.lowagie.text.pdf.PdfDestination;
import com.lowagie.text.pdf.PdfDeveloperExtension;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfDocument;
import com.lowagie.text.pdf.PdfEncryption;
import com.lowagie.text.pdf.PdfException;
import com.lowagie.text.pdf.PdfFileSpecification;
import com.lowagie.text.pdf.PdfFormField;
import com.lowagie.text.pdf.PdfICCBased;
import com.lowagie.text.pdf.PdfImage;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfIndirectObject;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfLayer;
import com.lowagie.text.pdf.PdfLayerMembership;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfOCG;
import com.lowagie.text.pdf.PdfOCProperties;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfOutline;
import com.lowagie.text.pdf.PdfPage;
import com.lowagie.text.pdf.PdfPageEvent;
import com.lowagie.text.pdf.PdfPageLabels;
import com.lowagie.text.pdf.PdfPages;
import com.lowagie.text.pdf.PdfPatternPainter;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfReaderInstance;
import com.lowagie.text.pdf.PdfShading;
import com.lowagie.text.pdf.PdfShadingPattern;
import com.lowagie.text.pdf.PdfSpotColor;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfStructureTreeRoot;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfTransition;
import com.lowagie.text.pdf.PdfXConformanceException;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.lowagie.text.pdf.SimpleBookmark;
import com.lowagie.text.pdf.SpotColor;
import com.lowagie.text.pdf.collection.PdfCollection;
import com.lowagie.text.pdf.events.PdfPageEventForwarder;
import com.lowagie.text.pdf.interfaces.PdfAnnotations;
import com.lowagie.text.pdf.interfaces.PdfDocumentActions;
import com.lowagie.text.pdf.interfaces.PdfEncryptionSettings;
import com.lowagie.text.pdf.interfaces.PdfPageActions;
import com.lowagie.text.pdf.interfaces.PdfRunDirection;
import com.lowagie.text.pdf.interfaces.PdfVersion;
import com.lowagie.text.pdf.interfaces.PdfViewerPreferences;
import com.lowagie.text.pdf.interfaces.PdfXConformance;
import com.lowagie.text.pdf.internal.PdfVersionImp;
import com.lowagie.text.pdf.internal.PdfXConformanceImp;
import com.lowagie.text.xml.xmp.XmpWriter;
import java.awt.Color;
import java.awt.color.ICC_Profile;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class PdfWriter
extends DocWriter
implements PdfViewerPreferences,
PdfEncryptionSettings,
PdfVersion,
PdfDocumentActions,
PdfPageActions,
PdfXConformance,
PdfRunDirection,
PdfAnnotations {
    public static final int GENERATION_MAX = 65535;
    protected PdfDocument pdf;
    protected PdfContentByte directContent;
    protected PdfContentByte directContentUnder;
    protected PdfBody body;
    protected PdfDictionary extraCatalog;
    protected PdfPages root = new PdfPages(this);
    protected ArrayList<PdfIndirectReference> pageReferences = new ArrayList();
    protected int currentPageNumber = 1;
    protected PdfName tabs = null;
    private PdfPageEvent pageEvent;
    protected int prevxref = 0;
    protected List newBookmarks;
    public static final char VERSION_1_2 = '2';
    public static final char VERSION_1_3 = '3';
    public static final char VERSION_1_4 = '4';
    public static final char VERSION_1_5 = '5';
    public static final char VERSION_1_6 = '6';
    public static final char VERSION_1_7 = '7';
    public static final PdfName PDF_VERSION_1_2 = new PdfName("1.2");
    public static final PdfName PDF_VERSION_1_3 = new PdfName("1.3");
    public static final PdfName PDF_VERSION_1_4 = new PdfName("1.4");
    public static final PdfName PDF_VERSION_1_5 = new PdfName("1.5");
    public static final PdfName PDF_VERSION_1_6 = new PdfName("1.6");
    public static final PdfName PDF_VERSION_1_7 = new PdfName("1.7");
    protected PdfVersionImp pdf_version = new PdfVersionImp();
    public static final int PageLayoutSinglePage = 1;
    public static final int PageLayoutOneColumn = 2;
    public static final int PageLayoutTwoColumnLeft = 4;
    public static final int PageLayoutTwoColumnRight = 8;
    public static final int PageLayoutTwoPageLeft = 16;
    public static final int PageLayoutTwoPageRight = 32;
    public static final int PageModeUseNone = 64;
    public static final int PageModeUseOutlines = 128;
    public static final int PageModeUseThumbs = 256;
    public static final int PageModeFullScreen = 512;
    public static final int PageModeUseOC = 1024;
    public static final int PageModeUseAttachments = 2048;
    public static final int HideToolbar = 4096;
    public static final int HideMenubar = 8192;
    public static final int HideWindowUI = 16384;
    public static final int FitWindow = 32768;
    public static final int CenterWindow = 65536;
    public static final int DisplayDocTitle = 131072;
    public static final int NonFullScreenPageModeUseNone = 262144;
    public static final int NonFullScreenPageModeUseOutlines = 524288;
    public static final int NonFullScreenPageModeUseThumbs = 0x100000;
    public static final int NonFullScreenPageModeUseOC = 0x200000;
    public static final int DirectionL2R = 0x400000;
    public static final int DirectionR2L = 0x800000;
    public static final int PrintScalingNone = 0x1000000;
    public static final PdfName DOCUMENT_CLOSE = PdfName.WC;
    public static final PdfName WILL_SAVE = PdfName.WS;
    public static final PdfName DID_SAVE = PdfName.DS;
    public static final PdfName WILL_PRINT = PdfName.WP;
    public static final PdfName DID_PRINT = PdfName.DP;
    public static final int SIGNATURE_EXISTS = 1;
    public static final int SIGNATURE_APPEND_ONLY = 2;
    protected byte[] xmpMetadata = null;
    public static final int PDFXNONE = 0;
    public static final int PDFX1A2001 = 1;
    public static final int PDFX32002 = 2;
    public static final int PDFA1A = 3;
    public static final int PDFA1B = 4;
    private PdfXConformanceImp pdfxConformance = new PdfXConformanceImp();
    public static final int STANDARD_ENCRYPTION_40 = 0;
    public static final int STANDARD_ENCRYPTION_128 = 1;
    public static final int ENCRYPTION_AES_128 = 2;
    static final int ENCRYPTION_MASK = 7;
    public static final int DO_NOT_ENCRYPT_METADATA = 8;
    public static final int EMBEDDED_FILES_ONLY = 24;
    public static final int ALLOW_PRINTING = 2052;
    public static final int ALLOW_MODIFY_CONTENTS = 8;
    public static final int ALLOW_COPY = 16;
    public static final int ALLOW_MODIFY_ANNOTATIONS = 32;
    public static final int ALLOW_FILL_IN = 256;
    public static final int ALLOW_SCREENREADERS = 512;
    public static final int ALLOW_ASSEMBLY = 1024;
    public static final int ALLOW_DEGRADED_PRINTING = 4;
    public static final int AllowPrinting = 2052;
    public static final int AllowModifyContents = 8;
    public static final int AllowCopy = 16;
    public static final int AllowModifyAnnotations = 32;
    public static final int AllowFillIn = 256;
    public static final int AllowScreenReaders = 512;
    public static final int AllowAssembly = 1024;
    public static final int AllowDegradedPrinting = 4;
    public static final boolean STRENGTH40BITS = false;
    public static final boolean STRENGTH128BITS = true;
    protected PdfEncryption crypto;
    protected boolean fullCompression = false;
    protected int compressionLevel = -1;
    protected LinkedHashMap<BaseFont, FontDetails> documentFonts = new LinkedHashMap();
    protected int fontNumber = 1;
    protected LinkedHashMap<PdfIndirectReference, Object[]> formXObjects = new LinkedHashMap();
    protected int formXObjectsCounter = 1;
    protected HashMap<PdfReader, PdfReaderInstance> importedPages = new HashMap();
    protected PdfReaderInstance currentPdfReaderInstance;
    protected HashMap<PdfSpotColor, ColorDetails> documentColors = new HashMap();
    protected int colorNumber = 1;
    protected HashMap<PdfPatternPainter, PdfName> documentPatterns = new HashMap();
    protected int patternNumber = 1;
    protected HashMap<PdfShadingPattern, Object> documentShadingPatterns = new HashMap();
    protected HashMap<PdfShading, Object> documentShadings = new HashMap();
    protected HashMap<PdfDictionary, PdfObject[]> documentExtGState = new HashMap();
    protected HashMap<Object, PdfObject[]> documentProperties = new HashMap();
    protected boolean tagged = false;
    protected PdfStructureTreeRoot structureTreeRoot;
    protected Set<PdfOCG> documentOCG = new HashSet<PdfOCG>();
    protected List<PdfOCG> documentOCGorder = new ArrayList<PdfOCG>();
    protected PdfOCProperties OCProperties;
    protected PdfArray OCGRadioGroup = new PdfArray();
    protected PdfArray OCGLocked = new PdfArray();
    public static final PdfName PAGE_OPEN = PdfName.O;
    public static final PdfName PAGE_CLOSE = PdfName.C;
    protected PdfDictionary group;
    public static final float SPACE_CHAR_RATIO_DEFAULT = 2.5f;
    public static final float NO_SPACE_CHAR_RATIO = 1.0E7f;
    private float spaceCharRatio = 2.5f;
    public static final int RUN_DIRECTION_DEFAULT = 0;
    public static final int RUN_DIRECTION_NO_BIDI = 1;
    public static final int RUN_DIRECTION_LTR = 2;
    public static final int RUN_DIRECTION_RTL = 3;
    protected int runDirection = 1;
    protected float userunit = 0.0f;
    protected PdfDictionary defaultColorspace = new PdfDictionary();
    protected HashMap<ColorDetails, ColorDetails> documentSpotPatterns = new HashMap();
    protected ColorDetails patternColorspaceRGB;
    protected ColorDetails patternColorspaceGRAY;
    protected ColorDetails patternColorspaceCMYK;
    protected PdfDictionary imageDictionary = new PdfDictionary();
    private HashMap<Long, PdfName> images = new HashMap();
    protected HashMap<PdfStream, PdfIndirectReference> JBIG2Globals = new HashMap();
    private boolean userProperties;
    private boolean rgbTransparencyBlending;

    protected PdfWriter() {
    }

    protected PdfWriter(PdfDocument document, OutputStream os) {
        super(document, os);
        this.pdf = document;
        this.directContent = new PdfContentByte(this);
        this.directContentUnder = new PdfContentByte(this);
    }

    public static PdfWriter getInstance(Document document, OutputStream os) throws DocumentException {
        PdfDocument pdf = new PdfDocument();
        document.addDocListener(pdf);
        PdfWriter writer = new PdfWriter(pdf, os);
        pdf.addWriter(writer);
        return writer;
    }

    public static PdfWriter getInstance(Document document, OutputStream os, DocListener listener) throws DocumentException {
        PdfDocument pdf = new PdfDocument();
        pdf.addDocListener(listener);
        document.addDocListener(pdf);
        PdfWriter writer = new PdfWriter(pdf, os);
        pdf.addWriter(writer);
        return writer;
    }

    PdfDocument getPdfDocument() {
        return this.pdf;
    }

    public PdfDictionary getInfo() {
        return this.pdf.getInfo();
    }

    public float getVerticalPosition(boolean ensureNewLine) {
        return this.pdf.getVerticalPosition(ensureNewLine);
    }

    public void setInitialLeading(float leading) throws DocumentException {
        if (this.open) {
            throw new DocumentException(MessageLocalization.getComposedMessage("you.can.t.set.the.initial.leading.if.the.document.is.already.open"));
        }
        this.pdf.setLeading(leading);
    }

    public PdfContentByte getDirectContent() {
        if (!this.open) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("the.document.is.not.open"));
        }
        return this.directContent;
    }

    public PdfContentByte getDirectContentUnder() {
        if (!this.open) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("the.document.is.not.open"));
        }
        return this.directContentUnder;
    }

    void resetContent() {
        this.directContent.reset();
        this.directContentUnder.reset();
    }

    void addLocalDestinations(TreeMap dest) throws IOException {
        Iterator iterator = dest.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry o;
            Map.Entry entry = o = iterator.next();
            String name = (String)entry.getKey();
            Object[] obj = (Object[])entry.getValue();
            PdfDestination destination = (PdfDestination)obj[2];
            if (obj[1] == null) {
                obj[1] = this.getPdfIndirectReference();
            }
            if (destination == null) {
                this.addToBody((PdfObject)new PdfString("invalid_" + name), (PdfIndirectReference)obj[1]);
                continue;
            }
            this.addToBody((PdfObject)destination, (PdfIndirectReference)obj[1]);
        }
    }

    public PdfIndirectObject addToBody(PdfObject object) throws IOException {
        PdfIndirectObject iobj = this.body.add(object);
        return iobj;
    }

    public PdfIndirectObject addToBody(PdfObject object, boolean inObjStm) throws IOException {
        PdfIndirectObject iobj = this.body.add(object, inObjStm);
        return iobj;
    }

    public PdfIndirectObject addToBody(PdfObject object, PdfIndirectReference ref) throws IOException {
        PdfIndirectObject iobj = this.body.add(object, ref);
        return iobj;
    }

    public PdfIndirectObject addToBody(PdfObject object, PdfIndirectReference ref, boolean inObjStm) throws IOException {
        PdfIndirectObject iobj = this.body.add(object, ref, inObjStm);
        return iobj;
    }

    public PdfIndirectObject addToBody(PdfObject object, int refNumber) throws IOException {
        PdfIndirectObject iobj = this.body.add(object, refNumber);
        return iobj;
    }

    public PdfIndirectObject addToBody(PdfObject object, int refNumber, boolean inObjStm) throws IOException {
        PdfIndirectObject iobj = this.body.add(object, refNumber, inObjStm);
        return iobj;
    }

    public PdfIndirectReference getPdfIndirectReference() {
        return this.body.getPdfIndirectReference();
    }

    int getIndirectReferenceNumber() {
        return this.body.getIndirectReferenceNumber();
    }

    OutputStreamCounter getOs() {
        return this.os;
    }

    protected PdfDictionary getCatalog(PdfIndirectReference rootObj) {
        PdfDocument.PdfCatalog catalog = this.pdf.getCatalog(rootObj);
        if (this.tagged) {
            try {
                this.getStructureTreeRoot().buildTree();
            }
            catch (Exception e) {
                throw new ExceptionConverter(e);
            }
            catalog.put(PdfName.STRUCTTREEROOT, this.structureTreeRoot.getReference());
            PdfDictionary mi = new PdfDictionary();
            mi.put(PdfName.MARKED, PdfBoolean.PDFTRUE);
            if (this.userProperties) {
                mi.put(PdfName.USERPROPERTIES, PdfBoolean.PDFTRUE);
            }
            catalog.put(PdfName.MARKINFO, mi);
        }
        if (!this.documentOCG.isEmpty()) {
            this.fillOCProperties(false);
            catalog.put(PdfName.OCPROPERTIES, this.OCProperties);
        }
        return catalog;
    }

    public PdfDictionary getExtraCatalog() {
        if (this.extraCatalog == null) {
            this.extraCatalog = new PdfDictionary();
        }
        return this.extraCatalog;
    }

    public void setLinearPageMode() {
        this.root.setLinearMode(null);
    }

    public int reorderPages(int[] order) throws DocumentException {
        return this.root.reorderPages(order);
    }

    public PdfIndirectReference getPageReference(int page) {
        PdfIndirectReference ref;
        if (--page < 0) {
            throw new IndexOutOfBoundsException(MessageLocalization.getComposedMessage("the.page.number.must.be.gt.eq.1"));
        }
        if (page < this.pageReferences.size()) {
            ref = this.pageReferences.get(page);
            if (ref == null) {
                ref = this.body.getPdfIndirectReference();
                this.pageReferences.set(page, ref);
            }
        } else {
            int empty = page - this.pageReferences.size();
            for (int k = 0; k < empty; ++k) {
                this.pageReferences.add(null);
            }
            ref = this.body.getPdfIndirectReference();
            this.pageReferences.add(ref);
        }
        return ref;
    }

    public int getPageNumber() {
        return this.pdf.getPageNumber();
    }

    PdfIndirectReference getCurrentPage() {
        return this.getPageReference(this.currentPageNumber);
    }

    public int getCurrentPageNumber() {
        return this.currentPageNumber;
    }

    public void setTabs(PdfName tabs) {
        this.tabs = tabs;
    }

    public PdfName getTabs() {
        return this.tabs;
    }

    PdfIndirectReference add(PdfPage page, PdfContents contents) throws PdfException {
        PdfIndirectObject object;
        if (!this.open) {
            throw new PdfException(MessageLocalization.getComposedMessage("the.document.is.not.open"));
        }
        try {
            object = this.addToBody(contents);
        }
        catch (IOException ioe) {
            throw new ExceptionConverter(ioe);
        }
        page.add(object.getIndirectReference());
        if (this.group != null) {
            page.put(PdfName.GROUP, this.group);
            this.group = null;
        } else if (this.rgbTransparencyBlending) {
            PdfDictionary pp = new PdfDictionary();
            pp.put(PdfName.TYPE, PdfName.GROUP);
            pp.put(PdfName.S, PdfName.TRANSPARENCY);
            pp.put(PdfName.CS, PdfName.DEVICERGB);
            page.put(PdfName.GROUP, pp);
        }
        this.root.addPage(page);
        ++this.currentPageNumber;
        return null;
    }

    public void setPageEvent(PdfPageEvent event) {
        if (event == null) {
            this.pageEvent = null;
        } else if (this.pageEvent == null) {
            this.pageEvent = event;
        } else if (this.pageEvent instanceof PdfPageEventForwarder) {
            ((PdfPageEventForwarder)this.pageEvent).addPageEvent(event);
        } else {
            PdfPageEventForwarder forward = new PdfPageEventForwarder();
            forward.addPageEvent(this.pageEvent);
            forward.addPageEvent(event);
            this.pageEvent = forward;
        }
    }

    public PdfPageEvent getPageEvent() {
        return this.pageEvent;
    }

    @Override
    public void open() {
        super.open();
        try {
            this.pdf_version.writeHeader(this.os);
            this.body = new PdfBody(this);
            if (this.pdfxConformance.isPdfX32002()) {
                PdfDictionary sec = new PdfDictionary();
                sec.put(PdfName.GAMMA, new PdfArray(new float[]{2.2f, 2.2f, 2.2f}));
                sec.put(PdfName.MATRIX, new PdfArray(new float[]{0.4124f, 0.2126f, 0.0193f, 0.3576f, 0.7152f, 0.1192f, 0.1805f, 0.0722f, 0.9505f}));
                sec.put(PdfName.WHITEPOINT, new PdfArray(new float[]{0.9505f, 1.0f, 1.089f}));
                PdfArray arr = new PdfArray(PdfName.CALRGB);
                arr.add(sec);
                this.setDefaultColorspace(PdfName.DEFAULTRGB, this.addToBody(arr).getIndirectReference());
            }
        }
        catch (IOException ioe) {
            throw new ExceptionConverter(ioe);
        }
    }

    @Override
    public void close() {
        if (this.open) {
            if (this.currentPageNumber - 1 != this.pageReferences.size()) {
                throw new RuntimeException("The page " + this.pageReferences.size() + " was requested but the document has only " + (this.currentPageNumber - 1) + " pages.");
            }
            try {
                this.addSharedObjectsToBody();
                PdfIndirectReference rootRef = this.root.writePageTree();
                PdfDictionary catalog = this.getCatalog(rootRef);
                if (this.xmpMetadata != null) {
                    PdfStream xmp = new PdfStream(this.xmpMetadata);
                    xmp.put(PdfName.TYPE, PdfName.METADATA);
                    xmp.put(PdfName.SUBTYPE, PdfName.XML);
                    if (this.crypto != null && !this.crypto.isMetadataEncrypted()) {
                        PdfArray ar = new PdfArray();
                        ar.add(PdfName.CRYPT);
                        xmp.put(PdfName.FILTER, ar);
                    }
                    catalog.put(PdfName.METADATA, this.body.add(xmp).getIndirectReference());
                }
                if (this.isPdfX()) {
                    this.pdfxConformance.completeInfoDictionary(this.getInfo());
                    this.pdfxConformance.completeExtraCatalog(this.getExtraCatalog());
                }
                if (this.extraCatalog != null) {
                    catalog.mergeDifferent(this.extraCatalog);
                }
                this.writeOutlines(catalog, false);
                PdfIndirectObject indirectCatalog = this.addToBody((PdfObject)catalog, false);
                PdfIndirectObject infoObj = this.addToBody((PdfObject)this.getInfo(), false);
                PdfIndirectReference encryption = null;
                PdfObject fileID = null;
                this.body.flushObjStm();
                if (this.crypto != null) {
                    PdfIndirectObject encryptionObject = this.addToBody((PdfObject)this.crypto.getEncryptionDictionary(), false);
                    encryption = encryptionObject.getIndirectReference();
                    fileID = this.crypto.getFileID();
                } else {
                    fileID = this.getInfo().contains(PdfName.FILEID) ? this.getInfo().get(PdfName.FILEID) : PdfEncryption.createInfoId(PdfEncryption.createDocumentId());
                }
                this.body.writeCrossReferenceTable(this.os, indirectCatalog.getIndirectReference(), infoObj.getIndirectReference(), encryption, fileID, this.prevxref);
                this.os.write(PdfWriter.getISOBytes("startxref\n"));
                this.os.write(PdfWriter.getISOBytes(String.valueOf(this.body.offset())));
                this.os.write(PdfWriter.getISOBytes("\n%%EOF\n"));
                super.close();
            }
            catch (IOException ioe) {
                throw new ExceptionConverter(ioe);
            }
        }
    }

    protected void addSharedObjectsToBody() throws IOException {
        PdfObject[] obj;
        for (FontDetails fontDetails : this.documentFonts.values()) {
            fontDetails.writeFont(this);
        }
        for (Object[] objectArray : this.formXObjects.values()) {
            PdfTemplate template = (PdfTemplate)objectArray[1];
            if (template != null && template.getIndirectReference() instanceof PRIndirectReference || template == null || template.getType() != 1) continue;
            this.addToBody((PdfObject)template.getFormXObject(this.compressionLevel), template.getIndirectReference());
        }
        Iterator<Object> iterator = this.importedPages.values().iterator();
        while (iterator.hasNext()) {
            PdfReaderInstance pdfReaderInstance;
            this.currentPdfReaderInstance = pdfReaderInstance = (PdfReaderInstance)iterator.next();
            this.currentPdfReaderInstance.writeAllPages();
        }
        this.currentPdfReaderInstance = null;
        for (ColorDetails colorDetails : this.documentColors.values()) {
            this.addToBody(colorDetails.getSpotColor(this), colorDetails.getIndirectReference());
        }
        for (PdfPatternPainter pdfPatternPainter : this.documentPatterns.keySet()) {
            this.addToBody((PdfObject)pdfPatternPainter.getPattern(this.compressionLevel), pdfPatternPainter.getIndirectReference());
        }
        for (PdfShadingPattern pdfShadingPattern : this.documentShadingPatterns.keySet()) {
            pdfShadingPattern.addToBody();
        }
        for (PdfShading pdfShading : this.documentShadings.keySet()) {
            pdfShading.addToBody();
        }
        for (Map.Entry entry : this.documentExtGState.entrySet()) {
            PdfDictionary gstate = (PdfDictionary)entry.getKey();
            obj = (PdfObject[])entry.getValue();
            this.addToBody((PdfObject)gstate, (PdfIndirectReference)obj[1]);
        }
        for (Map.Entry entry : this.documentProperties.entrySet()) {
            Object prop = entry.getKey();
            obj = (PdfObject[])entry.getValue();
            if (prop instanceof PdfLayerMembership) {
                PdfLayerMembership layer = (PdfLayerMembership)prop;
                this.addToBody(layer.getPdfObject(), layer.getRef());
                continue;
            }
            if (!(prop instanceof PdfDictionary) || prop instanceof PdfLayer) continue;
            this.addToBody((PdfObject)((PdfDictionary)prop), (PdfIndirectReference)obj[1]);
        }
        for (PdfOCG pdfOCG : this.documentOCG) {
            this.addToBody(pdfOCG.getPdfObject(), pdfOCG.getRef());
        }
    }

    public PdfOutline getRootOutline() {
        return this.directContent.getRootOutline();
    }

    public void setOutlines(List outlines) {
        this.newBookmarks = outlines;
    }

    protected void writeOutlines(PdfDictionary catalog, boolean namedAsNames) throws IOException {
        if (this.newBookmarks == null || this.newBookmarks.isEmpty()) {
            return;
        }
        PdfDictionary top = new PdfDictionary();
        PdfIndirectReference topRef = this.getPdfIndirectReference();
        Object[] kids = SimpleBookmark.iterateOutlines(this, topRef, this.newBookmarks, namedAsNames);
        top.put(PdfName.FIRST, (PdfIndirectReference)kids[0]);
        top.put(PdfName.LAST, (PdfIndirectReference)kids[1]);
        top.put(PdfName.COUNT, new PdfNumber((Integer)kids[2]));
        this.addToBody((PdfObject)top, topRef);
        catalog.put(PdfName.OUTLINES, topRef);
    }

    @Override
    public void setPdfVersion(char version) {
        this.pdf_version.setPdfVersion(version);
    }

    @Override
    public void setAtLeastPdfVersion(char version) {
        this.pdf_version.setAtLeastPdfVersion(version);
    }

    @Override
    public void setPdfVersion(PdfName version) {
        this.pdf_version.setPdfVersion(version);
    }

    @Override
    public void addDeveloperExtension(PdfDeveloperExtension de) {
        this.pdf_version.addDeveloperExtension(de);
    }

    PdfVersionImp getPdfVersion() {
        return this.pdf_version;
    }

    @Override
    public void setViewerPreferences(int preferences) {
        this.pdf.setViewerPreferences(preferences);
    }

    @Override
    public void addViewerPreference(PdfName key, PdfObject value) {
        this.pdf.addViewerPreference(key, value);
    }

    public void setPageLabels(PdfPageLabels pageLabels) {
        this.pdf.setPageLabels(pageLabels);
    }

    public void addNamedDestinations(Map<String, String> map, int page_offset) {
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> stringStringEntry;
            Map.Entry<String, String> entry = stringStringEntry = iterator.next();
            String dest = entry.getValue();
            int page = Integer.parseInt(dest.substring(0, dest.indexOf(" ")));
            PdfDestination destination = new PdfDestination(dest.substring(dest.indexOf(" ") + 1));
            this.addNamedDestination(entry.getKey(), page + page_offset, destination);
        }
    }

    public void addNamedDestination(String name, int page, PdfDestination dest) {
        dest.addPage(this.getPageReference(page));
        this.pdf.localDestination(name, dest);
    }

    public void addJavaScript(PdfAction js) {
        this.pdf.addJavaScript(js);
    }

    public void addJavaScript(String code, boolean unicode) {
        this.addJavaScript(PdfAction.javaScript(code, this, unicode));
    }

    public void addJavaScript(String code) {
        this.addJavaScript(code, false);
    }

    public void addJavaScript(String name, PdfAction js) {
        this.pdf.addJavaScript(name, js);
    }

    public void addJavaScript(String name, String code, boolean unicode) {
        this.addJavaScript(name, PdfAction.javaScript(code, this, unicode));
    }

    public void addJavaScript(String name, String code) {
        this.addJavaScript(name, code, false);
    }

    public void addFileAttachment(String description, byte[] fileStore, String file, String fileDisplay) throws IOException {
        this.addFileAttachment(description, PdfFileSpecification.fileEmbedded(this, file, fileDisplay, fileStore));
    }

    public void addFileAttachment(String description, PdfFileSpecification fs) throws IOException {
        this.pdf.addFileAttachment(description, fs);
    }

    public void addFileAttachment(PdfFileSpecification fs) throws IOException {
        this.addFileAttachment(null, fs);
    }

    @Override
    public void setOpenAction(String name) {
        this.pdf.setOpenAction(name);
    }

    @Override
    public void setOpenAction(PdfAction action) {
        this.pdf.setOpenAction(action);
    }

    @Override
    public void setAdditionalAction(PdfName actionType, PdfAction action) throws DocumentException {
        if (!(actionType.equals(DOCUMENT_CLOSE) || actionType.equals(WILL_SAVE) || actionType.equals(DID_SAVE) || actionType.equals(WILL_PRINT) || actionType.equals(DID_PRINT))) {
            throw new DocumentException(MessageLocalization.getComposedMessage("invalid.additional.action.type.1", actionType.toString()));
        }
        this.pdf.addAdditionalAction(actionType, action);
    }

    public void setCollection(PdfCollection collection) {
        this.setAtLeastPdfVersion('7');
        this.pdf.setCollection(collection);
    }

    @Override
    public PdfAcroForm getAcroForm() {
        return this.pdf.getAcroForm();
    }

    @Override
    public void addAnnotation(PdfAnnotation annot) {
        this.pdf.addAnnotation(annot);
    }

    void addAnnotation(PdfAnnotation annot, int page) {
        this.addAnnotation(annot);
    }

    @Override
    public void addCalculationOrder(PdfFormField annot) {
        this.pdf.addCalculationOrder(annot);
    }

    @Override
    public void setSigFlags(int f) {
        this.pdf.setSigFlags(f);
    }

    public void setXmpMetadata(byte[] xmpMetadata) {
        this.xmpMetadata = xmpMetadata;
    }

    public void setPageXmpMetadata(byte[] xmpMetadata) {
        this.pdf.setXmpMetadata(xmpMetadata);
    }

    public void createXmpMetadata() {
        this.setXmpMetadata(this.createXmpMetadataBytes());
    }

    private byte[] createXmpMetadataBytes() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            XmpWriter xmp = new XmpWriter((OutputStream)baos, this.pdf.getInfo(), this.pdfxConformance.getPDFXConformance());
            xmp.close();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return baos.toByteArray();
    }

    @Override
    public void setPDFXConformance(int pdfx) {
        if (this.pdfxConformance.getPDFXConformance() == pdfx) {
            return;
        }
        if (this.pdf.isOpen()) {
            throw new PdfXConformanceException(MessageLocalization.getComposedMessage("pdfx.conformance.can.only.be.set.before.opening.the.document"));
        }
        if (this.crypto != null) {
            throw new PdfXConformanceException(MessageLocalization.getComposedMessage("a.pdfx.conforming.document.cannot.be.encrypted"));
        }
        if (pdfx == 3 || pdfx == 4) {
            this.setPdfVersion('4');
        } else if (pdfx != 0) {
            this.setPdfVersion('3');
        }
        this.pdfxConformance.setPDFXConformance(pdfx);
    }

    @Override
    public int getPDFXConformance() {
        return this.pdfxConformance.getPDFXConformance();
    }

    @Override
    public boolean isPdfX() {
        return this.pdfxConformance.isPdfX();
    }

    public void setOutputIntents(String outputConditionIdentifier, String outputCondition, String registryName, String info, ICC_Profile colorProfile) throws IOException {
        this.getExtraCatalog();
        PdfDictionary out = new PdfDictionary(PdfName.OUTPUTINTENT);
        if (outputCondition != null) {
            out.put(PdfName.OUTPUTCONDITION, new PdfString(outputCondition, "UnicodeBig"));
        }
        if (outputConditionIdentifier != null) {
            out.put(PdfName.OUTPUTCONDITIONIDENTIFIER, new PdfString(outputConditionIdentifier, "UnicodeBig"));
        }
        if (registryName != null) {
            out.put(PdfName.REGISTRYNAME, new PdfString(registryName, "UnicodeBig"));
        }
        if (info != null) {
            out.put(PdfName.INFO, new PdfString(info, "UnicodeBig"));
        }
        if (colorProfile != null) {
            PdfICCBased stream = new PdfICCBased(colorProfile, this.compressionLevel);
            out.put(PdfName.DESTOUTPUTPROFILE, this.addToBody(stream).getIndirectReference());
        }
        PdfName intentSubtype = this.pdfxConformance.isPdfA1() || "PDFA/1".equals(outputCondition) ? PdfName.GTS_PDFA1 : PdfName.GTS_PDFX;
        out.put(PdfName.S, intentSubtype);
        this.extraCatalog.put(PdfName.OUTPUTINTENTS, new PdfArray(out));
    }

    public void setOutputIntents(String outputConditionIdentifier, String outputCondition, String registryName, String info, byte[] destOutputProfile) throws IOException {
        ICC_Profile colorProfile = destOutputProfile == null ? null : ICC_Profile.getInstance(destOutputProfile);
        this.setOutputIntents(outputConditionIdentifier, outputCondition, registryName, info, colorProfile);
    }

    public boolean setOutputIntents(PdfReader reader, boolean checkExistence) throws IOException {
        PdfDictionary catalog = reader.getCatalog();
        PdfArray outs = catalog.getAsArray(PdfName.OUTPUTINTENTS);
        if (outs == null) {
            return false;
        }
        if (outs.isEmpty()) {
            return false;
        }
        PdfDictionary out = outs.getAsDict(0);
        PdfObject obj = PdfReader.getPdfObject(out.get(PdfName.S));
        if (!PdfName.GTS_PDFX.equals(obj)) {
            return false;
        }
        if (checkExistence) {
            return true;
        }
        PRStream stream = (PRStream)PdfReader.getPdfObject(out.get(PdfName.DESTOUTPUTPROFILE));
        byte[] destProfile = null;
        if (stream != null) {
            destProfile = PdfReader.getStreamBytes(stream);
        }
        this.setOutputIntents(PdfWriter.getNameString(out, PdfName.OUTPUTCONDITIONIDENTIFIER), PdfWriter.getNameString(out, PdfName.OUTPUTCONDITION), PdfWriter.getNameString(out, PdfName.REGISTRYNAME), PdfWriter.getNameString(out, PdfName.INFO), destProfile);
        return true;
    }

    private static String getNameString(PdfDictionary dic, PdfName key) {
        PdfObject obj = PdfReader.getPdfObject(dic.get(key));
        if (obj == null || !obj.isString()) {
            return null;
        }
        return ((PdfString)obj).toUnicodeString();
    }

    PdfEncryption getEncryption() {
        return this.crypto;
    }

    @Override
    public void setEncryption(byte[] userPassword, byte[] ownerPassword, int permissions, int encryptionType) throws DocumentException {
        if (this.pdf.isOpen()) {
            throw new DocumentException(MessageLocalization.getComposedMessage("encryption.can.only.be.added.before.opening.the.document"));
        }
        this.crypto = new PdfEncryption();
        this.crypto.setCryptoMode(encryptionType, 0);
        this.crypto.setupAllKeys(userPassword, ownerPassword, permissions);
    }

    @Override
    public void setEncryption(Certificate[] certs, int[] permissions, int encryptionType) throws DocumentException {
        if (this.pdf.isOpen()) {
            throw new DocumentException(MessageLocalization.getComposedMessage("encryption.can.only.be.added.before.opening.the.document"));
        }
        this.crypto = new PdfEncryption();
        if (certs != null) {
            for (int i = 0; i < certs.length; ++i) {
                this.crypto.addRecipient(certs[i], permissions[i]);
            }
        }
        this.crypto.setCryptoMode(encryptionType, 0);
        this.crypto.getEncryptionDictionary();
    }

    public void setEncryption(byte[] userPassword, byte[] ownerPassword, int permissions, boolean strength128Bits) throws DocumentException {
        this.setEncryption(userPassword, ownerPassword, permissions, strength128Bits ? 1 : 0);
    }

    public void setEncryption(boolean strength, String userPassword, String ownerPassword, int permissions) throws DocumentException {
        this.setEncryption(PdfWriter.getISOBytes(userPassword), PdfWriter.getISOBytes(ownerPassword), permissions, strength ? 1 : 0);
    }

    public void setEncryption(int encryptionType, String userPassword, String ownerPassword, int permissions) throws DocumentException {
        this.setEncryption(PdfWriter.getISOBytes(userPassword), PdfWriter.getISOBytes(ownerPassword), permissions, encryptionType);
    }

    public boolean isFullCompression() {
        return this.fullCompression;
    }

    public void setFullCompression() {
        this.fullCompression = true;
        this.setAtLeastPdfVersion('5');
    }

    public int getCompressionLevel() {
        return this.compressionLevel;
    }

    public void setCompressionLevel(int compressionLevel) {
        this.compressionLevel = compressionLevel < 0 || compressionLevel > 9 ? -1 : compressionLevel;
    }

    FontDetails addSimple(BaseFont bf) {
        if (bf.getFontType() == 4) {
            return new FontDetails(new PdfName("F" + this.fontNumber++), ((DocumentFont)bf).getIndirectReference(), bf);
        }
        FontDetails ret = this.documentFonts.get(bf);
        if (ret == null) {
            PdfXConformanceImp.checkPDFXConformance(this, 4, bf);
            ret = new FontDetails(new PdfName("F" + this.fontNumber++), this.body.getPdfIndirectReference(), bf);
            this.documentFonts.put(bf, ret);
        }
        return ret;
    }

    void eliminateFontSubset(PdfDictionary fonts) {
        for (FontDetails ft : this.documentFonts.values()) {
            if (fonts.get(ft.getFontName()) == null) continue;
            ft.setSubset(false);
        }
    }

    PdfName addDirectTemplateSimple(PdfTemplate template, PdfName forcedName) {
        PdfIndirectReference ref = template.getIndirectReference();
        Object[] obj = this.formXObjects.get(ref);
        PdfName name = null;
        try {
            if (obj == null) {
                if (forcedName == null) {
                    name = new PdfName("Xf" + this.formXObjectsCounter);
                    ++this.formXObjectsCounter;
                } else {
                    name = forcedName;
                }
                if (template.getType() == 2) {
                    PdfImportedPage ip = (PdfImportedPage)template;
                    PdfReader r = ip.getPdfReaderInstance().getReader();
                    if (!this.importedPages.containsKey(r)) {
                        this.importedPages.put(r, ip.getPdfReaderInstance());
                    }
                    template = null;
                }
                this.formXObjects.put(ref, new Object[]{name, template});
            } else {
                name = (PdfName)obj[0];
            }
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
        return name;
    }

    public void releaseTemplate(PdfTemplate tp) throws IOException {
        PdfIndirectReference ref = tp.getIndirectReference();
        Object[] objs = this.formXObjects.get(ref);
        if (objs == null || objs[1] == null) {
            return;
        }
        PdfTemplate template = (PdfTemplate)objs[1];
        if (template.getIndirectReference() instanceof PRIndirectReference) {
            return;
        }
        if (template.getType() == 1) {
            this.addToBody((PdfObject)template.getFormXObject(this.compressionLevel), template.getIndirectReference());
            objs[1] = null;
        }
    }

    public PdfImportedPage getImportedPage(PdfReader reader, int pageNumber) {
        PdfReaderInstance inst = this.importedPages.get(reader);
        if (inst == null) {
            inst = reader.getPdfReaderInstance(this);
            this.importedPages.put(reader, inst);
        }
        return inst.getImportedPage(pageNumber);
    }

    public void freeReader(PdfReader reader) throws IOException {
        this.currentPdfReaderInstance = this.importedPages.get(reader);
        if (this.currentPdfReaderInstance == null) {
            return;
        }
        this.currentPdfReaderInstance.writeAllPages();
        this.currentPdfReaderInstance = null;
        this.importedPages.remove(reader);
    }

    public long getCurrentDocumentSize() {
        return this.body.offset() + (long)this.body.size() * 20L + 72L;
    }

    protected int getNewObjectNumber(PdfReader reader, int number, int generation) {
        if (this.currentPdfReaderInstance == null && this.importedPages.get(reader) == null) {
            this.importedPages.put(reader, reader.getPdfReaderInstance(this));
        }
        this.currentPdfReaderInstance = this.importedPages.get(reader);
        int n = this.currentPdfReaderInstance.getNewObjectNumber(number, generation);
        this.currentPdfReaderInstance = null;
        return n;
    }

    RandomAccessFileOrArray getReaderFile(PdfReader reader) {
        return this.currentPdfReaderInstance.getReaderFile();
    }

    PdfName getColorspaceName() {
        return new PdfName("CS" + this.colorNumber++);
    }

    ColorDetails addSimple(PdfSpotColor spc) {
        ColorDetails ret = this.documentColors.get(spc);
        if (ret == null) {
            ret = new ColorDetails(this.getColorspaceName(), this.body.getPdfIndirectReference(), spc);
            this.documentColors.put(spc, ret);
        }
        return ret;
    }

    PdfName addSimplePattern(PdfPatternPainter painter) {
        PdfName name = this.documentPatterns.get(painter);
        try {
            if (name == null) {
                name = new PdfName("P" + this.patternNumber);
                ++this.patternNumber;
                this.documentPatterns.put(painter, name);
            }
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
        return name;
    }

    void addSimpleShadingPattern(PdfShadingPattern shading) {
        if (!this.documentShadingPatterns.containsKey(shading)) {
            shading.setName(this.patternNumber);
            ++this.patternNumber;
            this.documentShadingPatterns.put(shading, null);
            this.addSimpleShading(shading.getShading());
        }
    }

    void addSimpleShading(PdfShading shading) {
        if (!this.documentShadings.containsKey(shading)) {
            this.documentShadings.put(shading, null);
            shading.setName(this.documentShadings.size());
        }
    }

    PdfObject[] addSimpleExtGState(PdfDictionary gstate) {
        if (!this.documentExtGState.containsKey(gstate)) {
            PdfXConformanceImp.checkPDFXConformance(this, 6, gstate);
            this.documentExtGState.put(gstate, new PdfObject[]{new PdfName("GS" + (this.documentExtGState.size() + 1)), this.getPdfIndirectReference()});
        }
        return this.documentExtGState.get(gstate);
    }

    PdfObject[] addSimpleProperty(Object prop, PdfIndirectReference refi) {
        if (!this.documentProperties.containsKey(prop)) {
            if (prop instanceof PdfOCG) {
                PdfXConformanceImp.checkPDFXConformance(this, 7, null);
            }
            this.documentProperties.put(prop, new PdfObject[]{new PdfName("Pr" + (this.documentProperties.size() + 1)), refi});
        }
        return this.documentProperties.get(prop);
    }

    boolean propertyExists(Object prop) {
        return this.documentProperties.containsKey(prop);
    }

    public void setTagged() {
        if (this.open) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("tagging.must.be.set.before.opening.the.document"));
        }
        this.tagged = true;
    }

    public boolean isTagged() {
        return this.tagged;
    }

    public PdfStructureTreeRoot getStructureTreeRoot() {
        if (this.tagged && this.structureTreeRoot == null) {
            this.structureTreeRoot = new PdfStructureTreeRoot(this);
        }
        return this.structureTreeRoot;
    }

    public PdfOCProperties getOCProperties() {
        this.fillOCProperties(true);
        return this.OCProperties;
    }

    public void addOCGRadioGroup(List<PdfLayer> group) {
        PdfArray ar = new PdfArray();
        for (PdfLayer layer : group) {
            if (layer.getTitle() != null) continue;
            ar.add(layer.getRef());
        }
        if (ar.size() == 0) {
            return;
        }
        this.OCGRadioGroup.add(ar);
    }

    public void lockLayer(PdfLayer layer) {
        this.OCGLocked.add(layer.getRef());
    }

    private static void getOCGOrder(PdfArray order, PdfLayer layer) {
        ArrayList<PdfLayer> children;
        if (!layer.isOnPanel()) {
            return;
        }
        if (layer.getTitle() == null) {
            order.add(layer.getRef());
        }
        if ((children = layer.getChildren()) == null) {
            return;
        }
        PdfArray kids = new PdfArray();
        if (layer.getTitle() != null) {
            kids.add(new PdfString(layer.getTitle(), "UnicodeBig"));
        }
        for (PdfLayer child : children) {
            PdfWriter.getOCGOrder(kids, child);
        }
        if (kids.size() > 0) {
            order.add(kids);
        }
    }

    private void addASEvent(PdfName event, PdfName category) {
        PdfArray arr = new PdfArray();
        for (PdfOCG o : this.documentOCG) {
            PdfLayer layer = (PdfLayer)o;
            PdfDictionary usage = (PdfDictionary)layer.get(PdfName.USAGE);
            if (usage == null || usage.get(category) == null) continue;
            arr.add(layer.getRef());
        }
        if (arr.size() == 0) {
            return;
        }
        PdfDictionary d = (PdfDictionary)this.OCProperties.get(PdfName.D);
        PdfArray arras = (PdfArray)d.get(PdfName.AS);
        if (arras == null) {
            arras = new PdfArray();
            d.put(PdfName.AS, arras);
        }
        PdfDictionary as = new PdfDictionary();
        as.put(PdfName.EVENT, event);
        as.put(PdfName.CATEGORY, new PdfArray(category));
        as.put(PdfName.OCGS, arr);
        arras.add(as);
    }

    protected void fillOCProperties(boolean erase) {
        if (this.OCProperties == null) {
            this.OCProperties = new PdfOCProperties();
        }
        if (erase) {
            this.OCProperties.remove(PdfName.OCGS);
            this.OCProperties.remove(PdfName.D);
        }
        if (this.OCProperties.get(PdfName.OCGS) == null) {
            PdfArray gr = new PdfArray();
            for (PdfOCG pdfOCG2 : this.documentOCG) {
                PdfLayer layer = (PdfLayer)pdfOCG2;
                gr.add(layer.getRef());
            }
            this.OCProperties.put(PdfName.OCGS, gr);
        }
        if (this.OCProperties.get(PdfName.D) != null) {
            return;
        }
        List docOrder = this.documentOCGorder.stream().filter(pdfOCG -> ((PdfLayer)pdfOCG).getParent() == null).collect(Collectors.toList());
        PdfArray order = new PdfArray();
        for (PdfOCG o1 : docOrder) {
            PdfLayer layer = (PdfLayer)o1;
            PdfWriter.getOCGOrder(order, layer);
        }
        PdfDictionary pdfDictionary = new PdfDictionary();
        this.OCProperties.put(PdfName.D, pdfDictionary);
        pdfDictionary.put(PdfName.ORDER, order);
        PdfArray gr = new PdfArray();
        for (PdfOCG o : this.documentOCG) {
            PdfLayer layer = (PdfLayer)o;
            if (layer.isOn()) continue;
            gr.add(layer.getRef());
        }
        if (gr.size() > 0) {
            pdfDictionary.put(PdfName.OFF, gr);
        }
        if (this.OCGRadioGroup.size() > 0) {
            pdfDictionary.put(PdfName.RBGROUPS, this.OCGRadioGroup);
        }
        if (this.OCGLocked.size() > 0) {
            pdfDictionary.put(PdfName.LOCKED, this.OCGLocked);
        }
        this.addASEvent(PdfName.VIEW, PdfName.ZOOM);
        this.addASEvent(PdfName.VIEW, PdfName.VIEW);
        this.addASEvent(PdfName.PRINT, PdfName.PRINT);
        this.addASEvent(PdfName.EXPORT, PdfName.EXPORT);
        pdfDictionary.put(PdfName.LISTMODE, PdfName.VISIBLEPAGES);
    }

    void registerLayer(PdfOCG layer) {
        PdfXConformanceImp.checkPDFXConformance(this, 7, null);
        if (layer instanceof PdfLayer) {
            PdfLayer la = (PdfLayer)layer;
            if (la.getTitle() == null) {
                if (!this.documentOCG.contains(layer)) {
                    this.documentOCG.add(layer);
                    this.documentOCGorder.add(layer);
                }
            } else {
                this.documentOCGorder.add(layer);
            }
        } else {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("only.pdflayer.is.accepted"));
        }
    }

    public Rectangle getPageSize() {
        return this.pdf.getPageSize();
    }

    public void setCropBoxSize(Rectangle crop) {
        this.pdf.setCropBoxSize(crop);
    }

    public void setBoxSize(String boxName, Rectangle size) {
        this.pdf.setBoxSize(boxName, size);
    }

    public Rectangle getBoxSize(String boxName) {
        return this.pdf.getBoxSize(boxName);
    }

    public void setPageEmpty(boolean pageEmpty) {
        if (pageEmpty) {
            return;
        }
        this.pdf.setPageEmpty(pageEmpty);
    }

    public boolean isPageEmpty() {
        return this.pdf.isPageEmpty();
    }

    @Override
    public void setPageAction(PdfName actionType, PdfAction action) throws DocumentException {
        if (!actionType.equals(PAGE_OPEN) && !actionType.equals(PAGE_CLOSE)) {
            throw new DocumentException(MessageLocalization.getComposedMessage("invalid.page.additional.action.type.1", actionType.toString()));
        }
        this.pdf.setPageAction(actionType, action);
    }

    @Override
    public void setDuration(int seconds) {
        this.pdf.setDuration(seconds);
    }

    @Override
    public void setTransition(PdfTransition transition) {
        this.pdf.setTransition(transition);
    }

    public void setThumbnail(Image image) throws DocumentException {
        this.pdf.setThumbnail(image);
    }

    public PdfDictionary getGroup() {
        return this.group;
    }

    public void setGroup(PdfDictionary group) {
        this.group = group;
    }

    public float getSpaceCharRatio() {
        return this.spaceCharRatio;
    }

    public void setSpaceCharRatio(float spaceCharRatio) {
        this.spaceCharRatio = spaceCharRatio < 0.001f ? 0.001f : spaceCharRatio;
    }

    @Override
    public void setRunDirection(int runDirection) {
        if (runDirection < 1 || runDirection > 3) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("invalid.run.direction.1", runDirection));
        }
        this.runDirection = runDirection;
    }

    @Override
    public int getRunDirection() {
        return this.runDirection;
    }

    public float getUserunit() {
        return this.userunit;
    }

    public void setUserunit(float userunit) throws DocumentException {
        if (userunit < 1.0f || userunit > 75000.0f) {
            throw new DocumentException(MessageLocalization.getComposedMessage("userunit.should.be.a.value.between.1.and.75000"));
        }
        this.userunit = userunit;
        this.setAtLeastPdfVersion('6');
    }

    public PdfDictionary getDefaultColorspace() {
        return this.defaultColorspace;
    }

    public void setDefaultColorspace(PdfName key, PdfObject cs) {
        if (cs == null || cs.isNull()) {
            this.defaultColorspace.remove(key);
        }
        this.defaultColorspace.put(key, cs);
    }

    ColorDetails addSimplePatternColorspace(Color color) {
        int type = ExtendedColor.getType(color);
        if (type == 4 || type == 5) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("an.uncolored.tile.pattern.can.not.have.another.pattern.or.shading.as.color"));
        }
        try {
            switch (type) {
                case 0: {
                    if (this.patternColorspaceRGB == null) {
                        this.patternColorspaceRGB = new ColorDetails(this.getColorspaceName(), this.body.getPdfIndirectReference(), null);
                        PdfArray array = new PdfArray(PdfName.PATTERN);
                        array.add(PdfName.DEVICERGB);
                        this.addToBody((PdfObject)array, this.patternColorspaceRGB.getIndirectReference());
                    }
                    return this.patternColorspaceRGB;
                }
                case 2: {
                    if (this.patternColorspaceCMYK == null) {
                        this.patternColorspaceCMYK = new ColorDetails(this.getColorspaceName(), this.body.getPdfIndirectReference(), null);
                        PdfArray array = new PdfArray(PdfName.PATTERN);
                        array.add(PdfName.DEVICECMYK);
                        this.addToBody((PdfObject)array, this.patternColorspaceCMYK.getIndirectReference());
                    }
                    return this.patternColorspaceCMYK;
                }
                case 1: {
                    if (this.patternColorspaceGRAY == null) {
                        this.patternColorspaceGRAY = new ColorDetails(this.getColorspaceName(), this.body.getPdfIndirectReference(), null);
                        PdfArray array = new PdfArray(PdfName.PATTERN);
                        array.add(PdfName.DEVICEGRAY);
                        this.addToBody((PdfObject)array, this.patternColorspaceGRAY.getIndirectReference());
                    }
                    return this.patternColorspaceGRAY;
                }
                case 3: {
                    ColorDetails details = this.addSimple(((SpotColor)color).getPdfSpotColor());
                    ColorDetails patternDetails = this.documentSpotPatterns.get(details);
                    if (patternDetails == null) {
                        patternDetails = new ColorDetails(this.getColorspaceName(), this.body.getPdfIndirectReference(), null);
                        PdfArray array = new PdfArray(PdfName.PATTERN);
                        array.add(details.getIndirectReference());
                        this.addToBody((PdfObject)array, patternDetails.getIndirectReference());
                        this.documentSpotPatterns.put(details, patternDetails);
                    }
                    return patternDetails;
                }
            }
            throw new RuntimeException(MessageLocalization.getComposedMessage("invalid.color.type"));
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean isStrictImageSequence() {
        return this.pdf.isStrictImageSequence();
    }

    public void setStrictImageSequence(boolean strictImageSequence) {
        this.pdf.setStrictImageSequence(strictImageSequence);
    }

    public void clearTextWrap() throws DocumentException {
        this.pdf.clearTextWrap();
    }

    public PdfName addDirectImageSimple(Image image) throws DocumentException {
        return this.addDirectImageSimple(image, null);
    }

    public PdfName addDirectImageSimple(Image image, PdfIndirectReference fixedRef) throws DocumentException {
        PdfName name;
        if (this.images.containsKey(image.getMySerialId())) {
            name = this.images.get(image.getMySerialId());
        } else {
            if (image.isImgTemplate()) {
                name = new PdfName("img" + this.images.size());
                if (image instanceof ImgWMF) {
                    try {
                        ImgWMF wmf = (ImgWMF)image;
                        wmf.readWMF(PdfTemplate.createTemplate(this, 0.0f, 0.0f));
                    }
                    catch (Exception e) {
                        throw new DocumentException(e);
                    }
                }
            } else {
                byte[] globals;
                PdfIndirectReference dref = image.getDirectReference();
                if (dref != null) {
                    PdfName rname = new PdfName("img" + this.images.size());
                    this.images.put(image.getMySerialId(), rname);
                    this.imageDictionary.put(rname, dref);
                    return rname;
                }
                Image maskImage = image.getImageMask();
                PdfIndirectReference maskRef = null;
                if (maskImage != null) {
                    PdfName mname = this.images.get(maskImage.getMySerialId());
                    maskRef = this.getImageReference(mname);
                }
                PdfImage i = new PdfImage(image, "img" + this.images.size(), maskRef);
                if (image instanceof ImgJBIG2 && (globals = ((ImgJBIG2)image).getGlobalBytes()) != null) {
                    PdfDictionary decodeparms = new PdfDictionary();
                    decodeparms.put(PdfName.JBIG2GLOBALS, this.getReferenceJBIG2Globals(globals));
                    i.put(PdfName.DECODEPARMS, decodeparms);
                }
                if (image.hasICCProfile()) {
                    PdfICCBased icc = new PdfICCBased(image.getICCProfile(), image.getCompressionLevel());
                    PdfIndirectReference iccRef = this.add(icc);
                    PdfArray iccArray = new PdfArray();
                    iccArray.add(PdfName.ICCBASED);
                    iccArray.add(iccRef);
                    PdfArray colorspace = i.getAsArray(PdfName.COLORSPACE);
                    if (colorspace != null) {
                        if (colorspace.size() > 1 && PdfName.INDEXED.equals(colorspace.getPdfObject(0))) {
                            colorspace.set(1, iccArray);
                        } else {
                            i.put(PdfName.COLORSPACE, iccArray);
                        }
                    } else {
                        i.put(PdfName.COLORSPACE, iccArray);
                    }
                }
                this.add(i, fixedRef);
                name = i.name();
            }
            this.images.put(image.getMySerialId(), name);
        }
        return name;
    }

    PdfIndirectReference add(PdfImage pdfImage, PdfIndirectReference fixedRef) throws PdfException {
        if (!this.imageDictionary.contains(pdfImage.name())) {
            PdfXConformanceImp.checkPDFXConformance(this, 5, pdfImage);
            if (fixedRef instanceof PRIndirectReference) {
                PRIndirectReference r2 = (PRIndirectReference)fixedRef;
                fixedRef = new PdfIndirectReference(0, this.getNewObjectNumber(r2.getReader(), r2.getNumber(), r2.getGeneration()));
            }
            try {
                if (fixedRef == null) {
                    fixedRef = this.addToBody(pdfImage).getIndirectReference();
                } else {
                    this.addToBody((PdfObject)pdfImage, fixedRef);
                }
            }
            catch (IOException ioe) {
                throw new ExceptionConverter(ioe);
            }
            this.imageDictionary.put(pdfImage.name(), fixedRef);
            return fixedRef;
        }
        return (PdfIndirectReference)this.imageDictionary.get(pdfImage.name());
    }

    PdfIndirectReference getImageReference(PdfName name) {
        return (PdfIndirectReference)this.imageDictionary.get(name);
    }

    protected PdfIndirectReference add(PdfICCBased icc) {
        PdfIndirectObject object;
        try {
            object = this.addToBody(icc);
        }
        catch (IOException ioe) {
            throw new ExceptionConverter(ioe);
        }
        return object.getIndirectReference();
    }

    protected PdfIndirectReference getReferenceJBIG2Globals(byte[] content) {
        PdfIndirectObject ref;
        PdfStream stream;
        if (content == null) {
            return null;
        }
        for (PdfStream pdfStream : this.JBIG2Globals.keySet()) {
            stream = pdfStream;
            if (!Arrays.equals(content, stream.getBytes())) continue;
            return this.JBIG2Globals.get(stream);
        }
        stream = new PdfStream(content);
        try {
            ref = this.addToBody(stream);
        }
        catch (IOException e) {
            return null;
        }
        this.JBIG2Globals.put(stream, ref.getIndirectReference());
        return ref.getIndirectReference();
    }

    public boolean fitsPage(Table table, float margin) {
        return this.pdf.bottom(table) > this.pdf.indentBottom() + margin;
    }

    public boolean fitsPage(Table table) {
        return this.fitsPage(table, 0.0f);
    }

    public boolean isUserProperties() {
        return this.userProperties;
    }

    public void setUserProperties(boolean userProperties) {
        this.userProperties = userProperties;
    }

    public boolean isRgbTransparencyBlending() {
        return this.rgbTransparencyBlending;
    }

    public void setRgbTransparencyBlending(boolean rgbTransparencyBlending) {
        this.rgbTransparencyBlending = rgbTransparencyBlending;
    }

    static class PdfTrailer
    extends PdfDictionary {
        PdfTrailer(int size, PdfIndirectReference root, PdfIndirectReference info, PdfIndirectReference encryption, PdfObject fileID, int prevxref) {
            this.put(PdfName.SIZE, new PdfNumber(size));
            this.put(PdfName.ROOT, root);
            if (info != null) {
                this.put(PdfName.INFO, info);
            }
            if (encryption != null) {
                this.put(PdfName.ENCRYPT, encryption);
            }
            if (fileID != null) {
                this.put(PdfName.ID, fileID);
            }
            if (prevxref > 0) {
                this.put(PdfName.PREV, new PdfNumber(prevxref));
            }
        }

        @Override
        public void toPdf(PdfWriter writer, OutputStream os) throws IOException {
            os.write(DocWriter.getISOBytes("trailer\n"));
            super.toPdf(null, os);
            os.write(10);
        }
    }

    public static class PdfBody {
        private static final int OBJSINSTREAM = 200;
        private TreeSet<PdfCrossReference> xrefs = new TreeSet();
        private int refnum;
        private long position;
        private PdfWriter writer;
        private ByteBuffer index;
        private ByteBuffer streamObjects;
        private int currentObjNum;
        private int numObj = 0;

        PdfBody(PdfWriter writer) {
            this.xrefs.add(new PdfCrossReference(0, 0L, 65535));
            this.position = writer.getOs().getCounter();
            this.refnum = 1;
            this.writer = writer;
        }

        void setRefnum(int refnum) {
            this.refnum = refnum;
        }

        private PdfCrossReference addToObjStm(PdfObject obj, int nObj) throws IOException {
            if (this.numObj >= 200) {
                this.flushObjStm();
            }
            if (this.index == null) {
                this.index = new ByteBuffer();
                this.streamObjects = new ByteBuffer();
                this.currentObjNum = this.getIndirectReferenceNumber();
                this.numObj = 0;
            }
            int p = this.streamObjects.size();
            int idx = this.numObj++;
            PdfEncryption enc = this.writer.crypto;
            this.writer.crypto = null;
            obj.toPdf(this.writer, this.streamObjects);
            this.writer.crypto = enc;
            this.streamObjects.append(' ');
            this.index.append(nObj).append(' ').append(p).append(' ');
            return new PdfCrossReference(2, nObj, this.currentObjNum, idx);
        }

        private void flushObjStm() throws IOException {
            if (this.numObj == 0) {
                return;
            }
            int first = this.index.size();
            this.index.append(this.streamObjects);
            PdfStream stream = new PdfStream(this.index.toByteArray());
            stream.flateCompress(this.writer.getCompressionLevel());
            stream.put(PdfName.TYPE, PdfName.OBJSTM);
            stream.put(PdfName.N, new PdfNumber(this.numObj));
            stream.put(PdfName.FIRST, new PdfNumber(first));
            this.add((PdfObject)stream, this.currentObjNum);
            this.index = null;
            this.streamObjects = null;
            this.numObj = 0;
        }

        PdfIndirectObject add(PdfObject object) throws IOException {
            return this.add(object, this.getIndirectReferenceNumber());
        }

        PdfIndirectObject add(PdfObject object, boolean inObjStm) throws IOException {
            return this.add(object, this.getIndirectReferenceNumber(), inObjStm);
        }

        PdfIndirectReference getPdfIndirectReference() {
            return new PdfIndirectReference(0, this.getIndirectReferenceNumber());
        }

        int getIndirectReferenceNumber() {
            int n = this.refnum++;
            this.xrefs.add(new PdfCrossReference(n, 0L, 65535));
            return n;
        }

        PdfIndirectObject add(PdfObject object, PdfIndirectReference ref) throws IOException {
            return this.add(object, ref.getNumber());
        }

        PdfIndirectObject add(PdfObject object, PdfIndirectReference ref, boolean inObjStm) throws IOException {
            return this.add(object, ref.getNumber(), inObjStm);
        }

        PdfIndirectObject add(PdfObject object, int refNumber) throws IOException {
            return this.add(object, refNumber, true);
        }

        PdfIndirectObject add(PdfObject object, int refNumber, boolean inObjStm) throws IOException {
            if (inObjStm && object.canBeInObjStm() && this.writer.isFullCompression()) {
                PdfCrossReference pxref = this.addToObjStm(object, refNumber);
                PdfIndirectObject indirect = new PdfIndirectObject(refNumber, object, this.writer);
                if (!this.xrefs.add(pxref)) {
                    this.xrefs.remove(pxref);
                    this.xrefs.add(pxref);
                }
                return indirect;
            }
            PdfIndirectObject indirect = new PdfIndirectObject(refNumber, object, this.writer);
            PdfCrossReference pxref = new PdfCrossReference(refNumber, this.position);
            if (!this.xrefs.add(pxref)) {
                this.xrefs.remove(pxref);
                this.xrefs.add(pxref);
            }
            indirect.writeTo(this.writer.getOs());
            this.position = this.writer.getOs().getCounter();
            return indirect;
        }

        long offset() {
            return this.position;
        }

        int size() {
            return Math.max(this.xrefs.last().getRefnum() + 1, this.refnum);
        }

        void writeCrossReferenceTable(OutputStream os, PdfIndirectReference root, PdfIndirectReference info, PdfIndirectReference encryption, PdfObject fileID, int prevxref) throws IOException {
            boolean useNewXrefFormat;
            int refNumber = 0;
            boolean bl = useNewXrefFormat = this.writer.isFullCompression() || this.position > 9999999999L;
            if (useNewXrefFormat) {
                this.flushObjStm();
                refNumber = this.getIndirectReferenceNumber();
                this.xrefs.add(new PdfCrossReference(refNumber, this.position));
            }
            PdfCrossReference entry = this.xrefs.first();
            int first = entry.getRefnum();
            int len = 0;
            ArrayList<Integer> sections = new ArrayList<Integer>();
            for (PdfCrossReference xref1 : this.xrefs) {
                entry = xref1;
                if (first + len == entry.getRefnum()) {
                    ++len;
                    continue;
                }
                sections.add(first);
                sections.add(len);
                first = entry.getRefnum();
                len = 1;
            }
            sections.add(first);
            sections.add(len);
            PdfTrailer trailer = new PdfTrailer(this.size(), root, info, encryption, fileID, prevxref);
            if (useNewXrefFormat) {
                int mid = 8 - (Long.numberOfLeadingZeros(this.position) >> 3);
                ByteBuffer buf = new ByteBuffer();
                Iterator<PdfCrossReference> iterator = this.xrefs.iterator();
                while (iterator.hasNext()) {
                    PdfCrossReference xref;
                    entry = xref = iterator.next();
                    entry.toPdf(mid, buf);
                }
                PdfStream xr = new PdfStream(buf.toByteArray());
                buf = null;
                xr.flateCompress(this.writer.getCompressionLevel());
                xr.putAll(trailer);
                xr.put(PdfName.W, new PdfArray(new int[]{1, mid, 2}));
                xr.put(PdfName.TYPE, PdfName.XREF);
                PdfArray idx = new PdfArray();
                for (Integer section : sections) {
                    idx.add(new PdfNumber(section));
                }
                xr.put(PdfName.INDEX, idx);
                PdfEncryption enc = this.writer.crypto;
                this.writer.crypto = null;
                PdfIndirectObject indirect = new PdfIndirectObject(refNumber, (PdfObject)xr, this.writer);
                indirect.writeTo(this.writer.getOs());
                this.writer.crypto = enc;
            } else {
                os.write(DocWriter.getISOBytes("xref\n"));
                Iterator<PdfCrossReference> i = this.xrefs.iterator();
                for (int k = 0; k < sections.size(); k += 2) {
                    first = (Integer)sections.get(k);
                    len = (Integer)sections.get(k + 1);
                    os.write(DocWriter.getISOBytes(String.valueOf(first)));
                    os.write(DocWriter.getISOBytes(" "));
                    os.write(DocWriter.getISOBytes(String.valueOf(len)));
                    os.write(10);
                    while (len-- > 0) {
                        entry = i.next();
                        entry.toPdf(os);
                    }
                }
                trailer.toPdf(this.writer, os);
            }
        }

        public static class PdfCrossReference
        implements Comparable<PdfCrossReference> {
            private static final String CROSS_REFERENCE_ENTRY_FORMAT = "%010d %05d %c \n";
            private int type;
            private long offset;
            private int refnum;
            private int generation;

            public PdfCrossReference(int refnum, long offset, int generation) {
                this.type = 0;
                this.offset = offset;
                this.refnum = refnum;
                this.generation = generation;
            }

            public PdfCrossReference(int refnum, long offset) {
                this.type = 1;
                this.offset = offset;
                this.refnum = refnum;
                this.generation = 0;
            }

            public PdfCrossReference(int type, int refnum, long offset, int generation) {
                this.type = type;
                this.offset = offset;
                this.refnum = refnum;
                this.generation = generation;
            }

            int getRefnum() {
                return this.refnum;
            }

            public void toPdf(OutputStream os) throws IOException {
                char inUse = this.generation == 65535 ? (char)'f' : 'n';
                os.write(String.format(CROSS_REFERENCE_ENTRY_FORMAT, this.offset, this.generation, Character.valueOf(inUse)).getBytes());
            }

            public void toPdf(int midSize, OutputStream os) throws IOException {
                os.write((byte)this.type);
                while (--midSize >= 0) {
                    os.write((byte)(this.offset >>> 8 * midSize & 0xFFL));
                }
                os.write((byte)(this.generation >>> 8 & 0xFF));
                os.write((byte)(this.generation & 0xFF));
            }

            @Override
            public int compareTo(PdfCrossReference reference) {
                return Integer.compare(this.refnum, reference.refnum);
            }

            public boolean equals(Object obj) {
                if (!(obj instanceof PdfCrossReference)) {
                    return false;
                }
                PdfCrossReference other = (PdfCrossReference)obj;
                return this.refnum == other.refnum;
            }

            public int hashCode() {
                return this.refnum;
            }
        }
    }
}

