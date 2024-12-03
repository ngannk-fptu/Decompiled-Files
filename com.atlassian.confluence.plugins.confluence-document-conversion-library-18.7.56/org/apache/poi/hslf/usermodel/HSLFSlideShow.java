/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream
 */
package org.apache.poi.hslf.usermodel;

import java.awt.Dimension;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.POIDocument;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.common.usermodel.fonts.FontInfo;
import org.apache.poi.ddf.EscherBSERecord;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherOptRecord;
import org.apache.poi.hpsf.ClassID;
import org.apache.poi.hpsf.ClassIDPredefined;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.PropertySet;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hpsf.extractor.HPSFPropertiesExtractor;
import org.apache.poi.hslf.exceptions.CorruptPowerPointFileException;
import org.apache.poi.hslf.exceptions.HSLFException;
import org.apache.poi.hslf.model.HeadersFooters;
import org.apache.poi.hslf.record.Document;
import org.apache.poi.hslf.record.DocumentAtom;
import org.apache.poi.hslf.record.ExAviMovie;
import org.apache.poi.hslf.record.ExControl;
import org.apache.poi.hslf.record.ExEmbed;
import org.apache.poi.hslf.record.ExEmbedAtom;
import org.apache.poi.hslf.record.ExMCIMovie;
import org.apache.poi.hslf.record.ExObjList;
import org.apache.poi.hslf.record.ExObjListAtom;
import org.apache.poi.hslf.record.ExOleObjAtom;
import org.apache.poi.hslf.record.ExOleObjStg;
import org.apache.poi.hslf.record.ExVideoContainer;
import org.apache.poi.hslf.record.FontCollection;
import org.apache.poi.hslf.record.MainMaster;
import org.apache.poi.hslf.record.Notes;
import org.apache.poi.hslf.record.PersistPtrHolder;
import org.apache.poi.hslf.record.PositionDependentRecord;
import org.apache.poi.hslf.record.PositionDependentRecordContainer;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.hslf.record.RecordContainer;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.hslf.record.Slide;
import org.apache.poi.hslf.record.SlideListWithText;
import org.apache.poi.hslf.record.SlidePersistAtom;
import org.apache.poi.hslf.record.TxMasterStyleAtom;
import org.apache.poi.hslf.record.UserEditAtom;
import org.apache.poi.hslf.usermodel.HSLFFontInfo;
import org.apache.poi.hslf.usermodel.HSLFMasterSheet;
import org.apache.poi.hslf.usermodel.HSLFNotes;
import org.apache.poi.hslf.usermodel.HSLFObjectData;
import org.apache.poi.hslf.usermodel.HSLFPictureData;
import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFShapeContainer;
import org.apache.poi.hslf.usermodel.HSLFSlide;
import org.apache.poi.hslf.usermodel.HSLFSlideMaster;
import org.apache.poi.hslf.usermodel.HSLFSlideShowImpl;
import org.apache.poi.hslf.usermodel.HSLFSoundData;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.hslf.usermodel.HSLFTextShape;
import org.apache.poi.hslf.usermodel.HSLFTitleMaster;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.Ole10Native;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.sl.usermodel.MasterSheet;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.sl.usermodel.SlideShow;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Internal;
import org.apache.poi.util.Units;

