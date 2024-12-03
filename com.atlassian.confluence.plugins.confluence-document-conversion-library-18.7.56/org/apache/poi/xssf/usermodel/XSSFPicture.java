/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xssf.usermodel;

import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.util.ImageUtils;
import org.apache.poi.util.Internal;
import org.apache.poi.xssf.usermodel.XSSFAnchor;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPictureData;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlipFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualPictureProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetGeometry2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTransform2D;
import org.openxmlformats.schemas.drawingml.x2006.main.STShapeType;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTPicture;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTPictureNonVisual;

public final class XSSFPicture
extends XSSFShape
implements Picture {
    private static final Logger LOG = LogManager.getLogger(XSSFPicture.class);
    private static CTPicture prototype;
    private final CTPicture ctPicture;

    protected XSSFPicture(XSSFDrawing drawing, CTPicture ctPicture) {
        this.drawing = drawing;
        this.ctPicture = ctPicture;
    }

    protected static CTPicture prototype() {
        if (prototype == null) {
            CTPicture pic = CTPicture.Factory.newInstance();
            CTPictureNonVisual nvpr = pic.addNewNvPicPr();
            CTNonVisualDrawingProps nvProps = nvpr.addNewCNvPr();
            nvProps.setId(1L);
            nvProps.setName("Picture 1");
            nvProps.setDescr("Picture");
            CTNonVisualPictureProperties nvPicProps = nvpr.addNewCNvPicPr();
            nvPicProps.addNewPicLocks().setNoChangeAspect(true);
            CTBlipFillProperties blip = pic.addNewBlipFill();
            blip.addNewBlip().setEmbed("");
            blip.addNewStretch().addNewFillRect();
            CTShapeProperties sppr = pic.addNewSpPr();
            CTTransform2D t2d = sppr.addNewXfrm();
            CTPositiveSize2D ext = t2d.addNewExt();
            ext.setCx(0L);
            ext.setCy(0L);
            CTPoint2D off = t2d.addNewOff();
            off.setX(0);
            off.setY(0);
            CTPresetGeometry2D prstGeom = sppr.addNewPrstGeom();
            prstGeom.setPrst(STShapeType.RECT);
            prstGeom.addNewAvLst();
            prototype = pic;
        }
        return prototype;
    }

    protected void setPictureReference(PackageRelationship rel) {
        this.ctPicture.getBlipFill().getBlip().setEmbed(rel.getId());
    }

    @Internal
    public CTPicture getCTPicture() {
        return this.ctPicture;
    }

    @Override
    public void resize() {
        this.resize(Double.MAX_VALUE);
    }

    @Override
    public void resize(double scale) {
        this.resize(scale, scale);
    }

    @Override
    public void resize(double scaleX, double scaleY) {
        XSSFClientAnchor anchor = this.getClientAnchor();
        XSSFClientAnchor pref = this.getPreferredSize(scaleX, scaleY);
        if (anchor == null || pref == null) {
            LOG.atWarn().log("picture is not anchored via client anchor - ignoring resize call");
            return;
        }
        int row2 = anchor.getRow1() + (pref.getRow2() - pref.getRow1());
        int col2 = anchor.getCol1() + (pref.getCol2() - pref.getCol1());
        anchor.setCol2(col2);
        anchor.setDx2(pref.getDx2());
        anchor.setRow2(row2);
        anchor.setDy2(pref.getDy2());
    }

    @Override
    public XSSFClientAnchor getPreferredSize() {
        return this.getPreferredSize(1.0);
    }

    public XSSFClientAnchor getPreferredSize(double scale) {
        return this.getPreferredSize(scale, scale);
    }

    @Override
    public XSSFClientAnchor getPreferredSize(double scaleX, double scaleY) {
        Dimension dim = ImageUtils.setPreferredSize(this, scaleX, scaleY);
        CTPositiveSize2D size2d = this.ctPicture.getSpPr().getXfrm().getExt();
        size2d.setCx((int)dim.getWidth());
        size2d.setCy((int)dim.getHeight());
        return this.getClientAnchor();
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    protected static Dimension getImageDimension(PackagePart part, int type) {
        try (InputStream stream = part.getInputStream();){
            Dimension dimension = ImageUtils.getImageDimension(stream, type);
            return dimension;
        }
        catch (IOException e) {
            LOG.atWarn().withThrowable(e).log("Failed to read image");
            return new Dimension();
        }
    }

    @Override
    public Dimension getImageDimension() {
        XSSFPictureData picData = this.getPictureData();
        return XSSFPicture.getImageDimension(picData.getPackagePart(), picData.getPictureType());
    }

    @Override
    public XSSFPictureData getPictureData() {
        String blipId = this.ctPicture.getBlipFill().getBlip().getEmbed();
        return (XSSFPictureData)this.getDrawing().getRelationById(blipId);
    }

    @Override
    protected CTShapeProperties getShapeProperties() {
        return this.ctPicture.getSpPr();
    }

    @Override
    public XSSFClientAnchor getClientAnchor() {
        XSSFAnchor a = this.getAnchor();
        return a instanceof XSSFClientAnchor ? (XSSFClientAnchor)a : null;
    }

    @Override
    public XSSFSheet getSheet() {
        return (XSSFSheet)this.getDrawing().getParent();
    }

    @Override
    public String getShapeName() {
        return this.ctPicture.getNvPicPr().getCNvPr().getName();
    }
}

