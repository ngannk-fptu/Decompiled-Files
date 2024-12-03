/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.annotation;

import com.sun.pdfview.PDFCmd;
import com.sun.pdfview.PDFImage;
import com.sun.pdfview.PDFObject;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFParseException;
import com.sun.pdfview.PDFParser;
import com.sun.pdfview.annotation.PDFAnnotation;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WidgetAnnotation
extends PDFAnnotation {
    private String fieldValue;
    private FieldType fieldType;
    private String fieldName;
    private PDFObject fieldValueRef;
    private List<PDFCmd> cmd;

    public WidgetAnnotation(PDFObject annotObject) throws IOException {
        super(annotObject, PDFAnnotation.ANNOTATION_TYPE.WIDGET);
        PDFObject fieldTypeRef = annotObject.getDictRef("FT");
        if (fieldTypeRef != null) {
            this.fieldType = FieldType.getByCode(fieldTypeRef.getStringValue());
        } else {
            PDFObject parent;
            for (parent = annotObject.getDictRef("Parent"); parent != null && parent.isIndirect(); parent = parent.dereference()) {
            }
            if (parent != null) {
                fieldTypeRef = parent.getDictRef("FT");
                this.fieldType = FieldType.getByCode(fieldTypeRef.getStringValue());
            }
        }
        PDFObject fieldNameRef = annotObject.getDictRef("T");
        if (fieldNameRef != null) {
            this.fieldName = fieldNameRef.getTextStringValue();
        }
        this.fieldValueRef = annotObject.getDictRef("V");
        if (this.fieldValueRef != null) {
            this.fieldValue = this.fieldValueRef.getTextStringValue();
        }
        this.parseAP(annotObject.getDictRef("AP"));
    }

    private void parseAP(PDFObject dictRef) throws IOException {
        if (dictRef == null) {
            return;
        }
        PDFObject normalAP = dictRef.getDictRef("N");
        if (normalAP == null) {
            return;
        }
        this.cmd = this.parseCommand(normalAP);
    }

    private List<PDFCmd> parseCommand(PDFObject obj) throws IOException {
        String type = obj.getDictRef("Subtype").getStringValue();
        if (type == null) {
            type = obj.getDictRef("S").getStringValue();
        }
        ArrayList<PDFCmd> result = new ArrayList<PDFCmd>();
        result.add(PDFPage.createPushCmd());
        result.add(PDFPage.createPushCmd());
        if (type.equals("Image")) {
            AffineTransform rectAt = this.getPositionTransformation();
            result.add(PDFPage.createXFormCmd(rectAt));
            PDFImage img = PDFImage.createImage(obj, new HashMap(), false);
            result.add(PDFPage.createImageCmd(img));
        } else if (type.equals("Form")) {
            AffineTransform at;
            PDFObject bobj = obj.getDictRef("BBox");
            Rectangle2D.Float bbox = new Rectangle2D.Float(bobj.getAt(0).getFloatValue(), bobj.getAt(1).getFloatValue(), bobj.getAt(2).getFloatValue(), bobj.getAt(3).getFloatValue());
            PDFPage formCmds = new PDFPage(bbox, 0);
            AffineTransform rectAt = this.getPositionTransformation();
            formCmds.addXform(rectAt);
            PDFObject matrix = obj.getDictRef("Matrix");
            if (matrix == null) {
                at = new AffineTransform();
            } else {
                float[] elts = new float[6];
                for (int i = 0; i < elts.length; ++i) {
                    elts[i] = matrix.getAt(i).getFloatValue();
                }
                at = new AffineTransform(elts);
            }
            formCmds.addXform(at);
            HashMap<String, PDFObject> r = new HashMap<String, PDFObject>(new HashMap());
            PDFObject rsrc = obj.getDictRef("Resources");
            if (rsrc != null) {
                r.putAll(rsrc.getDictionary());
            }
            PDFParser form = new PDFParser(formCmds, obj.getStream(), r);
            form.go(true);
            result.addAll(formCmds.getCommands());
        } else {
            throw new PDFParseException("Unknown XObject subtype: " + type);
        }
        result.add(PDFPage.createPopCmd());
        result.add(PDFPage.createPopCmd());
        return result;
    }

    private AffineTransform getPositionTransformation() {
        Rectangle2D.Float rect2 = this.getRect();
        double[] f = new double[]{1.0, 0.0, 0.0, 1.0, rect2.getMinX(), rect2.getMinY()};
        return new AffineTransform(f);
    }

    public FieldType getFieldType() {
        return this.fieldType;
    }

    public String getFieldValue() {
        return this.fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    @Override
    public List<PDFCmd> getPageCommandsForAnnotation() {
        List<PDFCmd> pageCommandsForAnnotation = super.getPageCommandsForAnnotation();
        pageCommandsForAnnotation.addAll(this.cmd);
        return pageCommandsForAnnotation;
    }

    public static enum FieldType {
        Button("Btn"),
        Text("Tx"),
        Choice("Ch"),
        Signature("Sig");

        private final String typeCode;

        private FieldType(String typeCode) {
            this.typeCode = typeCode;
        }

        static FieldType getByCode(String typeCode) {
            FieldType[] values;
            for (FieldType value : values = FieldType.values()) {
                if (!value.typeCode.equals(typeCode)) continue;
                return value;
            }
            return null;
        }
    }
}

