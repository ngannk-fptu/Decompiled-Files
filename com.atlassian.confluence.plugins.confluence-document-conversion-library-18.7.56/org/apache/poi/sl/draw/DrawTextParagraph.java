/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.sl.draw;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.io.InvalidObjectException;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.common.usermodel.fonts.FontGroup;
import org.apache.poi.common.usermodel.fonts.FontInfo;
import org.apache.poi.sl.draw.DrawFactory;
import org.apache.poi.sl.draw.DrawFontInfo;
import org.apache.poi.sl.draw.DrawFontManager;
import org.apache.poi.sl.draw.DrawPaint;
import org.apache.poi.sl.draw.DrawShape;
import org.apache.poi.sl.draw.DrawTextFragment;
import org.apache.poi.sl.draw.Drawable;
import org.apache.poi.sl.usermodel.AutoNumberingScheme;
import org.apache.poi.sl.usermodel.Hyperlink;
import org.apache.poi.sl.usermodel.Insets2D;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.sl.usermodel.PlaceableShape;
import org.apache.poi.sl.usermodel.PlaceholderDetails;
import org.apache.poi.sl.usermodel.SimpleShape;
import org.apache.poi.sl.usermodel.Slide;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.sl.usermodel.TextRun;
import org.apache.poi.sl.usermodel.TextShape;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LocaleUtil;
import org.apache.poi.util.StringUtil;
import org.apache.poi.util.Units;

