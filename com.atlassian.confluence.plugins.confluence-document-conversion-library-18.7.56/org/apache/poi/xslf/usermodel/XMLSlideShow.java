/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xslf.usermodel;

import java.awt.Dimension;
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
import java.util.OptionalLong;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import org.apache.poi.ooxml.extractor.POIXMLPropertiesTextExtractor;
import org.apache.poi.ooxml.util.PackageHelper;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.sl.usermodel.MasterSheet;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.sl.usermodel.SlideShow;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.Units;
import org.apache.poi.xslf.usermodel.SlideLayout;
import org.apache.poi.xslf.usermodel.XSLFChart;
import org.apache.poi.xslf.usermodel.XSLFCommentAuthors;
import org.apache.poi.xslf.usermodel.XSLFFactory;
import org.apache.poi.xslf.usermodel.XSLFFontInfo;
import org.apache.poi.xslf.usermodel.XSLFGroupShape;
import org.apache.poi.xslf.usermodel.XSLFNotes;
import org.apache.poi.xslf.usermodel.XSLFNotesMaster;
import org.apache.poi.xslf.usermodel.XSLFPictureData;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFRelation;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFShapeContainer;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFSlideLayout;
import org.apache.poi.xslf.usermodel.XSLFSlideMaster;
import org.apache.poi.xslf.usermodel.XSLFTableStyles;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTheme;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.presentationml.x2006.main.CTNotesMasterIdList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTNotesMasterIdListEntry;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPresentation;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideIdList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideIdListEntry;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideSize;
import org.openxmlformats.schemas.presentationml.x2006.main.PresentationDocument;

