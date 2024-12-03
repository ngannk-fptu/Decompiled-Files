/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.fontbox.ttf.TrueTypeFont
 */
package org.apache.pdfbox.pdmodel;

import java.awt.Point;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.io.RandomAccess;
import org.apache.pdfbox.io.RandomAccessBuffer;
import org.apache.pdfbox.io.RandomAccessBufferedFileInputStream;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.io.ScratchFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdfwriter.COSWriter;
import org.apache.pdfbox.pdmodel.DefaultResourceCache;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.ResourceCache;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.PDEncryption;
import org.apache.pdfbox.pdmodel.encryption.ProtectionPolicy;
import org.apache.pdfbox.pdmodel.encryption.SecurityHandler;
import org.apache.pdfbox.pdmodel.encryption.SecurityHandlerFactory;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.ExternalSigningSupport;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureInterface;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureOptions;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SigningSupport;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDSignatureField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTerminalField;

public class PDDocument
implements Closeable {
    private static final int[] RESERVE_BYTE_RANGE = new int[]{0, 1000000000, 1000000000, 1000000000};
    private static final Log LOG = LogFactory.getLog(PDDocument.class);
    private final COSDocument document;
    private PDDocumentInformation documentInformation;
    private PDDocumentCatalog documentCatalog;
    private PDEncryption encryption;
    private boolean allSecurityToBeRemoved;
    private Long documentId;
    private final RandomAccessRead pdfSource;
    private AccessPermission accessPermission;
    private final Set<PDFont> fontsToSubset = new HashSet<PDFont>();
    private final Set<TrueTypeFont> fontsToClose = new HashSet<TrueTypeFont>();
    private SignatureInterface signInterface;
    private SigningSupport signingSupport;
    private ResourceCache resourceCache = new DefaultResourceCache();
    private boolean signatureAdded = false;

    public PDDocument() {
        this(MemoryUsageSetting.setupMainMemoryOnly());
    }

    public PDDocument(MemoryUsageSetting memUsageSetting) {
        ScratchFile scratchFile = null;
        try {
            scratchFile = new ScratchFile(memUsageSetting);
        }
        catch (IOException ioe) {
            LOG.warn((Object)("Error initializing scratch file: " + ioe.getMessage() + ". Fall back to main memory usage only."));
            try {
                scratchFile = new ScratchFile(MemoryUsageSetting.setupMainMemoryOnly());
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        this.document = new COSDocument(scratchFile);
        this.pdfSource = null;
        COSDictionary trailer = new COSDictionary();
        this.document.setTrailer(trailer);
        COSDictionary rootDictionary = new COSDictionary();
        trailer.setItem(COSName.ROOT, (COSBase)rootDictionary);
        rootDictionary.setItem(COSName.TYPE, (COSBase)COSName.CATALOG);
        rootDictionary.setItem(COSName.VERSION, (COSBase)COSName.getPDFName("1.4"));
        COSDictionary pages = new COSDictionary();
        rootDictionary.setItem(COSName.PAGES, (COSBase)pages);
        pages.setItem(COSName.TYPE, (COSBase)COSName.PAGES);
        COSArray kidsArray = new COSArray();
        pages.setItem(COSName.KIDS, (COSBase)kidsArray);
        pages.setItem(COSName.COUNT, (COSBase)COSInteger.ZERO);
    }

    public PDDocument(COSDocument doc) {
        this(doc, null);
    }

    public PDDocument(COSDocument doc, RandomAccessRead source) {
        this(doc, source, null);
    }

    public PDDocument(COSDocument doc, RandomAccessRead source, AccessPermission permission) {
        this.document = doc;
        this.pdfSource = source;
        this.accessPermission = permission;
    }

    public void addPage(PDPage page) {
        this.getPages().add(page);
    }

    public void addSignature(PDSignature sigObject) throws IOException {
        this.addSignature(sigObject, new SignatureOptions());
    }

    public void addSignature(PDSignature sigObject, SignatureOptions options) throws IOException {
        this.addSignature(sigObject, null, options);
    }

    public void addSignature(PDSignature sigObject, SignatureInterface signatureInterface) throws IOException {
        this.addSignature(sigObject, signatureInterface, new SignatureOptions());
    }

    public void addSignature(PDSignature sigObject, SignatureInterface signatureInterface, SignatureOptions options) throws IOException {
        PDAnnotationWidget firstWidget;
        if (this.signatureAdded) {
            throw new IllegalStateException("Only one signature may be added in a document");
        }
        this.signatureAdded = true;
        int preferredSignatureSize = options.getPreferredSignatureSize();
        if (preferredSignatureSize > 0) {
            sigObject.setContents(new byte[preferredSignatureSize]);
        } else {
            sigObject.setContents(new byte[9472]);
        }
        sigObject.setByteRange(RESERVE_BYTE_RANGE);
        this.signInterface = signatureInterface;
        PDPageTree pageTree = this.getPages();
        int pageCount = pageTree.getCount();
        if (pageCount == 0) {
            throw new IllegalStateException("Cannot sign an empty document");
        }
        int startIndex = Math.min(Math.max(options.getPage(), 0), pageCount - 1);
        PDPage page = pageTree.get(startIndex);
        PDDocumentCatalog catalog = this.getDocumentCatalog();
        PDAcroForm acroForm = catalog.getAcroForm(null);
        catalog.getCOSObject().setNeedToBeUpdated(true);
        if (acroForm == null) {
            acroForm = new PDAcroForm(this);
            catalog.setAcroForm(acroForm);
        } else {
            acroForm.getCOSObject().setNeedToBeUpdated(true);
        }
        PDTerminalField signatureField = null;
        COSBase cosFieldBase = acroForm.getCOSObject().getDictionaryObject(COSName.FIELDS);
        if (cosFieldBase instanceof COSArray) {
            COSArray fieldArray = (COSArray)cosFieldBase;
            fieldArray.setNeedToBeUpdated(true);
            signatureField = this.findSignatureField(acroForm.getFieldIterator(), sigObject);
        } else {
            acroForm.getCOSObject().setItem(COSName.FIELDS, (COSBase)new COSArray());
        }
        if (signatureField == null) {
            signatureField = new PDSignatureField(acroForm);
            ((PDSignatureField)signatureField).setValue(sigObject);
            firstWidget = signatureField.getWidgets().get(0);
            firstWidget.setPage(page);
        } else {
            firstWidget = signatureField.getWidgets().get(0);
            sigObject.getCOSObject().setNeedToBeUpdated(true);
        }
        firstWidget.setPrinted(true);
        List<PDField> acroFormFields = acroForm.getFields();
        acroForm.getCOSObject().setDirect(true);
        acroForm.setSignaturesExist(true);
        acroForm.setAppendOnly(true);
        boolean checkFields = this.checkSignatureField(acroForm.getFieldIterator(), (PDSignatureField)signatureField);
        if (checkFields) {
            signatureField.getCOSObject().setNeedToBeUpdated(true);
        } else {
            acroFormFields.add(signatureField);
        }
        COSDocument visualSignature = options.getVisualSignature();
        if (visualSignature == null) {
            this.prepareNonVisibleSignature(firstWidget);
        } else {
            this.prepareVisibleSignature(firstWidget, acroForm, visualSignature);
        }
        List<PDAnnotation> annotations = page.getAnnotations();
        page.setAnnotations(annotations);
        if (!(checkFields && annotations instanceof COSArrayList && acroFormFields instanceof COSArrayList && ((COSArrayList)annotations).toList().equals(((COSArrayList)acroFormFields).toList()))) {
            if (this.checkSignatureAnnotation(annotations, firstWidget)) {
                firstWidget.getCOSObject().setNeedToBeUpdated(true);
            } else {
                annotations.add(firstWidget);
            }
        }
        page.getCOSObject().setNeedToBeUpdated(true);
    }

    private PDSignatureField findSignatureField(Iterator<PDField> fieldIterator, PDSignature sigObject) {
        PDSignatureField signatureField = null;
        while (fieldIterator.hasNext()) {
            PDSignature signature;
            PDField pdField = fieldIterator.next();
            if (!(pdField instanceof PDSignatureField) || (signature = ((PDSignatureField)pdField).getSignature()) == null || !signature.getCOSObject().equals(sigObject.getCOSObject())) continue;
            signatureField = (PDSignatureField)pdField;
            break;
        }
        return signatureField;
    }

    private boolean checkSignatureField(Iterator<PDField> fieldIterator, PDSignatureField signatureField) {
        while (fieldIterator.hasNext()) {
            PDField field = fieldIterator.next();
            if (!(field instanceof PDSignatureField) || !field.getCOSObject().equals(signatureField.getCOSObject())) continue;
            return true;
        }
        return false;
    }

    private boolean checkSignatureAnnotation(List<PDAnnotation> annotations, PDAnnotationWidget widget) {
        for (PDAnnotation annotation : annotations) {
            if (!annotation.getCOSObject().equals(widget.getCOSObject())) continue;
            return true;
        }
        return false;
    }

    private void prepareNonVisibleSignature(PDAnnotationWidget firstWidget) {
        firstWidget.setRectangle(new PDRectangle());
        PDAppearanceDictionary appearanceDictionary = new PDAppearanceDictionary();
        PDAppearanceStream appearanceStream = new PDAppearanceStream(this);
        appearanceStream.setBBox(new PDRectangle());
        appearanceDictionary.setNormalAppearance(appearanceStream);
        firstWidget.setAppearance(appearanceDictionary);
    }

    private void prepareVisibleSignature(PDAnnotationWidget firstWidget, PDAcroForm acroForm, COSDocument visualSignature) {
        boolean annotNotFound = true;
        boolean sigFieldNotFound = true;
        for (COSObject cosObject : visualSignature.getObjects()) {
            if (!annotNotFound && !sigFieldNotFound) break;
            COSBase base = cosObject.getObject();
            if (!(base instanceof COSDictionary)) continue;
            COSDictionary cosBaseDict = (COSDictionary)base;
            COSBase type = cosBaseDict.getDictionaryObject(COSName.TYPE);
            if (annotNotFound && COSName.ANNOT.equals(type)) {
                this.assignSignatureRectangle(firstWidget, cosBaseDict);
                annotNotFound = false;
            }
            COSBase fieldType = cosBaseDict.getDictionaryObject(COSName.FT);
            COSBase apDict = cosBaseDict.getDictionaryObject(COSName.AP);
            if (!sigFieldNotFound || !COSName.SIG.equals(fieldType) || !(apDict instanceof COSDictionary)) continue;
            this.assignAppearanceDictionary(firstWidget, (COSDictionary)apDict);
            this.assignAcroFormDefaultResource(acroForm, cosBaseDict);
            sigFieldNotFound = false;
        }
        if (annotNotFound || sigFieldNotFound) {
            throw new IllegalArgumentException("Template is missing required objects");
        }
    }

    private void assignSignatureRectangle(PDAnnotationWidget firstWidget, COSDictionary annotDict) {
        PDRectangle existingRectangle = firstWidget.getRectangle();
        if (existingRectangle == null || existingRectangle.getCOSArray().size() != 4) {
            COSArray rectArray = (COSArray)annotDict.getDictionaryObject(COSName.RECT);
            PDRectangle rect = new PDRectangle(rectArray);
            firstWidget.setRectangle(rect);
        }
    }

    private void assignAppearanceDictionary(PDAnnotationWidget firstWidget, COSDictionary apDict) {
        PDAppearanceDictionary ap = new PDAppearanceDictionary(apDict);
        apDict.setDirect(true);
        firstWidget.setAppearance(ap);
    }

    private void assignAcroFormDefaultResource(PDAcroForm acroForm, COSDictionary newDict) {
        COSBase newBase = newDict.getDictionaryObject(COSName.DR);
        if (newBase instanceof COSDictionary) {
            COSDictionary newDR = (COSDictionary)newBase;
            PDResources defaultResources = acroForm.getDefaultResources();
            if (defaultResources == null) {
                acroForm.getCOSObject().setItem(COSName.DR, (COSBase)newDR);
                newDR.setDirect(true);
                newDR.setNeedToBeUpdated(true);
            } else {
                COSDictionary oldDR = defaultResources.getCOSObject();
                COSBase newXObjectBase = newDR.getItem(COSName.XOBJECT);
                COSBase oldXObjectBase = oldDR.getItem(COSName.XOBJECT);
                if (newXObjectBase instanceof COSDictionary && oldXObjectBase instanceof COSDictionary) {
                    ((COSDictionary)oldXObjectBase).addAll((COSDictionary)newXObjectBase);
                    oldDR.setNeedToBeUpdated(true);
                }
            }
        }
    }

    @Deprecated
    public void addSignatureField(List<PDSignatureField> sigFields, SignatureInterface signatureInterface, SignatureOptions options) throws IOException {
        PDDocumentCatalog catalog = this.getDocumentCatalog();
        catalog.getCOSObject().setNeedToBeUpdated(true);
        PDAcroForm acroForm = catalog.getAcroForm(null);
        if (acroForm == null) {
            acroForm = new PDAcroForm(this);
            catalog.setAcroForm(acroForm);
        }
        COSDictionary acroFormDict = acroForm.getCOSObject();
        acroFormDict.setDirect(true);
        acroFormDict.setNeedToBeUpdated(true);
        if (!acroForm.isSignaturesExist()) {
            acroForm.setSignaturesExist(true);
        }
        List<PDField> acroformFields = acroForm.getFields();
        for (PDSignatureField sigField : sigFields) {
            sigField.getCOSObject().setNeedToBeUpdated(true);
            boolean checkSignatureField = this.checkSignatureField(acroForm.getFieldIterator(), sigField);
            if (checkSignatureField) {
                sigField.getCOSObject().setNeedToBeUpdated(true);
            } else {
                acroformFields.add(sigField);
            }
            if (sigField.getSignature() == null) continue;
            sigField.getCOSObject().setNeedToBeUpdated(true);
            if (options == null) {
                // empty if block
            }
            this.addSignature(sigField.getSignature(), signatureInterface, options);
        }
    }

    public void removePage(PDPage page) {
        this.getPages().remove(page);
    }

    public void removePage(int pageNumber) {
        this.getPages().remove(pageNumber);
    }

    public PDPage importPage(PDPage page) throws IOException {
        PDPage importedPage = new PDPage(new COSDictionary(page.getCOSObject()), this.resourceCache);
        PDStream dest = new PDStream(this, page.getContents(), COSName.FLATE_DECODE);
        importedPage.setContents(dest);
        this.addPage(importedPage);
        importedPage.setCropBox(new PDRectangle(page.getCropBox().getCOSArray()));
        importedPage.setMediaBox(new PDRectangle(page.getMediaBox().getCOSArray()));
        importedPage.setRotation(page.getRotation());
        if (page.getResources() != null && !page.getCOSObject().containsKey(COSName.RESOURCES)) {
            LOG.warn((Object)"inherited resources of source document are not imported to destination page");
            LOG.warn((Object)"call importedPage.setResources(page.getResources()) to do this");
        }
        return importedPage;
    }

    public COSDocument getDocument() {
        return this.document;
    }

    public PDDocumentInformation getDocumentInformation() {
        if (this.documentInformation == null) {
            COSDictionary trailer = this.document.getTrailer();
            COSDictionary infoDic = trailer.getCOSDictionary(COSName.INFO);
            if (infoDic == null) {
                infoDic = new COSDictionary();
                trailer.setItem(COSName.INFO, (COSBase)infoDic);
            }
            this.documentInformation = new PDDocumentInformation(infoDic);
        }
        return this.documentInformation;
    }

    public void setDocumentInformation(PDDocumentInformation info) {
        this.documentInformation = info;
        this.document.getTrailer().setItem(COSName.INFO, (COSBase)info.getCOSObject());
    }

    public PDDocumentCatalog getDocumentCatalog() {
        if (this.documentCatalog == null) {
            COSDictionary trailer = this.document.getTrailer();
            COSBase dictionary = trailer.getDictionaryObject(COSName.ROOT);
            this.documentCatalog = dictionary instanceof COSDictionary ? new PDDocumentCatalog(this, (COSDictionary)dictionary) : new PDDocumentCatalog(this);
        }
        return this.documentCatalog;
    }

    public boolean isEncrypted() {
        return this.document.isEncrypted();
    }

    public PDEncryption getEncryption() {
        if (this.encryption == null && this.isEncrypted()) {
            this.encryption = new PDEncryption(this.document.getEncryptionDictionary());
        }
        return this.encryption;
    }

    public void setEncryptionDictionary(PDEncryption encryption) throws IOException {
        this.encryption = encryption;
    }

    public PDSignature getLastSignatureDictionary() throws IOException {
        List<PDSignature> signatureDictionaries = this.getSignatureDictionaries();
        int size = signatureDictionaries.size();
        if (size > 0) {
            return signatureDictionaries.get(size - 1);
        }
        return null;
    }

    public List<PDSignatureField> getSignatureFields() throws IOException {
        ArrayList<PDSignatureField> fields = new ArrayList<PDSignatureField>();
        PDAcroForm acroForm = this.getDocumentCatalog().getAcroForm(null);
        if (acroForm != null) {
            for (PDField field : acroForm.getFieldTree()) {
                if (!(field instanceof PDSignatureField)) continue;
                fields.add((PDSignatureField)field);
            }
        }
        return fields;
    }

    public List<PDSignature> getSignatureDictionaries() throws IOException {
        ArrayList<PDSignature> signatures = new ArrayList<PDSignature>();
        for (PDSignatureField field : this.getSignatureFields()) {
            COSBase value = field.getCOSObject().getDictionaryObject(COSName.V);
            if (value == null) continue;
            signatures.add(new PDSignature((COSDictionary)value));
        }
        return signatures;
    }

    public void registerTrueTypeFontForClosing(TrueTypeFont ttf) {
        this.fontsToClose.add(ttf);
    }

    Set<PDFont> getFontsToSubset() {
        return this.fontsToSubset;
    }

    public static PDDocument load(File file) throws IOException {
        return PDDocument.load(file, "", MemoryUsageSetting.setupMainMemoryOnly());
    }

    public static PDDocument load(File file, MemoryUsageSetting memUsageSetting) throws IOException {
        return PDDocument.load(file, "", null, null, memUsageSetting);
    }

    public static PDDocument load(File file, String password) throws IOException {
        return PDDocument.load(file, password, null, null, MemoryUsageSetting.setupMainMemoryOnly());
    }

    public static PDDocument load(File file, String password, MemoryUsageSetting memUsageSetting) throws IOException {
        return PDDocument.load(file, password, null, null, memUsageSetting);
    }

    public static PDDocument load(File file, String password, InputStream keyStore, String alias) throws IOException {
        return PDDocument.load(file, password, keyStore, alias, MemoryUsageSetting.setupMainMemoryOnly());
    }

    public static PDDocument load(File file, String password, InputStream keyStore, String alias, MemoryUsageSetting memUsageSetting) throws IOException {
        RandomAccessBufferedFileInputStream raFile = new RandomAccessBufferedFileInputStream(file);
        try {
            return PDDocument.load(raFile, password, keyStore, alias, memUsageSetting);
        }
        catch (IOException ioe) {
            IOUtils.closeQuietly(raFile);
            throw ioe;
        }
    }

    private static PDDocument load(RandomAccessBufferedFileInputStream raFile, String password, InputStream keyStore, String alias, MemoryUsageSetting memUsageSetting) throws IOException {
        ScratchFile scratchFile = new ScratchFile(memUsageSetting);
        try {
            PDFParser parser = new PDFParser(raFile, password, keyStore, alias, scratchFile);
            parser.parse();
            return parser.getPDDocument();
        }
        catch (IOException ioe) {
            IOUtils.closeQuietly(scratchFile);
            throw ioe;
        }
    }

    public static PDDocument load(InputStream input) throws IOException {
        return PDDocument.load(input, "", null, null, MemoryUsageSetting.setupMainMemoryOnly());
    }

    public static PDDocument load(InputStream input, MemoryUsageSetting memUsageSetting) throws IOException {
        return PDDocument.load(input, "", null, null, memUsageSetting);
    }

    public static PDDocument load(InputStream input, String password) throws IOException {
        return PDDocument.load(input, password, null, null, MemoryUsageSetting.setupMainMemoryOnly());
    }

    public static PDDocument load(InputStream input, String password, InputStream keyStore, String alias) throws IOException {
        return PDDocument.load(input, password, keyStore, alias, MemoryUsageSetting.setupMainMemoryOnly());
    }

    public static PDDocument load(InputStream input, String password, MemoryUsageSetting memUsageSetting) throws IOException {
        return PDDocument.load(input, password, null, null, memUsageSetting);
    }

    public static PDDocument load(InputStream input, String password, InputStream keyStore, String alias, MemoryUsageSetting memUsageSetting) throws IOException {
        ScratchFile scratchFile = new ScratchFile(memUsageSetting);
        try {
            RandomAccess source = scratchFile.createBuffer(input);
            PDFParser parser = new PDFParser(source, password, keyStore, alias, scratchFile);
            parser.parse();
            return parser.getPDDocument();
        }
        catch (IOException ioe) {
            IOUtils.closeQuietly(scratchFile);
            throw ioe;
        }
    }

    public static PDDocument load(byte[] input) throws IOException {
        return PDDocument.load(input, "");
    }

    public static PDDocument load(byte[] input, String password) throws IOException {
        return PDDocument.load(input, password, null, null);
    }

    public static PDDocument load(byte[] input, String password, InputStream keyStore, String alias) throws IOException {
        return PDDocument.load(input, password, keyStore, alias, MemoryUsageSetting.setupMainMemoryOnly());
    }

    public static PDDocument load(byte[] input, String password, InputStream keyStore, String alias, MemoryUsageSetting memUsageSetting) throws IOException {
        ScratchFile scratchFile = new ScratchFile(memUsageSetting);
        RandomAccessBuffer source = new RandomAccessBuffer(input);
        PDFParser parser = new PDFParser(source, password, keyStore, alias, scratchFile);
        parser.parse();
        return parser.getPDDocument();
    }

    public void save(String fileName) throws IOException {
        this.save(new File(fileName));
    }

    public void save(File file) throws IOException {
        this.save(new BufferedOutputStream(new FileOutputStream(file)));
    }

    public void save(OutputStream output) throws IOException {
        if (this.document.isClosed()) {
            throw new IOException("Cannot save a document which has been closed");
        }
        this.subsetDesignatedFonts();
        COSWriter writer = new COSWriter(output);
        try {
            writer.write(this);
        }
        finally {
            writer.close();
        }
    }

    private void subsetDesignatedFonts() throws IOException {
        for (PDFont font : this.fontsToSubset) {
            font.subset();
        }
        this.fontsToSubset.clear();
    }

    public void saveIncremental(OutputStream output) throws IOException {
        this.subsetDesignatedFonts();
        COSWriter writer = null;
        try {
            if (this.pdfSource == null) {
                throw new IllegalStateException("document was not loaded from a file or a stream");
            }
            writer = new COSWriter(output, this.pdfSource);
            writer.write(this, this.signInterface);
        }
        finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void saveIncremental(OutputStream output, Set<COSDictionary> objectsToWrite) throws IOException {
        this.subsetDesignatedFonts();
        if (this.pdfSource == null) {
            throw new IllegalStateException("document was not loaded from a file or a stream");
        }
        COSWriter writer = null;
        try {
            writer = new COSWriter(output, this.pdfSource, objectsToWrite);
            writer.write(this, this.signInterface);
        }
        finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public ExternalSigningSupport saveIncrementalForExternalSigning(OutputStream output) throws IOException {
        int[] byteRange;
        this.subsetDesignatedFonts();
        if (this.pdfSource == null) {
            throw new IllegalStateException("document was not loaded from a file or a stream");
        }
        PDSignature foundSignature = null;
        Iterator<PDSignature> iterator = this.getSignatureDictionaries().iterator();
        while (iterator.hasNext()) {
            PDSignature sig;
            foundSignature = sig = iterator.next();
            if (!sig.getCOSObject().isNeedToBeUpdated()) continue;
            break;
        }
        if (!Arrays.equals(byteRange = foundSignature.getByteRange(), RESERVE_BYTE_RANGE)) {
            throw new IllegalStateException("signature reserve byte range has been changed after addSignature(), please set the byte range that existed after addSignature()");
        }
        COSWriter writer = new COSWriter(output, this.pdfSource);
        writer.write(this);
        this.signingSupport = new SigningSupport(writer);
        return this.signingSupport;
    }

    public PDPage getPage(int pageIndex) {
        return this.getDocumentCatalog().getPages().get(pageIndex);
    }

    public PDPageTree getPages() {
        return this.getDocumentCatalog().getPages();
    }

    public int getNumberOfPages() {
        return this.getDocumentCatalog().getPages().getCount();
    }

    @Override
    public void close() throws IOException {
        if (!this.document.isClosed()) {
            IOException firstException = null;
            if (this.signingSupport != null) {
                firstException = IOUtils.closeAndLogException(this.signingSupport, LOG, "SigningSupport", firstException);
            }
            firstException = IOUtils.closeAndLogException(this.document, LOG, "COSDocument", firstException);
            if (this.pdfSource != null) {
                firstException = IOUtils.closeAndLogException(this.pdfSource, LOG, "RandomAccessRead pdfSource", firstException);
            }
            for (TrueTypeFont ttf : this.fontsToClose) {
                firstException = IOUtils.closeAndLogException((Closeable)ttf, LOG, "TrueTypeFont", firstException);
            }
            if (firstException != null) {
                throw firstException;
            }
        }
    }

    public void protect(ProtectionPolicy policy) throws IOException {
        SecurityHandler securityHandler;
        if (this.isAllSecurityToBeRemoved()) {
            LOG.warn((Object)"do not call setAllSecurityToBeRemoved(true) before calling protect(), as protect() implies setAllSecurityToBeRemoved(false)");
            this.setAllSecurityToBeRemoved(false);
        }
        if (!this.isEncrypted()) {
            this.encryption = new PDEncryption();
        }
        if ((securityHandler = SecurityHandlerFactory.INSTANCE.newSecurityHandlerForPolicy(policy)) == null) {
            throw new IOException("No security handler for policy " + policy);
        }
        this.getEncryption().setSecurityHandler(securityHandler);
    }

    public AccessPermission getCurrentAccessPermission() {
        if (this.accessPermission == null) {
            this.accessPermission = AccessPermission.getOwnerAccessPermission();
        }
        return this.accessPermission;
    }

    public boolean isAllSecurityToBeRemoved() {
        return this.allSecurityToBeRemoved;
    }

    public void setAllSecurityToBeRemoved(boolean removeAllSecurity) {
        this.allSecurityToBeRemoved = removeAllSecurity;
    }

    public Long getDocumentId() {
        return this.documentId;
    }

    public void setDocumentId(Long docId) {
        this.documentId = docId;
    }

    public float getVersion() {
        float headerVersionFloat = this.getDocument().getVersion();
        if (headerVersionFloat >= 1.4f) {
            String catalogVersion = this.getDocumentCatalog().getVersion();
            float catalogVersionFloat = -1.0f;
            if (catalogVersion != null) {
                try {
                    catalogVersionFloat = Float.parseFloat(catalogVersion);
                }
                catch (NumberFormatException exception) {
                    LOG.error((Object)"Can't extract the version number of the document catalog.", (Throwable)exception);
                }
            }
            return Math.max(catalogVersionFloat, headerVersionFloat);
        }
        return headerVersionFloat;
    }

    public void setVersion(float newVersion) {
        float currentVersion = this.getVersion();
        if (newVersion == currentVersion) {
            return;
        }
        if (newVersion < currentVersion) {
            LOG.error((Object)"It's not allowed to downgrade the version of a pdf.");
            return;
        }
        if (this.getDocument().getVersion() >= 1.4f) {
            this.getDocumentCatalog().setVersion(Float.toString(newVersion));
        } else {
            this.getDocument().setVersion(newVersion);
        }
    }

    public ResourceCache getResourceCache() {
        return this.resourceCache;
    }

    public void setResourceCache(ResourceCache resourceCache) {
        this.resourceCache = resourceCache;
    }

    static {
        try {
            WritableRaster raster = Raster.createBandedRaster(0, 1, 1, 3, new Point(0, 0));
            PDDeviceRGB.INSTANCE.toRGBImage(raster);
        }
        catch (IOException ex) {
            LOG.debug((Object)"voodoo error", (Throwable)ex);
        }
        try {
            COSNumber.get("0");
            COSNumber.get("1");
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }
}

