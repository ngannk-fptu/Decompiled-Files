/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview;

import com.sun.pdfview.NameTree;
import com.sun.pdfview.PDFObject;
import com.sun.pdfview.PDFParseException;
import java.io.IOException;

public class PDFDestination {
    public static final int XYZ = 0;
    public static final int FIT = 1;
    public static final int FITH = 2;
    public static final int FITV = 3;
    public static final int FITR = 4;
    public static final int FITB = 5;
    public static final int FITBH = 6;
    public static final int FITBV = 7;
    private int type;
    private PDFObject pageObj;
    private float left;
    private float right;
    private float top;
    private float bottom;
    private float zoom;

    protected PDFDestination(PDFObject pageObj, int type) {
        this.pageObj = pageObj;
        this.type = type;
    }

    public static PDFDestination getDestination(PDFObject obj, PDFObject root) throws IOException {
        if (obj.getType() == 4) {
            obj = PDFDestination.getDestFromName(obj, root);
        } else if (obj.getType() == 3) {
            obj = PDFDestination.getDestFromString(obj, root);
        }
        if (obj == null || obj.getType() != 5) {
            throw new PDFParseException("Can't create destination from: " + obj);
        }
        PDFObject[] destArray = obj.getArray();
        PDFDestination dest = null;
        String type = destArray[1].getStringValue();
        if (type.equals("XYZ")) {
            dest = new PDFDestination(destArray[0], 0);
        } else if (type.equals("Fit")) {
            dest = new PDFDestination(destArray[0], 1);
        } else if (type.equals("FitH")) {
            dest = new PDFDestination(destArray[0], 2);
        } else if (type.equals("FitV")) {
            dest = new PDFDestination(destArray[0], 3);
        } else if (type.equals("FitR")) {
            dest = new PDFDestination(destArray[0], 4);
        } else if (type.equals("FitB")) {
            dest = new PDFDestination(destArray[0], 5);
        } else if (type.equals("FitBH")) {
            dest = new PDFDestination(destArray[0], 6);
        } else if (type.equals("FitBV")) {
            dest = new PDFDestination(destArray[0], 7);
        } else {
            throw new PDFParseException("Unknown destination type: " + type);
        }
        switch (dest.getType()) {
            case 0: {
                dest.setLeft(destArray[2].getFloatValue());
                dest.setTop(destArray[3].getFloatValue());
                dest.setZoom(destArray[4].getFloatValue());
                break;
            }
            case 2: {
                dest.setTop(destArray[2].getFloatValue());
                break;
            }
            case 3: {
                dest.setLeft(destArray[2].getFloatValue());
                break;
            }
            case 4: {
                dest.setLeft(destArray[2].getFloatValue());
                dest.setBottom(destArray[3].getFloatValue());
                dest.setRight(destArray[4].getFloatValue());
                dest.setTop(destArray[5].getFloatValue());
                break;
            }
            case 6: {
                dest.setTop(destArray[2].getFloatValue());
                break;
            }
            case 7: {
                dest.setLeft(destArray[2].getFloatValue());
            }
        }
        return dest;
    }

    public int getType() {
        return this.type;
    }

    public PDFObject getPage() {
        return this.pageObj;
    }

    public float getLeft() {
        return this.left;
    }

    public void setLeft(float left) {
        this.left = left;
    }

    public float getRight() {
        return this.right;
    }

    public void setRight(float right) {
        this.right = right;
    }

    public float getTop() {
        return this.top;
    }

    public void setTop(float top) {
        this.top = top;
    }

    public float getBottom() {
        return this.bottom;
    }

    public void setBottom(float bottom) {
        this.bottom = bottom;
    }

    public float getZoom() {
        return this.zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    private static PDFObject getDestFromName(PDFObject name, PDFObject root) throws IOException {
        PDFObject dests = root.getDictRef("Dests");
        if (dests != null) {
            return dests.getDictRef(name.getStringValue());
        }
        return null;
    }

    private static PDFObject getDestFromString(PDFObject str, PDFObject root) throws IOException {
        PDFObject dests;
        PDFObject names = root.getDictRef("Names");
        if (names != null && (dests = names.getDictRef("Dests")) != null) {
            NameTree tree = new NameTree(dests);
            PDFObject obj = tree.find(str.getStringValue());
            if (obj != null && obj.getType() == 6) {
                obj = obj.getDictRef("D");
            }
            return obj;
        }
        return null;
    }
}

