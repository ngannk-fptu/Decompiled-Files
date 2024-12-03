/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.form;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceCMYK;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceGray;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;

class PDDefaultAppearanceString {
    private static final float DEFAULT_FONT_SIZE = 12.0f;
    private final PDResources defaultResources;
    private COSName fontName;
    private PDFont font;
    private float fontSize = 12.0f;
    private PDColor fontColor;

    PDDefaultAppearanceString(COSString defaultAppearance, PDResources defaultResources) throws IOException {
        if (defaultAppearance == null) {
            throw new IllegalArgumentException("/DA is a required entry. Please set a default appearance first.");
        }
        if (defaultResources == null) {
            throw new IllegalArgumentException("/DR is a required entry");
        }
        this.defaultResources = defaultResources;
        this.processAppearanceStringOperators(defaultAppearance.getBytes());
    }

    private void processAppearanceStringOperators(byte[] content) throws IOException {
        ArrayList<COSBase> arguments = new ArrayList<COSBase>();
        PDFStreamParser parser = new PDFStreamParser(content);
        Object token = parser.parseNextToken();
        while (token != null) {
            if (token instanceof Operator) {
                this.processOperator((Operator)token, arguments);
                arguments = new ArrayList();
            } else {
                arguments.add((COSBase)token);
            }
            token = parser.parseNextToken();
        }
    }

    private void processOperator(Operator operator, List<COSBase> operands) throws IOException {
        String name = operator.getName();
        if ("Tf".equals(name)) {
            this.processSetFont(operands);
        } else if ("g".equals(name)) {
            this.processSetFontColor(operands);
        } else if ("rg".equals(name)) {
            this.processSetFontColor(operands);
        } else if ("k".equals(name)) {
            this.processSetFontColor(operands);
        }
    }

    private void processSetFont(List<COSBase> operands) throws IOException {
        if (operands.size() < 2) {
            throw new IOException("Missing operands for set font operator " + Arrays.toString(operands.toArray()));
        }
        COSBase base0 = operands.get(0);
        COSBase base1 = operands.get(1);
        if (!(base0 instanceof COSName)) {
            return;
        }
        if (!(base1 instanceof COSNumber)) {
            return;
        }
        COSName fontName = (COSName)base0;
        PDFont font = this.defaultResources.getFont(fontName);
        float fontSize = ((COSNumber)base1).floatValue();
        if (font == null) {
            throw new IOException("Could not find font: /" + fontName.getName());
        }
        this.setFontName(fontName);
        this.setFont(font);
        this.setFontSize(fontSize);
    }

    private void processSetFontColor(List<COSBase> operands) throws IOException {
        PDDeviceColorSpace colorSpace;
        switch (operands.size()) {
            case 1: {
                colorSpace = PDDeviceGray.INSTANCE;
                break;
            }
            case 3: {
                colorSpace = PDDeviceRGB.INSTANCE;
                break;
            }
            case 4: {
                colorSpace = PDDeviceCMYK.INSTANCE;
                break;
            }
            default: {
                throw new IOException("Missing operands for set non stroking color operator " + Arrays.toString(operands.toArray()));
            }
        }
        COSArray array = new COSArray();
        array.addAll(operands);
        this.setFontColor(new PDColor(array, (PDColorSpace)colorSpace));
    }

    COSName getFontName() {
        return this.fontName;
    }

    void setFontName(COSName fontName) {
        this.fontName = fontName;
    }

    PDFont getFont() {
        return this.font;
    }

    void setFont(PDFont font) {
        this.font = font;
    }

    public float getFontSize() {
        return this.fontSize;
    }

    void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }

    PDColor getFontColor() {
        return this.fontColor;
    }

    void setFontColor(PDColor fontColor) {
        this.fontColor = fontColor;
    }

    void writeTo(PDPageContentStream contents, float zeroFontSize) throws IOException {
        float fontSize = this.getFontSize();
        if (fontSize == 0.0f) {
            fontSize = zeroFontSize;
        }
        contents.setFont(this.getFont(), fontSize);
        if (this.getFontColor() != null) {
            contents.setNonStrokingColor(this.getFontColor());
        }
    }

    void copyNeededResourcesTo(PDAppearanceStream appearanceStream) throws IOException {
        PDResources streamResources = appearanceStream.getResources();
        if (streamResources == null) {
            streamResources = new PDResources();
            appearanceStream.setResources(streamResources);
        }
        if (streamResources.getFont(this.fontName) == null) {
            streamResources.put(this.fontName, this.getFont());
        }
    }
}