public class XMLSlideShow
extends POIXMLDocument
implements SlideShow<XSLFShape, XSLFTextParagraph> {
    private static final Logger LOG = LogManager.getLogger(XMLSlideShow.class);
    private static final int DEFAULT_MAX_RECORD_LENGTH = 1000000;
    private static int MAX_RECORD_LENGTH = 1000000;
    private static final Pattern GET_ALL_EMBEDDED_PARTS_PATTERN = Pattern.compile("/ppt/embeddings/.*?");
    private static final Pattern GET_PICTURE_DATA_PATTERN = Pattern.compile("/ppt/media/.*?");
    private CTPresentation _presentation;
    private final List<XSLFSlide> _slides = new ArrayList<XSLFSlide>();
    private final List<XSLFSlideMaster> _masters = new ArrayList<XSLFSlideMaster>();
    private final List<XSLFPictureData> _pictures = new ArrayList<XSLFPictureData>();
    private final List<XSLFChart> _charts = new ArrayList<XSLFChart>();
    private XSLFTableStyles _tableStyles;
    private XSLFNotesMaster _notesMaster;
    private XSLFCommentAuthors _commentAuthors;

    public static void setMaxRecordLength(int length) {
        MAX_RECORD_LENGTH = length;
    }

    public static int getMaxRecordLength() {
        return MAX_RECORD_LENGTH;
    }

    public XMLSlideShow() {
        this(XMLSlideShow.empty());
    }

    public XMLSlideShow(OPCPackage pkg) {
        super(pkg);
        try {
            if (this.getCorePart().getContentType().equals(XSLFRelation.THEME_MANAGER.getContentType())) {
                this.rebase(this.getPackage());
            }
            this.load(XSLFFactory.getInstance());
        }
        catch (Exception e) {
            throw new POIXMLException(e);
        }
    }

    public XMLSlideShow(InputStream is) throws IOException {
        this(PackageHelper.open(is));
    }

    static OPCPackage empty() {
        InputStream is = XMLSlideShow.class.getResourceAsStream("empty.pptx");
        if (is == null) {
            throw new POIXMLException("Missing resource 'empty.pptx'");
        }
        try {
            OPCPackage oPCPackage = OPCPackage.open(is);
            return oPCPackage;
        }
        catch (Exception e) {
            throw new POIXMLException(e);
        }
        finally {
            IOUtils.closeQuietly(is);
        }
    }

    @Override
    protected void onDocumentRead() throws IOException {
        try {
            try (InputStream stream = this.getCorePart().getInputStream();){
                PresentationDocument doc = (PresentationDocument)PresentationDocument.Factory.parse(stream, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
                this._presentation = doc.getPresentation();
            }
            HashMap<String, XSLFSlideMaster> masterMap = new HashMap<String, XSLFSlideMaster>();
            HashMap<String, XSLFSlide> shIdMap = new HashMap<String, XSLFSlide>();
            HashMap<String, XSLFChart> chartMap = new HashMap<String, XSLFChart>();
            for (POIXMLDocumentPart.RelationPart rp : this.getRelationParts()) {
                Object p = rp.getDocumentPart();
                if (p instanceof XSLFSlide) {
                    shIdMap.put(rp.getRelationship().getId(), (XSLFSlide)p);
                    for (POIXMLDocumentPart c : ((POIXMLDocumentPart)p).getRelations()) {
                        if (!(c instanceof XSLFChart)) continue;
                        chartMap.put(c.getPackagePart().getPartName().getName(), (XSLFChart)c);
                    }
                    continue;
                }
                if (p instanceof XSLFSlideMaster) {
                    masterMap.put(this.getRelationId((POIXMLDocumentPart)p), (XSLFSlideMaster)p);
                    continue;
                }
                if (p instanceof XSLFTableStyles) {
                    this._tableStyles = (XSLFTableStyles)p;
                    continue;
                }
                if (p instanceof XSLFNotesMaster) {
                    this._notesMaster = (XSLFNotesMaster)p;
                    continue;
                }
                if (!(p instanceof XSLFCommentAuthors)) continue;
                this._commentAuthors = (XSLFCommentAuthors)p;
            }
            this._charts.clear();
            this._charts.addAll(chartMap.values());
            this._masters.clear();
            if (this._presentation.isSetSldMasterIdLst()) {
                this._presentation.getSldMasterIdLst().getSldMasterIdList().forEach(id -> this._masters.add((XSLFSlideMaster)masterMap.get(id.getId2())));
            }
            this._slides.clear();
            if (this._presentation.isSetSldIdLst()) {
                this._presentation.getSldIdLst().getSldIdList().forEach(id -> {
                    XSLFSlide sh = (XSLFSlide)shIdMap.get(id.getId2());
                    if (sh == null) {
                        LOG.atWarn().log("Slide with r:id {} was defined, but didn't exist in package, skipping", (Object)Unbox.box(id.getId()));
                    } else {
                        this._slides.add(sh);
                    }
                });
            }
        }
        catch (XmlException e) {
            throw new POIXMLException(e);
        }
    }

    @Override
    protected void commit() throws IOException {
        PackagePart part = this.getPackagePart();
        try (OutputStream out = part.getOutputStream();){
            this._presentation.save(out, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        }
    }

    @Override
    public List<PackagePart> getAllEmbeddedParts() {
        return Collections.unmodifiableList(this.getPackage().getPartsByName(GET_ALL_EMBEDDED_PARTS_PATTERN));
    }

    @Override
    public List<XSLFPictureData> getPictureData() {
        if (this._pictures.isEmpty()) {
            this.getPackage().getPartsByName(GET_PICTURE_DATA_PATTERN).forEach(part -> {
                XSLFPictureData pd = new XSLFPictureData((PackagePart)part);
                pd.setIndex(this._pictures.size());
                this._pictures.add(pd);
            });
        }
        return Collections.unmodifiableList(this._pictures);
    }

    public XSLFSlide createSlide(XSLFSlideLayout layout) {
        CTSlideIdList slideList = this._presentation.isSetSldIdLst() ? this._presentation.getSldIdLst() : this._presentation.addNewSldIdLst();
        OptionalLong maxId = Stream.of(slideList.getSldIdArray()).mapToLong(CTSlideIdListEntry::getId).max();
        XSLFRelation relationType = XSLFRelation.SLIDE;
        int slideNumber = (int)(Math.max(maxId.orElse(0L), 255L) + 1L);
        int cnt = this.findNextAvailableFileNameIndex(relationType);
        POIXMLDocumentPart.RelationPart rp = this.createRelationship(relationType, XSLFFactory.getInstance(), cnt, false);
        XSLFSlide slide = (XSLFSlide)rp.getDocumentPart();
        CTSlideIdListEntry slideId = slideList.addNewSldId();
        slideId.setId(slideNumber);
        slideId.setId2(rp.getRelationship().getId());
        layout.copyLayout(slide);
        slide.getPackagePart().clearRelationships();
        slide.addRelation(null, XSLFRelation.SLIDE_LAYOUT, layout);
        this._slides.add(slide);
        return slide;
    }

    private int findNextAvailableFileNameIndex(XSLFRelation relationType) {
        try {
            return this.getPackage().getUnusedPartIndex(relationType.getDefaultFileName());
        }
        catch (InvalidFormatException e) {
            throw new RuntimeException(e);
        }
    }

    public XSLFSlide createSlide() {
        XSLFSlideMaster sm = this._masters.get(0);
        XSLFSlideLayout layout = sm.getLayout(SlideLayout.BLANK);
        if (layout == null) {
            LOG.atWarn().log("Blank layout was not found - defaulting to first slide layout in master");
            XSLFSlideLayout[] sl = sm.getSlideLayouts();
            if (sl.length == 0) {
                throw new POIXMLException("SlideMaster must contain a SlideLayout.");
            }
            layout = sl[0];
        }
        return this.createSlide(layout);
    }

    public XSLFChart createChart(XSLFSlide slide) {
        XSLFChart chart = this.createChart();
        slide.addRelation(null, XSLFRelation.CHART, chart);
        return chart;
    }

    public XSLFChart createChart() {
        int chartIdx = this.findNextAvailableFileNameIndex(XSLFRelation.CHART);
        XSLFChart chart = (XSLFChart)this.createRelationship(XSLFRelation.CHART, XSLFFactory.getInstance(), chartIdx, true).getDocumentPart();
        chart.setChartIndex(chartIdx);
        this._charts.add(chart);
        return chart;
    }

    public XSLFNotes getNotesSlide(XSLFSlide slide) {
        XSLFNotes notesSlide = slide.getNotes();
        if (notesSlide == null) {
            notesSlide = this.createNotesSlide(slide);
        }
        return notesSlide;
    }

    private XSLFNotes createNotesSlide(XSLFSlide slide) {
        if (this._notesMaster == null) {
            this.createNotesMaster();
        }
        XSLFRelation relationType = XSLFRelation.NOTES;
        int slideIndex = this.findNextAvailableFileNameIndex(relationType);
        XSLFNotes notesSlide = (XSLFNotes)this.createRelationship(relationType, XSLFFactory.getInstance(), slideIndex);
        slide.addRelation(null, relationType, notesSlide);
        notesSlide.addRelation(null, XSLFRelation.NOTES_MASTER, this._notesMaster);
        notesSlide.addRelation(null, XSLFRelation.SLIDE, slide);
        notesSlide.importContent(this._notesMaster);
        return notesSlide;
    }

    public void createNotesMaster() {
        POIXMLDocumentPart.RelationPart rp = this.createRelationship(XSLFRelation.NOTES_MASTER, XSLFFactory.getInstance(), 1, false);
        this._notesMaster = (XSLFNotesMaster)rp.getDocumentPart();
        CTNotesMasterIdList notesMasterIdList = this._presentation.addNewNotesMasterIdLst();
        CTNotesMasterIdListEntry notesMasterId = notesMasterIdList.addNewNotesMasterId();
        notesMasterId.setId(rp.getRelationship().getId());
        int themeIndex = 1;
        ArrayList<Integer> themeIndexList = new ArrayList<Integer>();
        for (POIXMLDocumentPart p : this.getRelations()) {
            if (!(p instanceof XSLFTheme)) continue;
            themeIndexList.add(XSLFRelation.THEME.getFileNameIndex(p));
        }
        if (!themeIndexList.isEmpty()) {
            boolean found = false;
            for (int i = 1; i <= themeIndexList.size(); ++i) {
                if (themeIndexList.contains(i)) continue;
                found = true;
                themeIndex = i;
            }
            if (!found) {
                themeIndex = themeIndexList.size() + 1;
            }
        }
        XSLFTheme theme = (XSLFTheme)this.createRelationship(XSLFRelation.THEME, XSLFFactory.getInstance(), themeIndex);
        theme.importTheme(this.getSlides().get(0).getTheme());
        this._notesMaster.addRelation(null, XSLFRelation.THEME, theme);
    }

    public XSLFNotesMaster getNotesMaster() {
        return this._notesMaster;
    }

    @Override
    public List<XSLFSlideMaster> getSlideMasters() {
        return this._masters;
    }

    @Override
    public List<XSLFSlide> getSlides() {
        return this._slides;
    }

    public List<XSLFChart> getCharts() {
        return Collections.unmodifiableList(this._charts);
    }

    public XSLFCommentAuthors getCommentAuthors() {
        return this._commentAuthors;
    }

    public void setSlideOrder(XSLFSlide slide, int newIndex) {
        int oldIndex = this._slides.indexOf(slide);
        if (oldIndex == -1) {
            throw new IllegalArgumentException("Slide not found");
        }
        if (oldIndex == newIndex) {
            return;
        }
        this._slides.add(newIndex, this._slides.remove(oldIndex));
        CTSlideIdList sldIdLst = this._presentation.getSldIdLst();
        CTSlideIdListEntry[] entries = sldIdLst.getSldIdArray();
        CTSlideIdListEntry oldEntry = entries[oldIndex];
        if (oldIndex < newIndex) {
            System.arraycopy(entries, oldIndex + 1, entries, oldIndex, newIndex - oldIndex);
        } else {
            System.arraycopy(entries, newIndex, entries, newIndex + 1, oldIndex - newIndex);
        }
        entries[newIndex] = oldEntry;
        sldIdLst.setSldIdArray(entries);
    }

    public XSLFSlide removeSlide(int index) {
        XSLFSlide slide = this._slides.remove(index);
        this.removeRelation(slide);
        this._presentation.getSldIdLst().removeSldId(index);
        for (POIXMLDocumentPart p : slide.getRelations()) {
            if (p instanceof XSLFChart) {
                XSLFChart chart = (XSLFChart)p;
                slide.removeChartRelation(chart);
                this._charts.remove(chart);
                continue;
            }
            if (p instanceof XSLFSlideLayout) {
                XSLFSlideLayout layout = (XSLFSlideLayout)p;
                slide.removeLayoutRelation(layout);
                continue;
            }
            if (p instanceof XSLFNotes) {
                XSLFNotes notes = slide.removeNotes(this._notesMaster);
                this.removeRelation(notes);
                continue;
            }
            if (!(p instanceof XSLFPictureData)) continue;
            XSLFPictureData picture = (XSLFPictureData)p;
            this.removePictureRelations(slide, picture);
            this._pictures.remove(picture);
        }
        return slide;
    }

    private void removePictureRelations(XSLFSlide slide, XSLFPictureData picture) {
        this.removePictureRelations(slide, slide, picture);
    }

    private void removePictureRelations(XSLFSlide slide, XSLFShapeContainer container, XSLFPictureData picture) {
        for (XSLFShape shape : container.getShapes()) {
            XSLFPictureShape pic;
            if (shape instanceof XSLFGroupShape) {
                this.removePictureRelations(slide, (XSLFGroupShape)shape, picture);
            }
            if (!(shape instanceof XSLFPictureShape) || (pic = (XSLFPictureShape)shape).getPictureData() != picture) continue;
            slide.removePictureRelation(pic);
        }
    }

    @Override
    public Dimension getPageSize() {
        CTSlideSize sz = this._presentation.getSldSz();
        int cx = sz.getCx();
        int cy = sz.getCy();
        return new Dimension((int)Units.toPoints(cx), (int)Units.toPoints(cy));
    }

    @Override
    public void setPageSize(Dimension pgSize) {
        CTSlideSize sz = CTSlideSize.Factory.newInstance();
        sz.setCx(Units.toEMU(pgSize.getWidth()));
        sz.setCy(Units.toEMU(pgSize.getHeight()));
        this._presentation.setSldSz(sz);
    }

    @Internal
    public CTPresentation getCTPresentation() {
        return this._presentation;
    }

    @Override
    public XSLFPictureData addPicture(byte[] pictureData, PictureData.PictureType format) {
        int imageNumber;
        XSLFPictureData img = this.findPictureData(pictureData);
        if (img != null) {
            return img;
        }
        XSLFRelation relType = XSLFPictureData.getRelationForType(format);
        if (relType == null) {
            throw new IllegalArgumentException("Picture type " + (Object)((Object)format) + " is not supported.");
        }
        try {
            imageNumber = this.getPackage().getUnusedPartIndex("/ppt/media/image#\\..+");
        }
        catch (InvalidFormatException e) {
            imageNumber = this._pictures.size() + 1;
        }
        img = (XSLFPictureData)this.createRelationship(relType, XSLFFactory.getInstance(), imageNumber, true).getDocumentPart();
        img.setIndex(this._pictures.size());
        this._pictures.add(img);
        try (OutputStream out = img.getPackagePart().getOutputStream();){
            out.write(pictureData);
        }
        catch (IOException e) {
            throw new POIXMLException(e);
        }
        return img;
    }

    @Override
    public XSLFPictureData addPicture(InputStream is, PictureData.PictureType format) throws IOException {
        return this.addPicture(IOUtils.toByteArrayWithMaxLength(is, XSLFPictureData.getMaxImageSize()), format);
    }

    @Override
    public XSLFPictureData addPicture(File pict, PictureData.PictureType format) throws IOException {
        byte[] data = IOUtils.safelyAllocate(pict.length(), MAX_RECORD_LENGTH);
        try (FileInputStream is = new FileInputStream(pict);){
            IOUtils.readFully(is, data);
        }
        return this.addPicture(data, format);
    }

    @Override
    public XSLFPictureData findPictureData(byte[] pictureData) {
        long checksum = IOUtils.calculateChecksum(pictureData);
        byte[] cs = new byte[8];
        LittleEndian.putLong(cs, 0, checksum);
        for (XSLFPictureData pic : this.getPictureData()) {
            if (!Arrays.equals(pic.getChecksum(), cs)) continue;
            return pic;
        }
        return null;
    }

    public XSLFSlideLayout findLayout(String name) {
        for (XSLFSlideMaster master : this.getSlideMasters()) {
            XSLFSlideLayout layout = master.getLayout(name);
            if (layout == null) continue;
            return layout;
        }
        return null;
    }

    public XSLFTableStyles getTableStyles() {
        return this._tableStyles;
    }

    @Override
    public MasterSheet<XSLFShape, XSLFTextParagraph> createMasterSheet() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public POIXMLPropertiesTextExtractor getMetadataTextExtractor() {
        return new POIXMLPropertiesTextExtractor(this);
    }

    @Override
    public Object getPersistDocument() {
        return this;
    }

    @Override
    public XSLFFontInfo addFont(InputStream fontStream) throws IOException {
        return XSLFFontInfo.addFontToSlideShow(this, fontStream);
    }

    @Override
    public List<XSLFFontInfo> getFonts() {
        return XSLFFontInfo.getFonts(this);
    }

    String importBlip(String blipId, POIXMLDocumentPart parent, POIXMLDocumentPart target) {
        OPCPackage targetPackage = target.getPackagePart().getPackage();
        if (targetPackage != this.getPackage()) {
            throw new RuntimeException("the target document part is not a child of this package");
        }
        Object docPart = parent.getRelationPartById(blipId).getDocumentPart();
        if (!(docPart instanceof XSLFPictureData)) {
            throw new RuntimeException("cannot import blip " + blipId + " - its document part is not XSLFPictureData");
        }
        XSLFPictureData parData = (XSLFPictureData)docPart;
        XSLFPictureData pictureData = targetPackage == parent.getPackagePart().getPackage() ? parData : this.addPicture(parData.getData(), parData.getType());
        POIXMLDocumentPart.RelationPart rp = target.addRelation(null, XSLFRelation.IMAGES, pictureData);
        return rp.getRelationship().getId();
    }
}

