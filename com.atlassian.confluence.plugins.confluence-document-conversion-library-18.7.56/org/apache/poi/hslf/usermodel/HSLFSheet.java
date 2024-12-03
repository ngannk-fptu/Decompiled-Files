/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.usermodel;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherDgRecord;
import org.apache.poi.ddf.EscherDggRecord;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.hslf.exceptions.HSLFException;
import org.apache.poi.hslf.model.HeadersFooters;
import org.apache.poi.hslf.record.CString;
import org.apache.poi.hslf.record.ColorSchemeAtom;
import org.apache.poi.hslf.record.PPDrawing;
import org.apache.poi.hslf.record.RecordContainer;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.hslf.record.SheetContainer;
import org.apache.poi.hslf.usermodel.HSLFAutoShape;
import org.apache.poi.hslf.usermodel.HSLFBackground;
import org.apache.poi.hslf.usermodel.HSLFConnectorShape;
import org.apache.poi.hslf.usermodel.HSLFFreeformShape;
import org.apache.poi.hslf.usermodel.HSLFGroupShape;
import org.apache.poi.hslf.usermodel.HSLFHyperlink;
import org.apache.poi.hslf.usermodel.HSLFMasterSheet;
import org.apache.poi.hslf.usermodel.HSLFObjectShape;
import org.apache.poi.hslf.usermodel.HSLFPictureData;
import org.apache.poi.hslf.usermodel.HSLFPictureShape;
import org.apache.poi.hslf.usermodel.HSLFPlaceholderDetails;
import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFShapeContainer;
import org.apache.poi.hslf.usermodel.HSLFShapeFactory;
import org.apache.poi.hslf.usermodel.HSLFShapePlaceholderDetails;
import org.apache.poi.hslf.usermodel.HSLFSimpleShape;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.HSLFTable;
import org.apache.poi.hslf.usermodel.HSLFTextBox;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.hslf.usermodel.HSLFTextShape;
import org.apache.poi.sl.draw.DrawFactory;
import org.apache.poi.sl.draw.DrawSheet;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.sl.usermodel.Placeholder;
import org.apache.poi.sl.usermodel.ShapeType;
import org.apache.poi.sl.usermodel.Sheet;
import org.apache.poi.util.Internal;

