/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.bouncycastle.BouncyCastleHelper;
import com.lowagie.text.DocWriter;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.exceptions.BadPasswordException;
import com.lowagie.text.exceptions.InvalidPdfException;
import com.lowagie.text.exceptions.UnsupportedPdfException;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.IntHashtable;
import com.lowagie.text.pdf.LZWDecoder;
import com.lowagie.text.pdf.PRAcroForm;
import com.lowagie.text.pdf.PRIndirectReference;
import com.lowagie.text.pdf.PRStream;
import com.lowagie.text.pdf.PRTokeniser;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfEncodings;
import com.lowagie.text.pdf.PdfEncryption;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfLiteral;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNameTree;
import com.lowagie.text.pdf.PdfNull;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReaderInstance;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.lowagie.text.pdf.SequenceList;
import com.lowagie.text.pdf.interfaces.PdfViewerPreferences;
import com.lowagie.text.pdf.internal.PdfViewerPreferencesImp;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.Key;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.zip.InflaterInputStream;

public class PdfReader
implements PdfViewerPreferences,
Closeable {
    static final PdfName[] pageInhCandidates = new PdfName[]{PdfName.MEDIABOX, PdfName.ROTATE, PdfName.RESOURCES, PdfName.CROPBOX};
    private static final byte[] endstream = PdfEncodings.convertToBytes("endstream", null);
    private static final byte[] endobj = PdfEncodings.convertToBytes("endobj", null);
    protected PRTokeniser tokens;
    protected int[] xref;
    protected Map<Integer, IntHashtable> objStmMark;
    protected IntHashtable objStmToOffset;
    protected boolean newXrefType;
    private List<PdfObject> xrefObj;
    PdfDictionary rootPages;
    protected PdfDictionary trailer;
    protected PdfDictionary catalog;
    protected PageRefs pageRefs;
    protected PRAcroForm acroForm = null;
    protected boolean acroFormParsed = false;
    protected boolean encrypted = false;
    protected boolean rebuilt = false;
    protected int freeXref;
    protected boolean tampered = false;
    protected int lastXref;
    protected int eofPos;
    protected char pdfVersion;
    protected PdfEncryption decrypt;
    protected byte[] password = null;
    protected Key certificateKey = null;
    protected Certificate certificate = null;
    protected String certificateKeyProvider = null;
    private boolean ownerPasswordUsed;
    private boolean modificationAllowedWithoutOwnerPassword = true;
    protected List<PdfObject> strings = new ArrayList<PdfObject>();
    protected boolean sharedStreams = true;
    protected boolean consolidateNamedDestinations = false;
    protected boolean remoteToLocalNamedDestinations = false;
    protected int rValue;
    protected int pValue;
    private int objNum;
    private int objGen;
    private int fileLength;
    private boolean hybridXref;
    private int lastXrefPartial = -1;
    private boolean partial;
    private PRIndirectReference cryptoRef;
    private final PdfViewerPreferencesImp viewerPreferences = new PdfViewerPreferencesImp();
    private boolean encryptionError;
    private boolean appendable;
    private int readDepth = 0;

    protected PdfReader() {
    }

    public PdfReader(String filename) throws IOException {
        this(filename, null);
    }

    public PdfReader(String filename, byte[] ownerPassword) throws IOException {
        this.password = ownerPassword;
        this.tokens = new PRTokeniser(filename);
        this.readPdf();
    }

    public PdfReader(byte[] pdfIn) throws IOException {
        this(pdfIn, null);
    }

    public PdfReader(byte[] pdfIn, byte[] ownerPassword) throws IOException {
        this.password = ownerPassword;
        this.tokens = new PRTokeniser(pdfIn);
        this.readPdf();
    }

    public PdfReader(String filename, Certificate certificate, Key certificateKey, String certificateKeyProvider) throws IOException {
        this.certificate = certificate;
        this.certificateKey = certificateKey;
        this.certificateKeyProvider = certificateKeyProvider;
        this.tokens = new PRTokeniser(filename);
        this.readPdf();
    }

    public PdfReader(URL url) throws IOException {
        this(url, null);
    }

    public PdfReader(URL url, byte[] ownerPassword) throws IOException {
        this.password = ownerPassword;
        this.tokens = new PRTokeniser(new RandomAccessFileOrArray(url));
        this.readPdf();
    }

    public PdfReader(InputStream is, byte[] ownerPassword) throws IOException {
        this.password = ownerPassword;
        this.tokens = new PRTokeniser(new RandomAccessFileOrArray(is));
        this.readPdf();
    }

    public PdfReader(InputStream is) throws IOException {
        this(is, null);
    }

    public PdfReader(RandomAccessFileOrArray raf, byte[] ownerPassword) throws IOException {
        this.password = ownerPassword;
        this.partial = true;
        this.tokens = new PRTokeniser(raf);
        this.readPdfPartial();
    }

    public PdfReader(PdfReader reader) {
        this.appendable = reader.appendable;
        this.consolidateNamedDestinations = reader.consolidateNamedDestinations;
        this.encrypted = reader.encrypted;
        this.rebuilt = reader.rebuilt;
        this.sharedStreams = reader.sharedStreams;
        this.tampered = reader.tampered;
        this.password = reader.password;
        this.pdfVersion = reader.pdfVersion;
        this.eofPos = reader.eofPos;
        this.freeXref = reader.freeXref;
        this.lastXref = reader.lastXref;
        this.tokens = new PRTokeniser(reader.tokens.getSafeFile());
        if (reader.decrypt != null) {
            this.decrypt = new PdfEncryption(reader.decrypt);
        }
        this.pValue = reader.pValue;
        this.rValue = reader.rValue;
        this.xrefObj = new ArrayList<PdfObject>(reader.xrefObj);
        for (int k = 0; k < reader.xrefObj.size(); ++k) {
            this.xrefObj.set(k, PdfReader.duplicatePdfObject(reader.xrefObj.get(k), this));
        }
        this.pageRefs = new PageRefs(reader.pageRefs, this);
        this.trailer = (PdfDictionary)PdfReader.duplicatePdfObject(reader.trailer, this);
        this.catalog = this.trailer.getAsDict(PdfName.ROOT);
        this.rootPages = this.catalog.getAsDict(PdfName.PAGES);
        this.fileLength = reader.fileLength;
        this.partial = reader.partial;
        this.hybridXref = reader.hybridXref;
        this.objStmToOffset = reader.objStmToOffset;
        this.xref = reader.xref;
        this.cryptoRef = (PRIndirectReference)PdfReader.duplicatePdfObject(reader.cryptoRef, this);
        this.ownerPasswordUsed = reader.ownerPasswordUsed;
    }

    public RandomAccessFileOrArray getSafeFile() {
        return this.tokens.getSafeFile();
    }

    protected PdfReaderInstance getPdfReaderInstance(PdfWriter writer) {
        return new PdfReaderInstance(this, writer);
    }

    public int getNumberOfPages() {
        return this.pageRefs.size();
    }

    public PdfDictionary getCatalog() {
        return this.catalog;
    }

    public PRAcroForm getAcroForm() {
        if (!this.acroFormParsed) {
            this.acroFormParsed = true;
            PdfObject form = this.catalog.get(PdfName.ACROFORM);
            if (form != null) {
                try {
                    this.acroForm = new PRAcroForm(this);
                    this.acroForm.readAcroForm((PdfDictionary)PdfReader.getPdfObject(form));
                }
                catch (Exception e) {
                    this.acroForm = null;
                }
            }
        }
        return this.acroForm;
    }

    public int getPageRotation(int index) {
        return this.getPageRotation(this.pageRefs.getPageNRelease(index));
    }

    int getPageRotation(PdfDictionary page) {
        PdfNumber rotate = page.getAsNumber(PdfName.ROTATE);
        if (rotate == null) {
            return 0;
        }
        int n = rotate.intValue();
        return (n %= 360) < 0 ? n + 360 : n;
    }

    public Rectangle getPageSizeWithRotation(int index) {
        return this.getPageSizeWithRotation(this.pageRefs.getPageNRelease(index));
    }

    public Rectangle getPageSizeWithRotation(PdfDictionary page) {
        Rectangle rect = this.getPageSize(page);
        for (int rotation = this.getPageRotation(page); rotation > 0; rotation -= 90) {
            rect = rect.rotate();
        }
        return rect;
    }

    public Rectangle getPageSize(int index) {
        return this.getPageSize(this.pageRefs.getPageNRelease(index));
    }

    public Rectangle getPageSize(PdfDictionary page) {
        PdfArray mediaBox = page.getAsArray(PdfName.MEDIABOX);
        return PdfReader.getNormalizedRectangle(mediaBox);
    }

    public Rectangle getCropBox(int index) {
        PdfDictionary page = this.pageRefs.getPageNRelease(index);
        PdfArray cropBox = (PdfArray)PdfReader.getPdfObjectRelease(page.get(PdfName.CROPBOX));
        if (cropBox == null) {
            return this.getPageSize(page);
        }
        return PdfReader.getNormalizedRectangle(cropBox);
    }

    public Rectangle getBoxSize(int index, String boxName) {
        PdfDictionary page = this.pageRefs.getPageNRelease(index);
        PdfArray box = null;
        switch (boxName) {
            case "trim": {
                box = (PdfArray)PdfReader.getPdfObjectRelease(page.get(PdfName.TRIMBOX));
                break;
            }
            case "art": {
                box = (PdfArray)PdfReader.getPdfObjectRelease(page.get(PdfName.ARTBOX));
                break;
            }
            case "bleed": {
                box = (PdfArray)PdfReader.getPdfObjectRelease(page.get(PdfName.BLEEDBOX));
                break;
            }
            case "crop": {
                box = (PdfArray)PdfReader.getPdfObjectRelease(page.get(PdfName.CROPBOX));
                break;
            }
            case "media": {
                box = (PdfArray)PdfReader.getPdfObjectRelease(page.get(PdfName.MEDIABOX));
            }
        }
        if (box == null) {
            return null;
        }
        return PdfReader.getNormalizedRectangle(box);
    }

    public Map<String, String> getInfo() {
        HashMap<String, String> map = new HashMap<String, String>();
        PdfDictionary info = this.trailer.getAsDict(PdfName.INFO);
        if (info == null) {
            return map;
        }
        for (PdfName o : info.getKeys()) {
            PdfName key = o;
            PdfObject obj = PdfReader.getPdfObject(info.get(key));
            if (obj == null) continue;
            String value = obj.toString();
            switch (obj.type()) {
                case 3: {
                    value = ((PdfString)obj).toUnicodeString();
                    break;
                }
                case 4: {
                    value = PdfName.decodeName(value);
                }
            }
            map.put(PdfName.decodeName(key.toString()), value);
        }
        return map;
    }

    public static Rectangle getNormalizedRectangle(PdfArray box) {
        float llx = ((PdfNumber)PdfReader.getPdfObjectRelease(box.getPdfObject(0))).floatValue();
        float lly = ((PdfNumber)PdfReader.getPdfObjectRelease(box.getPdfObject(1))).floatValue();
        float urx = ((PdfNumber)PdfReader.getPdfObjectRelease(box.getPdfObject(2))).floatValue();
        float ury = ((PdfNumber)PdfReader.getPdfObjectRelease(box.getPdfObject(3))).floatValue();
        return new Rectangle(Math.min(llx, urx), Math.min(lly, ury), Math.max(llx, urx), Math.max(lly, ury));
    }

    protected void readPdf() throws IOException {
        try {
            this.fileLength = this.tokens.getFile().length();
            this.pdfVersion = this.tokens.checkPdfHeader();
            try {
                this.readXref();
            }
            catch (Exception e) {
                try {
                    this.rebuilt = true;
                    this.rebuildXref();
                    this.lastXref = -1;
                }
                catch (Exception ne) {
                    throw new InvalidPdfException(MessageLocalization.getComposedMessage("rebuild.failed.1.original.message.2", ne.getMessage(), e.getMessage()));
                }
            }
            try {
                this.readDocObj();
            }
            catch (Exception e) {
                if (e instanceof BadPasswordException) {
                    throw new BadPasswordException(e.getMessage());
                }
                if (this.rebuilt || this.encryptionError) {
                    throw new InvalidPdfException(e.getMessage());
                }
                this.rebuilt = true;
                this.encrypted = false;
                this.rebuildXref();
                this.lastXref = -1;
                this.readDocObj();
            }
            this.strings.clear();
            this.readPages();
            this.eliminateSharedStreams();
            this.removeUnusedObjects();
        }
        finally {
            try {
                this.tokens.close();
            }
            catch (Exception exception) {}
        }
    }

    protected void readPdfPartial() throws IOException {
        try {
            this.fileLength = this.tokens.getFile().length();
            this.pdfVersion = this.tokens.checkPdfHeader();
            try {
                this.readXref();
            }
            catch (Exception e) {
                try {
                    this.rebuilt = true;
                    this.rebuildXref();
                    this.lastXref = -1;
                }
                catch (Exception ne) {
                    throw new InvalidPdfException(MessageLocalization.getComposedMessage("rebuild.failed.1.original.message.2", ne.getMessage(), e.getMessage()));
                }
            }
            this.readDocObjPartial();
            this.readPages();
        }
        catch (IOException e) {
            try {
                this.tokens.close();
            }
            catch (Exception exception) {
                // empty catch block
            }
            throw e;
        }
    }

    private boolean equalsArray(byte[] ar1, byte[] ar2, int size) {
        for (int k = 0; k < size; ++k) {
            if (ar1[k] == ar2[k]) continue;
            return false;
        }
        return true;
    }

    private void readDecryptedDocObj() throws IOException {
        PdfObject filter;
        int lengthValue;
        int cryptoMode;
        byte[] oValue;
        byte[] uValue;
        byte[] documentID;
        byte[] encryptionKey;
        PdfObject encDic;
        block52: {
            PdfObject o;
            PdfDictionary enc;
            block51: {
                String s;
                if (this.encrypted) {
                    return;
                }
                if (this.trailer == null) {
                    return;
                }
                encDic = this.trailer.get(PdfName.ENCRYPT);
                if (encDic == null || encDic.toString().equals("null")) {
                    return;
                }
                this.encryptionError = true;
                encryptionKey = null;
                this.encrypted = true;
                enc = (PdfDictionary)PdfReader.getPdfObject(encDic);
                PdfArray documentIDs = this.trailer.getAsArray(PdfName.ID);
                documentID = null;
                if (documentIDs != null) {
                    o = documentIDs.getPdfObject(0);
                    this.strings.remove(o);
                    s = o.toString();
                    documentID = DocWriter.getISOBytes(s);
                    if (documentIDs.size() > 1) {
                        this.strings.remove(documentIDs.getPdfObject(1));
                    }
                }
                if (documentID == null) {
                    documentID = new byte[]{};
                }
                uValue = null;
                oValue = null;
                cryptoMode = 0;
                lengthValue = 0;
                filter = PdfReader.getPdfObjectRelease(enc.get(PdfName.FILTER));
                if (!filter.equals(PdfName.STANDARD)) break block51;
                s = enc.get(PdfName.U).toString();
                this.strings.remove(enc.get(PdfName.U));
                uValue = DocWriter.getISOBytes(s);
                s = enc.get(PdfName.O).toString();
                this.strings.remove(enc.get(PdfName.O));
                oValue = DocWriter.getISOBytes(s);
                o = enc.get(PdfName.P);
                if (!o.isNumber()) {
                    throw new InvalidPdfException(MessageLocalization.getComposedMessage("illegal.p.value"));
                }
                this.pValue = ((PdfNumber)o).intValue();
                o = enc.get(PdfName.R);
                if (!o.isNumber()) {
                    throw new InvalidPdfException(MessageLocalization.getComposedMessage("illegal.r.value"));
                }
                this.rValue = ((PdfNumber)o).intValue();
                switch (this.rValue) {
                    case 2: {
                        cryptoMode = 0;
                        break block52;
                    }
                    case 3: {
                        o = enc.get(PdfName.LENGTH);
                        if (!o.isNumber()) {
                            throw new InvalidPdfException(MessageLocalization.getComposedMessage("illegal.length.value"));
                        }
                        lengthValue = ((PdfNumber)o).intValue();
                        if (lengthValue > 128 || lengthValue < 40 || lengthValue % 8 != 0) {
                            throw new InvalidPdfException(MessageLocalization.getComposedMessage("illegal.length.value"));
                        }
                        cryptoMode = 1;
                        break block52;
                    }
                    case 4: {
                        PdfDictionary dic = (PdfDictionary)enc.get(PdfName.CF);
                        if (dic == null) {
                            throw new InvalidPdfException(MessageLocalization.getComposedMessage("cf.not.found.encryption"));
                        }
                        if ((dic = (PdfDictionary)dic.get(PdfName.STDCF)) == null) {
                            throw new InvalidPdfException(MessageLocalization.getComposedMessage("stdcf.not.found.encryption"));
                        }
                        if (PdfName.V2.equals(dic.get(PdfName.CFM))) {
                            cryptoMode = 1;
                        } else if (PdfName.AESV2.equals(dic.get(PdfName.CFM))) {
                            cryptoMode = 2;
                        } else {
                            throw new UnsupportedPdfException(MessageLocalization.getComposedMessage("no.compatible.encryption.found"));
                        }
                        PdfObject em = enc.get(PdfName.ENCRYPTMETADATA);
                        if (em != null && em.toString().equals("false")) {
                            cryptoMode |= 8;
                        }
                        break block52;
                    }
                    default: {
                        throw new UnsupportedPdfException(MessageLocalization.getComposedMessage("unknown.encryption.type.r.eq.1", this.rValue));
                    }
                }
            }
            if (filter.equals(PdfName.PUBSEC)) {
                PdfArray recipients;
                o = enc.get(PdfName.V);
                if (!o.isNumber()) {
                    throw new InvalidPdfException(MessageLocalization.getComposedMessage("illegal.v.value"));
                }
                int vValue = ((PdfNumber)o).intValue();
                switch (vValue) {
                    case 1: {
                        cryptoMode = 0;
                        lengthValue = 40;
                        recipients = (PdfArray)enc.get(PdfName.RECIPIENTS);
                        break;
                    }
                    case 2: {
                        o = enc.get(PdfName.LENGTH);
                        if (!o.isNumber()) {
                            throw new InvalidPdfException(MessageLocalization.getComposedMessage("illegal.length.value"));
                        }
                        lengthValue = ((PdfNumber)o).intValue();
                        if (lengthValue > 128 || lengthValue < 40 || lengthValue % 8 != 0) {
                            throw new InvalidPdfException(MessageLocalization.getComposedMessage("illegal.length.value"));
                        }
                        cryptoMode = 1;
                        recipients = (PdfArray)enc.get(PdfName.RECIPIENTS);
                        break;
                    }
                    case 4: {
                        PdfDictionary dic = (PdfDictionary)enc.get(PdfName.CF);
                        if (dic == null) {
                            throw new InvalidPdfException(MessageLocalization.getComposedMessage("cf.not.found.encryption"));
                        }
                        if ((dic = (PdfDictionary)dic.get(PdfName.DEFAULTCRYPTFILTER)) == null) {
                            throw new InvalidPdfException(MessageLocalization.getComposedMessage("defaultcryptfilter.not.found.encryption"));
                        }
                        if (PdfName.V2.equals(dic.get(PdfName.CFM))) {
                            cryptoMode = 1;
                            lengthValue = 128;
                        } else if (PdfName.AESV2.equals(dic.get(PdfName.CFM))) {
                            cryptoMode = 2;
                            lengthValue = 128;
                        } else {
                            throw new UnsupportedPdfException(MessageLocalization.getComposedMessage("no.compatible.encryption.found"));
                        }
                        PdfObject em = dic.get(PdfName.ENCRYPTMETADATA);
                        if (em != null && em.toString().equals("false")) {
                            cryptoMode |= 8;
                        }
                        recipients = (PdfArray)dic.get(PdfName.RECIPIENTS);
                        break;
                    }
                    default: {
                        throw new UnsupportedPdfException(MessageLocalization.getComposedMessage("unknown.encryption.type.v.eq.1", this.rValue));
                    }
                }
                BouncyCastleHelper.checkCertificateEncodingOrThrowException(this.certificate);
                byte[] envelopedData = BouncyCastleHelper.getEnvelopedData(recipients, this.strings, this.certificate, this.certificateKey, this.certificateKeyProvider);
                if (envelopedData == null) {
                    throw new UnsupportedPdfException(MessageLocalization.getComposedMessage("bad.certificate.and.key"));
                }
                try {
                    MessageDigest md = MessageDigest.getInstance("SHA-1");
                    md.update(envelopedData, 0, 20);
                    for (int i = 0; i < recipients.size(); ++i) {
                        byte[] encodedRecipient = recipients.getPdfObject(i).getBytes();
                        md.update(encodedRecipient);
                    }
                    if ((cryptoMode & 8) != 0) {
                        md.update(new byte[]{-1, -1, -1, -1});
                    }
                    encryptionKey = md.digest();
                }
                catch (Exception f) {
                    throw new ExceptionConverter(f);
                }
            }
        }
        this.decrypt = new PdfEncryption();
        this.decrypt.setCryptoMode(cryptoMode, lengthValue);
        if (filter.equals(PdfName.STANDARD)) {
            this.decrypt.setupByOwnerPassword(documentID, this.password, uValue, oValue, this.pValue);
            if (!this.equalsArray(uValue, this.decrypt.userKey, this.rValue == 3 || this.rValue == 4 ? 16 : 32)) {
                this.decrypt.setupByUserPassword(documentID, this.password, oValue, this.pValue);
                if (!this.equalsArray(uValue, this.decrypt.userKey, this.rValue == 3 || this.rValue == 4 ? 16 : 32)) {
                    throw new BadPasswordException(MessageLocalization.getComposedMessage("bad.user.password"));
                }
            } else {
                this.ownerPasswordUsed = true;
            }
        } else if (filter.equals(PdfName.PUBSEC)) {
            this.decrypt.setupByEncryptionKey(encryptionKey, lengthValue);
            this.ownerPasswordUsed = true;
        }
        for (PdfObject string : this.strings) {
            PdfString str = (PdfString)string;
            str.decrypt(this);
        }
        if (encDic.isIndirect()) {
            this.cryptoRef = (PRIndirectReference)encDic;
            this.xrefObj.set(this.cryptoRef.getNumber(), null);
        }
        this.encryptionError = false;
    }

    public static PdfObject getPdfObjectRelease(PdfObject obj) {
        PdfObject obj2 = PdfReader.getPdfObject(obj);
        PdfReader.releaseLastXrefPartial(obj);
        return obj2;
    }

    public static PdfObject getPdfObject(PdfObject obj) {
        if (obj == null) {
            return null;
        }
        if (!obj.isIndirect()) {
            return obj;
        }
        try {
            PRIndirectReference ref = (PRIndirectReference)obj;
            int idx = ref.getNumber();
            boolean appendable = ref.getReader().appendable;
            obj = ref.getReader().getPdfObject(idx);
            if (obj == null) {
                return null;
            }
            if (appendable) {
                switch (obj.type()) {
                    case 8: {
                        obj = new PdfNull();
                        break;
                    }
                    case 1: {
                        obj = new PdfBoolean(((PdfBoolean)obj).booleanValue());
                        break;
                    }
                    case 4: {
                        obj = new PdfName(obj.getBytes());
                    }
                }
                obj.setIndRef(ref);
            }
            return obj;
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    public static PdfObject getPdfObjectRelease(PdfObject obj, PdfObject parent) {
        PdfObject obj2 = PdfReader.getPdfObject(obj, parent);
        PdfReader.releaseLastXrefPartial(obj);
        return obj2;
    }

    public static PdfObject getPdfObject(PdfObject obj, PdfObject parent) {
        if (obj == null) {
            return null;
        }
        if (!obj.isIndirect()) {
            PRIndirectReference ref;
            if (parent != null && (ref = parent.getIndRef()) != null && ref.getReader().isAppendable()) {
                switch (obj.type()) {
                    case 8: {
                        obj = new PdfNull();
                        break;
                    }
                    case 1: {
                        obj = new PdfBoolean(((PdfBoolean)obj).booleanValue());
                        break;
                    }
                    case 4: {
                        obj = new PdfName(obj.getBytes());
                    }
                }
                obj.setIndRef(ref);
            }
            return obj;
        }
        return PdfReader.getPdfObject(obj);
    }

    public PdfObject getPdfObjectRelease(int idx) {
        PdfObject obj = this.getPdfObject(idx);
        this.releaseLastXrefPartial();
        return obj;
    }

    public PdfObject getPdfObject(int idx) {
        try {
            this.lastXrefPartial = -1;
            if (idx < 0 || idx >= this.xrefObj.size()) {
                return null;
            }
            PdfObject obj = this.xrefObj.get(idx);
            if (!this.partial || obj != null) {
                return obj;
            }
            if (idx * 2 >= this.xref.length) {
                return null;
            }
            obj = this.readSingleObject(idx);
            this.lastXrefPartial = -1;
            if (obj != null) {
                this.lastXrefPartial = idx;
            }
            return obj;
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    public void resetLastXrefPartial() {
        this.lastXrefPartial = -1;
    }

    public void releaseLastXrefPartial() {
        if (this.partial && this.lastXrefPartial != -1) {
            this.xrefObj.set(this.lastXrefPartial, null);
            this.lastXrefPartial = -1;
        }
    }

    public static void releaseLastXrefPartial(PdfObject obj) {
        if (obj == null) {
            return;
        }
        if (!obj.isIndirect()) {
            return;
        }
        if (!(obj instanceof PRIndirectReference)) {
            return;
        }
        PRIndirectReference ref = (PRIndirectReference)obj;
        PdfReader reader = ref.getReader();
        if (reader.partial && reader.lastXrefPartial != -1 && reader.lastXrefPartial == ref.getNumber()) {
            reader.xrefObj.set(reader.lastXrefPartial, null);
        }
        reader.lastXrefPartial = -1;
    }

    private void setXrefPartialObject(int idx, PdfObject obj) {
        if (!this.partial || idx < 0) {
            return;
        }
        this.xrefObj.set(idx, obj);
    }

    public PRIndirectReference addPdfObject(PdfObject obj) {
        this.xrefObj.add(obj);
        return new PRIndirectReference(this, this.xrefObj.size() - 1);
    }

    protected void readPages() throws IOException {
        this.catalog = this.trailer.getAsDict(PdfName.ROOT);
        this.rootPages = this.catalog.getAsDict(PdfName.PAGES);
        this.pageRefs = new PageRefs(this);
    }

    protected void readDocObjPartial() throws IOException {
        this.xrefObj = new ArrayList<PdfObject>(this.xref.length / 2);
        this.xrefObj.addAll(Collections.nCopies(this.xref.length / 2, null));
        this.readDecryptedDocObj();
        if (this.objStmToOffset != null) {
            int[] keys;
            for (int n : keys = this.objStmToOffset.getKeys()) {
                this.objStmToOffset.put(n, this.xref[n * 2]);
                this.xref[n * 2] = -1;
            }
        }
    }

    protected PdfObject readSingleObject(int k) throws IOException {
        PdfObject obj;
        this.strings.clear();
        int k2 = k * 2;
        int pos = this.xref[k2];
        if (pos < 0) {
            return null;
        }
        if (this.xref[k2 + 1] > 0) {
            pos = this.objStmToOffset.get(this.xref[k2 + 1]);
        }
        if (pos == 0) {
            return null;
        }
        this.tokens.seek(pos);
        this.tokens.nextValidToken();
        if (this.tokens.getTokenType() != 1) {
            this.tokens.throwError(MessageLocalization.getComposedMessage("invalid.object.number"));
        }
        this.objNum = this.tokens.intValue();
        this.tokens.nextValidToken();
        if (this.tokens.getTokenType() != 1) {
            this.tokens.throwError(MessageLocalization.getComposedMessage("invalid.generation.number"));
        }
        this.objGen = this.tokens.intValue();
        this.tokens.nextValidToken();
        if (!this.tokens.getStringValue().equals("obj")) {
            this.tokens.throwError(MessageLocalization.getComposedMessage("token.obj.expected"));
        }
        try {
            obj = this.readPRObject();
            for (PdfObject string : this.strings) {
                PdfString str = (PdfString)string;
                str.decrypt(this);
            }
            if (obj.isStream()) {
                this.checkPRStreamLength((PRStream)obj);
            }
        }
        catch (Exception e) {
            obj = null;
        }
        if (this.xref[k2 + 1] > 0) {
            obj = this.readOneObjStm((PRStream)obj, this.xref[k2]);
        }
        this.xrefObj.set(k, obj);
        return obj;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected PdfObject readOneObjStm(PRStream stream, int idx) throws IOException {
        int first = stream.getAsNumber(PdfName.FIRST).intValue();
        byte[] b = PdfReader.getStreamBytes(stream, this.tokens.getFile());
        PRTokeniser saveTokens = this.tokens;
        this.tokens = new PRTokeniser(b);
        try {
            int address = 0;
            boolean ok = true;
            ++idx;
            for (int k = 0; k < idx && (ok = this.tokens.nextToken()); ++k) {
                if (this.tokens.getTokenType() != 1) {
                    ok = false;
                    break;
                }
                ok = this.tokens.nextToken();
                if (!ok) break;
                if (this.tokens.getTokenType() != 1) {
                    ok = false;
                    break;
                }
                address = this.tokens.intValue() + first;
            }
            if (!ok) {
                throw new InvalidPdfException(MessageLocalization.getComposedMessage("error.reading.objstm"));
            }
            this.tokens.seek(address);
            PdfObject pdfObject = this.readPRObject();
            return pdfObject;
        }
        finally {
            this.tokens = saveTokens;
        }
    }

    public double dumpPerc() {
        int total = 0;
        for (PdfObject aXrefObj : this.xrefObj) {
            if (aXrefObj == null) continue;
            ++total;
        }
        return (double)total * 100.0 / (double)this.xrefObj.size();
    }

    protected void readDocObj() throws IOException {
        ArrayList<PdfObject> streams = new ArrayList<PdfObject>();
        this.xrefObj = new ArrayList<PdfObject>(this.xref.length / 2);
        this.xrefObj.addAll(Collections.nCopies(this.xref.length / 2, null));
        for (int k = 2; k < this.xref.length; k += 2) {
            PdfObject obj;
            int n = this.xref[k];
            if (n <= 0 || this.xref.length > k + 1 && this.xref[k + 1] > 0) continue;
            this.tokens.seek(n);
            this.tokens.nextValidToken();
            if (this.tokens.getTokenType() != 1) {
                this.tokens.throwError(MessageLocalization.getComposedMessage("invalid.object.number"));
            }
            this.objNum = this.tokens.intValue();
            this.tokens.nextValidToken();
            if (this.tokens.getTokenType() != 1) {
                this.tokens.throwError(MessageLocalization.getComposedMessage("invalid.generation.number"));
            }
            this.objGen = this.tokens.intValue();
            this.tokens.nextValidToken();
            if (!this.tokens.getStringValue().equals("obj")) {
                this.tokens.throwError(MessageLocalization.getComposedMessage("token.obj.expected"));
            }
            try {
                obj = this.readPRObject();
                if (obj.isStream()) {
                    streams.add(obj);
                }
            }
            catch (Exception e) {
                obj = null;
            }
            this.xrefObj.set(k / 2, obj);
        }
        for (PdfObject pdfObject : streams) {
            this.checkPRStreamLength((PRStream)pdfObject);
        }
        this.readDecryptedDocObj();
        if (this.objStmMark != null) {
            for (Object object : this.objStmMark.entrySet()) {
                Map.Entry entry = (Map.Entry)object;
                int n = (Integer)entry.getKey();
                IntHashtable h = (IntHashtable)entry.getValue();
                this.readObjStm((PRStream)this.xrefObj.get(n), h);
                this.xrefObj.set(n, null);
            }
            this.objStmMark = null;
        }
        this.xref = null;
    }

    private void checkPRStreamLength(PRStream stream) throws IOException {
        int streamLength;
        block8: {
            int fileLength = this.tokens.length();
            int start = stream.getOffset();
            boolean calc = false;
            streamLength = 0;
            PdfObject obj = PdfReader.getPdfObjectRelease(stream.get(PdfName.LENGTH));
            if (obj != null && obj.type() == 2) {
                streamLength = ((PdfNumber)obj).intValue();
                if (streamLength + start > fileLength - 20) {
                    calc = true;
                } else {
                    this.tokens.seek(start + streamLength);
                    String line = this.tokens.readString(20);
                    if (!(line.startsWith("\nendstream") || line.startsWith("\r\nendstream") || line.startsWith("\rendstream") || line.startsWith("endstream"))) {
                        calc = true;
                    }
                }
            } else {
                calc = true;
            }
            if (calc) {
                int pos;
                byte[] tline = new byte[16];
                this.tokens.seek(start);
                do {
                    pos = this.tokens.getFilePointer();
                    if (!this.tokens.readLineSegment(tline)) break block8;
                    if (!PdfReader.equalsn(tline, endstream)) continue;
                    streamLength = pos - start;
                    break block8;
                } while (!PdfReader.equalsn(tline, endobj));
                this.tokens.seek(pos - 16);
                String s = this.tokens.readString(16);
                int index = s.indexOf("endstream");
                if (index >= 0) {
                    pos = pos - 16 + index;
                }
                streamLength = pos - start;
            }
        }
        stream.setLength(streamLength);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void readObjStm(PRStream stream, IntHashtable map) throws IOException {
        int first = stream.getAsNumber(PdfName.FIRST).intValue();
        int n = stream.getAsNumber(PdfName.N).intValue();
        byte[] b = PdfReader.getStreamBytes(stream, this.tokens.getFile());
        PRTokeniser saveTokens = this.tokens;
        this.tokens = new PRTokeniser(b);
        try {
            int k;
            int[] address = new int[n];
            int[] objNumber = new int[n];
            boolean ok = true;
            for (k = 0; k < n && (ok = this.tokens.nextToken()); ++k) {
                if (this.tokens.getTokenType() != 1) {
                    ok = false;
                    break;
                }
                objNumber[k] = this.tokens.intValue();
                ok = this.tokens.nextToken();
                if (!ok) break;
                if (this.tokens.getTokenType() != 1) {
                    ok = false;
                    break;
                }
                address[k] = this.tokens.intValue() + first;
            }
            if (!ok) {
                throw new InvalidPdfException(MessageLocalization.getComposedMessage("error.reading.objstm"));
            }
            for (k = 0; k < n; ++k) {
                if (!map.containsKey(k)) continue;
                this.tokens.seek(address[k]);
                PdfObject obj = this.readPRObject();
                this.xrefObj.set(objNumber[k], obj);
            }
        }
        finally {
            this.tokens = saveTokens;
        }
    }

    public static PdfObject killIndirect(PdfObject obj) {
        if (obj == null || obj.isNull()) {
            return null;
        }
        PdfObject ret = PdfReader.getPdfObjectRelease(obj);
        if (obj.isIndirect()) {
            PRIndirectReference ref = (PRIndirectReference)obj;
            PdfReader reader = ref.getReader();
            int n = ref.getNumber();
            reader.xrefObj.set(n, null);
            if (reader.partial) {
                reader.xref[n * 2] = -1;
            }
        }
        return ret;
    }

    private void ensureXrefSize(int size) {
        if (size == 0) {
            return;
        }
        if (this.xref == null) {
            this.xref = new int[size];
        } else if (this.xref.length < size) {
            int[] xref2 = new int[size];
            System.arraycopy(this.xref, 0, xref2, 0, this.xref.length);
            this.xref = xref2;
        }
    }

    protected void readXref() throws IOException {
        PdfNumber prev;
        int startxref;
        this.hybridXref = false;
        this.newXrefType = false;
        this.tokens.seek(this.tokens.getStartxref());
        this.tokens.nextToken();
        if (!this.tokens.getStringValue().equals("startxref")) {
            throw new InvalidPdfException(MessageLocalization.getComposedMessage("startxref.not.found"));
        }
        this.tokens.nextToken();
        if (this.tokens.getTokenType() != 1) {
            throw new InvalidPdfException(MessageLocalization.getComposedMessage("startxref.is.not.followed.by.a.number"));
        }
        this.lastXref = startxref = this.tokens.intValue();
        this.eofPos = this.tokens.getFilePointer();
        try {
            if (this.readXRefStream(startxref)) {
                this.newXrefType = true;
                return;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.xref = null;
        this.tokens.seek(startxref);
        PdfDictionary trailer2 = this.trailer = this.readXrefSection();
        while ((prev = (PdfNumber)trailer2.get(PdfName.PREV)) != null) {
            this.tokens.seek(prev.intValue());
            trailer2 = this.readXrefSection();
        }
    }

    protected PdfDictionary readXrefSection() throws IOException {
        this.tokens.nextValidToken();
        if (!this.tokens.getStringValue().equals("xref")) {
            this.tokens.throwError(MessageLocalization.getComposedMessage("xref.subsection.not.found"));
        }
        block2: while (true) {
            int pos;
            this.tokens.nextValidToken();
            if (this.tokens.getStringValue().equals("trailer")) break;
            if (this.tokens.getTokenType() != 1) {
                this.tokens.throwError(MessageLocalization.getComposedMessage("object.number.of.the.first.object.in.this.xref.subsection.not.found"));
            }
            int start = this.tokens.intValue();
            this.tokens.nextValidToken();
            if (this.tokens.getTokenType() != 1) {
                this.tokens.throwError(MessageLocalization.getComposedMessage("number.of.entries.in.this.xref.subsection.not.found"));
            }
            int end = this.tokens.intValue() + start;
            if (start == 1) {
                int back = this.tokens.getFilePointer();
                this.tokens.nextValidToken();
                pos = this.tokens.intValue();
                this.tokens.nextValidToken();
                int gen = this.tokens.intValue();
                if (pos == 0 && gen == 65535) {
                    --end;
                }
                this.tokens.seek(back);
            }
            this.ensureXrefSize(end * 2);
            int k = --start;
            while (true) {
                if (k >= end) continue block2;
                this.tokens.nextValidToken();
                pos = this.tokens.intValue();
                this.tokens.nextValidToken();
                this.tokens.nextValidToken();
                int p = k * 2;
                if (this.tokens.getStringValue().equals("n")) {
                    if (this.xref[p] == 0 && this.xref[p + 1] == 0) {
                        this.xref[p] = pos;
                    }
                } else if (this.tokens.getStringValue().equals("f")) {
                    if (this.xref[p] == 0 && this.xref[p + 1] == 0) {
                        this.xref[p] = -1;
                    }
                } else {
                    this.tokens.throwError(MessageLocalization.getComposedMessage("invalid.cross.reference.entry.in.this.xref.subsection"));
                }
                ++k;
            }
            break;
        }
        PdfDictionary trailer = (PdfDictionary)this.readPRObject();
        PdfNumber xrefSize = (PdfNumber)trailer.get(PdfName.SIZE);
        this.ensureXrefSize(xrefSize.intValue() * 2);
        PdfObject xrs = trailer.get(PdfName.XREFSTM);
        if (xrs != null && xrs.isNumber()) {
            int loc = ((PdfNumber)xrs).intValue();
            try {
                this.readXRefStream(loc);
                this.newXrefType = true;
                this.hybridXref = true;
            }
            catch (IOException e) {
                this.xref = null;
                throw e;
            }
        }
        return trailer;
    }

    protected boolean readXRefStream(int ptr) throws IOException {
        PdfArray index;
        PRStream stm;
        this.tokens.seek(ptr);
        if (!this.tokens.nextToken()) {
            return false;
        }
        if (this.tokens.getTokenType() != 1) {
            return false;
        }
        int thisStream = this.tokens.intValue();
        if (!this.tokens.nextToken() || this.tokens.getTokenType() != 1) {
            return false;
        }
        if (!this.tokens.nextToken() || !this.tokens.getStringValue().equals("obj")) {
            return false;
        }
        PdfObject object = this.readPRObject();
        if (object.isStream()) {
            stm = (PRStream)object;
            if (!PdfName.XREF.equals(stm.get(PdfName.TYPE))) {
                return false;
            }
        } else {
            return false;
        }
        if (this.trailer == null) {
            this.trailer = new PdfDictionary();
            this.trailer.putAll(stm);
        }
        stm.setLength(((PdfNumber)stm.get(PdfName.LENGTH)).intValue());
        int size = ((PdfNumber)stm.get(PdfName.SIZE)).intValue();
        PdfObject obj = stm.get(PdfName.INDEX);
        if (obj == null) {
            index = new PdfArray();
            index.add(new int[]{0, size});
        } else {
            index = (PdfArray)obj;
        }
        PdfArray w = (PdfArray)stm.get(PdfName.W);
        int prev = -1;
        obj = stm.get(PdfName.PREV);
        if (obj != null) {
            prev = ((PdfNumber)obj).intValue();
        }
        this.ensureXrefSize(size * 2);
        if (this.objStmMark == null && !this.partial) {
            this.objStmMark = new HashMap<Integer, IntHashtable>();
        }
        if (this.objStmToOffset == null && this.partial) {
            this.objStmToOffset = new IntHashtable();
        }
        byte[] b = PdfReader.getStreamBytes(stm, this.tokens.getFile());
        int bptr = 0;
        int[] wc = new int[3];
        for (int k = 0; k < 3; ++k) {
            wc[k] = w.getAsNumber(k).intValue();
        }
        for (int idx = 0; idx < index.size(); idx += 2) {
            int start = index.getAsNumber(idx).intValue();
            int length = index.getAsNumber(idx + 1).intValue();
            this.ensureXrefSize((start + length) * 2);
            while (length-- > 0) {
                int type = 1;
                if (wc[0] > 0) {
                    type = 0;
                    for (int k = 0; k < wc[0]; ++k) {
                        type = (type << 8) + (b[bptr++] & 0xFF);
                    }
                }
                int field2 = 0;
                for (int k = 0; k < wc[1]; ++k) {
                    field2 = (field2 << 8) + (b[bptr++] & 0xFF);
                }
                int field3 = 0;
                for (int k = 0; k < wc[2]; ++k) {
                    field3 = (field3 << 8) + (b[bptr++] & 0xFF);
                }
                int base = start * 2;
                if (this.xref[base] == 0 && this.xref[base + 1] == 0) {
                    switch (type) {
                        case 0: {
                            this.xref[base] = -1;
                            break;
                        }
                        case 1: {
                            this.xref[base] = field2;
                            break;
                        }
                        case 2: {
                            this.xref[base] = field3;
                            this.xref[base + 1] = field2;
                            if (this.partial) {
                                this.objStmToOffset.put(field2, 0);
                                break;
                            }
                            Integer on = field2;
                            IntHashtable seq = this.objStmMark.get(on);
                            if (seq == null) {
                                seq = new IntHashtable();
                                seq.put(field3, 1);
                                this.objStmMark.put(on, seq);
                                break;
                            }
                            seq.put(field3, 1);
                        }
                    }
                }
                ++start;
            }
        }
        if ((thisStream *= 2) < this.xref.length) {
            this.xref[thisStream] = -1;
        }
        if (prev == -1) {
            return true;
        }
        return this.readXRefStream(prev);
    }

    protected void rebuildXref() throws IOException {
        int[] obj;
        this.hybridXref = false;
        this.newXrefType = false;
        this.tokens.seek(0);
        int[][] xr = new int[1024][];
        int top = 0;
        this.trailer = null;
        byte[] line = new byte[64];
        while (true) {
            int pos = this.tokens.getFilePointer();
            if (!this.tokens.readLineSegment(line)) break;
            if (line[0] == 116) {
                if (!PdfEncodings.convertToString(line, null).startsWith("trailer")) continue;
                this.tokens.seek(pos);
                this.tokens.nextToken();
                pos = this.tokens.getFilePointer();
                try {
                    PdfDictionary dic = (PdfDictionary)this.readPRObject();
                    if (dic.get(PdfName.ROOT) != null) {
                        this.trailer = dic;
                        continue;
                    }
                    this.tokens.seek(pos);
                }
                catch (Exception e) {
                    this.tokens.seek(pos);
                }
                continue;
            }
            if (line[0] < 48 || line[0] > 57 || (obj = PRTokeniser.checkObjectStart(line)) == null) continue;
            int num = obj[0];
            int gen = obj[1];
            if (num >= xr.length) {
                int newLength = num * 2;
                int[][] xr2 = new int[newLength][];
                System.arraycopy(xr, 0, xr2, 0, top);
                xr = xr2;
            }
            if (num >= top) {
                top = num + 1;
            }
            if (xr[num] != null && gen < xr[num][1]) continue;
            obj[0] = pos;
            xr[num] = obj;
        }
        this.xref = new int[top * 2];
        for (int k = 0; k < top; ++k) {
            obj = xr[k];
            if (obj == null) continue;
            this.xref[k * 2] = obj[0];
        }
    }

    protected PdfDictionary readDictionary() throws IOException {
        PdfDictionary dic = new PdfDictionary();
        while (true) {
            this.tokens.nextValidToken();
            if (this.tokens.getTokenType() == 8) break;
            if (this.tokens.getTokenType() != 3) {
                this.tokens.throwError(MessageLocalization.getComposedMessage("dictionary.key.is.not.a.name"));
            }
            PdfName name = new PdfName(this.tokens.getStringValue(), false);
            PdfObject obj = this.readPRObject();
            int type = obj.type();
            if (-type == 8) {
                this.tokens.throwError(MessageLocalization.getComposedMessage("unexpected.gt.gt"));
            }
            if (-type == 6) {
                this.tokens.throwError(MessageLocalization.getComposedMessage("unexpected.close.bracket"));
            }
            dic.put(name, obj);
        }
        return dic;
    }

    protected PdfArray readArray() throws IOException {
        PdfObject obj;
        int type;
        PdfArray array = new PdfArray();
        while (-(type = (obj = this.readPRObject()).type()) != 6) {
            if (-type == 8) {
                this.tokens.throwError(MessageLocalization.getComposedMessage("unexpected.gt.gt"));
            }
            array.add(obj);
        }
        return array;
    }

    protected PdfObject readPRObject() throws IOException {
        this.tokens.nextValidToken();
        int type = this.tokens.getTokenType();
        switch (type) {
            case 7: {
                boolean hasNext;
                ++this.readDepth;
                PdfDictionary dic = this.readDictionary();
                --this.readDepth;
                int pos = this.tokens.getFilePointer();
                while ((hasNext = this.tokens.nextToken()) && this.tokens.getTokenType() == 4) {
                }
                if (hasNext && this.tokens.getStringValue().equals("stream")) {
                    int ch;
                    while ((ch = this.tokens.read()) == 32 || ch == 9 || ch == 0 || ch == 12) {
                    }
                    if (ch != 10) {
                        ch = this.tokens.read();
                    }
                    if (ch != 10) {
                        this.tokens.backOnePosition(ch);
                    }
                    PRStream stream = new PRStream(this, this.tokens.getFilePointer());
                    stream.putAll(dic);
                    stream.setObjNum(this.objNum, this.objGen);
                    return stream;
                }
                this.tokens.seek(pos);
                return dic;
            }
            case 5: {
                ++this.readDepth;
                PdfArray arr = this.readArray();
                --this.readDepth;
                return arr;
            }
            case 1: {
                return new PdfNumber(this.tokens.getStringValue());
            }
            case 2: {
                PdfString str = new PdfString(this.tokens.getStringValue(), null).setHexWriting(this.tokens.isHexString());
                str.setObjNum(this.objNum, this.objGen);
                if (this.strings != null) {
                    this.strings.add(str);
                }
                return str;
            }
            case 3: {
                PdfName cachedName = PdfName.staticNames.get(this.tokens.getStringValue());
                if (this.readDepth > 0 && cachedName != null) {
                    return cachedName;
                }
                return new PdfName(this.tokens.getStringValue(), false);
            }
            case 9: {
                int num = this.tokens.getReference();
                PRIndirectReference ref = new PRIndirectReference(this, num, this.tokens.getGeneration());
                return ref;
            }
            case 11: {
                throw new IOException(MessageLocalization.getComposedMessage("unexpected.end.of.file"));
            }
        }
        String sv = this.tokens.getStringValue();
        if ("null".equals(sv)) {
            if (this.readDepth == 0) {
                return new PdfNull();
            }
            return PdfNull.PDFNULL;
        }
        if ("true".equals(sv)) {
            if (this.readDepth == 0) {
                return new PdfBoolean(true);
            }
            return PdfBoolean.PDFTRUE;
        }
        if ("false".equals(sv)) {
            if (this.readDepth == 0) {
                return new PdfBoolean(false);
            }
            return PdfBoolean.PDFFALSE;
        }
        return new PdfLiteral(-type, this.tokens.getStringValue());
    }

    public static byte[] FlateDecode(byte[] in) {
        byte[] b = PdfReader.FlateDecode(in, true);
        if (b == null) {
            return PdfReader.FlateDecode(in, false);
        }
        return b;
    }

    public static byte[] decodePredictor(byte[] in, PdfObject dicPar) {
        if (dicPar == null || !dicPar.isDictionary()) {
            return in;
        }
        PdfDictionary dic = (PdfDictionary)dicPar;
        PdfObject obj = PdfReader.getPdfObject(dic.get(PdfName.PREDICTOR));
        if (obj == null || !obj.isNumber()) {
            return in;
        }
        int predictor = ((PdfNumber)obj).intValue();
        if (predictor < 10) {
            return in;
        }
        int width = 1;
        obj = PdfReader.getPdfObject(dic.get(PdfName.COLUMNS));
        if (obj != null && obj.isNumber()) {
            width = ((PdfNumber)obj).intValue();
        }
        int colors = 1;
        obj = PdfReader.getPdfObject(dic.get(PdfName.COLORS));
        if (obj != null && obj.isNumber()) {
            colors = ((PdfNumber)obj).intValue();
        }
        int bpc = 8;
        obj = PdfReader.getPdfObject(dic.get(PdfName.BITSPERCOMPONENT));
        if (obj != null && obj.isNumber()) {
            bpc = ((PdfNumber)obj).intValue();
        }
        DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(in));
        ByteArrayOutputStream fout = new ByteArrayOutputStream(in.length);
        int bytesPerPixel = colors * bpc / 8;
        int bytesPerRow = (colors * width * bpc + 7) / 8;
        byte[] curr = new byte[bytesPerRow];
        byte[] prior = new byte[bytesPerRow];
        while (true) {
            int filter;
            try {
                filter = dataStream.read();
                if (filter < 0) {
                    return fout.toByteArray();
                }
                dataStream.readFully(curr, 0, bytesPerRow);
            }
            catch (Exception e) {
                return fout.toByteArray();
            }
            switch (filter) {
                case 0: {
                    break;
                }
                case 1: {
                    for (int i = bytesPerPixel; i < bytesPerRow; ++i) {
                        int n = i;
                        curr[n] = (byte)(curr[n] + curr[i - bytesPerPixel]);
                    }
                    break;
                }
                case 2: {
                    for (int i = 0; i < bytesPerRow; ++i) {
                        int n = i;
                        curr[n] = (byte)(curr[n] + prior[i]);
                    }
                    break;
                }
                case 3: {
                    int i;
                    for (i = 0; i < bytesPerPixel; ++i) {
                        int n = i;
                        curr[n] = (byte)(curr[n] + prior[i] / 2);
                    }
                    for (i = bytesPerPixel; i < bytesPerRow; ++i) {
                        int n = i;
                        curr[n] = (byte)(curr[n] + ((curr[i - bytesPerPixel] & 0xFF) + (prior[i] & 0xFF)) / 2);
                    }
                    break;
                }
                case 4: {
                    int i;
                    for (i = 0; i < bytesPerPixel; ++i) {
                        int n = i;
                        curr[n] = (byte)(curr[n] + prior[i]);
                    }
                    i = bytesPerPixel;
                    while (i < bytesPerRow) {
                        int a = curr[i - bytesPerPixel] & 0xFF;
                        int b = prior[i] & 0xFF;
                        int c = prior[i - bytesPerPixel] & 0xFF;
                        int p = a + b - c;
                        int pa = Math.abs(p - a);
                        int pb = Math.abs(p - b);
                        int pc = Math.abs(p - c);
                        int ret = pa <= pb && pa <= pc ? a : (pb <= pc ? b : c);
                        int n = i++;
                        curr[n] = (byte)(curr[n] + (byte)ret);
                    }
                    break;
                }
                default: {
                    throw new RuntimeException(MessageLocalization.getComposedMessage("png.filter.unknown"));
                }
            }
            try {
                fout.write(curr);
            }
            catch (IOException i) {
                // empty catch block
            }
            byte[] tmp = prior;
            prior = curr;
            curr = tmp;
        }
    }

    public static byte[] FlateDecode(byte[] in, boolean strict) {
        ByteArrayInputStream stream = new ByteArrayInputStream(in);
        InflaterInputStream zip = new InflaterInputStream(stream);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] b = new byte[strict ? 4092 : 1];
        try {
            int n;
            while ((n = zip.read(b)) >= 0) {
                out.write(b, 0, n);
            }
            zip.close();
            out.close();
            return out.toByteArray();
        }
        catch (Exception e) {
            if (strict) {
                return null;
            }
            return out.toByteArray();
        }
    }

    public static byte[] ASCIIHexDecode(byte[] in) {
        byte b;
        int ch;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        boolean first = true;
        int n1 = 0;
        byte[] byArray = in;
        int n = byArray.length;
        for (int i = 0; i < n && (ch = (b = byArray[i]) & 0xFF) != 62; ++i) {
            if (PRTokeniser.isWhitespace(ch)) continue;
            int n2 = PRTokeniser.getHex(ch);
            if (n2 == -1) {
                throw new RuntimeException(MessageLocalization.getComposedMessage("illegal.character.in.asciihexdecode"));
            }
            if (first) {
                n1 = n2;
            } else {
                out.write((byte)((n1 << 4) + n2));
            }
            first = !first;
        }
        if (!first) {
            out.write((byte)(n1 << 4));
        }
        return out.toByteArray();
    }

    public static byte[] ASCII85Decode(byte[] in) {
        byte b;
        int ch;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int state = 0;
        int[] chn = new int[5];
        byte[] byArray = in;
        int n = byArray.length;
        for (int i = 0; i < n && (ch = (b = byArray[i]) & 0xFF) != 126; ++i) {
            if (PRTokeniser.isWhitespace(ch)) continue;
            if (ch == 122 && state == 0) {
                out.write(0);
                out.write(0);
                out.write(0);
                out.write(0);
                continue;
            }
            if (ch < 33 || ch > 117) {
                throw new RuntimeException(MessageLocalization.getComposedMessage("illegal.character.in.ascii85decode"));
            }
            chn[state] = ch - 33;
            if (++state != 5) continue;
            state = 0;
            int r = 0;
            for (int j = 0; j < 5; ++j) {
                r = r * 85 + chn[j];
            }
            out.write((byte)(r >> 24));
            out.write((byte)(r >> 16));
            out.write((byte)(r >> 8));
            out.write((byte)r);
        }
        if (state == 2) {
            int r = chn[0] * 85 * 85 * 85 * 85 + chn[1] * 85 * 85 * 85 + 614125 + 7225 + 85;
            out.write((byte)(r >> 24));
        } else if (state == 3) {
            int r = chn[0] * 85 * 85 * 85 * 85 + chn[1] * 85 * 85 * 85 + chn[2] * 85 * 85 + 7225 + 85;
            out.write((byte)(r >> 24));
            out.write((byte)(r >> 16));
        } else if (state == 4) {
            int r = chn[0] * 85 * 85 * 85 * 85 + chn[1] * 85 * 85 * 85 + chn[2] * 85 * 85 + chn[3] * 85 + 85;
            out.write((byte)(r >> 24));
            out.write((byte)(r >> 16));
            out.write((byte)(r >> 8));
        }
        return out.toByteArray();
    }

    public static byte[] LZWDecode(byte[] in) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        LZWDecoder lzw = new LZWDecoder();
        lzw.decode(in, out);
        return out.toByteArray();
    }

    public boolean isRebuilt() {
        return this.rebuilt;
    }

    public PdfDictionary getPageN(int pageNum) {
        PdfDictionary dic = this.pageRefs.getPageN(pageNum);
        if (dic == null) {
            return null;
        }
        if (this.appendable) {
            dic.setIndRef(this.pageRefs.getPageOrigRef(pageNum));
        }
        return dic;
    }

    public PdfDictionary getPageNRelease(int pageNum) {
        PdfDictionary dic = this.getPageN(pageNum);
        this.pageRefs.releasePage(pageNum);
        return dic;
    }

    public void releasePage(int pageNum) {
        this.pageRefs.releasePage(pageNum);
    }

    public void resetReleasePage() {
        this.pageRefs.resetReleasePage();
    }

    public PRIndirectReference getPageOrigRef(int pageNum) {
        return this.pageRefs.getPageOrigRef(pageNum);
    }

    public byte[] getPageContent(int pageNum, RandomAccessFileOrArray file) throws IOException {
        PdfDictionary page = this.getPageNRelease(pageNum);
        if (page == null) {
            return null;
        }
        PdfObject contents = PdfReader.getPdfObjectRelease(page.get(PdfName.CONTENTS));
        if (contents == null) {
            return new byte[0];
        }
        if (contents.isStream()) {
            return PdfReader.getStreamBytes((PRStream)contents, file);
        }
        if (contents.isArray()) {
            PdfArray array = (PdfArray)contents;
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            for (int k = 0; k < array.size(); ++k) {
                PdfObject item = PdfReader.getPdfObjectRelease(array.getPdfObject(k));
                if (item == null || !item.isStream()) continue;
                byte[] b = PdfReader.getStreamBytes((PRStream)item, file);
                bout.write(b);
                if (k == array.size() - 1) continue;
                bout.write(10);
            }
            return bout.toByteArray();
        }
        return new byte[0];
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public byte[] getPageContent(int pageNum) throws IOException {
        RandomAccessFileOrArray rf = this.getSafeFile();
        try {
            rf.reOpen();
            byte[] byArray = this.getPageContent(pageNum, rf);
            return byArray;
        }
        finally {
            try {
                rf.close();
            }
            catch (Exception exception) {}
        }
    }

    protected void killXref(PdfObject obj) {
        if (obj == null) {
            return;
        }
        if (obj instanceof PdfIndirectReference && !obj.isIndirect()) {
            return;
        }
        switch (obj.type()) {
            case 10: {
                int xr = ((PRIndirectReference)obj).getNumber();
                obj = this.xrefObj.get(xr);
                this.xrefObj.set(xr, null);
                this.freeXref = xr;
                this.killXref(obj);
                break;
            }
            case 5: {
                PdfArray t = (PdfArray)obj;
                for (int i = 0; i < t.size(); ++i) {
                    this.killXref(t.getPdfObject(i));
                }
                break;
            }
            case 6: 
            case 7: {
                PdfDictionary dic = (PdfDictionary)obj;
                for (PdfName o : dic.getKeys()) {
                    this.killXref(dic.get(o));
                }
                break;
            }
        }
    }

    public void setPageContent(int pageNum, byte[] content) {
        this.setPageContent(pageNum, content, -1);
    }

    public void setPageContent(int pageNum, byte[] content, int compressionLevel) {
        PdfDictionary page = this.getPageN(pageNum);
        if (page == null) {
            return;
        }
        PdfObject contents = page.get(PdfName.CONTENTS);
        this.freeXref = -1;
        this.killXref(contents);
        if (this.freeXref == -1) {
            this.xrefObj.add(null);
            this.freeXref = this.xrefObj.size() - 1;
        }
        page.put(PdfName.CONTENTS, new PRIndirectReference(this, this.freeXref));
        this.xrefObj.set(this.freeXref, new PRStream(this, content, compressionLevel));
    }

    public static byte[] getStreamBytes(PRStream stream, RandomAccessFileOrArray file) throws IOException {
        PdfObject filter = PdfReader.getPdfObjectRelease(stream.get(PdfName.FILTER));
        byte[] b = PdfReader.getStreamBytesRaw(stream, file);
        List<PdfObject> filters = new ArrayList<PdfObject>();
        filters = PdfReader.addFilters(filters, filter);
        List<Object> dp = new ArrayList<PdfObject>();
        PdfObject dpo = PdfReader.getPdfObjectRelease(stream.get(PdfName.DECODEPARMS));
        if (dpo == null || !dpo.isDictionary() && !dpo.isArray()) {
            dpo = PdfReader.getPdfObjectRelease(stream.get(PdfName.DP));
        }
        if (dpo != null) {
            if (dpo.isDictionary()) {
                dp.add(dpo);
            } else if (dpo.isArray()) {
                dp = ((PdfArray)dpo).getElements();
            }
        }
        block17: for (int j = 0; j < filters.size(); ++j) {
            String name;
            switch (name = PdfReader.getPdfObjectRelease(filters.get(j)).toString()) {
                case "/FlateDecode": 
                case "/Fl": {
                    b = PdfReader.FlateDecode(b);
                    if (j >= dp.size()) continue block17;
                    PdfObject dicParam = (PdfObject)dp.get(j);
                    b = PdfReader.decodePredictor(b, dicParam);
                    continue block17;
                }
                case "/ASCIIHexDecode": 
                case "/AHx": {
                    b = PdfReader.ASCIIHexDecode(b);
                    continue block17;
                }
                case "/ASCII85Decode": 
                case "/A85": {
                    b = PdfReader.ASCII85Decode(b);
                    continue block17;
                }
                case "/LZWDecode": {
                    b = PdfReader.LZWDecode(b);
                    if (j >= dp.size()) continue block17;
                    PdfObject dicParam = (PdfObject)dp.get(j);
                    b = PdfReader.decodePredictor(b, dicParam);
                    continue block17;
                }
                case "/Crypt": {
                    continue block17;
                }
                default: {
                    throw new UnsupportedPdfException(MessageLocalization.getComposedMessage("the.filter.1.is.not.supported", name));
                }
            }
        }
        return b;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static byte[] getStreamBytes(PRStream stream) throws IOException {
        RandomAccessFileOrArray rf = stream.getReader().getSafeFile();
        try {
            rf.reOpen();
            byte[] byArray = PdfReader.getStreamBytes(stream, rf);
            return byArray;
        }
        finally {
            try {
                rf.close();
            }
            catch (Exception exception) {}
        }
    }

    public static byte[] getStreamBytesRaw(PRStream stream, RandomAccessFileOrArray file) throws IOException {
        byte[] b;
        PdfReader reader = stream.getReader();
        if (stream.getOffset() < 0) {
            b = stream.getBytes();
        } else {
            b = new byte[stream.getLength()];
            file.seek(stream.getOffset());
            file.readFully(b);
            PdfEncryption decrypt = reader.getDecrypt();
            if (decrypt != null) {
                PdfObject filter = PdfReader.getPdfObjectRelease(stream.get(PdfName.FILTER));
                List<PdfObject> filters = new ArrayList<PdfObject>();
                filters = PdfReader.addFilters(filters, filter);
                boolean skip = false;
                for (PdfObject filter1 : filters) {
                    PdfObject obj = PdfReader.getPdfObjectRelease(filter1);
                    if (obj == null || !obj.toString().equals("/Crypt")) continue;
                    skip = true;
                    break;
                }
                if (!skip) {
                    decrypt.setHashKey(stream.getObjNum(), stream.getObjGen());
                    b = decrypt.decryptByteArray(b);
                }
            }
        }
        return b;
    }

    private static List<PdfObject> addFilters(List<PdfObject> filters, PdfObject filter) {
        if (filter != null) {
            if (filter.isName()) {
                filters.add(filter);
            } else if (filter.isArray()) {
                filters = ((PdfArray)filter).getElements();
            }
        }
        return filters;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static byte[] getStreamBytesRaw(PRStream stream) throws IOException {
        RandomAccessFileOrArray rf = stream.getReader().getSafeFile();
        try {
            rf.reOpen();
            byte[] byArray = PdfReader.getStreamBytesRaw(stream, rf);
            return byArray;
        }
        finally {
            try {
                rf.close();
            }
            catch (Exception exception) {}
        }
    }

    public void eliminateSharedStreams() {
        int k;
        if (!this.sharedStreams) {
            return;
        }
        this.sharedStreams = false;
        if (this.pageRefs.size() == 1) {
            return;
        }
        ArrayList<PRIndirectReference> newRefs = new ArrayList<PRIndirectReference>();
        ArrayList<PRStream> newStreams = new ArrayList<PRStream>();
        IntHashtable visited = new IntHashtable();
        for (k = 1; k <= this.pageRefs.size(); ++k) {
            PdfObject contents;
            PdfDictionary page = this.pageRefs.getPageN(k);
            if (page == null || (contents = PdfReader.getPdfObject(page.get(PdfName.CONTENTS))) == null) continue;
            if (contents.isStream()) {
                PRIndirectReference ref = (PRIndirectReference)page.get(PdfName.CONTENTS);
                if (visited.containsKey(ref.getNumber())) {
                    newRefs.add(ref);
                    newStreams.add(new PRStream((PRStream)contents, null));
                    continue;
                }
                visited.put(ref.getNumber(), 1);
                continue;
            }
            if (!contents.isArray()) continue;
            PdfArray array = (PdfArray)contents;
            for (int j = 0; j < array.size(); ++j) {
                PRIndirectReference ref = (PRIndirectReference)array.getPdfObject(j);
                if (visited.containsKey(ref.getNumber())) {
                    newRefs.add(ref);
                    newStreams.add(new PRStream((PRStream)PdfReader.getPdfObject(ref), null));
                    continue;
                }
                visited.put(ref.getNumber(), 1);
            }
        }
        if (newStreams.isEmpty()) {
            return;
        }
        for (k = 0; k < newStreams.size(); ++k) {
            this.xrefObj.add((PdfObject)newStreams.get(k));
            PRIndirectReference ref = (PRIndirectReference)newRefs.get(k);
            ref.setNumber(this.xrefObj.size() - 1, 0);
        }
    }

    public boolean isTampered() {
        return this.tampered;
    }

    public void setTampered(boolean tampered) {
        this.tampered = tampered;
        this.pageRefs.keepPages();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public byte[] getMetadata() throws IOException {
        byte[] b;
        PdfObject obj = PdfReader.getPdfObject(this.catalog.get(PdfName.METADATA));
        if (!(obj instanceof PRStream)) {
            return null;
        }
        RandomAccessFileOrArray rf = this.getSafeFile();
        try {
            rf.reOpen();
            b = PdfReader.getStreamBytes((PRStream)obj, rf);
        }
        finally {
            try {
                rf.close();
            }
            catch (Exception exception) {}
        }
        return b;
    }

    public int getLastXref() {
        return this.lastXref;
    }

    public int getXrefSize() {
        return this.xrefObj.size();
    }

    public int getEofPos() {
        return this.eofPos;
    }

    public char getPdfVersion() {
        return this.pdfVersion;
    }

    public boolean isEncrypted() {
        return this.encrypted;
    }

    public int getPermissions() {
        return this.pValue;
    }

    public boolean is128Key() {
        return this.rValue == 3;
    }

    public PdfDictionary getTrailer() {
        return this.trailer;
    }

    PdfEncryption getDecrypt() {
        return this.decrypt;
    }

    private static boolean equalsn(byte[] a1, byte[] a2) {
        int length = a2.length;
        for (int k = 0; k < length; ++k) {
            if (a1[k] == a2[k]) continue;
            return false;
        }
        return true;
    }

    private static boolean existsName(PdfDictionary dic, PdfName key, PdfName value) {
        PdfObject type = PdfReader.getPdfObjectRelease(dic.get(key));
        if (type == null || !type.isName()) {
            return false;
        }
        PdfName name = (PdfName)type;
        return name.equals(value);
    }

    static String getFontNameFromDescriptor(PdfDictionary dic) {
        return PdfReader.getFontName(dic, PdfName.FONTNAME);
    }

    private static String getFontName(PdfDictionary dic) {
        return PdfReader.getFontName(dic, PdfName.BASEFONT);
    }

    private static String getFontName(PdfDictionary dic, PdfName property) {
        if (dic == null) {
            return null;
        }
        PdfObject type = PdfReader.getPdfObjectRelease(dic.get(property));
        if (type == null || !type.isName()) {
            return null;
        }
        return PdfName.decodeName(type.toString());
    }

    static boolean isFontSubset(String fontName) {
        return fontName != null && fontName.length() >= 8 && fontName.charAt(6) == '+';
    }

    private static String getSubsetPrefix(PdfDictionary dic) {
        if (dic == null) {
            return null;
        }
        String s = PdfReader.getFontName(dic);
        if (s == null) {
            return null;
        }
        if (s.length() < 8 || s.charAt(6) != '+') {
            return null;
        }
        for (int k = 0; k < 6; ++k) {
            char c = s.charAt(k);
            if (c >= 'A' && c <= 'Z') continue;
            return null;
        }
        return s;
    }

    public int shuffleSubsetNames() {
        int total = 0;
        for (int k = 1; k < this.xrefObj.size(); ++k) {
            PdfDictionary desc;
            String sde;
            String s;
            PdfDictionary dic;
            PdfObject obj = this.getPdfObjectRelease(k);
            if (obj == null || !obj.isDictionary() || !PdfReader.existsName(dic = (PdfDictionary)obj, PdfName.TYPE, PdfName.FONT)) continue;
            if (PdfReader.existsName(dic, PdfName.SUBTYPE, PdfName.TYPE1) || PdfReader.existsName(dic, PdfName.SUBTYPE, PdfName.MMTYPE1) || PdfReader.existsName(dic, PdfName.SUBTYPE, PdfName.TRUETYPE)) {
                s = PdfReader.getSubsetPrefix(dic);
                if (s == null) continue;
                String ns = BaseFont.createSubsetPrefix() + s.substring(7);
                PdfName newName = new PdfName(ns);
                dic.put(PdfName.BASEFONT, newName);
                this.setXrefPartialObject(k, dic);
                ++total;
                PdfDictionary fd = dic.getAsDict(PdfName.FONTDESCRIPTOR);
                if (fd == null) continue;
                fd.put(PdfName.FONTNAME, newName);
                continue;
            }
            if (!PdfReader.existsName(dic, PdfName.SUBTYPE, PdfName.TYPE0)) continue;
            s = PdfReader.getSubsetPrefix(dic);
            PdfArray arr = dic.getAsArray(PdfName.DESCENDANTFONTS);
            if (arr == null || arr.isEmpty() || (sde = PdfReader.getSubsetPrefix(desc = arr.getAsDict(0))) == null) continue;
            String ns = BaseFont.createSubsetPrefix();
            if (s != null) {
                dic.put(PdfName.BASEFONT, new PdfName(ns + s.substring(7)));
            }
            this.setXrefPartialObject(k, dic);
            PdfName newName = new PdfName(ns + sde.substring(7));
            desc.put(PdfName.BASEFONT, newName);
            ++total;
            PdfDictionary fd = desc.getAsDict(PdfName.FONTDESCRIPTOR);
            if (fd == null) continue;
            fd.put(PdfName.FONTNAME, newName);
        }
        return total;
    }

    public int createFakeFontSubsets() {
        int total = 0;
        for (int k = 1; k < this.xrefObj.size(); ++k) {
            String s;
            PdfDictionary dic;
            PdfObject obj = this.getPdfObjectRelease(k);
            if (obj == null || !obj.isDictionary() || !PdfReader.existsName(dic = (PdfDictionary)obj, PdfName.TYPE, PdfName.FONT) || !PdfReader.existsName(dic, PdfName.SUBTYPE, PdfName.TYPE1) && !PdfReader.existsName(dic, PdfName.SUBTYPE, PdfName.MMTYPE1) && !PdfReader.existsName(dic, PdfName.SUBTYPE, PdfName.TRUETYPE) || (s = PdfReader.getSubsetPrefix(dic)) != null || (s = PdfReader.getFontName(dic)) == null) continue;
            String ns = BaseFont.createSubsetPrefix() + s;
            PdfDictionary fd = (PdfDictionary)PdfReader.getPdfObjectRelease(dic.get(PdfName.FONTDESCRIPTOR));
            if (fd == null || fd.get(PdfName.FONTFILE) == null && fd.get(PdfName.FONTFILE2) == null && fd.get(PdfName.FONTFILE3) == null) continue;
            fd = dic.getAsDict(PdfName.FONTDESCRIPTOR);
            PdfName newName = new PdfName(ns);
            dic.put(PdfName.BASEFONT, newName);
            fd.put(PdfName.FONTNAME, newName);
            this.setXrefPartialObject(k, dic);
            ++total;
        }
        return total;
    }

    private static PdfArray getNameArray(PdfObject obj) {
        PdfObject arr2;
        if (obj == null) {
            return null;
        }
        if ((obj = PdfReader.getPdfObjectRelease(obj)) == null) {
            return null;
        }
        if (obj.isArray()) {
            return (PdfArray)obj;
        }
        if (obj.isDictionary() && (arr2 = PdfReader.getPdfObjectRelease(((PdfDictionary)obj).get(PdfName.D))) != null && arr2.isArray()) {
            return (PdfArray)arr2;
        }
        return null;
    }

    public HashMap<Object, PdfObject> getNamedDestination() {
        return this.getNamedDestination(false);
    }

    public HashMap<Object, PdfObject> getNamedDestination(boolean keepNames) {
        HashMap<Object, PdfObject> names = this.getNamedDestinationFromNames(keepNames);
        names.putAll(this.getNamedDestinationFromStrings());
        return names;
    }

    public HashMap getNamedDestinationFromNames() {
        return this.getNamedDestinationFromNames(false);
    }

    public HashMap<Object, PdfObject> getNamedDestinationFromNames(boolean keepNames) {
        HashMap<Object, PdfObject> names = new HashMap<Object, PdfObject>();
        if (this.catalog.get(PdfName.DESTS) != null) {
            PdfDictionary dic = (PdfDictionary)PdfReader.getPdfObjectRelease(this.catalog.get(PdfName.DESTS));
            if (dic == null) {
                return names;
            }
            Set<PdfName> keys = dic.getKeys();
            for (PdfName key1 : keys) {
                PdfName key = key1;
                PdfArray arr = PdfReader.getNameArray(dic.get(key));
                if (arr == null) continue;
                if (keepNames) {
                    names.put(key, arr);
                    continue;
                }
                String name = PdfName.decodeName(key.toString());
                names.put(name, arr);
            }
        }
        return names;
    }

    public HashMap getNamedDestinationFromStrings() {
        PdfDictionary dic;
        if (this.catalog.get(PdfName.NAMES) != null && (dic = (PdfDictionary)PdfReader.getPdfObjectRelease(this.catalog.get(PdfName.NAMES))) != null && (dic = (PdfDictionary)PdfReader.getPdfObjectRelease(dic.get(PdfName.DESTS))) != null) {
            HashMap<String, PdfObject> names = PdfNameTree.readTree(dic);
            Iterator<Map.Entry<String, PdfObject>> it = names.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, PdfObject> entry = it.next();
                PdfArray arr = PdfReader.getNameArray(entry.getValue());
                if (arr != null) {
                    entry.setValue(arr);
                    continue;
                }
                it.remove();
            }
            return names;
        }
        return new HashMap();
    }

    public void removeFields() {
        this.pageRefs.resetReleasePage();
        for (int k = 1; k <= this.pageRefs.size(); ++k) {
            PdfDictionary page = this.pageRefs.getPageN(k);
            PdfArray annots = page.getAsArray(PdfName.ANNOTS);
            if (annots == null) {
                this.pageRefs.releasePage(k);
                continue;
            }
            for (int j = 0; j < annots.size(); ++j) {
                PdfDictionary annot;
                PdfObject obj = PdfReader.getPdfObjectRelease(annots.getPdfObject(j));
                if (obj == null || !obj.isDictionary() || !PdfName.WIDGET.equals((annot = (PdfDictionary)obj).get(PdfName.SUBTYPE))) continue;
                annots.remove(j--);
            }
            if (annots.isEmpty()) {
                page.remove(PdfName.ANNOTS);
                continue;
            }
            this.pageRefs.releasePage(k);
        }
        this.catalog.remove(PdfName.ACROFORM);
        this.pageRefs.resetReleasePage();
    }

    public void removeAnnotations() {
        this.pageRefs.resetReleasePage();
        for (int k = 1; k <= this.pageRefs.size(); ++k) {
            PdfDictionary page = this.pageRefs.getPageN(k);
            if (page.get(PdfName.ANNOTS) == null) {
                this.pageRefs.releasePage(k);
                continue;
            }
            page.remove(PdfName.ANNOTS);
        }
        this.catalog.remove(PdfName.ACROFORM);
        this.pageRefs.resetReleasePage();
    }

    public ArrayList<PdfAnnotation.PdfImportedLink> getLinks(int page) {
        this.pageRefs.resetReleasePage();
        ArrayList<PdfAnnotation.PdfImportedLink> result = new ArrayList<PdfAnnotation.PdfImportedLink>();
        PdfDictionary pageDic = this.pageRefs.getPageN(page);
        if (pageDic.get(PdfName.ANNOTS) != null) {
            PdfArray annots = pageDic.getAsArray(PdfName.ANNOTS);
            for (int j = 0; j < annots.size(); ++j) {
                PdfDictionary annot = (PdfDictionary)PdfReader.getPdfObjectRelease(annots.getPdfObject(j));
                if (!PdfName.LINK.equals(annot.get(PdfName.SUBTYPE))) continue;
                result.add(new PdfAnnotation.PdfImportedLink(annot));
            }
        }
        this.pageRefs.releasePage(page);
        this.pageRefs.resetReleasePage();
        return result;
    }

    private void iterateBookmarks(PdfObject outlineRef, Map<Object, PdfObject> names) {
        while (outlineRef != null) {
            this.replaceNamedDestination(outlineRef, names);
            PdfDictionary outline = (PdfDictionary)PdfReader.getPdfObjectRelease(outlineRef);
            PdfObject first = outline.get(PdfName.FIRST);
            if (first != null) {
                this.iterateBookmarks(first, names);
            }
            outlineRef = outline.get(PdfName.NEXT);
        }
    }

    public void makeRemoteNamedDestinationsLocal() {
        if (this.remoteToLocalNamedDestinations) {
            return;
        }
        this.remoteToLocalNamedDestinations = true;
        HashMap<Object, PdfObject> names = this.getNamedDestination(true);
        if (names.isEmpty()) {
            return;
        }
        for (int k = 1; k <= this.pageRefs.size(); ++k) {
            PdfDictionary page = this.pageRefs.getPageN(k);
            PdfObject annotsRef = page.get(PdfName.ANNOTS);
            PdfArray annots = (PdfArray)PdfReader.getPdfObject(annotsRef);
            int annotIdx = this.lastXrefPartial;
            this.releaseLastXrefPartial();
            if (annots == null) {
                this.pageRefs.releasePage(k);
                continue;
            }
            boolean commitAnnots = false;
            for (int an = 0; an < annots.size(); ++an) {
                PdfObject objRef = annots.getPdfObject(an);
                if (!this.convertNamedDestination(objRef, names) || objRef.isIndirect()) continue;
                commitAnnots = true;
            }
            if (commitAnnots) {
                this.setXrefPartialObject(annotIdx, annots);
            }
            if (commitAnnots && !annotsRef.isIndirect()) continue;
            this.pageRefs.releasePage(k);
        }
    }

    private boolean convertNamedDestination(PdfObject obj, Map<Object, PdfObject> names) {
        PdfObject ob2;
        obj = PdfReader.getPdfObject(obj);
        int objIdx = this.lastXrefPartial;
        this.releaseLastXrefPartial();
        if (obj != null && obj.isDictionary() && (ob2 = PdfReader.getPdfObject(((PdfDictionary)obj).get(PdfName.A))) != null) {
            int obj2Idx = this.lastXrefPartial;
            this.releaseLastXrefPartial();
            PdfDictionary dic = (PdfDictionary)ob2;
            PdfName type = (PdfName)PdfReader.getPdfObjectRelease(dic.get(PdfName.S));
            if (PdfName.GOTOR.equals(type)) {
                PdfObject ob3 = PdfReader.getPdfObjectRelease(dic.get(PdfName.D));
                Object name = null;
                if (ob3 != null) {
                    if (ob3.isName()) {
                        name = ob3;
                    } else if (ob3.isString()) {
                        name = ob3.toString();
                    }
                    PdfArray dest = (PdfArray)names.get(name);
                    if (dest != null) {
                        dic.remove(PdfName.F);
                        dic.remove(PdfName.NEWWINDOW);
                        dic.put(PdfName.S, PdfName.GOTO);
                        this.setXrefPartialObject(obj2Idx, ob2);
                        this.setXrefPartialObject(objIdx, obj);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void consolidateNamedDestinations() {
        if (this.consolidateNamedDestinations) {
            return;
        }
        this.consolidateNamedDestinations = true;
        HashMap<Object, PdfObject> names = this.getNamedDestination(true);
        if (names.isEmpty()) {
            return;
        }
        for (int k = 1; k <= this.pageRefs.size(); ++k) {
            PdfDictionary page = this.pageRefs.getPageN(k);
            PdfObject annotsRef = page.get(PdfName.ANNOTS);
            PdfArray annots = (PdfArray)PdfReader.getPdfObject(annotsRef);
            int annotIdx = this.lastXrefPartial;
            this.releaseLastXrefPartial();
            if (annots == null) {
                this.pageRefs.releasePage(k);
                continue;
            }
            boolean commitAnnots = false;
            for (int an = 0; an < annots.size(); ++an) {
                PdfObject objRef = annots.getPdfObject(an);
                if (!this.replaceNamedDestination(objRef, names) || objRef.isIndirect()) continue;
                commitAnnots = true;
            }
            if (commitAnnots) {
                this.setXrefPartialObject(annotIdx, annots);
            }
            if (commitAnnots && !annotsRef.isIndirect()) continue;
            this.pageRefs.releasePage(k);
        }
        PdfDictionary outlines = (PdfDictionary)PdfReader.getPdfObjectRelease(this.catalog.get(PdfName.OUTLINES));
        if (outlines == null) {
            return;
        }
        this.iterateBookmarks(outlines.get(PdfName.FIRST), names);
    }

    private boolean replaceNamedDestination(PdfObject obj, Map<Object, PdfObject> names) {
        obj = PdfReader.getPdfObject(obj);
        int objIdx = this.lastXrefPartial;
        this.releaseLastXrefPartial();
        if (obj != null && obj.isDictionary()) {
            PdfObject ob2 = PdfReader.getPdfObjectRelease(((PdfDictionary)obj).get(PdfName.DEST));
            Object name = null;
            if (ob2 != null) {
                if (ob2.isName()) {
                    name = ob2;
                } else if (ob2.isString()) {
                    name = ob2.toString();
                }
                PdfArray dest = (PdfArray)names.get(name);
                if (dest != null) {
                    ((PdfDictionary)obj).put(PdfName.DEST, dest);
                    this.setXrefPartialObject(objIdx, obj);
                    return true;
                }
            } else {
                ob2 = PdfReader.getPdfObject(((PdfDictionary)obj).get(PdfName.A));
                if (ob2 != null) {
                    int obj2Idx = this.lastXrefPartial;
                    this.releaseLastXrefPartial();
                    PdfDictionary dic = (PdfDictionary)ob2;
                    PdfName type = (PdfName)PdfReader.getPdfObjectRelease(dic.get(PdfName.S));
                    if (PdfName.GOTO.equals(type)) {
                        PdfArray dest;
                        PdfObject ob3 = PdfReader.getPdfObjectRelease(dic.get(PdfName.D));
                        if (ob3 != null) {
                            if (ob3.isName()) {
                                name = ob3;
                            } else if (ob3.isString()) {
                                name = ob3.toString();
                            }
                        }
                        if ((dest = (PdfArray)names.get(name)) != null) {
                            dic.put(PdfName.D, dest);
                            this.setXrefPartialObject(obj2Idx, ob2);
                            this.setXrefPartialObject(objIdx, obj);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    protected static PdfDictionary duplicatePdfDictionary(PdfDictionary original, PdfDictionary copy, PdfReader newReader) {
        if (copy == null) {
            copy = new PdfDictionary();
        }
        Iterator<PdfName> iterator = original.getKeys().iterator();
        while (iterator.hasNext()) {
            PdfName o;
            PdfName key = o = iterator.next();
            copy.put(key, PdfReader.duplicatePdfObject(original.get(key), newReader));
        }
        return copy;
    }

    protected static PdfObject duplicatePdfObject(PdfObject original, PdfReader newReader) {
        if (original == null) {
            return null;
        }
        switch (original.type()) {
            case 6: {
                return PdfReader.duplicatePdfDictionary((PdfDictionary)original, null, newReader);
            }
            case 7: {
                PRStream org = (PRStream)original;
                PRStream stream = new PRStream(org, null, newReader);
                PdfReader.duplicatePdfDictionary(org, stream, newReader);
                return stream;
            }
            case 5: {
                PdfArray arr = new PdfArray();
                ((PdfArray)original).getElements().forEach(pdfObject -> arr.add(PdfReader.duplicatePdfObject(pdfObject, newReader)));
                return arr;
            }
            case 10: {
                PRIndirectReference org = (PRIndirectReference)original;
                return new PRIndirectReference(newReader, org.getNumber(), org.getGeneration());
            }
        }
        return original;
    }

    @Override
    public void close() {
        if (!this.partial) {
            return;
        }
        try {
            this.tokens.close();
        }
        catch (IOException e) {
            throw new ExceptionConverter(e);
        }
    }

    protected void removeUnusedNode(PdfObject obj, boolean[] hits) {
        Stack<Object> state = new Stack<Object>();
        state.push(obj);
        block5: while (!state.empty()) {
            int k;
            Object[] objs;
            PdfName[] keys;
            PdfDictionary dic;
            ArrayList ar;
            block18: {
                int idx;
                Object current;
                block17: {
                    current = state.pop();
                    if (current == null) continue;
                    ar = null;
                    dic = null;
                    keys = null;
                    objs = null;
                    idx = 0;
                    if (!(current instanceof PdfObject)) break block17;
                    obj = (PdfObject)current;
                    switch (obj.type()) {
                        case 6: 
                        case 7: {
                            dic = (PdfDictionary)obj;
                            keys = new PdfName[dic.size()];
                            dic.getKeys().toArray(keys);
                            break block18;
                        }
                        case 5: {
                            ar = ((PdfArray)obj).getElements();
                            break block18;
                        }
                        case 10: {
                            PRIndirectReference ref = (PRIndirectReference)obj;
                            int num = ref.getNumber();
                            if (hits[num]) continue block5;
                            hits[num] = true;
                            state.push(PdfReader.getPdfObjectRelease(ref));
                            break;
                        }
                    }
                    continue;
                }
                objs = (Object[])current;
                if (objs[0] instanceof ArrayList) {
                    ar = (ArrayList)objs[0];
                    idx = (Integer)objs[1];
                } else {
                    keys = (PdfName[])objs[0];
                    dic = (PdfDictionary)objs[1];
                    idx = (Integer)objs[2];
                }
            }
            if (ar != null) {
                for (k = idx; k < ar.size(); ++k) {
                    int num;
                    PdfObject v = (PdfObject)ar.get(k);
                    if (!v.isIndirect() || (num = ((PRIndirectReference)v).getNumber()) < this.xrefObj.size() && (this.partial || this.xrefObj.get(num) != null)) {
                        if (objs == null) {
                            state.push(new Object[]{ar, k + 1});
                        } else {
                            objs[1] = k + 1;
                            state.push(objs);
                        }
                        state.push(v);
                        continue block5;
                    }
                    ar.set(k, PdfNull.PDFNULL);
                }
                continue;
            }
            for (k = idx; k < keys.length; ++k) {
                int num;
                PdfName key = keys[k];
                PdfObject v = dic.get(key);
                if (!v.isIndirect() || (num = ((PRIndirectReference)v).getNumber()) < this.xrefObj.size() && (this.partial || this.xrefObj.get(num) != null)) {
                    if (objs == null) {
                        state.push(new Object[]{keys, dic, k + 1});
                    } else {
                        objs[2] = k + 1;
                        state.push(objs);
                    }
                    state.push(v);
                    continue block5;
                }
                dic.put(key, PdfNull.PDFNULL);
            }
        }
    }

    public int removeUnusedObjects() {
        boolean[] hits = new boolean[this.xrefObj.size()];
        this.removeUnusedNode(this.trailer, hits);
        int total = 0;
        if (this.partial) {
            for (int k = 1; k < hits.length; ++k) {
                if (hits[k]) continue;
                this.xref[k * 2] = -1;
                this.xref[k * 2 + 1] = 0;
                this.xrefObj.set(k, null);
                ++total;
            }
        } else {
            for (int k = 1; k < hits.length; ++k) {
                if (hits[k]) continue;
                this.xrefObj.set(k, null);
                ++total;
            }
        }
        return total;
    }

    public AcroFields getAcroFields() {
        return new AcroFields(this, null);
    }

    public String getJavaScript(RandomAccessFileOrArray file) throws IOException {
        PdfDictionary names = (PdfDictionary)PdfReader.getPdfObjectRelease(this.catalog.get(PdfName.NAMES));
        if (names == null) {
            return null;
        }
        PdfDictionary js = (PdfDictionary)PdfReader.getPdfObjectRelease(names.get(PdfName.JAVASCRIPT));
        if (js == null) {
            return null;
        }
        HashMap<String, PdfObject> jscript = PdfNameTree.readTree(js);
        Object[] sortedNames = new String[jscript.size()];
        sortedNames = jscript.keySet().toArray(sortedNames);
        Arrays.sort(sortedNames);
        StringBuilder buf = new StringBuilder();
        for (Object sortedName : sortedNames) {
            PdfObject obj;
            PdfDictionary j = (PdfDictionary)PdfReader.getPdfObjectRelease((PdfObject)jscript.get(sortedName));
            if (j == null || (obj = PdfReader.getPdfObjectRelease(j.get(PdfName.JS))) == null) continue;
            if (obj.isString()) {
                buf.append(((PdfString)obj).toUnicodeString()).append('\n');
                continue;
            }
            if (!obj.isStream()) continue;
            byte[] bytes = PdfReader.getStreamBytes((PRStream)obj, file);
            if (bytes.length >= 2 && bytes[0] == -2 && bytes[1] == -1) {
                buf.append(PdfEncodings.convertToString(bytes, "UnicodeBig"));
            } else {
                buf.append(PdfEncodings.convertToString(bytes, "PDF"));
            }
            buf.append('\n');
        }
        return buf.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String getJavaScript() throws IOException {
        RandomAccessFileOrArray rf = this.getSafeFile();
        try {
            rf.reOpen();
            String string = this.getJavaScript(rf);
            return string;
        }
        finally {
            try {
                rf.close();
            }
            catch (Exception exception) {}
        }
    }

    public void selectPages(String ranges) {
        this.selectPages(SequenceList.expand(ranges, this.getNumberOfPages()));
    }

    public void selectPages(List<Integer> pagesToKeep) {
        this.pageRefs.selectPages(pagesToKeep);
        this.removeUnusedObjects();
    }

    @Override
    public void setViewerPreferences(int preferences) {
        this.viewerPreferences.setViewerPreferences(preferences);
        this.setViewerPreferences(this.viewerPreferences);
    }

    @Override
    public void addViewerPreference(PdfName key, PdfObject value) {
        this.viewerPreferences.addViewerPreference(key, value);
        this.setViewerPreferences(this.viewerPreferences);
    }

    void setViewerPreferences(PdfViewerPreferencesImp vp) {
        vp.addToCatalog(this.catalog);
    }

    public int getSimpleViewerPreferences() {
        return PdfViewerPreferencesImp.getViewerPreferences(this.catalog).getPageLayoutAndMode();
    }

    public boolean isAppendable() {
        return this.appendable;
    }

    public void setAppendable(boolean appendable) {
        this.appendable = appendable;
        if (appendable) {
            PdfReader.getPdfObject(this.trailer.get(PdfName.ROOT));
        }
    }

    public boolean isNewXrefType() {
        return this.newXrefType;
    }

    public int getFileLength() {
        return this.fileLength;
    }

    public boolean isHybridXref() {
        return this.hybridXref;
    }

    PdfIndirectReference getCryptoRef() {
        if (this.cryptoRef == null) {
            return null;
        }
        return new PdfIndirectReference(0, this.cryptoRef.getNumber(), this.cryptoRef.getGeneration());
    }

    public void removeUsageRights() {
        PdfDictionary perms = this.catalog.getAsDict(PdfName.PERMS);
        if (perms == null) {
            return;
        }
        perms.remove(PdfName.UR);
        perms.remove(PdfName.UR3);
        if (perms.size() == 0) {
            this.catalog.remove(PdfName.PERMS);
        }
    }

    public int getCertificationLevel() {
        PdfDictionary dic = this.catalog.getAsDict(PdfName.PERMS);
        if (dic == null) {
            return 0;
        }
        if ((dic = dic.getAsDict(PdfName.DOCMDP)) == null) {
            return 0;
        }
        PdfArray arr = dic.getAsArray(PdfName.REFERENCE);
        if (arr == null || arr.size() == 0) {
            return 0;
        }
        dic = arr.getAsDict(0);
        if (dic == null) {
            return 0;
        }
        if ((dic = dic.getAsDict(PdfName.TRANSFORMPARAMS)) == null) {
            return 0;
        }
        PdfNumber p = dic.getAsNumber(PdfName.P);
        if (p == null) {
            return 0;
        }
        return p.intValue();
    }

    public boolean isModificationlowedWithoutOwnerPassword() {
        return this.modificationAllowedWithoutOwnerPassword;
    }

    public void setModificationAllowedWithoutOwnerPassword(boolean modificationAllowedWithoutOwnerPassword) {
        this.modificationAllowedWithoutOwnerPassword = modificationAllowedWithoutOwnerPassword;
    }

    public final boolean isOpenedWithFullPermissions() {
        return !this.encrypted || this.ownerPasswordUsed || this.modificationAllowedWithoutOwnerPassword;
    }

    public int getCryptoMode() {
        if (this.decrypt == null) {
            return -1;
        }
        return this.decrypt.getCryptoMode();
    }

    public boolean isMetadataEncrypted() {
        if (this.decrypt == null) {
            return false;
        }
        return this.decrypt.isMetadataEncrypted();
    }

    public byte[] computeUserPassword() {
        if (!this.encrypted || !this.ownerPasswordUsed) {
            return null;
        }
        return this.decrypt.computeUserPassword(this.password);
    }

    static class PageRefs {
        private final PdfReader reader;
        private List<PdfObject> refsn;
        private int sizep;
        private IntHashtable refsp;
        private int lastPageRead = -1;
        private List<PdfDictionary> pageInh;
        private boolean keepPages;

        private PageRefs(PdfReader reader) {
            this.reader = reader;
            if (reader.partial) {
                this.refsp = new IntHashtable();
                PdfNumber npages = (PdfNumber)PdfReader.getPdfObjectRelease(reader.rootPages.get(PdfName.COUNT));
                this.sizep = npages.intValue();
            } else {
                this.readPages();
            }
        }

        PageRefs(PageRefs other, PdfReader reader) {
            this.reader = reader;
            this.sizep = other.sizep;
            if (other.refsn != null) {
                this.refsn = new ArrayList<PdfObject>(other.refsn);
                for (int k = 0; k < this.refsn.size(); ++k) {
                    this.refsn.set(k, PdfReader.duplicatePdfObject(this.refsn.get(k), reader));
                }
            } else {
                this.refsp = (IntHashtable)other.refsp.clone();
            }
        }

        int size() {
            if (this.refsn != null) {
                return this.refsn.size();
            }
            return this.sizep;
        }

        void readPages() {
            if (this.refsn != null) {
                return;
            }
            this.refsp = null;
            this.refsn = new ArrayList<PdfObject>();
            this.pageInh = new ArrayList<PdfDictionary>();
            this.iteratePages((PRIndirectReference)this.reader.catalog.get(PdfName.PAGES));
            this.pageInh = null;
            this.reader.rootPages.put(PdfName.COUNT, new PdfNumber(this.refsn.size()));
        }

        void reReadPages() {
            this.refsn = null;
            this.readPages();
        }

        public PdfDictionary getPageN(int pageNum) {
            PRIndirectReference ref = this.getPageOrigRef(pageNum);
            return (PdfDictionary)PdfReader.getPdfObject(ref);
        }

        public PdfDictionary getPageNRelease(int pageNum) {
            PdfDictionary page = this.getPageN(pageNum);
            this.releasePage(pageNum);
            return page;
        }

        public PRIndirectReference getPageOrigRefRelease(int pageNum) {
            PRIndirectReference ref = this.getPageOrigRef(pageNum);
            this.releasePage(pageNum);
            return ref;
        }

        public PRIndirectReference getPageOrigRef(int pageNum) {
            try {
                if (--pageNum < 0 || pageNum >= this.size()) {
                    return null;
                }
                if (this.refsn != null) {
                    return (PRIndirectReference)this.refsn.get(pageNum);
                }
                int n = this.refsp.get(pageNum);
                if (n == 0) {
                    PRIndirectReference ref = this.getSinglePage(pageNum);
                    this.lastPageRead = this.reader.lastXrefPartial == -1 ? -1 : pageNum;
                    this.reader.lastXrefPartial = -1;
                    this.refsp.put(pageNum, ref.getNumber());
                    if (this.keepPages) {
                        this.lastPageRead = -1;
                    }
                    return ref;
                }
                if (this.lastPageRead != pageNum) {
                    this.lastPageRead = -1;
                }
                if (this.keepPages) {
                    this.lastPageRead = -1;
                }
                return new PRIndirectReference(this.reader, n);
            }
            catch (Exception e) {
                throw new ExceptionConverter(e);
            }
        }

        void keepPages() {
            if (this.refsp == null || this.keepPages) {
                return;
            }
            this.keepPages = true;
            this.refsp.clear();
        }

        public void releasePage(int pageNum) {
            if (this.refsp == null) {
                return;
            }
            if (--pageNum < 0 || pageNum >= this.size()) {
                return;
            }
            if (pageNum != this.lastPageRead) {
                return;
            }
            this.lastPageRead = -1;
            this.reader.lastXrefPartial = this.refsp.get(pageNum);
            this.reader.releaseLastXrefPartial();
            this.refsp.remove(pageNum);
        }

        public void resetReleasePage() {
            if (this.refsp == null) {
                return;
            }
            this.lastPageRead = -1;
        }

        void insertPage(int pageNum, PRIndirectReference ref) {
            --pageNum;
            if (this.refsn != null) {
                if (pageNum >= this.refsn.size()) {
                    this.refsn.add(ref);
                } else {
                    this.refsn.add(pageNum, ref);
                }
            } else {
                ++this.sizep;
                this.lastPageRead = -1;
                if (pageNum >= this.size()) {
                    this.refsp.put(this.size(), ref.getNumber());
                } else {
                    IntHashtable refs2 = new IntHashtable((this.refsp.size() + 1) * 2);
                    Iterator it = this.refsp.getEntryIterator();
                    while (it.hasNext()) {
                        IntHashtable.Entry entry = (IntHashtable.Entry)it.next();
                        int p = entry.getKey();
                        refs2.put(p >= pageNum ? p + 1 : p, entry.getValue());
                    }
                    refs2.put(pageNum, ref.getNumber());
                    this.refsp = refs2;
                }
            }
        }

        private void pushPageAttributes(PdfDictionary nodePages) {
            PdfDictionary dic = new PdfDictionary();
            if (!this.pageInh.isEmpty()) {
                dic.putAll(this.pageInh.get(this.pageInh.size() - 1));
            }
            for (PdfName pageInhCandidate : pageInhCandidates) {
                PdfObject obj = nodePages.get(pageInhCandidate);
                if (obj == null) continue;
                dic.put(pageInhCandidate, obj);
            }
            this.pageInh.add(dic);
        }

        private void popPageAttributes() {
            this.pageInh.remove(this.pageInh.size() - 1);
        }

        private void iteratePages(PRIndirectReference rpage) {
            PdfDictionary page = (PdfDictionary)PdfReader.getPdfObject(rpage);
            PdfArray kidsPR = page.getAsArray(PdfName.KIDS);
            if (kidsPR == null) {
                page.put(PdfName.TYPE, PdfName.PAGE);
                PdfDictionary dic = this.pageInh.get(this.pageInh.size() - 1);
                for (PdfName o : dic.getKeys()) {
                    PdfName key = o;
                    if (page.get(key) != null) continue;
                    page.put(key, dic.get(key));
                }
                if (page.get(PdfName.MEDIABOX) == null) {
                    PdfArray arr = new PdfArray(new float[]{0.0f, 0.0f, PageSize.LETTER.getRight(), PageSize.LETTER.getTop()});
                    page.put(PdfName.MEDIABOX, arr);
                }
                this.refsn.add(rpage);
            } else {
                page.put(PdfName.TYPE, PdfName.PAGES);
                this.pushPageAttributes(page);
                for (int k = 0; k < kidsPR.size(); ++k) {
                    PdfObject obj = kidsPR.getPdfObject(k);
                    if (!obj.isIndirect()) {
                        while (k < kidsPR.size()) {
                            kidsPR.remove(k);
                        }
                        break;
                    }
                    this.iteratePages((PRIndirectReference)obj);
                }
                this.popPageAttributes();
            }
        }

        /*
         * Unable to fully structure code
         */
        protected PRIndirectReference getSinglePage(int n) {
            acc = new PdfDictionary();
            top = this.reader.rootPages;
            base = 0;
            while (true) lbl-1000:
            // 5 sources

            {
                for (PdfName pageInhCandidate : PdfReader.pageInhCandidates) {
                    obj = top.get(pageInhCandidate);
                    if (obj == null) continue;
                    acc.put(pageInhCandidate, obj);
                }
                kids = (PdfArray)PdfReader.getPdfObjectRelease(top.get(PdfName.KIDS));
                var6_6 = kids.getElements().iterator();
                while (true) {
                    if (!var6_6.hasNext()) ** continue;
                    pdfObject = var6_6.next();
                    ref = (PRIndirectReference)pdfObject;
                    dic = (PdfDictionary)PdfReader.getPdfObject(ref);
                    last = PdfReader.access$300(this.reader);
                    count = PdfReader.getPdfObjectRelease(dic.get(PdfName.COUNT));
                    PdfReader.access$302(this.reader, last);
                    acn = 1;
                    if (count != null && count.type() == 2) {
                        acn = ((PdfNumber)count).intValue();
                    }
                    if (n < base + acn) {
                        if (count == null) {
                            dic.mergeDifferent(acc);
                            return ref;
                        }
                        this.reader.releaseLastXrefPartial();
                        top = dic;
                        ** continue;
                    }
                    this.reader.releaseLastXrefPartial();
                    base += acn;
                }
                break;
            }
        }

        private void selectPages(List<Integer> pagesToKeep) {
            PRIndirectReference pref;
            IntHashtable pg = new IntHashtable();
            ArrayList<Integer> finalPages = new ArrayList<Integer>();
            int psize = this.size();
            for (Integer aPagesToKeep : pagesToKeep) {
                if (aPagesToKeep < 1 || aPagesToKeep > psize || pg.put(aPagesToKeep, 1) != 0) continue;
                finalPages.add(aPagesToKeep);
            }
            if (this.reader.partial) {
                for (int k = 1; k <= psize; ++k) {
                    this.getPageOrigRef(k);
                    this.resetReleasePage();
                }
            }
            PRIndirectReference parent = (PRIndirectReference)this.reader.catalog.get(PdfName.PAGES);
            PdfDictionary topPages = (PdfDictionary)PdfReader.getPdfObject(parent);
            ArrayList<PdfObject> newPageRefs = new ArrayList<PdfObject>(finalPages.size());
            PdfArray kids = new PdfArray();
            for (Object e : finalPages) {
                int p = (Integer)e;
                pref = this.getPageOrigRef(p);
                this.resetReleasePage();
                kids.add(pref);
                newPageRefs.add(pref);
                this.getPageN(p).put(PdfName.PARENT, parent);
            }
            AcroFields af = this.reader.getAcroFields();
            boolean bl = af.getAllFields().size() > 0;
            for (int k = 1; k <= psize; ++k) {
                if (pg.containsKey(k)) continue;
                if (bl) {
                    af.removeFieldsFromPage(k);
                }
                pref = this.getPageOrigRef(k);
                int nref = pref.getNumber();
                this.reader.xrefObj.set(nref, null);
                if (!this.reader.partial) continue;
                this.reader.xref[nref * 2] = -1;
                this.reader.xref[nref * 2 + 1] = 0;
            }
            topPages.put(PdfName.COUNT, new PdfNumber(finalPages.size()));
            topPages.put(PdfName.KIDS, kids);
            this.refsp = null;
            this.refsn = newPageRefs;
        }
    }
}

