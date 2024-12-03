/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.fontbox.util.BoundingBox
 */
package org.apache.pdfbox.pdmodel.interactive.form;

import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdfwriter.ContentStreamWriter;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDSimpleFont;
import org.apache.pdfbox.pdmodel.font.PDType3CharProc;
import org.apache.pdfbox.pdmodel.font.PDType3Font;
import org.apache.pdfbox.pdmodel.font.PDVectorFont;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionJavaScript;
import org.apache.pdfbox.pdmodel.interactive.action.PDFormFieldAdditionalActions;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceCharacteristicsDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceEntry;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.apache.pdfbox.pdmodel.interactive.form.AppearanceStyle;
import org.apache.pdfbox.pdmodel.interactive.form.PDDefaultAppearanceString;
import org.apache.pdfbox.pdmodel.interactive.form.PDListBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.apache.pdfbox.pdmodel.interactive.form.PDVariableText;
import org.apache.pdfbox.pdmodel.interactive.form.PlainText;
import org.apache.pdfbox.pdmodel.interactive.form.PlainTextFormatter;
import org.apache.pdfbox.pdmodel.interactive.form.ScriptingHandler;
import org.apache.pdfbox.util.Matrix;

class AppearanceGeneratorHelper {
    private static final Log LOG = LogFactory.getLog(AppearanceGeneratorHelper.class);
    private static final Operator BMC = Operator.getOperator("BMC");
    private static final Operator EMC = Operator.getOperator("EMC");
    private final PDVariableText field;
    private PDDefaultAppearanceString defaultAppearance;
    private String value;
    private static final float[] HIGHLIGHT_COLOR = new float[]{0.6f, 0.75686276f, 0.84313726f};
    private static final int FONTSCALE = 1000;
    private static final float DEFAULT_FONT_SIZE = 12.0f;
    private static final float MINIMUM_FONT_SIZE = 4.0f;
    private static final float MAXIMUM_FONT_SIZE = 300.0f;
    private static final float DEFAULT_PADDING = 0.5f;

    AppearanceGeneratorHelper(PDVariableText field) throws IOException {
        this.field = field;
        this.validateAndEnsureAcroFormResources();
        try {
            this.defaultAppearance = field.getDefaultAppearanceString();
        }
        catch (IOException ex) {
            throw new IOException("Could not process default appearance string '" + field.getDefaultAppearance() + "' for field '" + field.getFullyQualifiedName() + "'", ex);
        }
    }

    private void validateAndEnsureAcroFormResources() {
        PDResources acroFormResources = this.field.getAcroForm().getDefaultResources();
        if (acroFormResources == null) {
            return;
        }
        for (PDAnnotationWidget widget : this.field.getWidgets()) {
            PDResources widgetResources;
            PDAppearanceStream stream = widget.getNormalAppearanceStream();
            if (stream == null || (widgetResources = stream.getResources()) == null) continue;
            COSDictionary widgetFontDict = widgetResources.getCOSObject().getCOSDictionary(COSName.FONT);
            COSDictionary acroFormFontDict = acroFormResources.getCOSObject().getCOSDictionary(COSName.FONT);
            for (COSName fontResourceName : widgetResources.getFontNames()) {
                try {
                    if (acroFormResources.getFont(fontResourceName) != null) continue;
                    LOG.debug((Object)("Adding font resource " + fontResourceName + " from widget to AcroForm"));
                    acroFormFontDict.setItem(fontResourceName, widgetFontDict.getItem(fontResourceName));
                }
                catch (IOException e) {
                    LOG.warn((Object)"Unable to match field level font with AcroForm font");
                }
            }
        }
    }

