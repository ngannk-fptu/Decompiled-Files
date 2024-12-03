/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xwpf.usermodel;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.poi.common.usermodel.PictureType;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import org.apache.poi.ooxml.util.DocumentHelper;
import org.apache.poi.ooxml.util.POIXMLUnits;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LocaleUtil;
import org.apache.poi.util.Removal;
import org.apache.poi.util.Units;
import org.apache.poi.wp.usermodel.CharacterRun;
import org.apache.poi.xwpf.usermodel.BreakClear;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.IRunBody;
import org.apache.poi.xwpf.usermodel.IRunElement;
import org.apache.poi.xwpf.usermodel.ISDTContents;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.VerticalAlign;
import org.apache.poi.xwpf.usermodel.XWPFComments;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHeaderFooter;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPicture;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.impl.values.XmlAnyTypeImpl;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChart;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlip;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlipFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObjectData;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualPictureProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetGeometry2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTransform2D;
import org.openxmlformats.schemas.drawingml.x2006.main.STShapeType;
import org.openxmlformats.schemas.drawingml.x2006.picture.CTPicture;
import org.openxmlformats.schemas.drawingml.x2006.picture.CTPictureNonVisual;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTAnchor;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTInline;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STHexColorRGB;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STOnOff1;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STVerticalAlignRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTColor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDrawing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTEm;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTEmpty;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFFCheckBox;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFldChar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFonts;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFtnEdnRef;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHighlight;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHpsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLanguage;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPTab;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRuby;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRubyContent;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSignedHpsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSignedTwipsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTextScale;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTUnderline;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVerticalAlignRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBrClear;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBrType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STEm;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STFldCharType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHexColorAuto;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHighlightColor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STThemeColor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STUnderline;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XWPFRun
implements ISDTContents,
IRunElement,
CharacterRun {
    private final CTR run;
    private final String pictureText;
    private final IRunBody parent;
    private final List<XWPFPicture> pictures;

    public XWPFRun(CTR r, IRunBody p) {
        this.run = r;
        this.parent = p;
        for (CTDrawing ctDrawing : r.getDrawingArray()) {
            for (CTAnchor cTAnchor : ctDrawing.getAnchorArray()) {
                if (cTAnchor.getDocPr() == null) continue;
                this.getDocument().getDrawingIdManager().reserve(cTAnchor.getDocPr().getId());
            }
            for (XmlObject xmlObject : ctDrawing.getInlineArray()) {
                if (xmlObject.getDocPr() == null) continue;
                this.getDocument().getDrawingIdManager().reserve(xmlObject.getDocPr().getId());
            }
        }
        StringBuilder text = new StringBuilder();
        ArrayList<XmlObject> pictTextObjs = new ArrayList<XmlObject>();
        pictTextObjs.addAll(Arrays.asList(r.getPictArray()));
        pictTextObjs.addAll(Arrays.asList(r.getDrawingArray()));
        for (XmlObject o : pictTextObjs) {
            XmlObject[] ts;
            for (XmlObject t : ts = o.selectPath("declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main' .//w:t")) {
                NodeList kids = t.getDomNode().getChildNodes();
                for (int n = 0; n < kids.getLength(); ++n) {
                    if (!(kids.item(n) instanceof Text)) continue;
                    if (text.length() > 0) {
                        text.append("\n");
                    }
                    text.append(kids.item(n).getNodeValue());
                }
            }
        }
        this.pictureText = text.toString();
        this.pictures = new ArrayList<XWPFPicture>();
        for (XmlObject o : pictTextObjs) {
            for (CTPicture pict : this.getCTPictures(o)) {
                XWPFPicture picture = new XWPFPicture(pict, this);
                this.pictures.add(picture);
            }
        }
    }

    @Deprecated
    public XWPFRun(CTR r, XWPFParagraph p) {
        this(r, (IRunBody)p);
    }

    static void preserveSpaces(XmlString xs) {
        String text = xs.getStringValue();
        if (text != null && text.length() >= 1 && (Character.isWhitespace(text.charAt(0)) || Character.isWhitespace(text.charAt(text.length() - 1)))) {
            try (XmlCursor c = xs.newCursor();){
                c.toNextToken();
                c.insertAttributeWithValue(new QName("http://www.w3.org/XML/1998/namespace", "space"), "preserve");
            }
        }
    }

    private List<CTPicture> getCTPictures(XmlObject o) {
        XmlObject[] picts;
        ArrayList<CTPicture> pics = new ArrayList<CTPicture>();
        for (XmlObject pict : picts = o.selectPath("declare namespace pic='" + CTPicture.type.getName().getNamespaceURI() + "' .//pic:pic")) {
            if (pict instanceof XmlAnyTypeImpl) {
                try {
                    pict = (XmlObject)CTPicture.Factory.parse(pict.toString(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
                }
                catch (XmlException e) {
                    throw new POIXMLException(e);
                }
            }
            if (!(pict instanceof CTPicture)) continue;
            pics.add((CTPicture)pict);
        }
        return pics;
    }

    @Internal
    public CTR getCTR() {
        return this.run;
    }

    public IRunBody getParent() {
        return this.parent;
    }

    @Deprecated
    public XWPFParagraph getParagraph() {
        if (this.parent instanceof XWPFParagraph) {
            return (XWPFParagraph)this.parent;
        }
        return null;
    }

    public XWPFDocument getDocument() {
        if (this.parent != null) {
            return this.parent.getDocument();
        }
        return null;
    }

    private static boolean isCTOnOff(CTOnOff onoff) {
        return !onoff.isSetVal() || POIXMLUnits.parseOnOff(onoff);
    }

    public String getLang() {
        CTRPr pr = this.getRunProperties(false);
        return pr == null || pr.sizeOfLangArray() == 0 ? null : pr.getLangArray(0).getVal();
    }

    public void setLang(String lang) {
        CTRPr pr = this.getRunProperties(true);
        CTLanguage ctLang = pr.sizeOfLangArray() > 0 ? pr.getLangArray(0) : pr.addNewLang();
        ctLang.setVal(lang);
    }

    @Override
    public boolean isBold() {
        CTRPr pr = this.getRunProperties(false);
        return pr != null && pr.sizeOfBArray() > 0 && XWPFRun.isCTOnOff(pr.getBArray(0));
    }

    @Override
    public void setBold(boolean value) {
        CTRPr pr = this.getRunProperties(true);
        CTOnOff bold = pr.sizeOfBArray() > 0 ? pr.getBArray(0) : pr.addNewB();
        bold.setVal(value ? STOnOff1.ON : STOnOff1.OFF);
    }

    public String getColor() {
        CTRPr pr;
        String color = null;
        if (this.run.isSetRPr() && (pr = this.getRunProperties(false)) != null && pr.sizeOfColorArray() > 0) {
            CTColor clr = pr.getColorArray(0);
            color = clr.xgetVal().getStringValue();
        }
        return color;
    }

    public void setColor(String rgbStr) {
        CTRPr pr = this.getRunProperties(true);
        CTColor color = pr.sizeOfColorArray() > 0 ? pr.getColorArray(0) : pr.addNewColor();
        color.setVal(rgbStr);
    }

    public String getText(int pos) {
        return this.run.sizeOfTArray() == 0 ? null : this.run.getTArray(pos).getStringValue();
    }

    public String getPictureText() {
        return this.pictureText;
    }

    public void setText(String value) {
        this.setText(value, this.run.sizeOfTArray());
    }

    public void setText(String value, int pos) {
        if (pos > this.run.sizeOfTArray()) {
            throw new ArrayIndexOutOfBoundsException("Value too large for the parameter position in XWPFRun.setText(String value,int pos)");
        }
        CTText t = pos < this.run.sizeOfTArray() && pos >= 0 ? this.run.getTArray(pos) : this.run.addNewT();
        t.setStringValue(value);
        XWPFRun.preserveSpaces(t);
    }

    @Override
    public boolean isItalic() {
        CTRPr pr = this.getRunProperties(false);
        return pr != null && pr.sizeOfIArray() > 0 && XWPFRun.isCTOnOff(pr.getIArray(0));
    }

    @Override
    public void setItalic(boolean value) {
        CTRPr pr = this.getRunProperties(true);
        CTOnOff italic = pr.sizeOfIArray() > 0 ? pr.getIArray(0) : pr.addNewI();
        italic.setVal(value ? STOnOff1.ON : STOnOff1.OFF);
    }

    public UnderlinePatterns getUnderline() {
        STUnderline.Enum baseValue;
        UnderlinePatterns value = UnderlinePatterns.NONE;
        CTUnderline underline = this.getCTUnderline(false);
        if (underline != null && (baseValue = underline.getVal()) != null) {
            value = UnderlinePatterns.valueOf(baseValue.intValue());
        }
        return value;
    }

    public void setUnderline(UnderlinePatterns value) {
        CTUnderline underline = this.getCTUnderline(true);
        assert (underline != null);
        underline.setVal(STUnderline.Enum.forInt(value.getValue()));
    }

    private CTUnderline getCTUnderline(boolean create) {
        CTRPr pr = this.getRunProperties(true);
        return pr.sizeOfUArray() > 0 ? pr.getUArray(0) : (create ? pr.addNewU() : null);
    }

    public void setUnderlineColor(String color) {
        SimpleValue svColor;
        CTUnderline underline = this.getCTUnderline(true);
        assert (underline != null);
        if (color.equals("auto")) {
            STHexColorAuto hexColor = (STHexColorAuto)STHexColorAuto.Factory.newInstance();
            hexColor.setEnumValue(STHexColorAuto.Enum.forString(color));
            svColor = (SimpleValue)((Object)hexColor);
        } else {
            STHexColorRGB rgbColor = (STHexColorRGB)STHexColorRGB.Factory.newInstance();
            rgbColor.setStringValue(color);
            svColor = (SimpleValue)((Object)rgbColor);
        }
        underline.setColor(svColor);
    }

    public void setUnderlineThemeColor(String themeColor) {
        CTUnderline underline = this.getCTUnderline(true);
        assert (underline != null);
        STThemeColor.Enum val = STThemeColor.Enum.forString(themeColor);
        if (val != null) {
            underline.setThemeColor(val);
        }
    }

    public STThemeColor.Enum getUnderlineThemeColor() {
        CTUnderline underline = this.getCTUnderline(false);
        STThemeColor.Enum color = STThemeColor.NONE;
        if (underline != null) {
            color = underline.getThemeColor();
        }
        return color;
    }

    public String getUnderlineColor() {
        CTUnderline underline = this.getCTUnderline(true);
        assert (underline != null);
        String colorName = "auto";
        Object rawValue = underline.getColor();
        if (rawValue != null) {
            if (rawValue instanceof String) {
                colorName = (String)rawValue;
            } else {
                byte[] rgbColor = (byte[])rawValue;
                colorName = HexDump.toHex(rgbColor[0]) + HexDump.toHex(rgbColor[1]) + HexDump.toHex(rgbColor[2]);
            }
        }
        return colorName;
    }

    @Override
    public boolean isStrikeThrough() {
        CTRPr pr = this.getRunProperties(false);
        return pr != null && pr.sizeOfStrikeArray() > 0 && XWPFRun.isCTOnOff(pr.getStrikeArray(0));
    }

    @Override
    public void setStrikeThrough(boolean value) {
        CTRPr pr = this.getRunProperties(true);
        CTOnOff strike = pr.sizeOfStrikeArray() > 0 ? pr.getStrikeArray(0) : pr.addNewStrike();
        strike.setVal(value ? STOnOff1.ON : STOnOff1.OFF);
    }

    @Deprecated
    public boolean isStrike() {
        return this.isStrikeThrough();
    }

    @Deprecated
    public void setStrike(boolean value) {
        this.setStrikeThrough(value);
    }

    @Override
    public boolean isDoubleStrikeThrough() {
        CTRPr pr = this.getRunProperties(false);
        return pr != null && pr.sizeOfDstrikeArray() > 0 && XWPFRun.isCTOnOff(pr.getDstrikeArray(0));
    }

    @Override
    public void setDoubleStrikethrough(boolean value) {
        CTRPr pr = this.getRunProperties(true);
        CTOnOff dstrike = pr.sizeOfDstrikeArray() > 0 ? pr.getDstrikeArray(0) : pr.addNewDstrike();
        dstrike.setVal(value ? STOnOff1.ON : STOnOff1.OFF);
    }

    @Override
    public boolean isSmallCaps() {
        CTRPr pr = this.getRunProperties(false);
        return pr != null && pr.sizeOfSmallCapsArray() > 0 && XWPFRun.isCTOnOff(pr.getSmallCapsArray(0));
    }

    @Override
    public void setSmallCaps(boolean value) {
        CTRPr pr = this.getRunProperties(true);
        CTOnOff caps = pr.sizeOfSmallCapsArray() > 0 ? pr.getSmallCapsArray(0) : pr.addNewSmallCaps();
        caps.setVal(value ? STOnOff1.ON : STOnOff1.OFF);
    }

    @Override
    public boolean isCapitalized() {
        CTRPr pr = this.getRunProperties(false);
        return pr != null && pr.sizeOfCapsArray() > 0 && XWPFRun.isCTOnOff(pr.getCapsArray(0));
    }

    @Override
    public void setCapitalized(boolean value) {
        CTRPr pr = this.getRunProperties(true);
        CTOnOff caps = pr.sizeOfCapsArray() > 0 ? pr.getCapsArray(0) : pr.addNewCaps();
        caps.setVal(value ? STOnOff1.ON : STOnOff1.OFF);
    }

    @Override
    public boolean isShadowed() {
        CTRPr pr = this.getRunProperties(false);
        return pr != null && pr.sizeOfShadowArray() > 0 && XWPFRun.isCTOnOff(pr.getShadowArray(0));
    }

    @Override
    public void setShadow(boolean value) {
        CTRPr pr = this.getRunProperties(true);
        CTOnOff shadow = pr.sizeOfShadowArray() > 0 ? pr.getShadowArray(0) : pr.addNewShadow();
        shadow.setVal(value ? STOnOff1.ON : STOnOff1.OFF);
    }

    @Override
    public boolean isImprinted() {
        CTRPr pr = this.getRunProperties(false);
        return pr != null && pr.sizeOfImprintArray() > 0 && XWPFRun.isCTOnOff(pr.getImprintArray(0));
    }

    @Override
    public void setImprinted(boolean value) {
        CTRPr pr = this.getRunProperties(true);
        CTOnOff imprinted = pr.sizeOfImprintArray() > 0 ? pr.getImprintArray(0) : pr.addNewImprint();
        imprinted.setVal(value ? STOnOff1.ON : STOnOff1.OFF);
    }

    @Override
    public boolean isEmbossed() {
        CTRPr pr = this.getRunProperties(false);
        return pr != null && pr.sizeOfEmbossArray() > 0 && XWPFRun.isCTOnOff(pr.getEmbossArray(0));
    }

    @Override
    public void setEmbossed(boolean value) {
        CTRPr pr = this.getRunProperties(true);
        CTOnOff emboss = pr.sizeOfEmbossArray() > 0 ? pr.getEmbossArray(0) : pr.addNewEmboss();
        emboss.setVal(value ? STOnOff1.ON : STOnOff1.OFF);
    }

    public void setSubscript(VerticalAlign valign) {
        CTRPr pr = this.getRunProperties(true);
        CTVerticalAlignRun ctValign = pr.sizeOfVertAlignArray() > 0 ? pr.getVertAlignArray(0) : pr.addNewVertAlign();
        ctValign.setVal(STVerticalAlignRun.Enum.forInt(valign.getValue()));
    }

    @Override
    public int getKerning() {
        CTRPr pr = this.getRunProperties(false);
        if (pr == null || pr.sizeOfKernArray() == 0) {
            return 0;
        }
        return (int)POIXMLUnits.parseLength(pr.getKernArray(0).xgetVal());
    }

    @Override
    public void setKerning(int kern) {
        CTRPr pr = this.getRunProperties(true);
        CTHpsMeasure kernmes = pr.sizeOfKernArray() > 0 ? pr.getKernArray(0) : pr.addNewKern();
        kernmes.setVal(BigInteger.valueOf(kern));
    }

    @Override
    public boolean isHighlighted() {
        CTRPr pr = this.getRunProperties(false);
        if (pr == null || pr.sizeOfHighlightArray() == 0) {
            return false;
        }
        STHighlightColor.Enum val = pr.getHighlightArray(0).getVal();
        return val != null && val != STHighlightColor.NONE;
    }

    @Override
    public int getCharacterSpacing() {
        CTRPr pr = this.getRunProperties(false);
        if (pr == null || pr.sizeOfSpacingArray() == 0) {
            return 0;
        }
        return (int)Units.toDXA(POIXMLUnits.parseLength(pr.getSpacingArray(0).xgetVal()));
    }

    @Override
    public void setCharacterSpacing(int twips) {
        CTRPr pr = this.getRunProperties(true);
        CTSignedTwipsMeasure spc = pr.sizeOfSpacingArray() > 0 ? pr.getSpacingArray(0) : pr.addNewSpacing();
        spc.setVal(BigInteger.valueOf(twips));
    }

    public String getFontFamily() {
        return this.getFontFamily(null);
    }

    public void setFontFamily(String fontFamily) {
        this.setFontFamily(fontFamily, null);
    }

    @Override
    public String getFontName() {
        return this.getFontFamily();
    }

    public String getFontFamily(FontCharRange fcr) {
        CTRPr pr = this.getRunProperties(false);
        if (pr == null || pr.sizeOfRFontsArray() == 0) {
            return null;
        }
        CTFonts fonts = pr.getRFontsArray(0);
        switch (fcr == null ? FontCharRange.ascii : fcr) {
            default: {
                return fonts.getAscii();
            }
            case cs: {
                return fonts.getCs();
            }
            case eastAsia: {
                return fonts.getEastAsia();
            }
            case hAnsi: 
        }
        return fonts.getHAnsi();
    }

    public void setFontFamily(String fontFamily, FontCharRange fcr) {
        CTFonts fonts;
        CTRPr pr = this.getRunProperties(true);
        CTFonts cTFonts = fonts = pr.sizeOfRFontsArray() > 0 ? pr.getRFontsArray(0) : pr.addNewRFonts();
        if (fcr == null) {
            fonts.setAscii(fontFamily);
            if (!fonts.isSetHAnsi()) {
                fonts.setHAnsi(fontFamily);
            }
            if (!fonts.isSetCs()) {
                fonts.setCs(fontFamily);
            }
            if (!fonts.isSetEastAsia()) {
                fonts.setEastAsia(fontFamily);
            }
        } else {
            switch (fcr) {
                case ascii: {
                    fonts.setAscii(fontFamily);
                    break;
                }
                case cs: {
                    fonts.setCs(fontFamily);
                    break;
                }
                case eastAsia: {
                    fonts.setEastAsia(fontFamily);
                    break;
                }
                case hAnsi: {
                    fonts.setHAnsi(fontFamily);
                }
            }
        }
    }

    @Override
    @Deprecated
    @Removal(version="6.0.0")
    public int getFontSize() {
        BigDecimal bd = this.getFontSizeAsBigDecimal(0);
        return bd == null ? -1 : bd.intValue();
    }

    @Override
    public Double getFontSizeAsDouble() {
        BigDecimal bd = this.getFontSizeAsBigDecimal(1);
        return bd == null ? null : Double.valueOf(bd.doubleValue());
    }

    private BigDecimal getFontSizeAsBigDecimal(int scale) {
        CTRPr pr = this.getRunProperties(false);
        return pr != null && pr.sizeOfSzArray() > 0 ? BigDecimal.valueOf(Units.toPoints(POIXMLUnits.parseLength(pr.getSzArray(0).xgetVal()))).divide(BigDecimal.valueOf(4L), scale, RoundingMode.HALF_UP) : null;
    }

    @Override
    public void setFontSize(int size) {
        BigInteger bint = BigInteger.valueOf(size);
        CTRPr pr = this.getRunProperties(true);
        CTHpsMeasure ctSize = pr.sizeOfSzArray() > 0 ? pr.getSzArray(0) : pr.addNewSz();
        ctSize.setVal(bint.multiply(BigInteger.valueOf(2L)));
    }

    @Override
    public void setFontSize(double size) {
        BigDecimal bd = BigDecimal.valueOf(size);
        CTRPr pr = this.getRunProperties(true);
        CTHpsMeasure ctSize = pr.sizeOfSzArray() > 0 ? pr.getSzArray(0) : pr.addNewSz();
        ctSize.setVal(bd.multiply(BigDecimal.valueOf(2L)).setScale(0, RoundingMode.HALF_UP).toBigInteger());
    }

    public int getTextPosition() {
        CTRPr pr = this.getRunProperties(false);
        return pr != null && pr.sizeOfPositionArray() > 0 ? (int)(Units.toPoints(POIXMLUnits.parseLength(pr.getPositionArray(0).xgetVal())) / 2.0) : -1;
    }

    public void setTextPosition(int val) {
        BigInteger bint = new BigInteger(Integer.toString(val));
        CTRPr pr = this.getRunProperties(true);
        CTSignedHpsMeasure position = pr.sizeOfPositionArray() > 0 ? pr.getPositionArray(0) : pr.addNewPosition();
        position.setVal(bint);
    }

    public void removeBreak() {
    }

    public void addBreak() {
        this.run.addNewBr();
    }

    public void addBreak(BreakType type) {
        CTBr br = this.run.addNewBr();
        br.setType(STBrType.Enum.forInt(type.getValue()));
    }

    public void addBreak(BreakClear clear) {
        CTBr br = this.run.addNewBr();
        br.setType(STBrType.Enum.forInt(BreakType.TEXT_WRAPPING.getValue()));
        br.setClear(STBrClear.Enum.forInt(clear.getValue()));
    }

    public void addTab() {
        this.run.addNewTab();
    }

    public void removeTab() {
    }

    public void addCarriageReturn() {
        this.run.addNewCr();
    }

    public void removeCarriageReturn() {
    }

    public XWPFPicture addPicture(InputStream pictureData, int pictureType, String filename, int width, int height) throws InvalidFormatException, IOException {
        return this.addPicture(pictureData, PictureType.findByOoxmlId(pictureType), filename, width, height);
    }

    public XWPFPicture addPicture(InputStream pictureData, PictureType pictureType, String filename, int width, int height) throws InvalidFormatException, IOException {
        XWPFPictureData picData;
        if (pictureType == null) {
            throw new InvalidFormatException("pictureType is not supported");
        }
        if (this.parent.getPart() instanceof XWPFHeaderFooter) {
            XWPFHeaderFooter headerFooter = (XWPFHeaderFooter)this.parent.getPart();
            String relationId = headerFooter.addPictureData(pictureData, pictureType);
            picData = (XWPFPictureData)headerFooter.getRelationById(relationId);
        } else if (this.parent.getPart() instanceof XWPFComments) {
            XWPFComments comments = (XWPFComments)this.parent.getPart();
            String relationId = comments.addPictureData(pictureData, pictureType);
            picData = (XWPFPictureData)comments.getRelationById(relationId);
        } else {
            XWPFDocument doc = this.parent.getDocument();
            String relationId = doc.addPictureData(pictureData, pictureType);
            picData = (XWPFPictureData)doc.getRelationById(relationId);
        }
        try {
            CTDrawing drawing = this.run.addNewDrawing();
            CTInline inline = drawing.addNewInline();
            String xml = "<a:graphic xmlns:a=\"" + CTGraphicalObject.type.getName().getNamespaceURI() + "\"><a:graphicData uri=\"" + CTPicture.type.getName().getNamespaceURI() + "\"><pic:pic xmlns:pic=\"" + CTPicture.type.getName().getNamespaceURI() + "\" /></a:graphicData></a:graphic>";
            InputSource is = new InputSource(new StringReader(xml));
            Document doc = DocumentHelper.readDocument(is);
            inline.set(XmlToken.Factory.parse(doc.getDocumentElement(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS));
            inline.setDistT(0L);
            inline.setDistR(0L);
            inline.setDistB(0L);
            inline.setDistL(0L);
            CTNonVisualDrawingProps docPr = inline.addNewDocPr();
            long id = this.getParent().getDocument().getDrawingIdManager().reserveNew();
            docPr.setId(id);
            docPr.setName("Drawing " + id);
            docPr.setDescr(filename);
            CTPositiveSize2D extent = inline.addNewExtent();
            extent.setCx(width);
            extent.setCy(height);
            CTGraphicalObject graphic = inline.getGraphic();
            CTGraphicalObjectData graphicData = graphic.getGraphicData();
            CTPicture pic = this.getCTPictures(graphicData).get(0);
            CTPictureNonVisual nvPicPr = pic.addNewNvPicPr();
            CTNonVisualDrawingProps cNvPr = nvPicPr.addNewCNvPr();
            cNvPr.setId(0L);
            cNvPr.setName("Picture " + id);
            cNvPr.setDescr(filename);
            CTNonVisualPictureProperties cNvPicPr = nvPicPr.addNewCNvPicPr();
            cNvPicPr.addNewPicLocks().setNoChangeAspect(true);
            CTBlipFillProperties blipFill = pic.addNewBlipFill();
            CTBlip blip = blipFill.addNewBlip();
            blip.setEmbed(this.parent.getPart().getRelationId(picData));
            blipFill.addNewStretch().addNewFillRect();
            CTShapeProperties spPr = pic.addNewSpPr();
            CTTransform2D xfrm = spPr.addNewXfrm();
            CTPoint2D off = xfrm.addNewOff();
            off.setX(0);
            off.setY(0);
            CTPositiveSize2D ext = xfrm.addNewExt();
            ext.setCx(width);
            ext.setCy(height);
            CTPresetGeometry2D prstGeom = spPr.addNewPrstGeom();
            prstGeom.setPrst(STShapeType.RECT);
            prstGeom.addNewAvLst();
            XWPFPicture xwpfPicture = new XWPFPicture(pic, this);
            this.pictures.add(xwpfPicture);
            return xwpfPicture;
        }
        catch (XmlException | SAXException e) {
            throw new IllegalStateException(e);
        }
    }

    @Internal
    public CTInline addChart(String chartRelId) throws InvalidFormatException, IOException {
        try {
            CTInline inline = this.run.addNewDrawing().addNewInline();
            String xml = "<a:graphic xmlns:a=\"" + CTGraphicalObject.type.getName().getNamespaceURI() + "\"><a:graphicData uri=\"" + CTChart.type.getName().getNamespaceURI() + "\"><c:chart xmlns:c=\"" + CTChart.type.getName().getNamespaceURI() + "\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\" r:id=\"" + chartRelId + "\" /></a:graphicData></a:graphic>";
            InputSource is = new InputSource(new StringReader(xml));
            Document doc = DocumentHelper.readDocument(is);
            inline.set(XmlToken.Factory.parse(doc.getDocumentElement(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS));
            inline.setDistT(0L);
            inline.setDistR(0L);
            inline.setDistB(0L);
            inline.setDistL(0L);
            CTNonVisualDrawingProps docPr = inline.addNewDocPr();
            long id = this.getParent().getDocument().getDrawingIdManager().reserveNew();
            docPr.setId(id);
            docPr.setName("chart " + id);
            return inline;
        }
        catch (XmlException | SAXException e) {
            throw new IllegalStateException(e);
        }
    }

    public List<XWPFPicture> getEmbeddedPictures() {
        return this.pictures;
    }

    public void setStyle(String styleId) {
        CTRPr pr = this.getCTR().getRPr();
        if (null == pr) {
            pr = this.getCTR().addNewRPr();
        }
        CTString style = pr.sizeOfRStyleArray() > 0 ? pr.getRStyleArray(0) : pr.addNewRStyle();
        style.setVal(styleId);
    }

    public String getStyle() {
        CTRPr pr = this.getCTR().getRPr();
        if (pr == null || pr.sizeOfRStyleArray() <= 0) {
            return "";
        }
        CTString style = pr.getRStyleArray(0);
        return null == style ? "" : style.getVal();
    }

    public String toString() {
        String phonetic = this.getPhonetic();
        if (phonetic.length() > 0) {
            return this.text() + " (" + phonetic + ")";
        }
        return this.text();
    }

    @Override
    public String text() {
        StringBuilder text = new StringBuilder(64);
        try (XmlCursor c = this.run.newCursor();){
            c.selectPath("./*");
            while (c.toNextSelection()) {
                XmlObject o = c.getObject();
                if (o instanceof CTRuby) {
                    this.handleRuby(o, text, false);
                    continue;
                }
                this._getText(o, text);
            }
        }
        return text.toString();
    }

    public String getPhonetic() {
        StringBuilder text = new StringBuilder(64);
        try (XmlCursor c = this.run.newCursor();){
            c.selectPath("./*");
            while (c.toNextSelection()) {
                XmlObject o = c.getObject();
                if (!(o instanceof CTRuby)) continue;
                this.handleRuby(o, text, true);
            }
            if (this.pictureText != null && this.pictureText.length() > 0) {
                text.append("\n").append(this.pictureText).append("\n");
            }
        }
        return text.toString();
    }

    private void handleRuby(XmlObject rubyObj, StringBuilder text, boolean extractPhonetic) {
        try (XmlCursor c = rubyObj.newCursor();){
            c.selectPath(".//*");
            boolean inRT = false;
            boolean inBase = false;
            while (c.toNextSelection()) {
                XmlObject o = c.getObject();
                if (o instanceof CTRubyContent) {
                    Node node = o.getDomNode();
                    if (!"http://schemas.openxmlformats.org/wordprocessingml/2006/main".equals(node.getNamespaceURI())) continue;
                    String tagName = node.getLocalName();
                    if ("rt".equals(tagName)) {
                        inRT = true;
                        continue;
                    }
                    if (!"rubyBase".equals(tagName)) continue;
                    inRT = false;
                    inBase = true;
                    continue;
                }
                if (extractPhonetic && inRT) {
                    this._getText(o, text);
                    continue;
                }
                if (extractPhonetic || !inBase) continue;
                this._getText(o, text);
            }
        }
    }

    private void _getText(XmlObject o, StringBuilder text) {
        CTFldChar ctfldChar;
        Object textValue;
        Node node;
        if (!(!(o instanceof CTText) || "instrText".equals((node = o.getDomNode()).getLocalName()) && "http://schemas.openxmlformats.org/wordprocessingml/2006/main".equals(node.getNamespaceURI()) || (textValue = ((CTText)o).getStringValue()) == null)) {
            if (this.isCapitalized() || this.isSmallCaps()) {
                textValue = ((String)textValue).toUpperCase(LocaleUtil.getUserLocale());
            }
            text.append((String)textValue);
        }
        if (o instanceof CTFldChar && (ctfldChar = (CTFldChar)o).getFldCharType() == STFldCharType.BEGIN && ctfldChar.getFfData() != null) {
            for (CTFFCheckBox checkBox : ctfldChar.getFfData().getCheckBoxList()) {
                String textValue2 = checkBox.getDefault() != null && POIXMLUnits.parseOnOff(checkBox.getDefault().xgetVal()) ? "|X|" : "|_|";
                text.append(textValue2);
            }
        }
        if (o instanceof CTPTab) {
            text.append('\t');
        }
        if (o instanceof CTBr) {
            text.append('\n');
        }
        if (o instanceof CTEmpty && "http://schemas.openxmlformats.org/wordprocessingml/2006/main".equals((node = o.getDomNode()).getNamespaceURI())) {
            switch (node.getLocalName()) {
                case "tab": {
                    text.append('\t');
                    break;
                }
                case "br": 
                case "cr": {
                    text.append('\n');
                }
            }
        }
        if (o instanceof CTFtnEdnRef) {
            CTFtnEdnRef ftn = (CTFtnEdnRef)o;
            String footnoteRef = ftn.getDomNode().getLocalName().equals("footnoteReference") ? "[footnoteRef:" + ftn.getId().intValue() + "]" : "[endnoteRef:" + ftn.getId().intValue() + "]";
            text.append(footnoteRef);
        }
    }

    public void setTextScale(int percentage) {
        CTRPr pr = this.getRunProperties(true);
        CTTextScale scale = pr.sizeOfWArray() > 0 ? pr.getWArray(0) : pr.addNewW();
        scale.setVal(percentage);
    }

    public int getTextScale() {
        CTRPr pr = this.getRunProperties(false);
        if (pr == null || pr.sizeOfWArray() == 0) {
            return 100;
        }
        int value = POIXMLUnits.parsePercent(pr.getWArray(0).xgetVal());
        return value == 0 ? 100 : value / 1000;
    }

    public void setTextHighlightColor(String colorName) {
        STHighlightColor.Enum val;
        CTRPr pr = this.getRunProperties(true);
        CTHighlight highlight = pr.sizeOfHighlightArray() > 0 ? pr.getHighlightArray(0) : pr.addNewHighlight();
        STHighlightColor color = highlight.xgetVal();
        if (color == null) {
            color = (STHighlightColor)STHighlightColor.Factory.newInstance();
        }
        if ((val = STHighlightColor.Enum.forString(colorName)) != null) {
            color.setStringValue(val.toString());
            highlight.xsetVal(color);
        }
    }

    @Deprecated
    @Removal(version="7.0.0")
    public STHighlightColor.Enum getTextHightlightColor() {
        return this.getTextHighlightColor();
    }

    public STHighlightColor.Enum getTextHighlightColor() {
        CTRPr pr = this.getRunProperties(false);
        if (pr == null) {
            return STHighlightColor.NONE;
        }
        CTHighlight highlight = pr.sizeOfHighlightArray() > 0 ? pr.getHighlightArray(0) : pr.addNewHighlight();
        STHighlightColor color = highlight.xgetVal();
        if (color == null) {
            color = (STHighlightColor)STHighlightColor.Factory.newInstance();
            color.setEnumValue(STHighlightColor.NONE);
        }
        return (STHighlightColor.Enum)color.getEnumValue();
    }

    public boolean isVanish() {
        CTRPr pr = this.getRunProperties(false);
        return pr != null && pr.sizeOfVanishArray() > 0 && XWPFRun.isCTOnOff(pr.getVanishArray(0));
    }

    public void setVanish(boolean value) {
        CTRPr pr = this.getRunProperties(true);
        CTOnOff vanish = pr.sizeOfVanishArray() > 0 ? pr.getVanishArray(0) : pr.addNewVanish();
        vanish.setVal(value ? STOnOff1.ON : STOnOff1.OFF);
    }

    public STVerticalAlignRun.Enum getVerticalAlignment() {
        CTRPr pr = this.getRunProperties(false);
        if (pr == null) {
            return STVerticalAlignRun.BASELINE;
        }
        CTVerticalAlignRun vertAlign = pr.sizeOfVertAlignArray() > 0 ? pr.getVertAlignArray(0) : pr.addNewVertAlign();
        STVerticalAlignRun.Enum val = vertAlign.getVal();
        if (val == null) {
            val = STVerticalAlignRun.BASELINE;
        }
        return val;
    }

    public void setVerticalAlignment(String verticalAlignment) {
        STVerticalAlignRun.Enum val;
        CTRPr pr = this.getRunProperties(true);
        CTVerticalAlignRun vertAlign = pr.sizeOfVertAlignArray() > 0 ? pr.getVertAlignArray(0) : pr.addNewVertAlign();
        STVerticalAlignRun align = vertAlign.xgetVal();
        if (align == null) {
            align = (STVerticalAlignRun)STVerticalAlignRun.Factory.newInstance();
        }
        if ((val = STVerticalAlignRun.Enum.forString(verticalAlignment)) != null) {
            align.setStringValue(val.toString());
            vertAlign.xsetVal(align);
        }
    }

    public STEm.Enum getEmphasisMark() {
        CTRPr pr = this.getRunProperties(false);
        if (pr == null) {
            return STEm.NONE;
        }
        CTEm emphasis = pr.sizeOfEmArray() > 0 ? pr.getEmArray(0) : pr.addNewEm();
        STEm.Enum val = emphasis.getVal();
        if (val == null) {
            val = STEm.NONE;
        }
        return val;
    }

    public void setEmphasisMark(String markType) {
        STEm.Enum val;
        CTRPr pr = this.getRunProperties(true);
        CTEm emphasisMark = pr.sizeOfEmArray() > 0 ? pr.getEmArray(0) : pr.addNewEm();
        STEm mark = emphasisMark.xgetVal();
        if (mark == null) {
            mark = (STEm)STEm.Factory.newInstance();
        }
        if ((val = STEm.Enum.forString(markType)) != null) {
            mark.setStringValue(val.toString());
            emphasisMark.xsetVal(mark);
        }
    }

    protected CTRPr getRunProperties(boolean create) {
        CTRPr pr;
        CTRPr cTRPr = pr = this.run.isSetRPr() ? this.run.getRPr() : null;
        if (create && pr == null) {
            pr = this.run.addNewRPr();
        }
        return pr;
    }

    public static enum FontCharRange {
        ascii,
        cs,
        eastAsia,
        hAnsi;

    }
}

