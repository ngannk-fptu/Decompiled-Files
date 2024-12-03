/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.usermodel;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherDgRecord;
import org.apache.poi.ddf.EscherDggRecord;
import org.apache.poi.ddf.EscherRecordTypes;
import org.apache.poi.ddf.EscherSpRecord;
import org.apache.poi.hslf.exceptions.HSLFException;
import org.apache.poi.hslf.model.HeadersFooters;
import org.apache.poi.hslf.record.CString;
import org.apache.poi.hslf.record.ColorSchemeAtom;
import org.apache.poi.hslf.record.Comment2000;
import org.apache.poi.hslf.record.EscherTextboxWrapper;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.hslf.record.RecordContainer;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.hslf.record.SSSlideInfoAtom;
import org.apache.poi.hslf.record.Slide;
import org.apache.poi.hslf.record.SlideAtom;
import org.apache.poi.hslf.record.SlideAtomLayout;
import org.apache.poi.hslf.record.SlideListWithText;
import org.apache.poi.hslf.record.StyleTextProp9Atom;
import org.apache.poi.hslf.usermodel.HSLFBackground;
import org.apache.poi.hslf.usermodel.HSLFComment;
import org.apache.poi.hslf.usermodel.HSLFMasterSheet;
import org.apache.poi.hslf.usermodel.HSLFNotes;
import org.apache.poi.hslf.usermodel.HSLFPlaceholder;
import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFSheet;
import org.apache.poi.hslf.usermodel.HSLFSlideMaster;
import org.apache.poi.hslf.usermodel.HSLFTextBox;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.hslf.usermodel.HSLFTextShape;
import org.apache.poi.hslf.usermodel.HSLFTitleMaster;
import org.apache.poi.sl.draw.DrawFactory;
import org.apache.poi.sl.draw.DrawSlide;
import org.apache.poi.sl.usermodel.Notes;
import org.apache.poi.sl.usermodel.Placeholder;
import org.apache.poi.sl.usermodel.ShapeType;
import org.apache.poi.sl.usermodel.SimpleShape;
import org.apache.poi.sl.usermodel.TextShape;

