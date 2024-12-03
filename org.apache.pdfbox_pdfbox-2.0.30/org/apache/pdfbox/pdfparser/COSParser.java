/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdfparser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNull;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSObjectKey;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.pdfparser.BaseParser;
import org.apache.pdfbox.pdfparser.EndstreamOutputStream;
import org.apache.pdfbox.pdfparser.PDFObjectStreamParser;
import org.apache.pdfbox.pdfparser.PDFXrefStreamParser;
import org.apache.pdfbox.pdfparser.RandomAccessSource;
import org.apache.pdfbox.pdfparser.XrefTrailerResolver;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.DecryptionMaterial;
import org.apache.pdfbox.pdmodel.encryption.PDEncryption;
import org.apache.pdfbox.pdmodel.encryption.PublicKeyDecryptionMaterial;
import org.apache.pdfbox.pdmodel.encryption.SecurityHandler;
import org.apache.pdfbox.pdmodel.encryption.StandardDecryptionMaterial;
import org.apache.pdfbox.util.Charsets;

public class COSParser
extends BaseParser {
    private static final String PDF_HEADER = "%PDF-";
    private static final String FDF_HEADER = "%FDF-";
    private static final String PDF_DEFAULT_VERSION = "1.4";
    private static final String FDF_DEFAULT_VERSION = "1.0";
    private static final char[] XREF_TABLE = new char[]{'x', 'r', 'e', 'f'};
    private static final char[] XREF_STREAM = new char[]{'/', 'X', 'R', 'e', 'f'};
    private static final char[] STARTXREF = new char[]{'s', 't', 'a', 'r', 't', 'x', 'r', 'e', 'f'};
    private static final byte[] ENDSTREAM = new byte[]{101, 110, 100, 115, 116, 114, 101, 97, 109};
    private static final byte[] ENDOBJ = new byte[]{101, 110, 100, 111, 98, 106};
    private static final long MINIMUM_SEARCH_OFFSET = 6L;
    private static final int X = 120;
    private static final int STRMBUFLEN = 2048;
    private final byte[] strmBuf = new byte[2048];
    protected final RandomAccessRead source;
    private AccessPermission accessPermission;
    private InputStream keyStoreInputStream = null;
    private String password = "";
    private String keyAlias = null;
    public static final String SYSPROP_PARSEMINIMAL = "org.apache.pdfbox.pdfparser.nonSequentialPDFParser.parseMinimal";
    public static final String SYSPROP_EOFLOOKUPRANGE = "org.apache.pdfbox.pdfparser.nonSequentialPDFParser.eofLookupRange";
    private static final int DEFAULT_TRAIL_BYTECOUNT = 2048;
    protected static final char[] EOF_MARKER = new char[]{'%', '%', 'E', 'O', 'F'};
    protected static final char[] OBJ_MARKER = new char[]{'o', 'b', 'j'};
    private static final char[] TRAILER_MARKER = new char[]{'t', 'r', 'a', 'i', 'l', 'e', 'r'};
    private static final char[] OBJ_STREAM = new char[]{'/', 'O', 'b', 'j', 'S', 't', 'm'};
    private long trailerOffset;
    protected long fileLen;
    private boolean isLenient = true;
    protected boolean initialParseDone = false;
    private boolean trailerWasRebuild = false;
    private Map<COSObjectKey, Long> bfSearchCOSObjectKeyOffsets = null;
    private Long lastEOFMarker = null;
    private List<Long> bfSearchXRefTablesOffsets = null;
    private List<Long> bfSearchXRefStreamsOffsets = null;
    private PDEncryption encryption = null;
    protected SecurityHandler securityHandler = null;
    private int readTrailBytes = 2048;
    private static final Log LOG = LogFactory.getLog(COSParser.class);
    protected XrefTrailerResolver xrefTrailerResolver = new XrefTrailerResolver();
    public static final String TMP_FILE_PREFIX = "tmpPDF";
    private static final int STREAMCOPYBUFLEN = 8192;
    private final byte[] streamCopyBuf = new byte[8192];

    public COSParser(RandomAccessRead source) {
        super(new RandomAccessSource(source));
        this.source = source;
    }

    public COSParser(RandomAccessRead source, String password, InputStream keyStore, String keyAlias) {
        super(new RandomAccessSource(source));
        this.source = source;
        this.password = password;
        this.keyAlias = keyAlias;
        this.keyStoreInputStream = keyStore;
    }

    public void setEOFLookupRange(int byteCount) {
        if (byteCount > 15) {
            this.readTrailBytes = byteCount;
        }
    }

    protected COSDictionary retrieveTrailer() throws IOException {
        COSDictionary trailer = null;
        boolean rebuildTrailer = false;
        try {
            long startXRefOffset = this.getStartxrefOffset();
            if (startXRefOffset > -1L) {
                trailer = this.parseXref(startXRefOffset);
            } else {
                rebuildTrailer = this.isLenient();
            }
        }
        catch (IOException exception) {
            if (this.isLenient()) {
                rebuildTrailer = true;
            }
            throw exception;
        }
        if (trailer != null && trailer.getItem(COSName.ROOT) == null) {
            rebuildTrailer = this.isLenient();
        }
        if (rebuildTrailer) {
            trailer = this.rebuildTrailer();
        } else {
            this.prepareDecryption();
            if (this.bfSearchCOSObjectKeyOffsets != null && !this.bfSearchCOSObjectKeyOffsets.isEmpty()) {
                this.bfSearchForObjStreams();
            }
        }
        return trailer;
    }

    protected COSDictionary parseXref(long startXRefOffset) throws IOException {
        this.source.seek(startXRefOffset);
        long startXrefOffset = Math.max(0L, this.parseStartXref());
        long fixedOffset = this.checkXRefOffset(startXrefOffset);
        if (fixedOffset > -1L) {
            startXrefOffset = fixedOffset;
        }
        this.document.setStartXref(startXrefOffset);
        long prev = startXrefOffset;
        HashSet<Long> prevSet = new HashSet<Long>();
        COSDictionary trailer = null;
        while (prev > 0L) {
            prevSet.add(prev);
            this.source.seek(prev);
            this.skipSpaces();
            prevSet.add(this.source.getPosition());
            if (this.source.peek() == 120) {
                if (!this.parseXrefTable(prev) || !this.parseTrailer()) {
                    throw new IOException("Expected trailer object at offset " + this.source.getPosition());
                }
                trailer = this.xrefTrailerResolver.getCurrentTrailer();
                if (trailer.containsKey(COSName.XREF_STM)) {
                    int streamOffset = trailer.getInt(COSName.XREF_STM);
                    fixedOffset = this.checkXRefOffset(streamOffset);
                    if (fixedOffset > -1L && fixedOffset != (long)streamOffset) {
                        LOG.warn((Object)("/XRefStm offset " + streamOffset + " is incorrect, corrected to " + fixedOffset));
                        streamOffset = (int)fixedOffset;
                        trailer.setInt(COSName.XREF_STM, streamOffset);
                    }
                    if (streamOffset > 0) {
                        this.source.seek(streamOffset);
                        this.skipSpaces();
                        try {
                            this.parseXrefObjStream(prev, false);
                        }
                        catch (IOException ex) {
                            if (this.isLenient) {
                                LOG.error((Object)("Failed to parse /XRefStm at offset " + streamOffset), (Throwable)ex);
                            }
                            throw ex;
                        }
                    } else if (this.isLenient) {
                        LOG.error((Object)("Skipped XRef stream due to a corrupt offset:" + streamOffset));
                    } else {
                        throw new IOException("Skipped XRef stream due to a corrupt offset:" + streamOffset);
                    }
                }
                prev = trailer.getLong(COSName.PREV);
            } else {
                prev = this.parseXrefObjStream(prev, true);
                trailer = this.xrefTrailerResolver.getCurrentTrailer();
            }
            if (prev > 0L && (fixedOffset = this.checkXRefOffset(prev)) > -1L && fixedOffset != prev) {
                prev = fixedOffset;
                trailer.setLong(COSName.PREV, prev);
            }
            if (!prevSet.contains(prev)) continue;
            throw new IOException("/Prev loop at offset " + prev);
        }
        this.xrefTrailerResolver.setStartxref(startXrefOffset);
        trailer = this.xrefTrailerResolver.getTrailer();
        this.document.setTrailer(trailer);
        this.document.setIsXRefStream(XrefTrailerResolver.XRefType.STREAM == this.xrefTrailerResolver.getXrefType());
        this.checkXrefOffsets();
        this.document.addXRefTable(this.xrefTrailerResolver.getXrefTable());
        return trailer;
    }

    private long parseXrefObjStream(long objByteOffset, boolean isStandalone) throws IOException {
        long objectNumber = this.readObjectNumber();
        long currentHighestXRefObjectNumber = this.document.getHighestXRefObjectNumber();
        this.document.setHighestXRefObjectNumber(Math.max(currentHighestXRefObjectNumber, objectNumber));
        this.readGenerationNumber();
        this.readExpectedString(OBJ_MARKER, true);
        COSDictionary dict = this.parseCOSDictionary();
        COSStream xrefStream = this.parseCOSStream(dict);
        this.parseXrefStream(xrefStream, objByteOffset, isStandalone);
        xrefStream.close();
        return dict.getLong(COSName.PREV);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final long getStartxrefOffset() throws IOException {
        long skipBytes;
        byte[] buf;
        try {
            int readBytes;
            int trailByteCount = this.fileLen < (long)this.readTrailBytes ? (int)this.fileLen : this.readTrailBytes;
            buf = new byte[trailByteCount];
            skipBytes = this.fileLen - (long)trailByteCount;
            this.source.seek(skipBytes);
            for (int off = 0; off < trailByteCount; off += readBytes) {
                readBytes = this.source.read(buf, off, trailByteCount - off);
                if (readBytes >= 1) continue;
                throw new IOException("No more bytes to read for trailing buffer, but expected: " + (trailByteCount - off));
            }
        }
        finally {
            this.source.seek(0L);
        }
        int bufOff = this.lastIndexOf(EOF_MARKER, buf, buf.length);
        if (bufOff < 0) {
            if (this.isLenient) {
                bufOff = buf.length;
                LOG.debug((Object)("Missing end of file marker '" + new String(EOF_MARKER) + "'"));
            } else {
                throw new IOException("Missing end of file marker '" + new String(EOF_MARKER) + "'");
            }
        }
        if ((bufOff = this.lastIndexOf(STARTXREF, buf, bufOff)) < 0) {
            throw new IOException("Missing 'startxref' marker.");
        }
        return skipBytes + (long)bufOff;
    }

    protected int lastIndexOf(char[] pattern, byte[] buf, int endOff) {
        int lastPatternChOff = pattern.length - 1;
        int bufOff = endOff;
        int patOff = lastPatternChOff;
        char lookupCh = pattern[patOff];
        while (--bufOff >= 0) {
            if (buf[bufOff] == lookupCh) {
                if (--patOff < 0) {
                    return bufOff;
                }
                lookupCh = pattern[patOff];
                continue;
            }
            if (patOff >= lastPatternChOff) continue;
            patOff = lastPatternChOff;
            lookupCh = pattern[patOff];
        }
        return -1;
    }

    public boolean isLenient() {
        return this.isLenient;
    }

    public void setLenient(boolean lenient) {
        if (this.initialParseDone) {
            throw new IllegalArgumentException("Cannot change leniency after parsing");
        }
        this.isLenient = lenient;
    }

    private long getObjectId(COSObject obj) {
        return obj.getObjectNumber() << 32 | (long)obj.getGenerationNumber();
    }

    private void addNewToList(Queue<COSBase> toBeParsedList, Collection<COSBase> newObjects, Set<Long> addedObjects) {
        for (COSBase newObject : newObjects) {
            this.addNewToList(toBeParsedList, newObject, addedObjects);
        }
    }

    private void addNewToList(Queue<COSBase> toBeParsedList, COSBase newObject, Set<Long> addedObjects) {
        if (newObject instanceof COSObject) {
            long objId = this.getObjectId((COSObject)newObject);
            if (!addedObjects.add(objId)) {
                return;
            }
            toBeParsedList.add(newObject);
        } else if (newObject instanceof COSDictionary || newObject instanceof COSArray) {
            toBeParsedList.add(newObject);
        }
    }

    /*
     * Enabled aggressive block sorting
     */
    protected void parseDictObjects(COSDictionary dict, COSName ... excludeObjects) throws IOException {
        LinkedList<COSBase> toBeParsedList = new LinkedList<COSBase>();
        TreeMap<Long, List<COSObject>> objToBeParsed = new TreeMap<Long, List<COSObject>>();
        HashSet<Long> parsedObjects = new HashSet<Long>();
        HashSet<Long> addedObjects = new HashSet<Long>();
        this.addExcludedToList(excludeObjects, dict, parsedObjects);
        this.addNewToList(toBeParsedList, dict.getValues(), addedObjects);
        block0: while (true) {
            COSBase baseObj;
            if (toBeParsedList.isEmpty()) {
                if (objToBeParsed.isEmpty()) return;
            }
            while ((baseObj = (COSBase)toBeParsedList.poll()) != null) {
                if (baseObj instanceof COSDictionary) {
                    this.addNewToList(toBeParsedList, ((COSDictionary)baseObj).getValues(), addedObjects);
                    continue;
                }
                if (baseObj instanceof COSArray) {
                    for (COSBase cosBase : (COSArray)baseObj) {
                        this.addNewToList(toBeParsedList, cosBase, addedObjects);
                    }
                    continue;
                }
                if (!(baseObj instanceof COSObject)) continue;
                COSObject obj = (COSObject)baseObj;
                long objId = this.getObjectId(obj);
                COSObjectKey objKey = new COSObjectKey(obj.getObjectNumber(), obj.getGenerationNumber());
                if (parsedObjects.contains(objId)) continue;
                Long fileOffset = this.document.getXrefTable().get(objKey);
                if (fileOffset == null && this.isLenient) {
                    this.bfSearchForObjects();
                    fileOffset = this.bfSearchCOSObjectKeyOffsets.get(objKey);
                    if (fileOffset != null) {
                        LOG.debug((Object)("Set missing " + fileOffset + " for object " + objKey));
                        this.document.getXrefTable().put(objKey, fileOffset);
                    }
                }
                if (fileOffset != null && fileOffset != 0L) {
                    ArrayList<COSObject> stmObjects;
                    block19: {
                        if (fileOffset > 0L) {
                            objToBeParsed.put(fileOffset, Collections.singletonList(obj));
                            continue;
                        }
                        COSObjectKey key = new COSObjectKey((int)(-fileOffset.longValue()), 0);
                        fileOffset = this.document.getXrefTable().get(key);
                        if (fileOffset == null || fileOffset <= 0L) {
                            if (this.isLenient) {
                                this.bfSearchForObjects();
                                fileOffset = this.bfSearchCOSObjectKeyOffsets.get(key);
                                if (fileOffset != null) {
                                    LOG.debug((Object)("Set missing " + fileOffset + " for object " + key));
                                    this.document.getXrefTable().put(key, fileOffset);
                                    break block19;
                                } else {
                                    LOG.warn((Object)("Invalid object stream xref object reference for key '" + objKey + "': " + fileOffset));
                                    continue;
                                }
                            }
                            String msg = "Invalid object stream xref object reference for key '" + objKey + "': " + fileOffset;
                            if (!this.isLenient) throw new IOException(msg);
                            if (fileOffset != null) throw new IOException(msg);
                            LOG.warn((Object)msg);
                            continue;
                        }
                    }
                    if ((stmObjects = (ArrayList<COSObject>)objToBeParsed.get(fileOffset)) == null) {
                        stmObjects = new ArrayList<COSObject>();
                        objToBeParsed.put(fileOffset, stmObjects);
                    } else if (!(stmObjects instanceof ArrayList)) {
                        throw new IOException(obj + " cannot be assigned to offset " + fileOffset + ", this belongs to " + stmObjects.get(0));
                    }
                    stmObjects.add(obj);
                    continue;
                }
                COSObject pdfObject = this.document.getObjectFromPool(objKey);
                pdfObject.setObject(COSNull.NULL);
            }
            if (objToBeParsed.isEmpty()) {
                return;
            }
            Iterator<COSBase> iterator = ((List)objToBeParsed.remove(objToBeParsed.firstKey())).iterator();
            while (true) {
                if (!iterator.hasNext()) continue block0;
                COSObject obj = (COSObject)iterator.next();
                COSBase parsedObj = this.parseObjectDynamically(obj, false);
                if (parsedObj == null) continue;
                obj.setObject(parsedObj);
                this.addNewToList(toBeParsedList, parsedObj, addedObjects);
                parsedObjects.add(this.getObjectId(obj));
            }
            break;
        }
    }

    private void addExcludedToList(COSName[] excludeObjects, COSDictionary dict, Set<Long> parsedObjects) {
        if (excludeObjects != null) {
            for (COSName objName : excludeObjects) {
                COSBase baseObj = dict.getItem(objName);
                if (!(baseObj instanceof COSObject)) continue;
                parsedObjects.add(this.getObjectId((COSObject)baseObj));
            }
        }
    }

    protected final COSBase parseObjectDynamically(COSObject obj, boolean requireExistingNotCompressedObj) throws IOException {
        return this.parseObjectDynamically(obj.getObjectNumber(), obj.getGenerationNumber(), requireExistingNotCompressedObj);
    }

    protected COSBase parseObjectDynamically(long objNr, int objGenNr, boolean requireExistingNotCompressedObj) throws IOException {
        COSObjectKey objKey = new COSObjectKey(objNr, objGenNr);
        COSObject pdfObject = this.document.getObjectFromPool(objKey);
        if (pdfObject.getObject() == null) {
            Long offsetOrObjstmObNr = this.document.getXrefTable().get(objKey);
            if (offsetOrObjstmObNr == null && this.isLenient) {
                this.bfSearchForObjects();
                offsetOrObjstmObNr = this.bfSearchCOSObjectKeyOffsets.get(objKey);
                if (offsetOrObjstmObNr != null) {
                    LOG.debug((Object)("Set missing offset " + offsetOrObjstmObNr + " for object " + objKey));
                    this.document.getXrefTable().put(objKey, offsetOrObjstmObNr);
                }
            }
            if (requireExistingNotCompressedObj && (offsetOrObjstmObNr == null || offsetOrObjstmObNr <= 0L)) {
                throw new IOException("Object must be defined and must not be compressed object: " + objKey.getNumber() + ":" + objKey.getGeneration());
            }
            if (pdfObject.derefencingInProgress()) {
                throw new IOException("Possible recursion detected when dereferencing object " + objNr + " " + objGenNr);
            }
            pdfObject.dereferencingStarted();
            if (offsetOrObjstmObNr == null && this.isLenient && this.bfSearchCOSObjectKeyOffsets == null) {
                this.bfSearchForObjects();
                if (!this.bfSearchCOSObjectKeyOffsets.isEmpty()) {
                    LOG.debug((Object)"Add all new read objects from brute force search to the xref table");
                    Map<COSObjectKey, Long> xrefOffset = this.document.getXrefTable();
                    Set<Map.Entry<COSObjectKey, Long>> entries = this.bfSearchCOSObjectKeyOffsets.entrySet();
                    for (Map.Entry<COSObjectKey, Long> entry : entries) {
                        COSObjectKey key = entry.getKey();
                        if (xrefOffset.containsKey(key)) continue;
                        xrefOffset.put(key, entry.getValue());
                    }
                    offsetOrObjstmObNr = xrefOffset.get(objKey);
                }
            }
            if (offsetOrObjstmObNr == null) {
                pdfObject.setObject(COSNull.NULL);
            } else if (offsetOrObjstmObNr > 0L) {
                this.parseFileObject(offsetOrObjstmObNr, objKey, pdfObject);
            } else {
                this.parseObjectStream((int)(-offsetOrObjstmObNr.longValue()));
            }
            pdfObject.dereferencingFinished();
        }
        return pdfObject.getObject();
    }

    private void parseFileObject(Long offsetOrObjstmObNr, COSObjectKey objKey, COSObject pdfObject) throws IOException {
        this.source.seek(offsetOrObjstmObNr);
        long readObjNr = this.readObjectNumber();
        int readObjGen = this.readGenerationNumber();
        this.readExpectedString(OBJ_MARKER, true);
        if (readObjNr != objKey.getNumber() || readObjGen != objKey.getGeneration()) {
            throw new IOException("XREF for " + objKey.getNumber() + ":" + objKey.getGeneration() + " points to wrong object: " + readObjNr + ":" + readObjGen + " at offset " + offsetOrObjstmObNr);
        }
        this.skipSpaces();
        COSBase pb = this.parseDirObject();
        String endObjectKey = this.readString();
        if (endObjectKey.equals("stream")) {
            COSStream stream;
            this.source.rewind(endObjectKey.getBytes(Charsets.ISO_8859_1).length);
            if (pb instanceof COSDictionary) {
                stream = this.parseCOSStream((COSDictionary)pb);
                if (this.securityHandler != null) {
                    this.securityHandler.decryptStream(stream, objKey.getNumber(), objKey.getGeneration());
                }
            } else {
                throw new IOException("Stream not preceded by dictionary (offset: " + offsetOrObjstmObNr + ").");
            }
            pb = stream;
            this.skipSpaces();
            endObjectKey = this.readLine();
            if (!endObjectKey.startsWith("endobj") && endObjectKey.startsWith("endstream") && (endObjectKey = endObjectKey.substring(9).trim()).length() == 0) {
                endObjectKey = this.readLine();
            }
        } else if (this.securityHandler != null) {
            this.securityHandler.decrypt(pb, objKey.getNumber(), objKey.getGeneration());
        }
        pdfObject.setObject(pb);
        if (!endObjectKey.startsWith("endobj")) {
            if (this.isLenient) {
                LOG.warn((Object)("Object (" + readObjNr + ":" + readObjGen + ") at offset " + offsetOrObjstmObNr + " does not end with 'endobj' but with '" + endObjectKey + "'"));
            } else {
                throw new IOException("Object (" + readObjNr + ":" + readObjGen + ") at offset " + offsetOrObjstmObNr + " does not end with 'endobj' but with '" + endObjectKey + "'");
            }
        }
    }

    private void parseObjectStream(int objstmObjNr) throws IOException {
        COSBase objstmBaseObj = this.parseObjectDynamically(objstmObjNr, 0, true);
        if (objstmBaseObj instanceof COSStream) {
            PDFObjectStreamParser parser;
            try {
                parser = new PDFObjectStreamParser((COSStream)objstmBaseObj, this.document);
            }
            catch (IOException ex) {
                if (this.isLenient) {
                    LOG.error((Object)("object stream " + objstmObjNr + " could not be parsed due to an exception"), (Throwable)ex);
                    return;
                }
                throw ex;
            }
            try {
                parser.parse();
            }
            catch (IOException exception) {
                if (this.isLenient) {
                    LOG.debug((Object)("Stop reading object stream " + objstmObjNr + " due to an exception"), (Throwable)exception);
                    return;
                }
                throw exception;
            }
            for (COSObject next : parser.getObjects()) {
                COSObjectKey stmObjKey = new COSObjectKey(next);
                Long offset = this.xrefTrailerResolver.getXrefTable().get(stmObjKey);
                if (offset == null || offset != (long)(-objstmObjNr)) continue;
                COSObject stmObj = this.document.getObjectFromPool(stmObjKey);
                stmObj.setObject(next.getObject());
            }
        }
    }

    private COSNumber getLength(COSBase lengthBaseObj, COSName streamType) throws IOException {
        COSNumber retVal;
        if (lengthBaseObj == null) {
            return null;
        }
        if (lengthBaseObj instanceof COSNumber) {
            retVal = (COSNumber)lengthBaseObj;
        } else if (lengthBaseObj instanceof COSObject) {
            COSObject lengthObj = (COSObject)lengthBaseObj;
            COSBase length = lengthObj.getObject();
            if (length == null) {
                long curFileOffset = this.source.getPosition();
                boolean isObjectStream = COSName.OBJ_STM.equals(streamType);
                this.parseObjectDynamically(lengthObj, isObjectStream);
                this.source.seek(curFileOffset);
                length = lengthObj.getObject();
            }
            if (length == null) {
                throw new IOException("Length object content was not read.");
            }
            if (COSNull.NULL == length) {
                LOG.warn((Object)("Length object (" + lengthObj.getObjectNumber() + " " + lengthObj.getGenerationNumber() + ") not found"));
                return null;
            }
            if (!(length instanceof COSNumber)) {
                throw new IOException("Wrong type of referenced length object " + lengthObj + ": " + length.getClass().getSimpleName());
            }
            retVal = (COSNumber)length;
        } else {
            throw new IOException("Wrong type of length object: " + lengthBaseObj.getClass().getSimpleName());
        }
        return retVal;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected COSStream parseCOSStream(COSDictionary dic) throws IOException {
        OutputStream out;
        COSStream stream = this.document.createCOSStream(dic);
        this.readString();
        this.skipWhiteSpaces();
        COSNumber streamLengthObj = this.getLength(dic.getItem(COSName.LENGTH), dic.getCOSName(COSName.TYPE));
        if (streamLengthObj == null) {
            if (this.isLenient) {
                LOG.warn((Object)("The stream doesn't provide any stream length, using fallback readUntilEnd, at offset " + this.source.getPosition()));
            } else {
                throw new IOException("Missing length for stream.");
            }
        }
        if (streamLengthObj != null && this.validateStreamLength(streamLengthObj.longValue())) {
            out = stream.createRawOutputStream();
            try {
                this.readValidStream(out, streamLengthObj);
            }
            finally {
                out.close();
                stream.setItem(COSName.LENGTH, (COSBase)streamLengthObj);
            }
        }
        out = stream.createRawOutputStream();
        try {
            this.readUntilEndStream(new EndstreamOutputStream(out));
        }
        finally {
            out.close();
            if (streamLengthObj != null) {
                stream.setItem(COSName.LENGTH, (COSBase)streamLengthObj);
            }
        }
        String endStream = this.readString();
        if (endStream.equals("endobj") && this.isLenient) {
            LOG.warn((Object)("stream ends with 'endobj' instead of 'endstream' at offset " + this.source.getPosition()));
            this.source.rewind(ENDOBJ.length);
        } else if (endStream.length() > 9 && this.isLenient && endStream.startsWith("endstream")) {
            LOG.warn((Object)("stream ends with '" + endStream + "' instead of 'endstream' at offset " + this.source.getPosition()));
            this.source.rewind(endStream.substring(9).getBytes(Charsets.ISO_8859_1).length);
        } else if (!endStream.equals("endstream")) {
            throw new IOException("Error reading stream, expected='endstream' actual='" + endStream + "' at offset " + this.source.getPosition());
        }
        return stream;
    }

    private void readUntilEndStream(OutputStream out) throws IOException {
        int bufSize;
        int charMatchCount = 0;
        byte[] keyw = ENDSTREAM;
        int quickTestOffset = 5;
        while ((bufSize = this.source.read(this.strmBuf, charMatchCount, 2048 - charMatchCount)) > 0) {
            int contentBytes;
            int bIdx;
            int maxQuicktestIdx = (bufSize += charMatchCount) - 5;
            for (bIdx = charMatchCount; bIdx < bufSize; ++bIdx) {
                byte ch;
                int quickTestIdx = bIdx + 5;
                if (charMatchCount == 0 && quickTestIdx < maxQuicktestIdx && ((ch = this.strmBuf[quickTestIdx]) > 116 || ch < 97)) {
                    bIdx = quickTestIdx;
                    continue;
                }
                ch = this.strmBuf[bIdx];
                if (ch == keyw[charMatchCount]) {
                    if (++charMatchCount != keyw.length) continue;
                    ++bIdx;
                    break;
                }
                if (charMatchCount == 3 && ch == ENDOBJ[charMatchCount]) {
                    keyw = ENDOBJ;
                    ++charMatchCount;
                    continue;
                }
                charMatchCount = ch == 101 ? 1 : (ch == 110 && charMatchCount == 7 ? 2 : 0);
                keyw = ENDSTREAM;
            }
            if ((contentBytes = Math.max(0, bIdx - charMatchCount)) > 0) {
                out.write(this.strmBuf, 0, contentBytes);
            }
            if (charMatchCount == keyw.length) {
                this.source.rewind(bufSize - contentBytes);
                break;
            }
            System.arraycopy(keyw, 0, this.strmBuf, 0, charMatchCount);
        }
        out.flush();
    }

    private void readValidStream(OutputStream out, COSNumber streamLengthObj) throws IOException {
        int readBytes;
        for (long remainBytes = streamLengthObj.longValue(); remainBytes > 0L; remainBytes -= (long)readBytes) {
            int chunk = remainBytes > 8192L ? 8192 : (int)remainBytes;
            readBytes = this.source.read(this.streamCopyBuf, 0, chunk);
            if (readBytes <= 0) {
                throw new IOException("read error at offset " + this.source.getPosition() + ": expected " + chunk + " bytes, but read() returns " + readBytes);
            }
            out.write(this.streamCopyBuf, 0, readBytes);
        }
    }

    private boolean validateStreamLength(long streamLength) throws IOException {
        boolean streamLengthIsValid = true;
        long originOffset = this.source.getPosition();
        long expectedEndOfStream = originOffset + streamLength;
        if (expectedEndOfStream > this.fileLen) {
            streamLengthIsValid = false;
            LOG.warn((Object)("The end of the stream is out of range, using workaround to read the stream, stream start position: " + originOffset + ", length: " + streamLength + ", expected end position: " + expectedEndOfStream));
        } else {
            this.source.seek(expectedEndOfStream);
            this.skipSpaces();
            if (!this.isString(ENDSTREAM)) {
                streamLengthIsValid = false;
                LOG.warn((Object)("The end of the stream doesn't point to the correct offset, using workaround to read the stream, stream start position: " + originOffset + ", length: " + streamLength + ", expected end position: " + expectedEndOfStream));
            }
            this.source.seek(originOffset);
        }
        return streamLengthIsValid;
    }

    private long checkXRefOffset(long startXRefOffset) throws IOException {
        if (!this.isLenient) {
            return startXRefOffset;
        }
        this.source.seek(startXRefOffset);
        this.skipSpaces();
        if (this.source.peek() == 120 && this.isString(XREF_TABLE)) {
            return startXRefOffset;
        }
        if (startXRefOffset > 0L) {
            if (this.checkXRefStreamOffset(startXRefOffset)) {
                return startXRefOffset;
            }
            return this.calculateXRefFixedOffset(startXRefOffset, false);
        }
        return -1L;
    }

    private boolean checkXRefStreamOffset(long startXRefOffset) throws IOException {
        if (!this.isLenient || startXRefOffset == 0L) {
            return true;
        }
        this.source.seek(startXRefOffset - 1L);
        int nextValue = this.source.read();
        if (this.isWhitespace(nextValue)) {
            this.skipSpaces();
            if (this.isDigit()) {
                try {
                    this.readObjectNumber();
                    this.readGenerationNumber();
                    this.readExpectedString(OBJ_MARKER, true);
                    COSDictionary dict = this.parseCOSDictionary();
                    this.source.seek(startXRefOffset);
                    if ("XRef".equals(dict.getNameAsString(COSName.TYPE))) {
                        return true;
                    }
                }
                catch (IOException exception) {
                    this.source.seek(startXRefOffset);
                }
            }
        }
        return false;
    }

    private long calculateXRefFixedOffset(long objectOffset, boolean streamsOnly) throws IOException {
        if (objectOffset < 0L) {
            LOG.error((Object)("Invalid object offset " + objectOffset + " when searching for a xref table/stream"));
            return 0L;
        }
        long newOffset = this.bfSearchForXRef(objectOffset, streamsOnly);
        if (newOffset > -1L) {
            LOG.debug((Object)("Fixed reference for xref table/stream " + objectOffset + " -> " + newOffset));
            return newOffset;
        }
        LOG.error((Object)("Can't find the object xref table/stream at offset " + objectOffset));
        return 0L;
    }

    private boolean validateXrefOffsets(Map<COSObjectKey, Long> xrefOffset) throws IOException {
        if (xrefOffset == null) {
            return true;
        }
        HashMap<COSObjectKey, COSObjectKey> correctedKeys = new HashMap<COSObjectKey, COSObjectKey>();
        HashSet<COSObjectKey> validKeys = new HashSet<COSObjectKey>();
        for (Map.Entry<COSObjectKey, Long> objectEntry : xrefOffset.entrySet()) {
            COSObjectKey objectKey = objectEntry.getKey();
            Long objectOffset = objectEntry.getValue();
            if (objectOffset == null || objectOffset < 0L) continue;
            COSObjectKey foundObjectKey = this.findObjectKey(objectKey, objectOffset, xrefOffset);
            if (foundObjectKey == null) {
                LOG.debug((Object)("Stop checking xref offsets as at least one (" + objectKey + ") couldn't be dereferenced"));
                return false;
            }
            if (foundObjectKey != objectKey) {
                correctedKeys.put(objectKey, foundObjectKey);
                continue;
            }
            validKeys.add(objectKey);
        }
        HashMap correctedPointers = new HashMap();
        for (Map.Entry correctedKeyEntry : correctedKeys.entrySet()) {
            if (validKeys.contains(correctedKeyEntry.getValue())) continue;
            correctedPointers.put(correctedKeyEntry.getValue(), xrefOffset.get(correctedKeyEntry.getKey()));
        }
        for (Map.Entry correctedKeyEntry : correctedKeys.entrySet()) {
            xrefOffset.remove(correctedKeyEntry.getKey());
        }
        for (Map.Entry pointer : correctedPointers.entrySet()) {
            xrefOffset.put((COSObjectKey)pointer.getKey(), (Long)pointer.getValue());
        }
        return true;
    }

    private void checkXrefOffsets() throws IOException {
        if (!this.isLenient) {
            return;
        }
        Map<COSObjectKey, Long> xrefOffset = this.xrefTrailerResolver.getXrefTable();
        if (!this.validateXrefOffsets(xrefOffset)) {
            this.bfSearchForObjects();
            if (!this.bfSearchCOSObjectKeyOffsets.isEmpty()) {
                LOG.debug((Object)"Replaced read xref table with the results of a brute force search");
                xrefOffset.clear();
                xrefOffset.putAll(this.bfSearchCOSObjectKeyOffsets);
            }
        }
    }

    private COSObjectKey findObjectKey(COSObjectKey objectKey, long offset, Map<COSObjectKey, Long> xrefOffset) throws IOException {
        if (offset < 6L) {
            return null;
        }
        try {
            this.source.seek(offset);
            this.skipWhiteSpaces();
            if (this.source.getPosition() == offset) {
                this.source.seek(offset - 1L);
                if (this.source.getPosition() < offset) {
                    if (!this.isDigit()) {
                        this.source.read();
                    } else {
                        int newGenNr;
                        long current = this.source.getPosition();
                        this.source.seek(--current);
                        while (this.isDigit()) {
                            this.source.seek(--current);
                        }
                        long newObjNr = this.readObjectNumber();
                        COSObjectKey newObjKey = new COSObjectKey(newObjNr, newGenNr = this.readGenerationNumber());
                        Long existingOffset = xrefOffset.get(newObjKey);
                        if (existingOffset != null && existingOffset > 0L && Math.abs(offset - existingOffset) < 10L) {
                            LOG.debug((Object)("Found the object " + newObjKey + " instead of " + objectKey + " at offset " + offset + " - ignoring"));
                            return null;
                        }
                        this.source.seek(offset);
                    }
                }
            }
            long foundObjectNumber = this.readObjectNumber();
            if (objectKey.getNumber() != foundObjectNumber) {
                LOG.warn((Object)("found wrong object number. expected [" + objectKey.getNumber() + "] found [" + foundObjectNumber + "]"));
                if (!this.isLenient) {
                    return null;
                }
                objectKey = new COSObjectKey(foundObjectNumber, objectKey.getGeneration());
            }
            int genNumber = this.readGenerationNumber();
            this.readExpectedString(OBJ_MARKER, true);
            if (genNumber == objectKey.getGeneration()) {
                return objectKey;
            }
            if (this.isLenient && genNumber > objectKey.getGeneration()) {
                return new COSObjectKey(objectKey.getNumber(), genNumber);
            }
        }
        catch (IOException exception) {
            LOG.debug((Object)("No valid object at given location " + offset + " - ignoring"), (Throwable)exception);
        }
        return null;
    }

    private void bfSearchForObjects() throws IOException {
        if (this.bfSearchCOSObjectKeyOffsets == null) {
            this.bfSearchForLastEOFMarker();
            this.bfSearchCOSObjectKeyOffsets = new HashMap<COSObjectKey, Long>();
            long originOffset = this.source.getPosition();
            long currentOffset = 6L;
            long lastObjectId = Long.MIN_VALUE;
            int lastGenID = Integer.MIN_VALUE;
            long lastObjOffset = Long.MIN_VALUE;
            char[] endobjString = "ndo".toCharArray();
            char[] endobjRemainingString = "bj".toCharArray();
            boolean endOfObjFound = false;
            do {
                this.source.seek(currentOffset);
                int nextChar = this.source.read();
                ++currentOffset;
                if (this.isWhitespace(nextChar) && this.isString(OBJ_MARKER)) {
                    long tempOffset = currentOffset - 2L;
                    this.source.seek(tempOffset);
                    int genID = this.source.peek();
                    if (!COSParser.isDigit(genID)) continue;
                    genID -= 48;
                    this.source.seek(--tempOffset);
                    if (!this.isWhitespace()) continue;
                    while (tempOffset > 6L && this.isWhitespace()) {
                        this.source.seek(--tempOffset);
                    }
                    boolean objectIDFound = false;
                    while (tempOffset > 6L && this.isDigit()) {
                        this.source.seek(--tempOffset);
                        objectIDFound = true;
                    }
                    if (!objectIDFound) continue;
                    this.source.read();
                    long objectId = this.readObjectNumber();
                    if (lastObjOffset > 0L) {
                        this.bfSearchCOSObjectKeyOffsets.put(new COSObjectKey(lastObjectId, lastGenID), lastObjOffset);
                    }
                    lastObjectId = objectId;
                    lastGenID = genID;
                    lastObjOffset = tempOffset + 1L;
                    currentOffset += (long)(OBJ_MARKER.length - 1);
                    endOfObjFound = false;
                    continue;
                }
                if (nextChar != 101 || !this.isString(endobjString)) continue;
                this.source.seek(currentOffset += (long)endobjString.length);
                if (this.source.isEOF()) {
                    endOfObjFound = true;
                    continue;
                }
                if (!this.isString(endobjRemainingString)) continue;
                currentOffset += (long)endobjRemainingString.length;
                endOfObjFound = true;
            } while (currentOffset < this.lastEOFMarker && !this.source.isEOF());
            if ((this.lastEOFMarker < Long.MAX_VALUE || endOfObjFound) && lastObjOffset > 0L) {
                this.bfSearchCOSObjectKeyOffsets.put(new COSObjectKey(lastObjectId, lastGenID), lastObjOffset);
            }
            this.source.seek(originOffset);
        }
    }

    private long bfSearchForXRef(long xrefOffset, boolean streamsOnly) throws IOException {
        long newOffset = -1L;
        long newOffsetTable = -1L;
        long newOffsetStream = -1L;
        if (!streamsOnly) {
            this.bfSearchForXRefTables();
        }
        this.bfSearchForXRefStreams();
        if (!streamsOnly && this.bfSearchXRefTablesOffsets != null) {
            newOffsetTable = this.searchNearestValue(this.bfSearchXRefTablesOffsets, xrefOffset);
        }
        if (this.bfSearchXRefStreamsOffsets != null) {
            newOffsetStream = this.searchNearestValue(this.bfSearchXRefStreamsOffsets, xrefOffset);
        }
        if (newOffsetTable > -1L && newOffsetStream > -1L) {
            long differenceTable = xrefOffset - newOffsetTable;
            long differenceStream = xrefOffset - newOffsetStream;
            if (Math.abs(differenceTable) > Math.abs(differenceStream)) {
                newOffset = newOffsetStream;
                this.bfSearchXRefStreamsOffsets.remove(newOffsetStream);
            } else {
                newOffset = newOffsetTable;
                this.bfSearchXRefTablesOffsets.remove(newOffsetTable);
            }
        } else if (newOffsetTable > -1L) {
            newOffset = newOffsetTable;
            this.bfSearchXRefTablesOffsets.remove(newOffsetTable);
        } else if (newOffsetStream > -1L) {
            newOffset = newOffsetStream;
            this.bfSearchXRefStreamsOffsets.remove(newOffsetStream);
        }
        return newOffset;
    }

    private long searchNearestValue(List<Long> values, long offset) {
        long newValue = -1L;
        Long currentDifference = null;
        int currentOffsetIndex = -1;
        int numberOfOffsets = values.size();
        for (int i = 0; i < numberOfOffsets; ++i) {
            long newDifference = offset - values.get(i);
            if (currentDifference != null && Math.abs(currentDifference) <= Math.abs(newDifference)) continue;
            currentDifference = newDifference;
            currentOffsetIndex = i;
        }
        if (currentOffsetIndex > -1) {
            newValue = values.get(currentOffsetIndex);
        }
        return newValue;
    }

    private boolean bfSearchForTrailer(COSDictionary trailer) throws IOException {
        long originOffset = this.source.getPosition();
        this.source.seek(6L);
        while (!this.source.isEOF()) {
            if (this.isString(TRAILER_MARKER)) {
                this.source.seek(this.source.getPosition() + (long)TRAILER_MARKER.length);
                try {
                    COSDictionary infoDict;
                    COSObject infoObj;
                    COSDictionary rootDict;
                    boolean rootFound = false;
                    boolean infoFound = false;
                    this.skipSpaces();
                    COSDictionary trailerDict = this.parseCOSDictionary();
                    COSObject rootObj = trailerDict.getCOSObject(COSName.ROOT);
                    if (rootObj != null && (rootDict = this.retrieveCOSDictionary(rootObj)) != null && this.isCatalog(rootDict)) {
                        rootFound = true;
                    }
                    if ((infoObj = trailerDict.getCOSObject(COSName.INFO)) != null && (infoDict = this.retrieveCOSDictionary(infoObj)) != null && this.isInfo(infoDict)) {
                        infoFound = true;
                    }
                    if (rootFound && infoFound) {
                        COSBase idObj;
                        COSDictionary encDict;
                        COSObject encObj;
                        trailer.setItem(COSName.ROOT, (COSBase)rootObj);
                        trailer.setItem(COSName.INFO, (COSBase)infoObj);
                        if (trailerDict.containsKey(COSName.ENCRYPT) && (encObj = trailerDict.getCOSObject(COSName.ENCRYPT)) != null && (encDict = this.retrieveCOSDictionary(encObj)) != null) {
                            trailer.setItem(COSName.ENCRYPT, (COSBase)encObj);
                        }
                        if (trailerDict.containsKey(COSName.ID) && (idObj = trailerDict.getItem(COSName.ID)) instanceof COSArray) {
                            trailer.setItem(COSName.ID, idObj);
                        }
                        return true;
                    }
                }
                catch (IOException exception) {
                    continue;
                }
            }
            this.source.read();
        }
        this.source.seek(originOffset);
        return false;
    }

    private void bfSearchForLastEOFMarker() throws IOException {
        if (this.lastEOFMarker == null) {
            long originOffset = this.source.getPosition();
            this.source.seek(6L);
            while (!this.source.isEOF()) {
                if (this.isString(EOF_MARKER)) {
                    long tempMarker = this.source.getPosition();
                    this.source.seek(tempMarker + 5L);
                    try {
                        this.skipSpaces();
                        if (!this.isString(XREF_TABLE)) {
                            this.readObjectNumber();
                            this.readGenerationNumber();
                        }
                    }
                    catch (IOException exception) {
                        this.lastEOFMarker = tempMarker;
                    }
                }
                this.source.read();
            }
            this.source.seek(originOffset);
            if (this.lastEOFMarker == null) {
                this.lastEOFMarker = Long.MAX_VALUE;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void bfSearchForObjStreams() throws IOException {
        HashMap<Long, COSObjectKey> bfSearchObjStreamsOffsets = new HashMap<Long, COSObjectKey>();
        long originOffset = this.source.getPosition();
        this.source.seek(6L);
        char[] string = " obj".toCharArray();
        while (!this.source.isEOF()) {
            if (this.isString(OBJ_STREAM)) {
                long currentPosition = this.source.getPosition();
                long newOffset = -1L;
                boolean objFound = false;
                block7: for (int i = 1; i < 40 && !objFound; ++i) {
                    long currentOffset = currentPosition - (long)(i * 10);
                    if (currentOffset <= 0L) continue;
                    this.source.seek(currentOffset);
                    for (int j = 0; j < 10; ++j) {
                        if (this.isString(string)) {
                            long tempOffset = currentOffset - 1L;
                            this.source.seek(tempOffset);
                            int genID = this.source.peek();
                            if (COSParser.isDigit(genID)) {
                                this.source.seek(--tempOffset);
                                if (this.isSpace()) {
                                    int length = 0;
                                    this.source.seek(--tempOffset);
                                    while (tempOffset > 6L && this.isDigit()) {
                                        this.source.seek(--tempOffset);
                                        ++length;
                                    }
                                    if (length > 0) {
                                        this.source.read();
                                        newOffset = this.source.getPosition();
                                        long objNumber = this.readObjectNumber();
                                        int genNumber = this.readGenerationNumber();
                                        COSObjectKey streamObjectKey = new COSObjectKey(objNumber, genNumber);
                                        bfSearchObjStreamsOffsets.put(newOffset, streamObjectKey);
                                    }
                                }
                            }
                            LOG.debug((Object)("Dictionary start for object stream -> " + newOffset));
                            objFound = true;
                            continue block7;
                        }
                        ++currentOffset;
                        this.source.read();
                    }
                }
                this.source.seek(currentPosition + (long)OBJ_STREAM.length);
            }
            this.source.read();
        }
        for (Long offset : bfSearchObjStreamsOffsets.keySet()) {
            Long bfOffset = this.bfSearchCOSObjectKeyOffsets.get(bfSearchObjStreamsOffsets.get(offset));
            if (bfOffset == null) {
                LOG.warn((Object)("Skipped incomplete object stream:" + bfSearchObjStreamsOffsets.get(offset) + " at " + offset));
                continue;
            }
            if (!offset.equals(bfOffset)) continue;
            this.source.seek(offset);
            long stmObjNumber = this.readObjectNumber();
            int stmGenNumber = this.readGenerationNumber();
            this.readExpectedString(OBJ_MARKER, true);
            int nrOfObjects = 0;
            COSStream stream = null;
            ArrayList<Long> objectNumbers = null;
            try {
                COSDictionary dict = this.parseCOSDictionary();
                int offsetFirstStream = dict.getInt(COSName.FIRST);
                nrOfObjects = dict.getInt(COSName.N);
                if (offsetFirstStream == -1 || nrOfObjects == -1) continue;
                stream = this.parseCOSStream(dict);
                if (this.securityHandler != null) {
                    this.securityHandler.decryptStream(stream, stmObjNumber, stmGenNumber);
                }
                PDFObjectStreamParser strmParser = new PDFObjectStreamParser(stream, this.document);
                objectNumbers = new ArrayList<Long>();
                for (int i = 0; i < nrOfObjects; ++i) {
                    objectNumbers.add(strmParser.readObjectNumber());
                    strmParser.readLong();
                }
            }
            catch (IOException exception) {
                LOG.debug((Object)("Skipped corrupt stream: (" + stmObjNumber + " 0 at offset " + offset));
                continue;
            }
            finally {
                if (stream == null) continue;
                stream.close();
                continue;
            }
            if (objectNumbers.size() < nrOfObjects) {
                LOG.debug((Object)("Skipped corrupt stream: (" + stmObjNumber + " 0 at offset " + offset));
                continue;
            }
            Map<COSObjectKey, Long> xrefOffset = this.xrefTrailerResolver.getXrefTable();
            for (Long objNumber : objectNumbers) {
                COSObjectKey objKey = new COSObjectKey(objNumber, 0);
                Long existingOffset = this.bfSearchCOSObjectKeyOffsets.get(objKey);
                if (existingOffset != null && existingOffset < 0L) {
                    COSObjectKey objStmKey = new COSObjectKey(Math.abs(existingOffset), 0);
                    existingOffset = this.bfSearchCOSObjectKeyOffsets.get(objStmKey);
                }
                if (existingOffset != null && offset <= existingOffset) continue;
                this.bfSearchCOSObjectKeyOffsets.put(objKey, -stmObjNumber);
                xrefOffset.put(objKey, -stmObjNumber);
            }
        }
        this.source.seek(originOffset);
    }

    private void bfSearchForXRefTables() throws IOException {
        if (this.bfSearchXRefTablesOffsets == null) {
            this.bfSearchXRefTablesOffsets = new ArrayList<Long>();
            long originOffset = this.source.getPosition();
            this.source.seek(6L);
            while (!this.source.isEOF()) {
                if (this.isString(XREF_TABLE)) {
                    long newOffset = this.source.getPosition();
                    this.source.seek(newOffset - 1L);
                    if (this.isWhitespace()) {
                        this.bfSearchXRefTablesOffsets.add(newOffset);
                    }
                    this.source.seek(newOffset + 4L);
                }
                this.source.read();
            }
            this.source.seek(originOffset);
        }
    }

    private void bfSearchForXRefStreams() throws IOException {
        if (this.bfSearchXRefStreamsOffsets == null) {
            this.bfSearchXRefStreamsOffsets = new ArrayList<Long>();
            long originOffset = this.source.getPosition();
            this.source.seek(6L);
            String objString = " obj";
            char[] string = objString.toCharArray();
            while (!this.source.isEOF()) {
                if (this.isString(XREF_STREAM)) {
                    long newOffset = -1L;
                    long xrefOffset = this.source.getPosition();
                    boolean objFound = false;
                    block1: for (int i = 1; i < 40 && !objFound; ++i) {
                        long currentOffset = xrefOffset - (long)(i * 10);
                        if (currentOffset <= 0L) continue;
                        this.source.seek(currentOffset);
                        for (int j = 0; j < 10; ++j) {
                            if (this.isString(string)) {
                                long tempOffset = currentOffset - 1L;
                                this.source.seek(tempOffset);
                                int genID = this.source.peek();
                                if (COSParser.isDigit(genID)) {
                                    this.source.seek(--tempOffset);
                                    if (this.isSpace()) {
                                        int length = 0;
                                        this.source.seek(--tempOffset);
                                        while (tempOffset > 6L && this.isDigit()) {
                                            this.source.seek(--tempOffset);
                                            ++length;
                                        }
                                        if (length > 0) {
                                            this.source.read();
                                            newOffset = this.source.getPosition();
                                        }
                                    }
                                }
                                LOG.debug((Object)("Fixed reference for xref stream " + xrefOffset + " -> " + newOffset));
                                objFound = true;
                                continue block1;
                            }
                            ++currentOffset;
                            this.source.read();
                        }
                    }
                    if (newOffset > -1L) {
                        this.bfSearchXRefStreamsOffsets.add(newOffset);
                    }
                    this.source.seek(xrefOffset + 5L);
                }
                this.source.read();
            }
            this.source.seek(originOffset);
        }
    }

    protected final COSDictionary rebuildTrailer() throws IOException {
        COSDictionary trailer = null;
        this.bfSearchForObjects();
        if (this.bfSearchCOSObjectKeyOffsets != null) {
            this.xrefTrailerResolver.reset();
            this.xrefTrailerResolver.nextXrefObj(0L, XrefTrailerResolver.XRefType.TABLE);
            for (Map.Entry<COSObjectKey, Long> entry : this.bfSearchCOSObjectKeyOffsets.entrySet()) {
                this.xrefTrailerResolver.setXRef(entry.getKey(), entry.getValue());
            }
            this.xrefTrailerResolver.setStartxref(0L);
            trailer = this.xrefTrailerResolver.getTrailer();
            this.getDocument().setTrailer(trailer);
            boolean searchForObjStreamsDone = false;
            if (!this.bfSearchForTrailer(trailer) && !this.searchForTrailerItems(trailer)) {
                this.bfSearchForObjStreams();
                searchForObjStreamsDone = true;
                this.searchForTrailerItems(trailer);
            }
            this.prepareDecryption();
            if (!searchForObjStreamsDone) {
                this.bfSearchForObjStreams();
            }
        }
        this.trailerWasRebuild = true;
        return trailer;
    }

    private boolean searchForTrailerItems(COSDictionary trailer) throws IOException {
        COSObject rootObject = null;
        Long rootOffset = null;
        COSObject infoObject = null;
        Long infoOffset = null;
        for (Map.Entry<COSObjectKey, Long> entry : this.bfSearchCOSObjectKeyOffsets.entrySet()) {
            COSObject cosObject;
            COSDictionary dictionary = this.retrieveCOSDictionary(entry.getKey(), entry.getValue());
            if (dictionary == null) continue;
            if (this.isCatalog(dictionary)) {
                cosObject = this.document.getObjectFromPool(entry.getKey());
                rootObject = this.compareCOSObjects(cosObject, entry.getValue(), rootObject, rootOffset);
                if (rootObject != cosObject) continue;
                rootOffset = entry.getValue();
                continue;
            }
            if (!this.isInfo(dictionary) || (infoObject = this.compareCOSObjects(cosObject = this.document.getObjectFromPool(entry.getKey()), entry.getValue(), infoObject, infoOffset)) != cosObject) continue;
            infoOffset = entry.getValue();
        }
        if (rootObject != null) {
            trailer.setItem(COSName.ROOT, rootObject);
        }
        if (infoObject != null) {
            trailer.setItem(COSName.INFO, infoObject);
        }
        return rootObject != null;
    }

    private COSObject compareCOSObjects(COSObject newObject, Long newOffset, COSObject currentObject, Long currentOffset) {
        if (currentObject != null) {
            if (currentObject.getObjectNumber() == newObject.getObjectNumber()) {
                return currentObject.getGenerationNumber() < newObject.getGenerationNumber() ? newObject : currentObject;
            }
            return currentOffset != null && newOffset > currentOffset ? newObject : currentObject;
        }
        return newObject;
    }

    private COSDictionary retrieveCOSDictionary(COSObject object) throws IOException {
        COSObjectKey key = new COSObjectKey(object);
        Long offset = this.bfSearchCOSObjectKeyOffsets.get(key);
        if (offset != null) {
            long currentPosition = this.source.getPosition();
            COSDictionary dictionary = this.retrieveCOSDictionary(key, offset);
            this.source.seek(currentPosition);
            return dictionary;
        }
        return null;
    }

    private COSDictionary retrieveCOSDictionary(COSObjectKey key, long offset) throws IOException {
        COSDictionary dictionary = null;
        if (offset < 0L) {
            COSBase baseObject;
            COSObject compressedObject = this.document.getObjectFromPool(key);
            if (compressedObject.getObject() == null) {
                this.parseObjectStream((int)(-offset));
            }
            if ((baseObject = compressedObject.getObject()) instanceof COSDictionary) {
                dictionary = (COSDictionary)baseObject;
            }
        } else {
            this.source.seek(offset);
            this.readObjectNumber();
            this.readGenerationNumber();
            this.readExpectedString(OBJ_MARKER, true);
            if (this.source.peek() != 60) {
                return null;
            }
            try {
                dictionary = this.parseCOSDictionary();
            }
            catch (IOException exception) {
                LOG.debug((Object)("Skipped object " + key + ", either it's corrupt or not a dictionary"));
            }
        }
        return dictionary;
    }

    protected void checkPages(COSDictionary root) {
        COSBase pages;
        if (this.trailerWasRebuild && root != null && (pages = root.getDictionaryObject(COSName.PAGES)) instanceof COSDictionary) {
            this.checkPagesDictionary((COSDictionary)pages, new HashSet<COSObject>());
        }
    }

    private int checkPagesDictionary(COSDictionary pagesDict, Set<COSObject> set) {
        COSBase kids = pagesDict.getDictionaryObject(COSName.KIDS);
        int numberOfPages = 0;
        if (kids instanceof COSArray) {
            COSArray kidsArray = (COSArray)kids;
            List<? extends COSBase> kidsList = kidsArray.toList();
            for (COSBase cOSBase : kidsList) {
                if (!(cOSBase instanceof COSObject) || set.contains((COSObject)cOSBase)) {
                    kidsArray.remove(cOSBase);
                    continue;
                }
                COSObject kidObject = (COSObject)cOSBase;
                COSBase kidBaseobject = kidObject.getObject();
                if (kidBaseobject == null || kidBaseobject.equals(COSNull.NULL)) {
                    LOG.warn((Object)("Removed null object " + cOSBase + " from pages dictionary"));
                    kidsArray.remove(cOSBase);
                    continue;
                }
                if (!(kidBaseobject instanceof COSDictionary)) continue;
                COSDictionary kidDictionary = (COSDictionary)kidBaseobject;
                COSName type = kidDictionary.getCOSName(COSName.TYPE);
                if (COSName.PAGES.equals(type)) {
                    set.add(kidObject);
                    numberOfPages += this.checkPagesDictionary(kidDictionary, set);
                    continue;
                }
                if (!COSName.PAGE.equals(type)) continue;
                ++numberOfPages;
            }
        }
        pagesDict.setInt(COSName.COUNT, numberOfPages);
        return numberOfPages;
    }

    protected boolean isCatalog(COSDictionary dictionary) {
        return COSName.CATALOG.equals(dictionary.getCOSName(COSName.TYPE));
    }

    private boolean isInfo(COSDictionary dictionary) {
        if (dictionary.containsKey(COSName.PARENT) || dictionary.containsKey(COSName.A) || dictionary.containsKey(COSName.DEST)) {
            return false;
        }
        return dictionary.containsKey(COSName.MOD_DATE) || dictionary.containsKey(COSName.TITLE) || dictionary.containsKey(COSName.AUTHOR) || dictionary.containsKey(COSName.SUBJECT) || dictionary.containsKey(COSName.KEYWORDS) || dictionary.containsKey(COSName.CREATOR) || dictionary.containsKey(COSName.PRODUCER) || dictionary.containsKey(COSName.CREATION_DATE);
    }

    private long parseStartXref() throws IOException {
        long startXref = -1L;
        if (this.isString(STARTXREF)) {
            this.readString();
            this.skipSpaces();
            startXref = this.readLong();
        }
        return startXref;
    }

    private boolean isString(byte[] string) throws IOException {
        boolean bytesMatching = false;
        if (this.source.peek() == string[0]) {
            int numberOfBytes;
            int readMore;
            int length = string.length;
            byte[] bytesRead = new byte[length];
            for (numberOfBytes = this.source.read(bytesRead, 0, length); numberOfBytes < length && (readMore = this.source.read(bytesRead, numberOfBytes, length - numberOfBytes)) >= 0; numberOfBytes += readMore) {
            }
            bytesMatching = Arrays.equals(string, bytesRead);
            this.source.rewind(numberOfBytes);
        }
        return bytesMatching;
    }

    private boolean isString(char[] string) throws IOException {
        boolean bytesMatching = true;
        long originOffset = this.source.getPosition();
        for (char c : string) {
            if (this.source.read() == c) continue;
            bytesMatching = false;
            break;
        }
        this.source.seek(originOffset);
        return bytesMatching;
    }

    private boolean parseTrailer() throws IOException {
        this.trailerOffset = this.source.getPosition();
        if (this.isLenient) {
            int nextCharacter = this.source.peek();
            while (nextCharacter != 116 && COSParser.isDigit(nextCharacter)) {
                if (this.source.getPosition() == this.trailerOffset) {
                    LOG.warn((Object)("Expected trailer object at offset " + this.trailerOffset + ", keep trying"));
                }
                this.readLine();
                nextCharacter = this.source.peek();
            }
        }
        if (this.source.peek() != 116) {
            return false;
        }
        long currentOffset = this.source.getPosition();
        String nextLine = this.readLine();
        if (!nextLine.trim().equals("trailer")) {
            if (nextLine.startsWith("trailer")) {
                int len = "trailer".length();
                this.source.seek(currentOffset + (long)len);
            } else {
                return false;
            }
        }
        this.skipSpaces();
        COSDictionary parsedTrailer = this.parseCOSDictionary();
        this.xrefTrailerResolver.setTrailer(parsedTrailer);
        this.skipSpaces();
        return true;
    }

    protected boolean parsePDFHeader() throws IOException {
        return this.parseHeader(PDF_HEADER, PDF_DEFAULT_VERSION);
    }

    protected boolean parseFDFHeader() throws IOException {
        return this.parseHeader(FDF_HEADER, FDF_DEFAULT_VERSION);
    }

    private boolean parseHeader(String headerMarker, String defaultVersion) throws IOException {
        String header = this.readLine();
        if (!header.contains(headerMarker)) {
            header = this.readLine();
            while (!(header.contains(headerMarker) || header.length() > 0 && Character.isDigit(header.charAt(0)))) {
                header = this.readLine();
            }
        }
        if (!header.contains(headerMarker)) {
            this.source.seek(0L);
            return false;
        }
        int headerStart = header.indexOf(headerMarker);
        if (headerStart > 0) {
            header = header.substring(headerStart);
        }
        if (header.startsWith(headerMarker) && !header.matches(headerMarker + "\\d.\\d")) {
            if (header.length() < headerMarker.length() + 3) {
                header = headerMarker + defaultVersion;
                LOG.debug((Object)("No version found, set to " + defaultVersion + " as default."));
            } else {
                String headerGarbage = header.substring(headerMarker.length() + 3, header.length()) + "\n";
                header = header.substring(0, headerMarker.length() + 3);
                this.source.rewind(headerGarbage.getBytes(Charsets.ISO_8859_1).length);
            }
        }
        float headerVersion = -1.0f;
        try {
            String[] headerParts = header.split("-");
            if (headerParts.length == 2) {
                headerVersion = Float.parseFloat(headerParts[1]);
            }
        }
        catch (NumberFormatException exception) {
            LOG.debug((Object)"Can't parse the header version.", (Throwable)exception);
        }
        if (headerVersion < 0.0f) {
            if (this.isLenient) {
                headerVersion = 1.7f;
            } else {
                throw new IOException("Error getting header version: " + header);
            }
        }
        this.document.setVersion(headerVersion);
        this.source.seek(0L);
        return true;
    }

    /*
     * Unable to fully structure code
     */
    protected boolean parseXrefTable(long startByteOffset) throws IOException {
        if (this.source.peek() != 120) {
            return false;
        }
        xref = this.readString();
        if (!xref.trim().equals("xref")) {
            return false;
        }
        str = this.readString();
        b = str.getBytes(Charsets.ISO_8859_1);
        this.source.rewind(b.length);
        this.xrefTrailerResolver.nextXrefObj(startByteOffset, XrefTrailerResolver.XRefType.TABLE);
        if (str.startsWith("trailer")) {
            COSParser.LOG.warn((Object)"skipping empty xref table");
            return false;
        }
        do {
            if ((splitString = (currentLine = this.readLine()).split("\\s")).length != 2) {
                COSParser.LOG.warn((Object)("Unexpected XRefTable Entry: " + currentLine));
                return false;
            }
            try {
                currObjID = Long.parseLong(splitString[0]);
            }
            catch (NumberFormatException exception) {
                COSParser.LOG.warn((Object)("XRefTable: invalid ID for the first object: " + currentLine));
                return false;
            }
            count = 0;
            try {
                count = Integer.parseInt(splitString[1]);
            }
            catch (NumberFormatException exception) {
                COSParser.LOG.warn((Object)("XRefTable: invalid number of objects: " + currentLine));
                return false;
            }
            this.skipSpaces();
            for (i = 0; i < count && !this.source.isEOF() && !this.isEndOfName((char)this.source.peek()) && this.source.peek() != 116; ++i) {
                currentLine = this.readLine();
                splitString = currentLine.split("\\s");
                if (splitString.length < 3) {
                    COSParser.LOG.warn((Object)("invalid xref line: " + currentLine));
                    break;
                }
                if (splitString[splitString.length - 1].equals("n")) {
                    try {
                        currOffset = Long.parseLong(splitString[0]);
                        if (currOffset <= 0L) ** GOTO lbl49
                        currGenID = Integer.parseInt(splitString[1]);
                        objKey = new COSObjectKey(currObjID, currGenID);
                        this.xrefTrailerResolver.setXRef(objKey, currOffset);
                    }
                    catch (NumberFormatException e) {
                        throw new IOException(e);
                    }
                } else if (!splitString[2].equals("f")) {
                    throw new IOException("Corrupt XRefTable Entry - ObjID:" + currObjID);
                }
lbl49:
                // 4 sources

                ++currObjID;
                this.skipSpaces();
            }
            this.skipSpaces();
        } while (this.isDigit());
        return true;
    }

    private void parseXrefStream(COSStream stream, long objByteOffset, boolean isStandalone) throws IOException {
        if (isStandalone) {
            this.xrefTrailerResolver.nextXrefObj(objByteOffset, XrefTrailerResolver.XRefType.STREAM);
            this.xrefTrailerResolver.setTrailer(stream);
        }
        PDFXrefStreamParser parser = new PDFXrefStreamParser(stream, this.document, this.xrefTrailerResolver);
        parser.parse();
    }

    public COSDocument getDocument() throws IOException {
        if (this.document == null) {
            throw new IOException("You must parse the document first before calling getDocument()");
        }
        return this.document;
    }

    public PDEncryption getEncryption() throws IOException {
        if (this.document == null) {
            throw new IOException("You must parse the document first before calling getEncryption()");
        }
        return this.encryption;
    }

    public AccessPermission getAccessPermission() throws IOException {
        if (this.document == null) {
            throw new IOException("You must parse the document first before calling getAccessPermission()");
        }
        return this.accessPermission;
    }

    protected COSBase parseTrailerValuesDynamically(COSDictionary trailer) throws IOException {
        for (COSBase trailerEntry : trailer.getValues()) {
            if (!(trailerEntry instanceof COSObject)) continue;
            COSObject tmpObj = (COSObject)trailerEntry;
            this.parseObjectDynamically(tmpObj, false);
        }
        COSObject root = trailer.getCOSObject(COSName.ROOT);
        if (root == null) {
            throw new IOException("Missing root object specification in trailer.");
        }
        return root.getObject();
    }

    private void prepareDecryption() throws IOException {
        if (this.encryption != null) {
            return;
        }
        COSBase trailerEncryptItem = this.document.getTrailer().getItem(COSName.ENCRYPT);
        if (trailerEncryptItem == null || trailerEncryptItem instanceof COSNull) {
            return;
        }
        if (trailerEncryptItem instanceof COSObject) {
            COSObject trailerEncryptObj = (COSObject)trailerEncryptItem;
            this.parseDictionaryRecursive(trailerEncryptObj);
        }
        try {
            DecryptionMaterial decryptionMaterial;
            this.encryption = new PDEncryption(this.document.getEncryptionDictionary());
            if (this.keyStoreInputStream != null) {
                KeyStore ks = KeyStore.getInstance("PKCS12");
                ks.load(this.keyStoreInputStream, this.password.toCharArray());
                decryptionMaterial = new PublicKeyDecryptionMaterial(ks, this.keyAlias, this.password);
            } else {
                decryptionMaterial = new StandardDecryptionMaterial(this.password);
            }
            this.securityHandler = this.encryption.getSecurityHandler();
            this.securityHandler.prepareForDecryption(this.encryption, this.document.getDocumentID(), decryptionMaterial);
            this.accessPermission = this.securityHandler.getCurrentAccessPermission();
        }
        catch (IOException e) {
            throw e;
        }
        catch (Exception e) {
            throw new IOException("Error (" + e.getClass().getSimpleName() + ") while creating security handler for decryption", e);
        }
        finally {
            if (this.keyStoreInputStream != null) {
                IOUtils.closeQuietly(this.keyStoreInputStream);
            }
        }
    }

    private void parseDictionaryRecursive(COSObject dictionaryObject) throws IOException {
        this.parseObjectDynamically(dictionaryObject, true);
        if (!(dictionaryObject.getObject() instanceof COSDictionary)) {
            throw new IOException("Dictionary object expected at offset " + this.source.getPosition());
        }
        COSDictionary dictionary = (COSDictionary)dictionaryObject.getObject();
        for (COSBase value : dictionary.getValues()) {
            COSObject object;
            if (!(value instanceof COSObject) || (object = (COSObject)value).getObject() != null) continue;
            this.parseDictionaryRecursive(object);
        }
    }
}

