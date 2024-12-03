/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.annotation;

import com.sun.pdfview.PDFCmd;
import com.sun.pdfview.PDFObject;
import com.sun.pdfview.PDFParseException;
import com.sun.pdfview.annotation.FreetextAnnotation;
import com.sun.pdfview.annotation.LinkAnnotation;
import com.sun.pdfview.annotation.StampAnnotation;
import com.sun.pdfview.annotation.WidgetAnnotation;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class PDFAnnotation {
    public static final String GOTO = "GoTo";
    public static final String GOTOE = "GoToE";
    public static final String GOTOR = "GoToR";
    public static final String URI = "URI";
    private final PDFObject pdfObj;
    private final ANNOTATION_TYPE type;
    private final Rectangle2D.Float rect;

    public PDFAnnotation(PDFObject annotObject) throws IOException {
        this(annotObject, ANNOTATION_TYPE.UNKNOWN);
    }

    protected PDFAnnotation(PDFObject annotObject, ANNOTATION_TYPE type) throws IOException {
        this.pdfObj = annotObject;
        this.type = type;
        this.rect = this.parseRect(annotObject.getDictRef("Rect"));
    }

    public static PDFAnnotation createAnnotation(PDFObject parent) throws IOException {
        PDFObject subtypeValue = parent.getDictRef("Subtype");
        if (subtypeValue == null) {
            return null;
        }
        String subtypeS = subtypeValue.getStringValue();
        ANNOTATION_TYPE annotationType = ANNOTATION_TYPE.getByDefinition(subtypeS);
        Class<?> className = annotationType.getClassName();
        try {
            Constructor<?> constructor = className.getConstructor(PDFObject.class);
            return (PDFAnnotation)constructor.newInstance(parent);
        }
        catch (Exception e) {
            throw new PDFParseException("Could not parse annotation!", e);
        }
    }

    public Rectangle2D.Float parseRect(PDFObject obj) throws IOException {
        if (obj.getType() == 5) {
            PDFObject[] bounds = obj.getArray();
            if (bounds.length == 4) {
                return new Rectangle2D.Float(bounds[0].getFloatValue(), bounds[1].getFloatValue(), bounds[2].getFloatValue() - bounds[0].getFloatValue(), bounds[3].getFloatValue() - bounds[1].getFloatValue());
            }
            throw new PDFParseException("Rectangle definition didn't have 4 elements");
        }
        throw new PDFParseException("Rectangle definition not an array");
    }

    public PDFObject getPdfObj() {
        return this.pdfObj;
    }

    public ANNOTATION_TYPE getType() {
        return this.type;
    }

    public Rectangle2D.Float getRect() {
        return this.rect;
    }

    public String toString() {
        return this.pdfObj.toString();
    }

    public List<PDFCmd> getPageCommandsForAnnotation() {
        return new ArrayList<PDFCmd>();
    }

    public static enum ANNOTATION_TYPE {
        UNKNOWN("-", 0, PDFAnnotation.class),
        LINK("Link", 1, LinkAnnotation.class),
        WIDGET("Widget", 2, WidgetAnnotation.class),
        STAMP("Stamp", 3, StampAnnotation.class),
        FREETEXT("FreeText", 5, FreetextAnnotation.class);

        private String definition;
        private int internalId;
        private Class<?> className;

        private ANNOTATION_TYPE(String definition, int typeId, Class<?> className) {
            this.definition = definition;
            this.internalId = typeId;
            this.className = className;
        }

        public String getDefinition() {
            return this.definition;
        }

        public int getInternalId() {
            return this.internalId;
        }

        public Class<?> getClassName() {
            return this.className;
        }

        public static ANNOTATION_TYPE getByDefinition(String definition) {
            for (ANNOTATION_TYPE type : ANNOTATION_TYPE.values()) {
                if (!type.definition.equals(definition)) continue;
                return type;
            }
            return UNKNOWN;
        }
    }
}

