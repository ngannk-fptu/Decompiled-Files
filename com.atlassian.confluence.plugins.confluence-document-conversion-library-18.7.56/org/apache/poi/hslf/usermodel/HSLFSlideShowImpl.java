/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections4.IteratorUtils
 *  org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream
 */
package org.apache.poi.hslf.usermodel;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.POIDocument;
import org.apache.poi.ddf.EscherBSERecord;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherOptRecord;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.hpsf.PropertySet;
import org.apache.poi.hslf.exceptions.CorruptPowerPointFileException;
import org.apache.poi.hslf.exceptions.HSLFException;
import org.apache.poi.hslf.exceptions.OldPowerPointFormatException;
import org.apache.poi.hslf.record.CurrentUserAtom;
import org.apache.poi.hslf.record.Document;
import org.apache.poi.hslf.record.DocumentEncryptionAtom;
import org.apache.poi.hslf.record.ExOleObjStg;
import org.apache.poi.hslf.record.PersistPtrHolder;
import org.apache.poi.hslf.record.PersistRecord;
import org.apache.poi.hslf.record.PositionDependentRecord;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.hslf.record.UserEditAtom;
import org.apache.poi.hslf.usermodel.HSLFObjectData;
import org.apache.poi.hslf.usermodel.HSLFPictureData;
import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.HSLFSlideShowEncrypted;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.EntryUtils;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;

