/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.usermodel;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.common.usermodel.fonts.FontGroup;
import org.apache.poi.common.usermodel.fonts.FontInfo;
import org.apache.poi.hslf.exceptions.HSLFException;
import org.apache.poi.hslf.model.textproperties.BitMaskTextProp;
import org.apache.poi.hslf.model.textproperties.HSLFTabStop;
import org.apache.poi.hslf.model.textproperties.HSLFTabStopPropCollection;
import org.apache.poi.hslf.model.textproperties.IndentProp;
import org.apache.poi.hslf.model.textproperties.TextPFException9;
import org.apache.poi.hslf.model.textproperties.TextProp;
import org.apache.poi.hslf.model.textproperties.TextPropCollection;
import org.apache.poi.hslf.record.ColorSchemeAtom;
import org.apache.poi.hslf.record.EscherTextboxWrapper;
import org.apache.poi.hslf.record.InteractiveInfo;
import org.apache.poi.hslf.record.MasterTextPropAtom;
import org.apache.poi.hslf.record.OutlineTextRefAtom;
import org.apache.poi.hslf.record.PPDrawing;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.hslf.record.RecordAtom;
import org.apache.poi.hslf.record.RecordContainer;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.hslf.record.SlideListWithText;
import org.apache.poi.hslf.record.SlidePersistAtom;
import org.apache.poi.hslf.record.StyleTextProp9Atom;
import org.apache.poi.hslf.record.StyleTextPropAtom;
import org.apache.poi.hslf.record.TextBytesAtom;
import org.apache.poi.hslf.record.TextCharsAtom;
import org.apache.poi.hslf.record.TextHeaderAtom;
import org.apache.poi.hslf.record.TextRulerAtom;
import org.apache.poi.hslf.record.TextSpecInfoAtom;
import org.apache.poi.hslf.record.TxInteractiveInfoAtom;
import org.apache.poi.hslf.usermodel.HSLFFontInfo;
import org.apache.poi.hslf.usermodel.HSLFFontInfoPredefined;
import org.apache.poi.hslf.usermodel.HSLFHyperlink;
import org.apache.poi.hslf.usermodel.HSLFMasterSheet;
import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFSheet;
import org.apache.poi.hslf.usermodel.HSLFSlideMaster;
import org.apache.poi.hslf.usermodel.HSLFTextRun;
import org.apache.poi.hslf.usermodel.HSLFTextShape;
import org.apache.poi.sl.draw.DrawPaint;
import org.apache.poi.sl.usermodel.AutoNumberingScheme;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.sl.usermodel.Placeholder;
import org.apache.poi.sl.usermodel.TabStop;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.sl.usermodel.TextShape;
import org.apache.poi.util.StringUtil;
import org.apache.poi.util.Units;