public final class HSLFSlide
extends HSLFSheet
implements org.apache.poi.sl.usermodel.Slide<HSLFShape, HSLFTextParagraph> {
    private int _slideNo;
    private SlideListWithText.SlideAtomsSet _atomSet;
    private final List<List<HSLFTextParagraph>> _paragraphs = new ArrayList<List<HSLFTextParagraph>>();
    private HSLFNotes _notes;

    public HSLFSlide(Slide slide, HSLFNotes notes, SlideListWithText.SlideAtomsSet atomSet, int slideIdentifier, int slideNumber) {
        super(slide, slideIdentifier);
        this._notes = notes;
        this._atomSet = atomSet;
        this._slideNo = slideNumber;
        if (this._atomSet != null && this._atomSet.getSlideRecords().length > 0) {
            this._paragraphs.addAll(HSLFTextParagraph.findTextParagraphs(this._atomSet.getSlideRecords()));
            if (this._paragraphs.isEmpty()) {
                throw new HSLFException("No text records found for slide");
            }
        }
        for (List<HSLFTextParagraph> l : HSLFTextParagraph.findTextParagraphs(this.getPPDrawing(), (HSLFSheet)this)) {
            if (this._paragraphs.contains(l)) continue;
            this._paragraphs.add(l);
        }
    }

    public HSLFSlide(int sheetNumber, int sheetRefId, int slideNumber) {
        super(new Slide(), sheetNumber);
        this._slideNo = slideNumber;
        this.getSheetContainer().setSheetId(sheetRefId);
    }

    public HSLFNotes getNotes() {
        return this._notes;
    }

    @Override
    public void setNotes(Notes<HSLFShape, HSLFTextParagraph> notes) {
        if (notes != null && !(notes instanceof HSLFNotes)) {
            throw new IllegalArgumentException("notes needs to be of type HSLFNotes");
        }
        this._notes = (HSLFNotes)notes;
        SlideAtom sa = this.getSlideRecord().getSlideAtom();
        if (this._notes == null) {
            sa.setNotesID(0);
        } else {
            sa.setNotesID(this._notes._getSheetNumber());
        }
    }

    public void setSlideNumber(int newSlideNumber) {
        this._slideNo = newSlideNumber;
    }

    @Override
    public void onCreate() {
        EscherDggRecord dgg = this.getSlideShow().getDocumentRecord().getPPDrawingGroup().getEscherDggRecord();
        EscherContainerRecord dgContainer = this.getSheetContainer().getPPDrawing().getDgContainer();
        EscherDgRecord dg = (EscherDgRecord)HSLFShape.getEscherChild(dgContainer, EscherDgRecord.RECORD_ID);
        int dgId = dgg.getMaxDrawingGroupId() + 1;
        dg.setOptions((short)(dgId << 4));
        dgg.setDrawingsSaved(dgg.getDrawingsSaved() + 1);
        for (EscherContainerRecord c : dgContainer.getChildContainers()) {
            EscherSpRecord spr = null;
            switch (EscherRecordTypes.forTypeID(c.getRecordId())) {
                case SPGR_CONTAINER: {
                    EscherContainerRecord dc = (EscherContainerRecord)c.getChild(0);
                    spr = (EscherSpRecord)dc.getChildById(EscherSpRecord.RECORD_ID);
                    break;
                }
                case SP_CONTAINER: {
                    spr = (EscherSpRecord)c.getChildById(EscherSpRecord.RECORD_ID);
                    break;
                }
            }
            if (spr == null) continue;
            spr.setShapeId(this.allocateShapeId());
        }
        dg.setNumShapes(1);
    }

    public HSLFTextBox addTitle() {
        HSLFPlaceholder pl = new HSLFPlaceholder();
        pl.setShapeType(ShapeType.RECT);
        pl.setPlaceholder(Placeholder.TITLE);
        pl.setRunType(TextShape.TextPlaceholder.TITLE.nativeId);
        pl.setText("Click to edit title");
        pl.setAnchor(new Rectangle(54, 48, 612, 90));
        this.addShape(pl);
        return pl;
    }

    @Override
    public String getTitle() {
        for (List<HSLFTextParagraph> tp : this.getTextParagraphs()) {
            int type;
            if (tp.isEmpty() || !TextShape.TextPlaceholder.isTitle(type = tp.get(0).getRunType())) continue;
            String str = HSLFTextParagraph.getRawText(tp);
            return HSLFTextParagraph.toExternalString(str, type);
        }
        return null;
    }

    @Override
    public String getSlideName() {
        CString name = (CString)this.getSlideRecord().findFirstOfType(RecordTypes.CString.typeID);
        return name != null ? name.getText() : "Slide" + this.getSlideNumber();
    }

    @Override
    public List<List<HSLFTextParagraph>> getTextParagraphs() {
        return this._paragraphs;
    }

    @Override
    public int getSlideNumber() {
        return this._slideNo;
    }

    public Slide getSlideRecord() {
        return (Slide)this.getSheetContainer();
    }

    public SlideListWithText.SlideAtomsSet getSlideAtomsSet() {
        return this._atomSet;
    }

    @Override
    public HSLFMasterSheet getMasterSheet() {
        int masterId = this.getSlideRecord().getSlideAtom().getMasterID();
        for (HSLFSlideMaster sm : this.getSlideShow().getSlideMasters()) {
            if (masterId != sm._getSheetNumber()) continue;
            return sm;
        }
        for (HSLFTitleMaster tm : this.getSlideShow().getTitleMasters()) {
            if (masterId != tm._getSheetNumber()) continue;
            return tm;
        }
        return null;
    }

    public void setMasterSheet(HSLFMasterSheet master) {
        SlideAtom sa = this.getSlideRecord().getSlideAtom();
        int sheetNo = master._getSheetNumber();
        sa.setMasterID(sheetNo);
    }

    @Override
    public void setFollowMasterBackground(boolean flag) {
        SlideAtom sa = this.getSlideRecord().getSlideAtom();
        sa.setFollowMasterBackground(flag);
    }

    @Override
    public boolean getFollowMasterBackground() {
        SlideAtom sa = this.getSlideRecord().getSlideAtom();
        return sa.getFollowMasterBackground();
    }

    @Override
    public void setFollowMasterObjects(boolean flag) {
        SlideAtom sa = this.getSlideRecord().getSlideAtom();
        sa.setFollowMasterObjects(flag);
    }

    public boolean getFollowMasterScheme() {
        SlideAtom sa = this.getSlideRecord().getSlideAtom();
        return sa.getFollowMasterScheme();
    }

    public void setFollowMasterScheme(boolean flag) {
        SlideAtom sa = this.getSlideRecord().getSlideAtom();
        sa.setFollowMasterScheme(flag);
    }

    @Override
    public boolean getFollowMasterObjects() {
        SlideAtom sa = this.getSlideRecord().getSlideAtom();
        return sa.getFollowMasterObjects();
    }

    @Override
    public HSLFBackground getBackground() {
        if (this.getFollowMasterBackground()) {
            HSLFMasterSheet ms = this.getMasterSheet();
            return ms == null ? null : ms.getBackground();
        }
        return super.getBackground();
    }

    @Override
    public ColorSchemeAtom getColorScheme() {
        if (this.getFollowMasterScheme()) {
            HSLFMasterSheet ms = this.getMasterSheet();
            return ms == null ? null : ms.getColorScheme();
        }
        return super.getColorScheme();
    }

    private static RecordContainer selectContainer(RecordContainer root, int index, RecordTypes ... path) {
        if (root == null || index >= path.length) {
            return root;
        }
        RecordContainer newRoot = (RecordContainer)root.findFirstOfType(path[index].typeID);
        return HSLFSlide.selectContainer(newRoot, index + 1, path);
    }

    @Override
    public List<HSLFComment> getComments() {
        ArrayList<HSLFComment> comments = new ArrayList<HSLFComment>();
        RecordContainer binaryTags = HSLFSlide.selectContainer(this.getSheetContainer(), 0, RecordTypes.ProgTags, RecordTypes.ProgBinaryTag, RecordTypes.BinaryTagData);
        if (binaryTags != null) {
            for (Record record : binaryTags.getChildRecords()) {
                if (!(record instanceof Comment2000)) continue;
                comments.add(new HSLFComment((Comment2000)record));
            }
        }
        return comments;
    }

    @Override
    public HeadersFooters getHeadersFooters() {
        return new HeadersFooters(this, 63);
    }

    @Override
    protected void onAddTextShape(HSLFTextShape shape) {
        List<HSLFTextParagraph> newParas = shape.getTextParagraphs();
        this._paragraphs.add(newParas);
    }

    public StyleTextProp9Atom[] getNumberedListInfo() {
        return this.getPPDrawing().getNumberedListInfo();
    }

    public EscherTextboxWrapper[] getTextboxWrappers() {
        return this.getPPDrawing().getTextboxWrappers();
    }

    @Override
    public void setHidden(boolean hidden) {
        Slide cont = this.getSlideRecord();
        SSSlideInfoAtom slideInfo = (SSSlideInfoAtom)cont.findFirstOfType(RecordTypes.SSSlideInfoAtom.typeID);
        if (slideInfo == null) {
            slideInfo = new SSSlideInfoAtom();
            cont.addChildAfter(slideInfo, cont.findFirstOfType(RecordTypes.SlideAtom.typeID));
        }
        slideInfo.setEffectTransitionFlagByBit(4, hidden);
    }

    @Override
    public boolean isHidden() {
        SSSlideInfoAtom slideInfo = (SSSlideInfoAtom)this.getSlideRecord().findFirstOfType(RecordTypes.SSSlideInfoAtom.typeID);
        return slideInfo != null && slideInfo.getEffectTransitionFlagByBit(4);
    }

    @Override
    public void draw(Graphics2D graphics) {
        DrawFactory drawFact = DrawFactory.getInstance(graphics);
        DrawSlide draw = drawFact.getDrawable(this);
        draw.draw(graphics);
    }

    @Override
    public boolean getFollowMasterColourScheme() {
        return false;
    }

    @Override
    public void setFollowMasterColourScheme(boolean follow) {
    }

    @Override
    public boolean getFollowMasterGraphics() {
        return this.getFollowMasterObjects();
    }

    @Override
    public boolean getDisplayPlaceholder(Placeholder placeholder) {
        HeadersFooters hf = this.getHeadersFooters();
        SlideAtomLayout.SlideLayoutType slt = this.getSlideRecord().getSlideAtom().getSSlideLayoutAtom().getGeometryType();
        boolean isTitle = slt == SlideAtomLayout.SlideLayoutType.TITLE_SLIDE || slt == SlideAtomLayout.SlideLayoutType.TITLE_ONLY || slt == SlideAtomLayout.SlideLayoutType.MASTER_TITLE;
        switch (placeholder) {
            case DATETIME: {
                return hf.isDateTimeVisible() && (hf.isTodayDateVisible() || hf.isUserDateVisible() && hf.getUserDateAtom() != null) && !isTitle;
            }
            case SLIDE_NUMBER: {
                return hf.isSlideNumberVisible() && !isTitle;
            }
            case HEADER: {
                return hf.isHeaderVisible() && hf.getHeaderAtom() != null && !isTitle;
            }
            case FOOTER: {
                return hf.isFooterVisible() && hf.getFooterAtom() != null && !isTitle;
            }
        }
        return false;
    }

    @Override
    public boolean getDisplayPlaceholder(SimpleShape<?, ?> placeholderRef) {
        Placeholder placeholder = placeholderRef.getPlaceholder();
        if (placeholder == null) {
            return false;
        }
        HeadersFooters hf = this.getHeadersFooters();
        SlideAtomLayout.SlideLayoutType slt = this.getSlideRecord().getSlideAtom().getSSlideLayoutAtom().getGeometryType();
        boolean isTitle = slt == SlideAtomLayout.SlideLayoutType.TITLE_SLIDE || slt == SlideAtomLayout.SlideLayoutType.TITLE_ONLY || slt == SlideAtomLayout.SlideLayoutType.MASTER_TITLE;
        switch (placeholder) {
            case HEADER: {
                return hf.isHeaderVisible() && hf.getHeaderAtom() != null && !isTitle;
            }
            case FOOTER: {
                return hf.isFooterVisible() && hf.getFooterAtom() != null && !isTitle;
            }
        }
        return false;
    }

    public HSLFMasterSheet getSlideLayout() {
        return this.getMasterSheet();
    }
}

