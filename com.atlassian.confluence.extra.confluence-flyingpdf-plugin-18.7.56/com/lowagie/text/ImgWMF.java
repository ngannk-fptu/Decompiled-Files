/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text;

import com.lowagie.text.BadElementException;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Utilities;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.codec.wmf.InputMeta;
import com.lowagie.text.pdf.codec.wmf.MetaDo;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ImgWMF
extends Image {
    ImgWMF(Image image) {
        super(image);
    }

    public ImgWMF(URL url) throws BadElementException, IOException {
        super(url);
        this.processParameters();
    }

    public ImgWMF(String filename) throws BadElementException, IOException {
        this(Utilities.toURL(filename));
    }

    public ImgWMF(byte[] img) throws BadElementException, IOException {
        super((URL)null);
        this.rawData = img;
        this.originalData = img;
        this.processParameters();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void processParameters() throws BadElementException, IOException {
        this.type = 35;
        this.originalType = 6;
        InputStream is = null;
        try {
            String errorID;
            if (this.rawData == null) {
                is = this.url.openStream();
                errorID = this.url.toString();
            } else {
                is = new ByteArrayInputStream(this.rawData);
                errorID = "Byte array";
            }
            InputMeta in = new InputMeta(is);
            if (in.readInt() != -1698247209) {
                throw new BadElementException(MessageLocalization.getComposedMessage("1.is.not.a.valid.placeable.windows.metafile", errorID));
            }
            in.readWord();
            int left = in.readShort();
            int top = in.readShort();
            int right = in.readShort();
            int bottom = in.readShort();
            int inch = in.readWord();
            this.dpiX = 72;
            this.dpiY = 72;
            this.scaledHeight = (float)(bottom - top) / (float)inch * 72.0f;
            this.setTop(this.scaledHeight);
            this.scaledWidth = (float)(right - left) / (float)inch * 72.0f;
            this.setRight(this.scaledWidth);
        }
        finally {
            if (is != null) {
                is.close();
            }
            this.plainWidth = this.getWidth();
            this.plainHeight = this.getHeight();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void readWMF(PdfTemplate template) throws IOException, DocumentException {
        this.setTemplateData(template);
        template.setWidth(this.getWidth());
        template.setHeight(this.getHeight());
        try (InputStream is = null;){
            is = this.rawData == null ? this.url.openStream() : new ByteArrayInputStream(this.rawData);
            MetaDo meta = new MetaDo(is, template);
            meta.readAll();
        }
    }
}

