/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xslf.usermodel;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import org.apache.poi.ooxml.util.XPathHelper;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.sl.draw.DrawFactory;
import org.apache.poi.sl.draw.DrawPaint;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.sl.usermodel.PlaceableShape;
import org.apache.poi.sl.usermodel.Placeholder;
import org.apache.poi.sl.usermodel.Shape;
import org.apache.poi.util.Internal;
import org.apache.poi.xslf.model.PropertyFetcher;
import org.apache.poi.xslf.usermodel.XSLFColor;
import org.apache.poi.xslf.usermodel.XSLFGradientPaint;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFPlaceholderDetails;
import org.apache.poi.xslf.usermodel.XSLFPropertiesDelegate;
import org.apache.poi.xslf.usermodel.XSLFShapeContainer;
import org.apache.poi.xslf.usermodel.XSLFSheet;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.apache.poi.xslf.usermodel.XSLFSlideLayout;
import org.apache.poi.xslf.usermodel.XSLFSlideMaster;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTexturePaint;
import org.apache.poi.xslf.usermodel.XSLFTheme;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlipFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGradientFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGroupShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSchemeColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeStyle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTStyleMatrix;
import org.openxmlformats.schemas.drawingml.x2006.main.CTStyleMatrixReference;
import org.openxmlformats.schemas.drawingml.x2006.main.STSchemeColorVal;
import org.openxmlformats.schemas.presentationml.x2006.main.CTBackgroundProperties;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPicture;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPlaceholder;