public abstract class HSLFSheet
implements HSLFShapeContainer,
Sheet<HSLFShape, HSLFTextParagraph> {
    private HSLFSlideShow _slideShow;
    private HSLFBackground _background;
    private final SheetContainer _container;
    private final int _sheetNo;

    public HSLFSheet(SheetContainer container, int sheetNo) {
        this._container = container;
        this._sheetNo = sheetNo;
    }

    public abstract List<List<HSLFTextParagraph>> getTextParagraphs();

    public int _getSheetRefId() {
        return this._container.getSheetId();
    }

    public int _getSheetNumber() {
        return this._sheetNo;
    }

    public PPDrawing getPPDrawing() {
        return this._container.getPPDrawing();
    }

    public HSLFSlideShow getSlideShow() {
        return this._slideShow;
    }

    public SheetContainer getSheetContainer() {
        return this._container;
    }

    @Internal
    protected void setSlideShow(HSLFSlideShow ss) {
        if (this._slideShow != null) {
            throw new HSLFException("Can't change existing slideshow reference");
        }
        this._slideShow = ss;
        List<List<HSLFTextParagraph>> trs = this.getTextParagraphs();
        if (trs == null) {
            return;
        }
        for (List<HSLFTextParagraph> ltp : trs) {
            HSLFTextParagraph.supplySheet(ltp, this);
            HSLFTextParagraph.applyHyperlinks(ltp);
        }
    }

    @Override
    public List<HSLFShape> getShapes() {
        PPDrawing ppdrawing = this.getPPDrawing();
        EscherContainerRecord dg = ppdrawing.getDgContainer();
        EscherContainerRecord spgr = null;
        for (EscherRecord rec : dg) {
            if (rec.getRecordId() != EscherContainerRecord.SPGR_CONTAINER) continue;
            spgr = (EscherContainerRecord)rec;
            break;
        }
        if (spgr == null) {
            throw new IllegalStateException("spgr not found");
        }
        ArrayList<HSLFShape> shapeList = new ArrayList<HSLFShape>();
        boolean isFirst = true;
        for (EscherRecord r : spgr) {
            HSLFHyperlink link;
            if (isFirst) {
                isFirst = false;
                continue;
            }
            EscherContainerRecord sp = (EscherContainerRecord)r;
            HSLFShape sh = HSLFShapeFactory.createShape(sp, null);
            sh.setSheet(this);
            if (sh instanceof HSLFSimpleShape && (link = HSLFHyperlink.find(sh)) != null) {
                ((HSLFSimpleShape)sh).setHyperlink(link);
            }
            shapeList.add(sh);
        }
        return shapeList;
    }

    @Override
    public void addShape(HSLFShape shape) {
        PPDrawing ppdrawing = this.getPPDrawing();
        EscherContainerRecord dgContainer = ppdrawing.getDgContainer();
        EscherContainerRecord spgr = (EscherContainerRecord)HSLFShape.getEscherChild(dgContainer, EscherContainerRecord.SPGR_CONTAINER);
        spgr.addChildRecord(shape.getSpContainer());
        shape.setSheet(this);
        shape.setShapeId(this.allocateShapeId());
        shape.afterInsert(this);
    }

    public int allocateShapeId() {
        EscherDggRecord dgg = this._slideShow.getDocumentRecord().getPPDrawingGroup().getEscherDggRecord();
        EscherDgRecord dg = this._container.getPPDrawing().getEscherDgRecord();
        return dgg.allocateShapeId(dg, false);
    }

    @Override
    public boolean removeShape(HSLFShape shape) {
        PPDrawing ppdrawing = this.getPPDrawing();
        EscherContainerRecord dg = ppdrawing.getDgContainer();
        EscherContainerRecord spgr = (EscherContainerRecord)dg.getChildById(EscherContainerRecord.SPGR_CONTAINER);
        if (spgr == null) {
            return false;
        }
        return spgr.removeChildRecord(shape.getSpContainer());
    }

    public void onCreate() {
    }

    public abstract HSLFMasterSheet getMasterSheet();

    public ColorSchemeAtom getColorScheme() {
        return this._container.getColorScheme();
    }

    public HSLFBackground getBackground() {
        if (this._background == null) {
            PPDrawing ppdrawing = this.getPPDrawing();
            EscherContainerRecord dg = ppdrawing.getDgContainer();
            EscherContainerRecord spContainer = (EscherContainerRecord)dg.getChildById(EscherContainerRecord.SP_CONTAINER);
            this._background = new HSLFBackground(spContainer, null);
            this._background.setSheet(this);
        }
        return this._background;
    }

    @Override
    public void draw(Graphics2D graphics) {
        DrawFactory drawFact = DrawFactory.getInstance(graphics);
        DrawSheet draw = drawFact.getDrawable(this);
        draw.draw(graphics);
    }

    protected void onAddTextShape(HSLFTextShape shape) {
    }

    public HSLFTextShape getPlaceholderByTextType(int type) {
        for (HSLFShape shape : this.getShapes()) {
            HSLFTextShape tx;
            if (!(shape instanceof HSLFTextShape) || (tx = (HSLFTextShape)shape).getRunType() != type) continue;
            return tx;
        }
        return null;
    }

    public HSLFSimpleShape getPlaceholder(Placeholder type) {
        for (HSLFShape shape : this.getShapes()) {
            HSLFSimpleShape ss;
            if (!(shape instanceof HSLFSimpleShape) || type != (ss = (HSLFSimpleShape)shape).getPlaceholder()) continue;
            return ss;
        }
        return null;
    }

    public String getProgrammableTag() {
        CString binaryTag;
        RecordContainer progBinaryTag;
        String tag = null;
        RecordContainer progTags = (RecordContainer)this.getSheetContainer().findFirstOfType(RecordTypes.ProgTags.typeID);
        if (progTags != null && (progBinaryTag = (RecordContainer)progTags.findFirstOfType(RecordTypes.ProgBinaryTag.typeID)) != null && (binaryTag = (CString)progBinaryTag.findFirstOfType(RecordTypes.CString.typeID)) != null) {
            tag = binaryTag.getText();
        }
        return tag;
    }

    @Override
    public Iterator<HSLFShape> iterator() {
        return this.getShapes().iterator();
    }

    @Override
    public Spliterator<HSLFShape> spliterator() {
        return this.getShapes().spliterator();
    }

    @Override
    public boolean getFollowMasterGraphics() {
        return false;
    }

    @Override
    public HSLFTextBox createTextBox() {
        HSLFTextBox s = new HSLFTextBox();
        s.setHorizontalCentered(true);
        s.setAnchor(new Rectangle2D.Double(0.0, 0.0, 100.0, 100.0));
        this.addShape(s);
        return s;
    }

    @Override
    public HSLFAutoShape createAutoShape() {
        HSLFAutoShape s = new HSLFAutoShape(ShapeType.RECT);
        s.setHorizontalCentered(true);
        s.setAnchor(new Rectangle2D.Double(0.0, 0.0, 100.0, 100.0));
        this.addShape(s);
        return s;
    }

    @Override
    public HSLFFreeformShape createFreeform() {
        HSLFFreeformShape s = new HSLFFreeformShape();
        s.setHorizontalCentered(true);
        s.setAnchor(new Rectangle2D.Double(0.0, 0.0, 100.0, 100.0));
        this.addShape(s);
        return s;
    }

    @Override
    public HSLFConnectorShape createConnector() {
        HSLFConnectorShape s = new HSLFConnectorShape();
        s.setAnchor(new Rectangle2D.Double(0.0, 0.0, 100.0, 100.0));
        this.addShape(s);
        return s;
    }

    @Override
    public HSLFGroupShape createGroup() {
        HSLFGroupShape s = new HSLFGroupShape();
        s.setAnchor(new Rectangle2D.Double(0.0, 0.0, 100.0, 100.0));
        this.addShape(s);
        return s;
    }

    @Override
    public HSLFPictureShape createPicture(PictureData pictureData) {
        if (!(pictureData instanceof HSLFPictureData)) {
            throw new IllegalArgumentException("pictureData needs to be of type HSLFPictureData");
        }
        HSLFPictureShape s = new HSLFPictureShape((HSLFPictureData)pictureData);
        s.setAnchor(new Rectangle2D.Double(0.0, 0.0, 100.0, 100.0));
        this.addShape(s);
        return s;
    }

    public HSLFTable createTable(int numRows, int numCols) {
        if (numRows < 1 || numCols < 1) {
            throw new IllegalArgumentException("numRows and numCols must be greater than 0");
        }
        HSLFTable s = new HSLFTable(numRows, numCols);
        this.addShape(s);
        return s;
    }

    @Override
    public HSLFObjectShape createOleShape(PictureData pictureData) {
        if (!(pictureData instanceof HSLFPictureData)) {
            throw new IllegalArgumentException("pictureData needs to be of type HSLFPictureData");
        }
        HSLFObjectShape s = new HSLFObjectShape((HSLFPictureData)pictureData);
        s.setAnchor(new Rectangle2D.Double(0.0, 0.0, 100.0, 100.0));
        this.addShape(s);
        return s;
    }

    public HeadersFooters getHeadersFooters() {
        return new HeadersFooters(this, 63);
    }

    @Override
    public HSLFPlaceholderDetails getPlaceholderDetails(Placeholder placeholder) {
        HSLFSimpleShape ph = this.getPlaceholder(placeholder);
        return ph == null ? null : new HSLFShapePlaceholderDetails(ph);
    }
}