public final class HSLFTextParagraph
implements TextParagraph<HSLFShape, HSLFTextParagraph, HSLFTextRun> {
    private static final Logger LOG = LogManager.getLogger(HSLFTextParagraph.class);
    private static final int MAX_NUMBER_OF_STYLES = 20000;
    private final TextHeaderAtom _headerAtom;
    private TextBytesAtom _byteAtom;
    private TextCharsAtom _charAtom;
    private TextPropCollection _paragraphStyle;
    protected TextRulerAtom _ruler;
    protected final List<HSLFTextRun> _runs = new ArrayList<HSLFTextRun>();
    protected HSLFTextShape _parentShape;
    private HSLFSheet _sheet;
    private int shapeId;
    private StyleTextProp9Atom styleTextProp9Atom;
    private boolean _dirty;
    private final List<HSLFTextParagraph> parentList;

    HSLFTextParagraph(TextHeaderAtom tha, TextBytesAtom tba, TextCharsAtom tca, List<HSLFTextParagraph> parentList) {
        if (tha == null) {
            throw new IllegalArgumentException("TextHeaderAtom must be set.");
        }
        this._headerAtom = tha;
        this._byteAtom = tba;
        this._charAtom = tca;
        this.parentList = parentList;
        this.setParagraphStyle(new TextPropCollection(1, TextPropCollection.TextPropType.paragraph));
    }

    public void addTextRun(HSLFTextRun run) {
        this._runs.add(run);
    }

    @Override
    public List<HSLFTextRun> getTextRuns() {
        return this._runs;
    }

    public TextPropCollection getParagraphStyle() {
        return this._paragraphStyle;
    }

    public void setParagraphStyle(TextPropCollection paragraphStyle) {
        this._paragraphStyle = paragraphStyle;
    }

    public static void supplySheet(List<HSLFTextParagraph> paragraphs, HSLFSheet sheet) {
        if (paragraphs == null) {
            return;
        }
        for (HSLFTextParagraph p : paragraphs) {
            p.supplySheet(sheet);
        }
        assert (sheet.getSlideShow() != null);
    }

    private void supplySheet(HSLFSheet sheet) {
        this._sheet = sheet;
        for (HSLFTextRun rt : this._runs) {
            rt.updateSheet();
        }
    }

    public HSLFSheet getSheet() {
        return this._sheet;
    }

    protected int getShapeId() {
        return this.shapeId;
    }

    protected void setShapeId(int id) {
        this.shapeId = id;
    }

    protected int getIndex() {
        return this._headerAtom != null ? this._headerAtom.getIndex() : -1;
    }

    protected void setIndex(int index) {
        if (this._headerAtom != null) {
            this._headerAtom.setIndex(index);
        }
    }

    public int getRunType() {
        return this._headerAtom != null ? this._headerAtom.getTextType() : -1;
    }

    public void setRunType(int runType) {
        if (this._headerAtom != null) {
            this._headerAtom.setTextType(runType);
        }
    }

    public boolean isDrawingBased() {
        return this.getIndex() == -1;
    }

    public TextRulerAtom getTextRuler() {
        return this._ruler;
    }

    public TextRulerAtom createTextRuler() {
        this._ruler = this.getTextRuler();
        if (this._ruler == null) {
            this._ruler = TextRulerAtom.getParagraphInstance();
            RecordAtom childAfter = this._byteAtom;
            if (childAfter == null) {
                childAfter = this._charAtom;
            }
            if (childAfter == null) {
                childAfter = this._headerAtom;
            }
            this._headerAtom.getParentRecord().addChildAfter(this._ruler, childAfter);
        }
        return this._ruler;
    }

    public Record[] getRecords() {
        Record[] r = this._headerAtom.getParentRecord().getChildRecords();
        return HSLFTextParagraph.getRecords(r, new int[]{0}, this._headerAtom);
    }

    private static Record[] getRecords(Record[] records, int[] startIdx, TextHeaderAtom headerAtom) {
        Record r;
        Record r2;
        if (records == null) {
            throw new NullPointerException("records need to be set.");
        }
        while (startIdx[0] < records.length && (!((r2 = records[startIdx[0]]) instanceof TextHeaderAtom) || headerAtom != null && r2 != headerAtom)) {
            startIdx[0] = startIdx[0] + 1;
        }
        if (startIdx[0] >= records.length) {
            LOG.atInfo().log("header atom wasn't found - container might contain only an OutlineTextRefAtom");
            return new Record[0];
        }
        int length = 1;
        while (startIdx[0] + length < records.length && !((r = records[startIdx[0] + length]) instanceof TextHeaderAtom) && !(r instanceof SlidePersistAtom)) {
            ++length;
        }
        Record[] result = (Record[])Arrays.copyOfRange(records, startIdx[0], startIdx[0] + length, Record[].class);
        startIdx[0] = startIdx[0] + length;
        return result;
    }

    public void setStyleTextProp9Atom(StyleTextProp9Atom styleTextProp9Atom) {
        this.styleTextProp9Atom = styleTextProp9Atom;
    }

    public StyleTextProp9Atom getStyleTextProp9Atom() {
        return this.styleTextProp9Atom;
    }

    @Override
    public Iterator<HSLFTextRun> iterator() {
        return this._runs.iterator();
    }

    @Override
    public Spliterator<HSLFTextRun> spliterator() {
        return this._runs.spliterator();
    }

    @Override
    public Double getLeftMargin() {
        Integer val = null;
        if (this._ruler != null) {
            Integer[] toList = this._ruler.getTextOffsets();
            Integer n = val = toList.length > this.getIndentLevel() ? toList[this.getIndentLevel()] : null;
        }
        if (val == null) {
            Object tp = this.getPropVal(this._paragraphStyle, "text.offset");
            val = tp == null ? null : Integer.valueOf(((TextProp)tp).getValue());
        }
        return val == null ? null : Double.valueOf(Units.masterToPoints(val));
    }

    @Override
    public void setLeftMargin(Double leftMargin) {
        Integer val = leftMargin == null ? null : Integer.valueOf(Units.pointsToMaster(leftMargin));
        this.setParagraphTextPropVal("text.offset", val);
    }

    @Override
    public Double getRightMargin() {
        return null;
    }

    @Override
    public void setRightMargin(Double rightMargin) {
    }

    @Override
    public Double getIndent() {
        Integer val = null;
        if (this._ruler != null) {
            Integer[] toList = this._ruler.getBulletOffsets();
            Integer n = val = toList.length > this.getIndentLevel() ? toList[this.getIndentLevel()] : null;
        }
        if (val == null) {
            Object tp = this.getPropVal(this._paragraphStyle, "bullet.offset");
            val = tp == null ? null : Integer.valueOf(((TextProp)tp).getValue());
        }
        return val == null ? null : Double.valueOf(Units.masterToPoints(val));
    }

    @Override
    public void setIndent(Double indent) {
        Integer val = indent == null ? null : Integer.valueOf(Units.pointsToMaster(indent));
        this.setParagraphTextPropVal("bullet.offset", val);
    }

    @Override
    public String getDefaultFontFamily() {
        HSLFTextRun tr;
        FontInfo fontInfo = null;
        if (!this._runs.isEmpty() && (fontInfo = (tr = this._runs.get(0)).getFontInfo(null)) == null) {
            fontInfo = tr.getFontInfo(FontGroup.LATIN);
        }
        if (fontInfo == null) {
            fontInfo = HSLFFontInfoPredefined.ARIAL;
        }
        return fontInfo.getTypeface();
    }

    @Override
    public Double getDefaultFontSize() {
        Double d = null;
        if (!this._runs.isEmpty()) {
            d = this._runs.get(0).getFontSize();
        }
        return d != null ? d : 12.0;
    }

    @Override
    public void setTextAlign(TextParagraph.TextAlign align) {
        Integer alignInt = null;
        if (align != null) {
            switch (align) {
                default: {
                    alignInt = 0;
                    break;
                }
                case CENTER: {
                    alignInt = 1;
                    break;
                }
                case RIGHT: {
                    alignInt = 2;
                    break;
                }
                case DIST: {
                    alignInt = 4;
                    break;
                }
                case JUSTIFY: {
                    alignInt = 3;
                    break;
                }
                case JUSTIFY_LOW: {
                    alignInt = 6;
                    break;
                }
                case THAI_DIST: {
                    alignInt = 5;
                }
            }
        }
        this.setParagraphTextPropVal("alignment", alignInt);
    }

    @Override
    public TextParagraph.TextAlign getTextAlign() {
        Object tp = this.getPropVal(this._paragraphStyle, "alignment");
        if (tp == null) {
            return null;
        }
        switch (((TextProp)tp).getValue()) {
            default: {
                return TextParagraph.TextAlign.LEFT;
            }
            case 1: {
                return TextParagraph.TextAlign.CENTER;
            }
            case 2: {
                return TextParagraph.TextAlign.RIGHT;
            }
            case 3: {
                return TextParagraph.TextAlign.JUSTIFY;
            }
            case 6: {
                return TextParagraph.TextAlign.JUSTIFY_LOW;
            }
            case 4: {
                return TextParagraph.TextAlign.DIST;
            }
            case 5: 
        }
        return TextParagraph.TextAlign.THAI_DIST;
    }

    @Override
    public TextParagraph.FontAlign getFontAlign() {
        Object tp = this.getPropVal(this._paragraphStyle, "fontAlign");
        if (tp == null) {
            return null;
        }
        switch (((TextProp)tp).getValue()) {
            case 0: {
                return TextParagraph.FontAlign.BASELINE;
            }
            case 1: {
                return TextParagraph.FontAlign.TOP;
            }
            case 2: {
                return TextParagraph.FontAlign.CENTER;
            }
            case 3: {
                return TextParagraph.FontAlign.BOTTOM;
            }
        }
        return TextParagraph.FontAlign.AUTO;
    }

    public AutoNumberingScheme getAutoNumberingScheme() {
        if (this.styleTextProp9Atom == null) {
            return null;
        }
        TextPFException9[] ant = this.styleTextProp9Atom.getAutoNumberTypes();
        int level = this.getIndentLevel();
        if (ant == null || level == -1 || level >= ant.length) {
            return null;
        }
        return ant[level].getAutoNumberScheme();
    }

    public Integer getAutoNumberingStartAt() {
        if (this.styleTextProp9Atom == null) {
            return null;
        }
        TextPFException9[] ant = this.styleTextProp9Atom.getAutoNumberTypes();
        int level = this.getIndentLevel();
        if (ant == null || level >= ant.length) {
            return null;
        }
        Short startAt = ant[level].getAutoNumberStartNumber();
        assert (startAt != null);
        return startAt.intValue();
    }

    @Override
    public TextParagraph.BulletStyle getBulletStyle() {
        if (!this.isBullet() && this.getAutoNumberingScheme() == null) {
            return null;
        }
        return new TextParagraph.BulletStyle(){

            @Override
            public String getBulletCharacter() {
                Character chr = HSLFTextParagraph.this.getBulletChar();
                return chr == null || chr.charValue() == '\u0000' ? "" : "" + chr;
            }

            @Override
            public String getBulletFont() {
                return HSLFTextParagraph.this.getBulletFont();
            }

            @Override
            public Double getBulletFontSize() {
                return HSLFTextParagraph.this.getBulletSize();
            }

            @Override
            public void setBulletFontColor(Color color) {
                this.setBulletFontColor(DrawPaint.createSolidPaint(color));
            }

            @Override
            public void setBulletFontColor(PaintStyle color) {
                if (!(color instanceof PaintStyle.SolidPaint)) {
                    throw new IllegalArgumentException("HSLF only supports SolidPaint");
                }
                PaintStyle.SolidPaint sp = (PaintStyle.SolidPaint)color;
                Color col = DrawPaint.applyColorTransform(sp.getSolidColor());
                HSLFTextParagraph.this.setBulletColor(col);
            }

            @Override
            public PaintStyle getBulletFontColor() {
                Color col = HSLFTextParagraph.this.getBulletColor();
                return DrawPaint.createSolidPaint(col);
            }

            @Override
            public AutoNumberingScheme getAutoNumberingScheme() {
                return HSLFTextParagraph.this.getAutoNumberingScheme();
            }

            @Override
            public Integer getAutoNumberingStartAt() {
                return HSLFTextParagraph.this.getAutoNumberingStartAt();
            }
        };
    }

    @Override
    public void setBulletStyle(Object ... styles) {
        if (styles.length == 0) {
            this.setBullet(false);
        } else {
            this.setBullet(true);
            for (Object ostyle : styles) {
                if (ostyle instanceof Number) {
                    this.setBulletSize(((Number)ostyle).doubleValue());
                    continue;
                }
                if (ostyle instanceof Color) {
                    this.setBulletColor((Color)ostyle);
                    continue;
                }
                if (ostyle instanceof Character) {
                    this.setBulletChar((Character)ostyle);
                    continue;
                }
                if (ostyle instanceof String) {
                    this.setBulletFont((String)ostyle);
                    continue;
                }
                if (!(ostyle instanceof AutoNumberingScheme)) continue;
                throw new HSLFException("setting bullet auto-numberin scheme for HSLF not supported ... yet");
            }
        }
    }

    public HSLFTextShape getParentShape() {
        return this._parentShape;
    }

    public void setParentShape(HSLFTextShape parentShape) {
        this._parentShape = parentShape;
    }

    @Override
    public int getIndentLevel() {
        return this._paragraphStyle == null ? 0 : (int)this._paragraphStyle.getIndentLevel();
    }

    @Override
    public void setIndentLevel(int level) {
        if (this._paragraphStyle != null) {
            this._paragraphStyle.setIndentLevel((short)level);
        }
    }

    public void setBullet(boolean flag) {
        this.setFlag(0, flag);
    }

    public boolean isBullet() {
        return this.getFlag(0);
    }

    public void setBulletChar(Character c) {
        Integer val = c == null ? null : Integer.valueOf(c.charValue());
        this.setParagraphTextPropVal("bullet.char", val);
    }

    public Character getBulletChar() {
        Object tp = this.getPropVal(this._paragraphStyle, "bullet.char");
        return tp == null ? null : Character.valueOf((char)((TextProp)tp).getValue());
    }

    public void setBulletSize(Double size) {
        this.setPctOrPoints("bullet.size", size);
    }

    public Double getBulletSize() {
        return this.getPctOrPoints("bullet.size");
    }

    public void setBulletColor(Color color) {
        Integer val = color == null ? null : Integer.valueOf(new Color(color.getBlue(), color.getGreen(), color.getRed(), 254).getRGB());
        this.setParagraphTextPropVal("bullet.color", val);
        this.setFlag(2, color != null);
    }

    public Color getBulletColor() {
        Object tp = this.getPropVal(this._paragraphStyle, "bullet.color");
        boolean hasColor = this.getFlag(2);
        if (tp == null || !hasColor) {
            if (this._runs.isEmpty()) {
                return null;
            }
            PaintStyle.SolidPaint sp = this._runs.get(0).getFontColor();
            if (sp == null) {
                return null;
            }
            return DrawPaint.applyColorTransform(sp.getSolidColor());
        }
        return HSLFTextParagraph.getColorFromColorIndexStruct(((TextProp)tp).getValue(), this._sheet);
    }

    public void setBulletFont(String typeface) {
        if (typeface == null) {
            this.setPropVal(this._paragraphStyle, "bullet.font", null);
            this.setFlag(1, false);
            return;
        }
        HSLFFontInfo fi = new HSLFFontInfo(typeface);
        fi = this.getSheet().getSlideShow().addFont(fi);
        this.setParagraphTextPropVal("bullet.font", fi.getIndex());
        this.setFlag(1, true);
    }

    public String getBulletFont() {
        Object tp = this.getPropVal(this._paragraphStyle, "bullet.font");
        boolean hasFont = this.getFlag(1);
        if (tp == null || !hasFont) {
            return this.getDefaultFontFamily();
        }
        HSLFFontInfo ppFont = this.getSheet().getSlideShow().getFont(((TextProp)tp).getValue());
        assert (ppFont != null);
        return ppFont.getTypeface();
    }

    @Override
    public void setLineSpacing(Double lineSpacing) {
        this.setPctOrPoints("linespacing", lineSpacing);
    }

    @Override
    public Double getLineSpacing() {
        return this.getPctOrPoints("linespacing");
    }

    @Override
    public void setSpaceBefore(Double spaceBefore) {
        this.setPctOrPoints("spacebefore", spaceBefore);
    }

    @Override
    public Double getSpaceBefore() {
        return this.getPctOrPoints("spacebefore");
    }

    @Override
    public void setSpaceAfter(Double spaceAfter) {
        this.setPctOrPoints("spaceafter", spaceAfter);
    }

    @Override
    public Double getSpaceAfter() {
        return this.getPctOrPoints("spaceafter");
    }

    @Override
    public Double getDefaultTabSize() {
        return null;
    }

    @Override
    public List<? extends TabStop> getTabStops() {
        List<HSLFTabStop> tabStops;
        if (this.getSheet() instanceof HSLFSlideMaster) {
            HSLFTabStopPropCollection tpc = (HSLFTabStopPropCollection)this.getMasterPropVal(this._paragraphStyle, "tabStops");
            if (tpc == null) {
                return null;
            }
            tabStops = tpc.getTabStops();
        } else {
            TextRulerAtom textRuler = (TextRulerAtom)this._headerAtom.getParentRecord().findFirstOfType(RecordTypes.TextRulerAtom.typeID);
            if (textRuler == null) {
                return null;
            }
            tabStops = textRuler.getTabStops();
        }
        return tabStops.stream().map(x$0 -> new HSLFTabStopDecorator((HSLFTabStop)x$0)).collect(Collectors.toList());
    }

    @Override
    public void addTabStops(double positionInPoints, TabStop.TabStopType tabStopType) {
        HSLFTabStop ts = new HSLFTabStop(0, tabStopType);
        ts.setPositionInPoints(positionInPoints);
        if (this.getSheet() instanceof HSLFSlideMaster) {
            Consumer<HSLFTabStopPropCollection> con = tp -> tp.addTabStop(ts);
            this.setPropValInner(this._paragraphStyle, "tabStops", con);
        } else {
            RecordContainer cont = this._headerAtom.getParentRecord();
            TextRulerAtom textRuler = (TextRulerAtom)cont.findFirstOfType(RecordTypes.TextRulerAtom.typeID);
            if (textRuler == null) {
                textRuler = TextRulerAtom.getParagraphInstance();
                cont.appendChildRecord(textRuler);
            }
            textRuler.getTabStops().add(ts);
        }
    }

    @Override
    public void clearTabStops() {
        if (this.getSheet() instanceof HSLFSlideMaster) {
            this.setPropValInner(this._paragraphStyle, "tabStops", null);
        } else {
            RecordContainer cont = this._headerAtom.getParentRecord();
            TextRulerAtom textRuler = (TextRulerAtom)cont.findFirstOfType(RecordTypes.TextRulerAtom.typeID);
            if (textRuler == null) {
                return;
            }
            textRuler.getTabStops().clear();
        }
    }

    private Double getPctOrPoints(String propName) {
        Object tp = this.getPropVal(this._paragraphStyle, propName);
        if (tp == null) {
            return null;
        }
        int val = ((TextProp)tp).getValue();
        return val < 0 ? Units.masterToPoints(val) : (double)val;
    }

    private void setPctOrPoints(String propName, Double dval) {
        Integer ival = null;
        if (dval != null) {
            ival = dval < 0.0 ? Units.pointsToMaster(dval) : dval.intValue();
        }
        this.setParagraphTextPropVal(propName, ival);
    }

    private boolean getFlag(int index) {
        BitMaskTextProp tp = (BitMaskTextProp)this.getPropVal(this._paragraphStyle, "paragraph_flags");
        return tp != null && tp.getSubValue(index);
    }

    private void setFlag(int index, boolean value) {
        BitMaskTextProp tp = (BitMaskTextProp)this._paragraphStyle.addWithName("paragraph_flags");
        tp.setSubValue(value, index);
        this.setDirty();
    }

    protected <T extends TextProp> T getPropVal(TextPropCollection props, String propName) {
        String[] propNames;
        for (String pn : propNames = propName.split(",")) {
            Object prop = props.findByName(pn);
            if (!HSLFTextParagraph.isValidProp(prop)) continue;
            return prop;
        }
        return this.getMasterPropVal(props, propName);
    }

    private <T extends TextProp> T getMasterPropVal(TextPropCollection props, String propName) {
        HSLFMasterSheet master;
        boolean isChar;
        boolean bl = isChar = props.getTextPropType() == TextPropCollection.TextPropType.character;
        if (!isChar) {
            boolean hardAttribute;
            BitMaskTextProp maskProp = (BitMaskTextProp)props.findByName("paragraph_flags");
            boolean bl2 = hardAttribute = maskProp != null && maskProp.getValue() == 0;
            if (hardAttribute) {
                return null;
            }
        }
        String[] propNames = propName.split(",");
        HSLFSheet sheet = this.getSheet();
        int txtype = this.getRunType();
        if (sheet instanceof HSLFMasterSheet) {
            master = (HSLFMasterSheet)sheet;
        } else {
            master = sheet.getMasterSheet();
            if (master == null) {
                LOG.atWarn().log("MasterSheet is not available");
                return null;
            }
        }
        for (String pn : propNames) {
            Object prop;
            TextPropCollection masterProps = master.getPropCollection(txtype, this.getIndentLevel(), pn, isChar);
            if (masterProps == null || !HSLFTextParagraph.isValidProp(prop = masterProps.findByName(pn))) continue;
            return prop;
        }
        return null;
    }

    private static boolean isValidProp(TextProp prop) {
        return prop != null && (!prop.getName().contains("font") || prop.getValue() != -1);
    }

    protected void setPropVal(TextPropCollection props, String name, Integer val) {
        this.setPropValInner(props, name, val == null ? null : tp -> tp.setValue(val));
    }

    private void setPropValInner(TextPropCollection props, String name, Consumer<? extends TextProp> handler) {
        TextPropCollection pc;
        boolean isChar;
        boolean bl = isChar = props.getTextPropType() == TextPropCollection.TextPropType.character;
        if (this._sheet instanceof HSLFMasterSheet) {
            pc = ((HSLFMasterSheet)this._sheet).getPropCollection(this.getRunType(), this.getIndentLevel(), "*", isChar);
            if (pc == null) {
                throw new HSLFException("Master text property collection can't be determined.");
            }
        } else {
            pc = props;
        }
        if (handler == null) {
            pc.removeByName(name);
        } else {
            handler.accept((TextProp)pc.addWithName(name));
        }
        this.setDirty();
    }

    protected static void fixLineEndings(List<HSLFTextParagraph> paragraphs) {
        HSLFTextRun lastRun = null;
        for (HSLFTextParagraph p : paragraphs) {
            List<HSLFTextRun> ltr;
            if (lastRun != null && !lastRun.getRawText().endsWith("\r")) {
                lastRun.setText(lastRun.getRawText() + "\r");
            }
            if ((ltr = p.getTextRuns()).isEmpty()) {
                throw new HSLFException("paragraph without textruns found");
            }
            lastRun = ltr.get(ltr.size() - 1);
            assert (lastRun.getRawText() != null);
        }
    }

    private static StyleTextPropAtom findStyleAtomPresent(TextHeaderAtom header, int textLen) {
        boolean afterHeader = false;
        StyleTextPropAtom style = null;
        for (Record record : header.getParentRecord().getChildRecords()) {
            long rt = record.getRecordType();
            if (afterHeader && rt == (long)RecordTypes.TextHeaderAtom.typeID) break;
            if (!(afterHeader |= header == record) || rt != (long)RecordTypes.StyleTextPropAtom.typeID) continue;
            style = (StyleTextPropAtom)record;
        }
        if (style == null) {
            LOG.atInfo().log("styles atom doesn't exist. Creating dummy record for later saving.");
            style = new StyleTextPropAtom(textLen < 0 ? 1 : textLen);
        } else if (textLen >= 0) {
            style.setParentTextSize(textLen);
        }
        return style;
    }

    protected static void storeText(List<HSLFTextParagraph> paragraphs) {
        HSLFTextParagraph.fixLineEndings(paragraphs);
        HSLFTextParagraph.updateTextAtom(paragraphs);
        HSLFTextParagraph.updateStyles(paragraphs);
        HSLFTextParagraph.updateHyperlinks(paragraphs);
        HSLFTextParagraph.refreshRecords(paragraphs);
        for (HSLFTextParagraph p : paragraphs) {
            p._dirty = false;
        }
    }

    private static void updateTextAtom(List<HSLFTextParagraph> paragraphs) {
        RecordAtom newRecord;
        String rawText = HSLFTextParagraph.toInternalString(HSLFTextParagraph.getRawText(paragraphs));
        boolean isUnicode = StringUtil.hasMultibyte(rawText);
        TextHeaderAtom headerAtom = paragraphs.get((int)0)._headerAtom;
        TextBytesAtom byteAtom = paragraphs.get((int)0)._byteAtom;
        TextCharsAtom charAtom = paragraphs.get((int)0)._charAtom;
        StyleTextPropAtom styleAtom = HSLFTextParagraph.findStyleAtomPresent(headerAtom, rawText.length());
        RecordAtom oldRecord = null;
        if (isUnicode) {
            if (byteAtom != null || charAtom == null) {
                oldRecord = byteAtom;
                charAtom = new TextCharsAtom();
            }
            newRecord = charAtom;
            charAtom.setText(rawText);
        } else {
            if (charAtom != null || byteAtom == null) {
                oldRecord = charAtom;
                byteAtom = new TextBytesAtom();
            }
            newRecord = byteAtom;
            byte[] byteText = new byte[rawText.length()];
            StringUtil.putCompressedUnicode(rawText, byteText, 0);
            byteAtom.setText(byteText);
        }
        RecordContainer _txtbox = headerAtom.getParentRecord();
        Record[] cr = _txtbox.getChildRecords();
        int textIdx = -1;
        int styleIdx = -1;
        for (int i = 0; i < cr.length; ++i) {
            Record r = cr[i];
            if (r == headerAtom) continue;
            if (r == oldRecord || r == newRecord) {
                textIdx = i;
                continue;
            }
            if (r != styleAtom) continue;
            styleIdx = i;
        }
        if (textIdx == -1) {
            _txtbox.addChildAfter(newRecord, headerAtom);
        } else {
            cr[textIdx] = newRecord;
        }
        if (styleIdx == -1) {
            _txtbox.addChildAfter(styleAtom, newRecord);
        }
        for (HSLFTextParagraph p : paragraphs) {
            if (newRecord == byteAtom) {
                p._byteAtom = byteAtom;
                p._charAtom = null;
                continue;
            }
            p._byteAtom = null;
            p._charAtom = charAtom;
        }
    }

    private static void updateStyles(List<HSLFTextParagraph> paragraphs) {
        String rawText = HSLFTextParagraph.toInternalString(HSLFTextParagraph.getRawText(paragraphs));
        TextHeaderAtom headerAtom = paragraphs.get((int)0)._headerAtom;
        StyleTextPropAtom styleAtom = HSLFTextParagraph.findStyleAtomPresent(headerAtom, rawText.length());
        styleAtom.clearStyles();
        TextPropCollection lastPTPC = null;
        TextPropCollection lastRTPC = null;
        TextPropCollection ptpc = null;
        TextPropCollection rtpc = null;
        for (HSLFTextParagraph para : paragraphs) {
            ptpc = para.getParagraphStyle();
            ptpc.updateTextSize(0);
            if (!ptpc.equals(lastPTPC)) {
                lastPTPC = ptpc.copy();
                lastPTPC.updateTextSize(0);
                styleAtom.addParagraphTextPropCollection(lastPTPC);
            }
            for (HSLFTextRun tr : para.getTextRuns()) {
                rtpc = tr.getCharacterStyle();
                rtpc.updateTextSize(0);
                if (!rtpc.equals(lastRTPC)) {
                    lastRTPC = rtpc.copy();
                    lastRTPC.updateTextSize(0);
                    styleAtom.addCharacterTextPropCollection(lastRTPC);
                }
                int len = tr.getLength();
                ptpc.updateTextSize(ptpc.getCharactersCovered() + len);
                rtpc.updateTextSize(len);
                lastPTPC.updateTextSize(lastPTPC.getCharactersCovered() + len);
                lastRTPC.updateTextSize(lastRTPC.getCharactersCovered() + len);
            }
        }
        if (lastPTPC == null || lastRTPC == null || ptpc == null || rtpc == null) {
            throw new HSLFException("Not all TextPropCollection could be determined.");
        }
        ptpc.updateTextSize(ptpc.getCharactersCovered() + 1);
        rtpc.updateTextSize(rtpc.getCharactersCovered() + 1);
        lastPTPC.updateTextSize(lastPTPC.getCharactersCovered() + 1);
        lastRTPC.updateTextSize(lastRTPC.getCharactersCovered() + 1);
        for (Record r : paragraphs.get(0).getRecords()) {
            if (!(r instanceof TextSpecInfoAtom)) continue;
            ((TextSpecInfoAtom)r).setParentSize(rawText.length() + 1);
            break;
        }
    }

    private static void updateHyperlinks(List<HSLFTextParagraph> paragraphs) {
        TextHeaderAtom headerAtom = paragraphs.get((int)0)._headerAtom;
        RecordContainer _txtbox = headerAtom.getParentRecord();
        for (Record r : _txtbox.getChildRecords()) {
            if (!(r instanceof InteractiveInfo) && !(r instanceof TxInteractiveInfoAtom)) continue;
            _txtbox.removeChild(r);
        }
        HSLFHyperlink lastLink = null;
        for (HSLFTextParagraph para : paragraphs) {
            for (HSLFTextRun run : para) {
                HSLFHyperlink thisLink = run.getHyperlink();
                if (thisLink != null && thisLink == lastLink) {
                    thisLink.setEndIndex(thisLink.getEndIndex() + run.getLength());
                } else if (lastLink != null) {
                    InteractiveInfo info = lastLink.getInfo();
                    TxInteractiveInfoAtom txinfo = lastLink.getTextRunInfo();
                    assert (info != null && txinfo != null);
                    _txtbox.appendChildRecord(info);
                    _txtbox.appendChildRecord(txinfo);
                }
                lastLink = thisLink;
            }
        }
        if (lastLink != null) {
            InteractiveInfo info = lastLink.getInfo();
            TxInteractiveInfoAtom txinfo = lastLink.getTextRunInfo();
            assert (info != null && txinfo != null);
            _txtbox.appendChildRecord(info);
            _txtbox.appendChildRecord(txinfo);
        }
    }

    private static void refreshRecords(List<HSLFTextParagraph> paragraphs) {
        TextHeaderAtom headerAtom = paragraphs.get((int)0)._headerAtom;
        RecordContainer _txtbox = headerAtom.getParentRecord();
        if (_txtbox instanceof EscherTextboxWrapper) {
            try {
                _txtbox.writeOut(null);
            }
            catch (IOException e) {
                throw new HSLFException("failed dummy write", e);
            }
        }
    }

    protected static HSLFTextRun appendText(List<HSLFTextParagraph> paragraphs, String text, boolean newParagraph) {
        text = HSLFTextParagraph.toInternalString(text);
        assert (!paragraphs.isEmpty() && !paragraphs.get(0).getTextRuns().isEmpty());
        HSLFTextParagraph htp = paragraphs.get(paragraphs.size() - 1);
        HSLFTextRun htr = htp.getTextRuns().get(htp.getTextRuns().size() - 1);
        boolean addParagraph = newParagraph;
        for (String rawText : text.split("(?<=\r)")) {
            TextPropCollection tpc;
            boolean lastParaEmpty;
            boolean lastRunEmpty = htr.getLength() == 0;
            boolean bl = lastParaEmpty = lastRunEmpty && htp.getTextRuns().size() == 1;
            if (addParagraph && !lastParaEmpty) {
                tpc = htp.getParagraphStyle();
                HSLFTextParagraph prevHtp = htp;
                htp = new HSLFTextParagraph(htp._headerAtom, htp._byteAtom, htp._charAtom, paragraphs);
                htp.setParagraphStyle(tpc.copy());
                htp.setParentShape(prevHtp.getParentShape());
                htp.setShapeId(prevHtp.getShapeId());
                htp.supplySheet(prevHtp.getSheet());
                paragraphs.add(htp);
            }
            addParagraph = true;
            if (!lastRunEmpty) {
                tpc = htr.getCharacterStyle();
                htr = new HSLFTextRun(htp);
                htr.setCharacterStyle(tpc.copy());
                htp.addTextRun(htr);
            }
            htr.setText(rawText);
        }
        HSLFTextParagraph.storeText(paragraphs);
        return htr;
    }

    public static HSLFTextRun setText(List<HSLFTextParagraph> paragraphs, String text) {
        HSLFTextParagraph htp;
        assert (!paragraphs.isEmpty() && !paragraphs.get(0).getTextRuns().isEmpty());
        Iterator<HSLFTextParagraph> paraIter = paragraphs.iterator();
        HSLFTextParagraph hSLFTextParagraph = htp = paraIter.hasNext() ? paraIter.next() : null;
        assert (htp != null);
        while (paraIter.hasNext()) {
            paraIter.next();
            paraIter.remove();
        }
        Iterator<HSLFTextRun> runIter = htp.getTextRuns().iterator();
        if (runIter.hasNext()) {
            HSLFTextRun htr = runIter.next();
            htr.setText("");
            while (runIter.hasNext()) {
                runIter.next();
                runIter.remove();
            }
        } else {
            HSLFTextRun trun = new HSLFTextRun(htp);
            htp.addTextRun(trun);
        }
        return HSLFTextParagraph.appendText(paragraphs, text, false);
    }

    public static String getText(List<HSLFTextParagraph> paragraphs) {
        assert (!paragraphs.isEmpty());
        String rawText = HSLFTextParagraph.getRawText(paragraphs);
        return HSLFTextParagraph.toExternalString(rawText, paragraphs.get(0).getRunType());
    }

    public static String getRawText(List<HSLFTextParagraph> paragraphs) {
        StringBuilder sb = new StringBuilder();
        for (HSLFTextParagraph p : paragraphs) {
            for (HSLFTextRun r : p.getTextRuns()) {
                sb.append(r.getRawText());
            }
        }
        return sb.toString();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (HSLFTextRun r : this.getTextRuns()) {
            sb.append(r.getRawText());
        }
        return HSLFTextParagraph.toExternalString(sb.toString(), this.getRunType());
    }

    protected static String toInternalString(String s) {
        return s.replaceAll("\\r?\\n", "\r");
    }

    public static String toExternalString(String rawText, int runType) {
        String text = rawText.replace('\r', '\n');
        int repl = runType == -1 || runType == TextShape.TextPlaceholder.TITLE.nativeId || runType == TextShape.TextPlaceholder.CENTER_TITLE.nativeId ? 10 : 32;
        return text.replace('\u000b', (char)repl);
    }

    public static List<List<HSLFTextParagraph>> findTextParagraphs(PPDrawing ppdrawing, HSLFSheet sheet) {
        if (ppdrawing == null) {
            throw new IllegalArgumentException("Did not receive a valid drawing for sheet " + sheet._getSheetNumber());
        }
        ArrayList<List<HSLFTextParagraph>> runsV = new ArrayList<List<HSLFTextParagraph>>();
        for (EscherTextboxWrapper wrapper : ppdrawing.getTextboxWrappers()) {
            List<HSLFTextParagraph> p = HSLFTextParagraph.findTextParagraphs(wrapper, sheet);
            if (p == null) continue;
            runsV.add(p);
        }
        return runsV;
    }

    protected static List<HSLFTextParagraph> findTextParagraphs(EscherTextboxWrapper wrapper, HSLFSheet sheet) {
        List<List<HSLFTextParagraph>> sheetRuns;
        RecordContainer.handleParentAwareRecords(wrapper);
        int shapeId = wrapper.getShapeId();
        List<HSLFTextParagraph> rv = null;
        OutlineTextRefAtom ota = (OutlineTextRefAtom)wrapper.findFirstOfType(RecordTypes.OutlineTextRefAtom.typeID);
        if (ota != null) {
            if (sheet == null) {
                throw new HSLFException("Outline atom reference can't be solved without a sheet record");
            }
            sheetRuns = sheet.getTextParagraphs();
            assert (sheetRuns != null);
            int idx = ota.getTextIndex();
            for (List<HSLFTextParagraph> r : sheetRuns) {
                if (r.isEmpty()) continue;
                int ridx = r.get(0).getIndex();
                if (ridx > idx) break;
                if (ridx != idx) continue;
                if (rv == null) {
                    rv = r;
                    continue;
                }
                rv = new ArrayList<HSLFTextParagraph>(rv);
                rv.addAll(r);
            }
            if (rv == null || rv.isEmpty()) {
                LOG.atWarn().log("text run not found for OutlineTextRefAtom.TextIndex={}", (Object)Unbox.box(idx));
            }
        } else {
            if (sheet != null) {
                sheetRuns = sheet.getTextParagraphs();
                assert (sheetRuns != null);
                for (List<HSLFTextParagraph> paras : sheetRuns) {
                    if (paras.isEmpty() || paras.get((int)0)._headerAtom.getParentRecord() != wrapper) continue;
                    rv = paras;
                    break;
                }
            }
            if (rv == null) {
                List<List<HSLFTextParagraph>> rvl = HSLFTextParagraph.findTextParagraphs(wrapper.getChildRecords());
                switch (rvl.size()) {
                    case 0: {
                        break;
                    }
                    case 1: {
                        rv = rvl.get(0);
                        break;
                    }
                    default: {
                        throw new HSLFException("TextBox contains more than one list of paragraphs.");
                    }
                }
            }
        }
        if (rv != null) {
            StyleTextProp9Atom styleTextProp9Atom = wrapper.getStyleTextProp9Atom();
            for (HSLFTextParagraph htp : rv) {
                htp.setShapeId(shapeId);
                htp.setStyleTextProp9Atom(styleTextProp9Atom);
            }
        }
        return rv;
    }

    protected static List<List<HSLFTextParagraph>> findTextParagraphs(Record[] records) {
        ArrayList<List<HSLFTextParagraph>> paragraphCollection = new ArrayList<List<HSLFTextParagraph>>();
        int[] recordIdx = new int[]{0};
        int slwtIndex = 0;
        while (recordIdx[0] < records.length) {
            TextHeaderAtom header = null;
            TextBytesAtom tbytes = null;
            TextCharsAtom tchars = null;
            TextRulerAtom ruler = null;
            MasterTextPropAtom indents = null;
            for (Record r : HSLFTextParagraph.getRecords(records, recordIdx, null)) {
                long rt = r.getRecordType();
                if ((long)RecordTypes.TextHeaderAtom.typeID == rt) {
                    header = (TextHeaderAtom)r;
                    continue;
                }
                if ((long)RecordTypes.TextBytesAtom.typeID == rt) {
                    tbytes = (TextBytesAtom)r;
                    continue;
                }
                if ((long)RecordTypes.TextCharsAtom.typeID == rt) {
                    tchars = (TextCharsAtom)r;
                    continue;
                }
                if ((long)RecordTypes.TextRulerAtom.typeID == rt) {
                    ruler = (TextRulerAtom)r;
                    continue;
                }
                if ((long)RecordTypes.MasterTextPropAtom.typeID != rt) continue;
                indents = (MasterTextPropAtom)r;
            }
            if (header == null) break;
            if (header.getParentRecord() instanceof SlideListWithText) {
                header.setIndex(slwtIndex);
            }
            if (tbytes == null && tchars == null) {
                tbytes = new TextBytesAtom();
                LOG.atInfo().log("bytes nor chars atom doesn't exist. Creating dummy record for later saving.");
            }
            String rawText = tchars != null ? tchars.getText() : tbytes.getText();
            StyleTextPropAtom styles = HSLFTextParagraph.findStyleAtomPresent(header, rawText.length());
            ArrayList<HSLFTextParagraph> paragraphs = new ArrayList<HSLFTextParagraph>();
            paragraphCollection.add(paragraphs);
            for (String para : rawText.split("(?<=\r)")) {
                HSLFTextParagraph tpara = new HSLFTextParagraph(header, tbytes, tchars, paragraphs);
                paragraphs.add(tpara);
                tpara._ruler = ruler;
                tpara.getParagraphStyle().updateTextSize(para.length());
                HSLFTextRun trun = new HSLFTextRun(tpara);
                tpara.addTextRun(trun);
                trun.setText(para);
            }
            HSLFTextParagraph.applyCharacterStyles(paragraphs, styles.getCharacterStyles());
            HSLFTextParagraph.applyParagraphStyles(paragraphs, styles.getParagraphStyles());
            if (indents != null) {
                HSLFTextParagraph.applyParagraphIndents(paragraphs, indents.getIndents());
            }
            ++slwtIndex;
        }
        if (paragraphCollection.isEmpty()) {
            LOG.atDebug().log("No text records found.");
        }
        return paragraphCollection;
    }

    protected static void applyHyperlinks(List<HSLFTextParagraph> paragraphs) {
        List<HSLFHyperlink> links = HSLFHyperlink.find(paragraphs);
        block0: for (HSLFHyperlink h : links) {
            int csIdx = 0;
            for (HSLFTextParagraph p : paragraphs) {
                if (csIdx > h.getEndIndex()) continue block0;
                List<HSLFTextRun> runs = p.getTextRuns();
                for (int rIdx = 0; rIdx < runs.size(); ++rIdx) {
                    HSLFTextRun run = runs.get(rIdx);
                    int rlen = run.getLength();
                    if (csIdx < h.getEndIndex() && h.getStartIndex() < csIdx + rlen) {
                        String rawText = run.getRawText();
                        int startIdx = h.getStartIndex() - csIdx;
                        if (startIdx > 0) {
                            HSLFTextRun newRun = new HSLFTextRun(p);
                            newRun.setCharacterStyle(run.getCharacterStyle());
                            newRun.setText(rawText.substring(startIdx));
                            run.setText(rawText.substring(0, startIdx));
                            runs.add(rIdx + 1, newRun);
                            rlen = startIdx;
                        } else {
                            int endIdx = Math.min(rlen, h.getEndIndex() - h.getStartIndex());
                            if (endIdx < rlen) {
                                HSLFTextRun newRun = new HSLFTextRun(p);
                                newRun.setCharacterStyle(run.getCharacterStyle());
                                newRun.setText(rawText.substring(0, endIdx));
                                run.setText(rawText.substring(endIdx));
                                runs.add(rIdx, newRun);
                                rlen = endIdx;
                                run = newRun;
                            }
                            run.setHyperlink(h);
                        }
                    }
                    csIdx += rlen;
                }
            }
        }
    }

    protected static void applyCharacterStyles(List<HSLFTextParagraph> paragraphs, List<TextPropCollection> charStyles) {
        int paraIdx = 0;
        int runIdx = 0;
        for (int csIdx = 0; csIdx < charStyles.size(); ++csIdx) {
            TextPropCollection p = charStyles.get(csIdx);
            int ccStyle = p.getCharactersCovered();
            if (ccStyle > 20000) {
                throw new IllegalStateException("Cannot process more than 20000 styles, but had paragraph with " + ccStyle);
            }
            int ccRun = 0;
            while (ccRun < ccStyle) {
                HSLFTextParagraph para = paragraphs.get(paraIdx);
                List<HSLFTextRun> runs = para.getTextRuns();
                HSLFTextRun trun = runs.get(runIdx);
                int len = trun.getLength();
                if (ccRun + len <= ccStyle) {
                    ccRun += len;
                } else {
                    String text = trun.getRawText();
                    trun.setText(text.substring(0, ccStyle - ccRun));
                    HSLFTextRun nextRun = new HSLFTextRun(para);
                    nextRun.setText(text.substring(ccStyle - ccRun));
                    runs.add(runIdx + 1, nextRun);
                    ccRun += ccStyle - ccRun;
                }
                trun.setCharacterStyle(p);
                if (paraIdx == paragraphs.size() - 1 && runIdx == runs.size() - 1) {
                    if (csIdx < charStyles.size() - 1) {
                        HSLFTextRun nextRun = new HSLFTextRun(para);
                        nextRun.setText("");
                        runs.add(nextRun);
                        ++ccRun;
                    } else {
                        trun.getCharacterStyle().updateTextSize(trun.getLength() + 1);
                        ++ccRun;
                    }
                }
                if (++runIdx != runs.size()) continue;
                ++paraIdx;
                runIdx = 0;
            }
        }
    }

    protected static void applyParagraphStyles(List<HSLFTextParagraph> paragraphs, List<TextPropCollection> paraStyles) {
        int paraIdx = 0;
        for (TextPropCollection p : paraStyles) {
            int ccPara = 0;
            int ccStyle = p.getCharactersCovered();
            while (ccPara < ccStyle) {
                if (paraIdx >= paragraphs.size()) {
                    return;
                }
                HSLFTextParagraph htp = paragraphs.get(paraIdx);
                TextPropCollection pCopy = p.copy();
                htp.setParagraphStyle(pCopy);
                int len = 0;
                for (HSLFTextRun trun : htp.getTextRuns()) {
                    len += trun.getLength();
                }
                if (paraIdx == paragraphs.size() - 1) {
                    ++len;
                }
                pCopy.updateTextSize(len);
                ccPara += len;
                ++paraIdx;
            }
        }
    }

    protected static void applyParagraphIndents(List<HSLFTextParagraph> paragraphs, List<IndentProp> paraStyles) {
        int paraIdx = 0;
        for (IndentProp p : paraStyles) {
            int ccPara = 0;
            int ccStyle = p.getCharactersCovered();
            while (ccPara < ccStyle) {
                if (paraIdx >= paragraphs.size() || ccPara >= ccStyle - 1) {
                    return;
                }
                HSLFTextParagraph para = paragraphs.get(paraIdx);
                int len = 0;
                for (HSLFTextRun trun : para.getTextRuns()) {
                    len += trun.getLength();
                }
                para.setIndentLevel(p.getIndentLevel());
                ccPara += len + 1;
                ++paraIdx;
            }
        }
    }

    public EscherTextboxWrapper getTextboxWrapper() {
        return (EscherTextboxWrapper)this._headerAtom.getParentRecord();
    }

    protected static Color getColorFromColorIndexStruct(int rgb, HSLFSheet sheet) {
        Color tmp;
        int cidx = rgb >>> 24;
        switch (cidx) {
            case 0: 
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 7: {
                if (sheet == null) {
                    return null;
                }
                ColorSchemeAtom ca = sheet.getColorScheme();
                tmp = new Color(ca.getColor(cidx), true);
                break;
            }
            case 254: {
                tmp = new Color(rgb, true);
                break;
            }
            default: {
                return null;
            }
        }
        return new Color(tmp.getBlue(), tmp.getGreen(), tmp.getRed());
    }

    public void setParagraphTextPropVal(String propName, Integer val) {
        this.setPropVal(this._paragraphStyle, propName, val);
        this.setDirty();
    }

    public void setDirty() {
        this._dirty = true;
    }

    public boolean isDirty() {
        return this._dirty;
    }

    int getStartIdxOfTextRun(HSLFTextRun textrun) {
        int idx = 0;
        for (HSLFTextParagraph p : this.parentList) {
            for (HSLFTextRun r : p) {
                if (r == textrun) {
                    return idx;
                }
                idx += r.getLength();
            }
        }
        return -1;
    }

    @Override
    public boolean isHeaderOrFooter() {
        HSLFTextShape s = this.getParentShape();
        if (s == null) {
            return false;
        }
        Placeholder ph = s.getPlaceholder();
        if (ph == null) {
            return false;
        }
        switch (ph) {
            case DATETIME: 
            case SLIDE_NUMBER: 
            case FOOTER: 
            case HEADER: {
                return true;
            }
        }
        return false;
    }

    private class HSLFTabStopDecorator
    implements TabStop {
        final HSLFTabStop tabStop;

        HSLFTabStopDecorator(HSLFTabStop tabStop) {
            this.tabStop = tabStop;
        }

        @Override
        public double getPositionInPoints() {
            return this.tabStop.getPositionInPoints();
        }

        @Override
        public void setPositionInPoints(double position) {
            this.tabStop.setPositionInPoints(position);
            HSLFTextParagraph.this.setDirty();
        }

        @Override
        public TabStop.TabStopType getType() {
            return this.tabStop.getType();
        }

        @Override
        public void setType(TabStop.TabStopType type) {
            this.tabStop.setType(type);
            HSLFTextParagraph.this.setDirty();
        }
    }
}

