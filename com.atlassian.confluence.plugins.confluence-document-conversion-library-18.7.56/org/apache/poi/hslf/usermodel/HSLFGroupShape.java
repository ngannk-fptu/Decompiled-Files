/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.usermodel;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ddf.EscherChildAnchorRecord;
import org.apache.poi.ddf.EscherClientAnchorRecord;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.ddf.EscherSpRecord;
import org.apache.poi.ddf.EscherSpgrRecord;
import org.apache.poi.hslf.usermodel.HSLFAutoShape;
import org.apache.poi.hslf.usermodel.HSLFConnectorShape;
import org.apache.poi.hslf.usermodel.HSLFFreeformShape;
import org.apache.poi.hslf.usermodel.HSLFHyperlink;
import org.apache.poi.hslf.usermodel.HSLFObjectShape;
import org.apache.poi.hslf.usermodel.HSLFPictureData;
import org.apache.poi.hslf.usermodel.HSLFPictureShape;
import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFShapeContainer;
import org.apache.poi.hslf.usermodel.HSLFShapeFactory;
import org.apache.poi.hslf.usermodel.HSLFSheet;
import org.apache.poi.hslf.usermodel.HSLFTable;
import org.apache.poi.hslf.usermodel.HSLFTextBox;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.sl.usermodel.GroupShape;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.sl.usermodel.ShapeContainer;
import org.apache.poi.sl.usermodel.ShapeType;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.Units;