public abstract class XSLFShape
implements Shape<XSLFShape, XSLFTextParagraph> {
    static final String DML_NS = "http://schemas.openxmlformats.org/drawingml/2006/main";
    static final String PML_NS = "http://schemas.openxmlformats.org/presentationml/2006/main";
    private static final QName[] NV_CONTAINER = new QName[]{new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "nvSpPr"), new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "nvCxnSpPr"), new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "nvGrpSpPr"), new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "nvPicPr"), new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "nvGraphicFramePr")};
    private static final QName[] CNV_PROPS = new QName[]{new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "cNvPr")};
    private final XmlObject _shape;
    private final XSLFSheet _sheet;
    private XSLFShapeContainer _parent;
    private CTShapeStyle _spStyle;
    private CTNonVisualDrawingProps _nvPr;

    protected XSLFShape(XmlObject shape, XSLFSheet sheet) {
        this._shape = shape;
        this._sheet = sheet;
    }

    public final XmlObject getXmlObject() {
        return this._shape;
    }

    public XSLFSheet getSheet() {
        return this._sheet;
    }

    @Override
    public String getShapeName() {
        CTNonVisualDrawingProps nonVisualDrawingProps = this.getCNvPr();
        return nonVisualDrawingProps == null ? null : nonVisualDrawingProps.getName();
    }

    @Override
    public int getShapeId() {
        CTNonVisualDrawingProps nonVisualDrawingProps = this.getCNvPr();
        if (nonVisualDrawingProps == null) {
            throw new IllegalStateException("no underlying shape exists");
        }
        return Math.toIntExact(nonVisualDrawingProps.getId());
    }

    @Internal
    void copy(XSLFShape sh) {
        if (!this.getClass().isInstance(sh)) {
            throw new IllegalArgumentException("Can't copy " + sh.getClass().getSimpleName() + " into " + this.getClass().getSimpleName());
        }
        if (this instanceof PlaceableShape) {
            PlaceableShape ps = (PlaceableShape)((Object)this);
            Rectangle2D anchor = sh.getAnchor();
            if (anchor != null) {
                ps.setAnchor(anchor);
            }
        }
    }

    public void setParent(XSLFShapeContainer parent) {
        this._parent = parent;
    }

    public XSLFShapeContainer getParent() {
        return this._parent;
    }

    protected PaintStyle getFillPaint() {
        final XSLFTheme theme = this.getSheet().getTheme();
        final boolean hasPlaceholder = this.getPlaceholder() != null;
        PropertyFetcher<PaintStyle> fetcher = new PropertyFetcher<PaintStyle>(){

            @Override
            public boolean fetch(XSLFShape shape) {
                CTPicture pic;
                PackagePart pp = shape.getSheet().getPackagePart();
                if (shape instanceof XSLFPictureShape && (pic = (CTPicture)shape.getXmlObject()).getBlipFill() != null) {
                    this.setValue(XSLFShape.this.selectPaint(pic.getBlipFill(), pp, null, theme));
                    return true;
                }
                XSLFPropertiesDelegate.XSLFFillProperties fp = XSLFPropertiesDelegate.getFillDelegate(shape.getShapeProperties());
                if (fp == null) {
                    return false;
                }
                if (fp.isSetNoFill()) {
                    this.setValue(null);
                    return true;
                }
                PaintStyle paint = XSLFShape.this.selectPaint(fp, null, pp, theme, hasPlaceholder);
                if (paint != null) {
                    this.setValue(paint);
                    return true;
                }
                CTShapeStyle style = shape.getSpStyle();
                if (style != null) {
                    fp = XSLFPropertiesDelegate.getFillDelegate(style.getFillRef());
                    paint = XSLFShape.this.selectPaint(fp, null, pp, theme, hasPlaceholder);
                }
                if (paint != null) {
                    this.setValue(paint);
                    return true;
                }
                return false;
            }
        };
        this.fetchShapeProperty(fetcher);
        return (PaintStyle)fetcher.getValue();
    }

    protected CTBackgroundProperties getBgPr() {
        return this.getChild(CTBackgroundProperties.class, PML_NS, "bgPr");
    }

    protected CTStyleMatrixReference getBgRef() {
        return this.getChild(CTStyleMatrixReference.class, PML_NS, "bgRef");
    }

    protected CTGroupShapeProperties getGrpSpPr() {
        return this.getChild(CTGroupShapeProperties.class, PML_NS, "grpSpPr");
    }

    protected CTNonVisualDrawingProps getCNvPr() {
        try {
            if (this._nvPr == null) {
                this._nvPr = XPathHelper.selectProperty(this.getXmlObject(), CTNonVisualDrawingProps.class, null, NV_CONTAINER, CNV_PROPS);
            }
            return this._nvPr;
        }
        catch (XmlException e) {
            return null;
        }
    }

    protected CTShapeStyle getSpStyle() {
        if (this._spStyle == null) {
            this._spStyle = this.getChild(CTShapeStyle.class, PML_NS, "style");
        }
        return this._spStyle;
    }

    protected <T extends XmlObject> T getChild(Class<T> childClass, String namespace, String nodename) {
        XmlObject child = null;
        try (XmlCursor cur = this.getXmlObject().newCursor();){
            if (cur.toChild(namespace, nodename)) {
                child = cur.getObject();
            }
            if (cur.toChild(DML_NS, nodename)) {
                child = cur.getObject();
            }
        }
        return (T)child;
    }

    public boolean isPlaceholder() {
        return this.getPlaceholderDetails().getCTPlaceholder(false) != null;
    }

    public Placeholder getPlaceholder() {
        return this.getPlaceholderDetails().getPlaceholder();
    }

    public void setPlaceholder(Placeholder placeholder) {
        this.getPlaceholderDetails().setPlaceholder(placeholder);
    }

    public XSLFPlaceholderDetails getPlaceholderDetails() {
        return new XSLFPlaceholderDetails(this);
    }

    protected <T extends XmlObject> T selectProperty(Class<T> resultClass, String xquery) {
        XmlObject[] rs = this.getXmlObject().selectPath(xquery);
        if (rs.length == 0) {
            return null;
        }
        return (T)(resultClass.isInstance(rs[0]) ? rs[0] : null);
    }

    @Internal
    public boolean fetchShapeProperty(PropertyFetcher<?> visitor) {
        if (visitor.fetch(this)) {
            return true;
        }
        CTPlaceholder ph = this.getPlaceholderDetails().getCTPlaceholder(false);
        if (ph == null) {
            return false;
        }
        XSLFSlideMaster sm = this.getSheet().getMasterSheet();
        if (sm instanceof XSLFSlideLayout) {
            XSLFSlideLayout slideLayout = (XSLFSlideLayout)((Object)sm);
            XSLFSimpleShape placeholderShape = slideLayout.getPlaceholder(ph);
            if (placeholderShape != null && visitor.fetch(placeholderShape)) {
                return true;
            }
            sm = slideLayout.getMasterSheet();
        }
        if (sm instanceof XSLFSlideMaster) {
            XSLFSlideMaster master = sm;
            int textType = XSLFShape.getPlaceholderType(ph);
            XSLFSimpleShape masterShape = master.getPlaceholderByType(textType);
            return masterShape != null && visitor.fetch(masterShape);
        }
        return false;
    }

    private static int getPlaceholderType(CTPlaceholder ph) {
        if (!ph.isSetType()) {
            return 2;
        }
        switch (ph.getType().intValue()) {
            case 1: 
            case 3: {
                return 1;
            }
            case 5: 
            case 6: 
            case 7: {
                return ph.getType().intValue();
            }
        }
        return 2;
    }

    protected PaintStyle selectPaint(XSLFPropertiesDelegate.XSLFFillProperties fp, CTSchemeColor phClr, PackagePart parentPart, XSLFTheme theme, boolean hasPlaceholder) {
        if (fp == null || fp.isSetNoFill()) {
            return null;
        }
        if (fp.isSetSolidFill()) {
            return this.selectPaint(fp.getSolidFill(), phClr, theme);
        }
        if (fp.isSetBlipFill()) {
            return this.selectPaint(fp.getBlipFill(), parentPart, phClr, theme);
        }
        if (fp.isSetGradFill()) {
            return this.selectPaint(fp.getGradFill(), phClr, theme);
        }
        if (fp.isSetMatrixStyle()) {
            return this.selectPaint(fp.getMatrixStyle(), theme, fp.isLineStyle(), hasPlaceholder);
        }
        if (phClr != null) {
            return this.selectPaint(phClr, theme);
        }
        return null;
    }

    protected PaintStyle selectPaint(CTSchemeColor phClr, XSLFTheme theme) {
        XSLFColor c = new XSLFColor(null, theme, phClr, this._sheet);
        return DrawPaint.createSolidPaint(c.getColorStyle());
    }

    protected PaintStyle selectPaint(CTSolidColorFillProperties solidFill, CTSchemeColor phClr, XSLFTheme theme) {
        CTSchemeColor nestedPhClr = solidFill.getSchemeClr();
        boolean useNested = nestedPhClr != null && nestedPhClr.getVal() != null && !STSchemeColorVal.PH_CLR.equals(nestedPhClr.getVal());
        XSLFColor c = new XSLFColor(solidFill, theme, useNested ? nestedPhClr : phClr, this._sheet);
        return DrawPaint.createSolidPaint(c.getColorStyle());
    }

    protected PaintStyle selectPaint(CTBlipFillProperties blipFill, PackagePart parentPart, CTSchemeColor phClr, XSLFTheme theme) {
        return new XSLFTexturePaint(this, blipFill, parentPart, phClr, theme, this._sheet);
    }

    protected PaintStyle selectPaint(CTGradientFillProperties gradFill, CTSchemeColor phClr, XSLFTheme theme) {
        return new XSLFGradientPaint(gradFill, phClr, theme, this._sheet);
    }

    protected PaintStyle selectPaint(CTStyleMatrixReference fillRef, XSLFTheme theme, boolean isLineStyle, boolean hasPlaceholder) {
        XmlObject styleLst;
        long childIdx;
        if (fillRef == null) {
            return null;
        }
        long idx = fillRef.getIdx();
        CTStyleMatrix matrix = theme.getXmlObject().getThemeElements().getFmtScheme();
        if (idx >= 1L && idx <= 999L) {
            childIdx = idx - 1L;
            styleLst = isLineStyle ? matrix.getLnStyleLst() : matrix.getFillStyleLst();
        } else if (idx >= 1001L) {
            childIdx = idx - 1001L;
            styleLst = matrix.getBgFillStyleLst();
        } else {
            return null;
        }
        XSLFPropertiesDelegate.XSLFFillProperties fp = null;
        try (XmlCursor cur = styleLst.newCursor();){
            if (cur.toChild(Math.toIntExact(childIdx))) {
                fp = XSLFPropertiesDelegate.getFillDelegate(cur.getObject());
            }
        }
        CTSchemeColor phClr = fillRef.getSchemeClr();
        PaintStyle res = this.selectPaint(fp, phClr, theme.getPackagePart(), theme, hasPlaceholder);
        if (res != null || hasPlaceholder) {
            return res;
        }
        XSLFColor col = new XSLFColor(fillRef, theme, phClr, this._sheet);
        return DrawPaint.createSolidPaint(col.getColorStyle());
    }

    @Override
    public void draw(Graphics2D graphics, Rectangle2D bounds) {
        DrawFactory.getInstance(graphics).drawShape(graphics, this, bounds);
    }

    protected XmlObject getShapeProperties() {
        return this.getChild(CTShapeProperties.class, PML_NS, "spPr");
    }

    @Internal
    public static interface ReparseFactory<T extends XmlObject> {
        public T parse(XMLStreamReader var1) throws XmlException;
    }
}

