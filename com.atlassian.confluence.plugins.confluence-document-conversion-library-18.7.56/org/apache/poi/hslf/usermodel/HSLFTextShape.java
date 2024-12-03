/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.usermodel;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ddf.AbstractEscherOptRecord;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherPropertyTypes;
import org.apache.poi.ddf.EscherSimpleProperty;
import org.apache.poi.ddf.EscherTextboxRecord;
import org.apache.poi.hslf.exceptions.HSLFException;
import org.apache.poi.hslf.model.HSLFMetroShape;
import org.apache.poi.hslf.model.textproperties.TextPropCollection;
import org.apache.poi.hslf.record.EscherTextboxWrapper;
import org.apache.poi.hslf.record.OEPlaceholderAtom;
import org.apache.poi.hslf.record.PPDrawing;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.hslf.record.RoundTripHFPlaceholder12;
import org.apache.poi.hslf.record.StyleTextPropAtom;
import org.apache.poi.hslf.record.TextBytesAtom;
import org.apache.poi.hslf.record.TextCharsAtom;
import org.apache.poi.hslf.record.TextHeaderAtom;
import org.apache.poi.hslf.usermodel.HSLFGroupShape;
import org.apache.poi.hslf.usermodel.HSLFHyperlink;
import org.apache.poi.hslf.usermodel.HSLFMasterSheet;
import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFSheet;
import org.apache.poi.hslf.usermodel.HSLFSimpleShape;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.hslf.usermodel.HSLFTextRun;
import org.apache.poi.sl.draw.DrawFactory;
import org.apache.poi.sl.draw.DrawTextShape;
import org.apache.poi.sl.usermodel.Insets2D;
import org.apache.poi.sl.usermodel.Placeholder;
import org.apache.poi.sl.usermodel.Shape;
import org.apache.poi.sl.usermodel.ShapeContainer;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.sl.usermodel.TextRun;
import org.apache.poi.sl.usermodel.TextShape;
import org.apache.poi.sl.usermodel.VerticalAlignment;
import org.apache.poi.util.Units;

