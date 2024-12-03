/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdmodel.fdf;

import java.awt.Color;
import java.io.IOException;
import java.util.Calendar;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotationCaret;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotationCircle;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotationFileAttachment;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotationFreeText;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotationHighlight;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotationInk;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotationLine;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotationLink;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotationPolygon;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotationPolyline;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotationSound;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotationSquare;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotationSquiggly;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotationStamp;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotationStrikeOut;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotationText;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotationUnderline;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderEffectDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.apache.pdfbox.util.DateConverter;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public abstract class FDFAnnotation
implements COSObjectable {
    private static final Log LOG = LogFactory.getLog(FDFAnnotation.class);
    private static final int FLAG_INVISIBLE = 1;
    private static final int FLAG_HIDDEN = 2;
    private static final int FLAG_PRINTED = 4;
    private static final int FLAG_NO_ZOOM = 8;
    private static final int FLAG_NO_ROTATE = 16;
    private static final int FLAG_NO_VIEW = 32;
    private static final int FLAG_READ_ONLY = 64;
    private static final int FLAG_LOCKED = 128;
    private static final int FLAG_TOGGLE_NO_VIEW = 256;
    private static final int FLAG_LOCKED_CONTENTS = 512;
    protected COSDictionary annot;

    public FDFAnnotation() {
        this.annot = new COSDictionary();
        this.annot.setItem(COSName.TYPE, (COSBase)COSName.ANNOT);
    }

    public FDFAnnotation(COSDictionary a) {
        this.annot = a;
    }

    public FDFAnnotation(Element element) throws IOException {
        this();
        String page = element.getAttribute("page");
        if (page == null || page.isEmpty()) {
            throw new IOException("Error: missing required attribute 'page'");
        }
        this.setPage(Integer.parseInt(page));
        String color = element.getAttribute("color");
        if (color != null && color.length() == 7 && color.charAt(0) == '#') {
            int colorValue = Integer.parseInt(color.substring(1, 7), 16);
            this.setColor(new Color(colorValue));
        }
        this.setDate(element.getAttribute("date"));
        String flags = element.getAttribute("flags");
        if (flags != null) {
            String[] flagTokens;
            for (String flagToken : flagTokens = flags.split(",")) {
                if (flagToken.equals("invisible")) {
                    this.setInvisible(true);
                    continue;
                }
                if (flagToken.equals("hidden")) {
                    this.setHidden(true);
                    continue;
                }
                if (flagToken.equals("print")) {
                    this.setPrinted(true);
                    continue;
                }
                if (flagToken.equals("nozoom")) {
                    this.setNoZoom(true);
                    continue;
                }
                if (flagToken.equals("norotate")) {
                    this.setNoRotate(true);
                    continue;
                }
                if (flagToken.equals("noview")) {
                    this.setNoView(true);
                    continue;
                }
                if (flagToken.equals("readonly")) {
                    this.setReadOnly(true);
                    continue;
                }
                if (flagToken.equals("locked")) {
                    this.setLocked(true);
                    continue;
                }
                if (!flagToken.equals("togglenoview")) continue;
                this.setToggleNoView(true);
            }
        }
        this.setName(element.getAttribute("name"));
        String rect = element.getAttribute("rect");
        if (rect == null) {
            throw new IOException("Error: missing attribute 'rect'");
        }
        String[] rectValues = rect.split(",");
        if (rectValues.length != 4) {
            throw new IOException("Error: wrong amount of numbers in attribute 'rect'");
        }
        float[] values = new float[4];
        for (int i = 0; i < 4; ++i) {
            values[i] = Float.parseFloat(rectValues[i]);
        }
        COSArray array = new COSArray();
        array.setFloatArray(values);
        this.setRectangle(new PDRectangle(array));
        this.setTitle(element.getAttribute("title"));
        this.setCreationDate(DateConverter.toCalendar(element.getAttribute("creationdate")));
        String opac = element.getAttribute("opacity");
        if (opac != null && !opac.isEmpty()) {
            this.setOpacity(Float.parseFloat(opac));
        }
        this.setSubject(element.getAttribute("subject"));
        String intent = element.getAttribute("intent");
        if (intent.isEmpty()) {
            intent = element.getAttribute("IT");
        }
        if (!intent.isEmpty()) {
            this.setIntent(intent);
        }
        XPath xpath = XPathFactory.newInstance().newXPath();
        try {
            this.setContents(xpath.evaluate("contents[1]", element));
        }
        catch (XPathExpressionException e) {
            LOG.debug((Object)"Error while evaluating XPath expression for richtext contents");
        }
        try {
            Node richContents = (Node)xpath.evaluate("contents-richtext[1]", element, XPathConstants.NODE);
            if (richContents != null) {
                this.setRichContents(this.richContentsToString(richContents, true));
                this.setContents(richContents.getTextContent().trim());
            }
        }
        catch (XPathExpressionException e) {
            LOG.debug((Object)"Error while evaluating XPath expression for richtext contents");
        }
        PDBorderStyleDictionary borderStyle = new PDBorderStyleDictionary();
        String width = element.getAttribute("width");
        if (width != null && !width.isEmpty()) {
            borderStyle.setWidth(Float.parseFloat(width));
        }
        if (borderStyle.getWidth() > 0.0f) {
            String dashes;
            String style = element.getAttribute("style");
            if (style != null && !style.isEmpty()) {
                if (style.equals("dash")) {
                    borderStyle.setStyle("D");
                } else if (style.equals("bevelled")) {
                    borderStyle.setStyle("B");
                } else if (style.equals("inset")) {
                    borderStyle.setStyle("I");
                } else if (style.equals("underline")) {
                    borderStyle.setStyle("U");
                } else if (style.equals("cloudy")) {
                    borderStyle.setStyle("S");
                    PDBorderEffectDictionary borderEffect = new PDBorderEffectDictionary();
                    borderEffect.setStyle("C");
                    String intensity = element.getAttribute("intensity");
                    if (intensity != null && !intensity.isEmpty()) {
                        borderEffect.setIntensity(Float.parseFloat(element.getAttribute("intensity")));
                    }
                    this.setBorderEffect(borderEffect);
                } else {
                    borderStyle.setStyle("S");
                }
            }
            if ((dashes = element.getAttribute("dashes")) != null && !dashes.isEmpty()) {
                String[] dashesValues = dashes.split(",");
                COSArray dashPattern = new COSArray();
                for (String dashesValue : dashesValues) {
                    dashPattern.add(COSNumber.get(dashesValue));
                }
                borderStyle.setDashStyle(dashPattern);
            }
            this.setBorderStyle(borderStyle);
        }
    }

    public static FDFAnnotation create(COSDictionary fdfDic) throws IOException {
        FDFAnnotation retval = null;
        if (fdfDic != null) {
            String fdfDicName = fdfDic.getNameAsString(COSName.SUBTYPE);
            if ("Text".equals(fdfDicName)) {
                retval = new FDFAnnotationText(fdfDic);
            } else if ("Caret".equals(fdfDicName)) {
                retval = new FDFAnnotationCaret(fdfDic);
            } else if ("FreeText".equals(fdfDicName)) {
                retval = new FDFAnnotationFreeText(fdfDic);
            } else if ("FileAttachment".equals(fdfDicName)) {
                retval = new FDFAnnotationFileAttachment(fdfDic);
            } else if ("Highlight".equals(fdfDicName)) {
                retval = new FDFAnnotationHighlight(fdfDic);
            } else if ("Ink".equals(fdfDicName)) {
                retval = new FDFAnnotationInk(fdfDic);
            } else if ("Line".equals(fdfDicName)) {
                retval = new FDFAnnotationLine(fdfDic);
            } else if ("Link".equals(fdfDicName)) {
                retval = new FDFAnnotationLink(fdfDic);
            } else if ("Circle".equals(fdfDicName)) {
                retval = new FDFAnnotationCircle(fdfDic);
            } else if ("Square".equals(fdfDicName)) {
                retval = new FDFAnnotationSquare(fdfDic);
            } else if ("Polygon".equals(fdfDicName)) {
                retval = new FDFAnnotationPolygon(fdfDic);
            } else if ("Polyline".equals(fdfDicName)) {
                retval = new FDFAnnotationPolyline(fdfDic);
            } else if ("Sound".equals(fdfDicName)) {
                retval = new FDFAnnotationSound(fdfDic);
            } else if ("Squiggly".equals(fdfDicName)) {
                retval = new FDFAnnotationSquiggly(fdfDic);
            } else if ("Stamp".equals(fdfDicName)) {
                retval = new FDFAnnotationStamp(fdfDic);
            } else if ("StrikeOut".equals(fdfDicName)) {
                retval = new FDFAnnotationStrikeOut(fdfDic);
            } else if ("Underline".equals(fdfDicName)) {
                retval = new FDFAnnotationUnderline(fdfDic);
            } else {
                LOG.warn((Object)("Unknown or unsupported annotation type '" + fdfDicName + "'"));
            }
        }
        return retval;
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.annot;
    }

    public Integer getPage() {
        Integer retval = null;
        COSNumber page = (COSNumber)this.annot.getDictionaryObject(COSName.PAGE);
        if (page != null) {
            retval = page.intValue();
        }
        return retval;
    }

    public final void setPage(int page) {
        this.annot.setInt(COSName.PAGE, page);
    }

    public Color getColor() {
        float[] rgb;
        Color retval = null;
        COSArray array = (COSArray)this.annot.getDictionaryObject(COSName.C);
        if (array != null && (rgb = array.toFloatArray()).length >= 3) {
            retval = new Color(rgb[0], rgb[1], rgb[2]);
        }
        return retval;
    }

    public final void setColor(Color c) {
        COSArray color = null;
        if (c != null) {
            float[] colors = c.getRGBColorComponents(null);
            color = new COSArray();
            color.setFloatArray(colors);
        }
        this.annot.setItem(COSName.C, color);
    }

    public String getDate() {
        return this.annot.getString(COSName.M);
    }

    public final void setDate(String date) {
        this.annot.setString(COSName.M, date);
    }

    public boolean isInvisible() {
        return this.annot.getFlag(COSName.F, 1);
    }

    public final void setInvisible(boolean invisible) {
        this.annot.setFlag(COSName.F, 1, invisible);
    }

    public boolean isHidden() {
        return this.annot.getFlag(COSName.F, 2);
    }

    public final void setHidden(boolean hidden) {
        this.annot.setFlag(COSName.F, 2, hidden);
    }

    public boolean isPrinted() {
        return this.annot.getFlag(COSName.F, 4);
    }

    public final void setPrinted(boolean printed) {
        this.annot.setFlag(COSName.F, 4, printed);
    }

    public boolean isNoZoom() {
        return this.annot.getFlag(COSName.F, 8);
    }

    public final void setNoZoom(boolean noZoom) {
        this.annot.setFlag(COSName.F, 8, noZoom);
    }

    public boolean isNoRotate() {
        return this.annot.getFlag(COSName.F, 16);
    }

    public final void setNoRotate(boolean noRotate) {
        this.annot.setFlag(COSName.F, 16, noRotate);
    }

    public boolean isNoView() {
        return this.annot.getFlag(COSName.F, 32);
    }

    public final void setNoView(boolean noView) {
        this.annot.setFlag(COSName.F, 32, noView);
    }

    public boolean isReadOnly() {
        return this.annot.getFlag(COSName.F, 64);
    }

    public final void setReadOnly(boolean readOnly) {
        this.annot.setFlag(COSName.F, 64, readOnly);
    }

    public boolean isLocked() {
        return this.annot.getFlag(COSName.F, 128);
    }

    public final void setLocked(boolean locked) {
        this.annot.setFlag(COSName.F, 128, locked);
    }

    public boolean isToggleNoView() {
        return this.annot.getFlag(COSName.F, 256);
    }

    public final void setToggleNoView(boolean toggleNoView) {
        this.annot.setFlag(COSName.F, 256, toggleNoView);
    }

    public boolean isLockedContents() {
        return this.annot.getFlag(COSName.F, 512);
    }

    public void setLockedContents(boolean lockedContents) {
        this.annot.setFlag(COSName.F, 512, lockedContents);
    }

    public final void setName(String name) {
        this.annot.setString(COSName.NM, name);
    }

    public String getName() {
        return this.annot.getString(COSName.NM);
    }

    public final void setRectangle(PDRectangle rectangle) {
        this.annot.setItem(COSName.RECT, (COSObjectable)rectangle);
    }

    public PDRectangle getRectangle() {
        PDRectangle retval = null;
        COSArray rectArray = (COSArray)this.annot.getDictionaryObject(COSName.RECT);
        if (rectArray != null) {
            retval = new PDRectangle(rectArray);
        }
        return retval;
    }

    public final void setContents(String contents) {
        this.annot.setString(COSName.CONTENTS, contents);
    }

    public String getContents() {
        return this.annot.getString(COSName.CONTENTS);
    }

    public final void setTitle(String title) {
        this.annot.setString(COSName.T, title);
    }

    public String getTitle() {
        return this.annot.getString(COSName.T);
    }

    public Calendar getCreationDate() throws IOException {
        return this.annot.getDate(COSName.CREATION_DATE);
    }

    public final void setCreationDate(Calendar date) {
        this.annot.setDate(COSName.CREATION_DATE, date);
    }

    public final void setOpacity(float opacity) {
        this.annot.setFloat(COSName.CA, opacity);
    }

    public float getOpacity() {
        return this.annot.getFloat(COSName.CA, 1.0f);
    }

    public final void setSubject(String subject) {
        this.annot.setString(COSName.SUBJ, subject);
    }

    public String getSubject() {
        return this.annot.getString(COSName.SUBJ);
    }

    public final void setIntent(String intent) {
        this.annot.setName(COSName.IT, intent);
    }

    public String getIntent() {
        return this.annot.getNameAsString(COSName.IT);
    }

    public String getRichContents() {
        return this.getStringOrStream(this.annot.getDictionaryObject(COSName.RC));
    }

    public final void setRichContents(String rc) {
        this.annot.setItem(COSName.RC, (COSBase)new COSString(rc));
    }

    public final void setBorderStyle(PDBorderStyleDictionary bs) {
        this.annot.setItem(COSName.BS, (COSObjectable)bs);
    }

    public PDBorderStyleDictionary getBorderStyle() {
        COSDictionary bs = (COSDictionary)this.annot.getDictionaryObject(COSName.BS);
        if (bs != null) {
            return new PDBorderStyleDictionary(bs);
        }
        return null;
    }

    public final void setBorderEffect(PDBorderEffectDictionary be) {
        this.annot.setItem(COSName.BE, (COSObjectable)be);
    }

    public PDBorderEffectDictionary getBorderEffect() {
        COSDictionary be = (COSDictionary)this.annot.getDictionaryObject(COSName.BE);
        if (be != null) {
            return new PDBorderEffectDictionary(be);
        }
        return null;
    }

    protected final String getStringOrStream(COSBase base) {
        if (base == null) {
            return "";
        }
        if (base instanceof COSString) {
            return ((COSString)base).getString();
        }
        if (base instanceof COSStream) {
            return ((COSStream)base).toTextString();
        }
        return "";
    }

    private String richContentsToString(Node node, boolean root) {
        StringBuilder sb = new StringBuilder();
        NodeList nodelist = node.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); ++i) {
            Node child = nodelist.item(i);
            if (child instanceof Element) {
                sb.append(this.richContentsToString(child, false));
                continue;
            }
            if (child instanceof CDATASection) {
                sb.append("<![CDATA[").append(((CDATASection)child).getData()).append("]]>");
                continue;
            }
            if (!(child instanceof Text)) continue;
            String cdata = ((Text)child).getData();
            if (cdata != null) {
                cdata = cdata.replace("&", "&amp;").replace("<", "&lt;");
            }
            sb.append(cdata);
        }
        if (root) {
            return sb.toString();
        }
        NamedNodeMap attributes = node.getAttributes();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < attributes.getLength(); ++i) {
            Node attribute = attributes.item(i);
            String attributeNodeValue = attribute.getNodeValue();
            if (attributeNodeValue != null) {
                attributeNodeValue = attributeNodeValue.replace("\"", "&quot;");
            }
            builder.append(String.format(" %s=\"%s\"", attribute.getNodeName(), attributeNodeValue));
        }
        return String.format("<%s%s>%s</%s>", node.getNodeName(), builder.toString(), sb.toString(), node.getNodeName());
    }
}

