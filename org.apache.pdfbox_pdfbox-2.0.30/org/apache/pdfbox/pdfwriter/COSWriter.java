/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdfwriter;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSBoolean;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNull;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSObjectKey;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.cos.COSUpdateInfo;
import org.apache.pdfbox.cos.ICOSVisitor;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.io.RandomAccessInputStream;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.pdfparser.PDFXRefStream;
import org.apache.pdfbox.pdfwriter.COSStandardOutputStream;
import org.apache.pdfbox.pdfwriter.COSWriterXRefEntry;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.SecurityHandler;
import org.apache.pdfbox.pdmodel.fdf.FDFDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.COSFilterInputStream;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureInterface;
import org.apache.pdfbox.util.Charsets;
import org.apache.pdfbox.util.Hex;

public class COSWriter
implements ICOSVisitor,
Closeable {
    private static final Log LOG = LogFactory.getLog(COSWriter.class);
    public static final byte[] DICT_OPEN = "<<".getBytes(Charsets.US_ASCII);
    public static final byte[] DICT_CLOSE = ">>".getBytes(Charsets.US_ASCII);
    public static final byte[] SPACE = new byte[]{32};
    public static final byte[] COMMENT = new byte[]{37};
    public static final byte[] VERSION = "PDF-1.4".getBytes(Charsets.US_ASCII);
    public static final byte[] GARBAGE = new byte[]{-10, -28, -4, -33};
    public static final byte[] EOF = "%%EOF".getBytes(Charsets.US_ASCII);
    public static final byte[] REFERENCE = "R".getBytes(Charsets.US_ASCII);
    public static final byte[] XREF = "xref".getBytes(Charsets.US_ASCII);
    public static final byte[] XREF_FREE = "f".getBytes(Charsets.US_ASCII);
    public static final byte[] XREF_USED = "n".getBytes(Charsets.US_ASCII);
    public static final byte[] TRAILER = "trailer".getBytes(Charsets.US_ASCII);
    public static final byte[] STARTXREF = "startxref".getBytes(Charsets.US_ASCII);
    public static final byte[] OBJ = "obj".getBytes(Charsets.US_ASCII);
    public static final byte[] ENDOBJ = "endobj".getBytes(Charsets.US_ASCII);
    public static final byte[] ARRAY_OPEN = "[".getBytes(Charsets.US_ASCII);
    public static final byte[] ARRAY_CLOSE = "]".getBytes(Charsets.US_ASCII);
    public static final byte[] STREAM = "stream".getBytes(Charsets.US_ASCII);
    public static final byte[] ENDSTREAM = "endstream".getBytes(Charsets.US_ASCII);
    private final NumberFormat formatXrefOffset = new DecimalFormat("0000000000", DecimalFormatSymbols.getInstance(Locale.US));
    private final NumberFormat formatXrefGeneration = new DecimalFormat("00000", DecimalFormatSymbols.getInstance(Locale.US));
    private OutputStream output;
    private COSStandardOutputStream standardOutput;
    private long startxref = 0L;
    private long number = 0L;
    private final Map<COSBase, COSObjectKey> objectKeys = new Hashtable<COSBase, COSObjectKey>();
    private final Map<COSObjectKey, COSBase> keyObject = new HashMap<COSObjectKey, COSBase>();
    private final List<COSWriterXRefEntry> xRefEntries = new ArrayList<COSWriterXRefEntry>();
    private final Set<COSBase> objectsToWriteSet = new HashSet<COSBase>();
    private final Deque<COSBase> objectsToWrite = new LinkedList<COSBase>();
    private final Set<COSBase> writtenObjects = new HashSet<COSBase>();
    private final Set<COSBase> actualsAdded = new HashSet<COSBase>();
    private COSObjectKey currentObjectKey = null;
    private PDDocument pdDocument = null;
    private FDFDocument fdfDocument = null;
    private boolean willEncrypt = false;
    private boolean incrementalUpdate = false;
    private boolean reachedSignature = false;
    private long signatureOffset;
    private long signatureLength;
    private long byteRangeOffset;
    private long byteRangeLength;
    private RandomAccessRead incrementalInput;
    private OutputStream incrementalOutput;
    private SignatureInterface signatureInterface;
    private byte[] incrementPart;
    private COSArray byteRangeArray;

    public COSWriter(OutputStream outputStream) {
        this.setOutput(outputStream);
        this.setStandardOutput(new COSStandardOutputStream(this.output));
    }

    public COSWriter(OutputStream outputStream, RandomAccessRead inputData) throws IOException {
        this.setOutput(new ByteArrayOutputStream());
        this.setStandardOutput(new COSStandardOutputStream(this.output, inputData.length()));
        this.incrementalInput = inputData;
        this.incrementalOutput = outputStream;
        this.incrementalUpdate = true;
    }

    public COSWriter(OutputStream outputStream, RandomAccessRead inputData, Set<COSDictionary> objectsToWrite) throws IOException {
        this(outputStream, inputData);
        this.objectsToWrite.addAll(objectsToWrite);
    }

    private void prepareIncrement(PDDocument doc) {
        try {
            if (doc != null) {
                COSDocument cosDoc = doc.getDocument();
                Map<COSObjectKey, Long> xrefTable = cosDoc.getXrefTable();
                Set<COSObjectKey> keySet = xrefTable.keySet();
                long highestNumber = doc.getDocument().getHighestXRefObjectNumber();
                for (COSObjectKey cosObjectKey : keySet) {
                    long num;
                    if (cosObjectKey == null) continue;
                    COSBase object = cosDoc.getObjectFromPool(cosObjectKey).getObject();
                    if (object != null && !(object instanceof COSNumber)) {
                        this.objectKeys.put(object, cosObjectKey);
                        this.keyObject.put(cosObjectKey, object);
                    }
                    if ((num = cosObjectKey.getNumber()) <= highestNumber) continue;
                    highestNumber = num;
                }
                this.setNumber(highestNumber);
            }
        }
        catch (IOException e) {
            LOG.error((Object)e, (Throwable)e);
        }
    }

    protected void addXRefEntry(COSWriterXRefEntry entry) {
        this.getXRefEntries().add(entry);
    }

    @Override
    public void close() throws IOException {
        if (this.getStandardOutput() != null) {
            this.getStandardOutput().close();
        }
        if (this.incrementalOutput != null) {
            this.incrementalOutput.close();
        }
    }

    protected long getNumber() {
        return this.number;
    }

    public Map<COSBase, COSObjectKey> getObjectKeys() {
        return this.objectKeys;
    }

    protected OutputStream getOutput() {
        return this.output;
    }

    protected COSStandardOutputStream getStandardOutput() {
        return this.standardOutput;
    }

    protected long getStartxref() {
        return this.startxref;
    }

    protected List<COSWriterXRefEntry> getXRefEntries() {
        return this.xRefEntries;
    }

    protected void setNumber(long newNumber) {
        this.number = newNumber;
    }

    private void setOutput(OutputStream newOutput) {
        this.output = newOutput;
    }

    private void setStandardOutput(COSStandardOutputStream newStandardOutput) {
        this.standardOutput = newStandardOutput;
    }

    protected void setStartxref(long newStartxref) {
        this.startxref = newStartxref;
    }

    protected void doWriteBody(COSDocument doc) throws IOException {
        COSDictionary trailer = doc.getTrailer();
        COSDictionary root = trailer.getCOSDictionary(COSName.ROOT);
        COSDictionary info = trailer.getCOSDictionary(COSName.INFO);
        COSDictionary encrypt = trailer.getCOSDictionary(COSName.ENCRYPT);
        if (root != null) {
            this.addObjectToWrite(root);
        }
        if (info != null) {
            this.addObjectToWrite(info);
        }
        this.doWriteObjects();
        this.willEncrypt = false;
        if (encrypt != null) {
            this.addObjectToWrite(encrypt);
        }
        this.doWriteObjects();
    }

    private void doWriteObjects() throws IOException {
        while (this.objectsToWrite.size() > 0) {
            COSBase nextObject = this.objectsToWrite.removeFirst();
            this.objectsToWriteSet.remove(nextObject);
            this.doWriteObject(nextObject);
        }
    }

    private void addObjectToWrite(COSBase object) {
        COSBase actual = object;
        if (actual instanceof COSObject) {
            actual = ((COSObject)actual).getObject();
        }
        if (this.writtenObjects.contains(object) || this.objectsToWriteSet.contains(object) || this.actualsAdded.contains(actual)) {
            return;
        }
        COSBase cosBase = null;
        COSObjectKey cosObjectKey = null;
        if (actual != null && (cosObjectKey = this.objectKeys.get(actual)) != null) {
            cosBase = this.keyObject.get(cosObjectKey);
            if (!this.isNeedToBeUpdated(object) && !this.isNeedToBeUpdated(cosBase)) {
                return;
            }
        }
        this.objectsToWrite.add(object);
        this.objectsToWriteSet.add(object);
        if (actual != null) {
            this.actualsAdded.add(actual);
        }
    }

    private boolean isNeedToBeUpdated(COSBase base) {
        if (base instanceof COSUpdateInfo) {
            return ((COSUpdateInfo)((Object)base)).isNeedToBeUpdated();
        }
        return false;
    }

    public void doWriteObject(COSBase obj) throws IOException {
        this.writtenObjects.add(obj);
        this.currentObjectKey = this.getObjectKey(obj);
        this.addXRefEntry(new COSWriterXRefEntry(this.getStandardOutput().getPos(), obj, this.currentObjectKey));
        this.getStandardOutput().write(String.valueOf(this.currentObjectKey.getNumber()).getBytes(Charsets.ISO_8859_1));
        this.getStandardOutput().write(SPACE);
        this.getStandardOutput().write(String.valueOf(this.currentObjectKey.getGeneration()).getBytes(Charsets.ISO_8859_1));
        this.getStandardOutput().write(SPACE);
        this.getStandardOutput().write(OBJ);
        this.getStandardOutput().writeEOL();
        obj.accept(this);
        this.getStandardOutput().writeEOL();
        this.getStandardOutput().write(ENDOBJ);
        this.getStandardOutput().writeEOL();
    }

    protected void doWriteHeader(COSDocument doc) throws IOException {
        String headerString = this.fdfDocument != null ? "%FDF-" + doc.getVersion() : "%PDF-" + doc.getVersion();
        this.getStandardOutput().write(headerString.getBytes(Charsets.ISO_8859_1));
        this.getStandardOutput().writeEOL();
        this.getStandardOutput().write(COMMENT);
        this.getStandardOutput().write(GARBAGE);
        this.getStandardOutput().writeEOL();
    }

    protected void doWriteTrailer(COSDocument doc) throws IOException {
        this.getStandardOutput().write(TRAILER);
        this.getStandardOutput().writeEOL();
        COSDictionary trailer = doc.getTrailer();
        Collections.sort(this.getXRefEntries());
        COSWriterXRefEntry lastEntry = this.getXRefEntries().get(this.getXRefEntries().size() - 1);
        trailer.setLong(COSName.SIZE, lastEntry.getKey().getNumber() + 1L);
        if (!this.incrementalUpdate) {
            trailer.removeItem(COSName.PREV);
        }
        if (!doc.isXRefStream()) {
            trailer.removeItem(COSName.XREF_STM);
        }
        trailer.removeItem(COSName.DOC_CHECKSUM);
        COSArray idArray = trailer.getCOSArray(COSName.ID);
        if (idArray != null) {
            idArray.setDirect(true);
        }
        trailer.accept(this);
    }

    private void doWriteXRefInc(COSDocument doc, long hybridPrev) throws IOException {
        if (doc.isXRefStream() || hybridPrev != -1L) {
            PDFXRefStream pdfxRefStream = new PDFXRefStream(doc);
            List<COSWriterXRefEntry> xRefEntries2 = this.getXRefEntries();
            for (COSWriterXRefEntry cosWriterXRefEntry : xRefEntries2) {
                pdfxRefStream.addEntry(cosWriterXRefEntry);
            }
            COSDictionary trailer = doc.getTrailer();
            if (this.incrementalUpdate) {
                trailer.setLong(COSName.PREV, doc.getStartXref());
            } else {
                trailer.removeItem(COSName.PREV);
            }
            pdfxRefStream.addTrailerInfo(trailer);
            pdfxRefStream.setSize(this.getNumber() + 2L);
            this.setStartxref(this.getStandardOutput().getPos());
            COSStream stream2 = pdfxRefStream.getStream();
            this.doWriteObject(stream2);
        }
        if (!doc.isXRefStream() || hybridPrev != -1L) {
            COSDictionary trailer = doc.getTrailer();
            trailer.setLong(COSName.PREV, doc.getStartXref());
            if (hybridPrev != -1L) {
                COSName xrefStm = COSName.XREF_STM;
                trailer.removeItem(xrefStm);
                trailer.setLong(xrefStm, this.getStartxref());
            }
            this.doWriteXRefTable();
            this.doWriteTrailer(doc);
        }
    }

    private void doWriteXRefTable() throws IOException {
        this.addXRefEntry(COSWriterXRefEntry.getNullEntry());
        Collections.sort(this.getXRefEntries());
        this.setStartxref(this.getStandardOutput().getPos());
        this.getStandardOutput().write(XREF);
        this.getStandardOutput().writeEOL();
        Long[] xRefRanges = this.getXRefRanges(this.getXRefEntries());
        int xRefLength = xRefRanges.length;
        int j = 0;
        if (xRefLength % 2 == 0) {
            for (int x = 0; x < xRefLength; x += 2) {
                long xRefRangeX1 = xRefRanges[x + 1];
                this.writeXrefRange(xRefRanges[x], xRefRangeX1);
                int i = 0;
                while ((long)i < xRefRangeX1) {
                    this.writeXrefEntry(this.xRefEntries.get(j++));
                    ++i;
                }
            }
        }
    }

    private void doWriteIncrement() throws IOException {
        IOUtils.copy(new RandomAccessInputStream(this.incrementalInput), this.incrementalOutput);
        this.incrementalOutput.write(((ByteArrayOutputStream)this.output).toByteArray());
    }

    private void doWriteSignature() throws IOException {
        long inLength = this.incrementalInput.length();
        long beforeLength = this.signatureOffset;
        long afterOffset = this.signatureOffset + this.signatureLength;
        long afterLength = this.getStandardOutput().getPos() - (inLength + this.signatureLength) - (this.signatureOffset - inLength);
        String byteRange = "0 " + beforeLength + " " + afterOffset + " " + afterLength + "]";
        this.byteRangeArray.set(0, COSInteger.ZERO);
        this.byteRangeArray.set(1, COSInteger.get(beforeLength));
        this.byteRangeArray.set(2, COSInteger.get(afterOffset));
        this.byteRangeArray.set(3, COSInteger.get(afterLength));
        if ((long)byteRange.length() > this.byteRangeLength) {
            throw new IOException("Can't write new byteRange '" + byteRange + "' not enough space: byteRange.length(): " + byteRange.length() + ", byteRangeLength: " + this.byteRangeLength + ", byteRangeOffset: " + this.byteRangeOffset);
        }
        ByteArrayOutputStream byteOut = (ByteArrayOutputStream)this.output;
        byteOut.flush();
        this.incrementPart = byteOut.toByteArray();
        byte[] byteRangeBytes = byteRange.getBytes(Charsets.ISO_8859_1);
        int i = 0;
        while ((long)i < this.byteRangeLength) {
            this.incrementPart[(int)(this.byteRangeOffset + (long)i - inLength)] = i >= byteRangeBytes.length ? 32 : byteRangeBytes[i];
            ++i;
        }
        if (this.signatureInterface != null) {
            InputStream dataToSign = this.getDataToSign();
            byte[] signatureBytes = this.signatureInterface.sign(dataToSign);
            this.writeExternalSignature(signatureBytes);
        }
    }

    public InputStream getDataToSign() throws IOException {
        if (this.incrementPart == null || this.incrementalInput == null) {
            throw new IllegalStateException("PDF not prepared for signing");
        }
        int incPartSigOffset = (int)(this.signatureOffset - this.incrementalInput.length());
        int afterSigOffset = incPartSigOffset + (int)this.signatureLength;
        int[] range = new int[]{0, incPartSigOffset, afterSigOffset, this.incrementPart.length - afterSigOffset};
        return new SequenceInputStream(new RandomAccessInputStream(this.incrementalInput), new COSFilterInputStream(this.incrementPart, range));
    }

    public void writeExternalSignature(byte[] cmsSignature) throws IOException {
        if (this.incrementPart == null || this.incrementalInput == null) {
            throw new IllegalStateException("PDF not prepared for setting signature");
        }
        byte[] signatureBytes = Hex.getBytes(cmsSignature);
        if ((long)signatureBytes.length > this.signatureLength - 2L) {
            throw new IOException("Can't write signature, not enough space; adjust it with SignatureOptions.setPreferredSignatureSize");
        }
        int incPartSigOffset = (int)(this.signatureOffset - this.incrementalInput.length());
        System.arraycopy(signatureBytes, 0, this.incrementPart, incPartSigOffset + 1, signatureBytes.length);
        IOUtils.copy(new RandomAccessInputStream(this.incrementalInput), this.incrementalOutput);
        this.incrementalOutput.write(this.incrementPart);
        this.incrementPart = null;
    }

    private void writeXrefRange(long x, long y) throws IOException {
        this.getStandardOutput().write(String.valueOf(x).getBytes(Charsets.ISO_8859_1));
        this.getStandardOutput().write(SPACE);
        this.getStandardOutput().write(String.valueOf(y).getBytes(Charsets.ISO_8859_1));
        this.getStandardOutput().writeEOL();
    }

    private void writeXrefEntry(COSWriterXRefEntry entry) throws IOException {
        String offset = this.formatXrefOffset.format(entry.getOffset());
        String generation = this.formatXrefGeneration.format(entry.getKey().getGeneration());
        this.getStandardOutput().write(offset.getBytes(Charsets.ISO_8859_1));
        this.getStandardOutput().write(SPACE);
        this.getStandardOutput().write(generation.getBytes(Charsets.ISO_8859_1));
        this.getStandardOutput().write(SPACE);
        this.getStandardOutput().write(entry.isFree() ? XREF_FREE : XREF_USED);
        this.getStandardOutput().writeCRLF();
    }

    protected Long[] getXRefRanges(List<COSWriterXRefEntry> xRefEntriesList) {
        long last = -2L;
        long count = 1L;
        ArrayList<Long> list = new ArrayList<Long>();
        for (COSWriterXRefEntry object : xRefEntriesList) {
            long nr = object.getKey().getNumber();
            if (nr == last + 1L) {
                ++count;
                last = nr;
                continue;
            }
            if (last == -2L) {
                last = nr;
                continue;
            }
            list.add(last - count + 1L);
            list.add(count);
            last = nr;
            count = 1L;
        }
        if (xRefEntriesList.size() > 0) {
            list.add(last - count + 1L);
            list.add(count);
        }
        return list.toArray(new Long[list.size()]);
    }

    private COSObjectKey getObjectKey(COSBase obj) {
        COSObjectKey key;
        COSBase actual = obj;
        if (actual instanceof COSObject) {
            actual = ((COSObject)obj).getObject();
        }
        if ((key = this.objectKeys.get(obj)) == null && actual != null) {
            key = this.objectKeys.get(actual);
        }
        if (key == null) {
            this.setNumber(this.getNumber() + 1L);
            key = new COSObjectKey(this.getNumber(), 0);
            this.objectKeys.put(obj, key);
            if (actual != null) {
                this.objectKeys.put(actual, key);
            }
        }
        return key;
    }

    @Override
    public Object visitFromArray(COSArray obj) throws IOException {
        int count = 0;
        this.getStandardOutput().write(ARRAY_OPEN);
        Iterator<COSBase> i = obj.iterator();
        while (i.hasNext()) {
            COSBase current = i.next();
            if (current instanceof COSDictionary) {
                if (current.isDirect()) {
                    this.visitFromDictionary((COSDictionary)current);
                } else {
                    this.addObjectToWrite(current);
                    this.writeReference(current);
                }
            } else if (current instanceof COSObject) {
                COSBase subValue = ((COSObject)current).getObject();
                if (this.willEncrypt || this.incrementalUpdate || subValue instanceof COSDictionary || subValue == null) {
                    this.addObjectToWrite(current);
                    this.writeReference(current);
                } else {
                    subValue.accept(this);
                }
            } else if (current == null) {
                COSNull.NULL.accept(this);
            } else {
                current.accept(this);
            }
            ++count;
            if (!i.hasNext()) continue;
            if (count % 10 == 0) {
                this.getStandardOutput().writeEOL();
                continue;
            }
            this.getStandardOutput().write(SPACE);
        }
        this.getStandardOutput().write(ARRAY_CLOSE);
        this.getStandardOutput().writeEOL();
        return null;
    }

    @Override
    public Object visitFromBoolean(COSBoolean obj) throws IOException {
        obj.writePDF(this.getStandardOutput());
        return null;
    }

    @Override
    public Object visitFromDictionary(COSDictionary obj) throws IOException {
        this.detectPossibleSignature(obj);
        this.getStandardOutput().write(DICT_OPEN);
        this.getStandardOutput().writeEOL();
        for (Map.Entry<COSName, COSBase> entry : obj.entrySet()) {
            COSBase value = entry.getValue();
            if (value == null) continue;
            entry.getKey().accept(this);
            this.getStandardOutput().write(SPACE);
            if (value instanceof COSDictionary) {
                COSDictionary dict = (COSDictionary)value;
                if (!this.incrementalUpdate) {
                    COSBase item = dict.getItem(COSName.XOBJECT);
                    if (item != null && !COSName.XOBJECT.equals(entry.getKey())) {
                        item.setDirect(true);
                    }
                    if ((item = dict.getItem(COSName.RESOURCES)) != null && !COSName.RESOURCES.equals(entry.getKey())) {
                        item.setDirect(true);
                    }
                }
                if (dict.isDirect()) {
                    this.visitFromDictionary(dict);
                } else {
                    this.addObjectToWrite(dict);
                    this.writeReference(dict);
                }
            } else if (value instanceof COSObject) {
                COSBase subValue = ((COSObject)value).getObject();
                if (this.willEncrypt || this.incrementalUpdate || subValue instanceof COSDictionary || subValue == null) {
                    this.addObjectToWrite(value);
                    this.writeReference(value);
                } else {
                    subValue.accept(this);
                }
            } else if (this.reachedSignature && COSName.CONTENTS.equals(entry.getKey())) {
                this.signatureOffset = this.getStandardOutput().getPos();
                value.accept(this);
                this.signatureLength = this.getStandardOutput().getPos() - this.signatureOffset;
            } else if (this.reachedSignature && COSName.BYTERANGE.equals(entry.getKey())) {
                this.byteRangeArray = (COSArray)entry.getValue();
                this.byteRangeOffset = this.getStandardOutput().getPos() + 1L;
                value.accept(this);
                this.byteRangeLength = this.getStandardOutput().getPos() - 1L - this.byteRangeOffset;
                this.reachedSignature = false;
            } else {
                value.accept(this);
            }
            this.getStandardOutput().writeEOL();
        }
        this.getStandardOutput().write(DICT_CLOSE);
        this.getStandardOutput().writeEOL();
        return null;
    }

    private void detectPossibleSignature(COSDictionary obj) throws IOException {
        COSArray byteRange;
        COSBase itemType;
        if (!this.reachedSignature && this.incrementalUpdate && (COSName.SIG.equals(itemType = obj.getItem(COSName.TYPE)) || COSName.DOC_TIME_STAMP.equals(itemType)) && (byteRange = obj.getCOSArray(COSName.BYTERANGE)) != null && byteRange.size() == 4) {
            long br3;
            long br2;
            COSBase base2 = byteRange.get(2);
            COSBase base3 = byteRange.get(3);
            if (base2 instanceof COSInteger && base3 instanceof COSInteger && (br2 = ((COSInteger)base2).longValue()) + (br3 = ((COSInteger)base3).longValue()) > this.incrementalInput.length()) {
                this.reachedSignature = true;
            }
        }
    }

    @Override
    public Object visitFromDocument(COSDocument doc) throws IOException {
        if (!this.incrementalUpdate) {
            this.doWriteHeader(doc);
        } else {
            this.getStandardOutput().writeCRLF();
        }
        this.doWriteBody(doc);
        COSDictionary trailer = doc.getTrailer();
        long hybridPrev = -1L;
        if (trailer != null) {
            hybridPrev = trailer.getLong(COSName.XREF_STM);
        }
        if (this.incrementalUpdate || doc.isXRefStream()) {
            this.doWriteXRefInc(doc, hybridPrev);
        } else {
            this.doWriteXRefTable();
            this.doWriteTrailer(doc);
        }
        this.getStandardOutput().write(STARTXREF);
        this.getStandardOutput().writeEOL();
        this.getStandardOutput().write(String.valueOf(this.getStartxref()).getBytes(Charsets.ISO_8859_1));
        this.getStandardOutput().writeEOL();
        this.getStandardOutput().write(EOF);
        this.getStandardOutput().writeEOL();
        if (this.incrementalUpdate) {
            if (this.signatureOffset == 0L || this.byteRangeOffset == 0L) {
                this.doWriteIncrement();
            } else {
                this.doWriteSignature();
            }
        }
        return null;
    }

    @Override
    public Object visitFromFloat(COSFloat obj) throws IOException {
        obj.writePDF(this.getStandardOutput());
        return null;
    }

    @Override
    public Object visitFromInt(COSInteger obj) throws IOException {
        obj.writePDF(this.getStandardOutput());
        return null;
    }

    @Override
    public Object visitFromName(COSName obj) throws IOException {
        obj.writePDF(this.getStandardOutput());
        return null;
    }

    @Override
    public Object visitFromNull(COSNull obj) throws IOException {
        obj.writePDF(this.getStandardOutput());
        return null;
    }

    public void writeReference(COSBase obj) throws IOException {
        COSObjectKey key = this.getObjectKey(obj);
        this.getStandardOutput().write(String.valueOf(key.getNumber()).getBytes(Charsets.ISO_8859_1));
        this.getStandardOutput().write(SPACE);
        this.getStandardOutput().write(String.valueOf(key.getGeneration()).getBytes(Charsets.ISO_8859_1));
        this.getStandardOutput().write(SPACE);
        this.getStandardOutput().write(REFERENCE);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object visitFromStream(COSStream obj) throws IOException {
        if (this.willEncrypt) {
            this.pdDocument.getEncryption().getSecurityHandler().encryptStream(obj, this.currentObjectKey.getNumber(), this.currentObjectKey.getGeneration());
        }
        InputStream input = null;
        try {
            this.visitFromDictionary(obj);
            this.getStandardOutput().write(STREAM);
            this.getStandardOutput().writeCRLF();
            input = obj.createRawInputStream();
            IOUtils.copy(input, this.getStandardOutput());
            this.getStandardOutput().writeCRLF();
            this.getStandardOutput().write(ENDSTREAM);
            this.getStandardOutput().writeEOL();
            Object var3_3 = null;
            return var3_3;
        }
        finally {
            if (input != null) {
                input.close();
            }
        }
    }

    @Override
    public Object visitFromString(COSString obj) throws IOException {
        if (this.willEncrypt) {
            this.pdDocument.getEncryption().getSecurityHandler().encryptString(obj, this.currentObjectKey.getNumber(), this.currentObjectKey.getGeneration());
        }
        COSWriter.writeString(obj, (OutputStream)this.getStandardOutput());
        return null;
    }

    public void write(COSDocument doc) throws IOException {
        PDDocument pdDoc = new PDDocument(doc);
        this.write(pdDoc);
    }

    public void write(PDDocument doc) throws IOException {
        this.write(doc, null);
    }

    public void write(PDDocument doc, SignatureInterface signInterface) throws IOException {
        COSDictionary trailer;
        COSDocument cosDoc;
        Long idTime = doc.getDocumentId() == null ? System.currentTimeMillis() : doc.getDocumentId();
        this.pdDocument = doc;
        this.signatureInterface = signInterface;
        if (this.incrementalUpdate) {
            this.prepareIncrement(doc);
        }
        if (doc.isAllSecurityToBeRemoved()) {
            this.willEncrypt = false;
            cosDoc = doc.getDocument();
            trailer = cosDoc.getTrailer();
            trailer.removeItem(COSName.ENCRYPT);
        } else if (this.pdDocument.getEncryption() != null) {
            if (!this.incrementalUpdate) {
                SecurityHandler securityHandler = this.pdDocument.getEncryption().getSecurityHandler();
                if (!securityHandler.hasProtectionPolicy()) {
                    throw new IllegalStateException("PDF contains an encryption dictionary, please remove it with setAllSecurityToBeRemoved() or set a protection policy with protect()");
                }
                securityHandler.prepareDocumentForEncryption(this.pdDocument);
            }
            this.willEncrypt = true;
        } else {
            this.willEncrypt = false;
        }
        cosDoc = this.pdDocument.getDocument();
        trailer = cosDoc.getTrailer();
        COSArray idArray = null;
        boolean missingID = true;
        COSBase base = trailer.getDictionaryObject(COSName.ID);
        if (base instanceof COSArray && (idArray = (COSArray)base).size() == 2) {
            missingID = false;
        }
        if (idArray != null && idArray.size() == 2) {
            missingID = false;
        }
        if (missingID || this.incrementalUpdate) {
            MessageDigest md5;
            try {
                md5 = MessageDigest.getInstance("MD5");
            }
            catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            md5.update(Long.toString(idTime).getBytes(Charsets.ISO_8859_1));
            COSDictionary info = trailer.getCOSDictionary(COSName.INFO);
            if (info != null) {
                for (COSBase cosBase : info.getValues()) {
                    md5.update(cosBase.toString().getBytes(Charsets.ISO_8859_1));
                }
            }
            COSString firstID = missingID ? new COSString(md5.digest()) : (COSString)idArray.get(0);
            COSString secondID = missingID ? firstID : new COSString(md5.digest());
            idArray = new COSArray();
            idArray.add(firstID);
            idArray.add(secondID);
            trailer.setItem(COSName.ID, (COSBase)idArray);
        }
        cosDoc.accept(this);
    }

    public void write(FDFDocument doc) throws IOException {
        this.fdfDocument = doc;
        this.willEncrypt = false;
        COSDocument cosDoc = this.fdfDocument.getDocument();
        cosDoc.accept(this);
    }

    public static void writeString(COSString string, OutputStream output) throws IOException {
        COSWriter.writeString(string.getBytes(), string.getForceHexForm(), output);
    }

    public static void writeString(byte[] bytes, OutputStream output) throws IOException {
        COSWriter.writeString(bytes, false, output);
    }

    private static void writeString(byte[] bytes, boolean forceHex, OutputStream output) throws IOException {
        boolean isASCII = true;
        if (!forceHex) {
            for (byte b : bytes) {
                if (b < 0) {
                    isASCII = false;
                    break;
                }
                if (b != 13 && b != 10) continue;
                isASCII = false;
                break;
            }
        }
        if (isASCII && !forceHex) {
            output.write(40);
            block4: for (byte b : bytes) {
                switch (b) {
                    case 40: 
                    case 41: 
                    case 92: {
                        output.write(92);
                        output.write(b);
                        continue block4;
                    }
                    default: {
                        output.write(b);
                    }
                }
            }
            output.write(41);
        } else {
            output.write(60);
            Hex.writeHexBytes(bytes, output);
            output.write(62);
        }
    }
}