public abstract class HSLFTextShape
extends HSLFSimpleShape
implements TextShape<HSLFShape, HSLFTextParagraph> {
    private static final Logger LOG = LogManager.getLogger(HSLFTextShape.class);
    public static final int WrapSquare = 0;
    public static final int WrapByPoints = 1;
    public static final int WrapNone = 2;
    public static final int WrapTopBottom = 3;
    public static final int WrapThrough = 4;
    private List<HSLFTextParagraph> _paragraphs = new ArrayList<HSLFTextParagraph>();
    private EscherTextboxWrapper _txtbox;

    protected HSLFTextShape(EscherContainerRecord escherRecord, ShapeContainer<HSLFShape, HSLFTextParagraph> parent) {
        super(escherRecord, parent);
    }

    public HSLFTextShape(ShapeContainer<HSLFShape, HSLFTextParagraph> parent) {
        super(null, parent);
        this.createSpContainer(parent instanceof HSLFGroupShape);
    }

    public HSLFTextShape() {
        this(null);
    }

    protected void setDefaultTextProperties(HSLFTextParagraph _txtrun) {
    }

    @Override
    protected void afterInsert(HSLFSheet sh) {
        super.afterInsert(sh);
        this.storeText();
        EscherTextboxWrapper thisTxtbox = this.getEscherTextboxWrapper();
        if (thisTxtbox != null) {
            boolean isFilledTxt;
            this.getSpContainer().addChildRecord(thisTxtbox.getEscherRecord());
            PPDrawing ppdrawing = sh.getPPDrawing();
            ppdrawing.addTextboxWrapper(thisTxtbox);
            try {
                thisTxtbox.writeOut(null);
            }
            catch (IOException e) {
                throw new HSLFException(e);
            }
            boolean isInitialAnchor = this.getAnchor().equals(new Rectangle2D.Double());
            boolean bl = isFilledTxt = !"".equals(this.getText());
            if (isInitialAnchor && isFilledTxt) {
                this.resizeToFitText();
            }
        }
        for (HSLFTextParagraph htp : this._paragraphs) {
            htp.setShapeId(this.getShapeId());
        }
        sh.onAddTextShape(this);
    }

    protected EscherTextboxWrapper getEscherTextboxWrapper() {
        EscherTextboxWrapper[] wrappers;
        PPDrawing drawing;
        if (this._txtbox != null) {
            return this._txtbox;
        }
        EscherTextboxRecord textRecord = (EscherTextboxRecord)this.getEscherChild(EscherTextboxRecord.RECORD_ID);
        if (textRecord == null) {
            return null;
        }
        HSLFSheet sheet = this.getSheet();
        if (sheet != null && (drawing = sheet.getPPDrawing()) != null && (wrappers = drawing.getTextboxWrappers()) != null) {
            for (EscherTextboxWrapper w : wrappers) {
                if (textRecord != w.getEscherRecord()) continue;
                this._txtbox = w;
                return this._txtbox;
            }
        }
        this._txtbox = new EscherTextboxWrapper(textRecord);
        return this._txtbox;
    }

    private void createEmptyParagraph() {
        TextHeaderAtom tha = (TextHeaderAtom)this._txtbox.findFirstOfType(TextHeaderAtom._type);
        if (tha == null) {
            tha = new TextHeaderAtom();
            tha.setParentRecord(this._txtbox);
            this._txtbox.appendChildRecord(tha);
        }
        TextBytesAtom tba = (TextBytesAtom)this._txtbox.findFirstOfType(TextBytesAtom._type);
        TextCharsAtom tca = (TextCharsAtom)this._txtbox.findFirstOfType(TextCharsAtom._type);
        if (tba == null && tca == null) {
            tba = new TextBytesAtom();
            tba.setText(new byte[0]);
            this._txtbox.appendChildRecord(tba);
        }
        String text = tba != null ? tba.getText() : tca.getText();
        StyleTextPropAtom sta = (StyleTextPropAtom)this._txtbox.findFirstOfType(StyleTextPropAtom._type);
        TextPropCollection paraStyle = null;
        TextPropCollection charStyle = null;
        if (sta == null) {
            int parSiz = text.length();
            sta = new StyleTextPropAtom(parSiz + 1);
            if (this._paragraphs.isEmpty()) {
                paraStyle = sta.addParagraphTextPropCollection(parSiz + 1);
                charStyle = sta.addCharacterTextPropCollection(parSiz + 1);
            } else {
                for (HSLFTextParagraph htp : this._paragraphs) {
                    int runsLen = 0;
                    for (HSLFTextRun htr : htp.getTextRuns()) {
                        runsLen += htr.getLength();
                        charStyle = sta.addCharacterTextPropCollection(htr.getLength());
                        htr.setCharacterStyle(charStyle);
                    }
                    paraStyle = sta.addParagraphTextPropCollection(runsLen);
                    htp.setParagraphStyle(paraStyle);
                }
                assert (paraStyle != null && charStyle != null);
            }
            this._txtbox.appendChildRecord(sta);
        } else {
            paraStyle = sta.getParagraphStyles().get(0);
            charStyle = sta.getCharacterStyles().get(0);
        }
        if (this._paragraphs.isEmpty()) {
            HSLFTextParagraph htp = new HSLFTextParagraph(tha, tba, tca, this._paragraphs);
            htp.setParagraphStyle(paraStyle);
            htp.setParentShape(this);
            this._paragraphs.add(htp);
            HSLFTextRun htr = new HSLFTextRun(htp);
            htr.setCharacterStyle(charStyle);
            htr.setText(text);
            htp.addTextRun(htr);
        }
    }

    @Override
    public Rectangle2D resizeToFitText() {
        return this.resizeToFitText(null);
    }

    @Override
    public Rectangle2D resizeToFitText(Graphics2D graphics) {
        Rectangle2D anchor = this.getAnchor();
        if (anchor.getWidth() == 0.0) {
            LOG.atWarn().log("Width of shape wasn't set. Defaulting to 200px");
            anchor.setRect(anchor.getX(), anchor.getY(), 200.0, anchor.getHeight());
            this.setAnchor(anchor);
        }
        double height = this.getTextHeight(graphics);
        Insets2D insets = this.getInsets();
        anchor.setRect(anchor.getX(), anchor.getY(), anchor.getWidth(), (height += 1.0) + insets.top + insets.bottom);
        this.setAnchor(anchor);
        return anchor;
    }

    public int getRunType() {
        this.getEscherTextboxWrapper();
        if (this._txtbox == null) {
            return -1;
        }
        List<HSLFTextParagraph> paras = HSLFTextParagraph.findTextParagraphs(this._txtbox, this.getSheet());
        return paras.isEmpty() || paras.get(0).getIndex() == -1 ? -1 : paras.get(0).getRunType();
    }

    public void setRunType(int type) {
        this.getEscherTextboxWrapper();
        if (this._txtbox == null) {
            return;
        }
        List<HSLFTextParagraph> paras = HSLFTextParagraph.findTextParagraphs(this._txtbox, this.getSheet());
        if (!paras.isEmpty()) {
            paras.get(0).setRunType(type);
        }
    }

    HSLFTextAnchor getAlignment() {
        HSLFTextAnchor align;
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        EscherSimpleProperty prop = (EscherSimpleProperty)HSLFTextShape.getEscherProperty(opt, EscherPropertyTypes.TEXT__ANCHORTEXT);
        if (prop == null) {
            HSLFTextShape masterShape;
            int type = this.getRunType();
            HSLFSheet sh = this.getSheet();
            HSLFMasterSheet master = sh != null ? sh.getMasterSheet() : null;
            HSLFTextShape hSLFTextShape = masterShape = master != null ? master.getPlaceholderByTextType(type) : null;
            align = masterShape != null && type != TextShape.TextPlaceholder.OTHER.nativeId ? masterShape.getAlignment() : (TextShape.TextPlaceholder.isTitle(type) ? HSLFTextAnchor.MIDDLE : HSLFTextAnchor.TOP);
        } else {
            align = HSLFTextAnchor.fromNativeId(prop.getPropertyValue());
        }
        return align == null ? HSLFTextAnchor.TOP : align;
    }

    void setAlignment(Boolean isCentered, VerticalAlignment vAlign, boolean baseline) {
        for (HSLFTextAnchor hta : HSLFTextAnchor.values()) {
            if (hta.centered != (isCentered != null && isCentered != false) || hta.vAlign != vAlign || hta.baseline != null && hta.baseline != baseline) continue;
            this.setEscherProperty(EscherPropertyTypes.TEXT__ANCHORTEXT, hta.nativeId);
            break;
        }
    }

    public boolean isAlignToBaseline() {
        return this.getAlignment().baseline;
    }

    public void setAlignToBaseline(boolean alignToBaseline) {
        this.setAlignment(this.isHorizontalCentered(), this.getVerticalAlignment(), alignToBaseline);
    }

    @Override
    public boolean isHorizontalCentered() {
        return this.getAlignment().centered;
    }

    @Override
    public void setHorizontalCentered(Boolean isCentered) {
        this.setAlignment(isCentered, this.getVerticalAlignment(), this.getAlignment().baseline);
    }

    @Override
    public VerticalAlignment getVerticalAlignment() {
        return this.getAlignment().vAlign;
    }

    @Override
    public void setVerticalAlignment(VerticalAlignment vAlign) {
        this.setAlignment(this.isHorizontalCentered(), vAlign, this.getAlignment().baseline);
    }

    public double getBottomInset() {
        return this.getInset(EscherPropertyTypes.TEXT__TEXTBOTTOM, 0.05);
    }

    public void setBottomInset(double margin) {
        this.setInset(EscherPropertyTypes.TEXT__TEXTBOTTOM, margin);
    }

    public double getLeftInset() {
        return this.getInset(EscherPropertyTypes.TEXT__TEXTLEFT, 0.1);
    }

    public void setLeftInset(double margin) {
        this.setInset(EscherPropertyTypes.TEXT__TEXTLEFT, margin);
    }

    public double getRightInset() {
        return this.getInset(EscherPropertyTypes.TEXT__TEXTRIGHT, 0.1);
    }

    public void setRightInset(double margin) {
        this.setInset(EscherPropertyTypes.TEXT__TEXTRIGHT, margin);
    }

    public double getTopInset() {
        return this.getInset(EscherPropertyTypes.TEXT__TEXTTOP, 0.05);
    }

    public void setTopInset(double margin) {
        this.setInset(EscherPropertyTypes.TEXT__TEXTTOP, margin);
    }

    private double getInset(EscherPropertyTypes type, double defaultInch) {
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        EscherSimpleProperty prop = (EscherSimpleProperty)HSLFTextShape.getEscherProperty(opt, type);
        int val = prop == null ? (int)((double)Units.toEMU(72.0) * defaultInch) : prop.getPropertyValue();
        return Units.toPoints(val);
    }

    private void setInset(EscherPropertyTypes type, double margin) {
        this.setEscherProperty(type, Units.toEMU(margin));
    }

    public int getWordWrapEx() {
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        EscherSimpleProperty prop = (EscherSimpleProperty)HSLFTextShape.getEscherProperty(opt, EscherPropertyTypes.TEXT__WRAPTEXT);
        return prop == null ? 0 : prop.getPropertyValue();
    }

    public void setWordWrapEx(int wrap) {
        this.setEscherProperty(EscherPropertyTypes.TEXT__WRAPTEXT, wrap);
    }

    @Override
    public boolean getWordWrap() {
        int ww = this.getWordWrapEx();
        return ww != 2;
    }

    @Override
    public void setWordWrap(boolean wrap) {
        this.setWordWrapEx(wrap ? 0 : 2);
    }

    public int getTextId() {
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        EscherSimpleProperty prop = (EscherSimpleProperty)HSLFTextShape.getEscherProperty(opt, EscherPropertyTypes.TEXT__TEXTID);
        return prop == null ? 0 : prop.getPropertyValue();
    }

    public void setTextId(int id) {
        this.setEscherProperty(EscherPropertyTypes.TEXT__TEXTID, id);
    }

    @Override
    public List<HSLFTextParagraph> getTextParagraphs() {
        if (!this._paragraphs.isEmpty()) {
            return this._paragraphs;
        }
        this._txtbox = this.getEscherTextboxWrapper();
        if (this._txtbox == null) {
            this._txtbox = new EscherTextboxWrapper();
            this.createEmptyParagraph();
        } else {
            List<HSLFTextParagraph> pList = HSLFTextParagraph.findTextParagraphs(this._txtbox, this.getSheet());
            if (pList == null) {
                this.createEmptyParagraph();
            } else {
                this._paragraphs = pList;
            }
            if (this._paragraphs.isEmpty()) {
                LOG.atWarn().log("TextRecord didn't contained any text lines");
            }
        }
        for (HSLFTextParagraph p : this._paragraphs) {
            p.setParentShape(this);
        }
        return this._paragraphs;
    }

    @Override
    public void setSheet(HSLFSheet sheet) {
        super.setSheet(sheet);
        List<HSLFTextParagraph> ltp = this.getTextParagraphs();
        HSLFTextParagraph.supplySheet(ltp, sheet);
    }

    public OEPlaceholderAtom getPlaceholderAtom() {
        return (OEPlaceholderAtom)this.getClientDataRecord(RecordTypes.OEPlaceholderAtom.typeID);
    }

    public RoundTripHFPlaceholder12 getHFPlaceholderAtom() {
        return (RoundTripHFPlaceholder12)this.getClientDataRecord(RecordTypes.RoundTripHFPlaceholder12.typeID);
    }

    @Override
    public boolean isPlaceholder() {
        return this.getPlaceholderAtom() != null || this.getHFPlaceholderAtom() != null;
    }

    @Override
    public Iterator<HSLFTextParagraph> iterator() {
        return this._paragraphs.iterator();
    }

    @Override
    public Spliterator<HSLFTextParagraph> spliterator() {
        return this._paragraphs.spliterator();
    }

    @Override
    public Insets2D getInsets() {
        return new Insets2D(this.getTopInset(), this.getLeftInset(), this.getBottomInset(), this.getRightInset());
    }

    @Override
    public void setInsets(Insets2D insets) {
        this.setTopInset(insets.top);
        this.setLeftInset(insets.left);
        this.setBottomInset(insets.bottom);
        this.setRightInset(insets.right);
    }

    @Override
    public double getTextHeight() {
        return this.getTextHeight(null);
    }

    @Override
    public double getTextHeight(Graphics2D graphics) {
        DrawFactory drawFact = DrawFactory.getInstance(graphics);
        DrawTextShape dts = drawFact.getDrawable(this);
        return dts.getTextHeight(graphics);
    }

    @Override
    public TextShape.TextDirection getTextDirection() {
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        EscherSimpleProperty prop = (EscherSimpleProperty)HSLFTextShape.getEscherProperty(opt, EscherPropertyTypes.TEXT__TEXTFLOW);
        int msotxfl = prop == null ? 0 : prop.getPropertyValue();
        switch (msotxfl) {
            default: {
                return TextShape.TextDirection.HORIZONTAL;
            }
            case 1: 
            case 3: 
            case 5: {
                return TextShape.TextDirection.VERTICAL;
            }
            case 2: 
        }
        return TextShape.TextDirection.VERTICAL_270;
    }

    @Override
    public void setTextDirection(TextShape.TextDirection orientation) {
        int msotxfl;
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        if (orientation == null) {
            msotxfl = -1;
        } else {
            switch (orientation) {
                default: {
                    msotxfl = -1;
                    break;
                }
                case HORIZONTAL: {
                    msotxfl = 0;
                    break;
                }
                case VERTICAL: {
                    msotxfl = 1;
                    break;
                }
                case VERTICAL_270: {
                    msotxfl = 2;
                }
            }
        }
        HSLFTextShape.setEscherProperty(opt, EscherPropertyTypes.TEXT__TEXTFLOW, msotxfl);
    }

    @Override
    public Double getTextRotation() {
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        EscherSimpleProperty prop = (EscherSimpleProperty)HSLFTextShape.getEscherProperty(opt, EscherPropertyTypes.TEXT__FONTROTATION);
        return prop == null ? null : Double.valueOf(90.0 * (double)prop.getPropertyValue());
    }

    @Override
    public void setTextRotation(Double rotation) {
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        if (rotation == null) {
            opt.removeEscherProperty(EscherPropertyTypes.TEXT__FONTROTATION);
        } else {
            int rot = (int)(Math.round(rotation / 90.0) % 4L);
            this.setEscherProperty(EscherPropertyTypes.TEXT__FONTROTATION, rot);
        }
    }

    public String getRawText() {
        return HSLFTextParagraph.getRawText(this.getTextParagraphs());
    }

    @Override
    public String getText() {
        String rawText = this.getRawText();
        return HSLFTextParagraph.toExternalString(rawText, this.getRunType());
    }

    @Override
    public HSLFTextRun appendText(String text, boolean newParagraph) {
        List<HSLFTextParagraph> paras = this.getTextParagraphs();
        HSLFTextRun htr = HSLFTextParagraph.appendText(paras, text, newParagraph);
        this.setTextId(this.getRawText().hashCode());
        return htr;
    }

    @Override
    public HSLFTextRun setText(String text) {
        List<HSLFTextParagraph> paras = this.getTextParagraphs();
        HSLFTextRun htr = HSLFTextParagraph.setText(paras, text);
        this.setTextId(this.getRawText().hashCode());
        return htr;
    }

    protected void storeText() {
        List<HSLFTextParagraph> paras = this.getTextParagraphs();
        HSLFTextParagraph.storeText(paras);
    }

    public List<HSLFHyperlink> getHyperlinks() {
        return HSLFHyperlink.find(this);
    }

    @Override
    public void setTextPlaceholder(TextShape.TextPlaceholder placeholder) {
        int runType;
        Placeholder ph = null;
        switch (placeholder) {
            default: {
                runType = TextShape.TextPlaceholder.BODY.nativeId;
                ph = Placeholder.BODY;
                break;
            }
            case TITLE: {
                runType = TextShape.TextPlaceholder.TITLE.nativeId;
                ph = Placeholder.TITLE;
                break;
            }
            case CENTER_BODY: {
                runType = TextShape.TextPlaceholder.CENTER_BODY.nativeId;
                ph = Placeholder.BODY;
                break;
            }
            case CENTER_TITLE: {
                runType = TextShape.TextPlaceholder.CENTER_TITLE.nativeId;
                ph = Placeholder.TITLE;
                break;
            }
            case HALF_BODY: {
                runType = TextShape.TextPlaceholder.HALF_BODY.nativeId;
                ph = Placeholder.BODY;
                break;
            }
            case QUARTER_BODY: {
                runType = TextShape.TextPlaceholder.QUARTER_BODY.nativeId;
                ph = Placeholder.BODY;
                break;
            }
            case NOTES: {
                runType = TextShape.TextPlaceholder.NOTES.nativeId;
                break;
            }
            case OTHER: {
                runType = TextShape.TextPlaceholder.OTHER.nativeId;
            }
        }
        this.setRunType(runType);
        if (ph != null) {
            this.setPlaceholder(ph);
        }
    }

    @Override
    public TextShape.TextPlaceholder getTextPlaceholder() {
        return TextShape.TextPlaceholder.fromNativeId(this.getRunType());
    }

    public <S extends Shape<S, P>, P extends TextParagraph<S, P, ? extends TextRun>> Shape<S, P> getMetroShape() {
        return new HSLFMetroShape(this).getShape();
    }

    private static enum HSLFTextAnchor {
        TOP(0, VerticalAlignment.TOP, false, false),
        MIDDLE(1, VerticalAlignment.MIDDLE, false, false),
        BOTTOM(2, VerticalAlignment.BOTTOM, false, false),
        TOP_CENTER(3, VerticalAlignment.TOP, true, false),
        MIDDLE_CENTER(4, VerticalAlignment.MIDDLE, true, false),
        BOTTOM_CENTER(5, VerticalAlignment.BOTTOM, true, false),
        TOP_BASELINE(6, VerticalAlignment.TOP, false, true),
        BOTTOM_BASELINE(7, VerticalAlignment.BOTTOM, false, true),
        TOP_CENTER_BASELINE(8, VerticalAlignment.TOP, true, true),
        BOTTOM_CENTER_BASELINE(9, VerticalAlignment.BOTTOM, true, true);

        public final int nativeId;
        public final VerticalAlignment vAlign;
        public final boolean centered;
        public final Boolean baseline;

        private HSLFTextAnchor(int nativeId, VerticalAlignment vAlign, boolean centered, Boolean baseline) {
            this.nativeId = nativeId;
            this.vAlign = vAlign;
            this.centered = centered;
            this.baseline = baseline;
        }

        static HSLFTextAnchor fromNativeId(int nativeId) {
            for (HSLFTextAnchor ta : HSLFTextAnchor.values()) {
                if (ta.nativeId != nativeId) continue;
                return ta;
            }
            return null;
        }
    }
}