public class HSLFGroupShape
extends HSLFShape
implements HSLFShapeContainer,
GroupShape<HSLFShape, HSLFTextParagraph> {
    private static final Logger LOG = LogManager.getLogger(HSLFGroupShape.class);

    public HSLFGroupShape() {
        this(null, null);
        this.createSpContainer(false);
    }

    public HSLFGroupShape(ShapeContainer<HSLFShape, HSLFTextParagraph> parent) {
        this(null, parent);
        this.createSpContainer(parent instanceof HSLFGroupShape);
    }

    protected HSLFGroupShape(EscherContainerRecord escherRecord, ShapeContainer<HSLFShape, HSLFTextParagraph> parent) {
        super(escherRecord, parent);
    }

    @Override
    public void setAnchor(Rectangle2D anchor) {
        boolean isInitialized;
        EscherClientAnchorRecord clientAnchor = (EscherClientAnchorRecord)this.getEscherChild(EscherClientAnchorRecord.RECORD_ID);
        boolean bl = isInitialized = clientAnchor.getDx1() != 0 || clientAnchor.getRow1() != 0;
        if (isInitialized) {
            this.moveAndScale(anchor);
        } else {
            this.setExteriorAnchor(anchor);
        }
    }

    @Override
    public void setInteriorAnchor(Rectangle2D anchor) {
        EscherSpgrRecord spgr = (EscherSpgrRecord)this.getEscherChild(EscherSpgrRecord.RECORD_ID);
        int x1 = Units.pointsToMaster(anchor.getX());
        int y1 = Units.pointsToMaster(anchor.getY());
        int x2 = Units.pointsToMaster(anchor.getX() + anchor.getWidth());
        int y2 = Units.pointsToMaster(anchor.getY() + anchor.getHeight());
        spgr.setRectX1(x1);
        spgr.setRectY1(y1);
        spgr.setRectX2(x2);
        spgr.setRectY2(y2);
    }

    @Override
    public Rectangle2D getInteriorAnchor() {
        EscherSpgrRecord rec = (EscherSpgrRecord)this.getEscherChild(EscherSpgrRecord.RECORD_ID);
        double x1 = Units.masterToPoints(rec.getRectX1());
        double y1 = Units.masterToPoints(rec.getRectY1());
        double x2 = Units.masterToPoints(rec.getRectX2());
        double y2 = Units.masterToPoints(rec.getRectY2());
        return new Rectangle2D.Double(x1, y1, x2 - x1, y2 - y1);
    }

    protected void setExteriorAnchor(Rectangle2D anchor) {
        EscherClientAnchorRecord clientAnchor = (EscherClientAnchorRecord)this.getEscherChild(EscherClientAnchorRecord.RECORD_ID);
        byte[] header = new byte[16];
        LittleEndian.putUShort(header, 0, 0);
        LittleEndian.putUShort(header, 2, 0);
        LittleEndian.putInt(header, 4, 8);
        clientAnchor.fillFields(header, 0, null);
        clientAnchor.setFlag((short)Units.pointsToMaster(anchor.getY()));
        clientAnchor.setCol1((short)Units.pointsToMaster(anchor.getX()));
        clientAnchor.setDx1((short)Units.pointsToMaster(anchor.getWidth() + anchor.getX()));
        clientAnchor.setRow1((short)Units.pointsToMaster(anchor.getHeight() + anchor.getY()));
        this.setInteriorAnchor(anchor);
    }

    @Override
    protected EscherContainerRecord createSpContainer(boolean isChild) {
        EscherContainerRecord ecr = super.createSpContainer(isChild);
        ecr.setRecordId(EscherContainerRecord.SPGR_CONTAINER);
        EscherContainerRecord spcont = new EscherContainerRecord();
        spcont.setRecordId(EscherContainerRecord.SP_CONTAINER);
        spcont.setOptions((short)15);
        EscherSpgrRecord spg = new EscherSpgrRecord();
        spg.setOptions((short)1);
        spcont.addChildRecord(spg);
        EscherSpRecord sp = new EscherSpRecord();
        short type = (short)((ShapeType.NOT_PRIMITIVE.nativeId << 4) + 2);
        sp.setOptions(type);
        sp.setFlags(513);
        spcont.addChildRecord(sp);
        EscherClientAnchorRecord anchor = new EscherClientAnchorRecord();
        spcont.addChildRecord(anchor);
        ecr.addChildRecord(spcont);
        return ecr;
    }

    @Override
    public void addShape(HSLFShape shape) {
        this.getSpContainer().addChildRecord(shape.getSpContainer());
        HSLFSheet sheet = this.getSheet();
        shape.setSheet(sheet);
        shape.setShapeId(sheet.allocateShapeId());
        shape.afterInsert(sheet);
    }

    protected void moveAndScale(Rectangle2D anchorDest) {
        Rectangle2D anchorSrc = this.getAnchor();
        double scaleX = anchorSrc.getWidth() == 0.0 ? 0.0 : anchorDest.getWidth() / anchorSrc.getWidth();
        double scaleY = anchorSrc.getHeight() == 0.0 ? 0.0 : anchorDest.getHeight() / anchorSrc.getHeight();
        this.setExteriorAnchor(anchorDest);
        for (HSLFShape shape : this.getShapes()) {
            Rectangle2D chanchor = shape.getAnchor();
            double x = anchorDest.getX() + (chanchor.getX() - anchorSrc.getX()) * scaleX;
            double y = anchorDest.getY() + (chanchor.getY() - anchorSrc.getY()) * scaleY;
            double width = chanchor.getWidth() * scaleX;
            double height = chanchor.getHeight() * scaleY;
            shape.setAnchor(new Rectangle2D.Double(x, y, width, height));
        }
    }

    @Override
    public Rectangle2D getAnchor() {
        int y2;
        int x2;
        int y1;
        int x1;
        EscherClientAnchorRecord clientAnchor = (EscherClientAnchorRecord)this.getEscherChild(EscherClientAnchorRecord.RECORD_ID);
        if (clientAnchor == null) {
            LOG.atInfo().log("EscherClientAnchorRecord was not found for shape group. Searching for EscherChildAnchorRecord.");
            EscherChildAnchorRecord rec = (EscherChildAnchorRecord)this.getEscherChild(EscherChildAnchorRecord.RECORD_ID);
            x1 = rec.getDx1();
            y1 = rec.getDy1();
            x2 = rec.getDx2();
            y2 = rec.getDy2();
        } else {
            x1 = clientAnchor.getCol1();
            y1 = clientAnchor.getFlag();
            x2 = clientAnchor.getDx1();
            y2 = clientAnchor.getRow1();
        }
        return new Rectangle2D.Double(x1 == -1 ? -1.0 : Units.masterToPoints(x1), y1 == -1 ? -1.0 : Units.masterToPoints(y1), x2 == -1 ? -1.0 : Units.masterToPoints(x2 - x1), y2 == -1 ? -1.0 : Units.masterToPoints(y2 - y1));
    }

    @Override
    public ShapeType getShapeType() {
        EscherSpRecord spRecord = (EscherSpRecord)this.getEscherChild(EscherSpRecord.RECORD_ID);
        int nativeId = spRecord.getOptions() >> 4;
        return ShapeType.forId(nativeId, false);
    }

    public HSLFHyperlink getHyperlink() {
        return null;
    }

    @Override
    public <T extends EscherRecord> T getEscherChild(int recordId) {
        EscherContainerRecord groupInfoContainer = (EscherContainerRecord)this.getSpContainer().getChild(0);
        return groupInfoContainer.getChildById((short)recordId);
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
    public boolean removeShape(HSLFShape shape) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<HSLFShape> getShapes() {
        ArrayList<HSLFShape> shapeList = new ArrayList<HSLFShape>();
        boolean isFirst = true;
        for (EscherRecord r : this.getSpContainer()) {
            if (isFirst) {
                isFirst = false;
                continue;
            }
            if (r instanceof EscherContainerRecord) {
                EscherContainerRecord container = (EscherContainerRecord)r;
                HSLFShape shape = HSLFShapeFactory.createShape(container, this);
                shape.setSheet(this.getSheet());
                shapeList.add(shape);
                continue;
            }
            LOG.atError().log("Shape contained non container escher record, was {}", (Object)r.getClass().getName());
        }
        return shapeList;
    }

    @Override
    public HSLFTextBox createTextBox() {
        HSLFTextBox s = new HSLFTextBox(this);
        s.setHorizontalCentered(true);
        s.setAnchor(new Rectangle2D.Double(0.0, 0.0, 100.0, 100.0));
        this.addShape(s);
        return s;
    }

    @Override
    public HSLFAutoShape createAutoShape() {
        HSLFAutoShape s = new HSLFAutoShape(ShapeType.RECT, (ShapeContainer<HSLFShape, HSLFTextParagraph>)this);
        s.setHorizontalCentered(true);
        s.setAnchor(new Rectangle2D.Double(0.0, 0.0, 100.0, 100.0));
        this.addShape(s);
        return s;
    }

    @Override
    public HSLFFreeformShape createFreeform() {
        HSLFFreeformShape s = new HSLFFreeformShape(this);
        s.setHorizontalCentered(true);
        s.setAnchor(new Rectangle2D.Double(0.0, 0.0, 100.0, 100.0));
        this.addShape(s);
        return s;
    }

    @Override
    public HSLFConnectorShape createConnector() {
        HSLFConnectorShape s = new HSLFConnectorShape(this);
        s.setAnchor(new Rectangle2D.Double(0.0, 0.0, 100.0, 100.0));
        this.addShape(s);
        return s;
    }

    @Override
    public HSLFGroupShape createGroup() {
        HSLFGroupShape s = new HSLFGroupShape(this);
        s.setAnchor(new Rectangle2D.Double(0.0, 0.0, 100.0, 100.0));
        this.addShape(s);
        return s;
    }

    @Override
    public HSLFPictureShape createPicture(PictureData pictureData) {
        if (!(pictureData instanceof HSLFPictureData)) {
            throw new IllegalArgumentException("pictureData needs to be of type HSLFPictureData");
        }
        HSLFPictureShape s = new HSLFPictureShape((HSLFPictureData)pictureData, (ShapeContainer<HSLFShape, HSLFTextParagraph>)this);
        s.setAnchor(new Rectangle2D.Double(0.0, 0.0, 100.0, 100.0));
        this.addShape(s);
        return s;
    }

    public HSLFTable createTable(int numRows, int numCols) {
        if (numRows < 1 || numCols < 1) {
            throw new IllegalArgumentException("numRows and numCols must be greater than 0");
        }
        HSLFTable s = new HSLFTable(numRows, numCols, this);
        s.setAnchor(new Rectangle2D.Double(0.0, 0.0, 100.0, 100.0));
        this.addShape(s);
        return s;
    }

    @Override
    public HSLFObjectShape createOleShape(PictureData pictureData) {
        if (!(pictureData instanceof HSLFPictureData)) {
            throw new IllegalArgumentException("pictureData needs to be of type HSLFPictureData");
        }
        HSLFObjectShape s = new HSLFObjectShape((HSLFPictureData)pictureData, (ShapeContainer<HSLFShape, HSLFTextParagraph>)this);
        s.setAnchor(new Rectangle2D.Double(0.0, 0.0, 100.0, 100.0));
        this.addShape(s);
        return s;
    }
}