    public void setAppearanceValue(String apValue) throws IOException {
        this.value = this.getFormattedValue(apValue);
        if (this.field instanceof PDTextField && !((PDTextField)this.field).isMultiline()) {
            this.value = this.value.replaceAll("\\u000D\\u000A|[\\u000A\\u000B\\u000C\\u000D\\u0085\\u2028\\u2029]", " ");
        }
        for (PDAnnotationWidget widget : this.field.getWidgets()) {
            PDAppearanceStream appearanceStream;
            PDAppearanceEntry appearance;
            PDRectangle rect;
            if (widget.getCOSObject().containsKey("PMD")) {
                LOG.warn((Object)("widget of field " + this.field.getFullyQualifiedName() + " is a PaperMetaData widget, no appearance stream created"));
                continue;
            }
            PDDefaultAppearanceString acroFormAppearance = this.defaultAppearance;
            if (widget.getCOSObject().getDictionaryObject(COSName.DA) != null) {
                this.defaultAppearance = this.getWidgetDefaultAppearanceString(widget);
            }
            if ((rect = widget.getRectangle()) == null) {
                widget.getCOSObject().removeItem(COSName.AP);
                LOG.warn((Object)("widget of field " + this.field.getFullyQualifiedName() + " has no rectangle, no appearance stream created"));
                continue;
            }
            PDAppearanceDictionary appearanceDict = widget.getAppearance();
            if (appearanceDict == null) {
                appearanceDict = new PDAppearanceDictionary();
                widget.setAppearance(appearanceDict);
            }
            if (AppearanceGeneratorHelper.isValidAppearanceStream(appearance = appearanceDict.getNormalAppearance())) {
                appearanceStream = appearance.getAppearanceStream();
            } else {
                appearanceStream = this.prepareNormalAppearanceStream(widget);
                appearanceDict.setNormalAppearance(appearanceStream);
            }
            PDAppearanceCharacteristicsDictionary appearanceCharacteristics = widget.getAppearanceCharacteristics();
            if (appearanceCharacteristics != null || appearanceStream.getContentStream().getLength() == 0) {
                this.initializeAppearanceContent(widget, appearanceCharacteristics, appearanceStream);
            }
            this.setAppearanceContent(widget, appearanceStream);
            this.defaultAppearance = acroFormAppearance;
        }
    }

    private String getFormattedValue(String apValue) {
        PDFormFieldAdditionalActions actions = this.field.getActions();
        if (actions == null) {
            return apValue;
        }
        PDAction actionF = actions.getF();
        if (actionF != null) {
            if (this.field.getAcroForm().getScriptingHandler() != null) {
                ScriptingHandler scriptingHandler = this.field.getAcroForm().getScriptingHandler();
                return scriptingHandler.format((PDActionJavaScript)actionF, apValue);
            }
            LOG.info((Object)"Field contains a formatting action but no ScriptingHandler has been supplied - formatted value might be incorrect");
        }
        return apValue;
    }

    private static boolean isValidAppearanceStream(PDAppearanceEntry appearance) {
        if (appearance == null) {
            return false;
        }
        if (!appearance.isStream()) {
            return false;
        }
        PDRectangle bbox = appearance.getAppearanceStream().getBBox();
        if (bbox == null) {
            return false;
        }
        return Math.abs(bbox.getWidth()) > 0.0f && Math.abs(bbox.getHeight()) > 0.0f;
    }

    private PDAppearanceStream prepareNormalAppearanceStream(PDAnnotationWidget widget) {
        PDAppearanceStream appearanceStream = new PDAppearanceStream(this.field.getAcroForm().getDocument());
        int rotation = this.resolveRotation(widget);
        PDRectangle rect = widget.getRectangle();
        Matrix matrix = Matrix.getRotateInstance(Math.toRadians(rotation), 0.0f, 0.0f);
        Point2D.Float point2D = matrix.transformPoint(rect.getWidth(), rect.getHeight());
        PDRectangle bbox = new PDRectangle(Math.abs((float)point2D.getX()), Math.abs((float)point2D.getY()));
        appearanceStream.setBBox(bbox);
        AffineTransform at = this.calculateMatrix(bbox, rotation);
        if (!at.isIdentity()) {
            appearanceStream.setMatrix(at);
        }
        appearanceStream.setFormType(1);
        appearanceStream.setResources(new PDResources());
        return appearanceStream;
    }