public class DrawTextParagraph
implements Drawable {
    private static final Logger LOG = LogManager.getLogger(DrawTextParagraph.class);
    public static final XlinkAttribute HYPERLINK_HREF = new XlinkAttribute("href");
    public static final XlinkAttribute HYPERLINK_LABEL = new XlinkAttribute("label");
    protected TextParagraph<?, ?, ?> paragraph;
    double x;
    double y;
    protected List<DrawTextFragment> lines = new ArrayList<DrawTextFragment>();
    protected String rawText;
    protected DrawTextFragment bullet;
    protected int autoNbrIdx;
    protected boolean firstParagraph = true;

    public DrawTextParagraph(TextParagraph<?, ?, ?> paragraph) {
        this.paragraph = paragraph;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getY() {
        return this.y;
    }

    public void setAutoNumberingIdx(int index) {
        this.autoNbrIdx = index;
    }

    @Override
    public void draw(Graphics2D graphics) {
        Double spacing;
        Double indent;
        if (this.lines.isEmpty()) {
            return;
        }
        boolean isHSLF = this.isHSLF();
        double penY = this.y;
        int indentLevel = this.paragraph.getIndentLevel();
        Double leftMargin = this.paragraph.getLeftMargin();
        if (leftMargin == null) {
            leftMargin = Units.toPoints(347663L * (long)indentLevel);
        }
        if ((indent = this.paragraph.getIndent()) == null) {
            indent = Units.toPoints(347663L * (long)indentLevel);
        }
        if ((spacing = this.paragraph.getLineSpacing()) == null) {
            spacing = 100.0;
        }
        DrawTextFragment lastLine = null;
        for (DrawTextFragment line : this.lines) {
            if (!this.isFirstParagraph() || lastLine != null) {
                penY -= (double)(line.getLeading() + (lastLine == null ? 0.0f : lastLine.getLayout().getDescent()));
                penY = spacing > 0.0 ? (penY += spacing * 0.01 * (double)line.getHeight()) : (penY += -spacing.doubleValue());
                penY -= (double)line.getLayout().getAscent();
            }
            double penX = this.x + leftMargin;
            if (lastLine == null) {
                if (!this.isEmptyParagraph()) {
                    this.bullet = this.getBullet(graphics, line.getAttributedString().getIterator());
                }
                if (this.bullet != null) {
                    this.bullet.setPosition(isHSLF ? this.x + indent : this.x + leftMargin + indent, penY);
                    this.bullet.draw(graphics);
                    double bulletWidth = this.bullet.getLayout().getAdvance() + 1.0f;
                    penX = this.x + (isHSLF ? leftMargin : Math.max(leftMargin, leftMargin + indent + bulletWidth));
                }
            }
            Rectangle2D anchor = DrawShape.getAnchor(graphics, this.paragraph.getParentShape());
            Insets2D insets = this.paragraph.getParentShape().getInsets();
            double leftInset = insets.left;
            double rightInset = insets.right;
            TextParagraph.TextAlign ta = this.paragraph.getTextAlign();
            if (ta == null) {
                ta = TextParagraph.TextAlign.LEFT;
            }
            switch (ta) {
                case CENTER: {
                    penX += (anchor.getWidth() - (double)line.getWidth() - leftInset - rightInset - leftMargin) / 2.0;
                    break;
                }
                case RIGHT: {
                    penX += anchor.getWidth() - (double)line.getWidth() - leftInset - rightInset;
                    break;
                }
            }
            line.setPosition(penX, penY);
            line.draw(graphics);
            penY += (double)line.getHeight();
            lastLine = line;
        }
        this.y = penY - this.y;
    }

    public float getFirstLineLeading() {
        return this.lines.isEmpty() ? 0.0f : this.lines.get(0).getLeading();
    }

    public float getFirstLineHeight() {
        return this.lines.isEmpty() ? 0.0f : this.lines.get(0).getHeight();
    }

    public float getLastLineHeight() {
        return this.lines.isEmpty() ? 0.0f : this.lines.get(this.lines.size() - 1).getHeight();
    }

    public boolean isEmptyParagraph() {
        return this.lines.isEmpty() || StringUtil.isBlank(this.rawText);
    }

    @Override
    public void applyTransform(Graphics2D graphics) {
    }

    @Override
    public void drawContent(Graphics2D graphics) {
    }

    protected void breakText(Graphics2D graphics) {
        int endIndex;
        this.lines.clear();
        DrawFactory fact = DrawFactory.getInstance(graphics);
        StringBuilder text = new StringBuilder();
        List<AttributedStringData> attList = this.getAttributedString(graphics, text);
        AttributedString as = new AttributedString(text.toString());
        AttributedString asNoCR = new AttributedString(text.toString().replaceAll("[\\r\\n]", " "));
        for (AttributedStringData asd : attList) {
            as.addAttribute(asd.attribute, asd.value, asd.beginIndex, asd.endIndex);
            asNoCR.addAttribute(asd.attribute, asd.value, asd.beginIndex, asd.endIndex);
        }
        AttributedCharacterIterator it = as.getIterator();
        AttributedCharacterIterator itNoCR = asNoCR.getIterator();
        LineBreakMeasurer measurer = new LineBreakMeasurer(it, graphics.getFontRenderContext());
        do {
            TextLayout layout;
            int startIndex = measurer.getPosition();
            double wrappingWidth = this.getWrappingWidth(this.lines.isEmpty(), graphics) + 1.0;
            if (wrappingWidth < 0.0) {
                wrappingWidth = 1.0;
            }
            if (startIndex == 0 && text.toString().startsWith("\n")) {
                layout = measurer.nextLayout((float)wrappingWidth, 1, false);
                endIndex = 1;
            } else {
                TextParagraph.TextAlign hAlign;
                int nextBreak = text.indexOf("\n", startIndex + 1);
                if (nextBreak == -1) {
                    nextBreak = it.getEndIndex();
                }
                if ((layout = measurer.nextLayout((float)wrappingWidth, nextBreak, true)) == null) {
                    layout = measurer.nextLayout((float)wrappingWidth, nextBreak, false);
                }
                if (layout == null) break;
                endIndex = measurer.getPosition();
                if (endIndex < it.getEndIndex() && text.charAt(endIndex) == '\n') {
                    measurer.setPosition(endIndex + 1);
                }
                if ((hAlign = this.paragraph.getTextAlign()) == TextParagraph.TextAlign.JUSTIFY || hAlign == TextParagraph.TextAlign.JUSTIFY_LOW) {
                    layout = layout.getJustifiedLayout((float)wrappingWidth);
                }
            }
            AttributedString str = new AttributedString(itNoCR, startIndex, endIndex);
            DrawTextFragment line = fact.getTextFragment(layout, str);
            this.lines.add(line);
        } while (endIndex != it.getEndIndex());
        this.rawText = text.toString();
    }

    protected DrawTextFragment getBullet(Graphics2D graphics, AttributedCharacterIterator firstLineAttr) {
        TextParagraph.BulletStyle bulletStyle = this.paragraph.getBulletStyle();
        if (bulletStyle == null) {
            return null;
        }
        AutoNumberingScheme ans = bulletStyle.getAutoNumberingScheme();
        String buCharacter = ans != null ? ans.format(this.autoNbrIdx) : bulletStyle.getBulletCharacter();
        if (buCharacter == null) {
            return null;
        }
        PlaceableShape<?, ?> ps = this.getParagraphShape();
        PaintStyle fgPaintStyle = bulletStyle.getBulletFontColor();
        Paint fgPaint = fgPaintStyle == null ? (Paint)firstLineAttr.getAttribute(TextAttribute.FOREGROUND) : new DrawPaint(ps).getPaint(graphics, fgPaintStyle);
        float fontSize = ((Float)firstLineAttr.getAttribute(TextAttribute.SIZE)).floatValue();
        Double buSz = bulletStyle.getBulletFontSize();
        if (buSz == null) {
            buSz = 100.0;
        }
        fontSize = buSz > 0.0 ? (fontSize *= (float)(buSz * 0.01)) : (float)(-buSz.doubleValue());
        String buFontStr = bulletStyle.getBulletFont();
        if (buFontStr == null) {
            buFontStr = this.paragraph.getDefaultFontFamily();
        }
        assert (buFontStr != null);
        FontInfo buFont = new DrawFontInfo(buFontStr);
        DrawFontManager dfm = DrawFactory.getInstance(graphics).getFontManager(graphics);
        buFont = dfm.getMappedFont(graphics, buFont);
        HashMap<TextAttribute, Object> att = new HashMap<TextAttribute, Object>();
        att.put(TextAttribute.FOREGROUND, fgPaint);
        att.put(TextAttribute.FAMILY, buFont.getTypeface());
        att.put(TextAttribute.SIZE, Float.valueOf(fontSize));
        att.put(TextAttribute.FONT, new Font(att));
        AttributedString str = new AttributedString(dfm.mapFontCharset(graphics, buFont, buCharacter));
        att.forEach(str::addAttribute);
        TextLayout layout = new TextLayout(str.getIterator(), graphics.getFontRenderContext());
        DrawFactory fact = DrawFactory.getInstance(graphics);
        return fact.getTextFragment(layout, str);
    }

    protected String getRenderableText(Graphics2D graphics, TextRun tr) {
        TextRun.FieldType ft = tr.getFieldType();
        if (ft == null) {
            return this.getRenderableText(tr);
        }
        if (tr.getRawText() != null && !tr.getRawText().isEmpty()) {
            switch (ft) {
                case SLIDE_NUMBER: {
                    Slide slide = (Slide)graphics.getRenderingHint(Drawable.CURRENT_SLIDE);
                    return slide == null ? "" : Integer.toString(slide.getSlideNumber());
                }
                case DATE_TIME: {
                    PlaceholderDetails pd = ((SimpleShape)this.getParagraphShape()).getPlaceholderDetails();
                    pd.getPlaceholder();
                    String uda = pd.getUserDate();
                    if (uda != null) {
                        return uda;
                    }
                    Calendar cal = LocaleUtil.getLocaleCalendar();
                    LocalDateTime now = LocalDateTime.ofInstant(cal.toInstant(), cal.getTimeZone().toZoneId());
                    return now.format(pd.getDateFormat());
                }
            }
        }
        return "";
    }

    @Internal
    public String getRenderableText(TextRun tr) {
        String txtSpace = tr.getRawText();
        if (txtSpace == null) {
            return null;
        }
        if (txtSpace.contains("\t")) {
            txtSpace = txtSpace.replace("\t", this.tab2space(tr));
        }
        txtSpace = txtSpace.replace('\u000b', '\n');
        Locale loc = LocaleUtil.getUserLocale();
        switch (tr.getTextCap()) {
            case ALL: {
                return txtSpace.toUpperCase(loc);
            }
            case SMALL: {
                return txtSpace.toLowerCase(loc);
            }
        }
        return txtSpace;
    }

    private String tab2space(TextRun tr) {
        int numSpaces;
        AttributedString string = new AttributedString(" ");
        String fontFamily = tr.getFontFamily();
        if (fontFamily == null) {
            fontFamily = "Lucida Sans";
        }
        string.addAttribute(TextAttribute.FAMILY, fontFamily);
        Double fs = tr.getFontSize();
        if (fs == null) {
            fs = 12.0;
        }
        string.addAttribute(TextAttribute.SIZE, Float.valueOf(fs.floatValue()));
        TextLayout l = new TextLayout(string.getIterator(), new FontRenderContext(null, true, true));
        double wspace = l.getAdvance();
        Double tabSz = this.paragraph.getDefaultTabSize();
        if (wspace <= 0.0) {
            numSpaces = 4;
        } else {
            if (tabSz == null) {
                tabSz = wspace * 4.0;
            }
            numSpaces = (int)Math.min(Math.ceil(tabSz / wspace), 20.0);
        }
        char[] buf = new char[numSpaces];
        Arrays.fill(buf, ' ');
        return new String(buf);
    }

    protected double getWrappingWidth(boolean firstLine, Graphics2D graphics) {
        double width;
        Double rightMargin;
        Double indent;
        Double leftMargin;
        long TAB_SIZE = 347663L;
        TextShape<?, ?> ts = this.paragraph.getParentShape();
        Insets2D insets = ts.getInsets();
        double leftInset = insets.left;
        double rightInset = insets.right;
        int indentLevel = this.paragraph.getIndentLevel();
        if (indentLevel == -1) {
            indentLevel = 0;
        }
        if ((leftMargin = this.paragraph.getLeftMargin()) == null) {
            leftMargin = Units.toPoints(347663L * (long)indentLevel);
        }
        if ((indent = this.paragraph.getIndent()) == null) {
            indent = 0.0;
        }
        if ((rightMargin = this.paragraph.getRightMargin()) == null) {
            rightMargin = 0.0;
        }
        Rectangle2D anchor = DrawShape.getAnchor(graphics, ts);
        TextShape.TextDirection textDir = ts.getTextDirection();
        if (!ts.getWordWrap()) {
            Dimension pageDim = ts.getSheet().getSlideShow().getPageSize();
            switch (textDir) {
                default: {
                    width = pageDim.getWidth() - anchor.getX();
                    break;
                }
                case VERTICAL: {
                    width = pageDim.getHeight() - anchor.getX();
                    break;
                }
                case VERTICAL_270: {
                    width = anchor.getX();
                    break;
                }
            }
        } else {
            switch (textDir) {
                default: {
                    width = anchor.getWidth() - leftInset - rightInset - leftMargin - rightMargin;
                    break;
                }
                case VERTICAL: 
                case VERTICAL_270: {
                    width = anchor.getHeight() - leftInset - rightInset - leftMargin - rightMargin;
                }
            }
            if (firstLine && this.bullet == null) {
                width += this.isHSLF() ? leftMargin - indent : -indent.doubleValue();
            }
        }
        return width;
    }

    private PlaceableShape<?, ?> getParagraphShape() {
        return this.paragraph.getParentShape();
    }

    protected List<AttributedStringData> getAttributedString(Graphics2D graphics, StringBuilder text) {
        if (text == null) {
            text = new StringBuilder();
        }
        DrawPaint dp = new DrawPaint(this.getParagraphShape());
        DrawFontManager dfm = DrawFactory.getInstance(graphics).getFontManager(graphics);
        assert (dfm != null);
        HashMap<AttributedCharacterIterator.Attribute, Object> att = new HashMap<AttributedCharacterIterator.Attribute, Object>();
        ArrayList<AttributedStringData> attList = new ArrayList<AttributedStringData>();
        for (TextRun run : this.paragraph) {
            Hyperlink<?, ?> hl;
            String runText = this.getRenderableText(graphics, run);
            if (runText.isEmpty()) continue;
            att.clear();
            FontInfo fontInfo = run.getFontInfo(null);
            runText = dfm.mapFontCharset(graphics, fontInfo, runText);
            int beginIndex = text.length();
            text.append(runText);
            int endIndex = text.length();
            PaintStyle fgPaintStyle = run.getFontColor();
            Paint fgPaint = dp.getPaint(graphics, fgPaintStyle);
            att.put(TextAttribute.FOREGROUND, fgPaint);
            Double fontSz = run.getFontSize();
            if (fontSz == null) {
                fontSz = this.paragraph.getDefaultFontSize();
            }
            att.put(TextAttribute.SIZE, Float.valueOf(fontSz.floatValue()));
            if (run.isBold()) {
                att.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
            }
            if (run.isItalic()) {
                att.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
            }
            if (run.isUnderlined()) {
                att.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
                att.put(TextAttribute.INPUT_METHOD_UNDERLINE, TextAttribute.UNDERLINE_LOW_TWO_PIXEL);
            }
            if (run.isStrikethrough()) {
                att.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
            }
            if (run.isSubscript()) {
                att.put(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB);
            }
            if (run.isSuperscript()) {
                att.put(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER);
            }
            if ((hl = run.getHyperlink()) != null) {
                att.put(HYPERLINK_HREF, hl.getAddress());
                att.put(HYPERLINK_LABEL, hl.getLabel());
            }
            if (fontInfo != null) {
                att.put(TextAttribute.FAMILY, fontInfo.getTypeface());
            } else {
                att.put(TextAttribute.FAMILY, this.paragraph.getDefaultFontFamily());
            }
            att.put(TextAttribute.FONT, new Font(att));
            att.forEach((k, v) -> attList.add(new AttributedStringData((AttributedCharacterIterator.Attribute)k, v, beginIndex, endIndex)));
            this.processGlyphs(graphics, dfm, attList, beginIndex, run, runText);
        }
        if (text.length() == 0) {
            text.append(" ");
            Double fontSz = this.paragraph.getDefaultFontSize();
            att.put(TextAttribute.SIZE, Float.valueOf(fontSz.floatValue()));
            att.put(TextAttribute.FAMILY, this.paragraph.getDefaultFontFamily());
            att.put(TextAttribute.FONT, new Font(att));
            att.forEach((k, v) -> attList.add(new AttributedStringData((AttributedCharacterIterator.Attribute)k, v, 0, 1)));
        }
        return attList;
    }

    private void processGlyphs(Graphics2D graphics, DrawFontManager dfm, List<AttributedStringData> attList, int beginIndex, TextRun run, String runText) {
        List<FontGroup.FontGroupRange> ttrList = FontGroup.getFontGroupRanges(runText);
        int rangeBegin = 0;
        for (FontGroup.FontGroupRange ttr : ttrList) {
            FontInfo fiRun = run.getFontInfo(ttr.getFontGroup());
            if (fiRun == null) {
                fiRun = run.getFontInfo(FontGroup.LATIN);
            }
            FontInfo fiMapped = dfm.getMappedFont(graphics, fiRun);
            FontInfo fiFallback = dfm.getFallbackFont(graphics, fiRun);
            assert (fiFallback != null);
            if (fiMapped == null) {
                fiMapped = dfm.getMappedFont(graphics, new DrawFontInfo(this.paragraph.getDefaultFontFamily()));
            }
            if (fiMapped == null) {
                fiMapped = fiFallback;
            }
            Font fontMapped = dfm.createAWTFont(graphics, fiMapped, 10.0, run.isBold(), run.isItalic());
            Font fontFallback = dfm.createAWTFont(graphics, fiFallback, 10.0, run.isBold(), run.isItalic());
            int rangeLen = ttr.getLength();
            int partEnd = rangeBegin;
            while (partEnd < rangeBegin + rangeLen) {
                int endIndex;
                int startIndex;
                String fontName;
                int partBegin = partEnd;
                if (partBegin < (partEnd = DrawTextParagraph.nextPart(fontMapped, runText, partBegin, rangeBegin + rangeLen, true))) {
                    fontName = fontMapped.getFontName(Locale.ROOT);
                    startIndex = beginIndex + partBegin;
                    endIndex = beginIndex + partEnd;
                    attList.add(new AttributedStringData(TextAttribute.FAMILY, fontName, startIndex, endIndex));
                    LOG.atDebug().log("mapped: {} {} {} - {}", (Object)fontName, (Object)Unbox.box(startIndex), (Object)Unbox.box(endIndex), (Object)runText.substring(partBegin, partEnd));
                }
                if ((partBegin = partEnd) >= (partEnd = DrawTextParagraph.nextPart(fontMapped, runText, partBegin, rangeBegin + rangeLen, false))) continue;
                fontName = fontFallback.getFontName(Locale.ROOT);
                startIndex = beginIndex + partBegin;
                endIndex = beginIndex + partEnd;
                attList.add(new AttributedStringData(TextAttribute.FAMILY, fontName, startIndex, endIndex));
                LOG.atDebug().log("fallback: {} {} {} - {}", (Object)fontName, (Object)Unbox.box(startIndex), (Object)Unbox.box(endIndex), (Object)runText.substring(partBegin, partEnd));
            }
            rangeBegin += rangeLen;
        }
    }

    private static int nextPart(Font fontMapped, String runText, int beginPart, int endPart, boolean isDisplayed) {
        int rIdx;
        int codepoint;
        for (rIdx = beginPart; rIdx < endPart && fontMapped.canDisplay(codepoint = runText.codePointAt(rIdx)) == isDisplayed; rIdx += Character.charCount(codepoint)) {
        }
        return rIdx;
    }

    protected boolean isHSLF() {
        return DrawShape.isHSLF(this.paragraph.getParentShape());
    }

    protected boolean isFirstParagraph() {
        return this.firstParagraph;
    }

    protected void setFirstParagraph(boolean firstParagraph) {
        this.firstParagraph = firstParagraph;
    }

    private static class AttributedStringData {
        AttributedCharacterIterator.Attribute attribute;
        Object value;
        int beginIndex;
        int endIndex;

        AttributedStringData(AttributedCharacterIterator.Attribute attribute, Object value, int beginIndex, int endIndex) {
            this.attribute = attribute;
            this.value = value;
            this.beginIndex = beginIndex;
            this.endIndex = endIndex;
        }
    }

    private static class XlinkAttribute
    extends AttributedCharacterIterator.Attribute {
        XlinkAttribute(String name) {
            super(name);
        }

        @Override
        protected Object readResolve() throws InvalidObjectException {
            if (HYPERLINK_HREF.getName().equals(this.getName())) {
                return HYPERLINK_HREF;
            }
            if (HYPERLINK_LABEL.getName().equals(this.getName())) {
                return HYPERLINK_LABEL;
            }
            throw new InvalidObjectException("unknown attribute name");
        }
    }
}