public final class HSLFSlideShowImpl
extends POIDocument
implements Closeable {
    private static final Logger LOG = LogManager.getLogger(HSLFSlideShowImpl.class);
    static final int UNSET_OFFSET = -1;
    private static final int DEFAULT_MAX_RECORD_LENGTH = 200000000;
    private static final int MAX_DOCUMENT_SIZE = 100000000;
    private static int MAX_RECORD_LENGTH = 200000000;
    private CurrentUserAtom currentUser;
    private byte[] _docstream;
    private Record[] _records;
    private List<HSLFPictureData> _pictures;
    private HSLFObjectData[] _objects;

    public static void setMaxRecordLength(int length) {
        MAX_RECORD_LENGTH = length;
    }

    public static int getMaxRecordLength() {
        return MAX_RECORD_LENGTH;
    }

    public HSLFSlideShowImpl(String fileName) throws IOException {
        this(new POIFSFileSystem(new File(fileName)));
    }

    public HSLFSlideShowImpl(InputStream inputStream) throws IOException {
        this(new POIFSFileSystem(inputStream));
    }

    public HSLFSlideShowImpl(POIFSFileSystem filesystem) throws IOException {
        this(filesystem.getRoot());
    }

    public HSLFSlideShowImpl(DirectoryNode dir) throws IOException {
        super(HSLFSlideShowImpl.handleDualStorage(dir));
        try {
            this.readCurrentUserStream();
            this.readPowerPointStream();
            this.buildRecords();
            this.readOtherStreams();
        }
        catch (IOException | RuntimeException e) {
            dir.getFileSystem().close();
            throw e;
        }
    }

    private static DirectoryNode handleDualStorage(DirectoryNode dir) throws IOException {
        if (!dir.hasEntry("PP97_DUALSTORAGE")) {
            return dir;
        }
        return (DirectoryNode)dir.getEntry("PP97_DUALSTORAGE");
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static HSLFSlideShowImpl create() {
        try (InputStream is = HSLFSlideShowImpl.class.getResourceAsStream("/org/apache/poi/hslf/data/empty.ppt");){
            if (is == null) {
                throw new HSLFException("Missing resource 'empty.ppt'");
            }
            HSLFSlideShowImpl hSLFSlideShowImpl = new HSLFSlideShowImpl(is);
            return hSLFSlideShowImpl;
        }
        catch (IOException e) {
            throw new HSLFException(e);
        }
    }

    private void readPowerPointStream() throws IOException {
        DirectoryNode dir = this.getDirectory();
        if (!dir.hasEntry("PowerPoint Document") && dir.hasEntry("PP40")) {
            throw new OldPowerPointFormatException("You seem to have supplied a PowerPoint95 file, which isn't supported");
        }
        DocumentEntry docProps = (DocumentEntry)dir.getEntry("PowerPoint Document");
        int len = docProps.getSize();
        try (DocumentInputStream is = dir.createDocumentInputStream(docProps);){
            this._docstream = IOUtils.toByteArray(is, len, 100000000);
        }
    }

    private void buildRecords() throws IOException {
        this._records = this.read(this._docstream, (int)this.currentUser.getCurrentEditOffset());
    }

    private Record[] read(byte[] docstream, int usrOffset) throws IOException {
        TreeMap<Integer, Record> records = new TreeMap<Integer, Record>();
        HashMap<Integer, Integer> persistIds = new HashMap<Integer, Integer>();
        this.initRecordOffsets(docstream, usrOffset, records, persistIds);
        HSLFSlideShowEncrypted decryptData = new HSLFSlideShowEncrypted(docstream, records);
        for (Map.Entry entry : records.entrySet()) {
            Integer offset = (Integer)entry.getKey();
            Record record = (Record)entry.getValue();
            Integer persistId = (Integer)persistIds.get(offset);
            if (record == null) {
                decryptData.decryptRecord(docstream, persistId, offset);
                record = Record.buildRecordAtOffset(docstream, offset);
                entry.setValue(record);
            }
            if (!(record instanceof PersistRecord)) continue;
            ((PersistRecord)((Object)record)).setPersistId(persistId);
        }
        decryptData.close();
        return records.values().toArray(new Record[0]);
    }

    private void initRecordOffsets(byte[] docstream, int usrOffset, NavigableMap<Integer, Record> recordMap, Map<Integer, Integer> offset2id) {
        while (usrOffset != 0) {
            UserEditAtom usr = (UserEditAtom)Record.buildRecordAtOffset(docstream, usrOffset);
            if (usr == null) {
                throw new CorruptPowerPointFileException("Powerpoint document contains no user edit atom");
            }
            recordMap.put(usrOffset, usr);
            int psrOffset = usr.getPersistPointersOffset();
            PersistPtrHolder ptr = (PersistPtrHolder)Record.buildRecordAtOffset(docstream, psrOffset);
            if (ptr == null) {
                throw new CorruptPowerPointFileException("Powerpoint document is missing a PersistPtrHolder at " + psrOffset);
            }
            recordMap.put(psrOffset, ptr);
            for (Map.Entry<Integer, Integer> entry : ptr.getSlideLocationsLookup().entrySet()) {
                Integer offset = entry.getValue();
                Integer id = entry.getKey();
                recordMap.put(offset, null);
                offset2id.put(offset, id);
            }
            usrOffset = usr.getLastUserEditAtomOffset();
            if (usrOffset <= 0 || !recordMap.containsKey(usrOffset)) continue;
            usrOffset = (Integer)recordMap.firstKey() - 36;
            int ver_inst = LittleEndian.getUShort(docstream, usrOffset);
            int type = LittleEndian.getUShort(docstream, usrOffset + 2);
            int len = LittleEndian.getInt(docstream, usrOffset + 4);
            if (ver_inst == 0 && type == 4085 && (len == 28 || len == 32)) {
                LOG.atWarn().log("Repairing invalid user edit atom");
                usr.setLastUserEditAtomOffset(usrOffset);
                continue;
            }
            throw new CorruptPowerPointFileException("Powerpoint document contains invalid user edit atom");
        }
    }

    public DocumentEncryptionAtom getDocumentEncryptionAtom() {
        for (Record r : this._records) {
            if (!(r instanceof DocumentEncryptionAtom)) continue;
            return (DocumentEncryptionAtom)r;
        }
        return null;
    }

    private void readCurrentUserStream() {
        try {
            this.currentUser = new CurrentUserAtom(this.getDirectory());
        }
        catch (IOException ie) {
            LOG.atError().withThrowable(ie).log("Error finding Current User Atom");
            this.currentUser = new CurrentUserAtom();
        }
    }

    private void readOtherStreams() {
    }

    private void readPictures() throws IOException {
        byte[] pictstream;
        if (!this.getDirectory().hasEntry("Pictures")) {
            this._pictures = new ArrayList<HSLFPictureData>();
            return;
        }
        DocumentEntry entry = (DocumentEntry)this.getDirectory().getEntry("Pictures");
        EscherContainerRecord blipStore = this.getBlipStore();
        try (DocumentInputStream is = this.getDirectory().createDocumentInputStream(entry);){
            pictstream = IOUtils.toByteArray(is, entry.getSize());
        }
        ArrayList<PictureFactory> factories = new ArrayList<PictureFactory>();
        HSLFSlideShowEncrypted decryptData = new HSLFSlideShowEncrypted(this.getDocumentEncryptionAtom());
        Object object = null;
        try {
            int imgsize;
            for (int pos = 0; pos <= pictstream.length - 8; pos += imgsize) {
                int offset = pos;
                decryptData.decryptPicture(pictstream, offset);
                int signature = LittleEndian.getUShort(pictstream, pos);
                int type = LittleEndian.getUShort(pictstream, pos += 2);
                imgsize = LittleEndian.getInt(pictstream, pos += 2);
                pos += 4;
                if (type != 61447) {
                    if (type < 61464) break;
                    if (type > 61719) {
                        break;
                    }
                }
                if (imgsize < 0) {
                    throw new CorruptPowerPointFileException("The file contains a picture, at position " + factories.size() + ", which has a negatively sized data length, so we can't trust any of the picture data");
                }
                PictureData.PictureType pt = PictureData.PictureType.forNativeID(type - 61464);
                if (pt == null) {
                    LOG.atError().log("Problem reading picture: Invalid image type 0, on picture with length {}.\nYour document will probably become corrupted if you save it! Position: {}", (Object)Unbox.box(imgsize), (Object)Unbox.box(pos));
                    continue;
                }
                if (pos + imgsize > pictstream.length) {
                    LOG.atWarn().log("\"Pictures\" stream may have ended early. In some circumstances, this is not a problem; in others, this could indicate a corrupt file");
                    break;
                }
                byte[] imgdata = IOUtils.safelyClone(pictstream, pos, imgsize, MAX_RECORD_LENGTH);
                factories.add(new PictureFactory(blipStore, pt, imgdata, offset, signature));
            }
        }
        catch (Throwable pos) {
            object = pos;
            throw pos;
        }
        finally {
            if (decryptData != null) {
                if (object != null) {
                    try {
                        decryptData.close();
                    }
                    catch (Throwable pos) {
                        ((Throwable)object).addSuppressed(pos);
                    }
                } else {
                    decryptData.close();
                }
            }
        }
        HSLFSlideShowImpl.matchPicturesAndRecords(factories, blipStore);
        ArrayList<HSLFPictureData> pictures = new ArrayList<HSLFPictureData>();
        for (PictureFactory it : factories) {
            try {
                HSLFPictureData pict = it.build();
                pict.setIndex(pictures.size() + 1);
                pictures.add(pict);
            }
            catch (IllegalArgumentException e) {
                LOG.atError().withThrowable(e).log("Problem reading picture. Your document will probably become corrupted if you save it!");
            }
        }
        this._pictures = pictures;
    }

    private static void matchPicturesAndRecords(List<PictureFactory> factories, EscherContainerRecord blipStore) {
        byte[] imageHeader;
        byte[] recordUid;
        EscherBSERecord record;
        LinkedList<PictureFactory> unmatchedFactories = new LinkedList<PictureFactory>(factories);
        unmatchedFactories.sort(Comparator.comparingInt(PictureFactory::getOffset));
        HashMap<Integer, List> unmatchedRecords = new HashMap<Integer, List>();
        for (EscherRecord child : blipStore) {
            EscherBSERecord record2 = (EscherBSERecord)child;
            unmatchedRecords.computeIfAbsent(record2.getOffset(), k -> new ArrayList()).add(record2);
        }
        Iterator iterator = unmatchedFactories.iterator();
        block1: while (iterator.hasNext()) {
            PictureFactory factory = (PictureFactory)iterator.next();
            int physicalOffset = factory.getOffset();
            List recordsAtOffset = (List)unmatchedRecords.get(physicalOffset);
            if (recordsAtOffset == null || recordsAtOffset.isEmpty()) {
                LOG.atDebug().log("No records with offset {}", (Object)Unbox.box(physicalOffset));
                continue;
            }
            if (recordsAtOffset.size() == 1) {
                factory.setRecord((EscherBSERecord)recordsAtOffset.get(0));
                unmatchedRecords.remove(physicalOffset);
                iterator.remove();
                continue;
            }
            for (int i = 0; i < recordsAtOffset.size(); ++i) {
                record = (EscherBSERecord)recordsAtOffset.get(i);
                recordUid = record.getUid();
                if (!Arrays.equals(recordUid, imageHeader = Arrays.copyOf(factory.imageData, 16))) continue;
                factory.setRecord(record);
                recordsAtOffset.remove(i);
                iterator.remove();
                continue block1;
            }
        }
        List remainingRecords = unmatchedRecords.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
        for (PictureFactory factory : unmatchedFactories) {
            boolean matched = false;
            for (int i = remainingRecords.size() - 1; i >= 0; --i) {
                record = (EscherBSERecord)remainingRecords.get(i);
                recordUid = record.getUid();
                if (!Arrays.equals(recordUid, imageHeader = Arrays.copyOf(factory.imageData, 16))) continue;
                remainingRecords.remove(i);
                factory.setRecord(record);
                record.setOffset(factory.getOffset());
                matched = true;
            }
            if (matched) continue;
            LOG.atDebug().log("No record found for picture at offset {}", (Object)Unbox.box(factory.offset));
            EscherBSERecord record3 = HSLFSlideShow.addNewEscherBseRecord(blipStore, factory.type, factory.imageData, factory.offset);
            factory.setRecord(record3);
        }
        LOG.atDebug().log("Found {} unmatched records.", (Object)Unbox.box(remainingRecords.size()));
    }

    public void normalizeRecords() {
        try {
            this.updateAndWriteDependantRecords(null, null);
        }
        catch (IOException e) {
            throw new CorruptPowerPointFileException(e);
        }
        this._records = HSLFSlideShowEncrypted.normalizeRecords(this._records);
    }

    public void updateAndWriteDependantRecords(OutputStream os, Map<RecordTypes, PositionDependentRecord> interestingRecords) throws IOException {
        HashMap<Integer, Integer> oldToNewPositions = new HashMap<Integer, Integer>();
        UserEditAtom usr = null;
        PersistPtrHolder ptr = null;
        CountingOS cos = new CountingOS();
        for (Record record : this._records) {
            PositionDependentRecord pdr = (PositionDependentRecord)((Object)record);
            int oldPos = pdr.getLastOnDiskOffset();
            int newPos = cos.size();
            pdr.setLastOnDiskOffset(newPos);
            if (oldPos != -1) {
                oldToNewPositions.put(oldPos, newPos);
            }
            RecordTypes saveme = null;
            int recordType = (int)record.getRecordType();
            if (recordType == RecordTypes.PersistPtrIncrementalBlock.typeID) {
                saveme = RecordTypes.PersistPtrIncrementalBlock;
                ptr = (PersistPtrHolder)pdr;
            } else if (recordType == RecordTypes.UserEditAtom.typeID) {
                saveme = RecordTypes.UserEditAtom;
                usr = (UserEditAtom)pdr;
            }
            if (interestingRecords != null && saveme != null) {
                interestingRecords.put(saveme, pdr);
            }
            record.writeOut(cos);
        }
        cos.close();
        if (usr == null || ptr == null) {
            throw new HSLFException("UserEditAtom or PersistPtr can't be determined.");
        }
        HashMap persistIds = new HashMap();
        for (Map.Entry<Integer, Integer> entry : ptr.getSlideLocationsLookup().entrySet()) {
            persistIds.put(oldToNewPositions.get(entry.getValue()), entry.getKey());
        }
        try (HSLFSlideShowEncrypted encData = new HSLFSlideShowEncrypted(this.getDocumentEncryptionAtom());){
            for (Record record : this._records) {
                PositionDependentRecord pdr = (PositionDependentRecord)((Object)record);
                Integer persistId = (Integer)persistIds.get(pdr.getLastOnDiskOffset());
                if (persistId == null) {
                    persistId = 0;
                }
                pdr.updateOtherRecordReferences(oldToNewPositions);
                if (os == null) continue;
                record.writeOut(encData.encryptRecord(os, persistId, record));
            }
        }
        int oldLastUserEditAtomPos = (int)this.currentUser.getCurrentEditOffset();
        Integer newLastUserEditAtomPos = (Integer)oldToNewPositions.get(oldLastUserEditAtomPos);
        if (newLastUserEditAtomPos == null || usr.getLastOnDiskOffset() != newLastUserEditAtomPos.intValue()) {
            throw new HSLFException("Couldn't find the new location of the last UserEditAtom that used to be at " + oldLastUserEditAtomPos);
        }
        this.currentUser.setCurrentEditOffset(usr.getLastOnDiskOffset());
    }

    @Override
    public void write() throws IOException {
        this.validateInPlaceWritePossible();
        this.write(this.getDirectory().getFileSystem(), false);
        this.getDirectory().getFileSystem().writeFilesystem();
    }

    @Override
    public void write(File newFile) throws IOException {
        this.write(newFile, false);
    }

    public void write(File newFile, boolean preserveNodes) throws IOException {
        try (POIFSFileSystem outFS = POIFSFileSystem.create(newFile);){
            this.write(outFS, preserveNodes);
            outFS.writeFilesystem();
        }
    }

    @Override
    public void write(OutputStream out) throws IOException {
        this.write(out, false);
    }

    public void write(OutputStream out, boolean preserveNodes) throws IOException {
        try (POIFSFileSystem outFS = new POIFSFileSystem();){
            this.write(outFS, preserveNodes);
            outFS.writeFilesystem(out);
        }
    }

    private void write(POIFSFileSystem outFS, boolean copyAllOtherNodes) throws IOException {
        ArrayList<String> writtenEntries;
        block41: {
            if (this._pictures == null) {
                this.readPictures();
            }
            this.getDocumentSummaryInformation();
            writtenEntries = new ArrayList<String>(1);
            try (HSLFSlideShowEncrypted encryptedSS = new HSLFSlideShowEncrypted(this.getDocumentEncryptionAtom());){
                this._records = encryptedSS.updateEncryptionRecord(this._records);
                this.writeProperties(outFS, writtenEntries);
                try (UnsynchronizedByteArrayOutputStream baos = new UnsynchronizedByteArrayOutputStream();){
                    this.updateAndWriteDependantRecords((OutputStream)baos, null);
                    this._docstream = baos.toByteArray();
                }
                ByteArrayInputStream bais = new ByteArrayInputStream(this._docstream);
                outFS.createOrUpdateDocument(bais, "PowerPoint Document");
                writtenEntries.add("PowerPoint Document");
                this.currentUser.setEncrypted(encryptedSS.getDocumentEncryptionAtom() != null);
                this.currentUser.writeToFS(outFS);
                writtenEntries.add("Current User");
                if (this._pictures.isEmpty()) break block41;
                Enumeration pictEnum = IteratorUtils.asEnumeration(this._pictures.stream().map(data -> HSLFSlideShowImpl.encryptOnePicture(encryptedSS, data)).iterator());
                try (SequenceInputStream sis = new SequenceInputStream(pictEnum);){
                    outFS.createOrUpdateDocument(sis, "Pictures");
                    writtenEntries.add("Pictures");
                }
                catch (IllegalStateException e) {
                    throw (IOException)e.getCause();
                }
            }
        }
        if (copyAllOtherNodes) {
            EntryUtils.copyNodes(this.getDirectory().getFileSystem(), outFS, writtenEntries);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static InputStream encryptOnePicture(HSLFSlideShowEncrypted encryptedSS, HSLFPictureData data) {
        try (UnsynchronizedByteArrayOutputStream baos = new UnsynchronizedByteArrayOutputStream();){
            data.write((OutputStream)baos);
            byte[] pictBytes = baos.toByteArray();
            encryptedSS.encryptPicture(pictBytes, 0);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(pictBytes);
            return byteArrayInputStream;
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public EncryptionInfo getEncryptionInfo() {
        DocumentEncryptionAtom dea = this.getDocumentEncryptionAtom();
        return dea != null ? dea.getEncryptionInfo() : null;
    }

    public synchronized int appendRootLevelRecord(Record newRecord) {
        int addedAt = -1;
        Record[] r = new Record[this._records.length + 1];
        boolean added = false;
        for (int i = this._records.length - 1; i >= 0; --i) {
            if (added) {
                r[i] = this._records[i];
                continue;
            }
            r[i + 1] = this._records[i];
            if (!(this._records[i] instanceof PersistPtrHolder)) continue;
            r[i] = newRecord;
            added = true;
            addedAt = i;
        }
        this._records = r;
        return addedAt;
    }

    public int addPicture(HSLFPictureData img) {
        if (this._pictures == null) {
            try {
                this.readPictures();
            }
            catch (IOException e) {
                throw new CorruptPowerPointFileException(e.getMessage());
            }
        }
        int offset = 0;
        if (!this._pictures.isEmpty()) {
            HSLFPictureData prev = this._pictures.get(this._pictures.size() - 1);
            offset = prev.getOffset() + prev.getBseSize();
        }
        img.setIndex(this._pictures.size() + 1);
        this._pictures.add(img);
        return offset;
    }

    public Record[] getRecords() {
        return this._records;
    }

    public byte[] getUnderlyingBytes() {
        return this._docstream;
    }

    public CurrentUserAtom getCurrentUserAtom() {
        return this.currentUser;
    }

    public List<HSLFPictureData> getPictureData() {
        if (this._pictures == null) {
            try {
                this.readPictures();
            }
            catch (IOException e) {
                throw new CorruptPowerPointFileException(e.getMessage());
            }
        }
        return Collections.unmodifiableList(this._pictures);
    }

    public HSLFObjectData[] getEmbeddedObjects() {
        if (this._objects == null) {
            ArrayList<HSLFObjectData> objects = new ArrayList<HSLFObjectData>();
            for (Record r : this._records) {
                if (!(r instanceof ExOleObjStg)) continue;
                objects.add(new HSLFObjectData((ExOleObjStg)r));
            }
            this._objects = objects.toArray(new HSLFObjectData[0]);
        }
        return this._objects;
    }

    private EscherContainerRecord getBlipStore() {
        Document documentRecord = null;
        for (Record record : this._records) {
            if (record.getRecordType() != (long)RecordTypes.Document.typeID) continue;
            documentRecord = (Document)record;
            break;
        }
        if (documentRecord == null) {
            throw new CorruptPowerPointFileException("Document record is missing");
        }
        EscherContainerRecord dggContainer = documentRecord.getPPDrawingGroup().getDggContainer();
        EscherContainerRecord blipStore = (EscherContainerRecord)HSLFShape.getEscherChild(dggContainer, EscherContainerRecord.BSTORE_CONTAINER);
        if (blipStore == null) {
            blipStore = new EscherContainerRecord();
            blipStore.setRecordId(EscherContainerRecord.BSTORE_CONTAINER);
            dggContainer.addChildBefore(blipStore, EscherOptRecord.RECORD_ID);
        }
        return blipStore;
    }

    @Override
    public void close() throws IOException {
        POIFSFileSystem fs;
        if ((this.getDirectory().getParent() == null || "PP97_DUALSTORAGE".equals(this.getDirectory().getName())) && (fs = this.getDirectory().getFileSystem()) != null) {
            fs.close();
        }
    }

    @Override
    protected String getEncryptedPropertyStreamName() {
        return "EncryptedSummary";
    }

    void writePropertiesImpl() throws IOException {
        super.writeProperties();
    }

    PropertySet getPropertySetImpl(String setName) throws IOException {
        return super.getPropertySet(setName);
    }

    PropertySet getPropertySetImpl(String setName, EncryptionInfo encryptionInfo) throws IOException {
        return super.getPropertySet(setName, encryptionInfo);
    }

    void writePropertiesImpl(POIFSFileSystem outFS, List<String> writtenEntries) throws IOException {
        super.writeProperties(outFS, writtenEntries);
    }

    void validateInPlaceWritePossibleImpl() throws IllegalStateException {
        super.validateInPlaceWritePossible();
    }

    void clearDirectoryImpl() {
        super.clearDirectory();
    }

    boolean initDirectoryImpl() {
        return super.initDirectory();
    }

    void replaceDirectoryImpl(DirectoryNode newDirectory) throws IOException {
        super.replaceDirectory(newDirectory);
    }

    static final class PictureFactory {
        final byte[] imageData;
        private final EscherContainerRecord recordContainer;
        private final PictureData.PictureType type;
        private final int offset;
        private final int signature;
        private EscherBSERecord record;

        PictureFactory(EscherContainerRecord recordContainer, PictureData.PictureType type, byte[] imageData, int offset, int signature) {
            this.recordContainer = Objects.requireNonNull(recordContainer);
            this.type = Objects.requireNonNull(type);
            this.imageData = Objects.requireNonNull(imageData);
            this.offset = offset;
            this.signature = signature;
        }

        int getOffset() {
            return this.offset;
        }

        HSLFPictureData build() {
            Objects.requireNonNull(this.record, "Can't build an instance until the record has been assigned.");
            return HSLFPictureData.createFromSlideshowData(this.type, this.recordContainer, this.record, this.imageData, this.signature);
        }

        PictureFactory setRecord(EscherBSERecord bse) {
            this.record = bse;
            return this;
        }
    }

    private static class CountingOS
    extends OutputStream {
        int count;

        private CountingOS() {
        }

        @Override
        public void write(int b) throws IOException {
            ++this.count;
        }

        @Override
        public void write(byte[] b) throws IOException {
            this.count += b.length;
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            this.count += len;
        }

        public int size() {
            return this.count;
        }
    }
}

