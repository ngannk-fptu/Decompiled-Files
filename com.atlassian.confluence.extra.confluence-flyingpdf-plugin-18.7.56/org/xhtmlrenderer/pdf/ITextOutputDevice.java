/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.CMYKColor;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfBorderArray;
import com.lowagie.text.pdf.PdfBorderDictionary;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDestination;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfOutline;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfTextArray;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Pattern;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.parser.FSCMYKColor;
import org.xhtmlrenderer.css.parser.FSColor;
import org.xhtmlrenderer.css.parser.FSRGBColor;
import org.xhtmlrenderer.css.style.CssContext;
import org.xhtmlrenderer.css.value.FontSpecification;
import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.extend.NamespaceHandler;
import org.xhtmlrenderer.extend.OutputDevice;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.DOMUtil;
import org.xhtmlrenderer.pdf.HTMLOutline;
import org.xhtmlrenderer.pdf.ITextFSFont;
import org.xhtmlrenderer.pdf.ITextFSImage;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextReplacedElement;
import org.xhtmlrenderer.pdf.PDFAsImage;
import org.xhtmlrenderer.pdf.PagePosition;
import org.xhtmlrenderer.render.AbstractOutputDevice;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.render.Box;
import org.xhtmlrenderer.render.FSFont;
import org.xhtmlrenderer.render.InlineLayoutBox;
import org.xhtmlrenderer.render.InlineText;
import org.xhtmlrenderer.render.JustificationInfo;
import org.xhtmlrenderer.render.PageBox;
import org.xhtmlrenderer.render.RenderingContext;
import org.xhtmlrenderer.util.Configuration;
import org.xhtmlrenderer.util.XRLog;
import org.xhtmlrenderer.util.XRRuntimeException;

