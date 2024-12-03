/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.PdfTemplate;
import java.net.URL;

public class ImgTemplate
extends Image {
    ImgTemplate(Image image) {
        super(image);
    }

    public ImgTemplate(PdfTemplate template) throws BadElementException {
        super((URL)null);
        if (template == null) {
            throw new BadElementException(MessageLocalization.getComposedMessage("the.template.can.not.be.null"));
        }
        if (template.getType() == 3) {
            throw new BadElementException(MessageLocalization.getComposedMessage("a.pattern.can.not.be.used.as.a.template.to.create.an.image"));
        }
        this.type = 35;
        this.scaledHeight = template.getHeight();
        this.setTop(this.scaledHeight);
        this.scaledWidth = template.getWidth();
        this.setRight(this.scaledWidth);
        this.setTemplateData(template);
        this.plainWidth = this.getWidth();
        this.plainHeight = this.getHeight();
    }
}

