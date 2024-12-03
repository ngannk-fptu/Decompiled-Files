/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.decode;

import com.sun.pdfview.PDFObject;
import com.sun.pdfview.PDFParseException;
import com.sun.pdfview.decode.PNGPredictor;
import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class Predictor {
    public static final int TIFF = 0;
    public static final int PNG = 1;
    private int algorithm;
    private int colors = 1;
    private int bpc = 8;
    private int columns = 1;

    protected Predictor(int algorithm) {
        this.algorithm = algorithm;
    }

    public abstract ByteBuffer unpredict(ByteBuffer var1) throws IOException;

    public static Predictor getPredictor(PDFObject params) throws IOException {
        PDFObject columnsObj;
        PDFObject bpcObj;
        PDFObject algorithmObj = params.getDictRef("Predictor");
        if (algorithmObj == null) {
            return null;
        }
        int algorithm = algorithmObj.getIntValue();
        PNGPredictor predictor = null;
        switch (algorithm) {
            case 1: {
                return null;
            }
            case 2: {
                throw new PDFParseException("Tiff Predictor not supported");
            }
            case 10: 
            case 11: 
            case 12: 
            case 13: 
            case 14: 
            case 15: {
                predictor = new PNGPredictor();
                break;
            }
            default: {
                throw new PDFParseException("Unknown predictor: " + algorithm);
            }
        }
        PDFObject colorsObj = params.getDictRef("Colors");
        if (colorsObj != null) {
            predictor.setColors(colorsObj.getIntValue());
        }
        if ((bpcObj = params.getDictRef("BitsPerComponent")) != null) {
            predictor.setBitsPerComponent(bpcObj.getIntValue());
        }
        if ((columnsObj = params.getDictRef("Columns")) != null) {
            predictor.setColumns(columnsObj.getIntValue());
        }
        return predictor;
    }

    public int getAlgorithm() {
        return this.algorithm;
    }

    public int getColors() {
        return this.colors;
    }

    protected void setColors(int colors) {
        this.colors = colors;
    }

    public int getBitsPerComponent() {
        return this.bpc;
    }

    public void setBitsPerComponent(int bpc) {
        this.bpc = bpc;
    }

    public int getColumns() {
        return this.columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }
}

