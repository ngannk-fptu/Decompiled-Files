/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf.events;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Rectangle;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfFormField;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPCellEvent;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfRectangle;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.TextField;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FieldPositioningEvents
extends PdfPageEventHelper
implements PdfPCellEvent {
    protected Map<String, PdfFormField> genericChunkFields = new HashMap<String, PdfFormField>();
    protected PdfFormField cellField = null;
    protected PdfWriter fieldWriter = null;
    protected PdfFormField parent = null;
    public float padding;

    public FieldPositioningEvents() {
    }

    public void addField(String text, PdfFormField field) {
        this.genericChunkFields.put(text, field);
    }

    public FieldPositioningEvents(PdfWriter writer, PdfFormField field) {
        this.cellField = field;
        this.fieldWriter = writer;
    }

    public FieldPositioningEvents(PdfFormField parent, PdfFormField field) {
        this.cellField = field;
        this.parent = parent;
    }

    public FieldPositioningEvents(PdfWriter writer, String text) throws IOException, DocumentException {
        this.fieldWriter = writer;
        TextField tf = new TextField(writer, new Rectangle(0.0f, 0.0f), text);
        tf.setFontSize(14.0f);
        this.cellField = tf.getTextField();
    }

    public FieldPositioningEvents(PdfWriter writer, PdfFormField parent, String text) throws IOException, DocumentException {
        this.parent = parent;
        TextField tf = new TextField(writer, new Rectangle(0.0f, 0.0f), text);
        tf.setFontSize(14.0f);
        this.cellField = tf.getTextField();
    }

    public void setPadding(float padding) {
        this.padding = padding;
    }

    public void setParent(PdfFormField parent) {
        this.parent = parent;
    }

    @Override
    public void onGenericTag(PdfWriter writer, Document document, Rectangle rect, String text) {
        rect.setBottom(rect.getBottom() - 3.0f);
        PdfFormField field = this.genericChunkFields.get(text);
        if (field == null) {
            TextField tf = new TextField(writer, new Rectangle(rect.getLeft(this.padding), rect.getBottom(this.padding), rect.getRight(this.padding), rect.getTop(this.padding)), text);
            tf.setFontSize(14.0f);
            try {
                field = tf.getTextField();
            }
            catch (Exception e) {
                throw new ExceptionConverter(e);
            }
        } else {
            field.put(PdfName.RECT, new PdfRectangle(rect.getLeft(this.padding), rect.getBottom(this.padding), rect.getRight(this.padding), rect.getTop(this.padding)));
        }
        if (this.parent == null) {
            writer.addAnnotation(field);
        } else {
            this.parent.addKid(field);
        }
    }

    @Override
    public void cellLayout(PdfPCell cell, Rectangle rect, PdfContentByte[] canvases) {
        if (this.cellField == null || this.fieldWriter == null && this.parent == null) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("you.have.used.the.wrong.constructor.for.this.fieldpositioningevents.class"));
        }
        this.cellField.put(PdfName.RECT, new PdfRectangle(rect.getLeft(this.padding), rect.getBottom(this.padding), rect.getRight(this.padding), rect.getTop(this.padding)));
        if (this.parent == null) {
            this.fieldWriter.addAnnotation(this.cellField);
        } else {
            this.parent.addKid(this.cellField);
        }
    }
}

