/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xslf.usermodel;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ooxml.util.POIXMLUnits;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.sl.usermodel.ColorStyle;
import org.apache.poi.sl.usermodel.Insets2D;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.sl.usermodel.Shape;
import org.apache.poi.util.Dimension2DDouble;
import org.apache.poi.util.Internal;
import org.apache.poi.util.Units;
import org.apache.poi.xslf.usermodel.XSLFColor;
import org.apache.poi.xslf.usermodel.XSLFDiagram;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSheet;
import org.apache.poi.xslf.usermodel.XSLFTheme;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlip;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlipFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTDuotoneEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRelativeRect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSchemeColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTileInfoProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.STPercentage;

@Internal
public class XSLFTexturePaint
implements PaintStyle.TexturePaint {
    private final XSLFShape shape;
    private final CTBlipFillProperties blipFill;
    private final PackagePart parentPart;
    private final CTBlip blip;
    private final CTSchemeColor phClr;
    private final XSLFTheme theme;
    private final XSLFSheet sheet;

    public XSLFTexturePaint(XSLFShape shape, CTBlipFillProperties blipFill, PackagePart parentPart, CTSchemeColor phClr, XSLFTheme theme, XSLFSheet sheet) {
        this.shape = shape;
        this.blipFill = blipFill;
        this.parentPart = parentPart;
        this.blip = blipFill.getBlip();
        this.phClr = phClr;
        this.theme = theme;
        this.sheet = sheet;
    }

    private PackagePart getPart() throws InvalidFormatException {
        XSLFDiagram.XSLFDiagramGroupShape diagramGroupShape;
        POIXMLDocumentPart documentPart;
        String blipId = this.blip.getEmbed();
        if (this.shape.getParent() != null && this.shape.getParent() instanceof XSLFDiagram.XSLFDiagramGroupShape && (documentPart = (diagramGroupShape = (XSLFDiagram.XSLFDiagramGroupShape)this.shape.getParent()).getRelationById(blipId)) != null) {
            return documentPart.getPackagePart();
        }
        PackageRelationship rel = this.parentPart.getRelationship(blipId);
        return this.parentPart.getRelatedPart(rel);
    }

    @Override
    public InputStream getImageData() {
        try {
            return this.getPart().getInputStream();
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to read image data", e);
        }
    }

    @Override
    public String getContentType() {
        if (this.blip == null || !this.blip.isSetEmbed() || this.blip.getEmbed().isEmpty()) {
            return null;
        }
        try {
            return this.getPart().getContentType();
        }
        catch (InvalidFormatException e) {
            throw new RuntimeException("Failed to read package part", e);
        }
    }

    @Override
    public int getAlpha() {
        return this.blip.sizeOfAlphaModFixArray() > 0 ? POIXMLUnits.parsePercent(this.blip.getAlphaModFixArray(0).xgetAmt()) : 100000;
    }

    @Override
    public boolean isRotatedWithShape() {
        return !this.blipFill.isSetRotWithShape() || this.blipFill.getRotWithShape();
    }

    @Override
    public Dimension2D getScale() {
        CTTileInfoProperties tile = this.blipFill.getTile();
        return tile == null ? null : new Dimension2DDouble(tile.isSetSx() ? (double)POIXMLUnits.parsePercent(tile.xgetSx()) / 100000.0 : 1.0, tile.isSetSy() ? (double)POIXMLUnits.parsePercent(tile.xgetSy()) / 100000.0 : 1.0);
    }

    @Override
    public Point2D getOffset() {
        CTTileInfoProperties tile = this.blipFill.getTile();
        return tile == null ? null : new Point2D.Double(tile.isSetTx() ? Units.toPoints(POIXMLUnits.parseLength(tile.xgetTx())) : 0.0, tile.isSetTy() ? Units.toPoints(POIXMLUnits.parseLength(tile.xgetTy())) : 0.0);
    }

    @Override
    public PaintStyle.FlipMode getFlipMode() {
        CTTileInfoProperties tile = this.blipFill.getTile();
        switch (tile == null || tile.getFlip() == null ? 1 : tile.getFlip().intValue()) {
            default: {
                return PaintStyle.FlipMode.NONE;
            }
            case 2: {
                return PaintStyle.FlipMode.X;
            }
            case 3: {
                return PaintStyle.FlipMode.Y;
            }
            case 4: 
        }
        return PaintStyle.FlipMode.XY;
    }

    @Override
    public PaintStyle.TextureAlignment getAlignment() {
        CTTileInfoProperties tile = this.blipFill.getTile();
        return tile == null || !tile.isSetAlgn() ? null : PaintStyle.TextureAlignment.fromOoxmlId(tile.getAlgn().toString());
    }

    @Override
    public Insets2D getInsets() {
        return XSLFTexturePaint.getRectVal(this.blipFill.getSrcRect());
    }

    @Override
    public Insets2D getStretch() {
        return XSLFTexturePaint.getRectVal(this.blipFill.isSetStretch() ? this.blipFill.getStretch().getFillRect() : null);
    }

    @Override
    public List<ColorStyle> getDuoTone() {
        if (this.blip.sizeOfDuotoneArray() == 0) {
            return null;
        }
        ArrayList<ColorStyle> colors = new ArrayList<ColorStyle>();
        CTDuotoneEffect duoEff = this.blip.getDuotoneArray(0);
        for (CTSchemeColor phClrDuo : duoEff.getSchemeClrArray()) {
            colors.add(new XSLFColor(phClrDuo, this.theme, this.phClr, this.sheet).getColorStyle());
        }
        return colors;
    }

    @Override
    public Shape getShape() {
        return this.shape;
    }

    private static Insets2D getRectVal(CTRelativeRect rect) {
        return rect == null ? null : new Insets2D(XSLFTexturePaint.getRectVal(rect::isSetT, rect::xgetT), XSLFTexturePaint.getRectVal(rect::isSetL, rect::xgetL), XSLFTexturePaint.getRectVal(rect::isSetB, rect::xgetB), XSLFTexturePaint.getRectVal(rect::isSetR, rect::xgetR));
    }

    private static int getRectVal(Supplier<Boolean> isSet, Supplier<STPercentage> val) {
        return isSet.get() != false ? POIXMLUnits.parsePercent(val.get()) : 0;
    }
}

