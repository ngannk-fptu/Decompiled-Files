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

public class StampAnnotation
extends PDFAnnotation {
    private String iconName;
    private PDFAnnotation popupAnnotation;
    private PDFObject onAppearance;
    private PDFObject offAppearance;
    private List<PDFCmd> onCmd;
    private List<PDFCmd> offCmd;
    private boolean appearanceStateOn;

    public StampAnnotation(PDFObject annotObject, PDFAnnotation.ANNOTATION_TYPE type) throws IOException {
        super(annotObject, type);
        this.parsePopupAnnotation(annotObject.getDictRef("Popup"));
        this.parseAP(annotObject.getDictRef("AP"));
    }

    public StampAnnotation(PDFObject annotObject) throws IOException {
        this(annotObject, PDFAnnotation.ANNOTATION_TYPE.STAMP);
    }

    private void parseAP(PDFObject dictRef) throws IOException {
        if (dictRef == null) {
            return;
        }
        PDFObject normalAP = dictRef.getDictRef("N");
        if (normalAP == null) {
            return;
        }
        if (normalAP.getType() == 6) {
            this.onAppearance = normalAP.getDictRef("On");
            this.offAppearance = normalAP.getDictRef("Off");
            PDFObject as = dictRef.getDictRef("AS");
            this.appearanceStateOn = as != null && "On".equals(as.getStringValue());
        } else {
            this.onAppearance = normalAP;
            this.offAppearance = null;
            this.appearanceStateOn = true;
        }
        this.parseCommands();
    }

    private void parseCommands() throws IOException {
        if (this.onAppearance != null) {
            this.onCmd = this.parseCommand(this.onAppearance);
        }
        if (this.offAppearance != null) {
            this.offCmd = this.parseCommand(this.offAppearance);
        }
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
            List<PDFCmd> cmds = formCmds.getCommands();
            result.addAll(cmds);
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

    private void parsePopupAnnotation(PDFObject popupObj) throws IOException {
        this.popupAnnotation = popupObj != null ? StampAnnotation.createAnnotation(popupObj) : null;
    }

    public String getIconName() {
        return this.iconName;
    }

    public PDFAnnotation getPopupAnnotation() {
        return this.popupAnnotation;
    }

    public PDFObject getOnAppearance() {
        return this.onAppearance;
    }

    public PDFObject getOffAppearance() {
        return this.offAppearance;
    }

    public boolean isAppearanceStateOn() {
        return this.appearanceStateOn;
    }

    public void switchAppearance() {
        this.appearanceStateOn = !this.appearanceStateOn;
    }

    public PDFObject getCurrentAppearance() {
        return this.appearanceStateOn ? this.onAppearance : this.offAppearance;
    }

    public List<PDFCmd> getCurrentCommand() {
        return this.appearanceStateOn ? this.onCmd : this.offCmd;
    }

    @Override
    public List<PDFCmd> getPageCommandsForAnnotation() {
        List<PDFCmd> pageCommandsForAnnotation = super.getPageCommandsForAnnotation();
        if (this.getCurrentCommand() != null) {
            pageCommandsForAnnotation.addAll(this.getCurrentCommand());
        }
        return pageCommandsForAnnotation;
    }
}

