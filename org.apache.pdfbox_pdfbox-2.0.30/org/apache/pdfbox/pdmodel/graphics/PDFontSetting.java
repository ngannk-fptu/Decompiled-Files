/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics;

import java.io.IOException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDFontFactory;

public class PDFontSetting
implements COSObjectable {
    private COSArray fontSetting = null;

    public PDFontSetting() {
        this.fontSetting = new COSArray();
        this.fontSetting.add(null);
        this.fontSetting.add(new COSFloat(1.0f));
    }

    public PDFontSetting(COSArray fs) {
        this.fontSetting = fs;
    }

    @Override
    public COSBase getCOSObject() {
        return this.fontSetting;
    }

    public PDFont getFont() throws IOException {
        PDFont retval = null;
        COSBase font = this.fontSetting.getObject(0);
        if (font instanceof COSDictionary) {
            retval = PDFontFactory.createFont((COSDictionary)font);
        }
        return retval;
    }

    public void setFont(PDFont font) {
        this.fontSetting.set(0, font);
    }

    public float getFontSize() {
        COSNumber size = (COSNumber)this.fontSetting.get(1);
        return size.floatValue();
    }

    public void setFontSize(float size) {
        this.fontSetting.set(1, new COSFloat(size));
    }
}