public class ITextOutputDevice
extends AbstractOutputDevice
implements OutputDevice {
    private static final int FILL = 1;
    private static final int STROKE = 2;
    private static final int CLIP = 3;
    private static AffineTransform IDENTITY = new AffineTransform();
    private static final BasicStroke STROKE_ONE = new BasicStroke(1.0f);
    private static final boolean ROUND_RECT_DIMENSIONS_DOWN = Configuration.isTrue("xr.pdf.round.rect.dimensions.down", false);
    private PdfContentByte _currentPage;
    private float _pageHeight;
    private ITextFSFont _font;
    private AffineTransform _transform = new AffineTransform();
    private Color _color = Color.BLACK;
    private Color _fillColor;
    private Color _strokeColor;
    private Stroke _stroke = null;
    private Stroke _originalStroke = null;
    private Stroke _oldStroke = null;
    private Area _clip;
    private SharedContext _sharedContext;
    private float _dotsPerPoint;
    private PdfWriter _writer;
    private Map _readerCache = new HashMap();
    private PdfDestination _defaultDestination;
    private List _bookmarks = new ArrayList();
    private List _metadata = new ArrayList();
    private Box _root;
    private int _startPageNo;
    private int _nextFormFieldIndex;
    private Set _linkTargetAreas;

    public ITextOutputDevice(float dotsPerPoint) {
        this._dotsPerPoint = dotsPerPoint;
    }

    public void setWriter(PdfWriter writer) {
        this._writer = writer;
    }

    public PdfWriter getWriter() {
        return this._writer;
    }

    public int getNextFormFieldIndex() {
        return ++this._nextFormFieldIndex;
    }

    public void initializePage(PdfContentByte currentPage, float height) {
        this._currentPage = currentPage;
        this._pageHeight = height;
        this._currentPage.saveState();
        this._transform = new AffineTransform();
        this._transform.scale(1.0 / (double)this._dotsPerPoint, 1.0 / (double)this._dotsPerPoint);
        this._originalStroke = this._stroke = this.transformStroke(STROKE_ONE);
        this._oldStroke = this._stroke;
        this.setStrokeDiff(this._stroke, null);
        if (this._defaultDestination == null) {
            this._defaultDestination = new PdfDestination(2, height);
            this._defaultDestination.addPage(this._writer.getPageReference(1));
        }
        this._linkTargetAreas = new HashSet();
    }

    public void finishPage() {
        this._currentPage.restoreState();
    }

    @Override
    public void paintReplacedElement(RenderingContext c, BlockBox box) {
        ITextReplacedElement element = (ITextReplacedElement)box.getReplacedElement();
        element.paint(c, this, box);
    }

    @Override
    public void paintBackground(RenderingContext c, Box box) {
        super.paintBackground(c, box);
        this.processLink(c, box);
    }

    private Rectangle calcTotalLinkArea(RenderingContext c, Box box) {
        Box prev;
        Box current = box;
        while ((prev = current.getPreviousSibling()) != null && prev.getElement() == box.getElement()) {
            current = prev;
        }
        Rectangle result = this.createLocalTargetArea(c, current, true);
        for (current = current.getNextSibling(); current != null && current.getElement() == box.getElement(); current = current.getNextSibling()) {
            result = this.add(result, this.createLocalTargetArea(c, current, true));
        }
        return result;
    }

    private Rectangle add(Rectangle r1, Rectangle r2) {
        float llx = Math.min(r1.getLeft(), r2.getLeft());
        float urx = Math.max(r1.getRight(), r2.getRight());
        float lly = Math.min(r1.getBottom(), r2.getBottom());
        float ury = Math.max(r1.getTop(), r2.getTop());
        return new Rectangle(llx, lly, urx, ury);
    }

    private String createRectKey(Rectangle rect) {
        return rect.getLeft() + ":" + rect.getBottom() + ":" + rect.getRight() + ":" + rect.getTop();
    }

    private Rectangle checkLinkArea(RenderingContext c, Box box) {
        Rectangle targetArea = this.calcTotalLinkArea(c, box);
        String key = this.createRectKey(targetArea);
        if (this._linkTargetAreas.contains(key)) {
            return null;
        }
        this._linkTargetAreas.add(key);
        return targetArea;
    }

    private void processLink(RenderingContext c, Box box) {
        NamespaceHandler handler;
        String uri;
        Element elem = box.getElement();
        if (elem != null && (uri = (handler = this._sharedContext.getNamespaceHandler()).getLinkUri(elem)) != null) {
            if (uri.length() > 1 && uri.charAt(0) == '#') {
                PdfDestination dest;
                String anchor = uri.substring(1);
                Box target = this._sharedContext.getBoxById(anchor);
                if (target != null && (dest = this.createDestination(c, target)) != null) {
                    PdfAction action = new PdfAction();
                    if (!"".equals(handler.getAttributeValue(elem, "onclick"))) {
                        action = PdfAction.javaScript(handler.getAttributeValue(elem, "onclick"), this._writer);
                    } else {
                        action.put(PdfName.S, PdfName.GOTO);
                        action.put(PdfName.D, dest);
                    }
                    Rectangle targetArea = this.checkLinkArea(c, box);
                    if (targetArea == null) {
                        return;
                    }
                    targetArea.setBorder(0);
                    targetArea.setBorderWidth(0.0f);
                    PdfAnnotation annot = new PdfAnnotation(this._writer, targetArea.getLeft(), targetArea.getBottom(), targetArea.getRight(), targetArea.getTop(), action);
                    annot.put(PdfName.SUBTYPE, PdfName.LINK);
                    annot.setBorderStyle(new PdfBorderDictionary(0.0f, 0));
                    annot.setBorder(new PdfBorderArray(0.0f, 0.0f, 0.0f));
                    this._writer.addAnnotation(annot);
                }
            } else {
                PdfAction action = new PdfAction(uri);
                Rectangle targetArea = this.checkLinkArea(c, box);
                if (targetArea == null) {
                    return;
                }
                PdfAnnotation annot = new PdfAnnotation(this._writer, targetArea.getLeft(), targetArea.getBottom(), targetArea.getRight(), targetArea.getTop(), action);
                annot.put(PdfName.SUBTYPE, PdfName.LINK);
                annot.setBorderStyle(new PdfBorderDictionary(0.0f, 0));
                annot.setBorder(new PdfBorderArray(0.0f, 0.0f, 0.0f));
                this._writer.addAnnotation(annot);
            }
        }
    }

    public Rectangle createLocalTargetArea(RenderingContext c, Box box) {
        return this.createLocalTargetArea(c, box, false);
    }

    private Rectangle createLocalTargetArea(RenderingContext c, Box box, boolean useAggregateBounds) {
        java.awt.Rectangle bounds = useAggregateBounds && box.getPaintingInfo() != null ? box.getPaintingInfo().getAggregateBounds() : box.getContentAreaEdge(box.getAbsX(), box.getAbsY(), c);
        Point2D.Double docCorner = new Point2D.Double(bounds.x, bounds.y + bounds.height);
        Point2D.Double pdfCorner = new Point2D.Double();
        this._transform.transform(docCorner, pdfCorner);
        ((Point2D)pdfCorner).setLocation(((Point2D)pdfCorner).getX(), this.normalizeY((float)((Point2D)pdfCorner).getY()));
        Rectangle result = new Rectangle((float)((Point2D)pdfCorner).getX(), (float)((Point2D)pdfCorner).getY(), (float)((Point2D)pdfCorner).getX() + this.getDeviceLength(bounds.width), (float)((Point2D)pdfCorner).getY() + this.getDeviceLength(bounds.height));
        return result;
    }

    public Rectangle createTargetArea(RenderingContext c, Box box) {
        boolean inCurrentPage;
        PageBox current = c.getPage();
        boolean bl = inCurrentPage = box.getAbsY() > current.getTop() && box.getAbsY() < current.getBottom();
        if (inCurrentPage || box.isContainedInMarginBox()) {
            return this.createLocalTargetArea(c, box);
        }
        java.awt.Rectangle bounds = box.getContentAreaEdge(box.getAbsX(), box.getAbsY(), c);
        PageBox page = this._root.getLayer().getPage(c, bounds.y);
        float bottom = this.getDeviceLength(page.getBottom() - (bounds.y + bounds.height) + page.getMarginBorderPadding(c, 4));
        float left = this.getDeviceLength(page.getMarginBorderPadding(c, 1) + bounds.x);
        Rectangle result = new Rectangle(left, bottom, left + this.getDeviceLength(bounds.width), bottom + this.getDeviceLength(bounds.height));
        return result;
    }

    public float getDeviceLength(float length) {
        return length / this._dotsPerPoint;
    }

    private PdfDestination createDestination(RenderingContext c, Box box) {
        PdfDestination result = null;
        PageBox page = this._root.getLayer().getPage(c, this.getPageRefY(box));
        if (page != null) {
            int distanceFromTop = page.getMarginBorderPadding(c, 3);
            distanceFromTop = (int)((float)distanceFromTop + ((float)box.getAbsY() + box.getMargin(c).top() - (float)page.getTop()));
            result = new PdfDestination(0, 0.0f, (float)page.getHeight(c) / this._dotsPerPoint - (float)distanceFromTop / this._dotsPerPoint, 0.0f);
            result.addPage(this._writer.getPageReference(this._startPageNo + page.getPageNo() + 1));
        }
        return result;
    }

    @Override
    public void drawBorderLine(Shape bounds, int side, int lineWidth, boolean solid) {
        this.draw(bounds);
    }

    @Override
    public void setColor(FSColor color) {
        if (color instanceof FSRGBColor) {
            FSRGBColor rgb = (FSRGBColor)color;
            this._color = new Color(rgb.getRed(), rgb.getGreen(), rgb.getBlue());
        } else if (color instanceof FSCMYKColor) {
            FSCMYKColor cmyk = (FSCMYKColor)color;
            this._color = new CMYKColor(cmyk.getCyan(), cmyk.getMagenta(), cmyk.getYellow(), cmyk.getBlack());
        } else {
            throw new RuntimeException("internal error: unsupported color class " + color.getClass().getName());
        }
    }

    @Override
    public void draw(Shape s) {
        this.followPath(s, 2);
    }

    @Override
    protected void drawLine(int x1, int y1, int x2, int y2) {
        Line2D.Double line = new Line2D.Double(x1, y1, x2, y2);
        this.draw(line);
    }

    @Override
    public void drawRect(int x, int y, int width, int height) {
        this.draw(new java.awt.Rectangle(x, y, width, height));
    }

    @Override
    public void drawOval(int x, int y, int width, int height) {
        Ellipse2D.Float oval = new Ellipse2D.Float(x, y, width, height);
        this.draw(oval);
    }

    @Override
    public void fill(Shape s) {
        this.followPath(s, 1);
    }

    @Override
    public void fillRect(int x, int y, int width, int height) {
        if (ROUND_RECT_DIMENSIONS_DOWN) {
            this.fill(new java.awt.Rectangle(x, y, width - 1, height - 1));
        } else {
            this.fill(new java.awt.Rectangle(x, y, width, height));
        }
    }

    @Override
    public void fillOval(int x, int y, int width, int height) {
        Ellipse2D.Float oval = new Ellipse2D.Float(x, y, width, height);
        this.fill(oval);
    }

    @Override
    public void translate(double tx, double ty) {
        this._transform.translate(tx, ty);
    }

    @Override
    public Object getRenderingHint(RenderingHints.Key key) {
        return null;
    }

    @Override
    public void setRenderingHint(RenderingHints.Key key, Object value) {
    }

    @Override
    public void setFont(FSFont font) {
        this._font = (ITextFSFont)font;
    }

    private AffineTransform normalizeMatrix(AffineTransform current) {
        double[] mx = new double[6];
        AffineTransform result = new AffineTransform();
        result.getMatrix(mx);
        mx[3] = -1.0;
        mx[5] = this._pageHeight;
        result = new AffineTransform(mx);
        result.concatenate(current);
        return result;
    }

    public void drawString(String s, float x, float y, JustificationInfo info) {
        if (Configuration.isTrue("xr.renderer.replace-missing-characters", false)) {
            s = this.replaceMissingCharacters(s);
        }
        if (s.length() == 0) {
            return;
        }
        PdfContentByte cb = this._currentPage;
        this.ensureFillColor();
        AffineTransform at = (AffineTransform)this.getTransform().clone();
        at.translate(x, y);
        AffineTransform inverse = this.normalizeMatrix(at);
        AffineTransform flipper = AffineTransform.getScaleInstance(1.0, -1.0);
        inverse.concatenate(flipper);
        inverse.scale(this._dotsPerPoint, this._dotsPerPoint);
        double[] mx = new double[6];
        inverse.getMatrix(mx);
        cb.beginText();
        boolean resetMode = false;
        ITextFontResolver.FontDescription desc = this._font.getFontDescription();
        float fontSize = this._font.getSize2D() / this._dotsPerPoint;
        cb.setFontAndSize(desc.getFont(), fontSize);
        float b = (float)mx[1];
        float c = (float)mx[2];
        FontSpecification fontSpec = this.getFontSpecification();
        if (fontSpec != null) {
            int have;
            int need = ITextFontResolver.convertWeightToInt(fontSpec.fontWeight);
            if (need > (have = desc.getWeight())) {
                cb.setTextRenderingMode(2);
                float lineWidth = fontSize * 0.04f;
                cb.setLineWidth(lineWidth);
                resetMode = true;
                this.ensureStrokeColor();
            }
            if (fontSpec.fontStyle == IdentValue.ITALIC && desc.getStyle() != IdentValue.ITALIC && desc.getStyle() != IdentValue.OBLIQUE) {
                b = 0.0f;
                c = 0.21256f;
            }
        }
        cb.setTextMatrix((float)mx[0], b, c, (float)mx[3], (float)mx[4], (float)mx[5]);
        if (info == null) {
            cb.showText(s);
        } else {
            PdfTextArray array = this.makeJustificationArray(s, info);
            cb.showText(array);
        }
        if (resetMode) {
            cb.setTextRenderingMode(0);
            cb.setLineWidth(1.0f);
        }
        cb.endText();
    }

    private String replaceMissingCharacters(String string) {
        char[] charArr = string.toCharArray();
        char replacementCharacter = Configuration.valueAsChar("xr.renderer.missing-character-replacement", '#');
        if (!this._font.getFontDescription().getFont().charExists(replacementCharacter)) {
            XRLog.render(Level.INFO, "Missing replacement character [" + replacementCharacter + ":" + replacementCharacter + "]. No replacement will occur.");
            return string;
        }
        for (int i = 0; i < charArr.length; ++i) {
            if (charArr[i] == ' ' || charArr[i] == '\u00a0' || charArr[i] == '\u3000' || this._font.getFontDescription().getFont().charExists(charArr[i])) continue;
            XRLog.render(Level.INFO, "Missing character [" + charArr[i] + ":" + charArr[i] + "] in string [" + string + "]. Replacing with '" + replacementCharacter + "'");
            charArr[i] = replacementCharacter;
        }
        return String.valueOf(charArr);
    }

    private PdfTextArray makeJustificationArray(String s, JustificationInfo info) {
        PdfTextArray array = new PdfTextArray();
        int len = s.length();
        for (int i = 0; i < len; ++i) {
            char c = s.charAt(i);
            array.add(Character.toString(c));
            if (i == len - 1) continue;
            float offset = c == ' ' || c == '\u00a0' || c == '\u3000' ? info.getSpaceAdjust() : info.getNonSpaceAdjust();
            array.add(-offset / this._dotsPerPoint * 1000.0f / (this._font.getSize2D() / this._dotsPerPoint));
        }
        return array;
    }

    public AffineTransform getTransform() {
        return this._transform;
    }

    private void ensureFillColor() {
        if (!this._color.equals(this._fillColor)) {
            this._fillColor = this._color;
            this._currentPage.setColorFill(this._fillColor);
        }
    }

    private void ensureStrokeColor() {
        if (!this._color.equals(this._strokeColor)) {
            this._strokeColor = this._color;
            this._currentPage.setColorStroke(this._strokeColor);
        }
    }

    public PdfContentByte getCurrentPage() {
        return this._currentPage;
    }

    private void followPath(Shape s, int drawType) {
        PdfContentByte cb = this._currentPage;
        if (s == null) {
            return;
        }
        if (drawType == 2 && !(this._stroke instanceof BasicStroke)) {
            s = this._stroke.createStrokedShape(s);
            this.followPath(s, 1);
            return;
        }
        if (drawType == 2) {
            this.setStrokeDiff(this._stroke, this._oldStroke);
            this._oldStroke = this._stroke;
            this.ensureStrokeColor();
        } else if (drawType == 1) {
            this.ensureFillColor();
        }
        PathIterator points = drawType == 3 ? s.getPathIterator(IDENTITY) : s.getPathIterator(this._transform);
        float[] coords = new float[6];
        int traces = 0;
        while (!points.isDone()) {
            ++traces;
            int segtype = points.currentSegment(coords);
            this.normalizeY(coords);
            switch (segtype) {
                case 4: {
                    cb.closePath();
                    break;
                }
                case 3: {
                    cb.curveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
                    break;
                }
                case 1: {
                    cb.lineTo(coords[0], coords[1]);
                    break;
                }
                case 0: {
                    cb.moveTo(coords[0], coords[1]);
                    break;
                }
                case 2: {
                    System.out.println("Quad to " + coords[0] + " " + coords[1] + " " + coords[2] + " " + coords[3]);
                    cb.curveTo(coords[0], coords[1], coords[2], coords[3]);
                }
            }
            points.next();
        }
        switch (drawType) {
            case 1: {
                if (traces <= 0) break;
                if (points.getWindingRule() == 0) {
                    cb.eoFill();
                    break;
                }
                cb.fill();
                break;
            }
            case 2: {
                if (traces <= 0) break;
                cb.stroke();
                break;
            }
            default: {
                if (traces == 0) {
                    cb.rectangle(0.0f, 0.0f, 0.0f, 0.0f);
                }
                if (points.getWindingRule() == 0) {
                    cb.eoClip();
                } else {
                    cb.clip();
                }
                cb.newPath();
            }
        }
    }

    private float normalizeY(float y) {
        return this._pageHeight - y;
    }

    private void normalizeY(float[] coords) {
        coords[1] = this.normalizeY(coords[1]);
        coords[3] = this.normalizeY(coords[3]);
        coords[5] = this.normalizeY(coords[5]);
    }

    private void setStrokeDiff(Stroke newStroke, Stroke oldStroke) {
        PdfContentByte cb = this._currentPage;
        if (newStroke == oldStroke) {
            return;
        }
        if (!(newStroke instanceof BasicStroke)) {
            return;
        }
        BasicStroke nStroke = (BasicStroke)newStroke;
        boolean oldOk = oldStroke instanceof BasicStroke;
        BasicStroke oStroke = null;
        if (oldOk) {
            oStroke = (BasicStroke)oldStroke;
        }
        if (!oldOk || nStroke.getLineWidth() != oStroke.getLineWidth()) {
            cb.setLineWidth(nStroke.getLineWidth());
        }
        if (!oldOk || nStroke.getEndCap() != oStroke.getEndCap()) {
            switch (nStroke.getEndCap()) {
                case 0: {
                    cb.setLineCap(0);
                    break;
                }
                case 2: {
                    cb.setLineCap(2);
                    break;
                }
                default: {
                    cb.setLineCap(1);
                }
            }
        }
        if (!oldOk || nStroke.getLineJoin() != oStroke.getLineJoin()) {
            switch (nStroke.getLineJoin()) {
                case 0: {
                    cb.setLineJoin(0);
                    break;
                }
                case 2: {
                    cb.setLineJoin(2);
                    break;
                }
                default: {
                    cb.setLineJoin(1);
                }
            }
        }
        if (!oldOk || nStroke.getMiterLimit() != oStroke.getMiterLimit()) {
            cb.setMiterLimit(nStroke.getMiterLimit());
        }
        boolean makeDash = oldOk ? (nStroke.getDashArray() != null ? (nStroke.getDashPhase() != oStroke.getDashPhase() ? true : !Arrays.equals(nStroke.getDashArray(), oStroke.getDashArray())) : oStroke.getDashArray() != null) : true;
        if (makeDash) {
            float[] dash = nStroke.getDashArray();
            if (dash == null) {
                cb.setLiteral("[]0 d\n");
            } else {
                cb.setLiteral('[');
                int lim = dash.length;
                for (int k = 0; k < lim; ++k) {
                    cb.setLiteral(dash[k]);
                    cb.setLiteral(' ');
                }
                cb.setLiteral(']');
                cb.setLiteral(nStroke.getDashPhase());
                cb.setLiteral(" d\n");
            }
        }
    }

    @Override
    public void setStroke(Stroke s) {
        this._originalStroke = s;
        this._stroke = this.transformStroke(s);
    }

    private Stroke transformStroke(Stroke stroke) {
        if (!(stroke instanceof BasicStroke)) {
            return stroke;
        }
        BasicStroke st = (BasicStroke)stroke;
        float scale = (float)Math.sqrt(Math.abs(this._transform.getDeterminant()));
        float[] dash = st.getDashArray();
        if (dash != null) {
            int k = 0;
            while (k < dash.length) {
                int n = k++;
                dash[n] = dash[n] * scale;
            }
        }
        return new BasicStroke(st.getLineWidth() * scale, st.getEndCap(), st.getLineJoin(), st.getMiterLimit(), dash, st.getDashPhase() * scale);
    }

    @Override
    public void clip(Shape s) {
        if (s != null) {
            s = this._transform.createTransformedShape(s);
            if (this._clip == null) {
                this._clip = new Area(s);
            } else {
                this._clip.intersect(new Area(s));
            }
        } else {
            throw new XRRuntimeException("Shape is null, unexpected");
        }
        this.followPath(s, 3);
    }

    @Override
    public Shape getClip() {
        try {
            return this._transform.createInverse().createTransformedShape(this._clip);
        }
        catch (NoninvertibleTransformException e) {
            return null;
        }
    }

    @Override
    public void setClip(Shape s) {
        PdfContentByte cb = this._currentPage;
        cb.restoreState();
        cb.saveState();
        if (s != null) {
            s = this._transform.createTransformedShape(s);
        }
        if (s == null) {
            this._clip = null;
        } else {
            this._clip = new Area(s);
            this.followPath(s, 3);
        }
        this._fillColor = null;
        this._strokeColor = null;
        this._oldStroke = null;
    }

    @Override
    public Stroke getStroke() {
        return this._originalStroke;
    }

    @Override
    public void drawImage(FSImage fsImage, int x, int y) {
        if (fsImage instanceof PDFAsImage) {
            this.drawPDFAsImage((PDFAsImage)fsImage, x, y);
        } else {
            Image image = ((ITextFSImage)fsImage).getImage();
            if (fsImage.getHeight() <= 0 || fsImage.getWidth() <= 0) {
                return;
            }
            AffineTransform at = AffineTransform.getTranslateInstance(x, y);
            at.translate(0.0, fsImage.getHeight());
            at.scale(fsImage.getWidth(), fsImage.getHeight());
            AffineTransform inverse = this.normalizeMatrix(this._transform);
            AffineTransform flipper = AffineTransform.getScaleInstance(1.0, -1.0);
            inverse.concatenate(at);
            inverse.concatenate(flipper);
            double[] mx = new double[6];
            inverse.getMatrix(mx);
            try {
                this._currentPage.addImage(image, (float)mx[0], (float)mx[1], (float)mx[2], (float)mx[3], (float)mx[4], (float)mx[5]);
            }
            catch (DocumentException e) {
                throw new XRRuntimeException(e.getMessage(), e);
            }
        }
    }

    private void drawPDFAsImage(PDFAsImage image, int x, int y) {
        URI uri = image.getURI();
        PdfReader reader = null;
        try {
            reader = this.getReader(uri);
        }
        catch (IOException e) {
            throw new XRRuntimeException("Could not load " + uri + ": " + e.getMessage(), e);
        }
        PdfImportedPage page = this.getWriter().getImportedPage(reader, 1);
        AffineTransform at = AffineTransform.getTranslateInstance(x, y);
        at.translate(0.0, image.getHeightAsFloat());
        at.scale(image.getWidthAsFloat(), image.getHeightAsFloat());
        AffineTransform inverse = this.normalizeMatrix(this._transform);
        AffineTransform flipper = AffineTransform.getScaleInstance(1.0, -1.0);
        inverse.concatenate(at);
        inverse.concatenate(flipper);
        double[] mx = new double[6];
        inverse.getMatrix(mx);
        mx[0] = image.scaleWidth();
        mx[3] = image.scaleHeight();
        this._currentPage.restoreState();
        this._currentPage.addTemplate(page, (float)mx[0], (float)mx[1], (float)mx[2], (float)mx[3], (float)mx[4], (float)mx[5]);
        this._currentPage.saveState();
    }

    public PdfReader getReader(URI uri) throws IOException {
        PdfReader result = (PdfReader)this._readerCache.get(uri);
        if (result == null) {
            result = new PdfReader(this.getSharedContext().getUserAgentCallback().getBinaryResource(uri.toString()));
            this._readerCache.put(uri, result);
        }
        return result;
    }

    public float getDotsPerPoint() {
        return this._dotsPerPoint;
    }

    public void start(Document doc) {
        this.loadBookmarks(doc);
        this.loadMetadata(doc);
    }

    public void finish(RenderingContext c, Box root) {
        this.writeOutline(c, root);
        this.writeNamedDestinations(c);
        this._bookmarks.clear();
    }

    private void writeOutline(RenderingContext c, Box root) {
        if (this._bookmarks.isEmpty()) {
            this._bookmarks = HTMLOutline.generate(root.getElement(), root);
        }
        if (this._bookmarks.size() > 0) {
            this._writer.setViewerPreferences(128);
            this.writeBookmarks(c, root, this._writer.getRootOutline(), this._bookmarks);
        }
    }

    private void writeBookmarks(RenderingContext c, Box root, PdfOutline parent, List bookmarks) {
        for (Bookmark bookmark : bookmarks) {
            this.writeBookmark(c, root, parent, bookmark);
        }
    }

    private void writeNamedDestinations(RenderingContext c) {
        Map idMap = this.getSharedContext().getIdMap();
        if (idMap != null && !idMap.isEmpty()) {
            PdfArray dests = new PdfArray();
            try {
                for (Map.Entry entry : idMap.entrySet()) {
                    Box targetBox = (Box)entry.getValue();
                    if (!targetBox.getStyle().isIdent(CSSName.FS_NAMED_DESTINATION, IdentValue.CREATE)) continue;
                    String anchorName = (String)entry.getKey();
                    dests.add(new PdfString(anchorName, "UnicodeBig"));
                    PdfDestination dest = this.createDestination(c, targetBox);
                    if (dest == null) continue;
                    PdfIndirectReference ref = this._writer.addToBody(dest).getIndirectReference();
                    dests.add(ref);
                }
                if (!dests.isEmpty()) {
                    PdfDictionary nametree = new PdfDictionary();
                    nametree.put(PdfName.NAMES, dests);
                    PdfIndirectReference nameTreeRef = this._writer.addToBody(nametree).getIndirectReference();
                    PdfDictionary names = new PdfDictionary();
                    names.put(PdfName.DESTS, nameTreeRef);
                    PdfIndirectReference destinationsRef = this._writer.addToBody(names).getIndirectReference();
                    this._writer.getExtraCatalog().put(PdfName.NAMES, destinationsRef);
                }
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private int getPageRefY(Box box) {
        if (box instanceof InlineLayoutBox) {
            InlineLayoutBox iB = (InlineLayoutBox)box;
            return iB.getAbsY() + iB.getBaseline();
        }
        return box.getAbsY();
    }

    private void writeBookmark(RenderingContext c, Box root, PdfOutline parent, Bookmark bookmark) {
        String href = bookmark.getHRef();
        PdfDestination target = null;
        Box box = bookmark.getBox();
        if (href.length() > 0 && href.charAt(0) == '#') {
            box = this._sharedContext.getBoxById(href.substring(1));
        }
        if (box != null) {
            PageBox page = root.getLayer().getPage(c, this.getPageRefY(box));
            int distanceFromTop = page.getMarginBorderPadding(c, 3);
            target = new PdfDestination(0, 0.0f, this.normalizeY((float)(distanceFromTop += box.getAbsY() - page.getTop()) / this._dotsPerPoint), 0.0f);
            target.addPage(this._writer.getPageReference(this._startPageNo + page.getPageNo() + 1));
        }
        if (target == null) {
            target = this._defaultDestination;
        }
        PdfOutline outline = new PdfOutline(parent, target, bookmark.getName());
        this.writeBookmarks(c, root, outline, bookmark.getChildren());
    }

    private void loadBookmarks(Document doc) {
        List l;
        Element bookmarks;
        Element head = DOMUtil.getChild(doc.getDocumentElement(), "head");
        if (head != null && (bookmarks = DOMUtil.getChild(head, "bookmarks")) != null && (l = DOMUtil.getChildren(bookmarks, "bookmark")) != null) {
            for (Element e : l) {
                this.loadBookmark(null, e);
            }
        }
    }

    private void loadBookmark(Bookmark parent, Element bookmark) {
        Bookmark us = new Bookmark(bookmark.getAttribute("name"), bookmark.getAttribute("href"));
        if (parent == null) {
            this._bookmarks.add(us);
        } else {
            parent.addChild(us);
        }
        List l = DOMUtil.getChildren(bookmark, "bookmark");
        if (l != null) {
            for (Element e : l) {
                this.loadBookmark(us, e);
            }
        }
    }

    public void addMetadata(String name, String value) {
        if (name != null && value != null) {
            Metadata m = new Metadata(name, value);
            this._metadata.add(m);
        }
    }

    public String getMetadataByName(String name) {
        if (name != null) {
            int len = this._metadata.size();
            for (int i = 0; i < len; ++i) {
                Metadata m = (Metadata)this._metadata.get(i);
                if (m == null || !m.getName().equalsIgnoreCase(name)) continue;
                return m.getContent();
            }
        }
        return null;
    }

    public ArrayList getMetadataListByName(String name) {
        ArrayList<String> result = new ArrayList<String>();
        if (name != null) {
            int len = this._metadata.size();
            for (int i = 0; i < len; ++i) {
                Metadata m = (Metadata)this._metadata.get(i);
                if (m == null || !m.getName().equalsIgnoreCase(name)) continue;
                result.add(m.getContent());
            }
        }
        return result;
    }

    private void loadMetadata(Document doc) {
        Element head = DOMUtil.getChild(doc.getDocumentElement(), "head");
        if (head != null) {
            Element t;
            String title;
            List l = DOMUtil.getChildren(head, "meta");
            if (l != null) {
                for (Element e : l) {
                    String name = e.getAttribute("name");
                    if (name == null) continue;
                    String content = e.getAttribute("content");
                    Metadata m = new Metadata(name, content);
                    this._metadata.add(m);
                }
            }
            if ((title = this.getMetadataByName("title")) == null && (t = DOMUtil.getChild(head, "title")) != null) {
                title = DOMUtil.getText(t).trim();
                Metadata m = new Metadata("title", title);
                this._metadata.add(m);
            }
        }
    }

    public void setMetadata(String name, String value) {
        if (name != null) {
            boolean remove = value == null;
            int free = -1;
            int len = this._metadata.size();
            for (int i = 0; i < len; ++i) {
                Metadata m = (Metadata)this._metadata.get(i);
                if (m != null) {
                    if (!m.getName().equalsIgnoreCase(name)) continue;
                    if (!remove) {
                        remove = true;
                        m.setContent(value);
                        continue;
                    }
                    this._metadata.set(i, null);
                    continue;
                }
                if (free != -1) continue;
                free = i;
            }
            if (!remove) {
                Metadata m = new Metadata(name, value);
                if (free == -1) {
                    this._metadata.add(m);
                } else {
                    this._metadata.set(free, m);
                }
            }
        }
    }

    public SharedContext getSharedContext() {
        return this._sharedContext;
    }

    public void setSharedContext(SharedContext sharedContext) {
        this._sharedContext = sharedContext;
        sharedContext.getCss().setSupportCMYKColors(true);
    }

    public void setRoot(Box root) {
        this._root = root;
    }

    public int getStartPageNo() {
        return this._startPageNo;
    }

    public void setStartPageNo(int startPageNo) {
        this._startPageNo = startPageNo;
    }

    @Override
    public void drawSelection(RenderingContext c, InlineText inlineText) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSupportsSelection() {
        return false;
    }

    @Override
    public boolean isSupportsCMYKColors() {
        return true;
    }

    public List findPagePositionsByID(CssContext c, Pattern pattern) {
        Map idMap = this._sharedContext.getIdMap();
        if (idMap == null) {
            return Collections.EMPTY_LIST;
        }
        ArrayList<PagePosition> result = new ArrayList<PagePosition>();
        for (Map.Entry entry : idMap.entrySet()) {
            Box box;
            PagePosition pos;
            String id = (String)entry.getKey();
            if (!pattern.matcher(id).find() || (pos = this.calcPDFPagePosition(c, id, box = (Box)entry.getValue())) == null) continue;
            result.add(pos);
        }
        Collections.sort(result, new Comparator(){

            public int compare(Object arg0, Object arg1) {
                PagePosition p1 = (PagePosition)arg0;
                PagePosition p2 = (PagePosition)arg1;
                return p1.getPageNo() - p2.getPageNo();
            }
        });
        return result;
    }

    private PagePosition calcPDFPagePosition(CssContext c, String id, Box box) {
        PageBox page = this._root.getLayer().getLastPage(c, box);
        if (page == null) {
            return null;
        }
        float x = box.getAbsX() + page.getMarginBorderPadding(c, 1);
        float y = page.getBottom() - (box.getAbsY() + box.getHeight()) + page.getMarginBorderPadding(c, 4);
        PagePosition result = new PagePosition();
        result.setId(id);
        result.setPageNo(page.getPageNo());
        result.setX(x /= this._dotsPerPoint);
        result.setY(y /= this._dotsPerPoint);
        result.setWidth((float)box.getEffectiveWidth() / this._dotsPerPoint);
        result.setHeight((float)box.getHeight() / this._dotsPerPoint);
        return result;
    }

    private static class Metadata {
        private String _name;
        private String _content;

        public Metadata(String name, String content) {
            this._name = name;
            this._content = content;
        }

        public String getContent() {
            return this._content;
        }

        public void setContent(String content) {
            this._content = content;
        }

        public String getName() {
            return this._name;
        }

        public void setName(String name) {
            this._name = name;
        }
    }

    static class Bookmark {
        private String _name;
        private String _HRef;
        private Box _box;
        private List _children;

        public Bookmark() {
        }

        public Bookmark(String name, String href) {
            this._name = name;
            this._HRef = href;
        }

        public Box getBox() {
            return this._box;
        }

        public void setBox(Box box) {
            this._box = box;
        }

        public String getHRef() {
            return this._HRef;
        }

        public void setHRef(String href) {
            this._HRef = href;
        }

        public String getName() {
            return this._name;
        }

        public void setName(String name) {
            this._name = name;
        }

        public void addChild(Bookmark child) {
            if (this._children == null) {
                this._children = new ArrayList();
            }
            this._children.add(child);
        }

        public List getChildren() {
            return this._children == null ? Collections.EMPTY_LIST : this._children;
        }
    }
}