public final class HSLFSlideShow
extends POIDocument
implements SlideShow<HSLFShape, HSLFTextParagraph>,
Closeable,
GenericRecord {
    public static final String POWERPOINT_DOCUMENT = "PowerPoint Document";
    public static final String PP97_DOCUMENT = "PP97_DUALSTORAGE";
    public static final String PP95_DOCUMENT = "PP40";
    private static final Logger LOG = LogManager.getLogger(HSLFSlideShow.class);
    private static final int DEFAULT_MAX_RECORD_LENGTH = 10000000;
    private static int MAX_RECORD_LENGTH = 10000000;
    private static final ThreadLocal<LoadSavePhase> loadSavePhase = new ThreadLocal();
    private final HSLFSlideShowImpl _hslfSlideShow;
    private Record[] _mostRecentCoreRecords;
    private Map<Integer, Integer> _sheetIdToCoreRecordsLookup;
    private Document _documentRecord;
    private final List<HSLFSlideMaster> _masters = new ArrayList<HSLFSlideMaster>();
    private final List<HSLFTitleMaster> _titleMasters = new ArrayList<HSLFTitleMaster>();
    private final List<HSLFSlide> _slides = new ArrayList<HSLFSlide>();
    private final List<HSLFNotes> _notes = new ArrayList<HSLFNotes>();
    private FontCollection _fonts;

    public static void setMaxRecordLength(int length) {
        MAX_RECORD_LENGTH = length;
    }

    public static int getMaxRecordLength() {
        return MAX_RECORD_LENGTH;
    }

    public HSLFSlideShow(HSLFSlideShowImpl hslfSlideShow) {
        super(hslfSlideShow.getDirectory());
        loadSavePhase.set(LoadSavePhase.INIT);
        this._hslfSlideShow = hslfSlideShow;
        for (Record record : this._hslfSlideShow.getRecords()) {
            if (!(record instanceof RecordContainer)) continue;
            RecordContainer.handleParentAwareRecords((RecordContainer)record);
        }
        this.findMostRecentCoreRecords();
        this.buildSlidesAndNotes();
        loadSavePhase.set(LoadSavePhase.LOADED);
    }

    public HSLFSlideShow() {
        this(HSLFSlideShowImpl.create());
    }

    public HSLFSlideShow(InputStream inputStream) throws IOException {
        this(new HSLFSlideShowImpl(inputStream));
    }

    public HSLFSlideShow(POIFSFileSystem poifs) throws IOException {
        this(new HSLFSlideShowImpl(poifs));
    }

    public HSLFSlideShow(DirectoryNode root) throws IOException {
        this(new HSLFSlideShowImpl(root));
    }

    static LoadSavePhase getLoadSavePhase() {
        return loadSavePhase.get();
    }

    private void findMostRecentCoreRecords() {
        HashMap<Integer, Integer> mostRecentByBytes = new HashMap<Integer, Integer>();
        for (Record record : this._hslfSlideShow.getRecords()) {
            int[] ids;
            if (!(record instanceof PersistPtrHolder)) continue;
            PersistPtrHolder pph = (PersistPtrHolder)record;
            for (int id : ids = pph.getKnownSlideIDs()) {
                mostRecentByBytes.remove(id);
            }
            Map<Integer, Integer> thisSetOfLocations = pph.getSlideLocationsLookup();
            int[] nArray = ids;
            int n = nArray.length;
            for (int id = 0; id < n; ++id) {
                int id2 = nArray[id];
                mostRecentByBytes.put(id2, thisSetOfLocations.get(id2));
            }
        }
        this._mostRecentCoreRecords = new Record[mostRecentByBytes.size()];
        this._sheetIdToCoreRecordsLookup = new HashMap<Integer, Integer>();
        Object[] allIDs = mostRecentByBytes.keySet().toArray(new Integer[0]);
        Arrays.sort(allIDs);
        for (int i = 0; i < allIDs.length; ++i) {
            this._sheetIdToCoreRecordsLookup.put((Integer)allIDs[i], i);
        }
        HashMap mostRecentByBytesRev = new HashMap(mostRecentByBytes.size());
        for (Map.Entry me : mostRecentByBytes.entrySet()) {
            mostRecentByBytesRev.put(me.getValue(), me.getKey());
        }
        for (Record record : this._hslfSlideShow.getRecords()) {
            PositionDependentRecord pdr;
            int recordAt;
            Integer thisID;
            if (!(record instanceof PositionDependentRecord) || (thisID = (Integer)mostRecentByBytesRev.get(recordAt = (pdr = (PositionDependentRecord)((Object)record)).getLastOnDiskOffset())) == null) continue;
            int storeAt = this._sheetIdToCoreRecordsLookup.get(thisID);
            if (pdr instanceof PositionDependentRecordContainer) {
                PositionDependentRecordContainer pdrc = (PositionDependentRecordContainer)record;
                pdrc.setSheetId(thisID);
            }
            this._mostRecentCoreRecords[storeAt] = record;
        }
        for (Record record : this._mostRecentCoreRecords) {
            if (record == null || record.getRecordType() != (long)RecordTypes.Document.typeID) continue;
            this._documentRecord = (Document)record;
            if (this._documentRecord.getEnvironment() == null) continue;
            this._fonts = this._documentRecord.getEnvironment().getFontCollection();
        }
    }

    public Record getCoreRecordForSAS(SlideListWithText.SlideAtomsSet sas) {
        SlidePersistAtom spa = sas.getSlidePersistAtom();
        int refID = spa.getRefID();
        return this.getCoreRecordForRefID(refID);
    }

    public Record getCoreRecordForRefID(int refID) {
        Integer coreRecordId = this._sheetIdToCoreRecordsLookup.get(refID);
        if (coreRecordId != null) {
            return this._mostRecentCoreRecords[coreRecordId];
        }
        LOG.atError().log("We tried to look up a reference to a core record, but there was no core ID for reference ID {}", (Object)Unbox.box(refID));
        return null;
    }

    private void buildSlidesAndNotes() {
        if (this._documentRecord == null) {
            throw new CorruptPowerPointFileException("The PowerPoint file didn't contain a Document Record in its PersistPtr blocks. It is probably corrupt.");
        }
        this.findMasterSlides();
        HashMap<Integer, Integer> slideIdToNotes = new HashMap<Integer, Integer>();
        this.findNotesSlides(slideIdToNotes);
        this.findSlides(slideIdToNotes);
    }

    private void findMasterSlides() {
        SlideListWithText masterSLWT = this._documentRecord.getMasterSlideListWithText();
        if (masterSLWT == null) {
            return;
        }
        for (SlideListWithText.SlideAtomsSet sas : masterSLWT.getSlideAtomsSets()) {
            HSLFMasterSheet master;
            Record r = this.getCoreRecordForSAS(sas);
            int sheetNo = sas.getSlidePersistAtom().getSlideIdentifier();
            if (r instanceof Slide) {
                master = new HSLFTitleMaster((Slide)r, sheetNo);
                master.setSlideShow(this);
                this._titleMasters.add((HSLFTitleMaster)master);
                continue;
            }
            if (!(r instanceof MainMaster)) continue;
            master = new HSLFSlideMaster((MainMaster)r, sheetNo);
            ((HSLFSlideMaster)master).setSlideShow(this);
            this._masters.add((HSLFSlideMaster)master);
        }
    }

    private void findNotesSlides(Map<Integer, Integer> slideIdToNotes) {
        SlideListWithText notesSLWT = this._documentRecord.getNotesSlideListWithText();
        if (notesSLWT == null) {
            return;
        }
        int idx = -1;
        for (SlideListWithText.SlideAtomsSet notesSet : notesSLWT.getSlideAtomsSets()) {
            Record r = this.getCoreRecordForSAS(notesSet);
            SlidePersistAtom spa = notesSet.getSlidePersistAtom();
            String loggerLoc = "A Notes SlideAtomSet at " + ++idx + " said its record was at refID " + spa.getRefID();
            if (r == null) {
                LOG.atWarn().log("{}, but that record didn't exist - record ignored.", (Object)loggerLoc);
                continue;
            }
            if (!(r instanceof Notes)) {
                LOG.atError().log("{}, but that was actually a {}", (Object)loggerLoc, (Object)r);
                continue;
            }
            Notes notesRecord = (Notes)r;
            int slideId = spa.getSlideIdentifier();
            slideIdToNotes.put(slideId, idx);
            if (notesRecord.getNotesAtom() == null) {
                throw new IllegalStateException("Could not read NotesAtom from the NotesRecord for " + idx);
            }
            HSLFNotes hn = new HSLFNotes(notesRecord);
            hn.setSlideShow(this);
            this._notes.add(hn);
        }
    }

    private void findSlides(Map<Integer, Integer> slideIdToNotes) {
        SlideListWithText slidesSLWT = this._documentRecord.getSlideSlideListWithText();
        if (slidesSLWT == null) {
            return;
        }
        int idx = -1;
        for (SlideListWithText.SlideAtomsSet sas : slidesSLWT.getSlideAtomsSets()) {
            ++idx;
            SlidePersistAtom spa = sas.getSlidePersistAtom();
            Record r = this.getCoreRecordForSAS(sas);
            if (!(r instanceof Slide)) {
                LOG.atError().log("A Slide SlideAtomSet at {} said its record was at refID {}, but that was actually a {}", (Object)Unbox.box(idx), (Object)Unbox.box(spa.getRefID()), (Object)r);
                continue;
            }
            Slide slide = (Slide)r;
            if (slide.getSlideAtom() == null) {
                LOG.atError().log("SlideAtomSet at {} at refID {} is null", (Object)Unbox.box(idx), (Object)Unbox.box(spa.getRefID()));
                continue;
            }
            HSLFNotes notes = null;
            int noteId = slide.getSlideAtom().getNotesID();
            if (noteId != 0) {
                Integer notesPos = slideIdToNotes.get(noteId);
                if (notesPos != null && 0 <= notesPos && notesPos < this._notes.size()) {
                    notes = this._notes.get(notesPos);
                } else {
                    LOG.atError().log("Notes not found for noteId={}", (Object)Unbox.box(noteId));
                }
            }
            int slideIdentifier = spa.getSlideIdentifier();
            HSLFSlide hs = new HSLFSlide(slide, notes, sas, slideIdentifier, idx + 1);
            hs.setSlideShow(this);
            this._slides.add(hs);
        }
    }

    @Override
    public void write(OutputStream out) throws IOException {
        for (HSLFSlide hSLFSlide : this.getSlides()) {
            this.writeDirtyParagraphs(hSLFSlide);
        }
        for (HSLFSlideMaster hSLFSlideMaster : this.getSlideMasters()) {
            boolean isDirty = false;
            for (List<HSLFTextParagraph> paras : hSLFSlideMaster.getTextParagraphs()) {
                for (HSLFTextParagraph p : paras) {
                    isDirty |= p.isDirty();
                }
            }
            if (!isDirty) continue;
            for (TxMasterStyleAtom sa : hSLFSlideMaster.getTxMasterStyleAtoms()) {
                if (sa == null) continue;
                sa.updateStyles();
            }
        }
        this._hslfSlideShow.write(out);
    }

    private void writeDirtyParagraphs(HSLFShapeContainer container) {
        for (HSLFShape sh : container.getShapes()) {
            if (sh instanceof HSLFShapeContainer) {
                this.writeDirtyParagraphs((HSLFShapeContainer)((Object)sh));
                continue;
            }
            if (!(sh instanceof HSLFTextShape)) continue;
            HSLFTextShape hts = (HSLFTextShape)sh;
            boolean isDirty = false;
            for (HSLFTextParagraph p : hts.getTextParagraphs()) {
                isDirty |= p.isDirty();
            }
            if (!isDirty) continue;
            hts.storeText();
        }
    }

    public Record[] getMostRecentCoreRecords() {
        return this._mostRecentCoreRecords;
    }

    @Override
    public List<HSLFSlide> getSlides() {
        return this._slides;
    }

    public List<HSLFNotes> getNotes() {
        return this._notes;
    }

    @Override
    public List<HSLFSlideMaster> getSlideMasters() {
        return this._masters;
    }

    public List<HSLFTitleMaster> getTitleMasters() {
        return this._titleMasters;
    }

    @Override
    public List<HSLFPictureData> getPictureData() {
        return this._hslfSlideShow.getPictureData();
    }

    public HSLFObjectData[] getEmbeddedObjects() {
        return this._hslfSlideShow.getEmbeddedObjects();
    }

    public HSLFSoundData[] getSoundData() {
        return HSLFSoundData.find(this._documentRecord);
    }

    @Override
    public Dimension getPageSize() {
        DocumentAtom docatom = this._documentRecord.getDocumentAtom();
        int pgx = (int)Units.masterToPoints((int)docatom.getSlideSizeX());
        int pgy = (int)Units.masterToPoints((int)docatom.getSlideSizeY());
        return new Dimension(pgx, pgy);
    }

    @Override
    public void setPageSize(Dimension pgsize) {
        DocumentAtom docatom = this._documentRecord.getDocumentAtom();
        docatom.setSlideSizeX(Units.pointsToMaster(pgsize.width));
        docatom.setSlideSizeY(Units.pointsToMaster(pgsize.height));
    }

    FontCollection getFontCollection() {
        return this._fonts;
    }

    public Document getDocumentRecord() {
        return this._documentRecord;
    }

    public void reorderSlide(int oldSlideNumber, int newSlideNumber) {
        if (oldSlideNumber < 1 || newSlideNumber < 1) {
            throw new IllegalArgumentException("Old and new slide numbers must be greater than 0");
        }
        if (oldSlideNumber > this._slides.size() || newSlideNumber > this._slides.size()) {
            throw new IllegalArgumentException("Old and new slide numbers must not exceed the number of slides (" + this._slides.size() + ")");
        }
        SlideListWithText slwt = this._documentRecord.getSlideSlideListWithText();
        if (slwt == null) {
            throw new IllegalStateException("Slide record not defined.");
        }
        SlideListWithText.SlideAtomsSet[] sas = slwt.getSlideAtomsSets();
        SlideListWithText.SlideAtomsSet tmp = sas[oldSlideNumber - 1];
        sas[oldSlideNumber - 1] = sas[newSlideNumber - 1];
        sas[newSlideNumber - 1] = tmp;
        Collections.swap(this._slides, oldSlideNumber - 1, newSlideNumber - 1);
        this._slides.get(newSlideNumber - 1).setSlideNumber(newSlideNumber);
        this._slides.get(oldSlideNumber - 1).setSlideNumber(oldSlideNumber);
        ArrayList<Record> lst = new ArrayList<Record>();
        for (SlideListWithText.SlideAtomsSet s : sas) {
            lst.add(s.getSlidePersistAtom());
            lst.addAll(Arrays.asList(s.getSlideRecords()));
        }
        Record[] r = lst.toArray(new Record[0]);
        slwt.setChildRecord(r);
    }

    public HSLFSlide removeSlide(int index) {
        int lastSlideIdx = this._slides.size() - 1;
        if (index < 0 || index > lastSlideIdx) {
            throw new IllegalArgumentException("Slide index (" + index + ") is out of range (0.." + lastSlideIdx + ")");
        }
        SlideListWithText slwt = this._documentRecord.getSlideSlideListWithText();
        if (slwt == null) {
            throw new IllegalStateException("Slide record not defined.");
        }
        SlideListWithText.SlideAtomsSet[] sas = slwt.getSlideAtomsSets();
        ArrayList<Record> records = new ArrayList<Record>();
        ArrayList<SlideListWithText.SlideAtomsSet> sa = new ArrayList<SlideListWithText.SlideAtomsSet>(Arrays.asList(sas));
        HSLFSlide removedSlide = this._slides.remove(index);
        this._notes.remove(removedSlide.getNotes());
        sa.remove(index);
        int i = 0;
        for (HSLFSlide hSLFSlide : this._slides) {
            hSLFSlide.setSlideNumber(i++);
        }
        for (SlideListWithText.SlideAtomsSet slideAtomsSet : sa) {
            records.add(slideAtomsSet.getSlidePersistAtom());
            records.addAll(Arrays.asList(slideAtomsSet.getSlideRecords()));
        }
        if (sa.isEmpty()) {
            this._documentRecord.removeSlideListWithText(slwt);
        } else {
            slwt.setSlideAtomsSets(sa.toArray(new SlideListWithText.SlideAtomsSet[0]));
            slwt.setChildRecord(records.toArray(new Record[0]));
        }
        int notesId = removedSlide.getSlideRecord().getSlideAtom().getNotesID();
        if (notesId != 0) {
            SlideListWithText slideListWithText = this._documentRecord.getNotesSlideListWithText();
            records = new ArrayList();
            ArrayList<SlideListWithText.SlideAtomsSet> na = new ArrayList<SlideListWithText.SlideAtomsSet>();
            if (slideListWithText != null) {
                for (SlideListWithText.SlideAtomsSet ns : slideListWithText.getSlideAtomsSets()) {
                    if (ns.getSlidePersistAtom().getSlideIdentifier() == notesId) continue;
                    na.add(ns);
                    records.add(ns.getSlidePersistAtom());
                    if (ns.getSlideRecords() == null) continue;
                    records.addAll(Arrays.asList(ns.getSlideRecords()));
                }
                if (!na.isEmpty()) {
                    slideListWithText.setSlideAtomsSets(na.toArray(new SlideListWithText.SlideAtomsSet[0]));
                    slideListWithText.setChildRecord(records.toArray(new Record[0]));
                }
            }
            if (na.isEmpty()) {
                this._documentRecord.removeSlideListWithText(slideListWithText);
            }
        }
        return removedSlide;
    }

    public HSLFSlide createSlide() {
        SlideListWithText slist = this._documentRecord.getSlideSlideListWithText();
        if (slist == null) {
            slist = new SlideListWithText();
            slist.setInstance(0);
            this._documentRecord.addSlideListWithText(slist);
        }
        SlidePersistAtom prev = null;
        for (SlideListWithText.SlideAtomsSet sas : slist.getSlideAtomsSets()) {
            SlidePersistAtom spa = sas.getSlidePersistAtom();
            if (spa.getSlideIdentifier() < 0) continue;
            if (prev == null) {
                prev = spa;
            }
            if (prev.getSlideIdentifier() >= spa.getSlideIdentifier()) continue;
            prev = spa;
        }
        SlidePersistAtom sp = new SlidePersistAtom();
        sp.setSlideIdentifier(prev == null ? 256 : prev.getSlideIdentifier() + 1);
        slist.addSlidePersistAtom(sp);
        HSLFSlide slide = new HSLFSlide(sp.getSlideIdentifier(), sp.getRefID(), this._slides.size() + 1);
        slide.setSlideShow(this);
        slide.onCreate();
        this._slides.add(slide);
        LOG.atInfo().log("Added slide {} with ref {} and identifier {}", (Object)Unbox.box(this._slides.size()), (Object)Unbox.box(sp.getRefID()), (Object)Unbox.box(sp.getSlideIdentifier()));
        Slide slideRecord = slide.getSlideRecord();
        int psrId = this.addPersistentObject(slideRecord);
        sp.setRefID(psrId);
        slideRecord.setSheetId(psrId);
        slide.setMasterSheet(this._masters.get(0));
        return slide;
    }

    @Override
    public HSLFPictureData addPicture(byte[] data, PictureData.PictureType format) throws IOException {
        if (format == null || format.nativeId == -1) {
            throw new IllegalArgumentException("Unsupported picture format: " + (Object)((Object)format));
        }
        HSLFPictureData pd = this.findPictureData(data);
        if (pd != null) {
            return pd;
        }
        EscherContainerRecord dggContainer = this._documentRecord.getPPDrawingGroup().getDggContainer();
        EscherContainerRecord bstore = (EscherContainerRecord)HSLFShape.getEscherChild(dggContainer, EscherContainerRecord.BSTORE_CONTAINER);
        if (bstore == null) {
            bstore = new EscherContainerRecord();
            bstore.setRecordId(EscherContainerRecord.BSTORE_CONTAINER);
            dggContainer.addChildBefore(bstore, EscherOptRecord.RECORD_ID);
        }
        EscherBSERecord bse = HSLFSlideShow.addNewEscherBseRecord(bstore, format, data, 0);
        HSLFPictureData pict = HSLFPictureData.createFromImageData(format, bstore, bse, data);
        int offset = this._hslfSlideShow.addPicture(pict);
        bse.setOffset(offset);
        return pict;
    }

    @Override
    public HSLFPictureData addPicture(InputStream is, PictureData.PictureType format) throws IOException {
        if (format == null || format.nativeId == -1) {
            throw new IllegalArgumentException("Unsupported picture format: " + (Object)((Object)format));
        }
        return this.addPicture(IOUtils.toByteArray(is), format);
    }

    @Override
    public HSLFPictureData addPicture(File pict, PictureData.PictureType format) throws IOException {
        if (format == null || format.nativeId == -1) {
            throw new IllegalArgumentException("Unsupported picture format: " + (Object)((Object)format));
        }
        byte[] data = IOUtils.safelyAllocate(pict.length(), MAX_RECORD_LENGTH);
        try (FileInputStream is = new FileInputStream(pict);){
            IOUtils.readFully(is, data);
        }
        return this.addPicture(data, format);
    }

    @Override
    public HSLFPictureData findPictureData(byte[] pictureData) {
        byte[] uid = HSLFPictureData.getChecksum(pictureData);
        for (HSLFPictureData pic : this.getPictureData()) {
            if (!Arrays.equals(pic.getUID(), uid)) continue;
            return pic;
        }
        return null;
    }

    public HSLFFontInfo addFont(FontInfo fontInfo) {
        return this.getDocumentRecord().getEnvironment().getFontCollection().addFont(fontInfo);
    }

    @Override
    public HSLFFontInfo addFont(InputStream fontData) throws IOException {
        Document doc = this.getDocumentRecord();
        doc.getDocumentAtom().setSaveWithFonts(true);
        return doc.getEnvironment().getFontCollection().addFont(fontData);
    }

    public HSLFFontInfo getFont(int idx) {
        return this.getDocumentRecord().getEnvironment().getFontCollection().getFontInfo(idx);
    }

    public int getNumberOfFonts() {
        return this.getDocumentRecord().getEnvironment().getFontCollection().getNumberOfFonts();
    }

    @Override
    public List<HSLFFontInfo> getFonts() {
        return this.getDocumentRecord().getEnvironment().getFontCollection().getFonts();
    }

    public HeadersFooters getSlideHeadersFooters() {
        return new HeadersFooters(this, 63);
    }

    public HeadersFooters getNotesHeadersFooters() {
        if (this._notes.isEmpty()) {
            return new HeadersFooters(this, 79);
        }
        return new HeadersFooters(this._notes.get(0), 79);
    }

    public int addMovie(String path, int type) {
        ExMCIMovie mci;
        switch (type) {
            case 1: {
                mci = new ExMCIMovie();
                break;
            }
            case 2: {
                mci = new ExAviMovie();
                break;
            }
            default: {
                throw new IllegalArgumentException("Unsupported Movie: " + type);
            }
        }
        ExVideoContainer exVideo = mci.getExVideo();
        exVideo.getExMediaAtom().setMask(0xE80000);
        exVideo.getPathAtom().setText(path);
        int objectId = this.addToObjListAtom(mci);
        exVideo.getExMediaAtom().setObjectId(objectId);
        return objectId;
    }

    public int addControl(String name, String progId) {
        ExControl ctrl = new ExControl();
        ctrl.setProgId(progId);
        ctrl.setMenuName(name);
        ctrl.setClipboardName(name);
        ExOleObjAtom oleObj = ctrl.getExOleObjAtom();
        oleObj.setDrawAspect(1);
        oleObj.setType(2);
        oleObj.setSubType(0);
        int objectId = this.addToObjListAtom(ctrl);
        oleObj.setObjID(objectId);
        return objectId;
    }

    public int addEmbed(POIFSFileSystem poiData) {
        DirectoryNode root = poiData.getRoot();
        if (new ClassID().equals(root.getStorageClsid())) {
            Map<String, ClassID> olemap = HSLFSlideShow.getOleMap();
            ClassID classID = null;
            for (Map.Entry<String, ClassID> entry : olemap.entrySet()) {
                if (!root.hasEntry(entry.getKey())) continue;
                classID = entry.getValue();
                break;
            }
            if (classID == null) {
                throw new IllegalArgumentException("Unsupported embedded document");
            }
            root.setStorageClsid(classID);
        }
        ExEmbed exEmbed = new ExEmbed();
        Record[] children = exEmbed.getChildRecords();
        exEmbed.removeChild(children[2]);
        exEmbed.removeChild(children[3]);
        exEmbed.removeChild(children[4]);
        ExEmbedAtom eeEmbed = exEmbed.getExEmbedAtom();
        eeEmbed.setCantLockServerB(true);
        ExOleObjAtom eeAtom = exEmbed.getExOleObjAtom();
        eeAtom.setDrawAspect(1);
        eeAtom.setType(0);
        eeAtom.setOptions(1226240);
        ExOleObjStg exOleObjStg = new ExOleObjStg();
        try {
            Ole10Native.createOleMarkerEntry(poiData);
            UnsynchronizedByteArrayOutputStream bos = new UnsynchronizedByteArrayOutputStream();
            poiData.writeFilesystem((OutputStream)bos);
            exOleObjStg.setData(bos.toByteArray());
        }
        catch (IOException e) {
            throw new HSLFException(e);
        }
        int psrId = this.addPersistentObject(exOleObjStg);
        exOleObjStg.setPersistId(psrId);
        eeAtom.setObjStgDataRef(psrId);
        int objectId = this.addToObjListAtom(exEmbed);
        eeAtom.setObjID(objectId);
        return objectId;
    }

    @Override
    public HPSFPropertiesExtractor getMetadataTextExtractor() {
        return new HPSFPropertiesExtractor(this.getSlideShowImpl());
    }

    int addToObjListAtom(RecordContainer exObj) {
        ExObjList lst = this.getDocumentRecord().getExObjList(true);
        ExObjListAtom objAtom = lst.getExObjListAtom();
        int objectId = (int)objAtom.getObjectIDSeed() + 1;
        objAtom.setObjectIDSeed(objectId);
        lst.addChildAfter(exObj, objAtom);
        return objectId;
    }

    private static Map<String, ClassID> getOleMap() {
        HashMap<String, ClassID> olemap = new HashMap<String, ClassID>();
        olemap.put(POWERPOINT_DOCUMENT, ClassIDPredefined.POWERPOINT_V8.getClassID());
        olemap.put("Workbook", ClassIDPredefined.EXCEL_V8.getClassID());
        olemap.put("WORKBOOK", ClassIDPredefined.EXCEL_V8.getClassID());
        olemap.put("BOOK", ClassIDPredefined.EXCEL_V8.getClassID());
        return olemap;
    }

    private int addPersistentObject(PositionDependentRecord slideRecord) {
        slideRecord.setLastOnDiskOffset(-1);
        this._hslfSlideShow.appendRootLevelRecord((Record)((Object)slideRecord));
        HashMap<RecordTypes, PositionDependentRecord> interestingRecords = new HashMap<RecordTypes, PositionDependentRecord>();
        try {
            this._hslfSlideShow.updateAndWriteDependantRecords(null, interestingRecords);
        }
        catch (IOException e) {
            throw new HSLFException(e);
        }
        PersistPtrHolder ptr = (PersistPtrHolder)interestingRecords.get((Object)RecordTypes.PersistPtrIncrementalBlock);
        UserEditAtom usr = (UserEditAtom)interestingRecords.get((Object)RecordTypes.UserEditAtom);
        int psrId = usr.getMaxPersistWritten() + 1;
        usr.setLastViewType((short)1);
        usr.setMaxPersistWritten(psrId);
        int slideOffset = slideRecord.getLastOnDiskOffset();
        slideRecord.setLastOnDiskOffset(slideOffset);
        ptr.addSlideLookup(psrId, slideOffset);
        LOG.atInfo().log("New slide/object ended up at {}", (Object)Unbox.box(slideOffset));
        return psrId;
    }

    @Override
    public MasterSheet<HSLFShape, HSLFTextParagraph> createMasterSheet() {
        return null;
    }

    @Internal
    public HSLFSlideShowImpl getSlideShowImpl() {
        return this._hslfSlideShow;
    }

    @Override
    public void close() throws IOException {
        this._hslfSlideShow.close();
    }

    @Override
    public Object getPersistDocument() {
        return this.getSlideShowImpl();
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("pictures", this::getPictureData, "embeddedObjects", this::getEmbeddedObjects);
    }

    @Override
    public List<? extends GenericRecord> getGenericChildren() {
        return Arrays.asList(this._hslfSlideShow.getRecords());
    }

    @Override
    public void write() throws IOException {
        this.getSlideShowImpl().write();
    }

    @Override
    public void write(File newFile) throws IOException {
        this.getSlideShowImpl().write(newFile);
    }

    @Override
    public DocumentSummaryInformation getDocumentSummaryInformation() {
        return this.getSlideShowImpl().getDocumentSummaryInformation();
    }

    @Override
    public SummaryInformation getSummaryInformation() {
        return this.getSlideShowImpl().getSummaryInformation();
    }

    @Override
    public void createInformationProperties() {
        this.getSlideShowImpl().createInformationProperties();
    }

    @Override
    public void readProperties() {
        this.getSlideShowImpl().readProperties();
    }

    @Override
    protected PropertySet getPropertySet(String setName) throws IOException {
        return this.getSlideShowImpl().getPropertySetImpl(setName);
    }

    @Override
    protected PropertySet getPropertySet(String setName, EncryptionInfo encryptionInfo) throws IOException {
        return this.getSlideShowImpl().getPropertySetImpl(setName, encryptionInfo);
    }

    @Override
    protected void writeProperties() throws IOException {
        this.getSlideShowImpl().writePropertiesImpl();
    }

    @Override
    public void writeProperties(POIFSFileSystem outFS) throws IOException {
        this.getSlideShowImpl().writeProperties(outFS);
    }

    @Override
    protected void writeProperties(POIFSFileSystem outFS, List<String> writtenEntries) throws IOException {
        this.getSlideShowImpl().writePropertiesImpl(outFS, writtenEntries);
    }

    @Override
    protected void validateInPlaceWritePossible() throws IllegalStateException {
        this.getSlideShowImpl().validateInPlaceWritePossibleImpl();
    }

    @Override
    public DirectoryNode getDirectory() {
        return this.getSlideShowImpl().getDirectory();
    }

    @Override
    protected void clearDirectory() {
        this.getSlideShowImpl().clearDirectoryImpl();
    }

    @Override
    protected boolean initDirectory() {
        return this.getSlideShowImpl().initDirectoryImpl();
    }

    @Override
    protected void replaceDirectory(DirectoryNode newDirectory) throws IOException {
        this.getSlideShowImpl().replaceDirectoryImpl(newDirectory);
    }

    @Override
    protected String getEncryptedPropertyStreamName() {
        return this.getSlideShowImpl().getEncryptedPropertyStreamName();
    }

    @Override
    public EncryptionInfo getEncryptionInfo() {
        return this.getSlideShowImpl().getEncryptionInfo();
    }

    static EscherBSERecord addNewEscherBseRecord(EscherContainerRecord blipStore, PictureData.PictureType type, byte[] imageData, int offset) {
        EscherBSERecord record = new EscherBSERecord();
        record.setRecordId(EscherBSERecord.RECORD_ID);
        record.setOptions((short)(2 | type.nativeId << 4));
        record.setSize(imageData.length + 8);
        record.setUid(Arrays.copyOf(imageData, 16));
        record.setBlipTypeMacOS((byte)type.nativeId);
        record.setBlipTypeWin32((byte)type.nativeId);
        if (type == PictureData.PictureType.EMF) {
            record.setBlipTypeMacOS((byte)PictureData.PictureType.PICT.nativeId);
        } else if (type == PictureData.PictureType.WMF) {
            record.setBlipTypeMacOS((byte)PictureData.PictureType.PICT.nativeId);
        } else if (type == PictureData.PictureType.PICT) {
            record.setBlipTypeWin32((byte)PictureData.PictureType.WMF.nativeId);
        }
        record.setOffset(offset);
        blipStore.addChildRecord(record);
        int count = blipStore.getChildCount();
        blipStore.setOptions((short)(count << 4 | 0xF));
        return record;
    }

    static enum LoadSavePhase {
        INIT,
        LOADED;

    }
}

