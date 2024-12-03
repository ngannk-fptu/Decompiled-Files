/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.cos;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSObjectKey;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.cos.ICOSVisitor;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.io.ScratchFile;
import org.apache.pdfbox.pdfparser.PDFObjectStreamParser;

public class COSDocument
extends COSBase
implements Closeable {
    private static final Log LOG = LogFactory.getLog(COSDocument.class);
    private float version = 1.4f;
    private final Map<COSObjectKey, COSObject> objectPool = new HashMap<COSObjectKey, COSObject>();
    private final Map<COSObjectKey, Long> xrefTable = new HashMap<COSObjectKey, Long>();
    private final List<COSStream> streams = new ArrayList<COSStream>();
    private COSDictionary trailer;
    private boolean warnMissingClose = true;
    private boolean isDecrypted = false;
    private long startXref;
    private boolean closed = false;
    private boolean isXRefStream;
    private ScratchFile scratchFile;
    private long highestXRefObjectNumber;

    public COSDocument() {
        this(ScratchFile.getMainMemoryOnlyInstance());
    }

    public COSDocument(ScratchFile scratchFile) {
        this.scratchFile = scratchFile;
    }

    public COSStream createCOSStream() {
        COSStream stream = new COSStream(this.scratchFile);
        this.streams.add(stream);
        return stream;
    }

    public COSStream createCOSStream(COSDictionary dictionary) {
        COSStream stream = new COSStream(this.scratchFile);
        for (Map.Entry<COSName, COSBase> entry : dictionary.entrySet()) {
            stream.setItem(entry.getKey(), entry.getValue());
        }
        return stream;
    }

    public COSObject getObjectByType(COSName type) throws IOException {
        for (COSObject object : this.objectPool.values()) {
            COSBase realObject = object.getObject();
            if (!(realObject instanceof COSDictionary)) continue;
            try {
                COSDictionary dic = (COSDictionary)realObject;
                COSBase typeItem = dic.getItem(COSName.TYPE);
                if (typeItem instanceof COSName) {
                    COSName objectType = (COSName)typeItem;
                    if (!objectType.equals(type)) continue;
                    return object;
                }
                if (typeItem == null) continue;
                LOG.debug((Object)("Expected a /Name object after /Type, got '" + typeItem + "' instead"));
            }
            catch (ClassCastException e) {
                LOG.warn((Object)e, (Throwable)e);
            }
        }
        return null;
    }

    public List<COSObject> getObjectsByType(String type) throws IOException {
        return this.getObjectsByType(COSName.getPDFName(type));
    }

    public List<COSObject> getObjectsByType(COSName type) throws IOException {
        ArrayList<COSObject> retval = new ArrayList<COSObject>();
        for (COSObject object : this.objectPool.values()) {
            COSBase realObject = object.getObject();
            if (!(realObject instanceof COSDictionary)) continue;
            try {
                COSDictionary dic = (COSDictionary)realObject;
                COSBase typeItem = dic.getItem(COSName.TYPE);
                if (typeItem instanceof COSName) {
                    COSName objectType = (COSName)typeItem;
                    if (!objectType.equals(type)) continue;
                    retval.add(object);
                    continue;
                }
                if (typeItem == null) continue;
                LOG.debug((Object)("Expected a /Name object after /Type, got '" + typeItem + "' instead"));
            }
            catch (ClassCastException e) {
                LOG.warn((Object)e, (Throwable)e);
            }
        }
        return retval;
    }

    public COSObjectKey getKey(COSBase object) {
        for (Map.Entry<COSObjectKey, COSObject> entry : this.objectPool.entrySet()) {
            if (entry.getValue().getObject() != object) continue;
            return entry.getKey();
        }
        return null;
    }

    public void print() {
        for (COSObject object : this.objectPool.values()) {
            System.out.println(object);
        }
    }

    public void setVersion(float versionValue) {
        this.version = versionValue;
    }

    public float getVersion() {
        return this.version;
    }

    public void setDecrypted() {
        this.isDecrypted = true;
    }

    public boolean isDecrypted() {
        return this.isDecrypted;
    }

    public boolean isEncrypted() {
        boolean encrypted = false;
        if (this.trailer != null) {
            encrypted = this.trailer.getDictionaryObject(COSName.ENCRYPT) instanceof COSDictionary;
        }
        return encrypted;
    }

    public COSDictionary getEncryptionDictionary() {
        return this.trailer.getCOSDictionary(COSName.ENCRYPT);
    }

    public void setEncryptionDictionary(COSDictionary encDictionary) {
        this.trailer.setItem(COSName.ENCRYPT, (COSBase)encDictionary);
    }

    public COSArray getDocumentID() {
        return this.getTrailer().getCOSArray(COSName.ID);
    }

    public void setDocumentID(COSArray id) {
        this.getTrailer().setItem(COSName.ID, (COSBase)id);
    }

    public COSObject getCatalog() throws IOException {
        COSObject catalog = this.getObjectByType(COSName.CATALOG);
        if (catalog == null) {
            throw new IOException("Catalog cannot be found");
        }
        return catalog;
    }

    public List<COSObject> getObjects() {
        return new ArrayList<COSObject>(this.objectPool.values());
    }

    public COSDictionary getTrailer() {
        return this.trailer;
    }

    public void setTrailer(COSDictionary newTrailer) {
        this.trailer = newTrailer;
    }

    public long getHighestXRefObjectNumber() {
        return this.highestXRefObjectNumber;
    }

    public void setHighestXRefObjectNumber(long highestXRefObjectNumber) {
        this.highestXRefObjectNumber = highestXRefObjectNumber;
    }

    @Override
    public Object accept(ICOSVisitor visitor) throws IOException {
        return visitor.visitFromDocument(this);
    }

    @Override
    public void close() throws IOException {
        if (this.closed) {
            return;
        }
        IOException firstException = null;
        for (COSObject object : this.getObjects()) {
            COSBase cosObject = object.getObject();
            if (!(cosObject instanceof COSStream)) continue;
            firstException = IOUtils.closeAndLogException((COSStream)cosObject, LOG, "COSStream", firstException);
        }
        for (COSStream stream : this.streams) {
            firstException = IOUtils.closeAndLogException(stream, LOG, "COSStream", firstException);
        }
        if (this.scratchFile != null) {
            firstException = IOUtils.closeAndLogException(this.scratchFile, LOG, "ScratchFile", firstException);
        }
        this.closed = true;
        if (firstException != null) {
            throw firstException;
        }
    }

    public boolean isClosed() {
        return this.closed;
    }

    protected void finalize() throws IOException {
        if (!this.closed) {
            if (this.warnMissingClose) {
                LOG.warn((Object)"Warning: You did not close a PDF Document");
            }
            this.close();
        }
    }

    public void setWarnMissingClose(boolean warn) {
        this.warnMissingClose = warn;
    }

    public void dereferenceObjectStreams() throws IOException {
        for (COSObject objStream : this.getObjectsByType(COSName.OBJ_STM)) {
            COSStream stream = (COSStream)objStream.getObject();
            PDFObjectStreamParser parser = new PDFObjectStreamParser(stream, this);
            parser.parse();
            for (COSObject next : parser.getObjects()) {
                COSObjectKey key = new COSObjectKey(next);
                if (this.objectPool.get(key) != null && this.objectPool.get(key).getObject() != null && (!this.xrefTable.containsKey(key) || this.xrefTable.get(key) != -objStream.getObjectNumber())) continue;
                COSObject obj = this.getObjectFromPool(key);
                obj.setObject(next.getObject());
            }
        }
    }

    public COSObject getObjectFromPool(COSObjectKey key) throws IOException {
        COSObject obj = null;
        if (key != null) {
            obj = this.objectPool.get(key);
        }
        if (obj == null) {
            obj = new COSObject(null);
            if (key != null) {
                obj.setObjectNumber(key.getNumber());
                obj.setGenerationNumber(key.getGeneration());
                this.objectPool.put(key, obj);
            }
        }
        return obj;
    }

    public COSObject removeObject(COSObjectKey key) {
        return this.objectPool.remove(key);
    }

    public void addXRefTable(Map<COSObjectKey, Long> xrefTableValues) {
        this.xrefTable.putAll(xrefTableValues);
    }

    public Map<COSObjectKey, Long> getXrefTable() {
        return this.xrefTable;
    }

    public void setStartXref(long startXrefValue) {
        this.startXref = startXrefValue;
    }

    public long getStartXref() {
        return this.startXref;
    }

    public boolean isXRefStream() {
        return this.isXRefStream;
    }

    public void setIsXRefStream(boolean isXRefStreamValue) {
        this.isXRefStream = isXRefStreamValue;
    }
}