    private PDDefaultAppearanceString getWidgetDefaultAppearanceString(PDAnnotationWidget widget) throws IOException {
        COSString da = (COSString)widget.getCOSObject().getDictionaryObject(COSName.DA);
        PDResources dr = this.field.getAcroForm().getDefaultResources();
        return new PDDefaultAppearanceString(da, dr);
    }

    private int resolveRotation(PDAnnotationWidget widget) {
        PDAppearanceCharacteristicsDictionary characteristicsDictionary = widget.getAppearanceCharacteristics();
        if (characteristicsDictionary != null) {
            return characteristicsDictionary.getRotation();
        }
        return 0;
    }

    private void initializeAppearanceContent(PDAnnotationWidget widget, PDAppearanceCharacteristicsDictionary appearanceCharacteristics, PDAppearanceStream appearanceStream) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PDPageContentStream contents = new PDPageContentStream(this.field.getAcroForm().getDocument(), appearanceStream, (OutputStream)output);
        if (appearanceCharacteristics != null) {
            PDBorderStyleDictionary borderStyle;
            PDColor backgroundColour = appearanceCharacteristics.getBackground();
            if (backgroundColour != null) {
                contents.setNonStrokingColor(backgroundColour);
                PDRectangle bbox = this.resolveBoundingBox(widget, appearanceStream);
                contents.addRect(bbox.getLowerLeftX(), bbox.getLowerLeftY(), bbox.getWidth(), bbox.getHeight());
                contents.fill();
            }
            float lineWidth = 0.0f;
            PDColor borderColour = appearanceCharacteristics.getBorderColour();
            if (borderColour != null) {
                contents.setStrokingColor(borderColour);
                lineWidth = 1.0f;
            }
            if ((borderStyle = widget.getBorderStyle()) != null && borderStyle.getWidth() > 0.0f) {
                lineWidth = borderStyle.getWidth();
            }
            if (lineWidth > 0.0f && borderColour != null) {
                if (lineWidth != 1.0f) {
                    contents.setLineWidth(lineWidth);
                }
                PDRectangle bbox = this.resolveBoundingBox(widget, appearanceStream);
                PDRectangle clipRect = this.applyPadding(bbox, Math.max(0.5f, lineWidth / 2.0f));
                contents.addRect(clipRect.getLowerLeftX(), clipRect.getLowerLeftY(), clipRect.getWidth(), clipRect.getHeight());
                contents.closeAndStroke();
            }
        }
        contents.close();
        output.close();
        this.writeToStream(output.toByteArray(), appearanceStream);
    }

    private List<Object> tokenize(PDAppearanceStream appearanceStream) throws IOException {
        PDFStreamParser parser = new PDFStreamParser(appearanceStream);
        parser.parse();
        return parser.getTokens();
    }

    private void setAppearanceContent(PDAnnotationWidget widget, PDAppearanceStream appearanceStream) throws IOException {
        this.defaultAppearance.copyNeededResourcesTo(appearanceStream);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ContentStreamWriter writer = new ContentStreamWriter(output);
        List<Object> tokens = this.tokenize(appearanceStream);
        int bmcIndex = tokens.indexOf(BMC);
        if (bmcIndex == -1) {
            writer.writeTokens(tokens);
            writer.writeTokens(COSName.TX, BMC);
        } else {
            writer.writeTokens(tokens.subList(0, bmcIndex + 1));
        }
        this.insertGeneratedAppearance(widget, appearanceStream, output);
        int emcIndex = tokens.indexOf(EMC);
        if (emcIndex == -1) {
            writer.writeTokens(EMC);
        } else {
            writer.writeTokens(tokens.subList(emcIndex, tokens.size()));
        }
        output.close();
        this.writeToStream(output.toByteArray(), appearanceStream);
    }

    private void insertGeneratedAppearance(PDAnnotationWidget widget, PDAppearanceStream appearanceStream, OutputStream output) throws IOException {
        float y;
        float fontDescentAtSize;
        float fontCapAtSize;
        float fontSize;
        PDPageContentStream contents = new PDPageContentStream(this.field.getAcroForm().getDocument(), appearanceStream, output);
        PDRectangle bbox = this.resolveBoundingBox(widget, appearanceStream);
        float borderWidth = 0.0f;
        if (widget.getBorderStyle() != null) {
            borderWidth = widget.getBorderStyle().getWidth();
        }
        float padding = Math.max(1.0f, borderWidth);
        PDRectangle clipRect = this.applyPadding(bbox, padding);
        PDRectangle contentRect = this.applyPadding(clipRect, padding);
        contents.saveGraphicsState();
        contents.addRect(clipRect.getLowerLeftX(), clipRect.getLowerLeftY(), clipRect.getWidth(), clipRect.getHeight());
        contents.clip();
        PDFont font = this.defaultAppearance.getFont();
        if (font == null) {
            throw new IllegalArgumentException("font is null, check whether /DA entry is incomplete or incorrect");
        }
        if (font.getName().contains("+")) {
            LOG.warn((Object)("Font '" + this.defaultAppearance.getFontName().getName() + "' of field '" + this.field.getFullyQualifiedName() + "' contains subsetted font '" + font.getName() + "'"));
            LOG.warn((Object)"This may bring trouble with PDField.setValue(), PDAcroForm.flatten() or PDAcroForm.refreshAppearances()");
            LOG.warn((Object)"You should replace this font with a non-subsetted font:");
            LOG.warn((Object)"PDFont font = PDType0Font.load(doc, new FileInputStream(fontfile), false);");
            LOG.warn((Object)("acroForm.getDefaultResources().put(COSName.getPDFName(\"" + this.defaultAppearance.getFontName().getName() + "\", font);"));
        }
        if ((fontSize = this.defaultAppearance.getFontSize()) == 0.0f) {
            fontSize = this.calculateFontSize(font, contentRect);
        }
        if (this.field instanceof PDListBox) {
            this.insertGeneratedListboxSelectionHighlight(contents, appearanceStream, font, fontSize);
        }
        contents.beginText();
        this.defaultAppearance.writeTo(contents, fontSize);
        float fontScaleY = fontSize / 1000.0f;
        float fontBoundingBoxAtSize = font.getBoundingBox().getHeight() * fontScaleY;
        if (font.getFontDescriptor() != null) {
            fontCapAtSize = font.getFontDescriptor().getCapHeight() * fontScaleY;
            fontDescentAtSize = font.getFontDescriptor().getDescent() * fontScaleY;
        } else {
            float fontCapHeight = this.resolveCapHeight(font);
            float fontDescent = this.resolveDescent(font);
            LOG.debug((Object)("missing font descriptor - resolved Cap/Descent to " + fontCapHeight + "/" + fontDescent));
            fontCapAtSize = fontCapHeight * fontScaleY;
            fontDescentAtSize = fontDescent * fontScaleY;
        }
        if (this.field instanceof PDTextField && ((PDTextField)this.field).isMultiline()) {
            y = contentRect.getUpperRightY() - fontBoundingBoxAtSize;
        } else if (fontCapAtSize > clipRect.getHeight()) {
            y = clipRect.getLowerLeftY() + -fontDescentAtSize;
        } else {
            y = clipRect.getLowerLeftY() + (clipRect.getHeight() - fontCapAtSize) / 2.0f;
            if (y - clipRect.getLowerLeftY() < -fontDescentAtSize) {
                float fontDescentBased = -fontDescentAtSize + contentRect.getLowerLeftY();
                float fontCapBased = contentRect.getHeight() - contentRect.getLowerLeftY() - fontCapAtSize;
                y = Math.min(fontDescentBased, Math.max(y, fontCapBased));
            }
        }
        float x = contentRect.getLowerLeftX();
        if (this.shallComb()) {
            this.insertGeneratedCombAppearance(contents, appearanceStream, font, fontSize);
        } else if (this.field instanceof PDListBox) {
            this.insertGeneratedListboxAppearance(contents, appearanceStream, contentRect, font, fontSize);
        } else {
            PlainText textContent = new PlainText(this.value);
            AppearanceStyle appearanceStyle = new AppearanceStyle();
            appearanceStyle.setFont(font);
            appearanceStyle.setFontSize(fontSize);
            appearanceStyle.setLeading(font.getBoundingBox().getHeight() * fontScaleY);
            PlainTextFormatter formatter = new PlainTextFormatter.Builder(contents).style(appearanceStyle).text(textContent).width(contentRect.getWidth()).wrapLines(this.isMultiLine()).initialOffset(x, y).textAlign(this.getTextAlign(widget)).build();
            formatter.format();
        }
        contents.endText();
        contents.restoreGraphicsState();
        contents.close();
    }

    private int getTextAlign(PDAnnotationWidget widget) {
        return widget.getCOSObject().getInt(COSName.Q, this.field.getQ());
    }

    private AffineTransform calculateMatrix(PDRectangle bbox, int rotation) {
        if (rotation == 0) {
            return new AffineTransform();
        }
        float tx = 0.0f;
        float ty = 0.0f;
        switch (rotation) {
            case 90: {
                tx = bbox.getUpperRightY();
                break;
            }
            case 180: {
                tx = bbox.getUpperRightY();
                ty = bbox.getUpperRightX();
                break;
            }
            case 270: {
                ty = bbox.getUpperRightX();
                break;
            }
        }
        Matrix matrix = Matrix.getRotateInstance(Math.toRadians(rotation), tx, ty);
        return matrix.createAffineTransform();
    }

    private boolean isMultiLine() {
        return this.field instanceof PDTextField && ((PDTextField)this.field).isMultiline();
    }

    private boolean shallComb() {
        return this.field instanceof PDTextField && ((PDTextField)this.field).isComb() && ((PDTextField)this.field).getMaxLen() != -1 && !((PDTextField)this.field).isMultiline() && !((PDTextField)this.field).isPassword() && !((PDTextField)this.field).isFileSelect();
    }

    private void insertGeneratedCombAppearance(PDPageContentStream contents, PDAppearanceStream appearanceStream, PDFont font, float fontSize) throws IOException {
        int maxLen = ((PDTextField)this.field).getMaxLen();
        int quadding = this.field.getQ();
        int numChars = Math.min(this.value.length(), maxLen);
        PDRectangle paddingEdge = this.applyPadding(appearanceStream.getBBox(), 1.0f);
        float combWidth = appearanceStream.getBBox().getWidth() / (float)maxLen;
        float ascentAtFontSize = font.getFontDescriptor().getAscent() / 1000.0f * fontSize;
        float baselineOffset = paddingEdge.getLowerLeftY() + (appearanceStream.getBBox().getHeight() - ascentAtFontSize) / 2.0f;
        float prevCharWidth = 0.0f;
        float xOffset = combWidth / 2.0f;
        if (quadding == 2) {
            xOffset += (float)(maxLen - numChars) * combWidth;
        } else if (quadding == 1) {
            xOffset += (float)((maxLen - numChars) / 2) * combWidth;
        }
        for (int i = 0; i < numChars; ++i) {
            String combString = this.value.substring(i, i + 1);
            float currCharWidth = font.getStringWidth(combString) / 1000.0f * fontSize / 2.0f;
            xOffset = xOffset + prevCharWidth / 2.0f - currCharWidth / 2.0f;
            contents.newLineAtOffset(xOffset, baselineOffset);
            contents.showText(combString);
            baselineOffset = 0.0f;
            prevCharWidth = currCharWidth;
            xOffset = combWidth;
        }
    }

    private void insertGeneratedListboxSelectionHighlight(PDPageContentStream contents, PDAppearanceStream appearanceStream, PDFont font, float fontSize) throws IOException {
        PDListBox listBox = (PDListBox)this.field;
        List<Integer> indexEntries = listBox.getSelectedOptionsIndex();
        List<String> values = listBox.getValue();
        List<String> options = listBox.getOptionsExportValues();
        if (!values.isEmpty() && !options.isEmpty() && indexEntries.isEmpty()) {
            indexEntries = new ArrayList<Integer>(values.size());
            for (String v : values) {
                indexEntries.add(options.indexOf(v));
            }
        }
        int topIndex = listBox.getTopIndex();
        float highlightBoxHeight = font.getBoundingBox().getHeight() * fontSize / 1000.0f;
        PDRectangle paddingEdge = this.applyPadding(appearanceStream.getBBox(), 1.0f);
        for (int selectedIndex : indexEntries) {
            contents.setNonStrokingColor(HIGHLIGHT_COLOR[0], HIGHLIGHT_COLOR[1], HIGHLIGHT_COLOR[2]);
            contents.addRect(paddingEdge.getLowerLeftX(), paddingEdge.getUpperRightY() - highlightBoxHeight * (float)(selectedIndex - topIndex + 1) + 2.0f, paddingEdge.getWidth(), highlightBoxHeight);
            contents.fill();
        }
        contents.setNonStrokingColor(0.0f);
    }

    private void insertGeneratedListboxAppearance(PDPageContentStream contents, PDAppearanceStream appearanceStream, PDRectangle contentRect, PDFont font, float fontSize) throws IOException {
        contents.setNonStrokingColor(0.0f);
        int q = this.field.getQ();
        if (q == 1 || q == 2) {
            float fieldWidth = appearanceStream.getBBox().getWidth();
            float stringWidth = font.getStringWidth(this.value) / 1000.0f * fontSize;
            float adjustAmount = fieldWidth - stringWidth - 4.0f;
            if (q == 1) {
                adjustAmount /= 2.0f;
            }
            contents.newLineAtOffset(adjustAmount, 0.0f);
        } else if (q != 0) {
            throw new IOException("Error: Unknown justification value:" + q);
        }
        List<String> options = ((PDListBox)this.field).getOptionsDisplayValues();
        int numOptions = options.size();
        float yTextPos = contentRect.getUpperRightY();
        int topIndex = ((PDListBox)this.field).getTopIndex();
        float ascent = font.getFontDescriptor().getAscent();
        float height = font.getBoundingBox().getHeight();
        for (int i = topIndex; i < numOptions; ++i) {
            if (i == topIndex) {
                yTextPos -= ascent / 1000.0f * fontSize;
            } else {
                yTextPos -= height / 1000.0f * fontSize;
                contents.beginText();
            }
            contents.newLineAtOffset(contentRect.getLowerLeftX(), yTextPos);
            contents.showText(options.get(i));
            if (i == numOptions - 1) continue;
            contents.endText();
        }
    }

    private void writeToStream(byte[] data, PDAppearanceStream appearanceStream) throws IOException {
        OutputStream out = appearanceStream.getCOSObject().createOutputStream();
        out.write(data);
        out.close();
    }

    private float calculateFontSize(PDFont font, PDRectangle contentRect) throws IOException {
        float fontSize = this.defaultAppearance.getFontSize();
        if (fontSize == 0.0f) {
            if (this.isMultiLine()) {
                PlainText textContent = new PlainText(this.value);
                if (textContent.getParagraphs() != null) {
                    float fs;
                    float width = contentRect.getWidth() - contentRect.getLowerLeftX();
                    for (fs = 4.0f; fs <= 12.0f; fs += 1.0f) {
                        int numLines = 0;
                        for (PlainText.Paragraph paragraph : textContent.getParagraphs()) {
                            numLines += paragraph.getLines(font, fs, width).size();
                        }
                        float fontScaleY = fs / 1000.0f;
                        float leading = font.getBoundingBox().getHeight() * fontScaleY;
                        float height = leading * (float)numLines;
                        if (!(height > contentRect.getHeight())) continue;
                        return Math.max(fs - 1.0f, 4.0f);
                    }
                    return Math.min(fs, 12.0f);
                }
                return 12.0f;
            }
            float yScalingFactor = 1000.0f * font.getFontMatrix().getScaleY();
            float xScalingFactor = 1000.0f * font.getFontMatrix().getScaleX();
            float width = font.getStringWidth(this.value) * font.getFontMatrix().getScaleX();
            float widthBasedFontSize = contentRect.getWidth() / width * xScalingFactor;
            float height = (font.getFontDescriptor().getCapHeight() + -font.getFontDescriptor().getDescent()) * font.getFontMatrix().getScaleY();
            if (height <= 0.0f) {
                height = font.getBoundingBox().getHeight() * font.getFontMatrix().getScaleY();
            }
            float heightBasedFontSize = contentRect.getHeight() / height * yScalingFactor;
            return Math.min(heightBasedFontSize, widthBasedFontSize);
        }
        return fontSize;
    }

    private float resolveCapHeight(PDFont font) throws IOException {
        return this.resolveGlyphHeight(font, "H".codePointAt(0));
    }

    private float resolveDescent(PDFont font) throws IOException {
        return this.resolveGlyphHeight(font, "y".codePointAt(0)) - this.resolveGlyphHeight(font, "a".codePointAt(0));
    }

    private float resolveGlyphHeight(PDFont font, int code) throws IOException {
        GeneralPath path = null;
        if (font instanceof PDType3Font) {
            PDType3Font t3Font = (PDType3Font)font;
            PDType3CharProc charProc = t3Font.getCharProc(code);
            if (charProc != null) {
                BoundingBox fontBBox = t3Font.getBoundingBox();
                PDRectangle glyphBBox = charProc.getGlyphBBox();
                if (glyphBBox != null) {
                    glyphBBox.setLowerLeftX(Math.max(fontBBox.getLowerLeftX(), glyphBBox.getLowerLeftX()));
                    glyphBBox.setLowerLeftY(Math.max(fontBBox.getLowerLeftY(), glyphBBox.getLowerLeftY()));
                    glyphBBox.setUpperRightX(Math.min(fontBBox.getUpperRightX(), glyphBBox.getUpperRightX()));
                    glyphBBox.setUpperRightY(Math.min(fontBBox.getUpperRightY(), glyphBBox.getUpperRightY()));
                    path = glyphBBox.toGeneralPath();
                }
            }
        } else if (font instanceof PDVectorFont) {
            PDVectorFont vectorFont = (PDVectorFont)((Object)font);
            path = vectorFont.getPath(code);
        } else if (font instanceof PDSimpleFont) {
            PDSimpleFont simpleFont = (PDSimpleFont)font;
            String name = simpleFont.getEncoding().getName(code);
            path = simpleFont.getPath(name);
        } else {
            LOG.warn((Object)("Unknown font class: " + font.getClass()));
        }
        if (path == null) {
            return -1.0f;
        }
        return (float)path.getBounds2D().getHeight();
    }

    private PDRectangle resolveBoundingBox(PDAnnotationWidget fieldWidget, PDAppearanceStream appearanceStream) {
        PDRectangle boundingBox = appearanceStream.getBBox();
        if (boundingBox == null) {
            boundingBox = fieldWidget.getRectangle().createRetranslatedRectangle();
        }
        return boundingBox;
    }

    private PDRectangle applyPadding(PDRectangle box, float padding) {
        return new PDRectangle(box.getLowerLeftX() + padding, box.getLowerLeftY() + padding, box.getWidth() - 2.0f * padding, box.getHeight() - 2.0f * padding);
    }
}

